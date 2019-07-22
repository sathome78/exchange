package me.exrates.model.dto.qubera.responses;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ExternalPaymentResponseDto {

    private Integer id;
    private PaymentAmount paymentAmount;
    private PaymentAmount feeAmount;
    private Rate rate;

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    public class PaymentAmount {
        private Double amount;
        private String currencyCode;
    }

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    public class Rate {
        private String from;
        private String to;
        private BigDecimal value;
    }
}
