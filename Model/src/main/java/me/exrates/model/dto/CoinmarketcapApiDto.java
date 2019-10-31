package me.exrates.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import me.exrates.model.util.BigDecimalProcessing;

import java.math.BigDecimal;

@Data
@Builder(builderClassName = "Builder", toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class CoinmarketcapApiDto {

    @JsonProperty("currency_pair_id")
    private Integer currencyPairId;
    @JsonProperty("currency_pair_name")
    private String currencyPairName;
    private BigDecimal first;
    private BigDecimal last;
    @JsonProperty("base_volume")
    private BigDecimal baseVolume;
    @JsonProperty("quote_volume")
    private BigDecimal quoteVolume;
    @JsonProperty("high_24hr")
    private BigDecimal high24hr;
    @JsonProperty("low_24hr")
    private BigDecimal low24hr;
    @JsonProperty("highest_bid")
    private BigDecimal highestBid;
    @JsonProperty("lowest_ask")
    private BigDecimal lowestAsk;
    @JsonProperty("is_frozen")
    private Integer isFrozen;
    @JsonProperty("percent_change")
    private BigDecimal percentChange;

    @Override
    public String toString() {
        return '"' + currencyPairName.replace('/', '_') + "\":" +
                "{\"last\":" + BigDecimalProcessing.formatNonePointQuoted(last, true) +
                ", \"lowestAsk\":" + BigDecimalProcessing.formatNonePointQuoted(lowestAsk, true) +
                ", \"highestBid\":" + BigDecimalProcessing.formatNonePointQuoted(highestBid, true) +
                ", \"percentChange\":" + BigDecimalProcessing.formatNonePointQuoted(percentChange, true) +
                ", \"baseVolume\":" + BigDecimalProcessing.formatNonePointQuoted(baseVolume, true) +
                ", \"quoteVolume\":" + BigDecimalProcessing.formatNonePointQuoted(quoteVolume, true) +
                ", \"isFrozen\":" + '"' + isFrozen + '"' +
                ", \"high24hr\":" + BigDecimalProcessing.formatNonePointQuoted(high24hr, true) +
                ", \"low24hr\":" + BigDecimalProcessing.formatNonePointQuoted(low24hr, true) +
                '}';
    }
}