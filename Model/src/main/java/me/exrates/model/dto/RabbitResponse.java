package me.exrates.model.dto;

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

    public String getProcessId() {
        return processId;
    }

    public void setProcessId(String processId) {
        this.processId = processId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

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
