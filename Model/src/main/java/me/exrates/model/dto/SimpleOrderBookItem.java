package me.exrates.model.dto;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Builder;
import lombok.Data;
import me.exrates.model.enums.OrderType;
import me.exrates.model.util.BigDecimalToStringSerializer;

import java.math.BigDecimal;

@Data
@Builder
public class SimpleOrderBookItem {

    private Integer currencyPairId;

    private OrderType orderType;

    private String currencyPairName;

    @JsonSerialize(using = BigDecimalToStringSerializer.class)
    private BigDecimal exrate;

    @JsonSerialize(using = BigDecimalToStringSerializer.class)
    private BigDecimal amount;

    @JsonSerialize(using = BigDecimalToStringSerializer.class)
    private BigDecimal total;

    @JsonSerialize(using = BigDecimalToStringSerializer.class)
    private BigDecimal sumAmount;
}