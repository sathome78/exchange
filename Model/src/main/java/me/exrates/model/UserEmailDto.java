package me.exrates.model;

import lombok.Data;
import org.hibernate.validator.constraints.Email;

import javax.validation.constraints.NotNull;

@Data
public class UserEmailDto {

    @NotNull
    @Email
    private String email;
    private String parentEmail;
}
