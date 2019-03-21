package me.exrates.dao.impl;

import lombok.extern.log4j.Log4j2;
import me.exrates.dao.MerchantSpecParamsDao;
import me.exrates.model.condition.MonolitConditional;
import me.exrates.model.dto.MerchantSpecParamDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Conditional;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by maks on 09.06.2017.
 */

@Log4j2
@Repository
@Conditional(MonolitConditional.class)
public class MerchantSpecParamsDaoImpl implements MerchantSpecParamsDao {

    @Autowired
    @Qualifier(value = "masterTemplate")
    private NamedParameterJdbcTemplate jdbcTemplate;

    @Override
    public MerchantSpecParamDto getByMerchantNameAndParamName(String merchantName, String paramName) {
        String sql = " SELECT MSP.* FROM MERCHANT_SPEC_PARAMETERS MSP " +
                " INNER JOIN MERCHANT M ON M.id = MSP.merchant_id " +
                " WHERE M.name = :merchant_name AND MSP.param_name = :param_name ";
        Map<String, Object> params = new HashMap<>();
        params.put("merchant_name", merchantName);
        params.put("param_name", paramName);
        try {
            return jdbcTemplate.queryForObject(sql, params, (rs, rowNum) -> {
                MerchantSpecParamDto dto = new MerchantSpecParamDto();
                dto.setId(rs.getInt("id"));
                dto.setMerchantId(rs.getInt("merchant_id"));
                dto.setParamName(rs.getString("param_name"));
                dto.setParamValue(rs.getString("param_value"));
                return dto;
            });
        } catch (DataAccessException e) {
            log.error(e);
            return null;
        }
    }

    @Override
    public MerchantSpecParamDto getByMerchantIdAndParamName(int merchantId, String paramName) {
        String sql = " SELECT MSP.* FROM MERCHANT_SPEC_PARAMETERS MSP " +
                " INNER JOIN MERCHANT M ON M.id = MSP.merchant_id " +
                " WHERE M.id = :merchant_id AND MSP.param_name = :param_name ";
        Map<String, Object> params = new HashMap<>();
        params.put("merchant_id", merchantId);
        params.put("param_name", paramName);
        try {
            return jdbcTemplate.queryForObject(sql, params, (rs, rowNum) -> {
                MerchantSpecParamDto dto = new MerchantSpecParamDto();
                dto.setId(rs.getInt("id"));
                dto.setMerchantId(rs.getInt("merchant_id"));
                dto.setParamName(rs.getString("param_name"));
                dto.setParamValue(rs.getString("param_value"));
                return dto;
            });
        } catch (DataAccessException e) {
            log.error(e);
            return null;
        }
    }

    @Override
    public boolean updateParam(String merchantName, String paramName, String newValue) {
        String sql = " UPDATE MERCHANT_SPEC_PARAMETERS MSP " +
                " INNER JOIN MERCHANT M ON M.id = MSP.merchant_id " +
                " SET MSP.param_value = :new_value " +
                " WHERE M.name = :merchant_name AND MSP.param_name = :param_name ";
        Map<String, Object> params = new HashMap<>();
        params.put("merchant_name", merchantName);
        params.put("param_name", paramName);
        params.put("new_value", newValue);
        return jdbcTemplate.update(sql, params) > 0;
    }
}
