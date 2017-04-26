package me.exrates.model.dto;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import me.exrates.model.dto.onlineTableDto.OnlineTableDto;
import me.exrates.model.enums.ActionType;
import me.exrates.model.enums.TransactionSourceType;
import me.exrates.model.enums.invoice.InvoiceOperationPermission;
import me.exrates.model.enums.invoice.RefillStatusEnum;
import me.exrates.model.serializer.LocalDateTimeSerializer;
import me.exrates.model.util.BigDecimalProcessing;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import static me.exrates.model.enums.TransactionSourceType.REFILL;

/**
 * Created by ValkSam
 */
@Getter @Setter
@ToString
public class RefillRequestsAdminTableDto extends OnlineTableDto {
  private Integer id;
  @JsonSerialize(using = LocalDateTimeSerializer.class)
  private LocalDateTime dateCreation;
  private Integer userId;
  private String userEmail;
  private BigDecimal amount;
  private String currencyName;
  private BigDecimal commissionAmount;
  private BigDecimal enrolledAmount;
  private BigDecimal receivedAmount;
  private String merchantName;
  private String address = "";
  private Integer adminHolderId;
  private String adminHolderEmail;
  private String recipientBankName = "";
  private String recipientBankAccount = "";
  private String recipientBankRecipient = "";
  private String payerBankName = "";
  private String payerBankCode = "";
  private String payerBankAccount = "";
  private String receiptScan = "";
  private String userFullName = "";
  private String remark;
  private String wifPrivKey = "";
  private String pubKey = "";
  private String brainPrivKey = "";
  private String hash = "";
  private String merchantTransactionId = "";
  private RefillStatusEnum status;
  @JsonSerialize(using = LocalDateTimeSerializer.class)
  private LocalDateTime statusModificationDate;
  private TransactionSourceType sourceType = REFILL;
  private InvoiceOperationPermission invoiceOperationPermission;
  private Boolean isEndStatus;
  private List<Map<String, Object>> buttons;

  public RefillRequestsAdminTableDto(
      RefillRequestFlatDto refillRequestFlatDto,
      RefillRequestFlatAdditionalDataDto refillRequestFlatAdditionalDataDto) {
    this.id = refillRequestFlatDto.getId();
    this.dateCreation = refillRequestFlatDto.getDateCreation();
    this.userId = refillRequestFlatDto.getUserId();
    this.userEmail = refillRequestFlatAdditionalDataDto.getUserEmail();
    this.amount = refillRequestFlatDto.getAmount();
    this.currencyName = refillRequestFlatAdditionalDataDto.getCurrencyName();
    this.enrolledAmount = refillRequestFlatAdditionalDataDto.getTransactionAmount() == null ? BigDecimal.ZERO : refillRequestFlatAdditionalDataDto.getTransactionAmount();
    this.commissionAmount = refillRequestFlatAdditionalDataDto.getCommissionAmount() == null ? BigDecimal.ZERO : refillRequestFlatAdditionalDataDto.getCommissionAmount();
    this.receivedAmount = BigDecimalProcessing.doAction(this.enrolledAmount, this.commissionAmount, ActionType.ADD);
    this.merchantName = refillRequestFlatAdditionalDataDto.getMerchantName();
    this.address = refillRequestFlatDto.getAddress();
    this.adminHolderId = refillRequestFlatDto.getAdminHolderId();
    this.adminHolderEmail = refillRequestFlatAdditionalDataDto.getAdminHolderEmail();
    this.recipientBankName = refillRequestFlatDto.getRecipientBankName();
    this.recipientBankAccount = refillRequestFlatDto.getRecipientBankAccount();
    this.recipientBankRecipient = refillRequestFlatDto.getRecipientBankRecipient();
    this.payerBankName = refillRequestFlatDto.getPayerBankName();
    this.payerBankCode = refillRequestFlatDto.getPayerBankCode();
    this.payerBankAccount = refillRequestFlatDto.getPayerAccount();
    this.receiptScan = refillRequestFlatDto.getReceiptScan();
    this.userFullName = refillRequestFlatDto.getUserFullName();
    this.remark = refillRequestFlatDto.getRemark();
    this.wifPrivKey = null;
    this.pubKey = null;
    this.brainPrivKey = null;
    this.status = refillRequestFlatDto.getStatus();
    this.statusModificationDate = refillRequestFlatDto.getStatusModificationDate();
    this.invoiceOperationPermission = refillRequestFlatDto.getInvoiceOperationPermission();
    this.isEndStatus = this.status.isEndStatus();
    this.buttons = null;
  }
}
