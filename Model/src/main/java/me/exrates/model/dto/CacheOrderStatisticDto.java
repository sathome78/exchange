package me.exrates.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import me.exrates.model.enums.CurrencyPairType;

import java.math.BigDecimal;

@Data
@Builder(builderClassName = "Builder", toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class CacheOrderStatisticDto {

    @JsonProperty("currency_pair_id")
    private Integer currencyPairId;
    @JsonProperty("currency_pair_name")
    private String currencyPairName;
    @JsonProperty("currency_pair_precision")
    private Integer currencyPairPrecision;
    @JsonProperty("currency_pair_type")
    private CurrencyPairType currencyPairType;
    @JsonProperty("last_order_rate")
    private BigDecimal lastOrderRate;
    @JsonProperty("pred_last_order_rate")
    private BigDecimal predLastOrderRate;
    @JsonProperty("percent_change")
    private BigDecimal percentChange;
    private String market;
    private BigDecimal volume;
    private BigDecimal currencyVolume;
    @JsonProperty("price_in_usd")
    private BigDecimal priceInUSD;
    @JsonProperty("high_24hr")
    private BigDecimal high24hr;
    @JsonProperty("low_24hr")
    private BigDecimal low24hr;
}