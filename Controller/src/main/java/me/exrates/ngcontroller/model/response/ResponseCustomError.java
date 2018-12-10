package me.exrates.ngcontroller.model.response;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ResponseCustomError {

    private int code;

    private String message;

    public ResponseCustomError(String message) {
        this.message = message;
    }

    @JsonProperty("code")
    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    @JsonProperty("message")
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
