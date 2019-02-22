package me.exrates.ngcontroller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import me.exrates.controller.openAPI.TestUtils;
import me.exrates.model.User;
import me.exrates.service.UserService;
import me.exrates.service.exception.UserNotFoundException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static junit.framework.TestCase.assertTrue;
import static junit.framework.TestCase.fail;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {AngularAppTestConfig.class})
@WebAppConfiguration
public class NgPublicControllerTest {

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserService userService;

    private MockMvc mockMvc;

    @Before
    public void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }

    @Test
    public void checkIfNewUserEmailExists_whenUserNotFound() throws Exception {
        when(userService.findByEmail(anyString())).thenThrow(UserNotFoundException.class);
        try {
            mockMvc.perform(MockMvcRequestBuilders.get("/api/public/v2/if_email_exists?email=test@test.com")
                    .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE));
            fail("UserNotFoundException is expected");
        } catch (Exception e) {
            assertTrue(true);
        }
    }

    @Test
    public void checkIfNewUserEmailExists_whenOk() throws Exception {
        when(userService.findByEmail(anyString())).thenReturn(Mockito.mock(User.class));
        mockMvc.perform(MockMvcRequestBuilders.get("/api/public/v2/if_email_exists?email=test@test.com")
                .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE)
                .content(objectMapper.writeValueAsString(Boolean.class)))
                .andExpect(MockMvcResultMatchers.status().isOk());

        verify(userService, times(1)).findByEmail(anyString());
        verifyNoMoreInteractions(userService);
    }

    @Test
    public void isGoogleTwoFAEnabled() {
    }

    @Test
    public void checkIfNewUserUsernameExists() {
    }

    @Test
    public void getChatMessages() {
    }

    @Test
    public void getAllPairs() {
    }

    @Test
    public void sendChatMessage() {
    }

    @Test
    public void getOpenOrders() {
    }

    @Test
    public void getCurrencyPairInfo() {
    }

    @Test
    public void getMaxCurrencyPair24h() {
    }

    @Test
    public void getCurrencyPairInfoAll() {
    }

    @Test
    public void getLastAcceptedOrders() {
    }

    @Test
    public void getPairsByPartName() {
    }

    @Test
    public void getCryptoCurrencies() {
    }

    @Test
    public void getFiatCurrencies() {
    }

}