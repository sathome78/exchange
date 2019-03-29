package me.exrates.model.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class PinOrderInfoDto {

    @NotNull
    private String currencyName;

    @NotNull
    private BigDecimal amount;
}
