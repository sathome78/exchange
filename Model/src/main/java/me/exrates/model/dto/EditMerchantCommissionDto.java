package me.exrates.model.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.math.BigDecimal;

/**
 * Created by OLEG on 01.03.2017.
 */
@Getter @Setter
@NoArgsConstructor
@ToString
public class EditMerchantCommissionDto {
    private String merchantName;
    private String currencyName;
    private BigDecimal inputValue;
    private BigDecimal outputValue;
    private BigDecimal transferValue;
    private BigDecimal minFixedAmount;
    private BigDecimal minFixedAmountUSD;
    private BigDecimal secondaryOutputCommission;
}
