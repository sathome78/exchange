package me.exrates.model.referral;

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
public class ReferralTransaction {
    private Integer id;
    private int currencyId;
    private String currencyName;
    private int userIdFrom;
    private int userIdTo;
    private BigDecimal amount;
}
