package me.exrates.model.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import me.exrates.model.CurrencyPair;

@Getter
@Setter
@ToString
public class CurrentParams {
    private CurrencyPair currencyPair;
    private Boolean showAllPairs;
    private Boolean orderRoleFilterEnabled;
}