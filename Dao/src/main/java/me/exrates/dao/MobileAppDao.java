package me.exrates.dao;

import me.exrates.model.enums.UserAgent;

import java.util.Optional;

public interface MobileAppDao {
    Optional<String> getAppKey(UserAgent userAgent);

    boolean appKeyCheckEnabled();
}
