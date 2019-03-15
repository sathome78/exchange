package me.exrates.controller;

import me.exrates.controller.exception.ErrorInfo;
import me.exrates.controller.exception.NotCreateUserException;
import me.exrates.controller.exception.PasswordCreationException;
import me.exrates.controller.validator.FeedbackMessageFormValidator;
import me.exrates.controller.validator.RegisterFormValidation;
import me.exrates.model.User;
import me.exrates.model.UserEmailDto;
import me.exrates.model.dto.QRCodeDto;
import me.exrates.model.dto.UpdateUserDto;
import me.exrates.model.enums.OrderHistoryPeriod;
import me.exrates.model.enums.OrderType;
import me.exrates.model.enums.TokenType;
import me.exrates.model.enums.UserRole;
import me.exrates.model.enums.UserStatus;
import me.exrates.model.form.FeedbackMessageForm;
import me.exrates.model.vo.openApiDoc.OpenApiMethodDoc;
import me.exrates.model.vo.openApiDoc.OpenApiMethodGroup;
import me.exrates.security.exception.BannedIpException;
import me.exrates.security.exception.IncorrectPinException;
import me.exrates.security.exception.PinCodeCheckNeedException;
import me.exrates.security.exception.UnconfirmedUserException;
import me.exrates.security.service.SecureService;
import me.exrates.service.QRCodeService;
import me.exrates.service.ReferralService;
import me.exrates.service.SendMailService;
import me.exrates.service.TransactionService;
import me.exrates.service.UserService;
import me.exrates.service.exception.AbsentFinPasswordException;
import me.exrates.service.exception.NotConfirmedFinPasswordException;
import me.exrates.service.exception.WrongFinPasswordException;
import me.exrates.service.geetest.GeetestLib;
import me.exrates.service.util.IpUtils;
import org.apache.axis.utils.SessionUtils;
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
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.view.RedirectView;
import org.springframework.web.util.WebUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.File;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.security.Principal;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.TimeZone;
import java.util.stream.Collectors;

import static java.util.Collections.singletonMap;

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
    private HttpServletRequest request;
    @Autowired
    private MessageSource messageSource;
    @Autowired
    private LocaleResolver localeResolver;
    @Autowired
    private ReferralService referralService;
    @Autowired
    private GeetestLib geetest;
    @Autowired
    private QRCodeService qrCodeService;

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

    @RequestMapping("/502")
    public String error502() {
        return "/errorPages/502";
    }

    /**
     * Register user on referral link (redirect to dashboard, call pop-up with registration)
     *
     * @param refReference
     * @param attr
     * @return ModalAndView (dashboard), referral link (if this exists), user object with parent email
     */
    @RequestMapping("/register")
    public ModelAndView registerUser(@RequestParam(value = "ref", required = false) String refReference, RedirectAttributes attr) {

        User refferalRoot = userService.getCommonReferralRoot();
        String parentEmail = "";

        if (!Objects.isNull(refReference)) {
            final Optional<Integer> parentId = referralService.reduceReferralRef(refReference);
            if (parentId.isPresent()) {
                parentEmail = userService.getUserById(parentId.get()).getEmail();
            }
        } else if (refferalRoot != null) {
            parentEmail = refferalRoot.getEmail();
        }

        logger.info("*** Used referral link with reference (" + refReference + ") && Parent email: " + parentEmail);

        attr.addFlashAttribute("refferalLink", refReference);
        attr.addFlashAttribute("parentEmail", parentEmail);

        return new ModelAndView(new RedirectView("/dashboard"));
    }

    @RequestMapping("/generateReferral")
    public
    @ResponseBody
    Map<String, String> generateReferral(final Principal principal) {
        if (principal == null) return null;
        return singletonMap("referral", referralService.generateReferral(principal.getName()));
    }


    @RequestMapping(value = "/createUser", method = RequestMethod.POST)
    public ResponseEntity createNewUser(@ModelAttribute("user") UserEmailDto userEmailDto,
                                        @RequestParam(required = false) String source,
                                        BindingResult result,
                                        HttpServletRequest request) {
        String challenge = request.getParameter(GeetestLib.fn_geetest_challenge);
        String validate = request.getParameter(GeetestLib.fn_geetest_validate);
        String seccode = request.getParameter(GeetestLib.fn_geetest_seccode);
        User user = new User();
        user.setEmail(userEmailDto.getEmail());
        user.setParentEmail(userEmailDto.getParentEmail());
        int gt_server_status_code = (Integer) request.getSession().getAttribute(geetest.gtServerStatusSessionKey);
        String userid = (String) request.getSession().getAttribute("userid");

        HashMap<String, String> param = new HashMap<>();
        param.put("user_id", userid);

        int gtResult = 0;
        if (gt_server_status_code == 1) {
            gtResult = geetest.enhencedValidateRequest(challenge, validate, seccode, param);
            logger.info(gtResult);
        } else {
            logger.error("failback:use your own server captcha validate");
            gtResult = geetest.failbackValidateRequest(challenge, validate, seccode);
            logger.error(gtResult);
        }

        if (gtResult == 1) {
            registerFormValidation.validate(null, user.getEmail(), null, result, localeResolver.resolveLocale(request));
            user.setPhone("");
            if (result.hasErrors()) {
                return ResponseEntity.badRequest().body(result);
            } else {
                boolean flag = false;
                try {
                    String ip = IpUtils.getClientIpAddress(request, 100);
                    if (ip == null) {
                        ip = request.getRemoteHost();
                    }
                    user.setIp(ip);
                    if (userService.create(user, localeResolver.resolveLocale(request), source)) {
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
                    if (child > 0 && parent > 0) {
                        referralService.bindChildAndParent(child, parent);
                        logger.info("*** Referal graph | Child: " + user.getEmail() + " && Parent: " + user.getParentEmail());
                    }

                    String successNoty = null;
                    try {
                        successNoty = URLEncoder.encode(messageSource.getMessage("register.sendletter", null,
                                localeResolver.resolveLocale(request)), "utf-8");
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }

                    Map<String, Object> body = new HashMap<>();
                    body.put("result", successNoty);
                    body.put("user", user);
                    return ResponseEntity.ok(body);

                } else {
                    throw new NotCreateUserException("DBError");
                }
            }
        } else {
            //TODO
            throw new RuntimeException("Geetest error");
        }
    }

    @RequestMapping(value = "/createPassword", method = RequestMethod.GET)
    public ModelAndView createPassword(@RequestParam(required = false) String view, HttpServletRequest request) {
        ModelAndView mav = new ModelAndView("fragments/createPassword");
        mav.addObject("view", view);
        mav.addObject("user", WebUtils.getRequiredSessionAttribute(request, "reg_user"));
        return mav;
    }

    @RequestMapping(value = "/createPasswordConfirm", method = RequestMethod.POST)
    public ModelAndView createPassword(@ModelAttribute User user,
                                       @RequestParam(required = false) String view,
                                       BindingResult result,
                                       HttpServletRequest request,
                                       RedirectAttributes attr) {
        registerFormValidation.validate(null, null, user.getPassword(), result, localeResolver.resolveLocale(request));
        if (result.hasErrors()) {
            //TODO
            throw new PasswordCreationException("Error while creating password.");
        } else {
            User sessionUser = (User) WebUtils.getRequiredSessionAttribute(request, "reg_user");
            User userUpdate = userService.findByEmail(sessionUser.getEmail());
            UpdateUserDto updateUserDto = new UpdateUserDto(userUpdate.getId());
            updateUserDto.setPassword(user.getPassword());
            updateUserDto.setRole(UserRole.USER);
            updateUserDto.setStatus(UserStatus.ACTIVE);
            userService.updateUserByAdmin(updateUserDto);
            attr.addFlashAttribute("successNoty", messageSource.getMessage("register.successfullyproved", null, localeResolver.resolveLocale(request)));
            WebUtils.setSessionAttribute(request, "reg_user", null);
            if (view != null && view.equals("ico_dashboard")) {
                return new ModelAndView("redirect:/ieo_dashboard?login");
            }
            return new ModelAndView("redirect:/dashboard?login");
        }
    }

    @RequestMapping(value = "/registrationConfirm")
    public ModelAndView verifyEmail(HttpServletRequest request,
                                    @RequestParam("token") String token,
                                    @RequestParam(required = false) String view,
                                    RedirectAttributes attr) {
        ModelAndView model = new ModelAndView();
        try {
            int userId = userService.verifyUserEmail(token);
            if (userId != 0) {
                User user = userService.getUserById(userId);
                WebUtils.setSessionAttribute(request, "reg_user", user);
                attr.addFlashAttribute("successConfirm", messageSource.getMessage("register.successfullyproved", null, localeResolver.resolveLocale(request)));
                user.setRole(UserRole.ROLE_CHANGE_PASSWORD);
                user.setStatus(UserStatus.REGISTERED);
                user.setPassword(null);
                if (view != null) {
                    model.addObject("view", view);
                    model.setViewName("redirect:/createPassword");
                }
            } else {
                attr.addFlashAttribute("errorNoty", messageSource.getMessage("register.unsuccessfullyproved", null, localeResolver.resolveLocale(request)));
                model.setViewName("redirect:/dashboard");
            }
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
                              @RequestParam(value = "error", required = false) String error, HttpServletRequest request, RedirectAttributes attr) {
        if (principal != null) {
            return new ModelAndView(new RedirectView("/dashboard"));
        }
        logger.info("login(), last security exception " + httpSession.getAttribute("SPRING_SECURITY_LAST_EXCEPTION") + " sessionId " + httpSession.getId());
        if (httpSession.getAttribute("SPRING_SECURITY_LAST_EXCEPTION") != null) {
                String[] parts = httpSession.getAttribute("SPRING_SECURITY_LAST_EXCEPTION").getClass().getName().split("\\.");
                String exceptionClass = parts[parts.length - 1];
                logger.info("login(), exceptionClass {}", exceptionClass);
                if (exceptionClass.equals("DisabledException")) {
                    attr.addFlashAttribute("blockedUser", messageSource.getMessage("login.blocked", null, localeResolver.resolveLocale(request)));
                    attr.addFlashAttribute("contactsUrl", "/contacts");
                } else if (exceptionClass.equals("BadCredentialsException")) {
                    attr.addFlashAttribute("loginErr", messageSource.getMessage("login.notFound", null, localeResolver.resolveLocale(request)));
                } else if (exceptionClass.equals("NotVerifiedCaptchaError")) {
                    attr.addFlashAttribute("loginErr", messageSource.getMessage("register.capchaincorrect", null, localeResolver.resolveLocale(request)));
                } else if (exceptionClass.equals("PinCodeCheckNeedException")) {
                    logger.debug("pin needed");
                    PinCodeCheckNeedException exception = (PinCodeCheckNeedException) httpSession.getAttribute("SPRING_SECURITY_LAST_EXCEPTION");
                    attr.addFlashAttribute("pinNeed", exception.getMessage());
                } else if (exceptionClass.equals("IncorrectPinException")) {
                    System.out.println("pin needed");
                    IncorrectPinException exception = (IncorrectPinException) httpSession.getAttribute("SPRING_SECURITY_LAST_EXCEPTION");
                    attr.addFlashAttribute("pinNeed", exception.getMessage());
                    attr.addFlashAttribute("pinError", messageSource.getMessage("message.pin_code.incorrect", null, localeResolver.resolveLocale(request)));
                } else if (exceptionClass.equals("BannedIpException")) {
                    BannedIpException exception = (BannedIpException) httpSession.getAttribute("SPRING_SECURITY_LAST_EXCEPTION");
                    attr.addFlashAttribute("loginErr", exception.getMessage());
                } else if (exceptionClass.equals("UnconfirmedUserException")) {
                    UnconfirmedUserException exception = (UnconfirmedUserException) httpSession.getAttribute("SPRING_SECURITY_LAST_EXCEPTION");
                    attr.addFlashAttribute("unconfirmedUserEmail", exception.getMessage());
                    attr.addFlashAttribute("unconfirmedUserMessage", messageSource.getMessage("register.unconfirmedUserMessage",
                            new Object[]{exception.getMessage()}, localeResolver.resolveLocale(request)));
                    attr.addFlashAttribute("unconfirmedUser", messageSource.getMessage("register.unconfirmedUser", null, localeResolver.resolveLocale(request)));
                } else {
                    attr.addFlashAttribute("loginErr", messageSource.getMessage("login.errorLogin", null, localeResolver.resolveLocale(request)));
                }
                httpSession.setAttribute("SPRING_SECURITY_LAST_EXCEPTION", null);
        }

        return new ModelAndView(new RedirectView("/dashboard"));

    }

    /*@ResponseBody
    @RequestMapping(value = "/login/new_pin_send", method = RequestMethod.POST)
    public ResponseEntity<String> sendLoginPinAgain(HttpServletRequest request, HttpServletResponse response) {
        response.setCharacterEncoding("UTF-8");
        Object auth = request.getSession().getAttribute("authentication");
        if (auth == null) {
            return ResponseEntity.badRequest().contentType(MediaType.APPLICATION_JSON_UTF8).body("error");
        }
        Authentication authentication = (Authentication) auth;
        org.springframework.security.core.userdetails.User principal = (org.springframework.security.core.userdetails.User) authentication.getPrincipal();
        String res = secureService.reSendLoginMessage(request, authentication.getName(), true).getMessage();
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .body(res);
    }*/

    @ResponseBody
    @RequestMapping(value = "/register/new_link_to_confirm", method = RequestMethod.POST)
    public void sendRegisterLinkAgain(@ModelAttribute("unconfirmedUserEmail") String unconfirmedUserEmail, @RequestParam(required = false) String source, Locale locale) {
        User userForSend = userService.findByEmail(unconfirmedUserEmail);
        if (source != null && !source.isEmpty()) {
            String viewForRequest = "view=" + source;
            userService.sendEmailWithToken(userForSend, TokenType.REGISTRATION, "/registrationConfirm", "emailsubmitregister.subject", "emailsubmitregister.text", locale, null, viewForRequest);
        } else {
            userService.sendEmailWithToken(userForSend, TokenType.REGISTRATION, "/registrationConfirm", "emailsubmitregister.subject", "emailsubmitregister.text", locale);
        }
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

    @RequestMapping(value = "/utcOffset")
    @ResponseBody
    public Integer getServerUtcOffsetMinutes() {
        return TimeZone.getDefault().getOffset(System.currentTimeMillis()) / (1000 * 60);
    }


    @RequestMapping(value = "/api_docs", method = RequestMethod.GET)
    public ModelAndView apiDocs(HttpServletRequest request) {
        ModelAndView modelAndView = new ModelAndView("/globalPages/apiDocs", "captchaType", CAPTCHA_TYPE);
        String baseUrl = String.join("", "https://", request.getServerName(), "/openapi/v1");
        modelAndView.addObject("baseUrl", baseUrl);
        modelAndView.addObject("orderTypeValues", Arrays.asList(OrderType.values()));
        modelAndView.addObject("periodValues", Arrays.stream(OrderHistoryPeriod.values())
                .map(OrderHistoryPeriod::toUrlValue).collect(Collectors.toList()));
        Map<OpenApiMethodGroup, List<OpenApiMethodDoc>> methodGroups = Arrays.stream(OpenApiMethodDoc.values())
                .collect(Collectors.groupingBy(OpenApiMethodDoc::getMethodGroup));
        modelAndView.addObject("publicMethodsInfo", methodGroups.get(OpenApiMethodGroup.PUBLIC));
        modelAndView.addObject("userMethodsInfo", methodGroups.get(OpenApiMethodGroup.USER_INFO));
        modelAndView.addObject("orderMethodsInfo", methodGroups.get(OpenApiMethodGroup.ORDERS));
        return modelAndView;
    }

    @ResponseBody
    @GetMapping(value = "/getQrCode", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<QRCodeDto> getQrCodeBytes(@RequestParam("ticker") String ticker,
                                                    @RequestParam("wallet") String wallet,
                                                    @RequestParam("amount") BigDecimal amountToWithdraw) {
        return ResponseEntity.ok(qrCodeService.getQrCodeImage(ticker, wallet, amountToWithdraw));
    }
}
