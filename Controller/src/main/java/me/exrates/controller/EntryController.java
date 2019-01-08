package me.exrates.controller;

import com.google.common.base.Preconditions;
import lombok.extern.log4j.Log4j2;
import me.exrates.controller.validator.RegisterFormValidation;
import me.exrates.controller.exception.ErrorInfo;
import me.exrates.controller.exception.FileLoadingException;
import me.exrates.controller.exception.NewsCreationException;
import me.exrates.controller.exception.NoFileForLoadingException;
import me.exrates.model.*;
import me.exrates.model.dto.*;
import me.exrates.model.enums.*;
import me.exrates.model.exceptions.InvalidCredentialsException;
import me.exrates.model.exceptions.SessionParamTimeExceedException;
import me.exrates.model.userOperation.enums.UserOperationAuthority;
import me.exrates.security.exception.IncorrectPinException;
import me.exrates.security.exception.PinCodeCheckNeedException;
import me.exrates.security.service.SecureService;
import me.exrates.service.*;
import me.exrates.service.exception.IncorrectSmsPinException;
import me.exrates.service.exception.PaymentException;
import me.exrates.service.exception.ServiceUnavailableException;
import me.exrates.service.exception.UnoperableNumberException;
import me.exrates.service.exception.invoice.MerchantException;
import me.exrates.service.notifications.NotificationsSettingsService;
import me.exrates.service.notifications.NotificatorsService;
import me.exrates.service.notifications.*;
import me.exrates.service.userOperation.UserOperationService;
import me.exrates.service.session.UserSessionService;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.support.RequestContextUtils;
import org.springframework.web.servlet.view.RedirectView;
import org.springframework.web.util.WebUtils;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.Principal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import static java.util.stream.Collectors.toList;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

/**
 * The Controller contains methods, which mapped to entry points (main pages):
 * - dashboard
 * - settings
 * - news
 * First entry to this pages starts new session
 */
@Log4j2
@Controller
@PropertySource(value = {"classpath:/news.properties", "classpath:/captcha.properties", "classpath:/telegram_bot.properties"})
public class EntryController {
    private static final Logger LOGGER = LogManager.getLogger(EntryController.class);

    @Autowired
    MessageSource messageSource;
    @Value("${captcha.type}")
    String CAPTCHA_TYPE;
    @Value("${telegram.bot.url}")
    String TBOT_URL;
    @Value("${telegram_bot_name}")
    String TBOT_NAME;
    private
    @Value("${news.locationDir}")
    String newsLocationDir;
    @Autowired
    private LocaleResolver localeResolver;
    @Autowired
    private NewsService newsService;
    @Autowired
    private UserService userService;
    @Autowired
    private UserOperationService userOperationService;
    @Autowired
    private SessionParamsService sessionService;
    @Autowired
    private NotificationService notificationService;
    @Autowired
    private UserRoleService userRoleService;
    @Autowired
    private NotificationsSettingsService settingsService;
    @Autowired
    private NotificatorsService notificatorService;
    @Autowired
    private ReferralService referralService;
    @Autowired
    private CurrencyService currencyService;
    @Autowired
    private RegisterFormValidation registerFormValidation;
    @Autowired
    private UserFilesService userFilesService;
    @Autowired
    private UserSessionService userSessionService;
    @Autowired
    private SecureService secureService;
    @Autowired
    private G2faService g2faService;
    @Autowired
    private SurveyService surveyService;

    @RequestMapping(value = {"/dashboard"})
    public ModelAndView dashboard(
            @RequestParam(required = false) String qrLogin,
            @RequestParam(required = false) String sessionEnd,
            @RequestParam(required = false) String startupPage,
            @RequestParam(required = false) String startupSubPage,
            @RequestParam(required = false) String currencyPair,
            HttpServletRequest request, Principal principal) {
        ModelAndView model = new ModelAndView();
        String successNoty = null;
        String errorNoty = null;
        if (qrLogin != null) {
            successNoty = messageSource
                    .getMessage("dashboard.qrLogin.successful", null,
                            localeResolver.resolveLocale(request));
        }
        if (sessionEnd != null) {
            errorNoty = messageSource.getMessage("session.expire", null, localeResolver.resolveLocale(request));
        }
        if (StringUtils.isEmpty(successNoty)) {
            successNoty = (String) request.getSession().getAttribute("successNoty");
            request.getSession().removeAttribute("successNoty");
        }
        if (StringUtils.isEmpty(successNoty) && RequestContextUtils.getInputFlashMap(request) != null) {
            successNoty = (String) RequestContextUtils.getInputFlashMap(request).get("successNoty");
        }
        model.addObject("successNoty", successNoty);
        /**/
        if (StringUtils.isEmpty(errorNoty)) {
            errorNoty = (String) request.getSession().getAttribute("errorNoty");
            request.getSession().removeAttribute("errorNoty");
        }
        if (StringUtils.isEmpty(errorNoty) && RequestContextUtils.getInputFlashMap(request) != null) {
            errorNoty = (String) RequestContextUtils.getInputFlashMap(request).get("errorNoty");
        }
        /**/
        model.addObject("errorNoty", errorNoty);
        model.addObject("captchaType", CAPTCHA_TYPE);
        model.addObject("startupPage", startupPage == null ? "trading" : startupPage);
        model.addObject("startupSubPage", startupSubPage == null ? "" : startupSubPage);
        model.addObject("sessionId", request.getSession().getId());
        model.addObject("notify2fa", principal != null
                                                && (Boolean) WebUtils.getSessionAttribute(request, "first_entry_after_login")
                                                && !userService.isLogin2faUsed(principal.getName()));
        WebUtils.setSessionAttribute(request, "first_entry_after_login", false);
        if (principal != null && !surveyService.checkPollIsDoneByUser(principal.getName())) {
            model.addObject("firstLogin", true);
            surveyService.savePollAsDoneByUser(principal.getName());
        }
        model.setViewName("globalPages/dashboard");
        OrderCreateDto orderCreateDto = new OrderCreateDto();
        model.addObject(orderCreateDto);
        if (principal != null) {
            userService.updateGaTag(getGaCookie(request),principal.getName());
            User user = userService.findByEmail(principal.getName());
            int userStatus = user.getStatus().getStatus();
            boolean accessToOperationForUser = userOperationService.getStatusAuthorityForUserByOperation(userService.getIdByEmail(principal.getName()), UserOperationAuthority.TRADING);
            model.addObject("accessToOperationForUser", accessToOperationForUser);

            model.addObject("userEmail", principal.getName());
            model.addObject("userStatus", userStatus);
            model.addObject("roleSettings", userRoleService.retrieveSettingsForRole(user.getRole().getRole()));
            model.addObject("referalPercents", referralService.findAllReferralLevels()
                    .stream()
                    .filter(p -> p.getPercent().compareTo(BigDecimal.ZERO) > 0)
                    .collect(toList()));
        }
        if (principal == null) {
            request.getSession().setAttribute("lastPageBeforeLogin", request.getRequestURI());
        }
        if (currencyPair != null) {
            currencyService.findPermitedCurrencyPairs(CurrencyPairType.MAIN).stream()
                    .filter(p -> p.getPairType() == CurrencyPairType.MAIN)
                    .filter(p -> p.getName().equals(currencyPair))
                    .limit(1)
                    .forEach(p -> model.addObject("preferedCurrencyPairName", currencyPair));
        }

        return model;
    }

    @RequestMapping(value = {"/ico_dashboard"})
    public ModelAndView icoDashboard(
            @RequestParam(required = false) String errorNoty,
            @RequestParam(required = false) String successNoty,
            @RequestParam(required = false) String startupPage,
            @RequestParam(required = false) String startupSubPage,
            @RequestParam(required = false) String currencyPair,
            HttpServletRequest request, Principal principal) {
        ModelAndView model = new ModelAndView();
        List<CurrencyPair> currencyPairs = currencyService.getAllCurrencyPairs(CurrencyPairType.ICO);
        if (currencyPairs.isEmpty()) {
            model.setViewName("redirect:/dashboard");
            return model;
        }
        if (StringUtils.isEmpty(successNoty)) {
            successNoty = (String) request.getSession().getAttribute("successNoty");
            request.getSession().removeAttribute("successNoty");
        }
        if (StringUtils.isEmpty(successNoty) && RequestContextUtils.getInputFlashMap(request) != null) {
            successNoty = (String) RequestContextUtils.getInputFlashMap(request).get("successNoty");
        }
        model.addObject("successNoty", successNoty);
        /**/
        if (StringUtils.isEmpty(errorNoty)) {
            errorNoty = (String) request.getSession().getAttribute("errorNoty");
            request.getSession().removeAttribute("errorNoty");
        }
        if (StringUtils.isEmpty(errorNoty) && RequestContextUtils.getInputFlashMap(request) != null) {
            errorNoty = (String) RequestContextUtils.getInputFlashMap(request).get("errorNoty");
        }
        /**/
        model.addObject("errorNoty", errorNoty);
        model.addObject("captchaType", CAPTCHA_TYPE);
        model.addObject("startupPage", startupPage == null ? "trading" : startupPage);
        model.addObject("startupSubPage", startupSubPage == null ? "" : startupSubPage);
        model.addObject("sessionId", request.getSession().getId());
       /* model.addObject("notify2fa", principal != null && !userService.isLogin2faUsed(principal.getName()));*/
        model.setViewName("globalPages/ico_dashboard");
        OrderCreateDto orderCreateDto = new OrderCreateDto();
        model.addObject(orderCreateDto);
        if (principal != null) {
            User user = userService.findByEmail(principal.getName());
            int userStatus = user.getStatus().getStatus();
            model.addObject("userEmail", principal.getName());
            model.addObject("userStatus", userStatus);
            model.addObject("roleSettings", userRoleService.retrieveSettingsForRole(user.getRole().getRole()));
            model.addObject("referalPercents", referralService.findAllReferralLevels()
                    .stream()
                    .filter(p -> p.getPercent().compareTo(BigDecimal.ZERO) > 0)
                    .collect(toList()));
        }
        if (principal == null) {
            request.getSession().setAttribute("lastPageBeforeLogin", request.getRequestURI());
        }
        if (currencyPair != null) {
            currencyService.findPermitedCurrencyPairs(CurrencyPairType.ICO).stream()
                    .filter(p -> p.getPairType() == CurrencyPairType.ICO)
                    .filter(p -> p.getName().equals(currencyPair))
                    .limit(1)
                    .forEach(p -> model.addObject("preferedCurrencyPairName", currencyPair));
        }
        return model;
    }


    @RequestMapping("/settings")
    public ModelAndView settings(Principal principal, @RequestParam(required = false) Integer tabIdx, @RequestParam(required = false) String msg,
                                 HttpServletRequest request) {
        final User user = userService.getUserById(userService.getIdByEmail(principal.getName()));
        final ModelAndView mav = new ModelAndView("globalPages/settings");
        final List<UserFile> userFile = userService.findUserDoc(user.getId());
        final Map<String, ?> map = RequestContextUtils.getInputFlashMap(request);
     /*   List<NotificationOption> notificationOptions = notificationService.getNotificationOptionsByUser(user.getId());
        notificationOptions.forEach(option -> option.localize(messageSource, localeResolver.resolveLocale(request)));
        NotificationOptionsForm notificationOptionsForm = new NotificationOptionsForm();
        notificationOptionsForm.setOptions(notificationOptions);*/
        if(request.getParameter("2fa") != null) {
            mav.addObject("activeTabId", "2fa-options-wrapper");
        }
        mav.addObject("user", user);
        mav.addObject("tabIdx", tabIdx);
        mav.addObject("sectionid", map != null && map.containsKey("sectionid") ? map.get("sectionid") : null);
        //mav.addObject("errorNoty", map != null ? map.get("msg") : msg);
        mav.addObject("userFiles", userFile);
       /* mav.addObject("notificationOptionsForm", notificationOptionsForm);*/
        mav.addObject("sessionSettings", sessionService.getByEmailOrDefault(user.getEmail()));
        mav.addObject("sessionLifeTimeTypes", sessionService.getAllByActive(true));
        mav.addObject("sessionMinTime", sessionService.getMinSessionTime());
        mav.addObject("sessionMaxTime", sessionService.getMaxSessionTime());
       /* mav.addObject("tBotName", TBOT_NAME);
        mav.addObject("tBotUrl", TBOT_URL);*/
        return mav;
    }

    /*todo move this method from admin controller*/
    @RequestMapping(value = "/settings/uploadFile", method = POST)
    public RedirectView uploadUserDocs(final @RequestParam("file") MultipartFile[] multipartFiles,
                                       RedirectAttributes redirectAttributes,
                                       final Principal principal,
                                       final Locale locale) {
        final RedirectView redirectView = new RedirectView("/settings");
        final User user = userService.getUserById(userService.getIdByEmail(principal.getName()));
        final List<MultipartFile> uploaded = userFilesService.reduceInvalidFiles(multipartFiles);
        redirectAttributes.addFlashAttribute("user", user);
        if (uploaded.isEmpty()) {
            redirectAttributes.addFlashAttribute("userFiles", userService.findUserDoc(user.getId()));
            redirectAttributes.addFlashAttribute("errorNoty", messageSource.getMessage("admin.errorUploadFiles", null, locale));
            return redirectView;
        }
        try {
            userFilesService.createUserFiles(user.getId(), uploaded);
        } catch (final IOException e) {
            log.error(e);
            redirectAttributes.addFlashAttribute("errorNoty", messageSource.getMessage("admin.internalError", null, locale));
            return redirectView;
        }
        redirectAttributes.addFlashAttribute("successNoty", messageSource.getMessage("admin.successUploadFiles", null, locale));
        redirectAttributes.addFlashAttribute("userFiles", userService.findUserDoc(user.getId()));
        redirectAttributes.addFlashAttribute("activeTabId", "files-upload-wrapper");
        return redirectView;
    }

    @ResponseBody
    @RequestMapping(value = "/settings/changePassword/submit", method = POST, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public String submitsettingsPassword(@Valid @ModelAttribute ChangePasswordDto changePasswordDto, BindingResult result,
                                         Principal principal, HttpServletRequest request, HttpServletResponse response) {
        registerFormValidation.validateChangePassword(changePasswordDto, result, localeResolver.resolveLocale(request));
        BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
        User userPrincipal = userService.findByEmail(principal.getName());
        Object message;
        if (result.hasErrors()) {
            response.setStatus(500);
            message = result.getAllErrors().stream().map(DefaultMessageSourceResolvable::getDefaultMessage).collect(toList());
        } else {
            if(bCryptPasswordEncoder.matches(changePasswordDto.getPassword(), userPrincipal.getPassword())) {
                UpdateUserDto updateUserDto = new UpdateUserDto(userPrincipal.getId());
                updateUserDto.setPassword(changePasswordDto.getConfirmPassword());
                updateUserDto.setEmail(principal.getName());
                userService.update(updateUserDto, localeResolver.resolveLocale(request));
                message = messageSource.getMessage("user.settings.changePassword.successful", null, localeResolver.resolveLocale(request));
                userSessionService.invalidateUserSessionExceptSpecific(principal.getName(), RequestContextHolder.currentRequestAttributes().getSessionId());
            } else {
                response.setStatus(500);
                message = messageSource.getMessage("user.settings.changePassword.fail", null, localeResolver.resolveLocale(request));
            }
        }
        return new JSONObject(){{put("message", message);}}.toString();
    }

    /*todo move this method from admin controller*/
    @RequestMapping(value = "settings/changeNickname/submit", method = POST)
    public ModelAndView submitsettingsNickname(@Valid @ModelAttribute User user,@RequestParam("nickname")String newNickName, BindingResult result,
                                               HttpServletRequest request, RedirectAttributes redirectAttributes, Principal principal) {
        registerFormValidation.validateNickname(user, result, localeResolver.resolveLocale(request));

        if (result.hasErrors()) {
            redirectAttributes.addFlashAttribute("errorNoty", "Error. Nickname NOT changed.");
            redirectAttributes.addFlashAttribute("sectionid", "nickname-changing");
        } else {
            boolean userNicknameUpdated = userService.setNickname(newNickName,principal.getName());
            if(userNicknameUpdated){
                redirectAttributes.addFlashAttribute("successNoty", "You have successfully updated nickname");
            }else{
                redirectAttributes.addFlashAttribute("errorNoty", "Error. Nickname NOT changed.");
            }
        }
        redirectAttributes.addFlashAttribute("activeTabId", "nickname-changing-wrapper");

        return new ModelAndView(new RedirectView("/settings"));
    }

    @RequestMapping(value = "/newIpConfirm")
    public ModelAndView verifyEmailForNewIp(@RequestParam("token") String token, HttpServletRequest req) {
        ModelAndView model = new ModelAndView();
        try {
            if (userService.verifyUserEmail(token) != 0) {
                req.getSession().setAttribute("successNoty", messageSource.getMessage("admin.newipproved", null, localeResolver.resolveLocale(req)));
            } else {
                req.getSession().setAttribute("errorNoty", messageSource.getMessage("admin.newipnotproved", null, localeResolver.resolveLocale(req)));
            }
            model.setViewName("redirect:/login");
        } catch (Exception e) {
            model.setViewName("DBError");
            e.printStackTrace();
        }
        return model;
    }


   /* @RequestMapping("/settings/notificationOptions/submit")
    public RedirectView submitNotificationOptions(@ModelAttribute NotificationOptionsForm notificationOptionsForm, RedirectAttributes redirectAttributes,
                                                  HttpServletRequest request, Principal principal) {
        notificationOptionsForm.getOptions().forEach(LOGGER::debug);
        RedirectView redirectView = new RedirectView("/settings");
        int userId = userService.getIdByEmail(principal.getName());
        List<NotificationOption> notificationOptions = notificationOptionsForm.getOptions().
                stream().
                map(option ->
                        {
                            option.setUserId(userId);
                            return option;
                        }
                ).
                collect(toList());
        //TODO uncomment after turning notifications on
        *//*if (notificationOptions.stream().anyMatch(option -> !option.isSendEmail() && !option.isSendNotification())) {
            redirectAttributes.addFlashAttribute("msg", messageSource.getMessage("notifications.invalid", null,
                    localeResolver.resolveLocale(request)));
            return redirectView;

        }*//*

        notificationService.updateUserNotifications(notificationOptions);
        redirectAttributes.addFlashAttribute("activeTabId", "notification-options-wrapper");
        return redirectView;
    }
*/
    @RequestMapping("/settings/sessionOptions/submit")
    public RedirectView submitNotificationOptions(@ModelAttribute SessionParams sessionParams, RedirectAttributes redirectAttributes,
                                                  HttpServletRequest request, Principal principal) {
        RedirectView redirectView = new RedirectView("/settings");
        if (!sessionService.isSessionLifeTypeIdValid(sessionParams.getSessionLifeTypeId())) {
            sessionParams.setSessionLifeTypeId(SessionLifeTypeEnum.INACTIVE_COUNT_LIFETIME.getTypeId());
        }
        if (sessionService.isSessionTimeValid(sessionParams.getSessionTimeMinutes())) {
            try {
                sessionService.saveOrUpdate(sessionParams, principal.getName());
                sessionService.setSessionLifeParams(request);
                redirectAttributes.addFlashAttribute("successNoty", messageSource.getMessage("session.settings.success", null,
                        localeResolver.resolveLocale(request)));
            } catch (Exception e) {
                log.error("error", e);
                redirectAttributes.addFlashAttribute("errorNoty", messageSource.getMessage("session.settings.invalid", null,
                        localeResolver.resolveLocale(request)));
            }
        } else {
            redirectAttributes.addFlashAttribute("errorNoty", messageSource.getMessage("session.settings.time.invalid", null,
                    localeResolver.resolveLocale(request)));
        }
        redirectAttributes.addFlashAttribute("activeTabId", "session-options-wrapper");
        return redirectView;
    }

    @RequestMapping(value = "/settings/2FaOptions/google2fa", method = RequestMethod.POST)
    @ResponseBody
    public Generic2faResponseDto getGoogle2FaState(Principal principal) throws UnsupportedEncodingException {
        User user = userService.findByEmail(principal.getName());
        Boolean isConnected = g2faService.isGoogleAuthenticatorEnable(user.getId());
        Generic2faResponseDto dto = null;
        if (!isConnected) {
            dto = new Generic2faResponseDto(g2faService.generateQRUrl(principal.getName()), g2faService.getGoogleAuthenticatorCode(user.getId()));
        }
        return dto;
    }


    @ResponseBody
    @RequestMapping (value = "/settings/2FaOptions/google2fa_connect_check_creds", method = POST, produces = "application/json;charset=UTF-8")
    public void connectGoogleAuthenticator(String password, String code, HttpServletRequest request, HttpServletResponse response, Principal principal) {
        User user = userService.findByEmail(principal.getName());
        Preconditions.checkState(!g2faService.isGoogleAuthenticatorEnable(user.getId()));
        if (!(g2faService.checkGoogle2faVerifyCode(code, user.getId()) && userService.checkPassword(user.getId(), password))) {
            throw new InvalidCredentialsException(messageSource.getMessage("ga.2fa.invalid_credentials", null, localeResolver.resolveLocale(request)));
        }
        try {
            secureService.checkEventAdditionalPin(request, principal.getName(), NotificationMessageEventEnum.CHANGE_2FA_SETTING, "");
        } catch (PinCodeCheckNeedException e) {
            WebUtils.setSessionAttribute(request, NotificationMessageEventEnum.CHANGE_2FA_SETTING.name(), LocalDateTime.now());
            throw e;
        }
    }


    @ResponseBody
    @RequestMapping (value = "/settings/2FaOptions/google2fa_connect", method = POST, produces = "application/json;charset=UTF-8")
    public String CheckPinAndSet(String pin, HttpServletRequest request, Principal principal) {
        HttpSession session = request.getSession();
        Preconditions.checkState(!g2faService.isGoogleAuthenticatorEnable(userService.getIdByEmail(principal.getName())));
        LocalDateTime sessionParamTime = (LocalDateTime) Preconditions.checkNotNull(session.getAttribute(NotificationMessageEventEnum.CHANGE_2FA_SETTING.name()));
        if (sessionParamTime.plusMinutes(5).isBefore(LocalDateTime.now())) {
            session.removeAttribute(NotificationMessageEventEnum.CHANGE_2FA_SETTING.name());
            throw new SessionParamTimeExceedException(messageSource.getMessage("message.enter.creds.again", null, localeResolver.resolveLocale(request)));
        }
        Preconditions.checkArgument( pin.length() > 2 && pin.length() < 10);
        if (userService.checkPin(principal.getName(), pin, NotificationMessageEventEnum.CHANGE_2FA_SETTING)) {
            session.removeAttribute(NotificationMessageEventEnum.CHANGE_2FA_SETTING.name());
        } else {
            PinDto res = secureService.resendEventPin(request, principal.getName(), NotificationMessageEventEnum.CHANGE_2FA_SETTING, "");
            session.setAttribute(NotificationMessageEventEnum.CHANGE_2FA_SETTING.name(), LocalDateTime.now());
            throw new IncorrectPinException(res);
        }
        g2faService.setEnable2faGoogleAuth(userService.getIdByEmail(principal.getName()), true);
        return new JSONObject(){{put("message", messageSource.getMessage("message.settings_successfully_saved", null, localeResolver.resolveLocale(request)));}}.toString();
    }

    @ResponseBody
    @RequestMapping (value = "/settings/2FaOptions/google2fa_disconnect", method = POST, produces = "application/json;charset=UTF-8")
    public String disconnectGoogleAuthenticator(String password, String code, HttpServletResponse response, Principal principal, HttpServletRequest request) {
        User user = userService.findByEmail(principal.getName());
        Preconditions.checkState(g2faService.isGoogleAuthenticatorEnable(user.getId()));
        Object mutex = WebUtils.getSessionMutex(request.getSession());
        synchronized (mutex) {
            if (!(g2faService.checkGoogle2faVerifyCode(code, user.getId()) && userService.checkPassword(user.getId(), password))) {
                throw new InvalidCredentialsException(messageSource.getMessage("ga.2fa.invalid_credentials", null, localeResolver.resolveLocale(request)));
            }
            g2faService.setEnable2faGoogleAuth(user.getId(), false);
            g2faService.updateGoogleAuthenticatorSecretCodeForUser(user.getId());
        }
        return new JSONObject(){{put("message", messageSource.getMessage("message.settings_successfully_disconnected", null, localeResolver.resolveLocale(request)));}}.toString();
    }


    /*skip resources: img, css, js*/
    @RequestMapping("/news/**/{newsVariant}/newstopic")
    public ModelAndView newsSingle(@PathVariable String newsVariant, HttpServletRequest request) {
        try {
            ModelAndView modelAndView = new ModelAndView();
            modelAndView.setViewName("globalPages/newstopic");
            String path = request.getServletPath(); //   /news/2015/MAY/27/48/ru/newstopic.html
            int newsId = Integer.valueOf(path.split("\\/\\p{Alpha}+\\/{1}[^\\/]*$")[0].split("^.*[\\/]")[1]); // =>  /news/2015/MAY/27/48  => 48
//            String locale = path.split("\\/{1}[^\\/]*$")[0].split("^.*[\\/]")[1];
            News news = newsService.getNews(newsId, new Locale(newsVariant));
            if (news != null) {
                String newsContentPath = new StringBuilder()
                        .append(newsLocationDir)    //    /Users/Public/news/
                        .append(news.getResource()) //                      2015/MAY/27/
                        .append(newsId)             //                                  48
                        .append("/")                //                                     /
                        .append(newsVariant)   //      ru
                        //ignore locale from path and take it from fact locale .append(locale)   //                                                ru
                        .append("/newstopic.html")  //                                          /newstopic.html
                        .toString();                //  /Users/Public/news/2015/MAY/27/48/ru/newstopic.html
                LOGGER.debug("News content path: " + newsContentPath);
                try {
                    String newsContent = new String(Files.readAllBytes(Paths.get(newsContentPath)), "UTF-8"); //content of the newstopic.html
                    news.setContent(newsContent);
                    LOGGER.debug("News content: " + newsContent);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                String newsContent = messageSource.getMessage("news.absent", null, localeResolver.resolveLocale(request));
                news = new News();
                news.setContent(newsContent);
                LOGGER.error("NEWS NOT FOUND");
            }
            modelAndView.addObject("captchaType", CAPTCHA_TYPE);
            modelAndView.addObject("news", news);
            return modelAndView;
        } catch (Exception e) {
            return null;
        }
    }

    /*@ResponseBody
    @RequestMapping(value = "/settings/2FaOptions/submit", method = POST)
    public void submitNotificationOptionsPin(HttpServletRequest request, Principal principal) {
        Map<Integer, Integer> paramsMap = new HashMap<>();
        Arrays.stream(NotificationMessageEventEnum.values()).filter(NotificationMessageEventEnum::isChangable).forEach(p->{
            paramsMap.put(p.getCode(), Integer.parseInt(request.getParameter(String.valueOf(p.getCode()))));

        });
        request.getSession().setAttribute("2fa_newParams", paramsMap);
        secureService.checkEventAdditionalPin(request, principal.getName(), NotificationMessageEventEnum.CHANGE_2FA_SETTING, "");
    }*/

    /*@ResponseBody
    @RequestMapping("/settings/2FaOptions/change")
    public String submitNotificationOptions(String pin, HttpServletRequest request, Principal principal) {
        Map<Integer, Integer> params = (Map<Integer, Integer>) request.getSession().getAttribute("2fa_newParams");
        request.getSession().removeAttribute("2fa_newParams");
        Preconditions.checkArgument(pin.length() > 2 && pin.length() < 15);
        if (userService.checkPin(principal.getName(), pin, NotificationMessageEventEnum.CHANGE_2FA_SETTING)) {
            try {
                int userId = userService.getIdByEmail(principal.getName());
                Map<Integer, NotificationsUserSetting> settingsMap = settingsService.getSettingsMap(userId);
                settingsMap.forEach((k, v) -> {
                    if (NotificationMessageEventEnum.convert(k).isChangable()) {
                        Integer notificatorId = params.get(k);
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
                    }
                });
                return messageSource.getMessage("message.settings_successfully_saved", null,
                        localeResolver.resolveLocale(request));
            } catch (Exception e) {
                log.error(e);
                *//*throw new RuntimeException(messageSource.getMessage("message.error_saving_settings", null,
                        localeResolver.resolveLocale(request)));*//*
                throw e;
            }
        } else {
            PinDto res = secureService.resendEventPin(request, principal.getName(), NotificationMessageEventEnum.CHANGE_2FA_SETTING, "");
            throw new IncorrectPinException(res);
        }
    }*/

    /*@ResponseBody
    @RequestMapping("/settings/2FaOptions/getNotyPrice")
    public NotificatorTotalPriceDto getNotyPrice(@RequestParam int id, Principal principal) {
        Preconditions.checkArgument(id == NotificationTypeEnum.TELEGRAM.getCode());
        Subscribable subscribable = Preconditions.checkNotNull(notificatorService.getByNotificatorId(id));
        Object subscription = subscribable.getSubscription(userService.getIdByEmail(principal.getName()));
        UserRole role = userService.getUserRoleFromDB(principal.getName());
        NotificatorTotalPriceDto dto = notificatorService.getPrices(id, role.getRole());
        if (subscription != null && subscription instanceof TelegramSubscription) {
            if (!((TelegramSubscription) subscription).getSubscriptionState().isBeginState()) {
                throw new IllegalStateException();
            }
            dto.setCode(((TelegramSubscription) subscription).getCode());
        }
        return dto;
    }*/

    /*@ResponseBody
    @RequestMapping("/settings/2FaOptions/preconnect_sms")
    public String preconnectSms(@RequestParam String number, Principal principal, HttpServletRequest request) {
        number = number.replaceAll("\\+", "").replaceAll("\\-", "").replaceAll("\\.", "").replaceAll(" ", "");
        if (!NumberUtils.isDigits(number)) {
            throw new UnoperableNumberException();
        }
        Subscribable subscribable = notificatorService.getByNotificatorId(NotificationTypeEnum.SMS.getCode());
        int userId = userService.getIdByEmail(principal.getName());
        SmsSubscriptionDto subscriptionDto = SmsSubscriptionDto.builder()
                .userId(userId)
                .newContact(number)
                .build();
        return subscribable.prepareSubscription(subscriptionDto).toString();
    }

    @ResponseBody
    @RequestMapping("/settings/2FaOptions/confirm_connect_sms")
    public String connectSms(Principal principal) {
        Subscribable subscribable = notificatorService.getByNotificatorId(NotificationTypeEnum.SMS.getCode());
        subscribable.createSubscription(principal.getName());
        return "ok";
    }

    @ResponseBody
    @RequestMapping("/settings/2FaOptions/verify_connect_sms")
    public String verifyConnectSms(@RequestParam String code, Principal principal) {
        Subscribable subscribable = notificatorService.getByNotificatorId(NotificationTypeEnum.SMS.getCode());
        int userId = userService.getIdByEmail(principal.getName());
        SmsSubscriptionDto subscriptionDto = SmsSubscriptionDto.builder()
                .code(code)
                .userId(userId)
                .build();
        return subscribable.subscribe(subscriptionDto).toString();
    }*/

   /* @ResponseBody
    @RequestMapping("/settings/2FaOptions/connect_telegram")
    public String getNotyPrice(Principal principal) {
        Subscribable subscribable = notificatorService.getByNotificatorId(NotificationTypeEnum.TELEGRAM.getCode());
        return subscribable.createSubscription(principal.getName()).toString();
    }*/

    /*@ResponseBody
    @RequestMapping("/settings/2FaOptions/reconnect_telegram")
    public String reconnectTelegram(Principal principal) {
        Subscribable subscribable = notificatorService.getByNotificatorId(NotificationTypeEnum.TELEGRAM.getCode());
        return subscribable.reconnect(principal.getName()).toString();
    }*/



    /*@ResponseBody
    @RequestMapping("/settings/2FaOptions/contact_info")
    public String getInfo(@RequestParam int id, Principal principal) {
        Subscribable subscribable = notificatorService.getByNotificatorId(id);
        Preconditions.checkNotNull(subscribable);
        NotificatorSubscription subscription = subscribable.getSubscription(userService.getIdByEmail(principal.getName()));
        Preconditions.checkState(subscription.isConnected());
        String contact = Preconditions.checkNotNull(subscription.getContactStr());
        int roleId = userService.getUserRoleFromSecurityContext().getRole();
        BigDecimal feePercent = notificatorService.getMessagePrice(id, roleId);
        BigDecimal price = doAction(doAction(subscription.getPrice(), feePercent, ActionType.MULTIPLY_PERCENT), subscription.getPrice(), ActionType.ADD);
        return new JSONObject() {{
            put("contact", contact);
            put("price", price);
        }}.toString();
    }*/


    @ResponseStatus(HttpStatus.NOT_IMPLEMENTED)
    @ExceptionHandler(NoFileForLoadingException.class)
    @ResponseBody
    public ErrorInfo NoFileForLoadingExceptionHandler(HttpServletRequest req, Exception exception) {
        return new ErrorInfo(req.getRequestURL(), exception);
    }

    @ResponseStatus(HttpStatus.ACCEPTED)
    @ExceptionHandler({PinCodeCheckNeedException.class})
    @ResponseBody
    public ErrorInfo pinCodeCheckNeedExceptionHandler(HttpServletRequest req, Exception exception) {
        return new ErrorInfo(req.getRequestURL(), exception, exception.getMessage());
    }

    @ResponseStatus(HttpStatus.ACCEPTED)
    @ExceptionHandler({IncorrectPinException.class})
    @ResponseBody
    public PinDto incorrectPinExceptionHandler(HttpServletRequest req, HttpServletResponse response, Exception exception) {
        IncorrectPinException ex = (IncorrectPinException) exception;
        return ex.getDto();
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
        return new ErrorInfo(req.getRequestURL(), exception, messageSource.getMessage("message.service.unavialble", null, localeResolver.resolveLocale(req)));
    }

    @ResponseStatus(HttpStatus.NOT_IMPLEMENTED)
    @ExceptionHandler(InvalidCredentialsException.class)
    @ResponseBody
    public ErrorInfo InvalidCredentialsExceptionHandler(HttpServletRequest req, Exception exception) {
        return new ErrorInfo(req.getRequestURL(), exception, messageSource.getMessage("ga.2fa.invalid_credentials", null, localeResolver.resolveLocale(req)));
    }

    @ResponseStatus(HttpStatus.NOT_IMPLEMENTED)
    @ExceptionHandler(IncorrectSmsPinException.class)
    @ResponseBody
    public ErrorInfo IncorrectSmsPinExceptionHandler(HttpServletRequest req, Exception exception) {
        return new ErrorInfo(req.getRequestURL(), exception, messageSource.getMessage("message.connectCode.wrong", null, localeResolver.resolveLocale(req)));
    }

    @ResponseStatus(HttpStatus.NOT_IMPLEMENTED)
    @ExceptionHandler(UnoperableNumberException.class)
    @ResponseBody
    public ErrorInfo SmsSubscribeUnoperableNumberExceptionHandler(HttpServletRequest req, Exception exception) {
        return new ErrorInfo(req.getRequestURL(), exception, messageSource.getMessage("message.numberUnoperable", null, localeResolver.resolveLocale(req)));
    }

    @ResponseStatus(HttpStatus.NOT_IMPLEMENTED)
    @ExceptionHandler(PaymentException.class)
    @ResponseBody
    public ErrorInfo msSubscribeMoneyExceptionHandler(HttpServletRequest req, Exception exception) {
        return new ErrorInfo(req.getRequestURL(), exception, messageSource.getMessage("message.notEnoughtUsd", null, localeResolver.resolveLocale(req)));
    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(Exception.class)
    @ResponseBody
    public ErrorInfo OtherErrorsHandler(HttpServletRequest req, Exception exception) {
        if (exception instanceof MerchantException) {
            return new ErrorInfo(req.getRequestURL(), exception,
                    messageSource.getMessage(((MerchantException) (exception)).getReason(), null, localeResolver.resolveLocale(req)));
        }
        log.error(ExceptionUtils.getStackTrace(exception));
        return new ErrorInfo(req.getRequestURL(), exception);
    }

    private String getGaCookie(HttpServletRequest request) {
        String result = null;
        Cookie[] cookies = request.getCookies();
        for (Cookie cookie : cookies) {
            String name = cookie.getName();
            if ("_ga".equalsIgnoreCase(name)) {
                result =  cookie.getValue();
            }
        }
        return result;
    }

}