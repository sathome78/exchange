package me.exrates.model.dto.onlineTableDto;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import me.exrates.model.enums.ActionType;
import me.exrates.model.enums.OperationType;
import me.exrates.model.enums.TransactionStatus;
import me.exrates.model.serializer.LocalDateTimeSerializer;
import me.exrates.model.util.BigDecimalProcessing;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Created by Valk on 28.06.2016.
 */
public class AccountStatementDto extends OnlineTableDto {
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    private LocalDateTime datetime;
    private Integer transactionId;
    private String activeBalanceBefore;
    private String reservedBalanceBefore;
    private String operationType;
    private String amount;
    private String commissionAmount;
    private String activeBalanceAfter;
    private String reservedBalanceAfter;
    private String sourceType;
    private String sourceTypeId;
    private Integer sourceId;
    private TransactionStatus transactionStatus;
    private Boolean checked;

    public AccountStatementDto() {
        this.needRefresh = true;
    }

    public AccountStatementDto(boolean needRefresh) {
        this.needRefresh = needRefresh;
    }

    /*hash*/

    @Override
    public int hashCode() {
        int result = transactionId != null ? transactionId.hashCode() : 0;
        result = 31 * result + (activeBalanceBefore != null ? activeBalanceBefore.hashCode() : 0);
        result = 31 * result + (reservedBalanceBefore != null ? reservedBalanceBefore.hashCode() : 0);
        result = 31 * result + (operationType != null ? operationType.hashCode() : 0);
        result = 31 * result + (amount != null ? amount.hashCode() : 0);
        result = 31 * result + (commissionAmount != null ? commissionAmount.hashCode() : 0);
        result = 31 * result + (sourceId != null ? sourceId.hashCode() : 0);
        result = 31 * result + (transactionStatus != null ? transactionStatus.hashCode() : 0);
        return result;
    }

    /*getters setters*/

    public LocalDateTime getDatetime() {
        return datetime;
    }

    public void setDatetime(LocalDateTime datetime) {
        this.datetime = datetime;
    }

    public Integer getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(Integer transactionId) {
        this.transactionId = transactionId;
    }

    public String getActiveBalanceBefore() {
        return activeBalanceBefore;
    }

    public void setActiveBalanceBefore(String activeBalanceBefore) {
        this.activeBalanceBefore = activeBalanceBefore;
    }

    public String getReservedBalanceBefore() {
        return reservedBalanceBefore;
    }

    public void setReservedBalanceBefore(String reservedBalanceBefore) {
        this.reservedBalanceBefore = reservedBalanceBefore;
    }

    public String getOperationType() {
        return operationType;
    }

    public void setOperationType(String operationType) {
        this.operationType = operationType;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getCommissionAmount() {
        return commissionAmount;
    }

    public void setCommissionAmount(String commissionAmount) {
        this.commissionAmount = commissionAmount;
    }

    public String getSourceType() {
        return sourceType;
    }

    public void setSourceType(String sourceType) {
        this.sourceType = sourceType;
    }

    public Integer getSourceId() {
        return sourceId;
    }

    public void setSourceId(Integer sourceId) {
        this.sourceId = sourceId;
    }

    public TransactionStatus getTransactionStatus() {
        return transactionStatus;
    }

    public void setTransactionStatus(TransactionStatus transactionStatus) {
        this.transactionStatus = transactionStatus;
    }

    public String getActiveBalanceAfter() {
        return activeBalanceAfter;
    }

    public void setActiveBalanceAfter(String activeBalanceAfter) {
        this.activeBalanceAfter = activeBalanceAfter;
    }

    public String getReservedBalanceAfter() {
        return reservedBalanceAfter;
    }

    public void setReservedBalanceAfter(String reservedBalanceAfter) {
        this.reservedBalanceAfter = reservedBalanceAfter;
    }

    public String getSourceTypeId() {
        return sourceTypeId;
    }

    public void setSourceTypeId(String sourceTypeId) {
        this.sourceTypeId = sourceTypeId;
    }

    public Boolean getChecked() {
        return checked;
    }

    public void setChecked(Boolean checked) {
        this.checked = checked;
    }
}
