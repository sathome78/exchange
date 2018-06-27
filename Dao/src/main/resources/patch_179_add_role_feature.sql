CREATE TABLE USER_ROLE_REPORT_GROUP_FEATURE
(
  id INT PRIMARY KEY,
  name VARCHAR(20) NOT NULL
);
CREATE UNIQUE INDEX user_role_report_group_feature_name_uindex ON USER_ROLE_REPORT_GROUP_FEATURE (name);

INSERT INTO USER_ROLE_REPORT_GROUP_FEATURE (id, name) VALUES
  (1, 'ADMIN'), (2, 'USER'), (3, 'TRADER'), (4, 'BOT');

ALTER TABLE USER_ROLE ADD user_role_report_group_feature_id INT NULL;
ALTER TABLE USER_ROLE
  ADD CONSTRAINT user_role___fk_report_group_feature
FOREIGN KEY (user_role_report_group_feature_id) REFERENCES USER_ROLE_REPORT_GROUP_FEATURE (id);

UPDATE USER_ROLE SET user_role_report_group_feature_id = 1 where user_role_business_feature_id = 1;
UPDATE USER_ROLE SET user_role_report_group_feature_id = 2 where user_role_business_feature_id IN (2, 3, 4);
UPDATE USER_ROLE SET user_role_report_group_feature_id = 3 where user_role_business_feature_id = 5;
UPDATE USER_ROLE SET user_role_report_group_feature_id = 4 where user_role_business_feature_id = 6;

CREATE UNIQUE INDEX currency_name_index ON CURRENCY (name);