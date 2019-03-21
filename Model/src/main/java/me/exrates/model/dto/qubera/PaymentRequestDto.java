package me.exrates.model.dto.qubera;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class PaymentRequestDto {

    @NotNull(message = "Amount must be not null")
    private BigDecimal amount;

    @NotNull(message = "Currency code must be not null and valid")
    @Min(value = 3, message = "Minimum chars is 3")
    @Max(value = 3, message = "Maximum chars is 3")
    private String currencyCode;
}
