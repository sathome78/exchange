DROP PROCEDURE IF EXISTS GET_CURRENCY_PAIR_STATISTICS_FOR_CACHE;

DELIMITER //

PROCEDURE `GET_CURRENCY_PAIR_STATISTICS_FOR_CACHE`(IN currency_pair varchar(45))
BEGIN
    DECLARE currency_pair_name VARCHAR(45);
    DECLARE currency_pair_id INT;
    DECLARE first DOUBLE(40, 9);
    DECLARE last DOUBLE(40, 9);
    DECLARE high24hr DOUBLE(40, 9);
    DECLARE low24hr DOUBLE(40, 9);
    DECLARE baseVolume DOUBLE(40, 9);
    DECLARE quoteVolume DOUBLE(40, 9) DEFAULT 0;
    DECLARE currency_pair_precision INT;
    DECLARE market VARCHAR(45);
    DECLARE currency_pair_type VARCHAR(45);

    DECLARE coinmarketcap_statistics VARCHAR(40);

    DECLARE AGRIGATE CURSOR
    FOR
    SELECT
		CP2.ticker_name,
		CP2.id,
		(IF (AGR.baseVolume IS NOT NULL, AGR.baseVolume, 0)) AS baseVolume,
		(IF (AGR.quoteVolume IS NOT NULL, AGR.quoteVolume, 0)) AS quoteVolume,
		(IF (AGR.first IS NOT NULL, AGR.first, 0)) AS first,
		(IF (AGR.last IS NOT NULL, AGR.last, 0)) AS last,
        (IF (AGR.high24hr IS NOT NULL, AGR.high24hr, 0)) AS high24hr,
        (IF (AGR.low24hr IS NOT NULL, AGR.low24hr, 0)) AS low24hr,
		CP2.scale AS currency_pair_precision,
		CP2.market,
    CP2.type AS currency_pair_type
	FROM (SELECT
          CP.ticker_name,
          EO.currency_pair_id,
          SUM(EO.amount_base) AS baseVolume,
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
        JOIN CURRENCY_PAIR CP ON (CP.id = EO.currency_pair_id)
        WHERE EO.status_id = 3 AND EO.date_acception >= now() - INTERVAL 24 HOUR
        GROUP BY CP.ticker_name, EO.currency_pair_id) AGR
RIGHT JOIN CURRENCY_PAIR CP2 ON (CP2.id = AGR.currency_pair_id)
WHERE (currency_pair IS NULL OR currency_pair = "" OR UPPER(currency_pair) IS NULL OR CP2.name = currency_pair);

    SELECT param_value
    FROM API_PARAMS
    WHERE param_name = "COINMARKETCAP_STATISTICS"
    INTO coinmarketcap_statistics;

    IF (coinmarketcap_statistics = "OFF") THEN
      SET currency_pair_name = "API DISABLED";
      SELECT
        currency_pair_name,
        currency_pair_id,
        first,
        last,
        high24hr,
        low24hr,
        baseVolume,
        quoteVolume,
        currency_pair_precision,
        market,
        currency_pair_type;

    ELSEIF (coinmarketcap_statistics = "ON") THEN

      BEGIN
        DECLARE eof BOOLEAN DEFAULT FALSE;
        DECLARE CONTINUE HANDLER FOR NOT FOUND SET eof = TRUE;

        set @@sql_mode = 'STRICT_TRANS_TABLES,ERROR_FOR_DIVISION_BY_ZERO,NO_AUTO_CREATE_USER,NO_ENGINE_SUBSTITUTION ';

        OPEN AGRIGATE;

        FETCH AGRIGATE
        INTO currency_pair_name, currency_pair_id, baseVolume, quoteVolume, first, last, high24hr, low24hr, currency_pair_precision, market, currency_pair_type;

        DROP TEMPORARY TABLE IF EXISTS COINMARKETCAP_STATISTICS_TMP_TBL;
        CREATE TEMPORARY TABLE COINMARKETCAP_STATISTICS_TMP_TBL
            SELECT
              currency_pair_name,
              currency_pair_id,
              first,
              last,
              high24hr,
              low24hr,
              baseVolume,
              quoteVolume,
              currency_pair_precision,
              market,
              currency_pair_type;

        DELETE FROM COINMARKETCAP_STATISTICS_TMP_TBL;

        WHILE (eof = FALSE) DO
          INSERT INTO COINMARKETCAP_STATISTICS_TMP_TBL VALUES (
            currency_pair_name,
            currency_pair_id,
            first,
            last,
            high24hr,
            low24hr,
            baseVolume,
            quoteVolume,
            currency_pair_precision,
            market,
            currency_pair_type);

          FETCH AGRIGATE
          INTO currency_pair_name, currency_pair_id, baseVolume, quoteVolume, first, last, high24hr, low24hr, currency_pair_precision, market, currency_pair_type;

        END WHILE;
      END;

      SELECT *
      FROM COINMARKETCAP_STATISTICS_TMP_TBL;
      DROP TABLE IF EXISTS COINMARKETCAP_STATISTICS_TMP_TBL;

      CLOSE AGRIGATE;

    END IF;

  END //

DELIMITER ;