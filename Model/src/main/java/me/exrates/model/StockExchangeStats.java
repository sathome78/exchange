package me.exrates.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import me.exrates.model.dto.CoinmarketcapApiDto;
import me.exrates.model.serializer.LocalDateTimeToLongSerializer;
import me.exrates.model.serializer.StockExchangeSerializer;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder(builderClassName = "Builder", toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class StockExchangeStats {

    @JsonIgnore
    private Long id;
    @JsonIgnore
    private Integer currencyPairId;
    @JsonProperty("stockExchange")
    @JsonSerialize(using = StockExchangeSerializer.class)
    private StockExchange stockExchange;
    @JsonProperty("last")
    private BigDecimal priceLast;
    @JsonProperty("buy")
    private BigDecimal priceBuy;
    @JsonProperty("sell")
    private BigDecimal priceSell;
    @JsonProperty("low")
    private BigDecimal priceLow;
    @JsonProperty("high")
    private BigDecimal priceHigh;
    @JsonProperty("volume")
    private BigDecimal volume;
    @JsonProperty("timestamp")
    @JsonSerialize(using = LocalDateTimeToLongSerializer.class)
    private LocalDateTime date;

    public StockExchangeStats(CoinmarketcapApiDto dto, StockExchange stockExchange) {
        this.currencyPairId = dto.getCurrencyPairId();
        this.stockExchange = stockExchange;
        this.priceLast = dto.getLast();
        this.priceBuy = dto.getHighestBid();
        this.priceSell = dto.getLowestAsk();
        this.priceLow = dto.getHigh24hr();
        this.priceHigh = dto.getLow24hr();
        this.volume = dto.getBaseVolume();
        this.date = LocalDateTime.now();
    }

    @Override
    public String toString() {
        return "StockExchangeStats{" +
                "id=" + id +
                ", currencyPairId=" + currencyPairId +
                ", stockExchange=" + stockExchange +
                ", priceLast=" + priceLast +
                ", priceBuy=" + priceBuy +
                ", priceSell=" + priceSell +
                ", priceLow=" + priceLow +
                ", priceHigh=" + priceHigh +
                ", volume=" + volume +
                ", date=" + date +
                '}';
    }
}