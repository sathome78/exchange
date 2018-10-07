package me.exrates.service.impl;

import me.exrates.dao.PageLayoutSettingsDao;
import me.exrates.model.User;
import me.exrates.model.dto.PageLayoutSettingsDto;
import me.exrates.model.enums.ColorScheme;
import me.exrates.service.PageLayoutSettingsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PageLayoutSettingsServiceImpl implements PageLayoutSettingsService {

    public final PageLayoutSettingsDao pageLayoutSettingsDao;

    @Autowired
    public PageLayoutSettingsServiceImpl(PageLayoutSettingsDao pageLayoutSettingsDao) {
        this.pageLayoutSettingsDao = pageLayoutSettingsDao;
    }

    @Override
    public PageLayoutSettingsDto save(PageLayoutSettingsDto settingsDto) {
        int result = pageLayoutSettingsDao.save(settingsDto);
        if (result > 0) {
            settingsDto.setId(result);
            return settingsDto;
        }
        return null;
    }

    @Override
    public PageLayoutSettingsDto findByUser(User user) {
        return pageLayoutSettingsDao.findByUserId(user.getId()).orElse(null);
    }

    @Override
    public boolean delete(PageLayoutSettingsDto settingsDto) {
        return pageLayoutSettingsDao.delete(settingsDto);
    }

    @Override
    public ColorScheme getColorScheme(User user) {
        PageLayoutSettingsDto userSettings = findByUser(user);
        return userSettings == null ? ColorScheme.LIGHT : userSettings.getScheme();
    }

    @Override
    public boolean toggleLowColorMode(User user, boolean enabled) {
        PageLayoutSettingsDto userSettings = findByUser(user);
        if (userSettings != null) {
            userSettings.setLowColorEnabled(enabled);
            return pageLayoutSettingsDao.save(userSettings) > 0;
        }
        throw new RuntimeException("User Color Settings not set");
    }
}
