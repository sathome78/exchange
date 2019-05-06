package me.exrates.model.dto.ieo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.Data;
import lombok.NoArgsConstructor;
import me.exrates.model.IEODetails;
import me.exrates.model.enums.IEODetailsStatus;
import me.exrates.model.serializer.LocalDateTimeDeserializer;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class IeoDetailsCreateDto {

    @NotNull(message = "Name must not be null")
    private String currencyName;
    @NotNull(message = "Description must not be null")
    private String description;
    @NotNull(message = "Maker email must not be null")
    private String makerEmail;
    @NotNull(message = "Rate must not be null")
    private BigDecimal rate;
    @NotNull(message = "Amount must not be null")
    private BigDecimal amount;
    @NotNull(message = "available balance must not be null")
    private BigDecimal availableBalance;
    @NotNull(message = "Min amount must not be null")
    private BigDecimal minAmount;
    @NotNull(message = "Max amount per user must not be null")
    private BigDecimal maxAmountPerUser;
    @NotNull(message = "Max amount per clime must not be null")
    private BigDecimal maxAmountPerClaim;
    @NotNull(message = "Start date amount per clime must not be null")
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    private LocalDateTime startDate;
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    private LocalDateTime endDate;

    public IEODetails toIEODetails(int makerId, int creatorId) {
        return IEODetails.builder()
                .currencyName(currencyName)
                .currencyDescription(description)
                .amount(amount)
                .rate(rate)
                .availableAmount(availableBalance)
                .minAmount(minAmount)
                .maxAmountPerUser(maxAmountPerUser)
                .maxAmountPerClaim(maxAmountPerClaim)
                .contributors(0)
                .status(IEODetailsStatus.PENDING)
                .startDate(startDate)
                .endDate(endDate)
                .makerId(makerId)
                .createdBy(creatorId)
                .build();
    }

}
