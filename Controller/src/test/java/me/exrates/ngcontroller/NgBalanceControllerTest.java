package me.exrates.ngcontroller;

import me.exrates.model.enums.CurrencyType;
import me.exrates.model.ngUtil.PagedResult;
import me.exrates.ngService.BalanceService;
import me.exrates.service.RefillService;
import me.exrates.service.WalletService;
import me.exrates.service.cache.ExchangeRatesHolder;
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

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.*;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

public class NgBalanceControllerTest extends AngularApiCommonTest {
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

    private final String BASE_URL = "/api/private/v2/balances";

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
    public void getBalances_required_false() throws Exception {
        UriComponents uriComponents = UriComponentsBuilder.newInstance()
                .path(BASE_URL)
                .queryParam("limit", "30")
                .queryParam("offset", "5")
                .queryParam("excludeZero", "false")
                .queryParam("currencyName", "BTC")
                .queryParam("currencyId", "111")
                .queryParam("currencyType", CurrencyType.CRYPTO)
                .build();

        Mockito.when(balanceService.getWalletsDetails(anyObject())).thenReturn(getPagedResult());

        mockMvc.perform(getApiRequestBuilder(uriComponents.toUri(), HttpMethod.GET, null, "", MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andDo(print())
                .andExpect(MockMvcResultMatchers.status().isOk());

        verify(balanceService, times(1)).getWalletsDetails(anyObject());
    }

    @Test
    public void getBalances_exception() throws Exception {
        UriComponents uriComponents = UriComponentsBuilder.newInstance()
                .path(BASE_URL)
                .build();

        String error = "{\"url\":\"http://localhost/api/private/v2/balances\",\"cause\":\"NgDashboardException\",\"detail\":\"Failed to get user balances: null\",\"title\":null,\"uuid\":null,\"code\":null}";

        Mockito.when(balanceService.getWalletsDetails(anyObject())).thenThrow(Exception.class);

        mockMvc.perform(getApiRequestBuilder(uriComponents.toUri(), HttpMethod.GET, null, "", MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andDo(print())
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(content().string(error));

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