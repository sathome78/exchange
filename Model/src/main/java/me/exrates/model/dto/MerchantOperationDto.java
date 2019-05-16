package me.exrates.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Map;

@Data
@AllArgsConstructor
public class MerchantOperationDto {
    private int merchantId;
    private Map<String, String> params;
}
