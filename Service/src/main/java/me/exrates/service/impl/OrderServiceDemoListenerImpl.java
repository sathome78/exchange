package me.exrates.service.impl;

import me.exrates.model.CurrencyPair;
import me.exrates.model.dto.InputCreateOrderDto;
import me.exrates.model.dto.OrderCreateDto;
import me.exrates.model.dto.OrderCreateSummaryDto;
import me.exrates.model.dto.OrderValidationDto;
import me.exrates.model.enums.OperationType;
import me.exrates.model.enums.OrderBaseType;
import me.exrates.model.userOperation.enums.UserOperationAuthority;
import me.exrates.service.*;
import me.exrates.service.exception.NotCreatableOrderException;
import me.exrates.service.exception.NotEnoughUserWalletMoneyException;
import me.exrates.service.exception.OrderCreationException;
import me.exrates.service.exception.UserOperationAccessException;
import me.exrates.service.exception.api.OrderParamsWrongException;
import me.exrates.service.stopOrder.StopOrderService;
import me.exrates.service.userOperation.UserOperationService;
import me.exrates.service.vo.ProfileData;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.LocaleResolver;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.security.Principal;
import java.util.Locale;
import java.util.Map;

import static me.exrates.model.enums.OrderActionEnum.CREATE;

@Service
public class OrderServiceDemoListenerImpl implements OrderServiceDemoListener {

    private static final Logger LOGGER = LogManager.getLogger(OrderServiceDemoListener.class);

    @Autowired
    OrderService orderService;

    @Autowired
    CommissionService commissionService;

    @Autowired
    CurrencyService currencyService;

    @Autowired
    WalletService walletService;

    @Autowired
    UserService userService;

    @Autowired
    private UserOperationService userOperationService;

    @Autowired
    MessageSource messageSource;

    @Autowired
    LocaleResolver localeResolver;

    @Autowired
    StopOrderService stopOrderService;

    @Override
    public OrderCreateSummaryDto newOrderToSell(OperationType orderType, int  userId, BigDecimal amount, BigDecimal rate, OrderBaseType baseType, CurrencyPair currencyPair, BigDecimal stop) {
        long before = System.currentTimeMillis();
        try {
            OrderCreateSummaryDto orderCreateSummaryDto;
            if (amount == null) amount = BigDecimal.ZERO;
            if (rate == null) rate = BigDecimal.ZERO;
            if (baseType == null) baseType = OrderBaseType.LIMIT;
            CurrencyPair activeCurrencyPair = currencyService.getNotHiddenCurrencyPairByName(currencyPair.getName());
            if (activeCurrencyPair == null) {
                throw new RuntimeException("Wrong currency pair");
            }
            if (baseType == OrderBaseType.STOP_LIMIT && stop == null) {
                throw new RuntimeException("Try to create stop-order without stop rate");
            }
            OrderCreateDto orderCreateDto = orderService.prepareNewOrder(activeCurrencyPair, orderType, userService.findById(userId), amount, rate, baseType);
            orderCreateDto.setOrderBaseType(baseType);
            orderCreateDto.setStop(stop);
            /**/
            OrderValidationDto orderValidationDto = orderService.validateOrder(orderCreateDto);
            Map<String, Object> errorMap = orderValidationDto.getErrors();
            orderCreateSummaryDto = new OrderCreateSummaryDto(orderCreateDto, Locale.ENGLISH);
            if (!errorMap.isEmpty()) {
                for (Map.Entry<String, Object> pair : errorMap.entrySet()) {
                    Object[] messageParams = orderValidationDto.getErrorParams().get(pair.getKey());
                    pair.setValue(messageSource.getMessage((String) pair.getValue(), messageParams, Locale.ENGLISH));
                }
                errorMap.put("order", orderCreateSummaryDto);
                throw new OrderParamsWrongException();
            }
            return orderCreateSummaryDto;
        } catch (Exception e) {
            long after = System.currentTimeMillis();
            LOGGER.error("error... ms: " + (after - before) + " : " + e);
            throw e;
        } finally {
            long after = System.currentTimeMillis();
            LOGGER.debug("completed... ms: " + (after - before));
        }

    }

    @Override
    public String recordOrderToDB(InputCreateOrderDto inputCreateOrderDto, OrderCreateDto orderCreateDto) {
        ProfileData profileData = new ProfileData(200);
        long before = System.currentTimeMillis();
        boolean accessToOperationForUser = userOperationService.getStatusAuthorityForUserByOperation(inputCreateOrderDto.getUserId(), UserOperationAuthority.TRADING);
        if (!accessToOperationForUser) {
            throw new UserOperationAccessException(messageSource.getMessage("merchant.operationNotAvailable", null, Locale.ENGLISH));
        }
        try {
            if (orderCreateDto == null) {
                /*it may be if user twice click the create button from the current submit form. After first click orderCreateDto will be cleaned*/
                throw new OrderCreationException(messageSource.getMessage("order.recreateerror", null, Locale.ENGLISH));
            }
            try {
                switch (OrderBaseType.valueOf(inputCreateOrderDto.getBaseType())) {
                    case STOP_LIMIT: {
                        return stopOrderService.create(orderCreateDto, CREATE, Locale.ENGLISH);
                    }
                    default: {
                        return orderService.createOrder(orderCreateDto, CREATE, Locale.ENGLISH);
                    }
                }
            } catch (NotEnoughUserWalletMoneyException e) {
                throw new NotEnoughUserWalletMoneyException(messageSource.getMessage("validation.orderNotEnoughMoney", null, Locale.ENGLISH));
            } catch (OrderCreationException e) {
                throw new OrderCreationException(messageSource.getMessage("order.createerror", new Object[]{e.getLocalizedMessage()}, Locale.ENGLISH));
            } catch (NotCreatableOrderException e) {
                throw e;
            }
        } catch (Exception e) {
            long after = System.currentTimeMillis();
            LOGGER.error("error... ms: " + (after - before) + " :\n\t " + ExceptionUtils.getStackTrace(e));
            throw e;
        } finally {
            profileData.checkAndLog("slow creation order: " + orderCreateDto + " profile: " + profileData);
            long after = System.currentTimeMillis();
            LOGGER.debug("completed... ms: " + (after - before));
        }
    }
}
