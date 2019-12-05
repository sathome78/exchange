package me.exrates.model.dto.referral;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class UserReferralLink {
    private int userId;
    private String link;
}
