package me.exrates.model.dto.mobileApiDto;

import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotNull;

@Data
@NoArgsConstructor
@Getter @Setter
public class UserAuthenticationDto {

    @NotNull(message = "Email is missing")
    private String email;
    @NotNull(message = "Password is missing")
    private String password;
    private String appKey;

    private String clientIp;
    private String pin;
    private boolean isPinRequired;
    private int tries;
}
