package me.exrates.ngcontroller;

import me.exrates.model.User;
import me.exrates.model.dto.mobileApiDto.AuthTokenDto;
import me.exrates.model.dto.mobileApiDto.UserAuthenticationDto;
import me.exrates.model.enums.UserStatus;
import me.exrates.security.exception.IncorrectPasswordException;
import me.exrates.security.ipsecurity.IpBlockingService;
import me.exrates.security.ipsecurity.IpTypesOfChecking;
import me.exrates.security.service.AuthTokenService;
import me.exrates.service.ReferralService;
import me.exrates.service.UserService;
import me.exrates.service.util.IpUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.Locale;
import java.util.Optional;

@RestController
@RequestMapping("/info/public/users")
public class NgUserController {

    private final IpBlockingService ipBlockingService;
    private final AuthTokenService authTokenService;
    private final UserService userService;
    private final ReferralService referralService;

    @Autowired
    public NgUserController(IpBlockingService ipBlockingService, AuthTokenService authTokenService,
                            UserService userService, ReferralService referralService) {
        this.ipBlockingService = ipBlockingService;
        this.authTokenService = authTokenService;
        this.userService = userService;
        this.referralService = referralService;
    }

    @RequestMapping(value = "/authenticate", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<AuthTokenDto> authenticate(@RequestBody @Valid UserAuthenticationDto authenticationDto,
                                                     HttpServletRequest request) throws Exception {
        String ipAddress = IpUtils.getClientIpAddress(request);
        ipBlockingService.checkIp(ipAddress, IpTypesOfChecking.LOGIN);

        Optional<AuthTokenDto> authTokenResult;
        try {
            authTokenResult = authTokenService.retrieveToken(authenticationDto.getEmail(), authenticationDto.getPassword());
        } catch (UsernameNotFoundException | IncorrectPasswordException e) {
            ipBlockingService.failureProcessing(ipAddress, IpTypesOfChecking.LOGIN);
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED); // 401
        }
        AuthTokenDto authTokenDto =
                authTokenResult.orElseThrow(() -> new Exception("Failed to authenticate user with email: " + authenticationDto.getEmail()));

        User user = userService.findByEmail(authenticationDto.getEmail());

        if (user.getStatus() == UserStatus.REGISTERED) {
            return new ResponseEntity<>(HttpStatus.UPGRADE_REQUIRED); // 426
        }
        if (user.getStatus() == UserStatus.DELETED) {
            return new ResponseEntity<>(HttpStatus.GONE); // 410
        }
        authTokenDto.setNickname(user.getNickname());
        authTokenDto.setUserId(user.getId());
        authTokenDto.setLocale(new Locale(userService.getPreferedLang(user.getId())));
        String avatarLogicalPath = userService.getAvatarPath(user.getId());
        String avatarFullPath = avatarLogicalPath == null || avatarLogicalPath.isEmpty() ? null : getAvatarPathPrefix(request) + avatarLogicalPath;
        authTokenDto.setAvatarPath(avatarFullPath);
        authTokenDto.setFinPasswordSet(user.getFinpassword() != null);
        authTokenDto.setReferralReference(referralService.generateReferral(user.getEmail()));
        ipBlockingService.successfulProcessing(ipAddress, IpTypesOfChecking.LOGIN);
        return new ResponseEntity<>(authTokenDto, HttpStatus.OK); // 200
    }

    @RequestMapping(value = "/register", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public void register() {
        throw new UnsupportedOperationException("not yet");
    }

    private String getAvatarPathPrefix(HttpServletRequest request) {
        return request.getScheme() + "://" + request.getServerName() +
                ":" + request.getServerPort() + "/rest";
    }
}
