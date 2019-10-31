package me.exrates.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import me.exrates.model.enums.RestrictedOperation;
import org.apache.commons.collections4.CollectionUtils;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class CurrencyPairWithRestriction extends CurrencyPair {

    private List<CurrencyPairRestrictionsEnum> tradeRestrictions;

    public CurrencyPairWithRestriction(String pairName) {
        super(pairName);
    }

    public boolean hasTradeRestriction() {
        return !CollectionUtils.isEmpty(tradeRestrictions);
    }

    public CurrencyPairWithRestriction(CurrencyPair currencyPair) {
        super(currencyPair);
    }
}
