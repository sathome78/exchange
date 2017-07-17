package me.exrates.service;

import me.exrates.model.enums.TransferProcessTypeEnum;
import me.exrates.service.merchantStrategy.ITransferable;

/**
 * Created by ValkSam on 02.06.2017.
 */
public interface TransferVoucherFreeService extends ITransferable {

  @Override
  default public Boolean isVoucher() {
    return true;
  }

  @Override
  default public Boolean recipientUserIsNeeded() {
    return false;
  }

  @Override
  default public TransferProcessTypeEnum processType() {
    return TransferProcessTypeEnum.VOUCHER_FREE;
  }

}
