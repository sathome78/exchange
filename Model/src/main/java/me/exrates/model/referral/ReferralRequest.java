package me.exrates.model.referral;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import me.exrates.model.referral.enums.ReferralProcessStatus;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ReferralRequest {
    private int id;
    private int userId;
    private int currencyId;
    private int orderId;
    private BigDecimal amount;
    private ReferralProcessStatus processStatus;

    public static ReferralRequest of(int userId, int currencyId, BigDecimal amount, int orderId) {
        ReferralRequest request = new ReferralRequest();
        request.setUserId(userId);
        request.setCurrencyId(currencyId);
        request.setAmount(amount);
        request.setProcessStatus(ReferralProcessStatus.CREATED);
        return request;
    }
}
