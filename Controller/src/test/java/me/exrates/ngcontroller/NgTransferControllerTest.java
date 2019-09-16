package me.exrates.ngcontroller;

import me.exrates.model.Commission;
import me.exrates.model.Currency;
import me.exrates.model.dto.TransferDto;
import me.exrates.model.dto.TransferRequestFlatDto;
import me.exrates.model.dto.TransferRequestParamsDto;
import me.exrates.model.enums.OperationType;
import me.exrates.model.enums.invoice.InvoiceOperationPermission;
import me.exrates.model.enums.invoice.TransferStatusEnum;
import me.exrates.model.ngExceptions.NgResponseException;
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
import org.junit.Assert;
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
import org.springframework.web.util.NestedServletException;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
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
    public void acceptTransfer_isBadRequest() {
        Map<String, String> params = new HashMap<>();
        params.put("CODE", "VOUCHER_CODE");

        when(rateLimitService.checkLimitsExceed(anyString())).thenReturn(Boolean.FALSE);

        try {
            mockMvc.perform(post(BASE_URL + "/accept")
                    .contentType(MediaType.APPLICATION_JSON_UTF8)
                    .content(objectMapper.writeValueAsString(params)))
                    .andExpect(status().isBadRequest());
            Assert.fail();
        } catch (Exception e) {
            assertTrue(((NestedServletException) e).getRootCause() instanceof NgResponseException);
            NgResponseException responseException = (NgResponseException) ((NestedServletException) e).getRootCause();
            assertEquals("FAILED_ACCEPT_TRANSFER", responseException.getTitle());

            String expected = "message.limits_exceed";
            assertEquals(expected, e.getCause().getMessage());
        }

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

        TransferDto dto = TransferDto.builder().build();
        dto.setWalletUserFrom(getMockWallet());
        dto.setWalletUserTo(getMockWallet());
        dto.setUserToNickName("TEST_USER_TO_NICK_NAME");
        dto.setCurrencyId(100);
        dto.setUserFromId(200);
        dto.setUserToId(300);
        dto.setCommission(Commission.zeroComission());
        dto.setNotyAmount("TEST_NOTY_AMOUNT");
        dto.setInitialAmount(BigDecimal.TEN);
        dto.setComissionAmount(BigDecimal.ZERO);

        when(rateLimitService.checkLimitsExceed(anyString())).thenReturn(Boolean.TRUE);
        when(transferService.getByHashAndStatus(anyString(), anyInt(), anyBoolean()))
                .thenReturn(java.util.Optional.ofNullable(getMockTransferRequestFlatDto()));
        when(transferService.checkRequest(anyObject(), anyString())).thenReturn(Boolean.TRUE);
        when(transferService.performTransfer(anyObject(), anyObject(), anyObject())).thenReturn(dto);

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

    @Test
    public void createTransferRequest_IllegalOperationTypeException() {
        Integer userId = 100;

        when(userService.getIdByEmail(anyString())).thenReturn(userId);
        when(localeResolver.resolveLocale(anyObject())).thenReturn(Locale.ENGLISH);

        try {
            mockMvc.perform(post(BASE_URL + "/voucher/request/create")
                    .contentType(MediaType.APPLICATION_JSON_UTF8)
                    .content(objectMapper.writeValueAsString(getMockTransferRequestParamsDto(OperationType.INPUT, "TEST_RECIPIENT"))));
            Assert.fail();
        } catch (Exception e) {
            String expected = "INPUT";
            assertEquals(expected, e.getCause().getMessage());
        }

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

    @Test
    public void createTransferRequest_InvalidAmountException() {
        Integer userId = 100;

        when(userService.getIdByEmail(anyString())).thenReturn(userId);
        when(localeResolver.resolveLocale(anyObject())).thenReturn(Locale.ENGLISH);
        when(userOperationService.getStatusAuthorityForUserByOperation(anyInt(), anyObject())).thenReturn(Boolean.TRUE);
        when(merchantService.findMerchantForTransferByCurrencyId(anyInt(), anyObject())).thenReturn(getMockMerchantCurrency());
        when(inputOutputService.prepareCreditsOperation(anyObject(), anyString(), anyObject())).thenReturn(null);

        try {
            mockMvc.perform(post(BASE_URL + "/voucher/request/create")
                    .contentType(MediaType.APPLICATION_JSON_UTF8)
                    .content(objectMapper.writeValueAsString(getMockTransferRequestParamsDto(OperationType.USER_TRANSFER, "TEST_RECIPIENT"))))
                    .andExpect(status().isBadRequest());
            Assert.fail();
        } catch (Exception e) {
            String expected = null;
            assertEquals(expected, e.getCause().getMessage());
        }

        verify(userService, times(1)).getIdByEmail(anyString());
        verify(localeResolver, times(1)).resolveLocale(anyObject());
        verify(userOperationService, times(1)).getStatusAuthorityForUserByOperation(anyInt(), anyObject());
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
                /*.andExpect(jsonPath("$.detail", is("Incorrect Google 2FA oauth code: TEST_PIN")))*/;

        verify(userService, times(1)).getIdByEmail(anyString());
        verify(localeResolver, times(1)).resolveLocale(anyObject());
        verify(userOperationService, times(1)).getStatusAuthorityForUserByOperation(anyInt(), anyObject());
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
        when(secureService.sendTransferPinCode(anyObject(), anyString(), anyString())).thenReturn(getMockNotificationResultDto());
        when(currencyService.getById(anyInt())).thenReturn(getCurrency());

        mockMvc.perform(post(BASE_URL + "/voucher/request/create")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(objectMapper.writeValueAsString(getMockTransferRequestParamsDto(OperationType.USER_TRANSFER, "TEST_RECIPIENT"))))
                .andExpect(status().isBadRequest())
                /*.andExpect(jsonPath("$.detail", is("Incorrect pin: TEST_PIN")))*/;

        verify(userService, times(1)).getIdByEmail(anyString());
        verify(localeResolver, times(1)).resolveLocale(anyObject());
        verify(userOperationService, times(1)).getStatusAuthorityForUserByOperation(anyInt(), anyObject());
        verify(userService, times(1)).findByEmail(anyString());
        verify(g2faService, times(1)).isGoogleAuthenticatorEnable(anyInt());
        verify(userService, times(1)).checkPin(anyString(), anyString(), anyObject());
        verify(secureService, times(1)).sendTransferPinCode(anyObject(), anyObject(), anyObject());
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

    private TransferRequestFlatDto getMockTransferRequestFlatDto() {
        TransferRequestFlatDto dto = new TransferRequestFlatDto();
        dto.setId(100);
        dto.setAmount(BigDecimal.valueOf(10));
        dto.setDateCreation(LocalDateTime.of(2019, 3, 20, 14, 53, 1));
        dto.setStatus(TransferStatusEnum.POSTED);
        dto.setStatusModificationDate(LocalDateTime.of(2019, 3, 20, 14, 59, 1));
        dto.setMerchantId(200);
        dto.setCurrencyId(300);
        dto.setUserId(400);
        dto.setRecipientId(500);
        dto.setCommissionAmount(BigDecimal.valueOf(20));
        dto.setCommissionId(600);
        dto.setHash("TEST_HASH");
        dto.setInitiatorEmail("TEST_INITIATOR_EMAIL");
        dto.setMerchantName("TEST_MERCHANT_NAME");
        dto.setCreatorEmail("TEST_CREATOR_EMAIL");
        dto.setRecipientEmail("TEST_RECIPIENT_EMAIL");
        dto.setCurrencyName("TEST_CURRENCY_NAME");
        dto.setInvoiceOperationPermission(InvoiceOperationPermission.ACCEPT_DECLINE);

        return dto;
    }

    private TransferRequestParamsDto getMockTransferRequestParamsDto(OperationType operationType, String recipient) {
        TransferRequestParamsDto dto = new TransferRequestParamsDto();
        dto.setOperationType(operationType);
        dto.setMerchant(100);
        dto.setCurrency(200);
        dto.setSum(BigDecimal.TEN);
        dto.setRecipient(recipient);
        dto.setPin("TEST_PIN");
        dto.setType("TRANSFER");

        return dto;
    }

    private Currency getCurrency() {
        Currency currency = new Currency();
        currency.setName("BTC");
        currency.setHidden(false);
        currency.setId(3);
        return currency;
    }
}
