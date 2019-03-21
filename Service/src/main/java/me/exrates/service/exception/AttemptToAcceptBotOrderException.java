package me.exrates.service.exception;

import me.exrates.service.exception.process.OrderAcceptionException;

public class AttemptToAcceptBotOrderException extends OrderAcceptionException {
    public AttemptToAcceptBotOrderException(String message) {
        super(message);
    }
}
