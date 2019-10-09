package me.exrates.model.dto.merchants.adgroup.responses;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class AdGroupResponseDto<T> {

    private HeaderResponseDto header;
    private ResultResponseDto result;
    private T responseData;
    private List<AdGroupErrorResponseDto> errors;
}
