package me.exrates.ngcontroller;

import com.fasterxml.jackson.databind.ObjectMapper;
import me.exrates.model.CurrencyPair;
import me.exrates.model.dto.InputCreateOrderDto;
import me.exrates.model.dto.onlineTableDto.OrderWideListDto;
import me.exrates.model.enums.OperationType;
import me.exrates.model.enums.OrderStatus;
import me.exrates.ngService.NgOrderService;
import me.exrates.service.CurrencyService;
import me.exrates.service.DashboardService;
import me.exrates.service.OrderService;
import me.exrates.service.UserService;
import me.exrates.service.stopOrder.StopOrderService;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.servlet.LocaleResolver;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyBoolean;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyMapOf;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class NgDashboardControllerTest extends AngularApiCommonTest {
    private static final String BASE_URL = "/api/private/v2/dashboard";

    @Mock
    private DashboardService dashboardService;
    @Mock
    private CurrencyService currencyService;
    @Mock
    private OrderService orderService;
    @Mock
    private UserService userService;
    @Mock
    private LocaleResolver localeResolver;
    @Mock
    private NgOrderService ngOrderService;
    @Mock
    private ObjectMapper objectMapper;
    @Mock
    private SimpMessagingTemplate simpMessagingTemplate;
    @Mock
    private StopOrderService stopOrderService;
    @Mock
    private StopOrderService stopOrderServiceImpl;

    @InjectMocks
    NgDashboardController ngDashboardController;

    private MockMvc mockMvc;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        mockMvc = MockMvcBuilders
                .standaloneSetup(ngDashboardController)
                .build();

        SecurityContextHolder.getContext()
                .setAuthentication(new AnonymousAuthenticationToken("GUEST", "testemail@gmail.com",
                        AuthorityUtils.createAuthorityList("ADMIN")));
    }

    // TODO: Test failed
    @Ignore
    public void createOrder() throws Exception {
        InputCreateOrderDto dto = getMockInputCreateOrderDto();
        Mockito.when(ngOrderService.prepareOrder(anyObject())).thenReturn(getMockOrderCreateDto());
        Mockito.when(orderService.createOrder(anyObject(), anyObject(), anyObject())).thenReturn("TEST_RESULT");

        mockMvc.perform(post(BASE_URL + "/order")
                .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE)
                .content(objectMapper.writeValueAsString(dto)))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    public void deleteOrderById_isOk() throws Exception {
        Integer id = 1;
        Mockito.when(orderService.deleteOrderByAdmin(anyInt())).thenReturn(id);

        mockMvc.perform(delete(BASE_URL + "/order/{id}", id)
                .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(status().isOk());

        verify(orderService, times(1)).deleteOrderByAdmin(anyInt());
    }

    @Test
    public void deleteOrderById_bad_request() throws Exception {
        Integer id = 5;
        Mockito.when(orderService.deleteOrderByAdmin(anyInt())).thenReturn(id);

        mockMvc.perform(delete(BASE_URL + "/order/{id}", id)
                .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(status().isBadRequest());

        verify(orderService, times(1)).deleteOrderByAdmin(anyInt());
    }

    // TODO: Test failed
    @Ignore
    public void updateOrder() throws Exception {
        InputCreateOrderDto dto = getMockInputCreateOrderDto();

        mockMvc.perform(put(BASE_URL + "/order")
                .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE)
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void getBalanceByCurrency_isOk() throws Exception {
        Mockito.when(userService.getUserEmailFromSecurityContext()).thenReturn("TEST_USER_NAME");
        Mockito.when(userService.findByEmail(anyString())).thenReturn(getMockUser());
        Mockito.when(currencyService.findByName(anyString())).thenReturn(getMockCurrency("RUB"));
        Mockito.when(dashboardService.getBalanceByCurrency(anyInt(), anyInt())).thenReturn(BigDecimal.valueOf(100));

        mockMvc.perform(get(BASE_URL + "/balance/{currency}", "TEST_CURRENCY")
                .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(status().isOk());

        verify(userService, times(1)).getUserEmailFromSecurityContext();
        verify(userService, times(1)).findByEmail(anyString());
        verify(currencyService, times(1)).findByName(anyString());
        verify(dashboardService, times(1)).getBalanceByCurrency(anyInt(), anyInt());
    }

    @Test
    public void getBalanceByCurrency_bad_request() throws Exception {
        Mockito.when(userService.getUserEmailFromSecurityContext()).thenReturn("TEST_USER_NAME");
        Mockito.when(userService.findByEmail(anyString())).thenReturn(getMockUser());
        Mockito.when(currencyService.findByName(anyString())).thenReturn(getMockCurrency("RUB"));
        Mockito.when(dashboardService.getBalanceByCurrency(anyInt(), anyInt())).thenThrow(Exception.class);

        mockMvc.perform(get(BASE_URL + "/balance/{currency}", "TEST_CURRENCY")
                .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(status().isBadRequest());

        verify(userService, times(1)).getUserEmailFromSecurityContext();
        verify(userService, times(1)).findByEmail(anyString());
        verify(currencyService, times(1)).findByName(anyString());
        verify(dashboardService, times(1)).getBalanceByCurrency(anyInt(), anyInt());
    }

    @Test
    public void getCommission_isOk() throws Exception {
        Mockito.when(userService.getUserEmailFromSecurityContext()).thenReturn("TEST_EMAIL");
        Mockito.when(ngOrderService.getWalletAndCommision(anyString(), anyObject(), anyInt()))
                .thenReturn(getMockWalletsAndCommissionsForOrderCreationDto());

        mockMvc.perform(get(BASE_URL + "/commission/{orderType}/{currencyPairId}", OperationType.BUY, 5)
                .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(status().isOk());

        verify(userService, times(1)).getUserEmailFromSecurityContext();
        verify(ngOrderService, times(1)).getWalletAndCommision(anyString(), anyObject(), anyInt());
    }

    @Test
    public void getFilteredOrders_isOk_greater_than_zero() throws Exception {
        Pair<Integer, List<OrderWideListDto>> dto = new ImmutablePair<>(100, Collections.emptyList());

        Mockito.when(userService.getIdByEmail(anyString())).thenReturn(12);
        Mockito.when(currencyService.findCurrencyPairById(anyInt())).thenReturn(getMockCurrencyPair());
        Mockito.when(orderService.getMyOrdersWithStateMap(anyInt(), any(CurrencyPair.class), anyString(),
                any(OrderStatus.class), anyString(), anyInt(), anyInt(), anyBoolean(), anyMapOf(String.class, String.class),
                any(LocalDateTime.class), any(LocalDateTime.class), any(Locale.class))).thenReturn(dto);

        mockMvc.perform(get(BASE_URL + "/orders/{status}", OrderStatus.OPENED)
                .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE)
                .param("currencyPairId", "2"))
                .andExpect(jsonPath("$.count", is(100)))
                .andExpect(jsonPath("$.items", is(Collections.EMPTY_LIST)))
                .andExpect(status().isOk());

        verify(userService, times(1)).getIdByEmail(anyString());
        verify(currencyService, times(1)).findCurrencyPairById(anyInt());
        verify(orderService, times(1)).getMyOrdersWithStateMap(anyInt(), any(CurrencyPair.class), anyString(),
                any(OrderStatus.class), anyString(), anyInt(), anyInt(), anyBoolean(), anyMapOf(String.class, String.class),
                any(LocalDateTime.class), any(LocalDateTime.class), any(Locale.class));
    }

    @Test
    public void getFilteredOrders_isOk_equals_zero() throws Exception {
        Pair<Integer, List<OrderWideListDto>> dto = new ImmutablePair<>(100, Collections.emptyList());

        Mockito.when(userService.getIdByEmail(anyString())).thenReturn(12);
        Mockito.when(orderService.getMyOrdersWithStateMap(anyInt(), any(CurrencyPair.class), anyString(),
                any(OrderStatus.class), anyString(), anyInt(), anyInt(), anyBoolean(), anyMapOf(String.class, String.class),
                any(LocalDateTime.class), any(LocalDateTime.class), any(Locale.class))).thenReturn(dto);

        mockMvc.perform(get(BASE_URL + "/orders/{status}", OrderStatus.OPENED)
                .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE)
                .param("currencyPairId", "0")
                .param("currencyPairName", "USD/XRP")
                .param("sortByCreated", "ASC"))
                .andExpect(jsonPath("$.count", is(100)))
                .andExpect(jsonPath("$.items", is(Collections.EMPTY_LIST)))
                .andExpect(status().isOk());

        verify(userService, times(1)).getIdByEmail(anyString());
        verify(orderService, times(1)).getMyOrdersWithStateMap(anyInt(), any(CurrencyPair.class), anyString(),
                any(OrderStatus.class), anyString(), anyInt(), anyInt(), anyBoolean(), anyMapOf(String.class, String.class),
                any(LocalDateTime.class), any(LocalDateTime.class), any(Locale.class));
    }

    @Test
    public void getFilteredOrders_bad_request() throws Exception {
        Mockito.when(userService.getIdByEmail(anyString())).thenReturn(12);
        Mockito.when(currencyService.findCurrencyPairById(anyInt())).thenReturn(getMockCurrencyPair());
        Mockito.when(orderService.getMyOrdersWithStateMap(anyInt(), any(CurrencyPair.class), anyString(),
                any(OrderStatus.class), anyString(), anyInt(), anyInt(), anyBoolean(), anyMapOf(String.class, String.class),
                any(LocalDateTime.class), any(LocalDateTime.class), any(Locale.class))).thenThrow(Exception.class);

        mockMvc.perform(get(BASE_URL + "/orders/{status}", OrderStatus.DRAFT)
                .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(status().isBadRequest());

        verify(userService, times(1)).getIdByEmail(anyString());
        verify(orderService, times(1)).getMyOrdersWithStateMap(anyInt(), any(CurrencyPair.class), anyString(),
                any(OrderStatus.class), anyString(), anyInt(), anyInt(), anyBoolean(), anyMapOf(String.class, String.class),
                any(LocalDateTime.class), any(LocalDateTime.class), any(Locale.class));
    }

    @Test
    public void getLastOrders_isOk() throws Exception {
        Pair<Integer, List<OrderWideListDto>> dto = new ImmutablePair<>(100, Collections.emptyList());

        Mockito.when(userService.getIdByEmail(anyString())).thenReturn(12);
        Mockito.when(orderService.getMyOrdersWithStateMap(anyInt(), any(CurrencyPair.class), anyString(),
                any(OrderStatus.class), anyString(), anyInt(), anyInt(), anyBoolean(), anyMapOf(String.class, String.class),
                any(LocalDateTime.class), any(LocalDateTime.class), any(Locale.class))).thenReturn(dto);

        mockMvc.perform(get(BASE_URL + "/last/orders/{status}", OrderStatus.OPENED)
                .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(jsonPath("$.count", is(100)))
                .andExpect(jsonPath("$.items", is(Collections.EMPTY_LIST)))
                .andExpect(status().isOk());

        verify(userService, times(1)).getIdByEmail(anyString());
        verify(orderService, times(1)).getMyOrdersWithStateMap(anyInt(), any(CurrencyPair.class), anyString(),
                any(OrderStatus.class), anyString(), anyInt(), anyInt(), anyBoolean(), anyMapOf(String.class, String.class),
                any(LocalDateTime.class), any(LocalDateTime.class), any(Locale.class));
    }

    @Test
    public void getLastOrders_isOk_greater_than_zero() throws Exception {
        Pair<Integer, List<OrderWideListDto>> dto = new ImmutablePair<>(100, Collections.emptyList());

        Mockito.when(userService.getIdByEmail(anyString())).thenReturn(12);
        Mockito.when(currencyService.findCurrencyPairById(anyInt())).thenReturn(getMockCurrencyPair());
        Mockito.when(orderService.getMyOrdersWithStateMap(anyInt(), any(CurrencyPair.class), anyString(),
                any(OrderStatus.class), anyString(), anyInt(), anyInt(), anyBoolean(), anyMapOf(String.class, String.class),
                any(LocalDateTime.class), any(LocalDateTime.class), any(Locale.class))).thenReturn(dto);

        mockMvc.perform(get(BASE_URL + "/last/orders/{status}", OrderStatus.OPENED)
                .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE)
                .param("currencyPairId", "2"))
                .andExpect(jsonPath("$.count", is(100)))
                .andExpect(jsonPath("$.items", is(Collections.EMPTY_LIST)))
                .andExpect(status().isOk());

        verify(userService, times(1)).getIdByEmail(anyString());
        verify(currencyService, times(1)).findCurrencyPairById(anyInt());
        verify(orderService, times(1)).getMyOrdersWithStateMap(anyInt(), any(CurrencyPair.class), anyString(),
                any(OrderStatus.class), anyString(), anyInt(), anyInt(), anyBoolean(), anyMapOf(String.class, String.class),
                any(LocalDateTime.class), any(LocalDateTime.class), any(Locale.class));
    }

    @Test
    public void getLastOrders_isOk_equals_zero() throws Exception {
        Pair<Integer, List<OrderWideListDto>> dto = new ImmutablePair<>(100, Collections.emptyList());

        Mockito.when(userService.getIdByEmail(anyString())).thenReturn(12);
        Mockito.when(orderService.getMyOrdersWithStateMap(anyInt(), any(CurrencyPair.class), anyString(),
                any(OrderStatus.class), anyString(), anyInt(), anyInt(), anyBoolean(), anyMapOf(String.class, String.class),
                any(LocalDateTime.class), any(LocalDateTime.class), any(Locale.class))).thenReturn(dto);

        mockMvc.perform(get(BASE_URL + "/last/orders/{status}", OrderStatus.OPENED)
                .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE)
                .param("currencyPairId", "0")
                .param("currencyPairName", "USD/XRP"))
                .andExpect(jsonPath("$.count", is(100)))
                .andExpect(jsonPath("$.items", is(Collections.EMPTY_LIST)))
                .andExpect(status().isOk());

        verify(userService, times(1)).getIdByEmail(anyString());
        verify(orderService, times(1)).getMyOrdersWithStateMap(anyInt(), any(CurrencyPair.class), anyString(),
                any(OrderStatus.class), anyString(), anyInt(), anyInt(), anyBoolean(), anyMapOf(String.class, String.class),
                any(LocalDateTime.class), any(LocalDateTime.class), any(Locale.class));
    }

    @Test
    public void getLastOrders_bad_request() throws Exception {
        Mockito.when(userService.getIdByEmail(anyString())).thenReturn(12);
        Mockito.when(orderService.getMyOrdersWithStateMap(anyInt(), any(CurrencyPair.class), anyString(),
                any(OrderStatus.class), anyString(), anyInt(), anyInt(), anyBoolean(), anyMapOf(String.class, String.class),
                any(LocalDateTime.class), any(LocalDateTime.class), any(Locale.class))).thenThrow(Exception.class);

        mockMvc.perform(get(BASE_URL + "/last/orders/{status}", OrderStatus.DRAFT)
                .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(status().isBadRequest());

        verify(userService, times(1)).getIdByEmail(anyString());
        verify(orderService, times(1)).getMyOrdersWithStateMap(anyInt(), any(CurrencyPair.class), anyString(),
                any(OrderStatus.class), anyString(), anyInt(), anyInt(), anyBoolean(), anyMapOf(String.class, String.class),
                any(LocalDateTime.class), any(LocalDateTime.class), any(Locale.class));
    }

    @Test
    public void cancelOrder_data_true() throws Exception {
        Mockito.when(orderService.getOrderById(anyInt())).thenReturn(getMockExOrder());
        Mockito.when(orderService.cancelOrder(anyInt())).thenReturn(Boolean.TRUE);

        mockMvc.perform(post(BASE_URL + "/cancel")
                .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE)
                .param("order_id", "1"))
                .andExpect(jsonPath("$.data", is(Boolean.TRUE)))
                .andExpect(jsonPath("$.error", is(nullValue())))
                .andExpect(status().isOk());

        verify(orderService, times(1)).getOrderById(anyInt());
        verify(orderService, times(1)).cancelOrder(anyInt());
    }

    @Test
    public void cancelOrder_data_false() throws Exception {
        Mockito.when(orderService.getOrderById(anyInt())).thenReturn(null);
        Mockito.when(stopOrderServiceImpl.cancelOrder(anyInt(), anyObject())).thenReturn(Boolean.FALSE);

        mockMvc.perform(post(BASE_URL + "/cancel")
                .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE)
                .param("order_id", "1"))
                .andExpect(jsonPath("$.data", is(Boolean.FALSE)))
                .andExpect(jsonPath("$.error", is(nullValue())))
                .andExpect(status().isOk());

        verify(orderService, times(1)).getOrderById(anyInt());
    }

    // TODO: Test failed
    @Ignore
    public void cancelOrders() throws Exception {
        Mockito.when(orderService.cancelOrders(anyObject())).thenReturn(Boolean.TRUE);

        mockMvc.perform(post(BASE_URL + "/cancel/list")
                .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE)
                .param("order_ids", Arrays.asList(100, 200, 300, 400).toString()))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    public void cancelOrdersByCurrencyPair_canceled_true() throws Exception {
        Mockito.when(orderService.cancelOpenOrdersByCurrencyPair(anyString())).thenReturn(Boolean.TRUE);

        mockMvc.perform(post(BASE_URL + "/cancel/all")
                .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE)
                .param("currency_pair", "TEST_PAIR_NAME"))
                .andExpect(jsonPath("$.data", is(Boolean.TRUE)))
                .andExpect(jsonPath("$.error", is(nullValue())))
                .andExpect(status().isOk());

        verify(orderService, times(1)).cancelOpenOrdersByCurrencyPair(anyString());
    }

    @Test
    public void cancelOrdersByCurrencyPair_canceled_false() throws Exception {
        Mockito.when(orderService.cancelAllOpenOrders()).thenReturn(Boolean.FALSE);

        mockMvc.perform(post(BASE_URL + "/cancel/all")
                .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(jsonPath("$.data", is(Boolean.FALSE)))
                .andExpect(jsonPath("$.error", is(nullValue())))
                .andExpect(status().isOk());

        verify(orderService, times(1)).cancelAllOpenOrders();
    }

    @Test
    public void getCurrencyPairInfo_isOk() throws Exception {
        Mockito.when(userService.getUserEmailFromSecurityContext()).thenReturn("TEST_USER_NAME");
        Mockito.when(userService.findByEmail(anyString())).thenReturn(getMockUser());
        Mockito.when(ngOrderService.getBalanceByCurrencyPairId(anyInt(), anyObject())).thenReturn(Collections.EMPTY_MAP);

        mockMvc.perform(get(BASE_URL + "/info/{currencyPairId}", 14)
                .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(status().isOk());

        verify(userService, times(1)).getUserEmailFromSecurityContext();
        verify(userService, times(1)).findByEmail(anyString());
        verify(ngOrderService, times(1)).getBalanceByCurrencyPairId(anyInt(), anyObject());
    }
}