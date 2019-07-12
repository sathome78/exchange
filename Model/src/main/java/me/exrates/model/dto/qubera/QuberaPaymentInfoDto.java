package me.exrates.model.dto.qubera;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class QuberaPaymentInfoDto {
    private String iban;
    private String accountNumber;
    private Object companyDetails;
}
