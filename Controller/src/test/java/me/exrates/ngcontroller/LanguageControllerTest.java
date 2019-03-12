package me.exrates.ngcontroller;

import me.exrates.service.UserService;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.security.Principal;
import java.util.Optional;

import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

public class LanguageControllerTest {

    @Mock
    private UserService userService;

    @Mock
    private Principal principal;

    @InjectMocks
    private LanguageController languageController;

    private MockMvc mockMvc;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        mockMvc = MockMvcBuilders
                .standaloneSetup(languageController)
                .build();
    }

    @Test
    public void getUserLanguage_isOk() throws Exception {
        Mockito.when(principal.getName()).thenReturn("TEST_EMAIL");
        Mockito.when(userService.getPreferedLangByEmail(anyString())).thenReturn("TEST_LANGUAGE");

        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .get("/user-language")
                .principal(principal);

        mockMvc.perform(requestBuilder)
                .andDo(print())
                .andExpect(MockMvcResultMatchers.status().isOk());

        verify(userService, times(1)).getPreferedLangByEmail(anyString());
    }

    @Test
    public void getUserLanguage_invalid() throws Exception {
        Mockito.when(principal.getName()).thenReturn("TEST_EMAIL");
        Mockito.when(userService.getPreferedLangByEmail(anyString())).thenReturn(null);

        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .get("/user-language")
                .principal(principal);

        mockMvc.perform(requestBuilder)
                .andDo(print())
                .andExpect(MockMvcResultMatchers.status().isServiceUnavailable());

        verify(userService, times(1)).getPreferedLangByEmail(anyString());
    }

    @Test
    public void updateUserLanguage() {
    }
}