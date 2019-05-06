package me.exrates.model.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class LookupResponseDto {

    private String country;
    private String operator;
    private boolean isOperable;
}
