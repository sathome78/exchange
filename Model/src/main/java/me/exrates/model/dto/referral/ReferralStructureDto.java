package me.exrates.model.dto.referral;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@Builder
public class ReferralStructureDto {
    private Integer numberChild;
    private String name;
    private String link;
    private BigDecimal earnedBTC;
    private BigDecimal earnedUSD;
    private BigDecimal earnedUSDT;
    private boolean main;
    private Integer level;
    private Integer userId;
    private String userEmail;

    public ReferralStructureDto(String name, String link, boolean main) {
        this.name = name;
        this.link = link;
        this.earnedBTC = new BigDecimal(0);
        this.earnedUSD = new BigDecimal(0);
        this.earnedUSDT = new BigDecimal(0);
        this.main = main;
    }

    public ReferralStructureDto(String email, int level, int userId) {
        this.userEmail = email;
        this.userId = userId;
        this.level = level;
        this.earnedBTC = new BigDecimal(0);
        this.earnedUSD = new BigDecimal(0);
        this.earnedUSDT = new BigDecimal(0);
    }


}
