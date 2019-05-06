package me.exrates.model.dto;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import me.exrates.model.enums.UserRole;

import java.math.BigDecimal;

@Getter @Setter
@ToString
public class UserRoleBalanceDto {
    //currency id added
    private int curId;
    private String currency;
    private UserRole userRole;
    private BigDecimal totalBalance;

    public CurAndId getCurAndId(){return new CurAndId(curId, currency);}

    //static class for wrapping a  tuple
    @EqualsAndHashCode(of = {"id"})
    @Getter @AllArgsConstructor
    public class CurAndId{
        private int id;
        private String currency;
    }
}
