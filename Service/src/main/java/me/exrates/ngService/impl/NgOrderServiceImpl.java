package me.exrates.ngService.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Maps;
import lombok.extern.log4j.Log4j2;
import me.exrates.dao.OrderDao;
import me.exrates.dao.StopOrderDao;
import me.exrates.dao.exception.notfound.CurrencyPairNotFoundException;
import me.exrates.model.Currency;
import me.exrates.model.CurrencyPair;
import me.exrates.model.CurrencyPairWithRestriction;
import me.exrates.model.ExOrder;
import me.exrates.model.StopOrder;
import me.exrates.model.User;
import me.exrates.model.dto.ExOrderStatisticsDto;
import me.exrates.model.dto.InputCreateOrderDto;
import me.exrates.model.dto.OrderCreateDto;
import me.exrates.model.dto.OrderValidationDto;
import me.exrates.model.dto.SimpleOrderBookItem;
import me.exrates.model.dto.WalletsAndCommissionsForOrderCreationDto;
import me.exrates.model.dto.onlineTableDto.OrderListDto;
import me.exrates.model.enums.ActionType;
import me.exrates.model.enums.ChartPeriodsEnum;
import me.exrates.model.enums.CurrencyPairRestrictionsEnum;
import me.exrates.model.enums.CurrencyPairType;
import me.exrates.model.enums.OperationType;
import me.exrates.model.enums.OrderActionEnum;
import me.exrates.model.enums.OrderBaseType;
import me.exrates.model.enums.OrderStatus;
import me.exrates.model.enums.RestrictedCountrys;
import me.exrates.model.ngExceptions.NgDashboardException;
import me.exrates.model.ngExceptions.NgOrderValidationException;
import me.exrates.model.ngModel.ResponseInfoCurrencyPairDto;
import me.exrates.model.userOperation.enums.UserOperationAuthority;
import me.exrates.model.util.BigDecimalProcessing;
import me.exrates.model.util.BigDecimalToStringSerializer;
import me.exrates.ngService.NgOrderService;
import me.exrates.service.CurrencyService;
import me.exrates.service.DashboardService;
import me.exrates.service.OrderService;
import me.exrates.service.UserService;
import me.exrates.service.WalletService;
import me.exrates.service.cache.ExchangeRatesHolder;
import me.exrates.service.exception.NeedVerificationException;
import me.exrates.service.exception.OrderCreationRestrictedException;
import me.exrates.service.stopOrder.StopOrderService;
import me.exrates.service.userOperation.UserOperationService;
import org.apache.commons.lang.StringUtils;
import org.json.JSONArray;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.stream.IntStream;

@Log4j2
@Service
public class NgOrderServiceImpl implements NgOrderService {

    private static final Executor EXECUTOR = Executors.newSingleThreadExecutor();

    private final CurrencyService currencyService;
    private final OrderService orderService;
    private final OrderDao orderDao;
    private final StopOrderDao stopOrderDao;
    private final DashboardService dashboardService;
    private final SimpMessagingTemplate messagingTemplate;
    private final StopOrderService stopOrderService;
    private final UserService userService;
    private final WalletService walletService;
    private final UserOperationService userOperationService;

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
                              ExchangeRatesHolder exchangeRatesHolder, UserOperationService userOperationService) {
        this.currencyService = currencyService;
        this.orderService = orderService;
        this.orderDao = orderDao;
        this.stopOrderDao = stopOrderDao;
        this.dashboardService = dashboardService;
        this.messagingTemplate = messagingTemplate;
        this.stopOrderService = stopOrderService;
        this.userService = userService;
        this.walletService = walletService;
        this.userOperationService = userOperationService;
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
        CurrencyPairWithRestriction currencyPair = currencyService.findCurrencyPairByIdWithRestrictions(inputOrder.getCurrencyPairId());

        if (currencyPair.hasTradeRestriction()) {
            if (currencyPair.getTradeRestriction().contains(CurrencyPairRestrictionsEnum.ESCAPE_USA) && user.getVerificationRequired()) {
                if (Objects.isNull(user.getCountry())) {
                    throw new NeedVerificationException("Sorry, you must pass verification to trade this pair.");
                } else if(user.getCountry().equalsIgnoreCase(RestrictedCountrys.USA.name())) {
                    throw new OrderCreationRestrictedException("Sorry, you are not allowed to trade this pair");
                }
            }
        }

        OrderCreateDto prepareNewOrder = orderService.prepareNewOrder(currencyPair, operationType, user.getEmail(),
                inputOrder.getAmount(), inputOrder.getRate(), baseType);

        if (baseType == OrderBaseType.STOP_LIMIT) prepareNewOrder.setStop(inputOrder.getStop());

        OrderValidationDto orderValidationDto =
                orderService.validateOrder(prepareNewOrder);

        Map<String, Object> errorMap = orderValidationDto.getErrors();
        if (!errorMap.isEmpty()) {
            throw new NgOrderValidationException(orderValidationDto);
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

        return orderService.getWalletAndCommission(email, spendCurrency, operationType);

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
                log.debug("Currency {} rate {}", currencyPair.getName(), currentRateOptional.get());
                BigDecimal rateNow = BigDecimalProcessing.normalize(currentRateOptional.get());
                result.setCurrencyRate(rateNow.toPlainString());
            }

            ExOrderStatisticsDto orderStatistic =
                    orderService.getOrderStatistic(currencyPair, ChartPeriodsEnum.HOURS_24.getBackDealInterval(), null);
            log.debug("Current statistic for currency {}, statistic: {}", currencyPair.getName(), orderStatistic);
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
            log.error("Error calculating max and min values - {}", e.getLocalizedMessage());
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
            log.warn(message, e);
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
        EXECUTOR.execute(() ->
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
}
