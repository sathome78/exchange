package me.exrates.ngcontroller.service.impl;

import me.exrates.model.CurrencyPair;
import me.exrates.model.ExOrder;
import me.exrates.model.dto.onlineTableDto.OrderWideListDto;
import me.exrates.model.enums.CurrencyPairType;
import me.exrates.model.enums.OperationType;
import me.exrates.model.enums.OrderBaseType;
import me.exrates.model.enums.OrderStatus;
import me.exrates.ngcontroller.mobel.InputCreateOrderDto;
import me.exrates.service.CurrencyService;
import me.exrates.service.OrderService;
import org.apache.commons.lang.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

@Service
public class OrderMockService {

    @Autowired
    OrderService orderService;

    @Autowired
    CurrencyService currencyService;

    public void initOpenOrders(Map<CurrencyPair, Set<OrderWideListDto>> openOrders) {
        currencyService
                .findPermitedCurrencyPairs(CurrencyPairType.MAIN)
                .forEach(cp -> openOrders.putIfAbsent(cp, new LinkedHashSet<>()));
    }

    public void initClosedOrders(Map<CurrencyPair, Set<OrderWideListDto>> openOrders) {
        currencyService
                .findPermitedCurrencyPairs(CurrencyPairType.MAIN)
                .forEach(cp -> openOrders.putIfAbsent(cp, new LinkedHashSet<>()));
    }

    private LinkedHashSet<OrderWideListDto> getOpenOrders() {

        OrderWideListDto order1 = new OrderWideListDto();
        order1.setId(1);
        order1.setOperationType(OperationType.BUY.name());
        order1.setOperationTypeEnum(OperationType.BUY);
        order1.setAmountBase("0.035156250");
        order1.setExExchangeRate("6400.00");
        order1.setCommissionFixedAmount("0.450000000");
        order1.setAmountWithCommission("225.450000000");
        order1.setOrderBaseType(OrderBaseType.LIMIT);
        order1.setStatus(OrderStatus.OPENED);
        order1.setStatusString(OrderStatus.OPENED.toString());
        order1.setCurrencyPairId(1);
        order1.setCurrencyPairName("BTC/USD");
        order1.setDateCreation(LocalDateTime.now().minusHours(1).minusMinutes(15));

        OrderWideListDto order2 = new OrderWideListDto();
        order2.setId(2);
        order2.setOperationType(OperationType.BUY.name());
        order2.setOperationTypeEnum(OperationType.BUY);
        order2.setAmountBase("0.04836324");
        order2.setExExchangeRate("6378.077");
        order2.setAmountWithCommission("30.84644");
        order2.setCommissionFixedAmount("0.510151433");
        order2.setOrderBaseType(OrderBaseType.LIMIT);
        order2.setStatus(OrderStatus.OPENED);
        order2.setStatusString(OrderStatus.OPENED.toString());
        order2.setCurrencyPairId(1);
        order2.setCurrencyPairName("BTC/USD");
        order2.setDateCreation(LocalDateTime.now().minusHours(1).minusMinutes(30));

        OrderWideListDto order3 = new OrderWideListDto();
        order3.setId(3);
        order3.setOperationType(OperationType.BUY.name());
        order3.setOperationTypeEnum(OperationType.BUY);
        order3.setAmountBase("0.113900000");
        order3.setExExchangeRate("6349.95546969");
        order3.setAmountWithCommission("723.259927998");
        order3.setCommissionFixedAmount("3.715338384");
        order3.setOrderBaseType(OrderBaseType.LIMIT);
        order3.setStatus(OrderStatus.OPENED);
        order3.setStatusString(OrderStatus.OPENED.toString());
        order3.setCurrencyPairId(1);
        order3.setCurrencyPairName("BTC/USD");
        order3.setDateCreation(LocalDateTime.now().minusHours(1).minusMinutes(50));

        OrderWideListDto order4 = new OrderWideListDto();
        order4.setId(4);
        order4.setOperationType(OperationType.SELL.name());
        order4.setOperationTypeEnum(OperationType.SELL);
        order4.setAmountBase("0.005347064");
        order4.setExExchangeRate("6658.99999997");
        order4.setAmountWithCommission("35.606099176");
        order4.setCommissionFixedAmount("0.071212198");
        order4.setOrderBaseType(OrderBaseType.LIMIT);
        order4.setStatus(OrderStatus.OPENED);
        order4.setStatusString(OrderStatus.OPENED.toString());
        order4.setCurrencyPairId(1);
        order4.setCurrencyPairName("BTC/USD");
        order4.setDateCreation(LocalDateTime.now().minusHours(1).minusMinutes(55));

        OrderWideListDto order5 = new OrderWideListDto();
        order5.setId(5);
        order5.setOperationType(OperationType.SELL.name());
        order5.setOperationTypeEnum(OperationType.SELL);
        order5.setAmountBase("0.032495305");
        order5.setExExchangeRate("6741.00");
        order5.setCommissionFixedAmount("0.438101702");
        order5.setAmountWithCommission("219.488952707");
        order5.setOrderBaseType(OrderBaseType.LIMIT);
        order5.setStatus(OrderStatus.OPENED);
        order5.setStatusString(OrderStatus.OPENED.toString());
        order5.setCurrencyPairId(1);
        order5.setCurrencyPairName("BTC/USD");
        order5.setDateCreation(LocalDateTime.now().minusHours(2));

        OrderWideListDto order6 = new OrderWideListDto();
        order6.setId(6);
        order6.setOperationType(OperationType.SELL.name());
        order6.setOperationTypeEnum(OperationType.SELL);
        order6.setAmountBase("0.002");
        order6.setExExchangeRate("5583.00");
        order6.setCommissionFixedAmount("0.022332000");
        order6.setAmountWithCommission("11.188332000");
        order6.setOrderBaseType(OrderBaseType.LIMIT);
        order6.setStatus(OrderStatus.OPENED);
        order6.setStatusString(OrderStatus.OPENED.toString());
        order6.setCurrencyPairId(2);
        order6.setCurrencyPairName("BTC/EUR");
        order6.setDateCreation(LocalDateTime.now().minusHours(1).plusMinutes(5));

        OrderWideListDto order7 = new OrderWideListDto();
        order7.setId(7);
        order7.setOperationType(OperationType.SELL.name());
        order7.setOperationTypeEnum(OperationType.SELL);
        order7.setAmountBase("0.010014457");
        order7.setExExchangeRate("5735.93187003");
        order7.setCommissionFixedAmount("0.114884486");
        order7.setAmountWithCommission("57.557127553");
        order7.setOrderBaseType(OrderBaseType.LIMIT);
        order7.setStatus(OrderStatus.OPENED);
        order7.setStatusString(OrderStatus.OPENED.toString());
        order7.setCurrencyPairId(2);
        order7.setCurrencyPairName("BTC/EUR");
        order7.setDateCreation(LocalDateTime.now().minusHours(1).plusMinutes(8));

        OrderWideListDto order8 = new OrderWideListDto();
        order8.setId(8);
        order8.setOperationType(OperationType.SELL.name());
        order8.setOperationTypeEnum(OperationType.SELL);
        order8.setAmountBase("1.892767528");
        order8.setExExchangeRate("0.03");
        order8.setCommissionFixedAmount("0.000113566");
        order8.setAmountWithCommission("0.056896592");
        order8.setOrderBaseType(OrderBaseType.LIMIT);
        order8.setStatus(OrderStatus.OPENED);
        order8.setStatusString(OrderStatus.OPENED.toString());
        order8.setCurrencyPairId(41);
        order8.setCurrencyPairName("ETH/BTC");
        order8.setDateCreation(LocalDateTime.now().minusHours(1).plusMinutes(22));

        OrderWideListDto order9 = new OrderWideListDto();
        order9.setId(9);
        order9.setOperationType(OperationType.SELL.name());
        order9.setOperationTypeEnum(OperationType.SELL);
        order9.setAmountBase("5.487500183");
        order9.setExExchangeRate("0.03150964");
        order9.setCommissionFixedAmount("0.000345818");
        order9.setAmountWithCommission("0.173254973");
        order9.setOrderBaseType(OrderBaseType.LIMIT);
        order9.setStatus(OrderStatus.OPENED);
        order9.setStatusString(OrderStatus.OPENED.toString());
        order9.setCurrencyPairId(41);
        order9.setCurrencyPairName("ETH/BTC");
        order9.setDateCreation(LocalDateTime.now().minusHours(1).plusMinutes(33));

        OrderWideListDto order10 = new OrderWideListDto();
        order10.setId(10);
        order10.setOperationType(OperationType.BUY.name());
        order10.setOperationTypeEnum(OperationType.BUY);
        order10.setAmountBase("1.341470383");
        order10.setExExchangeRate("0.029");
        order10.setCommissionFixedAmount("0.000077805");
        order10.setAmountWithCommission("0.038980446");
        order10.setOrderBaseType(OrderBaseType.LIMIT);
        order10.setStatus(OrderStatus.OPENED);
        order10.setStatusString(OrderStatus.OPENED.toString());
        order10.setCurrencyPairId(41);
        order10.setCurrencyPairName("ETH/BTC");
        order10.setDateCreation(LocalDateTime.now().minusHours(2).minusMinutes(2));

        return null;
    }

    public ExOrder mockOrderFromInputOrderDTO(InputCreateOrderDto inputOrder) {
        ExOrder exOrder = new ExOrder();
        exOrder.setId(Integer.parseInt(RandomStringUtils.randomNumeric(8)));
        exOrder.setAmountBase(inputOrder.getAmount());
        exOrder.setOperationType(OperationType.valueOf(inputOrder.getOrderType()));
        exOrder.setOrderBaseType(OrderBaseType.convert(inputOrder.getBaseType()));
        exOrder.setCommissionFixedAmount(inputOrder.getCommission());
        exOrder.setCurrencyPairId(inputOrder.getCurrencyPairId());
        exOrder.setCurrencyPair(currencyService.findCurrencyPairById(inputOrder.getCurrencyPairId()));
        exOrder.setExRate(inputOrder.getRate());
        exOrder.setCommissionFixedAmount(inputOrder.getTotal());
        return exOrder;
    }

}
