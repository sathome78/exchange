package me.exrates.service;

import me.exrates.model.CurrencyPair;
import me.exrates.model.dto.InputCreateOrderDto;
import me.exrates.model.dto.OrderCreateDto;
import me.exrates.model.dto.OrderCreateSummaryDto;
import me.exrates.model.enums.OperationType;
import me.exrates.model.enums.OrderBaseType;
import java.math.BigDecimal;

public interface OrderServiceDemoListener {

    OrderCreateSummaryDto newOrderToSell(OperationType orderType,
                                         int userId,
                                         BigDecimal amount,
                                         BigDecimal rate,
                                         OrderBaseType baseType,
                                         int currencyPair,
                                         BigDecimal stop);

    String recordOrderToDB(InputCreateOrderDto order, OrderCreateDto orderCreateDto);
}
