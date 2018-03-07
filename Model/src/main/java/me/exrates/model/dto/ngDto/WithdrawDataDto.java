package me.exrates.model.dto.ngDto;

import lombok.Data;
import me.exrates.model.Currency;
import me.exrates.model.MerchantCurrency;
import me.exrates.model.Payment;
import me.exrates.model.Wallet;

import java.math.BigDecimal;
import java.util.List;

/**
 * Created by Maks on 07.03.2018.
 */
@Data
public class WithdrawDataDto {

    private Currency currency;
    private Wallet wallet;
    private Payment payment;
    private BigDecimal minWithdrawSum;
    private Integer scaleForCurrency;
    private List<Integer> currenciesId;
    private List<MerchantCurrency> merchantCurrencyData;
    private List<String> warningCodeList;
    private String balance;


}
