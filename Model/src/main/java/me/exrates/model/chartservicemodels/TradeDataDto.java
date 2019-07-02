package me.exrates.model.chartservicemodels;

import lombok.Data;
import me.exrates.model.ExOrder;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class TradeDataDto {

    private int orderId;
    private String pairName;
    private BigDecimal exrate;
    private BigDecimal amountBase;
    private BigDecimal amountConvert;
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
