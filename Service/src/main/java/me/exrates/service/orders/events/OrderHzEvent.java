package me.exrates.service.orders.events;

import me.exrates.model.enums.OrderEventEnum;
import me.exrates.model.newOrders.Order;
import org.springframework.context.ApplicationEvent;

public abstract class OrderHzEvent extends ApplicationEvent {

    private Long eventTimestamp;
    private OrderEventEnum orderEventEnum;

    public OrderHzEvent(Order source, long eventTimestamp, OrderEventEnum eventEnum) {
        super(source);
        this.eventTimestamp = eventTimestamp;
        this.orderEventEnum = eventEnum;
    }

    public OrderEventEnum getOrderEventEnum() {
        return orderEventEnum;
    }

    public Long getEventTimestamp() {
        return eventTimestamp;
    }
}
