package me.exrates.service.referral;

import me.exrates.model.dto.referral.ReferralStructureDto;
import me.exrates.model.referral.ReferralRequest;

import java.util.List;

public interface ReferralService {
    void saveReferralRequest(List<ReferralRequest> requests);

    List<ReferralStructureDto> getReferralStructure(String email);
}
