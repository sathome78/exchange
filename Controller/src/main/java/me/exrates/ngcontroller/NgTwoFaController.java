package me.exrates.ngcontroller;

import me.exrates.model.NotificationOption;
import me.exrates.model.User;
import me.exrates.model.dto.Generic2faResponseDto;
import me.exrates.model.dto.NotificationsUserSetting;
import me.exrates.model.enums.NotificationEvent;
import me.exrates.model.enums.NotificationMessageEventEnum;
import me.exrates.security.exception.IncorrectPinException;
import me.exrates.service.NotificationService;
import me.exrates.service.UserService;
import me.exrates.service.notifications.NotificationsSettingsService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.web3j.abi.datatypes.Int;

import java.io.UnsupportedEncodingException;
import java.util.Collections;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping(value = "/info/private/v2/2FaOptions/",
        produces = MediaType.APPLICATION_JSON_UTF8_VALUE,
        consumes = MediaType.APPLICATION_JSON_UTF8_VALUE
)
public class NgTwoFaController {

    private static final Logger logger = LogManager.getLogger(NgUserSettingsController.class);

    private static final String GOOGLE_2FA = "google2fa";
    private static final String VERIFY_GOOGLE = "verify_google2fa";


    private final NotificationService notificationService;
    private final NotificationsSettingsService notificationsSettingsService;
    private final UserService userService;

    @Autowired
    public NgTwoFaController(NotificationService notificationService, NotificationsSettingsService notificationsSettingsService, UserService userService) {
        this.notificationService = notificationService;
        this.notificationsSettingsService = notificationsSettingsService;
        this.userService = userService;
    }

    @GetMapping(GOOGLE_2FA)
    @ResponseBody
    public Generic2faResponseDto getGoogle2FA() throws UnsupportedEncodingException {
        return new Generic2faResponseDto(notificationService.generateQRUrl(getPrincipalEmail()));
    }

    @GetMapping(GOOGLE_2FA + "/hash")
    @ResponseBody
    public Generic2faResponseDto getSecurityCode() {
        Integer userId = userService.getIdByEmail(getPrincipalEmail());
        Generic2faResponseDto result = new Generic2faResponseDto("");
        try {
            result.setMessage(this.notificationService.getGoogleAuthenticatorCode(userId));
        } catch (Exception e) {
            logger.info("Failed to retrieve secret code for user with id: {}, as {} ",
                    userId, e.getLocalizedMessage());
            result.setError(e.getLocalizedMessage());
        }
        return result;
    }

    @GetMapping(GOOGLE_2FA + "/verify")
    public ResponseEntity<Void> verifyGoogleAuthenticatorConnect(@RequestParam String code) {
        User user = userService.findByEmail(getPrincipalEmail());
        if (notificationService.checkGoogle2faVerifyCode(code, user.getId())) {
            return new ResponseEntity<>(HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.FORBIDDEN);
    }

    @PatchMapping(GOOGLE_2FA)
    public ResponseEntity<Void> toggleGoogleTwoFaAuthentication(@RequestBody Map<String, Boolean> params) {
        boolean enabled = params.containsKey("STATE") && params.get("STATE");
        Integer userId = userService.getIdByEmail(getPrincipalEmail());

        try {
            if (enabled) {
                notificationService.getGoogleAuthenticatorCode(userId);
                notificationService.updateGoogleAuthenticatorSecretCodeForUser(userId);
            }
            notificationService.setEnable2faGoogleAuth(userId, enabled);
            return new ResponseEntity<>(HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);
        }
    }

    @GetMapping(value = VERIFY_GOOGLE)
    @ResponseBody
    public String verifyGoogleAuthenticatorConnect(@RequestParam String code,
                                                   @RequestParam boolean connected) {
        Integer userId = userService.getIdByEmail(getPrincipalEmail());
        if (!notificationService.checkGoogle2faVerifyCode(code, userId)) {
            throw new IncorrectPinException("");
        }
        if (connected) {
            notificationService.setEnable2faGoogleAuth(userId, connected);
        } else {
            notificationService.setEnable2faGoogleAuth(userId, false);
            notificationService.updateGoogleAuthenticatorSecretCodeForUser(userId);
        }
        return "OK";
    }

    @GetMapping(GOOGLE_2FA + "/user")
    @ResponseBody
    public Map<NotificationMessageEventEnum, Boolean> getUserNotifications() {
        try {
            User user = userService.findByEmail(getPrincipalEmail());
            return notificationsSettingsService.getUserTwoFASettings(user);
        } catch (Exception e) {
            return Collections.emptyMap();
        }
    }

    @PutMapping(GOOGLE_2FA + "/user")
    public ResponseEntity<Void> updateUser2FaNotificationSettings(@RequestBody NotificationsUserSetting setting) {
        Integer userId = userService.getIdByEmail(getPrincipalEmail());
        try {
            setting.setUserId(userId);
            this.notificationsSettingsService.createOrUpdate(setting);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (Exception e) {
            logger.info("Failed to update user settings for userId: {}, as {}", userId, e.getLocalizedMessage());
        }
        return new ResponseEntity<>(HttpStatus.UNPROCESSABLE_ENTITY);
    }

    private String getPrincipalEmail() {
        return SecurityContextHolder.getContext().getAuthentication().getName();
    }

}
