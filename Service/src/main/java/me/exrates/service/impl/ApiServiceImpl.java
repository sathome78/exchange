package me.exrates.service.impl;

import me.exrates.dao.MobileAppDao;
import me.exrates.model.enums.UserAgent;
import me.exrates.service.ApiService;
import me.exrates.service.exception.api.KeyNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class ApiServiceImpl implements ApiService {
    @Autowired
    private MobileAppDao mobileAppDao;

    @Override
    public String retrieveApplicationKey(UserAgent userAgent) {
        Optional<String> searchResult = mobileAppDao.getAppKey(userAgent);
        return searchResult.orElseThrow(KeyNotFoundException::new);
    }

    @Override
    public boolean appKeyCheckEnabled() {
        return mobileAppDao.appKeyCheckEnabled();
    }
}
