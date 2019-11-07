package me.exrates.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Restriction {

    private int id;
    private String currencyPairName;
    private String name;
    private String description;
    private String condition;
    private String errorCode;
    private String errorMessage;
}
