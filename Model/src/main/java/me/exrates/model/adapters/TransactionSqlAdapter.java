package me.exrates.model.adapters;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import me.exrates.model.Transaction;

import java.sql.Timestamp;

@NoArgsConstructor
@Data
@AllArgsConstructor
@Builder
public class TransactionSqlAdapter {

    private int userWalletId;
    private double amount;
    private double commissionAmount;
    private int operationTypeId;
    private int currencyId;
    private boolean provided = true;
    private double activeBalanceBefore;
    private String sourceType;
    private String description;

    public static TransactionSqlAdapter valueOf(Transaction transaction) {
        return TransactionSqlAdapter
                .builder()
                .userWalletId(transaction.getUserWallet().getId())
                .amount(transaction.getAmount().doubleValue())
                .commissionAmount(transaction.getCommissionAmount().doubleValue())
                .operationTypeId(transaction.getOperationType().getType())
                .currencyId(transaction.getCurrency().getId())
                .activeBalanceBefore(transaction.getActiveBalanceBefore().doubleValue())
                .sourceType(transaction.getSourceType().name())
                .description(transaction.getDescription())
                .build();
    }

}
