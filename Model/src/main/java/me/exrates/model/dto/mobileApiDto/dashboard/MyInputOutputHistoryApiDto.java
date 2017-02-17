package me.exrates.model.dto.mobileApiDto.dashboard;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import me.exrates.model.dto.onlineTableDto.MyInputOutputHistoryDto;
import me.exrates.model.enums.InvoiceRequestStatusEnum;
import me.exrates.model.serializer.LocalDateTimeToLongSerializer;
import me.exrates.model.util.BigDecimalProcessing;
import org.apache.commons.lang3.StringEscapeUtils;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Locale;

/**
 * Created by Ajet on 23.07.2016.
 */
@Getter @Setter
@EqualsAndHashCode
@ToString
public class MyInputOutputHistoryApiDto {
    @JsonSerialize(using = LocalDateTimeToLongSerializer.class)
    private LocalDateTime datetime;
    private String currencyName;
    private Double amount;
    private Double commissionAmount;
    private String merchantName;
    private String operationType;
    private Integer transactionId;
    private String transactionProvided;
    private Integer userId;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String bankAccount;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String invoiceStatus;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String userFullName;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String remark;

    public MyInputOutputHistoryApiDto(MyInputOutputHistoryDto dto, Locale locale) {
        this.datetime = dto.getDatetime();
        this.currencyName = dto.getCurrencyName();
        this.amount = BigDecimalProcessing.parseLocale(dto.getAmount(), locale, 2).doubleValue();
        this.commissionAmount = BigDecimalProcessing.parseLocale(dto.getCommissionAmount(), locale, 2).doubleValue();;
        this.merchantName = dto.getMerchantName();
        this.operationType = dto.getOperationType();
        this.transactionId = dto.getTransactionId();
        this.transactionProvided = dto.getTransactionProvided();
        this.userId = dto.getUserId();
        this.invoiceStatus = dto.getInvoiceRequestStatusId() == null ? null : InvoiceRequestStatusEnum.convert(dto.getInvoiceRequestStatusId()).name();
        this.bankAccount = dto.getBankAccount();
        this.userFullName = dto.getUserFullName();
        this.remark = StringEscapeUtils.unescapeHtml4(dto.getRemark());
    }



}
