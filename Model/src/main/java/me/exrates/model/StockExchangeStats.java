package me.exrates.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import me.exrates.model.serializer.LocalDateTimeToLongSerializer;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Created by OLEG on 14.12.2016.
 */
public class StockExchangeStats {

    @JsonIgnore
    private Long id;
    @JsonIgnore
    private Integer currencyPairId;
    @JsonIgnore
    private Integer stockExchangeId;

    @JsonProperty(value = "buy")
    private BigDecimal priceBuy;

    @JsonProperty(value = "sell")
    private BigDecimal priceSell;

    @JsonProperty(value = "low")
    private BigDecimal priceLow;

    @JsonProperty(value = "high")
    private BigDecimal priceHigh;

    @JsonProperty(value = "volume")
    private BigDecimal volume;

    @JsonProperty(value = "timestamp")
    @JsonSerialize(using = LocalDateTimeToLongSerializer.class)
    private LocalDateTime date;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getCurrencyPairId() {
        return currencyPairId;
    }

    public void setCurrencyPairId(Integer currencyPairId) {
        this.currencyPairId = currencyPairId;
    }

    public Integer getStockExchangeId() {
        return stockExchangeId;
    }

    public void setStockExchangeId(Integer stockExchangeIn) {
        this.stockExchangeId = stockExchangeIn;
    }

    public BigDecimal getPriceBuy() {
        return priceBuy;
    }

    public void setPriceBuy(BigDecimal priceBuy) {
        this.priceBuy = priceBuy;
    }

    public BigDecimal getPriceSell() {
        return priceSell;
    }

    public void setPriceSell(BigDecimal priceSell) {
        this.priceSell = priceSell;
    }

    public BigDecimal getPriceLow() {
        return priceLow;
    }

    public void setPriceLow(BigDecimal priceLow) {
        this.priceLow = priceLow;
    }

    public BigDecimal getPriceHigh() {
        return priceHigh;
    }

    public void setPriceHigh(BigDecimal priceHigh) {
        this.priceHigh = priceHigh;
    }

    public BigDecimal getVolume() {
        return volume;
    }

    public void setVolume(BigDecimal volume) {
        this.volume = volume;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }

    @Override
    public String toString() {
        return "StockExchangeStats{" +
                "id=" + id +
                ", currencyPairId=" + currencyPairId +
                ", stockExchangeId=" + stockExchangeId +
                ", priceBuy=" + priceBuy +
                ", priceSell=" + priceSell +
                ", priceLow=" + priceLow +
                ", priceHigh=" + priceHigh +
                ", volume=" + volume +
                ", date=" + date +
                '}';
    }
}
