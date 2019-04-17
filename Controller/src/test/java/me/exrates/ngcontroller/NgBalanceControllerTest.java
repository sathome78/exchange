package me.exrates.ngcontroller;

import me.exrates.model.dto.RefillRequestFlatDto;
import me.exrates.model.dto.TransferRequestFlatDto;
import me.exrates.model.dto.WithdrawRequestFlatDto;
import me.exrates.model.dto.onlineTableDto.ExOrderStatisticsShortByPairsDto;
import me.exrates.model.dto.onlineTableDto.MyInputOutputHistoryDto;
import me.exrates.model.dto.onlineTableDto.MyWalletsDetailedDto;
import me.exrates.model.dto.onlineTableDto.MyWalletsStatisticsDto;
import me.exrates.model.enums.CurrencyPairType;
import me.exrates.model.enums.TransactionSourceType;
import me.exrates.model.ngModel.RefillPendingRequestDto;
import me.exrates.model.ngUtil.PagedResult;
import me.exrates.ngService.BalanceService;
import me.exrates.service.RefillService;
import me.exrates.service.TransferService;
import me.exrates.service.UserService;
import me.exrates.service.WalletService;
import me.exrates.service.WithdrawService;
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
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.anySet;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
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
    private WithdrawService withdrawService;
    @Mock
    private TransferService transferService;
    @Mock
    private WalletService walletService;
    @Mock
    private UserService userService;
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

        PagedResult<MyWalletsDetailedDto> myWalletsDetailedDtoPagedResult = new PagedResult<>();
        myWalletsDetailedDtoPagedResult.setItems(Collections.singletonList(getMockMyWalletsDetailedDto()));

        Mockito.when(balanceService.getWalletsDetails(anyObject())).thenReturn(myWalletsDetailedDtoPagedResult);

        mockMvc.perform(getApiRequestBuilder(uriComponents.toUri(), HttpMethod.GET, null, StringUtils.EMPTY, MediaType.APPLICATION_JSON_UTF8_VALUE))
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
        String ngDashboardException = "Failed to get user balances: null";

        UriComponents uriComponents = UriComponentsBuilder.newInstance()
                .path(BASE_URL)
                .build();

        Mockito.when(balanceService.getWalletsDetails(anyObject())).thenThrow(Exception.class);

        mockMvc.perform(getApiRequestBuilder(uriComponents.toUri(), HttpMethod.GET, null, StringUtils.EMPTY, MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(jsonPath("$.detail", is(ngDashboardException)));

        verify(balanceService, times(1)).getWalletsDetails(anyObject());
    }

    @Test
    public void getPendingRequests_isOk() throws Exception {
        UriComponents uriComponents = UriComponentsBuilder.newInstance()
                .path(BASE_URL + "/pendingRequests")
                .build();

        PagedResult<RefillPendingRequestDto> myWalletsDetailedDtoPagedResult = new PagedResult<>();
        myWalletsDetailedDtoPagedResult.setItems(Collections.singletonList(getMockRefillPendingRequestDto()));

        Mockito.when(balanceService.getPendingRequests(anyInt(), anyInt(), anyString(), anyString())).thenReturn(myWalletsDetailedDtoPagedResult);

        mockMvc.perform(getApiRequestBuilder(uriComponents.toUri(), HttpMethod.GET, null, StringUtils.EMPTY, MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(MockMvcResultMatchers.status().isOk())

                .andExpect(jsonPath("$.items", hasSize(1)))

                .andExpect(jsonPath("$.items.[0].requestId", is(777)))
                .andExpect(jsonPath("$.items.[0].date", is("TEST_DATE")))
                .andExpect(jsonPath("$.items.[0].currency", is("TEST_CURRENCY")))
                .andExpect(jsonPath("$.items.[0].amount", is(100.0)))
                .andExpect(jsonPath("$.items.[0].commission", is(10.0)))
                .andExpect(jsonPath("$.items.[0].system", is("TEST_SYSTEM")))
                .andExpect(jsonPath("$.items.[0].status", is("TEST_STATUS")))
                .andExpect(jsonPath("$.items.[0].operation", is("TEST_OPERATION")));

        verify(balanceService, times(1)).getPendingRequests(anyInt(), anyInt(), anyString(), anyString());
    }

    @Test
    public void getPendingRequests_exception() throws Exception {
        String ngDashboardException = "Failed to get pending requests: null";

        UriComponents uriComponents = UriComponentsBuilder.newInstance()
                .path(BASE_URL + "/pendingRequests")
                .build();

        PagedResult<RefillPendingRequestDto> myWalletsDetailedDtoPagedResult = new PagedResult<>();
        myWalletsDetailedDtoPagedResult.setItems(Collections.singletonList(getMockRefillPendingRequestDto()));

        Mockito.when(balanceService.getPendingRequests(anyInt(), anyInt(), anyString(), anyString())).thenThrow(Exception.class);

        mockMvc.perform(getApiRequestBuilder(uriComponents.toUri(), HttpMethod.GET, null, StringUtils.EMPTY, MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(jsonPath("$.detail", is(ngDashboardException)));

        verify(balanceService, times(1)).getPendingRequests(anyInt(), anyInt(), anyString(), anyString());
    }

    @Test
    public void revokeRefillRequest_isUserIsOwnerOk() throws Exception {
        Integer requestId = 225;
        String operation = "REFILL";
        RefillRequestFlatDto dto = new RefillRequestFlatDto();
        dto.setUserId(100);

        when(userService.getIdByEmail(anyString())).thenReturn(100);
        when(refillService.getFlatById(anyInt())).thenReturn(dto);
        doNothing().when(refillService).revokeRefillRequest(anyInt());

        mockMvc.perform(MockMvcRequestBuilders.delete(BASE_URL + "/pending/revoke/{requestId}/{operation}", requestId, operation)
                .contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(MockMvcResultMatchers.status().isOk());

        verify(userService, times(1)).getIdByEmail(anyString());
        verify(refillService, times(1)).getFlatById(anyInt());
        verify(refillService, times(1)).revokeRefillRequest(anyInt());
    }

    @Test
    public void revokeRefillRequest_isUserIsNotOwner() throws Exception {
        Integer requestId = 225;
        String operation = "REFILL";
        RefillRequestFlatDto dto = new RefillRequestFlatDto();
        dto.setUserId(10);

        when(userService.getIdByEmail(anyString())).thenReturn(100);
        when(refillService.getFlatById(anyInt())).thenReturn(dto);

        mockMvc.perform(MockMvcRequestBuilders.delete(BASE_URL + "/pending/revoke/{requestId}/{operation}", requestId, operation)
                .contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(MockMvcResultMatchers.status().isForbidden());

        verify(userService, times(1)).getIdByEmail(anyString());
        verify(refillService, times(1)).getFlatById(anyInt());
    }

    @Test
    public void revokeWithdrawRequest_isOwnerWithdraw() throws Exception {
        Integer requestId = 225;
        String operation = "WITHDRAW";
        WithdrawRequestFlatDto dto = new WithdrawRequestFlatDto();
        dto.setUserId(100);

        when(userService.getIdByEmail(anyString())).thenReturn(100);
        when(withdrawService.getFlatById(anyInt())).thenReturn(dto);
        doNothing().when(withdrawService).revokeWithdrawalRequest(anyInt());

        mockMvc.perform(MockMvcRequestBuilders.delete(BASE_URL + "/pending/revoke/{requestId}/{operation}", requestId, operation)
                .contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(MockMvcResultMatchers.status().isOk());

        verify(userService, times(1)).getIdByEmail(anyString());
        verify(withdrawService, times(1)).getFlatById(anyInt());
        verify(withdrawService, times(1)).revokeWithdrawalRequest(anyInt());
    }

    @Test
    public void revokeWithdrawRequest_isNotOwnerWithdraw() throws Exception {
        Integer requestId = 225;
        String operation = "WITHDRAW";
        WithdrawRequestFlatDto dto = new WithdrawRequestFlatDto();
        dto.setUserId(100);

        when(userService.getIdByEmail(anyString())).thenReturn(225);
        when(withdrawService.getFlatById(anyInt())).thenReturn(dto);

        mockMvc.perform(MockMvcRequestBuilders.delete(BASE_URL + "/pending/revoke/{requestId}/{operation}", requestId, operation)
                .contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(MockMvcResultMatchers.status().isForbidden());

        verify(userService, times(1)).getIdByEmail(anyString());
        verify(withdrawService, times(1)).getFlatById(anyInt());
    }

    @Test
    public void revokeTransferRequest_isOk() throws Exception {
        Integer requestId = 225;
        String operation = "TRANSFER";
        TransferRequestFlatDto dto = new TransferRequestFlatDto();
        dto.setUserId(100);

        when(userService.getIdByEmail(anyString())).thenReturn(100);
        when(transferService.getFlatById(anyInt())).thenReturn(dto);
        doNothing().when(transferService).revokeTransferRequest(anyInt());

        mockMvc.perform(MockMvcRequestBuilders.delete(BASE_URL + "/pending/revoke/{requestId}/{operation}", requestId, operation)
                .contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(MockMvcResultMatchers.status().isOk());

        verify(userService, times(1)).getIdByEmail(anyString());
        verify(transferService, times(1)).getFlatById(anyInt());
        verify(transferService, times(1)).revokeTransferRequest(anyInt());
    }

    @Test
    public void revokeTransferRequest_forbidden() throws Exception {
        Integer requestId = 225;
        String operation = "TRANSFER";
        TransferRequestFlatDto dto = new TransferRequestFlatDto();
        dto.setUserId(225);

        when(userService.getIdByEmail(anyString())).thenReturn(100);
        when(transferService.getFlatById(anyInt())).thenReturn(dto);

        mockMvc.perform(MockMvcRequestBuilders.delete(BASE_URL + "/pending/revoke/{requestId}/{operation}", requestId, operation)
                .contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(MockMvcResultMatchers.status().isForbidden());

        verify(userService, times(1)).getIdByEmail(anyString());
        verify(transferService, times(1)).getFlatById(anyInt());
    }

    @Test
    public void revokeWithdrawRequest_NgBalanceException() throws Exception {
        Integer requestId = 225;
        String operation = "REFILL";
        RefillRequestFlatDto dto = new RefillRequestFlatDto();
        dto.setUserId(100);

        String ngBalanceException = "Failed to revoke such for operation REFILL";

        when(userService.getIdByEmail(anyString())).thenReturn(100);
        when(refillService.getFlatById(anyInt())).thenThrow(Exception.class);

        mockMvc.perform(MockMvcRequestBuilders.delete(BASE_URL + "/pending/revoke/{requestId}/{operation}", requestId, operation)
                .contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(jsonPath("$.detail", is(ngBalanceException)));

        verify(userService, times(1)).getIdByEmail(anyString());
        verify(refillService, times(1)).getFlatById(anyInt());
    }

    @Test
    public void getUserTotalBalance_resultWallet_size_equals_one() throws Exception {
        UriComponents uriComponents = UriComponentsBuilder.newInstance()
                .path(BASE_URL + "/totalBalance")
                .build();

        Mockito.when(walletService.getAllWalletsForUserReduced(anyObject(), anyString(), anyObject(), anyObject()))
                .thenReturn(Collections.singletonList(getMockMyWalletsStatisticsDto("USD")));

        mockMvc.perform(getApiRequestBuilder(uriComponents.toUri(), HttpMethod.GET, null, StringUtils.EMPTY, MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(MockMvcResultMatchers.status().isOk())

                .andExpect(jsonPath("$.mapWallets", hasSize(1)))

                .andExpect(jsonPath("$.mapWallets.[0].needRefresh", is(Boolean.TRUE)))
                .andExpect(jsonPath("$.mapWallets.[0].page", is(0)))
                .andExpect(jsonPath("$.mapWallets.[0].currencyName", is("USD")))
                .andExpect(jsonPath("$.mapWallets.[0].description", is("TEST_DESCRIPTION")))
                .andExpect(jsonPath("$.mapWallets.[0].activeBalance", is("TEST_ACTIVE_BALANCE")))
                .andExpect(jsonPath("$.mapWallets.[0].totalBalance", is("125")));

        verify(walletService, times(1)).getAllWalletsForUserReduced(anyObject(), anyString(), anyObject(), anyObject());
    }

    @Test
    public void getUserTotalBalance_resultWallet_size_more_one() throws Exception {
        UriComponents uriComponents = UriComponentsBuilder.newInstance()
                .path(BASE_URL + "/totalBalance")
                .build();

        Mockito.when(walletService.getAllWalletsForUserReduced(anyObject(), anyString(), anyObject(), anyObject()))
                .thenReturn(Arrays.asList(
                        getMockMyWalletsStatisticsDto("USD")
                        , getMockMyWalletsStatisticsDto("BTC")
                        , getMockMyWalletsStatisticsDto("ETH"))
                );
        Mockito.when(exchangeRatesHolder.getAllRates()).thenReturn(
                Arrays.asList(
                        getMockExOrderStatisticsShortByPairsDto("ETH/USD"),
                        getMockExOrderStatisticsShortByPairsDto("BTC/USD"))
        );

        mockMvc.perform(getApiRequestBuilder(uriComponents.toUri(), HttpMethod.GET, null, StringUtils.EMPTY, MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(MockMvcResultMatchers.status().isOk())

                .andExpect(jsonPath("$.mapWallets", hasSize(3)))

                .andExpect(jsonPath("$.mapWallets.[0].needRefresh", is(Boolean.TRUE)))
                .andExpect(jsonPath("$.mapWallets.[0].page", is(0)))
                .andExpect(jsonPath("$.mapWallets.[0].currencyName", is("USD")))
                .andExpect(jsonPath("$.mapWallets.[0].description", is("TEST_DESCRIPTION")))
                .andExpect(jsonPath("$.mapWallets.[0].activeBalance", is("TEST_ACTIVE_BALANCE")))
                .andExpect(jsonPath("$.mapWallets.[0].totalBalance", is("125")))

                .andExpect(jsonPath("$.mapWallets.[1].needRefresh", is(Boolean.TRUE)))
                .andExpect(jsonPath("$.mapWallets.[1].page", is(0)))
                .andExpect(jsonPath("$.mapWallets.[1].currencyName", is("BTC")))
                .andExpect(jsonPath("$.mapWallets.[1].description", is("TEST_DESCRIPTION")))
                .andExpect(jsonPath("$.mapWallets.[1].activeBalance", is("TEST_ACTIVE_BALANCE")))
                .andExpect(jsonPath("$.mapWallets.[1].totalBalance", is("125")))

                .andExpect(jsonPath("$.mapWallets.[2].needRefresh", is(Boolean.TRUE)))
                .andExpect(jsonPath("$.mapWallets.[2].page", is(0)))
                .andExpect(jsonPath("$.mapWallets.[2].currencyName", is("ETH")))
                .andExpect(jsonPath("$.mapWallets.[2].description", is("TEST_DESCRIPTION")))
                .andExpect(jsonPath("$.mapWallets.[2].activeBalance", is("TEST_ACTIVE_BALANCE")))
                .andExpect(jsonPath("$.mapWallets.[2].totalBalance", is("125")));

        verify(walletService, times(1)).getAllWalletsForUserReduced(anyObject(), anyString(), anyObject(), anyObject());
        verify(exchangeRatesHolder, times(1)).getAllRates();
    }

    @Test
    public void getSingleCurrency_isOk() throws Exception {
        Integer currencyId = 100;

        Mockito.when(balanceService.findOne(anyString(), anyInt())).thenReturn(java.util.Optional.ofNullable(getMockMyWalletsDetailedDto()));
        Mockito.when(refillService.getOnConfirmationRefills(anyString(), anyInt())).thenReturn(Collections.EMPTY_LIST);

        mockMvc.perform(MockMvcRequestBuilders.get(BASE_URL + "/currencies/{currencyId}", currencyId)
                .contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(MockMvcResultMatchers.status().isOk())

                .andExpect(jsonPath("$.id", is(100)))
                .andExpect(jsonPath("$.userId", is(1)))
                .andExpect(jsonPath("$.currencyId", is(111)))
                .andExpect(jsonPath("$.currencyPrecision", is(222)))
                .andExpect(jsonPath("$.currencyName", is("TEST_CURRENCY_NAME")))
                .andExpect(jsonPath("$.currencyDescription", is("TEST_CURRENCY_DESCRIPTION")))
                .andExpect(jsonPath("$.activeBalance", is("TEST_ACTIVE_BALANCE")))
                .andExpect(jsonPath("$.onConfirmation", is("TEST_ON_CONFIRMATION")))
                .andExpect(jsonPath("$.onConfirmationStage", is("TEST_ON_CONFIRMATION_STAGE")))
                .andExpect(jsonPath("$.onConfirmationCount", is("TEST_ON_CONFIRMATION_COUNT")))
                .andExpect(jsonPath("$.reservedBalance", is("TEST_RESERVED_BALANCE")))
                .andExpect(jsonPath("$.reservedByOrders", is("TEST_RESERVED_BY_ORDERS")))
                .andExpect(jsonPath("$.reservedByMerchant", is("TEST_RESERVED_BY_MERCHANT")))
                .andExpect(jsonPath("$.btcAmount", is("TEST_BTC_AMOUNT")))
                .andExpect(jsonPath("$.usdAmount", is("TEST_USD_AMOUNT")))
                .andExpect(jsonPath("$.confirmations", is(Collections.EMPTY_LIST)));

        verify(balanceService, times(1)).findOne(anyString(), anyInt());
        verify(refillService, times(1)).getOnConfirmationRefills(anyString(), anyInt());
    }

    @Test
    public void getSingleCurrency_isOk_confirmationRefills_equals_null() throws Exception {
        Integer currencyId = 100;

        Mockito.when(balanceService.findOne(anyString(), anyInt())).thenReturn(java.util.Optional.ofNullable(getMockMyWalletsDetailedDto()));
        Mockito.when(refillService.getOnConfirmationRefills(anyString(), anyInt())).thenReturn(null);

        mockMvc.perform(MockMvcRequestBuilders.get(BASE_URL + "/currencies/{currencyId}", currencyId)
                .contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(MockMvcResultMatchers.status().isOk())

                .andExpect(jsonPath("$.id", is(100)))
                .andExpect(jsonPath("$.userId", is(1)))
                .andExpect(jsonPath("$.currencyId", is(111)))
                .andExpect(jsonPath("$.currencyPrecision", is(222)))
                .andExpect(jsonPath("$.currencyName", is("TEST_CURRENCY_NAME")))
                .andExpect(jsonPath("$.currencyDescription", is("TEST_CURRENCY_DESCRIPTION")))
                .andExpect(jsonPath("$.activeBalance", is("TEST_ACTIVE_BALANCE")))
                .andExpect(jsonPath("$.onConfirmation", is("TEST_ON_CONFIRMATION")))
                .andExpect(jsonPath("$.onConfirmationStage", is("TEST_ON_CONFIRMATION_STAGE")))
                .andExpect(jsonPath("$.onConfirmationCount", is("TEST_ON_CONFIRMATION_COUNT")))
                .andExpect(jsonPath("$.reservedBalance", is("TEST_RESERVED_BALANCE")))
                .andExpect(jsonPath("$.reservedByOrders", is("TEST_RESERVED_BY_ORDERS")))
                .andExpect(jsonPath("$.reservedByMerchant", is("TEST_RESERVED_BY_MERCHANT")))
                .andExpect(jsonPath("$.btcAmount", is("TEST_BTC_AMOUNT")))
                .andExpect(jsonPath("$.usdAmount", is("TEST_USD_AMOUNT")))
                .andExpect(jsonPath("$.confirmations", is(Collections.EMPTY_LIST)));

        verify(balanceService, times(1)).findOne(anyString(), anyInt());
        verify(refillService, times(1)).getOnConfirmationRefills(anyString(), anyInt());
    }

    @Test
    public void getSingleCurrency_notFound() throws Exception {
        Integer currencyId = 100;

        Mockito.when(balanceService.findOne(anyString(), anyInt())).thenReturn(Optional.empty());
        Mockito.when(refillService.getOnConfirmationRefills(anyString(), anyInt())).thenReturn(null);

        mockMvc.perform(MockMvcRequestBuilders.get(BASE_URL + "/currencies/{currencyId}", currencyId)
                .contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(MockMvcResultMatchers.status().isNotFound());

        verify(balanceService, times(1)).findOne(anyString(), anyInt());
    }

    @Test
    public void getSingleCurrency_exception() throws Exception {
        Integer currencyId = -1;
        String ngBalanceException = "Failed to get single currency balance details as null";

        Mockito.when(balanceService.findOne(anyString(), anyInt())).thenThrow(Exception.class);
        Mockito.when(refillService.getOnConfirmationRefills(anyString(), anyInt())).thenReturn(Collections.EMPTY_LIST);

        mockMvc.perform(MockMvcRequestBuilders.get(BASE_URL + "/currencies/{currencyId}", currencyId)
                .contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(jsonPath("$.detail", is(ngBalanceException)));

        verify(balanceService, times(1)).findOne(anyString(), anyInt());
        verify(refillService, never()).getOnConfirmationRefills(anyString(), anyInt());
    }


    @Test
    public void getMyInputOutputData_isOk() throws Exception {
        UriComponents uriComponents = UriComponentsBuilder.newInstance()
                .path(BASE_URL + "/inputOutputData")
                .build();

        PagedResult<MyInputOutputHistoryDto> myInputOutputHistoryDtoPagedResult = new PagedResult<>();
        myInputOutputHistoryDtoPagedResult.setItems(Collections.singletonList(getMockMyInputOutputHistoryDto()));

        Mockito.when(balanceService.getUserInputOutputHistory(anyString(), anyInt(), anyString(), anyObject(),
                anyObject(), anyInt(), anyInt(), anyObject())).thenReturn(myInputOutputHistoryDtoPagedResult);

        mockMvc.perform(getApiRequestBuilder(uriComponents.toUri(), HttpMethod.GET, null, StringUtils.EMPTY, MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(MockMvcResultMatchers.status().isOk())

                .andExpect(jsonPath("$.items", hasSize(1)))

                .andExpect(jsonPath("$.items.[0].needRefresh", is(Boolean.TRUE)))
                .andExpect(jsonPath("$.items.[0].page", is(0)))
                .andExpect(jsonPath("$.items.[0].datetime", is("2019-03-13 14:00:00")))
                .andExpect(jsonPath("$.items.[0].currencyName", is("TEST_CURRENCY_NAME")))
                .andExpect(jsonPath("$.items.[0].amount", is("TEST_AMOUNT")))
                .andExpect(jsonPath("$.items.[0].commissionAmount", is("TEST_COMMISSION_AMOUNT")))
                .andExpect(jsonPath("$.items.[0].merchantName", is("TEST_MERCHANT_NAME")))
                .andExpect(jsonPath("$.items.[0].operationType", is("TEST_OPERATION_TYPE")))
                .andExpect(jsonPath("$.items.[0].transactionId", is(100)))
                .andExpect(jsonPath("$.items.[0].provided", is(200)))
                .andExpect(jsonPath("$.items.[0].transactionProvided", is("TEST_TRANSACTION_PROVIDED")))
                .andExpect(jsonPath("$.items.[0].id", is(300)))
                .andExpect(jsonPath("$.items.[0].destination", is("TEST_DESCRIPTION")))
                .andExpect(jsonPath("$.items.[0].userId", is(400)))
                .andExpect(jsonPath("$.items.[0].bankAccount", is("TEST_BANK_ACCOUNT")))
                .andExpect(jsonPath("$.items.[0].statusUpdateDate", is("2019-03-14 15:00:00")))
                .andExpect(jsonPath("$.items.[0].summaryStatus", is("TEST_SUMMARY_STATUS")))
                .andExpect(jsonPath("$.items.[0].userFullName", is("TEST_USER_FULL_NAME")))
                .andExpect(jsonPath("$.items.[0].remark", is("TEST_REMARK")))
                .andExpect(jsonPath("$.items.[0].sourceType", is("REFILL")))
                .andExpect(jsonPath("$.items.[0].sourceId", is(11)))
                .andExpect(jsonPath("$.items.[0].confirmation", is(700)))
                .andExpect(jsonPath("$.items.[0].neededConfirmations", is(800)))
                .andExpect(jsonPath("$.items.[0].adminHolderId", is(900)))
                .andExpect(jsonPath("$.items.[0].authorisedUserId", is(1000)))
                .andExpect(jsonPath("$.items.[0].buttons", is(Collections.EMPTY_LIST)))
                .andExpect(jsonPath("$.items.[0].transactionHash", is("TEST_TRANSACTIONAL_HASH")))
                .andExpect(jsonPath("$.items.[0].market", is("TEST_MARKET")))
                .andExpect(jsonPath("$.items.[0].accepted", is(Boolean.TRUE)));

        verify(balanceService, times(1)).getUserInputOutputHistory(anyString(), anyInt(), anyString(), anyObject(),
                anyObject(), anyInt(), anyInt(), anyObject());
    }

    @Test
    public void getMyInputOutputData_exception() throws Exception {
        String ngBalanceException = "Failed to get user inputOutputData as null";

        UriComponents uriComponents = UriComponentsBuilder.newInstance()
                .path(BASE_URL + "/inputOutputData")
                .build();

        Mockito.when(balanceService.getUserInputOutputHistory(anyString(), anyInt(), anyString(), anyObject(),
                anyObject(), anyInt(), anyInt(), anyObject())).thenThrow(Exception.class);

        mockMvc.perform(getApiRequestBuilder(uriComponents.toUri(), HttpMethod.GET, null, StringUtils.EMPTY, MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(jsonPath("$.detail", is(ngBalanceException)));

        verify(balanceService, times(1)).getUserInputOutputHistory(anyString(), anyInt(), anyString(), anyObject(),
                anyObject(), anyInt(), anyInt(), anyObject());
    }

    @Test
    public void getDefaultMyInputOutputData_isOk() throws Exception {
        UriComponents uriComponents = UriComponentsBuilder.newInstance()
                .path(BASE_URL + "/inputOutputData/default")
                .build();

        PagedResult<MyInputOutputHistoryDto> myInputOutputHistoryDtoPagedResult = new PagedResult<>();
        myInputOutputHistoryDtoPagedResult.setItems(Collections.singletonList(getMockMyInputOutputHistoryDto()));

        Mockito.when(balanceService.getUserInputOutputHistory(anyString(), anyInt(), anyString(), anyObject(),
                anyObject(), anyInt(), anyInt(), anyObject())).thenReturn(myInputOutputHistoryDtoPagedResult);

        mockMvc.perform(getApiRequestBuilder(uriComponents.toUri(), HttpMethod.GET, null, StringUtils.EMPTY, MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(MockMvcResultMatchers.status().isOk())

                .andExpect(jsonPath("$.items", hasSize(1)))

                .andExpect(jsonPath("$.items.[0].needRefresh", is(Boolean.TRUE)))
                .andExpect(jsonPath("$.items.[0].page", is(0)))
                .andExpect(jsonPath("$.items.[0].datetime", is("2019-03-13 14:00:00")))
                .andExpect(jsonPath("$.items.[0].currencyName", is("TEST_CURRENCY_NAME")))
                .andExpect(jsonPath("$.items.[0].amount", is("TEST_AMOUNT")))
                .andExpect(jsonPath("$.items.[0].commissionAmount", is("TEST_COMMISSION_AMOUNT")))
                .andExpect(jsonPath("$.items.[0].merchantName", is("TEST_MERCHANT_NAME")))
                .andExpect(jsonPath("$.items.[0].operationType", is("TEST_OPERATION_TYPE")))
                .andExpect(jsonPath("$.items.[0].transactionId", is(100)))
                .andExpect(jsonPath("$.items.[0].provided", is(200)))
                .andExpect(jsonPath("$.items.[0].transactionProvided", is("TEST_TRANSACTION_PROVIDED")))
                .andExpect(jsonPath("$.items.[0].id", is(300)))
                .andExpect(jsonPath("$.items.[0].destination", is("TEST_DESCRIPTION")))
                .andExpect(jsonPath("$.items.[0].userId", is(400)))
                .andExpect(jsonPath("$.items.[0].bankAccount", is("TEST_BANK_ACCOUNT")))
                .andExpect(jsonPath("$.items.[0].statusUpdateDate", is("2019-03-14 15:00:00")))
                .andExpect(jsonPath("$.items.[0].summaryStatus", is("TEST_SUMMARY_STATUS")))
                .andExpect(jsonPath("$.items.[0].userFullName", is("TEST_USER_FULL_NAME")))
                .andExpect(jsonPath("$.items.[0].remark", is("TEST_REMARK")))
                .andExpect(jsonPath("$.items.[0].sourceType", is("REFILL")))
                .andExpect(jsonPath("$.items.[0].sourceId", is(11)))
                .andExpect(jsonPath("$.items.[0].confirmation", is(700)))
                .andExpect(jsonPath("$.items.[0].neededConfirmations", is(800)))
                .andExpect(jsonPath("$.items.[0].adminHolderId", is(900)))
                .andExpect(jsonPath("$.items.[0].authorisedUserId", is(1000)))
                .andExpect(jsonPath("$.items.[0].buttons", is(Collections.EMPTY_LIST)))
                .andExpect(jsonPath("$.items.[0].transactionHash", is("TEST_TRANSACTIONAL_HASH")))
                .andExpect(jsonPath("$.items.[0].market", is("TEST_MARKET")))
                .andExpect(jsonPath("$.items.[0].accepted", is(Boolean.TRUE)));

        verify(balanceService, times(1)).getUserInputOutputHistory(anyString(), anyInt(), anyString(), anyObject(),
                anyObject(), anyInt(), anyInt(), anyObject());
    }

    @Test
    public void getDefaultMyInputOutputData_exception() throws Exception {
        String ngBalanceException = "Failed to get user default inputOutputData as null";

        UriComponents uriComponents = UriComponentsBuilder.newInstance()
                .path(BASE_URL + "/inputOutputData/default")
                .build();

        Mockito.when(balanceService.getUserInputOutputHistory(anyString(), anyInt(), anyString(), anyObject(),
                anyObject(), anyInt(), anyInt(), anyObject())).thenThrow(Exception.class);

        mockMvc.perform(getApiRequestBuilder(uriComponents.toUri(), HttpMethod.GET, null, StringUtils.EMPTY, MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(jsonPath("$.detail", is(ngBalanceException)));

        verify(balanceService, times(1)).getUserInputOutputHistory(anyString(), anyInt(), anyString(), anyObject(),
                anyObject(), anyInt(), anyInt(), anyObject());
    }

    @Test
    public void getBtcAndUsdBalancesSumDefault() throws Exception {
        Map<String, BigDecimal> balance = new HashMap<>();
        balance.put("BTC", BigDecimal.valueOf(0.00002343));
        balance.put("USD", BigDecimal.valueOf(32.00));

        Mockito.when(balanceService.getBalancesSumInBtcAndUsd()).thenReturn(balance);

        mockMvc.perform(MockMvcRequestBuilders.get(BASE_URL + "/myBalances")
                .contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(jsonPath("$.data.BTC", is(0.00002343)))
                .andExpect(jsonPath("$.data.USD", is(32.0)));
    }

    @Test
    public void getBtcAndUsdBalancesSum() throws Exception {
        Map<String, BigDecimal> balance = new HashMap<>();
        balance.put("BTC", BigDecimal.valueOf(0.00002343));
        balance.put("ETH", BigDecimal.valueOf(32.00));

        UriComponentsBuilder builder = UriComponentsBuilder.newInstance()
                .path(BASE_URL + "/myBalances")
                .queryParam("names", "BTC", "ETH");

        Mockito.when(balanceService.getActiveBalanceByCurrencyNamesAndEmail(anyString(), anySet())).thenReturn(balance);

        mockMvc.perform(getApiRequestBuilder(builder.build().toUri(), HttpMethod.GET, null, StringUtils.EMPTY, MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(jsonPath("$.data.BTC", is(0.00002343)))
                .andExpect(jsonPath("$.data.ETH", is(32.0)));
    }

    private MyInputOutputHistoryDto getMockMyInputOutputHistoryDto() {
        MyInputOutputHistoryDto myInputOutputHistoryDto = new MyInputOutputHistoryDto();
        myInputOutputHistoryDto.setDatetime(LocalDateTime.of(2019, 03, 13, 14, 00, 00));
        myInputOutputHistoryDto.setCurrencyName("TEST_CURRENCY_NAME");
        myInputOutputHistoryDto.setAmount("TEST_AMOUNT");
        myInputOutputHistoryDto.setCommissionAmount("TEST_COMMISSION_AMOUNT");
        myInputOutputHistoryDto.setMerchantName("TEST_MERCHANT_NAME");
        myInputOutputHistoryDto.setOperationType("TEST_OPERATION_TYPE");
        myInputOutputHistoryDto.setTransactionId(100);
        myInputOutputHistoryDto.setProvided(200);
        myInputOutputHistoryDto.setTransactionProvided("TEST_TRANSACTION_PROVIDED");
        myInputOutputHistoryDto.setId(300);
        myInputOutputHistoryDto.setDestination("TEST_DESCRIPTION");
        myInputOutputHistoryDto.setUserId(400);
        myInputOutputHistoryDto.setBankAccount("TEST_BANK_ACCOUNT");
        myInputOutputHistoryDto.setStatusUpdateDate(LocalDateTime.of(2019, 03, 14, 15, 00, 00));
        myInputOutputHistoryDto.setSummaryStatus("TEST_SUMMARY_STATUS");
        myInputOutputHistoryDto.setUserFullName("TEST_USER_FULL_NAME");
        myInputOutputHistoryDto.setRemark("TEST_REMARK");
        myInputOutputHistoryDto.setSourceId(TransactionSourceType.REFILL.toString());
        myInputOutputHistoryDto.setSourceType(TransactionSourceType.REFILL);
        myInputOutputHistoryDto.setConfirmation(700);
        myInputOutputHistoryDto.setNeededConfirmations(800);
        myInputOutputHistoryDto.setAdminHolderId(900);
        myInputOutputHistoryDto.setAuthorisedUserId(1000);
        myInputOutputHistoryDto.setButtons(Collections.EMPTY_LIST);
        myInputOutputHistoryDto.setTransactionHash("TEST_TRANSACTIONAL_HASH");
        myInputOutputHistoryDto.setMarket("TEST_MARKET");
        myInputOutputHistoryDto.setAccepted(Boolean.TRUE);

        return myInputOutputHistoryDto;
    }

    private ExOrderStatisticsShortByPairsDto getMockExOrderStatisticsShortByPairsDto(String currencyPairName) {
        ExOrderStatisticsShortByPairsDto exOrderStatisticsShortByPairsDto = new ExOrderStatisticsShortByPairsDto();
        exOrderStatisticsShortByPairsDto.setNeedRefresh(Boolean.TRUE);
        exOrderStatisticsShortByPairsDto.setPage(0);
        exOrderStatisticsShortByPairsDto.setCurrencyPairId(222);
        exOrderStatisticsShortByPairsDto.setCurrencyPairName(currencyPairName);
        exOrderStatisticsShortByPairsDto.setCurrencyPairPrecision(22);
        exOrderStatisticsShortByPairsDto.setLastOrderRate("22");
        exOrderStatisticsShortByPairsDto.setPredLastOrderRate("");
        exOrderStatisticsShortByPairsDto.setPercentChange("");
        exOrderStatisticsShortByPairsDto.setMarket("");
        exOrderStatisticsShortByPairsDto.setPriceInUSD("");
        exOrderStatisticsShortByPairsDto.setType(CurrencyPairType.MAIN);
        exOrderStatisticsShortByPairsDto.setVolume("");
        exOrderStatisticsShortByPairsDto.setCurrencyVolume("");
        exOrderStatisticsShortByPairsDto.setHigh24hr("");
        exOrderStatisticsShortByPairsDto.setLow24hr("");
        exOrderStatisticsShortByPairsDto.setLastUpdateCache("");

        return exOrderStatisticsShortByPairsDto;
    }

    private MyWalletsStatisticsDto getMockMyWalletsStatisticsDto(String currencyName) {
        MyWalletsStatisticsDto myWalletsStatisticsDto = new MyWalletsStatisticsDto();
        myWalletsStatisticsDto.setNeedRefresh(Boolean.TRUE);
        myWalletsStatisticsDto.setPage(0);
        myWalletsStatisticsDto.setDescription("TEST_DESCRIPTION");
        myWalletsStatisticsDto.setCurrencyName(currencyName);
        myWalletsStatisticsDto.setActiveBalance("TEST_ACTIVE_BALANCE");
        myWalletsStatisticsDto.setTotalBalance("125");

        return myWalletsStatisticsDto;
    }

    private RefillPendingRequestDto getMockRefillPendingRequestDto() {
        return new RefillPendingRequestDto(777, "TEST_DATE", "TEST_CURRENCY", 100.0,
                10.0, "TEST_SYSTEM", "TEST_STATUS", "TEST_OPERATION");
    }

    private MyWalletsDetailedDto getMockMyWalletsDetailedDto() {
        MyWalletsDetailedDto myWalletsDetailedDto = new MyWalletsDetailedDto();
        myWalletsDetailedDto.setId(100);
        myWalletsDetailedDto.setUserId(1);
        myWalletsDetailedDto.setCurrencyId(111);
        myWalletsDetailedDto.setCurrencyPrecision(222);
        myWalletsDetailedDto.setCurrencyName("TEST_CURRENCY_NAME");
        myWalletsDetailedDto.setCurrencyDescription("TEST_CURRENCY_DESCRIPTION");
        myWalletsDetailedDto.setActiveBalance("TEST_ACTIVE_BALANCE");
        myWalletsDetailedDto.setOnConfirmation("TEST_ON_CONFIRMATION");
        myWalletsDetailedDto.setOnConfirmationStage("TEST_ON_CONFIRMATION_STAGE");
        myWalletsDetailedDto.setOnConfirmationCount("TEST_ON_CONFIRMATION_COUNT");
        myWalletsDetailedDto.setReservedBalance("TEST_RESERVED_BALANCE");
        myWalletsDetailedDto.setReservedByOrders("TEST_RESERVED_BY_ORDERS");
        myWalletsDetailedDto.setReservedByMerchant("TEST_RESERVED_BY_MERCHANT");
        myWalletsDetailedDto.setBtcAmount("TEST_BTC_AMOUNT");
        myWalletsDetailedDto.setUsdAmount("TEST_USD_AMOUNT");
        myWalletsDetailedDto.setConfirmations(Collections.EMPTY_LIST);

        return myWalletsDetailedDto;
    }
}