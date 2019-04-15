package me.exrates.ngcontroller;

import com.fasterxml.jackson.databind.ObjectMapper;
import me.exrates.controller.exception.ErrorInfo;
import me.exrates.dao.exception.notfound.UserNotFoundException;
import me.exrates.model.NotificationOption;
import me.exrates.model.SessionParams;
import me.exrates.model.User;
import me.exrates.model.constants.Constants;
import me.exrates.model.constants.ErrorApiTitles;
import me.exrates.model.dto.PageLayoutSettingsDto;
import me.exrates.model.dto.UpdateUserDto;
import me.exrates.model.dto.UserNotificationMessage;
import me.exrates.model.dto.mobileApiDto.AuthTokenDto;
import me.exrates.model.enums.ColorScheme;
import me.exrates.model.enums.NotificationEvent;
import me.exrates.model.enums.SessionLifeTypeEnum;
import me.exrates.model.enums.UserNotificationType;
import me.exrates.model.ngExceptions.NgDashboardException;
import me.exrates.model.ngExceptions.NgResponseException;
import me.exrates.model.ngExceptions.WrongPasswordException;
import me.exrates.model.ngModel.ExceptionDto;
import me.exrates.model.ngModel.UserDocVerificationDto;
import me.exrates.model.ngModel.UserInfoVerificationDto;
import me.exrates.model.ngModel.enums.VerificationDocumentType;
import me.exrates.model.ngModel.response.ResponseModel;
import me.exrates.ngService.UserVerificationService;
import me.exrates.security.ipsecurity.IpBlockingService;
import me.exrates.security.ipsecurity.IpTypesOfChecking;
import me.exrates.security.service.AuthTokenService;
import me.exrates.security.service.CheckIp;
import me.exrates.service.NotificationService;
import me.exrates.service.PageLayoutSettingsService;
import me.exrates.service.SessionParamsService;
import me.exrates.service.UserService;
import me.exrates.service.stomp.StompMessenger;
import me.exrates.service.util.RestApiUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.nio.file.NoSuchFileException;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping(value = "/api/private/v2/settings",
        consumes = MediaType.APPLICATION_JSON_UTF8_VALUE,
        produces = MediaType.APPLICATION_JSON_UTF8_VALUE
)
public class NgUserSettingsController {

    private static final Logger logger = LogManager.getLogger(NgUserSettingsController.class);

    private static final String NICKNAME = "/nickname";
    private static final String SESSION_INTERVAL = "/sessionInterval";
    private static final String EMAIL_NOTIFICATION = "/notifications";
    private static final String COLOR_SCHEME = "/color-schema";
    private static final String IS_COLOR_BLIND = "/isLowColorEnabled";
    private static final String STATE = "/STATE";

    private final AuthTokenService authTokenService;
    private final UserService userService;
    private final NotificationService notificationService;
    private final SessionParamsService sessionService;
    private final PageLayoutSettingsService layoutSettingsService;
    private final UserVerificationService verificationService;
    private final IpBlockingService ipBlockingService;
    private final StompMessenger stompMessenger;
    private final ObjectMapper objectMapper;

    @Value("${contacts.feedbackEmail}")
    String feedbackEmail;

    @Autowired
    public NgUserSettingsController(AuthTokenService authTokenService,
                                    UserService userService,
                                    NotificationService notificationService,
                                    SessionParamsService sessionParamsService,
                                    PageLayoutSettingsService pageLayoutSettingsService,
                                    UserVerificationService userVerificationService,
                                    IpBlockingService ipBlockingService,
                                    StompMessenger stompMessenger,
                                    ObjectMapper objectMapper) {
        this.authTokenService = authTokenService;
        this.userService = userService;
        this.notificationService = notificationService;
        this.sessionService = sessionParamsService;
        this.layoutSettingsService = pageLayoutSettingsService;
        this.verificationService = userVerificationService;
        this.ipBlockingService = ipBlockingService;
        this.stompMessenger = stompMessenger;
        this.objectMapper = objectMapper;
    }

    // /info/private/v2/settings/updateMainPassword
    // body: {
    //    currentPassword: string,
    //    newPassword: string
    // }
    // 200 - OK
    // 400 - 1011 - either current or new password is blank
    // 400 - 1010 - wrong main user password
    @PutMapping(value = "/updateMainPassword", consumes = MediaType.APPLICATION_JSON_VALUE)
    @CheckIp(value = IpTypesOfChecking.UPDATE_MAIN_PASSWORD)
    public ResponseEntity<Void> updateMainPassword(@RequestBody Map<String, String> body, HttpServletRequest request) {
        String email = getPrincipalEmail();
        User user = userService.findByEmail(email);
        Locale locale = userService.getUserLocaleForMobile(email);
        String currentPassword = body.getOrDefault("currentPassword", "");
        String newPassword = body.getOrDefault("newPassword", "");
        if (StringUtils.isBlank(currentPassword) || StringUtils.isBlank(newPassword)) {
            String message = String.format("Failed as current password: [%s] or new password is [%s] is empty ", currentPassword, newPassword);
            logger.warn(message);
            throw new NgDashboardException(message, Constants.ErrorApi.USER_INCORRECT_PASSWORDS);
        }
        currentPassword = RestApiUtils.decodePassword(currentPassword);
        if (!userService.checkPassword(user.getId(), currentPassword)) {
            String clientIp = Optional.ofNullable(request.getHeader("X-Forwarded-For")).orElse("");
            String message = String.format("Failed to check password for user: %s from ip: %s ", user.getEmail(), clientIp);
            logger.warn(message);
            ipBlockingService.failureProcessing(clientIp, IpTypesOfChecking.UPDATE_MAIN_PASSWORD);
            throw new NgDashboardException(message, Constants.ErrorApi.USER_WRONG_CURRENT_PASSWORD);
        }
        newPassword = RestApiUtils.decodePassword(newPassword);
        user.setPassword(newPassword);
        user.setConfirmPassword(newPassword);
        //   registerFormValidation.validateResetPassword(user, result, locale);
        if (userService.update(getUpdateUserDto(user), locale)) {
            ipBlockingService.successfulProcessing(request.getHeader("X-Forwarded-For"), IpTypesOfChecking.UPDATE_MAIN_PASSWORD);
            return new ResponseEntity<>(HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_ACCEPTABLE);
        }
    }

    @GetMapping(value = NICKNAME)
    public ResponseEntity<Map<String, String>> getNickName() {
        User user = userService.findByEmail(getPrincipalEmail());
        String nickname = user.getNickname() == null ? "" : user.getNickname();
        return ResponseEntity.ok(Collections.singletonMap(NICKNAME, nickname));
    }

    @PutMapping(value = NICKNAME, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> updateNickName(@RequestBody Map<String, String> body) {
        User user = userService.findByEmail(getPrincipalEmail());
        if (body.containsKey(NICKNAME)) {
            user.setNickname(body.get(NICKNAME));
            if (userService.setNickname(user.getNickname(), user.getEmail())) {
                return new ResponseEntity<>(HttpStatus.OK);
            }
            return new ResponseEntity<>(HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @GetMapping(value = SESSION_INTERVAL)
    public ResponseModel<Integer> getSessionPeriod() {
        SessionParams params = sessionService.getByEmailOrDefault(getPrincipalEmail());
        if (null == params) {
            return new ResponseModel<>(0);
        }
        return new ResponseModel<>(params.getSessionTimeMinutes());
    }

    @PutMapping(value = SESSION_INTERVAL, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> updateSessionPeriod(@RequestBody Map<String, Integer> body) {
        try {
            int interval = body.get("sessionInterval");
            SessionParams sessionParams = new SessionParams(interval, SessionLifeTypeEnum.INACTIVE_COUNT_LIFETIME.getTypeId());
            if (sessionService.isSessionTimeValid(sessionParams.getSessionTimeMinutes())) {
                sessionService.saveOrUpdate(sessionParams, getPrincipalEmail());
//                sessionService.setSessionLifeParams(request);
                //todo inform user to logout to implement params next time
                return new ResponseEntity<>(HttpStatus.OK);
            } else {
                return new ResponseEntity<>(HttpStatus.NOT_ACCEPTABLE);
            }
        } catch (Exception e) {
            String message = String.format("Update session period %s failed", body.get("sessionInterval"));
            throw new NgResponseException(ErrorApiTitles.UPDATE_SESSION_PERIOD_FAILED, message);
        }
    }

    @GetMapping(value = EMAIL_NOTIFICATION)
    public Map<NotificationEvent, Boolean> getUserNotifications() {
        try {
            int userId = userService.getIdByEmail(getPrincipalEmail());
            return notificationService
                    .getNotificationOptionsByUser(userId)
                    .stream()
                    .collect(Collectors.toMap(
                            NotificationOption::getEvent,
                            NotificationOption::isSendEmail));
        } catch (Exception e) {
            return Collections.emptyMap();
        }
    }

    @PutMapping(value = EMAIL_NOTIFICATION, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> updateUserNotification(@RequestBody List<NotificationOption> options) {
        try {
            int userId = userService.getIdByEmail(getPrincipalEmail());
            notificationService.updateNotificationOptionsForUser(userId, options);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (Exception e) {
            String message = "Update user notification failed";
            throw new NgResponseException(ErrorApiTitles.UPDATE_USER_NOTIFICATION_FAILED, message);
        }
    }

    @GetMapping(IS_COLOR_BLIND)
    @ResponseBody
    public Boolean getUserColorDepth() {
        User user = userService.findByEmail(getPrincipalEmail());
        PageLayoutSettingsDto dto = this.layoutSettingsService.findByUser(user);
        return dto != null && dto.isLowColorEnabled();
    }

    @PutMapping(value = IS_COLOR_BLIND, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> updateUserColorDepth(@RequestBody Map<String, Boolean> params) {
        if (params.containsKey(STATE)) {
            User user = userService.findByEmail(getPrincipalEmail());
            this.layoutSettingsService.toggleLowColorMode(user, params.get(STATE));
            return new ResponseEntity<>(HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.UNPROCESSABLE_ENTITY);
        }
    }

//    @GetMapping(COLOR_SCHEME)
//    @ResponseBody
//    public ColorScheme getUserColorScheme() {
//        User user = userService.findByEmail(getPrincipalEmail());
//        return this.layoutSettingsService.getColorScheme(user);
//    }

    @PutMapping(value = COLOR_SCHEME, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> updateUserColorScheme(@RequestBody Map<String, String> params) {
        if (params.containsKey("SCHEME")) {
            int userId = userService.getIdByEmail(getPrincipalEmail());
            PageLayoutSettingsDto settingsDto = PageLayoutSettingsDto
                    .builder()
                    .userId(userId)
                    .scheme(ColorScheme.of(params.get("SCHEME")))
                    .build();
            this.layoutSettingsService.save(settingsDto);
            return new ResponseEntity<>(HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.UNPROCESSABLE_ENTITY);
        }
    }

    @PostMapping(value = "/docs")
    public ResponseEntity uploadUserVerification(@RequestBody @Valid UserInfoVerificationDto data) {
        logger.info("UserInfoVerificationDto - {}", data);
        int userId = userService.getIdByEmail(getPrincipalEmail());
        data.setUserId(userId);

        UserInfoVerificationDto attempt = verificationService.save(data);
        if (attempt != null) {
            return new ResponseEntity<>(HttpStatus.CREATED);
        }
        String message = "Upload user verification failed";
        throw new NgResponseException(ErrorApiTitles.UPLOAD_USER_VERIFICATION_FAILED, message);
    }

    @PostMapping("/userFiles/docs/{type}")
    public ResponseEntity<Void> uploadUserVerificationDocs(@RequestBody Map<String, String> body,
                                                           @PathVariable("type") String type) {

        VerificationDocumentType documentType = VerificationDocumentType.of(type);
        int userId = userService.getIdByEmail(getPrincipalEmail());
        String encoded = body.getOrDefault("BASE_64", "");

        if (StringUtils.isEmpty(encoded)) {
            String message = "Upload user verification code failed. String encoded is empty.";
            throw new NgResponseException(ErrorApiTitles.UPLOAD_USER_VERIFICATION_DOCS_FAILED, message);
        }
        UserDocVerificationDto data = new UserDocVerificationDto(userId, documentType, encoded);

        UserDocVerificationDto attempt = verificationService.save(data);
        if (attempt != null) {
            return new ResponseEntity<>(HttpStatus.CREATED);
        }
        String message = "Upload user verification code failed. Invalid path variable and/or request body.";
        throw new NgResponseException(ErrorApiTitles.UPLOAD_USER_VERIFICATION_DOCS_FAILED, message);
    }

    @GetMapping("/currency_pair/favourites")
    @ResponseBody
    public List<Integer> getUserFavouriteCurrencyPairs() {
        return userService.getUserFavouriteCurrencyPairs(getPrincipalEmail());
    }

    @PutMapping("/currency_pair/favourites")
    public ResponseEntity<Void> manegeUserFavouriteCurrencyPairs(@RequestBody Map<String, String> params) {
        int currencyPairId;
        boolean toDelete;
        try {
            currencyPairId = Integer.parseInt(params.get("PAIR_ID"));
            toDelete = Boolean.valueOf(params.get("TO_DELETE"));
        } catch (Exception e) {
            logger.info("Failed to convert attributes as {}", e.getMessage());
            String message = String.format("Failed to convert attributes %s.", params);
            throw new NgResponseException(ErrorApiTitles.FAILED_MANAGE_USER_FAVORITE_CURRENCY_PAIRS, message);
        }
        boolean result = userService.manageUserFavouriteCurrencyPair(getPrincipalEmail(), currencyPairId, toDelete);
        if (result) {
            return ResponseEntity.ok().build();
        }
        String message = "Cannot find user by email.";
        throw new NgResponseException(ErrorApiTitles.FAILED_MANAGE_USER_FAVORITE_CURRENCY_PAIRS, message);
    }

    @GetMapping("/token/refresh")
    public ResponseEntity<AuthTokenDto> refreshToken(HttpServletRequest request) {
        AuthTokenDto authTokenDto = authTokenService.refreshTokenNg(getPrincipalEmail(), request);
        return new ResponseEntity<>(authTokenDto, HttpStatus.OK); // 200
    }

    // /api/private/v2/users/jksdhfbsjfgsjdfgasj/personal/{status}?message=Hello
    // possible statuses (information, warning, alert, error, success)
    @GetMapping("/jksdhfbsjfgsjdfgasj/personal/{status}")
    public ResponseEntity<Void> sendMeMessage(@PathVariable String status,
                                              @RequestParam(required = false) String message) {
        try {
            UserNotificationType messageType = UserNotificationType.valueOf(status.toUpperCase());
            UserNotificationMessage userNotificationMessage = new UserNotificationMessage(messageType, message);
            String payload = objectMapper.writeValueAsString(userNotificationMessage);
            stompMessenger.sendPersonalMessageToUser(getPrincipalEmail(), payload);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    private UpdateUserDto getUpdateUserDto(User user) {
        UpdateUserDto dto = new UpdateUserDto(user.getId());
        dto.setEmail(user.getEmail());
        dto.setFinpassword(user.getFinpassword());
        dto.setPassword(user.getPassword());
        dto.setRole(user.getRole());
        dto.setStatus(user.getUserStatus());
        dto.setPhone(user.getPhone());
        return dto;
    }

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler({NoSuchFileException.class, UserNotFoundException.class})
    @ResponseBody
    public ResponseEntity<Object> NotFileFoundExceptionHandler(HttpServletRequest req, Exception exception) {
        return new ResponseEntity<>("Not found", HttpStatus.NOT_FOUND);
    }

    @ResponseStatus(HttpStatus.FORBIDDEN)
    @ExceptionHandler({WrongPasswordException.class})
    @ResponseBody
    public ResponseEntity<Object> WrongPasswordExceptionHandler(HttpServletRequest req, Exception exception) {
        return new ResponseEntity<>("Incorrect password", HttpStatus.FORBIDDEN);
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler({MethodArgumentNotValidException.class, IllegalArgumentException.class})
    @ResponseBody
    public ResponseEntity<Object> methodArgumentNotValidExceptionHandler(MethodArgumentNotValidException e) {
        return ResponseEntity.ok(new ExceptionDto(HttpStatus.BAD_REQUEST.toString(), e.getMessage()));
    }

    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    @ExceptionHandler(org.springframework.security.core.AuthenticationException.class)
    @ResponseBody
    public ResponseEntity<Object> AuthExceptionHandler(HttpServletRequest req, Exception exception) {
        return new ResponseEntity<>("Not authorised", HttpStatus.UNAUTHORIZED);
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler({NgDashboardException.class})
    @ResponseBody
    public ErrorInfo OtherErrorsHandler(HttpServletRequest req, Exception exception) {
        return new ErrorInfo(req.getRequestURL(), exception);
    }

    private String getPrincipalEmail() {
        return SecurityContextHolder.getContext().getAuthentication().getName();
    }

}
