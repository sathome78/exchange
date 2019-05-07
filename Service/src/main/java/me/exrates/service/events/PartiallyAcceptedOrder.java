package me.exrates.service.events;

import lombok.Data;
import me.exrates.model.ExOrder;

@Data
public class PartiallyAcceptedOrder extends OrderEvent {

    public PartiallyAcceptedOrder(ExOrder order) {
        super(order);
    }

}
