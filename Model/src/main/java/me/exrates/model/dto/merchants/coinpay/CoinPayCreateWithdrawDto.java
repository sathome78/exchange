package me.exrates.model.dto.merchants.coinpay;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.math.BigDecimal;

@Getter
@Setter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class CoinPayCreateWithdrawDto {

    private BigDecimal amount;
    @JsonProperty("withdrawal_type")
    private WithdrawalType withdrawalType;
    private String comment;
    private String currency;
    @JsonProperty("wallet_to")
    private String walletTo;
    private String callBack;


    public enum WithdrawalType {
        GATEWAY
    }

}
