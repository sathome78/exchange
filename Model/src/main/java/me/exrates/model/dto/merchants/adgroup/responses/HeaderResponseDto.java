package me.exrates.model.dto.merchants.adgroup.responses;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class HeaderResponseDto {
    private String version;
    private String txName;
    private String lang;
    private Double androidVersion;
    private Double iosVersion;
}
