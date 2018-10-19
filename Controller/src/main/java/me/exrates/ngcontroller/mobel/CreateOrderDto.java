package me.exrates.ngcontroller.mobel;

import me.exrates.model.enums.OperationType;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

public class CreateOrderDto {

    @NotNull
    private String currencyPair;
    @NotNull
    private OperationType orderType;
    @NotNull
    private BigDecimal amount;
    @NotNull
    private BigDecimal rate;
    @NotNull
    private BigDecimal commission;

    public String getCurrencyPair() {
        return currencyPair;
    }

    public void setCurrencyPair(String currencyPair) {
        this.currencyPair = currencyPair;
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

    public BigDecimal getCommission() {
        return commission;
    }

    public void setCommission(BigDecimal commission) {
        this.commission = commission;
    }
}
