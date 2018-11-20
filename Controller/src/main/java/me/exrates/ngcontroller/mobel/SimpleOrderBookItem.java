package me.exrates.ngcontroller.mobel;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Builder;
import lombok.Data;
import me.exrates.model.enums.OrderType;
import me.exrates.model.serializer.BigDecimalToDoubleSerializer;

import java.math.BigDecimal;

@Data
@Builder
public class SimpleOrderBookItem {

    private Integer currencyPairId;

    private OrderType orderType;

    @JsonSerialize(using = BigDecimalToDoubleSerializer.class)
    private BigDecimal exrate;

    @JsonSerialize(using = BigDecimalToDoubleSerializer.class)
    private BigDecimal amount;
}
