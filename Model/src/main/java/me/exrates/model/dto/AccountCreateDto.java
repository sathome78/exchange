package me.exrates.model.dto;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import me.exrates.model.serializer.LocalDateDeserializer;

import javax.validation.constraints.NotNull;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AccountCreateDto {
    private static final String SEMICOLON = ";";
    private static final String SPACE = " ";

    @NotNull
    private String firstName;

    @NotNull
    private String lastName;

    @NotNull
    private String dateOfBirth;

    @NotNull
    private String street;

    @NotNull
    private Integer zipCode;

    @NotNull
    private String city;

    @NotNull
    private String country;

    @NotNull
    private String phone;

    private String email;

    @NotNull
    private String currencyCode;

    public String getStringFromParams() {
        return new StringBuilder()
                .append(firstName).append(SPACE).append(lastName).append(SEMICOLON)
                .append(dateOfBirth).append(SEMICOLON)
                .append(street).append(SEMICOLON)
                .append(zipCode).append(SEMICOLON)
                .append(country).append(SEMICOLON)
                .append(phone).append(SEMICOLON)
                .append(email).append(SEMICOLON).toString();
    }
}
