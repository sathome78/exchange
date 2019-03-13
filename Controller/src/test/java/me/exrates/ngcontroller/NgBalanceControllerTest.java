package me.exrates.ngcontroller;

import me.exrates.model.enums.CurrencyType;
import me.exrates.model.ngUtil.PagedResult;
import me.exrates.ngService.BalanceService;
import me.exrates.service.RefillService;
import me.exrates.service.WalletService;
import me.exrates.service.cache.ExchangeRatesHolder;
import org.apache.commons.lang3.StringUtils;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Collections;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.*;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

public class NgBalanceControllerTest extends AngularApiCommonTest {

    private static final String BASE_URL = "/api/private/v2/balances";

    @Mock
    private BalanceService balanceService;
    @Mock
    private ExchangeRatesHolder exchangeRatesHolder;
    @Mock
    private LocaleResolver localeResolver;
    @Mock
    private RefillService refillService;
    @Mock
    private WalletService walletService;

    @InjectMocks
    NgBalanceController ngBalanceController;

    private MockMvc mockMvc;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        mockMvc = MockMvcBuilders
                .standaloneSetup(ngBalanceController)
                .build();

        SecurityContextHolder.getContext()
                .setAuthentication(new AnonymousAuthenticationToken("GUEST", "testemail@gmail.com",
                        AuthorityUtils.createAuthorityList("ADMIN")));
    }

    @Test
    public void getBalances_required_true() throws Exception {
        UriComponents uriComponents = UriComponentsBuilder.newInstance()
                .path(BASE_URL)
                .build();

        Mockito.when(balanceService.getWalletsDetails(anyObject())).thenReturn(getPagedResult());

        mockMvc.perform(getApiRequestBuilder(uriComponents.toUri(), HttpMethod.GET, null, StringUtils.EMPTY, MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andDo(print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(jsonPath("$.items", hasSize(1)))
                .andExpect(jsonPath("$.items.[0].id", is(100)))
                .andExpect(jsonPath("$.items.[0].userId", is(1)))
                .andExpect(jsonPath("$.items.[0].currencyId", is(111)))
                .andExpect(jsonPath("$.items.[0].currencyPrecision", is(222)))
                .andExpect(jsonPath("$.items.[0].currencyName", is("TEST_CURRENCY_NAME")))
                .andExpect(jsonPath("$.items.[0].currencyDescription", is("TEST_CURRENCY_DESCRIPTION")))
                .andExpect(jsonPath("$.items.[0].activeBalance", is("TEST_ACTIVE_BALANCE")))
                .andExpect(jsonPath("$.items.[0].onConfirmation", is("TEST_ON_CONFIRMATION")))
                .andExpect(jsonPath("$.items.[0].onConfirmationStage", is("TEST_ON_CONFIRMATION_STAGE")))
                .andExpect(jsonPath("$.items.[0].onConfirmationCount", is("TEST_ON_CONFIRMATION_COUNT")))
                .andExpect(jsonPath("$.items.[0].reservedBalance", is("TEST_RESERVED_BALANCE")))
                .andExpect(jsonPath("$.items.[0].reservedByOrders", is("TEST_RESERVED_BY_ORDERS")))
                .andExpect(jsonPath("$.items.[0].reservedByMerchant", is("TEST_RESERVED_BY_MERCHANT")))
                .andExpect(jsonPath("$.items.[0].btcAmount", is("TEST_BTC_AMOUNT")))
                .andExpect(jsonPath("$.items.[0].usdAmount", is("TEST_USD_AMOUNT")))
                .andExpect(jsonPath("$.items.[0].confirmations", is(Collections.EMPTY_LIST)));

        verify(balanceService, times(1)).getWalletsDetails(anyObject());
    }

    @Test
    public void getBalances_exception() throws Exception {
        UriComponents uriComponents = UriComponentsBuilder.newInstance()
                .path(BASE_URL)
                .build();

        Mockito.when(balanceService.getWalletsDetails(anyObject())).thenThrow(Exception.class);

        String ngDashboardException = "Failed to get user balances: null";

        mockMvc.perform(getApiRequestBuilder(uriComponents.toUri(), HttpMethod.GET, null, StringUtils.EMPTY, MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andDo(print())
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(jsonPath("$.detail", is(ngDashboardException)));

        verify(balanceService, times(1)).getWalletsDetails(anyObject());
    }

    @Test
    public void getPendingRequests() {
    }

    @Test
    public void revokeWithdrawRequest() {
    }

    @Test
    public void getUserTotalBalance() {
    }

    @Test
    public void getSingleCurrency() {
    }

    @Test
    public void getMyInputOutputData() {
    }

    @Test
    public void getDefaultMyInputOutputData() {
    }

    @Test
    public void getBtcAndUsdBalancesSum() {
    }

    @Test
    public void otherErrorsHandler() {
    }
}