package me.exrates.model.dto.qubera;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import me.exrates.model.constants.ErrorApiTitles;
import me.exrates.model.ngExceptions.NgDashboardException;

import java.util.Arrays;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder(builderClassName = "Builder")
public class ExternalPaymentShortDto {
    private String firstName;
    private String lastName;

    private String companyName;

    private String iban;
    private String narrative;
    private String amount;
    private String currencyCode;

    private String type;

    private String accountNumber;
    private String swift;

    private String address;
    private String city;
    private String countryCode;

    public enum PaymentType {
        SWIFT("swift"),
        SEPA("sepa");

        private String type;

        PaymentType(String type) {
            this.type = type;
        }

        public String getType() {
            return type;
        }

        public static PaymentType of(String name) {
            return Arrays.stream(PaymentType.values())
                    .filter(item -> item.getType().equalsIgnoreCase(name))
                    .findFirst()
                    .orElseThrow(() -> new NgDashboardException(ErrorApiTitles.QUBERA_PAYMENT_TYPE_ERROR));
        }
    }
}
