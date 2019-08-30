package me.exrates.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import me.exrates.model.enums.CurrencyPairType;

import java.io.Serializable;
import java.math.BigDecimal;


@Getter
@Setter
@ToString
@NoArgsConstructor
@EqualsAndHashCode
@JsonIgnoreProperties(ignoreUnknown = true)
public class CurrencyPair implements Serializable {
    private int id;
    private String name;
    private Currency currency1;
    private Currency currency2;
    private String market;
    private String marketName;
    private CurrencyPairType pairType;
    private boolean hidden;
    private boolean permittedLink;
    private Boolean isTopMarket;
    private BigDecimal topMarketVolume;

    public CurrencyPair(Currency currency1, Currency currency2) {
        this.currency1 = currency1;
        this.currency2 = currency2;
    }

    public CurrencyPair(CurrencyPair currencyPair) {
        this.id = currencyPair.getId();
        this.name = currencyPair.getName();
        this.currency1 = currencyPair.getCurrency1();
        this.currency2 = currencyPair.getCurrency2();
        this.market = currencyPair.getMarket();
        this.marketName = currencyPair.getMarketName();
        this.pairType = currencyPair.getPairType();
        this.hidden = currencyPair.isHidden();
        this.permittedLink = currencyPair.isPermittedLink();
        this.isTopMarket = currencyPair.getIsTopMarket();
        this.topMarketVolume = currencyPair.getTopMarketVolume();
    }

    public CurrencyPair(String currencyPairName) {
        this.name = currencyPairName;
    }

    /*service methods*/
    public Currency getAnotherCurrency(Currency currency) {
        return currency.equals(currency1) ? currency2 : currency1;
    }


}
