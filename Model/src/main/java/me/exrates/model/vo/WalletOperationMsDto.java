package me.exrates.model.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor
public class WalletOperationMsDto {

    private WalletOperationData walletOperationData;
    private int currencyId;
}

