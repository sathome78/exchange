package me.exrates.model.dto.ieo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ClaimDto {

    private int id;

    @NotNull
    private String currencyName;
    @NotNull
    private BigDecimal amount;
}
