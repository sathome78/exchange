package me.exrates.dao;

import me.exrates.dao.exception.ControlPhraseNotFoundException;
import me.exrates.dao.exception.PhraseNotAllowedException;

public interface ControlPhraseDao {
    String getByUserId(long userId);

    void updatePhrase(long userId, String phrase) throws PhraseNotAllowedException;

    void deletePhrase(long userId);

    void addPhrase(long userId, String phrase);
}
