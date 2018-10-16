package me.exrates.controller.exception;

/**
 * Created by Valk
 */
public class FileLoadingException extends RuntimeException {
    public FileLoadingException(String message) {
        super(message);
    }

    public FileLoadingException() {
        super("Failed to upload file");
    }
}
