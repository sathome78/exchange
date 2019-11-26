package me.exrates.model.dto.referral;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

@Data
@NoArgsConstructor
public class RequestReferral {
    @NotNull
    @Min(5)
    @Max(100)
    private String name;
}
