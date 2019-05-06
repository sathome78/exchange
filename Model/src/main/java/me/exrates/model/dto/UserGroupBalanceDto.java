package me.exrates.model.dto;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import me.exrates.model.enums.ReportGroupUserRole;

import java.math.BigDecimal;

@Getter @Setter
@ToString
public class UserGroupBalanceDto {
    private int curId;
    private String currency;
    private ReportGroupUserRole reportGroupUserRole;
    private BigDecimal totalBalance;

    public UserGroupBalanceDto.CurAndId getCurAndId(){return new UserGroupBalanceDto.CurAndId(curId, currency);}

    @EqualsAndHashCode(of = {"id"})
    @Getter @AllArgsConstructor
    public class CurAndId{
        private int id;
        private String currency;
    }
}
