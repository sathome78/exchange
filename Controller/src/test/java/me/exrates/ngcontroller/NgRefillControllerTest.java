package me.exrates.ngcontroller;

import me.exrates.model.dto.RefillRequestParamsDto;
import me.exrates.model.dto.ngDto.RefillOnConfirmationDto;
import me.exrates.model.enums.OperationType;
import me.exrates.model.enums.UserRole;
import me.exrates.service.CurrencyService;
import me.exrates.service.GtagRefillService;
import me.exrates.service.InputOutputService;
import me.exrates.service.MerchantService;
import me.exrates.service.RefillService;
import me.exrates.service.UserService;
import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Ignore;
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
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;

import static org.hamcrest.Matchers.is;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class NgRefillControllerTest extends AngularApiCommonTest {
    private static final String BASE_URL = "/api/private/v2/balances/refill";

    @Mock
    private CurrencyService currencyService;
    @Mock
    private InputOutputService inputOutputService;
    @Mock
    private MerchantService merchantService;
    @Mock
    private MessageSource messageSource;
    @Mock
    private RefillService refillService;
    @Mock
    private UserService userService;
    @Mock
    private GtagRefillService gtagRefillService;

    @InjectMocks
    private NgRefillController ngRefillController;

    private MockMvc mockMvc;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        mockMvc = MockMvcBuilders
                .standaloneSetup(ngRefillController)
                .build();

        SecurityContextHolder.getContext()
                .setAuthentication(new AnonymousAuthenticationToken("GUEST", "testemail@gmail.com",
                        AuthorityUtils.createAuthorityList("ADMIN")));
    }

    @Test
    public void getCryptoCurrencies_isOk() throws Exception {
        Mockito.when(currencyService.getCurrencies(anyObject()))
                .thenReturn(Collections.singletonList(getMockCurrency("TEST_NAME")));

        mockMvc.perform(MockMvcRequestBuilders.get(BASE_URL + "/crypto-currencies")
                .contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(jsonPath("$.[0].id", is(100)))
                .andExpect(jsonPath("$.[0].name", is("TEST_NAME")))
                .andExpect(jsonPath("$.[0].description", is("TEST_DESCRIPTION")))
                .andExpect(jsonPath("$.[0].hidden", is(Boolean.TRUE)));

        verify(currencyService, times(1)).getCurrencies(anyObject());
    }

    @Test
    public void getCryptoCurrencies_exception() throws Exception {
        Mockito.when(currencyService.getCurrencies(anyObject())).thenThrow(Exception.class);

        mockMvc.perform(MockMvcRequestBuilders.get(BASE_URL + "/crypto-currencies")
                .contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(jsonPath("$", is(Collections.emptyList())));

        verify(currencyService, times(1)).getCurrencies(anyObject());
    }

    @Test
    public void getFiatCurrencies_isOk() throws Exception {
        Mockito.when(currencyService.getCurrencies(anyObject(), anyObject()))
                .thenReturn(Collections.singletonList(getMockCurrency("TEST_NAME")));

        mockMvc.perform(MockMvcRequestBuilders.get(BASE_URL + "/fiat-currencies")
                .contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(jsonPath("$.[0].id", is(100)))
                .andExpect(jsonPath("$.[0].name", is("TEST_NAME")))
                .andExpect(jsonPath("$.[0].description", is("TEST_DESCRIPTION")))
                .andExpect(jsonPath("$.[0].hidden", is(Boolean.TRUE)));

        verify(currencyService, times(1)).getCurrencies(anyObject(), anyObject());
    }

    @Test
    public void getFiatCurrencies_exception() throws Exception {
        Mockito.when(currencyService.getCurrencies(anyObject(), anyObject())).thenThrow(Exception.class);

        mockMvc.perform(MockMvcRequestBuilders.get(BASE_URL + "/fiat-currencies")
                .contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(MockMvcResultMatchers.status().isOk());

        verify(currencyService, times(1)).getCurrencies(anyObject(), anyObject());
    }

    @Test
    public void getGtagRequests_isOk() throws Exception {
        Integer count = 100;
        Mockito.when(gtagRefillService.getUserRequests(anyString())).thenReturn(count);

        mockMvc.perform(MockMvcRequestBuilders.get(BASE_URL + "/afgssr/gtag")
                .contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(jsonPath("$", Matchers.hasKey("count")))
                .andExpect(jsonPath("$", Matchers.hasValue("100")));

        verify(gtagRefillService, times(1)).getUserRequests(anyString());
    }

    @Test
    public void resetGtagRequests_isOk() throws Exception {
        Mockito.doNothing().when(gtagRefillService).resetCount(anyString());

        mockMvc.perform(MockMvcRequestBuilders.delete(BASE_URL + "/afgssr/gtag")
                .contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(jsonPath("$", Matchers.hasKey("reset")))
                .andExpect(jsonPath("$", Matchers.hasValue("true")));

        verify(gtagRefillService, times(1)).resetCount(anyString());
    }

    @Test
    public void inputCredits_currency_equals_null() throws Exception {
        Mockito.when(currencyService.findByName(anyString())).thenReturn(null);

        mockMvc.perform(MockMvcRequestBuilders.get(BASE_URL + "/merchants/input")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .param("currency", "TEST_CURRENCY"))
                .andExpect(MockMvcResultMatchers.status().isNotAcceptable())
                .andExpect(jsonPath("$.detail", is("Currency not found for name: TEST_CURRENCY")));

        verify(currencyService, times(1)).findByName(anyString());
    }

    @Test
    public void inputCredits_currency_isOk() throws Exception {
        Mockito.when(currencyService.findByName(anyString())).thenReturn(getMockCurrency("TEST_NAME"));
        Mockito.when(userService.getUserRoleFromSecurityContext()).thenReturn(UserRole.USER);
        Mockito.when(currencyService.retrieveMinLimitForRoleAndCurrency(anyObject(), anyObject(), anyInt())).thenReturn(BigDecimal.TEN);
        Mockito.when(currencyService.getCurrencyScaleByCurrencyId(anyInt())).thenReturn(getMockMerchantCurrencyScaleDto());
        Mockito.when(merchantService.getAllUnblockedForOperationTypeByCurrencies(anyObject(), anyObject()))
                .thenReturn(Collections.singletonList(getMockMerchantCurrency()));
        Mockito.when(refillService.retrieveAddressAndAdditionalParamsForRefillForMerchantCurrencies(anyObject(), anyString()))
                .thenReturn(Collections.singletonList(getMockMerchantCurrency()));
        Mockito.when(currencyService.getWarningForCurrency(anyInt(), anyObject()))
                .thenReturn(Collections.singletonList("TEST_WARNING_CODE_LIST"));

        mockMvc.perform(MockMvcRequestBuilders.get(BASE_URL + "/merchants/input")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .param("currency", "TEST_CURRENCY"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(jsonPath("$.currency.id", is(100)))
                .andExpect(jsonPath("$.currency.name", is("TEST_NAME")))
                .andExpect(jsonPath("$.currency.description", is("TEST_DESCRIPTION")))
                .andExpect(jsonPath("$.currency.hidden", is(Boolean.TRUE)));

        verify(currencyService, times(1)).findByName(anyString());
        verify(userService, times(1)).getUserRoleFromSecurityContext();
        verify(currencyService, times(1)).retrieveMinLimitForRoleAndCurrency(anyObject(), anyObject(), anyInt());
        verify(currencyService, times(1)).getCurrencyScaleByCurrencyId(anyInt());
        verify(merchantService, times(1)).getAllUnblockedForOperationTypeByCurrencies(anyObject(), anyObject());
        verify(refillService, times(1)).retrieveAddressAndAdditionalParamsForRefillForMerchantCurrencies(anyObject(), anyString());
        verify(currencyService, times(1)).getWarningForCurrency(anyInt(), anyObject());
    }

    @Test
    @Ignore
    public void createRefillRequest_operation_type_not_equals_input() throws Exception {
        Mockito.when(userService.getUserLocaleForMobile(anyString())).thenReturn(Locale.ENGLISH);

        mockMvc.perform(MockMvcRequestBuilders.post(BASE_URL + "/request/create")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(objectMapper.writeValueAsString(getMockRefillRequestParamsDto(OperationType.BUY, Boolean.FALSE))))
                .andExpect(MockMvcResultMatchers.status().isNotAcceptable())
                .andExpect(jsonPath("$.detail", is("Request operation type is not INPUT, but BUY")));

        verify(userService, times(1)).getUserLocaleForMobile(anyString());
    }

    @Test
    @Ignore
    public void createRefillRequest_operation_type_equals_input() throws Exception {
        when(userService.getUserLocaleForMobile(anyString())).thenReturn(Locale.ENGLISH);
        when(refillService.checkInputRequestsLimit(anyInt(), anyString())).thenReturn(Boolean.FALSE);

        mockMvc.perform(MockMvcRequestBuilders.post(BASE_URL + "/request/create")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(objectMapper.writeValueAsString(getMockRefillRequestParamsDto(OperationType.INPUT, Boolean.TRUE))))
                .andExpect(MockMvcResultMatchers.status().isNotAcceptable())
                .andExpect(jsonPath("$.detail", is("Failed to process refill request as number of tries exceeded ")));

        verify(userService, times(1)).getUserLocaleForMobile(anyString());
        verify(refillService, times(1)).checkInputRequestsLimit(anyInt(), anyString());
    }

    @Test
    @Ignore
    public void createRefillRequest_forceGenerateNewAddress_equals_true_and_address_not_present() throws Exception {
        when(userService.getUserLocaleForMobile(anyString())).thenReturn(Locale.ENGLISH);
        when(userService.getIdByEmail(anyString())).thenReturn(100);
        when(refillService.checkInputRequestsLimit(anyInt(), anyString())).thenReturn(Boolean.TRUE);
        when(refillService.getAddressByMerchantIdAndCurrencyIdAndUserId(anyInt(), anyInt(), anyInt()))
                .thenReturn(Optional.of(""));
        when(messageSource.getMessage(anyString(), anyObject(), anyObject())).thenReturn("TEST_MESSAGE");

        mockMvc.perform(MockMvcRequestBuilders.post(BASE_URL + "/request/create")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(objectMapper.writeValueAsString(getMockRefillRequestParamsDto(OperationType.INPUT, Boolean.FALSE))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.params.qr", is("")))
                .andExpect(jsonPath("$.params.address", is("")))
                .andExpect(jsonPath("$.params.message", is("TEST_MESSAGE")));

        verify(userService, times(1)).getUserLocaleForMobile(anyString());
        verify(userService, times(1)).getIdByEmail(anyString());
        verify(refillService, times(1)).checkInputRequestsLimit(anyInt(), anyString());
        verify(refillService, times(1)).getAddressByMerchantIdAndCurrencyIdAndUserId(anyInt(), anyInt(), anyInt());
        verify(messageSource, times(1)).getMessage(anyString(), anyObject(), anyObject());
    }

    @Test
    @Ignore
    public void createRefillRequest_forceGenerateNewAddress_equals_true_and_address_is_present() throws Exception {
        Map<String, Object> response = new HashMap<>();
        response.put("TEST_RESPONSE_KEY", "TEST_RESPONSE_VALUE");

        when(userService.getUserLocaleForMobile(anyString())).thenReturn(Locale.ENGLISH);
        when(userService.getIdByEmail(anyString())).thenReturn(100);
        when(refillService.checkInputRequestsLimit(anyInt(), anyString())).thenReturn(Boolean.TRUE);
        when(refillService.getAddressByMerchantIdAndCurrencyIdAndUserId(anyInt(), anyInt(), anyInt()))
                .thenReturn(Optional.ofNullable(null));
        when(inputOutputService.prepareCreditsOperation(anyObject(), anyString(), anyObject()))
                .thenReturn(getMockCreditsOperation());
        when(refillService.createRefillRequest(anyObject())).thenReturn(response);

        mockMvc.perform(MockMvcRequestBuilders.post(BASE_URL + "/request/create")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(objectMapper.writeValueAsString(getMockRefillRequestParamsDto(OperationType.INPUT, Boolean.FALSE))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", Matchers.hasKey("TEST_RESPONSE_KEY")))
                .andExpect(jsonPath("$", Matchers.hasValue("TEST_RESPONSE_VALUE")));

        verify(userService, times(1)).getUserLocaleForMobile(anyString());
        verify(userService, times(1)).getIdByEmail(anyString());
        verify(refillService, times(1)).checkInputRequestsLimit(anyInt(), anyString());
        verify(refillService, times(1)).getAddressByMerchantIdAndCurrencyIdAndUserId(anyInt(), anyInt(), anyInt());
        verify(inputOutputService, times(1)).prepareCreditsOperation(anyObject(), anyString(), anyObject());
        verify(refillService, times(1)).createRefillRequest(anyObject());
    }

    @Test
    @Ignore
    public void createRefillRequest_exception() throws Exception {
        Map<String, Object> response = new HashMap<>();
        response.put("TEST_RESPONSE_KEY", "TEST_RESPONSE_VALUE");

        when(userService.getUserLocaleForMobile(anyString())).thenReturn(Locale.ENGLISH);
        when(userService.getIdByEmail(anyString())).thenReturn(100);
        when(refillService.checkInputRequestsLimit(anyInt(), anyString())).thenReturn(Boolean.TRUE);
        when(refillService.getAddressByMerchantIdAndCurrencyIdAndUserId(anyInt(), anyInt(), anyInt()))
                .thenReturn(Optional.ofNullable(null));
        when(inputOutputService.prepareCreditsOperation(anyObject(), anyString(), anyObject()))
                .thenReturn(getMockCreditsOperation());
        when(refillService.createRefillRequest(anyObject())).thenThrow(Exception.class);

        mockMvc.perform(MockMvcRequestBuilders.post(BASE_URL + "/request/create")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(objectMapper.writeValueAsString(getMockRefillRequestParamsDto(OperationType.INPUT, Boolean.FALSE))))
                .andExpect(status().isNotAcceptable());

        verify(userService, times(1)).getUserLocaleForMobile(anyString());
        verify(userService, times(1)).getIdByEmail(anyString());
        verify(refillService, times(1)).checkInputRequestsLimit(anyInt(), anyString());
        verify(refillService, times(1)).getAddressByMerchantIdAndCurrencyIdAndUserId(anyInt(), anyInt(), anyInt());
        verify(inputOutputService, times(1)).prepareCreditsOperation(anyObject(), anyString(), anyObject());
        verify(refillService, times(1)).createRefillRequest(anyObject());
    }

    @Test
    public void getRefillConfirmationsForCurrencyy_isOk() throws Exception {
        RefillOnConfirmationDto dto = new RefillOnConfirmationDto();
        dto.setHash("TEST_HASH");
        dto.setAmount(BigDecimal.valueOf(100));
        dto.setAddress("TEST_ADDRESS");
        dto.setCollectedConfirmations(200);
        dto.setNeededConfirmations(300);

        Mockito.when(refillService.getOnConfirmationRefills(anyString(), anyInt()))
                .thenReturn(Collections.singletonList(dto));

        mockMvc.perform(MockMvcRequestBuilders.get(BASE_URL + "/requests_on_confirmation/{currencyId}", 100)
                .contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(jsonPath("$.[0].hash", is("TEST_HASH")))
                .andExpect(jsonPath("$.[0].amount", is(100)))
                .andExpect(jsonPath("$.[0].address", is("TEST_ADDRESS")))
                .andExpect(jsonPath("$.[0].collectedConfirmations", is(200)))
                .andExpect(jsonPath("$.[0].neededConfirmations", is(300)));

        verify(refillService, times(1)).getOnConfirmationRefills(anyString(), anyInt());
    }

    @Test
    public void getRefillConfirmationsForCurrencyy_exception() throws Exception {
        Mockito.when(refillService.getOnConfirmationRefills(anyString(), anyInt())).thenThrow(Exception.class);

        mockMvc.perform(MockMvcRequestBuilders.get(BASE_URL + "/requests_on_confirmation/{currencyId}", 100)
                .contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(jsonPath("$", is(Collections.emptyList())));

        verify(refillService, times(1)).getOnConfirmationRefills(anyString(), anyInt());
    }

    private RefillRequestParamsDto getMockRefillRequestParamsDto(OperationType operationType, boolean generateNewAddress) {
        RefillRequestParamsDto dto = new RefillRequestParamsDto();
        dto.setOperationType(operationType);
        dto.setCurrency(100);
        dto.setSum(BigDecimal.TEN);
        dto.setMerchant(200);
        dto.setRecipientBankId(300);
        dto.setRecipientBankCode("TEST_RECIPIENT_BANK_CODE");
        dto.setRecipientBankName("TEST_RECIPIENT_BANK_NAME");
        dto.setRecipient("TEST_RECIPIENT");
        dto.setUserFullName("TEST_USER_FULL_NAME");
        dto.setRemark("TEST_REMARK");
        dto.setMerchantRequestSign("TEST_MERCHANT_REQUEST_SING");
        dto.setAddress("TEST_ADDRESS");
        dto.setGenerateNewAddress(generateNewAddress);
        dto.setChildMerchant("TEST_CHILD_MERCHANT");

        return dto;
    }
}