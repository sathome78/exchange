package me.exrates.ngService.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Maps;
import me.exrates.dao.OrderDao;
import me.exrates.dao.StopOrderDao;
import me.exrates.model.Currency;
import me.exrates.model.CurrencyPair;
import me.exrates.model.ExOrder;
import me.exrates.model.StopOrder;
import me.exrates.model.User;
import me.exrates.model.dto.CandleDto;
import me.exrates.model.dto.ExOrderStatisticsDto;
import me.exrates.model.dto.InputCreateOrderDto;
import me.exrates.model.dto.OrderCreateDto;
import me.exrates.model.dto.OrderValidationDto;
import me.exrates.model.dto.SimpleOrderBookItem;
import me.exrates.model.dto.WalletsAndCommissionsForOrderCreationDto;
import me.exrates.model.dto.onlineTableDto.OrderListDto;
import me.exrates.model.enums.ActionType;
import me.exrates.model.enums.ChartPeriodsEnum;
import me.exrates.model.enums.CurrencyPairType;
import me.exrates.model.enums.OperationType;
import me.exrates.model.enums.OrderActionEnum;
import me.exrates.model.enums.OrderBaseType;
import me.exrates.model.enums.OrderStatus;
import me.exrates.model.ngExceptions.NgDashboardException;
import me.exrates.model.ngModel.ResponseInfoCurrencyPairDto;
import me.exrates.model.util.BigDecimalProcessing;
import me.exrates.model.util.BigDecimalToStringSerializer;
import me.exrates.ngService.NgOrderService;
import me.exrates.service.CurrencyService;
import me.exrates.service.DashboardService;
import me.exrates.service.OrderService;
import me.exrates.service.UserService;
import me.exrates.service.WalletService;
import me.exrates.service.cache.ExchangeRatesHolder;
import me.exrates.dao.exception.notfound.CurrencyPairNotFoundException;
import me.exrates.service.stopOrder.StopOrderService;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONArray;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.temporal.ChronoField;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.stream.IntStream;

@Service
public class NgOrderServiceImpl implements NgOrderService {

    private static final Logger logger = LogManager.getLogger(NgOrderServiceImpl.class);
    private static final Executor executor = Executors.newSingleThreadExecutor();

    private final CurrencyService currencyService;
    private final OrderService orderService;
    private final OrderDao orderDao;
    private final ObjectMapper objectMapper;
    private final StopOrderDao stopOrderDao;
    private final DashboardService dashboardService;
    private final SimpMessagingTemplate messagingTemplate;
    private final StopOrderService stopOrderService;
    private final UserService userService;
    private final WalletService walletService;
    private final ExchangeRatesHolder exchangeRatesHolder;

    @Autowired
    public NgOrderServiceImpl(CurrencyService currencyService,
                              OrderService orderService,
                              OrderDao orderDao,
                              ObjectMapper objectMapper,
                              StopOrderDao stopOrderDao,
                              DashboardService dashboardService,
                              SimpMessagingTemplate messagingTemplate,
                              StopOrderService stopOrderService,
                              UserService userService,
                              WalletService walletService,
                              ExchangeRatesHolder exchangeRatesHolder) {
        this.currencyService = currencyService;
        this.orderService = orderService;
        this.orderDao = orderDao;
        this.objectMapper = objectMapper;
        this.stopOrderDao = stopOrderDao;
        this.dashboardService = dashboardService;
        this.messagingTemplate = messagingTemplate;
        this.stopOrderService = stopOrderService;
        this.userService = userService;
        this.walletService = walletService;
        this.exchangeRatesHolder = exchangeRatesHolder;
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

//        BigDecimal totalWithComission = prepareNewOrder.getTotalWithComission();
//        BigDecimal inputTotal = inputOrder.getTotal();
//
//        if (totalWithComission.setScale(2, RoundingMode.HALF_UP)
//                .compareTo(inputTotal.setScale(2, RoundingMode.HALF_UP)) != 0) {
//
////        if (prepareNewOrder.getTotalWithComission().compareTo(inputOrder.getTotal()) != 0) {
//            logger.error("Comparing total, from user - {}, from server - {}", inputOrder.getTotal(),
//                    prepareNewOrder.getTotalWithComission());
//            throw new NgDashboardException(String.format("Total value %.2f doesn't equal to calculate %.2f",
//                    inputOrder.getTotal(), prepareNewOrder.getTotalWithComission()));
//        }
//
//        BigDecimal commissionPrepareOrder = prepareNewOrder.getComission();
//        BigDecimal inputOrderCommission = inputOrder.getCommission();
//        if (commissionPrepareOrder.setScale(5, RoundingMode.HALF_UP)
//                .compareTo(inputOrderCommission.setScale(5, RoundingMode.HALF_UP)) != 0) {
//            logger.error("Comparing commission, from user - {}, from server - {}", inputOrder.getCommission(),
//                    prepareNewOrder.getComission());
//            throw new NgDashboardException(String.format("Commission %.2f doesn't equal to calculate %.2f",
//                    inputOrder.getCommission(), prepareNewOrder.getComission()));
//        }

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
            emitOpenOrderMessage(prepareOrder);
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
            throw new NgDashboardException("Wrong currency pair");
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
                    orderService.getLastOrderPriceByCurrencyPair(currencyPair);

            if (currentRateOptional.isPresent()) {
                logger.debug("Currency {} rate {}", currencyPair.getName(), currentRateOptional.get());
                BigDecimal rateNow = BigDecimalProcessing.normalize(currentRateOptional.get());
                result.setCurrencyRate(rateNow.toPlainString());
            }

            ExOrderStatisticsDto orderStatistic =
                    orderService.getOrderStatistic(currencyPair, ChartPeriodsEnum.HOURS_24.getBackDealInterval(), null);
            logger.debug("Current statistic for currency {}, statistic: {}", currencyPair.getName(), orderStatistic);
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
        } catch (ArithmeticException e) {
            logger.error("Error calculating max and min values - {}", e.getLocalizedMessage());
            throw new NgDashboardException("Error while processing calculate currency info, e - " + e.getMessage());
        }
        return result;
    }

    @Override
    public Map<String, Map<String, String>> getBalanceByCurrencyPairId(int currencyPairId, User user)
            throws CurrencyPairNotFoundException {

        Map<String, Map<String, String>> currencyPairBalances = Maps.newHashMap();

        CurrencyPair currencyPair;
        try {
            currencyPair = currencyService.findCurrencyPairById(currencyPairId);
        } catch (CurrencyPairNotFoundException e) {
            String message = "Failed to get currency pair for id: " + currencyPairId;
            logger.warn(message, e);
            throw new CurrencyPairNotFoundException(message);
        }

        BigDecimal balanceByCurrency1 =
                dashboardService.getBalanceByCurrency(user.getId(), currencyPair.getCurrency1().getId());
        if (balanceByCurrency1 == null || balanceByCurrency1.compareTo(BigDecimal.ZERO) < 1) {
            balanceByCurrency1 = BigDecimal.ZERO;
        }
        Map<String, String> firstMap = Maps.newHashMap();
        firstMap.put("name", currencyPair.getCurrency1().getName());
        firstMap.put("balance", BigDecimalToStringSerializer.convert(balanceByCurrency1));

        BigDecimal balanceByCurrency2 =
                dashboardService.getBalanceByCurrency(user.getId(), currencyPair.getCurrency2().getId());

        if (balanceByCurrency2 == null || balanceByCurrency2.compareTo(BigDecimal.ZERO) < 1) {
            balanceByCurrency2 = BigDecimal.ZERO;
        }
        Map<String, String> secondMap = Maps.newHashMap();
        secondMap.put("name", currencyPair.getCurrency2().getName());
        secondMap.put("balance", BigDecimalToStringSerializer.convert(balanceByCurrency2));

        currencyPairBalances.put("cur1", firstMap);
        currencyPairBalances.put("cur2", secondMap);
        return currencyPairBalances;
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
        emitOpenOrderMessage(prepareNewOrder);
        return result;
    }


    @SuppressWarnings("Duplicates")
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

    @Override
    public List<CurrencyPair> getAllPairsByFirstPartName(String pathName) {
        return currencyService.getPairsByFirstPartName(pathName);
    }

    @Override
    public List<CurrencyPair> getAllPairsBySecondPartName(String pathName) {
        return currencyService.getPairsBySecondPartName(pathName);
    }



//    private void countTotal(List<SimpleOrderBookItem> items, OrderType orderType) {
//        if (orderType == OrderType.BUY) {
//            for (int i = items.size() - 1; i >= 0; i--) {
//                if (i == (items.size() - 1)) {
//                    items.get(i).setTotal(items.get(i).getAmount());
//                } else {
//                    items.get(i).setTotal(items.get(i).getAmount().add(items.get(i + 1).getTotal()));
//                }
//            }
//        } else {
//            for (int i = 0; i < items.size(); i++) {
//                if (i == 0) {
//                    items.get(i).setTotal(items.get(i).getAmount());
//                } else {
//                    items.get(i).setTotal(items.get(i).getAmount().add(items.get(i - 1).getTotal()));
//                }
//            }
//        }
//    }

    private void setSumAmount(List<SimpleOrderBookItem> items) {
        for (int i = 0; i < items.size(); i++) {
            if (i == 0) {
                items.get(i).setSumAmount(items.get(i).getAmount());
            } else {
                BigDecimal add =
                        BigDecimalProcessing.doAction(items.get(i).getAmount(), items.get(i - 1).getSumAmount(), ActionType.ADD);
                items.get(i).setSumAmount(add);
            }
        }
    }

    private BigDecimal getAmount(List<OrderListDto> list) {
        BigDecimal amount = BigDecimal.ZERO;
        for (OrderListDto item : list) {
            amount = amount.add(new BigDecimal(item.getAmountBase()));
        }
        return amount;
    }

    private void emitOpenOrderMessage(OrderCreateDto order) {
        executor.execute(() ->
                IntStream.range(1, 6).forEach(precision -> {
                    int currencyPairId = order.getCurrencyPair().getId();
                    String destination = String.format("/topic/open-orders/%d/%d", currencyPairId, precision);
                    try {
                        messagingTemplate.convertAndSend(destination, convertToString(currencyPairId, precision));
                    } catch (JsonProcessingException e) {
                        e.printStackTrace();
                    }
                }));
    }

    private String convertToString(int currencyId, int precision) throws JsonProcessingException {
        JSONArray objectsArray = new JSONArray();
        throw new UnsupportedOperationException("NgOrderServiceImpl.convertToString needs update");
//        objectsArray.put(objectMapper.writeValueAsString(findAllOrderBookItems(OrderType.BUY, currencyId, precision)));
//        objectsArray.put(objectMapper.writeValueAsString(findAllOrderBookItems(OrderType.SELL, currencyId, precision)));
//        return objectsArray.toString();
    }


    @SuppressWarnings("Duplicates")
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
