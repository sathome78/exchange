package me.exrates.service.events.EventsForDetailed;

import me.exrates.model.enums.OrderEventEnum;
import me.exrates.service.events.OrderEvent;
import org.springframework.context.ApplicationEvent;

import java.util.List;

public class AutoAcceptEventsList extends DetailOrderEvent {

    public AutoAcceptEventsList(List<OrderEvent> source, int pairId) {
        super(source, pairId);
        setOrderEventEnum(OrderEventEnum.AUTO_ACCEPT);
    }

    private OrderEventEnum orderEventEnum;

    public OrderEventEnum getOrderEventEnum() {
        return orderEventEnum;
    }

    public void setOrderEventEnum(OrderEventEnum orderEventEnum) {
        this.orderEventEnum = orderEventEnum;
    }
}
