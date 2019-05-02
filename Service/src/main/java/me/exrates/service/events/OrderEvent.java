package me.exrates.service.events;

import me.exrates.model.ExOrder;
import me.exrates.model.enums.OrderEventEnum;

/**
 * Created by Maks on 30.08.2017.
 */
public class OrderEvent extends ApplicationEventWithProcessId {

    /**
     * Create a new ApplicationEvent.
     *
     * @param source the object on which the event initially occurred (never {@code null})
     */
    public OrderEvent(ExOrder source) {
        super(source);
        source.setEventTimestamp(getTimestamp());
    }

    private OrderEventEnum orderEventEnum;

    public OrderEventEnum getOrderEventEnum() {
        return orderEventEnum;
    }

    public void setOrderEventEnum(OrderEventEnum orderEventEnum) {
        this.orderEventEnum = orderEventEnum;
    }


}
