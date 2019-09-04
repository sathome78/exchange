package me.exrates.model.dto.merchants.adgroup.responses;

import lombok.Data;
import lombok.ToString;

import java.util.Map;

@Data
@ToString
public class ErrorsResponseDto {
    private Map<String, String> errors;
}
