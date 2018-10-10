package me.exrates.service.notifications;

import lombok.extern.log4j.Log4j2;
import me.exrates.dao.NotificationUserSettingsDao;
import me.exrates.model.User;
import me.exrates.model.dto.NotificationsUserSetting;
import me.exrates.model.enums.NotificationMessageEventEnum;
import me.exrates.model.enums.NotificationTypeEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by Maks on 08.10.2017.
 */
@Log4j2(topic = "message_notify")
@Component
public class NotificationsSettingsServiceImpl implements NotificationsSettingsService {

	@Autowired
	private NotificationUserSettingsDao settingsDao;
	@Autowired
	private NotificatorsService notificatorsService;


	@Override
	public List<NotificationsUserSetting> getByUserAndEvents(int userId, NotificationMessageEventEnum... events) {
		return settingsDao.getByUserAndEvents(userId, events);
	}

	@Override
	public NotificationsUserSetting getByUserAndEvent(int userId, NotificationMessageEventEnum event) {
		return settingsDao.getByUserAndEvent(userId, event);
	}

	@Override
	public void createOrUpdate(NotificationsUserSetting setting) {
		Optional<NotificationsUserSetting> found =
				Optional.ofNullable(getByUserAndEvent(setting.getUserId(), setting.getNotificationMessageEventEnum()));
		if (found.isPresent() ){
			setting.setId(found.get().getId());
			settingsDao.update(setting);
		} else {
			settingsDao.create(setting);
		}
	}

	@Override
	public Map<String, Object> get2faOptionsForUser(int userId) {
		Map<String, Object> map = new HashMap<>();
		map.put("notificators", notificatorsService.getAllNotificators());
		map.put("events", Arrays.asList(NotificationMessageEventEnum.values()));
		map.put("settings", getSettingsMap(userId));
		map.put("subscriptions", notificatorsService.getSubscriptions(userId));
		return map;
	}

	@Override
	public Map<Integer, NotificationsUserSetting> getSettingsMap(int userId) {
		HashMap<Integer, NotificationsUserSetting> settingsMap = new HashMap<>();
		Arrays.asList(NotificationMessageEventEnum.values()).forEach(p -> {
					settingsMap.put(p.getCode(), getByUserAndEvent(userId, p));
				}
		);
		return settingsMap;
	}

	@Override
	public Map<NotificationMessageEventEnum, NotificationTypeEnum> getUser2FactorSettings(int userId) {
		Map<NotificationMessageEventEnum, NotificationTypeEnum> settings = new HashMap<>();
		Arrays.asList(NotificationMessageEventEnum.values()).forEach(value -> {
					NotificationTypeEnum typeEnum = null;
					NotificationsUserSetting notification = getByUserAndEvent(userId, value);
					if (null != notification.getNotificatorId()) {
						typeEnum = NotificationTypeEnum.convert(notification.getNotificatorId());
					}
					settings.put(value, typeEnum);
				}
		);
		return settings;
	}

	@Override
	public Map<NotificationMessageEventEnum, Boolean> getUserTwoFASettings(User user) {
		return settingsDao.getByUserAndEvents(user.getId(), NotificationMessageEventEnum.LOGIN,
						NotificationMessageEventEnum.WITHDRAW, NotificationMessageEventEnum.TRANSFER)
				.stream()
				.collect(Collectors.toMap(NotificationsUserSetting::getNotificationMessageEventEnum,
						NotificationsUserSetting::isEnabled));
	}

	@Override
	public boolean isGoogleTwoFALoginEnabled(User user) {
		Map<NotificationMessageEventEnum, Boolean> settings = getUserTwoFASettings(user);
		return settings.containsKey(NotificationMessageEventEnum.LOGIN) &&
				settings.get(NotificationMessageEventEnum.LOGIN);
	}

	@Override
	public void updateUser2FactorSettings(int userId, Map<String, String> body) {
		Map<NotificationMessageEventEnum, NotificationTypeEnum> newSettings = new HashMap<>();
		body.forEach((key, value) -> {
			if (null != value){
				newSettings.put(NotificationMessageEventEnum.convert(key.toUpperCase()), NotificationTypeEnum.convert(value.toUpperCase()));
			} else {
				newSettings.put(NotificationMessageEventEnum.convert(key.toUpperCase()), null);
			}
		});
		Arrays.asList(NotificationMessageEventEnum.values()).forEach(type -> {
			NotificationsUserSetting notification = getByUserAndEvent(userId, type);
			if (null != notification) {
				if (null != newSettings.get(type)) {
					notification.setNotificatorId(newSettings.get(type).getCode());
				} else {
					notification.setNotificatorId(null);
				}
				settingsDao.update(notification);
			} else {
				settingsDao.create(NotificationsUserSetting
										.builder()
										.userId(userId)
										.notificationMessageEventEnum(type)
										.notificatorId(null != newSettings.get(type) ? newSettings.get(type).getCode() : null)
										.build());
			}
		});
	}


}
