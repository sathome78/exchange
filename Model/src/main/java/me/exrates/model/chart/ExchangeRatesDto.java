package me.exrates.model.chart;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import me.exrates.model.dto.onlineTableDto.ExOrderStatisticsShortByPairsDto;
import me.exrates.model.enums.CurrencyPairType;

@Data
@Builder(builderClassName = "Builder", toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class ExchangeRatesDto {

    @JsonProperty("currency_pair_id")
    private Integer currencyPairId;
    @JsonProperty("currency_pair_name")
    private String currencyPairName;
    @JsonProperty("currency_pair_precision")
    private Integer currencyPairPrecision;
    @JsonProperty("last_order_rate")
    private String lastOrderRate;
    @JsonProperty("pred_last_order_rate")
    private String predLastOrderRate;
    @JsonProperty("percent_change")
    private String percentChange;
    @JsonProperty("value_change")
    private String valueChange;
    private String market;
    private String type;
    private String volume;
    @JsonProperty("currency_volume")
    private String currencyVolume;
    @JsonProperty("high_24hr")
    private String high24hr;
    @JsonProperty("low_24hr")
    private String low24hr;
    @JsonProperty("last_order_rate_24hr")
    private String lastOrderRate24hr;
    private boolean hidden;
    @JsonProperty("is_top_market")
    private boolean isTopMarket;

    public ExOrderStatisticsShortByPairsDto transform() {
        return ExOrderStatisticsShortByPairsDto.builder()
                .currencyPairId(this.currencyPairId)
                .currencyPairName(this.currencyPairName)
                .currencyPairPrecision(this.currencyPairPrecision)
                .lastOrderRate(this.lastOrderRate)
                .predLastOrderRate(this.predLastOrderRate)
                .percentChange(this.percentChange)
                .valueChange(this.valueChange)
                .market(this.market)
                .type(CurrencyPairType.getType(this.type))
                .volume(this.volume)
                .currencyVolume(this.currencyVolume)
                .high24hr(this.high24hr)
                .low24hr(this.low24hr)
                .lastOrderRate24hr(this.lastOrderRate24hr)
                .hidden(this.hidden)
                .isTopMarket(this.isTopMarket)
                .build();
    }
}