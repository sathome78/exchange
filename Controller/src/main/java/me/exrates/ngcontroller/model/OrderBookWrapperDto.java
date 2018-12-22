package me.exrates.ngcontroller.model;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Builder;
import lombok.Data;
import me.exrates.model.enums.OrderType;
import me.exrates.model.util.BigDecimalToStringSerializer;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
public class OrderBookWrapperDto {

    private OrderType orderType;
    private String lastExrate;
    private String preLastExrate;
    private boolean positive;
    @JsonSerialize(using = BigDecimalToStringSerializer.class)
    private BigDecimal total;
    private List<SimpleOrderBookItem> orderBookItems;
}
