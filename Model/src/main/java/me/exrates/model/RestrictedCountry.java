package me.exrates.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import me.exrates.model.enums.RestrictedOperation;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RestrictedCountry {

    private int id;
    private RestrictedOperation operation;
    private String countryName;
    private String countryCode;
}
