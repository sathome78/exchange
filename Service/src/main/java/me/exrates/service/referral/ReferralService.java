package me.exrates.service.referral;

import me.exrates.model.dto.referral.ReferralStructureDto;
import me.exrates.model.referral.ReferralRequest;

import java.util.List;

public interface ReferralService {

    void saveReferralRequest(List<ReferralRequest> requests);

    List<ReferralStructureDto> getReferralStructure(String email);

    List<ReferralStructureDto> getChildReferralStructure(String email, int userId, int level, String link);

    boolean updateReferralName(String email, String link, String name);

    ReferralStructureDto createReferralLink(String email, String name);
}
