package me.exrates.ngcontroller.service.impl;

import me.exrates.dao.OrderDao;
import me.exrates.model.CurrencyPair;
import me.exrates.model.User;
import me.exrates.model.dto.OrderCreateDto;
import me.exrates.model.dto.OrderValidationDto;
import me.exrates.model.enums.OperationType;
import me.exrates.model.enums.OrderBaseType;
import me.exrates.ngcontroller.NgDashboardException;
import me.exrates.ngcontroller.mobel.InputCreateOrderDto;
import me.exrates.ngcontroller.service.NgOrderService;
import me.exrates.service.CurrencyService;
import me.exrates.service.OrderService;
import me.exrates.service.UserService;
import me.exrates.service.exception.api.OrderParamsWrongException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class NgOrderServiceImpl implements NgOrderService {

    private final UserService userService;
    private final CurrencyService currencyService;
    private final OrderService orderService;
    private final OrderDao orderDao;

    @Autowired
    public NgOrderServiceImpl(UserService userService,
                              CurrencyService currencyService,
                              OrderService orderService,
                              OrderDao orderDao) {
        this.userService = userService;
        this.currencyService = currencyService;
        this.orderService = orderService;
        this.orderDao = orderDao;
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
    public boolean completeDeleteOrder(int idOrder) {
        return orderDao.completeDeleteOrder(idOrder);
    }
}
