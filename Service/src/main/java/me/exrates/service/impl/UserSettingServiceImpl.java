package me.exrates.service.impl;

import me.exrates.dao.UserSettingsDao;
import me.exrates.service.UserSettingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserSettingServiceImpl implements UserSettingService {

    @Autowired
    private UserSettingsDao userSettingsDao;

    @Override
    public int addCallbackURL(final int userId, final String callbackURL) {
        return userSettingsDao.addCallBackUrl(userId, callbackURL);

    }

    @Override
    public String getCallbackURL(final int userId) {
        return userSettingsDao.getCallBackURLByUserId(userId);
    }

    @Override
    public int updateCallbackURL(final int userId, final String callbackURL) {
        return userSettingsDao.updateCallbackURL(userId, callbackURL);
    }
}
