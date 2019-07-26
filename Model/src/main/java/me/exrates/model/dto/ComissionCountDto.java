package me.exrates.model.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class ComissionCountDto {

    private String name;
    private BigDecimal withdrawComission;
    private BigDecimal refillComission;
    private BigDecimal transferComission;
    private BigDecimal tradeComission;
    private BigDecimal referralPayments;
    private BigDecimal total;

    public void countTotal() {
        this.total = withdrawComission.add(refillComission).add(transferComission).add(tradeComission).subtract(referralPayments);
    }
}
