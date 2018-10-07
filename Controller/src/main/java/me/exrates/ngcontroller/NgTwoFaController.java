package me.exrates.ngcontroller;

import me.exrates.model.NotificationOption;
import me.exrates.model.User;
import me.exrates.model.dto.Generic2faResponseDto;
import me.exrates.model.enums.NotificationEvent;
import me.exrates.security.exception.IncorrectPinException;
import me.exrates.service.NotificationService;
import me.exrates.service.UserService;
import me.exrates.service.exception.IncorrectSmsPinException;
import org.omg.CORBA.UNKNOWN;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.view.RedirectView;

import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;
import java.security.Principal;
import java.util.Collections;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping(value = "/info/private/v2/2FaOptions/",
        produces = MediaType.APPLICATION_JSON_UTF8_VALUE,
        consumes = MediaType.APPLICATION_JSON_UTF8_VALUE
)
public class NgTwoFaController {

    private static final String GOOGLE_2FA = "google2fa";
    public static final String VERIFY_GOOGLE = "verify_google2fa";


    private final NotificationService notificationService;
    private final UserService userService;

    @Autowired
    public NgTwoFaController(NotificationService notificationService, UserService userService) {
        this.notificationService = notificationService;
        this.userService = userService;
    }

    @GetMapping(GOOGLE_2FA)
    @ResponseBody
    public Generic2faResponseDto getGoogle2FA() throws UnsupportedEncodingException {
        return new Generic2faResponseDto(notificationService.generateQRUrl(getPrincipalEmail()));
    }

    @GetMapping(GOOGLE_2FA + "/verify")
    public ResponseEntity<Void> verifyGoogleAuthenticatorConnect(@RequestParam String code, Principal principal) {
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

    // only added support to google notification
    @GetMapping(GOOGLE_2FA + "/user")
    @ResponseBody
    public Map<NotificationEvent, Boolean> getUserNotifications() {
        try {
            int userId = userService.getIdByEmail(getPrincipalEmail());
            return notificationService
                    .getNotificationOptionsByUser(userId)
                    .stream()
                    .collect(Collectors.toMap(NotificationOption::getEvent, NotificationOption::isSendEmail));
        } catch (Exception e) {
            return Collections.emptyMap();
        }
    }

    private String getPrincipalEmail() {
        return SecurityContextHolder.getContext().getAuthentication().getName();
    }

}
