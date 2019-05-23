package me.exrates.service.usdx.model;

import lombok.Data;

@Data
public class UsdxHistoryTransaction {
    private UsdxTransaction[] history;

    private String errorCode;
    private String failReason;
}
