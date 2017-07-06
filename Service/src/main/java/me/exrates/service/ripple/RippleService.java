package me.exrates.service.ripple;

import me.exrates.model.CreditsOperation;
import me.exrates.service.merchantStrategy.IMerchantService;
import me.exrates.service.merchantStrategy.IRefillable;
import me.exrates.service.merchantStrategy.IWithdrawable;
import org.json.JSONObject;

/**
 * Created by maks on 11.05.2017.
 */
public interface RippleService extends IRefillable, IWithdrawable {

    /*method for admin manual check transaction by hash*/
    void manualCheckNotReceivedTransaction(String hash);

    /*return: true if tx validated; false if not validated but validationin process,
        throws Exception if declined*/
    boolean checkSendedTransaction(String hash, String additionalParams);

    void onTransactionReceive(JSONObject result);

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
    return false;
  }

  @Override
  default Boolean additionalTagForWithdrawAddressIsUsed() {
    return true;
  }

  @Override
  default Boolean additionalFieldForRefillIsUsed() {
    return true;
  };

  @Override
  default Boolean withdrawTransferringConfirmNeeded() {
    return true;
  }

  @Override
  default String additionalWithdrawFieldName() {
    return "Destination Tag";
  }

  @Override
  default String additionalRefillFieldName() {
    return "Destination Tag";
  }
}
