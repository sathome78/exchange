package me.exrates.service.exception;

public class UserCommentNotFoundException extends RuntimeException {

    public UserCommentNotFoundException(String message) {
        super(message);
    }

}
