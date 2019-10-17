package me.exrates.model.dto.merchants.enfins;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class EnfinsResponsePaymentRefillDto extends EnfinsResponsePaymentDto {
    private String url;
}
