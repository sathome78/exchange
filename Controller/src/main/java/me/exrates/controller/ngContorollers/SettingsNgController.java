package me.exrates.controller.ngContorollers;

import com.google.common.base.Preconditions;
import lombok.extern.log4j.Log4j2;
import me.exrates.controller.exception.ErrorInfo;
import me.exrates.controller.exception.FileLoadingException;
import me.exrates.controller.exception.NewsCreationException;
import me.exrates.controller.exception.NoFileForLoadingException;
import me.exrates.model.NotificationOption;
import me.exrates.model.SessionParams;
import me.exrates.model.User;
import me.exrates.model.UserFile;
import me.exrates.model.dto.*;
import me.exrates.model.dto.ngDto.UserSettingsDto;
import me.exrates.model.enums.*;
import me.exrates.model.form.NotificationOptionsForm;
import me.exrates.service.NotificationService;
import me.exrates.service.SessionParamsService;
import me.exrates.service.UserService;
import me.exrates.service.exception.IncorrectSmsPinException;
import me.exrates.service.exception.PaymentException;
import me.exrates.service.exception.ServiceUnavailableException;
import me.exrates.service.exception.UnoperableNumberException;
import me.exrates.service.notifications.NotificationsSettingsService;
import me.exrates.service.notifications.NotificatorService;
import me.exrates.service.notifications.NotificatorsService;
import me.exrates.service.notifications.Subscribable;
import org.apache.commons.lang.math.NumberUtils;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.security.Principal;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import static me.exrates.model.util.BigDecimalProcessing.doAction;

/**
 * Created by Maks on 07.02.2018.
 */
@Log4j2
@RestController
@RequestMapping("/info/private/settings")
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
    @Autowired
    private NotificatorsService notificatorService;
    @Value("${telegram.bot.url}")
    String TBOT_URL;
    @Value("${telegram_bot_name}")
    String TBOT_NAME;

    /*Controller for initialize user settings*/
    @RequestMapping(value = "/depr", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
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

    @RequestMapping(value = "/set_settings/depr", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
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


    @RequestMapping(value = "/sessionOptions/submit/depr", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
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

    @RequestMapping(value = "/2FaOptions/submit/depr", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<String> submitNotificationOptions(HttpServletRequest request) {
        String userEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        Locale locale = userService.getUserLocaleForMobile(userEmail);
        JSONObject jsonObject = new JSONObject();
        HttpStatus httpStatus;
        try {
            int userId = userService.getIdByEmail(userEmail);
            Map<Integer, NotificationsUserSetting> settingsMap = settingsService.getSettingsMap(userId);
            settingsMap.forEach((k,v) -> {
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
            });
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

    @ResponseBody
    @RequestMapping("/2FaOptions/getNotyPrice/depr")
    public NotificatorTotalPriceDto getNotyPrice(@RequestParam int id) {
        String userEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        Preconditions.checkArgument(id == NotificationTypeEnum.TELEGRAM.getCode());/*implemented for telegram only*/
        Subscribable subscribable = Preconditions.checkNotNull(notificatorService.getByNotificatorId(id));
        Object subscription = subscribable.getSubscription(userService.getIdByEmail(userEmail));
        UserRole role = userService.getUserRoleFromDB(userEmail);
        NotificatorTotalPriceDto dto = notificatorService.getPrices(id, role.getRole());
        if (subscription != null && subscription instanceof TelegramSubscription) {
            if (!((TelegramSubscription) subscription).getSubscriptionState().isBeginState()) {
                throw new IllegalStateException();
            }
            dto.setCode(((TelegramSubscription)subscription).getCode());
        }
        return dto;
    }

    @ResponseBody
    @RequestMapping("/2FaOptions/preconnect_sms/depr")
    public String preconnectSms(@RequestParam String number) {
        String userEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        number = number.replaceAll("\\+", "").replaceAll("\\-", "").replaceAll("\\.", "").replaceAll(" ", "");
        if (!NumberUtils.isDigits(number)) {
            throw new UnoperableNumberException();
        }
        Subscribable subscribable = notificatorService.getByNotificatorId(NotificationTypeEnum.SMS.getCode());
        int userId = userService.getIdByEmail(userEmail);
        SmsSubscriptionDto subscriptionDto = SmsSubscriptionDto.builder()
                .userId(userId)
                .newContact(number)
                .build();
        return subscribable.prepareSubscription(subscriptionDto).toString();
    }

    @ResponseBody
    @RequestMapping("/2FaOptions/confirm_connect_sms/depr")
    public String connectSms() {
        String userEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        Subscribable subscribable = notificatorService.getByNotificatorId(NotificationTypeEnum.SMS.getCode());
        subscribable.createSubscription(userEmail);
        return "ok";
    }

    @ResponseBody
    @RequestMapping("/2FaOptions/verify_connect_sms/depr")
    public String verifyConnectSms(@RequestParam String code) {
        String userEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        Subscribable subscribable = notificatorService.getByNotificatorId(NotificationTypeEnum.SMS.getCode());
        int userId = userService.getIdByEmail(userEmail);
        SmsSubscriptionDto subscriptionDto = SmsSubscriptionDto.builder()
                .code(code)
                .userId(userId)
                .build();
        return subscribable.subscribe(subscriptionDto).toString();
    }

    @ResponseBody
    @RequestMapping("/2FaOptions/connect_telegram/depr")
    public String getNotyPrice() {
        String userEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        Subscribable subscribable = notificatorService.getByNotificatorId(NotificationTypeEnum.TELEGRAM.getCode());
        return subscribable.createSubscription(userEmail).toString();
    }

    @ResponseBody
    @RequestMapping("/2FaOptions/reconnect_telegram/depr")
    public String reconnectTelegram(Principal principal) {
        Subscribable subscribable = notificatorService.getByNotificatorId(NotificationTypeEnum.TELEGRAM.getCode());
        return subscribable.reconnect(principal.getName()).toString();
    }

    @ResponseBody
    @RequestMapping("/2FaOptions/contact_info/depr")
    public String getInfo(@RequestParam int id) {
        String userEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        Subscribable subscribable = notificatorService.getByNotificatorId(id);
        Preconditions.checkNotNull(subscribable);
        NotificatorSubscription subscription = subscribable.getSubscription(userService.getIdByEmail(userEmail));
        Preconditions.checkState(subscription.isConnected());
        String contact = Preconditions.checkNotNull(subscription.getContactStr());
        int roleId = userService.getUserRoleFromSecurityContext().getRole();
        BigDecimal feePercent = notificatorService.getMessagePrice(id, roleId);
        BigDecimal price = doAction(doAction(subscription.getPrice(), feePercent, ActionType.MULTIPLY_PERCENT), subscription.getPrice(), ActionType.ADD);
        return new JSONObject(){{put("contact", contact);
            put("price", price);}}.toString();
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
