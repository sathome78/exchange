package me.exrates.model.dto;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import me.exrates.model.serializer.BigDecimalNonePointSerializer;

import java.math.BigDecimal;

@Data
@Builder(builderClassName = "Builder", toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class CoinmarketApiJsonDto {

    private Integer id;
    @JsonSerialize(using = BigDecimalNonePointSerializer.class)
    private BigDecimal last;
    @JsonSerialize(using = BigDecimalNonePointSerializer.class)
    private BigDecimal lowestAsk;
    @JsonSerialize(using = BigDecimalNonePointSerializer.class)
    private BigDecimal highestBid;
    @JsonSerialize(using = BigDecimalNonePointSerializer.class)
    private BigDecimal percentChange;
    @JsonSerialize(using = BigDecimalNonePointSerializer.class)
    private BigDecimal baseVolume;
    @JsonSerialize(using = BigDecimalNonePointSerializer.class)
    private BigDecimal quoteVolume;
    @JsonSerialize(using = ToStringSerializer.class)
    private Integer isFrozen;
    @JsonSerialize(using = BigDecimalNonePointSerializer.class)
    private BigDecimal high24hr;
    @JsonSerialize(using = BigDecimalNonePointSerializer.class)
    private BigDecimal low24hr;

    public CoinmarketApiJsonDto(CoinmarketcapApiDto coinmarketcapApiDto) {
        this.id = coinmarketcapApiDto.getCurrencyPairId();
        this.last = coinmarketcapApiDto.getLast();
        this.lowestAsk = coinmarketcapApiDto.getLowestAsk();
        this.highestBid = coinmarketcapApiDto.getHighestBid();
        this.percentChange = coinmarketcapApiDto.getPercentChange();
        this.baseVolume = coinmarketcapApiDto.getBaseVolume();
        this.quoteVolume = coinmarketcapApiDto.getQuoteVolume();
        this.isFrozen = coinmarketcapApiDto.getIsFrozen();
        this.high24hr = coinmarketcapApiDto.getHigh24hr();
        this.low24hr = coinmarketcapApiDto.getLow24hr();
    }
}