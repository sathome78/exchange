package me.exrates.service.btcCore;

import me.exrates.model.dto.BtcTransactionHistoryDto;
import me.exrates.model.dto.BtcWalletInfoDto;
import me.exrates.model.dto.TxReceivedByAddressFlatDto;
import me.exrates.model.dto.merchants.btc.*;
import reactor.core.publisher.Flux;

import javax.annotation.Nullable;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Created by OLEG on 14.03.2017.
 */
public interface CoreWalletService {
  void initCoreClient(String nodePropertySource, boolean supportInstantSend, boolean supportSubtractFee);
  
  void initBtcdDaemon(boolean zmqEnabled);
  
  String getNewAddress(String walletPassword);
  
  void backupWallet(String backupFolder);

  void shutdown();

  Optional<BtcTransactionDto> handleTransactionConflicts(String txId);
  
  BtcTransactionDto getTransaction(String txId);
  
  BtcWalletInfoDto getWalletInfo();
  
  List<TxReceivedByAddressFlatDto> listReceivedByAddress(Integer minConfirmations);
  
  List<BtcTransactionHistoryDto> listAllTransactions();

    List<BtcPaymentFlatDto> listSinceBlockEx(@Nullable String blockHash, Integer merchantId, Integer currencyId);

    List<BtcPaymentFlatDto> listSinceBlock(String blockHash, Integer merchantId, Integer currencyId);
  
  BigDecimal estimateFee(int blockCount);
  
  BigDecimal getActualFee();
  
  void setTxFee(BigDecimal fee);
  
  void submitWalletPassword(String password);
  
  String sendToAddressAuto(String address, BigDecimal amount, String walletPassword);
  
  BtcPaymentResultDto sendToMany(Map<String, BigDecimal> payments, boolean subtractFeeFromAmount);

    Flux<BtcBlockDto> blockFlux();

    Flux<BtcTransactionDto> walletFlux();

  Flux<BtcTransactionDto> instantSendFlux();

    BtcPreparedTransactionDto prepareRawTransaction(Map<String, BigDecimal> payments);

    BtcPreparedTransactionDto prepareRawTransaction(Map<String, BigDecimal> payments, @Nullable String oldTxHex);

    BtcPaymentResultDto signAndSendRawTransaction(String hex);

    String getTxIdByHex(String hex);

  String getLastBlockHash();
}
