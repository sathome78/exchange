package me.exrates.model.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class WithdrawableDataDto {
    private Boolean additionalTagForWithdrawAddressIsUsed;
    private String additionalWithdrawFieldName;

}
