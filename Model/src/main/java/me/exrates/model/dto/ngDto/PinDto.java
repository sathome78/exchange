package me.exrates.model.dto.ngDto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotNull;

/**
 * Created by Maks on 18.05.2018.
 */
@Getter@Setter
@NoArgsConstructor
public class PinDto {
    @NotNull
    private String key;
    @NotNull
    private String pin;
}
