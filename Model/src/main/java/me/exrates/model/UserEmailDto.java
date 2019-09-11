package me.exrates.model;

import lombok.Data;
import org.hibernate.validator.constraints.Email;
import javax.validation.constraints.NotNull;

@Data
public class UserEmailDto {

    private static final String EMAIL_PATTERN = "[A-Za-z0-9!#$%&'*+/=?^_`{|}~-]+(?:.[A-Za-z0-9!#$%&'*+/=?^_`{|}~-]+)*@(?:[A-Za-z0-9](?:[A-Za-z0-9-]*[A-Za-z0-9])?.)+[A-Za-z0-9](?:[A-Za-z0-9-]*[A-Za-z0-9])?";

    @NotNull
    @Email(message = "Email should be valid", regexp = EMAIL_PATTERN)
    private String email;
    @Email(message = "Parent email should be valid", regexp = EMAIL_PATTERN)
    private String parentEmail;
    @NotNull
    private Boolean isUsa;
}
