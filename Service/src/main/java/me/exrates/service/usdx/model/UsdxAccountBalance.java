package me.exrates.service.usdx.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class UsdxAccountBalance {
    @JsonProperty("USDX")
    private BigDecimal usdxBalance;
    @JsonProperty("LHT")
    private BigDecimal lhtBalance;

    private String errorCode;
    private String failReason;
}
