package me.exrates.dao.impl;

import me.exrates.dao.KYCSettingsDao;
import me.exrates.model.dto.kyc.KycCountryDto;
import me.exrates.model.dto.kyc.KycLanguageDto;
import me.exrates.model.ngExceptions.NgDashboardException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class KYCSettingsDaoImpl implements KYCSettingsDao {

    @Autowired
    private NamedParameterJdbcTemplate jdbcTemplate;

    @Override
    public List<KycCountryDto> getCountriesDictionary() {
        final String sql = "SELECT kcc.country_name, kcc.country_code FROM KYC_COUNTRY_CODES kcc";

        return jdbcTemplate.query(sql, (rs, i) -> KycCountryDto.builder()
                .countryName(rs.getString("country_name"))
                .countryCode(rs.getString("country_code"))
                .build());
    }

    @Override
    public List<KycLanguageDto> getLanguagesDictionary() {
        final String sql = "SELECT klc.language_name, klc.language_code FROM KYC_LANGUAGE_CODES klc";

        return jdbcTemplate.query(sql, (rs, i) -> KycLanguageDto.builder()
                .languageName(rs.getString("language_name"))
                .languageCode(rs.getString("language_code"))
                .build());
    }

    @Override
    public KycCountryDto getCountryByCode(String code) {
        final String sql = "SELECT kcc.country_name, kcc.country_code FROM KYC_COUNTRY_CODES kcc WHERE kcc.country_code = :code ";

        Map<String, String> params = new HashMap<String, String>() {
            {
                put("code", code);
            }
        };
        try {
            return jdbcTemplate.queryForObject(sql, params, (rs, rowNum) -> {
                KycCountryDto dto = new KycCountryDto();
                dto.setCountryName(rs.getString("country_name"));
                dto.setCountryCode(rs.getString("country_code"));
                return dto;
            });

        } catch (Exception e) {
            throw new NgDashboardException("Error getting country by code");
        }

    }
}