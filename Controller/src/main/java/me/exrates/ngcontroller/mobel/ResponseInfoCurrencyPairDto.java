package me.exrates.ngcontroller.mobel;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import me.exrates.model.StockExchangeStats;
import me.exrates.model.dto.CoinmarketApiDto;

import java.math.BigDecimal;
import java.util.List;

@Data
@Getter
@Setter
public class ResponseInfoCurrencyPairDto {

    private String currencyRate;
    private String percentChange;
    private String changedValue;
    private String lastCurrencyRate;
    private String volume24h;
    private String rateHigh;
    private String rateLow;
}
