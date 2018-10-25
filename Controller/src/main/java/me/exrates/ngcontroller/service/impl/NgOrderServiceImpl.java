package me.exrates.ngcontroller.service.impl;

import me.exrates.dao.OrderDao;
import me.exrates.dao.StopOrderDao;
import me.exrates.model.CurrencyPair;
import me.exrates.model.ExOrder;
import me.exrates.model.StopOrder;
import me.exrates.model.User;
import me.exrates.model.dto.OrderCreateDto;
import me.exrates.model.dto.OrderValidationDto;
import me.exrates.model.enums.CurrencyPairType;
import me.exrates.model.enums.OperationType;
import me.exrates.model.enums.OrderBaseType;
import me.exrates.model.enums.OrderStatus;
import me.exrates.ngcontroller.NgDashboardException;
import me.exrates.ngcontroller.mobel.InputCreateOrderDto;
import me.exrates.ngcontroller.service.NgOrderService;
import me.exrates.service.CurrencyService;
import me.exrates.service.OrderService;
import me.exrates.service.UserService;
import me.exrates.service.WalletService;
import me.exrates.service.exception.api.OrderParamsWrongException;
import me.exrates.service.stopOrder.StopOrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Map;

@Service
public class NgOrderServiceImpl implements NgOrderService {

    private final UserService userService;
    private final CurrencyService currencyService;
    private final OrderService orderService;
    private final OrderDao orderDao;
    private final WalletService walletService;
    private final StopOrderDao stopOrderDao;
    private final StopOrderService stopOrderService;

    @Autowired
    public NgOrderServiceImpl(UserService userService,
                              CurrencyService currencyService,
                              OrderService orderService,
                              OrderDao orderDao,
                              WalletService walletService,
                              StopOrderDao stopOrderDao,
                              StopOrderService stopOrderService) {
        this.userService = userService;
        this.currencyService = currencyService;
        this.orderService = orderService;
        this.orderDao = orderDao;
        this.walletService = walletService;
        this.stopOrderDao = stopOrderDao;
        this.stopOrderService = stopOrderService;
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

        if (prepareNewOrder.getTotalWithComission().compareTo(inputOrder.getTotal()) != 0 ) {
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

        int orderId = Integer.parseInt(inputOrder.getOrderId());
        ExOrder order = orderService.getOrderById(orderId);
        if (order == null) {
            throw new NgDashboardException("Order is not exist");
        }
        OperationType operationType = OperationType.valueOf(inputOrder.getOrderType());

        if (operationType != order.getOperationType()) {
            throw new NgDashboardException("Wrong operationType - " + operationType);
        }

        if (order.getCurrencyPair().getId() != inputOrder.getCurrencyPairId()) {
            throw new NgDashboardException("Not support change currency pair");
        }

        if (order.getUserId() != user.getId()) {
            throw new NgDashboardException("Order was created by another user");
        }
        if (order.getStatus() != OrderStatus.OPENED) {
            throw new NgDashboardException("Order status is not open");
        }

        OrderCreateDto prepareOrder = prepareOrder(inputOrder);

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
        int orderId = Integer.parseInt(inputOrder.getOrderId());
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

        OrderCreateDto prepareOrder = prepareOrder(inputOrder);

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
}
