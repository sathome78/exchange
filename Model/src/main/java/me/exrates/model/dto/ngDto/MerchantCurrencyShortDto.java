package me.exrates.model.dto.ngDto;

import lombok.Getter;
import lombok.Setter;
import me.exrates.model.MerchantCurrency;
import me.exrates.model.enums.OperationType;

import java.math.BigDecimal;

/**
 * Created by Maks on 15.05.2018.
 */
@Getter@Setter
public class MerchantCurrencyShortDto {
    private int merchantId;
    private int currencyId;
    private String name;
    private String processType;
    private String description;
    private BigDecimal minSum;
    private BigDecimal merchantComission;
    private BigDecimal fixedMinCommission;
    private String image_name;
    private String image_path;
    private String mainAddress;
    private String address;
    private Boolean additionalTagForWithdrawAddressIsUsed;
    private String additionalFieldName;
    private Boolean generateAdditionalRefillAddressAvailable;
    private Boolean recipientUserIsNeeded;
    private Boolean comissionDependsOnDestinationTag;
    private Boolean specMerchantComission;

    public MerchantCurrencyShortDto(MerchantCurrency merchantCurrency, OperationType operationType) {
        this.merchantId = merchantCurrency.getMerchantId();
        this.currencyId = merchantCurrency.getCurrencyId();
        this.name = merchantCurrency.getName();
        this.description = merchantCurrency.getDescription();
        this.minSum = merchantCurrency.getMinSum();
        this.fixedMinCommission = merchantCurrency.getFixedMinCommission();
        this.image_name = merchantCurrency.getListMerchantImage().get(0).getImage_name();
        this.image_path = merchantCurrency.getListMerchantImage().get(0).getImage_path();
        this.processType = merchantCurrency.getProcessType();
        if (operationType == OperationType.INPUT) {
            this.address = merchantCurrency.getAddress();
            this.mainAddress = merchantCurrency.getMainAddress();
            this.merchantComission = merchantCurrency.getInputCommission();
            this.generateAdditionalRefillAddressAvailable = merchantCurrency.getGenerateAdditionalRefillAddressAvailable();
            this.additionalFieldName = merchantCurrency.getAdditionalFieldName();
        }
        if (operationType == OperationType.USER_TRANSFER) {
            this.recipientUserIsNeeded = merchantCurrency.getRecipientUserIsNeeded();
            this.merchantComission = merchantCurrency.getOutputCommission();
        }
        if (operationType == OperationType.OUTPUT) {
            this.comissionDependsOnDestinationTag = merchantCurrency.getComissionDependsOnDestinationTag();
            this.merchantComission = merchantCurrency.getOutputCommission();
            this.specMerchantComission = merchantCurrency.getSpecMerchantComission();
            this.additionalTagForWithdrawAddressIsUsed = merchantCurrency.getAdditionalTagForWithdrawAddressIsUsed();
            this.additionalFieldName = merchantCurrency.getAdditionalFieldName();
        }
    }
}
