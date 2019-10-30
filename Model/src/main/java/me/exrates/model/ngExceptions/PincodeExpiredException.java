package me.exrates.model.ngExceptions;

import lombok.Getter;
import me.exrates.model.constants.ErrorApiTitles;

@Getter
public class PincodeExpiredException extends RuntimeException {

    private final String email;

    public PincodeExpiredException(String email) {
        super("Smth dangerous happened to " + email);
        this.email = email;
    }

    public NgResponseException toErrorResponse() {
        String message = String.format("Pincode for user with email: %s might already become expired", email);
        return new NgResponseException(ErrorApiTitles.EMAIL_AUTHORIZATION_PIN_EXPIRED, message);
    }
}
