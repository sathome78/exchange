package me.exrates.ngcontroller;

import me.exrates.model.NotificationOption;
import me.exrates.model.SessionParams;
import me.exrates.model.User;
import me.exrates.model.dto.PageLayoutSettingsDto;
import me.exrates.model.dto.UpdateUserDto;
import me.exrates.model.enums.ColorScheme;
import me.exrates.model.enums.NotificationEvent;
import me.exrates.model.ngModel.UserDocVerificationDto;
import me.exrates.model.ngModel.UserInfoVerificationDto;
import me.exrates.model.ngModel.enums.VerificationDocumentType;
import me.exrates.ngService.UserVerificationService;
import me.exrates.security.ipsecurity.IpBlockingService;
import me.exrates.security.ipsecurity.IpTypesOfChecking;
import me.exrates.service.NotificationService;
import me.exrates.service.PageLayoutSettingsService;
import me.exrates.service.SessionParamsService;
import me.exrates.service.UserService;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import static org.hamcrest.Matchers.hasKey;
import static org.hamcrest.Matchers.hasValue;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyBoolean;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyListOf;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class NgUserSettingsControllerTest extends AngularApiCommonTest {
    private static final String BASE_URL = "/api/private/v2/settings";
    private static final String NICKNAME = "/nickname";
    private static final String SESSION_INTERVAL = "/sessionInterval";
    private static final String EMAIL_NOTIFICATION = "/notifications";
    private static final String COLOR_SCHEME = "/color-schema";
    private static final String IS_COLOR_BLIND = "/isLowColorEnabled";
    private static final String STATE = "/STATE";

    @Mock
    private UserService userService;
    @Mock
    private NotificationService notificationService;
    @Mock
    private SessionParamsService sessionService;
    @Mock
    private PageLayoutSettingsService layoutSettingsService;
    @Mock
    private UserVerificationService verificationService;
    @Mock
    private IpBlockingService ipBlockingService;

    @InjectMocks
    NgUserSettingsController ngUserSettingsController;

    private MockMvc mockMvc;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        mockMvc = MockMvcBuilders
                .standaloneSetup(ngUserSettingsController)
                .build();

        SecurityContextHolder.getContext()
                .setAuthentication(new AnonymousAuthenticationToken("guest", "testemail@gmail.com",
                        AuthorityUtils.createAuthorityList("ADMIN")));
    }

    @Test
    public void updateMainPassword_bad_request_password_is_blank() throws Exception {
        Map<String, String> requestBody = new HashMap<>();
        requestBody.put("currentPassword", "");
        requestBody.put("newPassword", "");

        String detailMsg = "Failed as current password: [] or new password is [] is empty ";

        when(userService.findByEmail(anyString())).thenReturn(getMockUser());
        when(userService.getUserLocaleForMobile(anyString())).thenReturn(Locale.ENGLISH);

        mockMvc.perform(put(BASE_URL + "/updateMainPassword")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestBody)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.url", is("http://localhost/api/private/v2/settings/updateMainPassword")))
                .andExpect(jsonPath("$.cause", is("NgDashboardException")))
                .andExpect(jsonPath("$.detail", is(detailMsg)))
                .andExpect(jsonPath("$.title", is(nullValue())))
                .andExpect(jsonPath("$.uuid", is(nullValue())))
                .andExpect(jsonPath("$.code", is(1011)));

        verify(userService, times(1)).findByEmail(anyString());
        verify(userService, times(1)).getUserLocaleForMobile(anyString());
    }

    // TODO: Problem with mock static methods in class RestApiUtils.class!!!
    // @PrepareForTest(RestApiUtils.class)
    @Ignore
    public void updateMainPassword_bad_request() throws Exception {
        Map<String, String> requestBody = new HashMap<>();
        requestBody.put("currentPassword", "inapplicable");
        requestBody.put("newPassword", "TEST_NEW_PASSWORD");

        String detailMsg = "Failed to check password for user: TEST_EMAIL from ip:  ";

//        RestApiUtils restApiUtils = Mockito.mock(RestApiUtils.class);
//        PowerMockito.mockStatic(RestApiUtils.class);

        when(userService.findByEmail(anyString())).thenReturn(getMockUser());
        when(userService.getUserLocaleForMobile(anyString())).thenReturn(Locale.ENGLISH);
        when(userService.checkPassword(anyInt(), anyString())).thenReturn(Boolean.FALSE);
        doNothing().when(ipBlockingService).failureProcessing(anyString(), anyObject());

        mockMvc.perform(put(BASE_URL + "/updateMainPassword")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestBody)))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.url", is("http://localhost/api/private/v2/settings/updateMainPassword")))
                .andExpect(jsonPath("$.cause", is("NgDashboardException")))
                .andExpect(jsonPath("$.detail", is(detailMsg)))
                .andExpect(jsonPath("$.title", is(nullValue())))
                .andExpect(jsonPath("$.uuid", is(nullValue())))
                .andExpect(jsonPath("$.code", is(1010)));

        verify(userService, times(1)).findByEmail(anyString());
        verify(userService, times(1)).getUserLocaleForMobile(anyString());
        verify(userService, times(1)).checkPassword(anyInt(), anyString());
        verify(ipBlockingService, times(1)).failureProcessing(anyString(), anyObject());
    }

    // TODO: Problem with mock static methods in class RestApiUtils.class!!!
    // @PrepareForTest(RestApiUtils.class)
    @Ignore
    public void updateMainPassword_isOk() throws Exception {
        Map<String, String> requestBody = new HashMap<>();
        requestBody.put("currentPassword", "inapplicable");
        requestBody.put("newPassword", "inapplicable");

        String detailMsg = "Failed to check password for user: TEST_EMAIL from ip:  ";

//        RestApiUtils restApiUtils = Mockito.mock(RestApiUtils.class);
//        PowerMockito.mockStatic(RestApiUtils.class);

        when(userService.findByEmail(anyString())).thenReturn(getMockUser());
        when(userService.getUserLocaleForMobile(anyString())).thenReturn(Locale.ENGLISH);
        when(userService.checkPassword(anyInt(), anyString())).thenReturn(Boolean.TRUE);
        when(userService.update(any(UpdateUserDto.class), any(Locale.class))).thenReturn(Boolean.TRUE);
        doNothing().when(ipBlockingService).successfulProcessing(anyString(), any(IpTypesOfChecking.class));

        mockMvc.perform(put(BASE_URL + "/updateMainPassword")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestBody)))
                .andDo(print())
                .andExpect(status().isOk());

        verify(userService, times(1)).findByEmail(anyString());
        verify(userService, times(1)).getUserLocaleForMobile(anyString());
        verify(userService, times(1)).checkPassword(anyInt(), anyString());
        verify(userService, times(1)).update(any(UpdateUserDto.class), any(Locale.class));
    }

    // TODO: Problem with mock static methods in class RestApiUtils.class!!!
    // @PrepareForTest(RestApiUtils.class)
    @Ignore
    public void updateMainPassword_not_acceptable() throws Exception {
        Map<String, String> requestBody = new HashMap<>();
        requestBody.put("currentPassword", "inapplicable");
        requestBody.put("newPassword", "inapplicable");

        String detailMsg = "Failed to check password for user: TEST_EMAIL from ip:  ";

//        RestApiUtils restApiUtils = Mockito.mock(RestApiUtils.class);
//        PowerMockito.mockStatic(RestApiUtils.class);

        when(userService.findByEmail(anyString())).thenReturn(getMockUser());
        when(userService.getUserLocaleForMobile(anyString())).thenReturn(Locale.ENGLISH);
        when(userService.checkPassword(anyInt(), anyString())).thenReturn(Boolean.TRUE);
        when(userService.update(any(UpdateUserDto.class), any(Locale.class))).thenReturn(Boolean.FALSE);

        mockMvc.perform(put(BASE_URL + "/updateMainPassword")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestBody)))
                .andDo(print())
                .andExpect(status().isNotAcceptable());

        verify(userService, times(1)).findByEmail(anyString());
        verify(userService, times(1)).getUserLocaleForMobile(anyString());
        verify(userService, times(1)).checkPassword(anyInt(), anyString());
        verify(userService, times(1)).update(any(UpdateUserDto.class), any(Locale.class));
    }

    @Test
    public void getNickName_isOk_user_has_nickname() throws Exception {
        when(userService.findByEmail(anyString())).thenReturn(getMockUser());

        mockMvc.perform(get(BASE_URL + NICKNAME)
                .contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasKey("/nickname")))
                .andExpect(jsonPath("$", hasValue("TEST_NICKNAME")));

        verify(userService, times(1)).findByEmail(anyString());
    }

    @Test
    public void getNickName_isOk_user_has_not_nickname() throws Exception {
        User mockUser = getMockUser();
        mockUser.setNickname(null);

        when(userService.findByEmail(anyString())).thenReturn(mockUser);

        mockMvc.perform(get(BASE_URL + NICKNAME)
                .contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasKey("/nickname")))
                .andExpect(jsonPath("$", hasValue("")));

        verify(userService, times(1)).findByEmail(anyString());
    }

    @Test
    public void updateNickName_not_found() throws Exception {
        Map<String, String> requestBody = new HashMap<>();

        when(userService.findByEmail(anyString())).thenReturn(getMockUser());

        mockMvc.perform(put(BASE_URL + NICKNAME)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(objectMapper.writeValueAsString(requestBody)))
                .andExpect(status().isNotFound());

        verify(userService, times(1)).findByEmail(anyString());
    }

    @Test
    public void updateNickName_isOk_body_containsKey_nickname() throws Exception {
        Map<String, String> requestBody = new HashMap<>();
        requestBody.put("/nickname", "TEST_NICKNAME");

        when(userService.findByEmail(anyString())).thenReturn(getMockUser());

        mockMvc.perform(put(BASE_URL + NICKNAME)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(objectMapper.writeValueAsString(requestBody)))
                .andExpect(status().isOk());

        verify(userService, times(1)).findByEmail(anyString());
    }

    @Test
    public void updateNickName_isOk_userService_return_true() throws Exception {
        Map<String, String> requestBody = new HashMap<>();
        requestBody.put("/nickname", "TEST_NICKNAME");

        when(userService.findByEmail(anyString())).thenReturn(getMockUser());
        when(userService.setNickname(anyString(), anyString())).thenReturn(Boolean.TRUE);

        mockMvc.perform(put(BASE_URL + NICKNAME)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(objectMapper.writeValueAsString(requestBody)))
                .andExpect(status().isOk());

        verify(userService, times(1)).findByEmail(anyString());
        verify(userService, times(1)).setNickname(anyString(), anyString());
    }

    @Test
    public void getSessionPeriod_isOk_params_equals_null() throws Exception {
        when(sessionService.getByEmailOrDefault(anyString())).thenReturn(null);

        mockMvc.perform(get(BASE_URL + SESSION_INTERVAL)
                .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data", is(0)))
                .andExpect(jsonPath("$.error", is(nullValue())));

        verify(sessionService, times(1)).getByEmailOrDefault(anyString());
    }

    @Test
    public void getSessionPeriod_isOk_params_not_equals_null() throws Exception {
        SessionParams params = new SessionParams();
        params.setSessionTimeMinutes(300);

        when(sessionService.getByEmailOrDefault(anyString())).thenReturn(params);

        mockMvc.perform(get(BASE_URL + SESSION_INTERVAL)
                .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data", is(300)))
                .andExpect(jsonPath("$.error", is(nullValue())));

        verify(sessionService, times(1)).getByEmailOrDefault(anyString());
    }

    @Test
    public void updateSessionPeriod_bad_request() throws Exception {
        Map<String, String> requestBody = new HashMap<>();

        mockMvc.perform(put(BASE_URL + SESSION_INTERVAL)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(objectMapper.writeValueAsString(requestBody)))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void updateSessionPeriod_not_acceptable() throws Exception {
        Map<String, String> requestBody = new HashMap<>();
        requestBody.put("sessionInterval", "300");

        when(sessionService.isSessionTimeValid(anyInt())).thenReturn(Boolean.FALSE);

        mockMvc.perform(put(BASE_URL + SESSION_INTERVAL)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(objectMapper.writeValueAsString(requestBody)))
                .andExpect(status().isNotAcceptable());

        verify(sessionService, times(1)).isSessionTimeValid(anyInt());
    }

    @Test
    public void updateSessionPeriod_isOk() throws Exception {
        Map<String, String> requestBody = new HashMap<>();
        requestBody.put("sessionInterval", "300");

        SessionParams params = new SessionParams();
        params.setId(100);
        params.setUserId(200);
        params.setSessionTimeMinutes(300);
        params.setSessionLifeTypeId(400);

        when(sessionService.isSessionTimeValid(anyInt())).thenReturn(Boolean.TRUE);
        when(sessionService.saveOrUpdate(anyObject(), anyString())).thenReturn(params);

        mockMvc.perform(put(BASE_URL + SESSION_INTERVAL)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(objectMapper.writeValueAsString(requestBody)))
                .andExpect(status().isOk());

        verify(sessionService, times(1)).isSessionTimeValid(anyInt());
        verify(sessionService, times(1)).saveOrUpdate(anyObject(), anyString());
    }

    @Test
    public void getUserNotifications_isOk_return_empty_map() throws Exception {
        when(userService.getIdByEmail(anyString())).thenThrow(Exception.class);

        mockMvc.perform(get(BASE_URL + EMAIL_NOTIFICATION)
                .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", is(Collections.EMPTY_MAP)));
    }

    @Test
    public void getUserNotifications_isOk() throws Exception {
        NotificationOption notificationOption = new NotificationOption();
        notificationOption.setEvent(NotificationEvent.ACCOUNT);
        notificationOption.setUserId(100);
        notificationOption.setSendNotification(Boolean.TRUE);
        notificationOption.setSendEmail(Boolean.TRUE);

        when(userService.getIdByEmail(anyString())).thenReturn(100);
        when(notificationService.getNotificationOptionsByUser(anyInt()))
                .thenReturn(Collections.singletonList(notificationOption));

        mockMvc.perform(get(BASE_URL + EMAIL_NOTIFICATION)
                .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasKey("ACCOUNT")))
                .andExpect(jsonPath("$", hasValue(Boolean.TRUE)));

        verify(userService, times(1)).getIdByEmail(anyString());
        verify(notificationService, times(1)).getNotificationOptionsByUser(anyInt());
    }

    @Test
    public void updateUserNotification_bad_reques() throws Exception {
        List<NotificationOption> options = new ArrayList<>();

        when(userService.getIdByEmail(anyString())).thenThrow(Exception.class);

        mockMvc.perform(put(BASE_URL + EMAIL_NOTIFICATION)
                .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE)
                .content(objectMapper.writeValueAsString(options)))
                .andExpect(status().isBadRequest());

        verify(userService, times(1)).getIdByEmail(anyString());
    }

    @Test
    public void updateUserNotification_isOk() throws Exception {
        List<NotificationOption> options = new ArrayList<>();

        when(userService.getIdByEmail(anyString())).thenReturn(100);
        doNothing().when(notificationService)
                .updateNotificationOptionsForUser(anyInt(), anyListOf(NotificationOption.class));

        mockMvc.perform(put(BASE_URL + EMAIL_NOTIFICATION)
                .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE)
                .content(objectMapper.writeValueAsString(options)))
                .andExpect(status().isOk());

        verify(userService, times(1)).getIdByEmail(anyString());
        verify(notificationService, times(1))
                .updateNotificationOptionsForUser(anyInt(), anyListOf(NotificationOption.class));
    }

    @Test
    public void getUserColorDepth_isOk_PageLayoutSettingsDto_not_null() throws Exception {
        PageLayoutSettingsDto mockDto = PageLayoutSettingsDto.builder().build();
        mockDto.setUserId(100);
        mockDto.setScheme(ColorScheme.LIGHT);
        mockDto.setLowColorEnabled(Boolean.TRUE);

        when(userService.findByEmail(anyString())).thenReturn(getMockUser());
        when(layoutSettingsService.findByUser(anyObject())).thenReturn(mockDto);

        mockMvc.perform(get(BASE_URL + IS_COLOR_BLIND)
                .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", is(Boolean.TRUE)));

        verify(userService, times(1)).findByEmail(anyString());
        verify(layoutSettingsService, times(1)).findByUser(anyObject());
    }

    @Test
    public void getUserColorDepth_isOk_PageLayoutSettingsDto_null() throws Exception {
        when(userService.findByEmail(anyString())).thenReturn(getMockUser());
        when(layoutSettingsService.findByUser(anyObject())).thenReturn(null);

        mockMvc.perform(get(BASE_URL + IS_COLOR_BLIND)
                .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", is(Boolean.FALSE)));

        verify(userService, times(1)).findByEmail(anyString());
        verify(layoutSettingsService, times(1)).findByUser(anyObject());
    }

    @Test
    public void updateUserColorDepth_unprocessable_entity() throws Exception {
        Map<String, Boolean> params = new HashMap<>();

        mockMvc.perform(put(BASE_URL + IS_COLOR_BLIND)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(objectMapper.writeValueAsString(params)))
                .andExpect(status().isUnprocessableEntity());
    }

    @Test
    public void updateUserColorDepth_isOk() throws Exception {
        Map<String, Boolean> params = new HashMap<>();
        params.put(STATE, Boolean.TRUE);

        when(userService.findByEmail(anyString())).thenReturn(getMockUser());
        when(layoutSettingsService.toggleLowColorMode(anyObject(), anyBoolean())).thenReturn(Boolean.TRUE);

        mockMvc.perform(put(BASE_URL + IS_COLOR_BLIND)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(objectMapper.writeValueAsString(params)))
                .andExpect(status().isOk());

        verify(userService, times(1)).findByEmail(anyString());
        verify(layoutSettingsService, times(1)).toggleLowColorMode(anyObject(), anyBoolean());
    }

    @Test
    public void updateUserColorScheme_unprocessable_entity() throws Exception {
        Map<String, String> params = new HashMap<>();

        mockMvc.perform(put(BASE_URL + COLOR_SCHEME)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(objectMapper.writeValueAsString(params)))
                .andExpect(status().isUnprocessableEntity());
    }

    @Test
    public void updateUserColorScheme_isOk() throws Exception {
        Map<String, String> params = new HashMap<>();
        params.put("SCHEME", "TEST_SCHEME");

        PageLayoutSettingsDto mockDto = PageLayoutSettingsDto.builder().build();
        mockDto.setUserId(100);
        mockDto.setScheme(ColorScheme.LIGHT);
        mockDto.setLowColorEnabled(Boolean.TRUE);

        when(userService.getIdByEmail(anyString())).thenReturn(100);
        when(layoutSettingsService.save(anyObject())).thenReturn(mockDto);

        mockMvc.perform(put(BASE_URL + COLOR_SCHEME)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(objectMapper.writeValueAsString(params)))
                .andExpect(status().isOk());

        verify(userService, times(1)).getIdByEmail(anyString());
        verify(layoutSettingsService, times(1)).save(anyObject());
    }

    // TODO: TEST FAIL
    @Ignore
    public void uploadUserVerification_bad_request() throws Exception {
        UserInfoVerificationDto mockDto = UserInfoVerificationDto
                .builder()
                .userId(100)
                .firstName("first_name")
                .lastName("last_name")
                .born(LocalDate.of(1980, 10, 20))
                .residentialAddress("residential_address")
                .postalCode("country")
                .country("country")
                .city("city")
                .build();

        when(userService.getIdByEmail(anyString())).thenReturn(100);
        when(verificationService.save((UserInfoVerificationDto) anyObject())).thenReturn(mockDto);

        mockMvc.perform(post(BASE_URL + "/docs")
                .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE)
                .content(objectMapper.writeValueAsString(mockDto)))
                .andDo(print())
                .andExpect(status().isBadRequest());

        verify(userService, times(1)).getIdByEmail(anyString());
        verify(verificationService, times(1)).save((UserDocVerificationDto) anyObject());
    }

    // TODO: TEST FAIL
    @Ignore
    public void uploadUserVerification_created() throws Exception {
        UserInfoVerificationDto mockDto = UserInfoVerificationDto
                .builder()
                .userId(100)
                .firstName("first_name")
                .lastName("last_name")
                .born(LocalDate.of(1980, 10, 20))
                .residentialAddress("residential_address")
                .postalCode("country")
                .country("country")
                .city("city")
                .build();

        when(userService.getIdByEmail(anyString())).thenReturn(100);
        when(verificationService.save((UserInfoVerificationDto) anyObject())).thenReturn(null);

        mockMvc.perform(post(BASE_URL + "/docs")
                .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE)
                .content(objectMapper.writeValueAsString(mockDto)))
                .andDo(print())
                .andExpect(status().isCreated());

        verify(userService, times(1)).getIdByEmail(anyString());
        verify(verificationService, times(1)).save((UserDocVerificationDto) anyObject());
    }

    @Test
    public void uploadUserVerificationDocs_created() throws Exception {
        Map<String, String> body = new HashMap<>();
        body.put("BASE_64", "TEST_BASE_64");

        UserDocVerificationDto mockDto = UserDocVerificationDto.builder().build();
        mockDto.setUserId(100);
        mockDto.setDocumentType(VerificationDocumentType.PHOTO);
        mockDto.setEncoded("TEST_ENCODED");

        when(userService.getIdByEmail(anyString())).thenReturn(100);
        when(verificationService.save((UserDocVerificationDto) anyObject())).thenReturn(mockDto);

        mockMvc.perform(post(BASE_URL + "/userFiles/docs/{type}", "PHOTO")
                .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE)
                .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isCreated());

        verify(userService, times(1)).getIdByEmail(anyString());
        verify(verificationService, times(1)).save((UserDocVerificationDto) anyObject());
    }

    @Test
    public void uploadUserVerificationDocs_bad_request_UserDocVerificationDto_not_save() throws Exception {
        Map<String, String> body = new HashMap<>();
        body.put("BASE_64", "TEST_BASE_64");

        when(userService.getIdByEmail(anyString())).thenReturn(100);
        when(verificationService.save((UserDocVerificationDto) anyObject())).thenReturn(null);

        mockMvc.perform(post(BASE_URL + "/userFiles/docs/{type}", "PHOTO")
                .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE)
                .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isBadRequest());

        verify(userService, times(1)).getIdByEmail(anyString());
        verify(verificationService, times(1)).save((UserDocVerificationDto) anyObject());
    }

    @Test
    public void uploadUserVerificationDocs_bad_request() throws Exception {
        Map<String, String> body = new HashMap<>();

        when(userService.getIdByEmail(anyString())).thenReturn(100);

        mockMvc.perform(post(BASE_URL + "/userFiles/docs/{type}", "PHOTO")
                .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE)
                .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isBadRequest());

        verify(userService, times(1)).getIdByEmail(anyString());
    }

    @Test
    public void getUserFavouriteCurrencyPairs() throws Exception {
        int listFavouriteCurrencyPairs = 7;

        when(userService.getUserFavouriteCurrencyPairs(anyString()))
                .thenReturn(Collections.singletonList(listFavouriteCurrencyPairs));

        mockMvc.perform(get(BASE_URL + "/currency_pair/favourites")
                .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.[0]", is(listFavouriteCurrencyPairs)));

        verify(userService, times(1)).getUserFavouriteCurrencyPairs(anyString());
    }

    @Test
    public void manegeUserFavouriteCurrencyPairs_isOk() throws Exception {
        Map<String, String> params = new HashMap<>();
        params.put("PAIR_ID", "100");
        params.put("TO_DELETE", "false");

        when(userService.manageUserFavouriteCurrencyPair(anyString(), anyInt(), anyBoolean())).thenReturn(Boolean.TRUE);

        mockMvc.perform(put(BASE_URL + "/currency_pair/favourites")
                .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE)
                .content(objectMapper.writeValueAsString(params)))
                .andExpect(status().isOk());

        verify(userService, times(1))
                .manageUserFavouriteCurrencyPair(anyString(), anyInt(), anyBoolean());
    }

    @Test
    public void manegeUserFavouriteCurrencyPairs_bad_request_manageUserFavouriteCurrencyPair_equals_false() throws Exception {
        Map<String, String> params = new HashMap<>();
        params.put("PAIR_ID", "100");
        params.put("TO_DELETE", "false");

        when(userService.manageUserFavouriteCurrencyPair(anyString(), anyInt(), anyBoolean())).thenReturn(Boolean.FALSE);

        mockMvc.perform(put(BASE_URL + "/currency_pair/favourites")
                .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE)
                .content(objectMapper.writeValueAsString(params)))
                .andExpect(status().isBadRequest());

        verify(userService, times(1))
                .manageUserFavouriteCurrencyPair(anyString(), anyInt(), anyBoolean());
    }

    @Test
    public void manegeUserFavouriteCurrencyPairs_bad_request() throws Exception {
        Map<String, String> params = new HashMap<>();

        mockMvc.perform(put(BASE_URL + "/currency_pair/favourites")
                .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE)
                .content(objectMapper.writeValueAsString(params)))
                .andExpect(status().isBadRequest());
    }
}