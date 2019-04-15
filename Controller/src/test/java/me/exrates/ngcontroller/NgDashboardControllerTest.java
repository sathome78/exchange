package me.exrates.ngcontroller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.SerializationFeature;
import me.exrates.model.CurrencyPair;
import me.exrates.model.ExOrder;
import me.exrates.model.dto.InputCreateOrderDto;
import me.exrates.model.dto.OrderCreateDto;
import me.exrates.model.dto.WalletsAndCommissionsForOrderCreationDto;
import me.exrates.model.dto.onlineTableDto.OrderWideListDto;
import me.exrates.model.enums.OperationType;
import me.exrates.model.enums.OrderActionEnum;
import me.exrates.model.enums.OrderBaseType;
import me.exrates.model.enums.OrderStatus;
import me.exrates.model.ngExceptions.NgResponseException;
import me.exrates.ngService.NgOrderService;
import me.exrates.service.CurrencyService;
import me.exrates.service.DashboardService;
import me.exrates.service.OrderService;
import me.exrates.service.UserService;
import me.exrates.service.stopOrder.StopOrderService;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.junit.Assert;
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
import org.springframework.web.util.NestedServletException;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyBoolean;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
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

    @Test
    @Ignore
    public void createOrder_is_created_switch_stop_limit() throws Exception {
        InputCreateOrderDto requestBody = getMockInputCreateOrderDto();
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(SerializationFeature.WRAP_ROOT_VALUE, false);
        ObjectWriter ow = mapper.writer().withDefaultPrettyPrinter();
        String requestJson = ow.writeValueAsString(requestBody);

        OrderCreateDto orderCreateDto = getMockOrderCreateDto();
        orderCreateDto.setOrderBaseType(OrderBaseType.STOP_LIMIT);

        Mockito.when(ngOrderService.prepareOrder(anyObject())).thenReturn(orderCreateDto);
        Mockito.when(stopOrderService.create(any(OrderCreateDto.class), any(OrderActionEnum.class), any(Locale.class))).thenReturn("TEST_RESULT");

        mockMvc.perform(post(BASE_URL + "/order")
                .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE)
                .content(requestJson))
                .andExpect(status().isCreated());

        verify(ngOrderService, times(1)).prepareOrder(anyObject());
        verify(stopOrderService, times(1)).create(any(OrderCreateDto.class), any(OrderActionEnum.class), any(Locale.class));
    }

    @Test
    @Ignore
    public void createOrder_bad_reques_switch_stop_limit() throws Exception {
        InputCreateOrderDto requestBody = getMockInputCreateOrderDto();
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(SerializationFeature.WRAP_ROOT_VALUE, false);
        ObjectWriter ow = mapper.writer().withDefaultPrettyPrinter();
        String requestJson = ow.writeValueAsString(requestBody);

        OrderCreateDto orderCreateDto = getMockOrderCreateDto();
        orderCreateDto.setOrderBaseType(OrderBaseType.STOP_LIMIT);

        Mockito.when(ngOrderService.prepareOrder(anyObject())).thenReturn(orderCreateDto);
        Mockito.when(stopOrderService.create(anyObject(), anyObject(), anyObject())).thenReturn("");

        try {
            mockMvc.perform(post(BASE_URL + "/order")
                    .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE)
                    .content(requestJson))
                    .andExpect(status().isBadRequest());
            Assert.fail();
        } catch (Exception e) {
            assertTrue(((NestedServletException) e).getRootCause() instanceof NgResponseException);
            NgResponseException responseException = (NgResponseException) ((NestedServletException) e).getRootCause();
            assertEquals("CREATE_ORDER_FAILED", responseException.getTitle());

            String expected = "Invalid orderId= 111";
            assertEquals(expected, e.getCause().getMessage());
        }

        verify(ngOrderService, times(1)).prepareOrder(anyObject());
        verify(stopOrderService, times(1)).create(anyObject(), anyObject(), anyObject());
    }

    @Test
    @Ignore
    public void createOrder_is_created_switch_default() throws Exception {
        InputCreateOrderDto requestBody = getMockInputCreateOrderDto();
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(SerializationFeature.WRAP_ROOT_VALUE, false);
        ObjectWriter ow = mapper.writer().withDefaultPrettyPrinter();
        String requestJson = ow.writeValueAsString(requestBody);

        OrderCreateDto orderCreateDto = getMockOrderCreateDto();
        orderCreateDto.setOrderBaseType(OrderBaseType.LIMIT);

        Mockito.when(ngOrderService.prepareOrder(anyObject())).thenReturn(orderCreateDto);
        Mockito.when(orderService.createOrder(anyObject(), anyObject(), anyObject())).thenReturn("TEST_RESULT");

        mockMvc.perform(post(BASE_URL + "/order")
                .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE)
                .content(requestJson))
                .andExpect(status().isCreated());

        verify(ngOrderService, times(1)).prepareOrder(anyObject());
        verify(orderService, times(1)).createOrder(anyObject(), anyObject(), anyObject());
    }

    @Test
    @Ignore
    public void createOrder_bad_request_switch_default() throws Exception {
        InputCreateOrderDto requestBody = getMockInputCreateOrderDto();
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(SerializationFeature.WRAP_ROOT_VALUE, false);
        ObjectWriter ow = mapper.writer().withDefaultPrettyPrinter();
        String requestJson = ow.writeValueAsString(requestBody);

        OrderCreateDto orderCreateDto = getMockOrderCreateDto();
        orderCreateDto.setOrderBaseType(OrderBaseType.LIMIT);

        Mockito.when(ngOrderService.prepareOrder(anyObject())).thenReturn(orderCreateDto);
        Mockito.when(orderService.createOrder(anyObject(), anyObject(), anyObject())).thenReturn("");

        try {
            mockMvc.perform(post(BASE_URL + "/order")
                    .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE)
                    .content(requestJson))
                    .andExpect(status().isBadRequest());
            Assert.fail();
        } catch (Exception e) {
            assertTrue(((NestedServletException) e).getRootCause() instanceof NgResponseException);
            NgResponseException responseException = (NgResponseException) ((NestedServletException) e).getRootCause();
            assertEquals("CREATE_ORDER_FAILED", responseException.getTitle());

            String expected = "Invalid orderId= 111";
            assertEquals(expected, e.getCause().getMessage());
        }

        verify(ngOrderService, times(1)).prepareOrder(anyObject());
        verify(orderService, times(1)).createOrder(anyObject(), anyObject(), anyObject());
    }

    @Test
    public void deleteOrderById_isOk() throws Exception {
        Integer id = 1;
        Mockito.when(orderService.deleteOrderByAdmin(anyInt())).thenReturn(id);

        mockMvc.perform(delete(BASE_URL + "/order/{id}", id)
                .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(status().isOk());

        verify(orderService, times(1)).deleteOrderByAdmin(anyInt());
        reset(orderService);
    }

    @Test
    public void deleteOrderById_bad_request() {
        Integer id = 5;
        Mockito.when(orderService.deleteOrderByAdmin(anyInt())).thenReturn(id);

        try {
            mockMvc.perform(delete(BASE_URL + "/order/{id}", id)
                    .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                    .andExpect(status().isBadRequest());
            Assert.fail();
        } catch (Exception e) {
            assertTrue(((NestedServletException) e).getRootCause() instanceof NgResponseException);
            NgResponseException responseException = (NgResponseException) ((NestedServletException) e).getRootCause();
            assertEquals("DELETE_ORDER_FAILED", responseException.getTitle());

            String expected = "Invalid orderId= 5";
            assertEquals(expected, e.getCause().getMessage());
        }

        verify(orderService, times(1)).deleteOrderByAdmin(anyInt());
        reset(orderService);
    }

    @Test
    public void updateOrder() throws Exception {
        InputCreateOrderDto requestBody = getMockInputCreateOrderDto();
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(SerializationFeature.WRAP_ROOT_VALUE, false);
        ObjectWriter ow = mapper.writer().withDefaultPrettyPrinter();
        String requestJson = ow.writeValueAsString(requestBody);

        mockMvc.perform(put(BASE_URL + "/order")
                .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE)
                .content(requestJson))
                .andExpect(status().is4xxClientError())
                .andExpect(jsonPath("$.detail", is("Update orders is not supported")));
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
        reset(userService);
        verify(currencyService, times(1)).findByName(anyString());
        reset(currencyService);
        verify(dashboardService, times(1)).getBalanceByCurrency(anyInt(), anyInt());
        reset(dashboardService);
    }

    @Test
    public void getBalanceByCurrency_bad_request() throws Exception {
        Mockito.when(userService.getUserEmailFromSecurityContext()).thenReturn("TEST_USER_NAME");
        Mockito.when(userService.findByEmail(anyString())).thenReturn(getMockUser());
        Mockito.when(currencyService.findByName(anyString())).thenReturn(getMockCurrency("RUB"));
        Mockito.when(dashboardService.getBalanceByCurrency(anyInt(), anyInt())).thenThrow(Exception.class);

        try {
            mockMvc.perform(get(BASE_URL + "/balance/{currency}", "TEST_CURRENCY")
                    .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                    .andExpect(status().isBadRequest());
            Assert.fail();
        } catch (Exception e) {
            assertTrue(((NestedServletException) e).getRootCause() instanceof NgResponseException);
            NgResponseException responseException = (NgResponseException) ((NestedServletException) e).getRootCause();
            assertEquals("FAILED_TO_GET_BALANCE_BY_CURRENCY", responseException.getTitle());

            String expected = "Error while get balance by currency user {TEST_EMAIL}, currency {RUB}";
            assertEquals(expected, e.getCause().getMessage());
        }

        verify(userService, times(1)).getUserEmailFromSecurityContext();
        verify(userService, times(1)).findByEmail(anyString());
        reset(userService);
        verify(currencyService, times(1)).findByName(anyString());
        reset(currencyService);
        verify(dashboardService, times(1)).getBalanceByCurrency(anyInt(), anyInt());
        reset(dashboardService);
    }

    @Test
    public void getCommission_isOk() throws Exception {
        WalletsAndCommissionsForOrderCreationDto commissions = new WalletsAndCommissionsForOrderCreationDto();
        commissions.setSpendWalletId(777);
        commissions.setSpendWalletActiveBalance(BigDecimal.valueOf(258));
        commissions.setCommissionId(888);
        commissions.setCommissionValue(BigDecimal.valueOf(7));

        Mockito.when(userService.getUserEmailFromSecurityContext()).thenReturn("TEST_EMAIL");
        Mockito.when(ngOrderService.getWalletAndCommision(anyString(), anyObject(), anyInt()))
                .thenReturn(commissions);

        mockMvc.perform(get(BASE_URL + "/commission/{orderType}/{currencyPairId}", OperationType.BUY, 5)
                .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(status().isOk());

        verify(userService, times(1)).getUserEmailFromSecurityContext();
        reset(userService);
        verify(ngOrderService, times(1)).getWalletAndCommision(anyString(), anyObject(), anyInt());
        reset(ngOrderService);
    }

    @Test
    public void getFilteredOrders_isOk_greater_than_zero() throws Exception {
        Pair<Integer, List<OrderWideListDto>> dto = new ImmutablePair<>(100, Collections.emptyList());

        Mockito.when(userService.getIdByEmail(anyString())).thenReturn(12);
        Mockito.when(currencyService.findCurrencyPairById(anyInt())).thenReturn(getMockCurrencyPair());
        Mockito.when(orderService.getMyOrdersWithStateMap(
                anyInt(),
                any(CurrencyPair.class),
                anyString(),
                any(OrderStatus.class),
                anyString(), anyInt(),
                anyInt(),
                anyBoolean(),
                anyString(),
                any(LocalDateTime.class),
                any(LocalDateTime.class),
                any(Locale.class))
        ).thenReturn(dto);

        mockMvc.perform(get(BASE_URL + "/orders/{status}", OrderStatus.OPENED)
                .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE)
                .param("currencyPairId", "2"))
                .andExpect(jsonPath("$.count", is(100)))
                .andExpect(jsonPath("$.items", is(Collections.EMPTY_LIST)))
                .andExpect(status().isOk());

        verify(userService, times(1)).getIdByEmail(anyString());
        reset(userService);
        verify(currencyService, times(1)).findCurrencyPairById(anyInt());
        reset(currencyService);
        verify(orderService, times(1)).getMyOrdersWithStateMap(
                anyInt(),
                any(CurrencyPair.class),
                anyString(),
                any(OrderStatus.class),
                anyString(),
                anyInt(),
                anyInt(),
                anyBoolean(),
                anyString(),
                any(LocalDateTime.class),
                any(LocalDateTime.class),
                any(Locale.class));
        reset(orderService);
    }

    @Test
    public void getFilteredOrders_isOk_equals_zero() throws Exception {
        Pair<Integer, List<OrderWideListDto>> dto = new ImmutablePair<>(100, Collections.emptyList());

        Mockito.when(userService.getIdByEmail(anyString())).thenReturn(12);
        Mockito.when(orderService.getMyOrdersWithStateMap(
                anyInt(),
                any(CurrencyPair.class),
                anyString(),
                any(OrderStatus.class),
                anyString(),
                anyInt(),
                anyInt(),
                anyBoolean(),
                anyString(),
                any(LocalDateTime.class),
                any(LocalDateTime.class),
                any(Locale.class))
        ).thenReturn(dto);

        mockMvc.perform(get(BASE_URL + "/orders/{status}", OrderStatus.OPENED)
                .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE)
                .param("currencyPairId", "0")
                .param("currencyPairName", "USD/XRP")
                .param("sortByCreated", "ASC"))
                .andExpect(jsonPath("$.count", is(100)))
                .andExpect(jsonPath("$.items", is(Collections.EMPTY_LIST)))
                .andExpect(status().isOk());

        verify(userService, times(1)).getIdByEmail(anyString());
        reset(userService);
        verify(orderService, times(1)).getMyOrdersWithStateMap(
                anyInt(),
                any(CurrencyPair.class),
                anyString(),
                any(OrderStatus.class),
                anyString(),
                anyInt(),
                anyInt(),
                anyBoolean(),
                anyString(),
                any(LocalDateTime.class),
                any(LocalDateTime.class),
                any(Locale.class));
        reset(orderService);
    }

    @Test
    public void getFilteredOrders_bad_request() throws Exception {
        Mockito.when(userService.getIdByEmail(anyString())).thenReturn(12);
        Mockito.when(currencyService.findCurrencyPairById(anyInt())).thenReturn(getMockCurrencyPair());
        Mockito.when(orderService.getMyOrdersWithStateMap(
                anyInt(),
                any(CurrencyPair.class),
                anyString(),
                any(OrderStatus.class),
                anyString(),
                anyInt(),
                anyInt(),
                anyBoolean(),
                anyString(),
                any(LocalDateTime.class),
                any(LocalDateTime.class),
                any(Locale.class))
        ).thenThrow(Exception.class);

        try {
            mockMvc.perform(get(BASE_URL + "/orders/{status}", OrderStatus.DRAFT)
                    .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                    .andExpect(status().isBadRequest());
            Assert.fail();
        } catch (Exception e) {
            assertTrue(((NestedServletException) e).getRootCause() instanceof NgResponseException);
            NgResponseException responseException = (NgResponseException) ((NestedServletException) e).getRootCause();
            assertEquals("FAILED_TO_FILTERED_ORDERS", responseException.getTitle());

            String expected = "Failed to filtered orders";
            assertEquals(expected, e.getCause().getMessage());
        }

        verify(userService, times(1)).getIdByEmail(anyString());
        reset(userService);
        verify(orderService, times(1)).getMyOrdersWithStateMap(
                anyInt(),
                any(CurrencyPair.class),
                anyString(),
                any(OrderStatus.class),
                anyString(),
                anyInt(),
                anyInt(),
                anyBoolean(),
                anyString(),
                any(LocalDateTime.class),
                any(LocalDateTime.class),
                any(Locale.class));
        reset(orderService);
    }

    @Test
    public void getLastOrders_isOk() throws Exception {
        Pair<Integer, List<OrderWideListDto>> dto = new ImmutablePair<>(100, Collections.emptyList());

        Mockito.when(userService.getIdByEmail(anyString())).thenReturn(12);
        Mockito.when(orderService.getMyOrdersWithStateMap(
                anyInt(),
                any(CurrencyPair.class),
                anyString(),
                any(OrderStatus.class),
                anyString(),
                anyInt(),
                anyInt(),
                anyBoolean(),
                anyString(),
                any(LocalDateTime.class),
                any(LocalDateTime.class),
                any(Locale.class))
        ).thenReturn(dto);

        mockMvc.perform(get(BASE_URL + "/last/orders/{status}", OrderStatus.OPENED)
                .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(jsonPath("$.count", is(100)))
                .andExpect(jsonPath("$.items", is(Collections.EMPTY_LIST)))
                .andExpect(status().isOk());

        verify(userService, times(1)).getIdByEmail(anyString());
        reset(userService);
        verify(orderService, times(1)).getMyOrdersWithStateMap(
                anyInt(),
                any(CurrencyPair.class),
                anyString(),
                any(OrderStatus.class),
                anyString(),
                anyInt(),
                anyInt(),
                anyBoolean(),
                anyString(),
                any(LocalDateTime.class),
                any(LocalDateTime.class),
                any(Locale.class));
        reset(orderService);
    }

    @Test
    public void getLastOrders_isOk_greater_than_zero() throws Exception {
        Pair<Integer, List<OrderWideListDto>> dto = new ImmutablePair<>(100, Collections.emptyList());

        Mockito.when(userService.getIdByEmail(anyString())).thenReturn(12);
        Mockito.when(currencyService.findCurrencyPairById(anyInt())).thenReturn(getMockCurrencyPair());
        Mockito.when(orderService.getMyOrdersWithStateMap(
                anyInt(),
                any(CurrencyPair.class),
                anyString(),
                any(OrderStatus.class),
                anyString(),
                anyInt(),
                anyInt(),
                anyBoolean(),
                anyString(),
                any(LocalDateTime.class),
                any(LocalDateTime.class),
                any(Locale.class))
        ).thenReturn(dto);

        mockMvc.perform(get(BASE_URL + "/last/orders/{status}", OrderStatus.OPENED)
                .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE)
                .param("currencyPairId", "2"))
                .andExpect(jsonPath("$.count", is(100)))
                .andExpect(jsonPath("$.items", is(Collections.EMPTY_LIST)))
                .andExpect(status().isOk());

        verify(userService, times(1)).getIdByEmail(anyString());
        reset(userService);
        verify(currencyService, times(1)).findCurrencyPairById(anyInt());
        reset(currencyService);
        verify(orderService, times(1)).getMyOrdersWithStateMap(
                anyInt(),
                any(CurrencyPair.class),
                anyString(),
                any(OrderStatus.class),
                anyString(),
                anyInt(),
                anyInt(),
                anyBoolean(),
                anyString(),
                any(LocalDateTime.class),
                any(LocalDateTime.class),
                any(Locale.class));
        reset(orderService);
    }

    @Test
    public void getLastOrders_isOk_equals_zero() throws Exception {
        Pair<Integer, List<OrderWideListDto>> dto = new ImmutablePair<>(100, Collections.emptyList());

        Mockito.when(userService.getIdByEmail(anyString())).thenReturn(12);
        Mockito.when(orderService.getMyOrdersWithStateMap(
                anyInt(),
                any(CurrencyPair.class),
                anyString(),
                any(OrderStatus.class),
                anyString(),
                anyInt(),
                anyInt(),
                anyBoolean(),
                anyString(),
                any(LocalDateTime.class),
                any(LocalDateTime.class),
                any(Locale.class))
        ).thenReturn(dto);

        mockMvc.perform(get(BASE_URL + "/last/orders/{status}", OrderStatus.OPENED)
                .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE)
                .param("currencyPairId", "0")
                .param("currencyPairName", "USD/XRP"))
                .andExpect(jsonPath("$.count", is(100)))
                .andExpect(jsonPath("$.items", is(Collections.EMPTY_LIST)))
                .andExpect(status().isOk());

        verify(userService, times(1)).getIdByEmail(anyString());
        reset(userService);
        verify(orderService, times(1)).getMyOrdersWithStateMap(
                anyInt(),
                any(CurrencyPair.class),
                anyString(),
                any(OrderStatus.class),
                anyString(),
                anyInt(),
                anyInt(),
                anyBoolean(),
                anyString(),
                any(LocalDateTime.class),
                any(LocalDateTime.class),
                any(Locale.class));
        reset(orderService);
    }

    @Test
    public void getLastOrders_bad_request() throws Exception {
        Mockito.when(userService.getIdByEmail(anyString())).thenReturn(12);
        Mockito.when(orderService.getMyOrdersWithStateMap(
                anyInt(),
                any(CurrencyPair.class),
                anyString(),
                any(OrderStatus.class),
                anyString(),
                anyInt(),
                anyInt(),
                anyBoolean(),
                anyString(),
                any(LocalDateTime.class),
                any(LocalDateTime.class),
                any(Locale.class))
        ).thenThrow(Exception.class);

        try {
            mockMvc.perform(get(BASE_URL + "/last/orders/{status}", OrderStatus.DRAFT)
                    .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                    .andExpect(status().isBadRequest());
            Assert.fail();
        } catch (Exception e) {
            assertTrue(((NestedServletException) e).getRootCause() instanceof NgResponseException);
            NgResponseException responseException = (NgResponseException) ((NestedServletException) e).getRootCause();
            assertEquals("FAILED_TO_GET_LAST_ORDERS", responseException.getTitle());

            String expected = "Failed to get last orders";
            assertEquals(expected, e.getCause().getMessage());
        }

        verify(userService, times(1)).getIdByEmail(anyString());
        reset(userService);
        verify(orderService, times(1)).getMyOrdersWithStateMap(
                anyInt(),
                any(CurrencyPair.class),
                anyString(),
                any(OrderStatus.class),
                anyString(),
                anyInt(),
                anyInt(),
                anyBoolean(),
                anyString(),
                any(LocalDateTime.class),
                any(LocalDateTime.class),
                any(Locale.class));
        reset(orderService);
    }

    @Test
    @Ignore
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
        reset(orderService);
    }

    @Test
    @Ignore
    public void cancelOrder_data_false() throws Exception {
        Mockito.when(orderService.getOrderById(anyInt())).thenReturn(null);
        Mockito.when(stopOrderService.cancelOrder(anyInt(), anyObject())).thenReturn(Boolean.FALSE);

        mockMvc.perform(post(BASE_URL + "/cancel")
                .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE)
                .param("order_id", "1"))
                .andExpect(jsonPath("$.data", is(Boolean.FALSE)))
                .andExpect(jsonPath("$.error", is(nullValue())))
                .andExpect(status().isOk());

        verify(orderService, times(1)).getOrderById(anyInt());
        reset(orderService);
    }

    @Test
    public void cancelOrders() throws Exception {
        Mockito.when(orderService.cancelOrders(anyObject())).thenReturn(Boolean.TRUE);

        mockMvc.perform(post(BASE_URL + "/cancel/list")
                .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE)
                .param("order_ids", "100, 200, 300"))
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
        reset(orderService);
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
        reset(orderService);
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
        reset(userService);
        verify(ngOrderService, times(1)).getBalanceByCurrencyPairId(anyInt(), anyObject());
        reset(ngOrderService);
    }

    private InputCreateOrderDto getMockInputCreateOrderDto() {
        InputCreateOrderDto inputCreateOrderDto = new InputCreateOrderDto();
        inputCreateOrderDto.setOrderType("TEST_ORDER_type");
        inputCreateOrderDto.setOrderId(111);
        inputCreateOrderDto.setCurrencyPairId(999);
        inputCreateOrderDto.setAmount(BigDecimal.valueOf(15));
        inputCreateOrderDto.setRate(BigDecimal.valueOf(35));
        inputCreateOrderDto.setCommission(BigDecimal.valueOf(5));
        inputCreateOrderDto.setBaseType(getMockExOrder().getOrderBaseType().toString());
        inputCreateOrderDto.setTotal(BigDecimal.valueOf(100));
        inputCreateOrderDto.setStop(BigDecimal.valueOf(75));
        inputCreateOrderDto.setStatus("TEST_STATUS");
        inputCreateOrderDto.setUserId(400);
        inputCreateOrderDto.setUserId(666);
        inputCreateOrderDto.setCurrencyPair(getMockCurrencyPair());

        return inputCreateOrderDto;
    }

    protected ExOrder getMockExOrder() {
        ExOrder exOrder = new ExOrder();
        exOrder.setId(1515);
        exOrder.setUserId(1000);
        exOrder.setCurrencyPairId(2222);
        exOrder.setOperationType(OperationType.BUY);
        exOrder.setExRate(BigDecimal.TEN);
        exOrder.setAmountBase(BigDecimal.TEN);
        exOrder.setAmountConvert(BigDecimal.TEN);
        exOrder.setComissionId(3232);
        exOrder.setCommissionFixedAmount(BigDecimal.TEN);
        exOrder.setUserAcceptorId(3333);
        exOrder.setDateCreation(LocalDateTime.of(2019, 3, 18, 15, 15, 15));
        exOrder.setDateAcception(LocalDateTime.of(2019, 3, 18, 15, 15, 15));
        exOrder.setStatus(OrderStatus.OPENED);
        exOrder.setCurrencyPair(getMockCurrencyPair());
        exOrder.setSourceId(3598);
        exOrder.setStop(BigDecimal.ONE);
        exOrder.setOrderBaseType(OrderBaseType.LIMIT);
        exOrder.setPartiallyAcceptedAmount(BigDecimal.TEN);
        exOrder.setEventTimestamp(1111L);

        return exOrder;
    }

    private OrderCreateDto getMockOrderCreateDto() {
        OrderCreateDto orderCreateDto = new OrderCreateDto();
        orderCreateDto.setOrderId(111);
        orderCreateDto.setUserId(222);
        orderCreateDto.setStatus(OrderStatus.OPENED);
        orderCreateDto.setCurrencyPair(getMockCurrencyPair());
        orderCreateDto.setComissionForBuyId(10);
        orderCreateDto.setComissionForBuyRate(BigDecimal.valueOf(15));
        orderCreateDto.setComissionForSellId(333);
        orderCreateDto.setComissionForSellRate(BigDecimal.valueOf(20));
        orderCreateDto.setWalletIdCurrencyBase(444);
        orderCreateDto.setUserId(400);
        orderCreateDto.setCurrencyBaseBalance(BigDecimal.valueOf(200));
        orderCreateDto.setWalletIdCurrencyConvert(555);
        orderCreateDto.setCurrencyConvertBalance(BigDecimal.valueOf(25));

        return orderCreateDto;
    }
}