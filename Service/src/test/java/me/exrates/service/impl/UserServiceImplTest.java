package me.exrates.service.impl;

import me.exrates.dao.ReferralUserGraphDao;
import me.exrates.dao.UserDao;
import me.exrates.model.TemporalToken;
import me.exrates.model.User;
import me.exrates.model.UserFile;
import me.exrates.model.dto.UpdateUserDto;
import me.exrates.model.dto.UserIpDto;
import me.exrates.model.dto.UserSessionInfoDto;
import me.exrates.model.dto.mobileApiDto.TemporaryPasswordDto;
import me.exrates.model.enums.TokenType;
import me.exrates.model.enums.UserRole;
import me.exrates.model.enums.UserStatus;
import me.exrates.service.NotificationService;
import me.exrates.service.ReferralService;
import me.exrates.service.SendMailService;
import me.exrates.service.UserService;
import me.exrates.service.UserSettingService;
import me.exrates.service.api.ExchangeApi;
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
import org.mockito.MockitoAnnotations;
import org.quartz.JobKey;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.servlet.http.HttpServletRequest;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
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

    private List<String> LOCALES_LIST;
    private User user;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);

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
    public void getUserComments() {

    }

    @Test
    public void addUserComment() {
    }

    @Test
    public void editUserComment() {
    }

    @Test
    public void deleteUserComment() {
    }

    @Test
    public void getAuthorityOptionsForUser() {
    }

    @Test
    public void getActiveAuthorityOptionsForUser() {
    }

    @Test
    public void updateAdminAuthorities() {
    }

    @Test
    public void findNicknamesByPart() {
    }

    @Test
    public void getUserRoleFromSecurityContext() {
    }

    @Test
    public void setCurrencyPermissionsByUserId() {
    }

    @Test
    public void getCurrencyPermissionsByUserIdAndCurrencyIdAndDirection() {
    }

    @Test
    public void getEmailById() {
    }

    @Test
    public void getUserRoleFromDB() {
    }

    @Test
    public void getUserRoleFromDB1() {
    }

    @Test
    public void updatePinForUserForEvent() {
    }

    @Test
    public void checkPin() {
    }

    @Test
    public void isLogin2faUsed() {
    }

    @Test
    public void checkIsNotifyUserAbout2fa() {
    }

    @Test
    public void getUserIpReportForRoles() {
    }

    @Test
    public void getNewRegisteredUserNumber() {
    }

    @Test
    public void getUserEmailFromSecurityContext() {
    }

    @Test
    public void getTemporalTokenByValue() {
    }

    @Test
    public void getUserByTemporalToken() {
    }

    @Test
    public void checkPassword() {
    }

    @Test
    public void countUserIps() {
    }

    @Test
    public void isGlobal2FaActive() {
    }

    @Test
    public void getUserFavouriteCurrencyPairs() {
    }

    @Test
    public void manageUserFavouriteCurrencyPair() {
    }

    @Test
    public void deleteTempTokenByValue() {
    }

    @Test
    public void updateGaTag() {
    }

    @Test
    public void getReferenceId() {
    }

    @Test
    public void updateVerificationStep() {
    }

    @Test
    public void getVerificationStep() {
    }

    @Test
    public void updateReferenceId() {
    }

    @Test
    public void getEmailByReferenceId() {
    }

    @Test
    public void getCallBackUrlById() {
    }

    @Test
    public void getCallBackUrlByUserAcceptorId() {
    }

    @Test
    public void findEmailById() {
    }

    @Test
    public void getUsersInfoFromCache() {
    }

    @Test
    public void getUsersInfoFromDatabase() {
    }

    @Test
    public void blockUserByRequest() {
    }

    @Test
    public void updateCallbackURL() {
    }

    @Test
    public void setCallbackURL() {
    }

    @Test
    public void verifyUserEmailForForgetPassword() {
    }

    @Test
    public void getUserKycStatusByEmail() {
    }

    @Test
    public void updateKycReferenceByEmail() {
    }

    @Test
    public void findByKycReferenceId() {
    }

    @Test
    public void updateKycStatusByEmail() {
    }

    @Test
    public void getKycReferenceByEmail() {
    }
}