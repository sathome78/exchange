package me.exrates.dao;

import me.exrates.model.dto.AlertDto;

import java.util.List;

public interface UserAlertsDao {
    List<AlertDto> getAlerts(boolean getOnlyEnabled);

    boolean updateAlert(AlertDto alertDto);

    boolean setEnable(String alertType, boolean enable);

    AlertDto getAlert(String name);
}
