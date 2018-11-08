package me.exrates.dao;

import me.exrates.dao.exception.ControlPhraseNotFoundException;

public interface ControlPhraseDao {
    String getByUserId(long userId) throws ControlPhraseNotFoundException;
    void updatePharese(long userId, String phrase);
}
