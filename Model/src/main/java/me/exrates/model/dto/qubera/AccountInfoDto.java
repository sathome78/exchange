package me.exrates.model.dto.qubera;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AccountInfoDto {
    private String accountState;
    private QuberaBalanceDto availableBalance;
    private QuberaBalanceDto currentBalance;
}
