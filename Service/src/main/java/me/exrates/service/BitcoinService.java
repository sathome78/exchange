package me.exrates.service;

import me.exrates.model.dto.BtcTransactionHistoryDto;
import me.exrates.model.dto.BtcWalletInfoDto;
import me.exrates.model.dto.merchants.btc.*;
import me.exrates.service.events.BtcBlockEvent;
import me.exrates.service.events.BtcWalletEvent;
import me.exrates.service.merchantStrategy.IRefillable;
import me.exrates.service.merchantStrategy.IWithdrawable;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Scheduled;

import java.math.BigDecimal;
import java.util.List;

public interface BitcoinService extends IRefillable, IWithdrawable {

  int CONFIRMATION_NEEDED_COUNT = 4;

    boolean isRawTxEnabled();

    @EventListener(value = BtcWalletEvent.class)
    void onPayment(BtcTransactionDto transactionDto);

    @EventListener(value = BtcBlockEvent.class)
    void onIncomingBlock(BtcBlockDto blockDto);

    @Scheduled(initialDelay = 5 * 60000, fixedDelay = 12 * 60 * 60000)
  void backupWallet();
  
  BtcWalletInfoDto getWalletInfo();
  
  List<BtcTransactionHistoryDto> listAllTransactions();
  
  BigDecimal estimateFee();

    String getEstimatedFeeString();

    BigDecimal getActualFee();
  
  void setTxFee(BigDecimal fee);
  
  void submitWalletPassword(String password);
  
  List<BtcPaymentResultDetailedDto> sendToMany(List<BtcWalletPaymentItemDto> payments);

  @Override
  default Boolean createdRefillRequestRecordNeeded() {
    return false;
  }

  @Override
  default Boolean needToCreateRefillRequestRecord() {
    return false;
  }

  @Override
  default Boolean toMainAccountTransferringConfirmNeeded() {
    return false;
  }

  @Override
  default Boolean generatingAdditionalRefillAddressAvailable() {
    return true;
  }

  @Override
  default Boolean additionalTagForWithdrawAddressIsUsed() {
    return false;
  }

  @Override
  default Boolean withdrawTransferringConfirmNeeded() {
    return false;
  }

  @Override
  default Boolean additionalFieldForRefillIsUsed() {
    return false;
  }

    BtcAdminPreparedTxDto prepareRawTransactions(List<BtcWalletPaymentItemDto> payments);

    BtcAdminPreparedTxDto updateRawTransactions(List<BtcPreparedTransactionDto> preparedTransactions);

    List<BtcPaymentResultDetailedDto> sendRawTransactions(List<BtcPreparedTransactionDto> preparedTransactions);

  String getNewAddressForAdmin();

    void setSubtractFeeFromAmount(boolean subtractFeeFromAmount);

    boolean getSubtractFeeFromAmount();
}
