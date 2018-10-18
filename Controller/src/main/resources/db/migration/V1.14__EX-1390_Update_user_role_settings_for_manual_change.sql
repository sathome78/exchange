UPDATE USER_ROLE_SETTINGS us_role_settings INNER JOIN USER_ROLE us_role ON us_role_settings.user_role_id=us_role.id
SET manual_change_allowed=0 WHERE us_role.name!='USER' AND us_role.name!='VIP_USER';
