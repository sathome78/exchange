package me.exrates.model.dto.merchants.enfins;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class EnfinsResponseDto<T> {
    private boolean result;
    private T data;
    private EnfinsErrorDto error;
}
