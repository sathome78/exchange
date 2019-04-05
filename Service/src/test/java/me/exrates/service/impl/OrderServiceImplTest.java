package me.exrates.service.impl;

import lombok.extern.slf4j.Slf4j;
import me.exrates.dao.OrderDao;
import me.exrates.model.Currency;
import me.exrates.model.CurrencyPair;
import me.exrates.model.User;
import me.exrates.model.chart.ChartResolution;
import me.exrates.model.chart.ChartTimeFrame;
import me.exrates.model.dto.CandleChartItemDto;
import me.exrates.model.dto.CurrencyPairLimitDto;
import me.exrates.model.dto.ExOrderStatisticsDto;
import me.exrates.model.dto.OrderBookWrapperDto;
import me.exrates.model.dto.OrderCreateDto;
import me.exrates.model.dto.OrderValidationDto;
import me.exrates.model.dto.SimpleOrderBookItem;
import me.exrates.model.dto.WalletsAndCommissionsForOrderCreationDto;
import me.exrates.model.dto.onlineTableDto.ExOrderStatisticsShortByPairsDto;
import me.exrates.model.dto.onlineTableDto.OrderListDto;
import me.exrates.model.dto.onlineTableDto.OrderWideListDto;
import me.exrates.model.enums.ChartResolutionTimeUnit;
import me.exrates.model.enums.CurrencyPairType;
import me.exrates.model.enums.IntervalType;
import me.exrates.model.enums.IntervalType2;
import me.exrates.model.enums.OperationType;
import me.exrates.model.enums.OrderBaseType;
import me.exrates.model.enums.OrderStatus;
import me.exrates.model.enums.OrderType;
import me.exrates.model.enums.PrecissionsEnum;
import me.exrates.model.enums.RefreshObjectsEnum;
import me.exrates.model.enums.UserRole;
import me.exrates.model.vo.BackDealInterval;
import me.exrates.service.CurrencyService;
import me.exrates.service.OrderService;
import me.exrates.service.UserService;
import me.exrates.service.cache.ChartsCacheManager;
import me.exrates.service.cache.ExchangeRatesHolder;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyBoolean;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyMapOf;
import static org.mockito.Matchers.anySetOf;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@Slf4j
@RunWith(MockitoJUnitRunner.class)
public class OrderServiceImplTest {

    @Mock
    private OrderDao orderDao;
    @Mock
    private ExchangeRatesHolder exchangeRatesHolder;
    @Mock
    private ChartsCacheManager chartsCacheManager;
    @Mock
    private CurrencyService currencyService;
    @Mock
    private UserService userService;

    @InjectMocks
    private OrderService orderService = new OrderServiceImpl();

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
        assertEquals(expected.get(0).getIntervalValue(), intervals.get(0).getIntervalValue());
        assertEquals(expected.get(0).getIntervalType(), intervals.get(0).getIntervalType());
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
    public void getDataForCandleChart_has_arguments_CurrencyPair_BackDealInterval() {
        when(orderDao.getDataForCandleChart(any(CurrencyPair.class), any(BackDealInterval.class)))
                .thenReturn(Collections.singletonList(getMockCandleChartItemDto()));

        List<CandleChartItemDto> dataForCandleChart = orderService
                .getDataForCandleChart(new CurrencyPair("BTC/USD"), getMockBackDealInterval());

        assertNotNull(dataForCandleChart);
        assertEquals(1, dataForCandleChart.size());
        assertEquals(LocalDateTime.of(2019, 4, 4, 15, 9, 10), dataForCandleChart.get(0).getBeginPeriod());
        assertEquals(LocalDateTime.of(2019, 4, 4, 15, 15, 10), dataForCandleChart.get(0).getEndPeriod());
        assertEquals(BigDecimal.TEN, dataForCandleChart.get(0).getOpenRate());
        assertEquals(BigDecimal.TEN, dataForCandleChart.get(0).getCloseRate());
        assertEquals(BigDecimal.TEN, dataForCandleChart.get(0).getLowRate());
        assertEquals(BigDecimal.TEN, dataForCandleChart.get(0).getHighRate());
        assertEquals(BigDecimal.TEN, dataForCandleChart.get(0).getBaseVolume());
        assertEquals(Timestamp.valueOf(LocalDateTime.of(2019, 4, 4, 15, 9, 10)), dataForCandleChart.get(0).getBeginDate());
        assertEquals(Timestamp.valueOf(LocalDateTime.of(2019, 4, 4, 15, 15, 10)), dataForCandleChart.get(0).getEndDate());

        verify(orderDao, times(1))
                .getDataForCandleChart(any(CurrencyPair.class), any(BackDealInterval.class));
    }

    @Test
    public void getCachedDataForCandle() {
        ChartTimeFrame chartTimeFrame = new ChartTimeFrame(new ChartResolution(30, ChartResolutionTimeUnit.MINUTE), 5, IntervalType2.DAY);

        when(chartsCacheManager.getData(anyInt(), any(ChartTimeFrame.class)))
                .thenReturn(Collections.singletonList(getMockCandleChartItemDto()));

        List<CandleChartItemDto> dataForCandleChart = orderService
                .getCachedDataForCandle(new CurrencyPair("BTC/USD"), chartTimeFrame);

        assertNotNull(dataForCandleChart);
        assertEquals(1, dataForCandleChart.size());
        assertEquals(LocalDateTime.of(2019, 4, 4, 15, 9, 10), dataForCandleChart.get(0).getBeginPeriod());
        assertEquals(LocalDateTime.of(2019, 4, 4, 15, 15, 10), dataForCandleChart.get(0).getEndPeriod());
        assertEquals(BigDecimal.TEN, dataForCandleChart.get(0).getOpenRate());
        assertEquals(BigDecimal.TEN, dataForCandleChart.get(0).getCloseRate());
        assertEquals(BigDecimal.TEN, dataForCandleChart.get(0).getLowRate());
        assertEquals(BigDecimal.TEN, dataForCandleChart.get(0).getHighRate());
        assertEquals(BigDecimal.TEN, dataForCandleChart.get(0).getBaseVolume());
        assertEquals(Timestamp.valueOf(LocalDateTime.of(2019, 4, 4, 15, 9, 10)), dataForCandleChart.get(0).getBeginDate());
        assertEquals(Timestamp.valueOf(LocalDateTime.of(2019, 4, 4, 15, 15, 10)), dataForCandleChart.get(0).getEndDate());

        verify(chartsCacheManager, times(1)).getData(anyInt(), any(ChartTimeFrame.class));
    }

    @Test
    public void getLastDataForCandleChart() {
        when(currencyService.findCurrencyPairById(anyInt())).thenReturn(new CurrencyPair("BTC/USD"));
        when(orderDao.getDataForCandleChart(
                any(CurrencyPair.class),
                any(LocalDateTime.class),
                any(LocalDateTime.class),
                anyInt(),
                anyString())
        ).thenReturn(Collections.singletonList(getMockCandleChartItemDto()));

        List<CandleChartItemDto> lastDataForCandleChart = orderService
                .getLastDataForCandleChart(1, LocalDateTime.now(), new ChartResolution(30, ChartResolutionTimeUnit.MINUTE));

        verify(currencyService, atLeastOnce()).findCurrencyPairById(anyInt());
        verify(orderDao, times(1)).getDataForCandleChart(
                any(CurrencyPair.class),
                any(LocalDateTime.class),
                any(LocalDateTime.class),
                anyInt(),
                anyString());
    }

    @Test
    public void getDataForCandleChart_has_arguments_int_and_ChartTimeFrame() {
        when(currencyService.findCurrencyPairById(anyInt())).thenReturn(new CurrencyPair("BTC/USD"));
        when(orderDao.getDataForCandleChart(
                any(CurrencyPair.class),
                any(LocalDateTime.class),
                any(LocalDateTime.class),
                anyInt(),
                anyString())
        ).thenReturn(Collections.singletonList(getMockCandleChartItemDto()));

        List<CandleChartItemDto> dataForCandleChart = orderService.getDataForCandleChart(
                1,
                new ChartTimeFrame(new ChartResolution(30, ChartResolutionTimeUnit.MINUTE), 5, IntervalType2.DAY)
        );

        assertNotNull(dataForCandleChart);
        assertEquals(1, dataForCandleChart.size());
        assertEquals(LocalDateTime.of(2019, 4, 4, 15, 9, 10), dataForCandleChart.get(0).getBeginPeriod());
        assertEquals(LocalDateTime.of(2019, 4, 4, 15, 15, 10), dataForCandleChart.get(0).getEndPeriod());
        assertEquals(BigDecimal.TEN, dataForCandleChart.get(0).getOpenRate());
        assertEquals(BigDecimal.TEN, dataForCandleChart.get(0).getCloseRate());
        assertEquals(BigDecimal.TEN, dataForCandleChart.get(0).getLowRate());
        assertEquals(BigDecimal.TEN, dataForCandleChart.get(0).getHighRate());
        assertEquals(BigDecimal.TEN, dataForCandleChart.get(0).getBaseVolume());
        assertEquals(Timestamp.valueOf(LocalDateTime.of(2019, 4, 4, 15, 9, 10)), dataForCandleChart.get(0).getBeginDate());
        assertEquals(Timestamp.valueOf(LocalDateTime.of(2019, 4, 4, 15, 15, 10)), dataForCandleChart.get(0).getEndDate());

        verify(currencyService, times(1)).findCurrencyPairById(anyInt());
        verify(orderDao, times(1)).getDataForCandleChart(
                any(CurrencyPair.class),
                any(LocalDateTime.class),
                any(LocalDateTime.class),
                anyInt(),
                anyString());
    }

    @Test
    public void getDataForCandleChart_has_arguments_CurrencyPair_BackDealInterval_LocalDateTime() {
        when(orderDao.getDataForCandleChart(
                any(CurrencyPair.class),
                any(BackDealInterval.class),
                any(LocalDateTime.class))
        ).thenReturn(Collections.singletonList(getMockCandleChartItemDto()));

        List<CandleChartItemDto> dataForCandleChart = orderService
                .getDataForCandleChart(new CurrencyPair("BTC/USD"), getMockBackDealInterval(), LocalDateTime.now());

        assertNotNull(dataForCandleChart);
        assertEquals(1, dataForCandleChart.size());
        assertEquals(LocalDateTime.of(2019, 4, 4, 15, 9, 10), dataForCandleChart.get(0).getBeginPeriod());
        assertEquals(LocalDateTime.of(2019, 4, 4, 15, 15, 10), dataForCandleChart.get(0).getEndPeriod());
        assertEquals(BigDecimal.TEN, dataForCandleChart.get(0).getOpenRate());
        assertEquals(BigDecimal.TEN, dataForCandleChart.get(0).getCloseRate());
        assertEquals(BigDecimal.TEN, dataForCandleChart.get(0).getLowRate());
        assertEquals(BigDecimal.TEN, dataForCandleChart.get(0).getHighRate());
        assertEquals(BigDecimal.TEN, dataForCandleChart.get(0).getBaseVolume());
        assertEquals(Timestamp.valueOf(LocalDateTime.of(2019, 4, 4, 15, 9, 10)), dataForCandleChart.get(0).getBeginDate());
        assertEquals(Timestamp.valueOf(LocalDateTime.of(2019, 4, 4, 15, 15, 10)), dataForCandleChart.get(0).getEndDate());

        verify(orderDao, times(1)).getDataForCandleChart(
                any(CurrencyPair.class),
                any(BackDealInterval.class),
                any(LocalDateTime.class));
    }

    @Test
    public void getOrdersStatisticByPairsEx_ICO_CURRENCIES_STATISTIC() {
        when(exchangeRatesHolder.getAllRates()).thenReturn(Collections.singletonList(getMockExOrderStatisticsShortByPairsDto(CurrencyPairType.ICO)));

        List<ExOrderStatisticsShortByPairsDto> ordersStatisticByPairsEx = orderService.getOrdersStatisticByPairsEx(RefreshObjectsEnum.ICO_CURRENCIES_STATISTIC);

        assertNotNull(ordersStatisticByPairsEx);
        assertEquals(1, ordersStatisticByPairsEx.size());
        assertEquals(new Integer(1), ordersStatisticByPairsEx.get(0).getCurrencyPairId());
        assertEquals("BTC/USD", ordersStatisticByPairsEx.get(0).getCurrencyPairName());
        assertEquals(new Integer(2), ordersStatisticByPairsEx.get(0).getCurrencyPairPrecision());
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

        verify(exchangeRatesHolder, times(1)).getAllRates();
    }

    @Test
    public void getOrdersStatisticByPairsEx_MAIN_CURRENCIES_STATISTIC() {
        when(exchangeRatesHolder.getAllRates()).thenReturn(Collections.singletonList(getMockExOrderStatisticsShortByPairsDto(CurrencyPairType.MAIN)));

        List<ExOrderStatisticsShortByPairsDto> ordersStatisticByPairsEx = orderService.getOrdersStatisticByPairsEx(RefreshObjectsEnum.MAIN_CURRENCIES_STATISTIC);

        assertNotNull(ordersStatisticByPairsEx);
        assertEquals(1, ordersStatisticByPairsEx.size());
        assertEquals(new Integer(1), ordersStatisticByPairsEx.get(0).getCurrencyPairId());
        assertEquals("BTC/USD", ordersStatisticByPairsEx.get(0).getCurrencyPairName());
        assertEquals(new Integer(2), ordersStatisticByPairsEx.get(0).getCurrencyPairPrecision());
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

        verify(exchangeRatesHolder, times(1)).getAllRates();
    }

    @Test
    public void getOrdersStatisticByPairsEx_default() {
        when(exchangeRatesHolder.getAllRates()).thenReturn(Collections.singletonList(getMockExOrderStatisticsShortByPairsDto(CurrencyPairType.ALL)));

        List<ExOrderStatisticsShortByPairsDto> ordersStatisticByPairsEx = orderService.getOrdersStatisticByPairsEx(RefreshObjectsEnum.ALL_TRADES);

        assertNotNull(ordersStatisticByPairsEx);
        assertEquals(1, ordersStatisticByPairsEx.size());
        assertEquals(new Integer(1), ordersStatisticByPairsEx.get(0).getCurrencyPairId());
        assertEquals("BTC/USD", ordersStatisticByPairsEx.get(0).getCurrencyPairName());
        assertEquals(new Integer(2), ordersStatisticByPairsEx.get(0).getCurrencyPairPrecision());
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

        verify(exchangeRatesHolder, times(1)).getAllRates();
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
        assertEquals(new Integer(1), statForSomeCurrencies.get(0).getCurrencyPairId());
        assertEquals("BTC/USD", statForSomeCurrencies.get(0).getCurrencyPairName());
        assertEquals(new Integer(2), statForSomeCurrencies.get(0).getCurrencyPairPrecision());
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
    public void getStatForSomeCurrencies_exception() {
        when(exchangeRatesHolder.getCurrenciesRates(anySetOf(Integer.class))).thenReturn(null);

        try {
            orderService.getStatForSomeCurrencies(new HashSet<Integer>() {{
                add(1);
                add(2);
                add(3);
            }});
        } catch (Exception ex) {
        }
        verify(exchangeRatesHolder, times(1)).getCurrenciesRates(anySetOf(Integer.class));
    }

    @Test
    public void prepareNewOrder_operation_type_sell_order_base_type_ico_authentication_equals_null() {
        when(userService.getUserRoleFromDB(anyString())).thenReturn(UserRole.USER);
        when(orderDao.getWalletAndCommission(anyString(), any(Currency.class), any(OperationType.class), any(UserRole.class)))
                .thenReturn(getMockWalletsAndCommissionsForOrderCreationDto());

        OrderCreateDto orderCreateDto = orderService.prepareNewOrder(
                new CurrencyPair("BTC/USD"),
                OperationType.SELL,
                "test@test.com",
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

        verify(userService, times(1)).getUserRoleFromDB(anyString());
        verify(orderDao, times(1)).getWalletAndCommission(anyString(), any(Currency.class), any(OperationType.class), any(UserRole.class));
    }

    @Test
    public void prepareNewOrder_operation_type_sell_order_base_type_ico_authentication_not_null() {
        Authentication authentication = mock(Authentication.class);
        SecurityContext securityContext = mock(SecurityContext.class);
        SecurityContextHolder.setContext(securityContext);

        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(SecurityContextHolder.getContext().getAuthentication()).thenReturn(authentication);
        when(userService.getUserRoleFromSecurityContext()).thenReturn(UserRole.USER);
        when(orderDao.getWalletAndCommission(anyString(), any(Currency.class), any(OperationType.class), any(UserRole.class)))
                .thenReturn(getMockWalletsAndCommissionsForOrderCreationDto());

        OrderCreateDto orderCreateDto = orderService.prepareNewOrder(
                new CurrencyPair("BTC/USD"),
                OperationType.SELL,
                "test@test.com",
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

        verify(securityContext, times(1)).getAuthentication();
        verify(userService, times(1)).getUserRoleFromSecurityContext();
        verify(orderDao, times(1)).getWalletAndCommission(anyString(), any(Currency.class), any(OperationType.class), any(UserRole.class));

        reset(securityContext);
    }

    @Test
    public void prepareNewOrder_operation_type_buy_order_base_type_ico_authentication_equals_null() {
        when(userService.getUserRoleFromDB(anyString())).thenReturn(UserRole.USER);
        when(orderDao.getWalletAndCommission(anyString(), any(Currency.class), any(OperationType.class), any(UserRole.class)))
                .thenReturn(getMockWalletsAndCommissionsForOrderCreationDto());

        OrderCreateDto orderCreateDto = orderService.prepareNewOrder(
                new CurrencyPair("BTC/USD"),
                OperationType.BUY,
                "test@test.com",
                BigDecimal.ONE,
                BigDecimal.ONE,
                OrderBaseType.ICO
        );

        assertNotNull(orderCreateDto);
        assertEquals(getMockWalletsAndCommissionsForOrderCreationDto().getUserId(), orderCreateDto.getUserId());
        assertEquals("BTC/USD", orderCreateDto.getCurrencyPair().getName());
        assertEquals(OperationType.BUY, orderCreateDto.getOperationType());
        assertEquals(OrderBaseType.ICO, orderCreateDto.getOrderBaseType());
        assertEquals(BigDecimal.ONE, orderCreateDto.getAmount());
        assertEquals(BigDecimal.ONE, orderCreateDto.getExchangeRate());
        assertEquals(getMockWalletsAndCommissionsForOrderCreationDto().getCommissionValue(), orderCreateDto.getComission());
        assertEquals(24, orderCreateDto.getComissionId());

        verify(userService, times(1)).getUserRoleFromDB(anyString());
        verify(orderDao, times(1)).getWalletAndCommission(anyString(), any(Currency.class), any(OperationType.class), any(UserRole.class));
    }

    @Test
    public void prepareNewOrder_operation_type_buy_order_base_type_ico_authentication_not_null() {
        Authentication authentication = mock(Authentication.class);
        SecurityContext securityContext = mock(SecurityContext.class);
        SecurityContextHolder.setContext(securityContext);

        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(SecurityContextHolder.getContext().getAuthentication()).thenReturn(authentication);
        when(userService.getUserRoleFromSecurityContext()).thenReturn(UserRole.USER);
        when(orderDao.getWalletAndCommission(anyString(), any(Currency.class), any(OperationType.class), any(UserRole.class)))
                .thenReturn(getMockWalletsAndCommissionsForOrderCreationDto());

        OrderCreateDto orderCreateDto = orderService.prepareNewOrder(
                new CurrencyPair("BTC/USD"),
                OperationType.BUY,
                "test@test.com",
                BigDecimal.ONE,
                BigDecimal.ONE,
                OrderBaseType.ICO
        );

        assertNotNull(orderCreateDto);
        assertEquals(getMockWalletsAndCommissionsForOrderCreationDto().getUserId(), orderCreateDto.getUserId());
        assertEquals("BTC/USD", orderCreateDto.getCurrencyPair().getName());
        assertEquals(OperationType.BUY, orderCreateDto.getOperationType());
        assertEquals(OrderBaseType.ICO, orderCreateDto.getOrderBaseType());
        assertEquals(BigDecimal.ONE, orderCreateDto.getAmount());
        assertEquals(BigDecimal.ONE, orderCreateDto.getExchangeRate());
        assertEquals(getMockWalletsAndCommissionsForOrderCreationDto().getCommissionValue(), orderCreateDto.getComission());
        assertEquals(24, orderCreateDto.getComissionId());

        verify(securityContext, times(1)).getAuthentication();
        verify(userService, times(1)).getUserRoleFromSecurityContext();
        verify(orderDao, times(1)).getWalletAndCommission(anyString(), any(Currency.class), any(OperationType.class), any(UserRole.class));

        reset(securityContext);
    }

    @Test
    public void prepareNewOrder_operation_type_buy_order_base_type_any_authentication_equals_null() {
        when(userService.getUserRoleFromDB(anyString())).thenReturn(UserRole.USER);
        when(orderDao.getWalletAndCommission(anyString(), any(Currency.class), any(OperationType.class), any(UserRole.class)))
                .thenReturn(getMockWalletsAndCommissionsForOrderCreationDto());

        OrderCreateDto orderCreateDto = orderService.prepareNewOrder(
                new CurrencyPair("BTC/USD"),
                OperationType.BUY,
                "test@test.com",
                BigDecimal.ONE,
                BigDecimal.ONE,
                OrderBaseType.LIMIT
        );

        assertNotNull(orderCreateDto);
        assertEquals(getMockWalletsAndCommissionsForOrderCreationDto().getUserId(), orderCreateDto.getUserId());
        assertEquals("BTC/USD", orderCreateDto.getCurrencyPair().getName());
        assertEquals(OperationType.BUY, orderCreateDto.getOperationType());
        assertEquals(OrderBaseType.LIMIT, orderCreateDto.getOrderBaseType());
        assertEquals(BigDecimal.ONE, orderCreateDto.getAmount());
        assertEquals(BigDecimal.ONE, orderCreateDto.getExchangeRate());
        assertEquals(getMockWalletsAndCommissionsForOrderCreationDto().getCommissionValue(), orderCreateDto.getComission());
        assertEquals(getMockWalletsAndCommissionsForOrderCreationDto().getCommissionId(), orderCreateDto.getComissionId());

        verify(userService, times(1)).getUserRoleFromDB(anyString());
        verify(orderDao, times(1)).getWalletAndCommission(
                anyString(),
                any(Currency.class),
                any(OperationType.class),
                any(UserRole.class)
        );
    }

    @Test
    public void validateOrder_fromDemo_false() {
        when(currencyService.findLimitForRoleByCurrencyPairAndType(anyInt(), any(OperationType.class)))
                .thenReturn(getMockCurrencyPairLimitDto());

        OrderValidationDto orderValidationDto = orderService.validateOrder(getMockOrderCreateDto(BigDecimal.TEN), Boolean.FALSE, new User());

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
        assertEquals(new String[]{"10", "10"}, orderValidationDto.getErrorParams().get("amount_2"));
        assertTrue(orderValidationDto.getErrorParams().containsKey("amount_1"));
        assertEquals(new String[]{"10"}, orderValidationDto.getErrorParams().get("amount_1"));

        verify(currencyService, times(1)).findLimitForRoleByCurrencyPairAndType(anyInt(), any(OperationType.class));
    }

    @Test
    public void validateOrder_fromDemo_true() {
        when(currencyService.findLimitForRoleByCurrencyPairAndTypeAndUser(
                anyInt(),
                any(OperationType.class),
                any(User.class))).thenReturn(getMockCurrencyPairLimitDto());

        OrderValidationDto orderValidationDto = orderService.validateOrder(getMockOrderCreateDto(BigDecimal.ZERO), Boolean.TRUE, new User());

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
        assertEquals(new String[]{"10"}, orderValidationDto.getErrorParams().get("exrate_5"));
        assertTrue(orderValidationDto.getErrorParams().containsKey("amount_3"));
        assertEquals(new String[]{"10", "10"}, orderValidationDto.getErrorParams().get("amount_3"));
        assertTrue(orderValidationDto.getErrorParams().containsKey("amount_2"));
        assertEquals(new String[]{"10"}, orderValidationDto.getErrorParams().get("amount_2"));

        verify(currencyService, times(1))
                .findLimitForRoleByCurrencyPairAndTypeAndUser(anyInt(), any(OperationType.class), any(User.class));
    }

    @Test
    public void validateOrder_fromDemo_true_OrderBaseType_STOP_LIMIT() {
        OrderCreateDto orderCreateDto = getMockOrderCreateDto(BigDecimal.ZERO, CurrencyPairType.ALL, BigDecimal.ZERO);

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
        assertEquals(3, orderValidationDto.getErrorParams().size());
        assertTrue(orderValidationDto.getErrorParams().containsKey("exrate_6"));
        assertEquals(new String[]{"10"}, orderValidationDto.getErrorParams().get("exrate_6"));
        assertTrue(orderValidationDto.getErrorParams().containsKey("amount_4"));
        assertEquals(new String[]{"10", "10"}, orderValidationDto.getErrorParams().get("amount_4"));
        assertTrue(orderValidationDto.getErrorParams().containsKey("amount_3"));
        assertEquals(new String[]{"10"}, orderValidationDto.getErrorParams().get("amount_3"));

        verify(currencyService, times(1))
                .findLimitForRoleByCurrencyPairAndTypeAndUser(anyInt(), any(OperationType.class), any(User.class));
    }

    @Test
    public void validateOrder_fromDemo_true_OrderBaseType_STOP_LIMIT_min_rate() {
        OrderCreateDto orderCreateDto = getMockOrderCreateDto(
                BigDecimal.ZERO,
                CurrencyPairType.ALL,
                BigDecimal.ZERO,
                BigDecimal.ONE
        );

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
        assertEquals(new BigDecimal[]{BigDecimal.valueOf(10)}, orderValidationDto.getErrorParams().get("stop_2"));
        assertTrue(orderValidationDto.getErrorParams().containsKey("exrate_6"));
        assertEquals(new String[]{"10"}, orderValidationDto.getErrorParams().get("exrate_6"));
        assertTrue(orderValidationDto.getErrorParams().containsKey("amount_4"));
        assertEquals(new String[]{"10", "10"}, orderValidationDto.getErrorParams().get("amount_4"));
        assertTrue(orderValidationDto.getErrorParams().containsKey("amount_3"));
        assertEquals(new String[]{"10"}, orderValidationDto.getErrorParams().get("amount_3"));

        verify(currencyService, times(1))
                .findLimitForRoleByCurrencyPairAndTypeAndUser(anyInt(), any(OperationType.class), any(User.class));
    }

    @Test
    public void validateOrder_fromDemo_true_OrderBaseType_STOP_LIMIT_max_rate() {
        OrderCreateDto orderCreateDto = getMockOrderCreateDto(
                BigDecimal.ZERO,
                CurrencyPairType.ALL,
                BigDecimal.ZERO,
                BigDecimal.valueOf(11)
        );

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
        assertEquals(new BigDecimal[]{BigDecimal.valueOf(10)}, orderValidationDto.getErrorParams().get("stop_2"));
        assertTrue(orderValidationDto.getErrorParams().containsKey("exrate_6"));
        assertEquals(new String[]{"10"}, orderValidationDto.getErrorParams().get("exrate_6"));
        assertTrue(orderValidationDto.getErrorParams().containsKey("amount_4"));
        assertEquals(new String[]{"10", "10"}, orderValidationDto.getErrorParams().get("amount_4"));
        assertTrue(orderValidationDto.getErrorParams().containsKey("amount_3"));
        assertEquals(new String[]{"10"}, orderValidationDto.getErrorParams().get("amount_3"));

        verify(currencyService, times(1)).findLimitForRoleByCurrencyPairAndTypeAndUser(
                anyInt(),
                any(OperationType.class),
                any(User.class)
        );
    }

    @Test
    public void validateOrder_fromDemo_true_OrderBaseType_STOP_LIMIT_max_rate_CurrencyPairType_ICO() {
        OrderCreateDto orderCreateDto = getMockOrderCreateDto(
                BigDecimal.ZERO,
                CurrencyPairType.ICO,
                BigDecimal.ZERO,
                BigDecimal.valueOf(11)
        );

        when(currencyService.findLimitForRoleByCurrencyPairAndTypeAndUser(
                anyInt(),
                any(OperationType.class),
                any(User.class))).thenReturn(getMockCurrencyPairLimitDto());

        try {
            orderService.validateOrder(orderCreateDto, Boolean.TRUE, new User());
        } catch (RuntimeException e) {
            assertEquals("unsupported type of order", e.getMessage());
        }

        verify(currencyService, times(1)).findLimitForRoleByCurrencyPairAndTypeAndUser(
                anyInt(),
                any(OperationType.class),
                any(User.class)
        );
    }

    @Test
    public void validateOrder_fromDemo_true_OrderBaseType_STOP_LIMIT_max_amount() {
        OrderCreateDto orderCreateDto = getMockOrderCreateDto(
                BigDecimal.valueOf(11),
                CurrencyPairType.ALL,
                BigDecimal.ZERO,
                BigDecimal.valueOf(11)
        );

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
        assertEquals(new BigDecimal[]{BigDecimal.valueOf(10)}, orderValidationDto.getErrorParams().get("stop_1"));
        assertTrue(orderValidationDto.getErrorParams().containsKey("exrate_5"));
        assertEquals(new String[]{"10"}, orderValidationDto.getErrorParams().get("exrate_5"));
        assertTrue(orderValidationDto.getErrorParams().containsKey("amount_3"));
        assertEquals(new String[]{"10", "10"}, orderValidationDto.getErrorParams().get("amount_3"));
        assertTrue(orderValidationDto.getErrorParams().containsKey("amount_2"));
        assertEquals(new String[]{"10"}, orderValidationDto.getErrorParams().get("amount_2"));

        verify(currencyService, times(1)).findLimitForRoleByCurrencyPairAndTypeAndUser(
                anyInt(),
                any(OperationType.class),
                any(User.class)
        );
    }

    @Test
    public void validateOrder_fromDemo_true_ExchangeRate_not_null_max_rate() {
        OrderCreateDto orderCreateDto = getMockOrderCreateDto(
                BigDecimal.valueOf(11),
                CurrencyPairType.ALL,
                BigDecimal.valueOf(11),
                BigDecimal.valueOf(11)
        );

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
        assertEquals(new BigDecimal[]{BigDecimal.valueOf(10)}, orderValidationDto.getErrorParams().get("stop_0"));
        assertTrue(orderValidationDto.getErrorParams().containsKey("amount_2"));
        assertEquals(new String[]{"10", "10"}, orderValidationDto.getErrorParams().get("amount_2"));
        assertTrue(orderValidationDto.getErrorParams().containsKey("amount_1"));
        assertEquals(new String[]{"10"}, orderValidationDto.getErrorParams().get("amount_1"));
        assertTrue(orderValidationDto.getErrorParams().containsKey("exrate_3"));
        assertEquals(new String[]{"10"}, orderValidationDto.getErrorParams().get("exrate_3"));

        verify(currencyService, times(1)).findLimitForRoleByCurrencyPairAndTypeAndUser(
                anyInt(),
                any(OperationType.class),
                any(User.class)
        );
    }

    @Test
    public void validateOrder_has_one_argument() {
        when(currencyService.findLimitForRoleByCurrencyPairAndType(anyInt(), any(OperationType.class)))
                .thenReturn(getMockCurrencyPairLimitDto());

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
        assertEquals(new String[]{"10"}, orderValidationDto.getErrorParams().get("exrate_5"));
        assertTrue(orderValidationDto.getErrorParams().containsKey("amount_3"));
        assertEquals(new String[]{"10", "10"}, orderValidationDto.getErrorParams().get("amount_3"));
        assertTrue(orderValidationDto.getErrorParams().containsKey("amount_2"));
        assertEquals(new String[]{"10"}, orderValidationDto.getErrorParams().get("amount_2"));

        verify(currencyService, times(1)).findLimitForRoleByCurrencyPairAndType(anyInt(), any(OperationType.class));
    }

    @Test
    public void validateOrder_has_one_argument_OrderBaseType_STOP_LIMIT() {
        OrderCreateDto orderCreateDto = getMockOrderCreateDto(BigDecimal.ZERO, CurrencyPairType.ALL, BigDecimal.ZERO);

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
        assertEquals(3, orderValidationDto.getErrorParams().size());
        assertTrue(orderValidationDto.getErrorParams().containsKey("exrate_6"));
        assertEquals(new String[]{"10"}, orderValidationDto.getErrorParams().get("exrate_6"));
        assertTrue(orderValidationDto.getErrorParams().containsKey("amount_4"));
        assertEquals(new String[]{"10", "10"}, orderValidationDto.getErrorParams().get("amount_4"));
        assertTrue(orderValidationDto.getErrorParams().containsKey("amount_3"));
        assertEquals(new String[]{"10"}, orderValidationDto.getErrorParams().get("amount_3"));

        verify(currencyService, times(1))
                .findLimitForRoleByCurrencyPairAndType(anyInt(), any(OperationType.class));
    }

    @Test
    public void validateOrder_has_one_argument_OrderBaseType_STOP_LIMIT_min_rate() {
        OrderCreateDto orderCreateDto = getMockOrderCreateDto(
                BigDecimal.ZERO,
                CurrencyPairType.ALL,
                BigDecimal.ZERO,
                BigDecimal.ONE
        );

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
        assertEquals(new BigDecimal[]{BigDecimal.valueOf(10)}, orderValidationDto.getErrorParams().get("stop_2"));
        assertTrue(orderValidationDto.getErrorParams().containsKey("exrate_6"));
        assertEquals(new String[]{"10"}, orderValidationDto.getErrorParams().get("exrate_6"));
        assertTrue(orderValidationDto.getErrorParams().containsKey("amount_4"));
        assertEquals(new String[]{"10", "10"}, orderValidationDto.getErrorParams().get("amount_4"));
        assertTrue(orderValidationDto.getErrorParams().containsKey("amount_3"));
        assertEquals(new String[]{"10"}, orderValidationDto.getErrorParams().get("amount_3"));

        verify(currencyService, times(1))
                .findLimitForRoleByCurrencyPairAndType(anyInt(), any(OperationType.class));
    }

    @Test
    public void validateOrder_has_one_argument_OrderBaseType_STOP_LIMIT_max_rate() {
        OrderCreateDto orderCreateDto = getMockOrderCreateDto(
                BigDecimal.ZERO,
                CurrencyPairType.ALL,
                BigDecimal.ZERO,
                BigDecimal.valueOf(11)
        );

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
        assertEquals(new BigDecimal[]{BigDecimal.valueOf(10)}, orderValidationDto.getErrorParams().get("stop_2"));
        assertTrue(orderValidationDto.getErrorParams().containsKey("exrate_6"));
        assertEquals(new String[]{"10"}, orderValidationDto.getErrorParams().get("exrate_6"));
        assertTrue(orderValidationDto.getErrorParams().containsKey("amount_4"));
        assertEquals(new String[]{"10", "10"}, orderValidationDto.getErrorParams().get("amount_4"));
        assertTrue(orderValidationDto.getErrorParams().containsKey("amount_3"));
        assertEquals(new String[]{"10"}, orderValidationDto.getErrorParams().get("amount_3"));

        verify(currencyService, times(1))
                .findLimitForRoleByCurrencyPairAndType(anyInt(), any(OperationType.class));
    }

    @Test
    public void validateOrder_has_one_argument_CurrencyPairType_ICO() {
        OrderCreateDto orderCreateDto = getMockOrderCreateDto(
                BigDecimal.ZERO,
                CurrencyPairType.ICO,
                BigDecimal.ZERO,
                BigDecimal.valueOf(11)
        );

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
                BigDecimal.valueOf(11)
        );

        when(currencyService.findLimitForRoleByCurrencyPairAndType(anyInt(), any(OperationType.class)))
                .thenReturn(getMockCurrencyPairLimitDto());

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
        assertEquals(new BigDecimal[]{BigDecimal.valueOf(10)}, orderValidationDto.getErrorParams().get("stop_0"));
        assertTrue(orderValidationDto.getErrorParams().containsKey("amount_2"));
        assertEquals(new String[]{"10", "10"}, orderValidationDto.getErrorParams().get("amount_2"));
        assertTrue(orderValidationDto.getErrorParams().containsKey("amount_1"));
        assertEquals(new String[]{"10"}, orderValidationDto.getErrorParams().get("amount_1"));
        assertTrue(orderValidationDto.getErrorParams().containsKey("exrate_3"));
        assertEquals(new String[]{"10"}, orderValidationDto.getErrorParams().get("exrate_3"));

        verify(currencyService, times(1))
                .findLimitForRoleByCurrencyPairAndType(anyInt(), any(OperationType.class));
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
                15,
                0,
                false,
                Collections.emptyMap(),
                now.minusDays(1),
                now
        );

        doReturn(Collections.singletonList(new OrderWideListDto())).when(orderDao).getMyOrdersWithState(
                1,
                new CurrencyPair("BTC/USD"),
                StringUtils.EMPTY,
                OrderStatus.CLOSED,
                StringUtils.EMPTY,
                15,
                0,
                false,
                Collections.emptyMap(),
                now.minusDays(1),
                now,
                Locale.ENGLISH
        );

        Pair<Integer, List<OrderWideListDto>> pair = orderService.getMyOrdersWithStateMap(
                1,
                new CurrencyPair("BTC/USD"),
                StringUtils.EMPTY,
                OrderStatus.CLOSED,
                StringUtils.EMPTY,
                15,
                0,
                false,
                Collections.emptyMap(),
                now.minusDays(1),
                now,
                Locale.ENGLISH);

        verify(orderDao, atLeastOnce()).getMyOrdersWithStateCount(anyInt(), any(CurrencyPair.class), anyString(),
                any(OrderStatus.class), anyString(), anyInt(), anyInt(), anyBoolean(), anyMapOf(String.class, String.class),
                any(LocalDateTime.class), any(LocalDateTime.class));

        verify(orderDao, atLeastOnce()).getMyOrdersWithState(anyInt(), any(CurrencyPair.class), anyString(),
                any(OrderStatus.class), anyString(), anyInt(), anyInt(), anyBoolean(), anyMapOf(String.class, String.class),
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
                15,
                0,
                false,
                Collections.emptyMap(),
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
                Collections.emptyMap(),
                now.minusDays(1),
                now,
                Locale.ENGLISH);

        verify(orderDao, atLeastOnce()).getMyOrdersWithStateCount(anyInt(), any(CurrencyPair.class), anyString(),
                any(OrderStatus.class), anyString(), anyInt(), anyInt(), anyBoolean(), anyMapOf(String.class, String.class),
                any(LocalDateTime.class), any(LocalDateTime.class));

        assertNotNull("Pair could not be null", pair);
        assertEquals("Number of records could be equals", 0, (int) pair.getLeft());
        assertTrue("List could be empty", pair.getRight().isEmpty());

        log.debug("getMyOrdersWithStateMap_notFoundRecordsTest() - end");
    }

    @Test
    public void findAllOrderBookItems_ExOrderStatisticsShortByPairsDto_not_null() {
        when(orderDao.findAllByOrderTypeAndCurrencyId(anyInt(), any(OrderType.class))).thenReturn(getMockOrderListDto());
        when(exchangeRatesHolder.getOne(anyInt())).thenReturn(getMockExOrderStatisticsShortByPairsDto(CurrencyPairType.MAIN));

        OrderBookWrapperDto orderBookWrapperDto = orderService.findAllOrderBookItems(OrderType.SELL, 1, 5);

        BigDecimal item1 = new BigDecimal(getMockOrderListDto().get(0).getAmountConvert());
        BigDecimal item2 = new BigDecimal(getMockOrderListDto().get(1).getAmountConvert());
        BigDecimal total = item1.add(item2);


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
        when(orderDao.findAllByOrderTypeAndCurrencyId(anyInt(), any(OrderType.class))).thenReturn(getMockOrderListDto());
        when(exchangeRatesHolder.getOne(anyInt())).thenReturn(null);

        OrderBookWrapperDto orderBookWrapperDto = orderService.findAllOrderBookItems(OrderType.SELL, 1, 5);

        BigDecimal item1 = new BigDecimal(getMockOrderListDto().get(0).getAmountConvert());
        BigDecimal item2 = new BigDecimal(getMockOrderListDto().get(1).getAmountConvert());
        BigDecimal total = item1.add(item2);

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

    //TODO: objectMapper.writeValueAsString(Collections.singletonList(dto)) throw NullPointerException
    @Ignore
    public void findAllOrderBookItemsForAllPrecissions() {
        List<PrecissionsEnum> precisionsList = Arrays.asList(
                PrecissionsEnum.ONE,
                PrecissionsEnum.TWO,
                PrecissionsEnum.THREE,
                PrecissionsEnum.FOUR,
                PrecissionsEnum.FIVE
        );

        when(orderDao.findAllByOrderTypeAndCurrencyId(anyInt(), any(OrderType.class))).thenReturn(getMockOrderListDto());
        when(exchangeRatesHolder.getOne(anyInt())).thenReturn(getMockExOrderStatisticsShortByPairsDto(CurrencyPairType.MAIN));

        Map<PrecissionsEnum, String> allOrderBookItemsForAllPrecisions = orderService
                .findAllOrderBookItemsForAllPrecissions(OrderType.SELL, 1, precisionsList);

        assertNotNull(allOrderBookItemsForAllPrecisions);
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

    private CurrencyPair getMockCurrencyPair(CurrencyPairType all) {
        CurrencyPair currencyPair = new CurrencyPair();
        currencyPair.setId(100);
        currencyPair.setPairType(all);

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

    private OrderBookWrapperDto getMockOrderBookWrapperDto_SELL() {
        OrderBookWrapperDto orderBookWrapperDto = OrderBookWrapperDto.builder().build();
        orderBookWrapperDto.setOrderType(OrderType.SELL);
        orderBookWrapperDto.setLastExrate("1");
        orderBookWrapperDto.setPreLastExrate("2");
        orderBookWrapperDto.setPositive(Boolean.FALSE);
        orderBookWrapperDto.setTotal(BigDecimal.valueOf(76477.473721603));
        orderBookWrapperDto.setOrderBookItems(getMockSimpleOrderBookItem_SELL());

        return orderBookWrapperDto;
    }

    private List<SimpleOrderBookItem> getMockSimpleOrderBookItem_SELL() {
        SimpleOrderBookItem item1 = SimpleOrderBookItem.builder().build();
        item1.setCurrencyPairId(1);
        item1.setOrderType(OrderType.SELL);
        item1.setCurrencyPairName(null);
        item1.setExrate(BigDecimal.valueOf(13));
        item1.setAmount(BigDecimal.valueOf(3904.636432431));
        item1.setTotal(BigDecimal.valueOf(50760.273621603));
        item1.setSumAmount(BigDecimal.valueOf(3904.636432431));

        SimpleOrderBookItem item2 = SimpleOrderBookItem.builder().build();
        item2.setCurrencyPairId(1);
        item2.setOrderType(OrderType.SELL);
        item2.setCurrencyPairName(null);
        item2.setExrate(BigDecimal.valueOf(1000));
        item2.setAmount(BigDecimal.valueOf(14.1001));
        item2.setTotal(BigDecimal.valueOf(64874.473721603));
        item2.setSumAmount(BigDecimal.valueOf(3918.736532431));

        SimpleOrderBookItem item3 = SimpleOrderBookItem.builder().build();
        item3.setCurrencyPairId(1);
        item3.setOrderType(OrderType.SELL);
        item3.setCurrencyPairName(null);
        item3.setExrate(BigDecimal.valueOf(3600));
        item3.setAmount(BigDecimal.valueOf(1));
        item3.setTotal(BigDecimal.valueOf(68475.473721603));
        item3.setSumAmount(BigDecimal.valueOf(3919.736532431));

        SimpleOrderBookItem item4 = SimpleOrderBookItem.builder().build();
        item4.setCurrencyPairId(1);
        item4.setOrderType(OrderType.SELL);
        item4.setCurrencyPairName(null);
        item4.setExrate(BigDecimal.valueOf(4000));
        item4.setAmount(BigDecimal.valueOf(2));
        item4.setTotal(BigDecimal.valueOf(76477.473721603));
        item4.setSumAmount(BigDecimal.valueOf(3921.736532431));

        return Arrays.asList(item1, item2, item3, item4);
    }

    private OrderBookWrapperDto getMockOrderBookWrapperDto_BUY() {
        OrderBookWrapperDto orderBookWrapperDto = OrderBookWrapperDto.builder().build();
        orderBookWrapperDto.setOrderType(OrderType.BUY);
        orderBookWrapperDto.setLastExrate("1");
        orderBookWrapperDto.setPreLastExrate("2");
        orderBookWrapperDto.setPositive(Boolean.FALSE);
        orderBookWrapperDto.setTotal(BigDecimal.valueOf(2));
        orderBookWrapperDto.setOrderBookItems(getMockSimpleOrderBookItem_BUY());

        return orderBookWrapperDto;
    }

    private List<SimpleOrderBookItem> getMockSimpleOrderBookItem_BUY() {
        SimpleOrderBookItem item1 = SimpleOrderBookItem.builder().build();
        item1.setCurrencyPairId(1);
        item1.setOrderType(OrderType.BUY);
        item1.setCurrencyPairName(null);
        item1.setExrate(BigDecimal.valueOf(2));
        item1.setAmount(BigDecimal.valueOf(1));
        item1.setTotal(BigDecimal.valueOf(2));
        item1.setSumAmount(BigDecimal.valueOf(1));

        return Collections.singletonList(item1);
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
}
