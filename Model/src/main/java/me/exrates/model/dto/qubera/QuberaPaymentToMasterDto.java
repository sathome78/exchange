package me.exrates.model.dto.qubera;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class QuberaPaymentToMasterDto {
    private String currencyCode;
    private BigDecimal amount;
    private String accountNumber;
    private String narrative;

    //addition filed for internal payment
    private String beneficiaryAccountNumber;
    private String senderAccountNumber;
}
