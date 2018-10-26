package me.exrates.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder(builderClassName = "Builder")
@AllArgsConstructor
@NoArgsConstructor
public class ExternalReservedWalletAddressDto {

    @JsonProperty("currency_id")
    private Integer currencyId;
    @JsonProperty("wallet_address")
    private String walletAddress;
    private BigDecimal balance;
}
