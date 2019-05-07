package me.exrates.service;

import me.exrates.model.enums.UserAgent;

public interface ApiService {
    String retrieveApplicationKey(UserAgent userAgent);

    boolean appKeyCheckEnabled();
}
