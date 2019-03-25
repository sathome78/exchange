package me.exrates.ngcontroller;

import me.exrates.dao.exception.notfound.UserNotFoundException;
import me.exrates.model.User;
import me.exrates.model.UserEmailDto;
import me.exrates.model.dto.NotificationResultDto;
import me.exrates.model.dto.mobileApiDto.AuthTokenDto;
import me.exrates.model.dto.mobileApiDto.UserAuthenticationDto;
import me.exrates.model.enums.UserStatus;
import me.exrates.model.ngExceptions.NgResponseException;
import me.exrates.model.ngModel.PasswordCreateDto;
import me.exrates.security.ipsecurity.IpBlockingService;
import me.exrates.security.service.AuthTokenService;
import me.exrates.security.service.NgUserService;
import me.exrates.security.service.SecureService;
import me.exrates.service.ReferralService;
import me.exrates.service.UserService;
import me.exrates.service.notifications.G2faService;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Locale;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class NgUserControllerTest extends AngularApiCommonTest {
    private static final String BASE_URL = "/api/public/v2/users";

    @Mock
    private IpBlockingService ipBlockingService;
    @Mock
    private AuthTokenService authTokenService;
    @Mock
    private UserService userService;
    @Mock
    private ReferralService referralService;
    @Mock
    private SecureService secureService;
    @Mock
    private G2faService g2faService;
    @Mock
    private NgUserService ngUserService;
    @Mock
    private UserDetailsService userDetailsService;
    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    NgUserController ngUserController;

    private MockMvc mockMvc;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        mockMvc = MockMvcBuilders
                .standaloneSetup(ngUserController)
                .build();
    }

    @Test
    public void authenticate_pin_is_empty_NgResponseException() {
        UserAuthenticationDto requestBody = new UserAuthenticationDto();
        requestBody.setEmail("test@test.ru");
        requestBody.setPassword("pass");

        //*********** Start authenticateUser() ***************************************************
        User user = new User();
        user.setId(1);
        user.setNickname("TEST_NICKNAME");
        user.setEmail("TEST_EMAIL");
        user.setParentEmail("+380508008000");
        user.setStatus(UserStatus.ACTIVE);
        user.setPassword("TEST_PASSWORD");

        UserDetails userDetails = Mockito.mock(UserDetails.class);
        when(userDetails.getPassword()).thenReturn("TEST_PASSWORD");

        when(userService.findByEmail(anyString())).thenReturn(user);
        doNothing().when(userService).updateGaTag(anyString(), anyString());
        when(userDetailsService.loadUserByUsername(anyString())).thenReturn(userDetails);
        when(passwordEncoder.matches(anyString(), anyString())).thenReturn(Boolean.TRUE);
        //********** End authenticateUser() *****************************************************

        when(g2faService.isGoogleAuthenticatorEnable(anyInt())).thenReturn(Boolean.TRUE);

        try {
            mockMvc.perform(post(BASE_URL + "/authenticate")
                    .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE)
                    .content(objectMapper.writeValueAsString(requestBody)));
            Assert.fail();
        } catch (Exception e) {
            String expected = "User with email: test@test.ru must login with GOOGLE authorization code";
            assertEquals(expected, e.getCause().getMessage());
        }

        verify(userService, times(1)).findByEmail(anyString());
        verify(userService, times(1)).updateGaTag(anyString(), anyString());
        verify(userDetailsService, times(1)).loadUserByUsername(anyString());
        verify(passwordEncoder, times(1)).matches(anyString(), anyString());
    }

    @Test
    public void authenticate_shouldLoginWithGoogle_is_true_NgResponseException() {
        UserAuthenticationDto dto = new UserAuthenticationDto();
        dto.setEmail("test@test.ru");
        dto.setPassword("pass");
        dto.setPin("TEST_PIN");

        //*********** Start authenticateUser() ***************************************************
        User user = new User();
        user.setId(1);
        user.setNickname("TEST_NICKNAME");
        user.setEmail("TEST_EMAIL");
        user.setParentEmail("+380508008000");
        user.setStatus(UserStatus.ACTIVE);
        user.setPassword("TEST_PASSWORD");

        UserDetails userDetails = Mockito.mock(UserDetails.class);

        when(userService.findByEmail(anyString())).thenReturn(user);
        doNothing().when(userService).updateGaTag(anyString(), anyString());
        when(userDetailsService.loadUserByUsername(anyString())).thenReturn(userDetails);
        when(userDetails.getPassword()).thenReturn("TEST_PASSWORD");
        when(passwordEncoder.matches(anyString(), anyString())).thenReturn(Boolean.TRUE);
        //********** End authenticateUser() *****************************************************

        when(g2faService.isGoogleAuthenticatorEnable(anyInt())).thenReturn(Boolean.TRUE);
        when(g2faService.checkGoogle2faVerifyCode(anyString(), anyInt())).thenReturn(Boolean.FALSE);

        try {
            mockMvc.perform(post(BASE_URL + "/authenticate")
                    .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE)
                    .content(objectMapper.writeValueAsString(dto)));
            Assert.fail();
        } catch (Exception e) {
            String expected = "Invalid google auth code from user test@test.ru";
            assertEquals(expected, e.getCause().getMessage());
        }

        verify(userService, times(1)).findByEmail(anyString());
        verify(userService, times(1)).updateGaTag(anyString(), anyString());
        verify(userDetailsService, times(1)).loadUserByUsername(anyString());
        verify(passwordEncoder, times(1)).matches(anyString(), anyString());
        verify(g2faService, times(1)).isGoogleAuthenticatorEnable(anyInt());
        verify(g2faService, times(1)).checkGoogle2faVerifyCode(anyString(), anyInt());
    }

    @Test
    public void authenticate_shouldLoginWithGoogle_is_false_NgResponseException() {
        UserAuthenticationDto dto = new UserAuthenticationDto();
        dto.setEmail("test@test.ru");
        dto.setPassword("pass");
        dto.setPin("TEST_PIN");
        dto.setTries(9);

        //*********** Start authenticateUser() ***************************************************
        User user = new User();
        user.setId(1);
        user.setNickname("TEST_NICKNAME");
        user.setEmail("TEST_EMAIL");
        user.setParentEmail("+380508008000");
        user.setStatus(UserStatus.ACTIVE);
        user.setPassword("TEST_PASSWORD");

        UserDetails userDetails = Mockito.mock(UserDetails.class);

        when(userService.findByEmail(anyString())).thenReturn(user);
        doNothing().when(userService).updateGaTag(anyString(), anyString());

        when(userDetailsService.loadUserByUsername(anyString())).thenReturn(userDetails);
        when(userDetails.getPassword()).thenReturn("TEST_PASSWORD");
        when(passwordEncoder.matches(anyString(), anyString())).thenReturn(Boolean.TRUE);
        //********** End authenticateUser() *****************************************************

        String[] arguments = new String[]{"A", "B", "C"};

        when(g2faService.isGoogleAuthenticatorEnable(anyInt())).thenReturn(Boolean.FALSE);
        when(userService.checkPin(anyString(), anyString(), anyObject())).thenReturn(Boolean.FALSE);
        when(secureService.sendLoginPincode(anyObject(), anyObject(), anyString()))
                .thenReturn(new NotificationResultDto("TEST_MSG_SOURCE", arguments));

        try {
            mockMvc.perform(post(BASE_URL + "/authenticate")
                    .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE)
                    .content(objectMapper.writeValueAsString(dto)));
            Assert.fail();
        } catch (Exception e) {
            String expected = "Invalid email auth code from user test@test.ru";
            assertEquals(expected, e.getCause().getMessage());
        }

        verify(userService, times(1)).findByEmail(anyString());
        verify(userService, times(1)).updateGaTag(anyString(), anyString());
        verify(userDetailsService, times(1)).loadUserByUsername(anyString());
        verify(passwordEncoder, times(1)).matches(anyString(), anyString());
        verify(g2faService, times(1)).isGoogleAuthenticatorEnable(anyInt());
        verify(userService, times(1)).checkPin(anyString(), anyString(), anyObject());
    }

    @Test
    public void authenticate_isOk() throws Exception {
        UserAuthenticationDto requestBody = new UserAuthenticationDto();
        requestBody.setEmail("test@test.ru");
        requestBody.setPassword("pass");
        requestBody.setPin("TEST_PIN");
        requestBody.setTries(9);

        //*********** Start authenticateUser() ***************************************************
        User user = new User();
        user.setId(1);
        user.setNickname("TEST_NICKNAME");
        user.setEmail("TEST_EMAIL");
        user.setParentEmail("+380508008000");
        user.setStatus(UserStatus.ACTIVE);
        user.setPassword("TEST_PASSWORD");

        UserDetails userDetails = Mockito.mock(UserDetails.class);

        when(userService.findByEmail(anyString())).thenReturn(user);
        doNothing().when(userService).updateGaTag(anyString(), anyString());

        when(userDetailsService.loadUserByUsername(anyString())).thenReturn(userDetails);
        when(userDetails.getPassword()).thenReturn("TEST_PASSWORD");
        when(passwordEncoder.matches(anyString(), anyString())).thenReturn(Boolean.TRUE);
        //********** End authenticateUser() *****************************************************

        when(g2faService.isGoogleAuthenticatorEnable(anyInt())).thenReturn(Boolean.FALSE);
        when(userService.checkPin(anyString(), anyString(), anyObject())).thenReturn(Boolean.TRUE);

        //*********** Start createToken() ********************************************************
        AuthTokenDto tokenDto = new AuthTokenDto();
        tokenDto.setToken("TEST_TOKEN");

        when(authTokenService.retrieveTokenNg((UserAuthenticationDto) anyObject()))
                .thenReturn(java.util.Optional.of(tokenDto));
        when(userService.getPreferedLang(anyInt())).thenReturn("USA");
        when(userService.getAvatarPath(anyInt())).thenReturn("TEST_AVATAR_LOGICAL_PATH");
        when(referralService.generateReferral(anyString())).thenReturn("TEST_REFERRAL_REFERENCE");
        //********* End createToken() ************************************************************

        mockMvc.perform(post(BASE_URL + "/authenticate")
                .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE)
                .content(objectMapper.writeValueAsString(requestBody)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token", is("TEST_TOKEN")))
                .andExpect(jsonPath("$.nickname", is("TEST_NICKNAME")))
                .andExpect(jsonPath("$.avatarPath", is("http://localhost:80/restTEST_AVATAR_LOGICAL_PATH")))
                .andExpect(jsonPath("$.finPasswordSet", is(Boolean.FALSE)))
                .andExpect(jsonPath("$.referralReference", is("TEST_REFERRAL_REFERENCE")))
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.language", is("usa")));

        verify(g2faService, times(1)).isGoogleAuthenticatorEnable(anyInt());
        verify(userService, times(1)).findByEmail(anyString());
        verify(userService, times(1)).updateGaTag(anyString(), anyString());
        verify(userDetailsService, times(1)).loadUserByUsername(anyString());
        verify(passwordEncoder, times(1)).matches(anyString(), anyString());
        verify(userService, times(1)).checkPin(anyString(), anyString(), anyObject());
        verify(authTokenService, times(1)).retrieveTokenNg((UserAuthenticationDto) anyObject());
        verify(userService, times(1)).getPreferedLang(anyInt());
        verify(userService, times(1)).getAvatarPath(anyInt());
        verify(referralService, times(1)).generateReferral(anyString());
    }

    @Test
    public void authenticate_authenticateUser_NgResponseException_authenticationDto_email_isBlank() throws Exception {
        UserAuthenticationDto requestBody = new UserAuthenticationDto();
        requestBody.setEmail("");
        requestBody.setPassword("pass");
        requestBody.setPin("TEST_PIN");
        requestBody.setTries(9);

        try {
            mockMvc.perform(post(BASE_URL + "/authenticate")
                    .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE)
                    .content(objectMapper.writeValueAsString(requestBody)))
                    .andExpect(status().isBadRequest());
            Assert.fail();
        } catch (Exception e) {
            String expected = "User with email: [] and/or password: [pass] not found";
            assertEquals(expected, e.getCause().getMessage());
        }
    }

    @Test
    public void authenticate_authenticateUser_userService_throw_NgResponseException() {
        UserAuthenticationDto requestBody = new UserAuthenticationDto();
        requestBody.setEmail("test@test.ru");
        requestBody.setPassword("pass");
        requestBody.setPin("TEST_PIN");
        requestBody.setTries(9);

        when(userService.findByEmail(anyString())).thenThrow(UserNotFoundException.class);

        try {
            mockMvc.perform(post(BASE_URL + "/authenticate")
                    .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE)
                    .content(objectMapper.writeValueAsString(requestBody)))
                    .andExpect(status().isBadRequest());
            Assert.fail();
        } catch (Exception e) {
            String expected = "User with email test@test.ru not found";
            assertEquals(expected, e.getCause().getMessage());
        }

        verify(userService, times(1)).findByEmail(anyString());
    }

    @Test
    public void authenticate_authenticateUser_UserStatus_equals_REGISTERED() {
        UserAuthenticationDto requestBody = new UserAuthenticationDto();
        requestBody.setEmail("test@test.ru");
        requestBody.setPassword("pass");
        requestBody.setPin("TEST_PIN");
        requestBody.setTries(9);

        User user = new User();
        user.setId(1);
        user.setNickname("TEST_NICKNAME");
        user.setEmail("TEST_EMAIL");
        user.setParentEmail("+380508008000");
        user.setStatus(UserStatus.REGISTERED);
        user.setPassword("TEST_PASSWORD");

        when(userService.findByEmail(anyString())).thenReturn(user);
        doNothing().when(userService).updateGaTag(anyString(), anyString());
        doNothing().when(ngUserService).resendEmailForFinishRegistration(anyObject());

        try {
            mockMvc.perform(post(BASE_URL + "/authenticate")
                    .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE)
                    .content(objectMapper.writeValueAsString(requestBody)))
                    .andExpect(status().isBadRequest());
            Assert.fail();
        } catch (Exception e) {
            String expected = "User with email test@test.ru registration is not complete";
            assertEquals(expected, e.getCause().getMessage());
        }

        verify(userService, times(1)).findByEmail(anyString());
        verify(userService, times(1)).updateGaTag(anyString(), anyString());
        verify(ngUserService, times(1)).resendEmailForFinishRegistration(anyObject());
    }

    @Test
    public void authenticate_authenticateUser_UserStatus_equals_DELETED() {
        UserAuthenticationDto requestBody = new UserAuthenticationDto();
        requestBody.setEmail("test@test.ru");
        requestBody.setPassword("pass");
        requestBody.setPin("TEST_PIN");
        requestBody.setTries(9);

        User user = new User();
        user.setId(1);
        user.setNickname("TEST_NICKNAME");
        user.setEmail("TEST_EMAIL");
        user.setParentEmail("+380508008000");
        user.setStatus(UserStatus.DELETED);
        user.setPassword("TEST_PASSWORD");

        when(userService.findByEmail(anyString())).thenReturn(user);
        doNothing().when(userService).updateGaTag(anyString(), anyString());

        try {
            mockMvc.perform(post(BASE_URL + "/authenticate")
                    .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE)
                    .content(objectMapper.writeValueAsString(requestBody)))
                    .andExpect(status().isBadRequest());
            Assert.fail();
        } catch (Exception e) {
            String expected = "User with email test@test.ru is not active";
            assertEquals(expected, e.getCause().getMessage());
        }

        verify(userService, times(1)).findByEmail(anyString());
        verify(userService, times(1)).updateGaTag(anyString(), anyString());
    }

    @Test
    public void authenticate_authenticateUser_passwordEncoder_return_false() {
        UserAuthenticationDto requestBody = new UserAuthenticationDto();
        requestBody.setEmail("test@test.ru");
        requestBody.setPassword("pass");
        requestBody.setPin("TEST_PIN");
        requestBody.setTries(9);

        User user = new User();
        user.setId(1);
        user.setNickname("TEST_NICKNAME");
        user.setEmail("TEST_EMAIL");
        user.setParentEmail("+380508008000");
        user.setStatus(UserStatus.ACTIVE);
        user.setPassword("TEST_PASSWORD");

        UserDetails userDetails = Mockito.mock(UserDetails.class);

        when(userService.findByEmail(anyString())).thenReturn(user);
        doNothing().when(userService).updateGaTag(anyString(), anyString());
        when(userDetailsService.loadUserByUsername(anyString())).thenReturn(userDetails);
        when(userDetails.getPassword()).thenReturn("TEST_PASSWORD");
        when(passwordEncoder.matches(anyString(), anyString())).thenReturn(Boolean.FALSE);

        try {
            mockMvc.perform(post(BASE_URL + "/authenticate")
                    .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE)
                    .content(objectMapper.writeValueAsString(requestBody)))
                    .andExpect(status().isBadRequest());
            Assert.fail();
        } catch (Exception e) {
            String expected = "Invalid password and/or email [test@test.ru]";
            assertEquals(expected, e.getCause().getMessage());
        }

        verify(userService, times(1)).findByEmail(anyString());
        verify(userService, times(1)).updateGaTag(anyString(), anyString());
        verify(userDetailsService, times(1)).loadUserByUsername(anyString());
        verify(passwordEncoder, times(1)).matches(anyString(), anyString());
    }

    @Test
    public void authenticate_createToken_throw_NgResponseException() {
        UserAuthenticationDto requestBody = new UserAuthenticationDto();
        requestBody.setEmail("test@test.ru");
        requestBody.setPassword("pass");
        requestBody.setPin("TEST_PIN");
        requestBody.setTries(9);

        //*********** Start authenticateUser() ***************************************************
        User user = new User();
        user.setId(1);
        user.setNickname("TEST_NICKNAME");
        user.setEmail("TEST_EMAIL");
        user.setParentEmail("+380508008000");
        user.setStatus(UserStatus.ACTIVE);
        user.setPassword("TEST_PASSWORD");

        UserDetails userDetails = Mockito.mock(UserDetails.class);

        when(userService.findByEmail(anyString())).thenReturn(user);
        doNothing().when(userService).updateGaTag(anyString(), anyString());

        when(userDetailsService.loadUserByUsername(anyString())).thenReturn(userDetails);
        when(userDetails.getPassword()).thenReturn("TEST_PASSWORD");
        when(passwordEncoder.matches(anyString(), anyString())).thenReturn(Boolean.TRUE);
        //********** End authenticateUser() *****************************************************

        when(g2faService.isGoogleAuthenticatorEnable(anyInt())).thenReturn(Boolean.FALSE);
        when(userService.checkPin(anyString(), anyString(), anyObject())).thenReturn(Boolean.TRUE);

        //*********** Start createToken() ********************************************************
        AuthTokenDto tokenDto = new AuthTokenDto();
        tokenDto.setToken("TEST_TOKEN");

        when(authTokenService.retrieveTokenNg((UserAuthenticationDto) anyObject())).thenThrow(NgResponseException.class);
        //********* End createToken() ************************************************************

        try {
            mockMvc.perform(post(BASE_URL + "/authenticate")
                    .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE)
                    .content(objectMapper.writeValueAsString(requestBody)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.token", is("TEST_TOKEN")))
                    .andExpect(jsonPath("$.nickname", is("TEST_NICKNAME")))
                    .andExpect(jsonPath("$.avatarPath", is("http://localhost:80/restTEST_AVATAR_LOGICAL_PATH")))
                    .andExpect(jsonPath("$.finPasswordSet", is(Boolean.FALSE)))
                    .andExpect(jsonPath("$.referralReference", is("TEST_REFERRAL_REFERENCE")))
                    .andExpect(jsonPath("$.id", is(1)))
                    .andExpect(jsonPath("$.language", is("usa")));
            Assert.fail();
        } catch (Exception e) {
            String expected = null;
            assertEquals(expected, e.getCause().getMessage());
        }

        verify(g2faService, times(1)).isGoogleAuthenticatorEnable(anyInt());
        verify(userService, times(1)).findByEmail(anyString());
        verify(userService, times(1)).updateGaTag(anyString(), anyString());
        verify(userDetailsService, times(1)).loadUserByUsername(anyString());
        verify(passwordEncoder, times(1)).matches(anyString(), anyString());
        verify(userService, times(1)).checkPin(anyString(), anyString(), anyObject());
        verify(authTokenService, times(1)).retrieveTokenNg((UserAuthenticationDto) anyObject());
    }

    @Test
    public void register_isOk_registerUser_equals_true() throws Exception {
        UserEmailDto userEmailDto = new UserEmailDto();
        userEmailDto.setEmail("testemail@gmail.com");
        userEmailDto.setParentEmail("testparentemail@gmail.com");

        when(ngUserService.registerUser(anyObject(), anyObject())).thenReturn(Boolean.TRUE);
        doNothing().when(ipBlockingService).successfulProcessing(anyString(), anyObject());

        mockMvc.perform(post(BASE_URL + "/register")
                .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE)
                .content(objectMapper.writeValueAsString(userEmailDto)))
                .andExpect(status().isOk());

        verify(ngUserService, times(1)).registerUser(anyObject(), anyObject());
    }

    @Test
    public void register_isOk_registerUser_equals_false() throws Exception {
        UserEmailDto userEmailDto = new UserEmailDto();
        userEmailDto.setEmail("testemail@gmail.com");
        userEmailDto.setParentEmail("testparentemail@gmail.com");

        when(ngUserService.registerUser(anyObject(), anyObject())).thenReturn(Boolean.FALSE);
        doNothing().when(ipBlockingService).failureProcessing(anyString(), anyObject());

        mockMvc.perform(post(BASE_URL + "/register")
                .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE)
                .content(objectMapper.writeValueAsString(userEmailDto)))
                .andExpect(status().isBadRequest());

        verify(ngUserService, times(1)).registerUser(anyObject(), anyObject());
    }

    @Test
    public void savePassword_isOk() throws Exception {
        PasswordCreateDto passwordCreateDto = new PasswordCreateDto();
        passwordCreateDto.setTempToken("TEST_TEMP_TOKEN");
        passwordCreateDto.setPassword("TEST_PASSWORD");

        AuthTokenDto tokenDto = new AuthTokenDto();
        tokenDto.setToken("TEST_TOKEN");
        tokenDto.setNickname("TEST_NICKNAME");
        tokenDto.setFinPasswordSet(Boolean.TRUE);
        tokenDto.setReferralReference("TEST_REFERRAL_REFERENCE");
        tokenDto.setUserId(100);
        tokenDto.setLocale(Locale.ENGLISH);

        when(ngUserService.createPassword(anyObject(), anyObject())).thenReturn(tokenDto);

        mockMvc.perform(post(BASE_URL + "/password/create")
                .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE)
                .content(objectMapper.writeValueAsString(passwordCreateDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token", is("TEST_TOKEN")))
                .andExpect(jsonPath("$.nickname", is("TEST_NICKNAME")))
                .andExpect(jsonPath("$.finPasswordSet", is(Boolean.TRUE)))
                .andExpect(jsonPath("$.referralReference", is("TEST_REFERRAL_REFERENCE")))
                .andExpect(jsonPath("$.id", is(100)))
                .andExpect(jsonPath("$.language", is("en")));

        verify(ngUserService, times(1)).createPassword(anyObject(), anyObject());
    }

    @Test
    public void requestForRecoveryPassword_bad_request() throws Exception {
        UserEmailDto userEmailDto = new UserEmailDto();
        userEmailDto.setEmail("testemail@gmail.com");
        userEmailDto.setParentEmail("testparentemail@gmail.com");

        when(ngUserService.recoveryPassword(anyObject(), anyObject())).thenReturn(Boolean.FALSE);
        doNothing().when(ipBlockingService).failureProcessing(anyString(), anyObject());
        doNothing().when(ipBlockingService).successfulProcessing(anyString(), anyObject());

        mockMvc.perform(post(BASE_URL + "/password/recovery/reset")
                .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE)
                .content(objectMapper.writeValueAsString(userEmailDto)))
                .andExpect(status().isBadRequest());

        verify(ngUserService, times(1)).recoveryPassword(anyObject(), anyObject());
    }

    @Test
    public void requestForRecoveryPassword_isOk() throws Exception {
        UserEmailDto userEmailDto = new UserEmailDto();
        userEmailDto.setEmail("testemail@gmail.com");
        userEmailDto.setParentEmail("testparentemail@gmail.com");

        when(ngUserService.recoveryPassword(anyObject(), anyObject())).thenReturn(Boolean.TRUE);
        doNothing().when(ipBlockingService).successfulProcessing(anyString(), anyObject());

        mockMvc.perform(post(BASE_URL + "/password/recovery/reset")
                .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE)
                .content(objectMapper.writeValueAsString(userEmailDto)))
                .andExpect(status().isOk());

        verify(ngUserService, times(1)).recoveryPassword(anyObject(), anyObject());
    }

    @Test
    public void createRecoveryPassword_bad_request() throws Exception {
        PasswordCreateDto passwordCreateDto = new PasswordCreateDto();
        passwordCreateDto.setTempToken("TEST_TEMP_TOKEN");
        passwordCreateDto.setPassword("TEST_PASSWORD");

        when(ngUserService.createPasswordRecovery(anyObject(), anyObject())).thenReturn(Boolean.FALSE);
        doNothing().when(ipBlockingService).failureProcessing(anyString(), anyObject());
        doNothing().when(ipBlockingService).successfulProcessing(anyString(), anyObject());

        mockMvc.perform(post(BASE_URL + "/password/recovery/create")
                .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE)
                .content(objectMapper.writeValueAsString(passwordCreateDto)))
                .andExpect(status().isBadRequest());

        verify(ngUserService, times(1)).createPasswordRecovery(anyObject(), anyObject());
    }

    @Test
    public void createRecoveryPassword_isOK() throws Exception {
        PasswordCreateDto passwordCreateDto = new PasswordCreateDto();
        passwordCreateDto.setTempToken("TEST_TEMP_TOKEN");
        passwordCreateDto.setPassword("TEST_PASSWORD");

        when(ngUserService.createPasswordRecovery(anyObject(), anyObject())).thenReturn(Boolean.TRUE);
        doNothing().when(ipBlockingService).successfulProcessing(anyString(), anyObject());

        mockMvc.perform(post(BASE_URL + "/password/recovery/create")
                .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE)
                .content(objectMapper.writeValueAsString(passwordCreateDto)))
                .andExpect(status().isOk());

        verify(ngUserService, times(1)).createPasswordRecovery(anyObject(), anyObject());
    }

    @Test
    public void checkTempToken_isOk_validateTempToken_true() throws Exception {
        when(ngUserService.validateTempToken(anyString())).thenReturn(Boolean.TRUE);

        mockMvc.perform(get(BASE_URL + "/validateTempToken/{token}", "TEST_TOKEN")
                .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data", is(Boolean.TRUE)))
                .andExpect(jsonPath("$.error", is(nullValue())));

        verify(ngUserService, times(1)).validateTempToken(anyString());
    }

    @Test
    public void checkTempToken_isOk_validateTempToken_false() throws Exception {
        when(ngUserService.validateTempToken(anyString())).thenReturn(Boolean.FALSE);

        mockMvc.perform(get(BASE_URL + "/validateTempToken/{token}", "TEST_TOKEN")
                .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data", is(Boolean.FALSE)))
                .andExpect(jsonPath("$.error", is(nullValue())));

        verify(ngUserService, times(1)).validateTempToken(anyString());
    }
}