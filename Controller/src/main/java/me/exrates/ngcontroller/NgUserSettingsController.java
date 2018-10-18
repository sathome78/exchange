package me.exrates.ngcontroller;

import me.exrates.controller.validator.RegisterFormValidation;
import me.exrates.model.NotificationOption;
import me.exrates.model.SessionParams;
import me.exrates.model.User;
import me.exrates.model.dto.PageLayoutSettingsDto;
import me.exrates.model.dto.UpdateUserDto;
import me.exrates.ngcontroller.mobel.UserDocVerificationDto;
import me.exrates.ngcontroller.mobel.UserInfoVerificationDto;
import me.exrates.model.enums.ColorScheme;
import me.exrates.model.enums.NotificationEvent;
import me.exrates.model.enums.SessionLifeTypeEnum;
import me.exrates.ngcontroller.mobel.enums.VerificationDocumentType;
import me.exrates.ngcontroller.service.UserVerificationService;
import me.exrates.service.*;
import me.exrates.service.exception.UserNotFoundException;
import me.exrates.service.notifications.NotificationsSettingsService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.nio.file.NoSuchFileException;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping(value = "/info/private/v2/settings/",
        consumes = MediaType.APPLICATION_JSON_UTF8_VALUE,
        produces = MediaType.APPLICATION_JSON_UTF8_VALUE
)
public class NgUserSettingsController {

    private static final Logger logger = LogManager.getLogger(NgUserSettingsController.class);
    private static final String NICKNAME = "nickname";
    private static final String SESSION_INTERVAL = "sessionInterval";
    private static final String EMAIL_NOTIFICATION = "notifications";
    private static final String COLOR_SCHEME = "color-schema";
    private static final String IS_COLOR_BLIND = "isLowColorEnabled";
    private static final String USER_FILES = "userFiles";

    private static final String STATE = "STATE";

    @Autowired
    private RegisterFormValidation registerFormValidation;
    @Autowired
    private MessageSource messageSource;

    @Value("${contacts.feedbackEmail}")
    String feedbackEmail;

    private final UserService userService;
    private final NotificationService notificationService;
    private final SessionParamsService sessionService;
    private final PageLayoutSettingsService layoutSettingsService;
    private final NotificationsSettingsService settingsService;
    private final UserFilesService userFilesService;
    private final UserVerificationService verificationService;

    @Autowired
    public NgUserSettingsController(UserService userService,
                                    NotificationService notificationService,
                                    SessionParamsService sessionService,
                                    PageLayoutSettingsService layoutSettingsService,
                                    NotificationsSettingsService settingsService,
                                    UserFilesService userFilesService, UserVerificationService verificationService) {
        this.userService = userService;
        this.notificationService = notificationService;
        this.sessionService = sessionService;
        this.layoutSettingsService = layoutSettingsService;
        this.settingsService = settingsService;
        this.userFilesService = userFilesService;
        this.verificationService = verificationService;
    }

    @PutMapping(value = "/updateMainPassword")
    public ResponseEntity<Void> updateMainPassword(@RequestBody Map<String, String> body) {
        String email = getPrincipalEmail();
        User user = userService.findByEmail(email);
        Locale locale = userService.getUserLocaleForMobile(email);
        String password = body.getOrDefault("password", "");
        if (password.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        user.setPassword(password);
        user.setConfirmPassword(password);

        //   registerFormValidation.validateResetPassword(user, result, locale);
        if (userService.update(getUpdateUserDto(user), locale)) {
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

    @PutMapping(value = NICKNAME)
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
    public Integer getSessionPeriod() {
        SessionParams params = sessionService.getByEmailOrDefault(getPrincipalEmail());
        if (null == params) {
            return 0;
        }
        return params.getSessionTimeMinutes();
    }

    @PutMapping(value = SESSION_INTERVAL)
    public ResponseEntity<Void> updateSessionPeriod(@RequestBody Map<String, Integer> body) {
        try {
            int interval = body.get(SESSION_INTERVAL);
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
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping(value = EMAIL_NOTIFICATION)
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

    @PutMapping(value = EMAIL_NOTIFICATION)
    public ResponseEntity<Void> updateUserNotification(@RequestBody List<NotificationOption> options) {
        try {
            int userId = userService.getIdByEmail(getPrincipalEmail());
            notificationService.updateNotificationOptionsForUser(userId, options);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping(IS_COLOR_BLIND)
    @ResponseBody
    public Boolean getUserColorDepth() {
        User user = userService.findByEmail(getPrincipalEmail());
        PageLayoutSettingsDto dto = this.layoutSettingsService.findByUser(user);
        return dto != null && dto.isLowColorEnabled();
    }

    @PutMapping(IS_COLOR_BLIND)
    public ResponseEntity<Void> updateUserColorDepth(@RequestBody Map<String, Boolean> params) {
        if (params.containsKey(STATE)) {
            User user = userService.findByEmail(getPrincipalEmail());
            this.layoutSettingsService.toggleLowColorMode(user, params.get(STATE));
            return new ResponseEntity<>(HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.UNPROCESSABLE_ENTITY);
        }
    }

    @GetMapping(COLOR_SCHEME)
    @ResponseBody
    public ColorScheme getUserColorScheme() {
        User user = userService.findByEmail(getPrincipalEmail());
        return this.layoutSettingsService.getColorScheme(user);
    }

    @PutMapping(COLOR_SCHEME)
    public ResponseEntity<Void> updateUserColorScheme(@RequestBody Map<String, String> params) {
        if (params.containsKey("SCHEME")) {
            Integer userId = userService.getIdByEmail(getPrincipalEmail());
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

    @PostMapping(USER_FILES)
    public ResponseEntity<Void> uploadUserVerification(@RequestBody UserInfoVerificationDto data) {
        int userId = userService.getIdByEmail(getPrincipalEmail());

        data.setUserId(userId);

       UserInfoVerificationDto attempt = verificationService.save(data);
        if (attempt != null) {
            return new ResponseEntity<>(HttpStatus.CREATED);
        }
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }

    @PostMapping(USER_FILES + "/docs")
    public ResponseEntity<Void> uploadUserVerificationDocs(@RequestBody UserDocVerificationDto data) {
        int userId = userService.getIdByEmail(getPrincipalEmail());
        data.setUserId(userId);

        UserDocVerificationDto attempt = verificationService.save(data);
        if (attempt != null) {
            return new ResponseEntity<>(HttpStatus.CREATED);
        }
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }

    //        }
    //            return new ResponseEntity<>(HttpStatus.NOT_ACCEPTABLE);
    //        } else {
    //            return new ResponseEntity<>(HttpStatus.OK);
    //        if (userService.update(getUpdateUserDto(user), locale)){
    //        }
    //            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    //        if (!user.getFinpassword().equals(user.getConfirmFinPassword())) {
    //
    //        user.setConfirmFinPassword(confirmFinpassword);
    //        user.setFinpassword(finpassword);
    //        }
    //            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    //        if(finpassword.isEmpty() || confirmFinpassword.isEmpty()){
    //
    //        String confirmFinpassword = body.getOrDefault("confirmFinpassword", "");
    //        String finpassword = body.getOrDefault("finpassword", "");
    //        Locale locale = userService.getUserLocaleForMobile(email);
    //        User user = userService.findByEmail(email);
    //        String email = SecurityContextHolder.getContext().getAuthentication().getName();
    //    public ResponseEntity<Void> updateFinPassword(@RequestBody Map<String, String> body, HttpServletRequest request){
//    @PutMapping(value = "/updateFinPassword", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)

//    }
    //        }
    //            return new ResponseEntity<>(HttpStatus.NOT_ACCEPTABLE);
    //        } catch (Exception e) {
    //            return new ResponseEntity<>(settings, HttpStatus.OK);
    //                    new HashMap<>(settingsService.getUser2FactorSettings(userId));
    //            Map<NotificationMessageEventEnum, NotificationTypeEnum> settings =
    //            int userId = userService.getIdByEmail(userEmail);
    //        try {
    //        String userEmail = SecurityContextHolder.getContext().getAuthentication().getName();
    //    public ResponseEntity<Map<NotificationMessageEventEnum, NotificationTypeEnum>> getAuthSettings(HttpServletRequest request){
    //    @GetMapping(value = "/user2FactorAuthSettings", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    //
    //    }
    //        }
    //            return ResponseEntity.unprocessableEntity().build();
    //        } catch (Exception e) {
    //            return ResponseEntity.ok().build();
    //            settingsService.updateUser2FactorSettings(userId, body);
    //            int userId = userService.getIdByEmail(userEmail);
    //        try {
    //        String userEmail = SecurityContextHolder.getContext().getAuthentication().getName();
    //    public ResponseEntity<Void> updateAuthorization(@RequestBody Map<String, String> body){
//    @PutMapping(value = "/updateAuthorization", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)

//    }
//    @PutMapping(value = "/updateDocuments", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
//    public ResponseEntity<Void> updateDocuments(@RequestBody Map<String, Boolean> body, HttpServletRequest request){
//        String email = SecurityContextHolder.getContext().getAuthentication().getName();
//
//        return null;
//    }

    //            return new ResponseEntity<>(HttpStatus.UNSUPPORTED_MEDIA_TYPE);
//        }
    //        } catch (Exception e) {
    //            return new ResponseEntity<>(HttpStatus.OK);
    //            notificationService.updateNotificationOptionsForUser(userId, options);
    //            int userId = userService.getIdByEmail(email);
    //        try {
    //        String email = SecurityContextHolder.getContext().getAuthentication().getName();
    //    public ResponseEntity<Void> updateNotifications(@RequestBody  List<NotificationOption> options){
//    @PutMapping(value = "/updateNotifications", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)

//    }
    //        return docs;
    //        });
    //            docs.add(uf.getPath().toFile());
    //        userService.findUserDoc(userId).forEach(uf -> {
    //        List<File> docs = new ArrayList<>();
    //        int userId = userService.getIdByEmail(userEmail);
    //        String userEmail = SecurityContextHolder.getContext().getAuthentication().getName();
    //    public List<File> getUserFiles() throws Exception {
//    @GetMapping(value = "/userFiles")

//    }
    //                .body(resource);
    //                .contentType(MediaType.parseMediaType("application/octet-stream"))
    //                .contentLength(imagePath.toFile().length())
    //        return ResponseEntity.ok()
    //
    //        ByteArrayResource resource = new ByteArrayResource(Files.readAllBytes(imagePath));
    //        Path imagePath = userFilesService.getUserFilePath(fileName, userId);
    //
    //        System.out.println("fileName: " + fileName);
    //                                                   @PathVariable String fileName) throws NoSuchFileException, IOException {
    //    public ResponseEntity<Resource> getSingleImage(@PathVariable Integer userId,
//    @GetMapping(value = "/userFiles/{userId}/{fileName:.+}")

//    }

//    @DeleteMapping(value = "/userFiles/delete")
//    public ResponseEntity<List<File>> deleteUserFile(@RequestParam("filePath") String filePath) throws Exception {
//        String userEmail = SecurityContextHolder.getContext().getAuthentication().getName();
//        if(filePath.contains("/")) {
//            filePath = filePath.substring(filePath.lastIndexOf("/")+1);
//        }
//        int userId = userService.getIdByEmail(userEmail);
//        userFilesService.deleteUserFile(filePath, userId);
//        return new ResponseEntity<>(getUserFiles(), HttpStatus.OK);
//    }

//    @PutMapping(value = "/userLanguage/update", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
//    public ResponseEntity<Void> updateUserLanguage(@RequestBody Map<String, String> body){
//        String email = SecurityContextHolder.getContext().getAuthentication().getName();
//        if(userService.setPreferredLang(email, body.get("lang").toLowerCase())){
//            return new ResponseEntity<>(HttpStatus.OK);
//        } else {
//            return new ResponseEntity<>(HttpStatus.SERVICE_UNAVAILABLE);
//        }
//    }

//    @GetMapping(value = "/userLanguage", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
//    public ResponseEntity<Map<String, String>> getUserPreferredLanguage(){
//        String email = SecurityContextHolder.getContext().getAuthentication().getName();
//        try {
//            return ResponseEntity.ok().body(Collections.singletonMap("lang", userService.getPreferedLangByEmail(email)));
//        } catch (Exception e) {
//            throw new NotFoundException("Fail to get preferred langauge for user: " + email);
//        }
//    }

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

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler({NoSuchFileException.class, UserNotFoundException.class})
    @ResponseBody
    public ResponseEntity<Object> NotFileFoundExceptionHandler(HttpServletRequest req, Exception exception) {
        return new ResponseEntity<>("Not found", HttpStatus.NOT_FOUND);
    }

    @ResponseStatus(HttpStatus.SERVICE_UNAVAILABLE)
    @ExceptionHandler(Exception.class)
    @ResponseBody
    public ResponseEntity<Object> UnavailableFoundExceptionHandler(HttpServletRequest req, Exception exception) {
        return new ResponseEntity<>("Service unavailable", HttpStatus.SERVICE_UNAVAILABLE);
    }

    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    @ExceptionHandler(org.springframework.security.core.AuthenticationException.class)
    @ResponseBody
    public ResponseEntity<Object> AuthExceptionHandler(HttpServletRequest req, Exception exception) {
        return new ResponseEntity<>("Not authorised", HttpStatus.UNAUTHORIZED);
    }

    private String getPrincipalEmail() {
        return SecurityContextHolder.getContext().getAuthentication().getName();
    }

}
