package me.exrates.model.dto.merchants.enfins;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class CallBackRefillDto {
    private String status;
    @JsonProperty("m_order")
    private String mOrder;
    @JsonProperty("bill_id")
    private String billId;
    @JsonProperty("operation_type")
    private String operationType;
    private String amount;
    private String currency;
    private String sign;
    private Boolean testing;
    @JsonProperty("p_method")
    private String paymentMethod;
    @JsonProperty("p_account")
    private String paymentAccount;
}
