package me.exrates.model.dto.mobileApiDto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.math.BigDecimal;
import java.util.List;

/**
 * Created by OLEG on 13.10.2016.
 */
@Getter @Setter
@NoArgsConstructor
@ToString
public class MerchantCurrencyApiDto {
    private Integer merchantId;
    private Integer currencyId;
    private String name;
    private BigDecimal minInputSum;
    private BigDecimal minOutputSum;
    private BigDecimal minTransferSum;
    private BigDecimal inputCommission;
    private BigDecimal outputCommission;
    private BigDecimal transferCommission;
    private BigDecimal minFixedCommission;
    @JsonProperty(value = "withdrawBlocked")
    private Boolean isWithdrawBlocked;
    @JsonProperty(value = "refillBlocked")
    private Boolean isRefillBlocked;
    @JsonProperty(value = "transferBlocked")
    private Boolean isTransferBlocked;
    private List<MerchantImageShortenedDto> listMerchantImage;


}
