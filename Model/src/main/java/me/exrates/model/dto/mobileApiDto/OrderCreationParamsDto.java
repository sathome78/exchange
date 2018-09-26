package me.exrates.model.dto.mobileApiDto;

import me.exrates.model.enums.OperationType;
import me.exrates.model.enums.OrderBaseType;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

/**
 * Created by OLEG on 01.09.2016.
 */
public class OrderCreationParamsDto {
    @NotNull
    private Integer currencyPairId;
    @NotNull
    private OperationType orderType;
    @NotNull
    private BigDecimal amount;
    @NotNull
    private BigDecimal rate;

    private OrderBaseType baseType;

    private BigDecimal stopRate;

    public Integer getCurrencyPairId() {
        return currencyPairId;
    }

    public void setCurrencyPairId(Integer currencyPairId) {
        this.currencyPairId = currencyPairId;
    }

    public OperationType getOrderType() {
        return orderType;
    }

    public void setOrderType(OperationType orderType) {
        this.orderType = orderType;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public BigDecimal getRate() {
        return rate;
    }

    public void setRate(BigDecimal rate) {
        this.rate = rate;
    }

    public OrderBaseType getBaseType() {
        return baseType;
    }

    public void setBaseType(OrderBaseType baseType) {
        this.baseType = baseType;
    }

    public BigDecimal getStopRate() {
        return stopRate;
    }

    public void setStopRate(BigDecimal stopRate) {
        this.stopRate = stopRate;
    }

    @Override
    public String toString() {
        return "OrderCreationParamsDto{" +
                "currencyPairId=" + currencyPairId +
                ", orderType=" + orderType +
                ", amount=" + amount +
                ", rate=" + rate +
                ", baseType=" + baseType +
                ", stopRate=" + stopRate +
                '}';
    }
}
