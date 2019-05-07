package me.exrates.service.events.EventsForDetailed;

import me.exrates.model.ExOrder;
import me.exrates.model.enums.OrderEventEnum;

public class AcceptDetailOrderEvent extends DetailOrderEvent {

    public AcceptDetailOrderEvent(ExOrder source, int pairId) {
        super(source, pairId);
        setOrderEventEnum(OrderEventEnum.ACCEPT);
    }
}
