package me.exrates.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;
import me.exrates.model.enums.OperationType;

import java.math.BigDecimal;

/**
 * Created by OLEG on 26.01.2017.
 */
@Getter@Setter
public class CurrencyLimit {
    private Currency currency;
    private OperationType operationType;
    private BigDecimal minSum;
    private BigDecimal maxSum;
    private Integer maxDailyRequest;
    private BigDecimal currencyUsdRate;
    private BigDecimal minSumUsdRate;
    private BigDecimal refillReviewLimitUsdOnce;
    private BigDecimal refillReviewLimitCoinOnce;
    private BigDecimal refillReviewLimitUsdDay;
    private BigDecimal refillReviewLimitCoinDay;
}
