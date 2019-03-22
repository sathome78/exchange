package me.exrates.dao.exception.notfound;

/**
 * Created by Valk on 17.05.2016.
 */
public class OrderNotFoundException extends NotFoundException {

    public OrderNotFoundException() {
        super();
    }

    public OrderNotFoundException(String message) {
        super(message);
    }

    public OrderNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public OrderNotFoundException(Throwable cause) {
        super(cause);
    }
}
