package me.exrates.model;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import me.exrates.model.enums.MerchantCommissonTypeEnum;
import me.exrates.model.enums.MerchantVerificationType;
import me.exrates.model.util.BigDecimalToStringSerializer;

import java.math.BigDecimal;
import java.util.List;

/**
 * @author Denis Savin (pilgrimm333@gmail.com)
 */
@Getter
@Setter
@EqualsAndHashCode
@ToString
@NoArgsConstructor
public class MerchantCurrency {
    private int merchantId;
    private int currencyId;
    private String name;
    private String description;
    @JsonSerialize(using = BigDecimalToStringSerializer.class)
    private BigDecimal minSum;
    @JsonSerialize(using = BigDecimalToStringSerializer.class)
    private BigDecimal inputCommission;
    @JsonSerialize(using = BigDecimalToStringSerializer.class)
    private BigDecimal outputCommission;
    @JsonSerialize(using = BigDecimalToStringSerializer.class)
    private BigDecimal fixedMinCommission;
    private List<MerchantImage> listMerchantImage;
    private String processType;
    private String mainAddress;
    private String address;
    private Boolean additionalTagForWithdrawAddressIsUsed;
    private Boolean additionalTagForRefillIsUsed;
    private String additionalFieldName;
    private Boolean generateAdditionalRefillAddressAvailable;
    private Boolean recipientUserIsNeeded;
    private Boolean comissionDependsOnDestinationTag;
    private Boolean specMerchantComission;
    private Boolean availableForRefill;
    private Boolean needKycRefill;
    private Boolean needKycWithdraw;
    private Boolean needVerification;
    private MerchantVerificationType verificationType;
    private MerchantCommissonTypeEnum withdrawCommissionType;
    private String secondaryCommissionCurrency;
    private Integer secondaryCommissionCurrencyId;
    private BigDecimal secondaryCommissionAmount;

    private String paymentLink;
}
