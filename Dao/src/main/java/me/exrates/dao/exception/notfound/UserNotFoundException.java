package me.exrates.dao.exception.notfound;

/**
 * Created by OLEG on 18.01.2017.
 */
public class UserNotFoundException extends NotFoundException {

    public UserNotFoundException() {
        super();
    }

    public UserNotFoundException(String message) {
        super(message);
    }

    public UserNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public UserNotFoundException(Throwable cause) {
        super(cause);
    }
}
