package me.exrates.model.dto.ngDto;

import lombok.Data;
import me.exrates.model.Currency;
import me.exrates.model.MerchantCurrency;
import me.exrates.model.Payment;
import me.exrates.model.Wallet;

import java.math.BigDecimal;
import java.util.List;

/**
 * Created by Maks on 08.02.2018.
 */
@Data
public class TransferMerchantsDataDto {

    private Currency currency;
    private Wallet wallet;
    private String balance;
    private Payment payment;
    private BigDecimal minTransferSum;
    private Integer scaleForCurrency;
    private List<MerchantCurrency> merchantCurrencyData;
    private List<String> warningCodeList;


}
