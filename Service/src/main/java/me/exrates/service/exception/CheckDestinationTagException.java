package me.exrates.service.exception;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class CheckDestinationTagException extends RuntimeException {

    private String fieldName;

    public CheckDestinationTagException(String message, String fieldName) {
        super(message);
        this.fieldName = fieldName;
    }

    public String getFieldName() {
        return fieldName;
    }
}
