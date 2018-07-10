package me.exrates.dao.impl;

import me.exrates.dao.UserAlertsDao;
import me.exrates.model.dto.AlertDto;
import me.exrates.model.enums.AlertType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Time;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Created by Maks on 13.12.2017.
 * Updated by Vlad on 09.07.2018 (add type of alert - SYSTEM_MESSAGE_TO_USER)
 */
@Repository
public class UserAlertsDaoImpl implements UserAlertsDao {

    @Autowired
    private NamedParameterJdbcTemplate jdbcTemplate;

    private static RowMapper<AlertDto> getWalletsForOrderCancelDtoMapper = (rs, idx) -> {
        AlertDto alertDto = AlertDto
                .builder()
                .enabled(rs.getBoolean("enable"))
                .alertType(rs.getString("alert_type"))
                .build();
        Optional.ofNullable(rs.getTimestamp("launch_date"))
                .ifPresent(p->alertDto.setLaunchDateTime(p.toLocalDateTime()));
        Optional.ofNullable(rs.getTimestamp("time_of_start"))
                .ifPresent(p->alertDto.setEventStart(p.toLocalDateTime()));
        Optional.ofNullable(rs.getInt("length"))
                .ifPresent(alertDto::setLenghtOfWorks);
        return alertDto;
    };

    private static RowMapper<AlertDto> getAlertsSystemMessageForUsersDtoMapper = (rs, idx) -> {
        AlertDto alertDto = AlertDto
                .builder()
                .title(rs.getString("title"))
                .text(rs.getString("content"))
                .language(rs.getString("language"))
                .build();
        return alertDto;
    };

    @Override
    public List<AlertDto> getAlerts(boolean getOnlyEnabled) {
        String sql = "SELECT SA.* FROM SERVICE_ALERTS SA ";
        if (getOnlyEnabled) {
            sql = sql.concat(" WHERE SA.enable = true ");
        }
        return jdbcTemplate.query(sql, new HashMap<>(), getWalletsForOrderCancelDtoMapper);
    }

    @Override
    public boolean updateAlert(AlertDto alertDto) {
        String sql = "UPDATE SERVICE_ALERTS SA SET SA.enable = :enable, " +
                " SA.launch_date = :launch_date, SA.time_of_start = :time_of_start, SA.length = :length " +
                " WHERE SA.alert_type = :alert_type ";
        Map<String, Object> params = new HashMap<String, Object>() {{
            put("enable", alertDto.isEnabled());
            put("launch_date", alertDto.getLaunchDateTime());
            put("time_of_start", alertDto.getEventStart());
            put("length", alertDto.getLenghtOfWorks());
            put("alert_type", alertDto.getAlertType());
        }};
        return jdbcTemplate.update(sql, params) > 0;
    }

    @Override
    public boolean setEnable(String alertType, boolean enable) {
        String sql = "UPDATE SERVICE_ALERTS SA SET SA.enable = :enable " +
                " WHERE SA.alert_type = :alert_type ";
        Map<String, Object> params = new HashMap<String, Object>() {{
            put("enable", enable);
            put("alert_type", alertType);
        }};
        return jdbcTemplate.update(sql, params) > 0;
    }

    @Override
    public AlertDto getAlert(String name) {
        String sql = "SELECT * FROM SERVICE_ALERTS SA WHERE SA.alert_type = :name";
        Map<String, Object> params = new HashMap<String, Object>() {{
            put("name", name);
        }};
        return jdbcTemplate.queryForObject(sql, params, getWalletsForOrderCancelDtoMapper);
    }

    @Override
    public AlertDto getAlertSystemMessageToUser(String language){
        String sql = "SELECT title, content, language FROM SERVICE_ALERTS_SYSTEM_MESSAGE SASM WHERE SASM.language = :language";
        Map<String, Object> params = new HashMap<String, Object>() {{
            put("language", language);
        }};
        return jdbcTemplate.queryForObject(sql, params, getAlertsSystemMessageForUsersDtoMapper);
    }

    @Override
    public void setAlertSystemMessageToUser(AlertDto alertDto){
        String sql = "UPDATE SERVICE_ALERTS_SYSTEM_MESSAGE SASM SET SASM.title = :title, SASM.content= :content WHERE SASM.language = :language";
        Map<String, Object> params = new HashMap<String, Object>() {{
            put("title", alertDto.getTitle());
            put("content", alertDto.getText());
            put("language", alertDto.getLanguage());
        }};
        jdbcTemplate.update(sql, params);
    };

}
