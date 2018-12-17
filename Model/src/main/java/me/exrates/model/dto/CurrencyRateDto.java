package me.exrates.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@Builder
@NoArgsConstructor
public class CurrencyRateDto {

    private BigDecimal usdRate;
    private BigDecimal btcRate;

    public static CurrencyRateDto zeroRates() {
        return CurrencyRateDto
                .builder()
                .btcRate(BigDecimal.ZERO)
                .usdRate(BigDecimal.ZERO)
                .build();
    }
}