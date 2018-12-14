package me.exrates.ngcontroller.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;


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

}