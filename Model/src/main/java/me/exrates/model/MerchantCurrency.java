package me.exrates.model;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import me.exrates.model.serializer.BigDecimalToDoubleSerializer;

import java.math.BigDecimal;
import java.util.List;

/**
 * @author Denis Savin (pilgrimm333@gmail.com)
 */
@Getter @Setter
@EqualsAndHashCode
@ToString
public class MerchantCurrency {
    private int merchantId;
    private int currencyId;
    private String name;
    private String description;
    @JsonSerialize(using = BigDecimalToDoubleSerializer.class)
    private BigDecimal minSum;
    @JsonSerialize(using = BigDecimalToDoubleSerializer.class)
    private BigDecimal inputCommission;
    @JsonSerialize(using = BigDecimalToDoubleSerializer.class)
    private BigDecimal outputCommission;
    @JsonSerialize(using = BigDecimalToDoubleSerializer.class)
    private BigDecimal fixedMinCommission;
    private List<MerchantImage> listMerchantImage;
    private String processType;
    private String mainAddress;
    private String address;
    private Boolean additionalTagForWithdrawAddressIsUsed;
    private String additionalFieldName;
    private Boolean generateAdditionalRefillAddressAvailable;
    private Boolean recipientUserIsNeeded;
    private Boolean comissionDependsOnDestinationTag;
    private Boolean specMerchantComission;
    private Boolean availableForRefill;
}