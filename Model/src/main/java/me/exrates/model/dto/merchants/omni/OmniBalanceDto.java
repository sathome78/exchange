package me.exrates.model.dto.merchants.omni;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class OmniBalanceDto {

    private int propertyid;
    private String name;
    private BigDecimal balance;
    private BigDecimal reserved;
    private BigDecimal frozen;

    public static OmniBalanceDto getZeroBalancesDto(int propertyId, String name) {
        OmniBalanceDto omniBalanceDto = new OmniBalanceDto();
        omniBalanceDto.setPropertyid(propertyId);
        omniBalanceDto.setName(name);
        omniBalanceDto.setBalance(BigDecimal.ZERO);
        omniBalanceDto.setReserved(BigDecimal.ZERO);
        return omniBalanceDto;
    }

}
