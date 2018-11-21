package me.exrates.ngcontroller.model;

import lombok.Builder;
import lombok.Data;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;


@Data
@Builder
public class RefillPendingRequestDto implements RowMapper<RefillPendingRequestDto> {

    String date;
    String currency;
    double amount;
    double commission;
    String system;
    String status;


    @Override
    public RefillPendingRequestDto mapRow(ResultSet rs, int rowNum) throws SQLException {

        return RefillPendingRequestDto.builder()
                .date(rs.getString("date"))
                .currency(rs.getString(rs.getString("currency")))
                .amount(rs.getDouble("amount"))
                .commission(rs.getDouble("commission"))
                .system(rs.getString("system"))
                .status(rs.getString("status"))
                .build();
    }
}
