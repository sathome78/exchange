package me.exrates.ngcontroller;

import me.exrates.model.OpenApiToken;
import me.exrates.model.User;
import me.exrates.model.dto.NotificationResultDto;
import me.exrates.model.dto.openAPI.OpenApiTokenPublicDto;
import me.exrates.model.enums.NotificationMessageEventEnum;
import me.exrates.security.service.SecureService;
import me.exrates.service.OpenApiTokenService;
import me.exrates.service.UserService;
import me.exrates.service.notifications.G2faService;
import org.apache.commons.lang3.StringUtils;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.util.NestedServletException;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import javax.servlet.http.HttpServletRequest;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyBoolean;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Matchers.anyString;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

public class NgTokenSettingsControllerTest extends AngularApiCommonTest {
    private static final String BASE_URL = "/api/private/v2/settings/token";

    private static final String USER_EMAIL = "test@gmail.com";

    @Mock
    private OpenApiTokenService openApiTokenService;
    @Mock
    private UserService userService;
    @Mock
    private G2faService g2faService;
    @Mock
    private SecureService secureService;

    @InjectMocks
    NgTokenSettingsController ngTokenSettingsController;

    private MockMvc mockMvc;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        mockMvc = MockMvcBuilders
                .standaloneSetup(ngTokenSettingsController)
                .build();
    }

    @Test
    public void getUserTokens_successFoundTest() throws Exception {
        OpenApiTokenPublicDto token = new OpenApiTokenPublicDto();
        token.setUserId(1);
        token.setAlias("alias");
        token.setPublicKey("publicKey");
        token.setAllowTrade(true);
        token.setAllowWithdraw(false);
        List<OpenApiTokenPublicDto> tokens = Collections.singletonList(token);

        Mockito.when(userService.getUserEmailFromSecurityContext()).thenReturn(USER_EMAIL);
        Mockito.when(openApiTokenService.getUserTokens(anyString())).thenReturn(tokens);

        UriComponents uriComponents = UriComponentsBuilder.newInstance()
                .path(BASE_URL + "/findAll")
                .build();

        mockMvc.perform(getApiRequestBuilder(uriComponents.toUri(), HttpMethod.GET, null, StringUtils.EMPTY, MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(MockMvcResultMatchers.status().isOk())

                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].alias", is(token.getAlias())))
                .andExpect(jsonPath("$[0].userId", is(token.getUserId())))
                .andExpect(jsonPath("$[0].publicKey", is(token.getPublicKey())))
                .andExpect(jsonPath("$[0].allowTrade", is(token.getAllowTrade())))
                .andExpect(jsonPath("$[0].allowWithdraw", is(token.getAllowWithdraw())));

        Mockito.verify(userService, Mockito.atLeastOnce()).getUserEmailFromSecurityContext();
        Mockito.verify(openApiTokenService, Mockito.atLeastOnce()).getUserTokens(anyString());
    }

    @Test
    public void getUserTokens_successNotFoundTest() throws Exception {
        Mockito.when(userService.getUserEmailFromSecurityContext()).thenReturn(USER_EMAIL);
        Mockito.when(openApiTokenService.getUserTokens(anyString())).thenReturn(Collections.emptyList());

        UriComponents uriComponents = UriComponentsBuilder.newInstance()
                .path(BASE_URL + "/findAll")
                .build();

        mockMvc.perform(getApiRequestBuilder(uriComponents.toUri(), HttpMethod.GET, null, StringUtils.EMPTY, MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(MockMvcResultMatchers.status().isOk())

                .andExpect(jsonPath("$", hasSize(0)));

        Mockito.verify(userService, Mockito.atLeastOnce()).getUserEmailFromSecurityContext();
        Mockito.verify(openApiTokenService, Mockito.atLeastOnce()).getUserTokens(anyString());
    }

    @Test
    public void tokenCreate_successByGoogleTest() throws Exception {
        OpenApiToken token = new OpenApiToken();
        token.setUserId(1);
        token.setAlias("alias");
        token.setPublicKey("publicKey");
        token.setAllowTrade(true);
        token.setAllowWithdraw(false);

        Mockito.when(userService.getUserEmailFromSecurityContext()).thenReturn(USER_EMAIL);
        Mockito.when(userService.findByEmail(anyString())).thenReturn(getUser());
        Mockito.when(g2faService.isGoogleAuthenticatorEnable(anyInt())).thenReturn(true);
        Mockito.when(g2faService.checkGoogle2faVerifyCode(anyString(), anyInt())).thenReturn(true);
        Mockito.when(openApiTokenService.generateToken(anyString(), anyString())).thenReturn(token);

        UriComponents uriComponents = UriComponentsBuilder.newInstance()
                .path(BASE_URL + "/create")
                .queryParam("alias", "alias")
                .queryParam("pin", "pin")
                .build();

        mockMvc.perform(getApiRequestBuilder(uriComponents.toUri(), HttpMethod.POST, null, StringUtils.EMPTY, MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(MockMvcResultMatchers.status().isOk())

                .andExpect(jsonPath("$.alias", is(token.getAlias())))
                .andExpect(jsonPath("$.userId", is(token.getUserId())))
                .andExpect(jsonPath("$.publicKey", is(token.getPublicKey())))
                .andExpect(jsonPath("$.allowTrade", is(token.getAllowTrade())))
                .andExpect(jsonPath("$.allowWithdraw", is(token.getAllowWithdraw())));

        Mockito.verify(userService, Mockito.atLeastOnce()).getUserEmailFromSecurityContext();
        Mockito.verify(userService, Mockito.atLeastOnce()).findByEmail(anyString());
        Mockito.verify(g2faService, Mockito.atLeastOnce()).isGoogleAuthenticatorEnable(anyInt());
        Mockito.verify(g2faService, Mockito.atLeastOnce()).checkGoogle2faVerifyCode(anyString(), anyInt());
        Mockito.verify(openApiTokenService, Mockito.atLeastOnce()).generateToken(anyString(), anyString());
    }

    @Test
    public void tokenCreate_successByMailTest() throws Exception {
        OpenApiToken token = new OpenApiToken();
        token.setUserId(1);
        token.setAlias("alias");
        token.setPublicKey("publicKey");
        token.setAllowTrade(true);
        token.setAllowWithdraw(false);

        Mockito.when(userService.getUserEmailFromSecurityContext()).thenReturn(USER_EMAIL);
        Mockito.when(userService.findByEmail(anyString())).thenReturn(getUser());
        Mockito.when(g2faService.isGoogleAuthenticatorEnable(anyInt())).thenReturn(false);
        Mockito.when(userService.checkPin(anyString(), anyString(), any(NotificationMessageEventEnum.class))).thenReturn(true);
        Mockito.when(openApiTokenService.generateToken(anyString(), anyString())).thenReturn(token);

        UriComponents uriComponents = UriComponentsBuilder.newInstance()
                .path(BASE_URL + "/create")
                .queryParam("alias", "alias")
                .queryParam("pin", "pin")
                .build();

        mockMvc.perform(getApiRequestBuilder(uriComponents.toUri(), HttpMethod.POST, null, StringUtils.EMPTY, MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(MockMvcResultMatchers.status().isOk())

                .andExpect(jsonPath("$.alias", is(token.getAlias())))
                .andExpect(jsonPath("$.userId", is(token.getUserId())))
                .andExpect(jsonPath("$.publicKey", is(token.getPublicKey())))
                .andExpect(jsonPath("$.allowTrade", is(token.getAllowTrade())))
                .andExpect(jsonPath("$.allowWithdraw", is(token.getAllowWithdraw())));

        Mockito.verify(userService, Mockito.atLeastOnce()).getUserEmailFromSecurityContext();
        Mockito.verify(userService, Mockito.atLeastOnce()).findByEmail(anyString());
        Mockito.verify(g2faService, Mockito.atLeastOnce()).isGoogleAuthenticatorEnable(anyInt());
        Mockito.verify(userService, Mockito.atLeastOnce()).checkPin(anyString(), anyString(), any(NotificationMessageEventEnum.class));
        Mockito.verify(openApiTokenService, Mockito.atLeastOnce()).generateToken(anyString(), anyString());
    }

    @Test(expected = NestedServletException.class)
    public void tokenCreate_failedEmptyPinTest() throws Exception {
        Mockito.when(userService.getUserEmailFromSecurityContext()).thenReturn(USER_EMAIL);
        Mockito.when(userService.findByEmail(anyString())).thenReturn(getUser());
        Mockito.when(g2faService.isGoogleAuthenticatorEnable(anyInt())).thenReturn(false);

        UriComponents uriComponents = UriComponentsBuilder.newInstance()
                .path(BASE_URL + "/create")
                .queryParam("alias", "alias")
                .queryParam("pin", "")
                .build();

        mockMvc.perform(getApiRequestBuilder(uriComponents.toUri(), HttpMethod.POST, null, StringUtils.EMPTY, MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test(expected = NestedServletException.class)
    public void tokenCreate_failedWrongGooglePinTest() throws Exception {
        Mockito.when(userService.getUserEmailFromSecurityContext()).thenReturn(USER_EMAIL);
        Mockito.when(userService.findByEmail(anyString())).thenReturn(getUser());
        Mockito.when(g2faService.isGoogleAuthenticatorEnable(anyInt())).thenReturn(true);
        Mockito.when(g2faService.checkGoogle2faVerifyCode(anyString(), anyInt())).thenReturn(false);

        UriComponents uriComponents = UriComponentsBuilder.newInstance()
                .path(BASE_URL + "/create")
                .queryParam("alias", "alias")
                .queryParam("pin", "pin")
                .build();

        mockMvc.perform(getApiRequestBuilder(uriComponents.toUri(), HttpMethod.POST, null, StringUtils.EMPTY, MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test(expected = NestedServletException.class)
    public void tokenCreate_failedWrongMailPinTest() throws Exception {
        Mockito.when(userService.getUserEmailFromSecurityContext()).thenReturn(USER_EMAIL);
        Mockito.when(userService.findByEmail(anyString())).thenReturn(getUser());
        Mockito.when(g2faService.isGoogleAuthenticatorEnable(anyInt())).thenReturn(false);
        Mockito.when(userService.checkPin(anyString(), anyString(), any(NotificationMessageEventEnum.class))).thenReturn(false);
        Mockito.when(secureService.sendApiTokenPincode(any(User.class), any(HttpServletRequest.class))).thenReturn(new NotificationResultDto("", new String[]{""}));

        UriComponents uriComponents = UriComponentsBuilder.newInstance()
                .path(BASE_URL + "/create")
                .queryParam("alias", "alias")
                .queryParam("pin", "pin")
                .build();

        mockMvc.perform(getApiRequestBuilder(uriComponents.toUri(), HttpMethod.POST, null, StringUtils.EMPTY, MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test(expected = NestedServletException.class)
    public void tokenCreate_failedBadAliasFormatTest() throws Exception {
        Mockito.when(userService.getUserEmailFromSecurityContext()).thenReturn(USER_EMAIL);
        Mockito.when(userService.findByEmail(anyString())).thenReturn(getUser());
        Mockito.when(g2faService.isGoogleAuthenticatorEnable(anyInt())).thenReturn(true);
        Mockito.when(g2faService.checkGoogle2faVerifyCode(anyString(), anyInt())).thenReturn(true);
        Mockito.when(openApiTokenService.generateToken(anyString(), anyString())).thenThrow(Exception.class);

        UriComponents uriComponents = UriComponentsBuilder.newInstance()
                .path(BASE_URL + "/create")
                .queryParam("alias", "alias")
                .queryParam("pin", "pin")
                .build();

        mockMvc.perform(getApiRequestBuilder(uriComponents.toUri(), HttpMethod.POST, null, StringUtils.EMPTY, MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    public void allowTrade_successAllowTradeIsTrueTest() throws Exception {
        Mockito.when(userService.getUserEmailFromSecurityContext()).thenReturn(USER_EMAIL);
        Mockito.when(userService.findByEmail(anyString())).thenReturn(getUser());
        Mockito.when(g2faService.isGoogleAuthenticatorEnable(anyInt())).thenReturn(true);
        Mockito.when(g2faService.checkGoogle2faVerifyCode(anyString(), anyInt())).thenReturn(true);
        Mockito.doNothing().when(openApiTokenService).updateToken(anyLong(), anyBoolean(), anyString());

        UriComponents uriComponents = UriComponentsBuilder.newInstance()
                .path(BASE_URL + "/allowTrade")
                .queryParam("tokenId", 1)
                .queryParam("allowTrade", true)
                .queryParam("pin", "pin")
                .build();

        mockMvc.perform(getApiRequestBuilder(uriComponents.toUri(), HttpMethod.POST, null, StringUtils.EMPTY, MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(MockMvcResultMatchers.status().isOk());

        Mockito.verify(userService, Mockito.atLeastOnce()).getUserEmailFromSecurityContext();
        Mockito.verify(userService, Mockito.atLeastOnce()).findByEmail(anyString());
        Mockito.verify(g2faService, Mockito.atLeastOnce()).isGoogleAuthenticatorEnable(anyInt());
        Mockito.verify(g2faService, Mockito.atLeastOnce()).checkGoogle2faVerifyCode(anyString(), anyInt());
        Mockito.verify(openApiTokenService, Mockito.atLeastOnce()).updateToken(anyLong(), anyBoolean(), anyString());
    }

    @Test
    public void allowTrade_successAllowTradeIsFalseTest() throws Exception {
        Mockito.when(userService.getUserEmailFromSecurityContext()).thenReturn(USER_EMAIL);
        Mockito.doNothing().when(openApiTokenService).updateToken(anyLong(), anyBoolean(), anyString());

        UriComponents uriComponents = UriComponentsBuilder.newInstance()
                .path(BASE_URL + "/allowTrade")
                .queryParam("tokenId", 1)
                .queryParam("allowTrade", false)
                .build();

        mockMvc.perform(getApiRequestBuilder(uriComponents.toUri(), HttpMethod.POST, null, StringUtils.EMPTY, MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(MockMvcResultMatchers.status().isOk());

        Mockito.verify(userService, Mockito.atLeastOnce()).getUserEmailFromSecurityContext();
        Mockito.verify(openApiTokenService, Mockito.atLeastOnce()).updateToken(anyLong(), anyBoolean(), anyString());
    }

    @Test
    public void deleteToken_successTest() throws Exception {
        Mockito.when(userService.getUserEmailFromSecurityContext()).thenReturn(USER_EMAIL);
        Mockito.doNothing().when(openApiTokenService).deleteToken(anyLong(), anyString());

        UriComponents uriComponents = UriComponentsBuilder.newInstance()
                .path(BASE_URL + "/delete")
                .queryParam("tokenId", 1)
                .build();

        mockMvc.perform(MockMvcRequestBuilders.delete(uriComponents.toUri())
                .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(MockMvcResultMatchers.status().isOk());

        Mockito.verify(userService, Mockito.atLeastOnce()).getUserEmailFromSecurityContext();
        Mockito.verify(openApiTokenService, Mockito.atLeastOnce()).deleteToken(anyLong(), anyString());
    }

    @Test
    public void sendApiTokenPinCode_successByGoogleTest() throws Exception {
        Mockito.when(userService.getUserEmailFromSecurityContext()).thenReturn(USER_EMAIL);
        Mockito.when(userService.findByEmail(anyString())).thenReturn(getUser());
        Mockito.when(g2faService.isGoogleAuthenticatorEnable(anyInt())).thenReturn(true);

        UriComponents uriComponents = UriComponentsBuilder.newInstance()
                .path(BASE_URL + "/pin")
                .build();

        mockMvc.perform(getApiRequestBuilder(uriComponents.toUri(), HttpMethod.POST, null, StringUtils.EMPTY, MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(MockMvcResultMatchers.status().isOk());

        Mockito.verify(userService, Mockito.atLeastOnce()).getUserEmailFromSecurityContext();
        Mockito.verify(userService, Mockito.atLeastOnce()).findByEmail(anyString());
        Mockito.verify(g2faService, Mockito.atLeastOnce()).isGoogleAuthenticatorEnable(anyInt());
    }

    @Test
    public void sendApiTokenPinCode_successByMailTest() throws Exception {
        Mockito.when(userService.getUserEmailFromSecurityContext()).thenReturn(USER_EMAIL);
        Mockito.when(userService.findByEmail(anyString())).thenReturn(getUser());
        Mockito.when(g2faService.isGoogleAuthenticatorEnable(anyInt())).thenReturn(false);
        Mockito.when(secureService.sendApiTokenPincode(any(User.class), any(HttpServletRequest.class))).thenReturn(new NotificationResultDto("", new String[]{""}));

        UriComponents uriComponents = UriComponentsBuilder.newInstance()
                .path(BASE_URL + "/pin")
                .build();

        mockMvc.perform(getApiRequestBuilder(uriComponents.toUri(), HttpMethod.POST, null, StringUtils.EMPTY, MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(MockMvcResultMatchers.status().isCreated());

        Mockito.verify(userService, Mockito.atLeastOnce()).getUserEmailFromSecurityContext();
        Mockito.verify(userService, Mockito.atLeastOnce()).findByEmail(anyString());
        Mockito.verify(g2faService, Mockito.atLeastOnce()).isGoogleAuthenticatorEnable(anyInt());
        Mockito.verify(secureService, Mockito.atLeastOnce()).sendApiTokenPincode(any(User.class), any(HttpServletRequest.class));
    }

    @Test(expected = NestedServletException.class)
    public void sendApiTokenPinCode_failedTest() throws Exception {
        Mockito.when(userService.getUserEmailFromSecurityContext()).thenReturn(USER_EMAIL);
        Mockito.when(userService.findByEmail(anyString())).thenReturn(getUser());
        Mockito.when(g2faService.isGoogleAuthenticatorEnable(anyInt())).thenReturn(false);
        Mockito.when(secureService.sendApiTokenPincode(any(User.class), any(HttpServletRequest.class))).thenThrow(Exception.class);

        UriComponents uriComponents = UriComponentsBuilder.newInstance()
                .path(BASE_URL + "/pin")
                .build();

        mockMvc.perform(getApiRequestBuilder(uriComponents.toUri(), HttpMethod.POST, null, StringUtils.EMPTY, MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    private User getUser() {
        User user = new User();
        user.setId(1);
        return user;
    }
}