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
@Builder
public class QuberaPaymentInfoDto {
    private String iban;
    private String bic;
    private String swiftCode;
    private String bankName;
    private String country;
    private String city;
    private String address;
    private String url;
}
