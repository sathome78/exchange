package me.exrates.service.merchantStrategy;

import me.exrates.model.dto.TransferRequestCreateDto;
import me.exrates.model.enums.TransferProcessTypeEnum;

import java.util.Map;

public interface ITransferable  extends IMerchantService {

  Map<String, String> transfer(TransferRequestCreateDto transferRequestCreateDto);

  Boolean isVoucher();

  Boolean recipientUserIsNeeded();

  TransferProcessTypeEnum processType();

}
