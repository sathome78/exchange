package me.exrates.service;

import me.exrates.model.CreditsOperation;
import me.exrates.service.merchantStrategy.IRefillable;
import me.exrates.service.merchantStrategy.IWithdrawable;

import java.util.Map;

public interface Privat24Service extends IRefillable, IWithdrawable {

    Map<String, String> preparePayment(CreditsOperation creditsOperation, String email);

    boolean confirmPayment(Map<String, String> params, String signature, String payment);

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
