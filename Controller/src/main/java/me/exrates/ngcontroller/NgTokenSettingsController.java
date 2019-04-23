package me.exrates.ngcontroller;

import lombok.extern.log4j.Log4j2;
import me.exrates.model.OpenApiToken;
import me.exrates.model.User;
import me.exrates.model.constants.ErrorApiTitles;
import me.exrates.model.dto.openAPI.OpenApiTokenPublicDto;
import me.exrates.model.enums.NotificationMessageEventEnum;
import me.exrates.model.ngExceptions.NgResponseException;
import me.exrates.security.service.SecureService;
import me.exrates.service.OpenApiTokenService;
import me.exrates.service.UserService;
import me.exrates.service.notifications.G2faService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Log4j2
@RestController
@RequestMapping(value = "/api/private/v2/settings/token",
        produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
public class NgTokenSettingsController {

    private final OpenApiTokenService openApiTokenService;
    private final UserService userService;
    private final SecureService secureService;
    private final G2faService g2faService;

    @Autowired
    public NgTokenSettingsController(OpenApiTokenService openApiTokenService,
                                     UserService userService,
                                     SecureService secureService,
                                     G2faService g2faService) {
        this.openApiTokenService = openApiTokenService;
        this.userService = userService;
        this.secureService = secureService;
        this.g2faService = g2faService;
    }

    @GetMapping("/findAll")
    public ResponseEntity<List<OpenApiTokenPublicDto>> getUserTokens() {
        return ResponseEntity.ok(openApiTokenService.getUserTokens(userService.getUserEmailFromSecurityContext()));
    }

    @PostMapping("/create")
    public ResponseEntity<OpenApiToken> tokenCreate(@RequestParam String alias,
                                                    @RequestParam String pin,
                                                    HttpServletRequest request) {
        final String userEmail = userService.getUserEmailFromSecurityContext();

        check2faAuthorization(userEmail, pin, request);

        try {
            return ResponseEntity.ok(openApiTokenService.generateToken(userEmail, alias));
        } catch (IllegalArgumentException ex) {
            throw new NgResponseException(ErrorApiTitles.API_KEY_ALIAS_ERROR, ex.getMessage());
        }
    }

    @PostMapping("/allowTrade")
    public ResponseEntity allowTrade(@RequestParam Long tokenId,
                                     @RequestParam Boolean allowTrade,
                                     @RequestParam(required = false) String pin,
                                     HttpServletRequest request) {
        final String userEmail = userService.getUserEmailFromSecurityContext();

        if (allowTrade) {
            check2faAuthorization(userEmail, pin, request);
        }

        openApiTokenService.updateToken(tokenId, allowTrade, userEmail);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/delete")
    public ResponseEntity deleteToken(@RequestParam Long tokenId) {
        openApiTokenService.deleteToken(tokenId, userService.getUserEmailFromSecurityContext());
        return ResponseEntity.ok().build();
    }

    @PostMapping("/pin")
    public ResponseEntity sendApiTokenPinCode(HttpServletRequest request) {
        String userEmail = userService.getUserEmailFromSecurityContext();
        try {
            User user = userService.findByEmail(userEmail);
            boolean googleAuthenticatorEnabled = g2faService.isGoogleAuthenticatorEnable(user.getId());
            if (!googleAuthenticatorEnabled) {
                secureService.sendApiTokenPincode(user, request);
                return ResponseEntity.status(HttpStatus.CREATED).build();
            }
            return ResponseEntity.ok().build();
        } catch (Exception ex) {
            log.error("Failed to send pin code on user email", ex);
            final String message = "Failed to send pin code on user email";
            throw new NgResponseException(ErrorApiTitles.FAILED_TO_SEND_PIN_CODE_ON_USER_EMAIL, message);
        }
    }

    private void check2faAuthorization(String userEmail, String pin, HttpServletRequest request) {
        User user = userService.findByEmail(userEmail);

        boolean googleAuthenticatorEnabled = g2faService.isGoogleAuthenticatorEnable(user.getId());
        if (StringUtils.isEmpty(pin)) {
            String mode = googleAuthenticatorEnabled ? "GOOGLE" : "EMAIL";
            String message = String.format("User with email: %s must login with %s authorization code", userEmail, mode);
            String title = String.format(ErrorApiTitles.REQUIRED_MODE_AUTHORIZATION_CODE, mode);
            throw new NgResponseException(title, message);
        }
        if (googleAuthenticatorEnabled) {
            if (!g2faService.checkGoogle2faVerifyCode(pin, user.getId())) {
                final String message = String.format("Invalid google 2fa authorization code from user %s", userEmail);
                throw new NgResponseException(ErrorApiTitles.VERIFY_GOOGLE2FA_FAILED, message);
            }
        } else {
            if (!userService.checkPin(userEmail, pin, NotificationMessageEventEnum.API_TOKEN_SETTING)) {
                secureService.sendApiTokenPincode(user, request);

                final String message = String.format("Invalid google 2fa authorization code from user %s", userEmail);
                throw new NgResponseException(ErrorApiTitles.VERIFY_GOOGLE2FA_FAILED, message);
            }
        }
    }
}