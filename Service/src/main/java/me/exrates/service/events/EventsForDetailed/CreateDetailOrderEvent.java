package me.exrates.service.events.EventsForDetailed;

import me.exrates.model.ExOrder;
import me.exrates.model.enums.OrderEventEnum;

public class CreateDetailOrderEvent extends DetailOrderEvent {

    public CreateDetailOrderEvent(ExOrder source, int pairId) {
        super(source, pairId);
        setOrderEventEnum(OrderEventEnum.CREATE);
    }
}
