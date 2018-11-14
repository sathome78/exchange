package me.exrates.ngcontroller.service.impl;

import me.exrates.dao.OrderDao;
import me.exrates.dao.StopOrderDao;
import me.exrates.model.Currency;
import me.exrates.model.CurrencyPair;
import me.exrates.model.ExOrder;
import me.exrates.model.StockExchangeStats;
import me.exrates.model.StopOrder;
import me.exrates.model.User;
import me.exrates.model.dto.CandleDto;
import me.exrates.model.dto.ExOrderStatisticsDto;
import me.exrates.model.dto.OrderCreateDto;
import me.exrates.model.dto.OrderValidationDto;
import me.exrates.model.dto.WalletsAndCommissionsForOrderCreationDto;
import me.exrates.model.dto.onlineTableDto.ExOrderStatisticsShortByPairsDto;
import me.exrates.model.enums.ActionType;
import me.exrates.model.enums.ChartPeriodsEnum;
import me.exrates.model.enums.CurrencyPairType;
import me.exrates.model.enums.OperationType;
import me.exrates.model.enums.OrderActionEnum;
import me.exrates.model.enums.OrderBaseType;
import me.exrates.model.enums.OrderStatus;
import me.exrates.model.util.BigDecimalProcessing;
import me.exrates.ngcontroller.exception.NgDashboardException;
import me.exrates.ngcontroller.mobel.InputCreateOrderDto;
import me.exrates.ngcontroller.mobel.ResponseInfoCurrencyPairDto;
import me.exrates.ngcontroller.mobel.ResponseUserBalances;
import me.exrates.ngcontroller.service.NgOrderService;
import me.exrates.service.CurrencyService;
import me.exrates.service.DashboardService;
import me.exrates.service.OrderService;
import me.exrates.service.StockExchangeService;
import me.exrates.service.UserService;
import me.exrates.service.WalletService;
import me.exrates.service.stopOrder.StopOrderService;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.temporal.ChronoField;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class NgOrderServiceImpl implements NgOrderService {

    private static final Logger logger = LogManager.getLogger(NgOrderServiceImpl.class);

    private final UserService userService;
    private final CurrencyService currencyService;
    private final OrderService orderService;
    private final OrderDao orderDao;
    private final WalletService walletService;
    private final StopOrderDao stopOrderDao;
    private final StopOrderService stopOrderService;
    private final StockExchangeService stockExchangeService;
    private final DashboardService dashboardService;

    @Autowired
    public NgOrderServiceImpl(UserService userService,
                              CurrencyService currencyService,
                              OrderService orderService,
                              OrderDao orderDao,
                              WalletService walletService,
                              StopOrderDao stopOrderDao,
                              StopOrderService stopOrderService,
                              DashboardService dashboardService,
                              StockExchangeService stockExchangeService) {
        this.userService = userService;
        this.currencyService = currencyService;
        this.orderService = orderService;
        this.orderDao = orderDao;
        this.walletService = walletService;
        this.stopOrderDao = stopOrderDao;
        this.stopOrderService = stopOrderService;
        this.dashboardService = dashboardService;
        this.stockExchangeService = stockExchangeService;
    }

    @Override
    public OrderCreateDto prepareOrder(InputCreateOrderDto inputOrder) {
        OperationType operationType = OperationType.valueOf(inputOrder.getOrderType());

        if (operationType != OperationType.SELL && operationType != OperationType.BUY) {
            throw new NgDashboardException(String.format("OrderType %s not support here", operationType));
        }

        OrderBaseType baseType = OrderBaseType.convert(inputOrder.getBaseType());

        if (baseType == null) baseType = OrderBaseType.LIMIT;

        if (baseType == OrderBaseType.STOP_LIMIT && inputOrder.getStop() == null) {
            throw new NgDashboardException("Try to create stop-order without stop rate");
        }

        String email = userService.getUserEmailFromSecurityContext();
        User user = userService.findByEmail(email);
        CurrencyPair currencyPair = currencyService.findCurrencyPairById(inputOrder.getCurrencyPairId());

        OrderCreateDto prepareNewOrder = orderService.prepareNewOrder(currencyPair, operationType, user.getEmail(),
                inputOrder.getAmount(), inputOrder.getRate(), baseType);

        if (baseType == OrderBaseType.STOP_LIMIT) prepareNewOrder.setStop(inputOrder.getStop());

        OrderValidationDto orderValidationDto =
                orderService.validateOrder(prepareNewOrder);

        Map<String, Object> errorMap = orderValidationDto.getErrors();
        if (!errorMap.isEmpty()) {
            throw new NgDashboardException(errorMap.toString());
        }

        if (prepareNewOrder.getTotalWithComission().compareTo(inputOrder.getTotal()) != 0) {
            throw new NgDashboardException(String.format("Total value %.2f doesn't equal to calculate %.2f",
                    inputOrder.getTotal(), prepareNewOrder.getTotalWithComission()));
        }

        if (prepareNewOrder.getComission().compareTo(inputOrder.getCommission()) != 0) {
            throw new NgDashboardException(String.format("Commission %.2f doesn't equal to calculate %.2f",
                    inputOrder.getCommission(), prepareNewOrder.getComission()));
        }

        return prepareNewOrder;
    }

    @Override
    public boolean processUpdateOrder(User user, InputCreateOrderDto inputOrder) {
        boolean result = false;

        int orderId = inputOrder.getOrderId();
        ExOrder order = orderService.getOrderById(orderId);
        if (order == null) {
            throw new NgDashboardException("Order is not exist");
        }
        OperationType operationType = OperationType.valueOf(inputOrder.getOrderType());

        if (operationType != order.getOperationType()) {
            throw new NgDashboardException("Wrong operationType - " + operationType);
        }

        if (order.getCurrencyPairId() != inputOrder.getCurrencyPairId()) {
            throw new NgDashboardException("Not support change currency pair");
        }

        if (order.getUserId() != user.getId()) {
            throw new NgDashboardException("Order was created by another user");
        }
        if (order.getStatus() != OrderStatus.OPENED) {
            throw new NgDashboardException("Order status is not open");
        }

        if (StringUtils.isEmpty(inputOrder.getStatus())) {
            throw new NgDashboardException("Input order status is null");
        }

        OrderStatus orderStatus = OrderStatus.valueOf(inputOrder.getStatus());

        OrderCreateDto prepareOrder = prepareOrder(inputOrder);
        prepareOrder.setStatus(orderStatus);

        int outWalletId;
        BigDecimal outAmount;
        if (prepareOrder.getOperationType() == OperationType.BUY) {
            outWalletId = prepareOrder.getWalletIdCurrencyConvert();
            outAmount = prepareOrder.getTotalWithComission();
        } else {
            outWalletId = prepareOrder.getWalletIdCurrencyBase();
            outAmount = prepareOrder.getAmount();
        }

        if (walletService.ifEnoughMoney(outWalletId, outAmount)) {
            ExOrder exOrder = new ExOrder(prepareOrder);
            OrderBaseType orderBaseType = prepareOrder.getOrderBaseType();
            if (orderBaseType == null) {
                CurrencyPairType type = exOrder.getCurrencyPair().getPairType();
                orderBaseType = type == CurrencyPairType.ICO ? OrderBaseType.ICO : OrderBaseType.LIMIT;
                exOrder.setOrderBaseType(orderBaseType);
            }
            result = orderDao.updateOrder(orderId, exOrder);
        }
        return result;
    }

    @Override
    public boolean processUpdateStopOrder(User user, InputCreateOrderDto inputOrder) {
        boolean result = false;
        int orderId = inputOrder.getOrderId();
        OrderCreateDto stopOrder = stopOrderService.getOrderById(orderId, true);

        if (stopOrder == null) {
            throw new NgDashboardException("Order is not exist");
        }

        OperationType operationType = OperationType.valueOf(inputOrder.getOrderType());

        if (operationType != stopOrder.getOperationType()) {
            throw new NgDashboardException("Wrong operationType - " + operationType);
        }

        if (stopOrder.getCurrencyPair().getId() != inputOrder.getCurrencyPairId()) {
            throw new NgDashboardException("Not support change currency pair");
        }

        if (stopOrder.getUserId() != user.getId()) {
            throw new NgDashboardException("Order was created by another user");
        }
        if (stopOrder.getStatus() != OrderStatus.OPENED) {
            throw new NgDashboardException("Order status is not open");
        }

        if (StringUtils.isEmpty(inputOrder.getStatus())) {
            throw new NgDashboardException("Input order status is null");
        }

        OrderStatus orderStatus = OrderStatus.valueOf(inputOrder.getStatus());

        OrderCreateDto prepareOrder = prepareOrder(inputOrder);
        prepareOrder.setStatus(orderStatus);

        int outWalletId;
        BigDecimal outAmount;

        if (prepareOrder.getOperationType() == OperationType.BUY) {
            outWalletId = prepareOrder.getWalletIdCurrencyConvert();
            outAmount = prepareOrder.getTotalWithComission();
        } else {
            outWalletId = prepareOrder.getWalletIdCurrencyBase();
            outAmount = prepareOrder.getAmount();
        }

        if (walletService.ifEnoughMoney(outWalletId, outAmount)) {
            ExOrder exOrder = new ExOrder(prepareOrder);
            OrderBaseType orderBaseType = prepareOrder.getOrderBaseType();
            if (orderBaseType == null) {
                CurrencyPairType type = exOrder.getCurrencyPair().getPairType();
                orderBaseType = type == CurrencyPairType.ICO ? OrderBaseType.ICO : OrderBaseType.LIMIT;
                exOrder.setOrderBaseType(orderBaseType);
            }
            StopOrder order = new StopOrder(exOrder);
            result = stopOrderDao.updateOrder(orderId, order);
        }
        return result;
    }

    @Override
    public WalletsAndCommissionsForOrderCreationDto getWalletAndCommision(String email,
                                                                          OperationType operationType,
                                                                          int currencyPairId) {

        CurrencyPair activeCurrencyPair = currencyService.findCurrencyPairById(currencyPairId);

        if (activeCurrencyPair == null) {
            throw new RuntimeException("Wrong currency pair");
        }

        Currency spendCurrency = null;

        if (operationType == OperationType.SELL) {
            spendCurrency = activeCurrencyPair.getCurrency1();
        } else if (operationType == OperationType.BUY) {
            spendCurrency = activeCurrencyPair.getCurrency2();
        }

        WalletsAndCommissionsForOrderCreationDto walletAndCommission =
                orderService.getWalletAndCommission(email, spendCurrency, operationType);

        return walletAndCommission;

    }

    @Transactional(readOnly = true)
    @Override
    public ResponseInfoCurrencyPairDto getCurrencyPairInfo(int currencyPairId) {
        ResponseInfoCurrencyPairDto result = new ResponseInfoCurrencyPairDto();
        try {

            CurrencyPair currencyPair = currencyService.findCurrencyPairById(currencyPairId);
            Optional<BigDecimal> currentRateOptional =
                    orderService.getLastOrderPriceByCurrencyPairAndOperationType(currencyPair, OperationType.BUY);

            if (currentRateOptional.isPresent()) {
                logger.info("Currency {} rate {}", currencyPair.getName(), currentRateOptional.get());
                BigDecimal rateNow = BigDecimalProcessing.normalize(currentRateOptional.get());
                result.setCurrencyRate(rateNow.toPlainString());
            }

            ExOrderStatisticsDto orderStatistic =
                    orderService.getOrderStatistic(currencyPair, ChartPeriodsEnum.HOURS_24.getBackDealInterval(), null);
            logger.info("Current statistic for currency {}, statistic: {}", currencyPair.getName(), orderStatistic);
            if (orderStatistic != null) {
                result.setLastCurrencyRate(orderStatistic.getFirstOrderRate());//or orderStatistic.getLastOrderRate() ??
                result.setRateLow(orderStatistic.getMinRate());
                result.setRateHigh(orderStatistic.getMaxRate());
                result.setVolume24h(orderStatistic.getSumBase());

                if (currentRateOptional.isPresent()) {
                    BigDecimal currentRate = currentRateOptional.get();
                    BigDecimal lastRate = new BigDecimal(orderStatistic.getFirstOrderRate()); //or orderStatistic.getLastOrderRate() ??
                    if (!BigDecimalProcessing.moreThanZero(lastRate)) {
                        result.setPercentChange("100.00");
                        result.setChangedValue(currentRate.toPlainString());
                    } else {
                        BigDecimal percentGrowth = BigDecimalProcessing.doAction(
                                BigDecimalProcessing.normalize(lastRate),
                                currentRate,
                                ActionType.PERCENT_GROWTH);
                        result.setPercentChange(percentGrowth.toString());
                        BigDecimal subtract = BigDecimalProcessing.doAction(currentRate, lastRate, ActionType.SUBTRACT);
                        result.setChangedValue(BigDecimalProcessing.normalize(subtract).toPlainString());
                    }
                }
            }

//            List<ExOrderStatisticsShortByPairsDto> currencyRate =
//                    orderService.getStatForSomeCurrencies(Collections.singletonList(currencyPairId));
//
//            for (ExOrderStatisticsShortByPairsDto dto : currencyRate) {
//                if (dto == null) continue;
//
//                result.setCurrencyRate(dto.getLastOrderRate());
//                result.setPercentChange(dto.getPercentChange());
//                result.setLastCurrencyRate(dto.getPredLastOrderRate());
//
//                BigDecimal rateNow = new BigDecimal(dto.getLastOrderRate());
//                BigDecimal rateYesterday = new BigDecimal(dto.getPredLastOrderRate());
//                BigDecimal subtract = rateNow.subtract(rateYesterday);
//                if (subtract != null) {
//                    subtract = BigDecimalProcessing.normalize(subtract);
//                    result.setChangedValue(subtract.toString());
//                }
//                break;
//            }
//
//            List<StockExchangeStats> statistics;
//            statistics =
//                    stockExchangeService.getStockExchangeStatisticsByPeriod(currencyPairId);
//            //set rateHigh
//            statistics.stream()
//                    .map(StockExchangeStats::getPriceHigh)
//                    .max(Comparator.naturalOrder())
//                    .ifPresent(high -> result.setRateHigh(high.toString()));
//
//            //set rateLow
//            statistics.stream()
//                    .map(StockExchangeStats::getPriceLow)
//                    .max(Comparator.reverseOrder())
//                    .ifPresent(low -> result.setRateLow(low.toString()));
//
//            //set volume24h
//            statistics.stream()
//                    .map(StockExchangeStats::getVolume)
//                    .max(Comparator.naturalOrder())
//                    .ifPresent(volume -> result.setVolume24h(volume.toString()));
        } catch (ArithmeticException e) {
            logger.error("Error calculating max and min values - {}", e.getLocalizedMessage());
            throw new NgDashboardException("Error while processing calculate currency info, e - " + e.getMessage());
        }
        return result;
    }

    @Override
    public ResponseUserBalances getBalanceByCurrencyPairId(int currencyPairId, User user) {
        ResponseUserBalances result = new ResponseUserBalances();

        try {

            BigDecimal balanceByCurrency1;
            CurrencyPair currencyPair = currencyService.findCurrencyPairById(currencyPairId);
            List<ExOrderStatisticsShortByPairsDto> currencyRate =
                    orderService.getStatForSomeCurrencies(Collections.singletonList(currencyPairId));

            balanceByCurrency1 =
                    dashboardService.getBalanceByCurrency(user.getId(), currencyPair.getCurrency1().getId());

            result.setBalanceByCurrency1(balanceByCurrency1);

            BigDecimal balanceByCurrency2 = new BigDecimal(0);
            for (ExOrderStatisticsShortByPairsDto dto : currencyRate) {
                if (dto == null) continue;

                if (dto.getLastOrderRate() != null
                        && balanceByCurrency1 != null
                        && BigDecimalProcessing.moreThanZero(balanceByCurrency1)) {
                    BigDecimal rate = new BigDecimal(dto.getLastOrderRate());
                    balanceByCurrency2 = balanceByCurrency1.multiply(rate);
                }
                break;
            }

            result.setBalanceByCurrency2(balanceByCurrency2);

        } catch (ArithmeticException e) {
            logger.error("Error calculating max and min values - {}", e.getLocalizedMessage());
            throw new NgDashboardException("Error while processing calculate currency info, e - " + e.getMessage());
        }
        return result;
    }

    @Override
    public String createOrder(InputCreateOrderDto inputOrder) {

        OrderCreateDto prepareNewOrder = prepareOrder(inputOrder);

        String result;
        switch (prepareNewOrder.getOrderBaseType()) {
            case STOP_LIMIT: {
                result = stopOrderService.create(prepareNewOrder, OrderActionEnum.CREATE, null);
                break;
            }
            default: {
                result = orderService.createOrder(prepareNewOrder, OrderActionEnum.CREATE, null);
            }
        }
        return result;
    }

    @Override
    public Map<String, Object> filterDataPeriod(List<CandleDto> data, long fromSeconds, long toSeconds, String resolution) {
        List<CandleDto> filteredData = new ArrayList<>(data);
        HashMap<String, Object> filterDataResponse = new HashMap<>();
        if (filteredData.isEmpty()) {
            filterDataResponse.put("s", "ok");
            getData(filterDataResponse, filteredData, resolution);
            return filterDataResponse;
        }

        if ((filteredData.get(data.size() - 1).getTime() / 1000) < fromSeconds) {
            filterDataResponse.put("s", "no_data");
            filterDataResponse.put("nextTime", filteredData.get(data.size() - 1).getTime() / 1000);
            return filterDataResponse;

        }

        int fromIndex = -1;
        int toIndex = -1;

        for (int i = 0; i < filteredData.size(); i++) {
            long time = filteredData.get(i).getTime() / 1000;
            if (fromIndex == -1 && time >= fromSeconds) {
                fromIndex = i;
            }
            if (toIndex == -1 && time >= toSeconds) {
                toIndex = time > toSeconds ? i - 1 : i;
            }
            if (fromIndex != -1 && toIndex != -1) {
                break;
            }
        }

        fromIndex = fromIndex > 0 ? fromIndex : 0;
        toIndex = toIndex > 0 ? toIndex + 1 : filteredData.size();


        toIndex = Math.min(fromIndex + 1000, toIndex); // do not send more than 1000 bars for server capacity reasons

        String s = "ok";

        if (toSeconds < filteredData.get(0).getTime() / 1000) {
            s = "no_data";
        }
        filterDataResponse.put("s", s);

        toIndex = Math.min(fromIndex + 1000, toIndex);

        if (fromIndex > toIndex) {
            filterDataResponse.put("s", "no_data");
            filterDataResponse.put("nextTime", filteredData.get(data.size() - 1).getTime() / 1000);
            return filterDataResponse;
        }

        filteredData = filteredData.subList(fromIndex, toIndex);
        getData(filterDataResponse, filteredData, resolution);
        return filterDataResponse;

    }

    private void getData(HashMap<String, Object> response, List<CandleDto> result, String resolution) {
        List<Long> t = new ArrayList<>();
        List<BigDecimal> o = new ArrayList<>();
        List<BigDecimal> h = new ArrayList<>();
        List<BigDecimal> l = new ArrayList<>();
        List<BigDecimal> c = new ArrayList<>();
        List<BigDecimal> v = new ArrayList<>();

        LocalDateTime first = LocalDateTime.ofEpochSecond((result.get(0).getTime() / 1000), 0, ZoneOffset.UTC)
                .truncatedTo(ChronoUnit.DAYS);
        t.add(first.toEpochSecond(ZoneOffset.UTC));
        o.add(BigDecimal.ZERO);
        h.add(BigDecimal.ZERO);
        l.add(BigDecimal.ZERO);
        c.add(BigDecimal.ZERO);
        v.add(BigDecimal.ZERO);

        for (CandleDto r : result) {
            LocalDateTime now = LocalDateTime.ofEpochSecond((r.getTime() / 1000), 0, ZoneOffset.UTC)
                    .truncatedTo(ChronoUnit.MINUTES);
            LocalDateTime actualDateTime;
            long currentMinutesOfHour = now.getLong(ChronoField.MINUTE_OF_HOUR);
            long currentHourOfDay = now.getLong(ChronoField.HOUR_OF_DAY);

            switch (resolution) {
                case "30":
                    long minutes = Math.abs(currentMinutesOfHour - 30);
                    actualDateTime = now.minusMinutes(currentMinutesOfHour <= 30 ? currentMinutesOfHour : minutes);
                    break;
                case "60":
                    actualDateTime = now.minusMinutes(currentMinutesOfHour);
                    break;
                case "240":
                    actualDateTime = now.minusMinutes(currentMinutesOfHour).minusHours(currentHourOfDay % 4);
                    break;
                case "720":
                    actualDateTime = now.minusMinutes(currentMinutesOfHour).minusHours(currentHourOfDay % 12);
                    break;
                case "M":
                    actualDateTime = now.truncatedTo(ChronoUnit.DAYS).withDayOfMonth(1);
                    break;
                default:
                    actualDateTime = now.minusMinutes(currentMinutesOfHour);

            }

            t.add(actualDateTime.toEpochSecond(ZoneOffset.UTC));
            o.add(r.getOpen());
            h.add(r.getHigh());
            l.add(r.getLow());
            c.add(r.getClose());
            v.add(r.getVolume());
        }
        response.put("t", t);
        response.put("o", o);
        response.put("h", h);
        response.put("l", l);
        response.put("c", c);
        response.put("v", v);
    }


}
