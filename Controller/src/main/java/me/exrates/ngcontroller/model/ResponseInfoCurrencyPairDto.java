package me.exrates.ngcontroller.model;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

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
