package me.exrates.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Date;

@NoArgsConstructor
@Data
public class IEOClaim {
    private int id;
    private int ieoId;
    private String currencyName;
    private int makerId;
    private int userId;
    private BigDecimal rate;
    private BigDecimal amount;
    private BigDecimal priceInBtc;
    private Date created;
    @JsonIgnore
    // this property is needed to transfer who should get notification
    private String creatorEmail;
    private IEOResult.IEOResultStatus status;


    public IEOClaim(int ieoId, String currencyName, int makerId, int userId, BigDecimal amount, BigDecimal rate) {
        this.ieoId = ieoId;
        this.currencyName = currencyName;
        this.makerId = makerId;
        this.userId = userId;
        this.amount = amount;
        this.created = new Date();
        this.status = IEOResult.IEOResultStatus.NONE;
        this.rate = rate;
        this.priceInBtc = amount.multiply(rate);
    }
}
