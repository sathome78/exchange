package me.exrates.model.referral;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReferralLink {
    private int userId;
    private String name;
    private String link;
    private Date createdAt;
    private boolean main;
}
