DELETE FROM WALLET WHERE WALLET.active_balance = 0
                         AND WALLET.reserved_balance = 0
                         AND WALLET.user_id = 152
                         AND NOT EXISTS(SELECT id FROM TRANSACTION WHERE user_wallet_id = WALLET.id)