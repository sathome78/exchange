package me.exrates.service.orders.events;

import me.exrates.model.enums.OrderEventEnum;
import me.exrates.model.newOrders.Order;


public class CreateHzOrderEvent extends OrderHzEvent {

    public CreateHzOrderEvent(Order source) {
        super(source, System.currentTimeMillis(), OrderEventEnum.CREATE);
    }
}
