UPDATE WALLET as wal
JOIN BCH_HARD_FORK_BALANCE_SNAPSHOT as bch_fork
ON wal.user_id = bch_fork.user_id
  SET wal.active_balance = bch_fork.active_balance,
  wal.reserved_balance = bch_fork.reserve_balance
WHERE wal.currency_id = (SELECT id FROM CURRENCY WHERE name='BSV');