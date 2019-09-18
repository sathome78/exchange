package me.exrates.service.cache;

import java.util.Map;

public interface SettingsRedisRepository {
    Map<String, String> getEmailConfigs();
}
