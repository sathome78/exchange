package me.exrates.controller.ngContorollers;

import lombok.extern.log4j.Log4j2;
import me.exrates.model.NotificationOption;
import me.exrates.model.SessionParams;
import me.exrates.model.User;
import me.exrates.model.dto.UpdateUserDto;
import me.exrates.model.enums.NotificationMessageEventEnum;
import me.exrates.model.enums.NotificationTypeEnum;
import me.exrates.model.enums.SessionLifeTypeEnum;
import me.exrates.service.NotificationService;
import me.exrates.service.SendMailService;
import me.exrates.service.SessionParamsService;
import me.exrates.service.UserFilesService;
import me.exrates.service.UserService;
import me.exrates.service.notifications.NotificationsSettingsService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.acls.model.NotFoundException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.DeleteMapping;
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
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import static me.exrates.model.util.BigDecimalProcessing.doAction;
import static me.exrates.service.util.RestApiUtils.decodePassword;

/**
 * Created by Maks on 07.02.2018.
 */
@Log4j2
@RestController
@RequestMapping("/info/private/settings")
public class SettingsNgControllerNew {

    private static final Logger logger = LogManager.getLogger("restSettingsAPI");

    @Value("${contacts.feedbackEmail}")
    String feedbackEmail;

    private final UserService userService;
    private final NotificationService notificationService;
    private final SessionParamsService sessionService;
    private final NotificationsSettingsService settingsService;
    private final UserFilesService userFilesService;

    @Autowired
    public SettingsNgControllerNew(UserService userService,
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
    public ResponseEntity<Void> updateMainPassword(@RequestBody Map<String, String> body, HttpServletRequest request){
        String email = SecurityContextHolder.getContext().getAuthentication().getName();

        User user = userService.findByEmail(email);
        String encodedPassword = body.getOrDefault("pass", "");
        if(encodedPassword.isEmpty()){
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        user.setPassword(decodePassword(encodedPassword));
        if (userService.update(getUpdateUserDto(user), true, userService.getUserLocaleForMobile(email))){
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
        try {
            int userId = userService.getIdByEmail(email);
            notificationService.updateNotificationOptionsForUser(userId, options);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.UNSUPPORTED_MEDIA_TYPE);
        }
    }

    @PutMapping(value = "/updateSessionInterval", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<Void> updateSessionPeriod(@RequestBody Map<String, Integer> body){
        String userEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        int interval = body.get("interval");
        if (interval == 0){
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        SessionParams sessionParams = new SessionParams(interval, SessionLifeTypeEnum.INACTIVE_COUNT_LIFETIME.getTypeId());
        if (sessionService.isSessionTimeValid(sessionParams.getSessionTimeMinutes())) {
            sessionService.saveOrUpdate(sessionParams, userEmail);
            /* todo set new params for existing token???*/


            return new ResponseEntity<>(HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }

    @GetMapping(value = "/sessionInterval", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public Integer getSessionPeriod() {
        String userEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        SessionParams params = sessionService.getByEmailOrDefault(userEmail);
        if (null == params) {
            return 0;
        }
        return params.getSessionTimeMinutes();
    }

    @GetMapping(value = "/userFiles")
    public List<File> getUserFiles() throws Exception {
        String userEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        int userId = userService.getIdByEmail(userEmail);
        List<File> files = new ArrayList<>();
        userService.findUserDoc(userId).forEach(uf -> {
            files.add(uf.getPath().toFile());
        });
        return files;
    }

    @GetMapping(value = "/userFiles/{userId}/{fileName:.+}")
    public ResponseEntity<Resource> getSingleImage(@PathVariable Integer userId,
                                                   @PathVariable String fileName) throws NoSuchFileException, IOException {
        System.out.println("fileName: " + fileName);

        Path imagePath = userFilesService.getUserFilePath(fileName, userId);
        ByteArrayResource resource = new ByteArrayResource(Files.readAllBytes(imagePath));

        return ResponseEntity.ok()
                .contentLength(imagePath.toFile().length())
                .contentType(MediaType.parseMediaType("application/octet-stream"))
                .body(resource);
    }

    @PostMapping(value = "/userFiles/submit")
    public ResponseEntity<List<File>> saveUserFile(@RequestParam("file") MultipartFile file) throws Exception {
        String userEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        int userId = userService.getIdByEmail(userEmail);
        userFilesService.createUserFiles(userId, Collections.singletonList(file));
        return new ResponseEntity<>(getUserFiles(), HttpStatus.OK);
    }

    @DeleteMapping(value = "/userFiles/delete")
    public ResponseEntity<List<File>> deleteUserFile(@RequestParam("filePath") String filePath) throws Exception {
        String userEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        if(filePath.contains("/")) {
            filePath = filePath.substring(filePath.lastIndexOf("/")+1);
        }
        int userId = userService.getIdByEmail(userEmail);
        userFilesService.deleteUserFile(filePath, userId);
        return new ResponseEntity<>(getUserFiles(), HttpStatus.OK);
    }

    @PutMapping(value = "/userLanguage/update", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<Void> updateUserLanguage(@RequestBody Map<String, String> body){
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        if(userService.setPreferredLang(email, body.get("lang").toLowerCase())){
            return new ResponseEntity<>(HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.SERVICE_UNAVAILABLE);
        }
    }

    @GetMapping(value = "/userLanguage", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<Map<String, String>> getUserPreferredLanguage(){
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        try {
            return ResponseEntity.ok().body(Collections.singletonMap("lang", userService.getPreferedLangByEmail(email)));
        } catch (Exception e) {
            throw new NotFoundException("Fail to get preferred langauge for user: " + email);
        }
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

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(NoSuchFileException.class)
    @ResponseBody
    public ResponseEntity<Object> NotFileFoundExceptionHandler(HttpServletRequest req, Exception exception) {
        return new ResponseEntity<Object>("Not file found", HttpStatus.NOT_FOUND);
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