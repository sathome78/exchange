package me.exrates.model.dto.qubera;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BeneficiaryAccountDto {
    private String accountNumber;
    private String bankAddress;
    private String bankCountryCode;
    private String bankName;
    private String swift;
    private String iban;
}
