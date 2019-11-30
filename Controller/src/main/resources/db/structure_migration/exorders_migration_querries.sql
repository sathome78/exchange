# add tables

CREATE TABLE IF NOT EXISTS ORDERS LIKE EXORDERS;
CREATE TABLE IF NOT EXISTS BOT_ORDERS LIKE EXORDERS;

CREATE TABLE RATES
(
    currency_pair_id int(5) not null primary key,
    previous_rate double(18,9) null,
    last_rate double(18,9) null,
    modified timestamp default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP
);


# sequence gen

ALTER TABLE BOT_ORDERS AUTO_INCREMENT = 1500000000;

# add triggers

create trigger after_order_complete_or_closed
    after UPDATE
    on EXORDERS
    for each row
BEGIN
    SET @BOT_ROLE_ID = 10;
    SET @OPEN_ORDER_STATUS = 2;
    SET @CLOSED_ORDER_STATUS = 3;

    IF (NEW.status_id <> @OPEN_ORDER_STATUS) THEN

        SET @CREATOR_ROLE_ID = (SELECT roleid FROM USER WHERE id = NEW.user_id);

        IF (@CREATOR_ROLE_ID = @BOT_ROLE_ID) THEN
            INSERT INTO BOT_ORDERS
            (id, user_id, currency_pair_id, operation_type_id, exrate, amount_base, amount_convert, commission_id, commission_fixed_amount,
             user_acceptor_id, date_creation, date_acception, status_id, status_modification_date, order_source_id, counter_order_id, counter_order_type, base_type)
            VALUES (NEW.id, NEW.user_id, NEW.currency_pair_id, NEW.operation_type_id, NEW.exrate, NEW.amount_base, NEW.amount_convert, NEW.commission_id,
                    NEW.commission_fixed_amount, NEW.user_acceptor_id, NEW.date_creation, NEW.date_acception, NEW.status_id, NEW.status_modification_date, NEW.order_source_id,
                    NEW.counter_order_id, NEW.counter_order_type, NEW.base_type);
        END IF;

        IF (@CREATOR_ROLE_ID <> @BOT_ROLE_ID) THEN
            INSERT INTO ORDERS
            (id, user_id, currency_pair_id, operation_type_id, exrate, amount_base, amount_convert, commission_id, commission_fixed_amount,
             user_acceptor_id, date_creation, date_acception, status_id, status_modification_date, order_source_id, counter_order_id, counter_order_type, base_type)
            VALUES (NEW.id, NEW.user_id, NEW.currency_pair_id, NEW.operation_type_id, NEW.exrate, NEW.amount_base, NEW.amount_convert, NEW.commission_id,
                    NEW.commission_fixed_amount, NEW.user_acceptor_id, NEW.date_creation, NEW.date_acception, NEW.status_id, NEW.status_modification_date, NEW.order_source_id,
                    NEW.counter_order_id, NEW.counter_order_type, NEW.base_type);

            IF (@CLOSED_ORDER_STATUS = NEW.status_id) THEN
                INSERT INTO RATES (currency_pair_id, previous_rate, last_rate)
                VALUES (NEW.currency_pair_id, 0, NEW.exrate)
                ON DUPLICATE KEY UPDATE
                                     previous_rate = last_rate,
                                     last_rate = NEW.exrate;
            END IF ;

        END IF;

    END IF;
END;


create trigger after_bot_order_inserted
    after INSERT
    on BOT_ORDERS
    for each row
BEGIN

    INSERT INTO RATES (currency_pair_id, previous_rate, last_rate)
    VALUES (NEW.currency_pair_id, 0, NEW.exrate)
    ON DUPLICATE KEY UPDATE
                         previous_rate = last_rate,
                         last_rate = NEW.exrate;

END;

# constraints

alter table REFERRAL_TRANSACTION
    drop foreign key REFERRAL_TRANSACTION_ibfk_1;

alter table EXORDERS drop foreign key __exorders___fk_source_id;

alter table EXORDERS
    add constraint __exorders___fk_source_id
        foreign key (order_source_id) references EXORDERS (id)
            on delete set null;

alter table STOP_ORDERS
    drop foreign key fk_stop_orders_CHILD_ORDER;

# move data, not necessary, but recommended

# INSERT INTO ORDERS
#     (SELECT * FROM EXORDERS
#         JOIN USER U ON  U.id = EXORDERS.user_id AND U.roleid <> 10
#         WHERE status_id > 2)
# ON DUPLICATE KEY UPDATE ORDERS.id = ORDERS.id;
#
# INSERT INTO BOT_ORDERS
#     (SELECT * FROM EXORDERS
#                        JOIN USER U ON  U.id = EXORDERS.user_id AND U.roleid = 10
#      WHERE status_id > 2)
# ON DUPLICATE KEY UPDATE BOT_ORDERS.id = BOT_ORDERS.id;
#
# DELETE FROM EXORDERS WHERE status_id > 2





