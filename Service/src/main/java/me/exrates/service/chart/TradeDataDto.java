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

@Data
@Builder(builderClassName = "Builder")
@NoArgsConstructor
@AllArgsConstructor
public class TradeDataDto {

    private int orderId;
    private String pairName;
    private BigDecimal exrate;
    private BigDecimal amountBase;
    private BigDecimal amountConvert;
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    private LocalDateTime tradeDate;


    public TradeDataDto(ExOrder order) {
        this.orderId = order.getId();
        this.exrate = order.getExRate();
        this.amountBase = order.getAmountBase();
        this.amountConvert = order.getAmountConvert();
        this.tradeDate = order.getDateAcception();
    }
}