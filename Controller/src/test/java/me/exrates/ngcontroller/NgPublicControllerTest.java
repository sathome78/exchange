package me.exrates.ngcontroller;

import com.fasterxml.jackson.databind.ObjectMapper;
import me.exrates.dao.chat.telegram.TelegramChatDao;
import me.exrates.dao.exception.UserNotFoundException;
import me.exrates.model.User;
import me.exrates.model.dto.ChatHistoryDto;
import me.exrates.model.enums.ChatLang;
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

import java.util.*;

import static org.hamcrest.Matchers.*;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.*;
import static org.springframework.http.MediaType.APPLICATION_JSON_UTF8_VALUE;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


//https://www.baeldung.com/integration-testing-in-spring
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {AngularAppTestConfig.class})
@WebAppConfiguration
public class NgPublicControllerTest extends AngularApiCommonTest {
    private static final String BASE_URL = "/api/public/v2";
    private static final String EMAIL = "test@test.com";

    @Autowired
    private WebApplicationContext webApplicationContext;
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

    @InjectMocks
    private NgPublicController ngPublicController;

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
        mockMvc.perform(get(BASE_URL + "/if_email_exists")
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
        mockMvc.perform(get(BASE_URL + "/if_email_exists")
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
        mockMvc.perform(get(BASE_URL + "/if_email_exists")
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
        mockMvc.perform(get(BASE_URL + "/if_email_exists")
                .param("email", EMAIL)
                .contentType(APPLICATION_JSON_UTF8_VALUE))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.title").value("USER_NOT_ACTIVE"))
                .andExpect(jsonPath("$.detail").value(actualMessage));
    }

    @Test
    public void isGoogleTwoFAEnabled_WhenTrue() throws Exception {
        when(g2faService.isGoogleAuthenticatorEnable(EMAIL)).thenReturn(Boolean.TRUE);

        mockMvc.perform(get(BASE_URL + "/is_google_2fa_enabled?email={email}"
                , EMAIL)
                .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(content().string("true"));
    }

    @Test
    public void isGoogleTwoFAEnabled_WhenFalse() throws Exception {
        when(g2faService.isGoogleAuthenticatorEnable(EMAIL)).thenReturn(Boolean.FALSE);

        mockMvc.perform(get(BASE_URL + "/is_google_2fa_enabled")
                .param("email", EMAIL)
                .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(content().string("false"));
    }

    @Test
    public void checkIfNewUserUsernameExists_WhenUsernameExists() throws Exception {
        when(userService.ifNicknameIsUnique("username")).thenReturn(Boolean.TRUE);

        mockMvc.perform(get(BASE_URL + "/if_username_exists")
                .param("username", "username")
                .contentType(APPLICATION_JSON_UTF8_VALUE))
                .andExpect(status().isOk())
                .andExpect(content().string("false"));
    }

    @Test
    public void checkIfNewUserUsernameExists_WhenUsernameNotExists() throws Exception {
        when(userService.ifNicknameIsUnique("username")).thenReturn(Boolean.FALSE);

        mockMvc.perform(get(BASE_URL + "/if_username_exists")
                .param("username", "username")
                .contentType(APPLICATION_JSON_UTF8_VALUE))
                .andExpect(status().isOk())
                .andExpect(content().string("true"));
    }

    @Test
    public void getChatMessages_WhenOK() throws Exception {
        ChatHistoryDto historyDto = new ChatHistoryDto();
        List<ChatHistoryDto> listHistoryDto = Arrays.asList(historyDto);

        when(telegramChatDao.getChatHistoryQuick(anyObject())).thenReturn(listHistoryDto);

        mockMvc.perform(get(BASE_URL + "/chat/history")
                .param("lang", ChatLang.EN.toString())
                .contentType(APPLICATION_JSON_UTF8_VALUE))
                .andExpect(jsonPath("$[0].messages").exists());
    }

    @Test
    public void getChatMessages_WhenException() throws Exception {
        when(telegramChatDao.getChatHistoryQuick(ChatLang.EN)).thenThrow(Exception.class);

        mockMvc.perform(get(BASE_URL + "/chat/history")
                .param("lang", anyString())
                .contentType(APPLICATION_JSON_UTF8_VALUE))
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    public void getAllPairs_WhenOk() throws Exception {
        when(currencyService.getAllCurrencyPairs(anyObject())).thenReturn(Collections.singletonList(getMockCurrencyPair()));

        mockMvc.perform(get(BASE_URL + "/all-pairs")
                .contentType(APPLICATION_JSON_UTF8_VALUE))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", is(100)))
                .andExpect(jsonPath("$[0].name", is("TEST_NAME")))
                .andExpect(jsonPath("$[0].currency1.id", is(100)))
                .andExpect(jsonPath("$[0].currency1.name", is("TEST_NAME")))
                .andExpect(jsonPath("$[0].currency1.description", is("TEST_DESCRIPTION")))
                .andExpect(jsonPath("$[0].currency1.hidden", is(Boolean.TRUE)))
                .andExpect(jsonPath("$[0].currency2.id", is(100)))
                .andExpect(jsonPath("$[0].currency2.name", is("TEST_NAME")))
                .andExpect(jsonPath("$[0].currency2.description", is("TEST_DESCRIPTION")))
                .andExpect(jsonPath("$[0].currency2.hidden", is(Boolean.TRUE)))
                .andExpect(jsonPath("$[0].market", is("TEST_MARKET")))
                .andExpect(jsonPath("$[0].marketName", is("TEST_MARKET_NAME")))
                .andExpect(jsonPath("$[0].pairType", is("ALL")))
                .andExpect(jsonPath("$[0].hidden", is(Boolean.TRUE)))
                .andExpect(jsonPath("$[0].permittedLink", is(Boolean.TRUE)));

        verify(currencyService, times(1)).getAllCurrencyPairs(anyObject());
    }

    @Test
    public void getAllPairs_WhenEcxeption() throws Exception {
        when(currencyService.getAllCurrencyPairs(anyObject())).thenThrow(Exception.class);

        mockMvc.perform(get(BASE_URL + "/all-pairs")
                .contentType(APPLICATION_JSON_UTF8_VALUE))
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    public void sendChatMessage_WhenOK() throws Exception {
        Map<String, String> body = new HashMap<>();
        body.put("MESSAGE", "TEST_MESSAGE");
        body.put("LANG", "EN");
        body.put("EMAIL", "testemail@gmail.com");

        when(chatService.persistPublicMessage(anyString(), anyString(), anyObject())).thenReturn(getMockChatMessage());
        doNothing().when(messagingTemplate).convertAndSend(anyString(), anyString());

        mockMvc.perform(post(BASE_URL + "/chat")
                .contentType(APPLICATION_JSON_UTF8_VALUE)
                .content(objectMapper.writeValueAsBytes(body)))
                .andDo(print())
                .andExpect(status().isOk());

        verify(chatService, times(1)).persistPublicMessage(anyString(), anyString(), anyObject());
        verify(messagingTemplate, times(1)).convertAndSend(anyString(), anyString());
    }

    @Test
    public void sendChatMessage_WhenEmptySimpleMessage() throws Exception {
        Map<String, String> body = new HashMap<>();

        mockMvc.perform(post(BASE_URL + "/chat")
                .contentType(APPLICATION_JSON_UTF8_VALUE)
                .content(objectMapper.writeValueAsBytes(body)))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void sendChatMessage_WhenIllegalChatMessageException() throws Exception {
        Map<String, String> body = new HashMap<>();

        when(chatService.persistPublicMessage(anyString(), anyString(), anyObject())).thenThrow(IllegalChatMessageException.class);

        mockMvc.perform(post(BASE_URL + "/chat")
                .contentType(APPLICATION_JSON_UTF8_VALUE)
                .content(objectMapper.writeValueAsBytes(body)))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void sendChatMessage_bad_request() throws Exception {
        Map<String, String> body = new HashMap<>();
        body.put("MESSAGE", "TEST{}MESSAGE");
        body.put("LANG", "EN");
        body.put("EMAIL", "testemail@gmail.com");

        when(chatService.persistPublicMessage(anyString(), anyString(), anyObject())).thenThrow(IllegalChatMessageException.class);

        mockMvc.perform(post(BASE_URL + "/chat")
                .contentType(APPLICATION_JSON_UTF8_VALUE)
                .content(objectMapper.writeValueAsBytes(body)))
                .andDo(print())
                .andExpect(status().isBadRequest());

        verify(chatService, times(1)).persistPublicMessage(anyString(), anyString(), anyObject());
        verify(messagingTemplate, never()).convertAndSend(anyString(), anyString());
    }

    @Test
    public void getOpenOrders() throws Exception {
        when(orderService.findAllOrderBookItems(anyObject(), anyInt(), anyInt())).thenReturn(getMockOrderBookWrapperDto());

        mockMvc.perform(get(BASE_URL + "/open-orders/{pairId}/{precision}", 0, 5)
                .contentType(APPLICATION_JSON_UTF8_VALUE))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].orderType", is("SELL")))
                .andExpect(jsonPath("$[0].lastExrate", is("TEST_LAST_EXRATE")))
                .andExpect(jsonPath("$[0].preLastExrate", is("TEST_PRE_LAST_EXRATE")))
                .andExpect(jsonPath("$[0].positive", is(Boolean.TRUE)))
                .andExpect(jsonPath("$[0].total", is("25")))
                .andExpect(jsonPath("$[0].orderBookItems", is(Collections.EMPTY_LIST)));

        verify(orderService, times(2)).findAllOrderBookItems(anyObject(), anyInt(), anyInt());
    }

    @Test
    public void getCurrencyPairInfo_isOk() throws Exception {
        when(ngOrderService.getCurrencyPairInfo(anyInt())).thenReturn(getMockResponseInfoCurrencyPairDto());

        mockMvc.perform(get(BASE_URL + "/info/{currencyPairId}", 100)
                .contentType(APPLICATION_JSON_UTF8_VALUE))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.currencyRate", is("TEST_CURRENCY_RATE")))
                .andExpect(jsonPath("$.percentChange", is("TEST_PERCENT_CHANGE")))
                .andExpect(jsonPath("$.changedValue", is("TEST_CHANGED_VALUE")))
                .andExpect(jsonPath("$.lastCurrencyRate", is("TEST_LAST_CURRENCY_RATE")))
                .andExpect(jsonPath("$.volume24h", is("TEST_VOLUME_24H")))
                .andExpect(jsonPath("$.rateHigh", is("TEST_RATE_HIGH")))
                .andExpect(jsonPath("$.rateLow", is("TEST_RATE_LOW")));

        verify(ngOrderService, times(1)).getCurrencyPairInfo(anyInt());
    }

    @Test
    public void getCurrencyPairInfo_exception() throws Exception {
        when(ngOrderService.getCurrencyPairInfo(anyInt())).thenThrow(Exception.class);

        mockMvc.perform(get(BASE_URL + "/info/{currencyPairId}", 100)
                .contentType(APPLICATION_JSON_UTF8_VALUE))
                .andDo(print())
                .andExpect(status().isBadRequest());

        verify(ngOrderService, times(1)).getCurrencyPairInfo(anyInt());
    }

    @Test
    public void getMaxCurrencyPair24h_isOk() throws Exception {
        when(exchangeRatesHolder.getAllRates()).thenReturn(Collections.singletonList(getMockExOrderStatisticsShortByPairsDto()));

        mockMvc.perform(get(BASE_URL + "/info/max/{name}", "TEST_CURRENCY_PAIR_NAME")
                .contentType(APPLICATION_JSON_UTF8_VALUE))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.needRefresh", is(Boolean.TRUE)))
                .andExpect(jsonPath("$.data.page", is(0)))
                .andExpect(jsonPath("$.data.currencyPairId", is(100)))
                .andExpect(jsonPath("$.data.currencyPairName", is("TEST_CURRENCY_PAIR_NAME")))
                .andExpect(jsonPath("$.data.currencyPairPrecision", is(200)))
                .andExpect(jsonPath("$.data.lastOrderRate", is("TEST_LAST_ORDER_RATE")))
                .andExpect(jsonPath("$.data.predLastOrderRate", is("TEST_PRED_LAST_ORDER_RATE")))
                .andExpect(jsonPath("$.data.percentChange", is("TEST_PERCENT_CHANGE")))
                .andExpect(jsonPath("$.data.market", is("TEST_MARKET")))
                .andExpect(jsonPath("$.data.priceInUSD", is("TEST_PRICE_IN_USD")))
                .andExpect(jsonPath("$.data.type", is("MAIN")))
                .andExpect(jsonPath("$.data.volume", is("TEST_VOLUME")))
                .andExpect(jsonPath("$.data.currencyVolume", is("TEST_CURRENCY_VOLUME")))
                .andExpect(jsonPath("$.data.high24hr", is("TEST_HIGH_24H")))
                .andExpect(jsonPath("$.data.low24hr", is("TEST_LOW_24H")))
                .andExpect(jsonPath("$.data.hidden", is(Boolean.TRUE)))
                .andExpect(jsonPath("$.data.lastUpdateCache", is("TEST_LAST_UPDATE_CACHE")))
                .andExpect(jsonPath("$.error", is(nullValue())));

        verify(exchangeRatesHolder, times(1)).getAllRates();
    }

    @Test
    public void getMaxCurrencyPair24h_exception() throws Exception {
        String ngDashboardException = "No results";

        when(exchangeRatesHolder.getAllRates()).thenReturn(Collections.singletonList(getMockExOrderStatisticsShortByPairsDto()));

        mockMvc.perform(get(BASE_URL + "/info/max/{name}", "TEST_WRONG_NAME")
                .contentType(APPLICATION_JSON_UTF8_VALUE))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.detail", is(ngDashboardException)));

        verify(exchangeRatesHolder, times(1)).getAllRates();
    }

    @Test
    public void getCurrencyPairInfoAll_isOk() throws Exception {
        when(orderService.getAllCurrenciesMarkersForAllPairsModel())
                .thenReturn(Collections.singletonList(getMockExOrderStatisticsShortByPairsDto()));

        mockMvc.perform(get(BASE_URL + "/currencies/fast")
                .contentType(APPLICATION_JSON_UTF8_VALUE))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.[0].needRefresh", is(Boolean.TRUE)))
                .andExpect(jsonPath("$.[0].page", is(0)))
                .andExpect(jsonPath("$.[0].currencyPairId", is(100)))
                .andExpect(jsonPath("$.[0].currencyPairName", is("TEST_CURRENCY_PAIR_NAME")))
                .andExpect(jsonPath("$.[0].currencyPairPrecision", is(200)))
                .andExpect(jsonPath("$.[0].lastOrderRate", is("TEST_LAST_ORDER_RATE")))
                .andExpect(jsonPath("$.[0].predLastOrderRate", is("TEST_PRED_LAST_ORDER_RATE")))
                .andExpect(jsonPath("$.[0].percentChange", is("TEST_PERCENT_CHANGE")))
                .andExpect(jsonPath("$.[0].market", is("TEST_MARKET")))
                .andExpect(jsonPath("$.[0].priceInUSD", is("TEST_PRICE_IN_USD")))
                .andExpect(jsonPath("$.[0].type", is("MAIN")))
                .andExpect(jsonPath("$.[0].volume", is("TEST_VOLUME")))
                .andExpect(jsonPath("$.[0].currencyVolume", is("TEST_CURRENCY_VOLUME")))
                .andExpect(jsonPath("$.[0].high24hr", is("TEST_HIGH_24H")))
                .andExpect(jsonPath("$.[0].low24hr", is("TEST_LOW_24H")))
                .andExpect(jsonPath("$.[0].hidden", is(Boolean.TRUE)))
                .andExpect(jsonPath("$.[0].lastUpdateCache", is("TEST_LAST_UPDATE_CACHE")));

        verify(orderService, times(1)).getAllCurrenciesMarkersForAllPairsModel();
    }

    @Test
    public void getLastAcceptedOrders_isOk() throws Exception {
        when(currencyService.findCurrencyPairById(anyInt())).thenReturn(getMockCurrencyPair());
        when(orderService.getOrderAcceptedForPeriodEx(anyObject(), anyObject(), anyInt(), anyObject(), anyObject()))
                .thenReturn(Collections.singletonList(getMockOrderAcceptedHistoryDto()));

        mockMvc.perform(get(BASE_URL + "/accepted-orders/fast")
                .param("pairId", "1")
                .contentType(APPLICATION_JSON_UTF8_VALUE))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.[0].needRefresh", is(Boolean.TRUE)))
                .andExpect(jsonPath("$.[0].page", is(0)))
                .andExpect(jsonPath("$.[0].orderId", is(500)))
                .andExpect(jsonPath("$.[0].dateAcceptionTime", is("TEST_DATE_ACCEPTION_TIME")))
                .andExpect(jsonPath("$.[0].acceptionTime", is(1552655155000L)))
                .andExpect(jsonPath("$.[0].rate", is("TEST_RATE")))
                .andExpect(jsonPath("$.[0].amountBase", is("TEST_AMOUNT_BASE")))
                .andExpect(jsonPath("$.[0].operationType", is("BUY")));

        verify(currencyService, times(1)).findCurrencyPairById(anyInt());
        verify(orderService, times(1)).getOrderAcceptedForPeriodEx(anyObject(), anyObject(), anyInt(), anyObject(), anyObject());
    }

    @Test
    public void getPairsByPartName_isOk_part_equals_first() throws Exception {
        when(ngOrderService.getAllPairsByFirstPartName(anyString())).thenReturn(Collections.singletonList(getMockCurrencyPair()));

        mockMvc.perform(get(BASE_URL + "/pair/{part}/{name}", "first", "TEST_NAME")
                .param("pairId", "1")
                .contentType(APPLICATION_JSON_UTF8_VALUE))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", is(100)))
                .andExpect(jsonPath("$[0].name", is("TEST_NAME")))
                .andExpect(jsonPath("$[0].currency1.id", is(100)))
                .andExpect(jsonPath("$[0].currency1.name", is("TEST_NAME")))
                .andExpect(jsonPath("$[0].currency1.description", is("TEST_DESCRIPTION")))
                .andExpect(jsonPath("$[0].currency1.hidden", is(Boolean.TRUE)))
                .andExpect(jsonPath("$[0].currency2.id", is(100)))
                .andExpect(jsonPath("$[0].currency2.name", is("TEST_NAME")))
                .andExpect(jsonPath("$[0].currency2.description", is("TEST_DESCRIPTION")))
                .andExpect(jsonPath("$[0].currency2.hidden", is(Boolean.TRUE)))
                .andExpect(jsonPath("$[0].market", is("TEST_MARKET")))
                .andExpect(jsonPath("$[0].marketName", is("TEST_MARKET_NAME")))
                .andExpect(jsonPath("$[0].pairType", is("ALL")))
                .andExpect(jsonPath("$[0].hidden", is(Boolean.TRUE)))
                .andExpect(jsonPath("$[0].permittedLink", is(Boolean.TRUE)));

        verify(ngOrderService, times(1)).getAllPairsByFirstPartName(anyString());
    }

    @Test
    public void getPairsByPartName_isOk_part_notequals_first() throws Exception {
        when(ngOrderService.getAllPairsBySecondPartName(anyString())).thenReturn(Collections.singletonList(getMockCurrencyPair()));

        mockMvc.perform(get(BASE_URL + "/pair/{part}/{name}", "tsrif", "TEST_NAME")
                .param("pairId", "1")
                .contentType(APPLICATION_JSON_UTF8_VALUE))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", is(100)))
                .andExpect(jsonPath("$[0].name", is("TEST_NAME")))
                .andExpect(jsonPath("$[0].currency1.id", is(100)))
                .andExpect(jsonPath("$[0].currency1.name", is("TEST_NAME")))
                .andExpect(jsonPath("$[0].currency1.description", is("TEST_DESCRIPTION")))
                .andExpect(jsonPath("$[0].currency1.hidden", is(Boolean.TRUE)))
                .andExpect(jsonPath("$[0].currency2.id", is(100)))
                .andExpect(jsonPath("$[0].currency2.name", is("TEST_NAME")))
                .andExpect(jsonPath("$[0].currency2.description", is("TEST_DESCRIPTION")))
                .andExpect(jsonPath("$[0].currency2.hidden", is(Boolean.TRUE)))
                .andExpect(jsonPath("$[0].market", is("TEST_MARKET")))
                .andExpect(jsonPath("$[0].marketName", is("TEST_MARKET_NAME")))
                .andExpect(jsonPath("$[0].pairType", is("ALL")))
                .andExpect(jsonPath("$[0].hidden", is(Boolean.TRUE)))
                .andExpect(jsonPath("$[0].permittedLink", is(Boolean.TRUE)));

        verify(ngOrderService, times(1)).getAllPairsBySecondPartName(anyString());
    }

    @Test
    public void getCryptoCurrencies_isOk() throws Exception {
        when(currencyService.getCurrencies(anyObject())).thenReturn(Collections.singletonList(getMockCurrency("RUB")));

        mockMvc.perform(get(BASE_URL + "/crypto-currencies")
                .contentType(APPLICATION_JSON_UTF8_VALUE))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", is(100)))
                .andExpect(jsonPath("$[0].name", is("RUB")))
                .andExpect(jsonPath("$[0].description", is("TEST_DESCRIPTION")))
                .andExpect(jsonPath("$[0].hidden", is(Boolean.TRUE)));

        verify(currencyService, times(1)).getCurrencies(anyObject());
    }

    @Test
    public void getCryptoCurrencies_exception() throws Exception {
        when(currencyService.getCurrencies(anyObject())).thenThrow(Exception.class);

        mockMvc.perform(get(BASE_URL + "/crypto-currencies")
                .contentType(APPLICATION_JSON_UTF8_VALUE))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.*", is(Collections.EMPTY_LIST)));

        verify(currencyService, times(1)).getCurrencies(anyObject());
    }

    @Test
    public void getFiatCurrencies_isOk() throws Exception {
        when(currencyService.getCurrencies(anyObject(), anyObject())).thenReturn(Collections.singletonList(getMockCurrency("TEST_NAME")));

        mockMvc.perform(get(BASE_URL + "/fiat-currencies")
                .contentType(APPLICATION_JSON_UTF8_VALUE))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", is(100)))
                .andExpect(jsonPath("$[0].name", is("TEST_NAME")))
                .andExpect(jsonPath("$[0].description", is("TEST_DESCRIPTION")))
                .andExpect(jsonPath("$[0].hidden", is(Boolean.TRUE)));

        verify(currencyService, times(1)).getCurrencies(anyObject(), anyObject());
    }

    @Test
    public void getFiatCurrencies_exception() throws Exception {
        when(currencyService.getCurrencies(anyObject(), anyObject())).thenThrow(Exception.class);

        mockMvc.perform(get(BASE_URL + "/fiat-currencies")
                .contentType(APPLICATION_JSON_UTF8_VALUE))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.*", is(Collections.EMPTY_LIST)));

        verify(currencyService, times(1)).getCurrencies(anyObject(), anyObject());
    }
}