package me.exrates.model.dto.merchants.omni;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class OmniWalletInfoDto {

    private BigDecimal balance;
    private BigDecimal reserved;
    private BigDecimal frozen;
}
