package me.exrates.model.dto.mobileApiDto;

import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotNull;

/**
 * Created by OLEG on 19.08.2016.
 */
@Data
@NoArgsConstructor
@Getter @Setter
public class UserAuthenticationDto {

    @NotNull(message = "Email is missing")
    private String email;
    @NotNull(message = "Password is missing")
    private String password;
    @NotNull(message = "Application key is missing")
    private String appKey;

    private String clientIp;
    private String pin;
    private boolean isPinRequired;
    private int tries;
}
