package me.exrates.model.dto.openAPI;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import me.exrates.model.dto.CoinmarketcapApiDto;
import me.exrates.model.serializer.BigDecimalToDoubleSerializer;

import java.math.BigDecimal;

@Data
@Builder(builderClassName = "Builder", toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class TickerJsonDto {

    private Integer id;
    private String name;
    @JsonSerialize(using = BigDecimalToDoubleSerializer.class)
    private BigDecimal last;
    @JsonSerialize(using = BigDecimalToDoubleSerializer.class)
    private BigDecimal lowestAsk;
    @JsonSerialize(using = BigDecimalToDoubleSerializer.class)
    private BigDecimal highestBid;
    @JsonSerialize(using = BigDecimalToDoubleSerializer.class)
    private BigDecimal percentChange;
    @JsonSerialize(using = BigDecimalToDoubleSerializer.class)
    private BigDecimal baseVolume;
    @JsonSerialize(using = BigDecimalToDoubleSerializer.class)
    private BigDecimal quoteVolume;
    @JsonSerialize(using = BigDecimalToDoubleSerializer.class)
    private BigDecimal high;
    @JsonSerialize(using = BigDecimalToDoubleSerializer.class)
    private BigDecimal low;

    public TickerJsonDto(CoinmarketcapApiDto coinmarketcapApiDto) {
        this.id = coinmarketcapApiDto.getCurrencyPairId();
        this.name = coinmarketcapApiDto.getCurrencyPairName().replace('/', '_');
        this.last = coinmarketcapApiDto.getLast();
        this.lowestAsk = coinmarketcapApiDto.getLowestAsk();
        this.highestBid = coinmarketcapApiDto.getHighestBid();
        this.percentChange = coinmarketcapApiDto.getPercentChange();
        this.baseVolume = coinmarketcapApiDto.getBaseVolume();
        this.quoteVolume = coinmarketcapApiDto.getQuoteVolume();
        this.high = coinmarketcapApiDto.getHigh24hr();
        this.low = coinmarketcapApiDto.getLow24hr();
    }
}