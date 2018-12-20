package me.exrates.service.events;

import lombok.Data;
import me.exrates.model.ExOrder;
import me.exrates.model.dto.OrderCreateDto;

import java.math.BigDecimal;

@Data
public class PartiallyAcceptedOrder extends OrderEvent {

    public PartiallyAcceptedOrder(ExOrder order) {
        super(order);
    }

}
