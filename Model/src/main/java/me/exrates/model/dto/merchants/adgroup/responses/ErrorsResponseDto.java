package me.exrates.model.dto.merchants.adgroup.responses;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.Map;

@AllArgsConstructor
@NoArgsConstructor
@Data
@ToString
public class ErrorsResponseDto {
    private Map<String, Object> errors;
}
