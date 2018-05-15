package me.exrates.model.dto.ngDto;

import lombok.Data;
import me.exrates.model.Currency;
import me.exrates.model.MerchantCurrency;
import me.exrates.model.Payment;
import me.exrates.model.Wallet;
import me.exrates.model.enums.OperationType;

import java.math.BigDecimal;
import java.util.List;

/**
 * Created by Maks on 07.03.2018.
 */
@Data
public class WithdrawDataDto {

  /*  private Currency currency;*/
    private BigDecimal activeBalance;
    private String balanceAndName;
    private int userId;
    private OperationType operationType;
    private BigDecimal minWithdrawSum;
    private Integer scaleForCurrency;
    private List<Integer> currenciesId;
    private List<MerchantCurrencyShortDto> merchantCurrencyData;
    private List<String> warningCodeList;
    private String balance;
    private String processType;


}
