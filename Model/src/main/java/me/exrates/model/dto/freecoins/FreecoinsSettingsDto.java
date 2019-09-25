package me.exrates.model.dto.freecoins;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder(builderClassName = "Builder")
@NoArgsConstructor
@AllArgsConstructor
public class FreecoinsSettingsDto {

    @JsonProperty("currency_id")
    private int currencyId;
    @JsonProperty("currency_name")
    private String currencyName;
    @JsonProperty("min_amount")
    private BigDecimal minAmount;
    @JsonProperty("min_partial_amount")
    private BigDecimal minPartialAmount;
}