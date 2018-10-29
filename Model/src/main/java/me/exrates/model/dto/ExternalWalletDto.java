package me.exrates.model.dto;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import me.exrates.model.serializer.LocalDateTimeDeserializer;
import me.exrates.model.serializer.LocalDateTimeSerializer;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@ToString
public class ExternalWalletDto {

    private Integer currencyId;
    private Integer merchantId;
    private String currencyName;
    private BigDecimal reservedWalletBalance;
    private BigDecimal coldWalletBalance;
    private BigDecimal mainWalletBalance = BigDecimal.ZERO;
    private BigDecimal mainWalletBalanceUSD = BigDecimal.ZERO;
    private BigDecimal totalReal;
    private BigDecimal rateUsdAdditional;

    private BigDecimal totalWalletsBalance;
    private BigDecimal totalWalletsBalanceUSD;
    private BigDecimal totalWalletsDifference;
    private BigDecimal totalWalletsDifferenceUSD;
}
