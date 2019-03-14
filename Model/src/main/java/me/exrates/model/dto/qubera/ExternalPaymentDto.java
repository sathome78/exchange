package me.exrates.model.dto.qubera;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder(builderClassName = "Builder")
public class ExternalPaymentDto {
    private BeneficiaryAccountDto beneficiaryAccount;
    private BeneficiaryDetailsDto beneficiaryDetails;
    private String senderAccountNumber;
    private TransferDetailsDto transferDetails;
}
