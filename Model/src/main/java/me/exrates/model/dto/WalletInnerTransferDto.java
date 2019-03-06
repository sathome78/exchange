package me.exrates.model.dto;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import me.exrates.model.enums.TransactionSourceType;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
public class WalletInnerTransferDto {
    private int walletId;
    private BigDecimal amount;
    private TransactionSourceType sourceType;
    private int sourceId;
    private String description;
}