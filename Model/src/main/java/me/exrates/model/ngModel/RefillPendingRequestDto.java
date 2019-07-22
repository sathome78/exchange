package me.exrates.model.ngModel;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;


@Data
@Builder
@AllArgsConstructor
public class RefillPendingRequestDto {

    private Integer requestId;
    private String date;
    private String currency;
    private double amount;
    private double commission;
    private String system;
    private String status;
    private String operation;
    private List<Map<String, Object>> buttons;

}
