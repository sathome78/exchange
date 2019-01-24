package me.exrates.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;
import me.exrates.model.enums.CurrencyPairType;
import org.springframework.stereotype.Component;

@Component
@Getter @Setter @ToString
@NoArgsConstructor
@EqualsAndHashCode
@JsonIgnoreProperties(ignoreUnknown = true)
public class CurrencyPair {
    private int id;
    private String name;
    private Currency currency1;
    private Currency currency2;
    private String market;
    private String marketName;
    private CurrencyPairType pairType;
    private boolean hidden;
    private boolean permittedLink;

    public CurrencyPair(Currency currency1, Currency currency2) {
        this.currency1 = currency1;
        this.currency2 = currency2;
    }

    /*service methods*/
    public Currency getAnotherCurrency(Currency currency) {
        return currency.equals(currency1) ? currency2 : currency1;
    }
}
