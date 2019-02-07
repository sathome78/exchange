package me.exrates.model.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import me.exrates.model.enums.CurrencyType;

@Getter
@Builder(builderClassName = "Builder")
@AllArgsConstructor
@NoArgsConstructor
public class BalanceFilterDataDto {

    private Integer limit;
    private Integer offset;
    private Boolean excludeZero;
    private String currencyName;
    private CurrencyType currencyType;
    private String email;
}