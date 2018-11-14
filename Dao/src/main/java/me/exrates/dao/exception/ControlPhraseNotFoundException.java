package me.exrates.dao.exception;

public class ControlPhraseNotFoundException extends Exception {
    public ControlPhraseNotFoundException(long userId){
        super("Control phrase not found for user with id = " + userId);
    }
}
