package me.exrates.ngcontroller;

import me.exrates.model.CurrencyPair;
import me.exrates.model.dto.ReportDto;
import me.exrates.model.dto.onlineTableDto.MyInputOutputHistoryDto;
import me.exrates.model.dto.onlineTableDto.OrderWideListDto;
import me.exrates.model.enums.OrderStatus;
import me.exrates.ngService.BalanceService;
import me.exrates.service.CurrencyService;
import me.exrates.service.OrderService;
import me.exrates.service.UserService;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.servlet.LocaleResolver;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.Collections;
import java.util.Locale;

import static org.hamcrest.Matchers.is;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyBoolean;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyListOf;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class NgDownloadControllerTest extends AngularApiCommonTest {
    private static final String BASE_URL = "/api/private/v2/download";

    @Mock
    private UserService userService;
    @Mock
    private OrderService orderService;
    @Mock
    private CurrencyService currencyService;
    @Mock
    private LocaleResolver localeResolver;
    @Mock
    private BalanceService balanceService;

    @InjectMocks
    NgDownloadController ngDownloadController;

    private MockMvc mockMvc;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        mockMvc = MockMvcBuilders
                .standaloneSetup(ngDownloadController)
                .build();

        SecurityContextHolder.getContext()
                .setAuthentication(new AnonymousAuthenticationToken("guest", "testemail@gmail.com",
                        AuthorityUtils.createAuthorityList("ADMIN")));
    }

    @Test
    public void exportExcelOrders_no_content() throws Exception {
        when(userService.getIdByEmail(anyString())).thenReturn(100);
        when(localeResolver.resolveLocale(any(HttpServletRequest.class))).thenReturn(Locale.ENGLISH);
        when(orderService.getOrdersForExcel(
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
        ).thenReturn(Collections.singletonList(new OrderWideListDto()));
        when(orderService.getOrderExcelFile(anyListOf(OrderWideListDto.class)))
                .thenReturn(new ReportDto());

        mockMvc.perform(get(BASE_URL + "/orders/{status}/export", OrderStatus.OPENED))
                .andExpect(status().isNoContent());

        verify(userService, times(1)).getIdByEmail(anyString());
        verify(localeResolver, times(1)).resolveLocale(any(HttpServletRequest.class));
        verify(orderService, times(1)).getOrdersForExcel(
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
        verify(orderService, times(1)).getOrderExcelFile(
                anyListOf(OrderWideListDto.class));
    }

    @Test
    public void exportExcelOrders_isOk() throws Exception {
        when(userService.getIdByEmail(anyString())).thenReturn(100);
        when(localeResolver.resolveLocale(any(HttpServletRequest.class))).thenReturn(Locale.ENGLISH);
        when(currencyService.findCurrencyPairById(anyInt())).thenReturn(getMockCurrencyPair());
        when(orderService.getOrdersForExcel(
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
        ).thenReturn(Collections.singletonList(new OrderWideListDto()));
        when(orderService.getOrderExcelFile(anyListOf(OrderWideListDto.class)))
                .thenReturn(getMockReportDto());

        mockMvc.perform(get(BASE_URL + "/orders/{status}/export", OrderStatus.OPENED)
                .param("currencyPairId", "2"))
                .andExpect(status().isOk())
                .andExpect(header().string("Content-Type", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                .andExpect(header().string("Content-Length", "12"))
                .andExpect(header().string("Content-Disposition", "form-data; name=\"attachment\"; filename=\"TEST_FILE_NAME\""))
                .andExpect(jsonPath("$", is("TEST_CONTENT")));

        verify(userService, times(1)).getIdByEmail(anyString());
        verify(localeResolver, times(1)).resolveLocale(any(HttpServletRequest.class));
        verify(currencyService, times(1)).findCurrencyPairById(anyInt());
        verify(orderService, times(1)).getOrdersForExcel(
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
        verify(orderService, times(1)).getOrderExcelFile(
                anyListOf(OrderWideListDto.class));
    }

    @Test
    public void getMyInputOutputDataToExcel_no_content() throws Exception {
        when(localeResolver.resolveLocale(any(HttpServletRequest.class))).thenReturn(Locale.ENGLISH);
        when(balanceService.getUserInputOutputHistoryExcel(
                anyString(),
                anyInt(),
                anyString(),
                any(LocalDateTime.class),
                any(LocalDateTime.class),
                anyInt(),
                anyInt(),
                any(Locale.class))
        ).thenReturn(Collections.singletonList(new MyInputOutputHistoryDto()));
        when(orderService.getTransactionExcelFile(anyListOf(MyInputOutputHistoryDto.class))).thenReturn(new ReportDto());

        mockMvc.perform(get(BASE_URL + "/inputOutputData/excel"))
                .andExpect(status().isNoContent());

        verify(localeResolver, times(1)).resolveLocale(any(HttpServletRequest.class));
        verify(balanceService, times(1)).getUserInputOutputHistoryExcel(
                anyString(),
                anyInt(),
                anyString(),
                any(LocalDateTime.class),
                any(LocalDateTime.class),
                anyInt(),
                anyInt(),
                any(Locale.class));
        verify(orderService, times(1))
                .getTransactionExcelFile(anyListOf(MyInputOutputHistoryDto.class));
    }

    @Test
    public void getMyInputOutputDataToExcel_isOk() throws Exception {
        when(localeResolver.resolveLocale(any(HttpServletRequest.class))).thenReturn(Locale.ENGLISH);
        when(balanceService.getUserInputOutputHistoryExcel(
                anyString(),
                anyInt(),
                anyString(),
                any(LocalDateTime.class),
                any(LocalDateTime.class),
                anyInt(),
                anyInt(),
                any(Locale.class))
        ).thenReturn(Collections.singletonList(new MyInputOutputHistoryDto()));
        when(orderService.getTransactionExcelFile(anyListOf(MyInputOutputHistoryDto.class))).thenReturn(getMockReportDto());

        mockMvc.perform(get(BASE_URL + "/inputOutputData/excel"))
                .andExpect(status().isOk())
                .andExpect(header().string("Content-Type", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                .andExpect(header().string("Content-Length", "12"))
                .andExpect(header().string("Content-Disposition", "form-data; name=\"attachment\"; filename=\"TEST_FILE_NAME\""))
                .andExpect(jsonPath("$", is("TEST_CONTENT")));

        verify(localeResolver, times(1)).resolveLocale(any(HttpServletRequest.class));
        verify(balanceService, times(1)).getUserInputOutputHistoryExcel(
                anyString(),
                anyInt(),
                anyString(),
                any(LocalDateTime.class),
                any(LocalDateTime.class),
                anyInt(),
                anyInt(),
                any(Locale.class));
        verify(orderService, times(1))
                .getTransactionExcelFile(anyListOf(MyInputOutputHistoryDto.class));
    }

    private ReportDto getMockReportDto() {
        ReportDto reportDto = new ReportDto();
        reportDto.setId(100);
        reportDto.setFileName("TEST_FILE_NAME");
        reportDto.setContent("TEST_CONTENT".getBytes());
        reportDto.setCreatedAt(LocalDateTime.of(2015, Month.JULY, 29, 19, 30, 40));
        return reportDto;
    }
}