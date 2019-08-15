package me.exrates.model.dto.merchants.adgroup.responses;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ResultResponseDto {
    private Boolean status;
    private String message;
}
