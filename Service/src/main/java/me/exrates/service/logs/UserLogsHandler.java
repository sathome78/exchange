package me.exrates.service.logs;

import me.exrates.model.dto.logging.LogsWrapper;

public interface UserLogsHandler {

    void onUserLogEvent(LogsWrapper logsWrapper);
}
