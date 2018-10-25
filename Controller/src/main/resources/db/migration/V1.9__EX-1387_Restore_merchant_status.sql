alter table MERCHANT_CURRENCY add column withdraw_block_backup tinyint(1) default 0;
update MERCHANT_CURRENCY set withdraw_block_backup = withdraw_block;

alter table MERCHANT_CURRENCY add column refill_block_backup tinyint(1) default 0;
update MERCHANT_CURRENCY set refill_block_backup = refill_block;

