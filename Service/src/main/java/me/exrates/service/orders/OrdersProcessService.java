package me.exrates.service.orders;

import me.exrates.model.dto.OrderCreateDto;
import me.exrates.model.enums.OrderActionEnum;
import me.exrates.model.newOrders.TransactionDto;
import org.springframework.transaction.annotation.Transactional;

public interface OrdersProcessService {
    @Transactional(rollbackFor = {Exception.class})
    TransactionDto createOrder(OrderCreateDto orderCreateDto, OrderActionEnum action);
}
