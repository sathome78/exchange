package me.exrates.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import me.exrates.model.enums.IEODetailsStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@NoArgsConstructor
@Data
@AllArgsConstructor
@Builder
public class IEODetails {

    private int id;
    private String currencyName;
    private String currencyDescription;
    private String description;
    private String logo;
    @JsonIgnore
    private int makerId;
    private BigDecimal rate;
    private BigDecimal amount;
    private BigDecimal availableAmount;
    // not stored in db -> consider pattern visitor
    private BigDecimal personalAmount;
    private int contributors;
    private IEODetailsStatus status;
    private BigDecimal minAmount;
    private BigDecimal maxAmountPerClaim;
    private BigDecimal maxAmountPerUser;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime startDate;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime endDate;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime soldOutAt;
    @JsonIgnore
    private LocalDateTime createdAt;
    @JsonIgnore
    private int createdBy;
    private int version;
    private String content;

    private Boolean testIeo;
    @JsonIgnore
    private Integer countTestTransaction;

    private boolean readyToIeo;

    public IEODetails(int id, String currencyName, int makerId, BigDecimal rate, BigDecimal amount, int contributors,
                      IEODetailsStatus status, BigDecimal minAmount, BigDecimal maxAmountPerClaim, BigDecimal maxAmountPerUser,
                      LocalDateTime startDate, LocalDateTime endDate, int version) {
        this.id = id;
        this.currencyName = currencyName;
        this.makerId = makerId;
        this.rate = rate;
        this.amount = amount;
        this.availableAmount = amount;
        this.contributors = contributors;
        this.status = status;
        this.minAmount = minAmount;
        this.maxAmountPerClaim = maxAmountPerClaim;
        this.maxAmountPerUser = maxAmountPerUser;
        this.startDate = startDate;
        this.endDate = endDate;
        this.version = version;
    }
}
