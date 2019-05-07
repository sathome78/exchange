package me.exrates.service;

import me.exrates.model.enums.TransferProcessTypeEnum;
import me.exrates.service.merchantStrategy.ITransferable;

public interface TransferSimpleService extends ITransferable {

  @Override
  default public Boolean isVoucher() {
    return false;
  }

  @Override
  default public Boolean recipientUserIsNeeded() {
    return true;
  }

  @Override
  default public TransferProcessTypeEnum processType(){
    return TransferProcessTypeEnum.TRANSFER;
  }
}
