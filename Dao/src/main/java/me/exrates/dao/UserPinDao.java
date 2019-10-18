package me.exrates.dao;

import me.exrates.model.enums.NotificationMessageEventEnum;

import java.util.Optional;

public interface UserPinDao {

    String save(String pin, String useEmail, NotificationMessageEventEnum eventEnum);

    Optional<String> findPin(String useEmail, NotificationMessageEventEnum eventEnum);

    void delete(String useEmail, NotificationMessageEventEnum event);
}
