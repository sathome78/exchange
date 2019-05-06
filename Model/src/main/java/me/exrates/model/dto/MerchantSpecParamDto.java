package me.exrates.model.dto;

import lombok.Data;

@Data
public class MerchantSpecParamDto {

    private int id;
    private int merchantId;
    private String paramName;
    private String paramValue;
}
