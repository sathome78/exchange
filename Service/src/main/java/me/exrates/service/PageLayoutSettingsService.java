package me.exrates.service;

import me.exrates.model.User;
import me.exrates.model.dto.PageLayoutSettingsDto;
import me.exrates.model.enums.ColorScheme;

public interface PageLayoutSettingsService {

    PageLayoutSettingsDto save(PageLayoutSettingsDto settingsDto);

    PageLayoutSettingsDto findByUser(User user);

    boolean delete(PageLayoutSettingsDto settingsDto);

    ColorScheme getColorScheme(User user);

    boolean toggleLowColorMode(User user, boolean enabled);
}
