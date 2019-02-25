package me.exrates.model.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class BitsharesTransactionInfo {
    private String from;
    private String to;
    private BigDecimal amount;
    private String memo;
}
