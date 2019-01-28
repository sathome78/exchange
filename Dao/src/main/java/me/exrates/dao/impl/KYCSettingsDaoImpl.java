package me.exrates.dao.impl;

import me.exrates.dao.KYCSettingsDao;
import me.exrates.model.dto.kyc.KycCountryDto;
import me.exrates.model.dto.kyc.KycLanguageDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class KYCSettingsDaoImpl implements KYCSettingsDao {

    @Autowired
    private JdbcTemplate jdbcTemplate;

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
}