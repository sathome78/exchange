package me.exrates.service.exception;

import lombok.NoArgsConstructor;

/**
 * Created by Maks on 22.07.2017.
 */
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
