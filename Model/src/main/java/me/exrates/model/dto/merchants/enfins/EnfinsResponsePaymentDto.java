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
public class EnfinsResponsePaymentDto {
    @JsonProperty("bill_id")
    private int billId;
}
