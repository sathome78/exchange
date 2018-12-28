package me.exrates.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Data
@Builder(builderClassName = "Builder")
@AllArgsConstructor
@NoArgsConstructor
public class WalletBalancesDto {

    private int currencyId;
    private String currencyName;
    private ExternalWalletBalancesDto external;
    private List<InternalWalletBalancesDto> internals;
    private CurrencyRateDto rate;

    public static WalletBalancesDto buildForHiddenCurrency(int currencyId, String currencyName) {
        return WalletBalancesDto
                .builder()
                .currencyId(currencyId)
                .currencyName(currencyName)
                .external(ExternalWalletBalancesDto.getZeroBalances(currencyId, currencyName))
                .rate(CurrencyRateDto.zeroRates())
                .internals(Collections.emptyList())
                .build();
    }
}
