package me.exrates.model.dto.kyc;

import lombok.Getter;

import java.util.stream.Stream;

public enum EventStatus {

    ACCEPTED("verification.accepted"),
    DECLINED("verification.declined"),
    CANCELLED("verification.cancelled"),
    CHANGED("verification.status.changed"),

    PENDING("request.pending"),
    INVALID("request.invalid"),
    TIMEOUT("request.timeout"),
    UNAUTHORIZED("request.unauthorized");

    @Getter
    private String event;

    EventStatus(String event) {
        this.event = event;
    }

    public static EventStatus of(String event) {
        return Stream.of(EventStatus.values())
                .filter(eventStatus -> eventStatus.event.equals(event))
                .findFirst()
                .orElseThrow(() -> new RuntimeException(String.format("Event with status %s has not found", event)));
    }
}