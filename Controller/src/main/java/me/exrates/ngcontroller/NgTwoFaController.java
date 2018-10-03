package me.exrates.ngcontroller;

import me.exrates.model.dto.Generic2faResponseDto;
import me.exrates.service.NotificationService;
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

    private final NotificationService notificationService;

    @Autowired
    public NgTwoFaController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @GetMapping(value = GOOGLE_2FA)
    @ResponseBody
    public Generic2faResponseDto getGoogle2FA() throws UnsupportedEncodingException {
        return new Generic2faResponseDto(notificationService.generateQRUrl(getPrincipalEmail()));
    }

    private String getPrincipalEmail() {
        return SecurityContextHolder.getContext().getAuthentication().getName();
    }

}
