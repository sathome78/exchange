package me.exrates.model.dto.qubera;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.math.BigDecimal;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class QuberaPaymentToMasterDto {
    private String currencyCode;
    private BigDecimal amount;
    private String accountNumber;
    private String narrative;

    //addition filed for internal payment
    private String beneficiaryAccountNumber;
    private String senderAccountNumber;
}
