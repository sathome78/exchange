package me.exrates.controller;

import com.captcha.botdetect.web.servlet.Captcha;
import me.exrates.controller.exception.ErrorInfo;
import me.exrates.controller.exception.NotCreateUserException;
import me.exrates.controller.validator.FeedbackMessageFormValidator;
import me.exrates.controller.validator.RegisterFormValidation;
import me.exrates.model.User;
import me.exrates.model.form.FeedbackMessageForm;
import me.exrates.security.exception.BannedIpException;
import me.exrates.security.exception.IncorrectPinException;
import me.exrates.security.exception.PinCodeCheckNeedException;
import me.exrates.security.filter.VerifyReCaptchaSec;
import me.exrates.security.service.SecureService;
import me.exrates.service.ReferralService;
import me.exrates.service.SendMailService;
import me.exrates.service.TransactionService;
import me.exrates.service.UserService;
import me.exrates.service.exception.AbsentFinPasswordException;
import me.exrates.service.exception.NotConfirmedFinPasswordException;
import me.exrates.service.exception.WrongFinPasswordException;
import me.exrates.service.util.IpUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.view.RedirectView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.security.Principal;
import java.util.Map;
import java.util.Optional;
import java.util.TimeZone;

import static java.util.Collections.singletonMap;
import static java.util.Objects.isNull;

@Controller
@PropertySource(value = {"classpath:about_us.properties", "classpath:/captcha.properties"})
public class MainController {

    private static final Logger logger = LogManager.getLogger(MainController.class);
    @Value("${captcha.type}")
    String CAPTCHA_TYPE;
    private
    @Value("${contacts.telephone}")
    String telephone;
    private
    @Value("${contacts.email}")
    String email;
    @Value("${contacts.feedbackEmail}")
    String feedbackEmail;
    @Autowired
    private UserService userService;
    @Autowired
    private RegisterFormValidation registerFormValidation;
    @Autowired
    private FeedbackMessageFormValidator messageFormValidator;

    @Autowired
    private HttpServletRequest request;
    @Autowired
    private TransactionService transactionService;
    @Autowired
    private MessageSource messageSource;
    @Autowired
    private LocaleResolver localeResolver;
    @Autowired
    private VerifyReCaptchaSec verifyReCaptchaSec;
    @Autowired
    private ReferralService referralService;
    @Autowired
    private SendMailService sendMailService;
    @Autowired
    private SecureService secureService;

    @RequestMapping(value = "57163a9b3d1eafe27b8b456a.txt", method = RequestMethod.GET)
    @ResponseBody
    public FileSystemResource getFile() {
        final ClassLoader classLoader = getClass().getClassLoader();
        final File file = new File(classLoader.getResource("public/57163a9b3d1eafe27b8b456a.txt").getFile());
        return new FileSystemResource(file);
    }

    @RequestMapping("/403")
    public String error403() {
        return "403";
    }

    @RequestMapping("/register")
    public ModelAndView registerUser(@RequestParam(value = "ref", required = false) String refReference, HttpServletRequest request) {
        User user = new User();
        ModelAndView mav = new ModelAndView("register", "user", user);
        mav.addObject("cpch", "");
        mav.addObject("captchaType", CAPTCHA_TYPE);
        if (!isNull(refReference)) {
            final Optional<Integer> parentId = referralService.reduceReferralRef(refReference);
            if (parentId.isPresent()) {
                final String email = userService.getUserById(parentId.get()).getEmail();
                if (email != null) {
                    user.setParentEmail(email);
                    return mav;
                }
            }
        }
        //TODO for Denis
        User refferalRoot = userService.getCommonReferralRoot();
        if (refferalRoot != null) {
            user.setParentEmail(refferalRoot.getEmail());
        }
        return mav;
    }

    @RequestMapping("/generateReferral")
    public
    @ResponseBody
    Map<String, String> generateReferral(final Principal principal) {
        if (principal == null) return null;
        return singletonMap("referral", referralService.generateReferral(principal.getName()));
    }

    @RequestMapping(value = "/create", method = RequestMethod.POST)
    public ModelAndView createUser(@ModelAttribute("user") User user, BindingResult result, HttpServletRequest request, RedirectAttributes redirectAttributes) {
        boolean flag = false;
        String captchaType = request.getParameter("captchaType");
        Map<String, String> xssErrors = (Map<String, String>) request.getAttribute("xssErrors");
        if ( xssErrors.size() != 0 ){
            for (Map.Entry<String, String> errorsEntry : xssErrors.entrySet()) {
                result.rejectValue(errorsEntry.getKey(),errorsEntry.getValue());
            }
            ModelAndView modelAndView = new ModelAndView("register", "user", user);
            modelAndView.addObject("cpch", "");
            modelAndView.addObject("captchaType", CAPTCHA_TYPE);
            return modelAndView;

        }
        switch (captchaType) {
            case "BOTDETECT": {
                String captchaId = request.getParameter("captchaId");
                Captcha captcha = Captcha.load(request, captchaId);
                String captchaCode = request.getParameter("captchaCode");
                if (!captcha.validate(captchaCode)) {
                    String correctCapchaRequired = messageSource.getMessage("register.capchaincorrect", null, localeResolver.resolveLocale(request));
                    ModelAndView modelAndView = new ModelAndView("register", "user", user);
                    modelAndView.addObject("cpch", correctCapchaRequired);
                    modelAndView.addObject("captchaType", CAPTCHA_TYPE);
                    return modelAndView;
                }
                break;
            }
            case "RECAPTCHA": {
                String recapchaResponse = request.getParameter("g-recaptcha-response");
                if ((recapchaResponse != null) && !verifyReCaptchaSec.verify(recapchaResponse)) {
                    String correctCapchaRequired = messageSource.getMessage("register.capchaincorrect", null, localeResolver.resolveLocale(request));
                    ModelAndView modelAndView = new ModelAndView("register", "user", user);
                    modelAndView.addObject("cpch", correctCapchaRequired);
                    modelAndView.addObject("captchaType", CAPTCHA_TYPE);
                    return modelAndView;
                }
                break;
            }
        }

        if (result.hasErrors()) {
            ModelAndView modelAndView = new ModelAndView("register", "user", user);
            modelAndView.addObject("cpch", "");
            modelAndView.addObject("captchaType", CAPTCHA_TYPE);
            return modelAndView;
        }
        registerFormValidation.validate(user, result, localeResolver.resolveLocale(request));
        user.setPhone("");
        if (result.hasErrors()) {
            ModelAndView modelAndView = new ModelAndView("register", "user", user);
            modelAndView.addObject("cpch", "");
            modelAndView.addObject("captchaType", CAPTCHA_TYPE);
            return modelAndView;
        } else {
            user = (User) result.getModel().get("user");
            try {
                String ip = IpUtils.getClientIpAddress(request, 100);
                if (ip == null) {
                    ip = request.getRemoteHost();
                }
                user.setIp(ip);
                if (userService.create(user, localeResolver.resolveLocale(request))) {
                    flag = true;
                    logger.info("User registered with parameters = " + user.toString());
                } else {
                    throw new NotCreateUserException("Error while user creation");
                }
            } catch (Exception e) {
                e.printStackTrace();
                logger.error("User can't be registered with parameters = " + user.toString() + "  " + e.getMessage());
            }
            if (flag) {
                final int child = userService.getIdByEmail(user.getEmail());
                final int parent = userService.getIdByEmail(user.getParentEmail());
                //TODO for Denis
                if (child > 0 && parent > 0) {
                    referralService.bindChildAndParent(child, parent);
                }
                String successNoty = null;
                try {
                    successNoty = URLEncoder.encode(messageSource.getMessage("register.sendletter", null, localeResolver.resolveLocale(request)), "utf-8");
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                ModelAndView modelAndView = new ModelAndView("redirect:/dashboard");
                redirectAttributes.addFlashAttribute("successNoty", successNoty);
                modelAndView.addObject("successRegister");
                return modelAndView;
            } else return new ModelAndView("DBError", "user", user);
        }
    }

    @RequestMapping(value = "/registrationConfirm")
    public ModelAndView verifyEmail(HttpServletRequest request, @RequestParam("token") String token) {
        ModelAndView model = new ModelAndView();
        try {
            if (userService.verifyUserEmail(token) != 0) {
                model.addObject("successNoty", messageSource.getMessage("register.successfullyproved", null, localeResolver.resolveLocale(request)));
            } else {
                model.addObject("errorNoty", messageSource.getMessage("register.unsuccessfullyproved", null, localeResolver.resolveLocale(request)));
            }
            model.setViewName("redirect:/dashboard");
        } catch (Exception e) {
            model.setViewName("DBError");
            e.printStackTrace();
            logger.error("Error while verifing user registration email  " + e.getLocalizedMessage());
        }
        return model;
    }

    @RequestMapping("/personalpage")
    public ModelAndView gotoPersonalPage(@ModelAttribute User user, Principal principal) {
        String host = request.getRemoteHost();
        String email = principal.getName();
        String userIP = userService.logIP(email, host);
        return new ModelAndView("personalpage", "userIP", userIP);
    }

    @RequestMapping(value = "/login", method = RequestMethod.GET)
    public ModelAndView login(HttpSession httpSession, Principal principal,
                              @RequestParam(value = "error", required = false) String error, HttpServletRequest request) {
        if (principal != null) {
            return new ModelAndView(new RedirectView("/dashboard"));
        }
        ModelAndView model = new ModelAndView();
        model.addObject("captchaType", CAPTCHA_TYPE);
        if (error != null) {
            if (httpSession.getAttribute("SPRING_SECURITY_LAST_EXCEPTION") != null) {
                String[] parts = httpSession.getAttribute("SPRING_SECURITY_LAST_EXCEPTION").getClass().getName().split("\\.");
                String exceptionClass = parts[parts.length - 1];
                if (exceptionClass.equals("DisabledException")) {
                    model.addObject("error", messageSource.getMessage("login.blocked", null, localeResolver.resolveLocale(request)));
                    model.addObject("contactsUrl", "/contacts");
                } else if (exceptionClass.equals("BadCredentialsException")) {
                    model.addObject("error", messageSource.getMessage("login.notFound", null, localeResolver.resolveLocale(request)));
                } else if (exceptionClass.equals("NotVerifiedCaptchaError")) {
                    model.addObject("error", messageSource.getMessage("register.capchaincorrect", null, localeResolver.resolveLocale(request)));
                } else if (exceptionClass.equals("PinCodeCheckNeedException")) {
                    PinCodeCheckNeedException exception = (PinCodeCheckNeedException) httpSession.getAttribute("SPRING_SECURITY_LAST_EXCEPTION");
                    model.addObject("pinNeed", exception.getMessage());
                } else if (exceptionClass.equals("IncorrectPinException")) {
                    IncorrectPinException exception = (IncorrectPinException) httpSession.getAttribute("SPRING_SECURITY_LAST_EXCEPTION");
                    model.addObject("pinNeed", exception.getMessage());
                    model.addObject("error", messageSource.getMessage("message.pin_code.incorrect", null, localeResolver.resolveLocale(request)));
                } else if (exceptionClass.equals("BannedIpException")) {
                    BannedIpException exception = (BannedIpException) httpSession.getAttribute("SPRING_SECURITY_LAST_EXCEPTION");
                    model.addObject("error", exception.getMessage());
                } else {
                    model.addObject("error", messageSource.getMessage("login.errorLogin", null, localeResolver.resolveLocale(request)));
                }
            }
        }

        model.setViewName("login");

        return model;

    }

    @ResponseBody
    @RequestMapping(value = "/login/new_pin_send", method = RequestMethod.POST)
    public ResponseEntity<String> sendLoginPinAgain(HttpServletRequest request, HttpServletResponse response) {
        response.setCharacterEncoding("UTF-8");
        Object auth = request.getSession().getAttribute("authentication");
        if (auth == null) {
            ;
            return ResponseEntity.badRequest().contentType(MediaType.APPLICATION_JSON_UTF8).body("error");
        }
        Authentication authentication = (Authentication) auth;
        org.springframework.security.core.userdetails.User principal = (org.springframework.security.core.userdetails.User) authentication.getPrincipal();
        String res = secureService.reSendLoginMessage(request, authentication.getName(), true).getMessage();
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .body(res);
    }


    @RequestMapping(value = "/referral")
    public ModelAndView referral(final Principal principal) {
        final int id = userService.getIdByEmail(principal.getName());
        return new ModelAndView("referral", singletonMap("referralTxs", referralService.findAll(id)));
    }

    @RequestMapping("/aboutUs")
    public ModelAndView aboutUs() {
        ModelAndView modelAndView = new ModelAndView("/globalPages/aboutUs", "captchaType", CAPTCHA_TYPE);
        return modelAndView;
    }

    /*CHECK FIN PASSWORD*/

    @RequestMapping(value = "/checkfinpass", method = RequestMethod.POST)
    @ResponseBody
    public void checkFinPassword(User user, HttpServletRequest request) {
        String enteredFinPassword = user.getFinpassword();
        User storedUser = userService.getUserById(userService.getIdByEmail(user.getEmail()));
        userService.checkFinPassword(enteredFinPassword, storedUser, localeResolver.resolveLocale(request));
    }

    /*
    error handlers for this controller
    * */

    @ResponseStatus(HttpStatus.NOT_ACCEPTABLE)
    @ExceptionHandler(WrongFinPasswordException.class)
    @ResponseBody
    public ErrorInfo WrongFinPasswordExceptionHandler(HttpServletRequest req, Exception exception) {
        return new ErrorInfo(req.getRequestURL(), exception);
    }

    @ResponseStatus(HttpStatus.NOT_ACCEPTABLE)
    @ExceptionHandler(AbsentFinPasswordException.class)
    @ResponseBody
    public ErrorInfo AbsentFinPasswordExceptionHandler(HttpServletRequest req, Exception exception) {
        return new ErrorInfo(req.getRequestURL(), exception);
    }

    @ResponseStatus(HttpStatus.NOT_ACCEPTABLE)
    @ExceptionHandler(NotConfirmedFinPasswordException.class)
    @ResponseBody
    public ErrorInfo NotConfirmedFinPasswordExceptionHandler(HttpServletRequest req, Exception exception) {
        return new ErrorInfo(req.getRequestURL(), exception);
    }

    @RequestMapping(value = "/termsAndConditions", method = RequestMethod.GET)
    public ModelAndView termsAndConditions() {
        return new ModelAndView("/globalPages/termsAndConditions", "captchaType", CAPTCHA_TYPE);
    }

    @RequestMapping(value = "/privacyPolicy", method = RequestMethod.GET)
    public ModelAndView privacyPolicy() {
        return new ModelAndView("/globalPages/privacyPolicy", "captchaType", CAPTCHA_TYPE);
    }

    @RequestMapping(value = "/contacts", method = RequestMethod.GET)
    public ModelAndView contacts(ModelMap model) {
        ModelAndView modelAndView = new ModelAndView("globalPages/contacts", "captchaType", CAPTCHA_TYPE);
        model.forEach((key, value) -> logger.debug(key + " :: " + value));
        if (model.containsAttribute("messageForm")) {
            modelAndView.addObject("messageForm", model.get("messageForm"));
        } else {
            modelAndView.addObject("messageForm", new FeedbackMessageForm());
        }
        return modelAndView;
    }

    @RequestMapping(value = "/partners", method = RequestMethod.GET)
    public ModelAndView partners() {
        return new ModelAndView("/globalPages/partners", "captchaType", CAPTCHA_TYPE);
    }

    @RequestMapping(value = "/sendFeedback", method = RequestMethod.POST)
    @ResponseBody
    public ModelAndView sendFeedback(@ModelAttribute("messageForm") FeedbackMessageForm messageForm, BindingResult result,
                                     HttpServletRequest request, RedirectAttributes redirectAttributes) {


        ModelAndView modelAndView = new ModelAndView("redirect:/contacts");
        String captchaType = request.getParameter("captchaType");
        switch (captchaType) {
            case "BOTDETECT": {
                String captchaId = request.getParameter("captchaId");
                Captcha captcha = Captcha.load(request, captchaId);
                String captchaCode = request.getParameter("captchaCode");
                if (!captcha.validate(captchaCode)) {
                    String correctCapchaRequired = messageSource.getMessage("register.capchaincorrect", null, localeResolver.resolveLocale(request));
                    redirectAttributes.addFlashAttribute("errorNoty", correctCapchaRequired);
                    redirectAttributes.addFlashAttribute("messageForm", messageForm);

                    modelAndView.addObject("captchaType", CAPTCHA_TYPE);
                    return modelAndView;
                }
                break;
            }
            case "RECAPTCHA": {
                String recapchaResponse = request.getParameter("g-recaptcha-response");
                if ((recapchaResponse != null) && !verifyReCaptchaSec.verify(recapchaResponse)) {
                    String correctCapchaRequired = messageSource.getMessage("register.capchaincorrect", null, localeResolver.resolveLocale(request));
                    redirectAttributes.addFlashAttribute("errorNoty", correctCapchaRequired);
                    redirectAttributes.addFlashAttribute("messageForm", messageForm);
                    modelAndView.addObject("captchaType", CAPTCHA_TYPE);
                    return modelAndView;
                }
                break;
            }
        }
        messageFormValidator.validate(messageForm, result, localeResolver.resolveLocale(request));
        result.getAllErrors().forEach(logger::debug);
        if (result.hasErrors()) {
            modelAndView = new ModelAndView("globalPages/contacts", "messageForm", messageForm);
            modelAndView.addObject("cpch", "");
            modelAndView.addObject("captchaType", CAPTCHA_TYPE);
            return modelAndView;
        }

        sendMailService.sendFeedbackMail(messageForm.getSenderName(), messageForm.getSenderEmail(), messageForm.getMessageText(), feedbackEmail);
        redirectAttributes.addFlashAttribute("successNoty", messageSource.getMessage("contacts.lettersent", null, localeResolver.resolveLocale(request)));

        return modelAndView;
    }

    @RequestMapping(value = "/utcOffset")
    @ResponseBody
    public Integer getServerUtcOffsetMinutes() {
        return TimeZone.getDefault().getOffset(System.currentTimeMillis()) / (1000 * 60);
    }


}
