package me.exrates.ngcontroller;

import me.exrates.model.ngExceptions.NgResponseException;
import me.exrates.service.UserService;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.util.NestedServletException;

import java.security.Principal;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

public class LanguageControllerTest extends AngularApiCommonTest {

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
                .andExpect(MockMvcResultMatchers.status().isOk());

        Mockito.verify(userService, times(1)).getPreferedLangByEmail(anyString());
    }

    @Test
    public void getUserLanguage_invalid() throws Exception {
        Mockito.when(principal.getName()).thenReturn("TEST_EMAIL");
        Mockito.when(userService.getPreferedLangByEmail(anyString())).thenReturn(null);

        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .get("/user-language")
                .principal(principal);

        mockMvc.perform(requestBuilder)
                .andExpect(MockMvcResultMatchers.status().isServiceUnavailable());

        verify(userService, times(1)).getPreferedLangByEmail(anyString());
    }

    @Test
    public void updateUserLanguage_accepted() throws Exception {
        Mockito.when(principal.getName()).thenReturn("TEST_EMAIL");
        Mockito.when(userService.findByEmail(anyString())).thenReturn(getMockUser());
        Mockito.when(userService.setPreferedLang(anyInt(), anyObject())).thenReturn(Boolean.TRUE);

        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .patch("/user-language/update")
                .param("language", "NEW_LANGUAGE")
                .principal(principal);

        mockMvc.perform(requestBuilder)
                .andExpect(MockMvcResultMatchers.status().isAccepted());

        verify(userService, times(1)).findByEmail(anyString());
        verify(userService, times(1)).setPreferedLang(anyInt(), anyObject());
    }

    @Test
    public void updateUserLanguage_invalid() throws Exception {
        Mockito.when(principal.getName()).thenReturn("TEST_EMAIL");
        Mockito.when(userService.findByEmail(anyString())).thenReturn(getMockUser());
        Mockito.when(userService.setPreferedLang(anyInt(), anyObject())).thenReturn(Boolean.FALSE);

        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .patch("/user-language/update")
                .param("language", "NEW_LANGUAGE")
                .principal(principal);

        try {
            mockMvc.perform(requestBuilder)
                    .andExpect(MockMvcResultMatchers.status().isBadRequest());
            Assert.fail();
        } catch (Exception e) {
            assertTrue(((NestedServletException) e).getRootCause() instanceof NgResponseException);
            NgResponseException responseException = (NgResponseException) ((NestedServletException) e).getRootCause();
            assertEquals("PREFERRED_LOCALE_NOT_SAVE", responseException.getTitle());

            String expected = "Preferred locale for user cannot save in DB";
            assertEquals(expected, e.getCause().getMessage());
        }

        verify(userService, times(1)).findByEmail(anyString());
        verify(userService, times(1)).setPreferedLang(anyInt(), anyObject());
    }
}