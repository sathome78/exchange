package me.exrates.service.events;

import me.exrates.model.ExOrder;
import me.exrates.model.enums.OrderEventEnum;

public class AcceptOrderEvent extends OrderEvent {
    /**
     * Create a new ApplicationEvent.
     *
     * @param source the object on which the event initially occurred (never {@code null})
     */
    public AcceptOrderEvent(ExOrder source) {
        super(source);
        setOrderEventEnum(OrderEventEnum.ACCEPT);
    }
}
