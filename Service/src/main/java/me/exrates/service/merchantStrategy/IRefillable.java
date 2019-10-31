package me.exrates.service.merchantStrategy;

import com.neemre.btcdcli4j.core.BitcoindException;
import com.neemre.btcdcli4j.core.CommunicationException;
import me.exrates.model.dto.RefillRequestCreateDto;
import me.exrates.service.exception.NotImplimentedMethod;
import me.exrates.service.exception.RefillRequestAppropriateNotFoundException;
import me.exrates.service.syndex.SyndexService;

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

  default Boolean storeSameAddressForParentAndTokens() {
    return false;
  };

  default String additionalRefillFieldName() {
    return "MEMO";
  };

  default Integer minConfirmationsRefill() {
    return null;
  };

  default boolean concatAdditionalToMainAddress() { return false; }

  default String getMerchantName(){
    return "Not defined";
  }

  default void cancelMerchantRequest(int id, String email) {
    /*default - do nothing*/
  }

  default void declineAdmin(int id) {
    /*default - do nothing*/
  }

  default void acceptAdmin(int id) {
    /*default - do nothing*/
  }

  default long getBlocksCount() throws BitcoindException, CommunicationException {
    throw new NotImplimentedMethod("Not implemented yet");
  }

  default Long getLastBlockTime() throws BitcoindException, CommunicationException, BitcoindException, CommunicationException {
    throw new NotImplimentedMethod("Not implemented yet");
  }

}
