package me.exrates.model.newOrders;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Data;
import me.exrates.model.enums.TransactionSourceType;
import me.exrates.model.serializer.LocalDateTimeSerializer;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class TransactionDto implements Serializable {

    private Long id;
    private Integer userWalletId;
    private Integer companyWalletId;
    private BigDecimal amount;
    private BigDecimal commissionAmount;
    private Integer commissionId;
    private Integer operationTypeId;
    private Integer currencyId;
    private Integer merchantId;
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    private LocalDateTime datetime = LocalDateTime.now();
    private BigDecimal activeBalanceBefore;
    private BigDecimal reservedBalanceBefore;
    private BigDecimal companyBalanceBefore;
    private BigDecimal companyCommissionBalanceBefore;
    private TransactionSourceType sourceType;
    private Long sourceId;
    private String description;
    private boolean isSynced;
}
