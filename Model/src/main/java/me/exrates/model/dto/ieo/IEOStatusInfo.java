package me.exrates.model.dto.ieo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import me.exrates.model.dto.kyc.KycCountryDto;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class IEOStatusInfo {
    private boolean kycCheck;
    private boolean policyCheck;
    private boolean countryCheck;
    private KycCountryDto country;
}
