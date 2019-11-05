package me.exrates.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import me.exrates.model.dto.mobileApiDto.MerchantCurrencyApiDto;

import java.util.List;

@Data
@AllArgsConstructor
public class UserMerchantCurrencyOperationDto {

    private boolean isOperationRestrictedToUser;
    private List<MerchantCurrencyApiDto> merchantCurrencies;
}
