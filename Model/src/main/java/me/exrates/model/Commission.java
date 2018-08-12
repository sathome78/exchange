package me.exrates.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import me.exrates.model.enums.OperationType;
import me.exrates.model.enums.UserRole;

import java.math.BigDecimal;
import java.util.Date;

@Getter @Setter
@ToString
@NoArgsConstructor
public class Commission {
    private int id;
    private OperationType operationType;
    private BigDecimal value;
    private BigDecimal unitValue;
    private Date dateOfChange;
    private UserRole userRole;

    public Commission(int id) {
        this.id = id;
    }
}