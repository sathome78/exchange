package me.exrates.ngcontroller;

import me.exrates.service.referral.ReferralService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/api/private/v2/referral")
public class NgReferralController {

    private final ReferralService referralService;

    @Autowired
    public NgReferralController(ReferralService referralService) {
        this.referralService = referralService;
    }

    @GetMapping(value = "/my", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<?> getMyReferralStructure() {
        String email = getPrincipalEmail();

        return null;
    }


    private String getPrincipalEmail() {
        return SecurityContextHolder.getContext().getAuthentication().getName();
    }
}
