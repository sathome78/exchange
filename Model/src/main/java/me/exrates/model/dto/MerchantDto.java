package me.exrates.model.dto;

import lombok.Builder;
import lombok.Data;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

@Data
@Builder
public class MerchantDto implements RowMapper<MerchantDto> {
    private String merchantDescription;
    private String merchantProcess;

    @Override
    public MerchantDto mapRow(ResultSet rs, int rowNum) throws SQLException {
        return null;
    }
}
