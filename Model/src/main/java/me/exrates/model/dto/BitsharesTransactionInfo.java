package me.exrates.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@Builder
public class BitsharesTransactionInfo {
    private String from;
    private String to;
    private BigDecimal amount;
    private String memo;
}
