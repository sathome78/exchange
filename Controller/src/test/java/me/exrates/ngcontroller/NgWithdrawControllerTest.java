package me.exrates.ngcontroller;

import me.exrates.model.Currency;
import me.exrates.model.dto.PinOrderInfoDto;
import me.exrates.model.dto.WithdrawRequestParamsDto;
import me.exrates.model.enums.OperationType;
import me.exrates.model.enums.UserRole;
import me.exrates.model.ngExceptions.NgResponseException;
import me.exrates.security.service.SecureService;
import me.exrates.service.CurrencyService;
import me.exrates.service.InputOutputService;
import me.exrates.service.MerchantService;
import me.exrates.service.UserService;
import me.exrates.service.WalletService;
import me.exrates.service.WithdrawService;
import me.exrates.service.exception.InvalidAmountException;
import me.exrates.service.merchantStrategy.IWithdrawable;
import me.exrates.service.merchantStrategy.MerchantServiceContext;
import me.exrates.service.notifications.G2faService;
import me.exrates.service.userOperation.UserOperationService;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.context.MessageSource;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.util.NestedServletException;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.hamcrest.Matchers.hasKey;
import static org.hamcrest.Matchers.hasValue;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyList;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class NgWithdrawControllerTest extends AngularApiCommonTest {
    private static final String BASE_URL = "/api/private/v2/balances/withdraw";

    @Mock
    private CurrencyService currencyService;
    @Mock
    private G2faService g2faService;
    @Mock
    private InputOutputService inputOutputService;
    @Mock
    private MerchantService merchantService;
    @Mock
    private MessageSource messageSource;
    @Mock
    private SecureService secureService;
    @Mock
    private UserOperationService userOperationService;
    @Mock
    private UserService userService;
    @Mock
    private WalletService walletService;
    @Mock
    private WithdrawService withdrawService;
    @Mock
    private MerchantServiceContext merchantServiceContext;

    @InjectMocks
    NgWithdrawController ngWithdrawController;

    private MockMvc mockMvc;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        mockMvc = MockMvcBuilders
                .standaloneSetup(ngWithdrawController)
                .build();

        SecurityContextHolder.getContext()
                .setAuthentication(new AnonymousAuthenticationToken("guest", "test@test.ru",
                        AuthorityUtils.createAuthorityList("ADMIN")));
    }

    @Test
    public void createWithdrawalRequest_UserOperationAccessException() throws Exception {
        when(userOperationService.getStatusAuthorityForUserByOperation(anyInt(), anyObject())).thenReturn(Boolean.FALSE);
        when(messageSource.getMessage(anyString(), anyObject(), anyObject())).thenReturn("TEST ERROR MSG");

        mockMvc.perform(post(BASE_URL + "/request/create")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(objectMapper.writeValueAsString(getMockWithdrawRequestParamsDto("TEST_SECURITY_CODE"))))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.detail", is("TEST ERROR MSG")));

        verify(userOperationService, times(1))
                .getStatusAuthorityForUserByOperation(anyInt(), anyObject());
        verify(messageSource, times(1)).getMessage(anyString(), anyObject(), anyObject());
    }

    @Test
    public void createWithdrawalRequest_RequestLimitExceededException() {
        when(userOperationService.getStatusAuthorityForUserByOperation(anyInt(), anyObject())).thenReturn(Boolean.TRUE);
        when(messageSource.getMessage(anyString(), anyObject(), anyObject())).thenReturn("TEST ERROR MSG");
        when(withdrawService.checkOutputRequestsLimit(anyInt(), anyString())).thenReturn(Boolean.FALSE);

        try {
            mockMvc.perform(post(BASE_URL + "/request/create")
                    .contentType(MediaType.APPLICATION_JSON_UTF8)
                    .content(objectMapper.writeValueAsString(getMockWithdrawRequestParamsDto("TEST_SECURITY_CODE"))));
            Assert.fail();
        } catch (Exception e) {
            String expected = "TEST ERROR MSG";
            assertEquals(expected, e.getCause().getMessage());
        }

        verify(userOperationService, times(1))
                .getStatusAuthorityForUserByOperation(anyInt(), anyObject());
        verify(messageSource, times(1)).getMessage(anyString(), anyObject(), anyObject());
    }

    @Test
    public void createWithdrawalRequest_forbidden() throws Exception {
        when(userOperationService.getStatusAuthorityForUserByOperation(anyInt(), anyObject())).thenReturn(Boolean.TRUE);
        when(withdrawService.checkOutputRequestsLimit(anyInt(), anyString())).thenReturn(Boolean.TRUE);
        doNothing().when(merchantService).checkDestinationTag(anyInt(), anyString());

        mockMvc.perform(post(BASE_URL + "/request/create")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(objectMapper.writeValueAsString(getMockWithdrawRequestParamsDto(""))))
                .andExpect(status().isForbidden());

        verify(userOperationService, times(1))
                .getStatusAuthorityForUserByOperation(anyInt(), anyObject());
    }

    @Test
    public void createWithdrawalRequest_checkGoogle2faVerifyCode_IncorrectPinException() throws Exception {
        when(userOperationService.getStatusAuthorityForUserByOperation(anyInt(), anyObject())).thenReturn(Boolean.TRUE);
        when(withdrawService.checkOutputRequestsLimit(anyInt(), anyString())).thenReturn(Boolean.TRUE);
        doNothing().when(merchantService).checkDestinationTag(anyInt(), anyString());
        when(userService.findByEmail(anyString())).thenReturn(getMockUser());
        when(g2faService.isGoogleAuthenticatorEnable(anyInt())).thenReturn(Boolean.TRUE);
        when(g2faService.checkGoogle2faVerifyCode(anyString(), anyInt())).thenReturn(Boolean.FALSE);

        mockMvc.perform(post(BASE_URL + "/request/create")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(objectMapper.writeValueAsString(getMockWithdrawRequestParamsDto("TEST_SECURITY_CODE"))))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.detail", is("Incorrect Google 2FA oauth code: TEST_SECURITY_CODE")));

        verify(userOperationService, times(1))
                .getStatusAuthorityForUserByOperation(anyInt(), anyObject());
        verify(withdrawService, times(1)).checkOutputRequestsLimit(anyInt(), anyString());
        verify(merchantService, times(1)).checkDestinationTag(anyInt(), anyString());
        verify(userService, times(1)).findByEmail(anyString());
        verify(g2faService, times(1)).isGoogleAuthenticatorEnable(anyInt());
        verify(g2faService, times(1)).checkGoogle2faVerifyCode(anyString(), anyInt());
    }

    @Test
    public void createWithdrawalRequest_checkPin_IncorrectPinException() throws Exception {
        when(userOperationService.getStatusAuthorityForUserByOperation(anyInt(), anyObject())).thenReturn(Boolean.TRUE);
        when(withdrawService.checkOutputRequestsLimit(anyInt(), anyString())).thenReturn(Boolean.TRUE);
        doNothing().when(merchantService).checkDestinationTag(anyInt(), anyString());
        when(userService.findByEmail(anyString())).thenReturn(getMockUser());
        when(g2faService.isGoogleAuthenticatorEnable(anyInt())).thenReturn(Boolean.FALSE);
        when(userService.checkPin(anyString(), anyString(), anyObject())).thenReturn(Boolean.FALSE);
        when(secureService.sendWithdrawPinCode(anyObject(), anyString(), anyString())).thenReturn(getMockNotificationResultDto());
        when(currencyService.getById(anyInt())).thenReturn(getCurrency());

        mockMvc.perform(post(BASE_URL + "/request/create")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(objectMapper.writeValueAsString(getMockWithdrawRequestParamsDto("TEST_SECURITY_CODE"))))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.detail", is("Incorrect pin: TEST_SECURITY_CODE")));

        verify(userOperationService, times(1))
                .getStatusAuthorityForUserByOperation(anyInt(), anyObject());
        verify(withdrawService, times(1)).checkOutputRequestsLimit(anyInt(), anyString());
        verify(merchantService, times(1)).checkDestinationTag(anyInt(), anyString());
        verify(userService, times(1)).findByEmail(anyString());
        verify(g2faService, times(1)).isGoogleAuthenticatorEnable(anyInt());
        verify(userService, times(1)).checkPin(anyString(), anyString(), anyObject());
        verify(secureService, times(1)).sendWithdrawPinCode(anyObject(), anyString(), anyString());
    }

    @Test
    public void createWithdrawalRequest_isOk() throws Exception {
        Map<String, String> response = new HashMap<>();
        response.put("TEST_KEY", "TEST_VALUE");

        when(userOperationService.getStatusAuthorityForUserByOperation(anyInt(), anyObject())).thenReturn(Boolean.TRUE);
        when(withdrawService.checkOutputRequestsLimit(anyInt(), anyString())).thenReturn(Boolean.TRUE);
        doNothing().when(merchantService).checkDestinationTag(anyInt(), anyString());
        when(userService.findByEmail(anyString())).thenReturn(getMockUser());
        when(g2faService.isGoogleAuthenticatorEnable(anyInt())).thenReturn(Boolean.FALSE);
        when(userService.checkPin(anyString(), anyString(), anyObject())).thenReturn(Boolean.TRUE);
        when(inputOutputService.prepareCreditsOperation(anyObject(), anyString(), anyObject()))
                .thenReturn(getMockCreditsOperation());
        when(withdrawService.createWithdrawalRequest(anyObject(), anyObject())).thenReturn(response);

        mockMvc.perform(post(BASE_URL + "/request/create")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(objectMapper.writeValueAsString(getMockWithdrawRequestParamsDto("TEST_SECURITY_CODE"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasKey("TEST_KEY")))
                .andExpect(jsonPath("$", hasValue("TEST_VALUE")));

        verify(userOperationService, times(1))
                .getStatusAuthorityForUserByOperation(anyInt(), anyObject());
        verify(withdrawService, times(1)).checkOutputRequestsLimit(anyInt(), anyString());
        verify(merchantService, times(1)).checkDestinationTag(anyInt(), anyString());
        verify(userService, times(1)).findByEmail(anyString());
        verify(g2faService, times(1)).isGoogleAuthenticatorEnable(anyInt());
        verify(userService, times(1)).checkPin(anyString(), anyString(), anyObject());
        verify(inputOutputService, times(1))
                .prepareCreditsOperation(anyObject(), anyString(), anyObject());
    }

    @Test
    public void createWithdrawalRequest_InvalidAmountException() throws Exception {
        when(userOperationService.getStatusAuthorityForUserByOperation(anyInt(), anyObject())).thenReturn(Boolean.TRUE);
        when(withdrawService.checkOutputRequestsLimit(anyInt(), anyString())).thenReturn(Boolean.TRUE);
        doNothing().when(merchantService).checkDestinationTag(anyInt(), anyString());
        when(userService.findByEmail(anyString())).thenReturn(getMockUser());
        when(g2faService.isGoogleAuthenticatorEnable(anyInt())).thenReturn(Boolean.FALSE);
        when(userService.checkPin(anyString(), anyString(), anyObject())).thenReturn(Boolean.TRUE);
        when(inputOutputService.prepareCreditsOperation(anyObject(), anyString(), anyObject()))
                .thenThrow(InvalidAmountException.class);

        try {
            mockMvc.perform(post(BASE_URL + "/request/create")
                    .contentType(MediaType.APPLICATION_JSON_UTF8)
                    .content(objectMapper.writeValueAsString(getMockWithdrawRequestParamsDto("TEST_SECURITY_CODE"))))
                    .andExpect(status().isBadRequest());
            Assert.fail();
        } catch (Exception e) {
            assertTrue(((NestedServletException) e).getRootCause() instanceof NgResponseException);
            NgResponseException responseException = (NgResponseException) ((NestedServletException) e).getRootCause();
            assertEquals("FAILED_TO_CREATE_WITHDRAW_REQUEST", responseException.getTitle());

            String expected = "Failed to create withdraw request me.exrates.service.exception.InvalidAmountException";
            assertEquals(expected, e.getCause().getMessage());
        }

        verify(userOperationService, times(1))
                .getStatusAuthorityForUserByOperation(anyInt(), anyObject());
        verify(withdrawService, times(1)).checkOutputRequestsLimit(anyInt(), anyString());
        verify(merchantService, times(1)).checkDestinationTag(anyInt(), anyString());
        verify(userService, times(1)).findByEmail(anyString());
        verify(g2faService, times(1)).isGoogleAuthenticatorEnable(anyInt());
        verify(userService, times(1)).checkPin(anyString(), anyString(), anyObject());
        verify(inputOutputService, times(1))
                .prepareCreditsOperation(anyObject(), anyString(), anyObject());
    }

    @Test
    public void outputCredits_isOk_merchantCurrencyData_is_empty() throws Exception {
        when(currencyService.findByName(anyString())).thenReturn(getMockCurrency("TEST_CURRENCY"));
        when(userService.findByEmail(anyString())).thenReturn(getMockUser());
        when(walletService.findByUserAndCurrency(anyObject(), anyObject())).thenReturn(getMockWallet());
        when(userService.getUserRoleFromSecurityContext()).thenReturn(UserRole.USER);
        when(currencyService.retrieveMinLimitForRoleAndCurrency(anyObject(), anyObject(), anyInt())).thenReturn(BigDecimal.TEN);
        when(currencyService.retrieveMaxDailyRequestForRoleAndCurrency(anyObject(), anyObject(), anyInt())).thenReturn(BigDecimal.TEN);
        when(withdrawService.getLeftOutputRequestsSum(anyInt(), anyString())).thenReturn(BigDecimal.ONE);
        when(currencyService.getCurrencyScaleByCurrencyId(anyInt())).thenReturn(getMockMerchantCurrencyScaleDto());
        when(merchantService.getAllUnblockedForOperationTypeByCurrencies(anyList(), anyObject())).thenReturn((Collections.EMPTY_LIST));
        when(currencyService.getWarningForCurrency(anyInt(), anyObject())).thenReturn(getWarningCodeList());

        mockMvc.perform(get(BASE_URL + "/merchants/output")
                .param("currency", "BTC")
                .contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.operationType", is("OUTPUT")))
                .andExpect(jsonPath("$.minWithdrawSum", is(10.0)))
                .andExpect(jsonPath("$.maxDailyRequestSum", is(10.0)))
                .andExpect(jsonPath("$.warningCodeList.[0]", is("WARNING_CODE_ONE")))
                .andExpect(jsonPath("$.warningCodeList.[1]", is("WARNING_CODE_TWO")));

        verify(currencyService, times(1)).findByName(anyString());
        verify(userService, times(1)).findByEmail(anyString());
        verify(walletService, times(1)).findByUserAndCurrency(anyObject(), anyObject());
        verify(userService, times(1)).getUserRoleFromSecurityContext();
        verify(currencyService, times(1)).retrieveMinLimitForRoleAndCurrency(anyObject(), anyObject(), anyInt());
        verify(currencyService, times(1)).retrieveMaxDailyRequestForRoleAndCurrency(anyObject(), anyObject(), anyInt());
        verify(currencyService, times(1)).getCurrencyScaleByCurrencyId(anyInt());
        verify(merchantService, times(1)).getAllUnblockedForOperationTypeByCurrencies(anyList(), anyObject());
        verify(currencyService, times(1)).getWarningForCurrency(anyInt(), anyObject());
    }

    @Test
    public void outputCredits_exception() throws Exception {
        when(currencyService.findByName(anyString())).thenThrow(Exception.class);
        when(userService.findByEmail(anyString())).thenThrow(Exception.class);
        when(walletService.findByUserAndCurrency(anyObject(), anyObject())).thenThrow(Exception.class);
        when(userService.getUserRoleFromSecurityContext()).thenThrow(Exception.class);
        when(currencyService.retrieveMinLimitForRoleAndCurrency(anyObject(), anyObject(), anyInt())).thenThrow(Exception.class);
        when(currencyService.retrieveMaxDailyRequestForRoleAndCurrency(anyObject(), anyObject(), anyInt())).thenThrow(Exception.class);
        when(currencyService.getCurrencyScaleByCurrencyId(anyInt())).thenThrow(Exception.class);
        when(merchantService.getAllUnblockedForOperationTypeByCurrencies(anyList(), anyObject())).thenThrow(Exception.class);
        when(merchantServiceContext.getMerchantService(anyInt())).thenThrow(Exception.class);
        when(currencyService.getWarningForCurrency(anyInt(), anyObject())).thenThrow(Exception.class);

        try {
            mockMvc.perform(get(BASE_URL + "/merchants/output")
                    .param("currency", "BTC")
                    .contentType(MediaType.APPLICATION_JSON_UTF8))
                    .andExpect(status().isBadRequest());
            Assert.fail();
        } catch (Exception e) {
            assertTrue(((NestedServletException) e).getRootCause() instanceof NgResponseException);
            NgResponseException responseException = (NgResponseException) ((NestedServletException) e).getRootCause();
            assertEquals("FAILED_OUTPUT_CREDITS", responseException.getTitle());

            String expected = "Failed output credits BTC";
            assertEquals(expected, e.getCause().getMessage());
        }
    }

    @Test
    public void outputCredits_isOk_additionalTagForWithdrawAddressIsUsed_equals_false() throws Exception {
        IWithdrawable withdrawable = Mockito.mock(IWithdrawable.class);

        when(currencyService.findByName(anyString())).thenReturn(getMockCurrency("TEST_CURRENCY"));
        when(userService.findByEmail(anyString())).thenReturn(getMockUser());
        when(walletService.findByUserAndCurrency(anyObject(), anyObject())).thenReturn(getMockWallet());
        when(userService.getUserRoleFromSecurityContext()).thenReturn(UserRole.USER);
        when(currencyService.retrieveMinLimitForRoleAndCurrency(anyObject(), anyObject(), anyInt())).thenReturn(BigDecimal.TEN);
        when(currencyService.retrieveMaxDailyRequestForRoleAndCurrency(anyObject(), anyObject(), anyInt())).thenReturn(BigDecimal.TEN);
        when(withdrawService.getLeftOutputRequestsSum(anyInt(), anyString())).thenReturn(BigDecimal.ONE);
        when(currencyService.getCurrencyScaleByCurrencyId(anyInt())).thenReturn(getMockMerchantCurrencyScaleDto());
        when(merchantService.getAllUnblockedForOperationTypeByCurrencies(anyList(), anyObject())).thenReturn((Collections.singletonList(getMockMerchantCurrency())));
        when(merchantServiceContext.getMerchantService(anyInt())).thenReturn(withdrawable);
        when(withdrawable.additionalTagForWithdrawAddressIsUsed()).thenReturn(Boolean.FALSE);
        when(currencyService.getWarningForCurrency(anyInt(), anyObject())).thenReturn(getWarningCodeList());

        mockMvc.perform(get(BASE_URL + "/merchants/output")
                .param("currency", "BTC")
                .contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.operationType", is("OUTPUT")))
                .andExpect(jsonPath("$.minWithdrawSum", is(10.0)))
                .andExpect(jsonPath("$.maxDailyRequestSum", is(10.0)))
                .andExpect(jsonPath("$.warningCodeList.[0]", is("WARNING_CODE_ONE")))
                .andExpect(jsonPath("$.warningCodeList.[1]", is("WARNING_CODE_TWO")));

        verify(currencyService, times(1)).findByName(anyString());
        verify(userService, times(1)).findByEmail(anyString());
        verify(walletService, times(1)).findByUserAndCurrency(anyObject(), anyObject());
        verify(userService, times(1)).getUserRoleFromSecurityContext();
        verify(currencyService, times(1)).retrieveMinLimitForRoleAndCurrency(anyObject(), anyObject(), anyInt());
        verify(currencyService, times(1)).retrieveMaxDailyRequestForRoleAndCurrency(anyObject(), anyObject(), anyInt());
        verify(currencyService, times(1)).getCurrencyScaleByCurrencyId(anyInt());
        verify(merchantService, times(1)).getAllUnblockedForOperationTypeByCurrencies(anyList(), anyObject());
        verify(currencyService, times(1)).getWarningForCurrency(anyInt(), anyObject());
    }


    @Test
    public void outputCredits_isOk_additionalTagForWithdrawAddressIsUsed_equals_true() throws Exception {
        IWithdrawable withdrawable = Mockito.mock(IWithdrawable.class);

        when(currencyService.findByName(anyString())).thenReturn(getMockCurrency("TEST_CURRENCY"));
        when(userService.findByEmail(anyString())).thenReturn(getMockUser());
        when(walletService.findByUserAndCurrency(anyObject(), anyObject())).thenReturn(getMockWallet());
        when(userService.getUserRoleFromSecurityContext()).thenReturn(UserRole.USER);
        when(currencyService.retrieveMinLimitForRoleAndCurrency(anyObject(), anyObject(), anyInt())).thenReturn(BigDecimal.TEN);
        when(currencyService.retrieveMaxDailyRequestForRoleAndCurrency(anyObject(), anyObject(), anyInt())).thenReturn(BigDecimal.TEN);
        when(withdrawService.getLeftOutputRequestsSum(anyInt(), anyString())).thenReturn(BigDecimal.ONE);
        when(currencyService.getCurrencyScaleByCurrencyId(anyInt())).thenReturn(getMockMerchantCurrencyScaleDto());
        when(merchantService.getAllUnblockedForOperationTypeByCurrencies(anyList(), anyObject())).thenReturn((Collections.singletonList(getMockMerchantCurrency())));
        when(merchantServiceContext.getMerchantService(anyInt())).thenReturn(withdrawable);
        when(withdrawable.additionalTagForWithdrawAddressIsUsed()).thenReturn(Boolean.TRUE);
        when(currencyService.getWarningForCurrency(anyInt(), anyObject())).thenReturn(getWarningCodeList());

        mockMvc.perform(get(BASE_URL + "/merchants/output")
                .param("currency", "BTC")
                .contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.operationType", is("OUTPUT")))
                .andExpect(jsonPath("$.minWithdrawSum", is(10.0)))
                .andExpect(jsonPath("$.maxDailyRequestSum", is(10.0)))
                .andExpect(jsonPath("$.warningCodeList.[0]", is("WARNING_CODE_ONE")))
                .andExpect(jsonPath("$.warningCodeList.[1]", is("WARNING_CODE_TWO")));

        verify(currencyService, times(1)).findByName(anyString());
        verify(userService, times(1)).findByEmail(anyString());
        verify(walletService, times(1)).findByUserAndCurrency(anyObject(), anyObject());
        verify(userService, times(1)).getUserRoleFromSecurityContext();
        verify(currencyService, times(1)).retrieveMinLimitForRoleAndCurrency(anyObject(), anyObject(), anyInt());
        verify(currencyService, times(1)).retrieveMaxDailyRequestForRoleAndCurrency(anyObject(), anyObject(), anyInt());
        verify(currencyService, times(1)).getCurrencyScaleByCurrencyId(anyInt());
        verify(merchantService, times(1)).getAllUnblockedForOperationTypeByCurrencies(anyList(), anyObject());
        verify(currencyService, times(1)).getWarningForCurrency(anyInt(), anyObject());
    }

    @Test
    public void sendUserPinCode_created() throws Exception {
        when(userService.findByEmail(anyString())).thenReturn(getMockUser());
        when(g2faService.isGoogleAuthenticatorEnable(anyInt())).thenReturn(Boolean.FALSE);
        when(secureService.sendWithdrawPinCode(anyObject(), anyString(), anyString()))
                .thenReturn(getMockNotificationResultDto());

        mockMvc.perform(post(BASE_URL + "/request/pin")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(objectMapper.writeValueAsString(new PinOrderInfoDto("TEST_NAME", BigDecimal.ZERO))))
                .andExpect(status().isCreated());

        verify(userService, times(1)).findByEmail(anyString());
        verify(g2faService, times(1)).isGoogleAuthenticatorEnable(anyInt());
        verify(secureService, times(1)).sendWithdrawPinCode(anyObject(), anyString(), anyString());
    }

    @Test
    public void sendUserPinCode_isOk() throws Exception {
        when(userService.findByEmail(anyString())).thenReturn(getMockUser());
        when(g2faService.isGoogleAuthenticatorEnable(anyInt())).thenReturn(Boolean.TRUE);

        mockMvc.perform(post(BASE_URL + "/request/pin")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(objectMapper.writeValueAsString(new PinOrderInfoDto("TEST_NAME", BigDecimal.ZERO))))
                .andExpect(status().isOk());

        verify(userService, times(1)).findByEmail(anyString());
        verify(g2faService, times(1)).isGoogleAuthenticatorEnable(anyInt());
    }

    @Test
    public void sendUserPinCode_badRequest() throws Exception {
        when(userService.findByEmail(anyString())).thenThrow(Exception.class);

        try {
            mockMvc.perform(post(BASE_URL + "/request/pin")
                    .contentType(MediaType.APPLICATION_JSON_UTF8)
                    .content(objectMapper.writeValueAsString(new PinOrderInfoDto("TEST_NAME", BigDecimal.ZERO))))
                    .andExpect(status().isBadRequest());
            Assert.fail();
        } catch (Exception e) {
            assertTrue(((NestedServletException) e).getRootCause() instanceof NgResponseException);
            NgResponseException responseException = (NgResponseException) ((NestedServletException) e).getRootCause();
            assertEquals("FAILED_TO_SEND_PIN_CODE_ON_USER_EMAIL", responseException.getTitle());

            String expected = "Failed to send pin code on user email";
            assertEquals(expected, e.getCause().getMessage());
        }

        verify(userService, times(1)).findByEmail(anyString());
    }

    @Test
    public void getCommissions_isOk_memo_empty() throws Exception {
        int userId = 100;

        when(userService.getIdByEmail(anyString())).thenReturn(userId);

        mockMvc.perform(get(BASE_URL + "/commission")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .param("amount", "100")
                .param("currency", "100")
                .param("merchant", "100"))
                .andExpect(status().isOk());

        verify(userService, times(1)).getIdByEmail(anyString());
    }

    @Test
    public void getCommissions_isOk_memo_not_empty() throws Exception {
        int userId = 100;

        when(userService.getIdByEmail(anyString())).thenReturn(userId);

        mockMvc.perform(get(BASE_URL + "/commission")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .param("amount", "100")
                .param("currency", "100")
                .param("merchant", "100")
                .param("memo", "memo"))
                .andExpect(status().isOk());

        verify(userService, times(1)).getIdByEmail(anyString());
    }

    private WithdrawRequestParamsDto getMockWithdrawRequestParamsDto(String securityCode) {
        WithdrawRequestParamsDto dto = new WithdrawRequestParamsDto();
        dto.setCurrency(100);
        dto.setMerchant(200);
        dto.setSum(BigDecimal.TEN);
        dto.setDestination("TEST_DESTINATION");
        dto.setDestinationTag("TEST_DESTINATION_TAG");
        dto.setMerchantImage(300);
        dto.setOperationType(OperationType.BUY);
        dto.setRecipientBankName("TEST_RECIPIENT_BANK_NAME");
        dto.setUserFullName("TEST_USER_FULL_NAME");
        dto.setRemark("TEST_REMARK");
        dto.setWalletNumber("TEST_WALLET_NUMBER");
        dto.setSecurityCode(securityCode);

        return dto;
    }

    private List<String> getWarningCodeList() {
        List<String> warningCodeList = new ArrayList<>();
        warningCodeList.add("WARNING_CODE_ONE");
        warningCodeList.add("WARNING_CODE_TWO");

        return warningCodeList;
    }

    private Currency getCurrency() {
        Currency currency = new Currency();
        currency.setName("BTC");
        currency.setHidden(false);
        currency.setId(3);
        return currency;
    }
}