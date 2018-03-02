package me.exrates.controller.ngContorollers;

import com.google.common.base.Preconditions;
import lombok.extern.log4j.Log4j2;
import me.exrates.controller.exception.ErrorInfo;
import me.exrates.controller.exception.FileLoadingException;
import me.exrates.controller.exception.NewsCreationException;
import me.exrates.controller.exception.NoFileForLoadingException;
import me.exrates.model.Notification;
import me.exrates.model.NotificationOption;
import me.exrates.model.SessionParams;
import me.exrates.model.User;
import me.exrates.model.UserFile;
import me.exrates.model.dto.NotificationsUserSetting;
import me.exrates.model.dto.NotificatorSubscription;
import me.exrates.model.dto.NotificatorTotalPriceDto;
import me.exrates.model.dto.SmsSubscriptionDto;
import me.exrates.model.dto.TelegramSubscription;
import me.exrates.model.dto.UpdateUserDto;
import me.exrates.model.dto.ngDto.UserSettingsDto;
import me.exrates.model.enums.ActionType;
import me.exrates.model.enums.NotificationMessageEventEnum;
import me.exrates.model.enums.NotificationTypeEnum;
import me.exrates.model.enums.SessionLifeTypeEnum;
import me.exrates.model.enums.UserRole;
import me.exrates.model.form.NotificationOptionsForm;
import me.exrates.security.service.AuthTokenService;
import me.exrates.service.NotificationService;
import me.exrates.service.SessionParamsService;
import me.exrates.service.UserService;
import me.exrates.service.exception.IncorrectSmsPinException;
import me.exrates.service.exception.PaymentException;
import me.exrates.service.exception.ServiceUnavailableException;
import me.exrates.service.exception.UnoperableNumberException;
import me.exrates.service.notifications.NotificationsSettingsService;
import me.exrates.service.notifications.NotificatorsService;
import me.exrates.service.notifications.Subscribable;
import org.apache.commons.lang.math.NumberUtils;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.security.Principal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import static me.exrates.model.util.BigDecimalProcessing.doAction;
import static me.exrates.service.util.RestApiUtils.decodePassword;

/**
 * Created by Maks on 07.02.2018.
 */
@Log4j2
@RestController
@RequestMapping("/info/private/settings")
public class SettingsNgController1 {

    @Autowired
    private UserService userService;
    @Autowired
    private NotificationService notificationService;
    @Autowired
    private MessageSource messageSource;
    @Autowired
    private SessionParamsService sessionService;
    @Autowired
    private NotificationsSettingsService settingsService;
    @Autowired
    private NotificatorsService notificatorService;

    @Autowired
    private AuthTokenService authTokenService;

    @Value("${telegram.bot.url}")
    String TBOT_URL;
    @Value("${telegram_bot_name}")
    String TBOT_NAME;

    @PutMapping(value = "/updateMainPassword", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<Void> updateMainPassword(@RequestBody Map<String, String> body, HttpServletRequest request){
        String email = SecurityContextHolder.getContext().getAuthentication().getName();

        User user = userService.findByEmail(email);
        String encodedPassword = body.getOrDefault("pass", "");
        if(encodedPassword.isEmpty()){
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        user.setPassword(decodePassword(encodedPassword));
        if (userService.update(getUpdateUserDto(user), userService.getUserLocaleForMobile(email))){
            return new ResponseEntity<>(HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_ACCEPTABLE);
        }
    }

    @PutMapping(value = "/updateFinPassword", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<Void> updateFinPassword(@RequestBody Map<String, String> body, HttpServletRequest request){
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userService.findByEmail(email);
        String encodedPassword = body.getOrDefault("pass", "");
        if(encodedPassword.isEmpty()){
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        user.setFinpassword(decodePassword(encodedPassword));
        if (userService.update(getUpdateUserDto(user), userService.getUserLocaleForMobile(email))){
            return new ResponseEntity<>(HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_ACCEPTABLE);
        }
    }

    @PutMapping(value = "/updateAuthorization", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<Void> updateAuthorization(@RequestBody Map<String, String> body){
        String userEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        try {
            int userId = userService.getIdByEmail(userEmail);
            settingsService.updateUser2FactorSettings(userId, body);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.unprocessableEntity().build();
        }
    }

    @GetMapping(value = "/user2FactorAuthSettings", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<Map<NotificationMessageEventEnum, NotificationTypeEnum>> getAuthSettings(HttpServletRequest request){
        String userEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        try {
            int userId = userService.getIdByEmail(userEmail);
            Map<NotificationMessageEventEnum, NotificationTypeEnum> settings =
                    new HashMap<>(settingsService.getUser2FactorSettings(userId));
            return new ResponseEntity<>(settings, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.NOT_ACCEPTABLE);
        }
    }

    @PutMapping(value = "/updateDocuments", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<Void> updateDocuments(@RequestBody Map<String, Boolean> body, HttpServletRequest request){
        String email = SecurityContextHolder.getContext().getAuthentication().getName();

        return null;
    }

    @GetMapping(value = "/notifications", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public List<NotificationOption> getUserNotifications(){
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        try {
            int userId = userService.getIdByEmail(email);
            return notificationService.getNotificationOptionsByUser(userId);
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }

    @PutMapping(value = "/updateNotifications", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<Void> updateNotifications(@RequestBody  List<NotificationOption> options){
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
//        try {
//            int userId = userService.getIdByEmail(email);
//            return notificationService.updateUserNotificationOptions(userId, options);
//        } catch (Exception e) {
//            return new ArrayList<>();
//        }
//        return null;
    }

    @PutMapping(value = "/updateSessionPeriod", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<Void> updateSessionPeriod(@RequestBody Map<String, Boolean> body, HttpServletRequest request){
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        if (!(authTokenService.getUsernameFromToken(request).equals(email))) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        return null;
    }


    private UpdateUserDto getUpdateUserDto(User user) {
        UpdateUserDto dto = new UpdateUserDto(user.getId());
        dto.setEmail(user.getEmail());
        dto.setFinpassword(user.getFinpassword());
        dto.setPassword(user.getPassword());
        dto.setRole(user.getRole());
        dto.setStatus(user.getStatus());
        dto.setPhone(user.getPhone());
        return dto;
    }

    @ResponseStatus(HttpStatus.NOT_IMPLEMENTED)
    @ExceptionHandler(NoFileForLoadingException.class)
    @ResponseBody
    public ErrorInfo NoFileForLoadingExceptionHandler(HttpServletRequest req, Exception exception) {
        return new ErrorInfo(req.getRequestURL(), exception);
    }

    @ResponseStatus(HttpStatus.NOT_IMPLEMENTED)
    @ExceptionHandler(FileLoadingException.class)
    @ResponseBody
    public ErrorInfo FileLoadingExceptionHandler(HttpServletRequest req, Exception exception) {
        return new ErrorInfo(req.getRequestURL(), exception);
    }

    @ResponseStatus(HttpStatus.NOT_IMPLEMENTED)
    @ExceptionHandler(NewsCreationException.class)
    @ResponseBody
    public ErrorInfo NewsCreationExceptionHandler(HttpServletRequest req, Exception exception) {
        return new ErrorInfo(req.getRequestURL(), exception);
    }

    @ResponseStatus(HttpStatus.NOT_IMPLEMENTED)
    @ExceptionHandler(ServiceUnavailableException.class)
    @ResponseBody
    public ErrorInfo SmsSubscribeExceptionHandler(HttpServletRequest req, Exception exception) {
        String userEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        Locale locale = userService.getUserLocaleForMobile(userEmail);
        return new ErrorInfo(req.getRequestURL(), exception,
                messageSource.getMessage("message.service.unavialble", null, locale));
    }

    @ResponseStatus(HttpStatus.NOT_IMPLEMENTED)
    @ExceptionHandler(IncorrectSmsPinException.class)
    @ResponseBody
    public ErrorInfo IncorrectSmsPinExceptionHandler(HttpServletRequest req, Exception exception) {
        String userEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        Locale locale = userService.getUserLocaleForMobile(userEmail);
        return new ErrorInfo(req.getRequestURL(), exception,
                messageSource.getMessage("message.connectCode.wrong", null, locale));
    }

    @ResponseStatus(HttpStatus.NOT_IMPLEMENTED)
    @ExceptionHandler(UnoperableNumberException.class)
    @ResponseBody
    public ErrorInfo SmsSubscribeUnoperableNumberExceptionHandler(HttpServletRequest req, Exception exception) {
        String userEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        Locale locale = userService.getUserLocaleForMobile(userEmail);
        return new ErrorInfo(req.getRequestURL(), exception,
        messageSource.getMessage("message.numberUnoperable", null, locale));
    }

    @ResponseStatus(HttpStatus.NOT_IMPLEMENTED)
    @ExceptionHandler(PaymentException.class)
    @ResponseBody
    public ErrorInfo msSubscribeMoneyExceptionHandler(HttpServletRequest req, Exception exception) {
        String userEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        Locale locale = userService.getUserLocaleForMobile(userEmail);
        return new ErrorInfo(req.getRequestURL(), exception,
        messageSource.getMessage("message.notEnoughtUsd", null, locale));
    }
}
