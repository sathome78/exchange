package me.exrates.service.cache;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class SettingsServiceImpl implements SettingsService {

    private final SettingsRedisRepository repository;

    @Autowired
    public SettingsServiceImpl(SettingsRedisRepository repository) {
        this.repository = repository;
    }

    @Override
    public Map<String, String> getEmailsSender() {
        return repository.getEmailConfigs();
    }
}
