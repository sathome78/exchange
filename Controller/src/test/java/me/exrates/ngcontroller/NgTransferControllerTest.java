package me.exrates.ngcontroller;

import me.exrates.model.enums.OperationType;
import me.exrates.model.enums.invoice.TransferStatusEnum;
import me.exrates.security.service.SecureService;
import me.exrates.service.CurrencyService;
import me.exrates.service.InputOutputService;
import me.exrates.service.MerchantService;
import me.exrates.service.TransferService;
import me.exrates.service.UserService;
import me.exrates.service.notifications.G2faService;
import me.exrates.service.userOperation.UserOperationService;
import me.exrates.service.util.RateLimitService;
import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.context.MessageSource;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.servlet.LocaleResolver;

import java.util.Collections;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.mockito.Matchers.anyBoolean;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class NgTransferControllerTest extends AngularApiCommonTest {
    private final static String BASE_URL = "/api/private/v2/balances/transfer";

    @Mock
    private RateLimitService rateLimitService;
    @Mock
    private TransferService transferService;
    @Mock
    private UserService userService;
    @Mock
    private MerchantService merchantService;
    @Mock
    private LocaleResolver localeResolver;
    @Mock
    private UserOperationService userOperationService;
    @Mock
    private InputOutputService inputOutputService;
    @Mock
    private MessageSource messageSource;
    @Mock
    private G2faService g2faService;
    @Mock
    private SecureService secureService;
    @Mock
    private CurrencyService currencyService;
    @Mock
    private TransferStatusEnum transferStatusEnum;

    @InjectMocks
    private NgTransferController ngTransferController;

    private MockMvc mockMvc;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        mockMvc = MockMvcBuilders
                .standaloneSetup(ngTransferController)
                .build();

        SecurityContextHolder.getContext()
                .setAuthentication(new AnonymousAuthenticationToken("GUEST", "testemail@gmail.com",
                        AuthorityUtils.createAuthorityList("ADMIN")));
    }

    @Test
    public void acceptTransfer_isBadRequest() throws Exception {
        Map<String, String> params = new HashMap<>();
        params.put("CODE", "VOUCHER_CODE");

        when(rateLimitService.checkLimitsExceed(anyString())).thenReturn(Boolean.FALSE);

        mockMvc.perform(post(BASE_URL + "/accept")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(objectMapper.writeValueAsString(params)))
                .andExpect(status().isBadRequest());

        verify(rateLimitService, times(1)).checkLimitsExceed(anyString());
    }

    @Test
    public void acceptTransfer_notFound() throws Exception {
        Map<String, String> params = new HashMap<>();
        params.put("CODE", "VOUCHER_CODE");

        when(rateLimitService.checkLimitsExceed(anyString())).thenReturn(Boolean.TRUE);
        when(transferService.getByHashAndStatus(anyString(), anyInt(), anyBoolean()))
                .thenReturn(java.util.Optional.ofNullable(getMockTransferRequestFlatDto()));
        when(transferService.checkRequest(anyObject(), anyString())).thenReturn(Boolean.FALSE);

        mockMvc.perform(post(BASE_URL + "/accept")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(objectMapper.writeValueAsString(params)))
                .andExpect(status().isNotFound());

        verify(rateLimitService, times(1)).checkLimitsExceed(anyString());
        verify(transferService, times(1)).getByHashAndStatus(anyString(), anyInt(), anyBoolean());
        verify(transferService, times(1)).checkRequest(anyObject(), anyString());
    }

    @Test
    public void acceptTransfer_isOk() throws Exception {
        Map<String, String> params = new HashMap<>();
        params.put("CODE", "VOUCHER_CODE");

        when(rateLimitService.checkLimitsExceed(anyString())).thenReturn(Boolean.TRUE);
        when(transferService.getByHashAndStatus(anyString(), anyInt(), anyBoolean()))
                .thenReturn(java.util.Optional.ofNullable(getMockTransferRequestFlatDto()));
        when(transferService.checkRequest(anyObject(), anyString())).thenReturn(Boolean.TRUE);
        when(transferService.performTransfer(anyObject(), anyObject(), anyObject())).thenReturn(getMockTransferDto());

        mockMvc.perform(post(BASE_URL + "/accept")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(objectMapper.writeValueAsString(params)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userToNickName").value("TEST_USER_TO_NICK_NAME"))
                .andExpect(jsonPath("$.currencyId").value(100))
                .andExpect(jsonPath("$.userFromId").value(200))
                .andExpect(jsonPath("$.userToId").value(300))
                .andExpect(jsonPath("$.notyAmount").value("TEST_NOTY_AMOUNT"))
                .andExpect(jsonPath("$.initialAmount").value(10))
                .andExpect(jsonPath("$.comissionAmount").value(0));

        verify(rateLimitService, times(1)).checkLimitsExceed(anyString());
        verify(transferService, times(1)).getByHashAndStatus(anyString(), anyInt(), anyBoolean());
        verify(transferService, times(1)).checkRequest(anyObject(), anyString());
        verify(transferService, times(1)).performTransfer(anyObject(), anyObject(), anyObject());
    }

    @Test
    public void getCommissionsForInnerVoucher_isOk() throws Exception {
        Integer userId = 100;
        Map<String, String> mockMap = new HashMap<>();
        mockMap.put("TEST_KEY", "TEST_VALUE");

        when(merchantService.findMerchantForTransferByCurrencyId(anyInt(), anyObject())).thenReturn(getMockMerchantCurrency());
        when(userService.getIdByEmail(anyString())).thenReturn(userId);
        when(transferService.correctAmountAndCalculateCommissionPreliminarily(anyInt(), anyObject(), anyObject(), anyInt(), anyObject()))
                .thenReturn(mockMap);

        mockMvc.perform(get(BASE_URL + "/voucher/commission")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .param("amount", "0.00165000")
                .param("currency", "20")
                .param("type", "TRANSFER"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", Matchers.hasKey("TEST_KEY")))
                .andExpect(jsonPath("$", Matchers.hasValue("TEST_VALUE")));

        verify(merchantService, times(1)).findMerchantForTransferByCurrencyId(anyInt(), anyObject());
        verify(userService, times(1)).getIdByEmail(anyString());
        verify(transferService, times(1))
                .correctAmountAndCalculateCommissionPreliminarily(anyInt(), anyObject(), anyObject(), anyInt(), anyObject());
    }

    @Test(expected = Exception.class)
    public void createTransferRequest_IllegalOperationTypeException() throws Exception {
        Integer userId = 100;

        when(userService.getIdByEmail(anyString())).thenReturn(userId);
        when(localeResolver.resolveLocale(anyObject())).thenReturn(Locale.ENGLISH);

        mockMvc.perform(post(BASE_URL + "/voucher/request/create")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(objectMapper.writeValueAsString(getMockTransferRequestParamsDto(OperationType.INPUT, "TEST_RECIPIENT"))));

        verify(userService, times(1)).getIdByEmail(anyString());
        verify(localeResolver, times(1)).resolveLocale(anyObject());
    }

    @Test
    public void createTransferRequest_UserOperationAccessException() throws Exception {
        Integer userId = 100;

        when(userService.getIdByEmail(anyString())).thenReturn(userId);
        when(localeResolver.resolveLocale(anyObject())).thenReturn(Locale.ENGLISH);
        when(userOperationService.getStatusAuthorityForUserByOperation(anyInt(), anyObject())).thenReturn(Boolean.FALSE);

        mockMvc.perform(post(BASE_URL + "/voucher/request/create")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(objectMapper.writeValueAsString(getMockTransferRequestParamsDto(OperationType.USER_TRANSFER, "TEST_RECIPIENT"))))
                .andExpect(status().isBadRequest());

        verify(userService, times(1)).getIdByEmail(anyString());
        verify(localeResolver, times(1)).resolveLocale(anyObject());
        verify(userOperationService, times(1)).getStatusAuthorityForUserByOperation(anyInt(), anyObject());
    }

    @Test
    public void createTransferRequest_IllegalArgumentException() throws Exception {
        Integer userId = 100;

        when(userService.getIdByEmail(anyString())).thenReturn(userId);
        when(localeResolver.resolveLocale(anyObject())).thenReturn(Locale.ENGLISH);
        when(userOperationService.getStatusAuthorityForUserByOperation(anyInt(), anyObject())).thenReturn(Boolean.TRUE);

        mockMvc.perform(post(BASE_URL + "/voucher/request/create")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(objectMapper.writeValueAsString(getMockTransferRequestParamsDto(OperationType.USER_TRANSFER, "КИРИЛЛИЦА"))))
                .andExpect(status().isBadRequest());

        verify(userService, times(1)).getIdByEmail(anyString());
        verify(localeResolver, times(1)).resolveLocale(anyObject());
        verify(userOperationService, times(1)).getStatusAuthorityForUserByOperation(anyInt(), anyObject());
    }

    @Test(expected = Exception.class)
    public void createTransferRequest_InvalidAmountException() throws Exception {
        Integer userId = 100;

        when(userService.getIdByEmail(anyString())).thenReturn(userId);
        when(localeResolver.resolveLocale(anyObject())).thenReturn(Locale.ENGLISH);
        when(userOperationService.getStatusAuthorityForUserByOperation(anyInt(), anyObject())).thenReturn(Boolean.TRUE);
        when(merchantService.findMerchantForTransferByCurrencyId(anyInt(), anyObject())).thenReturn(getMockMerchantCurrency());
        when(inputOutputService.prepareCreditsOperation(anyObject(), anyString(), anyObject())).thenReturn(null);

        mockMvc.perform(post(BASE_URL + "/voucher/request/create")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(objectMapper.writeValueAsString(getMockTransferRequestParamsDto(OperationType.USER_TRANSFER, "TEST_RECIPIENT"))))
                .andExpect(status().isBadRequest());

        verify(userService, times(1)).getIdByEmail(anyString());
        verify(localeResolver, times(1)).resolveLocale(anyObject());
        verify(userOperationService, times(1)).getStatusAuthorityForUserByOperation(anyInt(), anyObject());
        verify(merchantService, times(1)).findMerchantForTransferByCurrencyId(anyInt(), anyObject());
        verify(inputOutputService, times(1)).prepareCreditsOperation(anyObject(), anyString(), anyObject());
    }

    @Test
    public void createTransferRequest_isOK() throws Exception {
        Integer userId = 100;
        Map<String, Object> response = new HashMap<>();
        response.put("TEST_KEY", "TEST_OBJECT");

        when(userService.getIdByEmail(anyString())).thenReturn(userId);
        when(localeResolver.resolveLocale(anyObject())).thenReturn(Locale.ENGLISH);
        when(userOperationService.getStatusAuthorityForUserByOperation(anyInt(), anyObject())).thenReturn(Boolean.TRUE);
        when(merchantService.findMerchantForTransferByCurrencyId(anyInt(), anyObject())).thenReturn(getMockMerchantCurrency());
        when(inputOutputService.prepareCreditsOperation(anyObject(), anyString(), anyObject()))
                .thenReturn(getMockCreditsOperation());
        when(userService.findByEmail(anyString())).thenReturn(getMockUser());
        when(g2faService.isGoogleAuthenticatorEnable(anyInt())).thenReturn(Boolean.FALSE);
        when(userService.checkPin(anyString(), anyString(), anyObject())).thenReturn(Boolean.TRUE);
        when(transferService.createTransferRequest(anyObject())).thenReturn(response);

        mockMvc.perform(post(BASE_URL + "/voucher/request/create")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(objectMapper.writeValueAsString(getMockTransferRequestParamsDto(OperationType.USER_TRANSFER, "TEST_RECIPIENT"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", Matchers.hasKey("TEST_KEY")))
                .andExpect(jsonPath("$", Matchers.hasValue("TEST_OBJECT")));

        verify(userService, times(1)).getIdByEmail(anyString());
        verify(localeResolver, times(1)).resolveLocale(anyObject());
        verify(userOperationService, times(1)).getStatusAuthorityForUserByOperation(anyInt(), anyObject());
        verify(merchantService, times(1)).findMerchantForTransferByCurrencyId(anyInt(), anyObject());
        verify(inputOutputService, times(1)).prepareCreditsOperation(anyObject(), anyString(), anyObject());
        verify(userService, times(1)).findByEmail(anyString());
        verify(g2faService, times(1)).isGoogleAuthenticatorEnable(anyInt());
        verify(userService, times(1)).checkPin(anyString(), anyString(), anyObject());
    }

    @Test
    public void createTransferRequest_checkGoogle2faVerifyCode_IncorrectPinException() throws Exception {
        Integer userId = 100;
        Map<String, Object> response = new HashMap<>();
        response.put("TEST_KEY", "TEST_OBJECT");

        when(userService.getIdByEmail(anyString())).thenReturn(userId);
        when(localeResolver.resolveLocale(anyObject())).thenReturn(Locale.ENGLISH);
        when(userOperationService.getStatusAuthorityForUserByOperation(anyInt(), anyObject())).thenReturn(Boolean.TRUE);
        when(merchantService.findMerchantForTransferByCurrencyId(anyInt(), anyObject())).thenReturn(getMockMerchantCurrency());
        when(inputOutputService.prepareCreditsOperation(anyObject(), anyString(), anyObject()))
                .thenReturn(getMockCreditsOperation());
        when(userService.findByEmail(anyString())).thenReturn(getMockUser());
        when(g2faService.isGoogleAuthenticatorEnable(anyInt())).thenReturn(Boolean.TRUE);
        when(g2faService.checkGoogle2faVerifyCode(anyString(), anyObject())).thenReturn(Boolean.FALSE);

        mockMvc.perform(post(BASE_URL + "/voucher/request/create")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(objectMapper.writeValueAsString(getMockTransferRequestParamsDto(OperationType.USER_TRANSFER, "TEST_RECIPIENT"))))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.detail", is("Incorrect Google 2FA oauth code: TEST_PIN")));

        verify(userService, times(1)).getIdByEmail(anyString());
        verify(localeResolver, times(1)).resolveLocale(anyObject());
        verify(userOperationService, times(1)).getStatusAuthorityForUserByOperation(anyInt(), anyObject());
        verify(merchantService, times(1)).findMerchantForTransferByCurrencyId(anyInt(), anyObject());
        verify(inputOutputService, times(1)).prepareCreditsOperation(anyObject(), anyString(), anyObject());
        verify(userService, times(1)).findByEmail(anyString());
        verify(g2faService, times(1)).isGoogleAuthenticatorEnable(anyInt());
        verify(g2faService, times(1)).checkGoogle2faVerifyCode(anyString(), anyObject());
    }

    @Test
    public void createTransferRequest_checkGoogle2faVerifyCode_checkPin() throws Exception {
        Integer userId = 100;
        Map<String, Object> response = new HashMap<>();
        response.put("TEST_KEY", "TEST_OBJECT");

        when(userService.getIdByEmail(anyString())).thenReturn(userId);
        when(localeResolver.resolveLocale(anyObject())).thenReturn(Locale.ENGLISH);
        when(userOperationService.getStatusAuthorityForUserByOperation(anyInt(), anyObject())).thenReturn(Boolean.TRUE);
        when(merchantService.findMerchantForTransferByCurrencyId(anyInt(), anyObject())).thenReturn(getMockMerchantCurrency());
        when(inputOutputService.prepareCreditsOperation(anyObject(), anyString(), anyObject()))
                .thenReturn(getMockCreditsOperation());
        when(userService.findByEmail(anyString())).thenReturn(getMockUser());
        when(g2faService.isGoogleAuthenticatorEnable(anyInt())).thenReturn(Boolean.FALSE);
        when(userService.checkPin(anyString(), anyString(), anyObject())).thenReturn(Boolean.FALSE);
        when(secureService.sendWithdrawPincode(anyObject())).thenReturn(getMockNotificationResultDto());

        mockMvc.perform(post(BASE_URL + "/voucher/request/create")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(objectMapper.writeValueAsString(getMockTransferRequestParamsDto(OperationType.USER_TRANSFER, "TEST_RECIPIENT"))))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.detail", is("Incorrect pin: TEST_PIN")));

        verify(userService, times(1)).getIdByEmail(anyString());
        verify(localeResolver, times(1)).resolveLocale(anyObject());
        verify(userOperationService, times(1)).getStatusAuthorityForUserByOperation(anyInt(), anyObject());
        verify(merchantService, times(1)).findMerchantForTransferByCurrencyId(anyInt(), anyObject());
        verify(inputOutputService, times(1)).prepareCreditsOperation(anyObject(), anyString(), anyObject());
        verify(userService, times(1)).findByEmail(anyString());
        verify(g2faService, times(1)).isGoogleAuthenticatorEnable(anyInt());
        verify(userService, times(1)).checkPin(anyString(), anyString(), anyObject());
        verify(secureService, times(1)).sendWithdrawPincode(anyObject());
    }

    @Test
    public void checkEmailForTransfer_isOk_email_equalsIgnoreCase_principalEmail() throws Exception {
        when(messageSource.getMessage(anyString(), anyObject(), anyObject())).thenReturn("TEST ERROR MSG");

        mockMvc.perform(get(BASE_URL + "/check_email")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .param("email", "testemail@gmail.com"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data", is(Boolean.FALSE)))
                .andExpect(jsonPath("$.error.code", is(0)))
                .andExpect(jsonPath("$.error.message", is("TEST ERROR MSG")));

        verify(messageSource, times(1)).getMessage(anyString(), anyObject(), anyObject());
    }

    @Test
    public void checkEmailForTransfer_userExistByEmail_true() throws Exception {
        String wrongEmail = "wrongtestemail@gmail.com";

        when(messageSource.getMessage(anyString(), anyObject(), anyObject())).thenReturn("TEST ERROR MSG");
        when(userService.userExistByEmail(anyString())).thenReturn(Boolean.FALSE);

        mockMvc.perform(get(BASE_URL + "/check_email")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .param("email", wrongEmail))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data", is(Boolean.FALSE)))
                .andExpect(jsonPath("$.error.code", is(0)))
                .andExpect(jsonPath("$.error.message", is("TEST ERROR MSG")));

        verify(messageSource, times(1)).getMessage(anyString(), anyObject(), anyObject());
        verify(userService, times(1)).userExistByEmail(anyString());
    }

    @Test
    public void checkEmailForTransfer_userExistByEmail_false() throws Exception {
        String wrongEmail = "wrongtestemail@gmail.com";

        when(userService.userExistByEmail(anyString())).thenReturn(Boolean.TRUE);

        mockMvc.perform(get(BASE_URL + "/check_email")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .param("email", wrongEmail))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data", is(Boolean.TRUE)))
                .andExpect(jsonPath("$.error", is(nullValue())));

        verify(userService, times(1)).userExistByEmail(anyString());
    }

    @Test
    public void getMinimalTransferSum_isOk_erchant_min_sum() throws Exception {
        when(merchantService.findMerchantForTransferByCurrencyId(anyInt(), anyObject()))
                .thenReturn(getMockMerchantCurrency());

        mockMvc.perform(get(BASE_URL + "/get_minimal_sum")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .param("currency_id", "100")
                .param("type", "TRANSFER"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data", is(50)))
                .andExpect(jsonPath("$.error", is(nullValue())));
    }

    @Test
    public void getMinimalTransferSum_isOk_min_sum() throws Exception {
        int minSum = 0;

        when(merchantService.findMerchantForTransferByCurrencyId(anyInt(), anyObject())).thenReturn(null);

        mockMvc.perform(get(BASE_URL + "/get_minimal_sum")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .param("currency_id", "100")
                .param("type", "TRANSFER"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data", is(minSum)))
                .andExpect(jsonPath("$.error", is(nullValue())));
    }

    @Test
    public void getAllCurrenciesForTransfer() throws Exception {
        when(currencyService.getCurrencies(anyObject()))
                .thenReturn(Collections.singletonList(getMockCurrency("TEST_CURRENCY_NAME")));

        mockMvc.perform(get(BASE_URL + "/currencies")
                .contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.[0].id", is(100)))
                .andExpect(jsonPath("$.data.[0].name", is("TEST_CURRENCY_NAME")))
                .andExpect(jsonPath("$.data.[0].description", is("TEST_DESCRIPTION")))
                .andExpect(jsonPath("$.data.[0].hidden", is(Boolean.TRUE)))
                .andExpect(jsonPath("$.error", is(nullValue())));

        verify(currencyService, times(1)).getCurrencies(anyObject());
    }
}