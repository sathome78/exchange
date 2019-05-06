package me.exrates.model.dto.ieo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class IeoUserStatus {
    private String kycStatus;
    private String countryCode;
    private boolean policyAccepted;
}
