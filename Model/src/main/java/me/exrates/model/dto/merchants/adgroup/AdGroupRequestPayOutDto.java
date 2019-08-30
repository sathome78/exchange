package me.exrates.model.dto.merchants.adgroup;

import lombok.Builder;
import lombok.Data;
import lombok.ToString;

import java.math.BigDecimal;

@Data
@Builder
@ToString
public class AdGroupRequestPayOutDto {
    private BigDecimal amount;
    private String pin;
    private String address;
    private String platform;
    private String currency;
}
