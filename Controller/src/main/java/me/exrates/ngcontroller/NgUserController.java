package me.exrates.ngcontroller;

import me.exrates.controller.exception.ErrorInfo;
import me.exrates.dao.exception.UserNotFoundException;
import me.exrates.model.User;
import me.exrates.model.UserEmailDto;
import me.exrates.model.dto.mobileApiDto.AuthTokenDto;
import me.exrates.model.dto.mobileApiDto.UserAuthenticationDto;
import me.exrates.model.enums.NotificationMessageEventEnum;
import me.exrates.model.enums.UserStatus;
import me.exrates.ngcontroller.exception.NgDashboardException;
import me.exrates.ngcontroller.exception.NgResponseException;
import me.exrates.ngcontroller.model.PasswordCreateDto;
import me.exrates.ngcontroller.model.response.ResponseModel;
import me.exrates.ngcontroller.service.NgUserService;
import me.exrates.security.exception.IncorrectPasswordException;
import me.exrates.security.exception.IncorrectPinException;
import me.exrates.security.exception.MissingCredentialException;
import me.exrates.security.ipsecurity.IpBlockingService;
import me.exrates.security.ipsecurity.IpTypesOfChecking;
import me.exrates.security.service.AuthTokenService;
import me.exrates.security.service.CheckIp;
import me.exrates.security.service.SecureService;
import me.exrates.service.ReferralService;
import me.exrates.service.UserService;
import me.exrates.service.notifications.G2faService;
import me.exrates.service.util.RestApiUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.Locale;
import java.util.Optional;

import static org.apache.commons.lang.StringUtils.isEmpty;

@RestController
@RequestMapping(value = "/info/public/v2/users",
        produces = MediaType.APPLICATION_JSON_UTF8_VALUE,
        consumes = MediaType.APPLICATION_JSON_UTF8_VALUE
)
@PropertySource(value = {"classpath:/angular.properties"})
public class NgUserController {

    private static final Logger logger = LogManager.getLogger(NgUserController.class);

    private final IpBlockingService ipBlockingService;
    private final AuthTokenService authTokenService;
    private final UserService userService;
    private final ReferralService referralService;
    private final SecureService secureService;
    private final G2faService g2faService;
    private final NgUserService ngUserService;
    private final UserDetailsService userDetailsService;
    private final PasswordEncoder passwordEncoder;

    @Value("${dev.mode}")
    private boolean DEV_MODE;

    @Autowired
    public NgUserController(IpBlockingService ipBlockingService, AuthTokenService authTokenService,
                            UserService userService, ReferralService referralService,
                            SecureService secureService,
                            G2faService g2faService,
                            NgUserService ngUserService,
                            UserDetailsService userDetailsService,
                            PasswordEncoder passwordEncoder) {
        this.ipBlockingService = ipBlockingService;
        this.authTokenService = authTokenService;
        this.userService = userService;
        this.referralService = referralService;
        this.secureService = secureService;
        this.g2faService = g2faService;
        this.ngUserService = ngUserService;
        this.userDetailsService = userDetailsService;
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping(value = "/authenticate")
//    @CheckIp(value = IpTypesOfChecking.LOGIN)
    public ResponseEntity<AuthTokenDto> authenticate(@RequestBody @Valid UserAuthenticationDto authenticationDto,
                                                     HttpServletRequest request) throws Exception {

        logger.info("authenticate, email = {}, ip = {}", authenticationDto.getEmail(),
                authenticationDto.getClientIp());


        if (StringUtils.isBlank(authenticationDto.getEmail())
                || StringUtils.isBlank(authenticationDto.getPassword())) {
            String message = String.format("User with email: [%s] and/or password: [%s] not found", authenticationDto.getEmail(), authenticationDto.getPassword());
            logger.warn(message);
            throw new NgResponseException("USER_CREDENTIALS_NOT_COMPLETE", message);
        }

        User user;
        try {
            user = userService.findByEmail(authenticationDto.getEmail());
            userService.updateGaTag(getCookie(request.getHeader("GACookies")), user.getEmail());
        } catch (UserNotFoundException esc) {
//            ipBlockingService.failureProcessing(authenticationDto.getClientIp(), IpTypesOfChecking.LOGIN);
            String message = String.format("User with email %s not found", authenticationDto.getEmail());
            logger.warn(message, esc);
            throw new NgResponseException("USER_EMAIL_NOT_FOUND", message);
        }

        if (user.getStatus() == UserStatus.REGISTERED) {
            ngUserService.resendEmailForFinishRegistration(user);
            String message = String.format("User with email %s registration is not complete", authenticationDto.getEmail());
            logger.debug(message);
            throw new NgResponseException("USER_REGISTRATION_NOT_COMPLETED", message);
        }
        if (user.getStatus() == UserStatus.DELETED) {
            String message = String.format("User with email %s is not active", authenticationDto.getEmail());
            logger.debug(message);
            throw new NgResponseException("USER_NOT_ACTIVE", message);
        }
        String password = RestApiUtils.decodePassword(authenticationDto.getPassword());
        UserDetails userDetails = userDetailsService.loadUserByUsername(authenticationDto.getEmail());
        if (!passwordEncoder.matches(password, userDetails.getPassword())) {
            String message = String.format("Invalid password and/or email [%s]", authenticationDto.getEmail());
            logger.error(message);
            throw new NgResponseException("INVALID_CREDENTIALS", message);
        }

        boolean shouldLoginWithGoogle = g2faService.isGoogleAuthenticatorEnable(user.getId());
        if (isEmpty(authenticationDto.getPin())) {
            if (!shouldLoginWithGoogle) {
                secureService.sendLoginPincode(user, request, authenticationDto.getClientIp());
            }
            String mode = shouldLoginWithGoogle ? "GOOGLE" : "EMAIL";
            String message = String.format("User with email: %s must login with %s authorization code", authenticationDto.getEmail(), mode);
            String title = String.format("REQUIRED_%s_AUTHORIZATION_CODE", mode);
            throw new NgResponseException(title, message);
        }

        if (shouldLoginWithGoogle) {
            Integer userId = userService.getIdByEmail(authenticationDto.getEmail());
            if (!g2faService.checkGoogle2faVerifyCode(authenticationDto.getPin(), userId)) {
                String message = String.format("Invalid google auth code from user %s", authenticationDto.getEmail());
                throw new NgResponseException("GOOGLE_AUTHORIZATION_FAILED", message);
            }
        } else {
            if (!userService.checkPin(authenticationDto.getEmail(), authenticationDto.getPin(), NotificationMessageEventEnum.LOGIN)) {
                if (authenticationDto.getTries() % 3 == 0 && authenticationDto.getTries() > 1) {
                    secureService.sendLoginPincode(user, request, authenticationDto.getClientIp());
                }
                String message = String.format("Invalid email auth code from user %s", authenticationDto.getEmail());
                throw new NgResponseException("EMAIL_AUTHORIZATION_FAILED", message);
            }
        }
        AuthTokenDto authTokenDto =
                authTokenService.retrieveTokenNg(authenticationDto, authenticationDto.getClientIp())
                        .orElseThrow(() -> {
                            String message = String.format("Failed to get token for user %s", authenticationDto.getEmail());
                            return new NgResponseException("FAILED_TO_GET_USER_TOKEN", message);
                        });

        authTokenDto.setNickname(user.getNickname());
        authTokenDto.setUserId(user.getId());
        authTokenDto.setLocale(new Locale(userService.getPreferedLang(user.getId())));
        String avatarLogicalPath = userService.getAvatarPath(user.getId());
        String avatarFullPath = avatarLogicalPath == null || avatarLogicalPath.isEmpty() ? null : getAvatarPathPrefix(request) + avatarLogicalPath;
        authTokenDto.setAvatarPath(avatarFullPath);
        authTokenDto.setFinPasswordSet(user.getFinpassword() != null);
        authTokenDto.setReferralReference(referralService.generateReferral(user.getEmail()));
//        ipBlockingService.successfulProcessing(authenticationDto.getClientIp(), IpTypesOfChecking.LOGIN);
        return new ResponseEntity<>(authTokenDto, HttpStatus.OK); // 200
    }

    private String getCookie(String header) {
        final String[] gaValue = new String[2];
        Optional<String> gaCookiesValue = Optional.ofNullable(header);
        gaCookiesValue.ifPresent(value -> gaValue[0] = value.trim().split(";")[0].split("=")[1]);
        return Optional.ofNullable(gaValue[0]).orElse("");
    }

    @PostMapping(value = "/register")
    @CheckIp(value = IpTypesOfChecking.REGISTER)
    public ResponseEntity register(@RequestBody @Valid UserEmailDto userEmailDto, HttpServletRequest request) {

        boolean result = ngUserService.registerUser(userEmailDto, request);

        if (result) {
            ipBlockingService.successfulProcessing(request.getHeader("client_ip"), IpTypesOfChecking.REGISTER);
            return ResponseEntity.ok().build();
        }
        String ipAddress = request.getHeader("client_ip");
        if (ipAddress == null) ipAddress = request.getRemoteAddr();
        ipBlockingService.failureProcessing(ipAddress, IpTypesOfChecking.REGISTER);
        return new ResponseEntity(HttpStatus.BAD_REQUEST);
    }

    private String getAvatarPathPrefix(HttpServletRequest request) {
        return request.getScheme() + "://" + request.getServerName() +
                ":" + request.getServerPort() + "/rest";
    }

    @PostMapping("/createPassword")
    public ResponseEntity savePassword(@RequestBody @Valid PasswordCreateDto passwordCreateDto,
                                       HttpServletRequest request) {
        AuthTokenDto tokenDto = ngUserService.createPassword(passwordCreateDto, request);
        return new ResponseEntity<>(tokenDto, HttpStatus.OK);
    }

    @PostMapping("/recoveryPassword")
    @CheckIp(value = IpTypesOfChecking.REQUEST_FOR_RECOVERY_PASSWORD)
    public ResponseEntity requestForRecoveryPassword(@RequestBody @Valid UserEmailDto userEmailDto,
                                                     HttpServletRequest request) {
        boolean result = ngUserService.recoveryPassword(userEmailDto, request);
        if (!result) {
            ipBlockingService.failureProcessing(request.getHeader("client_ip"), IpTypesOfChecking.REQUEST_FOR_RECOVERY_PASSWORD);
        }
        ipBlockingService.successfulProcessing(request.getHeader("client_ip"), IpTypesOfChecking.REQUEST_FOR_RECOVERY_PASSWORD);
        return result ? ResponseEntity.ok().build() : ResponseEntity.badRequest().build();
    }

    @PostMapping("/createRecoveryPassword")
    @CheckIp(value = IpTypesOfChecking.CREATE_RECOVERY_PASSWORD)
    public ResponseEntity createRecoveryPassword(@RequestBody @Valid PasswordCreateDto passwordCreateDto,
                                                 HttpServletRequest request) {
        boolean result = ngUserService.createPasswordRecovery(passwordCreateDto, request);
        if (!result) {
            ipBlockingService.failureProcessing(request.getHeader("client_ip"), IpTypesOfChecking.CREATE_RECOVERY_PASSWORD);
        }
        ipBlockingService.successfulProcessing(request.getHeader("client_ip"), IpTypesOfChecking.CREATE_RECOVERY_PASSWORD);
        return result ? ResponseEntity.ok().build() : ResponseEntity.badRequest().build();
    }

    @GetMapping("/validateTempToken/{token}")
    public ResponseModel<Boolean> checkTempToken(@PathVariable("token") String token) {
        return new ResponseModel<>(ngUserService.validateTempToken(token));
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler({NgDashboardException.class, MethodArgumentNotValidException.class})
    @ResponseBody
    public ErrorInfo OtherErrorsHandler(HttpServletRequest req, Exception exception) {
        return new ErrorInfo(req.getRequestURL(), exception);
    }

    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    @ExceptionHandler({IncorrectPasswordException.class})
    @ResponseBody
    public ErrorInfo UnauthorizedErrorsHandler(HttpServletRequest req, Exception exception) {
        return new ErrorInfo(req.getRequestURL(), exception);
    }

    @ResponseStatus(HttpStatus.I_AM_A_TEAPOT)
    @ExceptionHandler({IncorrectPinException.class})
    @ResponseBody
    public ErrorInfo IncorrectPinExceptionHandler(HttpServletRequest req, Exception exception) {
        return new ErrorInfo(req.getRequestURL(), exception);
    }
//
//    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
//    @ExceptionHandler({NullPointerException.class})
//    @ResponseBody
//    public ErrorInfo npeHandler(HttpServletRequest req, Exception exception) {
//        logger.error(exception);
//        return new ErrorInfo(req.getRequestURL(), exception);
//    }
}
