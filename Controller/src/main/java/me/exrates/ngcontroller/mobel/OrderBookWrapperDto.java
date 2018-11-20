package me.exrates.ngcontroller.mobel;

import lombok.Builder;
import lombok.Data;
import me.exrates.model.enums.OrderType;

import java.util.List;

@Data
@Builder
public class OrderBookWrapperDto {

    private OrderType orderType;
    private String lastExrate;
    private boolean positive;
    private List<SimpleOrderBookItem> orderBookItems;
}
