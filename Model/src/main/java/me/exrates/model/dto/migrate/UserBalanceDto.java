package me.exrates.model.dto.migrate;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(builderClassName = "Builder")
public class UserBalanceDto {

    private String currencyName;
    private BigDecimal balance;

    public static UserBalanceDto zeroBalance(String currencyName) {
        return UserBalanceDto.builder()
                .currencyName(currencyName)
                .balance(BigDecimal.ZERO)
                .build();
    }
}