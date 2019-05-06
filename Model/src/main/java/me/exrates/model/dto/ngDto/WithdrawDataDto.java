package me.exrates.model.dto.ngDto;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import me.exrates.model.MerchantCurrency;
import me.exrates.model.enums.OperationType;
import me.exrates.model.serializer.BigDecimalToDoubleSerializer;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder(builderClassName = "Builder")
@NoArgsConstructor
@AllArgsConstructor
public class WithdrawDataDto {

    private BigDecimal activeBalance;
    private String balanceAndName;
    private int userId;
    private OperationType operationType;
    @JsonSerialize(using = BigDecimalToDoubleSerializer.class)
    private BigDecimal minWithdrawSum;
    @JsonSerialize(using = BigDecimalToDoubleSerializer.class)
    private BigDecimal maxDailyRequestSum;
    @JsonSerialize(using = BigDecimalToDoubleSerializer.class)
    private BigDecimal leftRequestSum;
    private Integer scaleForCurrency;
    private List<Integer> currenciesId;
    private List<MerchantCurrency> merchantCurrencyData;
    private List<String> warningCodeList;
    private String balance;
    private String processType;
}
