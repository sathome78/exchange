package me.exrates.ngcontroller.exception;

public class NgDashboardException extends RuntimeException {
    private Integer code;
    public NgDashboardException(String message) {
        super(message);
    }

    public NgDashboardException(String message, Integer code) {
        super(message);
        this.code = code;
    }

    public NgDashboardException(String message, Throwable cause) {
        super(message, cause);
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }
}
