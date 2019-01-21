package me.exrates.model.dto.kyc;

import lombok.Getter;

public enum EventStatus {

    ACCEPTED("verification.accepted"),
    DECLINED("verification.declined"),
    CANCELLED("verification.cancelled"),

    PENDING("request.pending"),
    INVALID("request.invalid"),
    TIMEOUT("request.timeout"),
    UNAUTHORIZED("request.unauthorized");

    @Getter
    private String event;

    EventStatus(String event) {
        this.event = event;
    }
}