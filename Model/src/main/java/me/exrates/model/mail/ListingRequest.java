package me.exrates.model.mail;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.validation.constraints.NotNull;

@Getter
@NoArgsConstructor
@ToString
@EqualsAndHashCode
public class ListingRequest {

    @NotNull
    private String name;
    @NotNull
    private String email;
    private String telegram;
    private String text;
}