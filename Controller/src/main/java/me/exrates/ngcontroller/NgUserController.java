package me.exrates.ngcontroller;

import me.exrates.dao.exception.UserNotFoundException;
import me.exrates.model.User;
import me.exrates.model.UserEmailDto;
import me.exrates.model.dto.mobileApiDto.AuthTokenDto;
import me.exrates.model.dto.mobileApiDto.UserAuthenticationDto;
import me.exrates.model.enums.UserStatus;
import me.exrates.ngcontroller.service.UserVerificationService;
import me.exrates.security.exception.BannedIpException;
import me.exrates.security.exception.IncorrectPasswordException;
import me.exrates.security.exception.IncorrectPinException;
import me.exrates.security.ipsecurity.IpBlockingService;
import me.exrates.security.ipsecurity.IpTypesOfChecking;
import me.exrates.security.service.AuthTokenService;
import me.exrates.security.service.SecureService;
import me.exrates.service.NotificationService;
import me.exrates.service.ReferralService;
import me.exrates.service.UserService;
import me.exrates.service.notifications.G2faService;
import me.exrates.service.notifications.NotificationsSettingsService;
import me.exrates.service.util.IpUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;

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
    private final NotificationsSettingsService notificationsSettingsService;
    private final G2faService g2faService;
    private final UserVerificationService userVerificationService;

    @Value("${dev.mode}")
    private boolean DEV_MODE;

    @Autowired
    public NgUserController(IpBlockingService ipBlockingService, AuthTokenService authTokenService,
                            UserService userService, ReferralService referralService,
                            SecureService secureService,
                            NotificationsSettingsService notificationsSettingsService,
                            NotificationService notificationService,
                            G2faService g2faService,
                            UserVerificationService userVerificationService) {
        this.ipBlockingService = ipBlockingService;
        this.authTokenService = authTokenService;
        this.userService = userService;
        this.referralService = referralService;
        this.secureService = secureService;
        this.notificationsSettingsService = notificationsSettingsService;
        this.g2faService = g2faService;
        this.userVerificationService = userVerificationService;
    }

    @PostMapping(value = "/authenticate")
    public ResponseEntity<AuthTokenDto> authenticate(@RequestBody @Valid UserAuthenticationDto authenticationDto,
                                                     HttpServletRequest request) throws Exception {
        try {
            ipBlockingService.checkIp(authenticationDto.getClientIp(), IpTypesOfChecking.LOGIN);
        } catch (BannedIpException ban) {
            return new ResponseEntity<>(HttpStatus.DESTINATION_LOCKED); // 419
        }

        if (authenticationDto.getEmail().startsWith("promo@ex") ||
                authenticationDto.getEmail().startsWith("dev@exrat")) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);   // 403
        }

        User user;
        try {
            user = userService.findByEmail(authenticationDto.getEmail());
        } catch (UserNotFoundException esc) {
            logger.debug("User with email {} not found", authenticationDto.getEmail());
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        if (user.getStatus() == UserStatus.REGISTERED) {
            return new ResponseEntity<>(HttpStatus.UPGRADE_REQUIRED); // 426
        }
        if (user.getStatus() == UserStatus.DELETED) {
            return new ResponseEntity<>(HttpStatus.GONE); // 410
        }

        boolean shouldLoginWithGoogle = g2faService.isGoogleAuthenticatorEnable(user.getId());

        if (!DEV_MODE) {

            if (isEmpty(authenticationDto.getPin())) {

                if (!shouldLoginWithGoogle) {
                    secureService.sendLoginPincode(user, request);
                }
                return new ResponseEntity<>(HttpStatus.I_AM_A_TEAPOT); //418
            }
        }

        authenticationDto.setPinRequired(true);
        Optional<AuthTokenDto> authTokenResult;
        try {
            authTokenResult = authTokenService.retrieveTokenNg(request, authenticationDto,
                    authenticationDto.getClientIp(), shouldLoginWithGoogle);
        } catch (IncorrectPinException wrongPin) {
            return new ResponseEntity<>(HttpStatus.I_AM_A_TEAPOT); //418
        } catch (UsernameNotFoundException | IncorrectPasswordException e) {
            ipBlockingService.failureProcessing(authenticationDto.getClientIp(), IpTypesOfChecking.LOGIN);
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
        ipBlockingService.successfulProcessing(authenticationDto.getClientIp(), IpTypesOfChecking.LOGIN);
        return new ResponseEntity<>(authTokenDto, HttpStatus.OK); // 200
    }

    @PostMapping(value = "/register")
    public ResponseEntity register(@RequestBody @Valid UserEmailDto userEmailDto, HttpServletRequest request) {

        if (!processIpBlocking(request, "email", userEmailDto.getEmail(),
                () -> userService.ifEmailIsUnique(userEmailDto.getEmail()))) {
            throw new NgDashboardException("email is exist or banned");
        }
        userVerificationService.saveUser(userEmailDto, request);


        throw new UnsupportedOperationException("not yet");
    }

    private String getAvatarPathPrefix(HttpServletRequest request) {
        return request.getScheme() + "://" + request.getServerName() +
                ":" + request.getServerPort() + "/rest";
    }

    private Boolean processIpBlocking(HttpServletRequest request, String email, Supplier<Boolean> operation) {
        String clientIpAddress = IpUtils.getClientIpAddress(request);
        ipBlockingService.checkIp(clientIpAddress, IpTypesOfChecking.OPEN_API);
        Boolean result = operation.get();
        if (!result) {
            ipBlockingService.failureProcessing(clientIpAddress, IpTypesOfChecking.OPEN_API);
            logger.debug("Authentication pincode for user with email: %s is not needed!", email);
        } else {
            ipBlockingService.successfulProcessing(clientIpAddress, IpTypesOfChecking.OPEN_API);
            logger.debug("Authentication pincode for user with email: %s is needed!", email);
        }
        return result;
    }

    private Boolean processIpBlocking(HttpServletRequest request, String logMessageValue,
                                      String value, Supplier<Boolean> operation) {
        String clientIpAddress = IpUtils.getClientIpAddress(request);
        ipBlockingService.checkIp(clientIpAddress, IpTypesOfChecking.OPEN_API);
        Boolean result = operation.get();
        if (!result) {
            ipBlockingService.failureProcessing(clientIpAddress, IpTypesOfChecking.OPEN_API);
            logger.debug("New user's %s %s is already stored!", logMessageValue, value);
        } else {
            ipBlockingService.successfulProcessing(clientIpAddress, IpTypesOfChecking.OPEN_API);
            logger.debug("New user's %s %s is not stored yet!", logMessageValue, value);
        }
        return result;
    }
}
