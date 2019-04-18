package me.exrates.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@NoArgsConstructor
@Data
@AllArgsConstructor
@Builder
public class IEOResult {

    private int claimId;
    private int ieoId;
    private IEOResultStatus status;
    private BigDecimal availableAmount;

    public enum IEOResultStatus {
        SUCCESS, FAILED, NONE, REVOKED
    }
}
