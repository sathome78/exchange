package me.exrates.service.impl;

import me.exrates.dao.TemporalTokenDao;
import me.exrates.service.TemporalTokenService;
import me.exrates.model.TemporalToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TemporalTokenServiceImpl implements TemporalTokenService {

    @Autowired
    TemporalTokenDao temporalTokenDao;
    @Override
    public boolean updateTemporalToken(TemporalToken temporalToken) {
        return false;
    }
}
