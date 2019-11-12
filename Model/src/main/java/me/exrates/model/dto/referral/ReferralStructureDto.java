package me.exrates.model.dto.referral;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ReferralStructureDto {
    private int numberChild;
    private String name;
    private String link;
    private BigDecimal earnedBTC;
    private BigDecimal earnedUSD;
    private BigDecimal earnedUSDT;
    private boolean main;
}
