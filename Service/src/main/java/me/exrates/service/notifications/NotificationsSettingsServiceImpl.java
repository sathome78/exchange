package me.exrates.service.notifications;

import lombok.extern.log4j.Log4j2;
import me.exrates.dao.NotificationUserSettingsDao;
import me.exrates.model.dto.NotificationsUserSetting;
import me.exrates.model.enums.NotificationMessageEventEnum;
import me.exrates.model.enums.NotificationTypeEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

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
	public NotificationsUserSetting getByUserAndEvent(int userId, NotificationMessageEventEnum event) {
		return settingsDao.getByUserAndEvent(userId, event);
	}

	@Override
	public void createOrUpdate(NotificationsUserSetting setting) {
		if (getByUserAndEvent(setting.getUserId(), setting.getNotificationMessageEventEnum()) == null) {
			settingsDao.create(setting);
		} else {
			settingsDao.update(setting);
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

	public Map<NotificationMessageEventEnum, NotificationTypeEnum> getUser2FactorSettings(int userId) {
		Map<NotificationMessageEventEnum, NotificationTypeEnum> settings = new HashMap<>();
		Arrays.asList(NotificationMessageEventEnum.values()).forEach(value -> {
					NotificationTypeEnum typeEnum = null;
					NotificationsUserSetting notification = getByUserAndEvent(userId, value);
					if (null != notification) {
						typeEnum = NotificationTypeEnum.convert(notification.getNotificatorId());
					}
					settings.put(value, typeEnum);
				}
		);
		return settings;
	}

	public void updateUser2FactorSettings(int userId, Map<NotificationMessageEventEnum, NotificationTypeEnum> newSettings) {
		Arrays.asList(NotificationMessageEventEnum.values()).forEach(type -> {
					NotificationsUserSetting notification = getByUserAndEvent(userId, type);
					if (null != notification) {
						notification.setNotificatorId(newSettings.get(type).getCode());
						createOrUpdate(notification);
					} else if (newSettings.get(type) == null) {
						settingsDao.delete(userId, type);
					} else {
						notification = new NotificationsUserSetting()
								.builder()
								.userId(userId)
								.notificationMessageEventEnum(type)
								.notificatorId(newSettings.get(type).getCode())
								.build();
						createOrUpdate(notification);
					}

				}
		);
	}


}
