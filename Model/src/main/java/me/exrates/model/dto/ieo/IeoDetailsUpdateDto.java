package me.exrates.model.dto.ieo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.Data;
import lombok.NoArgsConstructor;
import me.exrates.model.IEODetails;
import me.exrates.model.enums.IEODetailsStatus;
import me.exrates.model.serializer.LocalDateTimeDeserializer;

import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class IeoDetailsUpdateDto {

    @NotNull(message = "Description must not be null")
    private String coinDescription;
    @NotNull(message = "Description must not be null")
    private String description;
    @NotNull(message = "Status must not be null")
    private String status;
    @NotNull(message = "Rate must not be null")
    private BigDecimal rate;
    @NotNull(message = "Amount must not be null")
    private BigDecimal amount;
    @NotNull(message = "available balance must not be null")
    private BigDecimal availableBalance;
    @NotNull(message = "Min amount must not be null")
    @DecimalMin(value = "0.00001", message = "Min amount must not be higher than 0.00001")
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

    private String content;
    private Boolean testIeo;
    private String logo;

 public IEODetails toIEODetails(Integer id) {
  return IEODetails.builder()
          .id(id)
          .currencyDescription(coinDescription)
          .description(description)
          .amount(amount)
          .rate(rate)
          .availableAmount(availableBalance)
          .minAmount(minAmount)
          .maxAmountPerUser(maxAmountPerUser)
          .maxAmountPerClaim(maxAmountPerClaim)
          .status(IEODetailsStatus.valueOf(status))
          .startDate(startDate)
          .endDate(endDate)
          .content(content)
          .testIeo(testIeo)
          .logo(logo)
          .build();
 }


}
