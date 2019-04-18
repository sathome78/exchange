package me.exrates.service.impl;

import me.exrates.dao.ReferralUserGraphDao;
import me.exrates.dao.UserDao;
import me.exrates.dao.UserSettingsDao;
import me.exrates.dao.exception.notfound.UserNotFoundException;
import me.exrates.model.AdminAuthorityOption;
import me.exrates.model.Comment;
import me.exrates.model.TemporalToken;
import me.exrates.model.User;
import me.exrates.model.UserFile;
import me.exrates.model.dto.CallbackURL;
import me.exrates.model.dto.NotificationsUserSetting;
import me.exrates.model.dto.UpdateUserDto;
import me.exrates.model.dto.UserCurrencyOperationPermissionDto;
import me.exrates.model.dto.UserIpDto;
import me.exrates.model.dto.UserIpReportDto;
import me.exrates.model.dto.UserSessionInfoDto;
import me.exrates.model.dto.kyc.VerificationStep;
import me.exrates.model.dto.mobileApiDto.TemporaryPasswordDto;
import me.exrates.model.enums.NotificationMessageEventEnum;
import me.exrates.model.enums.TokenType;
import me.exrates.model.enums.UserCommentTopicEnum;
import me.exrates.model.enums.UserRole;
import me.exrates.model.enums.UserStatus;
import me.exrates.model.enums.invoice.InvoiceOperationDirection;
import me.exrates.model.enums.invoice.InvoiceOperationPermission;
import me.exrates.service.NotificationService;
import me.exrates.service.ReferralService;
import me.exrates.service.SendMailService;
import me.exrates.service.UserService;
import me.exrates.service.UserSettingService;
import me.exrates.service.api.ExchangeApi;
import me.exrates.service.exception.AuthenticationNotAvailableException;
import me.exrates.service.exception.CallBackUrlAlreadyExistException;
import me.exrates.service.exception.ForbiddenOperationException;
import me.exrates.service.exception.ResetPasswordExpirationException;
import me.exrates.service.exception.TokenNotFoundException;
import me.exrates.service.exception.UnRegisteredUserDeleteException;
import me.exrates.service.exception.api.UniqueEmailConstraintException;
import me.exrates.service.exception.api.UniqueNicknameConstraintException;
import me.exrates.service.notifications.G2faService;
import me.exrates.service.notifications.NotificationsSettingsService;
import me.exrates.service.session.UserSessionService;
import me.exrates.service.token.TokenScheduler;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.quartz.JobKey;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.servlet.http.HttpServletRequest;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.Random;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyBoolean;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyList;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Matchers.anySet;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {ServiceTestConfig.class})
public class UserServiceImplTest {

    @Autowired
    private UserService userService;

    @Autowired
    private UserDao userDao;

    @Autowired
    private UserSessionService userSessionService;

    @Autowired
    private SendMailService sendMailService;

    @Autowired
    private MessageSource messageSource;

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private HttpServletRequest request;

    @Autowired
    private TokenScheduler tokenScheduler;

    @Autowired
    private ReferralService referralService;

    @Autowired
    private NotificationsSettingsService settingsService;

    @Autowired
    private G2faService g2faService;

    @Autowired
    private ExchangeApi exchangeApi;

    @Autowired
    private UserSettingService userSettingService;

    @Autowired
    private ReferralUserGraphDao referralUserGraphDao;

    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @Autowired
    private UserSettingsDao userSettingsDao;


    private List<String> LOCALES_LIST;
    private User user;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);

        SecurityContextHolder.getContext()
                .setAuthentication(new AnonymousAuthenticationToken("USER", "testemail@gmail.com",
                        AuthorityUtils.createAuthorityList("USER")));

        LOCALES_LIST = new ArrayList<String>() {{
            add("EN");
            add("RU");
            add("CN");
            add("ID");
            add("AR");
        }};
        user = new User();
        user.setEmail("test@test.com");
        user.setNickname("Nick");
        user.setIp("127.0.0.1");
        user.setId(5);
        user.setRole(UserRole.USER);
        reset(userDao);
        reset(tokenScheduler);
        reset(userSessionService);
//        reset(messageSource);
    }

    @Test
    public void getLocalesList() {
        assertEquals(LOCALES_LIST, userService.getLocalesList());
    }

    @Test
    public void create_WhenEmailIsNotUnique() {
        when(userDao.ifEmailIsUnique(anyString())).thenReturn(false);
        assertEquals(false, userService.create(user, Locale.ENGLISH, "str"));

        verify(userDao, times(1)).ifEmailIsUnique("test@test.com");
    }

    @Test
    public void create_WhenNicknameIsNotUnique() {
        when(userDao.ifEmailIsUnique(anyString())).thenReturn(true);
        when(userDao.ifNicknameIsUnique(anyString())).thenReturn(false);
        assertEquals(false, userService.create(user, Locale.ENGLISH, "str"));

        verify(userDao, times(1)).ifEmailIsUnique("test@test.com");
        verify(userDao, times(1)).ifNicknameIsUnique("Nick");
    }

    @Test
    public void create_WhenUserDaoCreateFalse() {
        when(userDao.ifEmailIsUnique(anyString())).thenReturn(true);
        when(userDao.ifNicknameIsUnique(anyString())).thenReturn(true);
        when(userDao.create(any(User.class))).thenReturn(false);
        assertEquals(false, userService.create(user, Locale.ENGLISH, "str"));

        verify(userDao, times(1)).ifEmailIsUnique("test@test.com");
        verify(userDao, times(1)).ifNicknameIsUnique("Nick");
        verify(userDao, times(1)).create(user);
    }

    @Test
    public void create_WhenUserDaoInsertIpFalse() {
        when(userDao.ifEmailIsUnique(anyString())).thenReturn(true);
        when(userDao.ifNicknameIsUnique(anyString())).thenReturn(true);
        when(userDao.create(any(User.class))).thenReturn(true);
        when(userDao.insertIp(anyString(), anyString())).thenReturn(false);
        assertEquals(false, userService.create(user, Locale.ENGLISH, "str"));

        verify(userDao, times(1)).ifEmailIsUnique("test@test.com");
        verify(userDao, times(1)).ifNicknameIsUnique("Nick");
        verify(userDao, times(1)).create(user);
        verify(userDao, times(1)).insertIp("test@test.com", "127.0.0.1");
    }

    @Ignore
    public void create_WhenUserDaoCreateAndInsertIpTrue() {
        when(userDao.ifEmailIsUnique(anyString())).thenReturn(true);
        when(userDao.ifNicknameIsUnique(anyString())).thenReturn(true);
        when(userDao.create(any(User.class))).thenReturn(true);
        when(userDao.insertIp(anyString(), anyString())).thenReturn(true);
        when(userDao.getIdByEmail(anyString())).thenReturn(8);
        when(request.getScheme()).thenReturn("string");
//        when(messageSource.getMessage(anyString(), any(Object[].class), any(Locale.class))).thenReturn("OK");
        assertEquals(true, userService.create(user, Locale.ENGLISH, "str"));

        verify(userDao, times(1)).ifEmailIsUnique("test@test.com");
        verify(userDao, times(1)).ifNicknameIsUnique("Nick");
        verify(userDao, times(1)).create(user);
        verify(userDao, times(1)).insertIp("test@test.com", "127.0.0.1");
        verify(userDao, times(1)).getIdByEmail("test@test.com");
    }

    @Test(expected = UniqueNicknameConstraintException.class)
    public void createUserRest_WhenNicknameIsNotUnique() {
        when(userDao.ifNicknameIsUnique(anyString())).thenReturn(false);
        userService.createUserRest(user, Locale.ENGLISH);

        verify(userDao, times(1)).ifNicknameIsUnique("Nick");

    }

    @Test(expected = UniqueEmailConstraintException.class)
    public void createUserRest_WhenEmailIsNotUnique() {
        when(userDao.ifNicknameIsUnique(anyString())).thenReturn(true);
        when(userDao.ifEmailIsUnique(anyString())).thenReturn(false);
        userService.createUserRest(user, Locale.ENGLISH);

        verify(userDao, times(1)).ifNicknameIsUnique("Nick");
        verify(userDao, times(1)).ifEmailIsUnique("test@test.com");
    }

    @Test
    public void createUserRest_WhenResultIsFalse() {
        when(userDao.ifNicknameIsUnique(anyString())).thenReturn(true);
        when(userDao.ifEmailIsUnique(anyString())).thenReturn(true);
        when(userDao.create(any(User.class))).thenReturn(false);

       assertEquals(false, userService.createUserRest(user, Locale.ENGLISH));

        verify(userDao, times(1)).ifNicknameIsUnique("Nick");
        verify(userDao, times(1)).ifEmailIsUnique("test@test.com");
        verify(userDao, times(1)).create(user);
    }

    @Ignore
    public void createUserRest_WhenResultIsTrue() {

    }

    @Test
    public void verifyUserEmail_WhenTemporalTokenIsNull() {
        when(userDao.verifyToken(anyString())).thenReturn(null);

        assertEquals(0, userService.verifyUserEmail("token"));

        verify(userDao, times(1)).verifyToken("token");
    }

    @Test
    public void verifyUserEmail_WhenTemporalTokenIsNotNull_AndDeleteTemporalTokensFalse() {
        TemporalToken temporalToken = new TemporalToken();
        temporalToken.setUserId(49);
        when(userDao.verifyToken(anyString())).thenReturn(temporalToken);
        when(userDao.deleteTemporalTokensOfTokentypeForUser(any(TemporalToken.class))).thenReturn(false);

        assertEquals(49, userService.verifyUserEmail("token"));

        verify(userDao, times(1)).verifyToken("token");
        verify(userDao, times(1)).deleteTemporalTokensOfTokentypeForUser(temporalToken);
    }

    @Test
    public void verifyUserEmail_WhenTemporalTokenIsNotNull_AndTokenTypeCONFIRM_NEW_IP_False() {
        TemporalToken temporalToken = new TemporalToken();
        temporalToken.setUserId(49);
        temporalToken.setTokenType(TokenType.CHANGE_FIN_PASSWORD);
        when(userDao.verifyToken(anyString())).thenReturn(temporalToken);
        when(userDao.deleteTemporalTokensOfTokentypeForUser(any(TemporalToken.class))).thenReturn(true);
        when(tokenScheduler.deleteJobsRelatedWithToken(any(TemporalToken.class)))
                .thenReturn(Arrays.asList(new JobKey("Name")));

        assertEquals(49, userService.verifyUserEmail("token"));

        verify(userDao, times(1)).verifyToken("token");
        verify(userDao, times(1)).deleteTemporalTokensOfTokentypeForUser(temporalToken);
        verify(tokenScheduler, times(1)).deleteJobsRelatedWithToken(temporalToken);
    }

    @Test
    public void verifyUserEmail_WhenTemporalTokenIsNotNull_AndSetIpStateConfirmedTrue() {
        TemporalToken temporalToken = new TemporalToken();
        temporalToken.setUserId(49);
        temporalToken.setTokenType(TokenType.CONFIRM_NEW_IP);
        temporalToken.setCheckIp("127.0.0.1");
        when(userDao.verifyToken(anyString())).thenReturn(temporalToken);
        when(userDao.deleteTemporalTokensOfTokentypeForUser(any(TemporalToken.class))).thenReturn(true);
        when(tokenScheduler.deleteJobsRelatedWithToken(any(TemporalToken.class)))
                .thenReturn(Arrays.asList(new JobKey("Name")));
        when(userDao.setIpStateConfirmed(anyInt(), anyString())).thenReturn(true);

        assertEquals(49, userService.verifyUserEmail("token"));

        verify(userDao, times(1)).verifyToken("token");
        verify(userDao, times(1)).deleteTemporalTokensOfTokentypeForUser(temporalToken);
        verify(tokenScheduler, times(1)).deleteJobsRelatedWithToken(temporalToken);
        verify(userDao, times(1)).setIpStateConfirmed(49, "127.0.0.1");
    }

    @Test
    public void verifyUserEmail_WhenTemporalTokenIsNotNull_AndSetIpStateConfirmedFalse() {
        TemporalToken temporalToken = new TemporalToken();
        temporalToken.setUserId(49);
        temporalToken.setTokenType(TokenType.CONFIRM_NEW_IP);
        temporalToken.setCheckIp("127.0.0.1");
        when(userDao.verifyToken(anyString())).thenReturn(temporalToken);
        when(userDao.deleteTemporalTokensOfTokentypeForUser(any(TemporalToken.class))).thenReturn(true);
        when(tokenScheduler.deleteJobsRelatedWithToken(any(TemporalToken.class)))
                .thenReturn(Arrays.asList(new JobKey("Name")));
        when(userDao.setIpStateConfirmed(anyInt(), anyString())).thenReturn(false);

        assertEquals(0, userService.verifyUserEmail("token"));

        verify(userDao, times(1)).verifyToken("token");
        verify(userDao, times(1)).deleteTemporalTokensOfTokentypeForUser(temporalToken);
        verify(tokenScheduler, times(1)).deleteJobsRelatedWithToken(temporalToken);
        verify(userDao, times(1)).setIpStateConfirmed(49, "127.0.0.1");
    }

    @Test
    public void getTokenByUserAndType() {
        TemporalToken temporalToken = new TemporalToken();
        temporalToken.setUserId(49);
        when(userDao.getTokenByUserAndType(anyInt(), any(TokenType.class))).thenReturn(Arrays.asList(temporalToken));

        assertEquals(Arrays.asList(temporalToken), userService.getTokenByUserAndType(user, TokenType.CHANGE_PASSWORD));

        verify(userDao, times(1)).getTokenByUserAndType(5, TokenType.CHANGE_PASSWORD);
    }

    @Test
    public void getAllTokens() {
        TemporalToken temporalToken = new TemporalToken();
        temporalToken.setUserId(49);
        when(userDao.getAllTokens()).thenReturn(Arrays.asList(temporalToken));

        assertEquals(Arrays.asList(temporalToken), userService.getAllTokens());

        verify(userDao, times(1)).getAllTokens();
    }

    @Test
    public void deleteExpiredToken_WhenTokenTypeIsNotREGISTRATION() throws UnRegisteredUserDeleteException {
        TemporalToken temporalToken = new TemporalToken();
        temporalToken.setUserId(49);
        temporalToken.setTokenType(TokenType.CHANGE_FIN_PASSWORD);
        when(userDao.verifyToken(anyString())).thenReturn(temporalToken);
        when(userDao.deleteTemporalToken(any(TemporalToken.class))).thenReturn(true);

        assertEquals(true, userService.deleteExpiredToken("expiredToken"));

        verify(userDao, times(1)).verifyToken("expiredToken");
        verify(userDao, times(1)).deleteTemporalToken(temporalToken);
    }

    @Test
    public void deleteExpiredToken_WhenUserStatusNotREGISTERED() throws UnRegisteredUserDeleteException {
        TemporalToken temporalToken = new TemporalToken();
        temporalToken.setUserId(49);
        temporalToken.setTokenType(TokenType.REGISTRATION);
        User user2 = new User();
        user2.setUserStatus(UserStatus.ACTIVE);
        when(userDao.verifyToken(anyString())).thenReturn(temporalToken);
        when(userDao.deleteTemporalToken(any(TemporalToken.class))).thenReturn(true);
        when(userDao.getUserById(anyInt())).thenReturn(user2);

        assertEquals(true, userService.deleteExpiredToken("expiredToken"));

        verify(userDao, times(1)).verifyToken("expiredToken");
        verify(userDao, times(1)).deleteTemporalToken(temporalToken);
        verify(userDao, times(1)).getUserById(49);
    }

    @Test
    public void deleteExpiredToken_WhenUserDaoDeleteTrue() throws UnRegisteredUserDeleteException {
        TemporalToken temporalToken = new TemporalToken();
        temporalToken.setUserId(49);
        temporalToken.setTokenType(TokenType.REGISTRATION);
        when(userDao.verifyToken(anyString())).thenReturn(temporalToken);
        when(userDao.deleteTemporalToken(any(TemporalToken.class))).thenReturn(true);
        when(userDao.getUserById(anyInt())).thenReturn(user);
        when(referralUserGraphDao.getParent(anyInt())).thenReturn(12);
        doNothing().when(referralUserGraphDao).changeReferralParent(anyInt(), anyInt());
        when(userDao.delete(any(User.class))).thenReturn(true);

        assertEquals(true, userService.deleteExpiredToken("expiredToken"));

        verify(userDao, times(1)).verifyToken("expiredToken");
        verify(userDao, times(1)).deleteTemporalToken(temporalToken);
        verify(userDao, times(1)).getUserById(49);
        verify(referralUserGraphDao, times(1)).getParent(5);
        verify(referralUserGraphDao, times(1)).changeReferralParent(5, 12);
        verify(userDao, times(1)).delete(user);
    }

    @Test(expected = UnRegisteredUserDeleteException.class)
    public void deleteExpiredToken_WhenUserDaoDeleteFalse() throws UnRegisteredUserDeleteException {
        TemporalToken temporalToken = new TemporalToken();
        temporalToken.setUserId(49);
        temporalToken.setTokenType(TokenType.REGISTRATION);
        when(userDao.verifyToken(anyString())).thenReturn(temporalToken);
        when(userDao.deleteTemporalToken(any(TemporalToken.class))).thenReturn(true);
        when(userDao.getUserById(anyInt())).thenReturn(user);
        when(referralUserGraphDao.getParent(anyInt())).thenReturn(12);
        doNothing().when(referralUserGraphDao).changeReferralParent(anyInt(), anyInt());
        when(userDao.delete(any(User.class))).thenReturn(false);

        userService.deleteExpiredToken("expiredToken");
    }

    @Test
    public void getIdByEmail() {
        when(userDao.getIdByEmail(anyString())).thenReturn(41);

        assertEquals(41, userService.getIdByEmail("test@test.com"));

        verify(userDao, times(1)).getIdByEmail("test@test.com");
    }

    @Test
    public void getIdByNickname() {
        when(userDao.getIdByNickname(anyString())).thenReturn(41);

        assertEquals(41, userService.getIdByNickname("Nick"));

        verify(userDao, times(1)).getIdByNickname("Nick");
    }

    @Test
    public void setNickname() {
        when(userDao.setNickname(anyString(), anyString())).thenReturn(true);

        assertEquals(true, userService.setNickname("Nick", "test@test.com"));

        verify(userDao, times(1)).setNickname("Nick", "test@test.com");
    }

    @Test
    public void hasNickname_WhenNicknameExists() {
        when(userDao.findByEmail(anyString())).thenReturn(user);

        assertEquals(true, userService.hasNickname("test@test.com"));

        verify(userDao, times(1)).findByEmail("test@test.com");
    }

    @Test
    public void hasNickname_WhenNicknameNull() {
        User user = new User();
        when(userDao.findByEmail(anyString())).thenReturn(user);

        assertEquals(false, userService.hasNickname("test@test.com"));

        verify(userDao, times(1)).findByEmail("test@test.com");
    }

    @Test
    public void hasNickname_WhenNicknameIsSpacesOnly() {
        User user = new User();
        user.setNickname("  ");
        when(userDao.findByEmail(anyString())).thenReturn(user);

        assertEquals(false, userService.hasNickname("test@test.com"));

        verify(userDao, times(1)).findByEmail("test@test.com");
    }

    @Test
    public void findByEmail() {
        when(userDao.findByEmail(anyString())).thenReturn(user);

        assertEquals(user, userService.findByEmail("test@test.com"));

        verify(userDao, times(1)).findByEmail("test@test.com");
    }

    @Test
    public void findByNickname() {
        when(userDao.findByNickname(anyString())).thenReturn(user);

        assertEquals(user, userService.findByNickname("Nick"));

        verify(userDao, times(1)).findByNickname("Nick");
    }

    @Test(expected = IllegalStateException.class)
    public void createUserFile_When3docs() {
        UserFile userFile = new UserFile();
        userFile.setUserId(40);
        when(userDao.findUserDoc(anyInt())).thenReturn(Arrays.asList(userFile, new UserFile(),new UserFile()));

        userService.createUserFile(13, null);

        verify(userDao, times(1)).findUserDoc(13);
    }

    @Test
    public void createUserFile_WhenLessThan3docs() {
        UserFile userFile = new UserFile();
        userFile.setUserId(40);
        // TODO more than 3 times must call Excaption too, but now only 3 times for calling findUserDoc()
        when(userDao.findUserDoc(anyInt())).thenReturn(Arrays.asList(userFile, new UserFile()));
        doNothing().when(userDao).createUserDoc(anyInt(), anyList());

        userService.createUserFile(13, null);

        verify(userDao, times(1)).findUserDoc(13);
        verify(userDao, times(1)).createUserDoc(13,null);
    }

    @Test
    public void setUserAvatar(){
        Path path = Paths.get("/directory");
        doNothing().when(userDao).setUserAvatar(anyInt(), anyString());

        userService.setUserAvatar(7, path);

        verify(userDao, times(1)).setUserAvatar(7,path.toString());
    }

    @Test
    public void deleteUserFile() {
        doNothing().when(userDao).deleteUserDoc(anyInt());

        userService.deleteUserFile(12);

        verify(userDao, times(1)).deleteUserDoc(12);
    }

    @Test
    public void findUserDoc() {
        UserFile userFile = new UserFile();
        userFile.setPath(Paths.get("/path"));
        when(userDao.findUserDoc(anyInt())).thenReturn(Arrays.asList(userFile));

        assertEquals(Arrays.asList(userFile),userService.findUserDoc(15));

        verify(userDao, times(1)).findUserDoc(15);
    }

    @Test
    public void ifNicknameIsUnique() {
        when(userDao.ifNicknameIsUnique(anyString())).thenReturn(true);

        assertEquals(true,userService.ifNicknameIsUnique("Some String"));

        verify(userDao, times(1)).ifNicknameIsUnique("Some String");
    }

    @Test
    public void ifEmailIsUnique() {
        when(userDao.ifEmailIsUnique(anyString())).thenReturn(true);

        assertEquals(true,userService.ifEmailIsUnique("test@test.com"));

        verify(userDao, times(1)).ifEmailIsUnique("test@test.com");
    }

    @Test
    public void userExistByEmail() {
        when(userDao.userExistByEmail(anyString())).thenReturn(true);

        assertEquals(true,userService.userExistByEmail("test@test.com"));

        verify(userDao, times(1)).userExistByEmail("test@test.com");
    }

    @Test
    public void logIP_WhenUserIPNull() {
        when(userDao.getIdByEmail(anyString())).thenReturn(30);
        when(userDao.getIP(anyInt())).thenReturn(null);
        when(userDao.setIP(anyInt(), anyString())).thenReturn(true);
        when(userDao.addIPToLog(anyInt(), anyString())).thenReturn(true);

        assertEquals(null, userService.logIP("test@test.com", "127.0.0.1"));

        verify(userDao, times(1)).getIdByEmail("test@test.com");
        verify(userDao, times(1)).getIP(30);
        verify(userDao, times(1)).setIP(30, "127.0.0.1");
        verify(userDao, times(1)).addIPToLog(30, "127.0.0.1");
    }

    @Test
    public void logIP_WhenUserIPNotNull() {
        when(userDao.getIdByEmail(anyString())).thenReturn(30);
        when(userDao.getIP(anyInt())).thenReturn("userIp");
        when(userDao.addIPToLog(anyInt(), anyString())).thenReturn(true);

        assertEquals("userIp", userService.logIP("test@test.com", "127.0.0.1"));

        verify(userDao, times(1)).getIdByEmail("test@test.com");
        verify(userDao, times(1)).getIP(30);
        verify(userDao, times(1)).addIPToLog(30, "127.0.0.1");
    }

    @Test
    public void getAllRoles() {
        when(userDao.getAllRoles()).thenReturn(Arrays.asList(UserRole.USER));

        assertEquals(Arrays.asList(UserRole.USER),userService.getAllRoles());

        verify(userDao, times(1)).getAllRoles();
    }

    @Test
    public void getUserById() {
        when(userDao.getUserById(anyInt())).thenReturn(user);

        assertEquals(user,userService.getUserById(78));

        verify(userDao, times(1)).getUserById(78);
    }

    @Test
    public void createUserByAdmin_WhenResultFalse() {
        when(userDao.create(any(User.class))).thenReturn(false);

        assertEquals(false, userService.createUserByAdmin(user));

        verify(userDao, times(1)).create(user);
    }

    @Test
    public void createUserByAdmin_WhenUserRoleUSER() {
        User user1 = new User();
        user1.setRole(UserRole.USER);
        when(userDao.create(any(User.class))).thenReturn(true);

        assertEquals(true, userService.createUserByAdmin(user1));

        verify(userDao, times(1)).create(user1);
    }

    @Test
    public void createUserByAdmin_WhenUserRoleROLE_CHANGE_PASSWORD() {
        User user1 = new User();
        user1.setRole(UserRole.ROLE_CHANGE_PASSWORD);
        when(userDao.create(any(User.class))).thenReturn(true);

        assertEquals(true, userService.createUserByAdmin(user1));

        verify(userDao, times(1)).create(user1);
    }

    @Test
    public void createUserByAdmin_WhenCreateAdminAuthoritiesForUser() {
        User user1 = new User();
        user1.setRole(UserRole.ADMINISTRATOR);
        user1.setEmail("test1@test.com");
        when(userDao.create(any(User.class))).thenReturn(true);
        when(userDao.getIdByEmail(anyString())).thenReturn(15);
        when(userDao.createAdminAuthoritiesForUser(anyInt(), any(UserRole.class))).thenReturn(false);

        assertEquals(false, userService.createUserByAdmin(user1));

        verify(userDao, times(1)).create(user1);
        verify(userDao, times(1)).getIdByEmail("test1@test.com");
        verify(userDao, times(1)).createAdminAuthoritiesForUser(15, UserRole.ADMINISTRATOR);
    }

    @Test
    public void updateUserByAdmin_WhenResultFalse() {
        UpdateUserDto updateUserDto = new UpdateUserDto(55);
        when(userDao.update(any(UpdateUserDto.class))).thenReturn(false);

        assertEquals(false, userService.updateUserByAdmin(updateUserDto));

        verify(userDao, times(1)).update(updateUserDto);
    }

    @Test
    public void updateUserByAdmin_WhenRemoveUserAuthorities() {
        UpdateUserDto updateUserDto = new UpdateUserDto(55);
        updateUserDto.setRole(UserRole.USER);
        when(userDao.update(any(UpdateUserDto.class))).thenReturn(true);
        when(userDao.hasAdminAuthorities(anyInt())).thenReturn(true);
        when(userDao.removeUserAuthorities(anyInt())).thenReturn(true);

        assertEquals(true, userService.updateUserByAdmin(updateUserDto));

        verify(userDao, times(1)).update(updateUserDto);
        verify(userDao, times(1)).hasAdminAuthorities(55);
        verify(userDao, times(1)).removeUserAuthorities(55);
    }

    @Test
    public void updateUserByAdmin_WhenCreateAdminAuthoritiesForUser() {
        UpdateUserDto updateUserDto = new UpdateUserDto(55);
        updateUserDto.setRole(UserRole.ADMINISTRATOR);
        when(userDao.update(any(UpdateUserDto.class))).thenReturn(true);
        when(userDao.hasAdminAuthorities(anyInt())).thenReturn(false);
        when(userDao.createAdminAuthoritiesForUser(anyInt(), any(UserRole.class))).thenReturn(true);

        assertEquals(true, userService.updateUserByAdmin(updateUserDto));

        verify(userDao, times(1)).update(updateUserDto);
        verify(userDao, times(1)).hasAdminAuthorities(55);
        verify(userDao, times(1)).createAdminAuthoritiesForUser(55,UserRole.ADMINISTRATOR);
    }

    @Test
    public void updateUserSettings() {
        UpdateUserDto updateUserDto = new UpdateUserDto(55);
        when(userDao.update(any(UpdateUserDto.class))).thenReturn(true);

        assertEquals(true, userService.updateUserSettings(updateUserDto));

        verify(userDao, times(1)).update(updateUserDto);
    }

    @Ignore
    public void update() {
    }

    @Ignore
    public void update1() {
    }

    @Ignore
    public void sendEmailWithToken() {
    }

    @Ignore
    public void sendEmailWithToken1() {
    }

    @Ignore
    public void sendUnfamiliarIpNotificationEmail() {
//        String var1, Object[] var2, Locale var3
//        User user, String emailSubject, String emailText, Locale locale
//        "admin.changePasswordTitle", "user.settings.changePassword.successful
        User user = new User();
        user.setEmail("Test@test.com");
        user.setIp("127.0.0.1");
        when(messageSource.getMessage(anyString(), any(Object[].class), any(Locale.class))).thenReturn("str");
//        doNothing().when(sendMailService).sendInfoMail(any(Email.class));

        userService.sendUnfamiliarIpNotificationEmail(user, "admin.changePasswordTitle", "user.settings.changePassword.successful", Locale.ENGLISH);

    }

    @Test
    public void createTemporalToken_WhenResultFalse() {
        TemporalToken temporalToken = new TemporalToken();
        temporalToken.setUserId(49);
        when(userDao.createTemporalToken(any(TemporalToken.class))).thenReturn(false);

        assertEquals(false, userService.createTemporalToken(temporalToken));

        verify(userDao, times(1)).createTemporalToken(temporalToken);
    }

    @Test
    public void createTemporalToken_WhenResultTrue() {
        TemporalToken temporalToken = new TemporalToken();
        temporalToken.setUserId(49);
        when(userDao.createTemporalToken(any(TemporalToken.class))).thenReturn(true);
        when(tokenScheduler.initTrigers()).thenReturn(Arrays.asList(new JobKey("jobKey")));

        assertEquals(true, userService.createTemporalToken(temporalToken));

        verify(userDao, times(1)).createTemporalToken(temporalToken);
        verify(tokenScheduler, times(1)).initTrigers();
    }

    @Test
    public void getCommonReferralRoot_WhenOk() {
        when(userDao.getCommonReferralRoot()).thenReturn(user);

        assertEquals(user, userService.getCommonReferralRoot());

        verify(userDao, times(1)).getCommonReferralRoot();
    }

    @Test
    public void getCommonReferralRoot_WhenException() {
        when(userDao.getCommonReferralRoot()).thenThrow(EmptyResultDataAccessException.class);

        assertEquals(null, userService.getCommonReferralRoot());

        verify(userDao, times(1)).getCommonReferralRoot();
    }

    @Ignore
    public void checkFinPassword() {
    }

    @Test
    public void updateCommonReferralRoot() {
        doNothing().when(userDao).updateCommonReferralRoot(anyInt());

        userService.updateCommonReferralRoot(5);

        verify(userDao, times(1)).updateCommonReferralRoot(5);
    }

    @Test
    public void getPreferedLang() {
        when(userDao.getPreferredLang(anyInt())).thenReturn("PreferredLang");

        assertEquals("PreferredLang", userService.getPreferedLang(57));

        verify(userDao, times(1)).getPreferredLang(57);
    }

    @Test
    public void getPreferedLangByEmail() {
        when(userDao.getPreferredLangByEmail(anyString())).thenReturn("PreferredLang");

        assertEquals("PreferredLang", userService.getPreferedLangByEmail("test@test.com"));

        verify(userDao, times(1)).getPreferredLangByEmail("test@test.com");
    }

    @Test
    public void setPreferedLang() {
        when(userDao.setPreferredLang(anyInt(), any(Locale.class))).thenReturn(true);

        assertEquals(true, userService.setPreferedLang(38, Locale.ENGLISH));

        verify(userDao, times(1)).setPreferredLang(38, Locale.ENGLISH);
    }

    @Test
    public void insertIp() {
        when(userDao.insertIp(anyString(), anyString())).thenReturn(true);

        assertEquals(true, userService.insertIp("test@test.com", "127.0.0.1"));

        verify(userDao, times(1)).insertIp("test@test.com", "127.0.0.1");
    }

    @Test
    public void getUserIpState() {
        UserIpDto userIpDto = new UserIpDto(77);
        when(userDao.getUserIpState(anyString(), anyString())).thenReturn(userIpDto);

        assertEquals(userIpDto, userService.getUserIpState("test@test.com", "127.0.0.1"));

        verify(userDao, times(1)).getUserIpState("test@test.com", "127.0.0.1");
    }

    @Test
    public void setLastRegistrationDate() {
        when(userDao.setLastRegistrationDate(anyInt(), anyString())).thenReturn(true);

        assertEquals(true, userService.setLastRegistrationDate(88, "127.0.0.1"));

        verify(userDao, times(1)).setLastRegistrationDate(88, "127.0.0.1");
    }

    @Ignore
    public void saveTemporaryPasswordAndNotify() {
    }

    @Test(expected = TokenNotFoundException.class)
    public void replaceUserPassAndDelete_WhenTokenNotFoundException() {
        when(userDao.verifyToken(anyString())).thenReturn(null);
        when(userDao.deleteTemporaryPassword(anyLong())).thenReturn(true);

        userService.replaceUserPassAndDelete("str",3L);
    }

    @Test(expected = ResetPasswordExpirationException.class)
    public void replaceUserPassAndDelete_WhenResetPasswordExpirationException() {
        TemporalToken temporalToken = new TemporalToken();
        temporalToken.setUserId(49);
        TemporaryPasswordDto temporaryPasswordDto = new TemporaryPasswordDto();
        temporaryPasswordDto.setDateCreation(LocalDateTime.now().minusDays(3));

        when(userDao.verifyToken(anyString())).thenReturn(temporalToken);
        when(userDao.getTemporaryPasswordById(anyLong())).thenReturn(temporaryPasswordDto);
        when(userDao.deleteTemporaryPassword(anyLong())).thenReturn(true);

        userService.replaceUserPassAndDelete("str",3L);
    }

    @Test
    public void replaceUserPassAndDelete_WhenTemporalTokenIsNotNull_AndDeleteTemporalTokensFalse() {
        TemporalToken temporalToken = new TemporalToken();
        temporalToken.setUserId(49);
        TemporaryPasswordDto temporaryPasswordDto = new TemporaryPasswordDto();
        temporaryPasswordDto.setDateCreation(LocalDateTime.now());
        temporaryPasswordDto.setUserId(10);

        when(userDao.verifyToken(anyString())).thenReturn(temporalToken);
        when(userDao.getTemporaryPasswordById(anyLong())).thenReturn(temporaryPasswordDto);
        when(userDao.updateUserPasswordFromTemporary(anyLong())).thenReturn(true);
        when(userDao.deleteTemporaryPassword(anyLong())).thenReturn(true);
        when(userDao.getUserById(anyInt())).thenReturn(user);
        doNothing().when(userSessionService).invalidateUserSessionExceptSpecific(anyString(), anyString());
        when(userDao.deleteTemporalTokensOfTokentypeForUser(any(TemporalToken.class))).thenReturn(false);

        assertEquals(true, userService.replaceUserPassAndDelete("str",3L));

        verify(userDao, times(1)).verifyToken("str");
        verify(userDao, times(1)).getTemporaryPasswordById(3L);
        verify(userDao, times(1)).updateUserPasswordFromTemporary(3L);
        verify(userDao, times(1)).deleteTemporaryPassword(3L);
        verify(userDao, times(1)).getUserById(10);
        verify(userSessionService, times(1)).invalidateUserSessionExceptSpecific(user.getEmail(),null);
        verify(userDao, times(1)).deleteTemporalTokensOfTokentypeForUser(temporalToken);
    }

    @Test
    public void replaceUserPassAndDelete_WhenTemporalTokenIsNotNull_AndTokenTypeCONFIRM_NEW_IP_False() {
        TemporalToken temporalToken = new TemporalToken();
        temporalToken.setUserId(49);
        temporalToken.setTokenType(TokenType.CHANGE_FIN_PASSWORD);
        TemporaryPasswordDto temporaryPasswordDto = new TemporaryPasswordDto();
        temporaryPasswordDto.setDateCreation(LocalDateTime.now());
        temporaryPasswordDto.setUserId(10);

        when(userDao.verifyToken(anyString())).thenReturn(temporalToken);
        when(userDao.getTemporaryPasswordById(anyLong())).thenReturn(temporaryPasswordDto);
        when(userDao.updateUserPasswordFromTemporary(anyLong())).thenReturn(true);
        when(userDao.deleteTemporaryPassword(anyLong())).thenReturn(true);
        when(userDao.getUserById(anyInt())).thenReturn(user);
        doNothing().when(userSessionService).invalidateUserSessionExceptSpecific(anyString(), anyString());
        when(userDao.deleteTemporalTokensOfTokentypeForUser(any(TemporalToken.class))).thenReturn(true);
        when(tokenScheduler.deleteJobsRelatedWithToken(any(TemporalToken.class)))
                .thenReturn(Arrays.asList(new JobKey("Name")));

        assertEquals(true, userService.replaceUserPassAndDelete("str",3L));

        verify(userDao, times(1)).verifyToken("str");
        verify(userDao, times(1)).getTemporaryPasswordById(3L);
        verify(userDao, times(1)).updateUserPasswordFromTemporary(3L);
        verify(userDao, times(1)).deleteTemporaryPassword(3L);
        verify(userDao, times(1)).getUserById(10);
        verify(userSessionService, times(1)).invalidateUserSessionExceptSpecific(user.getEmail(),null);
        verify(userDao, times(1)).deleteTemporalTokensOfTokentypeForUser(temporalToken);
        verify(tokenScheduler, times(1)).deleteJobsRelatedWithToken(temporalToken);
    }

    @Test
    public void replaceUserPassAndDelete_WhenTemporalTokenIsNotNull_AndSetIpStateConfirmedTrue() {
        TemporalToken temporalToken = new TemporalToken();
        temporalToken.setUserId(49);
        temporalToken.setTokenType(TokenType.CONFIRM_NEW_IP);
        temporalToken.setCheckIp("127.0.0.1");
        TemporaryPasswordDto temporaryPasswordDto = new TemporaryPasswordDto();
        temporaryPasswordDto.setDateCreation(LocalDateTime.now());
        temporaryPasswordDto.setUserId(10);

        when(userDao.verifyToken(anyString())).thenReturn(temporalToken);
        when(userDao.getTemporaryPasswordById(anyLong())).thenReturn(temporaryPasswordDto);
        when(userDao.updateUserPasswordFromTemporary(anyLong())).thenReturn(true);
        when(userDao.deleteTemporaryPassword(anyLong())).thenReturn(true);
        when(userDao.getUserById(anyInt())).thenReturn(user);
        doNothing().when(userSessionService).invalidateUserSessionExceptSpecific(anyString(), anyString());
        when(userDao.deleteTemporalTokensOfTokentypeForUser(any(TemporalToken.class))).thenReturn(true);
        when(tokenScheduler.deleteJobsRelatedWithToken(any(TemporalToken.class))).thenReturn(Arrays.asList(new JobKey("Name")));
        when(userDao.setIpStateConfirmed(anyInt(), anyString())).thenReturn(true);

        assertEquals(true, userService.replaceUserPassAndDelete("str",3L));

        verify(userDao, times(1)).verifyToken("str");
        verify(userDao, times(1)).getTemporaryPasswordById(3L);
        verify(userDao, times(1)).updateUserPasswordFromTemporary(3L);
        verify(userDao, times(1)).deleteTemporaryPassword(3L);
        verify(userDao, times(1)).getUserById(10);
        verify(userSessionService, times(1)).invalidateUserSessionExceptSpecific(user.getEmail(),null);
        verify(userDao, times(1)).deleteTemporalTokensOfTokentypeForUser(temporalToken);
        verify(tokenScheduler, times(1)).deleteJobsRelatedWithToken(temporalToken);
        verify(userDao, times(1)).setIpStateConfirmed(49, "127.0.0.1");
    }

    @Test
    public void replaceUserPassAndDelete_WhenTemporalTokenIsNotNull_AndSetIpStateConfirmedFalse() {
        TemporalToken temporalToken = new TemporalToken();
        temporalToken.setUserId(49);
        temporalToken.setTokenType(TokenType.CONFIRM_NEW_IP);
        temporalToken.setCheckIp("127.0.0.1");
        TemporaryPasswordDto temporaryPasswordDto = new TemporaryPasswordDto();
        temporaryPasswordDto.setDateCreation(LocalDateTime.now());
        temporaryPasswordDto.setUserId(10);

        when(userDao.verifyToken(anyString())).thenReturn(temporalToken);
        when(userDao.getTemporaryPasswordById(anyLong())).thenReturn(temporaryPasswordDto);
        when(userDao.updateUserPasswordFromTemporary(anyLong())).thenReturn(true);
        when(userDao.deleteTemporaryPassword(anyLong())).thenReturn(true);
        when(userDao.getUserById(anyInt())).thenReturn(user);
        doNothing().when(userSessionService).invalidateUserSessionExceptSpecific(anyString(), anyString());
        when(userDao.deleteTemporalTokensOfTokentypeForUser(any(TemporalToken.class))).thenReturn(true);
        when(tokenScheduler.deleteJobsRelatedWithToken(any(TemporalToken.class))).thenReturn(Arrays.asList(new JobKey("Name")));
        when(userDao.setIpStateConfirmed(anyInt(), anyString())).thenReturn(false);

        assertEquals(false, userService.replaceUserPassAndDelete("str",3L));

        verify(userDao, times(1)).verifyToken("str");
        verify(userDao, times(1)).getTemporaryPasswordById(3L);
        verify(userDao, times(1)).updateUserPasswordFromTemporary(3L);
        verify(userDao, times(1)).deleteTemporaryPassword(3L);
        verify(userDao, times(1)).getUserById(10);
        verify(userSessionService, times(1)).invalidateUserSessionExceptSpecific(user.getEmail(),null);
        verify(userDao, times(1)).deleteTemporalTokensOfTokentypeForUser(temporalToken);
        verify(tokenScheduler, times(1)).deleteJobsRelatedWithToken(temporalToken);
        verify(userDao, times(1)).setIpStateConfirmed(49, "127.0.0.1");
    }

    @Test
    public void removeTemporaryPassword() {
        when(userDao.deleteTemporaryPassword(anyLong())).thenReturn(true);

        assertEquals(true, userService.removeTemporaryPassword(15L));

        verify(userDao, times(1)).deleteTemporaryPassword(15L);
    }

    @Test
    public void tempDeleteUser_WhenTrue() {
        when(userDao.getIdByEmail(anyString())).thenReturn(19);
        when(userDao.tempDeleteUserWallets(anyInt())).thenReturn(true);
        when(userDao.tempDeleteUser(anyInt())).thenReturn(true);

        assertEquals(true, userService.tempDeleteUser("test@test.com"));

        verify(userDao, times(1)).getIdByEmail("test@test.com");
        verify(userDao, times(1)).tempDeleteUserWallets(19);
        verify(userDao, times(1)).tempDeleteUser(19);
    }

    @Test(expected = RuntimeException.class)
    public void tempDeleteUser_WhenTempDeleteUserWalletsFalse() {
        when(userDao.getIdByEmail(anyString())).thenReturn(19);
        when(userDao.tempDeleteUserWallets(anyInt())).thenReturn(false);

        userService.tempDeleteUser("test@test.com");

        verify(userDao, times(1)).getIdByEmail("test@test.com");
        verify(userDao, times(1)).tempDeleteUserWallets(19);
    }

    @Test(expected = RuntimeException.class)
    public void tempDeleteUser_WhenTempDeleteUserFalse() {
        when(userDao.getIdByEmail(anyString())).thenReturn(19);
        when(userDao.tempDeleteUserWallets(anyInt())).thenReturn(true);
        when(userDao.tempDeleteUser(anyInt())).thenReturn(false);

        userService.tempDeleteUser("test@test.com");

        verify(userDao, times(1)).getIdByEmail("test@test.com");
        verify(userDao, times(1)).tempDeleteUserWallets(19);
        verify(userDao, times(1)).tempDeleteUser(19);
    }

    @Test
    public void getUserSessionInfo_WhenOk() {
        UserSessionInfoDto userSessionInfoDto = new UserSessionInfoDto();
        userSessionInfoDto.setUserEmail("email");
        Set<String> strings = new HashSet<>();
        strings.add("str");
        when(userDao.getUserSessionInfo(anySet())).thenReturn(Arrays.asList(userSessionInfoDto));

        assertEquals(Arrays.asList(userSessionInfoDto), userService.getUserSessionInfo(strings));

        verify(userDao, times(1)).getUserSessionInfo(strings);
    }

    @Test
    public void getUserSessionInfo_WhenException() {
        Set<String> strings = new HashSet<>();
        strings.add("str");
        when(userDao.getUserSessionInfo(anySet())).thenThrow(Exception.class);

        assertEquals(Collections.EMPTY_LIST, userService.getUserSessionInfo(strings));

        verify(userDao, times(1)).getUserSessionInfo(strings);
    }

    @Test
    public void getAvatarPath() {
        when(userDao.getAvatarPath(anyInt())).thenReturn("str");

        assertEquals("str", userService.getAvatarPath(15));

        verify(userDao, times(1)).getAvatarPath(15);
    }

    @Test
    public void getUserLocaleForMobile_WhenLangRu() {
        when(userDao.getPreferredLangByEmail(anyString())).thenReturn("ru");

        assertEquals(new Locale("ru"),userService.getUserLocaleForMobile("test@test.com"));

        verify(userDao, times(1)).getPreferredLangByEmail("test@test.com");
    }

    @Test
    public void getUserLocaleForMobile_WhenLangEn() {
        when(userDao.getPreferredLangByEmail(anyString())).thenReturn("en");

        assertEquals(new Locale("en"),userService.getUserLocaleForMobile("test@test.com"));

        verify(userDao, times(1)).getPreferredLangByEmail("test@test.com");
    }

    @Test
    public void getUserLocaleForMobile_WhenLangNotEnNotRu() {
        when(userDao.getPreferredLangByEmail(anyString())).thenReturn("fr");

        assertEquals(new Locale("en"),userService.getUserLocaleForMobile("test@test.com"));

        verify(userDao, times(1)).getPreferredLangByEmail("test@test.com");
    }

    @Test
    public void getUserComments_WhenSetEditableTrue() {
        Collection<Comment> comments = new ArrayList<>();
        Comment comment = new Comment();
        comment.setCreator(user);
        comment.setCreationTime(LocalDateTime.now());
        comment.setMessageSent(false);
        comment.setEditable(true);
        ((ArrayList<Comment>) comments).add(comment);
        when(userDao.findByEmail(anyString())).thenReturn(user);
        when(userDao.getUserComments(anyInt())).thenReturn(comments);

        assertEquals(((ArrayList<Comment>) comments).get(0).isEditable(),
                ((ArrayList<Comment>)userService.getUserComments(15, "test@test.com")).get(0).isEditable());

        verify(userDao, times(1)).findByEmail("test@test.com");
        verify(userDao, times(1)).getUserComments(15);
    }

    @Test
    public void getUserComments_WhenSetEditableFalse() {
        Collection<Comment> comments = new ArrayList<>();
        User user2 = new User();
        user2.setId(8);
        Comment comment = new Comment();
        comment.setCreator(user2);
        comment.setCreationTime(LocalDateTime.now().minusDays(3));
        comment.setMessageSent(true);
        comment.setEditable(false);
        ((ArrayList<Comment>) comments).add(comment);
        when(userDao.findByEmail(anyString())).thenReturn(user);
        when(userDao.getUserComments(anyInt())).thenReturn(comments);

        assertEquals(((ArrayList<Comment>) comments).get(0).isEditable(),
                ((ArrayList<Comment>)userService.getUserComments(15, "test@test.com")).get(0).isEditable());

        verify(userDao, times(1)).findByEmail("test@test.com");
        verify(userDao, times(1)).getUserComments(15);
    }

    @Test
    public void addUserComment_WhenOk() {
        Comment comment = new Comment();
        comment.setMessageSent(false);
        comment.setUser(user);
        comment.setComment("newComment");
        comment.setUserCommentTopic(UserCommentTopicEnum.GENERAL);
        comment.setCreator(user);
        when(userDao.findByEmail(anyString())).thenReturn(user);
        when(userDao.findByEmail(anyString())).thenReturn(user);
        when(userDao.addUserComment(any(Comment.class))).thenReturn(true);

        assertEquals(true, userService.addUserComment(UserCommentTopicEnum.GENERAL,
                "newComment", "email", false));

        verify(userDao, times(1)).findByEmail("email");
        verify(userDao, times(1)).findByEmail("testemail@gmail.com");
        verify(userDao, times(1)).addUserComment(comment);
    }

    @Test
    public void addUserComment_WhenException() {
        Comment comment = new Comment();
        comment.setMessageSent(false);
        comment.setUser(user);
        comment.setComment("newComment");
        comment.setUserCommentTopic(UserCommentTopicEnum.GENERAL);
        when(userDao.findByEmail(anyString())).thenReturn(user);
        SecurityContextHolder.getContext()
                .setAuthentication(null);
        when(userDao.addUserComment(any(Comment.class))).thenReturn(true);

        assertEquals(true, userService.addUserComment(UserCommentTopicEnum.GENERAL,
                "newComment", "email", false));

        verify(userDao, times(1)).findByEmail("email");
        verify(userDao, times(1)).addUserComment(comment);
    }

    @Ignore
    public void addUserComment_WhenMessageSentTrue() {
        Comment comment = new Comment();
        comment.setMessageSent(false);
        comment.setUser(user);
        comment.setComment("newComment");
        comment.setUserCommentTopic(UserCommentTopicEnum.GENERAL);
        comment.setCreator(user);
        when(userDao.findByEmail(anyString())).thenReturn(user);
        when(userDao.findByEmail(anyString())).thenReturn(user);
        when(userDao.addUserComment(any(Comment.class))).thenReturn(true);
//        doNothing().when(notificationService).notifyUser(anyInt(), any(NotificationEvent.class),
//                anyString(), anyString(), any(Object[].class));
// todo messageSource when sendMessage is true
        assertEquals(true, userService.addUserComment(UserCommentTopicEnum.GENERAL,
                "newComment", "email", true));

        verify(userDao, times(1)).findByEmail("email");
        verify(userDao, times(1)).findByEmail("testemail@gmail.com");
        verify(userDao, times(1)).addUserComment(comment);
    }

    @Ignore
    public void editUserComment() {
        // TODO notifyUser -> messageSource
    }

    @Test
    public void deleteUserComment() {
        when(userDao.deleteUserComment(anyInt())).thenReturn(true);

        assertEquals(true, userService.deleteUserComment(56));

        verify(userDao, times(1)).deleteUserComment(56);
    }

    @Test
    public void getAuthorityOptionsForUser() {
//        AdminAuthorityOption adminAuthorityOption = new AdminAuthorityOption();
//        adminAuthorityOption.setAdminAuthority(AdminAuthority.MANAGE_ACCESS);
//        Set<String> allowedAuthorities = new HashSet<>();
//        allowedAuthorities.add("MANAGE_ACCESS");
//        when(userDao.getAuthorityOptionsForUser(anyInt())).thenReturn(Arrays.asList(adminAuthorityOption));
//        doNothing().when(adminAuthorityOption)
//        when(adminAuthority.toLocalizedString(any(MessageSource.class), any(Locale.class))).thenReturn("str");
//
//        assertEquals(Arrays.asList(adminAuthorityOption),
//                userService.getAuthorityOptionsForUser(5, allowedAuthorities, Locale.ENGLISH));

    }

    @Test
    public void getActiveAuthorityOptionsForUser() {
        AdminAuthorityOption adminAuthorityOption = new AdminAuthorityOption();
        adminAuthorityOption.setEnabled(true);
        when(userDao.getAuthorityOptionsForUser(anyInt())).thenReturn(Arrays.asList(adminAuthorityOption));

        assertEquals(Arrays.asList(adminAuthorityOption),
                userService.getActiveAuthorityOptionsForUser(5));

        verify(userDao, times(1)).getAuthorityOptionsForUser(5);
    }

    @Test(expected = ForbiddenOperationException.class)
    public void updateAdminAuthorities_WhenForbiddenOperationException() {
        AdminAuthorityOption adminAuthorityOption = new AdminAuthorityOption();
        when(userDao.getUserRoles(anyString())).thenReturn(UserRole.USER);
        when(userDao.getUserRoleById(anyInt())).thenReturn(UserRole.ADMINISTRATOR);

        userService.updateAdminAuthorities(Arrays.asList(adminAuthorityOption), 15, "currentUserEmail");
    }

    @Test
    public void updateAdminAuthorities_WhenCurrentUserRoleIsADMINISTRATOR() {
        AdminAuthorityOption adminAuthorityOption = new AdminAuthorityOption();
        when(userDao.getUserRoles(anyString())).thenReturn(UserRole.ADMINISTRATOR);
        when(userDao.getUserRoleById(anyInt())).thenReturn(UserRole.ADMINISTRATOR);
        doNothing().when(userDao).updateAdminAuthorities(anyList(), anyInt());

        userService.updateAdminAuthorities(Arrays.asList(adminAuthorityOption), 15, "currentUserEmail");

        verify(userDao, times(1)).getUserRoles("currentUserEmail");
        verify(userDao, times(1)).getUserRoleById(15);
        verify(userDao, times(1)).updateAdminAuthorities(Arrays.asList(adminAuthorityOption) ,15);
    }

    @Test
    public void updateAdminAuthorities_WhenUpdatedUserRoleIsNotADMINISTRATOR() {
        AdminAuthorityOption adminAuthorityOption = new AdminAuthorityOption();
        when(userDao.getUserRoles(anyString())).thenReturn(UserRole.ADMINISTRATOR);
        when(userDao.getUserRoleById(anyInt())).thenReturn(UserRole.USER);
        doNothing().when(userDao).updateAdminAuthorities(anyList(), anyInt());

        userService.updateAdminAuthorities(Arrays.asList(adminAuthorityOption), 15, "currentUserEmail");

        verify(userDao, times(1)).getUserRoles("currentUserEmail");
        verify(userDao, times(1)).getUserRoleById(15);
        verify(userDao, times(1)).updateAdminAuthorities(Arrays.asList(adminAuthorityOption) ,15);
    }

    @Test
    public void findNicknamesByPart() {
        when(userDao.retrieveNicknameSearchLimit()).thenReturn(18);
        when(userDao.findNicknamesByPart(anyString(), anyInt())).thenReturn(Arrays.asList("str", "str2"));

        assertEquals(Arrays.asList("str", "str2") ,userService.findNicknamesByPart("anyStr"));

        verify(userDao, times(1)).retrieveNicknameSearchLimit();
        verify(userDao, times(1)).findNicknamesByPart("anyStr", 18);
    }

    @Test(expected = AuthenticationNotAvailableException.class)
    public void getUserRoleFromSecurityContext_WhenException() {
        SecurityContextHolder.getContext()
                .setAuthentication(null);

        userService.getUserRoleFromSecurityContext();
    }

    @Test
    public void getUserRoleFromSecurityContext_WhenUserRoleIsFromUSER_ROLES() {
       assertEquals(UserRole.USER, userService.getUserRoleFromSecurityContext());
    }

    @Test
    public void getUserRoleFromSecurityContext_WhenUserRoleIsROLE_DEFAULT_COMMISSION() {
             SecurityContextHolder.getContext()
             .setAuthentication(new AnonymousAuthenticationToken("w1", "testemail@gmail.com",
                                AuthorityUtils.createAuthorityList("d1")));
        assertEquals(UserRole.USER, userService.getUserRoleFromSecurityContext());
    }


    @Test
    public void setCurrencyPermissionsByUserId() {
        UserCurrencyOperationPermissionDto userDto = new UserCurrencyOperationPermissionDto();
        UserCurrencyOperationPermissionDto userDto2 = new UserCurrencyOperationPermissionDto();
        UserCurrencyOperationPermissionDto userDto3 = new UserCurrencyOperationPermissionDto();
        userDto.setUserId(78);
        userDto.setInvoiceOperationPermission(InvoiceOperationPermission.ACCEPT_DECLINE);
        userDto2.setInvoiceOperationPermission(InvoiceOperationPermission.VIEW_ONLY);
        userDto3.setInvoiceOperationPermission(InvoiceOperationPermission.NONE);
        doNothing().when(userDao).setCurrencyPermissionsByUserId(anyInt(), anyList());

        userService.setCurrencyPermissionsByUserId(Arrays.asList(userDto,userDto2,userDto3));

        verify(userDao, times(1)).setCurrencyPermissionsByUserId(78, Arrays.asList(userDto,userDto2));
    }

    @Test
    public void getCurrencyPermissionsByUserIdAndCurrencyIdAndDirection() {
        when(userDao.getCurrencyPermissionsByUserIdAndCurrencyIdAndDirection(anyInt(), anyInt(), any(InvoiceOperationDirection.class)))
                .thenReturn(InvoiceOperationPermission.ACCEPT_DECLINE);

        assertEquals(InvoiceOperationPermission.ACCEPT_DECLINE,
                userService.getCurrencyPermissionsByUserIdAndCurrencyIdAndDirection(45,45, InvoiceOperationDirection.REFILL));

        verify(userDao, times(1)).getCurrencyPermissionsByUserIdAndCurrencyIdAndDirection(45,45, InvoiceOperationDirection.REFILL);
    }

    @Test
    public void getEmailById() {
        when(userDao.getEmailById(anyInt())).thenReturn("test@test.com");

        assertEquals("test@test.com", userService.getEmailById(15));

        verify(userDao, times(1)).getEmailById(15);
    }

    @Test
    public void getUserRoleFromDBByEmail() {
        when(userDao.getUserRoleByEmail(anyString())).thenReturn(UserRole.USER);

        assertEquals(UserRole.USER, userService.getUserRoleFromDB("test@test.com"));

        verify(userDao, times(1)).getUserRoleByEmail("test@test.com");
    }

    @Test
    public void getUserRoleFromDBByUserId() {
        when(userDao.getUserRoleById(anyInt())).thenReturn(UserRole.USER);

        assertEquals(UserRole.USER, userService.getUserRoleFromDB(17));

        verify(userDao, times(1)).getUserRoleById(17);
    }

    @Test
    public void updatePinForUserForEvent() {
        Random random = Mockito.mock(Random.class);
        when(random.nextInt()).thenReturn(5);
//        when(bCryptPasswordEncoder.encode(anyString())).thenReturn("1234123");
        doNothing().when(userDao).updatePinByUserEmail(anyString(), anyString(), any(NotificationMessageEventEnum.class));

       assertEquals(String.valueOf(10000005), userService.updatePinForUserForEvent("test@test.com", NotificationMessageEventEnum.TRANSFER));

        verify(userDao, times(1)).updatePinByUserEmail("test@test.com", "1234123", NotificationMessageEventEnum.TRANSFER);
    }

    @Test
    public void checkPin_WhenGOOGLE2FA() {
        NotificationsUserSetting setting = new  NotificationsUserSetting();
        setting.setNotificatorId(4);
        when(userDao.getIdByEmail(anyString())).thenReturn(59);
        when(settingsService.getByUserAndEvent(anyInt(), any(NotificationMessageEventEnum.class))).thenReturn(setting);
        when(g2faService.checkGoogle2faVerifyCode(anyString(), anyInt())).thenReturn(true);

        assertEquals(true, userService.checkPin("test@test.com","1234123", NotificationMessageEventEnum.TRANSFER));

        verify(userDao, times(1)).getIdByEmail("test@test.com");
        verify(settingsService, times(1)).getByUserAndEvent(59, NotificationMessageEventEnum.TRANSFER);
        verify(g2faService, times(1)).checkGoogle2faVerifyCode("1234123", 59);
    }

    @Test
    public void checkPin_WhenEMAIL() {
        NotificationsUserSetting setting = new  NotificationsUserSetting();
        setting.setNotificatorId(1);
        when(userDao.getIdByEmail(anyString())).thenReturn(59);
        when(settingsService.getByUserAndEvent(anyInt(), any(NotificationMessageEventEnum.class))).thenReturn(setting);
        when(userDao.getPinByEmailAndEvent(anyString(), any(NotificationMessageEventEnum.class))).thenReturn("$2a$10$meKA97t3W5GBAWoBZVd9HeB4ONjt5C6zoMPSuTv5wGWsTTC48us22");

        assertEquals(true, userService.checkPin("test@test.com","123", NotificationMessageEventEnum.TRANSFER));

        verify(userDao, times(1)).getIdByEmail("test@test.com");
        verify(settingsService, times(1)).getByUserAndEvent(59, NotificationMessageEventEnum.TRANSFER);
        verify(userDao, times(1)).getPinByEmailAndEvent("test@test.com", NotificationMessageEventEnum.TRANSFER);
    }

    @Test
    public void checkPin_WhenSettingIsNull() {
        when(userDao.getIdByEmail(anyString())).thenReturn(59);
        when(settingsService.getByUserAndEvent(anyInt(), any(NotificationMessageEventEnum.class))).thenReturn(null);
        when(userDao.getPinByEmailAndEvent(anyString(), any(NotificationMessageEventEnum.class))).thenReturn("$2a$10$meKA97t3W5GBAWoBZVd9HeB4ONjt5C6zoMPSuTv5wGWsTTC48us22");

        assertEquals(true, userService.checkPin("test@test.com","123", NotificationMessageEventEnum.TRANSFER));

        verify(userDao, times(1)).getIdByEmail("test@test.com");
        verify(settingsService, times(1)).getByUserAndEvent(59, NotificationMessageEventEnum.TRANSFER);
        verify(userDao, times(1)).getPinByEmailAndEvent("test@test.com", NotificationMessageEventEnum.TRANSFER);
    }

    @Test
    public void checkPin_WhenNotificatorIdIsNull() {
        NotificationsUserSetting setting = new  NotificationsUserSetting();
        when(userDao.getIdByEmail(anyString())).thenReturn(59);
        when(settingsService.getByUserAndEvent(anyInt(), any(NotificationMessageEventEnum.class))).thenReturn(setting);
        when(userDao.getPinByEmailAndEvent(anyString(), any(NotificationMessageEventEnum.class))).thenReturn("$2a$10$meKA97t3W5GBAWoBZVd9HeB4ONjt5C6zoMPSuTv5wGWsTTC48us22");

        assertEquals(true, userService.checkPin("test@test.com","123", NotificationMessageEventEnum.TRANSFER));

        verify(userDao, times(1)).getIdByEmail("test@test.com");
        verify(settingsService, times(1)).getByUserAndEvent(59, NotificationMessageEventEnum.TRANSFER);
        verify(userDao, times(1)).getPinByEmailAndEvent("test@test.com", NotificationMessageEventEnum.TRANSFER);
    }

    @Test
    public void isLogin2faUsed() {
        when(userDao.getIdByEmail(anyString())).thenReturn(16);
        when(g2faService.isGoogleAuthenticatorEnable(anyInt())).thenReturn(true);

        assertEquals(true, userService.isLogin2faUsed("test@test.com"));

        verify(userDao, times(1)).getIdByEmail("test@test.com");
        verify(g2faService, times(1)).isGoogleAuthenticatorEnable(16);
    }

    @Test
    public void checkIsNotifyUserAbout2fa() {
        when(userDao.updateLast2faNotifyDate(anyString())).thenReturn(true);

        assertEquals(true, userService.checkIsNotifyUserAbout2fa("test@test.com"));

        verify(userDao, times(1)).updateLast2faNotifyDate("test@test.com");
    }

    @Test
    public void getUserIpReportForRoles() {
        UserIpReportDto userIpReportDto = new UserIpReportDto();
        userIpReportDto.setEmail("test@test.com");
        when(userDao.getUserIpReportByRoleList(anyList())).thenReturn(Arrays.asList(userIpReportDto));

        assertEquals(Arrays.asList(userIpReportDto), userService.getUserIpReportForRoles(Arrays.asList(7,9)));

        verify(userDao, times(1)).getUserIpReportByRoleList(Arrays.asList(7,9));
    }

    @Test
    public void getNewRegisteredUserNumber() {
        LocalDateTime start = LocalDateTime.now();
        LocalDateTime finish = LocalDateTime.now();
        when(userDao.getNewRegisteredUserNumber(any(LocalDateTime.class), any(LocalDateTime.class))).thenReturn(77);

        assertEquals(new Integer(77), userService.getNewRegisteredUserNumber(start, finish));

        verify(userDao, times(1)).getNewRegisteredUserNumber(start, finish);
    }

    @Test(expected = AuthenticationNotAvailableException.class)
    public void getUserEmailFromSecurityContext_WhenAuthenticationIsNull() {
        SecurityContextHolder.getContext()
                .setAuthentication(null);

        userService.getUserEmailFromSecurityContext();
    }

    @Test
    public void getUserEmailFromSecurityContext_WhenAuthenticationIsNotNull() {
        SecurityContextHolder.getContext()
                .setAuthentication(new AnonymousAuthenticationToken("w1", "testemail@gmail.com",
                        AuthorityUtils.createAuthorityList("d1")));

        assertEquals("testemail@gmail.com", userService.getUserEmailFromSecurityContext());
    }

    @Test
    public void getTemporalTokenByValue() {
        TemporalToken temporalToken = new TemporalToken();
        temporalToken.setId(56);
        when(userDao.verifyToken(anyString())).thenReturn(temporalToken);

        assertEquals(temporalToken, userService.getTemporalTokenByValue("String Token"));

        verify(userDao, times(1)).verifyToken("String Token");
    }

    @Test
    public void getUserByTemporalToken() {
        User user = new User();
        user.setId(35);
        when(userDao.getUserByTemporalToken(anyString())).thenReturn(user);

        assertEquals(user, userService.getUserByTemporalToken("String Token"));

        verify(userDao, times(1)).getUserByTemporalToken("String Token");
    }

    @Test
    public void checkPassword() {
        when(userDao.getPassword(anyInt())).thenReturn("$2a$10$meKA97t3W5GBAWoBZVd9HeB4ONjt5C6zoMPSuTv5wGWsTTC48us22");

        assertEquals(true, userService.checkPassword(78, "123"));

        verify(userDao, times(1)).getPassword(78);
    }

    @Test
    public void countUserIps() {
        when(userDao.countUserEntrance(anyString())).thenReturn(45L);

        assertEquals(45L, userService.countUserIps("testemail@gmail.com"));

        verify(userDao, times(1)).countUserEntrance("testemail@gmail.com");
    }

    @Test
    public void isGlobal2FaActive() {
        assertEquals(false, userService.isGlobal2FaActive());
    }

    @Test
    public void getUserFavouriteCurrencyPairs_WhenUserIsNotNull() {
        when(userDao.findByEmail(anyString())).thenReturn(user);
        when(userDao.findFavouriteCurrencyPairsById(anyInt())).thenReturn(Arrays.asList(5,6,7));

        assertEquals(Arrays.asList(5,6,7), userService.getUserFavouriteCurrencyPairs("testemail@gmail.com"));

        verify(userDao, times(1)).findByEmail("testemail@gmail.com");
        verify(userDao, times(1)).findFavouriteCurrencyPairsById(5);
    }

    @Test
    public void getUserFavouriteCurrencyPairs_WhenUserNull() {
        when(userDao.findByEmail(anyString())).thenReturn(null);

        assertEquals(Collections.emptyList(), userService.getUserFavouriteCurrencyPairs("testemail@gmail.com"));

        verify(userDao, times(1)).findByEmail("testemail@gmail.com");
    }

    @Test
    public void manageUserFavouriteCurrencyPair() {
        when(userDao.findByEmail(anyString())).thenReturn(user);
        when(userDao.manageUserFavouriteCurrencyPair(anyInt(), anyInt(), anyBoolean())).thenReturn(true);

        assertEquals(true, userService.manageUserFavouriteCurrencyPair("testemail@gmail.com", 88, false));

        verify(userDao, times(1)).findByEmail("testemail@gmail.com");
        verify(userDao, times(1)).manageUserFavouriteCurrencyPair(5, 88, false);
    }

    @Test
    public void manageUserFavouriteCurrencyPair_WhenNull() {
        when(userDao.findByEmail(anyString())).thenReturn(null);

        assertEquals(false, userService.manageUserFavouriteCurrencyPair("testemail@gmail.com", 88, false));

        verify(userDao, times(1)).findByEmail("testemail@gmail.com");
    }

    @Test
    public void deleteTempTokenByValue() {
        when(userDao.deleteTemporalToken(anyString())).thenReturn(true);

        assertEquals(true, userService.deleteTempTokenByValue("token"));

        verify(userDao, times(1)).deleteTemporalToken("token");
    }

    @Test
    public void updateGaTag() {
        when(userDao.updateGaTag(anyString(), anyString())).thenReturn(77);

        userService.updateGaTag("gaCookie","username");

        verify(userDao, times(1)).updateGaTag("gaCookie","username");
    }

    @Test
    public void getReferenceId() {
        when(userDao.getReferenceIdByUserEmail(anyString())).thenReturn("refID");

        assertEquals("refID", userService.getReferenceId());

        verify(userDao, times(1)).getReferenceIdByUserEmail("testemail@gmail.com");
    }

    @Test
    public void updateVerificationStep() {
        when(userDao.updateVerificationStep(anyString())).thenReturn(7);

        assertEquals(7, userService.updateVerificationStep("23testemail@gmail.com"));

        verify(userDao, times(1)).updateVerificationStep("23testemail@gmail.com");
    }

    @Test
    public void getVerificationStep() {
        when(userDao.getVerificationStep(anyString())).thenReturn(2);

        assertEquals(VerificationStep.LEVEL_TWO, userService.getVerificationStep());

        verify(userDao, times(1)).getVerificationStep("testemail@gmail.com");
    }

    @Test
    public void updateReferenceId() {
        when(userDao.updateReferenceId(anyString(), anyString())).thenReturn(16);

        assertEquals(16, userService.updateReferenceId("referenceId"));

        verify(userDao, times(1)).updateReferenceId("referenceId", "testemail@gmail.com");
    }

    @Test
    public void getEmailByReferenceId() {
        when(userDao.getEmailByReferenceId(anyString())).thenReturn("some@email.com");

        assertEquals("some@email.com", userService.getEmailByReferenceId("referenceId"));

        verify(userDao, times(1)).getEmailByReferenceId("referenceId");
    }

    @Test
    public void getCallBackUrlById() {
        assertEquals(null, userService.getCallBackUrlById(5,77));
    }

    @Test
    public void getCallBackUrlByUserAcceptorId() {
        assertEquals(null, userService.getCallBackUrlByUserAcceptorId(5,77));
    }

    @Test
    public void findEmailById() {
        assertEquals(null, userService.findEmailById(5));
    }

    @Test
    public void getUsersInfoFromCache() {
    }

    @Test
    public void getUsersInfoFromDatabase() {

    }

    @Test
    public void blockUserByRequest() {
        User user2 = new User();
        user2.setId(67);
        user2.setUserStatus(UserStatus.DELETED);
        when(userDao.updateUserStatus(any(User.class))).thenReturn(true);
        doNothing().when(userSessionService).invalidateUserSessionExceptSpecific(anyString(), anyString());
        when(userDao.getEmailById(anyInt())).thenReturn("getEmailById");

        userService.blockUserByRequest(67);

        verify(userDao, times(1)).updateUserStatus(any(User.class));
        verify(userSessionService, times(1)).invalidateUserSessionExceptSpecific("getEmailById", null);
        verify(userDao, times(1)).getEmailById(67);
    }

    @Test
    public void updateCallbackURL() {
        CallbackURL callbackUrl = new CallbackURL();
        callbackUrl.setPairId(8);
        when(userSettingsDao.updateCallbackURL(anyInt(), any(CallbackURL.class))).thenReturn(80);

        assertEquals(80, userService.updateCallbackURL(57, callbackUrl));

        verify(userSettingsDao, times(1)).updateCallbackURL(57, callbackUrl);
    }

    @Test
    public void setCallbackURL() {
        CallbackURL callbackUrl = new CallbackURL();
        callbackUrl.setPairId(8);
        when(userSettingsDao.getCallBackURLByUserId(anyInt(), anyInt())).thenReturn("");
        when(userSettingsDao.addCallBackUrl(anyInt(), any(CallbackURL.class))).thenReturn(89);

        assertEquals(89, userService.setCallbackURL(57, callbackUrl));

        verify(userSettingsDao, times(1)).getCallBackURLByUserId(57, 8);
        verify(userSettingsDao, times(1)).addCallBackUrl(57, callbackUrl);
    }

    @Test(expected = CallBackUrlAlreadyExistException.class)
    public void setCallbackURL_WhenException() {
        CallbackURL callbackUrl = new CallbackURL();
        callbackUrl.setPairId(8);
        when(userSettingsDao.getCallBackURLByUserId(anyInt(), anyInt())).thenReturn("Callback");

        userService.setCallbackURL(57, callbackUrl);
    }

    @Test
    public void verifyUserEmailForForgetPassword() {
        TemporalToken temporalToken = new TemporalToken();
        temporalToken.setId(55);
        when(userDao.verifyToken(anyString())).thenReturn(temporalToken);

        assertEquals(temporalToken, userService.verifyUserEmailForForgetPassword("token"));

        verify(userDao, times(1)).verifyToken("token");

    }

    @Test
    public void getUserKycStatusByEmail() {
        when(userDao.getKycStatusByEmail(anyString())).thenReturn("UserKycStatus");

        assertEquals("UserKycStatus", userService.getUserKycStatusByEmail("some@email.com"));

        verify(userDao, times(1)).getKycStatusByEmail("some@email.com");
    }

    @Test
    public void updateKycReferenceByEmail() {
        when(userDao.updateKycReferenceIdByEmail(anyString(), anyString())).thenReturn(true);

        assertEquals(true, userService.updateKycReferenceByEmail("some@email.com","referenceUID"));

        verify(userDao, times(1)).updateKycReferenceIdByEmail("some@email.com","referenceUID");
    }

    @Test(expected = UserNotFoundException.class)
    public void findByKycReferenceId_WhenException() {
        when(userDao.findByKycReferenceId(anyString())).thenReturn(Optional.empty());

        userService.findByKycReferenceId("some@email.com");
    }

    @Test
    public void findByKycReferenceId() {
        when(userDao.findByKycReferenceId(anyString())).thenReturn(Optional.of(user));

        assertEquals(user, userService.findByKycReferenceId("some@email.com"));

        verify(userDao, times(1)).findByKycReferenceId("some@email.com");
    }

    @Test
    public void updateKycStatusByEmail() {
        when(userDao.updateKycStatusByEmail(anyString(), anyString())).thenReturn(true);

        assertEquals(true, userService.updateKycStatusByEmail("some@email.com", "status"));

        verify(userDao, times(1)).updateKycStatusByEmail("some@email.com", "status");
    }

    @Test
    public void getKycReferenceByEmail() {
        when(userDao.findKycReferenceByUserEmail(anyString())).thenReturn("KYC");

        assertEquals("KYC", userService.getKycReferenceByEmail("some@email.com"));

        verify(userDao, times(1)).findKycReferenceByUserEmail("some@email.com");
    }
}