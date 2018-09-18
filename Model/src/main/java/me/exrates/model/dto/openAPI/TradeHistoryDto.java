package me.exrates.model.dto.openAPI;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import me.exrates.model.enums.OrderType;
import me.exrates.model.serializer.BigDecimalToDoubleSerializer;
import me.exrates.model.serializer.LocalDateTimeToLongSerializer;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter(AccessLevel.PRIVATE)
@Builder
@AllArgsConstructor
public class TradeHistoryDto {

    @JsonProperty("order_id")
    private Integer orderId;

    @JsonProperty("currency_pair")
    private String currencyPair;

    @JsonProperty("date_acceptance")
    @JsonSerialize(using = LocalDateTimeToLongSerializer.class)
    private LocalDateTime dateAcceptance;

    @JsonProperty("date_creation")
    @JsonSerialize(using = LocalDateTimeToLongSerializer.class)
    private LocalDateTime dateCreation;

    @JsonSerialize(using = BigDecimalToDoubleSerializer.class)
    private BigDecimal amount;

    @JsonSerialize(using = BigDecimalToDoubleSerializer.class)
    private BigDecimal price;

    @JsonSerialize(using = BigDecimalToDoubleSerializer.class)
    private BigDecimal total;

    @JsonProperty("order_type")
    private OrderType orderType;
}
