package me.exrates.service.orders;

import me.exrates.model.dto.OrderCreateDto;
import me.exrates.model.enums.OrderActionEnum;
import org.springframework.transaction.annotation.Transactional;

public interface OrdersProcessService {
    @Transactional(rollbackFor = {Exception.class})
    int createOrder(OrderCreateDto orderCreateDto, OrderActionEnum action);
}
