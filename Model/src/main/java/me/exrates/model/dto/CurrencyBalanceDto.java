package me.exrates.model.dto;

import lombok.Builder;
import lombok.Data;

/**
 * Created by Maks on 11.05.2018.
 */
@Data
@Builder(toBuilder = true)
public class CurrencyBalanceDto {

    private String message;
    private String email;
    private Integer currencyId;

}
