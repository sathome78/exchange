package me.exrates.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

import static java.util.Objects.isNull;

@Data
@Builder(builderClassName = "Builder", toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class UserSummaryOrdersDto {

    private String email;
    private String role;
    private String currencyName;
    private String currencyPairName;
    private BigDecimal amount;
    private BigDecimal commission;

    public Boolean isEmpty() {
        return (isNull(amount) || amount.compareTo(BigDecimal.ZERO) == 0)
                && (isNull(commission) || commission.compareTo(BigDecimal.ZERO) == 0);
    }
}
