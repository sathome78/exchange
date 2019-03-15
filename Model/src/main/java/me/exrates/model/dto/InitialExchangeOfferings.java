package me.exrates.model.dto;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter @Setter
@EqualsAndHashCode
public class InitialExchangeOfferings {
    private int id;
    private String email;
}
