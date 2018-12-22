ALTER TABLE CURRENCY_LIMIT ADD COLUMN refill_review_limit_usd_once DECIMAL(30, 8) default NULL;
ALTER TABLE CURRENCY_LIMIT ADD COLUMN refill_review_limit_coin_once DECIMAL(30, 8) default NULL;
ALTER TABLE CURRENCY_LIMIT ADD COLUMN refill_review_limit_usd_day DECIMAL(30, 8) default NULL;
ALTER TABLE CURRENCY_LIMIT ADD COLUMN refill_review_limit_coins_day DECIMAL(30, 8) default NULL;