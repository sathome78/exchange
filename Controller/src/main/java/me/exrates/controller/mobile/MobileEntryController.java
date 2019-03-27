package me.exrates.controller.mobile;

import me.exrates.controller.exception.*;
import me.exrates.model.User;
import me.exrates.model.dto.UpdateUserDto;
import me.exrates.model.dto.mobileApiDto.AuthTokenDto;
import me.exrates.model.dto.mobileApiDto.UserAuthenticationDto;
import me.exrates.model.enums.UserAgent;
import me.exrates.model.enums.UserStatus;
import me.exrates.security.exception.BannedIpException;
import me.exrates.security.exception.IncorrectPasswordException;
import me.exrates.security.exception.MissingCredentialException;
import me.exrates.security.exception.UserNotEnabledException;
import me.exrates.security.ipsecurity.IpTypesOfChecking;
import me.exrates.security.service.AuthTokenService;
import me.exrates.security.ipsecurity.IpBlockingService;
import me.exrates.service.*;
import me.exrates.service.ApiService;
import me.exrates.service.ReferralService;
import me.exrates.service.UserFilesService;
import me.exrates.service.UserService;
import me.exrates.service.exception.*;
import me.exrates.service.exception.api.*;
import me.exrates.service.session.UserSessionService;
import me.exrates.service.util.IpUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.io.IOException;
import java.util.*;

import static me.exrates.service.exception.api.ErrorCode.*;
import static me.exrates.service.util.RestApiUtils.decodePassword;
import static me.exrates.service.util.RestApiUtils.retrieveParamFormBody;

/**
 * Created by OLEG on 19.08.2016.
 */

/**
 * ALL controleers oommented for security reasons
 * */
@RestController
@PropertySource(value = {"classpath:about_us.properties", "classpath:/mobile.properties"})
public class MobileEntryController {
    private static final Logger logger = LogManager.getLogger("mobileAPI");

    private static final String PASSWORD_REGEX = "((?=.*\\d)(?=.*[a-zA-Z]).{8,20})";
    private static final String EMAIL_REGEX = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
            + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
    private static final String NICKNAME_REGEX = "^\\D+[\\w\\d\\-_]+";

    @Value("${contacts.telephone}")
    String telephone;
    private
    @Value("${contacts.email}")
    String email;
    @Value("${contacts.feedbackEmail}")
    String feedbackEmail;

    @Value("${pass.encode.key}")
    String passEncodeKey;

    @Autowired
    private UserSessionService userSessionService;

    @Autowired
    private AuthTokenService authTokenService;

    @Autowired
    private UserService userService;

    @Autowired
    private ReferralService referralService;
    @Autowired
    private UserFilesService userFilesService;
    @Autowired
    private ApiService apiService;

    /*TODO temporary disable

    @Autowired
    private StoreSessionListener storeSessionListener;*/

    @Autowired
    private IpBlockingService ipBlockingService;


    /**
     * @apiDefine MissingAuthenticationTokenError
     * @apiError (403) {String} errorCode error code
     * @apiError (403) {String} url request URL
     * @apiError (403) {String} cause name of root exception
     * @apiError (403) {String} details detail of root exception
     * @apiErrorExample {json} Missing Authentication Token:
     * HTTP/1.1 403 Forbidden
     *      {
     *          "errorCode": "MISSING_AUTHENTICATION_TOKEN",
     *          "url": "http://127.0.0.1:8080/api/dashboard/currencyPairs",
     *          "cause": "TokenException",
     *          "detail": "No authentication token header found"
     *      }
     *
     * */

    /**
     * @apiDefine InvalidAuthenticationTokenError
     * @apiError (403) {String} errorCode error code
     * @apiError (403) {String} url request URL
     * @apiError (403) {String} cause name of root exception
     * @apiError (403) {String} details detail of root exception
     * @apiErrorExample {json} Invalid Authentication Token:
     * HTTP/1.1 403 Forbidden
     *      {
     *          "errorCode": "INVALID_AUTHENTICATION_TOKEN",
     *          "url": "http://127.0.0.1:8080/api/dashboard/currencyPairs",
     *          "cause": "TokenException",
     *          "detail": "Token corrupted"
     *      }
     *
     * */

    /**
     * @apiDefine ExpiredAuthenticationTokenError
     * @apiError (403) {String} errorCode error code
     * @apiError (403) {String} url request URL
     * @apiError (403) {String} cause name of root exception
     * @apiError (403) {String} details detail of root exception
     * @apiErrorExample {json} Expired Authentication Token:
     * HTTP/1.1 403 Forbidden
     *      {
     *          "errorCode": "EXPIRED_AUTHENTICATION_TOKEN",
     *          "url": "http://127.0.0.1:8080/api/dashboard/currencyPairs",
     *          "cause": "TokenException",
     *          "detail": "Token expired"
     *      }
     *
     * */

    /**
     * @apiDefine AuthenticationError
     * @apiError (403) {String} errorCode error code
     * @apiError (403) {String} url request URL
     * @apiError (403) {String} cause name of root exception
     * @apiError (403) {String} details detail of root exception
     * @apiErrorExample {json} Error Response - Forbidden:
     * HTTP/1.1 403 Forbidden
     *      {
     *          "errorCode": "FAILED_AUTHENTICATION",
     *          "url": "http://127.0.0.1:8080/api/dashboard/currencyPairs",
     *          "cause": "TokenException",
     *          "detail": "..."
     *      }
     *
     * */

    /**
     * @apiDefine MessageNotReadableError
     * @apiError (400) {String} url request URL
     * @apiError (400) {String} cause name of root exception
     * @apiError (400) {String} details detail of root exception
     * @apiError (400) {String} errorCode error code
     * @apiErrorExample {json} Request Not Readable:
     * HTTP/1.1 400 Bad Request
     *      {
     *          "errorCode": "REQUEST_NOT_READABLE"
     *          "url": "http://127.0.0.1:8080/rest/user/register",
     *          "cause": "HttpMessageNotReadableException",
     *          "detail": "Unexpected end-of-input within/between OBJECT entries\n at [Source: java.io.PushbackInputStream@2113d722; line: 6, column: 167]"
     *      }
     * */

    /**
     * @apiDefine InvalidParamError
     * @apiError (400) {String} errorCode error code
     * @apiError (400) {String} url request URL
     * @apiError (400) {String} cause name of root exception
     * @apiError (400) {String} details detail of root exception
     * @apiErrorExample {json} Invalid Param Value:
     * HTTP/1.1 400 Bad Request
     *      {
     *          "errorCode": "INVALID_PARAM_VALUE",
     *          "url": "http://127.0.0.1:8080/rest/user/register",
     *          "cause": "MethodArgumentNotValidException",
     *          "detail": "{nickname=Nickname is missing, email=Email is missing}"
     *      }
     * */

    /**
     * @apiDefine DisabledAccountError
     * @apiError (403) {String} errorCode error code
     * @apiError (403) {String} url request URL
     * @apiError (403) {String} cause name of root exception
     * @apiError (403) {String} details detail of root exception
     * @apiErrorExample {json} Disabled Account:
     * HTTP/1.1 403 Forbidden
     *      {
     *          "errorCode": "ACCOUNT_DISABLED",
     *          "url": "http://127.0.0.1:8080/rest/user/authenticate",
     *          "cause": "UserNotEnabledException",
     *          "detail": "Blocked account"
     *      }
     * */

    /**
     * @apiDefine UnconfirmedAccountError
     * @apiError (403) {String} errorCode error code
     * @apiError (403) {String} url request URL
     * @apiError (403) {String} cause name of root exception
     * @apiError (403) {String} details detail of root exception
     * @apiErrorExample {json} Disabled Account:
     * HTTP/1.1 403 Forbidden
     *      {
     *          "errorCode": "ACCOUNT_NOT_CONFIRMED",
     *          "url": "http://127.0.0.1:8080/rest/user/authenticate",
     *          "cause": "UnconfirmedUserException",
     *          "detail": "User account not yet confirmed"
     *      }
     * */


    /**
     * @apiDefine IncorrectPasswordError
     * @apiError (406) {String} errorCode error code
     * @apiError (406) {String} url request URL
     * @apiError (406) {String} cause name of root exception
     * @apiError (406) {String} details detail of root exception
     * @apiErrorExample {json} Incorrect password:
     * HTTP/1.1 406 Not Acceptable
     *      {
     *          "errorCode": "INCORRECT_PASSWORD",
     *          "url": "http://127.0.0.1:8080/rest/user/authenticate",
     *          "cause": "IncorrectPasswordException",
     *          "detail": "Incorrect password"
     *      }
     *
     * */

    /**
     * @apiDefine NotExistingEmailError
     * @apiError (406) {String} errorCode error code
     * @apiError (406) {String} url request URL
     * @apiError (406) {String} cause name of root exception
     * @apiError (406) {String} details detail of root exception
     * @apiErrorExample {json} Email does not exist
     * HTTP/1.1 406 Not Acceptable
     *      {
     *          "errorCode": "EMAIL_NOT_EXISTS",
     *          "url": "http://127.0.0.1:8080/rest/user/authenticate",
     *          "cause": "UsernameNotFoundException",
     *          "detail": "Несуществующий логин"
     *      }
     *
     * */

    /**
     * @apiDefine MissingCredentialError
     * @apiError (406) {String} errorCode error code
     * @apiError (406) {String} url request URL
     * @apiError (406) {String} cause name of root exception
     * @apiError (406) {String} details detail of root exception
     * @apiErrorExample {json} Missing credential
     * HTTP/1.1 406 Not Acceptable
     *      {
     *          "errorCode": "MISSING_CREDENTIALS",
     *          "url": "http://127.0.0.1:8080/rest/user/authenticate",
     *          "cause": "MissingCredentialException",
     *          "detail": "Credentials missing"
     *      }
     *
     * */

    /**
     * @apiDefine InvalidFileError
     * @apiError (406) {String} errorCode error code
     * @apiError (406) {String} url request URL
     * @apiError (406) {String} cause name of root exception
     * @apiError (406) {String} details detail of root exception
     * @apiErrorExample {json} Invalid file:
     * HTTP/1.1 406 Not Acceptable
     *      {
     *          "errorCode": "INVALID_FILE",
     *          "url": "http://127.0.0.1:8080/rest/user/register",
     *          "cause": "InvalidFileException",
     *          "detail": "Invalid file"
     *      }
     *
     * */

    /**
     * @apiDefine IncorrectFinPasswordError
     * @apiError (406) {String} errorCode error code
     * @apiError (406) {String} url request URL
     * @apiError (406) {String} cause name of root exception
     * @apiError (406) {String} details detail of root exception
     * @apiErrorExample {json} Incorrect Financial Password:
     * HTTP/1.1 406 Not Acceptable
     *      {
     *          "errorCode": "INCORRECT_FIN_PASSWORD",
     *          "url": "http://127.0.0.1:8080/api/user/checkFinPass",
     *          "cause": "WrongFinPasswordException",
     *          "detail": "Entered the wrong financial password"
     *      }
     *
     * */

    /**
     * @apiDefine AbsentFinPasswordError
     * @apiError (406) {String} errorCode error code
     * @apiError (406) {String} url request URL
     * @apiError (406) {String} cause name of root exception
     * @apiError (406) {String} details detail of root exception
     * @apiErrorExample {json} Absent Financial Password:
     * HTTP/1.1 406 Not Acceptable
     *      {
     *          "errorCode": "ABSENT_FIN_PASSWORD",
     *          "url": "http://127.0.0.1:8080/api/user/checkFinPass",
     *          "cause": "AbsentFinPasswordException",
     *          "detail": "The financial password is not defined. You must set the financial password to make financial operations"
     *      }
     *
     * */

    /**
     * @apiDefine InvalidAppKeyError
     * @apiError (406) {String} errorCode error code
     * @apiError (406) {String} url request URL
     * @apiError (406) {String} cause name of root exception
     * @apiError (406) {String} details detail of root exception
     * @apiErrorExample {json} Invalid App Key:
     * HTTP/1.1 406 Not Acceptable
     *      {
     *          "errorCode": "INVALID_APP_KEY",
     *          "url": "http://127.0.0.1:8080/rest/user/authenticate",
     *          "cause": "InvalidAppKeyException",
     *          "detail": "Invalid app key"
     *      }
     *
     * */

    /**
     * @apiDefine MissingRequiredParamError
     * @apiError (406) {String} errorCode error code
     * @apiError (406) {String} url request URL
     * @apiError (406) {String} cause name of root exception
     * @apiError (406) {String} details detail of root exception
     * @apiErrorExample {json} Missing required param:
     * HTTP/1.1 406 Not Acceptable
     *      {
     *          "errorCode": "MISSING_REQUIRED_PARAM",
     *          "url": "http://test.exrates.me/api/user/changePass",
     *          "cause": "MissingBodyParamException",
     *          "detail": "Param password missing"
     *      }
     *
     * */

    /**
     * @apiDefine LanguageNotSupportedError
     * @apiError (406) {String} errorCode error code
     * @apiError (406) {String} url request URL
     * @apiError (406) {String} cause name of root exception
     * @apiError (406) {String} details detail of root exception
     * @apiErrorExample {json} Missing required param:
     * HTTP/1.1 406 Not Acceptable
     *      {
     *          "errorCode": "LANGUAGE_NOT_SUPPORTED",
     *          "url": "http://127.0.0.1:8080/api/user/setLanguage",
     *          "cause": "NotSupportedLanguageException",
     *          "detail": "Language ayyr not supported"
     *      }
     *
     * */

    /**
     * @apiDefine UnconfirmedFinPassError
     * @apiError (406) {String} errorCode error code
     * @apiError (406) {String} url request URL
     * @apiError (406) {String} cause name of root exception
     * @apiError (406) {String} details detail of root exception
     * @apiErrorExample {json} Nickname Already Exists:
     * HTTP/1.1 406 Not Acceptable
     *      {
     *          "errorCode": "UNCONFIRMED_FIN_PASSWORD",
     *          "url": "http://127.0.0.1:8080/api/user/checkFinPass",
     *          "cause": "NotConfirmedFinPasswordException",
     *          "detail": "You must to confirm financial password change. check your e-mail and follow the instructions in received message. Or follow the procedure for changing the password again."
     *      }
     *
     * */

    /**
     * @apiDefine ExistingEmailError
     * @apiError (406) {String} errorCode error code
     * @apiError (406) {String} url request URL
     * @apiError (406) {String} cause name of root exception
     * @apiError (406) {String} details detail of root exception
     * @apiErrorExample {json} Email already exists:
     * HTTP/1.1 406 Not Acceptable
     *      {
     *          "errorCode": "EXISTING_EMAIL",
     *          "url": "http://127.0.0.1:8080/rest/user/register",
     *          "cause": "UniqueEmailConstraintException",
     *          "detail": "Email already exists!"
     *      }
     *
     * */

    /**
     * @apiDefine ExistingNicknameError
     * @apiError (406) {String} errorCode error code
     * @apiError (406) {String} url request URL
     * @apiError (406) {String} cause name of root exception
     * @apiError (406) {String} details detail of root exception
     * @apiErrorExample {json} Nickname Already Exists:
     * HTTP/1.1 406 Not Acceptable
     *      {
     *          "errorCode": "EXISTING_NICKNAME",
     *          "url": "http://127.0.0.1:8080/rest/user/register",
     *          "cause": "UniqueNicknameConstraintException",
     *          "detail": "Nickname already exists!"
     *      }
     *
     * */

    /**
     * @apiDefine InvalidSessionIdError
     * @apiError (406) {String} errorCode error code
     * @apiError (406) {String} url request URL
     * @apiError (406) {String} cause name of root exception
     * @apiError (406) {String} details detail of root exception
     * @apiErrorExample {json} Invalid Session Id:
     * HTTP/1.1 406 Not Acceptable
     *      {
     *          "errorCode": "INVALID_SESSION_ID",
     *          "url": "http://127.0.0.1:8080/api/user/authenticateQR",
     *          "cause": "InvalidSessionIdException",
     *          "detail": null
     *      }
     *
     * */


    /**
     * @apiDefine InternalServerError
     * @apiError (500) {String} errorCode error code
     * @apiError (500) {String} url request URL
     * @apiError (500) {String} cause name of root exception
     * @apiError (500) {String} detail detail of root exception
     * @apiErrorExample {json} Internal Server Error
     * HTTP/1.1 500 InternalServerError
     *      {
     *          "url": "http://127.0.0.1:8080/rest/user/register",
     *          "cause": "UncategorizedSQLException",
     *          "detail": "Illegal mix of collations (latin1_swedish_ci,IMPLICIT) and (utf8_general_ci,COERCIBLE) for operation '='",
     *          "errorCode": "INTERNAL_SERVER_ERROR"
     *      }
     * */


    /**
     * @api {post} /rest/user/register Register user
     * @apiName registerUser
     * @apiGroup User
     * @apiParam {String} nickname User nickname
     * @apiParam {String} email User email
     * @apiParam {String} password User password
     * @apiParam {String} sponsor Referral of the registered user (OPTIONAL)
     * @apiParam {String} language Preferred language
     * @apiParam {File} avatar User avatar
     * @apiParamExample {multipart/form-data} Request Example:
     * POST /rest/user/register HTTP/1.1
     * Content-Type: multipart/form-data; boundary=----WebKitFormBoundary7MA4YWxkTrZu0gW
     * <p>
     * ------WebKitFormBoundary7MA4YWxkTrZu0gW
     * Content-Disposition: form-data; name="nickname"
     * <p>
     * user
     * ------WebKitFormBoundary7MA4YWxkTrZu0gW
     * Content-Disposition: form-data; name="email"
     * <p>
     * user@user.com
     * ------WebKitFormBoundary7MA4YWxkTrZu0gW
     * Content-Disposition: form-data; name="password"
     * <p>
     * AgAGARJFUUQ=
     * ------WebKitFormBoundary7MA4YWxkTrZu0gW
     * Content-Disposition: form-data; name="language"
     * <p>
     * en
     * ------WebKitFormBoundary7MA4YWxkTrZu0gW
     * Content-Disposition: form-data; name="avatar"; filename=""
     * Content-Type:
     * ------WebKitFormBoundary7MA4YWxkTrZu0gW--
     * @apiPermission anonymous
     * @apiDescription Registers user
     * @apiSuccess (201) {Integer} id User identification number (by which one is saved in database)
     * @apiSuccessExample {json} Success-Response:
     * HTTP/1.1 201 Created
     * 706
     * @apiUse MessageNotReadableError
     * @apiUse InvalidParamError
     * @apiUse ExistingEmailError
     * @apiUse ExistingNicknameError
     * @apiUse InternalServerError
     * @apiUse InvalidFileError
     */

    /*@RequestMapping(value = "/rest/user/register", produces = {"application/json;charset=utf-8"})
    public ResponseEntity<Integer> registerUser(@RequestParam String nickname,
                                                @RequestParam String email,
                                                @RequestParam String password,
                                                @RequestParam(required = false) String sponsor,
                                                @RequestParam String language,
                                                @RequestParam(required = false) MultipartFile avatar,
                                                HttpServletRequest request) throws IOException {

        if (avatar != null) {
            logger.debug(avatar.getSize());
            logger.debug(avatar.getContentType());
        }

        User user = new User();
        if (!nickname.matches(NICKNAME_REGEX)) {
            throw new InvalidNicknameException("Invalid nickname");
        }
        user.setNickname(nickname);
        if (!email.matches(EMAIL_REGEX)) {
            throw new InvalidEmailException("Invalid email");
        }
        user.setEmail(email);
        String decodedPassword = decodePassword(password, passEncodeKey);
        if (!decodedPassword.matches(PASSWORD_REGEX)) {
            throw new InvalidPasswordException("Password must be between 8 and 20 symbols, contain letters and numbers");
        }
        if (avatar != null && !userFilesService.checkFileValidity(avatar)) {
            throw new InvalidFileException("Invalid file");
        }


        user.setPassword(decodedPassword);
        user.setParentEmail(sponsor);
        user.setPhone("");
        user.setIp(request.getRemoteHost());
        Locale locale = new Locale(language);
        logger.debug(user);
        logger.debug(locale);

        try {
            if (userService.createUserRest(user, locale)) {
                logger.info("User registered with parameters = " + user.toString());
                final int userId = userService.getIdByEmail(user.getEmail());
                final int parentId;
                String parentEmail = user.getParentEmail();

                if (StringUtils.isEmpty(parentEmail) || parentEmail.equals(user.getEmail())) {
                    parentId = getCommonReferralRootId();
                } else {
                    int idByParentEmail = userService.getIdByEmail(user.getParentEmail());
                    parentId = idByParentEmail == 0 ? getCommonReferralRootId() : idByParentEmail;
                }
                if (userId > 0 && parentId > 0) {
                    referralService.bindChildAndParent(userId, parentId);
                }
                if (avatar != null) {
                    userFilesService.createUserAvatar(userId, avatar);
                }
                return new ResponseEntity<>(userId, HttpStatus.CREATED);

            } else {
                throw new NotCreateUserException("Error while user creation");
            }
        } catch (Exception e) {
            logger.error("User can't be registered with parameters = " + user.toString() + "  " + e.getMessage());
            throw e;
        }

    }

    private int getCommonReferralRootId() {
        User commonReferralRoot = userService.getCommonReferralRoot();
        return commonReferralRoot == null ? 0 : commonReferralRoot.getId();
    }*/


    /**
     * @api {post} /rest/user/authenticate Retrieve authentication token
     * @apiName authenticate
     * @apiGroup User
     * @apiParam {String} email User email
     * @apiParam {String} password User password
     * @apiParam {String} appKey App version key
     * @apiParamExample {json} Request Example:
     * {
     * "email": "user111@user.com",
     * "password": "user1234",
     * "appKey": "A1.0.0"
     * }
     * @apiPermission anonymous
     * @apiDescription Supplies authentication token and basic user info
     * @apiSuccess (200) {String} token Authentication token
     * @apiSuccess (200) {Long} expires Token expiration date
     * @apiSuccess (200) {String} nickname User nickname
     * @apiSuccess (200) {Integer} id User id
     * @apiSuccess (200) {String} avatarPath url of user avatar
     * @apiSuccess (200) {String} language preferred language
     * @apiSuccess (200) {Boolean} finPasswordSet false if fin pass is null, true otherwise
     * @apiSuccessExample {json} Success-Response:
     * HTTP/1.1 200 OK
     * {
     * "token": "eyJhbGciOiJIUzUxMiJ9.eyJjbGllbnRUeXBlIjoidXNlciIsInRva2VuX2V4cGlyYXRpb25fZGF0ZSI6ODY0MTQ3NjI3MTg0NDY1NCwidXNlcm5hbWUiOiJzZW50aW5lbDc3N0BiaWdtaXIubmV0IiwidG9rZW5fY3JlYXRlX2RhdGUiOjE0NzYyNzE4NDQ2NTR9.dwSDdUF8gOzI0AjDLT1h2KILqIZpvuTrK4Cnrl6lqZGX28QIPkHp23TGSbDzi2gWBk_c81HLN2bhsuSh_71vGw",
     * "expires": 8641476271844654,
     * "nickname": "talalai123",
     * "avatarPath": "http://test.exrates.me:80/rest/userFiles/494/avatar/38bd7383-688d-4d22-a378-13af4a7c5303.jpeg",
     * "finPasswordSet": true,
     * "id": 494,
     * "language": "ar"
     * }
     * @apiUse MessageNotReadableError
     * @apiUse MissingCredentialError
     * @apiUse DisabledAccountError
     * @apiUse IncorrectPasswordError
     * @apiUse NotExistingEmailError
     * @apiUse InvalidAppKeyError
     * @apiUse InternalServerError
     */
    /*@RequestMapping(value = "/rest/user/authenticate", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<AuthTokenDto> authenticate(@RequestBody @Valid UserAuthenticationDto authenticationDto,
                                                     HttpServletRequest request) {
        String ipAddress = IpUtils.getClientIpAddress(request);
        ipBlockingService.checkIp(ipAddress, IpTypesOfChecking.LOGIN);

        Optional<AuthTokenDto> authTokenResult = null;
        try {
            authTokenResult = authTokenService.retrieveToken(authenticationDto.getEmail(), authenticationDto.getPassword());
        } catch (UsernameNotFoundException | IncorrectPasswordException e) {
            ipBlockingService.failureProcessing(ipAddress, IpTypesOfChecking.LOGIN);
            throw new WrongUsernameOrPasswordException("Wrong credentials");
        }
        AuthTokenDto authTokenDto = authTokenResult.get();
        String appKey = authenticationDto.getAppKey();
        String userAgentHeader = request.getHeader("User-Agent");
        logger.debug(userAgentHeader);
        if (apiService.appKeyCheckEnabled()) {
            checkAppKey(appKey, userAgentHeader);
        }


        User user = userService.findByEmail(authenticationDto.getEmail());

        if (user.getUserStatus() == UserStatus.REGISTERED) {
            throw new UnconfirmedUserException("User account not yet confirmed");
        }
        if (user.getUserStatus() == UserStatus.DELETED) {
            throw new UserNotEnabledException("Blocked account");
        }
        authTokenDto.setNickname(user.getNickname());
        authTokenDto.setUserId(user.getId());
        authTokenDto.setLocale(new Locale(userService.getPreferedLang(user.getId())));
        String avatarLogicalPath = userService.getAvatarPath(user.getId());
        String avatarFullPath = avatarLogicalPath == null || avatarLogicalPath.isEmpty() ? null : getAvatarPathPrefix(request) + avatarLogicalPath;
        authTokenDto.setAvatarPath(avatarFullPath);
        authTokenDto.setFinPasswordSet(user.getFinpassword() != null);
        authTokenDto.setReferralReference(referralService.generateReferral(user.getEmail()));
        ipBlockingService.successfulProcessing(ipAddress,IpTypesOfChecking.LOGIN);
        return new ResponseEntity<>(authTokenDto, HttpStatus.OK);
    }*/

    /*private void checkAppKey(String appKey, String userAgentHeader) {
        UserAgent userAgent = UserAgent.DESKTOP;
        String headerLowerCase = userAgentHeader.toLowerCase();
        if (headerLowerCase.contains("android")) {
            userAgent = UserAgent.ANDROID;
        } else if (headerLowerCase.contains("iphone") || headerLowerCase.contains("ipod") || headerLowerCase.contains("ipad")) {
            userAgent = UserAgent.IOS;
        }
        if (userAgent == UserAgent.ANDROID || userAgent == UserAgent.IOS) {
            String actualKey = apiService.retrieveApplicationKey(userAgent);
            if (!appKey.equals(actualKey)) {
                throw new InvalidAppKeyException("Invalid app key");
            }
        }
    }*/


    /**
     * @api {post} /rest/user/restorePassword Submit password restoration
     * @apiName restorePassword
     * @apiGroup User
     * @apiParam {String} email User email
     * @apiParam {String} password User password
     * @apiParamExample {json} Request Example:
     * {
     * "email": "user111@user.com",
     * "password": "UFxdUxpLVkQVDA=="
     * }
     * @apiPermission anonymous
     * @apiDescription Accepts email and new password to send confirmation link.
     * In case of success returns empty response with HTTP 200
     * @apiSuccessExample {json} Success-Response:
     * HTTP/1.1 200 OK
     * @apiUse MessageNotReadableError
     * @apiUse MissingCredentialError
     * @apiUse DisabledAccountError
     * @apiUse NotExistingEmailError
     * @apiUse InternalServerError
     */
    /*@RequestMapping(value = "/rest/user/restorePassword", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<Void> restorePassword(@RequestBody Map<String, String> body, HttpServletRequest request) {
        String ipAddress = IpUtils.getClientIpAddress(request);
        ipBlockingService.checkIp(ipAddress, IpTypesOfChecking.LOGIN);
        if (!(body.containsKey("email") && body.containsKey("password"))) {
            throw new MissingCredentialException("Credentials missing");
        }
        String email = body.get("email");
        String newPass = decodePassword(body.get("password"), passEncodeKey);
        try {
            User user = userService.findByEmail(email);
            if (user.getUserStatus() == UserStatus.DELETED) {
                throw new UserNotEnabledException("Account disabled");
            }
            UpdateUserDto updateUserDto = new UpdateUserDto(user.getId());
            updateUserDto.setEmail(email);
            userService.saveTemporaryPasswordAndNotify(updateUserDto, newPass, new Locale(userService.getPreferedLang(user.getId())));
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (EmptyResultDataAccessException e) {
            logger.warn("Could not find user with email " + email);
            ipBlockingService.failureProcessing(ipAddress, IpTypesOfChecking.LOGIN);
            throw new UsernameNotFoundException("Email not found");
        }

    }

    @RequestMapping(value = "/rest/user/resetPasswordConfirm", method = RequestMethod.GET)
    public ResponseEntity<Boolean> resetPasswordConfirm(@RequestParam String token, @RequestParam Long tempId) {
        logger.debug(token);
        logger.debug(tempId);
        return new ResponseEntity<>(userService.replaceUserPassAndDelete(token, tempId), HttpStatus.OK);

    }*/


    /**
     * @api {post} /api/user/changePass Change password by user
     * @apiName changePass
     * @apiUse TokenHeader
     * @apiGroup User
     * @apiParam {String} password User password
     * @apiParamExample {json} Request Example:
     * {
     * "password": "XF5aWQwDBQc="
     * }
     * @apiPermission User
     * @apiDescription Change password by authenticated user.
     * In case of success returns empty response with HTTP 200
     * @apiSuccessExample {json} Success-Response:
     * HTTP/1.1 200 OK
     * @apiUse ExpiredAuthenticationTokenError
     * @apiUse MissingAuthenticationTokenError
     * @apiUse InvalidAuthenticationTokenError
     * @apiUse MessageNotReadableError
     * @apiUse MissingRequiredParamError
     * @apiUse InvalidParamError
     * @apiUse InternalServerError
     */
    /*@RequestMapping(value = "/api/user/changePass", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<Void> changePass(@RequestBody Map<String, String> body) {

        logger.debug(retrieveParamFormBody(body, "password", true));
        changeUserPasses(retrieveParamFormBody(body, "password", true), null);
        return new ResponseEntity<>(HttpStatus.OK);
    }*/


    /**
     * @api {post} /api/user/changeFinPass Change password by user
     * @apiName changeFinPass
     * @apiUse TokenHeader
     * @apiGroup User
     * @apiParam {String} finPass financial password
     * @apiParamExample {json} Request Example:
     * {
     * "finPass": "XF5aWQwDBQc="
     * }
     * @apiPermission User
     * @apiDescription Set or change financial password by authenticated user.
     * In case of success returns empty response with HTTP 200
     * @apiSuccessExample {json} Success-Response:
     * HTTP/1.1 200 OK
     * @apiUse ExpiredAuthenticationTokenError
     * @apiUse MissingAuthenticationTokenError
     * @apiUse InvalidAuthenticationTokenError
     * @apiUse MessageNotReadableError
     * @apiUse MissingRequiredParamError
     * @apiUse InvalidParamError
     * @apiUse InternalServerError
     */
   /* @RequestMapping(value = "/api/user/changeFinPass", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<Void> changeFinPass(@RequestBody Map<String, String> body) {
        logger.debug(retrieveParamFormBody(body, "finPass", true));
        changeUserPasses(null, retrieveParamFormBody(body, "finPass", true));
        return new ResponseEntity<>(HttpStatus.OK);
    }

    private void changeUserPasses(String password, String finPass) {
        String decodedPassword = password == null ? null : decodePassword(password, passEncodeKey);
        String decodedFinPass = finPass == null ? null : decodePassword(finPass, passEncodeKey);
        if ((decodedPassword != null && !decodedPassword.matches(PASSWORD_REGEX)) ||
                (decodedFinPass != null && !decodedFinPass.matches(PASSWORD_REGEX))) {
            throw new InvalidPasswordException("Password must be between 8 and 20 symbols, contain letters and numbers");
        }

        String userEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userService.findByEmail(userEmail);
        UpdateUserDto updateUserDto = new UpdateUserDto(user.getId());
        updateUserDto.setPassword(decodedPassword);
        updateUserDto.setFinpassword(decodedFinPass);
        updateUserDto.setEmail(user.getEmail()); //need for send the email
        userService.updateUserSettings(updateUserDto);

        userSessionService.invalidateUserSessionExceptSpecific(userEmail, RequestContextHolder.currentRequestAttributes().getSessionId());
    }*/


    /**
     * @api {post} /api/user/checkFinPass Check financial password
     * @apiName checkFinPass
     * @apiUse TokenHeader
     * @apiGroup User
     * @apiParam {String} finPass financial password
     * @apiParamExample {json} Request Example:
     * {
     * "finPass": "XF5aWQwDBQc="
     * }
     * @apiPermission User
     * @apiDescription Set or change financial password by authenticated user.
     * In case of success returns empty response with HTTP 200
     * @apiSuccessExample {json} Success-Response:
     * HTTP/1.1 200 OK
     * @apiUse ExpiredAuthenticationTokenError
     * @apiUse MissingAuthenticationTokenError
     * @apiUse InvalidAuthenticationTokenError
     * @apiUse MessageNotReadableError
     * @apiUse MissingRequiredParamError
     * @apiUse IncorrectFinPasswordError
     * @apiUse UnconfirmedFinPassError
     * @apiUse AbsentFinPasswordError
     * @apiUse InternalServerError
     */
   /* @RequestMapping(value = "/api/user/checkFinPass", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<Void> checkFinPass(@RequestBody Map<String, String> body) {
        logger.debug(retrieveParamFormBody(body, "finPass", true));
        String decodedFinPass = decodePassword(retrieveParamFormBody(body, "finPass", true), passEncodeKey);
        String userEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userService.findByEmail(userEmail);
        userService.checkFinPassword(decodedFinPass, user, new Locale(userService.getPreferedLang(user.getId())));
        return new ResponseEntity<>(HttpStatus.OK);
    }*/


    /**
     * @api {post} /api/user/setAvatar Set user avatar
     * @apiName setUserAvatar
     * @apiUse TokenHeader
     * @apiGroup User
     * @apiParam {MultipartFile} avatar Image for avatar (jpg or png < 5 MB)
     * @apiParamExample {json} Request Example:
     * {
     * POST /api/user/setAvatar HTTP/1.1
     * Exrates-Rest-Token: eyJhbGciOiJIUzUxMiJ9.eyJjbGllbnRUeXBlIjoidXNlciIsInRva2VuX2V4cGlyYXRpb25fZGF0ZSI6ODY0MTQ3NTY2OTk2ODM0NiwidXNlcm5hbWUiOiJzZW50aW5lbDc3N0BiaWdtaXIubmV0IiwidG9rZW5fY3JlYXRlX2RhdGUiOjE0NzU2Njk5NjgzNDZ9.L-g2ZZ7WJjl3cprwSoc7fyosyP2NGWxdag2JaO3MGbM6Nukn5_EigoRx9c8EZBi0OAj1oF36VHIG4bwFYe7UrA
     * Content-Type: multipart/form-data; boundary=----WebKitFormBoundary7MA4YWxkTrZu0gW
     * <p>
     * ------WebKitFormBoundary7MA4YWxkTrZu0gW
     * Content-Disposition: form-data; name="file"; filename=""
     * Content-Type:
     * ------WebKitFormBoundary7MA4YWxkTrZu0gW--
     * }
     * @apiPermission User
     * @apiDescription Set avatar by authenticated user.
     * Returns URL of image
     * @apiSuccess (200) {String} url Avatar url
     * @apiSuccessExample {json} Success-Response:
     * HTTP/1.1 200 OK
     * <p>
     * {
     * "url": "http://127.0.0.1:8080/rest/userFiles/494/avatar/f58df357-bf29-4043-bafb-0fbc470bbe7e.jpeg"
     * }
     * @apiUse MessageNotReadableError
     * @apiUse ExpiredAuthenticationTokenError
     * @apiUse MissingAuthenticationTokenError
     * @apiUse InvalidAuthenticationTokenError
     * @apiUse InvalidFileError
     * @apiUse InternalServerError
     */
    /*@RequestMapping(value = "/api/user/setAvatar", method = RequestMethod.POST)
    public ResponseEntity<String> setUserAvatar(final @RequestParam("avatar") MultipartFile multipartFile, HttpServletRequest request) throws IOException {
        logger.debug(multipartFile.getOriginalFilename());
        logger.debug(multipartFile.getSize());
        logger.debug(multipartFile.getContentType());
        if (!userFilesService.checkFileValidity(multipartFile)) {
            throw new InvalidFileException("Invalid file");
        }
        String userEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        Integer userId = userService.getIdByEmail(userEmail);
        String avatarUrl = getAvatarPathPrefix(request) + userFilesService.createUserAvatar(userId, multipartFile);
        logger.debug(avatarUrl);
        return new ResponseEntity<>(avatarUrl, HttpStatus.OK);
    }


    private String getAvatarPathPrefix(HttpServletRequest request) {
        return request.getScheme() + "://" + request.getServerName() +
                ":" + request.getServerPort() + "/rest";
    }*/


    /**
     * @api {post} /api/user/setLanguage Set language
     * @apiName setLanguage
     * @apiUse TokenHeader
     * @apiGroup User
     * @apiParam {String} language language code (en, ru, cn, in, ar available)
     * @apiParamExample {json} Request Example:
     * {
     * "language": "en"
     * }
     * @apiPermission User
     * @apiDescription Set user's preferred language.
     * In case of success returns empty response with HTTP 200
     * @apiSuccessExample {json} Success-Response:
     * HTTP/1.1 200 OK
     * @apiUse MessageNotReadableError
     * @apiUse ExpiredAuthenticationTokenError
     * @apiUse MissingAuthenticationTokenError
     * @apiUse InvalidAuthenticationTokenError
     * @apiUse MissingRequiredParamError
     * @apiUse LanguageNotSupportedError
     * @apiUse InternalServerError
     */
    /*@RequestMapping(value = "/api/user/setLanguage", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<Void> setLanguage(@RequestBody Map<String, String> body) {
        String language = retrieveParamFormBody(body, "language", true);
        Locale locale = new Locale(language);
        logger.debug(locale);
        String userEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        Integer userId = userService.getIdByEmail(userEmail);
        try {
            userService.setPreferedLang(userId, locale);
        } catch (DataIntegrityViolationException e) {
            throw new NotSupportedLanguageException("Language " + language + " not supported");
        }
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @RequestMapping(value = "/api/user/getReferralReference", method = RequestMethod.GET)
    public ResponseEntity<String> getReferralReference() {
        String userEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        return new ResponseEntity<>(referralService.generateReferral(userEmail), HttpStatus.OK);
    }


    @RequestMapping(value = "/api/user/deleteUser", method = RequestMethod.DELETE, consumes = "application/json;charset=utf-8")
    public ResponseEntity<Void> tempDeleteUser(@RequestBody Map<String, String> body, HttpServletRequest request) {
        logger.debug(body.get("email"));
        userService.tempDeleteUser(body.get("email"));
        return new ResponseEntity<>(HttpStatus.OK);
    }*/

    /**
     * @api {post} /api/user/findNicknames Find nicknames
     * @apiName findNicknames
     * @apiUse TokenHeader
     * @apiGroup User
     * @apiParam {String} language language code (en, ru, cn, in, ar available)
     * @apiParamExample {json} Request Example:
     * /api/user/findNicknames?part=sd
     * @apiPermission User
     * @apiDescription Find nickname variants by part
     * In case of success returns empty response with HTTP 200
     * @apiSuccess (200) {Array} data Nicknames found
     * @apiSuccessExample {json} Success-Response:
     * HTTP/1.1 200 OK
     * <p>
     * [
     * "asda",
     * "chinasddyzyx"
     * ]
     * @apiUse MessageNotReadableError
     * @apiUse ExpiredAuthenticationTokenError
     * @apiUse MissingAuthenticationTokenError
     * @apiUse InvalidAuthenticationTokenError
     * @apiUse MissingRequiredParamError
     * @apiUse LanguageNotSupportedError
     * @apiUse InternalServerError
     */
    /*@RequestMapping(value = "/api/user/findNicknames", method = RequestMethod.GET)
    @ResponseBody
    public List<String> findNicknames(@RequestParam String part) {
        if (part == null || part.isEmpty()) {
            return Collections.EMPTY_LIST;
        }
        return userService.findNicknamesByPart(part);

    }*/


    /**
     * @api {post} /api/user/authenticateQR Authenticate via QR
     * @apiName authenticateQR
     * @apiUse TokenHeader
     * @apiGroup User
     * @apiParam {String} sessionId session id retrieved from QR
     * @apiParamExample {test/plain} Request Example:
     * FC7A1B7703714C5C4CE20A7F8146D9FD
     * @apiPermission User
     * @apiDescription Authenticates user by QR
     * @apiSuccessExample {json} Success-Response:
     * HTTP/1.1 200 OK
     * @apiUse MessageNotReadableError
     * @apiUse ExpiredAuthenticationTokenError
     * @apiUse MissingAuthenticationTokenError
     * @apiUse InvalidAuthenticationTokenError
     * @apiUse InvalidSessionIdError
     * @apiUse InternalServerError
     */
    /*@RequestMapping(value = "/api/user/authenticateQR", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<Void> authenticateQR(@RequestBody Map<String, String> params, @RequestHeader("Exrates-Rest-Token") String token) {
        *//*TODO temporary disable
        logger.debug(params);
        String sessionId = params.get("sessionId");
        logger.debug(sessionId);
        UserDetails userDetails = authTokenService.getUserByToken(token);
        HttpSession session = storeSessionListener.getSessionById(sessionId).orElseThrow(InvalidSessionIdException::new);
        final Object mutex = WebUtils.getSessionMutex(session);
        synchronized (mutex) {
            session.setAttribute("USER_DETAIL_TOKEN", userDetails);
        }
        return new ResponseEntity<>(HttpStatus.OK);*//*
        throw new NotImplimentedMethod("NOT IMPLEMENTED");
    }


    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler({HttpMessageNotReadableException.class})
    public ApiError httpMessageNotReadableExceptionHandler(HttpServletRequest req, Exception exception) {
        return new ApiError(REQUEST_NOT_READABLE, req.getRequestURL(), exception);
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(InvalidNicknameException.class)
    public ApiError invalidNicknameExceptionHandler(HttpServletRequest req, Exception exception) {
        return new ApiError(INVALID_NICKNAME, req.getRequestURL(), exception);
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler({MethodArgumentNotValidException.class, InvalidPasswordException.class, InvalidEmailException.class})
    public ApiError methodArgumentNotValidExceptionHandler(HttpServletRequest req, Exception exception) {
        return new ApiError(INVALID_PARAM_VALUE, req.getRequestURL(), exception);
    }

    @ResponseStatus(HttpStatus.FORBIDDEN)
    @ExceptionHandler(UserNotEnabledException.class)
    public ApiError userNotEnabledExceptionHandler(HttpServletRequest req, UserNotEnabledException exception) {
        return new ApiError(ACCOUNT_DISABLED, req.getRequestURL(), exception);
    }

    @ResponseStatus(HttpStatus.FORBIDDEN)
    @ExceptionHandler(UnconfirmedUserException.class)
    public ApiError unconfirmedUserExceptionHandler(HttpServletRequest req, UnconfirmedUserException exception) {
        return new ApiError(ACCOUNT_NOT_CONFIRMED, req.getRequestURL(), exception);
    }

    @ResponseStatus(HttpStatus.NOT_ACCEPTABLE)
    @ExceptionHandler(MissingCredentialException.class)
    public ApiError MissingCredentialExceptionHandler(HttpServletRequest req, MissingCredentialException exception) {
        return new ApiError(MISSING_CREDENTIALS, req.getRequestURL(), exception);
    }

    @ResponseStatus(HttpStatus.FORBIDDEN)
    @ExceptionHandler(WrongUsernameOrPasswordException.class)
    public ApiError incorrectPasswordExceptionHandler(HttpServletRequest req, Exception exception) {
        return new ApiError(INCORRECT_LOGIN_OR_PASSWORD, req.getRequestURL(), exception);
    }

    @ResponseStatus(HttpStatus.FORBIDDEN)
    @ExceptionHandler(BannedIpException.class)
    public ApiError bannedIpExceptionHandler(HttpServletRequest req, Exception exception) {
        return new ApiError(BANNED_IP, req.getRequestURL(), exception);
    }


    @ResponseStatus(HttpStatus.NOT_ACCEPTABLE)
    @ExceptionHandler(UniqueEmailConstraintException.class)
    public ApiError uniqueEmailConstraintExceptionHandler(HttpServletRequest req, Exception exception) {
        return new ApiError(EXISTING_EMAIL, req.getRequestURL(), exception);
    }

    @ResponseStatus(HttpStatus.NOT_ACCEPTABLE)
    @ExceptionHandler(UniqueNicknameConstraintException.class)
    public ApiError uniqueNicknameConstraintExceptionHandler(HttpServletRequest req, Exception exception) {
        return new ApiError(EXISTING_NICKNAME, req.getRequestURL(), exception);
    }


    @ResponseStatus(HttpStatus.NOT_ACCEPTABLE)
    @ExceptionHandler(InvalidFileException.class)
    public ApiError invalidFileExceptionHandler(HttpServletRequest req, Exception exception) {
        return new ApiError(INVALID_FILE, req.getRequestURL(), exception);
    }

    @ResponseStatus(HttpStatus.NOT_ACCEPTABLE)
    @ExceptionHandler(NotConfirmedFinPasswordException.class)
    public ApiError notConfirmedFinPasswordExceptionHandler(HttpServletRequest req, Exception exception) {
        return new ApiError(UNCONFIRMED_FIN_PASSWORD, req.getRequestURL(), exception);
    }

    @ResponseStatus(HttpStatus.NOT_ACCEPTABLE)
    @ExceptionHandler(AbsentFinPasswordException.class)
    public ApiError absentFinPasswordExceptionHandler(HttpServletRequest req, Exception exception) {
        return new ApiError(ABSENT_FIN_PASSWORD, req.getRequestURL(), exception);
    }

    @ResponseStatus(HttpStatus.NOT_ACCEPTABLE)
    @ExceptionHandler(WrongFinPasswordException.class)
    public ApiError wrongFinPasswordExceptionHandler(HttpServletRequest req, Exception exception) {
        return new ApiError(INCORRECT_FIN_PASSWORD, req.getRequestURL(), exception);
    }

    @ResponseStatus(HttpStatus.NOT_ACCEPTABLE)
    @ExceptionHandler(InvalidAppKeyException.class)
    public ApiError invalidAppKeyExceptionHandler(HttpServletRequest req, Exception exception) {
        return new ApiError(INVALID_APP_KEY, req.getRequestURL(), exception);
    }

    @ResponseStatus(HttpStatus.NOT_ACCEPTABLE)
    @ExceptionHandler({MissingServletRequestParameterException.class, MissingBodyParamException.class})
    @ResponseBody
    public ApiError missingParamHandler(HttpServletRequest req, Exception exception) {
        return new ApiError(MISSING_REQUIRED_PARAM, req.getRequestURL(), exception);

    }

    @ResponseStatus(HttpStatus.NOT_ACCEPTABLE)
    @ExceptionHandler(NotSupportedLanguageException.class)
    @ResponseBody
    public ApiError notSupportedLanguageHandler(HttpServletRequest req, Exception exception) {
        return new ApiError(LANGUAGE_NOT_SUPPORTED, req.getRequestURL(), exception);

    }

    @ResponseStatus(HttpStatus.NOT_ACCEPTABLE)
    @ExceptionHandler(InvalidSessionIdException.class)
    @ResponseBody
    public ApiError invalidSessionIdHandler(HttpServletRequest req, Exception exception) {
        return new ApiError(INVALID_SESSION_ID, req.getRequestURL(), exception);

    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(NullPointerException.class)
    @ResponseBody
    public Map<String, Object> NullPointerHandler(HttpServletRequest req, Exception exception) {
        Map<String, Object> result = new HashMap<>();
        result.put("stacktrace", Arrays.asList(exception.getStackTrace()));
        return result;
    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(Exception.class)
    public ApiError OtherErrorsHandler(HttpServletRequest req, Exception exception) {
        exception.printStackTrace();
        return new ApiError(ErrorCode.INTERNAL_SERVER_ERROR, req.getRequestURL(), exception);
    }*/


}
