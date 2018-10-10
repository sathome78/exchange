package me.exrates.service;

import me.exrates.model.CreditsOperation;
import me.exrates.service.merchantStrategy.IRefillable;
import me.exrates.service.merchantStrategy.IWithdrawable;

import java.util.Map;

public interface YandexKassaService  extends IRefillable, IWithdrawable {

    /**
     * This method of prepearing parameters for the payment form
     * @param creditsOperation
     * @param email
     * @return Map with parameters
     */
    Map<String, String> preparePayment(CreditsOperation creditsOperation, String email);

    /**
     * Confirms payment in DB
     * @param params
     * @return true if checks accepted, false if none
     */
    boolean confirmPayment(final Map<String,String> params);

  @Override
  default Boolean createdRefillRequestRecordNeeded() {
    return null;
  }

  @Override
  default Boolean needToCreateRefillRequestRecord() {
    return null;
  }

  @Override
  default Boolean toMainAccountTransferringConfirmNeeded() {
    return null;
  }

  @Override
  default Boolean generatingAdditionalRefillAddressAvailable() {
    return null;
  }

  @Override
  default Boolean additionalTagForWithdrawAddressIsUsed() {
    return false;
  }

  @Override
  default Boolean withdrawTransferringConfirmNeeded() {
    return null;
  }

  @Override
  default Boolean additionalFieldForRefillIsUsed() {
    return false;
  }
}
