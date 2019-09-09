package me.exrates.model.dto.freecoins;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.math.BigDecimal;

@Data
@Builder(builderClassName = "Builder")
@NoArgsConstructor
@AllArgsConstructor
public class GiveawayClaimRequest {

    @NotNull
    @JsonProperty("currency_name")
    private String currencyName;
    @NotNull
    private BigDecimal amount;
    @NotNull
    @JsonProperty("partial_amount")
    private BigDecimal partialAmount;
    @JsonProperty("single")
    private boolean isSingle;
    @Min(1)
    @Max(10080)
    @JsonProperty("time_range")
    private Integer timeRange;
    @NotEmpty
    @Size(min = 6, max = 8)
    private String pin;
}