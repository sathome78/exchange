package me.exrates.service.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.math.RoundingMode;

@NoArgsConstructor(access = AccessLevel.NONE)
public class BigDecimalConverter {

    public static BigDecimal convert(BigDecimal initialValue) {
        if (BigDecimal.valueOf(0.0001).compareTo(initialValue) > 0) {
            initialValue = initialValue.setScale(7, RoundingMode.HALF_UP);
        } else if (BigDecimal.valueOf(0.0001).compareTo(initialValue) < 0 && BigDecimal.valueOf(0.001).compareTo(initialValue) > 0) {
            initialValue = initialValue.setScale(6, RoundingMode.HALF_UP);
        } else if (BigDecimal.valueOf(0.001).compareTo(initialValue) < 0 && BigDecimal.valueOf(0.01).compareTo(initialValue) > 0) {
            initialValue = initialValue.setScale(5, RoundingMode.HALF_UP);
        } else if (BigDecimal.valueOf(0.01).compareTo(initialValue) < 0 && BigDecimal.valueOf(0.1).compareTo(initialValue) > 0) {
            initialValue = initialValue.setScale(4, RoundingMode.HALF_UP);
        } else if (BigDecimal.valueOf(0.1).compareTo(initialValue) < 0 && BigDecimal.valueOf(1).compareTo(initialValue) > 0) {
            initialValue = initialValue.setScale(3, RoundingMode.HALF_UP);
        } else if (BigDecimal.ONE.compareTo(initialValue) < 0 && BigDecimal.valueOf(100).compareTo(initialValue) > 0) {
            initialValue = initialValue.setScale(0, RoundingMode.HALF_UP);
        } else if (BigDecimal.valueOf(100).compareTo(initialValue) < 0 && BigDecimal.valueOf(10000).compareTo(initialValue) > 0) {
            initialValue = initialValue.setScale(-1, RoundingMode.HALF_UP);
        } else if (BigDecimal.valueOf(10000).compareTo(initialValue) < 0 && BigDecimal.valueOf(100000).compareTo(initialValue) > 0) {
            initialValue = initialValue.setScale(-2, RoundingMode.HALF_UP);
        } else if (BigDecimal.valueOf(100000).compareTo(initialValue) < 0) {
            initialValue = initialValue.setScale(-3, RoundingMode.HALF_UP);
        }
        return BigDecimal.valueOf(initialValue.doubleValue());
    }
}