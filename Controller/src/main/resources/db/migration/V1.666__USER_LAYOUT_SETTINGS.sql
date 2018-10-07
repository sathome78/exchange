CREATE TABLE IF NOT EXISTS `USER_PAGE_LAYOUT_SETTINGS` (
  id INT AUTO_INCREMENT,
  user_id INT NOT NULL PRIMARY KEY,
  color_scheme ENUM('LIGHT', 'DARK') DEFAULT 'LIGHT',
  is_low_color_enabled BOOLEAN DEFAULT FALSE,
  constraint `USER_PAGE_LAYOUT_SETTINGS_user_fk_1`
  foreign key (user_id) references USER (id)
);