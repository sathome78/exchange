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
import me.exrates.ngcontroller.model.PasswordCreateDto;
import me.exrates.ngcontroller.model.response.ResponseModel;
import me.exrates.ngcontroller.service.NgUserService;
import me.exrates.security.exception.BannedIpException;
import me.exrates.security.exception.IncorrectPasswordException;
import me.exrates.security.exception.IncorrectPinException;
import me.exrates.security.exception.MissingCredentialException;
import me.exrates.security.ipsecurity.IpBlockingService;
import me.exrates.security.service.AuthTokenService;
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

import javax.servlet.http.Cookie;
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
    public ResponseEntity<AuthTokenDto> authenticate(@RequestBody @Valid UserAuthenticationDto authenticationDto,
                                                     HttpServletRequest request) throws Exception {

        logger.info("authenticate, email = {}, ip = {}", authenticationDto.getEmail(),
                authenticationDto.getClientIp());

        Optional<String> gaCookiesValue = Optional.ofNullable(request.getHeader("GACookies"));

        gaCookiesValue.ifPresent(value -> {
            // todo if header is present it looks like
            // GACookies value : _ga=GA1.2.708749341.1544137610; _gid=GA1.2.1072675088.1547038628

            
        });

        try {
            if (!DEV_MODE) {
//                ipBlockingService.checkIp(authenticationDto.getClientIp(), IpTypesOfChecking.LOGIN);
            }
        } catch (BannedIpException ban) {
            return new ResponseEntity<>(HttpStatus.DESTINATION_LOCKED); // 419
        }

        User user;
        try {
            user = userService.findByEmail(authenticationDto.getEmail());
            userService.updateGaTag(getCookie(request), user.getEmail());
        } catch (UserNotFoundException esc) {
            logger.debug("User with email {} not found", authenticationDto.getEmail());
            return new ResponseEntity<>(HttpStatus.UNPROCESSABLE_ENTITY);  // 422
        }

        if (user.getStatus() == UserStatus.REGISTERED) {
            return new ResponseEntity<>(HttpStatus.UPGRADE_REQUIRED); // 426
        }
        if (user.getStatus() == UserStatus.DELETED) {
            return new ResponseEntity<>(HttpStatus.GONE); // 410
        }

        if (user.getStatus() == UserStatus.REGISTERED) {
            return new ResponseEntity<>(HttpStatus.UPGRADE_REQUIRED); // 426
        }
        if (user.getStatus() == UserStatus.DELETED) {
            return new ResponseEntity<>(HttpStatus.GONE); // 410
        }

        if (StringUtils.isBlank(authenticationDto.getEmail())
                || StringUtils.isBlank(authenticationDto.getPassword())) {
            throw new MissingCredentialException("Credentials missing");
        }
        String password = RestApiUtils.decodePassword(authenticationDto.getPassword());
        UserDetails userDetails = userDetailsService.loadUserByUsername(authenticationDto.getEmail());
        if (!passwordEncoder.matches(password, userDetails.getPassword())) {
            throw new IncorrectPasswordException("Incorrect password");
        }

        boolean shouldLoginWithGoogle = g2faService.isGoogleAuthenticatorEnable(user.getId());

        if (!DEV_MODE) {

            if (isEmpty(authenticationDto.getPin())) {

                if (!shouldLoginWithGoogle) {
                    secureService.sendLoginPincode(user, request, authenticationDto.getClientIp());
                }
                return new ResponseEntity<>(HttpStatus.I_AM_A_TEAPOT); //418
            }
        }

        if (shouldLoginWithGoogle) {
            Integer userId = userService.getIdByEmail(authenticationDto.getEmail());
            if (!g2faService.checkGoogle2faVerifyCode(authenticationDto.getPin(), userId)) {
                if (!DEV_MODE) {
                    throw new IncorrectPinException("Incorrect google auth code");
                }
            }
        } else if (!DEV_MODE) {
            if (!userService.checkPin(authenticationDto.getEmail(), authenticationDto.getPin(), NotificationMessageEventEnum.LOGIN)) {
                if (authenticationDto.getTries() > 1 && authenticationDto.getTries() % 3 == 0) {
                    secureService.sendLoginPincode(user, request, authenticationDto.getClientIp());
                }
                throw new IncorrectPinException("Incorrect pin code");
            }
        }

        Optional<AuthTokenDto> authTokenResult;
        try {
            authTokenResult = authTokenService.retrieveTokenNg(authenticationDto, authenticationDto.getClientIp());
        } catch (IncorrectPinException wrongPin) {
            return new ResponseEntity<>(HttpStatus.I_AM_A_TEAPOT); //418
        } catch (UsernameNotFoundException | IncorrectPasswordException e) {
//            ipBlockingService.failureProcessing(authenticationDto.getClientIp(), IpTypesOfChecking.LOGIN);
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED); // 401
        }
        AuthTokenDto authTokenDto =
                authTokenResult.orElseThrow(() -> new Exception("Failed to authenticate user with email: " + authenticationDto.getEmail()));

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

    private String getCookie(HttpServletRequest request) {
        for (Cookie cookie : request.getCookies()) {
            if ("_ga".equalsIgnoreCase(cookie.getName())) {
                return cookie.getValue();
            }
        }
        return "";
    }

    @PostMapping(value = "/register")
    public ResponseEntity register(@RequestBody @Valid UserEmailDto userEmailDto, HttpServletRequest request) {

        boolean result = ngUserService.registerUser(userEmailDto, request);

        if (result) {
            return ResponseEntity.ok().build();
        }

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
    public ResponseEntity requestForRecoveryPassword(@RequestBody @Valid UserEmailDto userEmailDto,
                                                     HttpServletRequest request) {
        boolean result = ngUserService.recoveryPassword(userEmailDto, request);
        return result ? ResponseEntity.ok().build() : ResponseEntity.badRequest().build();
    }

    @PostMapping("/createRecoveryPassword")
    public ResponseEntity createRecoveryPassword(@RequestBody @Valid PasswordCreateDto passwordCreateDto,
                                                 HttpServletRequest request) {
        boolean result = ngUserService.createPasswordRecovery(passwordCreateDto, request);
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
}
