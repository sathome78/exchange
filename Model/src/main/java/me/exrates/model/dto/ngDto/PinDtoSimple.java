package me.exrates.model.dto.ngDto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotNull;

@Getter
@Setter
@NoArgsConstructor
public class PinDtoSimple {
    @NotNull
    private String pin;
}
