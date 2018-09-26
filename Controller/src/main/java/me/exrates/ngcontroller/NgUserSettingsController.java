package me.exrates.ngcontroller;

import me.exrates.controller.validator.RegisterFormValidation;
import me.exrates.model.SessionParams;
import me.exrates.model.User;
import me.exrates.model.dto.UpdateUserDto;
import me.exrates.service.NotificationService;
import me.exrates.service.SessionParamsService;
import me.exrates.service.UserFilesService;
import me.exrates.service.UserService;
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
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.nio.file.NoSuchFileException;
import java.util.Locale;
import java.util.Map;

@RestController
@RequestMapping("/info/private/settings")
public class NgUserSettingsController {

    private static final Logger logger = LogManager.getLogger("restSettingsAPI");
    @Autowired
    private RegisterFormValidation registerFormValidation;
    @Autowired
    private MessageSource messageSource;

    @Value("${contacts.feedbackEmail}")
    String feedbackEmail;

    private final UserService userService;
    private final NotificationService notificationService;
    private final SessionParamsService sessionService;
    private final NotificationsSettingsService settingsService;
    private final UserFilesService userFilesService;

    @Autowired
    public NgUserSettingsController(UserService userService,
                                   NotificationService notificationService,
                                   SessionParamsService sessionService,
                                   NotificationsSettingsService settingsService,
                                   UserFilesService userFilesService) {
        this.userService = userService;
        this.notificationService = notificationService;
        this.sessionService = sessionService;
        this.settingsService = settingsService;
        this.userFilesService = userFilesService;
    }

    @PutMapping(value = "/updateMainPassword", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<Void> updateMainPassword(@RequestBody Map<String, String> body, BindingResult result){
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userService.findByEmail(email);
        Locale locale = userService.getUserLocaleForMobile(email);
        String password = body.getOrDefault("password", "");
        if(password.isEmpty()){
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        user.setPassword(password);
        user.setConfirmPassword(password);

        //   registerFormValidation.validateResetPassword(user, result, locale);
        if (userService.update(getUpdateUserDto(user), locale)){
            return new ResponseEntity<>(HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_ACCEPTABLE);
        }
    }

//    @PutMapping(value = "/updateFinPassword", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
//    public ResponseEntity<Void> updateFinPassword(@RequestBody Map<String, String> body, HttpServletRequest request){
//        String email = SecurityContextHolder.getContext().getAuthentication().getName();
//        User user = userService.findByEmail(email);
//        Locale locale = userService.getUserLocaleForMobile(email);
//        String finpassword = body.getOrDefault("finpassword", "");
//        String confirmFinpassword = body.getOrDefault("confirmFinpassword", "");
//
//        if(finpassword.isEmpty() || confirmFinpassword.isEmpty()){
//            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
//        }
//        user.setFinpassword(finpassword);
//        user.setConfirmFinPassword(confirmFinpassword);
//
//        if (!user.getFinpassword().equals(user.getConfirmFinPassword())) {
//            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
//        }
//        if (userService.update(getUpdateUserDto(user), locale)){
//            return new ResponseEntity<>(HttpStatus.OK);
//        } else {
//            return new ResponseEntity<>(HttpStatus.NOT_ACCEPTABLE);
//        }
//    }

//    @PutMapping(value = "/updateAuthorization", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
//    public ResponseEntity<Void> updateAuthorization(@RequestBody Map<String, String> body){
//        String userEmail = SecurityContextHolder.getContext().getAuthentication().getName();
//        try {
//            int userId = userService.getIdByEmail(userEmail);
//            settingsService.updateUser2FactorSettings(userId, body);
//            return ResponseEntity.ok().build();
//        } catch (Exception e) {
//            return ResponseEntity.unprocessableEntity().build();
//        }
//    }
//
//    @GetMapping(value = "/user2FactorAuthSettings", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
//    public ResponseEntity<Map<NotificationMessageEventEnum, NotificationTypeEnum>> getAuthSettings(HttpServletRequest request){
//        String userEmail = SecurityContextHolder.getContext().getAuthentication().getName();
//        try {
//            int userId = userService.getIdByEmail(userEmail);
//            Map<NotificationMessageEventEnum, NotificationTypeEnum> settings =
//                    new HashMap<>(settingsService.getUser2FactorSettings(userId));
//            return new ResponseEntity<>(settings, HttpStatus.OK);
//        } catch (Exception e) {
//            return new ResponseEntity<>(HttpStatus.NOT_ACCEPTABLE);
//        }
//    }

//    @PutMapping(value = "/updateDocuments", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
//    public ResponseEntity<Void> updateDocuments(@RequestBody Map<String, Boolean> body, HttpServletRequest request){
//        String email = SecurityContextHolder.getContext().getAuthentication().getName();
//
//        return null;
//    }

//    @GetMapping(value = "/notifications", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
//    public List<NotificationOption> getUserNotifications(){
//        String email = SecurityContextHolder.getContext().getAuthentication().getName();
//        try {
//            int userId = userService.getIdByEmail(email);
//            return notificationService.getNotificationOptionsByUser(userId);
//        } catch (Exception e) {
//            return new ArrayList<>();
//        }
//    }

//    @PutMapping(value = "/updateNotifications", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
//    public ResponseEntity<Void> updateNotifications(@RequestBody  List<NotificationOption> options){
//        String email = SecurityContextHolder.getContext().getAuthentication().getName();
//        try {
//            int userId = userService.getIdByEmail(email);
//            notificationService.updateNotificationOptionsForUser(userId, options);
//            return new ResponseEntity<>(HttpStatus.OK);
//        } catch (Exception e) {
//            return new ResponseEntity<>(HttpStatus.UNSUPPORTED_MEDIA_TYPE);
//        }
//    }

//    @PutMapping(value = "/updateSessionInterval", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
//    public ResponseEntity<String> updateSessionPeriod(@RequestBody Map<String, Integer> body, HttpServletRequest request){
//        String userEmail = SecurityContextHolder.getContext().getAuthentication().getName();
//        Locale locale = userService.getUserLocaleForMobile(userEmail);
//        JSONObject jsonObject = new JSONObject();
//
//        try {
//            int interval = body.get("interval");
//            SessionParams sessionParams = new SessionParams(interval, SessionLifeTypeEnum.INACTIVE_COUNT_LIFETIME.getTypeId());
//            if (sessionService.isSessionTimeValid(sessionParams.getSessionTimeMinutes())) {
//                sessionService.saveOrUpdate(sessionParams, userEmail);
//                sessionService.setSessionLifeParams(request); /*todo set new params for existing token???*/
//                jsonObject.put("successNoty", messageSource.getMessage("session.settings.success", null, locale));
//                return new ResponseEntity<String>(jsonObject.toString(), HttpStatus.OK);
//            } else {
//                jsonObject.put("msg", messageSource.getMessage("session.settings.time.invalid", null, locale));
//                return new ResponseEntity<String>(jsonObject.toString(), HttpStatus.OK);
//            }
//        } catch (Exception e) {
//            jsonObject.put("msg", messageSource.getMessage("session.settings.invalid", null, locale));
//            return new ResponseEntity<String>(jsonObject.toString(), HttpStatus.BAD_REQUEST);
//        }
///*        if (sessionService.isSessionTimeValid(sessionParams.getSessionTimeMinutes())) {
//            sessionService.saveOrUpdate(sessionParams, userEmail);
//            *//* todo set new params for existing token???*//*
//
//
//            return new ResponseEntity<>(HttpStatus.OK);
//        }
//        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);*/
//    }

    @GetMapping(value = "/sessionInterval", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public Integer getSessionPeriod() {
        String userEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        SessionParams params = sessionService.getByEmailOrDefault(userEmail);
        if (null == params) {
            return 0;
        }
        return params.getSessionTimeMinutes();
    }

//    @GetMapping(value = "/userFiles")
//    public List<File> getUserFiles() throws Exception {
//        String userEmail = SecurityContextHolder.getContext().getAuthentication().getName();
//        int userId = userService.getIdByEmail(userEmail);
//        List<File> files = new ArrayList<>();
//        userService.findUserDoc(userId).forEach(uf -> {
//            files.add(uf.getPath().toFile());
//        });
//        return files;
//    }

//    @GetMapping(value = "/userFiles/{userId}/{fileName:.+}")
//    public ResponseEntity<Resource> getSingleImage(@PathVariable Integer userId,
//                                                   @PathVariable String fileName) throws NoSuchFileException, IOException {
//        System.out.println("fileName: " + fileName);
//
//        Path imagePath = userFilesService.getUserFilePath(fileName, userId);
//        ByteArrayResource resource = new ByteArrayResource(Files.readAllBytes(imagePath));
//
//        return ResponseEntity.ok()
//                .contentLength(imagePath.toFile().length())
//                .contentType(MediaType.parseMediaType("application/octet-stream"))
//                .body(resource);
//    }

//    @PostMapping(value = "/userFiles/submit")
//    public ResponseEntity<List<File>> saveUserFile(@RequestParam("file") MultipartFile file) throws Exception {
//        String userEmail = SecurityContextHolder.getContext().getAuthentication().getName();
//        int userId = userService.getIdByEmail(userEmail);
//        userFilesService.createUserFiles(userId, Collections.singletonList(file));
//        return new ResponseEntity<>(getUserFiles(), HttpStatus.OK);
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
    @ExceptionHandler(NoSuchFileException.class)
    @ResponseBody
    public ResponseEntity<Object> NotFileFoundExceptionHandler(HttpServletRequest req, Exception exception) {
        return new ResponseEntity<Object>("File not found", HttpStatus.NOT_FOUND);
    }

    @ResponseStatus(HttpStatus.SERVICE_UNAVAILABLE)
    @ExceptionHandler(Exception.class)
    @ResponseBody
    public ResponseEntity<Object> UnavailableFoundExceptionHandler(HttpServletRequest req, Exception exception) {
        return new ResponseEntity<Object>("Service unavailable", HttpStatus.SERVICE_UNAVAILABLE);
    }

    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    @ExceptionHandler(org.springframework.security.core.AuthenticationException.class)
    @ResponseBody
    public ResponseEntity<Object> AuthExceptionHandler(HttpServletRequest req, Exception exception) {
        return new ResponseEntity<Object>("Not authorised", HttpStatus.UNAUTHORIZED);
    }

}
