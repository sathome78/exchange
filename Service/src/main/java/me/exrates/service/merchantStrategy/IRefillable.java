package me.exrates.service.merchantStrategy;

import me.exrates.model.dto.RefillRequestCreateDto;
import me.exrates.service.exception.RefillRequestAppropriateNotFoundException;

import java.util.Map;

/**
 * Created by ValkSam on 24.03.2017.
 */
public interface IRefillable extends IMerchantService{

  Map<String, String> refill(RefillRequestCreateDto request);

  void processPayment(Map<String, String> params) throws RefillRequestAppropriateNotFoundException;

  Boolean createdRefillRequestRecordNeeded();

  Boolean needToCreateRefillRequestRecord();

  Boolean toMainAccountTransferringConfirmNeeded();

  Boolean generatingAdditionalRefillAddressAvailable();

  Boolean additionalFieldForRefillIsUsed();

  default String additionalRefillFieldName() {
    return "MEMO";
  };
}
