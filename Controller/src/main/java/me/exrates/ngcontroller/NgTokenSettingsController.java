package me.exrates.ngcontroller;

import me.exrates.model.OpenApiToken;
import me.exrates.model.constants.ErrorApiTitles;
import me.exrates.model.dto.openAPI.OpenApiTokenPublicDto;
import me.exrates.model.ngExceptions.NgResponseException;
import me.exrates.service.OpenApiTokenService;
import me.exrates.service.UserService;
import me.exrates.service.notifications.G2faService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(value = "/api/private/v2/settings/token",
        produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
public class NgTokenSettingsController {

    private final OpenApiTokenService openApiTokenService;
    private final UserService userService;
    private final G2faService g2faService;

    @Autowired
    public NgTokenSettingsController(OpenApiTokenService openApiTokenService,
                                     UserService userService,
                                     G2faService g2faService) {
        this.openApiTokenService = openApiTokenService;
        this.userService = userService;
        this.g2faService = g2faService;
    }

    @GetMapping("/findAll")
    public ResponseEntity<List<OpenApiTokenPublicDto>> getUserTokens() {
        return ResponseEntity.ok(openApiTokenService.getUserTokens(userService.getUserEmailFromSecurityContext()));
    }

    @PostMapping("/create")
    public ResponseEntity<OpenApiToken> tokenCreate(@RequestParam String alias,
                                                    @RequestParam String pin) {
        final String userEmail = userService.getUserEmailFromSecurityContext();

        check2faAuthorization(userEmail, pin);

        try {
            return ResponseEntity.ok(openApiTokenService.generateToken(userEmail, alias));
        } catch (IllegalArgumentException ex) {
            throw new NgResponseException(ErrorApiTitles.API_KEY_ALIAS_ERROR, ex.getMessage());
        }
    }

    @PostMapping("/allowTrade")
    public ResponseEntity allowTrade(@RequestParam Long tokenId,
                                     @RequestParam Boolean allowTrade,
                                     @RequestParam String pin) {
        final String userEmail = userService.getUserEmailFromSecurityContext();

        check2faAuthorization(userEmail, pin);

        openApiTokenService.updateToken(tokenId, allowTrade, userEmail);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/delete")
    public ResponseEntity deleteToken(@RequestParam Long tokenId,
                                      @RequestParam String pin) {
        final String userEmail = userService.getUserEmailFromSecurityContext();

        check2faAuthorization(userEmail, pin);

        openApiTokenService.deleteToken(tokenId, userEmail);
        return ResponseEntity.ok().build();
    }

    private void check2faAuthorization(String userEmail, String pin) {
        int userId = userService.getIdByEmail(userEmail);
        if (!g2faService.isGoogleAuthenticatorEnable(userId)) {
            String message = String.format("Google 2fa authorization is not enabled for user %s", userEmail);
            throw new NgResponseException(ErrorApiTitles.GOOGLE2FA_DISABLED, message);
        }
        if (!g2faService.checkGoogle2faVerifyCode(pin, userId)) {
            String message = String.format("Invalid google 2fa authorization code from user %s", userEmail);
            throw new NgResponseException(ErrorApiTitles.VERIFY_GOOGLE2FA_FAILED, message);
        }
    }
}