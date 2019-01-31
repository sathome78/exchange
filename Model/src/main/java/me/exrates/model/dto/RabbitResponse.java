package me.exrates.model.dto;

public class RabbitResponse {

    private boolean success;
    private String processId;

    public RabbitResponse() {
    }

    public RabbitResponse(boolean success, String processId) {
        this.success = success;
        this.processId = processId;
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
