package me.exrates.service.events.EventsForDetailed;

import me.exrates.model.ExOrder;
import me.exrates.model.enums.OrderEventEnum;
import me.exrates.service.events.OrderEvent;
import org.springframework.context.ApplicationEvent;

import java.util.List;

public class DetailOrderEvent extends ApplicationEvent {

    /**
     * Create a new ApplicationEvent.
     *
     * @param source the object on which the event initially occurred (never {@code null})
     *
     *
     */
    private int pairId;

    public DetailOrderEvent(ExOrder source, int pairId) {
        super(source);
        source.setEventTimestamp(getTimestamp());
        this.pairId = pairId;
    }

    public DetailOrderEvent(List<ExOrder> events, int pairId) {
        super(events);
        this.pairId = pairId;
    }

    private OrderEventEnum orderEventEnum;

    public OrderEventEnum getOrderEventEnum() {
        return orderEventEnum;
    }

    public void setOrderEventEnum(OrderEventEnum orderEventEnum) {
        this.orderEventEnum = orderEventEnum;
    }

    public int getPairId() {
        return pairId;
    }
}
