package me.exrates.service;

import me.exrates.model.enums.TransferProcessTypeEnum;
import me.exrates.service.merchantStrategy.ITransferable;

public interface TransferVoucherService extends ITransferable {

  @Override
  default public Boolean isVoucher() {
    return true;
  }

  @Override
  default public Boolean recipientUserIsNeeded() {
    return true;
  }

  @Override
  default public TransferProcessTypeEnum processType() {
    return TransferProcessTypeEnum.VOUCHER;
  }

}
