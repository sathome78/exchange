package me.exrates.model.dto.merchants.adgroup;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AdGroupCommonRequestDto<T> {
    public CommonAdGroupHeaderDto header;
    public T reqData;
}
