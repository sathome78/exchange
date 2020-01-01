package me.exrates.model.dto.migrate;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(builderClassName = "Builder")
public class ExtendedUserDto {

    private String email;
    private String encodedPassword;
    private boolean enabled2fa;
    private String secret2fa;

    private List<UserBalanceDto> balances;
}