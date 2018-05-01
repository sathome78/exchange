package me.exrates.model.dto;

import lombok.Builder;
import lombok.Data;

/**
 * Created by Maks on 01.05.2018.
 */
@Data
@Builder(toBuilder = true)
public class EthTokenWithdrawInfoDto {

    private WithdrawMerchantOperationDto withdrawMerchantOperationDto;
    private Object tokenService;
    private String merchantName;
}
