package me.exrates.model.dto;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import me.exrates.model.enums.OperationType;
import me.exrates.model.enums.UserRole;

import java.math.BigDecimal;

@Data
@Builder
public class NormalizeAmountDto {
    private Integer userId;
    private BigDecimal amount;
    private OperationType type;
    private Integer currencyId;
    private Integer merchantId;
    private String destinationTag;
    private UserRole userRole;
}
