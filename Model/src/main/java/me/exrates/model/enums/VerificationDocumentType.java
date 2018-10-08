package me.exrates.model.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum VerificationDocumentType {

    PASSPORT, IDENTITY_CARD, DRIVER_LICENSE;

    @JsonCreator
    public static VerificationDocumentType of(String value) {
        if (value.equalsIgnoreCase(PASSPORT.toString())) {
            return PASSPORT;
        } else if (value.equalsIgnoreCase(IDENTITY_CARD.toString())) {
            return IDENTITY_CARD;
        } else if (value.equalsIgnoreCase(DRIVER_LICENSE.toString())) {
            return DRIVER_LICENSE;
        }
        return null;
    }

    @JsonValue
    public String getValue() {
        return this.toString();
    }
}
