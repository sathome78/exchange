package me.exrates.model.dto.ieo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class ClaimDto {

    private int id;
    private String email;
    private String uuid;

    @NotNull
    private String currencyName;
    @NotNull
    private BigDecimal amount;
    private boolean test;
}
