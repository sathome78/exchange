package me.exrates.model.dto;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@EqualsAndHashCode(of = {"id"})
public class RatesUSDForReportDto {
    private int id;
    private String currencyName;
    private BigDecimal rate;

    public String getName() {
        return this.currencyName.replace("\\/.+$", "");
    }
}