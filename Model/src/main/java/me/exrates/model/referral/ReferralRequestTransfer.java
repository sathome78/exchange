package me.exrates.model.referral;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import me.exrates.model.referral.enums.ReferralRequestStatus;

import java.math.BigDecimal;
import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ReferralRequestTransfer {
    private int id;
    private int currencyId;
    private String currencyName;
    private int userId;
    private BigDecimal amount;
    private Integer transactionId;
    private String remark;
    private Date createdAt;
    private Date statusModificationDate;
    private ReferralRequestStatus status;
}
