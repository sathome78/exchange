package me.exrates.ngcontroller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Lists;
import me.exrates.dao.chat.telegram.TelegramChatDao;
import me.exrates.dao.exception.UserNotFoundException;
import me.exrates.model.User;
import me.exrates.model.dto.ChatHistoryDateWrapperDto;
import me.exrates.model.dto.ChatHistoryDto;
import me.exrates.model.enums.ChatLang;
import me.exrates.model.enums.UserStatus;
import me.exrates.ngcontroller.service.NgOrderService;
import me.exrates.ngcontroller.service.NgUserService;
import me.exrates.security.ipsecurity.IpBlockingService;
import me.exrates.service.ChatService;
import me.exrates.service.CurrencyService;
import me.exrates.service.OrderService;
import me.exrates.service.UserService;
import me.exrates.service.cache.ExchangeRatesHolder;
import me.exrates.service.notifications.G2faService;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatcher;
import org.mockito.InjectMocks;
import org.mockito.Matchers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.handler.HandlerExceptionResolverComposite;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.mockito.Matchers.*;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


//https://www.baeldung.com/integration-testing-in-spring
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {AngularAppTestConfig.class})
@WebAppConfiguration
public class NgPublicControllerTest {

    private static final String EMAIL = "test@test.com";

    @Autowired
    private WebApplicationContext webApplicationContext;

    @InjectMocks
    private NgPublicController ngPublicController;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private UserService userService;
    @Autowired
    private NgUserService ngUserService;
    @Autowired
    private ChatService chatService;
    @Autowired
    private CurrencyService currencyService;
    @Autowired
    private IpBlockingService ipBlockingService;
    @Autowired
    private SimpMessagingTemplate messagingTemplate;
    @Autowired
    private OrderService orderService;
    @Autowired
    private G2faService g2faService;
    @Autowired
    private NgOrderService ngOrderService;
    @Autowired
    private TelegramChatDao telegramChatDao;
    @Autowired
    private ExchangeRatesHolder exchangeRatesHolder;

    private MockMvc mockMvc;

    @Before
    public void setUp() {
        ngPublicController = new NgPublicController(chatService, currencyService, ipBlockingService, userService, ngUserService,
                messagingTemplate, orderService, g2faService, ngOrderService, telegramChatDao, exchangeRatesHolder);
        HandlerExceptionResolver resolver =
                ((HandlerExceptionResolverComposite) webApplicationContext.getBean("handlerExceptionResolver")).getExceptionResolvers().get(0);
        mockMvc = MockMvcBuilders.standaloneSetup(ngPublicController)
                .setHandlerExceptionResolvers(resolver)
                .setMessageConverters(new MappingJackson2HttpMessageConverter())
                .build();
    }

    @Test
    public void checkIfNewUserEmailExists_whenOk() throws Exception {
        User user = new User();
        user.setStatus(UserStatus.ACTIVE);
        when(userService.findByEmail(anyString())).thenReturn(user);
        mockMvc.perform(get("/api/public/v2/if_email_exists")
                .param("email", EMAIL)
                .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(status().isOk());

        verify(userService, times(1)).findByEmail(anyString());
        verifyNoMoreInteractions(userService);
    }

    @Test
    public void checkIfNewUserEmailExists_whenUserNotFound() throws Exception {
        when(userService.findByEmail(anyString())).thenThrow(UserNotFoundException.class);

        String actualMessage = String.format("User with email %s not found", EMAIL);
        mockMvc.perform(get("/api/public/v2/if_email_exists")
                .param("email", EMAIL)
                .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.title").value("USER_EMAIL_NOT_FOUND"))
                .andExpect(jsonPath("$.detail").value(actualMessage));
    }

    @Test
    public void checkIfNewUserEmailExists_whenRegistrationIncomplete() throws Exception {
        User user = new User();
        user.setStatus(UserStatus.REGISTERED);
        when(userService.findByEmail(anyString())).thenReturn(user);
        doNothing().when(ngUserService).resendEmailForFinishRegistration(anyObject());

        String actualMessage = String.format("User with email %s registration is not complete", EMAIL);
        mockMvc.perform(get("/api/public/v2/if_email_exists")
                .param("email", EMAIL)
                .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.title").value("USER_REGISTRATION_NOT_COMPLETED"))
                .andExpect(jsonPath("$.detail").value(actualMessage));
    }

    @Test
    public void checkIfNewUserEmailExists_whenUserDeleted() throws Exception {
        User user = new User();
        user.setStatus(UserStatus.DELETED);
        when(userService.findByEmail(anyString())).thenReturn(user);
        doNothing().when(ngUserService).resendEmailForFinishRegistration(anyObject());
        String actualMessage = String.format("User with email %s is not active", EMAIL);
        mockMvc.perform(get("/api/public/v2/if_email_exists")
                .param("email", EMAIL)
                .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.title").value("USER_NOT_ACTIVE"))
                .andExpect(jsonPath("$.detail").value(actualMessage));
    }

    @Test
    public void isGoogleTwoFAEnabled_WhenTrue() throws Exception{
        when(g2faService.isGoogleAuthenticatorEnable(EMAIL)).thenReturn(Boolean.TRUE);

        mockMvc.perform(get("/api/public/v2/is_google_2fa_enabled?email={email}"
                , EMAIL)
                .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(content().string("true"));

    }

    @Test
    public void isGoogleTwoFAEnabled_WhenFalse() throws Exception{
        when(g2faService.isGoogleAuthenticatorEnable(EMAIL)).thenReturn(Boolean.FALSE);

        mockMvc.perform(get("/api/public/v2/is_google_2fa_enabled")
                .param("email", EMAIL)
                .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(content().string("false"));

    }

    @Test
    public void checkIfNewUserUsernameExists_WhenUsernameExists() throws Exception {
        when(userService.ifNicknameIsUnique("username")).thenReturn(Boolean.TRUE);

        mockMvc.perform(get("/api/public/v2/if_username_exists")
                .param("username", "username")
                .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(status().isOk())
                .andExpect(content().string("false"));
    }

    @Test
    public void checkIfNewUserUsernameExists_WhenUsernameNotExists() throws Exception {
        when(userService.ifNicknameIsUnique("username")).thenReturn(Boolean.FALSE);

        mockMvc.perform(get("/api/public/v2/if_username_exists")
                .param("username", "username")
                .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(status().isOk())
                .andExpect(content().string("true"));
    }

    @Test
    public void getChatMessages_WhenOK() throws Exception {
//        ChatHistoryDto historyDto = new ChatHistoryDto();
//        LocalDate date = LocalDate.now();
//        ChatHistoryDateWrapperDto dto = new ChatHistoryDateWrapperDto(date, Arrays.asList(historyDto));
//
//        when(Lists.newArrayList(new ChatHistoryDateWrapperDto(LocalDate.now(),telegramChatDao.getChatHistoryQuick(ChatLang.EN))).thenReturn(Arrays.asList(dto)));
//        List<ChatHistoryDto> chatHistoryDto = Lists.newArrayList(telegramChatDao.getChatHistoryQuick(ChatLang.EN));
//        List<ChatHistoryDateWrapperDto> chatHistoryDateWrapperDto = Lists.newArrayList(new ChatHistoryDateWrapperDto(LocalDate.now(),
//                chatHistoryDto));

//        mockMvc.perform(get("/api/public/v2/chat/history")
//                .param("lang", anyString())
//                .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
//                .andReturn();

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