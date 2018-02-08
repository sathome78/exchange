package me.exrates.controller.ngContorollers;

import lombok.extern.log4j.Log4j2;
import me.exrates.model.NotificationOption;
import me.exrates.model.SessionParams;
import me.exrates.model.User;
import me.exrates.model.UserFile;
import me.exrates.model.dto.NotificationsUserSetting;
import me.exrates.model.dto.ngDto.UserSettingsDto;
import me.exrates.model.enums.NotificationMessageEventEnum;
import me.exrates.model.enums.SessionLifeTypeEnum;
import me.exrates.model.form.NotificationOptionsForm;
import me.exrates.service.NotificationService;
import me.exrates.service.SessionParamsService;
import me.exrates.service.UserService;
import me.exrates.service.notifications.NotificationsSettingsService;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Created by Maks on 07.02.2018.
 */
@Log4j2
@RestController
public class SettingsNgController {

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
    @Value("${telegram.bot.url}")
    String TBOT_URL;
    @Value("${telegram_bot_name}")
    String TBOT_NAME;

    /*Controller for initialize user settings*/
    @RequestMapping(value = "/info/private/get_settings", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public UserSettingsDto getSettings() {
        String userEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        final User user = userService.getUserById(userService.getIdByEmail(userEmail));
        final List<UserFile> userFile = userService.findUserDoc(user.getId());
        List<NotificationOption> notificationOptions = notificationService.getNotificationOptionsByUser(user.getId());
        notificationOptions.forEach(option -> option.localize(messageSource, Locale.forLanguageTag(user.getPrefferedLang())));
        NotificationOptionsForm notificationOptionsForm = new NotificationOptionsForm();
        notificationOptionsForm.setOptions(notificationOptions);
        UserSettingsDto settingsDto = new UserSettingsDto();
        settingsDto.setUser(user);
        settingsDto.setUserFiles(userFile);
        settingsDto.setNotificationOptionsForm(notificationOptionsForm);
        settingsDto.setSessionParams(sessionService.getByEmailOrDefault(user.getEmail()));
        settingsDto.setSessionLifeTimeTypes(sessionService.getAllByActive(true));
        settingsDto.setUser2faOptions(settingsService.get2faOptionsForUser(user.getId()));
        settingsDto.setTelegramBotName(TBOT_NAME);
        settingsDto.setTelegramBotUrl(TBOT_URL);
        return settingsDto;
    }

    @RequestMapping(value = "/info/private/set_settings", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<String> submitNotificationOptions(@ModelAttribute NotificationOptionsForm notificationOptionsForm) {
        notificationOptionsForm.getOptions().forEach(log::debug);
        String userEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        Locale locale = userService.getUserLocaleForMobile(userEmail);
        List<NotificationOption> notificationOptions = notificationOptionsForm.getOptions();
        if (notificationOptions.stream().anyMatch(option -> !option.isSendEmail() && !option.isSendNotification())) {
            return new ResponseEntity<String>(new JSONObject()
            {{put("msg", messageSource.getMessage("notifications.invalid", null, locale));}}.toString(),
                    HttpStatus.NOT_ACCEPTABLE);
        }
        notificationService.updateUserNotifications(notificationOptions);
        return new ResponseEntity<>(HttpStatus.OK);
    }


    @RequestMapping(value = "/info/private/settings/sessionOptions/submit", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<String> submitNotificationOptions(@ModelAttribute SessionParams sessionParams) {
        String userEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        Locale locale = userService.getUserLocaleForMobile(userEmail);
        if (!sessionService.isSessionLifeTypeIdValid(sessionParams.getSessionLifeTypeId())) {
            sessionParams.setSessionLifeTypeId(SessionLifeTypeEnum.INACTIVE_COUNT_LIFETIME.getTypeId());
        }
        JSONObject jsonObject = new JSONObject();
        if (sessionService.isSessionTimeValid(sessionParams.getSessionTimeMinutes())) {
            try {
                sessionService.saveOrUpdate(sessionParams, userEmail);
                /*sessionService.setSessionLifeParams(request);*//*todo set new params for existing token???*/
                jsonObject.put("successNoty", messageSource.getMessage("session.settings.success", null, locale));
            } catch (Exception e) {
                log.error("error", e);
                jsonObject.put("msg", messageSource.getMessage("session.settings.invalid", null, locale));
            }
        } else {
            jsonObject.put("msg", messageSource.getMessage("session.settings.time.invalid", null, locale));
        }
        return new ResponseEntity<String>(jsonObject.toString(), HttpStatus.OK);
    }

    @RequestMapping(value = "/info/private/settings/2FaOptions/submit", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<String> submitNotificationOptions() {
        String userEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        Locale locale = userService.getUserLocaleForMobile(userEmail);
        JSONObject jsonObject = new JSONObject();
        HttpStatus httpStatus;
        try {
            int userId = userService.getIdByEmail(userEmail);
            Map<Integer, NotificationsUserSetting> settingsMap = settingsService.getSettingsMap(userId);
            /*todo: define type of data for update*/
            /*settingsMap.forEach((k,v) -> {
                Integer notificatorId = Integer.parseInt(request.getParameter(k.toString()));
                if (notificatorId.equals(0)) {
                    notificatorId = null;
                }
                if (v == null) {
                    NotificationsUserSetting setting = NotificationsUserSetting.builder()
                            .userId(userId)
                            .notificatorId(notificatorId)
                            .notificationMessageEventEnum(NotificationMessageEventEnum.convert(k))
                            .build();
                    settingsService.createOrUpdate(setting);
                } else if (v.getNotificatorId() == null || !v.getNotificatorId().equals(notificatorId)) {
                    v.setNotificatorId(notificatorId);
                    settingsService.createOrUpdate(v);
                }
            });*/
            jsonObject.put("successNoty", messageSource.getMessage("message.settings_successfully_saved", null,
                    locale));
            httpStatus = HttpStatus.OK;
        } catch (Exception e) {
            log.error(e);
            jsonObject.put("msg", messageSource.getMessage("message.error_saving_settings", null, locale));
            httpStatus = HttpStatus.NOT_ACCEPTABLE;
        }
        return new ResponseEntity<>(jsonObject.toString(), httpStatus);
    }
}
