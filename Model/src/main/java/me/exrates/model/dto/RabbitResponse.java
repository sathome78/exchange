package me.exrates.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class RabbitResponse {

    private boolean success;
    private String processId;
    private String message;

    public RabbitResponse() {
    }

    public RabbitResponse(boolean success, String processId) {
        this.success = success;
        this.processId = processId;
    }

    public RabbitResponse(boolean success, String processId, String message) {
        this.success = success;
        this.processId = processId;
        this.message = message;
    }

    @JsonProperty("processId")
    public String getProcessId() {
        return processId;
    }

    public void setProcessId(String processId) {
        this.processId = processId;
    }

    @JsonProperty("message")
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @JsonProperty("success")
    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getProccesId() {
        return processId;
    }

    public void setProccesId(String processId) {
        this.processId = processId;
    }
}
