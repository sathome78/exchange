package me.exrates.ngcontroller;

import me.exrates.model.dto.Generic2faResponseDto;
import me.exrates.security.exception.IncorrectPinException;
import me.exrates.service.NotificationService;
import me.exrates.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.io.UnsupportedEncodingException;

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

    @GetMapping(value = GOOGLE_2FA)
    @ResponseBody
    public Generic2faResponseDto getGoogle2FA() throws UnsupportedEncodingException {
        return new Generic2faResponseDto(notificationService.generateQRUrl(getPrincipalEmail()));
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

    private String getPrincipalEmail() {
        return SecurityContextHolder.getContext().getAuthentication().getName();
    }

}
