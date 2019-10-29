package me.exrates.service.chart;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import me.exrates.model.ExOrder;
import me.exrates.model.serializer.LocalDateTimeDeserializer;
import me.exrates.model.serializer.LocalDateTimeSerializer;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;

@Data
@Builder(builderClassName = "Builder")
@NoArgsConstructor
@AllArgsConstructor
public class OrderDataDto {

    private String currencyPairName;
    private BigDecimal exrate;
    private BigDecimal amountBase;
    private BigDecimal amountConvert;
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    private LocalDateTime tradeDate;
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    private LocalDateTime createDate;
    private int statusId;
    private int operationTypeId;

    public OrderDataDto(ExOrder order) {
        this.currencyPairName = order.getCurrencyPair().getName();
        this.exrate = order.getExRate();
        this.amountBase = order.getAmountBase();
        this.amountConvert = order.getAmountConvert();
        this.tradeDate = order.getDateAcception();
        this.createDate = Objects.nonNull(order.getDateCreation())
                ? order.getDateCreation()
                : LocalDateTime.now();
        this.statusId = order.getStatus().getStatus();
        this.operationTypeId = order.getOperationType().getType();
    }
}