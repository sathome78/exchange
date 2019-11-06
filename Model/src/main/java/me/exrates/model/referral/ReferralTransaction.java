package me.exrates.model.referral;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ReferralTransaction {
    private int id;
    private int currencyId;
    private String currencyName;
    private int userId;
    private BigDecimal amount;
    private String link;

}
