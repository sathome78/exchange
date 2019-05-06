package me.exrates.model.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import me.exrates.model.enums.OperationType;

import java.math.BigDecimal;

@Getter @Setter
@ToString
public class TransferRequestParamsDto {
    private OperationType operationType;
    private Integer merchant; //not use in new dashboard
    private Integer currency;
    private BigDecimal sum;
    private String recipient;
    private String pin;
    private String type;
}
