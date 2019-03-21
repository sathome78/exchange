package me.exrates.model.dto.qubera;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ResponsePaymentDto {
    private String currencyFrom;
    private String currencyTo;
    private BigDecimal feeAmount;
    private String feeCurrencyCode;
    private Integer paymentId;
    private BigDecimal rate;
    private BigDecimal transactionAmount;
    private String transactionCurrencyCode;

}
