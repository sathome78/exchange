package me.exrates.service.btcCore;

import com.neemre.btcdcli4j.core.BitcoindException;
import com.neemre.btcdcli4j.core.CommunicationException;
import me.exrates.model.PagingData;
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
import java.util.Properties;

/**
 * Created by OLEG on 14.03.2017.
 */
public interface CoreWalletService {
  void initCoreClient(String nodePropertySource, Properties passPropertySource, boolean supportInstantSend, boolean supportSubtractFee, boolean supportReferenceLine);
  
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

  BtcBlockDto getBlockByHash(String blockHash);

    long getBlocksCount() throws BitcoindException, CommunicationException;

    Long getLastBlockTime() throws BitcoindException, CommunicationException;

  List<BtcTransactionHistoryDto> listTransaction(int page);

  PagingData<List<BtcTransactionHistoryDto>> listTransaction(int start, int length, String searchValue);

  List<BtcTransactionHistoryDto> getTransactionsByPage(int page, int transactionsPerPage) throws BitcoindException, CommunicationException;

  List<BtcTransactionHistoryDto> getTransactionsForPagination(int start, int length) throws BitcoindException, CommunicationException;

  List<BtcTransactionHistoryDto> findTransactions(String value) throws BitcoindException, CommunicationException;
}
