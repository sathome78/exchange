package me.exrates.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import me.exrates.model.enums.OperationType;

import java.math.BigDecimal;

@Data
@Builder(builderClassName = "Builder")
@AllArgsConstructor
@NoArgsConstructor
public class CurrencyLimit {

    private int id;
    private Currency currency;
    private OperationType operationType;
    private BigDecimal minSum;
    private BigDecimal maxSum;
    private Integer maxDailyRequest;
    private BigDecimal currencyUsdRate;
    private BigDecimal minSumUsdRate;
    private BigDecimal maxSumUsd;
    private boolean recalculateToUsd;
}
