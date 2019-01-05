package me.exrates.model.dto;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
public class BalancesShortDto {

    private BigDecimal balanceUsd;
    private BigDecimal balanceBtc;

    public static BalancesShortDto zeroBalances() {
        return new BalancesShortDto(BigDecimal.ZERO, BigDecimal.ZERO);
    }
}
