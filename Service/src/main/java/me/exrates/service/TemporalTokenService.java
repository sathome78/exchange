package me.exrates.service;

import me.exrates.model.TemporalToken;

public interface TemporalTokenService {

    boolean updateTemporalToken(TemporalToken temporalToken);

    void deleteTemporalToken(String temporalToken);
}
