package me.exrates.service.btcCore;

import com.neemre.btcdcli4j.core.BitcoindException;
import com.neemre.btcdcli4j.core.CommunicationException;
import com.neemre.btcdcli4j.core.client.BtcdClient;
import com.neemre.btcdcli4j.core.client.BtcdClientImpl;
import com.neemre.btcdcli4j.core.domain.*;
import lombok.extern.log4j.Log4j2;
import me.exrates.model.dto.BtcTransactionHistoryDto;
import me.exrates.model.dto.BtcWalletInfoDto;
import me.exrates.model.dto.TxReceivedByAddressFlatDto;
import me.exrates.model.dto.merchants.btc.*;
import me.exrates.model.enums.ActionType;
import me.exrates.model.util.BigDecimalProcessing;
import me.exrates.service.btcCore.btcDaemon.BtcDaemon;
import me.exrates.service.btcCore.btcDaemon.BtcHttpDaemonImpl;
import me.exrates.service.exception.BitcoinCoreException;
import me.exrates.service.exception.invoice.InsufficientCostsInWalletException;
import me.exrates.service.exception.invoice.InvalidAccountException;
import me.exrates.service.exception.invoice.MerchantException;
import me.exrates.service.btcCore.btcDaemon.BtcdZMQDaemonImpl;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.zeromq.ZMQ;
import reactor.core.publisher.Flux;

import javax.annotation.Nullable;
import javax.annotation.PreDestroy;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Created by OLEG on 14.03.2017.
 */
@Component
@Scope("prototype")
@Log4j2(topic = "bitcoin_core")
public class CoreWalletServiceImpl implements CoreWalletService {
  
  private static final int KEY_POOL_LOW_THRESHOLD = 10;
  private static final int MIN_CONFIRMATIONS_FOR_SPENDING = 3;

  @Autowired
  private ZMQ.Context zmqContext;

  
  private BtcdClient btcdClient;

  private BtcDaemon btcDaemon;

  private Boolean supportInstantSend;

  private Map<String, CompletableFuture<Void>> unlockingTasks = new ConcurrentHashMap<>();

  private ScheduledExecutorService outputUnlockingExecutor = Executors.newSingleThreadScheduledExecutor();

  private final Object SENDING_LOCK = new Object();




  @Override
  public void initCoreClient(String nodePropertySource, boolean supportInstantSend) {
    try {
      PoolingHttpClientConnectionManager cm = new PoolingHttpClientConnectionManager();
      CloseableHttpClient httpProvider = HttpClients.custom().setConnectionManager(cm)
              .build();
      Properties nodeConfig = new Properties();
      nodeConfig.load(getClass().getClassLoader().getResourceAsStream(nodePropertySource));
      log.debug("Node config: " + nodeConfig);
      btcdClient = new BtcdClientImpl(httpProvider, nodeConfig);
      this.supportInstantSend = supportInstantSend;
    } catch (Exception e) {
      log.error(e);
    }
    
  }
  
  @Override
  public void initBtcdDaemon(boolean zmqEnabled)  {
    if (zmqEnabled) {
      btcDaemon = new BtcdZMQDaemonImpl(btcdClient, zmqContext);
    } else {
      btcDaemon = new BtcHttpDaemonImpl(btcdClient);
    }

    try {
      btcDaemon.init();
    } catch (Exception e) {
      log.error(e);
    }
  }
  
  
  @Override
  public String getNewAddress(String walletPassword) {
    try {
      WalletInfo walletInfo = btcdClient.getWalletInfo();
      Integer keyPoolSize = walletInfo.getKeypoolSize();
      
      /*
      * If wallet is encrypted and locked, pool of private keys is not refilled
      * Keys are automatically refilled on unlocking
      * */
      if (keyPoolSize < KEY_POOL_LOW_THRESHOLD) {
        unlockWallet(walletPassword, 1);
      }
      return btcdClient.getNewAddress();
    } catch (BitcoindException | CommunicationException e) {
      log.error(e);
      throw new BitcoinCoreException("Cannot generate new address!");
    }
  }
  
  @Override
  public void backupWallet(String backupFolder) {
    try {
      String filename = new StringJoiner("").add(backupFolder).add("backup_")
              .add((LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"))))
              .add(".dat").toString();
      log.debug("Backing up wallet to file: " + filename);
      btcdClient.backupWallet(filename);
    } catch (BitcoindException | CommunicationException e) {
      log.error(e);
    }
  }
  
  @Override
  public void shutdown() {
    btcdClient.close();
    btcDaemon.destroy();
  }
  
  
  private Optional<Transaction> handleConflicts(Transaction transaction) {
    if (transaction.getConfirmations() < 0 && !transaction.getWalletConflicts().isEmpty()) {
      log.warn("Wallet conflicts present");
      for (String txId : transaction.getWalletConflicts()) {
        try {
          Transaction conflicted = btcdClient.getTransaction(txId);
          if (conflicted.getConfirmations() >= 0) {
            return Optional.of(conflicted);
          }
        } catch (BitcoindException | CommunicationException e) {
          log.error(e);
        }
      }
      return Optional.empty();
    } else {
      return Optional.of(transaction);
    }
  }
  
  @Override
  public Optional<BtcTransactionDto> handleTransactionConflicts(String txId) {
    try {
      log.debug(this);
      return handleConflicts(btcdClient.getTransaction(txId)).map(this::convert);
    } catch (BitcoindException | CommunicationException e) {
      log.error(e);
    }
    return Optional.empty();
  }
  
  private BtcTransactionDto convert(Transaction tx) {
    List<BtcTxPaymentDto> payments = tx.getDetails().stream()
            .map((payment) -> new BtcTxPaymentDto(payment.getAddress(), payment.getCategory().getName(), payment.getAmount(), payment.getFee()))
            .collect(Collectors.toList());
    return new BtcTransactionDto(tx.getAmount(), tx.getFee(), tx.getConfirmations(), tx.getTxId(), tx.getBlockHash(), tx.getWalletConflicts(), tx.getTime(),
            tx.getTimeReceived(), tx.getComment(), tx.getTo(), payments);
  }
  
  @Override
  public BtcTransactionDto getTransaction(String txId) {
    try {
      Transaction tx = btcdClient.getTransaction(txId);
      return convert(tx);
    } catch (BitcoindException | CommunicationException e) {
      throw new BitcoinCoreException(e.getMessage());
    }
  }
  
 
  @Override
  public BtcWalletInfoDto getWalletInfo() {
    try {
      BtcWalletInfoDto dto = new BtcWalletInfoDto();
      WalletInfo walletInfo = btcdClient.getWalletInfo();
      BigDecimal spendableBalance = btcdClient.getBalance("", MIN_CONFIRMATIONS_FOR_SPENDING);
      BigDecimal confirmedNonSpendableBalance = BigDecimalProcessing.doAction(walletInfo.getBalance(), spendableBalance, ActionType.SUBTRACT);
      BigDecimal unconfirmedBalance = btcdClient.getUnconfirmedBalance();
      
      dto.setBalance(BigDecimalProcessing.formatNonePoint(spendableBalance, true));
      dto.setConfirmedNonSpendableBalance(BigDecimalProcessing.formatNonePoint(confirmedNonSpendableBalance, true));
      dto.setUnconfirmedBalance(BigDecimalProcessing.formatNonePoint(unconfirmedBalance, true));
      dto.setTransactionCount(walletInfo.getTxCount());
      return dto;
    } catch (BitcoindException | CommunicationException e) {
      log.error(e);
      throw new BitcoinCoreException(e.getMessage());
    }
  }
  
  @Override
  public List<TxReceivedByAddressFlatDto> listReceivedByAddress(Integer minConfirmations) {
    try {
      List<Address> received = btcdClient.listReceivedByAddress(minConfirmations);
      return received.stream().flatMap(address -> address.getTxIds().stream().map(txId -> {
        TxReceivedByAddressFlatDto dto = new TxReceivedByAddressFlatDto();
        dto.setAccount(address.getAccount());
        dto.setAddress(address.getAddress());
        dto.setAmount(address.getAmount());
        dto.setConfirmations(address.getConfirmations());
        dto.setTxId(txId);
        return dto;
      })).collect(Collectors.toList());
    } catch (BitcoindException | CommunicationException e) {
      log.error(e);
      throw new BitcoinCoreException(e.getMessage());
    }
  }
  
  @Override
  public List<BtcTransactionHistoryDto> listAllTransactions() {
    try {
      return btcdClient.listSinceBlock().getPayments().stream()
              .map(payment -> {
                BtcTransactionHistoryDto dto = new BtcTransactionHistoryDto();
                dto.setTxId(payment.getTxId());
                dto.setAddress(payment.getAddress());
                dto.setCategory(payment.getCategory().getName());
                dto.setAmount(BigDecimalProcessing.formatNonePoint(payment.getAmount(), true));
                dto.setFee(BigDecimalProcessing.formatNonePoint(payment.getFee(), true));
                dto.setConfirmations(payment.getConfirmations());
                dto.setTime(LocalDateTime.ofInstant(Instant.ofEpochMilli(payment.getTime() * 1000L), ZoneId.systemDefault()));
                return dto;
              }).collect(Collectors.toList());
    } catch (BitcoindException | CommunicationException e) {
      log.error(e);
      throw new BitcoinCoreException(e.getMessage());
    }
  }
  
  @Override
  public List<BtcPaymentFlatDto> listSinceBlock(String blockHash, Integer merchantId, Integer currencyId) {
    try {
      return btcdClient.listSinceBlock(blockHash).getPayments().stream()
              .map(payment -> BtcPaymentFlatDto.builder()
                        .amount(payment.getAmount())
                        .confirmations(payment.getConfirmations())
                        .merchantId(merchantId)
                        .currencyId(currencyId)
                        .address(payment.getAddress())
                        .txId(payment.getTxId())
                        .blockhash(payment.getBlockHash())
                        .build()).collect(Collectors.toList());
    } catch (Exception e) {
      log.error(e);
      return Collections.EMPTY_LIST;
    }
  }
  
  
  
  @Override
  public BigDecimal estimateFee(int blockCount) {
    try {
      return btcdClient.estimateFee(blockCount);
    } catch (BitcoindException | CommunicationException e) {
      log.error(e);
      throw new BitcoinCoreException(e.getMessage());
    }
  }
  
  @Override
  public BigDecimal getActualFee() {
    try {
      return btcdClient.getInfo().getPayTxFee();
    } catch (BitcoindException | CommunicationException e) {
      log.error(e);
      throw new BitcoinCoreException(e.getMessage());
    }
  }
  
  @Override
  public void setTxFee(BigDecimal fee) {
    try {
      btcdClient.setTxFee(fee);
    } catch (BitcoindException | CommunicationException e) {
      log.error(e);
      throw new BitcoinCoreException(e.getMessage());
    }
  }
  
  @Override
  public void submitWalletPassword(String password) {
    try {
      unlockWallet(password, 60);
    } catch (BitcoindException | CommunicationException e) {
      log.error(e);
      throw new BitcoinCoreException(e.getMessage());
    }
  }
  
  
  /*
  * Using sendMany instead of sendToAddress allows to send only UTXO with certain number of confirmations.
  * DO NOT use immutable map creation methods like Collections.singletonMap(...), it will cause an error within lib code
  * */
  @Override
  public String sendToAddressAuto(String address, BigDecimal amount, String walletPassword) {
    
    try {
      unlockWallet(walletPassword, 1);
      Map<String, BigDecimal> payments = new HashMap<>();
      payments.put(address, amount);
      String result;
      synchronized (SENDING_LOCK) {
        result = btcdClient.sendMany("", payments, MIN_CONFIRMATIONS_FOR_SPENDING);
      }
      return result;
    } catch (BitcoindException e) {
      log.error(e);
      if (e.getCode() == -5) {
        throw new InvalidAccountException();
      }
      if (e.getCode() == -6) {
        throw new InsufficientCostsInWalletException();
      }
      throw new MerchantException(e.getMessage());
    }
    catch (CommunicationException e) {
      log.error(e);
      throw new MerchantException(e.getMessage());
    }
  }
  
  private void unlockWallet(String password, int authTimeout) throws BitcoindException, CommunicationException {
    Long unlockedUntil = btcdClient.getWalletInfo().getUnlockedUntil();
    if (unlockedUntil != null && unlockedUntil == 0) {
      btcdClient.walletPassphrase(password, authTimeout);
    }
  }
  
  @Override
  public BtcPaymentResultDto sendToMany(Map<String, BigDecimal> payments, boolean subtractFeeFromAmount) {
    try {
      List<String> subtractFeeAddresses = new ArrayList<>();
      if (subtractFeeFromAmount) {
        subtractFeeAddresses = new ArrayList<>(payments.keySet());
      }
      String txId;
      synchronized (SENDING_LOCK) {
          if (supportInstantSend) {
              txId = btcdClient.sendMany("", payments, MIN_CONFIRMATIONS_FOR_SPENDING, false,
                      "", subtractFeeAddresses);
          } else {
              txId = btcdClient.sendMany("", payments, MIN_CONFIRMATIONS_FOR_SPENDING,
                      "", subtractFeeAddresses);
          }
      }
      return new BtcPaymentResultDto(txId);
    } catch (Exception e) {
      log.error(e);
      return new BtcPaymentResultDto(e);
    }
  }

  @Override
  public Flux<BtcBlockDto> blockFlux() {
    return notificationFlux("node.bitcoind.notification.block.port", btcDaemon::blockFlux, block ->
            new BtcBlockDto(block.getHash(), block.getHeight(), block.getTime()));
  }

  @Override
  public Flux<BtcTransactionDto> walletFlux() {
    return notificationFlux("node.bitcoind.notification.wallet.port", btcDaemon::walletFlux, this::convert);
  }

  @Override
  public Flux<BtcTransactionDto> instantSendFlux() {
    return notificationFlux("node.bitcoind.notification.instantsend.port", btcDaemon::instantSendFlux, this::convert);
  }

  private <S, T> Flux<T> notificationFlux(String portProperty, Function<String, Flux<S>> source, Function<S, T> mapper) {
    if (btcdClient != null) {
      String port = btcdClient.getNodeConfig().getProperty(portProperty);
      return source.apply(port).map(mapper);
    } else {
      log.error("Client not initialized!");
      return Flux.empty();
    }
  }


    @Override
    public BtcPreparedTransactionDto prepareRawTransaction(Map<String, BigDecimal> payments) {
        return prepareRawTransaction(payments, null);
    }

    @Override
    public BtcPreparedTransactionDto prepareRawTransaction(Map<String, BigDecimal> payments, @Nullable String oldTxHex) {
        try {

          FundingResult fundingResult;
          synchronized (SENDING_LOCK) {
            if (oldTxHex != null && unlockingTasks.containsKey(oldTxHex)) {

              unlockingTasks.get(oldTxHex).cancel(true);
              // unlock previously locked UTXO

              lockUnspentFromHex(oldTxHex, true);

            }

            String initialTxHex = btcdClient.createRawTransaction(new ArrayList<>(), payments);
            fundingResult = btcdClient.fundRawTransaction(initialTxHex);
            lockUnspentFromHex(fundingResult.getHex(), false);
          }

          unlockingTasks.put(fundingResult.getHex(),
                  CompletableFuture.runAsync(() -> {
                    try {
                      Thread.sleep(120_000);
                      lockUnspentFromHex(fundingResult.getHex(), true);
                    } catch (InterruptedException e) {
                      log.warn("output unlock interrupted for hex: " + fundingResult.getHex());
                    }

                  }, outputUnlockingExecutor)
                          .thenRun(() -> unlockingTasks.remove(fundingResult.getHex())));



          /*outputUnlockingExecutor.schedule(() -> {
                // unlock UTXO after 2 minutes - in case of no action;
                lockUnspentFromHex(fundingResult.getHex(), true);
            },
                    2, TimeUnit.MINUTES);*/

            return new BtcPreparedTransactionDto(payments, fundingResult.getFee(), fundingResult.getHex());
        } catch (BitcoindException | CommunicationException e) {
            throw new BitcoinCoreException(e);
        }
    }


    private void lockUnspentFromHex(String hex, boolean unlock)  {
        try {
            RawTransactionOverview txOverview = btcdClient.decodeRawTransaction(hex);
            btcdClient.lockUnspent(unlock, txOverview.getVIn().stream()
                    .map(vin -> new OutputOverview(vin.getTxId(), vin.getVOut())).collect(Collectors.toList()));
        } catch (BitcoindException | CommunicationException e) {
            log.error(e);
        }
    }

    @Override
  public BtcPaymentResultDto signAndSendRawTransaction(String hex) {
      try {
          checkLockStateForRawTx(hex);
          SignatureResult signatureResult = btcdClient.signRawTransaction(hex);
          if (!signatureResult.getComplete()) {
              throw new BitcoinCoreException("Signature failed!");
          }
          String txId = btcdClient.sendRawTransaction(signatureResult.getHex(), false);
          return new BtcPaymentResultDto(txId);
      } catch (Exception e) {
          log.error(e);
          return new BtcPaymentResultDto(e);
      }
  }


  private void checkLockStateForRawTx(String hex) {
      try {
          Set<BtcTxOutputDto> lockedOutputSet = btcdClient.listLockUnspent().stream()
                  .map(out -> new BtcTxOutputDto(out.getTxId(), out.getVOut())).collect(Collectors.toSet());

           boolean txOutputsLocked = btcdClient.decodeRawTransaction(hex).getVIn().stream()
                  .map(vin -> new BtcTxOutputDto(vin.getTxId(), vin.getVOut())).allMatch(lockedOutputSet::contains);

           if (!txOutputsLocked) {
               throw new IllegalStateException("Transaction outputs already unlocked!");
           }

      } catch (BitcoindException | CommunicationException e) {
          log.error(e);
      }
  }

  @Override
  public String getTxIdByHex(String hex) {
    try {
      return btcdClient.decodeRawTransaction(hex).getTxId();
    } catch (BitcoindException | CommunicationException e) {
      throw new BitcoinCoreException("Cannot decode tx " + hex, e);
    }
  }

  @PreDestroy
  private void shutDown() {
    outputUnlockingExecutor.shutdown();
  }




}
