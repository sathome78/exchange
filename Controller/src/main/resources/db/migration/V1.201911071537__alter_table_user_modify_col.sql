ALTER TABLE USER
    MODIFY verification_required tinyint default 0;
ALTER TABLE USER
    MODIFY has_trade_privileges tinyint default 0;
