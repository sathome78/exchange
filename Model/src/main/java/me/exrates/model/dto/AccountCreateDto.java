package me.exrates.model.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotNull;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AccountCreateDto {

    private String firstName;
    private String lastName;
    private String address;
    private String city;
    private String countryCode;
    private String email;

    @NotNull
    private String currencyCode;
}
