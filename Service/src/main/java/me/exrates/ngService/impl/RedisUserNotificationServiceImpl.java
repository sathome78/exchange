package me.exrates.ngService.impl;

import me.exrates.model.dto.UserNotificationMessage;
import me.exrates.ngDao.RedisUserNotificationDao;
import me.exrates.ngService.RedisUserNotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collection;

@Service
public class RedisUserNotificationServiceImpl implements RedisUserNotificationService {

    private final RedisUserNotificationDao redisUserNotificationDao;

    @Autowired
    public RedisUserNotificationServiceImpl(RedisUserNotificationDao redisUserNotificationDao) {
        this.redisUserNotificationDao = redisUserNotificationDao;
    }

    @Override
    public Collection<UserNotificationMessage> findAllByUser(String email) {
        return redisUserNotificationDao.findAllByUser(email);
    }

    @Override
    public boolean saveUserNotification(String email, UserNotificationMessage userNotification) {
        return redisUserNotificationDao.saveUserNotification(email, userNotification);
    }

    @Override
    public void deleteUserNotification(String key) {
        redisUserNotificationDao.deleteUserNotification(key);
    }
}
