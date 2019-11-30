package me.exrates.controller;

import lombok.extern.log4j.Log4j2;
import me.exrates.controller.exception.ErrorInfo;
import me.exrates.controller.exception.FileLoadingException;
import me.exrates.controller.exception.NewsCreationException;
import me.exrates.controller.exception.NoFileForLoadingException;
import me.exrates.controller.validator.RegisterFormValidation;
import me.exrates.model.News;
import me.exrates.model.User;
import me.exrates.model.dto.OrderCreateDto;
import me.exrates.model.dto.PinDto;
import me.exrates.model.enums.CurrencyPairType;
import me.exrates.model.exceptions.InvalidCredentialsException;
import me.exrates.model.userOperation.enums.UserOperationAuthority;
import me.exrates.security.exception.IncorrectPinException;
import me.exrates.security.exception.PinCodeCheckNeedException;
import me.exrates.security.service.SecureService;
import me.exrates.service.CurrencyService;
import me.exrates.service.NewsService;
import me.exrates.service.NotificationService;
import me.exrates.service.SessionParamsService;
import me.exrates.service.SurveyService;
import me.exrates.service.UserFilesService;
import me.exrates.service.UserRoleService;
import me.exrates.service.UserService;
import me.exrates.service.exception.IncorrectSmsPinException;
import me.exrates.service.exception.PaymentException;
import me.exrates.service.exception.ServiceUnavailableException;
import me.exrates.service.exception.UnoperableNumberException;
import me.exrates.service.exception.invoice.MerchantException;
import me.exrates.service.notifications.G2faService;
import me.exrates.service.notifications.NotificationsSettingsService;
import me.exrates.service.notifications.NotificatorsService;
import me.exrates.service.session.UserSessionService;
import me.exrates.service.userOperation.UserOperationService;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.PropertySource;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.support.RequestContextUtils;
import org.springframework.web.util.WebUtils;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.Principal;
import java.util.Locale;

import static java.util.stream.Collectors.toList;

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
            userService.updateGaTag(getGaCookie(request), principal.getName());
            User user = userService.findByEmail(principal.getName());
            int userStatus = user.getUserStatus().getStatus();
            boolean accessToOperationForUser = userOperationService.getStatusAuthorityForUserByOperation(userService.getIdByEmail(principal.getName()), UserOperationAuthority.TRADING);
            model.addObject("accessToOperationForUser", accessToOperationForUser);

            model.addObject("userEmail", principal.getName());
            model.addObject("userStatus", userStatus);
            model.addObject("roleSettings", userRoleService.retrieveSettingsForRole(user.getRole().getRole()));
        }
        if (principal == null) {
            request.getSession().setAttribute("lastPageBeforeLogin", request.getRequestURI());
        }
        if (currencyPair != null) {
            currencyService.findPermitedCurrencyPairs(CurrencyPairType.MAIN)
                    .stream()
                    .filter(p -> p.getPairType() == CurrencyPairType.MAIN)
                    .filter(p -> p.getName().equals(currencyPair))
                    .limit(1)
                    .forEach(p -> model.addObject("preferedCurrencyPairName", currencyPair));
        }
        return model;
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
                result = cookie.getValue();
            }
        }
        return result;
    }

}
