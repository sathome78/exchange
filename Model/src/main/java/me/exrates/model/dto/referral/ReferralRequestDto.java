package me.exrates.model.dto.referral;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReferralRequestDto {
    private int userId;
    private int currencyId;
    private BigDecimal amount;
}
