package me.exrates.dao.impl;

import me.exrates.dao.StockExchangeDao;
import me.exrates.model.CurrencyPair;
import me.exrates.model.StockExchange;
import me.exrates.model.StockExchangeRate;
import me.exrates.model.dto.StockExchangeRateDto;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by OLEG on 14.12.2016.
 */
@Repository
public class StockExchangeDaoImpl implements StockExchangeDao {

    private static final Logger LOGGER = LogManager.getLogger(StockExchangeDaoImpl.class);

    @Autowired
    private NamedParameterJdbcTemplate jdbcTemplate;

    private final String SELECT_STOCK_EXCHANGE = "SELECT STOCK_EXCHANGE.id AS stock_exchange_id, " +
            "STOCK_EXCHANGE.name AS stock_exchange_name, STOCK_EXCHANGE.link, CURRENCY_PAIR.id, " +
            "CURRENCY_PAIR.currency1_id, CURRENCY_PAIR.currency2_id, CURRENCY_PAIR.name, " +
            "(select name from CURRENCY where id = currency1_id) as currency1_name, " +
            "(select name from CURRENCY where id = currency2_id) as currency2_name " +
            " FROM STOCK_EXCHANGE " +
            "INNER JOIN STOCK_CURRENCY_PAIR ON STOCK_CURRENCY_PAIR.stock_exchange_id = STOCK_EXCHANGE.id " +
            "INNER JOIN CURRENCY_PAIR ON STOCK_CURRENCY_PAIR.currency_pair_id = CURRENCY_PAIR.id ";

    private final ResultSetExtractor<List<StockExchange>> stockExchangeResultSetExtractor = (resultSet -> {
        List<StockExchange> result = new ArrayList<>();
        StockExchange stockExchange = null;
        int lastStockExchangeId = 0;
        while (resultSet.next()) {
            int currentStockExchangeId = resultSet.getInt("stock_exchange_id");
            if (currentStockExchangeId != lastStockExchangeId) {
                lastStockExchangeId = currentStockExchangeId;
                stockExchange = new StockExchange();
                result.add(stockExchange);
                stockExchange.setId(currentStockExchangeId);
                stockExchange.setName(resultSet.getString("stock_exchange_name"));
                stockExchange.setLink(resultSet.getString("link"));
            }
            CurrencyPair currencyPair = CurrencyDaoImpl.currencyPairRowMapper.mapRow(resultSet, resultSet.getRow());
            if (stockExchange != null) {
                stockExchange.getAvailableCurrencyPairs().add(currencyPair);
            }
        }
        return result;
    });

    @Override
    public void saveStockExchangeRate(StockExchangeRate stockExchangeRate) {
        String sql = "INSERT INTO STOCK_EXRATE(currency_pair_id, stock_exchange_id, exrate) VALUES(:currency_pair_id, :stock_exchange_id, :exrate)";
        Map<String, Number> params = new HashMap<>();
        params.put("currency_pair_id", stockExchangeRate.getCurrencyPairId());
        params.put("stock_exchange_id", stockExchangeRate.getStockExchangeId());
        params.put("exrate", stockExchangeRate.getExrate());
        jdbcTemplate.update(sql, params);
    }

    @Override
    public void saveStockExchangeRates(List<StockExchangeRate> stockExchangeRates) {
        String sql = "INSERT INTO STOCK_EXRATE(currency_pair_id, stock_exchange_id, exrate) VALUES(:currency_pair_id, :stock_exchange_id, :exrate)";
        Map<String, Object>[] batchValues = stockExchangeRates.stream().map(stockExchangeRate -> {
            Map<String, Object> values = new HashMap<String, Object>() {{
                put("currency_pair_id", stockExchangeRate.getCurrencyPairId());
                put("stock_exchange_id", stockExchangeRate.getStockExchangeId());
                put("exrate", stockExchangeRate.getExrate());
            }};
            return values;
        }).collect(Collectors.toList()).toArray(new Map[stockExchangeRates.size()]);
        jdbcTemplate.batchUpdate(sql, batchValues);
    }

    @Override
    public Optional<StockExchange> findStockExchangeByName(String name) {
        String sql = SELECT_STOCK_EXCHANGE +
                "WHERE STOCK_EXCHANGE.name = :name";
        Map<String, String> params = Collections.singletonMap("name", name);
        List<StockExchange> result =  jdbcTemplate.query(sql, params, stockExchangeResultSetExtractor);
        if (result.size() != 1) {
            return Optional.empty();
        }
        return Optional.of(result.get(0));
    }

    @Override
    public List<StockExchange> findAll() {
        return jdbcTemplate.query(SELECT_STOCK_EXCHANGE, stockExchangeResultSetExtractor);
    }

    @Override
    public List<StockExchangeRateDto> getStockExchangeStatistics() {
        String sql = "SELECT stock_1.currency_pair_id, stock_1.stock_exchange_id, " +
                "              CURRENCY_PAIR.name AS currency_pair_name, STOCK_EXCHANGE.name AS stock_exchange_name, " +
                "              stock_1.exrate, stock_1.date FROM STOCK_EXRATE AS stock_1 " +
                "              JOIN (SELECT currency_pair_id, stock_exchange_id, MAX(STOCK_EXRATE.date) AS date FROM STOCK_EXRATE " +
                "              GROUP BY currency_pair_id, stock_exchange_id) AS stock_2 " +
                "              ON stock_1.currency_pair_id = stock_2.currency_pair_id AND stock_1.stock_exchange_id = stock_2.stock_exchange_id " +
                "              AND stock_1.date = stock_2.date " +
                "              JOIN STOCK_EXCHANGE ON stock_1.stock_exchange_id = STOCK_EXCHANGE.id " +
                "              JOIN CURRENCY_PAIR ON stock_1.currency_pair_id = CURRENCY_PAIR.id " +
                "              ORDER BY stock_1.currency_pair_id, stock_1.stock_exchange_id;";
        return jdbcTemplate.query(sql, resultSet -> {
            List<StockExchangeRateDto> result = new ArrayList<>();
            StockExchangeRateDto dto = null;
            int lastCurrencyPairId = 0;
            while (resultSet.next()) {
                int currentCurrencyPairId = resultSet.getInt("currency_pair_id");
                if (currentCurrencyPairId != lastCurrencyPairId) {
                    lastCurrencyPairId = currentCurrencyPairId;
                    dto = new StockExchangeRateDto();
                    result.add(dto);
                    dto.setCurrencyPairName(resultSet.getString("currency_pair_name"));
                }
                if (dto != null) {
                    dto.getRates().put(resultSet.getString("stock_exchange_name"), resultSet.getBigDecimal("exrate"));
                }
            }
            LOGGER.debug(result);
            return result;
        });
    }







}
