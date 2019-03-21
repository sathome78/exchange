package me.exrates.model.dto.qubera;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import me.exrates.model.serializer.QuberaJsNumberBigDecimalDeserializer;
import me.exrates.model.serializer.QuberaLocalDateDeserializer;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class QuberaRequestDto {

    private String accountIBAN;
    private String accountNumber;
    private int messageId;
    private int paymentId;
    @JsonDeserialize(using = QuberaLocalDateDeserializer.class)
    private LocalDateTime processingTime;
    private String rejectionReason;
    private String state;
    private String currency;
    @JsonDeserialize(using = QuberaJsNumberBigDecimalDeserializer.class)
    private BigDecimal paymentAmount;
    private String transferType;

    public Map<String, String> getParams() {
        Map<String, String> params = new HashMap<>();
        params.put("paymentId", String.valueOf(this.paymentId));
        params.put("currency", currency);
        params.put("accountNumber", accountNumber);
        params.put("paymentAmount", getPaymentAmount().toPlainString());
        return params;
    }

}
