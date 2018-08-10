package me.exrates.model;

import me.exrates.model.enums.OperationType;

import java.math.BigDecimal;

/**
 * Created by OLEG on 26.01.2017.
 */
public class CurrencyLimit {
    private Currency currency;
    private OperationType operationType;
    private BigDecimal rateUsdAdditional;
    private BigDecimal calcMinSum;
    private BigDecimal minSum;
    private BigDecimal maxSum;
    private Integer maxDailyRequest;

    public Currency getCurrency() {
        return currency;
    }

    public void setCurrency(Currency currency) {
        this.currency = currency;
    }

    public OperationType getOperationType() {
        return operationType;
    }

    public void setOperationType(OperationType operationType) {
        this.operationType = operationType;
    }

    public BigDecimal getRateUsdAdditional() {
        return rateUsdAdditional;
    }

    public void setRateUsdAdditional(BigDecimal rateUsdAdditional) {
        this.rateUsdAdditional = rateUsdAdditional;
    }

    public BigDecimal getCalcMinSum() {
        return calcMinSum;
    }

    public void setCalcMinSum(BigDecimal calcMinSum) {
        this.calcMinSum = calcMinSum;
    }

    public BigDecimal getMinSum() {
        return minSum;
    }

    public void setMinSum(BigDecimal minSum) {
        this.minSum = minSum;
    }

    public BigDecimal getMaxSum() {
        return maxSum;
    }

    public void setMaxSum(BigDecimal maxSum) {
        this.maxSum = maxSum;
    }

    public Integer getMaxDailyRequest() {
        return maxDailyRequest;
    }

    public void setMaxDailyRequest(Integer maxDailyRequest) {
        this.maxDailyRequest = maxDailyRequest;
    }

    @Override
    public String toString() {
        return "CurrencyLimit{" +
                "currency=" + currency +
                ", operationType=" + operationType +
                ", rateUsdAdditional=" + rateUsdAdditional +
                ", calcMinSum=" + calcMinSum +
                ", minSum=" + minSum +
                ", maxSum=" + maxSum +
                ", maxDailyRequest=" + maxDailyRequest +
                '}';
    }
}
