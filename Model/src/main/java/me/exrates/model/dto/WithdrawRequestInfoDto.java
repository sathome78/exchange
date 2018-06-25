package me.exrates.model.dto;

import lombok.Data;
import me.exrates.model.enums.invoice.WithdrawStatusEnum;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Created by Maks on 25.06.2018.
 */
@Data
public class WithdrawRequestInfoDto {

    private Integer requestId;

    private BigDecimal amount;

    private BigDecimal comissionAmount;

    private BigDecimal merchantComissionAmount;

    private BigDecimal finalAmount;

    private BigDecimal totalFee;

    private String userEmail;

    private String currencyName;

    private String merchantName;

    private String merchantDescription;

    private int delaySeconds;

    private String message;

    private WithdrawStatusEnum statusEnum;

    public void calculateFinalAmount() {
        finalAmount = amount.subtract(comissionAmount).subtract(merchantComissionAmount).setScale(8, RoundingMode.HALF_UP);
        totalFee = merchantComissionAmount.add(comissionAmount).setScale(8, RoundingMode.HALF_UP);
    }
}
