package me.exrates.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import me.exrates.model.enums.CurrencyPairRestrictionsEnum;
import org.apache.commons.collections4.CollectionUtils;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class CurrencyPairWithRestriction extends CurrencyPair {

    private List<CurrencyPairRestrictionsEnum> tradeRestriction;

    public boolean hasTradeRestriction() {
        return !CollectionUtils.isEmpty(tradeRestriction);
    }

    public CurrencyPairWithRestriction(CurrencyPair currencyPair) {
        super(currencyPair);
    }
}
