package me.exrates.service.stopOrder;

import me.exrates.model.enums.OperationType;

import java.math.BigDecimal;

public interface RatesHolder {

    void onRateChange(int pairId, OperationType operationType, BigDecimal rate);

    BigDecimal getCurrentRate(int pairId, OperationType operationType);
}
