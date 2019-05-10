package me.exrates.model.dto.logging;

import lombok.Data;

@Data
public class LogsWrapper {

    private Object logsObject;
    private String processId;
    private LogsTypeEnum typeEnum;


    public LogsWrapper(Object logsObject, String processId, LogsTypeEnum typeEnum) {
        this.logsObject = logsObject;
        this.processId = processId;
        this.typeEnum = typeEnum;
    }
}
