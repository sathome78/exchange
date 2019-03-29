package me.exrates.ngcontroller;

import me.exrates.model.User;
import me.exrates.model.constants.ErrorApiTitles;
import me.exrates.model.dto.Generic2faResponseDto;
import me.exrates.model.ngExceptions.NgResponseException;
import me.exrates.security.service.NgUserService;
import me.exrates.service.UserService;
import me.exrates.service.notifications.G2faService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

@RestController
@RequestMapping(value = "/api/private/v2/2FaOptions/",
        produces = MediaType.APPLICATION_JSON_UTF8_VALUE,
        consumes = MediaType.APPLICATION_JSON_UTF8_VALUE
)
public class NgTwoFaController {

    private static final Logger logger = LogManager.getLogger(NgUserSettingsController.class);

    private final UserService userService;
    private final G2faService g2faService;
    private final NgUserService ngUserService;

    @Autowired
    public NgTwoFaController(UserService userService,
                             G2faService g2faService,
                             NgUserService ngUserService) {
        this.userService = userService;
        this.g2faService = g2faService;
        this.ngUserService = ngUserService;
    }

    @GetMapping("/google2fa/hash")
    @ResponseBody
    public Generic2faResponseDto getSecurityCode() {
        Integer userId = userService.getIdByEmail(getPrincipalEmail());
        return g2faService.getGoogleAuthenticatorCodeNg(userId);
    }

    @GetMapping("/google2fa/pin")
    @ResponseBody
    public ResponseEntity<Void> getSecurityPinCode(HttpServletRequest request) {
        User user = userService.findByEmail(getPrincipalEmail());
        g2faService.sendGoogleAuthPinConfirm(user, request);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/google2fa/submit")
    public ResponseEntity<Void> submitGoogleSecret(@RequestBody Map<String, String> body) {
        User user = userService.findByEmail(getPrincipalEmail());
        boolean result = g2faService.submitGoogleSecret(user, body);
        if (result) {
            ngUserService.sendEmailEnable2Fa(user.getEmail());
            return ResponseEntity.ok().build();
        }
        String message = String.format("Incorrectly filled data %s", body);
        throw new NgResponseException(ErrorApiTitles.GOOGLE2FA_SUBMIT_FAILED, message);
    }

    @PutMapping("/google2fa/disable")
    public ResponseEntity<Void> disableGoogleAuthentication(@RequestBody Map<String, String> body) {
        User user = userService.findByEmail(getPrincipalEmail());
        boolean result = g2faService.disableGoogleAuth(user, body);
        if (result) {
            ngUserService.sendEmailDisable2Fa(user.getEmail());
            return ResponseEntity.ok().build();
        }
        String message = String.format("Incorrectly filled data %s", body);
        throw new NgResponseException(ErrorApiTitles.GOOGLE2FA_DISABLE_FAILED, message);
    }

    @GetMapping(value = "/verify_google2fa")
    @ResponseBody
    public ResponseEntity<Boolean> verifyGoogleAuthenticatorCode(@RequestParam String code) {
        Integer userId = userService.getIdByEmail(getPrincipalEmail());
        if (g2faService.checkGoogle2faVerifyCode(code, userId)) {
            return ResponseEntity.ok(Boolean.TRUE);
        }
        String message = String.format("Verification code [%s] wrong", code);
        throw new NgResponseException(ErrorApiTitles.VERIFY_GOOGLE2FA_FAILED, message);
    }

    private String getPrincipalEmail() {
        return SecurityContextHolder.getContext().getAuthentication().getName();
    }
}
