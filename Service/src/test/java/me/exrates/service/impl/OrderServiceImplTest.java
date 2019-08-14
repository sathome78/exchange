package me.exrates.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableList;
import lombok.extern.slf4j.Slf4j;
import me.exrates.dao.CallBackLogDao;
import me.exrates.dao.CommissionDao;
import me.exrates.dao.OrderDao;
import me.exrates.model.Commission;
import me.exrates.model.CompanyWallet;
import me.exrates.model.Currency;
import me.exrates.model.CurrencyPair;
import me.exrates.model.ExOrder;
import me.exrates.model.PagingData;
import me.exrates.model.User;
import me.exrates.model.UserRoleSettings;
import me.exrates.model.chart.ChartResolution;
import me.exrates.model.chart.ChartTimeFrame;
import me.exrates.model.dto.AdminOrderInfoDto;
import me.exrates.model.dto.CallBackLogDto;
import me.exrates.model.dto.CandleChartItemDto;
import me.exrates.model.dto.CoinmarketApiDto;
import me.exrates.model.dto.CurrencyPairLimitDto;
import me.exrates.model.dto.CurrencyPairTurnoverReportDto;
import me.exrates.model.dto.ExOrderStatisticsDto;
import me.exrates.model.dto.InputCreateOrderDto;
import me.exrates.model.dto.OrderBasicInfoDto;
import me.exrates.model.dto.OrderBookWrapperDto;
import me.exrates.model.dto.OrderCommissionsDto;
import me.exrates.model.dto.OrderCreateDto;
import me.exrates.model.dto.OrderCreationResultDto;
import me.exrates.model.dto.OrderDetailDto;
import me.exrates.model.dto.OrderInfoDto;
import me.exrates.model.dto.OrderReportInfoDto;
import me.exrates.model.dto.OrderValidationDto;
import me.exrates.model.dto.OrdersListWrapper;
import me.exrates.model.dto.RefreshStatisticDto;
import me.exrates.model.dto.ReportDto;
import me.exrates.model.dto.UserSummaryOrdersByCurrencyPairsDto;
import me.exrates.model.dto.UserSummaryOrdersDto;
import me.exrates.model.dto.WalletsAndCommissionsForOrderCreationDto;
import me.exrates.model.dto.WalletsForOrderAcceptionDto;
import me.exrates.model.dto.WalletsForOrderCancelDto;
import me.exrates.model.dto.dataTable.DataTable;
import me.exrates.model.dto.dataTable.DataTableParams;
import me.exrates.model.dto.filterData.AdminOrderFilterData;
import me.exrates.model.dto.mobileApiDto.OrderCreationParamsDto;
import me.exrates.model.dto.mobileApiDto.dashboard.CommissionsDto;
import me.exrates.model.dto.onlineTableDto.ExOrderStatisticsShortByPairsDto;
import me.exrates.model.dto.onlineTableDto.MyInputOutputHistoryDto;
import me.exrates.model.dto.onlineTableDto.OrderAcceptedHistoryDto;
import me.exrates.model.dto.onlineTableDto.OrderListDto;
import me.exrates.model.dto.onlineTableDto.OrderWideListDto;
import me.exrates.model.dto.openAPI.OpenOrderDto;
import me.exrates.model.dto.openAPI.OrderBookItem;
import me.exrates.model.dto.openAPI.TradeHistoryDto;
import me.exrates.model.dto.openAPI.TransactionDto;
import me.exrates.model.dto.openAPI.UserOrdersDto;
import me.exrates.model.dto.openAPI.UserTradeHistoryDto;
import me.exrates.model.enums.BusinessUserRoleEnum;
import me.exrates.model.enums.ChartResolutionTimeUnit;
import me.exrates.model.enums.CurrencyPairType;
import me.exrates.model.enums.IntervalType;
import me.exrates.model.enums.IntervalType2;
import me.exrates.model.enums.OperationType;
import me.exrates.model.enums.OrderActionEnum;
import me.exrates.model.enums.OrderBaseType;
import me.exrates.model.enums.OrderDeleteStatus;
import me.exrates.model.enums.OrderStatus;
import me.exrates.model.enums.OrderType;
import me.exrates.model.enums.PrecissionsEnum;
import me.exrates.model.enums.RefreshObjectsEnum;
import me.exrates.model.enums.TransactionSourceType;
import me.exrates.model.enums.TransactionStatus;
import me.exrates.model.enums.UserRole;
import me.exrates.model.enums.WalletTransferStatus;
import me.exrates.model.ngModel.ResponseInfoCurrencyPairDto;
import me.exrates.model.vo.BackDealInterval;
import me.exrates.model.vo.CacheData;
import me.exrates.model.vo.OrderRoleInfoForDelete;
import me.exrates.model.vo.TransactionDescription;
import me.exrates.model.vo.WalletOperationData;
import me.exrates.service.CompanyWalletService;
import me.exrates.service.CurrencyService;
import me.exrates.service.OrderService;
import me.exrates.service.ReferralService;
import me.exrates.service.TransactionService;
import me.exrates.service.UserRoleService;
import me.exrates.service.UserService;
import me.exrates.service.WalletService;
import me.exrates.service.cache.ExchangeRatesHolder;
import me.exrates.service.events.AcceptOrderEvent;
import me.exrates.service.exception.AttemptToAcceptBotOrderException;
import me.exrates.service.exception.IncorrectCurrentUserException;
import me.exrates.service.exception.OrderDeletingException;
import me.exrates.service.exception.invoice.InsufficientCostsInWalletException;
import me.exrates.service.exception.process.CancelOrderException;
import me.exrates.service.exception.process.NotCreatableOrderException;
import me.exrates.service.exception.process.NotEnoughUserWalletMoneyException;
import me.exrates.service.exception.process.OrderAcceptionException;
import me.exrates.service.exception.process.OrderCancellingException;
import me.exrates.service.exception.process.OrderCreationException;
import me.exrates.service.impl.proxy.ServiceCacheableProxy;
import me.exrates.service.stopOrder.StopOrderService;
import me.exrates.service.util.BiTuple;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.json.JSONArray;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.MessageSource;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.math.BigDecimal;
import java.security.Principal;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.IntStream;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyBoolean;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyListOf;
import static org.mockito.Matchers.anySetOf;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@Slf4j
@RunWith(MockitoJUnitRunner.class)
public class OrderServiceImplTest {
    private static final DateTimeFormatter FORMATTER_FOR_NAME = DateTimeFormatter.ofPattern("dd_MM_yyyy_HH_mm");
    private static final String USER_EMAIL = "test@test.com";

    @Mock
    private OrderDao orderDao;
    @Mock
    private ExchangeRatesHolder exchangeRatesHolder;
    @Mock
    private CurrencyService currencyService;
    @Mock
    private UserService userService;
    @Mock
    private MessageSource messageSource;
    @Mock
    private UserRoleService userRoleService;
    @Mock
    private ApplicationEventPublisher eventPublisher;
    @Mock
    private WalletService walletService;
    @Mock
    TransactionDescription transactionDescription;
    @Mock
    private CommissionDao commissionDao;
    @Mock
    private CompanyWalletService companyWalletService;
    @Mock
    private ReferralService referralService;
    @Mock
    private ObjectMapper objectMapper;
    @Mock
    private StopOrderService stopOrderService;
    @Mock
    private CallBackLogDao callBackDao;
    @Mock
    private ServiceCacheableProxy serviceCacheableProxy;
    @Mock
    private TransactionService transactionService;

    @InjectMocks
    private OrderService orderService = new OrderServiceImpl();

    @Before
    public void setUp() {
        final CurrencyPairLimitDto currencyPairLimitDto = new CurrencyPairLimitDto();
        currencyPairLimitDto.setMinAmount(BigDecimal.valueOf(0.0002));
        currencyPairLimitDto.setMaxAmount(BigDecimal.valueOf(1000000000));
        when(currencyService.findLimitForRoleByCurrencyPairAndType(anyInt(),
                any(OperationType.class))).thenReturn(currencyPairLimitDto);
    }

    @Test
    public void getIntervals() {
        BackDealInterval bdi1 = getMockBackDealInterval();

        BackDealInterval bdi2 = new BackDealInterval();
        bdi2.setIntervalValue(24);
        bdi2.setIntervalType(IntervalType.HOUR);

        BackDealInterval bdi3 = new BackDealInterval();
        bdi3.setIntervalValue(7);
        bdi3.setIntervalType(IntervalType.DAY);

        BackDealInterval bdi4 = new BackDealInterval();
        bdi4.setIntervalValue(1);
        bdi4.setIntervalType(IntervalType.MONTH);

        BackDealInterval bdi5 = new BackDealInterval();
        bdi5.setIntervalValue(6);
        bdi5.setIntervalType(IntervalType.MONTH);

        List<BackDealInterval> expected = Arrays.asList(bdi1, bdi2, bdi3, bdi4, bdi5);

        List<BackDealInterval> intervals = orderService.getIntervals();

        assertNotNull(intervals);
        assertEquals(expected.size(), intervals.size());
        assertThat(expected, is(intervals));
        assertEquals(expected, intervals);
    }

    @Test
    public void getChartTimeFrames() {
        ChartTimeFrame ctf1 = new ChartTimeFrame(new ChartResolution(30, ChartResolutionTimeUnit.MINUTE), 5, IntervalType2.DAY);
        ChartTimeFrame ctf2 = new ChartTimeFrame(new ChartResolution(60, ChartResolutionTimeUnit.MINUTE), 7, IntervalType2.DAY);
        ChartTimeFrame ctf3 = new ChartTimeFrame(new ChartResolution(240, ChartResolutionTimeUnit.MINUTE), 10, IntervalType2.DAY);
        ChartTimeFrame ctf4 = new ChartTimeFrame(new ChartResolution(720, ChartResolutionTimeUnit.MINUTE), 15, IntervalType2.DAY);
        ChartTimeFrame ctf5 = new ChartTimeFrame(new ChartResolution(1, ChartResolutionTimeUnit.MINUTE), 7, IntervalType2.MONTH);

        List<ChartTimeFrame> expected = Arrays.asList(ctf1, ctf2, ctf3, ctf4, ctf5);

        List<ChartTimeFrame> chartTimeFrames = orderService.getChartTimeFrames();

        assertNotNull(chartTimeFrames);
        assertEquals(expected.size(), chartTimeFrames.size());
        assertEquals(expected.get(0).getResolution(), chartTimeFrames.get(0).getResolution());
        assertEquals(expected.get(0).getTimeValue(), chartTimeFrames.get(0).getTimeValue());
        assertEquals(expected.get(0).getTimeUnit(), chartTimeFrames.get(0).getTimeUnit());
        assertEquals(expected.get(1).getResolution(), chartTimeFrames.get(1).getResolution());
        assertEquals(expected.get(1).getTimeValue(), chartTimeFrames.get(1).getTimeValue());
        assertEquals(expected.get(1).getTimeUnit(), chartTimeFrames.get(1).getTimeUnit());
        assertEquals(expected.get(2).getResolution(), chartTimeFrames.get(2).getResolution());
        assertEquals(expected.get(2).getTimeValue(), chartTimeFrames.get(2).getTimeValue());
        assertEquals(expected.get(2).getTimeUnit(), chartTimeFrames.get(2).getTimeUnit());
        assertEquals(expected.get(3).getResolution(), chartTimeFrames.get(3).getResolution());
        assertEquals(expected.get(3).getTimeValue(), chartTimeFrames.get(3).getTimeValue());
        assertEquals(expected.get(3).getTimeUnit(), chartTimeFrames.get(3).getTimeUnit());
        assertEquals(expected.get(4).getTimeValue(), chartTimeFrames.get(4).getTimeValue());
        assertEquals(expected.get(4).getTimeUnit(), chartTimeFrames.get(4).getTimeUnit());
    }

    @Test
    public void getOrderStatistic_default_ExOrderStatisticsDto() {
        when(orderDao.getOrderStatistic(any(CurrencyPair.class), any(BackDealInterval.class)))
                .thenReturn(new ExOrderStatisticsDto());

        ExOrderStatisticsDto orderStatistic = orderService
                .getOrderStatistic(new CurrencyPair(), new BackDealInterval(), Locale.ENGLISH);

        assertNotNull(orderStatistic);
        assertNull(orderStatistic.getCurrencyPair());
        assertEquals("0.000000000", orderStatistic.getFirstOrderAmountBase());
        assertEquals("0.000000000", orderStatistic.getFirstOrderRate());
        assertEquals("0.000000000", orderStatistic.getLastOrderAmountBase());
        assertEquals("0.000000000", orderStatistic.getLastOrderRate());
        assertEquals("0.00", orderStatistic.getPercentChange());
        assertEquals("0.000000000", orderStatistic.getMinRate());
        assertEquals("0.000000000", orderStatistic.getMaxRate());
        assertEquals("0.000000000", orderStatistic.getSumBase());
        assertEquals("0.000000000", orderStatistic.getSumConvert());

        verify(orderDao, times(1))
                .getOrderStatistic(any(CurrencyPair.class), any(BackDealInterval.class));
    }

    @Test
    public void getDataForAreaChart() {
        when(orderDao.getDataForAreaChart(any(CurrencyPair.class), any(BackDealInterval.class)))
                .thenReturn(Collections.singletonList(new HashMap<String, Object>() {{
                    put("key1", "value1");
                    put("key2", "value2");
                }}));

        List<Map<String, Object>> dataForAreaChart = orderService
                .getDataForAreaChart(new CurrencyPair("BTC/USD"), getMockBackDealInterval());

        assertNotNull(dataForAreaChart);
        assertEquals(1, dataForAreaChart.size());
        assertTrue(dataForAreaChart.get(0).containsKey("key1"));
        assertTrue(dataForAreaChart.get(0).containsValue("value1"));
        assertTrue(dataForAreaChart.get(0).containsKey("key2"));
        assertTrue(dataForAreaChart.get(0).containsValue("value2"));

        verify(orderDao, times(1))
                .getDataForAreaChart(any(CurrencyPair.class), any(BackDealInterval.class));
    }

    @Test
    public void getOrdersStatisticByPairsEx_ICO_CURRENCIES_STATISTIC() {
        when(exchangeRatesHolder.getAllRates())
                .thenReturn(Collections.singletonList(getMockExOrderStatisticsShortByPairsDto(CurrencyPairType.ICO)));
        when(currencyService.getAllCurrencyPairCached()).thenReturn(Collections.singletonMap(1, getMockCurrencyPair(CurrencyPairType.MAIN)));


        List<ExOrderStatisticsShortByPairsDto> ordersStatisticByPairsEx = orderService
                .getOrdersStatisticByPairsEx(RefreshObjectsEnum.ICO_CURRENCIES_STATISTIC);

        assertNotNull(ordersStatisticByPairsEx);
        assertEquals(1, ordersStatisticByPairsEx.size());
        assertEquals(Integer.valueOf(1), ordersStatisticByPairsEx.get(0).getCurrencyPairId());
        assertEquals("BTC/USD", ordersStatisticByPairsEx.get(0).getCurrencyPairName());
        assertEquals(Integer.valueOf(2), ordersStatisticByPairsEx.get(0).getCurrencyPairPrecision());
        assertEquals("0.000000000", ordersStatisticByPairsEx.get(0).getLastOrderRate());
        assertEquals("0.000000000", ordersStatisticByPairsEx.get(0).getPredLastOrderRate());
        assertEquals("0.00", ordersStatisticByPairsEx.get(0).getPercentChange());
        assertEquals("FIAT", ordersStatisticByPairsEx.get(0).getMarket());
        assertEquals("0", ordersStatisticByPairsEx.get(0).getPriceInUSD());
        assertEquals(CurrencyPairType.ICO, ordersStatisticByPairsEx.get(0).getType());
        assertEquals("0.000000000", ordersStatisticByPairsEx.get(0).getVolume());
        assertEquals("0.000000000", ordersStatisticByPairsEx.get(0).getCurrencyVolume());
        assertEquals("0.000000000", ordersStatisticByPairsEx.get(0).getHigh24hr());
        assertEquals("0.000000000", ordersStatisticByPairsEx.get(0).getLow24hr());
        assertEquals("0.000000000", ordersStatisticByPairsEx.get(0).getLastOrderRate24hr());
        assertEquals("2019-04-03 14:52:14", ordersStatisticByPairsEx.get(0).getLastUpdateCache());

        verify(exchangeRatesHolder, atLeastOnce()).getAllRates();
    }

    @Test
    public void getOrdersStatisticByPairsEx_MAIN_CURRENCIES_STATISTIC() {
        when(exchangeRatesHolder.getAllRates())
                .thenReturn(Collections.singletonList(getMockExOrderStatisticsShortByPairsDto(CurrencyPairType.MAIN)));
        when(currencyService.getAllCurrencyPairCached()).thenReturn(Collections.singletonMap(1, getMockCurrencyPair(CurrencyPairType.MAIN)));


        List<ExOrderStatisticsShortByPairsDto> ordersStatisticByPairsEx = orderService
                .getOrdersStatisticByPairsEx(RefreshObjectsEnum.MAIN_CURRENCIES_STATISTIC);

        assertNotNull(ordersStatisticByPairsEx);
        assertEquals(1, ordersStatisticByPairsEx.size());
        assertEquals(Integer.valueOf(1), ordersStatisticByPairsEx.get(0).getCurrencyPairId());
        assertEquals("BTC/USD", ordersStatisticByPairsEx.get(0).getCurrencyPairName());
        assertEquals(Integer.valueOf(2), ordersStatisticByPairsEx.get(0).getCurrencyPairPrecision());
        assertEquals("0.000000000", ordersStatisticByPairsEx.get(0).getLastOrderRate());
        assertEquals("0.000000000", ordersStatisticByPairsEx.get(0).getPredLastOrderRate());
        assertEquals("0.00", ordersStatisticByPairsEx.get(0).getPercentChange());
        assertEquals("FIAT", ordersStatisticByPairsEx.get(0).getMarket());
        assertEquals("0", ordersStatisticByPairsEx.get(0).getPriceInUSD());
        assertEquals(CurrencyPairType.MAIN, ordersStatisticByPairsEx.get(0).getType());
        assertEquals("0.000000000", ordersStatisticByPairsEx.get(0).getVolume());
        assertEquals("0.000000000", ordersStatisticByPairsEx.get(0).getCurrencyVolume());
        assertEquals("0.000000000", ordersStatisticByPairsEx.get(0).getHigh24hr());
        assertEquals("0.000000000", ordersStatisticByPairsEx.get(0).getLow24hr());
        assertEquals("0.000000000", ordersStatisticByPairsEx.get(0).getLastOrderRate24hr());
        assertEquals("2019-04-03 14:52:14", ordersStatisticByPairsEx.get(0).getLastUpdateCache());

        verify(exchangeRatesHolder, atLeastOnce()).getAllRates();
    }

    @Test
    public void getOrdersStatisticByPairsEx_default() {
        when(exchangeRatesHolder.getAllRates())
                .thenReturn(Collections.singletonList(getMockExOrderStatisticsShortByPairsDto(CurrencyPairType.ALL)));
        when(currencyService.getAllCurrencyPairCached()).thenReturn(Collections.singletonMap(1, getMockCurrencyPair(CurrencyPairType.MAIN)));


        List<ExOrderStatisticsShortByPairsDto> ordersStatisticByPairsEx = orderService
                .getOrdersStatisticByPairsEx(RefreshObjectsEnum.ALL_TRADES);

        assertNotNull(ordersStatisticByPairsEx);
        assertEquals(1, ordersStatisticByPairsEx.size());
        assertEquals(Integer.valueOf(1), ordersStatisticByPairsEx.get(0).getCurrencyPairId());
        assertEquals("BTC/USD", ordersStatisticByPairsEx.get(0).getCurrencyPairName());
        assertEquals(Integer.valueOf(2), ordersStatisticByPairsEx.get(0).getCurrencyPairPrecision());
        assertEquals("0.000000000", ordersStatisticByPairsEx.get(0).getLastOrderRate());
        assertEquals("0.000000000", ordersStatisticByPairsEx.get(0).getPredLastOrderRate());
        assertEquals("0.00", ordersStatisticByPairsEx.get(0).getPercentChange());
        assertEquals("FIAT", ordersStatisticByPairsEx.get(0).getMarket());
        assertEquals("0", ordersStatisticByPairsEx.get(0).getPriceInUSD());
        assertEquals(CurrencyPairType.ALL, ordersStatisticByPairsEx.get(0).getType());
        assertEquals("0.000000000", ordersStatisticByPairsEx.get(0).getVolume());
        assertEquals("0.000000000", ordersStatisticByPairsEx.get(0).getCurrencyVolume());
        assertEquals("0.000000000", ordersStatisticByPairsEx.get(0).getHigh24hr());
        assertEquals("0.000000000", ordersStatisticByPairsEx.get(0).getLow24hr());
        assertEquals("0.000000000", ordersStatisticByPairsEx.get(0).getLastOrderRate24hr());
        assertEquals("2019-04-03 14:52:14", ordersStatisticByPairsEx.get(0).getLastUpdateCache());

        verify(exchangeRatesHolder, atLeastOnce()).getAllRates();
    }

    @Test
    public void getStatForSomeCurrencies() {
        when(exchangeRatesHolder.getCurrenciesRates(anySetOf(Integer.class)))
                .thenReturn(Collections.singletonList(getMockExOrderStatisticsShortByPairsDto(CurrencyPairType.MAIN)));

        List<ExOrderStatisticsShortByPairsDto> statForSomeCurrencies = orderService
                .getStatForSomeCurrencies(new HashSet<Integer>() {{
                    add(1);
                    add(2);
                    add(3);
                }});

        assertNotNull(statForSomeCurrencies);
        assertEquals(1, statForSomeCurrencies.size());
        assertEquals(Integer.valueOf(1), statForSomeCurrencies.get(0).getCurrencyPairId());
        assertEquals("BTC/USD", statForSomeCurrencies.get(0).getCurrencyPairName());
        assertEquals(Integer.valueOf(2), statForSomeCurrencies.get(0).getCurrencyPairPrecision());
        assertEquals("0.000000000", statForSomeCurrencies.get(0).getLastOrderRate());
        assertEquals("0.000000000", statForSomeCurrencies.get(0).getPredLastOrderRate());
        assertEquals("0.00", statForSomeCurrencies.get(0).getPercentChange());
        assertEquals("FIAT", statForSomeCurrencies.get(0).getMarket());
        assertEquals("0", statForSomeCurrencies.get(0).getPriceInUSD());
        assertEquals(CurrencyPairType.MAIN, statForSomeCurrencies.get(0).getType());
        assertEquals("0.000000000", statForSomeCurrencies.get(0).getVolume());
        assertEquals("0.000000000", statForSomeCurrencies.get(0).getCurrencyVolume());
        assertEquals("0.000000000", statForSomeCurrencies.get(0).getHigh24hr());
        assertEquals("0.000000000", statForSomeCurrencies.get(0).getLow24hr());
        assertEquals("0.000000000", statForSomeCurrencies.get(0).getLastOrderRate24hr());
        assertEquals("2019-04-03 14:52:14", statForSomeCurrencies.get(0).getLastUpdateCache());
    }

    @Test
    public void getStatForSomeCurrencies_null() {
        when(exchangeRatesHolder.getCurrenciesRates(anySetOf(Integer.class))).thenReturn(null);

        List<ExOrderStatisticsShortByPairsDto> statForSomeCurrencies = orderService
                .getStatForSomeCurrencies(new HashSet<Integer>() {{
                    add(1);
                    add(2);
                    add(3);
                }});

        assertNull(statForSomeCurrencies);

        verify(exchangeRatesHolder, atLeastOnce()).getCurrenciesRates(anySetOf(Integer.class));
    }

    @Test
    public void prepareNewOrder_operation_type_sell_order_base_type_ico_authentication_equals_null() {
        when(userService.getUserRoleFromDB(anyString())).thenReturn(UserRole.USER);
        when(orderDao.getWalletAndCommission(
                anyString(),
                any(Currency.class),
                any(OperationType.class),
                any(UserRole.class))).thenReturn(getMockWalletsAndCommissionsForOrderCreationDto());

        OrderCreateDto orderCreateDto = orderService.prepareNewOrder(
                new CurrencyPair("BTC/USD"),
                OperationType.SELL,
                USER_EMAIL,
                BigDecimal.ONE,
                BigDecimal.ONE,
                OrderBaseType.ICO
        );

        assertNotNull(orderCreateDto);
        assertEquals(getMockWalletsAndCommissionsForOrderCreationDto().getUserId(), orderCreateDto.getUserId());
        assertEquals("BTC/USD", orderCreateDto.getCurrencyPair().getName());
        assertEquals(OperationType.SELL, orderCreateDto.getOperationType());
        assertEquals(OrderBaseType.ICO, orderCreateDto.getOrderBaseType());
        assertEquals(BigDecimal.ONE, orderCreateDto.getAmount());
        assertEquals(BigDecimal.ONE, orderCreateDto.getExchangeRate());
        assertEquals(getMockWalletsAndCommissionsForOrderCreationDto().getCommissionValue(), orderCreateDto.getComission());
        assertEquals(24, orderCreateDto.getComissionId());

        verify(orderDao, atLeastOnce()).getWalletAndCommission(
                anyString(),
                any(Currency.class),
                any(OperationType.class),
                any(UserRole.class));

        reset(userService);
    }

    @Test
    public void prepareNewOrder_operation_type_sell_order_base_type_ico() {
        when(userService.getUserRoleFromDB(anyString())).thenReturn(UserRole.USER);
        when(orderDao.getWalletAndCommission(
                anyString(),
                any(Currency.class),
                any(OperationType.class),
                any(UserRole.class))).thenReturn(getMockWalletsAndCommissionsForOrderCreationDto());

        OrderCreateDto orderCreateDto = orderService.prepareNewOrder(
                new CurrencyPair("BTC/USD"),
                OperationType.SELL,
                USER_EMAIL,
                BigDecimal.ONE,
                BigDecimal.ONE,
                OrderBaseType.ICO);

        assertNotNull(orderCreateDto);
        assertEquals(getMockWalletsAndCommissionsForOrderCreationDto().getUserId(), orderCreateDto.getUserId());
        assertEquals("BTC/USD", orderCreateDto.getCurrencyPair().getName());
        assertEquals(OperationType.SELL, orderCreateDto.getOperationType());
        assertEquals(OrderBaseType.ICO, orderCreateDto.getOrderBaseType());
        assertEquals(BigDecimal.ONE, orderCreateDto.getAmount());
        assertEquals(BigDecimal.ONE, orderCreateDto.getExchangeRate());
        assertEquals(getMockWalletsAndCommissionsForOrderCreationDto().getCommissionValue(), orderCreateDto.getComission());
        assertEquals(24, orderCreateDto.getComissionId());

        verify(userService, atLeastOnce()).getUserRoleFromDB(anyString());
        verify(orderDao, atLeastOnce()).getWalletAndCommission(
                anyString(),
                any(Currency.class),
                any(OperationType.class),
                any(UserRole.class));
    }

    @Test
    public void prepareNewOrder_operation_type_buy_order_base_type_ico() {
        when(userService.getUserRoleFromDB(anyString())).thenReturn(UserRole.USER);
        when(orderDao.getWalletAndCommission(
                anyString(),
                any(Currency.class),
                any(OperationType.class),
                any(UserRole.class))).thenReturn(getMockWalletsAndCommissionsForOrderCreationDto());

        OrderCreateDto orderCreateDto = orderService.prepareNewOrder(
                new CurrencyPair("BTC/USD"),
                OperationType.BUY,
                USER_EMAIL,
                BigDecimal.ONE,
                BigDecimal.ONE,
                OrderBaseType.ICO);

        assertNotNull(orderCreateDto);
        assertEquals(getMockWalletsAndCommissionsForOrderCreationDto().getUserId(), orderCreateDto.getUserId());
        assertEquals("BTC/USD", orderCreateDto.getCurrencyPair().getName());
        assertEquals(OperationType.BUY, orderCreateDto.getOperationType());
        assertEquals(OrderBaseType.ICO, orderCreateDto.getOrderBaseType());
        assertEquals(BigDecimal.ONE, orderCreateDto.getAmount());
        assertEquals(BigDecimal.ONE, orderCreateDto.getExchangeRate());
        assertEquals(getMockWalletsAndCommissionsForOrderCreationDto().getCommissionValue(), orderCreateDto.getComission());
        assertEquals(24, orderCreateDto.getComissionId());

        verify(userService, atLeastOnce()).getUserRoleFromDB(anyString());
        verify(orderDao, atLeastOnce()).getWalletAndCommission(
                anyString(),
                any(Currency.class),
                any(OperationType.class),
                any(UserRole.class));

        reset(userService);
    }

    @Test
    public void validateOrder_fromDemo_false() {
        when(currencyService.findLimitForRoleByCurrencyPairAndType(anyInt(), any(OperationType.class)))
                .thenReturn(getMockCurrencyPairLimitDto());

        when(exchangeRatesHolder.getOne(anyInt())).thenReturn(new ExOrderStatisticsShortByPairsDto("10"));

        OrderValidationDto orderValidationDto = orderService
                .validateOrder(getMockOrderCreateDto(BigDecimal.TEN), Boolean.FALSE, new User());

        assertNotNull(orderValidationDto);
        assertEquals(4, orderValidationDto.getErrors().size());
        assertTrue(orderValidationDto.getErrors().containsKey("balance_3"));
        assertTrue(orderValidationDto.getErrors().containsValue("validation.orderNotEnoughMoney"));
        assertTrue(orderValidationDto.getErrors().containsKey("amount_2"));
        assertTrue(orderValidationDto.getErrors().containsValue("order.valuerange"));
        assertTrue(orderValidationDto.getErrors().containsKey("amount_1"));
        assertTrue(orderValidationDto.getErrors().containsValue("order.minvalue"));
        assertTrue(orderValidationDto.getErrors().containsKey("amount_0"));
        assertTrue(orderValidationDto.getErrors().containsValue("order.fillfield"));
        assertEquals(2, orderValidationDto.getErrorParams().size());
        assertTrue(orderValidationDto.getErrorParams().containsKey("amount_2"));
        assertArrayEquals(new String[]{"10", "10"}, orderValidationDto.getErrorParams().get("amount_2"));
        assertTrue(orderValidationDto.getErrorParams().containsKey("amount_1"));
        assertArrayEquals(new String[]{"10"}, orderValidationDto.getErrorParams().get("amount_1"));

        verify(currencyService, atLeastOnce()).findLimitForRoleByCurrencyPairAndType(
                anyInt(),
                any(OperationType.class));
    }

    @Test
    public void validateOrder_fromDemo_true() {
        when(currencyService.findLimitForRoleByCurrencyPairAndTypeAndUser(
                anyInt(),
                any(OperationType.class),
                any(User.class))).thenReturn(getMockCurrencyPairLimitDto());

        when(exchangeRatesHolder.getOne(anyInt())).thenReturn(new ExOrderStatisticsShortByPairsDto("0"));

        OrderValidationDto orderValidationDto = orderService
                .validateOrder(getMockOrderCreateDto(BigDecimal.ZERO), Boolean.TRUE, new User());

        assertNotNull(orderValidationDto);
        assertEquals(7, orderValidationDto.getErrors().size());
        assertTrue(orderValidationDto.getErrors().containsKey("exrate_4"));
        assertTrue(orderValidationDto.getErrors().containsValue("order.zerorate"));
        assertTrue(orderValidationDto.getErrors().containsKey("exrate_5"));
        assertTrue(orderValidationDto.getErrors().containsValue("order.minrate"));
        assertTrue(orderValidationDto.getErrors().containsKey("amount_3"));
        assertTrue(orderValidationDto.getErrors().containsValue("order.valuerange"));
        assertTrue(orderValidationDto.getErrors().containsKey("balance_6"));
        assertTrue(orderValidationDto.getErrors().containsValue("validation.orderNotEnoughMoney"));
        assertTrue(orderValidationDto.getErrors().containsKey("amount_2"));
        assertTrue(orderValidationDto.getErrors().containsValue("order.minvalue"));
        assertTrue(orderValidationDto.getErrors().containsKey("exrate_1"));
        assertTrue(orderValidationDto.getErrors().containsValue("order.fillfield"));
        assertTrue(orderValidationDto.getErrors().containsKey("amount_0"));
        assertTrue(orderValidationDto.getErrors().containsValue("order.fillfield"));
        assertEquals(3, orderValidationDto.getErrorParams().size());
        assertTrue(orderValidationDto.getErrorParams().containsKey("exrate_5"));
        assertArrayEquals(new String[]{"10"}, orderValidationDto.getErrorParams().get("exrate_5"));
        assertTrue(orderValidationDto.getErrorParams().containsKey("amount_3"));
        assertArrayEquals(new String[]{"10", "10"}, orderValidationDto.getErrorParams().get("amount_3"));
        assertTrue(orderValidationDto.getErrorParams().containsKey("amount_2"));
        assertArrayEquals(new String[]{"10"}, orderValidationDto.getErrorParams().get("amount_2"));

        verify(currencyService, atLeastOnce()).findLimitForRoleByCurrencyPairAndTypeAndUser(
                anyInt(),
                any(OperationType.class),
                any(User.class));
    }

    @Test
    public void validateOrder_fromDemo_true_OrderBaseType_STOP_LIMIT() {
        OrderCreateDto orderCreateDto = getMockOrderCreateDto(BigDecimal.ZERO, CurrencyPairType.ALL, BigDecimal.ZERO);

        when(currencyService.findLimitForRoleByCurrencyPairAndTypeAndUser(
                anyInt(),
                any(OperationType.class),
                any(User.class))).thenReturn(getMockCurrencyPairLimitDto());

        when(exchangeRatesHolder.getOne(anyInt())).thenReturn(new ExOrderStatisticsShortByPairsDto("0"));

        OrderValidationDto orderValidationDto = orderService.validateOrder(orderCreateDto, Boolean.TRUE, new User());

        assertNotNull(orderValidationDto);
        assertEquals(8, orderValidationDto.getErrors().size());
        assertTrue(orderValidationDto.getErrors().containsKey("stop_2"));
        assertTrue(orderValidationDto.getErrors().containsValue("order.fillfield"));
        assertTrue(orderValidationDto.getErrors().containsKey("exrate_5"));
        assertTrue(orderValidationDto.getErrors().containsValue("order.zerorate"));
        assertTrue(orderValidationDto.getErrors().containsKey("exrate_6"));
        assertTrue(orderValidationDto.getErrors().containsValue("order.minrate"));
        assertTrue(orderValidationDto.getErrors().containsKey("amount_4"));
        assertTrue(orderValidationDto.getErrors().containsValue("order.valuerange"));
        assertTrue(orderValidationDto.getErrors().containsKey("amount_3"));
        assertTrue(orderValidationDto.getErrors().containsValue("order.minvalue"));
        assertTrue(orderValidationDto.getErrors().containsKey("balance_7"));
        assertTrue(orderValidationDto.getErrors().containsValue("validation.orderNotEnoughMoney"));
        assertTrue(orderValidationDto.getErrors().containsKey("exrate_1"));
        assertTrue(orderValidationDto.getErrors().containsValue("order.fillfield"));
        assertTrue(orderValidationDto.getErrors().containsKey("amount_0"));
        assertTrue(orderValidationDto.getErrors().containsValue("order.fillfield"));
        assertEquals(3, orderValidationDto.getErrorParams().size());
        assertTrue(orderValidationDto.getErrorParams().containsKey("exrate_6"));
        assertArrayEquals(new String[]{"10"}, orderValidationDto.getErrorParams().get("exrate_6"));
        assertTrue(orderValidationDto.getErrorParams().containsKey("amount_4"));
        assertArrayEquals(new String[]{"10", "10"}, orderValidationDto.getErrorParams().get("amount_4"));
        assertTrue(orderValidationDto.getErrorParams().containsKey("amount_3"));
        assertArrayEquals(new String[]{"10"}, orderValidationDto.getErrorParams().get("amount_3"));

        verify(currencyService, atLeastOnce()).findLimitForRoleByCurrencyPairAndTypeAndUser(
                anyInt(),
                any(OperationType.class),
                any(User.class));
    }

    @Test
    public void validateOrder_fromDemo_true_OrderBaseType_STOP_LIMIT_min_rate() {
        OrderCreateDto orderCreateDto = getMockOrderCreateDto(
                BigDecimal.ZERO,
                CurrencyPairType.ALL,
                BigDecimal.ZERO,
                BigDecimal.ONE);
        when(exchangeRatesHolder.getOne(anyInt())).thenReturn(new ExOrderStatisticsShortByPairsDto("0"));
        when(currencyService.findLimitForRoleByCurrencyPairAndTypeAndUser(
                anyInt(),
                any(OperationType.class),
                any(User.class))).thenReturn(getMockCurrencyPairLimitDto());

        OrderValidationDto orderValidationDto = orderService.validateOrder(orderCreateDto, Boolean.TRUE, new User());

        assertNotNull(orderValidationDto);
        assertEquals(8, orderValidationDto.getErrors().size());
        assertTrue(orderValidationDto.getErrors().containsKey("stop_2"));
        assertTrue(orderValidationDto.getErrors().containsValue("order.fillfield"));
        assertTrue(orderValidationDto.getErrors().containsKey("exrate_5"));
        assertTrue(orderValidationDto.getErrors().containsValue("order.zerorate"));
        assertTrue(orderValidationDto.getErrors().containsKey("exrate_6"));
        assertTrue(orderValidationDto.getErrors().containsValue("order.minrate"));
        assertTrue(orderValidationDto.getErrors().containsKey("amount_4"));
        assertTrue(orderValidationDto.getErrors().containsValue("order.valuerange"));
        assertTrue(orderValidationDto.getErrors().containsKey("amount_3"));
        assertTrue(orderValidationDto.getErrors().containsValue("order.minvalue"));
        assertTrue(orderValidationDto.getErrors().containsKey("balance_7"));
        assertTrue(orderValidationDto.getErrors().containsValue("validation.orderNotEnoughMoney"));
        assertTrue(orderValidationDto.getErrors().containsKey("exrate_1"));
        assertTrue(orderValidationDto.getErrors().containsValue("order.fillfield"));
        assertTrue(orderValidationDto.getErrors().containsKey("amount_0"));
        assertTrue(orderValidationDto.getErrors().containsValue("order.fillfield"));
        assertEquals(4, orderValidationDto.getErrorParams().size());
        assertTrue(orderValidationDto.getErrorParams().containsKey("stop_2"));
        assertArrayEquals(new BigDecimal[]{BigDecimal.valueOf(10)}, orderValidationDto.getErrorParams().get("stop_2"));
        assertTrue(orderValidationDto.getErrorParams().containsKey("exrate_6"));
        assertArrayEquals(new String[]{"10"}, orderValidationDto.getErrorParams().get("exrate_6"));
        assertTrue(orderValidationDto.getErrorParams().containsKey("amount_4"));
        assertArrayEquals(new String[]{"10", "10"}, orderValidationDto.getErrorParams().get("amount_4"));
        assertTrue(orderValidationDto.getErrorParams().containsKey("amount_3"));
        assertArrayEquals(new String[]{"10"}, orderValidationDto.getErrorParams().get("amount_3"));

        verify(currencyService, atLeastOnce()).findLimitForRoleByCurrencyPairAndTypeAndUser(
                anyInt(),
                any(OperationType.class),
                any(User.class));
    }

    @Test
    public void validateOrder_fromDemo_true_OrderBaseType_STOP_LIMIT_max_rate() {
        OrderCreateDto orderCreateDto = getMockOrderCreateDto(
                BigDecimal.ZERO,
                CurrencyPairType.ALL,
                BigDecimal.ZERO,
                BigDecimal.valueOf(11));

        when(currencyService.findLimitForRoleByCurrencyPairAndTypeAndUser(
                anyInt(),
                any(OperationType.class),
                any(User.class))).thenReturn(getMockCurrencyPairLimitDto());

        when(exchangeRatesHolder.getOne(anyInt())).thenReturn(new ExOrderStatisticsShortByPairsDto("0"));

        OrderValidationDto orderValidationDto = orderService.validateOrder(orderCreateDto, Boolean.TRUE, new User());

        assertNotNull(orderValidationDto);
        assertEquals(8, orderValidationDto.getErrors().size());
        assertTrue(orderValidationDto.getErrors().containsKey("stop_2"));
        assertTrue(orderValidationDto.getErrors().containsValue("order.fillfield"));
        assertTrue(orderValidationDto.getErrors().containsKey("exrate_5"));
        assertTrue(orderValidationDto.getErrors().containsValue("order.zerorate"));
        assertTrue(orderValidationDto.getErrors().containsKey("exrate_6"));
        assertTrue(orderValidationDto.getErrors().containsValue("order.minrate"));
        assertTrue(orderValidationDto.getErrors().containsKey("amount_4"));
        assertTrue(orderValidationDto.getErrors().containsValue("order.valuerange"));
        assertTrue(orderValidationDto.getErrors().containsKey("amount_3"));
        assertTrue(orderValidationDto.getErrors().containsValue("order.minvalue"));
        assertTrue(orderValidationDto.getErrors().containsKey("balance_7"));
        assertTrue(orderValidationDto.getErrors().containsValue("validation.orderNotEnoughMoney"));
        assertTrue(orderValidationDto.getErrors().containsKey("exrate_1"));
        assertTrue(orderValidationDto.getErrors().containsValue("order.fillfield"));
        assertTrue(orderValidationDto.getErrors().containsKey("amount_0"));
        assertTrue(orderValidationDto.getErrors().containsValue("order.fillfield"));
        assertEquals(4, orderValidationDto.getErrorParams().size());
        assertTrue(orderValidationDto.getErrorParams().containsKey("stop_2"));
        assertArrayEquals(new BigDecimal[]{BigDecimal.valueOf(10)}, orderValidationDto.getErrorParams().get("stop_2"));
        assertTrue(orderValidationDto.getErrorParams().containsKey("exrate_6"));
        assertArrayEquals(new String[]{"10"}, orderValidationDto.getErrorParams().get("exrate_6"));
        assertTrue(orderValidationDto.getErrorParams().containsKey("amount_4"));
        assertArrayEquals(new String[]{"10", "10"}, orderValidationDto.getErrorParams().get("amount_4"));
        assertTrue(orderValidationDto.getErrorParams().containsKey("amount_3"));
        assertArrayEquals(new String[]{"10"}, orderValidationDto.getErrorParams().get("amount_3"));

        verify(currencyService, atLeastOnce()).findLimitForRoleByCurrencyPairAndTypeAndUser(
                anyInt(),
                any(OperationType.class),
                any(User.class));
    }

    @Test
    public void validateOrder_fromDemo_true_OrderBaseType_STOP_LIMIT_max_rate_CurrencyPairType_ICO() {
        OrderCreateDto orderCreateDto = getMockOrderCreateDto(
                BigDecimal.ZERO,
                CurrencyPairType.ICO,
                BigDecimal.ZERO,
                BigDecimal.valueOf(11));

        when(currencyService.findLimitForRoleByCurrencyPairAndTypeAndUser(
                anyInt(),
                any(OperationType.class),
                any(User.class))).thenReturn(getMockCurrencyPairLimitDto());

        when(exchangeRatesHolder.getOne(anyInt())).thenReturn(new ExOrderStatisticsShortByPairsDto("0"));

        try {
            orderService.validateOrder(orderCreateDto, Boolean.TRUE, new User());
        } catch (RuntimeException e) {
            assertEquals("unsupported type of order", e.getMessage());
        }

        verify(currencyService, atLeastOnce()).findLimitForRoleByCurrencyPairAndTypeAndUser(
                anyInt(),
                any(OperationType.class),
                any(User.class));
    }

    @Test
    public void validateOrder_fromDemo_true_OrderBaseType_STOP_LIMIT_max_amount() {
        OrderCreateDto orderCreateDto = getMockOrderCreateDto(
                BigDecimal.valueOf(11),
                CurrencyPairType.ALL,
                BigDecimal.ZERO,
                BigDecimal.valueOf(11));

        when(exchangeRatesHolder.getOne(anyInt())).thenReturn(new ExOrderStatisticsShortByPairsDto("0"));

        when(currencyService.findLimitForRoleByCurrencyPairAndTypeAndUser(
                anyInt(),
                any(OperationType.class),
                any(User.class))).thenReturn(getMockCurrencyPairLimitDto());

        OrderValidationDto orderValidationDto = orderService.validateOrder(orderCreateDto, Boolean.TRUE, new User());

        assertNotNull(orderValidationDto);
        assertEquals(7, orderValidationDto.getErrors().size());
        assertTrue(orderValidationDto.getErrors().containsKey("stop_1"));
        assertTrue(orderValidationDto.getErrors().containsValue("order.maxrate"));
        assertTrue(orderValidationDto.getErrors().containsKey("exrate_4"));
        assertTrue(orderValidationDto.getErrors().containsValue("order.zerorate"));
        assertTrue(orderValidationDto.getErrors().containsKey("exrate_5"));
        assertTrue(orderValidationDto.getErrors().containsValue("order.minrate"));
        assertTrue(orderValidationDto.getErrors().containsKey("amount_3"));
        assertTrue(orderValidationDto.getErrors().containsValue("order.valuerange"));
        assertTrue(orderValidationDto.getErrors().containsKey("balance_6"));
        assertTrue(orderValidationDto.getErrors().containsValue("validation.orderNotEnoughMoney"));
        assertTrue(orderValidationDto.getErrors().containsKey("exrate_0"));
        assertTrue(orderValidationDto.getErrors().containsValue("order.fillfield"));
        assertTrue(orderValidationDto.getErrors().containsKey("amount_2"));
        assertTrue(orderValidationDto.getErrors().containsValue("order.maxvalue"));
        assertEquals(4, orderValidationDto.getErrorParams().size());
        assertTrue(orderValidationDto.getErrorParams().containsKey("stop_1"));
        assertArrayEquals(new BigDecimal[]{BigDecimal.valueOf(10)}, orderValidationDto.getErrorParams().get("stop_1"));
        assertTrue(orderValidationDto.getErrorParams().containsKey("exrate_5"));
        assertArrayEquals(new String[]{"10"}, orderValidationDto.getErrorParams().get("exrate_5"));
        assertTrue(orderValidationDto.getErrorParams().containsKey("amount_3"));
        assertArrayEquals(new String[]{"10", "10"}, orderValidationDto.getErrorParams().get("amount_3"));
        assertTrue(orderValidationDto.getErrorParams().containsKey("amount_2"));
        assertArrayEquals(new String[]{"10"}, orderValidationDto.getErrorParams().get("amount_2"));

        verify(currencyService, atLeastOnce()).findLimitForRoleByCurrencyPairAndTypeAndUser(
                anyInt(),
                any(OperationType.class),
                any(User.class));
    }

    @Test
    public void validateOrder_fromDemo_true_ExchangeRate_not_null_max_rate() {
        OrderCreateDto orderCreateDto = getMockOrderCreateDto(
                BigDecimal.valueOf(11),
                CurrencyPairType.ALL,
                BigDecimal.valueOf(11),
                BigDecimal.valueOf(11));

        when(exchangeRatesHolder.getOne(anyInt())).thenReturn(new ExOrderStatisticsShortByPairsDto("11"));

        when(currencyService.findLimitForRoleByCurrencyPairAndTypeAndUser(
                anyInt(),
                any(OperationType.class),
                any(User.class))).thenReturn(getMockCurrencyPairLimitDto());

        OrderValidationDto orderValidationDto = orderService.validateOrder(orderCreateDto, Boolean.TRUE, new User());

        assertNotNull(orderValidationDto);
        assertEquals(5, orderValidationDto.getErrors().size());
        assertTrue(orderValidationDto.getErrors().containsKey("stop_0"));
        assertTrue(orderValidationDto.getErrors().containsValue("order.maxrate"));
        assertTrue(orderValidationDto.getErrors().containsKey("balance_4"));
        assertTrue(orderValidationDto.getErrors().containsValue("validation.orderNotEnoughMoney"));
        assertTrue(orderValidationDto.getErrors().containsKey("amount_2"));
        assertTrue(orderValidationDto.getErrors().containsValue("order.valuerange"));
        assertTrue(orderValidationDto.getErrors().containsKey("amount_1"));
        assertTrue(orderValidationDto.getErrors().containsValue("order.maxvalue"));
        assertTrue(orderValidationDto.getErrors().containsKey("exrate_3"));
        assertTrue(orderValidationDto.getErrors().containsValue("order.maxrate"));
        assertEquals(4, orderValidationDto.getErrorParams().size());
        assertTrue(orderValidationDto.getErrorParams().containsKey("stop_0"));
        assertArrayEquals(new BigDecimal[]{BigDecimal.valueOf(10)}, orderValidationDto.getErrorParams().get("stop_0"));
        assertTrue(orderValidationDto.getErrorParams().containsKey("amount_2"));
        assertArrayEquals(new String[]{"10", "10"}, orderValidationDto.getErrorParams().get("amount_2"));
        assertTrue(orderValidationDto.getErrorParams().containsKey("amount_1"));
        assertArrayEquals(new String[]{"10"}, orderValidationDto.getErrorParams().get("amount_1"));
        assertTrue(orderValidationDto.getErrorParams().containsKey("exrate_3"));
        assertArrayEquals(new String[]{"10"}, orderValidationDto.getErrorParams().get("exrate_3"));

        verify(currencyService, atLeastOnce()).findLimitForRoleByCurrencyPairAndTypeAndUser(
                anyInt(),
                any(OperationType.class),
                any(User.class));
    }

    @Test
    public void validateOrder_has_one_argument() {
        when(currencyService.findLimitForRoleByCurrencyPairAndType(anyInt(), any(OperationType.class)))
                .thenReturn(getMockCurrencyPairLimitDto());
        when(exchangeRatesHolder.getOne(anyInt())).thenReturn(new ExOrderStatisticsShortByPairsDto("0"));
        OrderValidationDto orderValidationDto = orderService.validateOrder(getMockOrderCreateDto(BigDecimal.ZERO));

        assertNotNull(orderValidationDto);
        assertEquals(7, orderValidationDto.getErrors().size());
        assertTrue(orderValidationDto.getErrors().containsKey("exrate_4"));
        assertTrue(orderValidationDto.getErrors().containsValue("order.zerorate"));
        assertTrue(orderValidationDto.getErrors().containsKey("exrate_5"));
        assertTrue(orderValidationDto.getErrors().containsValue("order.minrate"));
        assertTrue(orderValidationDto.getErrors().containsKey("amount_3"));
        assertTrue(orderValidationDto.getErrors().containsValue("order.valuerange"));
        assertTrue(orderValidationDto.getErrors().containsKey("balance_6"));
        assertTrue(orderValidationDto.getErrors().containsValue("validation.orderNotEnoughMoney"));
        assertTrue(orderValidationDto.getErrors().containsKey("amount_2"));
        assertTrue(orderValidationDto.getErrors().containsValue("order.minvalue"));
        assertTrue(orderValidationDto.getErrors().containsKey("exrate_1"));
        assertTrue(orderValidationDto.getErrors().containsValue("order.fillfield"));
        assertTrue(orderValidationDto.getErrors().containsKey("amount_0"));
        assertTrue(orderValidationDto.getErrors().containsValue("order.fillfield"));
        assertEquals(3, orderValidationDto.getErrorParams().size());
        assertTrue(orderValidationDto.getErrorParams().containsKey("exrate_5"));
        assertArrayEquals(new String[]{"10"}, orderValidationDto.getErrorParams().get("exrate_5"));
        assertTrue(orderValidationDto.getErrorParams().containsKey("amount_3"));
        assertArrayEquals(new String[]{"10", "10"}, orderValidationDto.getErrorParams().get("amount_3"));
        assertTrue(orderValidationDto.getErrorParams().containsKey("amount_2"));
        assertArrayEquals(new String[]{"10"}, orderValidationDto.getErrorParams().get("amount_2"));

        verify(currencyService, atLeastOnce()).findLimitForRoleByCurrencyPairAndType(
                anyInt(),
                any(OperationType.class));
    }

    @Test
    public void validateOrder_has_one_argument_OrderBaseType_STOP_LIMIT() {
        OrderCreateDto orderCreateDto = getMockOrderCreateDto(BigDecimal.ZERO, CurrencyPairType.ALL, BigDecimal.ZERO);

        when(currencyService.findLimitForRoleByCurrencyPairAndType(
                anyInt(),
                any(OperationType.class))).thenReturn(getMockCurrencyPairLimitDto());

        when(exchangeRatesHolder.getOne(anyInt())).thenReturn(new ExOrderStatisticsShortByPairsDto("0"));

        OrderValidationDto orderValidationDto = orderService.validateOrder(orderCreateDto);

        assertNotNull(orderValidationDto);
        assertEquals(8, orderValidationDto.getErrors().size());
        assertTrue(orderValidationDto.getErrors().containsKey("stop_2"));
        assertTrue(orderValidationDto.getErrors().containsValue("order.fillfield"));
        assertTrue(orderValidationDto.getErrors().containsKey("exrate_5"));
        assertTrue(orderValidationDto.getErrors().containsValue("order.zerorate"));
        assertTrue(orderValidationDto.getErrors().containsKey("exrate_6"));
        assertTrue(orderValidationDto.getErrors().containsValue("order.minrate"));
        assertTrue(orderValidationDto.getErrors().containsKey("amount_4"));
        assertTrue(orderValidationDto.getErrors().containsValue("order.valuerange"));
        assertTrue(orderValidationDto.getErrors().containsKey("amount_3"));
        assertTrue(orderValidationDto.getErrors().containsValue("order.minvalue"));
        assertTrue(orderValidationDto.getErrors().containsKey("balance_7"));
        assertTrue(orderValidationDto.getErrors().containsValue("validation.orderNotEnoughMoney"));
        assertTrue(orderValidationDto.getErrors().containsKey("exrate_1"));
        assertTrue(orderValidationDto.getErrors().containsValue("order.fillfield"));
        assertTrue(orderValidationDto.getErrors().containsKey("amount_0"));
        assertTrue(orderValidationDto.getErrors().containsValue("order.fillfield"));
        assertEquals(3, orderValidationDto.getErrorParams().size());
        assertTrue(orderValidationDto.getErrorParams().containsKey("exrate_6"));
        assertArrayEquals(new String[]{"10"}, orderValidationDto.getErrorParams().get("exrate_6"));
        assertTrue(orderValidationDto.getErrorParams().containsKey("amount_4"));
        assertArrayEquals(new String[]{"10", "10"}, orderValidationDto.getErrorParams().get("amount_4"));
        assertTrue(orderValidationDto.getErrorParams().containsKey("amount_3"));
        assertArrayEquals(new String[]{"10"}, orderValidationDto.getErrorParams().get("amount_3"));

        verify(currencyService, atLeastOnce()).findLimitForRoleByCurrencyPairAndType(anyInt(), any(OperationType.class));
    }

    @Test
    public void validateOrder_has_one_argument_OrderBaseType_STOP_LIMIT_min_rate() {
        OrderCreateDto orderCreateDto = getMockOrderCreateDto(
                BigDecimal.ZERO,
                CurrencyPairType.ALL,
                BigDecimal.ZERO,
                BigDecimal.ONE);

        when(currencyService.findLimitForRoleByCurrencyPairAndType(
                anyInt(),
                any(OperationType.class))).thenReturn(getMockCurrencyPairLimitDto());

        when(exchangeRatesHolder.getOne(anyInt())).thenReturn(new ExOrderStatisticsShortByPairsDto("0"));

        OrderValidationDto orderValidationDto = orderService.validateOrder(orderCreateDto);

        assertNotNull(orderValidationDto);
        assertEquals(8, orderValidationDto.getErrors().size());
        assertTrue(orderValidationDto.getErrors().containsKey("stop_2"));
        assertTrue(orderValidationDto.getErrors().containsValue("order.fillfield"));
        assertTrue(orderValidationDto.getErrors().containsKey("exrate_5"));
        assertTrue(orderValidationDto.getErrors().containsValue("order.zerorate"));
        assertTrue(orderValidationDto.getErrors().containsKey("exrate_6"));
        assertTrue(orderValidationDto.getErrors().containsValue("order.minrate"));
        assertTrue(orderValidationDto.getErrors().containsKey("amount_4"));
        assertTrue(orderValidationDto.getErrors().containsValue("order.valuerange"));
        assertTrue(orderValidationDto.getErrors().containsKey("amount_3"));
        assertTrue(orderValidationDto.getErrors().containsValue("order.minvalue"));
        assertTrue(orderValidationDto.getErrors().containsKey("balance_7"));
        assertTrue(orderValidationDto.getErrors().containsValue("validation.orderNotEnoughMoney"));
        assertTrue(orderValidationDto.getErrors().containsKey("exrate_1"));
        assertTrue(orderValidationDto.getErrors().containsValue("order.fillfield"));
        assertTrue(orderValidationDto.getErrors().containsKey("amount_0"));
        assertTrue(orderValidationDto.getErrors().containsValue("order.fillfield"));
        assertEquals(4, orderValidationDto.getErrorParams().size());
        assertTrue(orderValidationDto.getErrorParams().containsKey("stop_2"));
        assertArrayEquals(new BigDecimal[]{BigDecimal.valueOf(10)}, orderValidationDto.getErrorParams().get("stop_2"));
        assertTrue(orderValidationDto.getErrorParams().containsKey("exrate_6"));
        assertArrayEquals(new String[]{"10"}, orderValidationDto.getErrorParams().get("exrate_6"));
        assertTrue(orderValidationDto.getErrorParams().containsKey("amount_4"));
        assertArrayEquals(new String[]{"10", "10"}, orderValidationDto.getErrorParams().get("amount_4"));
        assertTrue(orderValidationDto.getErrorParams().containsKey("amount_3"));
        assertArrayEquals(new String[]{"10"}, orderValidationDto.getErrorParams().get("amount_3"));

        verify(currencyService, atLeastOnce()).findLimitForRoleByCurrencyPairAndType(anyInt(), any(OperationType.class));
    }

    @Test
    public void validateOrder_has_one_argument_OrderBaseType_STOP_LIMIT_max_rate() {
        OrderCreateDto orderCreateDto = getMockOrderCreateDto(
                BigDecimal.ZERO,
                CurrencyPairType.ALL,
                BigDecimal.ZERO,
                BigDecimal.valueOf(11));

        when(exchangeRatesHolder.getOne(anyInt())).thenReturn(new ExOrderStatisticsShortByPairsDto("0"));

        when(currencyService.findLimitForRoleByCurrencyPairAndType(
                anyInt(),
                any(OperationType.class))).thenReturn(getMockCurrencyPairLimitDto());

        OrderValidationDto orderValidationDto = orderService.validateOrder(orderCreateDto);

        assertNotNull(orderValidationDto);
        assertEquals(8, orderValidationDto.getErrors().size());
        assertTrue(orderValidationDto.getErrors().containsKey("stop_2"));
        assertTrue(orderValidationDto.getErrors().containsValue("order.fillfield"));
        assertTrue(orderValidationDto.getErrors().containsKey("exrate_5"));
        assertTrue(orderValidationDto.getErrors().containsValue("order.zerorate"));
        assertTrue(orderValidationDto.getErrors().containsKey("exrate_6"));
        assertTrue(orderValidationDto.getErrors().containsValue("order.minrate"));
        assertTrue(orderValidationDto.getErrors().containsKey("amount_4"));
        assertTrue(orderValidationDto.getErrors().containsValue("order.valuerange"));
        assertTrue(orderValidationDto.getErrors().containsKey("amount_3"));
        assertTrue(orderValidationDto.getErrors().containsValue("order.minvalue"));
        assertTrue(orderValidationDto.getErrors().containsKey("balance_7"));
        assertTrue(orderValidationDto.getErrors().containsValue("validation.orderNotEnoughMoney"));
        assertTrue(orderValidationDto.getErrors().containsKey("exrate_1"));
        assertTrue(orderValidationDto.getErrors().containsValue("order.fillfield"));
        assertTrue(orderValidationDto.getErrors().containsKey("amount_0"));
        assertTrue(orderValidationDto.getErrors().containsValue("order.fillfield"));
        assertEquals(4, orderValidationDto.getErrorParams().size());
        assertTrue(orderValidationDto.getErrorParams().containsKey("stop_2"));
        assertArrayEquals(new BigDecimal[]{BigDecimal.valueOf(10)}, orderValidationDto.getErrorParams().get("stop_2"));
        assertTrue(orderValidationDto.getErrorParams().containsKey("exrate_6"));
        assertArrayEquals(new String[]{"10"}, orderValidationDto.getErrorParams().get("exrate_6"));
        assertTrue(orderValidationDto.getErrorParams().containsKey("amount_4"));
        assertArrayEquals(new String[]{"10", "10"}, orderValidationDto.getErrorParams().get("amount_4"));
        assertTrue(orderValidationDto.getErrorParams().containsKey("amount_3"));
        assertArrayEquals(new String[]{"10"}, orderValidationDto.getErrorParams().get("amount_3"));

        verify(currencyService, atLeastOnce()).findLimitForRoleByCurrencyPairAndType(anyInt(), any(OperationType.class));
    }

    @Test
    public void validateOrder_has_one_argument_CurrencyPairType_ICO() {
        OrderCreateDto orderCreateDto = getMockOrderCreateDto(
                BigDecimal.ZERO,
                CurrencyPairType.ICO,
                BigDecimal.ZERO,
                BigDecimal.valueOf(11));

        when(exchangeRatesHolder.getOne(anyInt())).thenReturn(new ExOrderStatisticsShortByPairsDto("0"));

        when(currencyService.findLimitForRoleByCurrencyPairAndType(anyInt(), any(OperationType.class)))
                .thenReturn(getMockCurrencyPairLimitDto());

        try {
            orderService.validateOrder(orderCreateDto);
        } catch (RuntimeException e) {
            assertEquals("unsupported type of order", e.getMessage());
        }

        verify(currencyService, times(1)).findLimitForRoleByCurrencyPairAndType(
                anyInt(),
                any(OperationType.class));
    }

    @Test
    public void validateOrder_has_one_argument_() {
        OrderCreateDto orderCreateDto = getMockOrderCreateDto(
                BigDecimal.valueOf(11),
                CurrencyPairType.ALL,
                BigDecimal.valueOf(11),
                BigDecimal.valueOf(11));

        when(currencyService.findLimitForRoleByCurrencyPairAndType(anyInt(), any(OperationType.class)))
                .thenReturn(getMockCurrencyPairLimitDto());

        when(exchangeRatesHolder.getOne(anyInt())).thenReturn(new ExOrderStatisticsShortByPairsDto("11"));
        OrderValidationDto orderValidationDto = orderService.validateOrder(orderCreateDto);

        assertNotNull(orderValidationDto);
        assertEquals(5, orderValidationDto.getErrors().size());
        assertTrue(orderValidationDto.getErrors().containsKey("stop_0"));
        assertTrue(orderValidationDto.getErrors().containsValue("order.maxrate"));
        assertTrue(orderValidationDto.getErrors().containsKey("balance_4"));
        assertTrue(orderValidationDto.getErrors().containsValue("validation.orderNotEnoughMoney"));
        assertTrue(orderValidationDto.getErrors().containsKey("amount_2"));
        assertTrue(orderValidationDto.getErrors().containsValue("order.valuerange"));
        assertTrue(orderValidationDto.getErrors().containsKey("amount_1"));
        assertTrue(orderValidationDto.getErrors().containsValue("order.maxvalue"));
        assertTrue(orderValidationDto.getErrors().containsKey("exrate_3"));
        assertTrue(orderValidationDto.getErrors().containsValue("order.maxrate"));
        assertEquals(4, orderValidationDto.getErrorParams().size());
        assertTrue(orderValidationDto.getErrorParams().containsKey("stop_0"));
        assertArrayEquals(new BigDecimal[]{BigDecimal.valueOf(10)}, orderValidationDto.getErrorParams().get("stop_0"));
        assertTrue(orderValidationDto.getErrorParams().containsKey("amount_2"));
        assertArrayEquals(new String[]{"10", "10"}, orderValidationDto.getErrorParams().get("amount_2"));
        assertTrue(orderValidationDto.getErrorParams().containsKey("amount_1"));
        assertArrayEquals(new String[]{"10"}, orderValidationDto.getErrorParams().get("amount_1"));
        assertTrue(orderValidationDto.getErrorParams().containsKey("exrate_3"));
        assertArrayEquals(new String[]{"10"}, orderValidationDto.getErrorParams().get("exrate_3"));

        verify(currencyService, atLeastOnce()).findLimitForRoleByCurrencyPairAndType(anyInt(), any(OperationType.class));
    }

    @Test
    public void createOrder_return_minus_one() {
        ExOrder exOrder = getMockExOrder();
        List<ExOrder> eventsList = new ArrayList<>();
        eventsList.add(exOrder);

        when(transactionDescription.get(any(), any(OrderActionEnum.class))).thenReturn("DESCRIPTION");
        when(walletService.ifEnoughMoney(anyInt(), any(BigDecimal.class))).thenReturn(Boolean.TRUE);
        when(orderDao.createOrder(any(ExOrder.class))).thenReturn(-1);
        doNothing().when(eventPublisher).publishEvent(any(ApplicationEvent.class));

        int order = orderService.createOrder(getMockOrderCreateDto(
                BigDecimal.TEN),
                OrderActionEnum.CREATE,
                eventsList,
                Boolean.TRUE);

        assertEquals(-1, order);

        verify(transactionDescription, atLeastOnce()).get(any(), any(OrderActionEnum.class));
        verify(walletService, atLeastOnce()).ifEnoughMoney(anyInt(), any(BigDecimal.class));
        verify(orderDao, atLeastOnce()).createOrder(any(ExOrder.class));
        verify(eventPublisher, atLeastOnce()).publishEvent(any(ApplicationEvent.class));
    }

    @Test
    public void createOrder_orderBaseType_ICO_return_zero() {
        OrderCreateDto mockOrderCreateDto = getMockOrderCreateDto(BigDecimal.TEN);
        mockOrderCreateDto.setOrderBaseType(OrderBaseType.ICO);

        ExOrder exOrder = getMockExOrder();
        List<ExOrder> eventsList = new ArrayList<>();
        eventsList.add(exOrder);

        when(transactionDescription.get(any(), any(OrderActionEnum.class))).thenReturn("DESCRIPTION");
        when(walletService.ifEnoughMoney(anyInt(), any(BigDecimal.class))).thenReturn(Boolean.TRUE);

        int order = orderService.createOrder(mockOrderCreateDto, OrderActionEnum.CREATE, eventsList, Boolean.TRUE);

        assertEquals(0, order);

        verify(transactionDescription, atLeastOnce()).get(any(), any(OrderActionEnum.class));
        verify(walletService, atLeastOnce()).ifEnoughMoney(anyInt(), any(BigDecimal.class));
    }

    @Test
    public void createOrder_orderBaseType_STOP_LIMIT_return_fifteen() {
        OrderCreateDto mockOrderCreateDto = getMockOrderCreateDto(BigDecimal.TEN);
        mockOrderCreateDto.setOrderBaseType(OrderBaseType.STOP_LIMIT);
        mockOrderCreateDto.setTotalWithComission(BigDecimal.ONE);

        ExOrder exOrder = getMockExOrder();
        List<ExOrder> eventsList = new ArrayList<>();
        eventsList.add(exOrder);

        when(transactionDescription.get(any(), any(OrderActionEnum.class))).thenReturn("DESCRIPTION");
        when(walletService.ifEnoughMoney(anyInt(), any(BigDecimal.class))).thenReturn(Boolean.TRUE);
        when(stopOrderService.createOrder(any(ExOrder.class))).thenReturn(15);
        when(walletService.walletInnerTransfer(
                anyInt(),
                any(BigDecimal.class),
                any(TransactionSourceType.class),
                anyInt(),
                anyString())).thenReturn(WalletTransferStatus.SUCCESS);
        doNothing().when(eventPublisher).publishEvent(any(ApplicationEvent.class));

        int order = orderService.createOrder(mockOrderCreateDto, OrderActionEnum.CREATE, eventsList, Boolean.TRUE);

        assertEquals(15, order);

        verify(transactionDescription, atLeastOnce()).get(any(), any(OrderActionEnum.class));
        verify(walletService, atLeastOnce()).ifEnoughMoney(anyInt(), any(BigDecimal.class));
        verify(stopOrderService, atLeastOnce()).createOrder(any(ExOrder.class));
        verify(walletService, atLeastOnce()).walletInnerTransfer(
                anyInt(),
                any(BigDecimal.class),
                any(TransactionSourceType.class),
                anyInt(),
                anyString());
        verify(eventPublisher, atLeastOnce()).publishEvent(any(ApplicationEvent.class));
    }

    @Test
    public void createOrder_orderBaseType_STOP_LIMIT_exception_OrderCreationException() {
        OrderCreateDto mockOrderCreateDto = getMockOrderCreateDto(BigDecimal.TEN);
        mockOrderCreateDto.setOrderBaseType(OrderBaseType.STOP_LIMIT);
        mockOrderCreateDto.setTotalWithComission(BigDecimal.ONE);

        ExOrder exOrder = getMockExOrder();
        List<ExOrder> eventsList = new ArrayList<>();
        eventsList.add(exOrder);

        when(transactionDescription.get(any(), any(OrderActionEnum.class))).thenReturn("DESCRIPTION");
        when(walletService.ifEnoughMoney(anyInt(), any(BigDecimal.class))).thenReturn(Boolean.TRUE);
        when(stopOrderService.createOrder(any(ExOrder.class))).thenReturn(15);
        when(walletService.walletInnerTransfer(
                anyInt(),
                any(BigDecimal.class),
                any(TransactionSourceType.class),
                anyInt(),
                anyString())).thenReturn(WalletTransferStatus.WALLET_NOT_FOUND);

        try {
            orderService.createOrder(mockOrderCreateDto, OrderActionEnum.CREATE, eventsList, Boolean.TRUE);
        } catch (Exception e) {
            assertTrue(e instanceof OrderCreationException);
            assertEquals("WALLET_NOT_FOUND", e.getMessage());
        }
        verify(transactionDescription, atLeastOnce()).get(any(), any(OrderActionEnum.class));
        verify(walletService, atLeastOnce()).ifEnoughMoney(anyInt(), any(BigDecimal.class));
        verify(stopOrderService, atLeastOnce()).createOrder(any(ExOrder.class));
        verify(walletService, atLeastOnce()).walletInnerTransfer(
                anyInt(),
                any(BigDecimal.class),
                any(TransactionSourceType.class),
                anyInt(),
                anyString());
    }

    @Test
    public void createOrder_OperationType_BUY() {
        OrderCreateDto mockOrderCreateDto = getMockOrderCreateDto(BigDecimal.TEN);
        mockOrderCreateDto.setOrderBaseType(OrderBaseType.STOP_LIMIT);
        mockOrderCreateDto.setTotalWithComission(BigDecimal.ONE);
        mockOrderCreateDto.setOperationType(OperationType.SELL);

        ExOrder exOrder = getMockExOrder();
        List<ExOrder> eventsList = new ArrayList<>();
        eventsList.add(exOrder);

        when(transactionDescription.get(any(), any(OrderActionEnum.class))).thenReturn("DESCRIPTION");
        when(walletService.ifEnoughMoney(anyInt(), any(BigDecimal.class))).thenReturn(Boolean.TRUE);
        when(stopOrderService.createOrder(any(ExOrder.class))).thenReturn(15);
        when(walletService.walletInnerTransfer(
                anyInt(),
                any(BigDecimal.class),
                any(TransactionSourceType.class),
                anyInt(),
                anyString())).thenReturn(WalletTransferStatus.SUCCESS);

        try {
            orderService.createOrder(mockOrderCreateDto, OrderActionEnum.CREATE, eventsList, Boolean.FALSE);
        } catch (Exception e) {
            assertTrue(e instanceof OrderCreationException);
            assertEquals("WALLET_NOT_FOUND", e.getMessage());
        }
        verify(transactionDescription, atLeastOnce()).get(any(), any(OrderActionEnum.class));
        verify(walletService, atLeastOnce()).ifEnoughMoney(anyInt(), any(BigDecimal.class));
        verify(stopOrderService, atLeastOnce()).createOrder(any(ExOrder.class));
        verify(walletService, atLeastOnce()).walletInnerTransfer(
                anyInt(),
                any(BigDecimal.class),
                any(TransactionSourceType.class),
                anyInt(),
                anyString());
    }

    @Test
    public void createOrder_ifEnoughMoney_false() {
        OrderCreateDto mockOrderCreateDto = getMockOrderCreateDto(BigDecimal.TEN);
        mockOrderCreateDto.setOrderBaseType(OrderBaseType.STOP_LIMIT);
        mockOrderCreateDto.setTotalWithComission(BigDecimal.ONE);
        mockOrderCreateDto.setOperationType(OperationType.SELL);

        ExOrder exOrder = getMockExOrder();
        List<ExOrder> eventsList = new ArrayList<>();
        eventsList.add(exOrder);

        when(transactionDescription.get(any(), any(OrderActionEnum.class))).thenReturn("DESCRIPTION");
        when(walletService.ifEnoughMoney(anyInt(), any(BigDecimal.class))).thenReturn(Boolean.FALSE);

        try {
            orderService.createOrder(mockOrderCreateDto, OrderActionEnum.CREATE, eventsList, Boolean.FALSE);
        } catch (Exception e) {
            assertTrue(e instanceof NotEnoughUserWalletMoneyException);
            assertEquals(StringUtils.EMPTY, e.getMessage());
        }
        verify(transactionDescription, atLeastOnce()).get(any(), any(OrderActionEnum.class));
        verify(walletService, atLeastOnce()).ifEnoughMoney(anyInt(), any(BigDecimal.class));
    }

    @Test
    public void createOrder() {
        OrderCreateDto mockOrderCreateDto = getMockOrderCreateDto(BigDecimal.TEN);
        mockOrderCreateDto.setOrderBaseType(OrderBaseType.STOP_LIMIT);
        mockOrderCreateDto.setTotalWithComission(BigDecimal.ONE);

        when(transactionDescription.get(any(), any(OrderActionEnum.class))).thenReturn("DESCRIPTION");
        when(walletService.ifEnoughMoney(anyInt(), any(BigDecimal.class))).thenReturn(Boolean.TRUE);
        when(stopOrderService.createOrder(any(ExOrder.class))).thenReturn(15);
        when(walletService.walletInnerTransfer(
                anyInt(),
                any(BigDecimal.class),
                any(TransactionSourceType.class),
                anyInt(),
                anyString())).thenReturn(WalletTransferStatus.SUCCESS);
        doNothing().when(eventPublisher).publishEvent(any(ApplicationEvent.class));

        int order = orderService.createOrder(mockOrderCreateDto, OrderActionEnum.CREATE);

        assertEquals(15, order);

        verify(transactionDescription, atLeastOnce()).get(any(), any(OrderActionEnum.class));
        verify(walletService, atLeastOnce()).ifEnoughMoney(anyInt(), any(BigDecimal.class));
        verify(stopOrderService, atLeastOnce()).createOrder(any(ExOrder.class));
        verify(walletService, atLeastOnce()).walletInnerTransfer(
                anyInt(),
                any(BigDecimal.class),
                any(TransactionSourceType.class),
                anyInt(),
                anyString());
    }

    @Test
    public void createOrderByStopOrder_autoAcceptResult_isPresent() {
        ExOrder exOrder = getMockExOrder();

        OrderCreateDto mockOrderCreateDto = getMockOrderCreateDto(BigDecimal.TEN);
        mockOrderCreateDto.setAmount(BigDecimal.valueOf(11));
        mockOrderCreateDto.setOrderBaseType(OrderBaseType.ICO);

        UserRoleSettings userRoleSettings = new UserRoleSettings();
        userRoleSettings.setUserRole(UserRole.BOT_TRADER);
        userRoleSettings.setBotAcceptionAllowedOnly(Boolean.FALSE);

        WalletsForOrderAcceptionDto walletsForOrderAcceptionDto = new WalletsForOrderAcceptionDto();
        walletsForOrderAcceptionDto.setOrderStatusId(2);

        String descriptionForCreator = "TEST_DESCRIPTION_FOR_CREATION";
        String descriptionForAcceptor = "TEST_DESCRIPTION_FOR_ACCEPTION";

        CurrencyPair cp = new CurrencyPair();
        cp.setName("BTC/USD");
        cp.setCurrency1(new Currency());

        Commission commission = new Commission();
        commission.setValue(BigDecimal.TEN);

        User user = new User();
        user.setEmail(USER_EMAIL);

        when(userRoleService.isOrderAcceptionAllowedForUser(anyInt())).thenReturn(Boolean.TRUE);
        when(userService.getUserRoleFromDB(anyInt())).thenReturn(UserRole.USER);
        when(orderDao.selectTopOrders(
                anyInt(),
                any(BigDecimal.class),
                any(OperationType.class),
                any(Boolean.class),
                anyInt(),
                any(OrderBaseType.class))).thenReturn(Collections.singletonList(exOrder));
        when(orderDao.lockOrdersListForAcception(anyListOf(Integer.class))).thenReturn(Boolean.TRUE);
        when(userService.getUserRoleFromDB(anyInt())).thenReturn(UserRole.BOT_TRADER);
        when(userRoleService.retrieveSettingsForRole(anyInt())).thenReturn(userRoleSettings);
        when(orderDao.getOrderById(anyInt())).thenReturn(exOrder);
        when(walletService.getWalletsForOrderByOrderIdAndBlock(anyInt(), anyInt()))
                .thenReturn(walletsForOrderAcceptionDto);
        when(transactionDescription.get(any(OrderStatus.class), any(OrderActionEnum.class)))
                .thenReturn(descriptionForCreator);
        when(transactionDescription.get(any(OrderStatus.class), any(OrderActionEnum.class)))
                .thenReturn(descriptionForAcceptor);
        when(currencyService.findCurrencyPairById(anyInt())).thenReturn(cp);
        when(commissionDao.getCommission(any(OperationType.class), any(UserRole.class))).thenReturn(commission);
        when(walletService.walletInnerTransfer(
                anyInt(),
                any(BigDecimal.class),
                any(TransactionSourceType.class),
                anyInt(),
                anyString())).thenReturn(WalletTransferStatus.SUCCESS);
        when(walletService.walletBalanceChange(any(WalletOperationData.class))).thenReturn(WalletTransferStatus.SUCCESS);
        doNothing().when(companyWalletService).deposit(
                any(CompanyWallet.class),
                any(BigDecimal.class),
                any(BigDecimal.class));
        doNothing().when(referralService).processReferral(
                any(ExOrder.class),
                any(BigDecimal.class),
                any(Currency.class),
                anyInt());
        when(orderDao.updateOrder(any(ExOrder.class))).thenReturn(Boolean.TRUE);
        doNothing().when(eventPublisher).publishEvent(any(AcceptOrderEvent.class));
        when(userService.getUserById(anyInt())).thenReturn(user);
        doNothing().when(eventPublisher).publishEvent(any(ApplicationEvent.class));

        Integer orderByStopOrder = orderService.createOrderByStopOrder(
                mockOrderCreateDto,
                OrderActionEnum.CREATE,
                Locale.ENGLISH);

        assertNull(orderByStopOrder);

        verify(userRoleService, atLeastOnce()).isOrderAcceptionAllowedForUser(anyInt());
        verify(userService, atLeastOnce()).getUserRoleFromDB(anyInt());
        verify(orderDao, atLeastOnce()).selectTopOrders(
                anyInt(),
                any(BigDecimal.class),
                any(OperationType.class),
                any(Boolean.class),
                anyInt(),
                any(OrderBaseType.class));
        verify(orderDao, atLeastOnce()).lockOrdersListForAcception(anyListOf(Integer.class));
        verify(userService, atLeastOnce()).getUserRoleFromDB(anyInt());
        verify(userRoleService, atLeastOnce()).retrieveSettingsForRole(anyInt());
        verify(orderDao, atLeastOnce()).getOrderById(anyInt());
        verify(walletService, atLeastOnce()).getWalletsForOrderByOrderIdAndBlock(anyInt(), anyInt());
        verify(transactionDescription, atLeastOnce()).get(any(OrderStatus.class), any(OrderActionEnum.class));
        verify(currencyService, atLeastOnce()).findCurrencyPairById(anyInt());
        verify(commissionDao, atLeastOnce()).getCommission(any(OperationType.class), any(UserRole.class));
        verify(walletService, atLeastOnce()).walletInnerTransfer(
                anyInt(),
                any(BigDecimal.class),
                any(TransactionSourceType.class),
                anyInt(),
                anyString());
        verify(walletService, atLeastOnce()).walletBalanceChange(any(WalletOperationData.class));
        verify(companyWalletService, atLeastOnce()).deposit(
                any(CompanyWallet.class),
                any(BigDecimal.class),
                any(BigDecimal.class));
        verify(referralService, atLeastOnce()).processReferral(
                any(ExOrder.class),
                any(BigDecimal.class),
                any(Currency.class),
                anyInt());
        verify(orderDao, atLeastOnce()).updateOrder(any(ExOrder.class));
        verify(eventPublisher, atLeastOnce()).publishEvent(any(AcceptOrderEvent.class));
    }

    @Test
    public void createOrderByStopOrder_exception() {
        OrderCreateDto mockOrderCreateDto = getMockOrderCreateDto(BigDecimal.TEN);
        mockOrderCreateDto.setAmount(BigDecimal.ZERO);
        mockOrderCreateDto.setOrderBaseType(OrderBaseType.ICO);

        when(userRoleService.isOrderAcceptionAllowedForUser(anyInt())).thenReturn(Boolean.TRUE);
        when(userService.getUserRoleFromDB(anyInt())).thenReturn(UserRole.USER);
        when(orderDao.selectTopOrders(
                anyInt(),
                any(BigDecimal.class),
                any(OperationType.class),
                any(Boolean.class),
                anyInt(),
                any(OrderBaseType.class))).thenReturn(Collections.EMPTY_LIST);

        when(transactionDescription.get(any(), any(OrderActionEnum.class))).thenReturn("DESCRIPTION");
        when(walletService.ifEnoughMoney(anyInt(), any(BigDecimal.class))).thenReturn(Boolean.TRUE);
        when(messageSource.getMessage(anyString(), any(), any(Locale.class))).thenReturn("dberror.text");

        try {
            orderService.createOrderByStopOrder(mockOrderCreateDto, OrderActionEnum.CREATE, Locale.ENGLISH);
        } catch (Exception e) {
            assertTrue(e instanceof NotCreatableOrderException);
            assertEquals("dberror.text", e.getMessage());
        }
        verify(userRoleService, atLeastOnce()).isOrderAcceptionAllowedForUser(anyInt());
        verify(userService, atLeastOnce()).getUserRoleFromDB(anyInt());
        verify(orderDao, atLeastOnce()).selectTopOrders(
                anyInt(),
                any(BigDecimal.class),
                any(OperationType.class),
                any(Boolean.class),
                anyInt(),
                any(OrderBaseType.class));
        verify(transactionDescription, atLeastOnce()).get(any(), any(OrderActionEnum.class));
        verify(walletService, atLeastOnce()).ifEnoughMoney(anyInt(), any(BigDecimal.class));
        verify(messageSource, atLeastOnce()).getMessage(anyString(), any(), any(Locale.class));
    }

    @Test
    public void createOrderByStopOrder() {
        OrderCreateDto mockOrderCreateDto = getMockOrderCreateDto(BigDecimal.TEN);
        mockOrderCreateDto.setOrderBaseType(OrderBaseType.STOP_LIMIT);
        mockOrderCreateDto.setTotalWithComission(BigDecimal.ONE);

        when(userRoleService.isOrderAcceptionAllowedForUser(anyInt())).thenReturn(Boolean.TRUE);
        when(userService.getUserRoleFromDB(anyInt())).thenReturn(UserRole.USER);
        when(orderDao.selectTopOrders(
                anyInt(),
                any(BigDecimal.class),
                any(OperationType.class),
                any(Boolean.class),
                anyInt(),
                any(OrderBaseType.class))).thenReturn(Collections.EMPTY_LIST);

        when(transactionDescription.get(any(), any(OrderActionEnum.class))).thenReturn("DESCRIPTION");
        when(walletService.ifEnoughMoney(anyInt(), any(BigDecimal.class))).thenReturn(Boolean.TRUE);
        when(stopOrderService.createOrder(any(ExOrder.class))).thenReturn(15);
        when(walletService.walletInnerTransfer(
                anyInt(),
                any(BigDecimal.class),
                any(TransactionSourceType.class),
                anyInt(),
                anyString())).thenReturn(WalletTransferStatus.SUCCESS);
        doNothing().when(eventPublisher).publishEvent(any(ApplicationEvent.class));

        Integer orderByStopOrder = orderService.createOrderByStopOrder(
                mockOrderCreateDto,
                OrderActionEnum.CREATE,
                Locale.ENGLISH);

        assertNotNull(orderByStopOrder);
        assertEquals(Integer.valueOf(15), orderByStopOrder);

        verify(userRoleService, atLeastOnce()).isOrderAcceptionAllowedForUser(anyInt());
        verify(userService, atLeastOnce()).getUserRoleFromDB(anyInt());
        verify(orderDao, atLeastOnce()).selectTopOrders(
                anyInt(),
                any(BigDecimal.class),
                any(OperationType.class),
                any(Boolean.class),
                anyInt(),
                any(OrderBaseType.class));
        verify(transactionDescription, atLeastOnce()).get(any(), any(OrderActionEnum.class));
        verify(walletService, atLeastOnce()).ifEnoughMoney(anyInt(), any(BigDecimal.class));
        verify(stopOrderService, atLeastOnce()).createOrder(any(ExOrder.class));
        verify(walletService, atLeastOnce()).walletInnerTransfer(
                anyInt(),
                any(BigDecimal.class),
                any(TransactionSourceType.class),
                anyInt(),
                anyString());
        verify(eventPublisher, atLeastOnce()).publishEvent(any(ApplicationEvent.class));
    }


    @Test
    public void createOrder_has_three_arguments_autoAcceptResult_ispresent() {
        Currency currency = new Currency();
        currency.setName("BTC");

        CurrencyPair mockCurrencyPair = getMockCurrencyPair(CurrencyPairType.MAIN);
        mockCurrencyPair.setCurrency1(currency);

        OrderCreateDto mockOrderCreateDto = getMockOrderCreateDto(BigDecimal.TEN);
        mockOrderCreateDto.setAmount(BigDecimal.ZERO);
        mockOrderCreateDto.setOrderBaseType(OrderBaseType.ICO);
        mockOrderCreateDto.setUserId(100);
        mockOrderCreateDto.setCurrencyPair(mockCurrencyPair);

        ExOrder mockExOrder = getMockExOrder();
        mockExOrder.setAmountBase(BigDecimal.valueOf(11));

        OrderDetailDto dto = new OrderDetailDto(
                1,
                7,
                BigDecimal.TEN,
                11,
                15,
                3,
                BigDecimal.TEN,
                18,
                19,
                BigDecimal.ONE);

        User user = new User();
        user.setEmail(USER_EMAIL);

        WalletsForOrderAcceptionDto walletsForOrderAcceptionDto = new WalletsForOrderAcceptionDto();
        walletsForOrderAcceptionDto.setOrderStatusId(2);

        String descriptionForCreator = "TEST_DESCRIPTION_FOR_CREATION";
        String descriptionForAcceptor = "TEST_DESCRIPTION_FOR_ACCEPTION";

        UserRoleSettings userRoleSettings = new UserRoleSettings();
        userRoleSettings.setUserRole(UserRole.BOT_TRADER);
        userRoleSettings.setBotAcceptionAllowedOnly(Boolean.FALSE);

        CurrencyPair cp = new CurrencyPair();
        cp.setName("BTC/USD");
        cp.setCurrency1(new Currency());

        Commission commission = new Commission();
        commission.setValue(BigDecimal.TEN);

        when(userRoleService.isOrderAcceptionAllowedForUser(anyInt())).thenReturn(Boolean.TRUE);
        when(orderDao.selectTopOrders(
                anyInt(),
                any(BigDecimal.class),
                any(OperationType.class),
                anyBoolean(),
                anyInt(),
                any(OrderBaseType.class))).thenReturn(Collections.singletonList(mockExOrder));
        when(userService.getUserRoleFromDB(anyInt())).thenReturn(UserRole.USER);
        when(walletService.getOrderRelatedDataAndBlock(anyInt())).thenReturn(Collections.singletonList(dto));
        when(transactionDescription.get(any(OrderStatus.class), any(OrderActionEnum.class))).thenReturn("DESCRIPTION");
        when(orderDao.setStatus(anyInt(), any(OrderStatus.class))).thenReturn(Boolean.TRUE);
        when(orderDao.getOrderById(anyInt())).thenReturn(mockExOrder);
        when(userService.getUserById(anyInt())).thenReturn(user);
        doNothing().when(eventPublisher).publishEvent(any(ApplicationEvent.class));
        when(userService.getUserRoleFromDB(anyString())).thenReturn(UserRole.USER);
        when(orderDao.getWalletAndCommission(
                anyString(),
                any(Currency.class),
                any(OperationType.class),
                any(UserRole.class))).thenReturn(getMockWalletsAndCommissionsForOrderCreationDto());
        when(transactionDescription.get(any(), any(OrderActionEnum.class))).thenReturn("DESCRIPTION");
        when(walletService.ifEnoughMoney(anyInt(), any(BigDecimal.class))).thenReturn(Boolean.TRUE);
        when(walletService.getWalletsForOrderByOrderIdAndBlock(anyInt(), anyInt())).thenReturn(walletsForOrderAcceptionDto);
        when(transactionDescription.get(any(OrderStatus.class), any(OrderActionEnum.class)))
                .thenReturn(descriptionForCreator);
        when(transactionDescription.get(any(OrderStatus.class), any(OrderActionEnum.class)))
                .thenReturn(descriptionForAcceptor);
        when(orderDao.getOrderById(anyInt())).thenReturn(mockExOrder);
        when(userService.getUserRoleFromDB(anyInt())).thenReturn(UserRole.BOT_TRADER);
        when(userRoleService.retrieveSettingsForRole(anyInt())).thenReturn(userRoleSettings);
        when(userRoleService.isOrderAcceptionAllowedForUser(anyInt())).thenReturn(Boolean.TRUE);
        when(walletService.walletInnerTransfer(
                anyInt(),
                any(BigDecimal.class),
                any(TransactionSourceType.class),
                anyInt(),
                anyString())).thenReturn(WalletTransferStatus.SUCCESS);
        when(walletService.walletBalanceChange(any(WalletOperationData.class))).thenReturn(WalletTransferStatus.SUCCESS);
        when(currencyService.findCurrencyPairById(anyInt())).thenReturn(cp);
        when(commissionDao.getCommission(any(OperationType.class), any(UserRole.class))).thenReturn(commission);
        doNothing().when(companyWalletService).deposit(
                any(CompanyWallet.class),
                any(BigDecimal.class),
                any(BigDecimal.class));
        doNothing().when(referralService).processReferral(
                any(ExOrder.class),
                any(BigDecimal.class),
                any(Currency.class),
                anyInt());
        when(orderDao.updateOrder(any(ExOrder.class))).thenReturn(Boolean.TRUE);
        doNothing().when(eventPublisher).publishEvent(any(ApplicationEvent.class));
        when(messageSource.getMessage(anyString(), any(), any(Locale.class))).thenReturn("orders.partialAccept.success");

        String order = orderService.createOrder(mockOrderCreateDto, OrderActionEnum.CREATE, Locale.ENGLISH);

        assertNotNull(order);
        assertEquals("{\"result\":\"orders.partialAccept.success\"}", order);

        verify(userRoleService, atLeastOnce()).isOrderAcceptionAllowedForUser(anyInt());
        verify(orderDao, atLeastOnce()).selectTopOrders(
                anyInt(),
                any(BigDecimal.class),
                any(OperationType.class),
                anyBoolean(),
                anyInt(),
                any(OrderBaseType.class));
        verify(userService, atLeastOnce()).getUserRoleFromDB(anyInt());
        verify(walletService, atLeastOnce()).getOrderRelatedDataAndBlock(anyInt());
        verify(transactionDescription, atLeastOnce()).get(any(OrderStatus.class), any(OrderActionEnum.class));
        verify(orderDao, atLeastOnce()).setStatus(anyInt(), any(OrderStatus.class));
        verify(orderDao, atLeastOnce()).getOrderById(anyInt());
        verify(userService, atLeastOnce()).getUserById(anyInt());
        verify(eventPublisher, atLeastOnce()).publishEvent(any(ApplicationEvent.class));
        verify(userService, atLeastOnce()).getUserRoleFromDB(anyString());
        verify(orderDao, atLeastOnce()).getWalletAndCommission(
                anyString(),
                any(Currency.class),
                any(OperationType.class),
                any(UserRole.class));
        verify(transactionDescription, atLeastOnce()).get(any(), any(OrderActionEnum.class));
        verify(walletService, atLeastOnce()).ifEnoughMoney(anyInt(), any(BigDecimal.class));
        verify(walletService, atLeastOnce()).getWalletsForOrderByOrderIdAndBlock(anyInt(), anyInt());
        verify(transactionDescription, atLeastOnce()).get(any(OrderStatus.class), any(OrderActionEnum.class));
        verify(orderDao, atLeastOnce()).getOrderById(anyInt());
        verify(userService, atLeastOnce()).getUserRoleFromDB(anyInt());
        verify(userRoleService, atLeastOnce()).retrieveSettingsForRole(anyInt());
        verify(userRoleService, atLeastOnce()).isOrderAcceptionAllowedForUser(anyInt());
        verify(walletService, atLeastOnce()).walletInnerTransfer(
                anyInt(),
                any(BigDecimal.class),
                any(TransactionSourceType.class),
                anyInt(),
                anyString());
        verify(walletService, atLeastOnce()).walletBalanceChange(any(WalletOperationData.class));
        verify(currencyService, atLeastOnce()).findCurrencyPairById(anyInt());
        verify(commissionDao, atLeastOnce()).getCommission(any(OperationType.class), any(UserRole.class));
        verify(companyWalletService, atLeastOnce()).deposit(
                any(CompanyWallet.class),
                any(BigDecimal.class),
                any(BigDecimal.class));
        verify(referralService, atLeastOnce()).processReferral(
                any(ExOrder.class),
                any(BigDecimal.class),
                any(Currency.class),
                anyInt());
        verify(orderDao, atLeastOnce()).updateOrder(any(ExOrder.class));
        verify(eventPublisher, atLeastOnce()).publishEvent(any(ApplicationEvent.class));
        verify(messageSource, atLeastOnce()).getMessage(anyString(), any(), any(Locale.class));

        reset(userService);
    }

    @Test
    public void createOrder_has_three_arguments_exception() {
        when(userRoleService.isOrderAcceptionAllowedForUser(anyInt())).thenReturn(Boolean.TRUE);
        when(userService.getUserRoleFromDB(anyInt())).thenReturn(UserRole.USER);
        when(orderDao.selectTopOrders(
                anyInt(),
                any(BigDecimal.class),
                any(OperationType.class),
                any(Boolean.class),
                anyInt(),
                any(OrderBaseType.class))).thenReturn(Collections.EMPTY_LIST);
        when(transactionDescription.get(any(), any(OrderActionEnum.class))).thenReturn("DESCRIPTION");
        when(walletService.ifEnoughMoney(anyInt(), any(BigDecimal.class))).thenReturn(Boolean.TRUE);
        when(orderDao.createOrder(any(ExOrder.class))).thenReturn(-1);
        doNothing().when(eventPublisher).publishEvent(any(ApplicationEvent.class));
        when(messageSource.getMessage(anyString(), any(), any(Locale.class))).thenReturn("dberror.text");

        try {
            orderService.createOrder(getMockOrderCreateDto(BigDecimal.TEN), OrderActionEnum.CREATE, Locale.ENGLISH);
        } catch (Exception e) {
            assertTrue(e instanceof NotCreatableOrderException);
            assertEquals("dberror.text", e.getMessage());
        }
        verify(userRoleService, atLeastOnce()).isOrderAcceptionAllowedForUser(anyInt());
        verify(userService, atLeastOnce()).getUserRoleFromDB(anyInt());
        verify(orderDao, atLeastOnce()).selectTopOrders(
                anyInt(),
                any(BigDecimal.class),
                any(OperationType.class),
                any(Boolean.class),
                anyInt(),
                any(OrderBaseType.class));
        verify(transactionDescription, atLeastOnce()).get(any(), any(OrderActionEnum.class));
        verify(walletService, atLeastOnce()).ifEnoughMoney(anyInt(), any(BigDecimal.class));
        verify(orderDao, atLeastOnce()).createOrder(any(ExOrder.class));
        verify(eventPublisher, atLeastOnce()).publishEvent(any(ApplicationEvent.class));
        verify(messageSource, atLeastOnce()).getMessage(anyString(), any(), any(Locale.class));
    }

    @Test
    public void createOrder_has_three_arguments() {
        OrderCreateDto mockOrderCreateDto = getMockOrderCreateDto(BigDecimal.TEN);
        mockOrderCreateDto.setOrderBaseType(OrderBaseType.STOP_LIMIT);
        mockOrderCreateDto.setTotalWithComission(BigDecimal.ONE);

        when(userRoleService.isOrderAcceptionAllowedForUser(anyInt())).thenReturn(Boolean.TRUE);
        when(userService.getUserRoleFromDB(anyInt())).thenReturn(UserRole.USER);
        when(orderDao.selectTopOrders(
                anyInt(),
                any(BigDecimal.class),
                any(OperationType.class),
                any(Boolean.class),
                anyInt(),
                any(OrderBaseType.class))).thenReturn(Collections.EMPTY_LIST);
        when(transactionDescription.get(any(), any(OrderActionEnum.class))).thenReturn("DESCRIPTION");
        when(walletService.ifEnoughMoney(anyInt(), any(BigDecimal.class))).thenReturn(Boolean.TRUE);
        when(stopOrderService.createOrder(any(ExOrder.class))).thenReturn(15);
        when(walletService.walletInnerTransfer(
                anyInt(),
                any(BigDecimal.class),
                any(TransactionSourceType.class),
                anyInt(),
                anyString())).thenReturn(WalletTransferStatus.SUCCESS);
        doNothing().when(eventPublisher).publishEvent(any(ApplicationEvent.class));
        when(messageSource.getMessage(anyString(), any(), any(Locale.class))).thenReturn("createdorder.text");

        String order = orderService.createOrder(mockOrderCreateDto, OrderActionEnum.CREATE, Locale.ENGLISH);
        assertNotNull(order);
        assertEquals("{\"result\":\"createdorder.text\"}", order);

        verify(userRoleService, atLeastOnce()).isOrderAcceptionAllowedForUser(anyInt());
        verify(userService, atLeastOnce()).getUserRoleFromDB(anyInt());
        verify(orderDao, atLeastOnce()).selectTopOrders(
                anyInt(),
                any(BigDecimal.class),
                any(OperationType.class),
                any(Boolean.class),
                anyInt(),
                any(OrderBaseType.class));
        verify(transactionDescription, atLeastOnce()).get(any(), any(OrderActionEnum.class));
        verify(walletService, atLeastOnce()).ifEnoughMoney(anyInt(), any(BigDecimal.class));
        verify(stopOrderService, atLeastOnce()).createOrder(any(ExOrder.class));
        verify(walletService, atLeastOnce()).walletInnerTransfer(
                anyInt(),
                any(BigDecimal.class),
                any(TransactionSourceType.class),
                anyInt(),
                anyString());
        verify(eventPublisher, atLeastOnce()).publishEvent(any(ApplicationEvent.class));
        verify(messageSource, atLeastOnce()).getMessage(anyString(), any(), any(Locale.class));
    }

    @Test
    public void postBotOrderToDb() {
        OrderCreateDto orderCreateDto = new OrderCreateDto();
        orderCreateDto.setOrderId(1);
        orderCreateDto.setUserId(100);
        orderCreateDto.setCurrencyPair(getMockCurrencyPair(CurrencyPairType.MAIN));
        orderCreateDto.setOperationType(OperationType.BUY);
        orderCreateDto.setExchangeRate(BigDecimal.TEN);
        orderCreateDto.setAmount(BigDecimal.TEN);
        orderCreateDto.setTotal(BigDecimal.TEN);
        orderCreateDto.setComissionId(2);
        orderCreateDto.setComission(BigDecimal.ONE);
        orderCreateDto.setStatus(OrderStatus.OPENED);
        orderCreateDto.setCurrencyPair(getMockCurrencyPair(CurrencyPairType.MAIN));
        orderCreateDto.setStop(BigDecimal.ONE);
        orderCreateDto.setOrderBaseType(OrderBaseType.ICO);
        orderCreateDto.setTradeId(12L);

        doNothing().when(orderDao).postAcceptedOrderToDB(any(ExOrder.class));
        doNothing().when(eventPublisher).publishEvent(any(AcceptOrderEvent.class));

        orderService.postBotOrderToDb(orderCreateDto);

        verify(orderDao, atLeastOnce()).postAcceptedOrderToDB(any(ExOrder.class));
        verify(eventPublisher, atLeastOnce()).publishEvent(any(AcceptOrderEvent.class));
    }

    @Test
    public void prepareOrderRest_OrderParamsWrongException() {
        OrderCreationParamsDto dto = new OrderCreationParamsDto();
        dto.setCurrencyPairId(1);
        dto.setOrderType(OperationType.BUY);
        dto.setAmount(BigDecimal.TEN);
        dto.setRate(BigDecimal.ONE);
        when(exchangeRatesHolder.getOne(anyInt())).thenReturn(new ExOrderStatisticsShortByPairsDto("0"));
        when(currencyService.findCurrencyPairById(anyInt())).thenReturn(new CurrencyPair("BTC/USD"));
        when(orderDao.getWalletAndCommission(
                anyString(),
                any(Currency.class),
                any(OperationType.class),
                any(UserRole.class))).thenReturn(getMockWalletsAndCommissionsForOrderCreationDto());
        when(currencyService.findLimitForRoleByCurrencyPairAndType(anyInt(), any(OperationType.class)))
                .thenReturn(getMockCurrencyPairLimitDto());
        try {
            orderService.prepareOrderRest(dto, USER_EMAIL, Locale.ENGLISH, OrderBaseType.ICO);
        } catch (Exception e) {
            assertTrue(e instanceof InsufficientCostsInWalletException);
            assertEquals("Failed as user has insufficient funds for this operation!", e.getMessage());
        }
        verify(currencyService, atLeastOnce()).findCurrencyPairById(anyInt());
        verify(orderDao, atLeastOnce()).getWalletAndCommission(
                anyString(),
                any(Currency.class),
                any(OperationType.class),
                any(UserRole.class));
        verify(currencyService, atLeastOnce()).findLimitForRoleByCurrencyPairAndType(anyInt(), any(OperationType.class));
    }

    @Test
    public void prepareOrderRest() {
        OrderCreationParamsDto dto = new OrderCreationParamsDto();
        dto.setCurrencyPairId(1);
        dto.setOrderType(OperationType.BUY);
        dto.setAmount(BigDecimal.ONE);
        dto.setRate(BigDecimal.ONE);

        CurrencyPairLimitDto mockCurrencyPairLimitDto = getMockCurrencyPairLimitDto();
        mockCurrencyPairLimitDto.setMinAmount(BigDecimal.ZERO);
        mockCurrencyPairLimitDto.setMinRate(BigDecimal.ZERO);
        when(exchangeRatesHolder.getOne(anyInt())).thenReturn(new ExOrderStatisticsShortByPairsDto("1"));
        when(currencyService.findCurrencyPairById(anyInt())).thenReturn(new CurrencyPair("BTC/USD"));
        when(orderDao.getWalletAndCommission(
                anyString(),
                any(Currency.class),
                any(OperationType.class),
                any(UserRole.class))).thenReturn(getMockWalletsAndCommissionsForOrderCreationDto());
        when(currencyService.findLimitForRoleByCurrencyPairAndType(anyInt(), any(OperationType.class)))
                .thenReturn(mockCurrencyPairLimitDto);

        OrderCreateDto orderCreateDto = orderService.prepareOrderRest(
                dto,
                USER_EMAIL,
                Locale.ENGLISH,
                OrderBaseType.ICO);

        assertNotNull(orderCreateDto);
        assertEquals(OrderBaseType.ICO, orderCreateDto.getOrderBaseType());
        assertEquals(dto.getOrderType(), orderCreateDto.getOperationType());
        assertEquals(dto.getAmount(), orderCreateDto.getAmount());
        assertEquals(dto.getRate(), orderCreateDto.getExchangeRate());

        verify(currencyService, atLeastOnce()).findCurrencyPairById(anyInt());
        verify(orderDao, atLeastOnce()).getWalletAndCommission(
                anyString(),
                any(Currency.class),
                any(OperationType.class),
                any(UserRole.class));
        verify(currencyService, atLeastOnce()).findLimitForRoleByCurrencyPairAndType(anyInt(), any(OperationType.class));
    }

    @Test
    public void createPreparedOrderRest_autoAcceptResult_ispresent() {
        Currency currency = new Currency();
        currency.setName("BTC");

        CurrencyPair mockCurrencyPair = getMockCurrencyPair(CurrencyPairType.MAIN);
        mockCurrencyPair.setCurrency1(currency);

        OrderCreateDto mockOrderCreateDto = getMockOrderCreateDto(BigDecimal.TEN);
        mockOrderCreateDto.setAmount(BigDecimal.ZERO);
        mockOrderCreateDto.setOrderBaseType(OrderBaseType.ICO);
        mockOrderCreateDto.setUserId(100);
        mockOrderCreateDto.setCurrencyPair(mockCurrencyPair);

        ExOrder mockExOrder = getMockExOrder();
        mockExOrder.setAmountBase(BigDecimal.valueOf(11));

        OrderDetailDto dto = new OrderDetailDto(
                1,
                7,
                BigDecimal.TEN,
                11,
                15,
                3,
                BigDecimal.TEN,
                18,
                19,
                BigDecimal.ONE);

        User user = new User();
        user.setEmail(USER_EMAIL);

        WalletsForOrderAcceptionDto walletsForOrderAcceptionDto = new WalletsForOrderAcceptionDto();
        walletsForOrderAcceptionDto.setOrderStatusId(2);

        String descriptionForCreator = "TEST_DESCRIPTION_FOR_CREATION";
        String descriptionForAcceptor = "TEST_DESCRIPTION_FOR_ACCEPTION";

        UserRoleSettings userRoleSettings = new UserRoleSettings();
        userRoleSettings.setUserRole(UserRole.BOT_TRADER);
        userRoleSettings.setBotAcceptionAllowedOnly(Boolean.FALSE);

        CurrencyPair cp = new CurrencyPair();
        cp.setName("BTC/USD");
        cp.setCurrency1(new Currency());

        Commission commission = new Commission();
        commission.setValue(BigDecimal.TEN);

        when(userRoleService.isOrderAcceptionAllowedForUser(anyInt())).thenReturn(Boolean.TRUE);
        when(orderDao.selectTopOrders(
                anyInt(),
                any(BigDecimal.class),
                any(OperationType.class),
                anyBoolean(),
                anyInt(),
                any(OrderBaseType.class))).thenReturn(Collections.singletonList(mockExOrder));
        when(userService.getUserRoleFromDB(anyInt())).thenReturn(UserRole.USER);
        when(walletService.getOrderRelatedDataAndBlock(anyInt())).thenReturn(Collections.singletonList(dto));
        when(transactionDescription.get(any(OrderStatus.class), any(OrderActionEnum.class))).thenReturn("DESCRIPTION");
        when(orderDao.setStatus(anyInt(), any(OrderStatus.class))).thenReturn(Boolean.TRUE);
        when(orderDao.getOrderById(anyInt())).thenReturn(mockExOrder);
        when(userService.getUserById(anyInt())).thenReturn(user);
        doNothing().when(eventPublisher).publishEvent(any(ApplicationEvent.class));
        when(orderDao.getWalletAndCommission(
                anyString(),
                any(Currency.class),
                any(OperationType.class),
                any(UserRole.class))).thenReturn(getMockWalletsAndCommissionsForOrderCreationDto());
        when(transactionDescription.get(any(), any(OrderActionEnum.class))).thenReturn("DESCRIPTION");
        when(walletService.ifEnoughMoney(anyInt(), any(BigDecimal.class))).thenReturn(Boolean.TRUE);
        when(walletService.getWalletsForOrderByOrderIdAndBlock(anyInt(), anyInt()))
                .thenReturn(walletsForOrderAcceptionDto);
        when(transactionDescription.get(any(OrderStatus.class), any(OrderActionEnum.class)))
                .thenReturn(descriptionForCreator);
        when(transactionDescription.get(any(OrderStatus.class), any(OrderActionEnum.class)))
                .thenReturn(descriptionForAcceptor);
        when(orderDao.getOrderById(anyInt())).thenReturn(mockExOrder);
        when(userService.getUserRoleFromDB(anyInt())).thenReturn(UserRole.BOT_TRADER);
        when(userRoleService.retrieveSettingsForRole(anyInt())).thenReturn(userRoleSettings);
        when(userRoleService.isOrderAcceptionAllowedForUser(anyInt())).thenReturn(Boolean.TRUE);
        when(walletService.walletInnerTransfer(
                anyInt(),
                any(BigDecimal.class),
                any(TransactionSourceType.class),
                anyInt(),
                anyString())).thenReturn(WalletTransferStatus.SUCCESS);
        when(walletService.walletBalanceChange(any(WalletOperationData.class))).thenReturn(WalletTransferStatus.SUCCESS);
        when(currencyService.findCurrencyPairById(anyInt())).thenReturn(cp);
        when(commissionDao.getCommission(any(OperationType.class), any(UserRole.class))).thenReturn(commission);
        doNothing().when(companyWalletService).deposit(
                any(CompanyWallet.class),
                any(BigDecimal.class),
                any(BigDecimal.class));
        doNothing().when(referralService).processReferral(
                any(ExOrder.class),
                any(BigDecimal.class),
                any(Currency.class),
                anyInt());
        when(orderDao.updateOrder(any(ExOrder.class))).thenReturn(Boolean.TRUE);
        doNothing().when(eventPublisher).publishEvent(any(ApplicationEvent.class));

        OrderCreationResultDto preparedOrderRest = orderService
                .createPreparedOrderRest(mockOrderCreateDto, Locale.ENGLISH);

        assertNotNull(preparedOrderRest);
        assertEquals(BigDecimal.valueOf(11), preparedOrderRest.getPartiallyAcceptedOrderFullAmount());

        verify(userRoleService, atLeastOnce()).isOrderAcceptionAllowedForUser(anyInt());
        verify(orderDao, atLeastOnce()).selectTopOrders(
                anyInt(),
                any(BigDecimal.class),
                any(OperationType.class),
                anyBoolean(),
                anyInt(),
                any(OrderBaseType.class));
        verify(userService, atLeastOnce()).getUserRoleFromDB(anyInt());
        verify(walletService, atLeastOnce()).getOrderRelatedDataAndBlock(anyInt());
        verify(transactionDescription, atLeastOnce()).get(any(OrderStatus.class), any(OrderActionEnum.class));
        verify(orderDao, atLeastOnce()).setStatus(anyInt(), any(OrderStatus.class));
        verify(orderDao, atLeastOnce()).getOrderById(anyInt());
        verify(userService, atLeastOnce()).getUserById(anyInt());
        verify(eventPublisher, atLeastOnce()).publishEvent(any(ApplicationEvent.class));
        verify(orderDao, atLeastOnce()).getWalletAndCommission(
                anyString(),
                any(Currency.class),
                any(OperationType.class),
                any(UserRole.class));
        verify(transactionDescription, atLeastOnce()).get(any(), any(OrderActionEnum.class));
        verify(walletService, atLeastOnce()).ifEnoughMoney(anyInt(), any(BigDecimal.class));
        verify(walletService, atLeastOnce()).getWalletsForOrderByOrderIdAndBlock(anyInt(), anyInt());
        verify(transactionDescription, atLeastOnce()).get(any(OrderStatus.class), any(OrderActionEnum.class));
        verify(orderDao, atLeastOnce()).getOrderById(anyInt());
        verify(userService, atLeastOnce()).getUserRoleFromDB(anyInt());
        verify(userRoleService, atLeastOnce()).retrieveSettingsForRole(anyInt());
        verify(userRoleService, atLeastOnce()).isOrderAcceptionAllowedForUser(anyInt());
        verify(walletService, atLeastOnce()).walletInnerTransfer(
                anyInt(),
                any(BigDecimal.class),
                any(TransactionSourceType.class),
                anyInt(),
                anyString());
        verify(walletService, atLeastOnce()).walletBalanceChange(any(WalletOperationData.class));
        verify(currencyService, atLeastOnce()).findCurrencyPairById(anyInt());
        verify(commissionDao, atLeastOnce()).getCommission(any(OperationType.class), any(UserRole.class));
        verify(companyWalletService, atLeastOnce()).deposit(
                any(CompanyWallet.class),
                any(BigDecimal.class),
                any(BigDecimal.class));
        verify(referralService, atLeastOnce()).processReferral(
                any(ExOrder.class),
                any(BigDecimal.class),
                any(Currency.class),
                anyInt());
        verify(orderDao, atLeastOnce()).updateOrder(any(ExOrder.class));
        verify(eventPublisher, atLeastOnce()).publishEvent(any(ApplicationEvent.class));

        reset(userService);
    }

    @Test
    public void createPreparedOrderRest_exception() {
        OrderCreateDto mockOrderCreateDto = getMockOrderCreateDto(BigDecimal.TEN);
        mockOrderCreateDto.setAmount(BigDecimal.ZERO);
        mockOrderCreateDto.setOrderBaseType(OrderBaseType.ICO);

        when(userRoleService.isOrderAcceptionAllowedForUser(anyInt())).thenReturn(Boolean.TRUE);
        when(userService.getUserRoleFromDB(anyInt())).thenReturn(UserRole.USER);
        when(orderDao.selectTopOrders(
                anyInt(),
                any(BigDecimal.class),
                any(OperationType.class),
                any(Boolean.class),
                anyInt(),
                any(OrderBaseType.class))).thenReturn(Collections.EMPTY_LIST);
        when(transactionDescription.get(any(), any(OrderActionEnum.class))).thenReturn("DESCRIPTION");
        when(walletService.ifEnoughMoney(anyInt(), any(BigDecimal.class))).thenReturn(Boolean.TRUE);
        when(messageSource.getMessage(anyString(), any(), any(Locale.class))).thenReturn("dberror.text");

        try {
            orderService.createPreparedOrderRest(mockOrderCreateDto, Locale.ENGLISH);
        } catch (Exception e) {
            assertTrue(e instanceof NotCreatableOrderException);
            assertEquals("dberror.text", e.getMessage());
        }
        verify(userRoleService, atLeastOnce()).isOrderAcceptionAllowedForUser(anyInt());
        verify(userService, atLeastOnce()).getUserRoleFromDB(anyInt());
        verify(orderDao, atLeastOnce()).selectTopOrders(
                anyInt(),
                any(BigDecimal.class),
                any(OperationType.class),
                any(Boolean.class),
                anyInt(),
                any(OrderBaseType.class));
        verify(transactionDescription, atLeastOnce()).get(any(), any(OrderActionEnum.class));
        verify(walletService, atLeastOnce()).ifEnoughMoney(anyInt(), any(BigDecimal.class));
        verify(messageSource, atLeastOnce()).getMessage(anyString(), any(), any(Locale.class));
    }

    @Test
    public void createPreparedOrderRest() {
        OrderCreateDto mockOrderCreateDto = getMockOrderCreateDto(BigDecimal.TEN);
        mockOrderCreateDto.setOrderBaseType(OrderBaseType.STOP_LIMIT);
        mockOrderCreateDto.setTotalWithComission(BigDecimal.ONE);

        when(userRoleService.isOrderAcceptionAllowedForUser(anyInt())).thenReturn(Boolean.TRUE);
        when(userService.getUserRoleFromDB(anyInt())).thenReturn(UserRole.USER);
        when(orderDao.selectTopOrders(
                anyInt(),
                any(BigDecimal.class),
                any(OperationType.class),
                any(Boolean.class),
                anyInt(),
                any(OrderBaseType.class))).thenReturn(Collections.EMPTY_LIST);
        when(transactionDescription.get(any(), any(OrderActionEnum.class))).thenReturn("DESCRIPTION");
        when(walletService.ifEnoughMoney(anyInt(), any(BigDecimal.class))).thenReturn(Boolean.TRUE);
        when(stopOrderService.createOrder(any(ExOrder.class))).thenReturn(15);
        when(walletService.walletInnerTransfer(
                anyInt(),
                any(BigDecimal.class),
                any(TransactionSourceType.class),
                anyInt(),
                anyString())).thenReturn(WalletTransferStatus.SUCCESS);
        doNothing().when(eventPublisher).publishEvent(any(ApplicationEvent.class));

        OrderCreationResultDto preparedOrderRest = orderService
                .createPreparedOrderRest(mockOrderCreateDto, Locale.ENGLISH);

        assertNotNull(preparedOrderRest);
        assertEquals(Integer.valueOf(15), preparedOrderRest.getCreatedOrderId());

        verify(userRoleService, atLeastOnce()).isOrderAcceptionAllowedForUser(anyInt());
        verify(userService, atLeastOnce()).getUserRoleFromDB(anyInt());
        verify(orderDao, atLeastOnce()).selectTopOrders(
                anyInt(),
                any(BigDecimal.class),
                any(OperationType.class),
                any(Boolean.class),
                anyInt(),
                any(OrderBaseType.class));
        verify(transactionDescription, atLeastOnce()).get(any(), any(OrderActionEnum.class));
        verify(walletService, atLeastOnce()).ifEnoughMoney(anyInt(), any(BigDecimal.class));
        verify(stopOrderService, atLeastOnce()).createOrder(any(ExOrder.class));
        verify(walletService, atLeastOnce()).walletInnerTransfer(
                anyInt(),
                any(BigDecimal.class),
                any(TransactionSourceType.class),
                anyInt(),
                anyString());
        verify(eventPublisher, atLeastOnce()).publishEvent(any(ApplicationEvent.class));
    }

    @Test
    public void prepareAndCreateOrderRest_NotCreatableOrderException() {
        when(userService.getUserLocaleForMobile(anyString())).thenReturn(Locale.ENGLISH);
        when(currencyService.getCurrencyPairByName(anyString())).thenReturn(getMockCurrencyPair(CurrencyPairType.ICO));

        try {
            orderService.prepareAndCreateOrderRest(
                    "BTC/USD",
                    OperationType.BUY,
                    BigDecimal.ONE,
                    BigDecimal.TEN,
                    USER_EMAIL);
        } catch (Exception e) {
            assertTrue(e instanceof NotCreatableOrderException);
            assertEquals("This pair available only through website", e.getMessage());
        }
        verify(userService, atLeastOnce()).getUserLocaleForMobile(anyString());
        verify(currencyService, atLeastOnce()).getCurrencyPairByName(anyString());
    }

    @Test
    public void autoAccept_optional_empty() {
        when(userRoleService.isOrderAcceptionAllowedForUser(anyInt())).thenReturn(Boolean.TRUE);
        when(userService.getUserRoleFromDB(anyInt())).thenReturn(UserRole.USER);
        when(orderDao.selectTopOrders(
                anyInt(),
                any(BigDecimal.class),
                any(OperationType.class),
                any(Boolean.class),
                anyInt(),
                any(OrderBaseType.class))).thenReturn(Collections.EMPTY_LIST);

        Optional<String> autoAccept = orderService.autoAccept(getMockOrderCreateDto(BigDecimal.TEN), Locale.ENGLISH);

        assertNotNull(autoAccept);
        assertEquals(Optional.empty(), autoAccept);

        verify(userRoleService, atLeastOnce()).isOrderAcceptionAllowedForUser(anyInt());
        verify(userService, atLeastOnce()).getUserRoleFromDB(anyInt());
        verify(orderDao, atLeastOnce()).selectTopOrders(
                anyInt(),
                any(BigDecimal.class),
                any(OperationType.class),
                any(Boolean.class),
                anyInt(),
                any(OrderBaseType.class));
    }

    @Test
    public void autoAccept_order_acceptsuccess() {
        ExOrder exOrder = getMockExOrder();

        OrderCreateDto mockOrderCreateDto = getMockOrderCreateDto(BigDecimal.TEN);
        mockOrderCreateDto.setAmount(BigDecimal.valueOf(11));
        mockOrderCreateDto.setOrderBaseType(OrderBaseType.ICO);

        UserRoleSettings userRoleSettings = new UserRoleSettings();
        userRoleSettings.setUserRole(UserRole.BOT_TRADER);
        userRoleSettings.setBotAcceptionAllowedOnly(Boolean.FALSE);

        WalletsForOrderAcceptionDto walletsForOrderAcceptionDto = new WalletsForOrderAcceptionDto();
        walletsForOrderAcceptionDto.setOrderStatusId(2);

        String descriptionForCreator = "TEST_DESCRIPTION_FOR_CREATION";
        String descriptionForAcceptor = "TEST_DESCRIPTION_FOR_ACCEPTION";

        CurrencyPair cp = new CurrencyPair();
        cp.setName("BTC/USD");
        cp.setCurrency1(new Currency());

        Commission commission = new Commission();
        commission.setValue(BigDecimal.TEN);

        User user = new User();
        user.setEmail(USER_EMAIL);

        when(userRoleService.isOrderAcceptionAllowedForUser(anyInt())).thenReturn(Boolean.TRUE);
        when(userService.getUserRoleFromDB(anyInt())).thenReturn(UserRole.USER);
        when(orderDao.selectTopOrders(
                anyInt(),
                any(BigDecimal.class),
                any(OperationType.class),
                any(Boolean.class),
                anyInt(),
                any(OrderBaseType.class))
        ).thenReturn(Collections.singletonList(exOrder));
        when(orderDao.lockOrdersListForAcception(anyListOf(Integer.class))).thenReturn(Boolean.TRUE);
        when(userService.getUserRoleFromDB(anyInt())).thenReturn(UserRole.BOT_TRADER);
        when(userRoleService.retrieveSettingsForRole(anyInt())).thenReturn(userRoleSettings);
        when(orderDao.getOrderById(anyInt())).thenReturn(exOrder);
        when(walletService.getWalletsForOrderByOrderIdAndBlock(anyInt(), anyInt()))
                .thenReturn(walletsForOrderAcceptionDto);
        when(transactionDescription.get(any(OrderStatus.class), any(OrderActionEnum.class)))
                .thenReturn(descriptionForCreator);
        when(transactionDescription.get(any(OrderStatus.class), any(OrderActionEnum.class)))
                .thenReturn(descriptionForAcceptor);
        when(currencyService.findCurrencyPairById(anyInt())).thenReturn(cp);
        when(commissionDao.getCommission(any(OperationType.class), any(UserRole.class))).thenReturn(commission);
        when(walletService.walletInnerTransfer(
                anyInt(),
                any(BigDecimal.class),
                any(TransactionSourceType.class),
                anyInt(),
                anyString())
        ).thenReturn(WalletTransferStatus.SUCCESS);
        when(walletService.walletBalanceChange(any(WalletOperationData.class))).thenReturn(WalletTransferStatus.SUCCESS);
        doNothing().when(companyWalletService).deposit(
                any(CompanyWallet.class),
                any(BigDecimal.class),
                any(BigDecimal.class));
        doNothing().when(referralService).processReferral(
                any(ExOrder.class),
                any(BigDecimal.class),
                any(Currency.class),
                anyInt());
        when(orderDao.updateOrder(any(ExOrder.class))).thenReturn(Boolean.TRUE);
        doNothing().when(eventPublisher).publishEvent(any(AcceptOrderEvent.class));
        when(userService.getUserById(anyInt())).thenReturn(user);
        doNothing().when(eventPublisher).publishEvent(any(ApplicationEvent.class));
        when(messageSource.getMessage(anyString(), any(), any(Locale.class))).thenReturn("order.acceptsuccess");

        Optional<String> autoAccept = orderService.autoAccept(mockOrderCreateDto, Locale.ENGLISH);

        assertNotNull(autoAccept);
        assertEquals("{\"result\":\"order.acceptsuccess; \"}", autoAccept.get());

        verify(userRoleService, atLeastOnce()).isOrderAcceptionAllowedForUser(anyInt());
        verify(userService, atLeastOnce()).getUserRoleFromDB(anyInt());
        verify(orderDao, atLeastOnce()).selectTopOrders(
                anyInt(),
                any(BigDecimal.class),
                any(OperationType.class),
                any(Boolean.class),
                anyInt(),
                any(OrderBaseType.class));
        verify(orderDao, atLeastOnce()).lockOrdersListForAcception(anyListOf(Integer.class));
        verify(userService, atLeastOnce()).getUserRoleFromDB(anyInt());
        verify(userRoleService, atLeastOnce()).retrieveSettingsForRole(anyInt());
        verify(orderDao, atLeastOnce()).getOrderById(anyInt());
        verify(walletService, atLeastOnce()).getWalletsForOrderByOrderIdAndBlock(anyInt(), anyInt());
        verify(transactionDescription, atLeastOnce()).get(any(OrderStatus.class), any(OrderActionEnum.class));
        verify(currencyService, atLeastOnce()).findCurrencyPairById(anyInt());
        verify(commissionDao, atLeastOnce()).getCommission(any(OperationType.class), any(UserRole.class));
        verify(walletService, atLeastOnce()).walletInnerTransfer(
                anyInt(),
                any(BigDecimal.class),
                any(TransactionSourceType.class),
                anyInt(),
                anyString());
        verify(walletService, atLeastOnce()).walletBalanceChange(any(WalletOperationData.class));
        verify(companyWalletService, atLeastOnce()).deposit(
                any(CompanyWallet.class),
                any(BigDecimal.class),
                any(BigDecimal.class));
        verify(referralService, atLeastOnce()).processReferral(
                any(ExOrder.class),
                any(BigDecimal.class),
                any(Currency.class),
                anyInt());
        verify(orderDao, atLeastOnce()).updateOrder(any(ExOrder.class));
        verify(eventPublisher, atLeastOnce()).publishEvent(any(AcceptOrderEvent.class));
    }

    @Test
    public void autoAccept_orders_partialAccept_success() {
        Currency currency = new Currency();
        currency.setName("BTC");

        CurrencyPair mockCurrencyPair = getMockCurrencyPair(CurrencyPairType.MAIN);
        mockCurrencyPair.setCurrency1(currency);

        OrderCreateDto mockOrderCreateDto = getMockOrderCreateDto(BigDecimal.TEN);
        mockOrderCreateDto.setAmount(BigDecimal.ZERO);
        mockOrderCreateDto.setOrderBaseType(OrderBaseType.ICO);
        mockOrderCreateDto.setUserId(100);
        mockOrderCreateDto.setCurrencyPair(mockCurrencyPair);

        ExOrder mockExOrder = getMockExOrder();
        mockExOrder.setAmountBase(BigDecimal.valueOf(11));

        OrderDetailDto dto = new OrderDetailDto(
                1,
                7,
                BigDecimal.TEN,
                11,
                15,
                3,
                BigDecimal.TEN,
                18,
                19,
                BigDecimal.ONE);

        User user = new User();
        user.setEmail(USER_EMAIL);

        WalletsForOrderAcceptionDto walletsForOrderAcceptionDto = new WalletsForOrderAcceptionDto();
        walletsForOrderAcceptionDto.setOrderStatusId(2);

        String descriptionForCreator = "TEST_DESCRIPTION_FOR_CREATION";
        String descriptionForAcceptor = "TEST_DESCRIPTION_FOR_ACCEPTION";

        UserRoleSettings userRoleSettings = new UserRoleSettings();
        userRoleSettings.setUserRole(UserRole.BOT_TRADER);
        userRoleSettings.setBotAcceptionAllowedOnly(Boolean.FALSE);

        CurrencyPair cp = new CurrencyPair();
        cp.setName("BTC/USD");
        cp.setCurrency1(new Currency());

        Commission commission = new Commission();
        commission.setValue(BigDecimal.TEN);

        when(userRoleService.isOrderAcceptionAllowedForUser(anyInt())).thenReturn(Boolean.TRUE);
        when(orderDao.selectTopOrders(
                anyInt(),
                any(BigDecimal.class),
                any(OperationType.class),
                anyBoolean(),
                anyInt(),
                any(OrderBaseType.class))).thenReturn(Collections.singletonList(mockExOrder));
        when(userService.getUserRoleFromDB(anyInt())).thenReturn(UserRole.USER);
        when(walletService.getOrderRelatedDataAndBlock(anyInt())).thenReturn(Collections.singletonList(dto));
        when(transactionDescription.get(any(OrderStatus.class), any(OrderActionEnum.class))).thenReturn("DESCRIPTION");
        when(orderDao.setStatus(anyInt(), any(OrderStatus.class))).thenReturn(Boolean.TRUE);
        when(orderDao.getOrderById(anyInt())).thenReturn(mockExOrder);
        when(userService.getUserById(anyInt())).thenReturn(user);
        doNothing().when(eventPublisher).publishEvent(any(ApplicationEvent.class));
        when(orderDao.getWalletAndCommission(
                anyString(),
                any(Currency.class),
                any(OperationType.class),
                any(UserRole.class))).thenReturn(getMockWalletsAndCommissionsForOrderCreationDto());
        when(transactionDescription.get(any(), any(OrderActionEnum.class))).thenReturn("DESCRIPTION");
        when(walletService.ifEnoughMoney(anyInt(), any(BigDecimal.class))).thenReturn(Boolean.TRUE);
        when(walletService.getWalletsForOrderByOrderIdAndBlock(anyInt(), anyInt()))
                .thenReturn(walletsForOrderAcceptionDto);
        when(transactionDescription.get(any(OrderStatus.class), any(OrderActionEnum.class)))
                .thenReturn(descriptionForCreator);
        when(transactionDescription.get(any(OrderStatus.class), any(OrderActionEnum.class)))
                .thenReturn(descriptionForAcceptor);
        when(orderDao.getOrderById(anyInt())).thenReturn(mockExOrder);
        when(userService.getUserRoleFromDB(anyInt())).thenReturn(UserRole.BOT_TRADER);
        when(userRoleService.retrieveSettingsForRole(anyInt())).thenReturn(userRoleSettings);
        when(userRoleService.isOrderAcceptionAllowedForUser(anyInt())).thenReturn(Boolean.TRUE);
        when(walletService.walletInnerTransfer(
                anyInt(),
                any(BigDecimal.class),
                any(TransactionSourceType.class),
                anyInt(),
                anyString())).thenReturn(WalletTransferStatus.SUCCESS);
        when(walletService.walletBalanceChange(any(WalletOperationData.class))).thenReturn(WalletTransferStatus.SUCCESS);
        when(currencyService.findCurrencyPairById(anyInt())).thenReturn(cp);
        when(commissionDao.getCommission(any(OperationType.class), any(UserRole.class))).thenReturn(commission);
        doNothing().when(companyWalletService).deposit(
                any(CompanyWallet.class),
                any(BigDecimal.class),
                any(BigDecimal.class));
        doNothing().when(referralService).processReferral(
                any(ExOrder.class),
                any(BigDecimal.class),
                any(Currency.class),
                anyInt());
        when(orderDao.updateOrder(any(ExOrder.class))).thenReturn(Boolean.TRUE);
        doNothing().when(eventPublisher).publishEvent(any(ApplicationEvent.class));
        when(messageSource.getMessage(anyString(), any(), any(Locale.class))).thenReturn("orders.partialAccept.success");

        Optional<String> autoAccept = orderService.autoAccept(mockOrderCreateDto, Locale.ENGLISH);

        assertNotNull(autoAccept);
        assertEquals("{\"result\":\"orders.partialAccept.success\"}", autoAccept.get());

        verify(userRoleService, atLeastOnce()).isOrderAcceptionAllowedForUser(anyInt());
        verify(orderDao, atLeastOnce()).selectTopOrders(
                anyInt(),
                any(BigDecimal.class),
                any(OperationType.class),
                anyBoolean(),
                anyInt(),
                any(OrderBaseType.class));
        verify(userService, atLeastOnce()).getUserRoleFromDB(anyInt());
        verify(walletService, atLeastOnce()).getOrderRelatedDataAndBlock(anyInt());
        verify(transactionDescription, atLeastOnce()).get(any(OrderStatus.class), any(OrderActionEnum.class));
        verify(orderDao, atLeastOnce()).setStatus(anyInt(), any(OrderStatus.class));
        verify(orderDao, atLeastOnce()).getOrderById(anyInt());
        verify(userService, atLeastOnce()).getUserById(anyInt());
        verify(eventPublisher, atLeastOnce()).publishEvent(any(ApplicationEvent.class));
        verify(orderDao, atLeastOnce()).getWalletAndCommission(
                anyString(),
                any(Currency.class),
                any(OperationType.class),
                any(UserRole.class));
        verify(transactionDescription, atLeastOnce()).get(any(), any(OrderActionEnum.class));
        verify(walletService, atLeastOnce()).ifEnoughMoney(anyInt(), any(BigDecimal.class));
        verify(walletService, atLeastOnce()).getWalletsForOrderByOrderIdAndBlock(anyInt(), anyInt());
        verify(transactionDescription, atLeastOnce()).get(any(OrderStatus.class), any(OrderActionEnum.class));
        verify(orderDao, atLeastOnce()).getOrderById(anyInt());
        verify(userRoleService, atLeastOnce()).retrieveSettingsForRole(anyInt());
        verify(userRoleService, atLeastOnce()).isOrderAcceptionAllowedForUser(anyInt());
        verify(walletService, atLeastOnce()).walletInnerTransfer(
                anyInt(),
                any(BigDecimal.class),
                any(TransactionSourceType.class),
                anyInt(),
                anyString());
        verify(walletService, atLeastOnce()).walletBalanceChange(any(WalletOperationData.class));
        verify(currencyService, atLeastOnce()).findCurrencyPairById(anyInt());
        verify(commissionDao, atLeastOnce()).getCommission(any(OperationType.class), any(UserRole.class));
        verify(companyWalletService, atLeastOnce()).deposit(
                any(CompanyWallet.class),
                any(BigDecimal.class),
                any(BigDecimal.class));
        verify(referralService, atLeastOnce()).processReferral(
                any(ExOrder.class),
                any(BigDecimal.class),
                any(Currency.class),
                anyInt());
        verify(orderDao, atLeastOnce()).updateOrder(any(ExOrder.class));
        verify(eventPublisher, atLeastOnce()).publishEvent(any(ApplicationEvent.class));
        verify(messageSource, atLeastOnce()).getMessage(anyString(), any(), any(Locale.class));

        reset(userService);
    }

    @Test
    public void autoAcceptOrders_list_ExOrder_is_empty() {
        when(userRoleService.isOrderAcceptionAllowedForUser(anyInt())).thenReturn(Boolean.TRUE);
        when(userService.getUserRoleFromDB(anyInt())).thenReturn(UserRole.USER);
        when(orderDao.selectTopOrders(
                anyInt(),
                any(BigDecimal.class),
                any(OperationType.class),
                any(Boolean.class),
                anyInt(),
                any(OrderBaseType.class))).thenReturn(Collections.EMPTY_LIST);

        Optional<OrderCreationResultDto> orderCreationResultDto = orderService.autoAcceptOrders(getMockOrderCreateDto(
                BigDecimal.TEN),
                Locale.ENGLISH);

        assertEquals(Optional.empty(), orderCreationResultDto);

        verify(userRoleService, times(1)).isOrderAcceptionAllowedForUser(anyInt());
        verify(userService, times(1)).getUserRoleFromDB(anyInt());
        verify(orderDao, times(1)).selectTopOrders(
                anyInt(),
                any(BigDecimal.class),
                any(OperationType.class),
                any(Boolean.class),
                anyInt(),
                any(OrderBaseType.class));
    }

    @Test
    public void autoAcceptOrders_list_ExOrder_has_element_amount_more_amountBase() {
        ExOrder exOrder = getMockExOrder();

        OrderCreateDto mockOrderCreateDto = getMockOrderCreateDto(BigDecimal.TEN);
        mockOrderCreateDto.setAmount(BigDecimal.valueOf(11));
        mockOrderCreateDto.setOrderBaseType(OrderBaseType.ICO);

        UserRoleSettings userRoleSettings = new UserRoleSettings();
        userRoleSettings.setUserRole(UserRole.BOT_TRADER);
        userRoleSettings.setBotAcceptionAllowedOnly(Boolean.FALSE);

        WalletsForOrderAcceptionDto walletsForOrderAcceptionDto = new WalletsForOrderAcceptionDto();
        walletsForOrderAcceptionDto.setOrderStatusId(2);

        String descriptionForCreator = "TEST_DESCRIPTION_FOR_CREATION";
        String descriptionForAcceptor = "TEST_DESCRIPTION_FOR_ACCEPTION";

        CurrencyPair cp = new CurrencyPair();
        cp.setName("BTC/USD");
        cp.setCurrency1(new Currency());

        Commission commission = new Commission();
        commission.setValue(BigDecimal.TEN);

        User user = new User();
        user.setEmail(USER_EMAIL);

        when(userRoleService.isOrderAcceptionAllowedForUser(anyInt())).thenReturn(Boolean.TRUE);
        when(userService.getUserRoleFromDB(anyInt())).thenReturn(UserRole.USER);
        when(orderDao.selectTopOrders(
                anyInt(),
                any(BigDecimal.class),
                any(OperationType.class),
                any(Boolean.class),
                anyInt(),
                any(OrderBaseType.class))).thenReturn(Collections.singletonList(exOrder));
        when(orderDao.lockOrdersListForAcception(anyListOf(Integer.class))).thenReturn(Boolean.TRUE);
        when(userService.getUserRoleFromDB(anyInt())).thenReturn(UserRole.BOT_TRADER);
        when(userRoleService.retrieveSettingsForRole(anyInt())).thenReturn(userRoleSettings);
        when(orderDao.getOrderById(anyInt())).thenReturn(exOrder);
        when(walletService.getWalletsForOrderByOrderIdAndBlock(anyInt(), anyInt()))
                .thenReturn(walletsForOrderAcceptionDto);
        when(transactionDescription.get(any(OrderStatus.class), any(OrderActionEnum.class)))
                .thenReturn(descriptionForCreator);
        when(transactionDescription.get(any(OrderStatus.class), any(OrderActionEnum.class)))
                .thenReturn(descriptionForAcceptor);
        when(currencyService.findCurrencyPairById(anyInt())).thenReturn(cp);
        when(commissionDao.getCommission(any(OperationType.class), any(UserRole.class))).thenReturn(commission);
        when(walletService.walletInnerTransfer(
                anyInt(),
                any(BigDecimal.class),
                any(TransactionSourceType.class),
                anyInt(),
                anyString())).thenReturn(WalletTransferStatus.SUCCESS);
        when(walletService.walletBalanceChange(any(WalletOperationData.class))).thenReturn(WalletTransferStatus.SUCCESS);
        doNothing().when(companyWalletService).deposit(
                any(CompanyWallet.class),
                any(BigDecimal.class),
                any(BigDecimal.class));
        doNothing().when(referralService).processReferral(
                any(ExOrder.class),
                any(BigDecimal.class),
                any(Currency.class),
                anyInt());
        when(orderDao.updateOrder(any(ExOrder.class))).thenReturn(Boolean.TRUE);
        doNothing().when(eventPublisher).publishEvent(any(AcceptOrderEvent.class));
        when(userService.getUserById(anyInt())).thenReturn(user);
        doNothing().when(eventPublisher).publishEvent(any(ApplicationEvent.class));

        Optional<OrderCreationResultDto> orderCreationResultDto = orderService
                .autoAcceptOrders(mockOrderCreateDto, Locale.ENGLISH);

        assertNotNull(orderCreationResultDto);

        verify(userRoleService, atLeastOnce()).isOrderAcceptionAllowedForUser(anyInt());
        verify(userService, atLeastOnce()).getUserRoleFromDB(anyInt());
        verify(orderDao, atLeastOnce()).selectTopOrders(
                anyInt(),
                any(BigDecimal.class),
                any(OperationType.class),
                any(Boolean.class),
                anyInt(),
                any(OrderBaseType.class));
        verify(orderDao, atLeastOnce()).lockOrdersListForAcception(anyListOf(Integer.class));
        verify(userService, atLeastOnce()).getUserRoleFromDB(anyInt());
        verify(userRoleService, atLeastOnce()).retrieveSettingsForRole(anyInt());
        verify(orderDao, atLeastOnce()).getOrderById(anyInt());
        verify(walletService, atLeastOnce()).getWalletsForOrderByOrderIdAndBlock(anyInt(), anyInt());
        verify(transactionDescription, atLeastOnce()).get(any(OrderStatus.class), any(OrderActionEnum.class));
        verify(currencyService, atLeastOnce()).findCurrencyPairById(anyInt());
        verify(commissionDao, atLeastOnce()).getCommission(any(OperationType.class), any(UserRole.class));
        verify(walletService, atLeastOnce()).walletInnerTransfer(
                anyInt(),
                any(BigDecimal.class),
                any(TransactionSourceType.class),
                anyInt(),
                anyString());
        verify(walletService, atLeastOnce()).walletBalanceChange(any(WalletOperationData.class));
        verify(companyWalletService, atLeastOnce()).deposit(
                any(CompanyWallet.class),
                any(BigDecimal.class),
                any(BigDecimal.class));
        verify(referralService, atLeastOnce()).processReferral(
                any(ExOrder.class),
                any(BigDecimal.class),
                any(Currency.class),
                anyInt());
        verify(orderDao, atLeastOnce()).updateOrder(any(ExOrder.class));
        verify(eventPublisher, atLeastOnce()).publishEvent(any(AcceptOrderEvent.class));
    }

    @Test
    public void autoAcceptOrders_list_ExOrder_has_element_amount_equals_amountBase() {
        OrderCreateDto mockOrderCreateDto = getMockOrderCreateDto(BigDecimal.TEN);
        mockOrderCreateDto.setAmount(BigDecimal.ZERO);
        mockOrderCreateDto.setOrderBaseType(OrderBaseType.ICO);
        mockOrderCreateDto.setUserId(100);

        ExOrder mockExOrder = getMockExOrder();
        mockExOrder.setAmountBase(BigDecimal.ZERO);

        WalletsForOrderAcceptionDto walletsForOrderAcceptionDto = new WalletsForOrderAcceptionDto();
        walletsForOrderAcceptionDto.setOrderStatusId(2);

        String descriptionForCreator = "TEST_DESCRIPTION_FOR_CREATION";
        String descriptionForAcceptor = "TEST_DESCRIPTION_FOR_ACCEPTION";

        UserRoleSettings userRoleSettings = new UserRoleSettings();
        userRoleSettings.setUserRole(UserRole.BOT_TRADER);
        userRoleSettings.setBotAcceptionAllowedOnly(Boolean.FALSE);

        CurrencyPair cp = new CurrencyPair();
        cp.setName("BTC/USD");
        cp.setCurrency1(new Currency());

        Commission commission = new Commission();
        commission.setValue(BigDecimal.TEN);

        when(userRoleService.isOrderAcceptionAllowedForUser(anyInt())).thenReturn(Boolean.TRUE);
        when(orderDao.selectTopOrders(
                anyInt(),
                any(BigDecimal.class),
                any(OperationType.class),
                anyBoolean(),
                anyInt(),
                any(OrderBaseType.class))).thenReturn(Collections.singletonList(mockExOrder));
        when(userService.getUserRoleFromDB(anyInt())).thenReturn(UserRole.USER);
        when(orderDao.lockOrdersListForAcception(anyListOf(Integer.class))).thenReturn(Boolean.TRUE);
        when(walletService.getWalletsForOrderByOrderIdAndBlock(anyInt(), anyInt()))
                .thenReturn(walletsForOrderAcceptionDto);
        when(transactionDescription.get(any(OrderStatus.class), any(OrderActionEnum.class)))
                .thenReturn(descriptionForCreator);
        when(transactionDescription.get(any(OrderStatus.class), any(OrderActionEnum.class)))
                .thenReturn(descriptionForAcceptor);
        when(orderDao.getOrderById(anyInt())).thenReturn(mockExOrder);
        when(userService.getUserRoleFromDB(anyInt())).thenReturn(UserRole.BOT_TRADER);
        when(userRoleService.retrieveSettingsForRole(anyInt())).thenReturn(userRoleSettings);
        when(userRoleService.isOrderAcceptionAllowedForUser(anyInt())).thenReturn(Boolean.TRUE);
        when(walletService.walletInnerTransfer(
                anyInt(),
                any(BigDecimal.class),
                any(TransactionSourceType.class),
                anyInt(),
                anyString())).thenReturn(WalletTransferStatus.SUCCESS);
        when(walletService.walletBalanceChange(any(WalletOperationData.class))).thenReturn(WalletTransferStatus.SUCCESS);
        when(currencyService.findCurrencyPairById(anyInt())).thenReturn(cp);
        when(commissionDao.getCommission(any(OperationType.class), any(UserRole.class))).thenReturn(commission);
        doNothing().when(companyWalletService).deposit(
                any(CompanyWallet.class),
                any(BigDecimal.class),
                any(BigDecimal.class));
        doNothing().when(referralService).processReferral(
                any(ExOrder.class),
                any(BigDecimal.class),
                any(Currency.class),
                anyInt());
        when(orderDao.updateOrder(any(ExOrder.class))).thenReturn(Boolean.TRUE);
        doNothing().when(eventPublisher).publishEvent(any(ApplicationEvent.class));

        Optional<OrderCreationResultDto> orderCreationResultDto = orderService.autoAcceptOrders(
                mockOrderCreateDto,
                Locale.ENGLISH);

        assertNotNull(orderCreationResultDto);
        assertEquals(Integer.valueOf(1), orderCreationResultDto.get().getAutoAcceptedQuantity());
        assertEquals(1, orderCreationResultDto.get().getFullyAcceptedOrdersIds().size());

        verify(userRoleService, atLeastOnce()).isOrderAcceptionAllowedForUser(anyInt());
        verify(orderDao, atLeastOnce()).selectTopOrders(
                anyInt(),
                any(BigDecimal.class),
                any(OperationType.class),
                anyBoolean(),
                anyInt(),
                any(OrderBaseType.class));
        verify(userService, atLeastOnce()).getUserRoleFromDB(anyInt());
        verify(orderDao, atLeastOnce()).lockOrdersListForAcception(anyListOf(Integer.class));
        verify(walletService, atLeastOnce()).getWalletsForOrderByOrderIdAndBlock(anyInt(), anyInt());
        verify(transactionDescription, atLeastOnce()).get(any(OrderStatus.class), any(OrderActionEnum.class));
        verify(orderDao, atLeastOnce()).getOrderById(anyInt());
        verify(userRoleService, atLeastOnce()).retrieveSettingsForRole(anyInt());
        verify(userRoleService, atLeastOnce()).isOrderAcceptionAllowedForUser(anyInt());
        verify(walletService, atLeastOnce()).walletInnerTransfer(
                anyInt(),
                any(BigDecimal.class),
                any(TransactionSourceType.class),
                anyInt(),
                anyString());
        verify(walletService, atLeastOnce()).walletBalanceChange(any(WalletOperationData.class));
        verify(currencyService, atLeastOnce()).findCurrencyPairById(anyInt());
        verify(commissionDao, atLeastOnce()).getCommission(any(OperationType.class), any(UserRole.class));
        verify(companyWalletService, atLeastOnce()).deposit(
                any(CompanyWallet.class),
                any(BigDecimal.class),
                any(BigDecimal.class));
        verify(referralService, atLeastOnce()).processReferral(
                any(ExOrder.class),
                any(BigDecimal.class),
                any(Currency.class),
                anyInt());
        verify(orderDao, atLeastOnce()).updateOrder(any(ExOrder.class));
        verify(eventPublisher, atLeastOnce()).publishEvent(any(ApplicationEvent.class));

        reset(userService);
    }

    @Test
    public void autoAcceptOrders_list_ExOrder_has_element_amount_less_amountBase() {
        OrderCreateDto mockOrderCreateDto = getMockOrderCreateDto(BigDecimal.TEN);
        mockOrderCreateDto.setAmount(BigDecimal.ZERO);
        mockOrderCreateDto.setOrderBaseType(OrderBaseType.ICO);
        mockOrderCreateDto.setUserId(100);

        ExOrder mockExOrder = getMockExOrder();
        mockExOrder.setAmountBase(BigDecimal.valueOf(11));

        OrderDetailDto dto = new OrderDetailDto(
                1,
                7,
                BigDecimal.TEN,
                11,
                15,
                3,
                BigDecimal.TEN,
                18,
                19,
                BigDecimal.ONE);

        User user = new User();
        user.setEmail(USER_EMAIL);

        WalletsForOrderAcceptionDto walletsForOrderAcceptionDto = new WalletsForOrderAcceptionDto();
        walletsForOrderAcceptionDto.setOrderStatusId(2);

        String descriptionForCreator = "TEST_DESCRIPTION_FOR_CREATION";
        String descriptionForAcceptor = "TEST_DESCRIPTION_FOR_ACCEPTION";

        UserRoleSettings userRoleSettings = new UserRoleSettings();
        userRoleSettings.setUserRole(UserRole.BOT_TRADER);
        userRoleSettings.setBotAcceptionAllowedOnly(Boolean.FALSE);

        CurrencyPair cp = new CurrencyPair();
        cp.setName("BTC/USD");
        cp.setCurrency1(new Currency());

        Commission commission = new Commission();
        commission.setValue(BigDecimal.TEN);

        when(userRoleService.isOrderAcceptionAllowedForUser(anyInt())).thenReturn(Boolean.TRUE);
        when(orderDao.selectTopOrders(
                anyInt(),
                any(BigDecimal.class),
                any(OperationType.class),
                anyBoolean(),
                anyInt(),
                any(OrderBaseType.class))).thenReturn(Collections.singletonList(mockExOrder));
        when(userService.getUserRoleFromDB(anyInt())).thenReturn(UserRole.USER);
        when(walletService.getOrderRelatedDataAndBlock(anyInt())).thenReturn(Collections.singletonList(dto));
        when(transactionDescription.get(any(OrderStatus.class), any(OrderActionEnum.class))).thenReturn("DESCRIPTION");
        when(orderDao.setStatus(anyInt(), any(OrderStatus.class))).thenReturn(Boolean.TRUE);
        when(orderDao.getOrderById(anyInt())).thenReturn(mockExOrder);
        when(userService.getUserById(anyInt())).thenReturn(user);
        doNothing().when(eventPublisher).publishEvent(any(ApplicationEvent.class));
        when(userService.getUserRoleFromDB(anyString())).thenReturn(UserRole.USER);
        when(orderDao.getWalletAndCommission(
                anyString(),
                any(Currency.class),
                any(OperationType.class),
                any(UserRole.class))).thenReturn(getMockWalletsAndCommissionsForOrderCreationDto());
        when(transactionDescription.get(any(), any(OrderActionEnum.class))).thenReturn("DESCRIPTION");
        when(walletService.ifEnoughMoney(anyInt(), any(BigDecimal.class))).thenReturn(Boolean.TRUE);
        when(walletService.getWalletsForOrderByOrderIdAndBlock(anyInt(), anyInt()))
                .thenReturn(walletsForOrderAcceptionDto);
        when(transactionDescription.get(any(OrderStatus.class), any(OrderActionEnum.class)))
                .thenReturn(descriptionForCreator);
        when(transactionDescription.get(any(OrderStatus.class), any(OrderActionEnum.class)))
                .thenReturn(descriptionForAcceptor);
        when(orderDao.getOrderById(anyInt())).thenReturn(mockExOrder);
        when(userService.getUserRoleFromDB(anyInt())).thenReturn(UserRole.BOT_TRADER);
        when(userRoleService.retrieveSettingsForRole(anyInt())).thenReturn(userRoleSettings);
        when(userRoleService.isOrderAcceptionAllowedForUser(anyInt())).thenReturn(Boolean.TRUE);
        when(walletService.walletInnerTransfer(
                anyInt(),
                any(BigDecimal.class),
                any(TransactionSourceType.class),
                anyInt(),
                anyString())).thenReturn(WalletTransferStatus.SUCCESS);
        when(walletService.walletBalanceChange(any(WalletOperationData.class))).thenReturn(WalletTransferStatus.SUCCESS);
        when(currencyService.findCurrencyPairById(anyInt())).thenReturn(cp);
        when(commissionDao.getCommission(any(OperationType.class), any(UserRole.class))).thenReturn(commission);
        doNothing().when(companyWalletService).deposit(
                any(CompanyWallet.class),
                any(BigDecimal.class),
                any(BigDecimal.class));
        doNothing().when(referralService).processReferral(
                any(ExOrder.class),
                any(BigDecimal.class),
                any(Currency.class),
                anyInt());
        when(orderDao.updateOrder(any(ExOrder.class))).thenReturn(Boolean.TRUE);
        doNothing().when(eventPublisher).publishEvent(any(ApplicationEvent.class));

        Optional<OrderCreationResultDto> orderCreationResultDto = orderService.autoAcceptOrders(
                mockOrderCreateDto,
                Locale.ENGLISH);

        assertNotNull(orderCreationResultDto);
        assertEquals(BigDecimal.valueOf(11), orderCreationResultDto.get().getPartiallyAcceptedOrderFullAmount());


        verify(userRoleService, atLeastOnce()).isOrderAcceptionAllowedForUser(anyInt());
        verify(orderDao, atLeastOnce()).selectTopOrders(
                anyInt(),
                any(BigDecimal.class),
                any(OperationType.class),
                anyBoolean(),
                anyInt(),
                any(OrderBaseType.class));
        verify(userService, atLeastOnce()).getUserRoleFromDB(anyInt());
        verify(walletService, atLeastOnce()).getOrderRelatedDataAndBlock(anyInt());
        verify(transactionDescription, atLeastOnce()).get(any(OrderStatus.class), any(OrderActionEnum.class));
        verify(orderDao, atLeastOnce()).setStatus(anyInt(), any(OrderStatus.class));
        verify(orderDao, atLeastOnce()).getOrderById(anyInt());
        verify(userService, atLeastOnce()).getUserById(anyInt());
        verify(eventPublisher, atLeastOnce()).publishEvent(any(ApplicationEvent.class));
        verify(userService, atLeastOnce()).getUserRoleFromDB(anyString());
        verify(orderDao, atLeastOnce()).getWalletAndCommission(
                anyString(),
                any(Currency.class),
                any(OperationType.class),
                any(UserRole.class));
        verify(transactionDescription, atLeastOnce()).get(any(), any(OrderActionEnum.class));
        verify(walletService, atLeastOnce()).ifEnoughMoney(anyInt(), any(BigDecimal.class));
        verify(walletService, atLeastOnce()).getWalletsForOrderByOrderIdAndBlock(anyInt(), anyInt());
        verify(transactionDescription, atLeastOnce()).get(any(OrderStatus.class), any(OrderActionEnum.class));
        verify(orderDao, atLeastOnce()).getOrderById(anyInt());
        verify(userService, atLeastOnce()).getUserRoleFromDB(anyInt());
        verify(userRoleService, atLeastOnce()).retrieveSettingsForRole(anyInt());
        verify(userRoleService, atLeastOnce()).isOrderAcceptionAllowedForUser(anyInt());
        verify(walletService, atLeastOnce()).walletInnerTransfer(
                anyInt(),
                any(BigDecimal.class),
                any(TransactionSourceType.class),
                anyInt(),
                anyString());
        verify(walletService, atLeastOnce()).walletBalanceChange(any(WalletOperationData.class));
        verify(currencyService, atLeastOnce()).findCurrencyPairById(anyInt());
        verify(commissionDao, atLeastOnce()).getCommission(any(OperationType.class), any(UserRole.class));
        verify(companyWalletService, atLeastOnce()).deposit(
                any(CompanyWallet.class),
                any(BigDecimal.class),
                any(BigDecimal.class));
        verify(referralService, atLeastOnce()).processReferral(
                any(ExOrder.class),
                any(BigDecimal.class),
                any(Currency.class),
                anyInt());
        verify(orderDao, atLeastOnce()).updateOrder(any(ExOrder.class));
        verify(eventPublisher, atLeastOnce()).publishEvent(any(ApplicationEvent.class));

        reset(userService);
    }

    @Test
    public void testPrepareMarketOrder() {
        InputCreateOrderDto inputOrder = getTestInputCreateOrderDto();
        when(userService.getUserEmailFromSecurityContext()).thenReturn("test@test.com");
        when(currencyService.findCurrencyPairById(anyInt())).thenReturn(new CurrencyPair("BTC/USD"));
        when(userService.getUserRoleFromDB(anyString())).thenReturn(UserRole.USER);
        when(orderDao.getWalletAndCommission(anyString(), any(Currency.class), any(OperationType.class), any(UserRole.class)))
                .thenReturn(new WalletsAndCommissionsForOrderCreationDto());

        final OrderCreateDto orderCreateDto = orderService.prepareMarketOrder(inputOrder);
        assertEquals(OperationType.BUY, orderCreateDto.getOperationType());
        assertEquals("BTC/USD", orderCreateDto.getCurrencyPair().getName());
        assertEquals(OrderBaseType.MARKET, orderCreateDto.getOrderBaseType());
    }

    @Test
    public void testAutoAcceptMarketOrders() {
        final OrderCreateDto testOrderCreatedDto = getTestOrderCreatedDto();
        when(walletService.getActiveBalanceAndBlockByWalletId(anyInt())).thenReturn(BigDecimal.TEN);
        when(orderDao.findAllMarketOrderCandidates(anyInt(), any(OperationType.class))).thenReturn(getMarketOrdersCandidates());
        when(orderDao.getOrderById(anyInt())).thenReturn(getMockExOrder());
        when(userService.getUserRoleFromDB(anyInt())).thenReturn(UserRole.USER);
        when(userRoleService.retrieveSettingsForRole(anyInt())).thenReturn(new UserRoleSettings());
        when(transactionDescription.get(any(OrderStatus.class), any(OrderActionEnum.class))).thenReturn("description");
        final WalletsForOrderAcceptionDto walletsForOrderAcceptionDto = new WalletsForOrderAcceptionDto();
        walletsForOrderAcceptionDto.setOrderStatusId(2);
        when(walletService.getWalletsForOrderByOrderIdAndBlock(anyInt(), anyInt())).thenReturn(walletsForOrderAcceptionDto);
        when(currencyService.findCurrencyPairById(anyInt())).thenReturn(new CurrencyPair("BTC/USD"));
        final Commission commission = new Commission();
        commission.setValue(BigDecimal.valueOf(0.001));
        when(commissionDao.getCommission(any(OperationType.class), any(UserRole.class))).thenReturn(commission);
        when(walletService.walletBalanceChange(any(WalletOperationData.class))).thenReturn(WalletTransferStatus.SUCCESS);
        when(orderDao.updateOrder(any(ExOrder.class))).thenReturn(true);
        final OrderDetailDto orderDetailDto = new OrderDetailDto(3, 2, BigDecimal.ONE, 23, 23, 2, BigDecimal.TEN, 34, 35, BigDecimal.ZERO);
        when(walletService.getOrderRelatedDataAndBlock(anyInt())).thenReturn(ImmutableList.of(orderDetailDto));
        when(orderDao.setStatus(anyInt(), any(OrderStatus.class))).thenReturn(true);
        when(walletService.walletInnerTransfer(anyInt(), any(BigDecimal.class), any(TransactionSourceType.class), anyInt(), anyString())).thenReturn(WalletTransferStatus.SUCCESS);
        when(transactionService.setStatusById(anyInt(), anyInt())).thenReturn(true);
        final User user = new User();
        user.setEmail("test@test.com");
        when(userService.getUserById(anyInt())).thenReturn(user);
        final WalletsAndCommissionsForOrderCreationDto walletsAndCommissionsForOrderCreationDto = new WalletsAndCommissionsForOrderCreationDto();
        walletsAndCommissionsForOrderCreationDto.setUserId(1);
        walletsAndCommissionsForOrderCreationDto.setCommissionValue(BigDecimal.ONE);
        when(orderDao.getWalletAndCommission(anyString(), any(Currency.class), any(OperationType.class), any(UserRole.class))).thenReturn(walletsAndCommissionsForOrderCreationDto);
        when(walletService.ifEnoughMoney(anyInt(), any(BigDecimal.class))).thenReturn(true);

        final Optional<OrderCreationResultDto> resultDto = orderService.autoAcceptMarketOrders(testOrderCreatedDto, Locale.ENGLISH);

        final OrderCreationResultDto result = resultDto.orElseThrow(RuntimeException::new);
        assertEquals(3, (int) result.getAutoAcceptedQuantity());
        assertEquals(BigDecimal.ONE.intValue(), result.getPartiallyAcceptedAmount().intValue());
        assertEquals(3, result.getFullyAcceptedOrdersIds().size());
        assertEquals(BigDecimal.valueOf(3.0), result.getPartiallyAcceptedOrderFullAmount());
    }

    @Test
    public void getMyOrdersWithState_with_cacheData_checkCache_false() {
        OrderWideListDto dto = new OrderWideListDto();
        dto.setUserId(100);
        dto.setCurrencyPairId(100);
        dto.setCurrencyPairName("BTC/USD");
        dto.setStatus(OrderStatus.OPENED);

        HttpSession session = Mockito.mock(HttpSession.class);
        session.setAttribute("cacheHashMap", dto);

        HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
        request.setAttribute("cacheHashMap", dto);

        CacheData cacheData = getMockCacheData(request);

        when(userService.getIdByEmail(anyString())).thenReturn(100);
        when(orderDao.getMyOrdersWithState(
                anyInt(),
                any(CurrencyPair.class),
                any(OrderStatus.class),
                any(OperationType.class),
                anyString(),
                anyInt(),
                anyInt(),
                any(Locale.class))).thenReturn(Collections.singletonList(dto));
        when(currencyService.findCurrencyPairById(anyInt())).thenReturn(new CurrencyPair("BTC/USD"));
        when(request.getSession()).thenReturn(session);
        when(request.getSession()).thenReturn(session);

        List<OrderWideListDto> wideListDtos = orderService.getMyOrdersWithState(
                cacheData,
                USER_EMAIL,
                getMockCurrencyPair(CurrencyPairType.MAIN),
                OrderStatus.OPENED,
                OperationType.BUY,
                "FIAT",
                10,
                10,
                Locale.ENGLISH);

        assertNotNull(wideListDtos);
        assertEquals(1, wideListDtos.size());
        assertEquals(0, wideListDtos.get(0).getId());
        assertEquals(100, wideListDtos.get(0).getUserId());
        assertEquals(OrderStatus.OPENED, wideListDtos.get(0).getStatus());
        assertEquals("BTC/USD", wideListDtos.get(0).getCurrencyPairName());

        verify(userService, atLeastOnce()).getIdByEmail(anyString());
        verify(orderDao, atLeastOnce()).getMyOrdersWithState(
                anyInt(),
                any(CurrencyPair.class),
                any(OrderStatus.class),
                any(OperationType.class),
                anyString(),
                anyInt(),
                anyInt(),
                any(Locale.class));
    }

    @Ignore
    public void getMyOrdersWithState_with_cacheData_checkCache_true() {
        OrderWideListDto dto = new OrderWideListDto();
        dto.setUserId(100);
        dto.setCurrencyPairId(100);
        dto.setCurrencyPairName("BTC/USD");
        dto.setStatus(OrderStatus.OPENED);

        HttpSession session = Mockito.mock(HttpSession.class);
        session.setAttribute("cacheHashMap", dto);

        HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
        request.setAttribute("cacheHashMap", dto);

        CacheData cacheData = getMockCacheData(request);

        when(userService.getIdByEmail(anyString())).thenReturn(100);
        when(orderDao.getMyOrdersWithState(
                anyInt(),
                any(CurrencyPair.class),
                any(OrderStatus.class),
                any(OperationType.class),
                anyString(),
                anyInt(),
                anyInt(),
                any(Locale.class))).thenReturn(Collections.singletonList(dto));
        when(request.getSession()).thenReturn(session);

        Map<String, Integer> cacheHashMap = new HashMap<>();
        cacheHashMap.put("cacheHashMap", dto.hashCode());
        when(request.getSession().getAttribute(anyString())).thenReturn(cacheHashMap);

        List<OrderWideListDto> wideListDtos = orderService.getMyOrdersWithState(
                cacheData,
                USER_EMAIL,
                getMockCurrencyPair(CurrencyPairType.MAIN),
                OrderStatus.OPENED,
                OperationType.BUY,
                "FIAT",
                10,
                10,
                Locale.ENGLISH);

        assertNotNull(wideListDtos);
        assertEquals(1, wideListDtos.size());
        assertEquals(0, wideListDtos.get(0).getId());
        assertEquals(100, wideListDtos.get(0).getUserId());
        assertEquals(OrderStatus.OPENED, wideListDtos.get(0).getStatus());
        assertEquals("BTC/USD", wideListDtos.get(0).getCurrencyPairName());

        verify(userService, atLeastOnce()).getIdByEmail(anyString());
        verify(orderDao, atLeastOnce()).getMyOrdersWithState(
                anyInt(),
                any(CurrencyPair.class),
                any(OrderStatus.class),
                any(OperationType.class),
                anyString(),
                anyInt(),
                anyInt(),
                any(Locale.class));
    }

    @Test
    public void getMyOrdersWithState() {
        OrderWideListDto dto = new OrderWideListDto();
        dto.setUserId(100);
        dto.setCurrencyPairId(100);
        dto.setCurrencyPairName("BTC/USD");
        dto.setStatus(OrderStatus.OPENED);

        when(userService.getIdByEmail(anyString())).thenReturn(1);
        when(orderDao.getMyOrdersWithState(
                anyInt(),
                any(CurrencyPair.class),
                any(OrderStatus.class),
                any(OperationType.class),
                anyString(),
                anyInt(),
                anyInt(),
                any(Locale.class))).thenReturn(Collections.singletonList(dto));
        when(currencyService.findCurrencyPairById(anyInt())).thenReturn(new CurrencyPair("BTC/USD"));

        List<OrderWideListDto> myOrdersWithState = orderService.getMyOrdersWithState(
                USER_EMAIL,
                new CurrencyPair(),
                OrderStatus.OPENED,
                OperationType.SELL,
                "ALL",
                10,
                15,
                Locale.ENGLISH);

        assertNotNull(myOrdersWithState);
        assertEquals(dto, myOrdersWithState.get(0));

        verify(userService, atLeastOnce()).getIdByEmail(anyString());
        verify(orderDao, atLeastOnce()).getMyOrdersWithState(
                anyInt(),
                any(CurrencyPair.class),
                any(OrderStatus.class),
                any(OperationType.class),
                anyString(),
                anyInt(),
                anyInt(),
                any(Locale.class));
    }

    @Test
    public void getMyOrderById() {
        OrderCreateDto mockOrderCreateDto = getMockOrderCreateDto(BigDecimal.ONE);
        when(orderDao.getMyOrderById(anyInt())).thenReturn(mockOrderCreateDto);

        OrderCreateDto myOrderById = orderService.getMyOrderById(12);

        assertNotNull(myOrderById);
        assertEquals(mockOrderCreateDto, myOrderById);

        verify(orderDao, atLeastOnce()).getMyOrderById(anyInt());
    }

    @Test
    public void getMyOrderById_null() {
        when(orderDao.getMyOrderById(anyInt())).thenReturn(null);

        OrderCreateDto orderCreateDto = orderService.getMyOrderById(100);

        assertNull(orderCreateDto);

        verify(orderDao, atLeastOnce()).getMyOrderById(anyInt());
    }

    @Test
    public void getOrderById() {
        ExOrder mockExOrder = getMockExOrder();
        when(orderDao.getOrderById(anyInt())).thenReturn(mockExOrder);

        ExOrder exOrder = orderService.getOrderById(100);

        assertNotNull(exOrder);
        assertEquals(mockExOrder, exOrder);

        verify(orderDao, atLeastOnce()).getOrderById(anyInt());
    }

    @Test
    public void getOrderById_has_two_arguments() {
        ExOrder mockExOrder = getMockExOrder();
        when(orderDao.getOrderById(anyInt(), anyInt())).thenReturn(mockExOrder);

        ExOrder exOrder = orderService.getOrderById(100, 200);

        assertNotNull(exOrder);
        assertEquals(mockExOrder, exOrder);

        verify(orderDao, atLeastOnce()).getOrderById(anyInt(), anyInt());
    }

    @Test
    public void getOrderById_has_two_arguments_null() {
        when(orderDao.getOrderById(anyInt(), anyInt())).thenReturn(null);

        ExOrder exOrder = orderService.getOrderById(100, 200);

        assertNull(exOrder);

        verify(orderDao, atLeastOnce()).getOrderById(anyInt(), anyInt());
    }

    @Test
    public void setStatus_false() {
        when(orderDao.setStatus(anyInt(), any(OrderStatus.class))).thenReturn(Boolean.FALSE);

        boolean setStatus = orderService.setStatus(1, OrderStatus.OPENED);

        assertFalse(setStatus);
        verify(orderDao, atLeastOnce()).setStatus(anyInt(), any(OrderStatus.class));
    }

    @Test
    public void setStatus_true() {
        when(orderDao.setStatus(anyInt(), any(OrderStatus.class))).thenReturn(Boolean.TRUE);

        boolean setStatus = orderService.setStatus(1, OrderStatus.OPENED);

        assertTrue(setStatus);
        verify(orderDao, atLeastOnce()).setStatus(anyInt(), any(OrderStatus.class));
    }

    @Test
    public void acceptOrder_OrderAcceptionException() {
        when(userService.getUserLocaleForMobile(anyString())).thenReturn(Locale.ENGLISH);
        when(userService.getIdByEmail(anyString())).thenReturn(100);
        when(orderDao.lockOrdersListForAcception(anyListOf(Integer.class))).thenReturn(Boolean.FALSE);
        when(messageSource.getMessage(anyString(), any(), any(Locale.class))).thenReturn("order.lockerror");

        try {
            orderService.acceptOrder(USER_EMAIL, 1);
        } catch (Exception e) {
            assertTrue(e instanceof OrderAcceptionException);
            assertEquals("order.lockerror", e.getMessage());
        }
        verify(userService, atLeastOnce()).getUserLocaleForMobile(anyString());
        verify(userService, atLeastOnce()).getIdByEmail(anyString());
        verify(orderDao, atLeastOnce()).lockOrdersListForAcception(anyListOf(Integer.class));
        verify(messageSource, atLeastOnce()).getMessage(anyString(), any(), any(Locale.class));
    }

    @Test
    public void acceptOrder_checkAcceptPermissionForUser_AttemptToAcceptBotOrderException() {
        UserRoleSettings userRoleSettings = new UserRoleSettings();
        userRoleSettings.setUserRole(UserRole.VIP_USER);
        userRoleSettings.setBotAcceptionAllowedOnly(Boolean.TRUE);

        when(userService.getUserLocaleForMobile(anyString())).thenReturn(Locale.ENGLISH);
        when(userService.getIdByEmail(anyString())).thenReturn(100);
        when(orderDao.lockOrdersListForAcception(anyListOf(Integer.class))).thenReturn(Boolean.TRUE);
        when(orderDao.getOrderById(anyInt())).thenReturn(getMockExOrder());
        when(userService.getUserRoleFromDB(anyInt())).thenReturn(UserRole.USER);
        when(userRoleService.retrieveSettingsForRole(anyInt())).thenReturn(userRoleSettings);
        when(messageSource.getMessage(anyString(), any(), any(Locale.class))).thenReturn("orders.acceptsaveerror");

        try {
            orderService.acceptOrder(USER_EMAIL, 1);
        } catch (Exception e) {
            assertTrue(e instanceof AttemptToAcceptBotOrderException);
            assertEquals("orders.acceptsaveerror", e.getMessage());
        }
        verify(userService, atLeastOnce()).getUserLocaleForMobile(anyString());
        verify(userService, atLeastOnce()).getIdByEmail(anyString());
        verify(orderDao, atLeastOnce()).lockOrdersListForAcception(anyListOf(Integer.class));
        verify(orderDao, atLeastOnce()).getOrderById(anyInt());
        verify(userService, atLeastOnce()).getUserRoleFromDB(anyInt());
        verify(userRoleService, atLeastOnce()).retrieveSettingsForRole(anyInt());
        verify(messageSource, atLeastOnce()).getMessage(anyString(), any(), any(Locale.class));
    }

    @Test
    public void acceptOrder() {
        CurrencyPair mockCurrencyPair = getMockCurrencyPair(CurrencyPairType.MAIN);
        mockCurrencyPair.setName("BTC/USD");

        ExOrder mockExOrder = getMockExOrder();
        mockExOrder.setOperationType(OperationType.INPUT);
        mockExOrder.setCurrencyPair(mockCurrencyPair);

        UserRoleSettings userRoleSettings = new UserRoleSettings();
        userRoleSettings.setUserRole(UserRole.VIP_USER);
        userRoleSettings.setBotAcceptionAllowedOnly(Boolean.FALSE);

        WalletsForOrderAcceptionDto walletsForOrderAcceptionDto = new WalletsForOrderAcceptionDto();
        walletsForOrderAcceptionDto.setOrderStatusId(2);
        walletsForOrderAcceptionDto.setUserCreatorOutWalletId(15);

        Commission commission = new Commission();
        commission.setValue(BigDecimal.TEN);

        when(userService.getUserLocaleForMobile(anyString())).thenReturn(Locale.ENGLISH);
        when(userService.getIdByEmail(anyString())).thenReturn(100);
        when(orderDao.lockOrdersListForAcception(anyListOf(Integer.class))).thenReturn(Boolean.TRUE);
        when(orderDao.getOrderById(anyInt())).thenReturn(mockExOrder);
        when(userService.getUserRoleFromDB(anyInt())).thenReturn(UserRole.USER);
        when(userRoleService.retrieveSettingsForRole(anyInt())).thenReturn(userRoleSettings);
        when(userRoleService.isOrderAcceptionAllowedForUser(anyInt())).thenReturn(Boolean.TRUE);
        when(walletService.getWalletsForOrderByOrderIdAndBlock(anyInt(), anyInt()))
                .thenReturn(walletsForOrderAcceptionDto);
        when(transactionDescription.get(any(OrderStatus.class), any(OrderActionEnum.class))).thenReturn("DESCRIPTION");
        when(currencyService.findCurrencyPairById(anyInt())).thenReturn(mockCurrencyPair);
        when(userService.getUserRoleFromDB(anyInt())).thenReturn(UserRole.ACCOUNTANT);
        when(commissionDao.getCommission(any(OperationType.class), any(UserRole.class))).thenReturn(commission);
        when(walletService.walletInnerTransfer(
                anyInt(),
                any(BigDecimal.class),
                any(TransactionSourceType.class),
                anyInt(),
                anyString())).thenReturn(WalletTransferStatus.SUCCESS);
        when(walletService.walletBalanceChange(any(WalletOperationData.class))).thenReturn(WalletTransferStatus.SUCCESS);
        doNothing().when(companyWalletService).deposit(
                any(CompanyWallet.class),
                any(BigDecimal.class),
                any(BigDecimal.class));
        when(currencyService.findCurrencyPairById(anyInt())).thenReturn(mockCurrencyPair);
        doNothing().when(referralService).processReferral(
                any(ExOrder.class),
                any(BigDecimal.class),
                any(Currency.class),
                anyInt());
        when(orderDao.updateOrder(any(ExOrder.class))).thenReturn(Boolean.TRUE);
        doNothing().when(eventPublisher).publishEvent(ApplicationEvent.class);

        orderService.acceptOrder(USER_EMAIL, 1);

        verify(userService, atLeastOnce()).getUserLocaleForMobile(anyString());
        verify(userService, atLeastOnce()).getIdByEmail(anyString());
        verify(orderDao, atLeastOnce()).lockOrdersListForAcception(anyListOf(Integer.class));
        verify(orderDao, atLeastOnce()).getOrderById(anyInt());
        verify(userService, atLeastOnce()).getUserRoleFromDB(anyInt());
        verify(userRoleService, atLeastOnce()).retrieveSettingsForRole(anyInt());
        verify(userRoleService, atLeastOnce()).isOrderAcceptionAllowedForUser(anyInt());
        verify(walletService, atLeastOnce()).getWalletsForOrderByOrderIdAndBlock(anyInt(), anyInt());
        verify(transactionDescription, atLeastOnce()).get(any(OrderStatus.class), any(OrderActionEnum.class));
        verify(currencyService, atLeastOnce()).findCurrencyPairById(anyInt());
        verify(userService, atLeastOnce()).getUserRoleFromDB(anyInt());
        verify(commissionDao, atLeastOnce()).getCommission(any(OperationType.class), any(UserRole.class));
        verify(walletService, atLeastOnce()).walletInnerTransfer(
                anyInt(),
                any(BigDecimal.class),
                any(TransactionSourceType.class),
                anyInt(),
                anyString());
        verify(walletService, atLeastOnce()).walletBalanceChange(any(WalletOperationData.class));
        verify(companyWalletService, atLeastOnce()).deposit(
                any(CompanyWallet.class),
                any(BigDecimal.class),
                any(BigDecimal.class));
        verify(currencyService, atLeastOnce()).findCurrencyPairById(anyInt());
        verify(referralService, atLeastOnce()).processReferral(
                any(ExOrder.class),
                any(BigDecimal.class),
                any(Currency.class),
                anyInt());
        verify(orderDao, atLeastOnce()).updateOrder(any(ExOrder.class));
    }

    @Test
    public void acceptOrderByAdmin() {
        CurrencyPair mockCurrencyPair = getMockCurrencyPair(CurrencyPairType.MAIN);
        mockCurrencyPair.setName("BTC/USD");

        ExOrder mockExOrder = getMockExOrder();
        mockExOrder.setOperationType(OperationType.INPUT);
        mockExOrder.setCurrencyPair(mockCurrencyPair);

        UserRoleSettings userRoleSettings = new UserRoleSettings();
        userRoleSettings.setUserRole(UserRole.VIP_USER);
        userRoleSettings.setBotAcceptionAllowedOnly(Boolean.FALSE);

        WalletsForOrderAcceptionDto walletsForOrderAcceptionDto = new WalletsForOrderAcceptionDto();
        walletsForOrderAcceptionDto.setOrderStatusId(2);
        walletsForOrderAcceptionDto.setUserCreatorOutWalletId(15);

        Commission commission = new Commission();
        commission.setValue(BigDecimal.TEN);

        when(userService.getIdByEmail(anyString())).thenReturn(100);
        when(orderDao.lockOrdersListForAcception(anyListOf(Integer.class))).thenReturn(Boolean.TRUE);
        when(orderDao.getOrderById(anyInt())).thenReturn(getMockExOrder());
        when(orderDao.getOrderById(anyInt())).thenReturn(mockExOrder);
        when(userService.getUserRoleFromDB(anyInt())).thenReturn(UserRole.USER);
        when(userRoleService.retrieveSettingsForRole(anyInt())).thenReturn(userRoleSettings);
        when(userRoleService.isOrderAcceptionAllowedForUser(anyInt())).thenReturn(Boolean.TRUE);
        when(walletService.getWalletsForOrderByOrderIdAndBlock(anyInt(), anyInt()))
                .thenReturn(walletsForOrderAcceptionDto);
        when(transactionDescription.get(any(OrderStatus.class), any(OrderActionEnum.class))).thenReturn("DESCRIPTION");
        when(currencyService.findCurrencyPairById(anyInt())).thenReturn(mockCurrencyPair);
        when(userService.getUserRoleFromDB(anyInt())).thenReturn(UserRole.ACCOUNTANT);
        when(commissionDao.getCommission(any(OperationType.class), any(UserRole.class))).thenReturn(commission);
        when(walletService.walletInnerTransfer(
                anyInt(),
                any(BigDecimal.class),
                any(TransactionSourceType.class),
                anyInt(),
                anyString())).thenReturn(WalletTransferStatus.SUCCESS);
        when(walletService.walletBalanceChange(any(WalletOperationData.class))).thenReturn(WalletTransferStatus.SUCCESS);
        doNothing().when(companyWalletService).deposit(
                any(CompanyWallet.class),
                any(BigDecimal.class),
                any(BigDecimal.class));
        when(currencyService.findCurrencyPairById(anyInt())).thenReturn(mockCurrencyPair);
        doNothing().when(referralService).processReferral(
                any(ExOrder.class),
                any(BigDecimal.class),
                any(Currency.class),
                anyInt());
        when(orderDao.updateOrder(any(ExOrder.class))).thenReturn(Boolean.TRUE);

        orderService.acceptOrderByAdmin(USER_EMAIL, 1, Locale.ENGLISH);

        verify(userService, atLeastOnce()).getIdByEmail(anyString());
        verify(orderDao, atLeastOnce()).lockOrdersListForAcception(anyListOf(Integer.class));
        verify(orderDao, atLeastOnce()).getOrderById(anyInt());
        verify(userService, atLeastOnce()).getUserRoleFromDB(anyInt());
        verify(userRoleService, atLeastOnce()).retrieveSettingsForRole(anyInt());
        verify(userRoleService, atLeastOnce()).isOrderAcceptionAllowedForUser(anyInt());
        verify(walletService, atLeastOnce()).getWalletsForOrderByOrderIdAndBlock(anyInt(), anyInt());
        verify(transactionDescription, atLeastOnce()).get(any(OrderStatus.class), any(OrderActionEnum.class));
        verify(currencyService, atLeastOnce()).findCurrencyPairById(anyInt());
        verify(userService, atLeastOnce()).getUserRoleFromDB(anyInt());
        verify(commissionDao, atLeastOnce()).getCommission(any(OperationType.class), any(UserRole.class));
        verify(walletService, atLeastOnce()).walletInnerTransfer(
                anyInt(),
                any(BigDecimal.class),
                any(TransactionSourceType.class),
                anyInt(),
                anyString());
        verify(walletService, atLeastOnce()).walletBalanceChange(any(WalletOperationData.class));
        verify(companyWalletService, atLeastOnce()).deposit(
                any(CompanyWallet.class),
                any(BigDecimal.class),
                any(BigDecimal.class));
        verify(currencyService, atLeastOnce()).findCurrencyPairById(anyInt());
        verify(referralService, atLeastOnce()).processReferral(
                any(ExOrder.class),
                any(BigDecimal.class),
                any(Currency.class),
                anyInt());
        verify(orderDao, atLeastOnce()).updateOrder(any(ExOrder.class));
    }

    @Test
    public void acceptManyOrdersByAdmin() {
        CurrencyPair mockCurrencyPair = getMockCurrencyPair(CurrencyPairType.MAIN);
        mockCurrencyPair.setName("BTC/USD");

        ExOrder mockExOrder = getMockExOrder();
        mockExOrder.setOperationType(OperationType.INPUT);
        mockExOrder.setCurrencyPair(mockCurrencyPair);

        UserRoleSettings userRoleSettings = new UserRoleSettings();
        userRoleSettings.setUserRole(UserRole.VIP_USER);
        userRoleSettings.setBotAcceptionAllowedOnly(Boolean.FALSE);

        WalletsForOrderAcceptionDto walletsForOrderAcceptionDto = new WalletsForOrderAcceptionDto();
        walletsForOrderAcceptionDto.setOrderStatusId(2);
        walletsForOrderAcceptionDto.setUserCreatorOutWalletId(15);

        Commission commission = new Commission();
        commission.setValue(BigDecimal.TEN);

        when(userService.getIdByEmail(anyString())).thenReturn(100);
        when(orderDao.lockOrdersListForAcception(anyListOf(Integer.class))).thenReturn(Boolean.TRUE);
        when(orderDao.getOrderById(anyInt())).thenReturn(getMockExOrder());
        when(orderDao.getOrderById(anyInt())).thenReturn(mockExOrder);
        when(userService.getUserRoleFromDB(anyInt())).thenReturn(UserRole.USER);
        when(userRoleService.retrieveSettingsForRole(anyInt())).thenReturn(userRoleSettings);
        when(userRoleService.isOrderAcceptionAllowedForUser(anyInt())).thenReturn(Boolean.TRUE);
        when(walletService.getWalletsForOrderByOrderIdAndBlock(anyInt(), anyInt()))
                .thenReturn(walletsForOrderAcceptionDto);
        when(transactionDescription.get(any(OrderStatus.class), any(OrderActionEnum.class))).thenReturn("DESCRIPTION");
        when(currencyService.findCurrencyPairById(anyInt())).thenReturn(mockCurrencyPair);
        when(userService.getUserRoleFromDB(anyInt())).thenReturn(UserRole.ACCOUNTANT);
        when(commissionDao.getCommission(any(OperationType.class), any(UserRole.class))).thenReturn(commission);
        when(walletService.walletInnerTransfer(
                anyInt(),
                any(BigDecimal.class),
                any(TransactionSourceType.class),
                anyInt(),
                anyString())).thenReturn(WalletTransferStatus.SUCCESS);
        when(walletService.walletBalanceChange(any(WalletOperationData.class))).thenReturn(WalletTransferStatus.SUCCESS);
        doNothing().when(companyWalletService).deposit(
                any(CompanyWallet.class),
                any(BigDecimal.class),
                any(BigDecimal.class));
        when(currencyService.findCurrencyPairById(anyInt())).thenReturn(mockCurrencyPair);
        doNothing().when(referralService).processReferral(
                any(ExOrder.class),
                any(BigDecimal.class),
                any(Currency.class),
                anyInt());
        when(orderDao.updateOrder(any(ExOrder.class))).thenReturn(Boolean.TRUE);

        orderService.acceptManyOrdersByAdmin(USER_EMAIL, Collections.singletonList(1), Locale.ENGLISH);

        verify(userService, atLeastOnce()).getIdByEmail(anyString());
        verify(orderDao, atLeastOnce()).lockOrdersListForAcception(anyListOf(Integer.class));
        verify(orderDao, atLeastOnce()).getOrderById(anyInt());
        verify(orderDao, atLeastOnce()).getOrderById(anyInt());
        verify(userService, atLeastOnce()).getUserRoleFromDB(anyInt());
        verify(userRoleService, atLeastOnce()).retrieveSettingsForRole(anyInt());
        verify(userRoleService, atLeastOnce()).isOrderAcceptionAllowedForUser(anyInt());
        verify(walletService, atLeastOnce()).getWalletsForOrderByOrderIdAndBlock(anyInt(), anyInt());
        verify(transactionDescription, atLeastOnce()).get(any(OrderStatus.class), any(OrderActionEnum.class));
        verify(currencyService, atLeastOnce()).findCurrencyPairById(anyInt());
        verify(userService, atLeastOnce()).getUserRoleFromDB(anyInt());
        verify(commissionDao, atLeastOnce()).getCommission(any(OperationType.class), any(UserRole.class));
        verify(walletService, atLeastOnce()).walletInnerTransfer(
                anyInt(),
                any(BigDecimal.class),
                any(TransactionSourceType.class),
                anyInt(),
                anyString());
        verify(walletService, atLeastOnce()).walletBalanceChange(any(WalletOperationData.class));
        verify(companyWalletService, atLeastOnce()).deposit(
                any(CompanyWallet.class),
                any(BigDecimal.class),
                any(BigDecimal.class));
        verify(currencyService, atLeastOnce()).findCurrencyPairById(anyInt());
        verify(referralService, atLeastOnce()).processReferral(
                any(ExOrder.class),
                any(BigDecimal.class),
                any(Currency.class),
                anyInt());
        verify(orderDao, atLeastOnce()).updateOrder(any(ExOrder.class));
    }

    @Test
    public void cancelOrder_exOrder_notNull_true() {
        WalletsForOrderCancelDto walletsForOrderCancelDto = new WalletsForOrderCancelDto();
        walletsForOrderCancelDto.setOrderStatusId(2);

        when(orderDao.getOrderById(anyInt())).thenReturn(getMockExOrder());
        when(userService.getUserEmailFromSecurityContext()).thenReturn(USER_EMAIL);
        when(userService.getEmailById(anyInt())).thenReturn(USER_EMAIL);
        when(userService.getUserLocaleForMobile(anyString())).thenReturn(Locale.ENGLISH);
        when(walletService.getWalletForOrderByOrderIdAndOperationTypeAndBlock(anyInt(), any(OperationType.class)))
                .thenReturn(walletsForOrderCancelDto);
        when(transactionDescription.get(any(OrderStatus.class), any(OrderActionEnum.class))).thenReturn("DESCRIPTION");
        when(walletService.walletInnerTransfer(
                anyInt(),
                any(BigDecimal.class),
                any(TransactionSourceType.class),
                anyInt(),
                anyString())).thenReturn(WalletTransferStatus.SUCCESS);
        when(orderDao.setStatus(anyInt(), any(OrderStatus.class))).thenReturn(Boolean.TRUE);
        doNothing().when(eventPublisher).publishEvent(any(ApplicationEvent.class));

        boolean cancelOrder = orderService.cancelOrder(100);

        assertTrue(cancelOrder);
        verify(orderDao, atLeastOnce()).getOrderById(anyInt());
        verify(userService, atLeastOnce()).getUserEmailFromSecurityContext();
        verify(userService, atLeastOnce()).getEmailById(anyInt());
        verify(userService, atLeastOnce()).getUserLocaleForMobile(anyString());
        verify(walletService, atLeastOnce()).getWalletForOrderByOrderIdAndOperationTypeAndBlock(
                anyInt(),
                any(OperationType.class));
        verify(transactionDescription, atLeastOnce()).get(any(OrderStatus.class), any(OrderActionEnum.class));
        verify(walletService, atLeastOnce()).walletInnerTransfer(
                anyInt(),
                any(BigDecimal.class),
                any(TransactionSourceType.class),
                anyInt(),
                anyString());
        verify(orderDao, atLeastOnce()).setStatus(anyInt(), any(OrderStatus.class));
        verify(eventPublisher, atLeastOnce()).publishEvent(any(ApplicationEvent.class));
    }

    @Test
    public void cancelOrder_exOrder_notNull_false() {
        WalletsForOrderCancelDto walletsForOrderCancelDto = new WalletsForOrderCancelDto();
        walletsForOrderCancelDto.setOrderStatusId(2);

        when(orderDao.getOrderById(anyInt())).thenReturn(getMockExOrder());
        when(userService.getUserEmailFromSecurityContext()).thenReturn(USER_EMAIL);
        when(userService.getEmailById(anyInt())).thenReturn(USER_EMAIL);
        when(userService.getUserLocaleForMobile(anyString())).thenReturn(Locale.ENGLISH);
        when(walletService.getWalletForOrderByOrderIdAndOperationTypeAndBlock(anyInt(), any(OperationType.class)))
                .thenReturn(walletsForOrderCancelDto);
        when(transactionDescription.get(any(OrderStatus.class), any(OrderActionEnum.class))).thenReturn("DESCRIPTION");
        when(walletService.walletInnerTransfer(
                anyInt(),
                any(BigDecimal.class),
                any(TransactionSourceType.class),
                anyInt(),
                anyString())).thenReturn(WalletTransferStatus.SUCCESS);
        when(orderDao.setStatus(anyInt(), any(OrderStatus.class))).thenReturn(Boolean.FALSE);

        boolean cancelOrder = orderService.cancelOrder(100);

        assertFalse(cancelOrder);
        verify(orderDao, atLeastOnce()).getOrderById(anyInt());
        verify(userService, atLeastOnce()).getUserEmailFromSecurityContext();
        verify(userService, atLeastOnce()).getEmailById(anyInt());
        verify(userService, atLeastOnce()).getUserLocaleForMobile(anyString());
        verify(walletService, atLeastOnce()).getWalletForOrderByOrderIdAndOperationTypeAndBlock(
                anyInt(),
                any(OperationType.class));
        verify(transactionDescription, atLeastOnce()).get(any(OrderStatus.class), any(OrderActionEnum.class));
        verify(walletService, atLeastOnce()).walletInnerTransfer(
                anyInt(),
                any(BigDecimal.class),
                any(TransactionSourceType.class),
                anyInt(),
                anyString());
        verify(orderDao, atLeastOnce()).setStatus(anyInt(), any(OrderStatus.class));
    }

    @Test
    public void cancelOrder_exOrder_null_true() {
        when(orderDao.getOrderById(anyInt())).thenReturn(null);
        when(stopOrderService.cancelOrder(anyInt(), any())).thenReturn(Boolean.TRUE);

        boolean cancelOrder = orderService.cancelOrder(100);

        assertTrue(cancelOrder);
        verify(orderDao, atLeastOnce()).getOrderById(anyInt());
        verify(stopOrderService, atLeastOnce()).cancelOrder(anyInt(), any());
    }

    @Test
    public void cancelOrder_exOrder_null_false() {
        when(orderDao.getOrderById(anyInt())).thenReturn(null);
        when(stopOrderService.cancelOrder(anyInt(), any())).thenReturn(Boolean.FALSE);

        boolean cancelOrder = orderService.cancelOrder(100);

        assertFalse(cancelOrder);
        verify(orderDao, atLeastOnce()).getOrderById(anyInt());
        verify(stopOrderService, atLeastOnce()).cancelOrder(anyInt(), any());
    }

    @Test
    public void cancelOpenOrdersByCurrencyPair_false_openedOrders_and_openedStopOrders_empty() {
        when(userService.getIdByEmail(anyString())).thenReturn(100);
        when(orderDao.getOpenedOrdersByCurrencyPair(anyInt(), anyString())).thenReturn(Collections.EMPTY_LIST);
        when(stopOrderService.getOpenedStopOrdersByCurrencyPair(anyInt(), anyString()))
                .thenReturn(Collections.EMPTY_LIST);

        boolean cancelOpenOrdersByCurrencyPair = orderService.cancelOpenOrdersByCurrencyPair("1");

        assertFalse(cancelOpenOrdersByCurrencyPair);
        verify(userService, atLeastOnce()).getIdByEmail(any());
        verify(orderDao, atLeastOnce()).getOpenedOrdersByCurrencyPair(anyInt(), anyString());
        verify(stopOrderService, atLeastOnce()).getOpenedStopOrdersByCurrencyPair(anyInt(), anyString());
    }

    @Test
    public void cancelOpenOrdersByCurrencyPair_true() {
        WalletsForOrderCancelDto walletsForOrderCancelDto = new WalletsForOrderCancelDto();
        walletsForOrderCancelDto.setOrderStatusId(2);

        when(userService.getIdByEmail(anyString())).thenReturn(100);
        when(orderDao.getOpenedOrdersByCurrencyPair(anyInt(), anyString()))
                .thenReturn(Collections.singletonList(getMockExOrder()));
        when(stopOrderService.getOpenedStopOrdersByCurrencyPair(anyInt(), anyString()))
                .thenReturn(Collections.singletonList(1));
        when(stopOrderService.cancelOrder(anyInt(), any())).thenReturn(Boolean.TRUE);
        when(userService.getUserEmailFromSecurityContext()).thenReturn(USER_EMAIL);
        when(userService.getEmailById(anyInt())).thenReturn(USER_EMAIL);
        when(userService.getUserLocaleForMobile(anyString())).thenReturn(Locale.ENGLISH);
        when(walletService.getWalletForOrderByOrderIdAndOperationTypeAndBlock(anyInt(), any(OperationType.class)))
                .thenReturn(walletsForOrderCancelDto);
        when(transactionDescription.get(any(OrderStatus.class), any(OrderActionEnum.class))).thenReturn("DESCRIPTION");
        when(walletService.walletInnerTransfer(
                anyInt(),
                any(BigDecimal.class),
                any(TransactionSourceType.class),
                anyInt(),
                anyString())).thenReturn(WalletTransferStatus.SUCCESS);
        when(orderDao.setStatus(anyInt(), any(OrderStatus.class))).thenReturn(Boolean.TRUE);
        doNothing().when(eventPublisher).publishEvent(any(ApplicationEvent.class));

        boolean cancelOpenOrdersByCurrencyPair = orderService.cancelOpenOrdersByCurrencyPair("1");

        assertTrue(cancelOpenOrdersByCurrencyPair);
        verify(userService, atLeastOnce()).getIdByEmail(any());
        verify(orderDao, atLeastOnce()).getOpenedOrdersByCurrencyPair(anyInt(), anyString());
        verify(stopOrderService, atLeastOnce()).getOpenedStopOrdersByCurrencyPair(anyInt(), anyString());
        verify(stopOrderService, atLeastOnce()).cancelOrder(anyInt(), any());
        verify(userService, atLeastOnce()).getUserEmailFromSecurityContext();
        verify(userService, atLeastOnce()).getEmailById(anyInt());
        verify(userService, atLeastOnce()).getUserLocaleForMobile(anyString());
        verify(walletService, atLeastOnce()).getWalletForOrderByOrderIdAndOperationTypeAndBlock(
                anyInt(),
                any(OperationType.class));
        verify(transactionDescription, atLeastOnce()).get(any(OrderStatus.class), any(OrderActionEnum.class));
        verify(walletService, atLeastOnce()).walletInnerTransfer(
                anyInt(),
                any(BigDecimal.class),
                any(TransactionSourceType.class),
                anyInt(),
                anyString());
        verify(orderDao, atLeastOnce()).setStatus(anyInt(), any(OrderStatus.class));
        verify(eventPublisher, atLeastOnce()).publishEvent(any(ApplicationEvent.class));
    }

    @Test
    public void cancelOpenOrdersByCurrencyPair_false() {
        WalletsForOrderCancelDto walletsForOrderCancelDto = new WalletsForOrderCancelDto();
        walletsForOrderCancelDto.setOrderStatusId(2);

        when(userService.getIdByEmail(anyString())).thenReturn(100);
        when(orderDao.getOpenedOrdersByCurrencyPair(anyInt(), anyString()))
                .thenReturn(Collections.singletonList(getMockExOrder()));
        when(stopOrderService.getOpenedStopOrdersByCurrencyPair(anyInt(), anyString()))
                .thenReturn(Collections.singletonList(1));
        when(stopOrderService.cancelOrder(anyInt(), any())).thenReturn(Boolean.FALSE);
        when(userService.getUserEmailFromSecurityContext()).thenReturn(USER_EMAIL);
        when(userService.getEmailById(anyInt())).thenReturn(USER_EMAIL);
        when(userService.getUserLocaleForMobile(anyString())).thenReturn(Locale.ENGLISH);
        when(walletService.getWalletForOrderByOrderIdAndOperationTypeAndBlock(anyInt(), any(OperationType.class)))
                .thenReturn(walletsForOrderCancelDto);
        when(transactionDescription.get(any(OrderStatus.class), any(OrderActionEnum.class))).thenReturn("DESCRIPTION");
        when(walletService.walletInnerTransfer(
                anyInt(),
                any(BigDecimal.class),
                any(TransactionSourceType.class),
                anyInt(),
                anyString())).thenReturn(WalletTransferStatus.SUCCESS);
        when(orderDao.setStatus(anyInt(), any(OrderStatus.class))).thenReturn(Boolean.TRUE);
        doNothing().when(eventPublisher).publishEvent(any(ApplicationEvent.class));

        boolean cancelOpenOrdersByCurrencyPair = orderService.cancelOpenOrdersByCurrencyPair("1");

        assertFalse(cancelOpenOrdersByCurrencyPair);
        verify(userService, atLeastOnce()).getIdByEmail(any());
        verify(orderDao, atLeastOnce()).getOpenedOrdersByCurrencyPair(anyInt(), anyString());
        verify(stopOrderService, atLeastOnce()).getOpenedStopOrdersByCurrencyPair(anyInt(), anyString());
        verify(stopOrderService, atLeastOnce()).cancelOrder(anyInt(), any());
        verify(userService, atLeastOnce()).getUserEmailFromSecurityContext();
        verify(userService, atLeastOnce()).getEmailById(anyInt());
        verify(userService, atLeastOnce()).getUserLocaleForMobile(anyString());
        verify(walletService, atLeastOnce()).getWalletForOrderByOrderIdAndOperationTypeAndBlock(
                anyInt(),
                any(OperationType.class));
        verify(transactionDescription, atLeastOnce()).get(any(OrderStatus.class), any(OrderActionEnum.class));
        verify(walletService, atLeastOnce()).walletInnerTransfer(
                anyInt(),
                any(BigDecimal.class),
                any(TransactionSourceType.class),
                anyInt(),
                anyString());
        verify(orderDao, atLeastOnce()).setStatus(anyInt(), any(OrderStatus.class));
        verify(eventPublisher, atLeastOnce()).publishEvent(any(ApplicationEvent.class));
    }

    @Test
    public void cancelAllOpenOrders_false_openedOrders_and_openedStopOrders_empty() {
        when(userService.getIdByEmail(anyString())).thenReturn(100);
        when(orderDao.getAllOpenedOrdersByUserId(anyInt())).thenReturn(Collections.EMPTY_LIST);
        when(stopOrderService.getAllOpenedStopOrdersByUserId(anyInt())).thenReturn(Collections.EMPTY_LIST);

        boolean cancelAllOpenOrders = orderService.cancelAllOpenOrders();

        assertFalse(cancelAllOpenOrders);
        verify(userService, atLeastOnce()).getIdByEmail(any());
        verify(orderDao, atLeastOnce()).getAllOpenedOrdersByUserId(anyInt());
        verify(stopOrderService, atLeastOnce()).getAllOpenedStopOrdersByUserId(anyInt());
    }

    @Test
    public void cancelAllOpenOrders_true() {
        WalletsForOrderCancelDto walletsForOrderCancelDto = new WalletsForOrderCancelDto();
        walletsForOrderCancelDto.setOrderStatusId(2);

        when(userService.getIdByEmail(anyString())).thenReturn(100);
        when(orderDao.getAllOpenedOrdersByUserId(anyInt())).thenReturn(Collections.singletonList(getMockExOrder()));
        when(stopOrderService.getAllOpenedStopOrdersByUserId(anyInt())).thenReturn(Collections.singletonList(1));
        when(stopOrderService.cancelOrder(anyInt(), any())).thenReturn(Boolean.TRUE);
        when(userService.getUserEmailFromSecurityContext()).thenReturn(USER_EMAIL);
        when(userService.getEmailById(anyInt())).thenReturn(USER_EMAIL);
        when(userService.getUserLocaleForMobile(anyString())).thenReturn(Locale.ENGLISH);
        when(walletService.getWalletForOrderByOrderIdAndOperationTypeAndBlock(anyInt(), any(OperationType.class)))
                .thenReturn(walletsForOrderCancelDto);
        when(transactionDescription.get(any(OrderStatus.class), any(OrderActionEnum.class))).thenReturn("DESCRIPTION");
        when(walletService.walletInnerTransfer(
                anyInt(),
                any(BigDecimal.class),
                any(TransactionSourceType.class),
                anyInt(),
                anyString())).thenReturn(WalletTransferStatus.SUCCESS);
        when(orderDao.setStatus(anyInt(), any(OrderStatus.class))).thenReturn(Boolean.TRUE);
        doNothing().when(eventPublisher).publishEvent(any(ApplicationEvent.class));

        boolean cancelAllOpenOrders = orderService.cancelAllOpenOrders();

        assertTrue(cancelAllOpenOrders);
        verify(userService, atLeastOnce()).getIdByEmail(any());
        verify(orderDao, atLeastOnce()).getAllOpenedOrdersByUserId(anyInt());
        verify(stopOrderService, atLeastOnce()).getAllOpenedStopOrdersByUserId(anyInt());
        verify(stopOrderService, atLeastOnce()).cancelOrder(anyInt(), any());
        verify(userService, atLeastOnce()).getUserEmailFromSecurityContext();
        verify(userService, atLeastOnce()).getEmailById(anyInt());
        verify(userService, atLeastOnce()).getUserLocaleForMobile(anyString());
        verify(walletService, atLeastOnce()).getWalletForOrderByOrderIdAndOperationTypeAndBlock(
                anyInt(),
                any(OperationType.class));
        verify(transactionDescription, atLeastOnce()).get(any(OrderStatus.class), any(OrderActionEnum.class));
        verify(walletService, atLeastOnce()).walletInnerTransfer(
                anyInt(),
                any(BigDecimal.class),
                any(TransactionSourceType.class),
                anyInt(),
                anyString());
        verify(orderDao, atLeastOnce()).setStatus(anyInt(), any(OrderStatus.class));
        verify(eventPublisher, atLeastOnce()).publishEvent(any(ApplicationEvent.class));
    }

    @Test
    public void cancelAllOpenOrders_false() {
        WalletsForOrderCancelDto walletsForOrderCancelDto = new WalletsForOrderCancelDto();
        walletsForOrderCancelDto.setOrderStatusId(2);

        when(userService.getIdByEmail(anyString())).thenReturn(100);
        when(orderDao.getAllOpenedOrdersByUserId(anyInt())).thenReturn(Collections.singletonList(getMockExOrder()));
        when(stopOrderService.getAllOpenedStopOrdersByUserId(anyInt())).thenReturn(Collections.singletonList(1));
        when(stopOrderService.cancelOrder(anyInt(), any())).thenReturn(Boolean.FALSE);
        when(userService.getUserEmailFromSecurityContext()).thenReturn(USER_EMAIL);
        when(userService.getEmailById(anyInt())).thenReturn(USER_EMAIL);
        when(userService.getUserLocaleForMobile(anyString())).thenReturn(Locale.ENGLISH);
        when(walletService.getWalletForOrderByOrderIdAndOperationTypeAndBlock(anyInt(), any(OperationType.class)))
                .thenReturn(walletsForOrderCancelDto);
        when(transactionDescription.get(any(OrderStatus.class), any(OrderActionEnum.class))).thenReturn("DESCRIPTION");
        when(walletService.walletInnerTransfer(
                anyInt(),
                any(BigDecimal.class),
                any(TransactionSourceType.class),
                anyInt(),
                anyString())).thenReturn(WalletTransferStatus.SUCCESS);
        when(orderDao.setStatus(anyInt(), any(OrderStatus.class))).thenReturn(Boolean.TRUE);
        doNothing().when(eventPublisher).publishEvent(any(ApplicationEvent.class));

        boolean cancelAllOpenOrders = orderService.cancelAllOpenOrders();

        assertFalse(cancelAllOpenOrders);
        verify(userService, atLeastOnce()).getIdByEmail(any());
        verify(orderDao, atLeastOnce()).getAllOpenedOrdersByUserId(anyInt());
        verify(stopOrderService, atLeastOnce()).getAllOpenedStopOrdersByUserId(anyInt());
        verify(stopOrderService, atLeastOnce()).cancelOrder(anyInt(), any());
        verify(userService, atLeastOnce()).getUserEmailFromSecurityContext();
        verify(userService, atLeastOnce()).getEmailById(anyInt());
        verify(userService, atLeastOnce()).getUserLocaleForMobile(anyString());
        verify(walletService, atLeastOnce()).getWalletForOrderByOrderIdAndOperationTypeAndBlock(
                anyInt(),
                any(OperationType.class));
        verify(transactionDescription, atLeastOnce()).get(any(OrderStatus.class), any(OrderActionEnum.class));
        verify(walletService, atLeastOnce()).walletInnerTransfer(
                anyInt(),
                any(BigDecimal.class),
                any(TransactionSourceType.class),
                anyInt(),
                anyString());
        verify(orderDao, atLeastOnce()).setStatus(anyInt(), any(OrderStatus.class));
        verify(eventPublisher, atLeastOnce()).publishEvent(any(ApplicationEvent.class));
    }

    @Test
    public void cancelOrder_locale_null_IncorrectCurrentUserException() {
        ExOrder exOrder = getMockExOrder();

        when(userService.getUserEmailFromSecurityContext()).thenReturn(USER_EMAIL);
        when(userService.getEmailById(anyInt())).thenReturn("test1@test1.com");

        try {
            orderService.cancelOrder(exOrder, null, Collections.singletonList(exOrder), Boolean.TRUE);
        } catch (Exception e) {
            assertTrue(e instanceof IncorrectCurrentUserException);
            assertEquals("Creator email: test1@test1.com and currentUser email: test@test.com are different", e.getMessage());
        }
        verify(userService, atLeastOnce()).getUserEmailFromSecurityContext();
        verify(userService, atLeastOnce()).getEmailById(anyInt());
    }


    @Test
    public void cancelOrder_locale_null_OrderStatus_CANCELLED_CancelOrderException() {
        WalletsForOrderCancelDto walletsForOrderCancelDto = new WalletsForOrderCancelDto();
        walletsForOrderCancelDto.setOrderStatusId(4);

        ExOrder exOrder = getMockExOrder();

        when(userService.getUserEmailFromSecurityContext()).thenReturn(USER_EMAIL);
        when(userService.getEmailById(anyInt())).thenReturn(USER_EMAIL);
        when(userService.getUserLocaleForMobile(anyString())).thenReturn(Locale.ENGLISH);
        when(walletService.getWalletForOrderByOrderIdAndOperationTypeAndBlock(anyInt(), any(OperationType.class)))
                .thenReturn(walletsForOrderCancelDto);
        when(messageSource.getMessage(anyString(), any(), any())).thenReturn("order.cannotcancel.allreadycancelled");

        try {
            orderService.cancelOrder(exOrder, null, Collections.singletonList(exOrder), Boolean.TRUE);
        } catch (Exception e) {
            assertTrue(e instanceof CancelOrderException);
            assertEquals("order.cannotcancel.allreadycancelled", e.getMessage());
        }
        verify(userService, atLeastOnce()).getUserEmailFromSecurityContext();
        verify(userService, atLeastOnce()).getEmailById(anyInt());
        verify(userService, atLeastOnce()).getUserLocaleForMobile(anyString());
        verify(walletService, atLeastOnce()).getWalletForOrderByOrderIdAndOperationTypeAndBlock(
                anyInt(),
                any(OperationType.class));
        verify(messageSource, atLeastOnce()).getMessage(anyString(), any(), any());
    }

    @Test
    public void cancelOrder_locale_null_OrderStatus_CLOSED_CancelOrderException() {
        WalletsForOrderCancelDto walletsForOrderCancelDto = new WalletsForOrderCancelDto();
        walletsForOrderCancelDto.setOrderStatusId(3);

        ExOrder exOrder = getMockExOrder();

        when(userService.getUserEmailFromSecurityContext()).thenReturn(USER_EMAIL);
        when(userService.getEmailById(anyInt())).thenReturn(USER_EMAIL);
        when(userService.getUserLocaleForMobile(anyString())).thenReturn(Locale.ENGLISH);
        when(walletService.getWalletForOrderByOrderIdAndOperationTypeAndBlock(anyInt(), any(OperationType.class)))
                .thenReturn(walletsForOrderCancelDto);
        when(messageSource.getMessage(anyString(), any(), any())).thenReturn("order.cannotcancel");

        try {
            orderService.cancelOrder(exOrder, null, Collections.singletonList(exOrder), Boolean.TRUE);
        } catch (Exception e) {
            assertTrue(e instanceof CancelOrderException);
            assertEquals("order.cannotcancel", e.getMessage());
        }
        verify(userService, atLeastOnce()).getUserEmailFromSecurityContext();
        verify(userService, atLeastOnce()).getEmailById(anyInt());
        verify(userService, atLeastOnce()).getUserLocaleForMobile(anyString());
        verify(walletService, atLeastOnce()).getWalletForOrderByOrderIdAndOperationTypeAndBlock(
                anyInt(),
                any(OperationType.class));
        verify(messageSource, atLeastOnce()).getMessage(anyString(), any(), any());
    }

    @Test
    public void cancelOrder_locale_null_OrderStatus_CLOSED_OrderCancellingException() {
        WalletsForOrderCancelDto walletsForOrderCancelDto = new WalletsForOrderCancelDto();
        walletsForOrderCancelDto.setOrderStatusId(2);

        ExOrder exOrder = getMockExOrder();

        when(userService.getUserEmailFromSecurityContext()).thenReturn(USER_EMAIL);
        when(userService.getEmailById(anyInt())).thenReturn(USER_EMAIL);
        when(userService.getUserLocaleForMobile(anyString())).thenReturn(Locale.ENGLISH);
        when(walletService.getWalletForOrderByOrderIdAndOperationTypeAndBlock(anyInt(), any(OperationType.class)))
                .thenReturn(walletsForOrderCancelDto);
        when(transactionDescription.get(any(OrderStatus.class), any(OrderActionEnum.class))).thenReturn("DESCRIPTION");
        when(walletService.walletInnerTransfer(
                anyInt(),
                any(BigDecimal.class),
                any(TransactionSourceType.class),
                anyInt(),
                anyString())).thenReturn(WalletTransferStatus.WALLET_NOT_FOUND);
        try {
            orderService.cancelOrder(exOrder, null, Collections.singletonList(exOrder), Boolean.TRUE);
        } catch (Exception e) {
            assertTrue(e instanceof OrderCancellingException);
            assertEquals("WALLET_NOT_FOUND", e.getMessage());
        }
        verify(userService, atLeastOnce()).getUserEmailFromSecurityContext();
        verify(userService, atLeastOnce()).getEmailById(anyInt());
        verify(userService, atLeastOnce()).getUserLocaleForMobile(anyString());
        verify(walletService, atLeastOnce()).getWalletForOrderByOrderIdAndOperationTypeAndBlock(
                anyInt(),
                any(OperationType.class));
        verify(transactionDescription, atLeastOnce()).get(any(OrderStatus.class), any(OrderActionEnum.class));
        verify(walletService, atLeastOnce()).walletInnerTransfer(
                anyInt(),
                any(BigDecimal.class),
                any(TransactionSourceType.class),
                anyInt(),
                anyString());
    }

    @Test
    public void cancelOrder_locale_null_false() {
        WalletsForOrderCancelDto walletsForOrderCancelDto = new WalletsForOrderCancelDto();
        walletsForOrderCancelDto.setOrderStatusId(2);

        ExOrder exOrder = getMockExOrder();

        when(userService.getUserEmailFromSecurityContext()).thenReturn(USER_EMAIL);
        when(userService.getEmailById(anyInt())).thenReturn(USER_EMAIL);
        when(userService.getUserLocaleForMobile(anyString())).thenReturn(Locale.ENGLISH);
        when(walletService.getWalletForOrderByOrderIdAndOperationTypeAndBlock(anyInt(), any(OperationType.class)))
                .thenReturn(walletsForOrderCancelDto);
        when(transactionDescription.get(any(OrderStatus.class), any(OrderActionEnum.class))).thenReturn("DESCRIPTION");
        when(walletService.walletInnerTransfer(
                anyInt(),
                any(BigDecimal.class),
                any(TransactionSourceType.class),
                anyInt(),
                anyString())).thenReturn(WalletTransferStatus.SUCCESS);
        when(orderDao.setStatus(anyInt(), any(OrderStatus.class))).thenReturn(Boolean.FALSE);

        boolean cancelOrder = orderService.cancelOrder(
                exOrder,
                null,
                Collections.singletonList(exOrder),
                Boolean.TRUE);

        assertFalse(cancelOrder);

        verify(userService, atLeastOnce()).getUserEmailFromSecurityContext();
        verify(userService, atLeastOnce()).getEmailById(anyInt());
        verify(userService, atLeastOnce()).getUserLocaleForMobile(anyString());
        verify(walletService, atLeastOnce()).getWalletForOrderByOrderIdAndOperationTypeAndBlock(
                anyInt(),
                any(OperationType.class));
        verify(orderDao, atLeastOnce()).setStatus(anyInt(), any(OrderStatus.class));
        verify(transactionDescription, atLeastOnce()).get(any(OrderStatus.class), any(OrderActionEnum.class));
        verify(walletService, atLeastOnce()).walletInnerTransfer(
                anyInt(),
                any(BigDecimal.class),
                any(TransactionSourceType.class),
                anyInt(),
                anyString());
    }

    @Test
    public void cancelOrder_locale_null_true_forPartialAccept_false() {
        WalletsForOrderCancelDto walletsForOrderCancelDto = new WalletsForOrderCancelDto();
        walletsForOrderCancelDto.setOrderStatusId(2);

        ExOrder exOrder = getMockExOrder();

        when(userService.getUserEmailFromSecurityContext()).thenReturn(USER_EMAIL);
        when(userService.getEmailById(anyInt())).thenReturn(USER_EMAIL);
        when(userService.getUserLocaleForMobile(anyString())).thenReturn(Locale.ENGLISH);
        when(walletService.getWalletForOrderByOrderIdAndOperationTypeAndBlock(anyInt(), any(OperationType.class)))
                .thenReturn(walletsForOrderCancelDto);
        when(transactionDescription.get(any(OrderStatus.class), any(OrderActionEnum.class))).thenReturn("DESCRIPTION");
        when(walletService.walletInnerTransfer(
                anyInt(),
                any(BigDecimal.class),
                any(TransactionSourceType.class),
                anyInt(),
                anyString())).thenReturn(WalletTransferStatus.SUCCESS);
        when(orderDao.setStatus(anyInt(), any(OrderStatus.class))).thenReturn(Boolean.TRUE);
        doNothing().when(eventPublisher).publishEvent(any(ApplicationEvent.class));

        boolean cancelOrder = orderService.cancelOrder(
                exOrder,
                null,
                Collections.singletonList(exOrder),
                Boolean.FALSE);

        assertTrue(cancelOrder);

        verify(userService, atLeastOnce()).getUserEmailFromSecurityContext();
        verify(userService, atLeastOnce()).getEmailById(anyInt());
        verify(userService, atLeastOnce()).getUserLocaleForMobile(anyString());
        verify(walletService, atLeastOnce()).getWalletForOrderByOrderIdAndOperationTypeAndBlock(
                anyInt(),
                any(OperationType.class));
        verify(transactionDescription, atLeastOnce()).get(any(OrderStatus.class), any(OrderActionEnum.class));
        verify(walletService, atLeastOnce()).walletInnerTransfer(
                anyInt(),
                any(BigDecimal.class),
                any(TransactionSourceType.class),
                anyInt(),
                anyString());
        verify(orderDao, atLeastOnce()).setStatus(anyInt(), any(OrderStatus.class));
        verify(eventPublisher, atLeastOnce()).publishEvent(any(ApplicationEvent.class));
    }

    @Test
    public void cancelOrder_locale_null_true_forPartialAccept_true() {
        WalletsForOrderCancelDto walletsForOrderCancelDto = new WalletsForOrderCancelDto();
        walletsForOrderCancelDto.setOrderStatusId(2);

        ExOrder exOrder = getMockExOrder();
        List<ExOrder> acceptEventsList = new ArrayList<>();
        acceptEventsList.add(exOrder);

        when(userService.getUserEmailFromSecurityContext()).thenReturn(USER_EMAIL);
        when(userService.getEmailById(anyInt())).thenReturn(USER_EMAIL);
        when(userService.getUserLocaleForMobile(anyString())).thenReturn(Locale.ENGLISH);
        when(walletService.getWalletForOrderByOrderIdAndOperationTypeAndBlock(anyInt(), any(OperationType.class)))
                .thenReturn(walletsForOrderCancelDto);
        when(transactionDescription.get(
                any(OrderStatus.class),
                any(OrderActionEnum.class))).thenReturn("DESCRIPTION");
        when(walletService.walletInnerTransfer(
                anyInt(), any(BigDecimal.class),
                any(TransactionSourceType.class),
                anyInt(),
                anyString())).thenReturn(WalletTransferStatus.SUCCESS);
        when(orderDao.setStatus(anyInt(), any(OrderStatus.class))).thenReturn(Boolean.TRUE);
        doNothing().when(eventPublisher).publishEvent(any(ApplicationEvent.class));

        boolean cancelOrder = orderService.cancelOrder(exOrder, null, acceptEventsList, Boolean.TRUE);

        assertTrue(cancelOrder);

        verify(userService, atLeastOnce()).getUserEmailFromSecurityContext();
        verify(userService, atLeastOnce()).getEmailById(anyInt());
        verify(userService, atLeastOnce()).getUserLocaleForMobile(anyString());
        verify(walletService, atLeastOnce()).getWalletForOrderByOrderIdAndOperationTypeAndBlock(
                anyInt(),
                any(OperationType.class));
        verify(transactionDescription, atLeastOnce()).get(any(OrderStatus.class), any(OrderActionEnum.class));
        verify(walletService, atLeastOnce()).walletInnerTransfer(
                anyInt(),
                any(BigDecimal.class),
                any(TransactionSourceType.class),
                anyInt(),
                anyString());
        verify(orderDao, atLeastOnce()).setStatus(anyInt(), any(OrderStatus.class));
        verify(eventPublisher, atLeastOnce()).publishEvent(any(ApplicationEvent.class));
    }

    @Test
    public void updateOrder_true() {
        when(orderDao.updateOrder(any(ExOrder.class))).thenReturn(Boolean.TRUE);

        boolean updateOrder = orderService.updateOrder(getMockExOrder());

        assertTrue(updateOrder);
        verify(orderDao, atLeastOnce()).updateOrder(any(ExOrder.class));
    }

    @Test
    public void updateOrder_false() {
        when(orderDao.updateOrder(any(ExOrder.class))).thenReturn(Boolean.FALSE);

        boolean updateOrder = orderService.updateOrder(getMockExOrder());

        assertFalse(updateOrder);
        verify(orderDao, atLeastOnce()).updateOrder(any(ExOrder.class));
    }

    @Test
    public void getCoinmarketData() {
        CurrencyPair mockCurrencyPair = getMockCurrencyPair(CurrencyPairType.ALL);
        mockCurrencyPair.setName("BTC/USD");

        when(orderDao.getCoinmarketData(anyString())).thenReturn(Collections.singletonList(getMockCoinmarketApiDto()));
        when(currencyService.getAllCurrencyPairs(any(CurrencyPairType.class)))
                .thenReturn(Collections.singletonList(mockCurrencyPair));

        List<CoinmarketApiDto> coinmarketData = orderService.getCoinmarketData("BTC/USD", new BackDealInterval());

        assertNotNull(coinmarketData);
        assertEquals(1, coinmarketData.size());
        assertEquals(Integer.valueOf(mockCurrencyPair.getId()), coinmarketData.get(0).getCurrencyPairId());
        assertEquals(mockCurrencyPair.getName(), coinmarketData.get(0).getCurrency_pair_name());
        assertEquals(BigDecimal.TEN, coinmarketData.get(0).getFirst());
        assertEquals(BigDecimal.ONE, coinmarketData.get(0).getLast());

        verify(orderDao, atLeastOnce()).getCoinmarketData(anyString());
        verify(currencyService, atLeastOnce()).getAllCurrencyPairs(any(CurrencyPairType.class));
    }

    @Test
    public void getCoinmarketDataForActivePairs() {
        CoinmarketApiDto dto = getMockCoinmarketApiDto();

        when(orderDao.getCoinmarketData(anyString())).thenReturn(Collections.singletonList(dto));

        List<CoinmarketApiDto> hourlyCoinmarketData = orderService.getCoinmarketDataForActivePairs(
                "BTC/USD",
                new BackDealInterval());

        assertNotNull(hourlyCoinmarketData);
        assertEquals(1, hourlyCoinmarketData.size());
        assertEquals(dto.getCurrencyPairId(), hourlyCoinmarketData.get(0).getCurrencyPairId());
        assertEquals(dto.getCurrency_pair_name(), hourlyCoinmarketData.get(0).getCurrency_pair_name());
        assertEquals(dto.getFirst(), hourlyCoinmarketData.get(0).getFirst());
        assertEquals(dto.getLast(), hourlyCoinmarketData.get(0).getLast());

        verify(orderDao, atLeastOnce()).getCoinmarketData(anyString());
    }

    @Test
    public void getDailyCoinmarketData_currencyPairName_empty() {
        CoinmarketApiDto dto = getMockCoinmarketApiDto();

        when(orderDao.getCoinmarketData(anyString())).thenReturn(Collections.singletonList(dto));
        when(orderDao.getCoinmarketData(anyString())).thenReturn(Collections.singletonList(dto));

        List<CoinmarketApiDto> hourlyCoinmarketData = orderService.getDailyCoinmarketData("");

        assertNotNull(hourlyCoinmarketData);
        assertEquals(1, hourlyCoinmarketData.size());
        assertEquals(dto.getCurrencyPairId(), hourlyCoinmarketData.get(0).getCurrencyPairId());
        assertEquals(dto.getCurrency_pair_name(), hourlyCoinmarketData.get(0).getCurrency_pair_name());
        assertEquals(dto.getFirst(), hourlyCoinmarketData.get(0).getFirst());
        assertEquals(dto.getLast(), hourlyCoinmarketData.get(0).getLast());

        verify(orderDao, atLeastOnce()).getCoinmarketData(anyString());
    }

    @Test
    public void getHourlyCoinmarketData() {
        CoinmarketApiDto dto = getMockCoinmarketApiDto();

        when(orderDao.getCoinmarketData(anyString())).thenReturn(Collections.singletonList(dto));

        List<CoinmarketApiDto> hourlyCoinmarketData = orderService.getHourlyCoinmarketData("BTC/USD");

        assertNotNull(hourlyCoinmarketData);
        assertEquals(1, hourlyCoinmarketData.size());
        assertEquals(dto.getCurrencyPairId(), hourlyCoinmarketData.get(0).getCurrencyPairId());
        assertEquals(dto.getCurrency_pair_name(), hourlyCoinmarketData.get(0).getCurrency_pair_name());
        assertEquals(dto.getFirst(), hourlyCoinmarketData.get(0).getFirst());
        assertEquals(dto.getLast(), hourlyCoinmarketData.get(0).getLast());

        verify(orderDao, atLeastOnce()).getCoinmarketData(anyString());
    }

    @Test
    public void getOrderInfo() {
        OrderInfoDto dto = new OrderInfoDto();

        when(orderDao.getOrderInfo(anyInt(), any(Locale.class))).thenReturn(dto);

        OrderInfoDto orderInfo = orderService.getOrderInfo(100, Locale.ENGLISH);

        assertNotNull(orderInfo);
        assertEquals(dto, orderInfo);

        verify(orderDao, atLeastOnce()).getOrderInfo(anyInt(), any(Locale.class));
    }

    @Test
    public void getAdminOrderInfo_getUserRoleFromDB_TRADER() {
        OrderInfoDto dto = new OrderInfoDto();
        dto.setOrderCreatorEmail(USER_EMAIL);

        when(orderDao.getOrderInfo(anyInt(), any(Locale.class))).thenReturn(dto);
        when(userService.getUserRoleFromDB(anyString())).thenReturn(UserRole.TRADER);
        when(userService.getUserRoleFromSecurityContext()).thenReturn(UserRole.TRADER);
        when(userRoleService.getRealUserRoleIdByBusinessRoleList(any(BusinessUserRoleEnum.class)))
                .thenReturn(Collections.singletonList(8));
        when(messageSource.getMessage(anyString(), any(), any(Locale.class))).thenReturn("admin.orders.accept.warning");

        AdminOrderInfoDto adminOrderInfo = orderService.getAdminOrderInfo(1, Locale.ENGLISH);

        assertNotNull(adminOrderInfo);
        assertTrue(adminOrderInfo.isAcceptable());
        assertEquals("admin.orders.accept.warning", adminOrderInfo.getNotification());

        verify(orderDao, atLeastOnce()).getOrderInfo(anyInt(), any(Locale.class));
        verify(userService, atLeastOnce()).getUserRoleFromDB(anyString());
        verify(userService, atLeastOnce()).getUserRoleFromSecurityContext();
        verify(userRoleService, atLeastOnce()).getRealUserRoleIdByBusinessRoleList(any(BusinessUserRoleEnum.class));
        verify(messageSource, atLeastOnce()).getMessage(anyString(), any(), any(Locale.class));

        reset(userService);
    }

    @Test
    public void getAdminOrderInfo_getUserRoleFromDB_not_TRADER() {
        OrderInfoDto dto = new OrderInfoDto();
        dto.setOrderCreatorEmail(USER_EMAIL);

        when(orderDao.getOrderInfo(anyInt(), any(Locale.class))).thenReturn(dto);
        when(userService.getUserRoleFromDB(anyString())).thenReturn(UserRole.USER);
        when(userService.getUserRoleFromSecurityContext()).thenReturn(UserRole.TRADER);
        when(messageSource.getMessage(anyString(), any(), any(Locale.class))).thenReturn("admin.orders.cantaccept");

        AdminOrderInfoDto adminOrderInfo = orderService.getAdminOrderInfo(1, Locale.ENGLISH);

        assertNotNull(adminOrderInfo);
        assertFalse(adminOrderInfo.isAcceptable());
        assertEquals("admin.orders.cantaccept", adminOrderInfo.getNotification());

        verify(orderDao, atLeastOnce()).getOrderInfo(anyInt(), any(Locale.class));
        verify(userService, atLeastOnce()).getUserRoleFromDB(anyString());
        verify(userService, atLeastOnce()).getUserRoleFromSecurityContext();
        verify(messageSource, atLeastOnce()).getMessage(anyString(), any(), any(Locale.class));

        reset(userService);
    }

    @Test
    public void getAdminOrderInfo_getUserRoleFromDB_default() {
        OrderInfoDto dto = new OrderInfoDto();
        dto.setOrderCreatorEmail(USER_EMAIL);

        when(orderDao.getOrderInfo(anyInt(), any(Locale.class))).thenReturn(dto);
        when(userService.getUserRoleFromDB(anyString())).thenReturn(UserRole.USER);
        when(userService.getUserRoleFromSecurityContext()).thenReturn(UserRole.ACCOUNTANT);

        AdminOrderInfoDto adminOrderInfo = orderService.getAdminOrderInfo(1, Locale.ENGLISH);

        assertNotNull(adminOrderInfo);
        assertTrue(adminOrderInfo.isAcceptable());

        verify(orderDao, atLeastOnce()).getOrderInfo(anyInt(), any(Locale.class));
        verify(userService, atLeastOnce()).getUserRoleFromDB(anyString());
        verify(userService, atLeastOnce()).getUserRoleFromSecurityContext();

        reset(userService);
    }

    @Test
    public void deleteManyOrdersByAdmin() {
        OrderRoleInfoForDelete orderRoleInfo = new OrderRoleInfoForDelete(
                OrderStatus.CLOSED,
                UserRole.BOT_TRADER,
                UserRole.BOT_TRADER,
                1);
        OrderDetailDto dto = new OrderDetailDto(
                1,
                7,
                BigDecimal.TEN,
                11,
                15,
                3,
                BigDecimal.TEN,
                18,
                19,
                BigDecimal.ONE);

        when(orderDao.getMyOrderById(anyInt())).thenReturn(new OrderCreateDto());
        when(orderDao.getOrderRoleInfo(anyInt())).thenReturn(orderRoleInfo);
        when(walletService.getOrderRelatedDataAndBlock(anyInt())).thenReturn(Collections.singletonList(dto));
        when(transactionDescription.get(any(OrderStatus.class), any(OrderActionEnum.class))).thenReturn("DESCRIPTION");
        when(orderDao.setStatus(anyInt(), any(OrderStatus.class))).thenReturn(Boolean.TRUE);
        when(orderDao.getOrderById(anyInt())).thenReturn(getMockExOrder());

        orderService.deleteManyOrdersByAdmin(Collections.singletonList(1));

        verify(orderDao, atLeastOnce()).getMyOrderById(anyInt());
        verify(orderDao, atLeastOnce()).getOrderRoleInfo(anyInt());
        verify(walletService, atLeastOnce()).getOrderRelatedDataAndBlock(anyInt());
        verify(transactionDescription, atLeastOnce()).get(any(OrderStatus.class), any(OrderActionEnum.class));
        verify(orderDao, atLeastOnce()).setStatus(anyInt(), any(OrderStatus.class));
        verify(orderDao, atLeastOnce()).getOrderById(anyInt());
    }

    @Test
    public void deleteOrderByAdmin_equals_one() {
        OrderRoleInfoForDelete orderRoleInfo = new OrderRoleInfoForDelete(
                OrderStatus.CLOSED,
                UserRole.BOT_TRADER,
                UserRole.BOT_TRADER,
                0);

        when(orderDao.getMyOrderById(anyInt())).thenReturn(new OrderCreateDto());
        when(orderDao.getOrderRoleInfo(anyInt())).thenReturn(orderRoleInfo);
        when(orderDao.setStatus(anyInt(), any(OrderStatus.class))).thenReturn(Boolean.TRUE);

        Integer deleteOrderByAdmin = (Integer) orderService.deleteOrderByAdmin(1);

        assertNotNull(deleteOrderByAdmin);
        assertEquals(Integer.valueOf(1), deleteOrderByAdmin);

        verify(orderDao, atLeastOnce()).getMyOrderById(anyInt());
        verify(orderDao, atLeastOnce()).getOrderRoleInfo(anyInt());
        verify(orderDao, atLeastOnce()).setStatus(anyInt(), any(OrderStatus.class));
    }

    @Test
    public void deleteOrderByAdmin_equals_zero() {
        OrderRoleInfoForDelete orderRoleInfo = new OrderRoleInfoForDelete(
                OrderStatus.CLOSED,
                UserRole.BOT_TRADER,
                UserRole.BOT_TRADER,
                1);

        when(orderDao.getMyOrderById(anyInt())).thenReturn(new OrderCreateDto());
        when(orderDao.getOrderRoleInfo(anyInt())).thenReturn(orderRoleInfo);
        when(walletService.getOrderRelatedDataAndBlock(anyInt())).thenReturn(Collections.EMPTY_LIST);

        Integer deleteOrderByAdmin = (Integer) orderService.deleteOrderByAdmin(1);

        assertNotNull(deleteOrderByAdmin);
        assertEquals(Integer.valueOf(0), deleteOrderByAdmin);

        verify(orderDao, atLeastOnce()).getMyOrderById(anyInt());
        verify(orderDao, atLeastOnce()).getOrderRoleInfo(anyInt());
        verify(walletService, atLeastOnce()).getOrderRelatedDataAndBlock(anyInt());
    }

    @Test
    public void deleteOrderByAdmin_equals_OrderDeletingException() {
        OrderRoleInfoForDelete orderRoleInfo = new OrderRoleInfoForDelete(
                OrderStatus.CLOSED,
                UserRole.BOT_TRADER,
                UserRole.BOT_TRADER,
                1);
        OrderDetailDto dto = new OrderDetailDto(
                1,
                7,
                BigDecimal.TEN,
                11,
                15,
                3,
                BigDecimal.TEN,
                18,
                19,
                BigDecimal.ONE);

        when(orderDao.getMyOrderById(anyInt())).thenReturn(new OrderCreateDto());
        when(orderDao.getOrderRoleInfo(anyInt())).thenReturn(orderRoleInfo);
        when(walletService.getOrderRelatedDataAndBlock(anyInt())).thenReturn(Collections.singletonList(dto));
        when(transactionDescription.get(any(OrderStatus.class), any(OrderActionEnum.class))).thenReturn("DESCRIPTION");
        when(orderDao.setStatus(anyInt(), any(OrderStatus.class))).thenReturn(Boolean.FALSE);

        try {
            orderService.deleteOrderByAdmin(1);
        } catch (Exception e) {
            assertTrue(e instanceof OrderDeletingException);
            assertEquals(OrderDeleteStatus.ORDER_UPDATE_ERROR.toString(), e.getMessage());
        }

        verify(orderDao, atLeastOnce()).getMyOrderById(anyInt());
        verify(orderDao, atLeastOnce()).getOrderRoleInfo(anyInt());
        verify(walletService, atLeastOnce()).getOrderRelatedDataAndBlock(anyInt());
        verify(transactionDescription, atLeastOnce()).get(any(OrderStatus.class), any(OrderActionEnum.class));
        verify(orderDao, atLeastOnce()).setStatus(anyInt(), any(OrderStatus.class));
    }

    @Test
    public void deleteOrderByAdmin() {
        OrderRoleInfoForDelete orderRoleInfo = new OrderRoleInfoForDelete(
                OrderStatus.CLOSED,
                UserRole.BOT_TRADER,
                UserRole.BOT_TRADER,
                1);
        OrderDetailDto dto = new OrderDetailDto(
                1,
                7,
                BigDecimal.TEN,
                11,
                15,
                3,
                BigDecimal.TEN,
                18,
                19,
                BigDecimal.ONE);

        when(orderDao.getMyOrderById(anyInt())).thenReturn(new OrderCreateDto());
        when(orderDao.getOrderRoleInfo(anyInt())).thenReturn(orderRoleInfo);
        when(walletService.getOrderRelatedDataAndBlock(anyInt())).thenReturn(Collections.singletonList(dto));
        when(transactionDescription.get(any(OrderStatus.class), any(OrderActionEnum.class))).thenReturn("DESCRIPTION");
        when(orderDao.setStatus(anyInt(), any(OrderStatus.class))).thenReturn(Boolean.TRUE);
        when(orderDao.getOrderById(anyInt())).thenReturn(getMockExOrder());

        Integer deleteOrderByAdmin = (Integer) orderService.deleteOrderByAdmin(1);

        assertNotNull(deleteOrderByAdmin);
        assertEquals(Integer.valueOf(1), deleteOrderByAdmin);


        verify(orderDao, atLeastOnce()).getMyOrderById(anyInt());
        verify(orderDao, atLeastOnce()).getOrderRoleInfo(anyInt());
        verify(walletService, atLeastOnce()).getOrderRelatedDataAndBlock(anyInt());
        verify(transactionDescription, atLeastOnce()).get(any(OrderStatus.class), any(OrderActionEnum.class));
        verify(orderDao, atLeastOnce()).setStatus(anyInt(), any(OrderStatus.class));
        verify(orderDao, atLeastOnce()).setStatus(anyInt(), any(OrderStatus.class));
    }

    @Test
    public void deleteOrderForPartialAccept_exception() {
        try {
            Object deleteOrderForPartialAccept = orderService.deleteOrderForPartialAccept(1, Collections.singletonList(getMockExOrder()));
        } catch (Exception e) {
            assertTrue(e instanceof OrderDeletingException);
            assertEquals(OrderDeleteStatus.NOT_FOUND.toString(), e.getMessage());
        }
    }

    @Test
    public void deleteOrderForPartialAccept() {
        List<ExOrder> acceptEventsList = new ArrayList<>();
        OrderDetailDto dto = new OrderDetailDto(
                1,
                7,
                BigDecimal.TEN,
                11,
                15,
                3,
                BigDecimal.TEN,
                18,
                19,
                BigDecimal.ONE);

        when(walletService.getOrderRelatedDataAndBlock(anyInt())).thenReturn(Collections.singletonList(dto));
        when(transactionDescription.get(any(OrderStatus.class), any(OrderActionEnum.class))).thenReturn("DESCRIPTION");
        when(orderDao.setStatus(anyInt(), any(OrderStatus.class))).thenReturn(Boolean.TRUE);
        when(orderDao.getOrderById(anyInt())).thenReturn(getMockExOrder());


        Integer deleteOrderForPartialAccept = (Integer) orderService.deleteOrderForPartialAccept(1, acceptEventsList);
        assertNotNull(deleteOrderForPartialAccept);
        assertEquals(Integer.valueOf(1), deleteOrderForPartialAccept);

        verify(walletService, atLeastOnce()).getOrderRelatedDataAndBlock(anyInt());
        verify(transactionDescription, atLeastOnce()).get(any(OrderStatus.class), any(OrderActionEnum.class));
        verify(orderDao, atLeastOnce()).setStatus(anyInt(), any(OrderStatus.class));
        verify(orderDao, atLeastOnce()).getOrderById(anyInt());
    }


    @Test
    public void searchOrderByAdmin() {
        when(orderDao.searchOrderByAdmin(
                anyInt(),
                anyInt(),
                anyString(),
                any(BigDecimal.class),
                any(BigDecimal.class))).thenReturn(100);

        Integer orderByAdmin = orderService.searchOrderByAdmin(
                100,
                "BUY",
                "2019-10-15",
                BigDecimal.TEN,
                BigDecimal.ONE);

        assertEquals(Integer.valueOf(100), orderByAdmin);
        verify(orderDao, atLeastOnce()).searchOrderByAdmin(
                anyInt(),
                anyInt(),
                anyString(),
                any(BigDecimal.class),
                any(BigDecimal.class));
    }

    @Test
    public void getOrderAcceptedForPeriod() {
        HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
        CacheData mockCacheData = getMockCacheData(request);
        OrderAcceptedHistoryDto dto = getMockOrderAcceptedHistoryDto();
        when(orderDao.getOrderAcceptedForPeriod(
                anyString(),
                any(BackDealInterval.class),
                anyInt(),
                any(CurrencyPair.class))).thenReturn(Collections.singletonList(dto));

        List<OrderAcceptedHistoryDto> orderAcceptedForPeriod = orderService.getOrderAcceptedForPeriod(
                mockCacheData,
                USER_EMAIL,
                new BackDealInterval(),
                10,
                new CurrencyPair(),
                Locale.ENGLISH);

        assertNotNull(orderAcceptedForPeriod);
        assertEquals(1, orderAcceptedForPeriod.size());
        assertEquals(dto.getOrderId(), orderAcceptedForPeriod.get(0).getOrderId());
        assertEquals(dto.getDateAcceptionTime(), orderAcceptedForPeriod.get(0).getDateAcceptionTime());
        assertEquals(dto.getAcceptionTime(), orderAcceptedForPeriod.get(0).getAcceptionTime());
        assertEquals("1.500000000", orderAcceptedForPeriod.get(0).getRate());
        assertEquals("25.000000000", orderAcceptedForPeriod.get(0).getAmountBase());
        assertEquals(dto.getOperationType(), orderAcceptedForPeriod.get(0).getOperationType());

        verify(orderDao, atLeastOnce()).getOrderAcceptedForPeriod(
                anyString(),
                any(BackDealInterval.class),
                anyInt(),
                any(CurrencyPair.class));
    }

    @Test
    public void getOrderAcceptedForPeriodEx() {
        OrderAcceptedHistoryDto dto = getMockOrderAcceptedHistoryDto();
        when(orderDao.getOrderAcceptedForPeriod(
                anyString(),
                any(BackDealInterval.class),
                anyInt(),
                any(CurrencyPair.class))).thenReturn(Collections.singletonList(dto));

        List<OrderAcceptedHistoryDto> orderAcceptedForPeriodEx = orderService.getOrderAcceptedForPeriodEx(
                USER_EMAIL,
                new BackDealInterval(),
                10,
                new CurrencyPair(),
                Locale.ENGLISH);

        assertNotNull(orderAcceptedForPeriodEx);
        assertEquals(1, orderAcceptedForPeriodEx.size());
        assertEquals(dto.getOrderId(), orderAcceptedForPeriodEx.get(0).getOrderId());
        assertEquals(dto.getDateAcceptionTime(), orderAcceptedForPeriodEx.get(0).getDateAcceptionTime());
        assertEquals(dto.getAcceptionTime(), orderAcceptedForPeriodEx.get(0).getAcceptionTime());
        assertEquals("1.500000000", orderAcceptedForPeriodEx.get(0).getRate());
        assertEquals("25.000000000", orderAcceptedForPeriodEx.get(0).getAmountBase());
        assertEquals(dto.getOperationType(), orderAcceptedForPeriodEx.get(0).getOperationType());

        verify(orderDao, atLeastOnce()).getOrderAcceptedForPeriod(
                anyString(),
                any(BackDealInterval.class),
                anyInt(),
                any(CurrencyPair.class));
    }

    @Test
    public void getCommissionForOrder() {
        OrderCommissionsDto dto = new OrderCommissionsDto();
        dto.setBuyCommission(BigDecimal.ZERO);
        dto.setSellCommission(BigDecimal.ONE);

        when(userService.getUserRoleFromSecurityContext()).thenReturn(UserRole.USER);
        when(orderDao.getCommissionForOrder(any(UserRole.class))).thenReturn(dto);

        OrderCommissionsDto commissionForOrder = orderService.getCommissionForOrder();

        assertNotNull(commissionForOrder);
        assertEquals(dto.getBuyCommission(), commissionForOrder.getBuyCommission());
        assertEquals(dto.getSellCommission(), commissionForOrder.getSellCommission());

        verify(userService, atLeastOnce()).getUserRoleFromSecurityContext();
        verify(orderDao, atLeastOnce()).getCommissionForOrder(any(UserRole.class));
    }

    @Test
    public void getAllCommissions() {
        CommissionsDto dto = new CommissionsDto();
        dto.setInputCommission(BigDecimal.ONE);
        dto.setOutputCommission(BigDecimal.TEN);
        dto.setSellCommission(BigDecimal.ZERO);
        dto.setBuyCommission(BigDecimal.ZERO);
        dto.setTransferCommission(BigDecimal.ONE);

        when(userService.getUserRoleFromSecurityContext()).thenReturn(UserRole.USER);
        when(orderDao.getAllCommissions(any(UserRole.class))).thenReturn(dto);

        CommissionsDto allCommissions = orderService.getAllCommissions();

        assertNotNull(allCommissions);
        assertEquals(dto.getBuyCommission(), allCommissions.getBuyCommission());
        assertEquals(dto.getInputCommission(), allCommissions.getInputCommission());
        assertEquals(dto.getOutputCommission(), allCommissions.getOutputCommission());
        assertEquals(dto.getSellCommission(), allCommissions.getSellCommission());
        assertEquals(dto.getTransferCommission(), allCommissions.getTransferCommission());

        verify(userService, atLeastOnce()).getUserRoleFromSecurityContext();
        verify(orderDao, atLeastOnce()).getAllCommissions(any(UserRole.class));
    }

    @Test
    public void getAllBuyOrders() {
        List<OrderListDto> mockOrderListDto = getMockOrderListDto();

        HttpSession session = Mockito.mock(HttpSession.class);
        session.setAttribute("cacheHashMap", mockOrderListDto);

        HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
        request.setAttribute("cacheHashMap", mockOrderListDto);

        CacheData cacheData = getMockCacheData(request);

        when(userService.getUserRoleFromSecurityContext()).thenReturn(UserRole.ACCOUNTANT);
        when(serviceCacheableProxy.getAllBuyOrders(any(CurrencyPair.class), any(UserRole.class), anyBoolean()))
                .thenReturn(mockOrderListDto);
        when(request.getSession()).thenReturn(session);
        Map<String, Integer> cacheHashMap = new HashMap<>();
        cacheHashMap.put("cacheHashMap", mockOrderListDto.hashCode());
        when(request.getSession().getAttribute(anyString())).thenReturn(cacheHashMap);

        List<OrderListDto> allBuyOrders = orderService.getAllBuyOrders(
                cacheData,
                getMockCurrencyPair(CurrencyPairType.MAIN),
                Locale.ENGLISH,
                Boolean.TRUE);

        assertNotNull(allBuyOrders);
        assertEquals(2, allBuyOrders.size());
        assertEquals(OperationType.BUY, allBuyOrders.get(0).getOrderType());
        assertEquals("4185.00", allBuyOrders.get(0).getExrate());
        assertEquals("0.001958208", allBuyOrders.get(0).getAmountBase());
        assertEquals("8.195100480", allBuyOrders.get(0).getAmountConvert());
        assertEquals("47069689", allBuyOrders.get(0).getOrdersIds());
        assertEquals(OperationType.BUY, allBuyOrders.get(0).getOrderType());
        assertEquals("4150.00", allBuyOrders.get(1).getExrate());
        assertEquals("0.055629498", allBuyOrders.get(1).getAmountBase());
        assertEquals("230.862416700", allBuyOrders.get(1).getAmountConvert());
        assertEquals("47074186", allBuyOrders.get(1).getOrdersIds());
    }

    @Test
    public void getAllBuyOrdersEx() {
        when(orderDao.getOrdersBuyForCurrencyPair(any(CurrencyPair.class), any(UserRole.class)))
                .thenReturn(getMockOrderListDto());

        List<OrderListDto> allBuyOrdersEx = orderService.getAllBuyOrdersEx(
                getMockCurrencyPair(CurrencyPairType.MAIN),
                Locale.ENGLISH,
                UserRole.USER);

        assertNotNull(allBuyOrdersEx);
        assertEquals(2, allBuyOrdersEx.size());
        assertEquals(OperationType.BUY, allBuyOrdersEx.get(0).getOrderType());
        assertEquals(OperationType.BUY, allBuyOrdersEx.get(1).getOrderType());

        verify(orderDao, atLeastOnce()).getOrdersBuyForCurrencyPair(any(CurrencyPair.class), any(UserRole.class));
    }

    @Test
    public void getAllSellOrdersEx() {
        when(orderDao.getOrdersSellForCurrencyPair(any(CurrencyPair.class), any(UserRole.class)))
                .thenReturn(getMockOrderListDto());

        List<OrderListDto> allSellOrdersEx = orderService.getAllSellOrdersEx(
                getMockCurrencyPair(CurrencyPairType.MAIN),
                Locale.ENGLISH,
                UserRole.USER);

        assertNotNull(allSellOrdersEx);
        assertEquals(2, allSellOrdersEx.size());
        assertEquals(getMockOrderListDto().get(0).getOrderType(), allSellOrdersEx.get(0).getOrderType());
        assertEquals(getMockOrderListDto().get(0).getAmountBase(), allSellOrdersEx.get(0).getAmountBase());
        assertEquals(getMockOrderListDto().get(1).getOrderType(), allSellOrdersEx.get(1).getOrderType());
        assertEquals(getMockOrderListDto().get(1).getAmountBase(), allSellOrdersEx.get(1).getAmountBase());

        verify(orderDao, atLeastOnce()).getOrdersSellForCurrencyPair(any(CurrencyPair.class), any(UserRole.class));
    }

    @Test
    public void getAllSellOrders_Cache_null() {
        HttpSession session = Mockito.mock(HttpSession.class);
        session.setAttribute("cacheHashMap", "123");

        HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
        request.setAttribute("cacheHashMap", "OBJECT");

        CacheData cacheData = new CacheData(request, "KEY123987", Boolean.TRUE);

        when(userService.getUserRoleFromSecurityContext()).thenReturn(UserRole.USER);
        when(serviceCacheableProxy.getAllSellOrders(any(CurrencyPair.class), any(UserRole.class), anyBoolean()))
                .thenReturn(getMockOrderListDto());
        when(request.getSession()).thenReturn(session);

        List<OrderListDto> allSellOrders = orderService.getAllSellOrders(
                cacheData,
                getMockCurrencyPair(CurrencyPairType.MAIN),
                Locale.ENGLISH,
                Boolean.TRUE);

        assertNotNull(allSellOrders);
        assertEquals(2, allSellOrders.size());

        verify(userService, atLeastOnce()).getUserRoleFromSecurityContext();
        verify(serviceCacheableProxy, atLeastOnce()).getAllSellOrders(
                any(CurrencyPair.class),
                any(UserRole.class),
                anyBoolean());
    }

    @Ignore
    public void getAllSellOrders_Cache_not_null() {
        HttpSession session = Mockito.mock(HttpSession.class);
        session.setAttribute("cacheHashMap", getMockOrderListDto());

        HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
        request.setAttribute("cacheHashMap", "OBJECT");

        CacheData cacheData = getMockCacheData(request);
        Map<String, Integer> cacheHashMap = new HashMap<>();
        cacheHashMap.put("cacheHashMap", 1);

        when(userService.getUserRoleFromSecurityContext()).thenReturn(UserRole.USER);
        when(serviceCacheableProxy.getAllSellOrders(any(CurrencyPair.class), any(UserRole.class), anyBoolean()))
                .thenReturn(getMockOrderListDto());
        when(request.getSession()).thenReturn(session);
        when(request.getSession().getAttribute(anyString())).thenReturn(cacheHashMap);

        List<OrderListDto> allSellOrders = orderService.getAllSellOrders(
                cacheData,
                getMockCurrencyPair(CurrencyPairType.MAIN),
                Locale.ENGLISH,
                Boolean.TRUE);

        assertNotNull(allSellOrders);
        assertEquals(2, allSellOrders.size());
    }

    @Test
    public void getOpenOrdersForWs_CurrencyPair_null() {
        when(currencyService.getCurrencyPairByName(anyString())).thenReturn(null);

        List<OrdersListWrapper> ordersListWrapper = orderService.getOpenOrdersForWs("BTC/USD");

        assertNull(ordersListWrapper);
        verify(currencyService, atLeastOnce()).getCurrencyPairByName(anyString());
    }

    @Test
    public void getOpenOrdersForWs_CurrencyPair_not_null() {
        when(currencyService.getCurrencyPairByName(anyString())).thenReturn(getMockCurrencyPair(CurrencyPairType.ICO));
        when(orderDao.getOrdersSellForCurrencyPair(any(CurrencyPair.class), any()))
                .thenReturn(getMockOrderListDto());
        when(orderDao.getOrdersBuyForCurrencyPair(any(CurrencyPair.class), any()))
                .thenReturn(getMockOrderListDto());

        List<OrdersListWrapper> ordersListWrapper = orderService.getOpenOrdersForWs("BTC/USD");

        assertNotNull(ordersListWrapper);
        assertEquals(2, ordersListWrapper.size());
        assertEquals("SELL", ordersListWrapper.get(0).getType());
        assertEquals(100, ordersListWrapper.get(0).getCurrencyPairId());
        assertEquals("BUY", ordersListWrapper.get(1).getType());
        assertEquals(100, ordersListWrapper.get(1).getCurrencyPairId());

        verify(currencyService, atLeastOnce()).getCurrencyPairByName(anyString());
        verify(orderDao, atLeastOnce()).getOrdersSellForCurrencyPair(any(CurrencyPair.class), any());
        verify(orderDao, atLeastOnce()).getOrdersBuyForCurrencyPair(any(CurrencyPair.class), any());
    }

    @Test
    public void getWalletAndCommission() {
        when(userService.getUserRoleFromDB(anyString())).thenReturn(UserRole.USER);
        when(orderDao.getWalletAndCommission(
                anyString(),
                any(Currency.class),
                any(OperationType.class),
                any(UserRole.class))).thenReturn(getMockWalletsAndCommissionsForOrderCreationDto());

        WalletsAndCommissionsForOrderCreationDto walletAndCommission = orderService.getWalletAndCommission(
                USER_EMAIL,
                getMockCurrency(),
                OperationType.BUY);

        assertNotNull(walletAndCommission);
        assertEquals(getMockWalletsAndCommissionsForOrderCreationDto().getUserId(), walletAndCommission.getUserId());
        assertEquals(getMockWalletsAndCommissionsForOrderCreationDto().getSpendWalletId(),
                walletAndCommission.getSpendWalletId());
        assertEquals(getMockWalletsAndCommissionsForOrderCreationDto().getSpendWalletActiveBalance(),
                walletAndCommission.getSpendWalletActiveBalance());
        assertEquals(getMockWalletsAndCommissionsForOrderCreationDto().getCommissionId(),
                walletAndCommission.getCommissionId());
        assertEquals(getMockWalletsAndCommissionsForOrderCreationDto().getCommissionValue(),
                walletAndCommission.getCommissionValue());

        verify(orderDao, atLeastOnce()).getWalletAndCommission(
                anyString(),
                any(Currency.class),
                any(OperationType.class),
                any(UserRole.class));

        reset(userService);
    }

    @Test
    public void searchOrdersByAdmin() {
        OrderBasicInfoDto dto = new OrderBasicInfoDto();
        dto.setId(100);
        dto.setDateCreation(LocalDateTime.of(2019, 4, 11, 11, 4, 52));
        dto.setCurrencyPairName("BTC/USD");
        dto.setOrderTypeName("SELL");
        dto.setExrate("FIAT");

        PagingData<List<OrderBasicInfoDto>> searchResult = new PagingData<>();
        searchResult.setTotal(10);
        searchResult.setFiltered(5);
        searchResult.setData(Collections.singletonList(dto));

        when(orderDao.searchOrders(any(AdminOrderFilterData.class), any(DataTableParams.class), any(Locale.class)))
                .thenReturn(searchResult);

        DataTable<List<OrderBasicInfoDto>> listDataTable = orderService.searchOrdersByAdmin(
                new AdminOrderFilterData(),
                DataTableParams.defaultParams(),
                Locale.ENGLISH);

        assertNotNull(listDataTable);
        assertEquals(searchResult.getTotal(), listDataTable.getRecordsTotal());
        assertEquals(searchResult.getFiltered(), listDataTable.getRecordsFiltered());
        assertEquals(1, listDataTable.getData().size());
        assertEquals(dto, listDataTable.getData().get(0));

        verify(orderDao, atLeastOnce()).searchOrders(
                any(AdminOrderFilterData.class),
                any(DataTableParams.class),
                any(Locale.class));
    }

    @Test
    public void getOrdersForReport() {
        OrderReportInfoDto dto = new OrderReportInfoDto();
        dto.setId(100);
        dto.setDateAcception(LocalDateTime.of(2019, 4, 11, 11, 4, 52));
        dto.setDateAcception(LocalDateTime.of(2019, 4, 11, 12, 4, 52));
        dto.setCurrencyPairName("BTC/USD");
        dto.setOrderTypeName("SELL");
        dto.setExrate("FIAT");

        when(orderDao.getOrdersForReport(any(AdminOrderFilterData.class))).thenReturn(Collections.singletonList(dto));

        List<OrderReportInfoDto> ordersForReport = orderService.getOrdersForReport(new AdminOrderFilterData());

        assertNotNull(ordersForReport);
        assertEquals(1, ordersForReport.size());
        assertEquals(dto, ordersForReport.get(0));

        verify(orderDao, atLeastOnce()).getOrdersForReport(any(AdminOrderFilterData.class));
    }

    @Test
    public void getUsersOrdersWithStateForAdmin() {
        OrderWideListDto dto = new OrderWideListDto();
        dto.setUserId(100);
        dto.setCurrencyPairId(100);
        dto.setCurrencyPairName("BTC/USD");
        dto.setStatus(OrderStatus.OPENED);

        when(orderDao.getMyOrdersWithState(
                anyInt(),
                any(CurrencyPair.class),
                any(OrderStatus.class),
                any(OperationType.class),
                anyString(),
                anyInt(),
                anyInt(),
                any(Locale.class))).thenReturn(Collections.singletonList(dto));
        when(currencyService.findCurrencyPairById(anyInt())).thenReturn(new CurrencyPair("BTC/USD"));

        List<OrderWideListDto> myOrdersWithState = orderService.getUsersOrdersWithStateForAdmin(
                1,
                new CurrencyPair(),
                OrderStatus.OPENED,
                OperationType.SELL,
                10,
                15,
                Locale.ENGLISH);

        assertNotNull(myOrdersWithState);
        assertEquals(dto, myOrdersWithState.get(0));

        verify(orderDao, atLeastOnce()).getMyOrdersWithState(
                anyInt(),
                any(CurrencyPair.class),
                any(OrderStatus.class),
                any(OperationType.class),
                anyString(),
                anyInt(),
                anyInt(),
                any(Locale.class));
    }

    @Test
    public void getMyOrdersWithState_return_list_OrderWideListDto() {
        OrderWideListDto dto = new OrderWideListDto();

        when(userService.getIdByEmail(anyString())).thenReturn(100);
        when(orderDao.getMyOrdersWithState(
                anyInt(),
                any(CurrencyPair.class),
                any(OrderStatus.class),
                any(OperationType.class),
                anyString(),
                anyInt(),
                anyInt(),
                any(Locale.class))).thenReturn(Collections.singletonList(dto));
        when(currencyService.findCurrencyPairById(anyInt())).thenReturn(new CurrencyPair("BTC/USD"));

        List<OrderWideListDto> getMyOrdersWithState = orderService.getMyOrdersWithState(
                USER_EMAIL,
                new CurrencyPair(),
                OrderStatus.CLOSED,
                OperationType.BUY,
                "SCOUP",
                10,
                10,
                Locale.ENGLISH);

        assertNotNull(getMyOrdersWithState);
        assertEquals(Collections.singletonList(dto), getMyOrdersWithState);

        verify(userService, atLeastOnce()).getIdByEmail(anyString());
        verify(orderDao, atLeastOnce()).getMyOrdersWithState(
                anyInt(),
                any(CurrencyPair.class),
                any(OrderStatus.class),
                any(OperationType.class),
                anyString(),
                anyInt(),
                anyInt(),
                any(Locale.class));
    }

    @Test
    public void getMyOrdersWithState_() {
        OrderWideListDto dto = new OrderWideListDto();

        when(userService.getIdByEmail(anyString())).thenReturn(100);
        when(orderDao.getMyOrdersWithState(
                anyInt(),
                any(CurrencyPair.class),
                any(OrderStatus.class),
                any(OperationType.class),
                any(),
                anyInt(),
                anyInt(),
                any(Locale.class))).thenReturn(Collections.singletonList(dto));
        when(currencyService.findCurrencyPairById(anyInt())).thenReturn(new CurrencyPair("BTC/USD"));

        List<OrderWideListDto> getMyOrdersWithState = orderService.getMyOrdersWithState(
                USER_EMAIL,
                new CurrencyPair(),
                OrderStatus.CLOSED,
                OperationType.BUY,
                "ALL",
                10,
                10,
                Locale.ENGLISH);

        assertNotNull(getMyOrdersWithState);
        assertEquals(Collections.singletonList(dto), getMyOrdersWithState);

        verify(userService, atLeastOnce()).getIdByEmail(anyString());
        verify(orderDao, atLeastOnce()).getMyOrdersWithState(
                anyInt(),
                any(CurrencyPair.class),
                any(OrderStatus.class),
                any(OperationType.class),
                any(),
                anyInt(),
                anyInt(),
                any(Locale.class));
    }

    @Test
    public void getOrderAcceptedForPeriod_return_list_OrderAcceptedHistoryDto() {
        OrderAcceptedHistoryDto mockOrderAcceptedHistoryDto = getMockOrderAcceptedHistoryDto();

        when(orderDao.getOrderAcceptedForPeriod(
                anyString(),
                any(BackDealInterval.class),
                anyInt(),
                any(CurrencyPair.class))).thenReturn(Collections.singletonList(mockOrderAcceptedHistoryDto));

        List<OrderAcceptedHistoryDto> orderAcceptedForPeriod = orderService.getOrderAcceptedForPeriod(
                USER_EMAIL,
                new BackDealInterval(),
                10,
                new CurrencyPair(),
                Locale.ENGLISH);

        assertNotNull(orderAcceptedForPeriod);
        assertEquals(1, orderAcceptedForPeriod.size());
        assertEquals(mockOrderAcceptedHistoryDto, orderAcceptedForPeriod.get(0));

        verify(orderDao, atLeastOnce()).getOrderAcceptedForPeriod(
                anyString(),
                any(BackDealInterval.class),
                anyInt(),
                any(CurrencyPair.class));
    }

    @Test
    public void getAllBuyOrders_has_two_arguments() {
        List<OrderListDto> mockOrderListDto = getMockOrderListDto();
        when(orderDao.getOrdersBuyForCurrencyPair(any(CurrencyPair.class), any())).thenReturn(mockOrderListDto);

        List<OrderListDto> allBuyOrders = orderService.getAllBuyOrders(new CurrencyPair(), Locale.ENGLISH);

        assertNotNull(allBuyOrders);
        assertEquals(2, allBuyOrders.size());
        assertEquals(mockOrderListDto, allBuyOrders);

        verify(orderDao, atLeastOnce()).getOrdersBuyForCurrencyPair(any(CurrencyPair.class), any());
    }

    @Test
    public void getAllSellOrders() {
        List<OrderListDto> mockOrderListDto = getMockOrderListDto();

        when(orderDao.getOrdersSellForCurrencyPair(any(CurrencyPair.class), any())).thenReturn(mockOrderListDto);

        List<OrderListDto> allSellOrders = orderService.getAllSellOrders(new CurrencyPair(), Locale.CANADA);

        assertNotNull(allSellOrders);
        assertEquals(2, allSellOrders.size());
        assertEquals(mockOrderListDto, allSellOrders);

        verify(orderDao, atLeastOnce()).getOrdersSellForCurrencyPair(any(CurrencyPair.class), any());
    }

    @Test
    public void getUserSummaryOrdersByCurrencyPairList() {
        UserSummaryOrdersByCurrencyPairsDto dto = new UserSummaryOrdersByCurrencyPairsDto();
        dto.setOperationType("BUY");
        dto.setDate(LocalDate.now().toString());
        dto.setOwnerEmail(USER_EMAIL);
        dto.setOwnerNickname("TEST_NICK");
        dto.setOwnerNickname(USER_EMAIL);
        dto.setAcceptorNickname("TEST_NICK");
        dto.setCurrencyPair("BTC/USD");
        dto.setAmountBase(BigDecimal.TEN);
        dto.setAmountConvert(BigDecimal.TEN);
        dto.setExrate(BigDecimal.TEN);

        when(orderDao.getUserSummaryOrdersByCurrencyPairList(
                anyInt(),
                anyString(),
                anyString(),
                anyListOf(Integer.class))).thenReturn(Collections.singletonList(dto));

        List<UserSummaryOrdersByCurrencyPairsDto> userSummaryOrdersByCurrencyPairList = orderService
                .getUserSummaryOrdersByCurrencyPairList(
                        1,
                        LocalDate.now().toString(),
                        LocalDate.now().toString(),
                        Arrays.asList(1, 2, 3));

        assertNotNull(userSummaryOrdersByCurrencyPairList);
        assertEquals(dto, userSummaryOrdersByCurrencyPairList.get(0));

        verify(orderDao, atLeastOnce()).getUserSummaryOrdersByCurrencyPairList(
                anyInt(),
                anyString(),
                anyString(),
                anyListOf(Integer.class));
    }

    @Test
    public void getOrdersForRefresh_default() {
        when(currencyService.findCurrencyPairById(anyInt())).thenReturn(getMockCurrencyPair(CurrencyPairType.MAIN));

        String ordersForRefresh = orderService.getOrdersForRefresh(1, OperationType.REFERRAL, UserRole.USER);

        assertNull(ordersForRefresh);
        verify(currencyService, atLeastOnce()).findCurrencyPairById(anyInt());
    }

    @Test
    public void getOrdersForRefresh_exception() throws Exception {
        when(currencyService.findCurrencyPairById(anyInt())).thenReturn(getMockCurrencyPair(CurrencyPairType.MAIN));
        doThrow(JsonProcessingException.class).when(objectMapper).writeValueAsString(anyListOf(OrdersListWrapper.class));

        String ordersForRefresh = orderService.getOrdersForRefresh(1, OperationType.BUY, UserRole.USER);

        assertNull(ordersForRefresh);
        verify(currencyService, atLeastOnce()).findCurrencyPairById(anyInt());
        verify(objectMapper, atLeastOnce()).writeValueAsString(anyListOf(OrdersListWrapper.class));
    }

    @Test
    public void getOrdersForRefresh_BUY() throws Exception {
        OrdersListWrapper ordersListWrapper = new OrdersListWrapper(getMockOrderListDto(), OperationType.BUY.name(), 1);
        String expected = new ObjectMapper().writeValueAsString(Collections.singletonList(ordersListWrapper));

        when(currencyService.findCurrencyPairById(anyInt())).thenReturn(getMockCurrencyPair(CurrencyPairType.MAIN));
        when(orderDao.getOrdersBuyForCurrencyPair(any(CurrencyPair.class), any(UserRole.class)))
                .thenReturn(getMockOrderListDto());
        when(objectMapper.writeValueAsString(anyListOf(OrdersListWrapper.class))).thenReturn(expected);

        String ordersForRefresh = orderService.getOrdersForRefresh(1, OperationType.BUY, UserRole.USER);

        assertNotNull(ordersForRefresh);
        assertEquals(expected, ordersForRefresh);
        verify(currencyService, atLeastOnce()).findCurrencyPairById(anyInt());
        verify(orderDao, atLeastOnce()).getOrdersBuyForCurrencyPair(any(CurrencyPair.class), any(UserRole.class));
        verify(objectMapper, atLeastOnce()).writeValueAsString(anyListOf(OrdersListWrapper.class));
    }

    @Test
    public void getOrdersForRefresh_SELL() throws Exception {
        OrdersListWrapper ordersListWrapper = new OrdersListWrapper(getMockOrderListDto(), OperationType.SELL.name(), 1);
        String expected = new ObjectMapper().writeValueAsString(Collections.singletonList(ordersListWrapper));

        when(currencyService.findCurrencyPairById(anyInt())).thenReturn(getMockCurrencyPair(CurrencyPairType.MAIN));
        when(orderDao.getOrdersBuyForCurrencyPair(any(CurrencyPair.class), any(UserRole.class)))
                .thenReturn(getMockOrderListDto());
        when(objectMapper.writeValueAsString(anyListOf(OrdersListWrapper.class))).thenReturn(expected);

        String ordersForRefresh = orderService.getOrdersForRefresh(1, OperationType.SELL, UserRole.USER);

        assertNotNull(ordersForRefresh);
        assertEquals(expected, ordersForRefresh);
        verify(currencyService, atLeastOnce()).findCurrencyPairById(anyInt());
        verify(objectMapper, atLeastOnce()).writeValueAsString(anyListOf(OrdersListWrapper.class));
    }

    @Test
    public void getTradesForRefresh() throws Exception {
        OrdersListWrapper ordersListWrapper = new OrdersListWrapper(
                getMockOrderAcceptedHistoryDto(),
                RefreshObjectsEnum.ALL_TRADES.name(),
                1);
        String expected_left = new ObjectMapper().writeValueAsString(ordersListWrapper);
        String expected_right = new JSONArray() {{
            put(new ObjectMapper().writeValueAsString(ordersListWrapper));
        }}.toString();

        when(currencyService.findCurrencyPairById(anyInt())).thenReturn(getMockCurrencyPair(CurrencyPairType.MAIN));
        when(orderDao.getOrderAcceptedForPeriod(any(), any(BackDealInterval.class), anyInt(), any(CurrencyPair.class)))
                .thenReturn(Collections.singletonList(getMockOrderAcceptedHistoryDto()));
        when(objectMapper.writeValueAsString(any(OrdersListWrapper.class))).thenReturn(expected_left);

        BiTuple tradesForRefresh = orderService.getTradesForRefresh(1, USER_EMAIL, RefreshObjectsEnum.ALL_TRADES);

        assertNotNull(tradesForRefresh);
        assertEquals(expected_left, tradesForRefresh.left);
        assertEquals(expected_right, tradesForRefresh.right);

        verify(currencyService, atLeastOnce()).findCurrencyPairById(anyInt());
        verify(orderDao, atLeastOnce()).getOrderAcceptedForPeriod(
                any(),
                any(BackDealInterval.class),
                anyInt(),
                any(CurrencyPair.class));
        verify(objectMapper, atLeastOnce()).writeValueAsString(any(OrdersListWrapper.class));
    }

    @Test
    public void getTradesForRefresh_exception() throws Exception {
        OrdersListWrapper ordersListWrapper = new OrdersListWrapper(
                getMockOrderAcceptedHistoryDto(),
                RefreshObjectsEnum.ALL_TRADES.name(),
                1);

        when(currencyService.findCurrencyPairById(anyInt())).thenReturn(getMockCurrencyPair(CurrencyPairType.MAIN));
        when(orderDao.getOrderAcceptedForPeriod(any(), any(BackDealInterval.class), anyInt(), any(CurrencyPair.class)))
                .thenReturn(Collections.singletonList(getMockOrderAcceptedHistoryDto()));
        doThrow(JsonProcessingException.class).when(objectMapper).writeValueAsString(any(OrdersListWrapper.class));

        BiTuple tradesForRefresh = orderService.getTradesForRefresh(1, USER_EMAIL, RefreshObjectsEnum.ALL_TRADES);

        assertNull(tradesForRefresh);

        verify(currencyService, atLeastOnce()).findCurrencyPairById(anyInt());
        verify(orderDao, atLeastOnce()).getOrderAcceptedForPeriod(
                any(),
                any(BackDealInterval.class),
                anyInt(),
                any(CurrencyPair.class));
        verify(objectMapper, atLeastOnce()).writeValueAsString(any(OrdersListWrapper.class));
    }

    @Test
    public void getAllAndMyTradesForInit_principal_null() throws Exception {
        OrdersListWrapper ordersListWrapper = new OrdersListWrapper(
                getMockOrderAcceptedHistoryDto(),
                RefreshObjectsEnum.ALL_TRADES.name(),
                1);

        String wrapper = new ObjectMapper().writeValueAsString(ordersListWrapper);
        String expected = new JSONArray() {{
            put(new ObjectMapper().writeValueAsString(ordersListWrapper));
        }}.toString();

        when(currencyService.findCurrencyPairById(anyInt())).thenReturn(getMockCurrencyPair(CurrencyPairType.MAIN));
        when(orderDao.getOrderAcceptedForPeriod(any(), any(BackDealInterval.class), anyInt(), any(CurrencyPair.class)))
                .thenReturn(Collections.singletonList(getMockOrderAcceptedHistoryDto()));
        when(objectMapper.writeValueAsString(any(OrdersListWrapper.class))).thenReturn(wrapper);

        String allAndMyTradesForInit = orderService.getAllAndMyTradesForInit(1, null);

        assertNotNull(allAndMyTradesForInit);
        assertEquals(expected, allAndMyTradesForInit);

        verify(currencyService, atLeastOnce()).findCurrencyPairById(anyInt());
        verify(orderDao, atLeastOnce()).getOrderAcceptedForPeriod(any(),
                any(BackDealInterval.class), anyInt(), any(CurrencyPair.class));
        verify(objectMapper, atLeastOnce()).writeValueAsString(any(OrdersListWrapper.class));
    }

    @Test
    public void getAllAndMyTradesForInit_principal_not_null() throws Exception {
        Principal principal = Mockito.mock(Principal.class);

        OrdersListWrapper ordersListWrapper = new OrdersListWrapper(
                getMockOrderAcceptedHistoryDto(),
                RefreshObjectsEnum.ALL_TRADES.name(), 1
        );
        String wrapper = new ObjectMapper().writeValueAsString(ordersListWrapper);
        String expected = new JSONArray() {{
            put(new ObjectMapper().writeValueAsString(ordersListWrapper));
            put(new ObjectMapper().writeValueAsString(ordersListWrapper));
        }}.toString();

        when(currencyService.findCurrencyPairById(anyInt())).thenReturn(getMockCurrencyPair(CurrencyPairType.MAIN));
        when(orderDao.getOrderAcceptedForPeriod(any(), any(BackDealInterval.class), anyInt(), any(CurrencyPair.class)))
                .thenReturn(Collections.singletonList(getMockOrderAcceptedHistoryDto()));
        when(objectMapper.writeValueAsString(any(OrdersListWrapper.class))).thenReturn(wrapper);

        String allAndMyTradesForInit = orderService.getAllAndMyTradesForInit(1, principal);

        assertNotNull(allAndMyTradesForInit);
        assertEquals(expected, allAndMyTradesForInit);

        verify(currencyService, atLeastOnce()).findCurrencyPairById(anyInt());
        verify(orderDao, atLeastOnce()).getOrderAcceptedForPeriod(any(),
                any(BackDealInterval.class), anyInt(), any(CurrencyPair.class));
        verify(objectMapper, atLeastOnce()).writeValueAsString(any(OrdersListWrapper.class));
    }

    @Test
    public void getLastOrderPriceByCurrencyPairAndOperationType() {
        when(orderDao.getLastOrderPriceByCurrencyPairAndOperationType(
                anyInt(),
                anyInt())).thenReturn(Optional.ofNullable(BigDecimal.ZERO));

        Optional<BigDecimal> lastOrderPriceByCurrencyPairAndOperationType = orderService
                .getLastOrderPriceByCurrencyPairAndOperationType(new CurrencyPair(), OperationType.SELL);

        assertNotNull(lastOrderPriceByCurrencyPairAndOperationType);
        assertEquals(Optional.ofNullable(BigDecimal.ZERO), lastOrderPriceByCurrencyPairAndOperationType);

        verify(orderDao, atLeastOnce()).getLastOrderPriceByCurrencyPairAndOperationType(anyInt(), anyInt());
    }

    @Test
    public void getAllCurrenciesStatForRefresh() throws Exception {
        Object[] data = {getMockExOrderStatisticsShortByPairsDto(CurrencyPairType.ICO)};
        OrdersListWrapper ordersListWrapper = new OrdersListWrapper(data, "CURRENCIES_STATISTIC", 0);

        String wrapper = new ObjectMapper().writeValueAsString(ordersListWrapper);
        String expected = new JSONArray() {{
            put(new ObjectMapper().writeValueAsString(ordersListWrapper));
        }}.toString();

        when(exchangeRatesHolder.getAllRates())
                .thenReturn(Collections.singletonList(getExOrderStatisticsShortByPairsDto(CurrencyPairType.ICO)));
        when(objectMapper.writeValueAsString(any(OrdersListWrapper.class))).thenReturn(wrapper);
        when(currencyService.getAllCurrencyPairCached()).thenReturn(Collections.singletonMap(1, getMockCurrencyPair(CurrencyPairType.MAIN)));
        String allCurrenciesStatForRefreshForAllPairs = orderService
                .getAllCurrenciesStatForRefresh(RefreshObjectsEnum.ICO_CURRENCIES_STATISTIC);

        assertNotNull(allCurrenciesStatForRefreshForAllPairs);
        assertEquals(expected, allCurrenciesStatForRefreshForAllPairs);

        verify(exchangeRatesHolder, atLeastOnce()).getAllRates();
        verify(objectMapper, atLeastOnce()).writeValueAsString(any(OrdersListWrapper.class));
    }

    @Test
    public void getAllCurrenciesStatForRefresh_exception() throws Exception {
        when(exchangeRatesHolder.getAllRates())
                .thenReturn(Collections.singletonList(getExOrderStatisticsShortByPairsDto(CurrencyPairType.ICO)));
        when(currencyService.getAllCurrencyPairCached()).thenReturn(Collections.singletonMap(1, getMockCurrencyPair(CurrencyPairType.MAIN)));

        doThrow(JsonProcessingException.class).when(objectMapper).writeValueAsString(any(OrdersListWrapper.class));

        String allCurrenciesStatForRefreshForAllPairs = orderService
                .getAllCurrenciesStatForRefresh(RefreshObjectsEnum.ICO_CURRENCIES_STATISTIC);
        assertNull(allCurrenciesStatForRefreshForAllPairs);

        verify(exchangeRatesHolder, atLeastOnce()).getAllRates();
        verify(objectMapper, atLeastOnce()).writeValueAsString(any(OrdersListWrapper.class));
    }

    @Test
    public void getAllCurrenciesStatForRefreshForAllPairs() throws Exception {
        Object[] data = {getMockExOrderStatisticsShortByPairsDto(CurrencyPairType.ICO)};
        OrdersListWrapper ordersListWrapper = new OrdersListWrapper(data, "CURRENCIES_STATISTIC", 0);

        String wrapper = new ObjectMapper().writeValueAsString(ordersListWrapper);
        String expected = new JSONArray() {{
            put(new ObjectMapper().writeValueAsString(ordersListWrapper));
        }}.toString();
        when(currencyService.getAllCurrencyPairCached()).thenReturn(Collections.singletonMap(1, getMockCurrencyPair(CurrencyPairType.MAIN)));

        when(exchangeRatesHolder.getAllRates())
                .thenReturn(Collections.singletonList(getExOrderStatisticsShortByPairsDto(CurrencyPairType.ICO)));
        when(objectMapper.writeValueAsString(any(OrdersListWrapper.class))).thenReturn(wrapper);

        String allCurrenciesStatForRefreshForAllPairs = orderService.getAllCurrenciesStatForRefreshForAllPairs();

        assertNotNull(allCurrenciesStatForRefreshForAllPairs);
        assertEquals(expected, allCurrenciesStatForRefreshForAllPairs);

        verify(exchangeRatesHolder, atLeastOnce()).getAllRates();
        verify(objectMapper, atLeastOnce()).writeValueAsString(any(OrdersListWrapper.class));
    }

    @Test
    public void getAllCurrenciesStatForRefreshForAllPairs_exception() throws Exception {
        when(exchangeRatesHolder.getAllRates())
                .thenReturn(Collections.singletonList(getExOrderStatisticsShortByPairsDto(CurrencyPairType.ICO)));
        when(currencyService.getAllCurrencyPairCached()).thenReturn(Collections.singletonMap(1, getMockCurrencyPair(CurrencyPairType.MAIN)));

        doThrow(JsonProcessingException.class).when(objectMapper).writeValueAsString(any(OrdersListWrapper.class));

        String allCurrenciesStatForRefreshForAllPairs = orderService.getAllCurrenciesStatForRefreshForAllPairs();
        assertNull(allCurrenciesStatForRefreshForAllPairs);

        verify(exchangeRatesHolder, atLeastOnce()).getAllRates();
        verify(objectMapper, atLeastOnce()).writeValueAsString(any(OrdersListWrapper.class));
    }

    @Test
    public void getSomeCurrencyStatForRefresh_dtos_isEmpty() {
        Set<Integer> currencyIds = new HashSet<>(Arrays.asList(1, 2, 3));

        when(exchangeRatesHolder.getCurrenciesRates(anySetOf(Integer.class))).thenReturn(Collections.EMPTY_LIST);

        RefreshStatisticDto someCurrencyStatForRefresh = orderService.getSomeCurrencyStatForRefresh(currencyIds);

        assertNotNull(someCurrencyStatForRefresh);
        assertNull(someCurrencyStatForRefresh.getIcoData());
        assertNull(someCurrencyStatForRefresh.getMainCurrenciesData());
        assertNull(someCurrencyStatForRefresh.getStatisticInfoDtos());

        verify(exchangeRatesHolder, atLeastOnce()).getCurrenciesRates(anySetOf(Integer.class));
    }

    @Test
    public void getSomeCurrencyStatForRefresh_CurrencyPairType_ICO() throws Exception {
        Set<Integer> currencyIds = new HashSet<>(Collections.singletonList(1));
        List<ExOrderStatisticsShortByPairsDto> exOrderStatisticsShortByPairsDtos = Collections
                .singletonList(getMockExOrderStatisticsShortByPairsDto(CurrencyPairType.ICO));
        OrdersListWrapper ordersListWrapper = new OrdersListWrapper(
                exOrderStatisticsShortByPairsDtos,
                "ICO_CURRENCY_STATISTIC");

        String wrapper = new ObjectMapper().writeValueAsString(ordersListWrapper);
        String expected = new JSONArray() {{
            put(new ObjectMapper().writeValueAsString(ordersListWrapper));
        }}.toString();

        when(exchangeRatesHolder.getCurrenciesRates(anySetOf(Integer.class)))
                .thenReturn(exOrderStatisticsShortByPairsDtos);
        when(objectMapper.writeValueAsString(any(OrdersListWrapper.class))).thenReturn(wrapper);

        RefreshStatisticDto someCurrencyStatForRefresh = orderService.getSomeCurrencyStatForRefresh(currencyIds);

        assertNotNull(someCurrencyStatForRefresh);
        assertEquals(expected, someCurrencyStatForRefresh.getIcoData());
        assertNull(someCurrencyStatForRefresh.getMainCurrenciesData());
        assertEquals(1, someCurrencyStatForRefresh.getStatisticInfoDtos().size());
        assertTrue(someCurrencyStatForRefresh.getStatisticInfoDtos().containsKey("BTC/USD"));
        assertTrue(someCurrencyStatForRefresh.getStatisticInfoDtos().containsValue(wrapper));

        verify(exchangeRatesHolder, atLeastOnce()).getCurrenciesRates(anySetOf(Integer.class));
        verify(objectMapper, atLeastOnce()).writeValueAsString(any(OrdersListWrapper.class));
    }

    @Test
    public void getSomeCurrencyStatForRefresh_CurrencyPairType_ICO_writeValueAsString_exception() throws Exception {
        Set<Integer> currencyIds = new HashSet<>(Collections.singletonList(1));
        List<ExOrderStatisticsShortByPairsDto> exOrderStatisticsShortByPairsDtos = Collections
                .singletonList(getMockExOrderStatisticsShortByPairsDto(CurrencyPairType.ICO));

        when(exchangeRatesHolder.getCurrenciesRates(anySetOf(Integer.class)))
                .thenReturn(exOrderStatisticsShortByPairsDtos);
        doThrow(JsonProcessingException.class).when(objectMapper).writeValueAsString(any(OrdersListWrapper.class));

        try {
            orderService.getSomeCurrencyStatForRefresh(currencyIds);
            fail();
        } catch (Exception e) {
            assertTrue(e instanceof RuntimeException);
            assertEquals("com.fasterxml.jackson.core.JsonProcessingException: N/A", e.getMessage());
        }

        verify(exchangeRatesHolder, atLeastOnce()).getCurrenciesRates(anySetOf(Integer.class));
        verify(objectMapper, atLeastOnce()).writeValueAsString(any(OrdersListWrapper.class));
    }

    @Test
    public void getSomeCurrencyStatForRefresh_CurrencyPairType_MAIN() throws Exception {
        Set<Integer> currencyIds = new HashSet<>(Collections.singletonList(1));
        List<ExOrderStatisticsShortByPairsDto> exOrderStatisticsShortByPairsDtos = Collections
                .singletonList(getMockExOrderStatisticsShortByPairsDto(CurrencyPairType.MAIN));
        OrdersListWrapper ordersListWrapper = new OrdersListWrapper(
                exOrderStatisticsShortByPairsDtos,
                "MAIN_CURRENCY_STATISTIC");

        String wrapper = new ObjectMapper().writeValueAsString(ordersListWrapper);
        String expected = new JSONArray() {{
            put(new ObjectMapper().writeValueAsString(ordersListWrapper));
        }}.toString();

        when(exchangeRatesHolder.getCurrenciesRates(anySetOf(Integer.class)))
                .thenReturn(exOrderStatisticsShortByPairsDtos);
        when(objectMapper.writeValueAsString(any(OrdersListWrapper.class))).thenReturn(wrapper);

        RefreshStatisticDto someCurrencyStatForRefresh = orderService.getSomeCurrencyStatForRefresh(currencyIds);

        assertNotNull(someCurrencyStatForRefresh);
        assertNull(someCurrencyStatForRefresh.getIcoData());
        assertEquals(expected, someCurrencyStatForRefresh.getMainCurrenciesData());
        assertEquals(1, someCurrencyStatForRefresh.getStatisticInfoDtos().size());
        assertTrue(someCurrencyStatForRefresh.getStatisticInfoDtos().containsKey("BTC/USD"));
        assertTrue(someCurrencyStatForRefresh.getStatisticInfoDtos().containsValue(wrapper));

        verify(exchangeRatesHolder, atLeastOnce()).getCurrenciesRates(anySetOf(Integer.class));
        verify(objectMapper, atLeastOnce()).writeValueAsString(any(OrdersListWrapper.class));
    }

    @Test
    public void getSomeCurrencyStatForRefresh_CurrencyPairType_MAIN_writeValueAsString_exception() throws Exception {
        Set<Integer> currencyIds = new HashSet<>(Collections.singletonList(1));
        List<ExOrderStatisticsShortByPairsDto> exOrderStatisticsShortByPairsDtos = Collections
                .singletonList(getMockExOrderStatisticsShortByPairsDto(CurrencyPairType.MAIN));

        when(exchangeRatesHolder.getCurrenciesRates(anySetOf(Integer.class))).thenReturn(exOrderStatisticsShortByPairsDtos);
        doThrow(JsonProcessingException.class).when(objectMapper).writeValueAsString(any(OrdersListWrapper.class));

        try {
            orderService.getSomeCurrencyStatForRefresh(currencyIds);
            fail();
        } catch (Exception e) {
            assertTrue(e instanceof RuntimeException);
            assertEquals("com.fasterxml.jackson.core.JsonProcessingException: N/A", e.getMessage());
        }

        verify(exchangeRatesHolder, atLeastOnce()).getCurrenciesRates(anySetOf(Integer.class));
        verify(objectMapper, atLeastOnce()).writeValueAsString(any(OrdersListWrapper.class));
    }

    @Test
    public void getStatForPair_ExOrderStatisticsShortByPairsDto_not_empty() {
        when(currencyService.getCurrencyPairByName(anyString())).thenReturn(getMockCurrencyPair(CurrencyPairType.ICO));
        when(exchangeRatesHolder.getCurrenciesRates(anySetOf(Integer.class)))
                .thenReturn(Collections.singletonList(getMockExOrderStatisticsShortByPairsDto(CurrencyPairType.ICO)));

        ResponseInfoCurrencyPairDto statForPair = orderService.getStatForPair("BTC/USD");

        assertNotNull(statForPair);
        assertEquals("0.000000000", statForPair.getCurrencyRate());
        assertEquals("0.00", statForPair.getPercentChange());
        assertEquals("0", statForPair.getChangedValue());
        assertEquals("0.000000000", statForPair.getLastCurrencyRate());
        assertEquals("0.000000000", statForPair.getVolume24h());
        assertEquals("0.000000000", statForPair.getRateHigh());
        assertEquals("0.000000000", statForPair.getRateLow());
        assertEquals("BTC/USD", statForPair.getPairName());

        verify(currencyService, atLeastOnce()).getCurrencyPairByName(anyString());
        verify(exchangeRatesHolder, atLeastOnce()).getCurrenciesRates(anySetOf(Integer.class));
    }

    @Test
    public void getStatForPair_ExOrderStatisticsShortByPairsDto_isEmpty() {
        when(currencyService.getCurrencyPairByName(anyString())).thenReturn(getMockCurrencyPair(CurrencyPairType.ICO));
        when(exchangeRatesHolder.getCurrenciesRates(anySetOf(Integer.class))).thenReturn(Collections.EMPTY_LIST);

        ResponseInfoCurrencyPairDto statForPair = orderService.getStatForPair("BTC/USD");

        assertNull(statForPair);

        verify(currencyService, atLeastOnce()).getCurrencyPairByName(anyString());
        verify(exchangeRatesHolder, atLeastOnce()).getCurrenciesRates(anySetOf(Integer.class));
    }

    @Test
    public void getOrderBook_OrderType_not_null() {
        when(currencyService.findCurrencyPairIdByName(anyString())).thenReturn(56);
        when(orderDao.getOrderBookItemsForType(anyInt(), any(OrderType.class)))
                .thenReturn(Collections.singletonList(getMockOrderBookItem()));

        Map<OrderType, List<OrderBookItem>> orderBook = orderService.getOrderBook("BTC/USD", OrderType.BUY);

        assertNotNull(orderBook);
        assertEquals(1, orderBook.size());
        assertTrue(orderBook.containsKey(OrderType.BUY));
        assertEquals(1, orderBook.get(OrderType.BUY).size());
        assertEquals(getMockOrderBookItem().getOrderType(), orderBook.get(OrderType.BUY).get(0).getOrderType());
        assertEquals(getMockOrderBookItem().getAmount(), orderBook.get(OrderType.BUY).get(0).getAmount());
        assertEquals(getMockOrderBookItem().getRate(), orderBook.get(OrderType.BUY).get(0).getRate());

        verify(currencyService, atLeastOnce()).findCurrencyPairIdByName(anyString());
        verify(orderDao, atLeastOnce()).getOrderBookItemsForType(anyInt(), any(OrderType.class));
    }

    @Test
    public void getOrderBook_OrderType_null() {
        when(currencyService.findCurrencyPairIdByName(anyString())).thenReturn(56);
        when(orderDao.getOrderBookItems(anyInt())).thenReturn(Collections.singletonList(getMockOrderBookItem()));

        Map<OrderType, List<OrderBookItem>> orderBook = orderService.getOrderBook("BTC/USD", null);

        assertNotNull(orderBook);
        assertEquals(1, orderBook.size());
        assertTrue(orderBook.containsKey(OrderType.BUY));
        assertEquals(1, orderBook.get(OrderType.BUY).size());
        assertEquals(getMockOrderBookItem().getOrderType(), orderBook.get(OrderType.BUY).get(0).getOrderType());
        assertEquals(getMockOrderBookItem().getAmount(), orderBook.get(OrderType.BUY).get(0).getAmount());
        assertEquals(getMockOrderBookItem().getRate(), orderBook.get(OrderType.BUY).get(0).getRate());

        verify(currencyService, atLeastOnce()).findCurrencyPairIdByName(anyString());
        verify(orderDao, atLeastOnce()).getOrderBookItems(anyInt());
    }

    @Test
    public void getTradeHistory() {
        TradeHistoryDto dto = new TradeHistoryDto();
        dto.setOrderId(23);
        dto.setDateAcceptance(LocalDateTime.of(2019, 4, 9, 15, 42, 33));
        dto.setDateCreation(LocalDateTime.of(2019, 4, 9, 15, 40, 0));
        dto.setAmount(BigDecimal.TEN);
        dto.setPrice(BigDecimal.ONE);
        dto.setTotal(BigDecimal.TEN);
        dto.setCommission(BigDecimal.ZERO);
        dto.setOrderType(OrderType.BUY);

        when(currencyService.findCurrencyPairIdByName(anyString())).thenReturn(45);
        when(orderDao.getTradeHistory(
                anyInt(),
                any(LocalDateTime.class),
                any(LocalDateTime.class),
                anyInt(),
                anyString())).thenReturn(Collections.singletonList(dto));

        List<TradeHistoryDto> tradeHistory = orderService.getTradeHistory(
                "BTC/USD",
                LocalDate.of(2019, 4, 9),
                LocalDate.of(2019, 4, 9),
                5,
                "DESCRIPTION");

        assertNotNull(tradeHistory);
        assertEquals(1, tradeHistory.size());
        assertEquals(dto.getOrderId(), tradeHistory.get(0).getOrderId());
        assertEquals(dto.getDateAcceptance(), tradeHistory.get(0).getDateAcceptance());
        assertEquals(dto.getDateCreation(), tradeHistory.get(0).getDateCreation());
        assertEquals(dto.getAmount(), tradeHistory.get(0).getAmount());
        assertEquals(dto.getPrice(), tradeHistory.get(0).getPrice());
        assertEquals(dto.getTotal(), tradeHistory.get(0).getTotal());
        assertEquals(dto.getCommission(), tradeHistory.get(0).getCommission());
        assertEquals(dto.getOrderType(), tradeHistory.get(0).getOrderType());

        verify(currencyService, atLeastOnce()).findCurrencyPairIdByName(anyString());
        verify(orderDao, atLeastOnce()).getTradeHistory(
                anyInt(),
                any(LocalDateTime.class),
                any(LocalDateTime.class),
                anyInt(),
                anyString());
    }

    @Test
    public void getUserOpenOrders_currencyPairName_not_null() {
        when(userService.getIdByEmail(any())).thenReturn(15);
        when(currencyService.findCurrencyPairIdByName(anyString())).thenReturn(105);
        when(orderDao.getUserOpenOrders(anyInt(), anyInt())).thenReturn(Collections.singletonList(getUserOrdersDto()));

        List<UserOrdersDto> userOpenOrders = orderService.getUserOpenOrders("BTC/USD");

        assertNotNull(userOpenOrders);
        assertEquals(1, userOpenOrders.size());
        assertEquals(getUserOrdersDto().getCurrencyPair(), userOpenOrders.get(0).getCurrencyPair());
        assertEquals(getUserOrdersDto().getAmount(), userOpenOrders.get(0).getAmount());
        assertEquals(getUserOrdersDto().getOrderType(), userOpenOrders.get(0).getOrderType());
        assertEquals(getUserOrdersDto().getPrice(), userOpenOrders.get(0).getPrice());
        assertEquals(getUserOrdersDto().getDateCreation(), userOpenOrders.get(0).getDateCreation());
        assertEquals(getUserOrdersDto().getDateAcceptance(), userOpenOrders.get(0).getDateAcceptance());

        verify(userService, atLeastOnce()).getIdByEmail(any());
        verify(currencyService, atLeastOnce()).findCurrencyPairIdByName(anyString());
        verify(orderDao, atLeastOnce()).getUserOpenOrders(anyInt(), anyInt());
    }

    @Test
    public void getUserOpenOrders_currencyPairName_null() {
        when(userService.getIdByEmail(any())).thenReturn(15);
        when(orderDao.getUserOpenOrders(anyInt(), any())).thenReturn(Collections.singletonList(getUserOrdersDto()));

        List<UserOrdersDto> userOpenOrders = orderService.getUserOpenOrders(null);

        assertNotNull(userOpenOrders);
        assertEquals(1, userOpenOrders.size());
        assertEquals(getUserOrdersDto().getCurrencyPair(), userOpenOrders.get(0).getCurrencyPair());
        assertEquals(getUserOrdersDto().getAmount(), userOpenOrders.get(0).getAmount());
        assertEquals(getUserOrdersDto().getOrderType(), userOpenOrders.get(0).getOrderType());
        assertEquals(getUserOrdersDto().getPrice(), userOpenOrders.get(0).getPrice());
        assertEquals(getUserOrdersDto().getDateCreation(), userOpenOrders.get(0).getDateCreation());
        assertEquals(getUserOrdersDto().getDateAcceptance(), userOpenOrders.get(0).getDateAcceptance());

        verify(userService, atLeastOnce()).getIdByEmail(any());
        verify(orderDao, atLeastOnce()).getUserOpenOrders(anyInt(), any());
    }

    @Test
    public void getUserClosedOrders() {
        when(userService.getIdByEmail(any())).thenReturn(1);
        when(currencyService.findCurrencyPairIdByName(anyString())).thenReturn(21);
        when(orderDao.getUserOrdersByStatus(anyInt(), anyInt(), any(OrderStatus.class), anyInt(), anyInt()))
                .thenReturn(Collections.singletonList(getUserOrdersDto()));

        List<UserOrdersDto> userCanceledOrders = orderService.getUserClosedOrders("BTC/USD", 1, 2);

        assertNotNull(userCanceledOrders);
        assertEquals(1, userCanceledOrders.size());
        assertEquals(getUserOrdersDto().getCurrencyPair(), userCanceledOrders.get(0).getCurrencyPair());
        assertEquals(getUserOrdersDto().getAmount(), userCanceledOrders.get(0).getAmount());
        assertEquals(getUserOrdersDto().getOrderType(), userCanceledOrders.get(0).getOrderType());
        assertEquals(getUserOrdersDto().getPrice(), userCanceledOrders.get(0).getPrice());
        assertEquals(getUserOrdersDto().getDateCreation(), userCanceledOrders.get(0).getDateCreation());
        assertEquals(getUserOrdersDto().getDateAcceptance(), userCanceledOrders.get(0).getDateAcceptance());

        verify(userService, atLeastOnce()).getIdByEmail(any());
        verify(currencyService, atLeastOnce()).findCurrencyPairIdByName(anyString());
        verify(orderDao, atLeastOnce()).getUserOrdersByStatus(
                anyInt(),
                anyInt(),
                any(OrderStatus.class),
                anyInt(),
                anyInt());
    }

    @Test
    public void getUserCanceledOrders() {
        when(userService.getIdByEmail(any())).thenReturn(1);
        when(currencyService.findCurrencyPairIdByName(anyString())).thenReturn(1);
        when(orderDao.getUserOrdersByStatus(
                anyInt(),
                anyInt(),
                any(OrderStatus.class),
                anyInt(),
                anyInt())).thenReturn(Collections.singletonList(getUserOrdersDto()));

        List<UserOrdersDto> userCanceledOrders = orderService.getUserCanceledOrders("BTC/USD", 14, 2);

        assertNotNull(userCanceledOrders);
        assertEquals(1, userCanceledOrders.size());
        assertEquals(getUserOrdersDto().getCurrencyPair(), userCanceledOrders.get(0).getCurrencyPair());
        assertEquals(getUserOrdersDto().getAmount(), userCanceledOrders.get(0).getAmount());
        assertEquals(getUserOrdersDto().getOrderType(), userCanceledOrders.get(0).getOrderType());
        assertEquals(getUserOrdersDto().getPrice(), userCanceledOrders.get(0).getPrice());
        assertEquals(getUserOrdersDto().getDateCreation(), userCanceledOrders.get(0).getDateCreation());
        assertEquals(getUserOrdersDto().getDateAcceptance(), userCanceledOrders.get(0).getDateAcceptance());

        verify(userService, atLeastOnce()).getIdByEmail(any());
        verify(currencyService, atLeastOnce()).findCurrencyPairIdByName(anyString());
        verify(orderDao, atLeastOnce()).getUserOrdersByStatus(
                anyInt(),
                anyInt(),
                any(OrderStatus.class),
                anyInt(),
                anyInt());
    }

    @Test
    public void getAllUserOrders() {
        when(userService.getIdByEmail(any())).thenReturn(15);
        when(currencyService.findCurrencyPairIdByName(anyString())).thenReturn(1);
        when(orderDao.getUserOrders(anyInt(), anyInt(), anyInt(), anyInt()))
                .thenReturn(Collections.singletonList(getUserOrdersDto()));

        List<UserOrdersDto> allUserOrders = orderService.getAllUserOrders("BTC/USD", 20, 3);

        assertNotNull(allUserOrders);
        assertEquals(1, allUserOrders.size());
        assertEquals(getUserOrdersDto().getCurrencyPair(), allUserOrders.get(0).getCurrencyPair());
        assertEquals(getUserOrdersDto().getAmount(), allUserOrders.get(0).getAmount());
        assertEquals(getUserOrdersDto().getOrderType(), allUserOrders.get(0).getOrderType());
        assertEquals(getUserOrdersDto().getPrice(), allUserOrders.get(0).getPrice());
        assertEquals(getUserOrdersDto().getDateCreation(), allUserOrders.get(0).getDateCreation());
        assertEquals(getUserOrdersDto().getDateAcceptance(), allUserOrders.get(0).getDateAcceptance());

        verify(userService, atLeastOnce()).getIdByEmail(any());
        verify(currencyService, atLeastOnce()).findCurrencyPairIdByName(anyString());
        verify(orderDao, atLeastOnce()).getUserOrders(anyInt(), anyInt(), anyInt(), anyInt());
    }

    @Test
    public void getOpenOrders() {
        OpenOrderDto dto = new OpenOrderDto();
        dto.setId(15);
        dto.setOrderType("BUY");
        dto.setAmount(BigDecimal.TEN);
        dto.setPrice(BigDecimal.valueOf(1.325));

        when(currencyService.findCurrencyPairIdByName(anyString())).thenReturn(100);
        when(orderDao.getOpenOrders(anyInt(), any(OrderType.class))).thenReturn(Collections.singletonList(dto));

        List<OpenOrderDto> openOrders = orderService.getOpenOrders("BTC/USD", OrderType.BUY);

        assertNotNull(openOrders);
        assertEquals(1, openOrders.size());
        assertEquals(dto.getId(), openOrders.get(0).getId());
        assertEquals(dto.getOrderType(), openOrders.get(0).getOrderType());
        assertEquals(dto.getAmount(), openOrders.get(0).getAmount());
        assertEquals(dto.getPrice(), openOrders.get(0).getPrice());

        verify(currencyService, atLeastOnce()).findCurrencyPairIdByName(anyString());
        verify(orderDao, atLeastOnce()).getOpenOrders(anyInt(), any(OrderType.class));
    }

    @Test
    public void getUserTradeHistoryByCurrencyPair() {
        UserTradeHistoryDto dto = new UserTradeHistoryDto();
        dto.setUserId(100);
        dto.setIsMaker(Boolean.TRUE);

        when(userService.getIdByEmail(any())).thenReturn(100);
        when(currencyService.findCurrencyPairIdByName(anyString())).thenReturn(1);
        when(orderDao.getUserTradeHistoryByCurrencyPair(
                anyInt(),
                anyInt(),
                any(LocalDateTime.class),
                any(LocalDateTime.class),
                anyInt())).thenReturn(Collections.singletonList(dto));

        List<UserTradeHistoryDto> userTradeHistoryByCurrencyPair = orderService.getUserTradeHistoryByCurrencyPair(
                "BTC/USD",
                LocalDate.of(2019, 4, 8),
                LocalDate.of(2019, 4, 9),
                50);

        assertNotNull(userTradeHistoryByCurrencyPair);
        assertEquals(1, userTradeHistoryByCurrencyPair.size());
        assertEquals(dto.getUserId(), userTradeHistoryByCurrencyPair.get(0).getUserId());
        assertEquals(dto.getIsMaker(), userTradeHistoryByCurrencyPair.get(0).getIsMaker());

        verify(userService, atLeastOnce()).getIdByEmail(any());
        verify(currencyService, atLeastOnce()).findCurrencyPairIdByName(anyString());
        verify(orderDao, atLeastOnce()).getUserTradeHistoryByCurrencyPair(
                anyInt(),
                anyInt(),
                any(LocalDateTime.class),
                any(LocalDateTime.class),
                anyInt());
    }

    @Test
    public void getOrderTransactions() {
        TransactionDto dto = TransactionDto.builder()
                .transactionId(111)
                .walletId(555)
                .amount(BigDecimal.TEN)
                .commission(BigDecimal.ONE)
                .currency("3254")
                .time(LocalDateTime.of(2019, 4, 9, 12, 26, 16))
                .operationType(OperationType.BUY)
                .orderStatus(OrderStatus.OPENED)
                .transactionStatus(TransactionStatus.CREATED)
                .build();

        when(userService.getIdByEmail(any())).thenReturn(100);
        when(orderDao.getOrderTransactions(anyInt(), anyInt())).thenReturn(Collections.singletonList(dto));

        List<TransactionDto> orderTransactions = orderService.getOrderTransactions(1);

        assertNotNull(orderTransactions);
        assertEquals(1, orderTransactions.size());
        assertEquals(dto.getTransactionId(), orderTransactions.get(0).getTransactionId());
        assertEquals(dto.getWalletId(), orderTransactions.get(0).getWalletId());
        assertEquals(dto.getAmount(), orderTransactions.get(0).getAmount());
        assertEquals(dto.getCommission(), orderTransactions.get(0).getCommission());
        assertEquals(dto.getCurrency(), orderTransactions.get(0).getCurrency());
        assertEquals(dto.getTime(), orderTransactions.get(0).getTime());
        assertEquals(dto.getOperationType(), orderTransactions.get(0).getOperationType());
        assertEquals(dto.getOrderStatus(), orderTransactions.get(0).getOrderStatus());
        assertEquals(dto.getTransactionStatus(), orderTransactions.get(0).getTransactionStatus());

        verify(userService, atLeastOnce()).getIdByEmail(any());
        verify(orderDao, atLeastOnce()).getOrderTransactions(anyInt(), anyInt());
    }

    @Test
    public void getCurrencyPairTurnoverByPeriodAndRoles() {
        CurrencyPairTurnoverReportDto dto = CurrencyPairTurnoverReportDto.builder()
                .currencyPairId(100)
                .currencyPairName("BTC/USD")
                .currencyPairId(1)
                .currencyAccountingName("ACCOUNTING_NAME")
                .quantity(10)
                .amountConvert(BigDecimal.ONE)
                .amountCommission(BigDecimal.TEN)
                .build();

        when(orderDao.getCurrencyPairTurnoverByPeriodAndRoles(
                any(LocalDateTime.class),
                any(LocalDateTime.class),
                anyListOf(UserRole.class))).thenReturn(Collections.singletonList(dto));

        List<CurrencyPairTurnoverReportDto> currencyPairTurnoverByPeriodAndRoles = orderService
                .getCurrencyPairTurnoverByPeriodAndRoles(
                        LocalDateTime.of(2019, 4, 8, 10, 45, 11),
                        LocalDateTime.of(2019, 4, 9, 12, 45, 11),
                        getUserRoles());

        assertNotNull(currencyPairTurnoverByPeriodAndRoles);
        assertEquals(1, currencyPairTurnoverByPeriodAndRoles.size());
        assertEquals(dto.getCurrencyPairId(), currencyPairTurnoverByPeriodAndRoles.get(0).getCurrencyPairId());
        assertEquals(dto.getCurrencyPairName(), currencyPairTurnoverByPeriodAndRoles.get(0).getCurrencyPairName());
        assertEquals(dto.getCurrencyAccountingName(), currencyPairTurnoverByPeriodAndRoles.get(0).getCurrencyAccountingName());
        assertEquals(dto.getQuantity(), currencyPairTurnoverByPeriodAndRoles.get(0).getQuantity());
        assertEquals(dto.getAmountConvert(), currencyPairTurnoverByPeriodAndRoles.get(0).getAmountConvert());
        assertEquals(dto.getAmountCommission(), currencyPairTurnoverByPeriodAndRoles.get(0).getAmountCommission());

        verify(orderDao, atLeastOnce()).getCurrencyPairTurnoverByPeriodAndRoles(
                any(LocalDateTime.class),
                any(LocalDateTime.class),
                anyListOf(UserRole.class));
    }

    @Test
    public void getUserSummaryOrdersData() {
        UserSummaryOrdersDto dto = UserSummaryOrdersDto.builder().build();
        dto.setCreatorEmail(USER_EMAIL);
        dto.setCreatorRole("USER");
        dto.setAcceptorRole("USER");
        dto.setAcceptorEmail(USER_EMAIL);
        dto.setCurrencyPairName("BTC/USD");
        dto.setCurrencyName("BTC/USD");
        dto.setAmount(BigDecimal.TEN);
        dto.setCommission(BigDecimal.ONE);

        List<UserRole> userRoles = getUserRoles();

        when(orderDao.getUserBuyOrdersDataByPeriodAndRoles(
                any(LocalDateTime.class),
                any(LocalDateTime.class),
                anyListOf(UserRole.class),
                anyInt())).thenReturn(Collections.singletonList(dto));
        when(orderDao.getUserSellOrdersDataByPeriodAndRoles(
                any(LocalDateTime.class),
                any(LocalDateTime.class),
                anyListOf(UserRole.class),
                anyInt())).thenReturn(Collections.singletonList(dto));

        Map<String, List<UserSummaryOrdersDto>> userSummaryOrdersData = orderService.getUserSummaryOrdersData(
                LocalDateTime.of(2019, 4, 8, 10, 45, 11),
                LocalDateTime.of(2019, 4, 9, 12, 45, 11),
                userRoles,
                77);

        assertNotNull(userSummaryOrdersData);
        assertEquals(2, userSummaryOrdersData.size());
        assertTrue(userSummaryOrdersData.containsKey("SELL"));
        assertEquals(1, userSummaryOrdersData.get("SELL").size());
        assertEquals(dto.getCreatorEmail(), userSummaryOrdersData.get("SELL").get(0).getCreatorEmail());
        assertEquals(dto.getCreatorRole(), userSummaryOrdersData.get("SELL").get(0).getCreatorRole());
        assertEquals(dto.getAcceptorEmail(), userSummaryOrdersData.get("SELL").get(0).getAcceptorEmail());
        assertEquals(dto.getAcceptorRole(), userSummaryOrdersData.get("SELL").get(0).getAcceptorRole());
        assertEquals(dto.getCurrencyName(), userSummaryOrdersData.get("SELL").get(0).getCurrencyName());
        assertEquals(dto.getCurrencyPairName(), userSummaryOrdersData.get("SELL").get(0).getCurrencyPairName());
        assertEquals(dto.getAmount(), userSummaryOrdersData.get("SELL").get(0).getAmount());
        assertEquals(dto.getCommission(), userSummaryOrdersData.get("SELL").get(0).getCommission());
        assertTrue(userSummaryOrdersData.containsKey("BUY"));
        assertEquals(1, userSummaryOrdersData.get("BUY").size());
        assertEquals(dto.getCreatorEmail(), userSummaryOrdersData.get("BUY").get(0).getCreatorEmail());
        assertEquals(dto.getCreatorRole(), userSummaryOrdersData.get("BUY").get(0).getCreatorRole());
        assertEquals(dto.getAcceptorEmail(), userSummaryOrdersData.get("BUY").get(0).getAcceptorEmail());
        assertEquals(dto.getAcceptorRole(), userSummaryOrdersData.get("BUY").get(0).getAcceptorRole());
        assertEquals(dto.getCurrencyName(), userSummaryOrdersData.get("BUY").get(0).getCurrencyName());
        assertEquals(dto.getCurrencyPairName(), userSummaryOrdersData.get("BUY").get(0).getCurrencyPairName());
        assertEquals(dto.getAmount(), userSummaryOrdersData.get("BUY").get(0).getAmount());
        assertEquals(dto.getCommission(), userSummaryOrdersData.get("BUY").get(0).getCommission());

        verify(orderDao, times(1)).getUserBuyOrdersDataByPeriodAndRoles(
                any(LocalDateTime.class),
                any(LocalDateTime.class),
                anyListOf(UserRole.class),
                anyInt());
        verify(orderDao, times(1)).getUserSellOrdersDataByPeriodAndRoles(
                any(LocalDateTime.class),
                any(LocalDateTime.class),
                anyListOf(UserRole.class),
                anyInt());
    }

    @Test
    public void getUserSummaryOrdersData_isEmpty() {
        List<UserRole> userRoles = getUserRoles();

        when(orderDao.getUserBuyOrdersDataByPeriodAndRoles(
                any(LocalDateTime.class),
                any(LocalDateTime.class),
                anyListOf(UserRole.class),
                anyInt())).thenReturn(Collections.EMPTY_LIST);
        when(orderDao.getUserSellOrdersDataByPeriodAndRoles(
                any(LocalDateTime.class),
                any(LocalDateTime.class),
                anyListOf(UserRole.class),
                anyInt())).thenReturn(Collections.EMPTY_LIST);

        Map<String, List<UserSummaryOrdersDto>> userSummaryOrdersData = orderService.getUserSummaryOrdersData(
                LocalDateTime.of(2019, 4, 8, 10, 45, 11),
                LocalDateTime.of(2019, 4, 9, 12, 45, 11),
                userRoles,
                77);

        assertNotNull(userSummaryOrdersData);
        assertEquals(2, userSummaryOrdersData.size());
        assertTrue(userSummaryOrdersData.containsKey("SELL"));
        assertTrue(userSummaryOrdersData.containsKey("BUY"));
        assertTrue(userSummaryOrdersData.containsValue(Collections.EMPTY_LIST));
        assertTrue(userSummaryOrdersData.containsValue(Collections.EMPTY_LIST));

        verify(orderDao, times(1)).getUserBuyOrdersDataByPeriodAndRoles(
                any(LocalDateTime.class),
                any(LocalDateTime.class),
                anyListOf(UserRole.class),
                anyInt());
        verify(orderDao, times(1)).getUserSellOrdersDataByPeriodAndRoles(
                any(LocalDateTime.class),
                any(LocalDateTime.class),
                anyListOf(UserRole.class),
                anyInt());
    }

    @Test
    public void logCallBackData() {
        doNothing().when(callBackDao).logCallBackData(any(CallBackLogDto.class));

        orderService.logCallBackData(new CallBackLogDto());

        verify(callBackDao, times(1)).logCallBackData(any(CallBackLogDto.class));
    }

    @Test
    public void getMyOrdersWithStateMap_foundOneRecordTest() {
        log.debug("getMyOrdersWithStateMap_foundOneRecordTest() - start");

        LocalDateTime now = LocalDateTime.now();

        doReturn(1).when(orderDao).getMyOrdersWithStateCount(
                1,
                new CurrencyPair("BTC/USD"),
                StringUtils.EMPTY,
                OrderStatus.CLOSED,
                StringUtils.EMPTY,
                false,
                now.minusDays(1),
                now);

        doReturn(Collections.singletonList(new OrderWideListDto())).when(orderDao).getMyOrdersWithState(
                1,
                new CurrencyPair("BTC/USD"),
                StringUtils.EMPTY,
                OrderStatus.CLOSED,
                StringUtils.EMPTY,
                15,
                0,
                false,
                "DESC",
                now.minusDays(1),
                now,
                Locale.ENGLISH);

        Pair<Integer, List<OrderWideListDto>> pair = orderService.getMyOrdersWithStateMap(
                1,
                new CurrencyPair("BTC/USD"),
                StringUtils.EMPTY,
                OrderStatus.CLOSED,
                StringUtils.EMPTY,
                15,
                0,
                false,
                "DESC",
                now.minusDays(1),
                now,
                false,
                Locale.ENGLISH);

        verify(orderDao, atLeastOnce()).getMyOrdersWithStateCount(anyInt(), any(CurrencyPair.class), anyString(),
                any(OrderStatus.class), anyString(), anyBoolean(),
                any(LocalDateTime.class), any(LocalDateTime.class));

        verify(orderDao, atLeastOnce()).getMyOrdersWithState(anyInt(), any(CurrencyPair.class), anyString(),
                any(OrderStatus.class), anyString(), anyInt(), anyInt(), anyBoolean(), anyString(),
                any(LocalDateTime.class), any(LocalDateTime.class), any(Locale.class));

        assertNotNull("Pair could not be null", pair);
        assertEquals("Number of records could be equals", 1, (int) pair.getLeft());
        assertFalse("List could not be empty", pair.getRight().isEmpty());
        assertEquals("The size of list could be equal to one", 1, pair.getRight().size());

        log.debug("getMyOrdersWithStateMap_foundOneRecordTest() - end");
    }

    @Test
    public void getMyOrdersWithStateMap_notFoundRecordsTest() {
        log.debug("getMyOrdersWithStateMap_notFoundRecordsTest() - start");

        LocalDateTime now = LocalDateTime.now();
        doReturn(0).when(orderDao).getMyOrdersWithStateCount(
                1,
                new CurrencyPair("BTC/USD"),
                StringUtils.EMPTY,
                OrderStatus.CLOSED,
                StringUtils.EMPTY,
                false,
                now.minusDays(1),
                now);

        Pair<Integer, List<OrderWideListDto>> pair = orderService.getMyOrdersWithStateMap(
                1,
                new CurrencyPair("BTC/USD"),
                StringUtils.EMPTY,
                OrderStatus.CLOSED,
                StringUtils.EMPTY,
                15,
                0,
                false,
                "DESC",
                now.minusDays(1),
                now,
                false,
                Locale.ENGLISH);

        verify(orderDao, atLeastOnce()).getMyOrdersWithStateCount(anyInt(), any(CurrencyPair.class), anyString(),
                any(OrderStatus.class), anyString(), anyBoolean(),
                any(LocalDateTime.class), any(LocalDateTime.class));

        assertNotNull("Pair could not be null", pair);
        assertEquals("Number of records could be equals", 0, (int) pair.getLeft());
        assertTrue("List could be empty", pair.getRight().isEmpty());

        log.debug("getMyOrdersWithStateMap_notFoundRecordsTest() - end");
    }

    @Test
    public void getMyOrdersWithStateMap_recordsCount_more_zero() {
        OrderWideListDto dto = new OrderWideListDto();
        dto.setUserId(100);
        dto.setCurrencyPairId(100);
        dto.setCurrencyPairName("BTC/USD");
        dto.setStatus(OrderStatus.OPENED);

        when(orderDao.getMyOrdersWithStateCount(
                anyInt(),
                any(CurrencyPair.class),
                anyString(),
                any(OrderStatus.class),
                anyString(),
                anyBoolean(),
                any(LocalDateTime.class),
                any(LocalDateTime.class)
        )).thenReturn(1);
        when(orderDao.getMyOrdersWithState(
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
                any(Locale.class)
        )).thenReturn(Collections.singletonList(dto));

        Pair<Integer, List<OrderWideListDto>> myOrdersWithStateMap = orderService.getMyOrdersWithStateMap(
                100,
                new CurrencyPair(),
                "BTC/USD",
                OrderStatus.CANCELLED,
                "SCOUP",
                50,
                10,
                Boolean.TRUE,
                "DESC",
                LocalDateTime.of(2019, 4, 8, 15, 10, 10),
                LocalDateTime.of(2019, 4, 8, 16, 10, 10),
                false,
                Locale.ENGLISH);

        assertNotNull(myOrdersWithStateMap);
        assertEquals(Integer.valueOf(1), myOrdersWithStateMap.getKey());
        assertEquals(1, myOrdersWithStateMap.getValue().size());
        assertEquals(dto.getUserId(), myOrdersWithStateMap.getValue().get(0).getUserId());
        assertEquals(dto.getCurrencyPairId(), myOrdersWithStateMap.getValue().get(0).getCurrencyPairId());
        assertEquals(dto.getCurrencyPairName(), myOrdersWithStateMap.getValue().get(0).getCurrencyPairName());
        assertEquals(dto.getStatus(), myOrdersWithStateMap.getValue().get(0).getStatus());

        verify(orderDao, times(1)).getMyOrdersWithStateCount(
                anyInt(),
                any(CurrencyPair.class),
                anyString(),
                any(OrderStatus.class),
                anyString(),
                anyBoolean(),
                any(LocalDateTime.class),
                any(LocalDateTime.class));
        verify(orderDao, times(1)).getMyOrdersWithState(
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
    }

    @Test
    public void getMyOrdersWithStateMap_recordsCount_less_zero() {
        when(orderDao.getMyOrdersWithStateCount(
                anyInt(),
                any(CurrencyPair.class),
                anyString(),
                any(OrderStatus.class),
                anyString(),
                anyBoolean(),
                any(LocalDateTime.class),
                any(LocalDateTime.class))).thenReturn(0);

        Pair<Integer, List<OrderWideListDto>> myOrdersWithStateMap = orderService.getMyOrdersWithStateMap(
                100,
                new CurrencyPair(),
                "BTC/USD",
                OrderStatus.CANCELLED,
                "SCOUP",
                50,
                10,
                Boolean.TRUE,
                "DESC",
                LocalDateTime.of(2019, 4, 8, 15, 10, 10),
                LocalDateTime.of(2019, 4, 8, 16, 10, 10),
                false,
                Locale.ENGLISH);

        assertNotNull(myOrdersWithStateMap);
        assertEquals(Integer.valueOf(0), myOrdersWithStateMap.getKey());
        assertEquals(Collections.EMPTY_LIST, myOrdersWithStateMap.getValue());

        verify(orderDao, times(1)).getMyOrdersWithStateCount(
                anyInt(),
                any(CurrencyPair.class),
                anyString(),
                any(OrderStatus.class),
                anyString(),
                anyBoolean(),
                any(LocalDateTime.class),
                any(LocalDateTime.class));
    }

    @Test
    public void cancelOrders_true() {
        boolean cancelOrders = orderService.cancelOrders(Collections.EMPTY_LIST);

        assertEquals(Boolean.TRUE, cancelOrders);
    }

    @Test
    public void cancelOrders_false() {
        when(stopOrderService.cancelOrder(anyInt(), any())).thenReturn(Boolean.FALSE);

        boolean cancelOrders = orderService.cancelOrders(Collections.singletonList(1));

        assertEquals(Boolean.FALSE, cancelOrders);

        verify(stopOrderService, times(1)).cancelOrder(anyInt(), any());
    }

    @Test
    public void getOrdersForExcel() {
        OrderWideListDto dto = new OrderWideListDto();
        dto.setUserId(100);
        dto.setCurrencyPairId(100);
        dto.setCurrencyPairName("BTC/USD");
        dto.setStatus(OrderStatus.OPENED);

        when(orderDao.getMyOrdersWithState(
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
                any(Locale.class)
        )).thenReturn(Collections.singletonList(dto));

        List<OrderWideListDto> ordersForExcel = orderService.getOrdersForExcel(
                100,
                new CurrencyPair(),
                "TEST_CURRENCY_NAME",
                OrderStatus.CANCELLED,
                "SCOPE",
                20,
                10,
                Boolean.TRUE,
                "DESC",
                LocalDateTime.of(2019, 4, 8, 15, 10, 10),
                LocalDateTime.of(2019, 4, 8, 16, 10, 10),
                Locale.ENGLISH
        );

        assertNotNull(ordersForExcel);
        assertEquals(1, ordersForExcel.size());
        assertEquals(dto.getUserId(), ordersForExcel.get(0).getUserId());
        assertEquals(dto.getCurrencyPairId(), ordersForExcel.get(0).getCurrencyPairId());
        assertEquals(dto.getCurrencyPairName(), ordersForExcel.get(0).getCurrencyPairName());
        assertEquals(dto.getStatus(), ordersForExcel.get(0).getStatus());

        verify(orderDao, times(1)).getMyOrdersWithState(
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
                any(Locale.class)
        );
    }

    @Test
    public void getOrderExcelFile_success() throws Exception {
        ReportDto transactionExcelFile = orderService
                .getOrderExcelFile(Collections.singletonList(new OrderWideListDto()));

        assertNotNull(transactionExcelFile);
        assertEquals(String.format("Orders_%s", LocalDateTime.now().format(FORMATTER_FOR_NAME)),
                transactionExcelFile.getFileName());
    }

    @Test
    public void getTransactionExcelFile() throws Exception {
        ReportDto transactionExcelFile = orderService.getTransactionExcelFile(Collections.singletonList(new MyInputOutputHistoryDto()));

        assertNotNull(transactionExcelFile);
        assertEquals(String.format("Transactions_%s", LocalDateTime.now().format(FORMATTER_FOR_NAME)),
                transactionExcelFile.getFileName());
    }

    @Test
    public void getTransactionExcelFile_transactions_isEmpty() throws Exception {
        ReportDto transactionExcelFile = orderService.getTransactionExcelFile(Collections.EMPTY_LIST);

        assertNotNull(transactionExcelFile);
        assertEquals(String.format("Transactions_%s", LocalDateTime.now().format(FORMATTER_FOR_NAME)),
                transactionExcelFile.getFileName());
    }

    @Test
    public void getAllCurrenciesMarkersForAllPairsModel() {
        ExOrderStatisticsShortByPairsDto dto = new ExOrderStatisticsShortByPairsDto();
        dto.setCurrencyPairId(100);
        dto.setCurrencyPairName("BTC/USD");

        when(exchangeRatesHolder.getAllRates()).thenReturn(Collections.singletonList(dto));

        List<ExOrderStatisticsShortByPairsDto> allCurrenciesMarkersForAllPairsModel = orderService
                .getAllCurrenciesMarkersForAllPairsModel();

        assertNotNull(allCurrenciesMarkersForAllPairsModel);
        assertEquals(1, allCurrenciesMarkersForAllPairsModel.size());
        assertEquals(dto.getCurrencyPairId(), allCurrenciesMarkersForAllPairsModel.get(0).getCurrencyPairId());
        assertEquals(dto.getCurrencyPairName(), allCurrenciesMarkersForAllPairsModel.get(0).getCurrencyPairName());

        verify(exchangeRatesHolder, times(1)).getAllRates();
    }

    @Test
    public void getLastOrderPriceByCurrencyPair() {
        when(orderDao.getLastOrderPriceByCurrencyPair(anyInt())).thenReturn(Optional.of(BigDecimal.TEN));

        Optional<BigDecimal> lastOrderPriceByCurrencyPair = orderService.getLastOrderPriceByCurrencyPair(new CurrencyPair());

        assertNotNull(lastOrderPriceByCurrencyPair);
        assertEquals(Optional.of(BigDecimal.TEN), lastOrderPriceByCurrencyPair);

        verify(orderDao, times(1)).getLastOrderPriceByCurrencyPair(anyInt());
    }

    @Test
    public void getMyOpenOrdersForWs_CurrencyPair_equals_null() {
        when(currencyService.getCurrencyPairByName(anyString())).thenReturn(null);

        List<OrdersListWrapper> ordersListWrappers = orderService.getMyOpenOrdersForWs(
                "BTC/USD",
                "USER_NAME");

        assertNull(ordersListWrappers);

        verify(currencyService, times(1)).getCurrencyPairByName(anyString());
    }

    @Test
    public void getMyOpenOrdersForWs_CurrencyPair_not_null() {
        CurrencyPair cp = new CurrencyPair();
        cp.setId(100);

        when(currencyService.getCurrencyPairByName(anyString())).thenReturn(cp);
        when(userService.getIdByEmail(anyString())).thenReturn(200);
        when(orderDao.getMyOpenOrdersForCurrencyPair(
                any(CurrencyPair.class),
                any(OrderType.class),
                anyInt())
        ).thenReturn(getMockOrderListDto());

        List<OrdersListWrapper> ordersListWrappers = orderService.getMyOpenOrdersForWs(
                "BTC/USD",
                "USER_NAME");

        assertNotNull(ordersListWrappers);
        assertEquals(2, ordersListWrappers.size());
        assertEquals(100, ordersListWrappers.get(0).getCurrencyPairId());
        assertEquals(OrderType.SELL.toString(), ordersListWrappers.get(0).getType());
        assertEquals(100, ordersListWrappers.get(1).getCurrencyPairId());
        assertEquals(OrderType.BUY.toString(), ordersListWrappers.get(1).getType());

        verify(currencyService, times(1)).getCurrencyPairByName(anyString());
        verify(userService, times(1)).getIdByEmail(anyString());
        verify(orderDao, times(2)).getMyOpenOrdersForCurrencyPair(
                any(CurrencyPair.class),
                any(OrderType.class),
                anyInt());
    }

    @Test
    public void findAllOrderBookItems_ExOrderStatisticsShortByPairsDto_not_null() {
        BigDecimal item1 = new BigDecimal(getMockOrderListDto().get(0).getAmountConvert());
        BigDecimal item2 = new BigDecimal(getMockOrderListDto().get(1).getAmountConvert());
        BigDecimal total = item1.add(item2);

        when(orderDao.findAllByOrderTypeAndCurrencyId(anyInt(), any(OrderType.class))).thenReturn(getMockOrderListDto());
        when(exchangeRatesHolder.getOne(anyInt())).thenReturn(getMockExOrderStatisticsShortByPairsDto(CurrencyPairType.MAIN));

        OrderBookWrapperDto orderBookWrapperDto = orderService.findAllOrderBookItems(OrderType.SELL, 1, 5);

        assertNotNull(orderBookWrapperDto);
        assertEquals("SELL", orderBookWrapperDto.getOrderType().toString());
        assertEquals("0", orderBookWrapperDto.getLastExrate());
        assertEquals("0", orderBookWrapperDto.getPreLastExrate());
        assertEquals(total, orderBookWrapperDto.getTotal());
        assertEquals(2, orderBookWrapperDto.getOrderBookItems().size());
        assertEquals(new Integer(1), orderBookWrapperDto.getOrderBookItems().get(0).getCurrencyPairId());
        assertEquals("SELL", orderBookWrapperDto.getOrderBookItems().get(0).getOrderType().toString());
        assertNull(orderBookWrapperDto.getOrderBookItems().get(0).getCurrencyPairName());
        assertEquals(BigDecimal.valueOf(4150), orderBookWrapperDto.getOrderBookItems().get(0).getExrate());
        assertEquals(BigDecimal.valueOf(0.055629498), orderBookWrapperDto.getOrderBookItems().get(0).getAmount());
        assertEquals(BigDecimal.valueOf(230.8624167), orderBookWrapperDto.getOrderBookItems().get(0).getTotal());
        assertEquals(BigDecimal.valueOf(0.055629498), orderBookWrapperDto.getOrderBookItems().get(0).getSumAmount());

        verify(orderDao, times(1)).findAllByOrderTypeAndCurrencyId(anyInt(), any(OrderType.class));
        verify(exchangeRatesHolder, times(1)).getOne(anyInt());
    }

    @Test
    public void findAllOrderBookItems_ExOrderStatisticsShortByPairsDto_equals_null() {
        BigDecimal item1 = new BigDecimal(getMockOrderListDto().get(0).getAmountConvert());
        BigDecimal item2 = new BigDecimal(getMockOrderListDto().get(1).getAmountConvert());
        BigDecimal total = item1.add(item2);

        when(orderDao.findAllByOrderTypeAndCurrencyId(anyInt(), any(OrderType.class))).thenReturn(getMockOrderListDto());
        when(exchangeRatesHolder.getOne(anyInt())).thenReturn(null);

        OrderBookWrapperDto orderBookWrapperDto = orderService.findAllOrderBookItems(OrderType.SELL, 1, 5);

        assertNotNull(orderBookWrapperDto);
        assertEquals("SELL", orderBookWrapperDto.getOrderType().toString());
        assertNull(orderBookWrapperDto.getLastExrate());
        assertNull(orderBookWrapperDto.getPreLastExrate());
        assertEquals(total, orderBookWrapperDto.getTotal());
        assertEquals(2, orderBookWrapperDto.getOrderBookItems().size());
        assertEquals(new Integer(1), orderBookWrapperDto.getOrderBookItems().get(0).getCurrencyPairId());
        assertEquals("SELL", orderBookWrapperDto.getOrderBookItems().get(0).getOrderType().toString());
        assertNull(orderBookWrapperDto.getOrderBookItems().get(0).getCurrencyPairName());
        assertEquals(BigDecimal.valueOf(4150), orderBookWrapperDto.getOrderBookItems().get(0).getExrate());
        assertEquals(BigDecimal.valueOf(0.055629498), orderBookWrapperDto.getOrderBookItems().get(0).getAmount());
        assertEquals(BigDecimal.valueOf(230.8624167), orderBookWrapperDto.getOrderBookItems().get(0).getTotal());
        assertEquals(BigDecimal.valueOf(0.055629498), orderBookWrapperDto.getOrderBookItems().get(0).getSumAmount());

        verify(orderDao, times(1)).findAllByOrderTypeAndCurrencyId(anyInt(), any(OrderType.class));
        verify(exchangeRatesHolder, times(1)).getOne(anyInt());
    }

    @Test
    public void findAllOrderBookItemsForAllPrecissions() {
        List<PrecissionsEnum> precisionsList = Arrays.asList(
                PrecissionsEnum.ONE,
                PrecissionsEnum.TWO,
                PrecissionsEnum.THREE,
                PrecissionsEnum.FOUR,
                PrecissionsEnum.FIVE);

        when(orderDao.findAllByOrderTypeAndCurrencyId(anyInt(), any(OrderType.class))).thenReturn(getMockOrderListDto());
        when(exchangeRatesHolder.getOne(anyInt())).thenReturn(getMockExOrderStatisticsShortByPairsDto(CurrencyPairType.MAIN));

        Map<PrecissionsEnum, String> allOrderBookItemsForAllPrecisions = orderService
                .findAllOrderBookItemsForAllPrecissions(OrderType.SELL, 1, precisionsList);

        assertNotNull(allOrderBookItemsForAllPrecisions);
        assertEquals(5, allOrderBookItemsForAllPrecisions.size());
        assertTrue(allOrderBookItemsForAllPrecisions.containsKey(PrecissionsEnum.ONE));
        assertTrue(allOrderBookItemsForAllPrecisions.containsKey(PrecissionsEnum.TWO));
        assertTrue(allOrderBookItemsForAllPrecisions.containsKey(PrecissionsEnum.THREE));
        assertTrue(allOrderBookItemsForAllPrecisions.containsKey(PrecissionsEnum.FOUR));
        assertTrue(allOrderBookItemsForAllPrecisions.containsKey(PrecissionsEnum.FIVE));

        verify(orderDao, atLeastOnce()).findAllByOrderTypeAndCurrencyId(anyInt(), any(OrderType.class));
        verify(exchangeRatesHolder, atLeastOnce()).getOne(anyInt());
    }

    @Test
    public void findAllOrderBookItemsForAllPrecissions_exception() throws Exception {
        List<PrecissionsEnum> precisionsList = Arrays.asList(
                PrecissionsEnum.ONE,
                PrecissionsEnum.TWO,
                PrecissionsEnum.THREE,
                PrecissionsEnum.FOUR,
                PrecissionsEnum.FIVE);

        when(orderDao.findAllByOrderTypeAndCurrencyId(anyInt(), any(OrderType.class))).thenReturn(getMockOrderListDto());
        when(exchangeRatesHolder.getOne(anyInt())).thenReturn(getMockExOrderStatisticsShortByPairsDto(CurrencyPairType.MAIN));
        doThrow(JsonProcessingException.class).when(objectMapper).writeValueAsString(any(Object.class));

        Map<PrecissionsEnum, String> allOrderBookItemsForAllPrecisions = orderService
                .findAllOrderBookItemsForAllPrecissions(OrderType.SELL, 1, precisionsList);

        assertNotNull(allOrderBookItemsForAllPrecisions);
        assertEquals(0, allOrderBookItemsForAllPrecisions.size());

        verify(orderDao, atLeastOnce()).findAllByOrderTypeAndCurrencyId(anyInt(), any(OrderType.class));
        verify(exchangeRatesHolder, atLeastOnce()).getOne(anyInt());
        verify(objectMapper, atLeastOnce()).writeValueAsString(any(Object.class));
    }

    @Test
    public void getRatesDataForCache() {
        when(orderDao.getRatesDataForCache(anyInt()))
                .thenReturn(Collections.singletonList(getMockExOrderStatisticsShortByPairsDto(CurrencyPairType.MAIN)));

        List<ExOrderStatisticsShortByPairsDto> ratesDataForCache = orderService.getRatesDataForCache(1);

        assertNotNull(ratesDataForCache);
        assertEquals(1, ratesDataForCache.size());
        assertEquals(new Integer(1), ratesDataForCache.get(0).getCurrencyPairId());
        assertEquals("BTC/USD", ratesDataForCache.get(0).getCurrencyPairName());
        assertEquals(new Integer(2), ratesDataForCache.get(0).getCurrencyPairPrecision());
        assertEquals("0", ratesDataForCache.get(0).getLastOrderRate());
        assertEquals("0", ratesDataForCache.get(0).getPredLastOrderRate());
        assertEquals("0", ratesDataForCache.get(0).getPercentChange());
        assertEquals("FIAT", ratesDataForCache.get(0).getMarket());
        assertEquals("0", ratesDataForCache.get(0).getPriceInUSD());
        assertEquals(CurrencyPairType.MAIN, ratesDataForCache.get(0).getType());
        assertEquals("0.000000000", ratesDataForCache.get(0).getVolume());
        assertEquals("0.000000000", ratesDataForCache.get(0).getCurrencyVolume());
        assertEquals("0.000000000", ratesDataForCache.get(0).getHigh24hr());
        assertEquals("0.000000000", ratesDataForCache.get(0).getLow24hr());
        assertEquals("0.000000000", ratesDataForCache.get(0).getLastOrderRate24hr());
        assertEquals("2019-04-03 14:52:14", ratesDataForCache.get(0).getLastUpdateCache());

        verify(orderDao, times(1)).getRatesDataForCache(anyInt());
    }

    @Test
    public void getAllDataForCache() {
        when(orderDao.getAllDataForCache(anyInt()))
                .thenReturn(Collections.singletonList(getMockExOrderStatisticsShortByPairsDto(CurrencyPairType.MAIN)));

        List<ExOrderStatisticsShortByPairsDto> ratesDataForCache = orderService.getAllDataForCache(1);

        assertNotNull(ratesDataForCache);
        assertEquals(1, ratesDataForCache.size());
        assertEquals(new Integer(1), ratesDataForCache.get(0).getCurrencyPairId());
        assertEquals("BTC/USD", ratesDataForCache.get(0).getCurrencyPairName());
        assertEquals(new Integer(2), ratesDataForCache.get(0).getCurrencyPairPrecision());
        assertEquals("0", ratesDataForCache.get(0).getLastOrderRate());
        assertEquals("0", ratesDataForCache.get(0).getPredLastOrderRate());
        assertEquals("0", ratesDataForCache.get(0).getPercentChange());
        assertEquals("FIAT", ratesDataForCache.get(0).getMarket());
        assertEquals("0", ratesDataForCache.get(0).getPriceInUSD());
        assertEquals(CurrencyPairType.MAIN, ratesDataForCache.get(0).getType());
        assertEquals("0.000000000", ratesDataForCache.get(0).getVolume());
        assertEquals("0.000000000", ratesDataForCache.get(0).getCurrencyVolume());
        assertEquals("0.000000000", ratesDataForCache.get(0).getHigh24hr());
        assertEquals("0.000000000", ratesDataForCache.get(0).getLow24hr());
        assertEquals("0.000000000", ratesDataForCache.get(0).getLastOrderRate24hr());
        assertEquals("2019-04-03 14:52:14", ratesDataForCache.get(0).getLastUpdateCache());

        verify(orderDao, times(1)).getAllDataForCache(anyInt());
    }

    private UserOrdersDto getUserOrdersDto() {
        return new UserOrdersDto(
                10,
                "BTC/USD",
                BigDecimal.TEN,
                "BUY",
                BigDecimal.ONE,
                LocalDateTime.of(2019, 4, 1, 12, 35, 21),
                LocalDateTime.of(2019, 4, 1, 15, 35, 21));
    }

    private CoinmarketApiDto getMockCoinmarketApiDto() {
        CoinmarketApiDto dto = new CoinmarketApiDto();
        dto.setCurrencyPairId(100);
        dto.setCurrency_pair_name("BTC/USD");
        dto.setFirst(BigDecimal.TEN);
        dto.setLast(BigDecimal.ONE);
        return dto;
    }

    private ExOrder getMockExOrder() {
        return getMockExOrder(3, BigDecimal.TEN);
    }

    private ExOrder getMockExOrder(int orderId, BigDecimal amountBase) {
        ExOrder exOrder = new ExOrder();
        exOrder.setId(orderId);
        exOrder.setAmountBase(amountBase);
        exOrder.setOperationType(OperationType.MANUAL);
        exOrder.setAmountConvert(BigDecimal.ONE);
        exOrder.setCommissionFixedAmount(BigDecimal.ONE);
        exOrder.setExRate(BigDecimal.ONE);
        exOrder.setComissionId(100);
        exOrder.setOrderBaseType(OrderBaseType.LIMIT);
        exOrder.setCurrencyPair(new CurrencyPair("BTC/USD"));
        return exOrder;
    }

    private ExOrder getTestExOrder(int orderId, BigDecimal amountBase) {
        ExOrder exOrder = new ExOrder();
        exOrder.setId(orderId);
        exOrder.setAmountBase(amountBase);
        exOrder.setOperationType(OperationType.SELL);
        exOrder.setAmountConvert(BigDecimal.ONE);
        exOrder.setCommissionFixedAmount(BigDecimal.ONE);
        exOrder.setExRate(BigDecimal.ONE);
        exOrder.setComissionId(100);
        exOrder.setOrderBaseType(OrderBaseType.LIMIT);
        exOrder.setCurrencyPair(new CurrencyPair("BTC/USD"));
        return exOrder;
    }

    private CacheData getMockCacheData(HttpServletRequest request) {
        return new CacheData(request, "cacheHashMap", Boolean.TRUE);
    }

    private Currency getMockCurrency() {
        return Currency.builder()
                .id(10)
                .name("NAME")
                .description("DESCRIPTION")
                .hidden(Boolean.FALSE)
                .build();
    }

    private OrderAcceptedHistoryDto getMockOrderAcceptedHistoryDto() {
        OrderAcceptedHistoryDto dto = new OrderAcceptedHistoryDto();
        dto.setOrderId(100);
        dto.setDateAcceptionTime(LocalDateTime.now().toString());
        dto.setAcceptionTime(Timestamp.valueOf(LocalDateTime.now()));
        dto.setRate("1.5");
        dto.setAmountBase("25");
        dto.setOperationType(OperationType.BUY);

        return dto;
    }

    private OrderBookItem getMockOrderBookItem() {
        OrderBookItem item = new OrderBookItem();
        item.setOrderType(OrderType.BUY);
        item.setAmount(BigDecimal.TEN);
        item.setRate(BigDecimal.ONE);

        return item;
    }

    private ExOrderStatisticsShortByPairsDto getExOrderStatisticsShortByPairsDto(CurrencyPairType pairType) {
        ExOrderStatisticsShortByPairsDto mock = getMockExOrderStatisticsShortByPairsDto(pairType);
        mock.setLastOrderRate("10.0");
        mock.setPairOrder(666);
        mock.setCurrency1Id(215);

        return mock;
    }

    private BackDealInterval getMockBackDealInterval() {
        BackDealInterval interval = new BackDealInterval();
        interval.setIntervalValue(12);
        interval.setIntervalType(IntervalType.HOUR);

        return interval;
    }

    private CandleChartItemDto getMockCandleChartItemDto() {
        CandleChartItemDto candleChartItemDto = new CandleChartItemDto();
        candleChartItemDto.setBeginPeriod(LocalDateTime.of(2019, 4, 4, 15, 9, 10));
        candleChartItemDto.setEndPeriod(LocalDateTime.of(2019, 4, 4, 15, 15, 10));
        candleChartItemDto.setOpenRate(BigDecimal.TEN);
        candleChartItemDto.setCloseRate(BigDecimal.TEN);
        candleChartItemDto.setLowRate(BigDecimal.TEN);
        candleChartItemDto.setHighRate(BigDecimal.TEN);
        candleChartItemDto.setBaseVolume(BigDecimal.TEN);
        candleChartItemDto.setBeginDate(Timestamp.valueOf(LocalDateTime.of(2019, 4, 4, 15, 9, 10)));
        candleChartItemDto.setEndDate(Timestamp.valueOf(LocalDateTime.of(2019, 4, 4, 15, 15, 10)));

        return candleChartItemDto;
    }

    private WalletsAndCommissionsForOrderCreationDto getMockWalletsAndCommissionsForOrderCreationDto() {
        WalletsAndCommissionsForOrderCreationDto dto = new WalletsAndCommissionsForOrderCreationDto();
        dto.setUserId(100);
        dto.setSpendWalletActiveBalance(BigDecimal.ONE);
        dto.setCommissionId(200);
        dto.setCommissionValue(BigDecimal.ZERO);

        return dto;
    }

    private CurrencyPairLimitDto getMockCurrencyPairLimitDto() {
        CurrencyPairLimitDto currencyPairLimit = new CurrencyPairLimitDto();
        currencyPairLimit.setMaxAmount(BigDecimal.TEN);
        currencyPairLimit.setMinAmount(BigDecimal.TEN);
        currencyPairLimit.setMinRate(BigDecimal.TEN);
        currencyPairLimit.setMaxRate(BigDecimal.TEN);

        return currencyPairLimit;
    }

    private CurrencyPair getMockCurrencyPair(CurrencyPairType pairType) {
        CurrencyPair currencyPair = new CurrencyPair();
        currencyPair.setId(100);
        currencyPair.setPairType(pairType);
        return currencyPair;
    }

    private OrderCreateDto getMockOrderCreateDto(BigDecimal exchangeRate) {
        OrderCreateDto orderCreateDto = new OrderCreateDto();
        orderCreateDto.setAmount(BigDecimal.ZERO);
        orderCreateDto.setOperationType(OperationType.BUY);
        orderCreateDto.setCurrencyPair(getMockCurrencyPair(CurrencyPairType.ALL));
        orderCreateDto.setExchangeRate(exchangeRate);
        orderCreateDto.setSpentWalletBalance(BigDecimal.ZERO);
        orderCreateDto.setSpentAmount(BigDecimal.ZERO);
        orderCreateDto.setSpentWalletBalance(BigDecimal.ZERO);

        return orderCreateDto;
    }

    private OrderCreateDto getMockOrderCreateDto(BigDecimal amount, CurrencyPairType pairType, BigDecimal exchangeRate, BigDecimal stop) {
        OrderCreateDto orderCreateDto = getMockOrderCreateDto(amount, pairType, exchangeRate);
        orderCreateDto.setStop(stop);

        return orderCreateDto;
    }

    private OrderCreateDto getMockOrderCreateDto(BigDecimal amount, CurrencyPairType pairType, BigDecimal exchangeRate) {
        OrderCreateDto orderCreateDto = new OrderCreateDto();
        orderCreateDto.setAmount(amount);
        orderCreateDto.setOperationType(OperationType.BUY);
        orderCreateDto.setCurrencyPair(getMockCurrencyPair(pairType));
        orderCreateDto.setExchangeRate(exchangeRate);
        orderCreateDto.setSpentWalletBalance(BigDecimal.ZERO);
        orderCreateDto.setSpentAmount(BigDecimal.ZERO);
        orderCreateDto.setSpentWalletBalance(BigDecimal.ZERO);
        orderCreateDto.setOrderBaseType(OrderBaseType.STOP_LIMIT);

        return orderCreateDto;
    }

    private ExOrderStatisticsShortByPairsDto getMockExOrderStatisticsShortByPairsDto(CurrencyPairType pairType) {
        ExOrderStatisticsShortByPairsDto dto = ExOrderStatisticsShortByPairsDto.builder().build();
        dto.setNeedRefresh(Boolean.FALSE);
        dto.setPage(0);
        dto.setCurrencyPairId(1);
        dto.setCurrencyPairName("BTC/USD");
        dto.setCurrencyPairPrecision(2);
        dto.setLastOrderRate("0");
        dto.setPredLastOrderRate("0");
        dto.setPercentChange("0");
        dto.setMarket("FIAT");
        dto.setPriceInUSD("0");
        dto.setType(pairType);
        dto.setVolume("0.000000000");
        dto.setCurrencyVolume("0.000000000");
        dto.setHigh24hr("0.000000000");
        dto.setLow24hr("0.000000000");
        dto.setLastOrderRate24hr("0.000000000");
        dto.setHidden(Boolean.FALSE);
        dto.setLastUpdateCache("2019-04-03 14:52:14");
        dto.setTopMarket(true);

        return dto;
    }

    private List<OrderListDto> getMockOrderListDto() {
        OrderListDto item1 = new OrderListDto();
        item1.setId(47074186);
        item1.setUserId(25);
        item1.setOrderType(OperationType.SELL);
        item1.setExrate("4150");
        item1.setAmountBase("0.055629498");
        item1.setAmountConvert("230.8624167");
        item1.setOrdersIds("3");
        item1.setCreated(LocalDateTime.of(2019, 4, 1, 12, 37, 30));
        item1.setAccepted(null);
        item1.setOrderSourceId(11);

        OrderListDto item2 = new OrderListDto();
        item2.setId(47069689);
        item2.setUserId(15);
        item2.setOrderType(OperationType.SELL);
        item2.setExrate("4185");
        item2.setAmountBase("0.001958208");
        item2.setAmountConvert("8.19510048");
        item2.setOrdersIds("3");
        item2.setCreated(LocalDateTime.of(2019, 4, 1, 11, 47, 30));
        item2.setAccepted(null);
        item2.setOrderSourceId(13);

        return Arrays.asList(item1, item2);
    }

    private List<UserRole> getUserRoles() {
        return Arrays.asList(
                UserRole.ADMINISTRATOR,
                UserRole.ACCOUNTANT,
                UserRole.ADMIN_USER,
                UserRole.USER,
                UserRole.ROLE_CHANGE_PASSWORD,
                UserRole.EXCHANGE,
                UserRole.VIP_USER,
                UserRole.TRADER,
                UserRole.FIN_OPERATOR,
                UserRole.BOT_TRADER,
                UserRole.ICO_MARKET_MAKER,
                UserRole.OUTER_MARKET_BOT);
    }

    private InputCreateOrderDto getTestInputCreateOrderDto() {
        InputCreateOrderDto dto = new InputCreateOrderDto();
        dto.setBaseType(OrderBaseType.MARKET.name());
        dto.setAmount(BigDecimal.TEN);
        dto.setOrderType(OrderType.BUY.name());
        dto.setCurrencyPairId(1);
        return dto;
    }

    private OrderCreateDto getTestOrderCreatedDto() {
        OrderCreateDto orderCreateDto = getMockOrderCreateDto(BigDecimal.TEN);
        orderCreateDto.setAmount(BigDecimal.TEN);
        orderCreateDto.setOrderBaseType(OrderBaseType.MARKET);
        orderCreateDto.setCurrencyPair(new CurrencyPair("BTC/USD"));
        orderCreateDto.setOperationType(OperationType.BUY);
        orderCreateDto.setUserId(100);
        return orderCreateDto;
    }

    private List<ExOrder> getMarketOrdersCandidates() {
        List<ExOrder> candidates = new ArrayList<>();
        IntStream.range(0, 6).forEach(i -> candidates.add(getTestExOrder(i, BigDecimal.valueOf(3.0))));
        return candidates;
    }
}
