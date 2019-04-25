package me.exrates.model.dto.ngDto;

import lombok.Data;
import me.exrates.model.Currency;
import me.exrates.model.MerchantCurrency;
import me.exrates.model.Payment;

import java.math.BigDecimal;
import java.util.List;

/**
 * Created by Maks on 07.03.2018.
 */
@Data
public class RefillPageDataDto {

    private Currency currency;
    private Payment payment;
    private BigDecimal minRefillSum;
    private Integer scaleForCurrency;
    private List<MerchantCurrency> merchantCurrencyData;
    private List<String> warningCodeList;
    private boolean isaMountInputNeeded;
    private Integer minConfirmations;
}
