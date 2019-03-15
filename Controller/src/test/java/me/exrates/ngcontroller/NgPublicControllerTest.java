package me.exrates.ngcontroller;

import com.fasterxml.jackson.databind.ObjectMapper;
import me.exrates.dao.chat.telegram.TelegramChatDao;
import me.exrates.dao.exception.UserNotFoundException;
import me.exrates.model.ChatMessage;
import me.exrates.model.User;
import me.exrates.model.dto.ChatHistoryDto;
import me.exrates.model.enums.ChatLang;
import me.exrates.model.enums.CurrencyPairType;
import me.exrates.model.enums.UserStatus;
import me.exrates.ngService.NgOrderService;
import me.exrates.security.ipsecurity.IpBlockingService;
import me.exrates.security.service.NgUserService;
import me.exrates.service.ChatService;
import me.exrates.service.CurrencyService;
import me.exrates.service.OrderService;
import me.exrates.service.UserService;
import me.exrates.service.cache.ExchangeRatesHolder;
import me.exrates.service.exception.IllegalChatMessageException;
import me.exrates.service.notifications.G2faService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.handler.HandlerExceptionResolverComposite;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.*;
import static org.springframework.http.MediaType.APPLICATION_JSON_UTF8_VALUE;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


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
                .contentType(APPLICATION_JSON_UTF8_VALUE))
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
                .contentType(APPLICATION_JSON_UTF8_VALUE))
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
                .contentType(APPLICATION_JSON_UTF8_VALUE))
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
                .contentType(APPLICATION_JSON_UTF8_VALUE))
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
                .contentType(APPLICATION_JSON_UTF8_VALUE))
                .andExpect(status().isOk())
                .andExpect(content().string("false"));
    }

    @Test
    public void checkIfNewUserUsernameExists_WhenUsernameNotExists() throws Exception {
        when(userService.ifNicknameIsUnique("username")).thenReturn(Boolean.FALSE);

        mockMvc.perform(get("/api/public/v2/if_username_exists")
                .param("username", "username")
                .contentType(APPLICATION_JSON_UTF8_VALUE))
                .andExpect(status().isOk())
                .andExpect(content().string("true"));
    }

    @Test
    public void getChatMessages_WhenOK() throws Exception {
        ChatHistoryDto historyDto = new ChatHistoryDto();
        List<ChatHistoryDto> listHistoryDto = Arrays.asList(historyDto);

        when(telegramChatDao.getChatHistoryQuick(ChatLang.EN)).thenReturn(listHistoryDto);

        mockMvc.perform(get("/api/public/v2/chat/history")
                .param("lang", anyString())
                .contentType(APPLICATION_JSON_UTF8_VALUE))
                .andExpect(jsonPath("$[0].messages").exists());
    }

    @Test
    public void getChatMessages_WhenException() throws Exception {
        when(telegramChatDao.getChatHistoryQuick(ChatLang.EN)).thenThrow(Exception.class);

        mockMvc.perform(get("/api/public/v2/chat/history")
                .param("lang", anyString())
                .contentType(APPLICATION_JSON_UTF8_VALUE))
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    public void getAllPairs_WhenOk() throws Exception {
//        CurrencyPair currencyPair = new CurrencyPair();
//        List<CurrencyPair> currencyPairs = ImmutableList.of(currencyPair);
//kkk
//        when(currencyService.getAllCurrencyPairs(CurrencyPairType.MAIN)).thenReturn(currencyPairs);
//        mockMvc.perform(get("/api/public/v2/all-pairs")
//                .contentType(APPLICATION_JSON_UTF8_VALUE))
//                .andExpect(jsonPath("$").value(Matchers.contains("1")));


    }

    @Test
    public void getAllPairs_WhenEcxeption() throws Exception {
        when(currencyService.getAllCurrencyPairs(CurrencyPairType.MAIN)).thenThrow(Exception.class);

        mockMvc.perform(get("/api/public/v2/all-pairs")
                .contentType(APPLICATION_JSON_UTF8_VALUE))
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    public void sendChatMessage_WhenEmptySimpleMessage() throws Exception {
        Map<String,String> body = new HashMap<>();

        mockMvc.perform(post("/api/public/v2/chat")
                .contentType(APPLICATION_JSON_UTF8_VALUE)
                .content(objectMapper.writeValueAsBytes(body)))
                .andExpect(status().isBadRequest());

    }

    @Test
    public void sendChatMessage_WhenIllegalChatMessageException() throws Exception {
        Map<String,String> body = new HashMap<>();
        when(chatService.persistPublicMessage(anyString(), anyString(), anyObject())).thenThrow(IllegalChatMessageException.class);

        mockMvc.perform(post("/api/public/v2/chat")
                .contentType(APPLICATION_JSON_UTF8_VALUE)
                .content(objectMapper.writeValueAsBytes(body)))
                .andExpect(status().isBadRequest());

    }

//    TODO: method does not work
    @Test
    public void sendChatMessage_WhenOK() throws Exception {
        Map<String,String> body = new HashMap<>();
        body.put("MESSAGE","gfg");
        body.put("LANG", "EN");
        body.put("EMAIL", "");
        ChatMessage chatMessage = new ChatMessage();
        when(chatService.persistPublicMessage(anyString(), anyString(), anyObject())).thenReturn(chatMessage);
        doNothing().when(messagingTemplate).convertAndSend(anyString(), anyString());

        mockMvc.perform(post("/api/public/v2/chat")
                .contentType(APPLICATION_JSON_UTF8_VALUE)
                .content(objectMapper.writeValueAsBytes(body)))
                .andExpect(status().isOk());
    }

    @Test
    public void getOpenOrders() throws Exception {
//        List<OrderBookWrapperDto> orderBookWrapperDto = Arrays.asList(new OrderBookWrapperDto( OrderType.SELL,
//        anyString(), anyString(), anyBoolean(), new BigDecimal(40),
//        new ArrayList<SimpleOrderBookItem>()));
//
//        int first = 1;
//        int second = 1;
//        when(orderService.findAllOrderBookItems(anyObject(), first, second)).thenReturn(orderBookWrapperDto);
//        mockMvc.perform(get("/api/public/v2/open-orders/{pairId}/{precision}",
//                first,second)
//                .contentType(APPLICATION_JSON_UTF8_VALUE))
//                .andExpect(jsonPath("$", hasSize(2)));
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