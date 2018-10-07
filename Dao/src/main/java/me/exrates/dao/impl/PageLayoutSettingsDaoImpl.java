package me.exrates.dao.impl;

import me.exrates.dao.PageLayoutSettingsDao;
import me.exrates.model.dto.PageLayoutSettingsDto;
import me.exrates.model.enums.ColorScheme;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.Types;
import java.util.Optional;

@Repository
public class PageLayoutSettingsDaoImpl implements PageLayoutSettingsDao {

    private static final String TABLE_NAME = "USER_PAGE_LAYOUT_SETTINGS";
    private static String ID_COL = "id";
    private static String USER_ID_COL = "user_id";
    private static String COLOR_SCHEME_COL = "color_scheme";
    private static String IS_LOW_COLOR_COL = "is_low_color_enabled";

    @Autowired
    @Qualifier(value = "masterTemplate")
    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;
    @Autowired
    @Qualifier(value = "slaveTemplate")
    private NamedParameterJdbcTemplate slaveJdbcTemplate;

    @Override
    public int save(PageLayoutSettingsDto settingsDto) {
        String sql = "INSERT INTO " + TABLE_NAME
                + " (" + String.join(", ", USER_ID_COL, COLOR_SCHEME_COL, IS_LOW_COLOR_COL) + ")"
                + " VALUES (:userId, :colorScheme, :isLowColor)"
                + " ON DUPLICATE KEY UPDATE " + COLOR_SCHEME_COL + "= :colorScheme, " + IS_LOW_COLOR_COL + "= :isLowColor";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        MapSqlParameterSource parameters = new MapSqlParameterSource()
                .addValue("user_id", settingsDto.getUserId())
                .addValue("colorScheme", settingsDto.getScheme(), Types.VARCHAR);
        int result = namedParameterJdbcTemplate.update(sql, parameters, keyHolder);
        int id = keyHolder.getKey().intValue();
        return result <= 0 ? 0 : id;
    }

    @Override
    public Optional<PageLayoutSettingsDto> findByUserId(Integer userId) {
        String sql = "SELECT * FROM " + TABLE_NAME + " WHERE  " + USER_ID_COL + " =:userId";
        MapSqlParameterSource parameters = new MapSqlParameterSource()
                .addValue("user_id", userId);
        return Optional.ofNullable(slaveJdbcTemplate.queryForObject(sql, parameters, getRowmapper()));
    }

    @Override
    public boolean delete(PageLayoutSettingsDto settingsDto) {
        String sql = "DELETE FROM " + TABLE_NAME + " WHERE  " + USER_ID_COL + " =:userId";
        MapSqlParameterSource parameters = new MapSqlParameterSource()
                .addValue("user_id", settingsDto.getUserId());

        return namedParameterJdbcTemplate.update(sql, parameters) > 0;
    }

    private RowMapper<PageLayoutSettingsDto> getRowmapper() {
        return (rs, rowNum) -> PageLayoutSettingsDto
                .builder()
                .id(rs.getInt(ID_COL))
                .userId(rs.getInt(USER_ID_COL))
                .scheme(ColorScheme.of(rs.getString(COLOR_SCHEME_COL)))
                .isLowColorEnabled(rs.getBoolean(IS_LOW_COLOR_COL))
                .build();
    }
}
