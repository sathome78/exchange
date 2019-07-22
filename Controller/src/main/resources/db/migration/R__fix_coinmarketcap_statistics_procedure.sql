DROP PROCEDURE IF EXISTS GET_COINMARKETCAP_STATISTICS;

DELIMITER //

CREATE PROCEDURE GET_COINMARKETCAP_STATISTICS(IN currency_pair VARCHAR(45))
  BEGIN
    DECLARE currency_pair_name VARCHAR(45);
    DECLARE currency_pair_id INT;
    DECLARE status_id INT;
    DECLARE first_date_acception TIMESTAMP;
    DECLARE last_date_acception TIMESTAMP;
    DECLARE predPoint TIMESTAMP;
    DECLARE baseVolume DOUBLE(40, 9);
    DECLARE first DOUBLE(40, 9);
    DECLARE last DOUBLE(40, 9);
    DECLARE lowestAsk DOUBLE(40, 9);
    DECLARE highestBid DOUBLE(40, 9);
    DECLARE high24hr DOUBLE(40, 9);
    DECLARE low24hr DOUBLE(40, 9);
    DECLARE quoteVolume DOUBLE(40, 9) DEFAULT 0;
    DECLARE isFrozen INT DEFAULT 0;

    DECLARE coinmarketcap_statistics VARCHAR(40);

    DECLARE AGRIGATE CURSOR
    FOR SELECT
          CP.ticker_name,
          EO.currency_pair_id,
          EO.status_id,
          MIN(EO.date_acception) AS first_date_acception,
          MAX(EO.date_acception) AS last_date_acception,
          SUM(EO.amount_base)    AS baseVolume,
          SUM(EO.amount_convert) AS quoteVolume,
          (
            SELECT FIRSTORDER.exrate
            FROM EXORDERS FIRSTORDER
            WHERE
              (FIRSTORDER.date_acception = MIN(EO.date_acception)) AND
              (FIRSTORDER.currency_pair_id = EO.currency_pair_id) AND
              (FIRSTORDER.status_id = EO.status_id)
            ORDER BY FIRSTORDER.id ASC
            LIMIT 1
          ) AS first,
          (
            SELECT LASTORDER.exrate
            FROM EXORDERS LASTORDER
            WHERE
              (LASTORDER.date_acception = MAX(EO.date_acception)) AND
              (LASTORDER.currency_pair_id = EO.currency_pair_id) AND
              (LASTORDER.status_id = EO.status_id)
            ORDER BY LASTORDER.id DESC
            LIMIT 1
          ) AS last,
          MAX(EO.exrate) AS high24hr,
          MIN(EO.exrate) AS low24hr
        FROM EXORDERS EO
          JOIN CURRENCY_PAIR CP ON (CP.id = EO.currency_pair_id) AND (CP.hidden IS NOT TRUE)
        WHERE (currency_pair IS NULL
         OR currency_pair = ""
          OR UPPER(currency_pair) = "NULL"
           OR EO.currency_pair_id = (SELECT CURRENCY_PAIR.id FROM CURRENCY_PAIR WHERE CURRENCY_PAIR.name = currency_pair))
            AND EO.status_id = 3
             AND EO.date_acception >= now() - INTERVAL 24 HOUR
        GROUP BY CP.ticker_name, EO.currency_pair_id, EO.status_id;

    SELECT param_value
    FROM API_PARAMS
    WHERE param_name = "COINMARKETCAP_STATISTICS"
    INTO coinmarketcap_statistics;

    IF (coinmarketcap_statistics = "OFF") THEN
      SET currency_pair_name = "API DISABLED";
      SELECT
        currency_pair_name,
        currency_pair_id,
        status_id,
        first_date_acception,
        last_date_acception,
        predPoint,
        baseVolume,
        first,
        last,
        lowestAsk,
        highestBid,
        high24hr,
        low24hr,
        quoteVolume,
        isFrozen;

    ELSEIF (coinmarketcap_statistics = "ON") THEN

      BEGIN
        DECLARE eof BOOLEAN DEFAULT FALSE;
        DECLARE CONTINUE HANDLER FOR NOT FOUND SET eof = TRUE;

        set @@sql_mode = 'STRICT_TRANS_TABLES,ERROR_FOR_DIVISION_BY_ZERO,NO_AUTO_CREATE_USER,NO_ENGINE_SUBSTITUTION ';

        OPEN AGRIGATE;

        FETCH AGRIGATE
        INTO currency_pair_name, currency_pair_id, status_id, first_date_acception, last_date_acception, baseVolume, quoteVolume, first, last, high24hr, low24hr;

        DROP TEMPORARY TABLE IF EXISTS COINMARKETCAP_STATISTICS_TMP_TBL;
        CREATE TEMPORARY TABLE COINMARKETCAP_STATISTICS_TMP_TBL
            SELECT
              currency_pair_name,
              currency_pair_id,
              status_id,
              first_date_acception,
              last_date_acception,
              predPoint,
              baseVolume,
              first,
              last,
              lowestAsk,
              highestBid,
              high24hr,
              low24hr,
              quoteVolume,
              isFrozen;

        DELETE FROM COINMARKETCAP_STATISTICS_TMP_TBL;

        WHILE (eof = FALSE) DO

          SET lowestAsk = NULL;
          SET highestBid = NULL;

          SELECT MIN(LOWESTASKORDER.exrate)
          FROM EXORDERS LOWESTASKORDER
          WHERE
            (LOWESTASKORDER.currency_pair_id = currency_pair_id) AND
            (LOWESTASKORDER.status_id = 2) AND
            (LOWESTASKORDER.operation_type_id = 3)
          INTO lowestAsk;

          SELECT MAX(HIGHESTBIDCORDER.exrate)
          FROM EXORDERS HIGHESTBIDCORDER
          WHERE
            (HIGHESTBIDCORDER.currency_pair_id = currency_pair_id) AND
            (HIGHESTBIDCORDER.status_id = 2) AND
            (HIGHESTBIDCORDER.operation_type_id = 4)
          INTO highestBid;

          INSERT INTO COINMARKETCAP_STATISTICS_TMP_TBL VALUES (
            currency_pair_name,
            currency_pair_id,
            status_id,
            first_date_acception,
            last_date_acception,
            predPoint,
            baseVolume,
            first,
            last,
            lowestAsk,
            highestBid,
            high24hr,
            low24hr,
            quoteVolume,
            isFrozen);

          FETCH AGRIGATE
          INTO currency_pair_name, currency_pair_id, status_id, first_date_acception, last_date_acception, baseVolume, quoteVolume, first, last, high24hr, low24hr;

        END WHILE;
      END;

      SELECT *
      FROM COINMARKETCAP_STATISTICS_TMP_TBL;
      DROP TABLE IF EXISTS COINMARKETCAP_STATISTICS_TMP_TBL;

      CLOSE AGRIGATE;

    END IF;

  END //

DELIMITER ;