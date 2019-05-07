package me.exrates.dao.exception.notfound;

public class OrderNotFoundException extends NotFoundException {

    public OrderNotFoundException(String message) {
        super(message);
    }
}
