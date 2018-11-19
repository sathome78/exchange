package me.exrates.model.dto;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Data
@Getter
@Setter
public class StatisticForMarket {

    public int currencyPairId;
    public String currencyPairName;
    private String market;
    private BigDecimal lastOrderRate;
    private BigDecimal predLastOrderRate;
    private BigDecimal volume;
    private String percentChange;
}
