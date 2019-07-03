package me.exrates.model.chartservicemodels;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Data;
import me.exrates.model.ExOrder;
import me.exrates.model.serializer.LocalDateDeserializer;
import me.exrates.model.serializer.LocalDateTimeSerializer;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class TradeDataDto {

    private int orderId;
    private String pairName;
    private BigDecimal exrate;
    private BigDecimal amountBase;
    private BigDecimal amountConvert;
    @JsonDeserialize(using = LocalDateDeserializer.class)
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    private LocalDateTime tradeDate;

    public TradeDataDto(ExOrder exOrder) {
        this.orderId = exOrder.getId();
        this.pairName = exOrder.getCurrencyPair().getName();
        this.exrate = exOrder.getExRate();
        this.amountBase = exOrder.getAmountBase();
        this.amountConvert = exOrder.getAmountConvert();
        this.tradeDate = exOrder.getDateAcception();
    }
}
