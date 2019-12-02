package me.exrates.model.dto.referral;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class ReferralIncomeDto {
    private Integer userId;
    private String email;
    private Integer currencyId;
    private String currencyName;
    private String currencyDescription;
    private BigDecimal cupIncome;
    private BigDecimal referralBalance;
    private boolean available;
    private BigDecimal leftForCup;
    private BigDecimal manualConfirmAboveSum;
}
