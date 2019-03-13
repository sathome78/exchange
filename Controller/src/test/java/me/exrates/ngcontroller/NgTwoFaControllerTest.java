package me.exrates.ngcontroller;

import me.exrates.model.User;
import me.exrates.model.dto.Generic2faResponseDto;
import me.exrates.security.service.NgUserService;
import me.exrates.service.UserService;
import me.exrates.service.notifications.G2faService;
import org.apache.commons.lang3.StringUtils;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.Matchers.is;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class NgTwoFaControllerTest extends AngularApiCommonTest {

    private static final String BASE_URL = "/api/private/v2/2FaOptions";

    @Mock
    UserService userService;
    @Mock
    NgUserService ngUserService;
    @Mock
    G2faService g2faService;

    @InjectMocks
    NgTwoFaController ngTwoFaController;

    private MockMvc mockMvc;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        mockMvc = MockMvcBuilders
                .standaloneSetup(ngTwoFaController)
                .build();

        SecurityContextHolder.getContext()
                .setAuthentication(new AnonymousAuthenticationToken("GUEST", "USERNAME",
                        AuthorityUtils.createAuthorityList("ROLE_ONE", "ROLE_TWO")));
    }

    @Test
    public void getSecurityCode_isOk() throws Exception {
        UriComponents uriComponents = UriComponentsBuilder.newInstance()
                .path(BASE_URL + "/google2fa/hash")
                .build();

        Generic2faResponseDto dto = new Generic2faResponseDto("TEST_MESSAGE", "TEST_CODE", "TEST_ERROR");

        when(userService.getIdByEmail(anyString())).thenReturn(1);
        when(g2faService.getGoogleAuthenticatorCodeNg(anyInt())).thenReturn(dto);

        mockMvc.perform(getApiRequestBuilder(uriComponents.toUri(), HttpMethod.GET, null, StringUtils.EMPTY, MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andDo(print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(jsonPath("$.message", is("TEST_MESSAGE")))
                .andExpect(jsonPath("$.code", is("TEST_CODE")))
                .andExpect(jsonPath("$.error", is("TEST_ERROR")));

        verify(userService, times(1)).getIdByEmail(anyString());
        verify(g2faService, times(1)).getGoogleAuthenticatorCodeNg(anyInt());
    }

    @Test
    public void getSecurityPinCode_isOk() throws Exception {
        UriComponents uriComponents = UriComponentsBuilder.newInstance()
                .path(BASE_URL + "/google2fa/pin")
                .build();

        when(userService.findByEmail(anyString())).thenReturn(getMockUser());
        doNothing().when(g2faService).sendGoogleAuthPinConfirm(isA(User.class), isA(HttpServletRequest.class));

        mockMvc.perform(getApiRequestBuilder(uriComponents.toUri(), HttpMethod.GET, null, StringUtils.EMPTY, MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andDo(print())
                .andExpect(status().isOk());

        verify(userService, times(1)).findByEmail(anyString());
        verify(g2faService, times(1)).sendGoogleAuthPinConfirm(anyObject(), anyObject());
    }

    @Test
    public void submitGoogleSecret_google_secret_invalid() throws Exception {
        UriComponents uriComponents = UriComponentsBuilder.newInstance()
                .path(BASE_URL + "/google2fa/submit")
                .build();

        Map<String, String> map = new HashMap<>();
        map.put("TEST_KEY", "TEST_VALUE");

        String body = objectMapper.writeValueAsString(map);

        when(userService.findByEmail(anyString())).thenReturn(getMockUser());
        when(g2faService.submitGoogleSecret(anyObject(), anyMapOf(String.class, String.class))).thenReturn(Boolean.FALSE);

        mockMvc.perform(getApiRequestBuilder(uriComponents.toUri(), HttpMethod.POST, null, body, MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andDo(print())
                .andExpect(status().isBadRequest());

        verify(userService, times(1)).findByEmail(anyString());
        verify(g2faService, never()).sendGoogleAuthPinConfirm(anyObject(), any(HttpServletRequest.class));
        verify(ngUserService, never()).sendEmailEnable2Fa(anyString());
    }

    @Test
    public void submitGoogleSecret_isOk() throws Exception {
        UriComponents uriComponents = UriComponentsBuilder.newInstance()
                .path(BASE_URL + "/google2fa/submit")
                .build();

        Map<String, String> map = new HashMap<>();
        map.put("TEST_KEY", "TEST_VALUE");

        String body = objectMapper.writeValueAsString(map);

        when(userService.findByEmail(anyString())).thenReturn(getMockUser());
        when(g2faService.submitGoogleSecret(anyObject(), anyMapOf(String.class, String.class))).thenReturn(Boolean.TRUE);
        doNothing().when(ngUserService).sendEmailEnable2Fa(isA(String.class));

        mockMvc.perform(getApiRequestBuilder(uriComponents.toUri(), HttpMethod.POST, null, body, MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andDo(print())
                .andExpect(status().isOk());

        verify(userService, times(1)).findByEmail(anyString());
        verify(g2faService, times(1)).submitGoogleSecret(anyObject(), anyMapOf(String.class, String.class));
        verify(ngUserService, times(1)).sendEmailEnable2Fa(anyString());
    }

    @Test
    public void disableGoogleAuthentication_isOk() throws Exception {
        UriComponents uriComponents = UriComponentsBuilder.newInstance()
                .path(BASE_URL + "/google2fa/disable")
                .build();

        Map<String, String> map = new HashMap<>();
        map.put("TEST_KEY", "TEST_VALUE");

        String body = objectMapper.writeValueAsString(map);

        when(userService.findByEmail(anyString())).thenReturn(getMockUser());
        when(g2faService.disableGoogleAuth(anyObject(), anyMapOf(String.class, String.class))).thenReturn(Boolean.TRUE);
        doNothing().when(ngUserService).sendEmailDisable2Fa(isA(String.class));

        mockMvc.perform(getApiRequestBuilder(uriComponents.toUri(), HttpMethod.PUT, null, body, MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andDo(print())
                .andExpect(status().isOk());

        verify(userService, times(1)).findByEmail(anyString());
        verify(userService, times(1)).findByEmail(anyString());
        verify(ngUserService, times(1)).sendEmailDisable2Fa(anyString());
    }

    @Test
    public void disableGoogleAuthentication_invalid() throws Exception {
        UriComponents uriComponents = UriComponentsBuilder.newInstance()
                .path(BASE_URL + "/google2fa/disable")
                .build();

        Map<String, String> map = new HashMap<>();
        map.put("TEST_KEY", "TEST_VALUE");

        String body = objectMapper.writeValueAsString(map);

        when(userService.findByEmail(anyString())).thenReturn(getMockUser());
        when(g2faService.disableGoogleAuth(anyObject(), anyMapOf(String.class, String.class))).thenReturn(Boolean.FALSE);

        mockMvc.perform(getApiRequestBuilder(uriComponents.toUri(), HttpMethod.PUT, null, body, MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andDo(print())
                .andExpect(status().isBadRequest());

        verify(userService, times(1)).findByEmail(anyString());
        verify(g2faService, times(1)).disableGoogleAuth(anyObject(), anyMapOf(String.class, String.class));
        verify(ngUserService, never()).sendEmailDisable2Fa(anyString());
    }

    @Test
    public void verifyGoogleAuthenticatorCode_isOk() throws Exception {
        UriComponents uriComponents = UriComponentsBuilder.newInstance()
                .path(BASE_URL + "/verify_google2fa")
                .queryParam("code", "TEST_CODE")
                .build();

        when(userService.getIdByEmail(anyString())).thenReturn(1);
        when(g2faService.checkGoogle2faVerifyCode(anyString(), anyInt())).thenReturn(Boolean.TRUE);

        mockMvc.perform(getApiRequestBuilder(uriComponents.toUri(), HttpMethod.GET, null, StringUtils.EMPTY, MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andDo(print())
                .andExpect(status().isOk());

        verify(userService, times(1)).getIdByEmail(anyString());
        verify(g2faService, times(1)).checkGoogle2faVerifyCode(anyString(), anyInt());
    }

    @Test
    public void verifyGoogleAuthenticatorCode_invalid() throws Exception {
        UriComponents uriComponents = UriComponentsBuilder.newInstance()
                .path(BASE_URL + "/verify_google2fa")
                .queryParam("code", "TEST_CODE")
                .build();

        when(userService.getIdByEmail(anyString())).thenReturn(1);
        when(g2faService.checkGoogle2faVerifyCode(anyString(), anyInt())).thenReturn(Boolean.FALSE);

        mockMvc.perform(getApiRequestBuilder(uriComponents.toUri(), HttpMethod.GET, null, "", MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andDo(print())
                .andExpect(status().isBadRequest());

        verify(userService, times(1)).getIdByEmail(anyString());
        verify(g2faService, times(1)).checkGoogle2faVerifyCode(anyString(), anyInt());
    }
}