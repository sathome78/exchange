package me.exrates.dao.impl;

import me.exrates.dao.StockExchangeDao;
import me.exrates.model.CurrencyPair;
import me.exrates.model.StockExchange;
import me.exrates.model.StockExchangeStats;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Created by OLEG on 14.12.2016.
 */
@Repository
public class StockExchangeDaoImpl implements StockExchangeDao {

    private static final Logger LOGGER = LogManager.getLogger(StockExchangeDaoImpl.class);

    @Autowired
    @Qualifier(value = "masterTemplate")
    private NamedParameterJdbcTemplate jdbcTemplate;

    private final String SELECT_STOCK_EXCHANGE = "SELECT SE.id AS stock_exchange_id, SE.name AS stock_exchange_name, " +
            "SE.last_field_name, SE.buy_field_name, SE.sell_field_name, SE.high_field_name, SE.low_field_name, SE.volume_field_name, " +
            "CURRENCY_PAIR.id, CURRENCY_PAIR.currency1_id, CURRENCY_PAIR.currency2_id, CURRENCY_PAIR.name, CURRENCY_PAIR.type, CURRENCY_PAIR.market, " +
            "(select name from CURRENCY where id = currency1_id) as currency1_name, " +
            "(select name from CURRENCY where id = currency2_id) as currency2_name," +
            " cur1_alias.alias AS currency1_alias, cur2_alias.alias AS currency2_alias " +
            " FROM STOCK_EXCHANGE SE " +
            "INNER JOIN STOCK_CURRENCY_PAIR SCP ON SCP.stock_exchange_id = SE.id " +
            "INNER JOIN CURRENCY_PAIR ON SCP.currency_pair_id = CURRENCY_PAIR.id " +
            "LEFT JOIN STOCK_EXCHANGE_CURRENCY_ALIAS cur1_alias ON SE.id = cur1_alias.stock_exchange_id " +
            "AND CURRENCY_PAIR.currency1_id = cur1_alias.currency_id " +
            "LEFT JOIN STOCK_EXCHANGE_CURRENCY_ALIAS cur2_alias ON SE.id = cur2_alias.stock_exchange_id " +
            "AND CURRENCY_PAIR.currency2_id = cur2_alias.currency_id ";

    private final String CREATE_STOCK_EXRATE = "INSERT INTO STOCK_EXRATE(currency_pair_id, stock_exchange_id, price_last, " +
            " price_buy, price_sell, price_low, price_high, volume) " +
            "VALUES(:currency_pair_id, :stock_exchange_id, :price_last, :price_buy, :price_sell, :price_low, :price_high, :volume)";

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
                stockExchange.setLastFieldName(resultSet.getString("last_field_name"));
                stockExchange.setBuyFieldName(resultSet.getString("buy_field_name"));
                stockExchange.setSellFieldName(resultSet.getString("sell_field_name"));
                stockExchange.setLowFieldName(resultSet.getString("low_field_name"));
                stockExchange.setHighFieldName(resultSet.getString("high_field_name"));
                stockExchange.setVolumeFieldName(resultSet.getString("volume_field_name"));
            }
            CurrencyPair currencyPair = CurrencyDaoImpl.currencyPairRowMapper.mapRow(resultSet, resultSet.getRow());
            String currency1Alias = resultSet.getString("currency1_alias");
            String currency2Alias = resultSet.getString("currency2_alias");
            if (stockExchange != null) {
                stockExchange.getAvailableCurrencyPairs().add(currencyPair);
                if (currency1Alias != null) {
                    stockExchange.getCurrencyAliases().put(resultSet.getString("currency1_name"), currency1Alias);
                }
                if (currency2Alias != null) {
                    stockExchange.getCurrencyAliases().put(resultSet.getString("currency2_name"), currency2Alias);
                }

            }
        }
        return result;
    });

    @Override
    public void saveStockExchangeStats(StockExchangeStats stockExchangeRate) {
        Map<String, Number> params = new HashMap<>();
        params.put("currency_pair_id", stockExchangeRate.getCurrencyPairId());
        params.put("stock_exchange_id", stockExchangeRate.getStockExchange().getId());
        params.put("price_last", stockExchangeRate.getPriceLast());
        params.put("price_buy", stockExchangeRate.getPriceBuy());
        params.put("price_sell", stockExchangeRate.getPriceSell());
        params.put("price_low", stockExchangeRate.getPriceLow());
        params.put("price_high", stockExchangeRate.getPriceHigh());
        params.put("volume", stockExchangeRate.getVolume());
        jdbcTemplate.update(CREATE_STOCK_EXRATE, params);
    }

    @Override
    public void saveStockExchangeStatsList(List<StockExchangeStats> stockExchangeRates) {
        Map<String, Object>[] batchValues = stockExchangeRates.stream().map(stockExchangeRate -> {
            Map<String, Object> values = new HashMap<String, Object>() {{
                put("currency_pair_id", stockExchangeRate.getCurrencyPairId());
                put("stock_exchange_id", stockExchangeRate.getStockExchange().getId());
                put("stock_exchange_id", stockExchangeRate.getStockExchange().getId());
                put("price_last", stockExchangeRate.getPriceLast());
                put("price_buy", stockExchangeRate.getPriceBuy());
                put("price_sell", stockExchangeRate.getPriceSell());
                put("price_low", stockExchangeRate.getPriceLow());
                put("price_high", stockExchangeRate.getPriceHigh());
                put("volume", stockExchangeRate.getVolume());
            }};
            return values;
        }).collect(Collectors.toList()).toArray(new Map[stockExchangeRates.size()]);
        jdbcTemplate.batchUpdate(CREATE_STOCK_EXRATE, batchValues);
    }

    @Override
    public Optional<StockExchange> findStockExchangeByName(String name) {
        String sql = SELECT_STOCK_EXCHANGE +
                "WHERE SE.name = :name";
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
    public List<StockExchange> findAllActive() {
        return jdbcTemplate.query(SELECT_STOCK_EXCHANGE + " WHERE SE.is_active = 1", stockExchangeResultSetExtractor);
    }

    @Override
    public List<StockExchangeStats> getStockExchangeStatistics(Integer currencyPairId) {
        String sql = "SELECT stock_1.stock_exchange_id, " +
                "              CURRENCY_PAIR.name AS currency_pair_name, STOCK_EXCHANGE.name AS stock_exchange_name, stock_1.price_last, " +
                "              stock_1.price_buy, stock_1.price_sell, stock_1.price_low, stock_1.price_high, stock_1.volume," +
                "              stock_1.date FROM STOCK_EXRATE AS stock_1 " +
                "              JOIN (SELECT currency_pair_id, stock_exchange_id, MAX(STOCK_EXRATE.date) AS date FROM STOCK_EXRATE " +
                "              GROUP BY currency_pair_id, stock_exchange_id) AS stock_2 " +
                "              ON stock_1.currency_pair_id = stock_2.currency_pair_id AND stock_1.stock_exchange_id = stock_2.stock_exchange_id " +
                "              AND stock_1.date = stock_2.date " +
                "              JOIN STOCK_EXCHANGE ON stock_1.stock_exchange_id = STOCK_EXCHANGE.id AND STOCK_EXCHANGE.is_active = 1" +
                "              JOIN CURRENCY_PAIR ON stock_1.currency_pair_id = CURRENCY_PAIR.id " +
                "       WHERE stock_1.currency_pair_id = :currency_pair_id " +
                "       ORDER BY stock_1.currency_pair_id, stock_1.stock_exchange_id;";
        Map<String, Integer> params = Collections.singletonMap("currency_pair_id", currencyPairId);


        return jdbcTemplate.query(sql, params, (resultSet, rowNum) -> {
            StockExchangeStats stockExchangeStats = new StockExchangeStats();
            StockExchange stockExchange = new StockExchange();
            stockExchange.setId(resultSet.getInt("stock_exchange_id"));
            stockExchange.setName(resultSet.getString("stock_exchange_name"));
            stockExchangeStats.setStockExchange(stockExchange);
            stockExchangeStats.setPriceLast(resultSet.getBigDecimal("price_last"));
            stockExchangeStats.setPriceBuy(resultSet.getBigDecimal("price_buy"));
            stockExchangeStats.setPriceSell(resultSet.getBigDecimal("price_sell"));
            stockExchangeStats.setPriceLow(resultSet.getBigDecimal("price_low"));
            stockExchangeStats.setPriceHigh(resultSet.getBigDecimal("price_high"));
            stockExchangeStats.setVolume(resultSet.getBigDecimal("volume"));
            stockExchangeStats.setDate(resultSet.getTimestamp("date").toLocalDateTime());
            return stockExchangeStats;
        });
    }

    @Override
    public List<StockExchangeStats> getStockExchangeStatisticsByPeriod(Integer currencyPairId) {
        String sql = "SELECT stock_1.stock_exchange_id, " +
                "              CURRENCY_PAIR.name AS currency_pair_name, STOCK_EXCHANGE.name AS stock_exchange_name, stock_1.price_last, " +
                "              stock_1.price_buy, stock_1.price_sell, stock_1.price_low, stock_1.price_high, stock_1.volume," +
                "              stock_1.date FROM STOCK_EXRATE AS stock_1 " +
                "              JOIN (SELECT currency_pair_id, stock_exchange_id, MAX(STOCK_EXRATE.date) AS date FROM STOCK_EXRATE " +
                "              GROUP BY currency_pair_id, stock_exchange_id) AS stock_2 " +
                "              ON stock_1.currency_pair_id = stock_2.currency_pair_id AND stock_1.stock_exchange_id = stock_2.stock_exchange_id " +
                "              AND stock_1.date = stock_2.date " +
                "              JOIN STOCK_EXCHANGE ON stock_1.stock_exchange_id = STOCK_EXCHANGE.id AND STOCK_EXCHANGE.is_active = 1" +
                "              JOIN CURRENCY_PAIR ON stock_1.currency_pair_id = CURRENCY_PAIR.id " +
                "       WHERE stock_1.currency_pair_id = :currency_pair_id AND stock_1.date > NOW() - INTERVAL 1 DAY " +
                "       ORDER BY stock_1.currency_pair_id, stock_1.stock_exchange_id;";
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("currency_pair_id", currencyPairId);

        return jdbcTemplate.query(sql, params, (resultSet, rowNum) -> {
            StockExchangeStats stockExchangeStats = new StockExchangeStats();
            StockExchange stockExchange = new StockExchange();
            stockExchange.setId(resultSet.getInt("stock_exchange_id"));
            stockExchange.setName(resultSet.getString("stock_exchange_name"));
            stockExchangeStats.setStockExchange(stockExchange);
            stockExchangeStats.setPriceLast(resultSet.getBigDecimal("price_last"));
            stockExchangeStats.setPriceBuy(resultSet.getBigDecimal("price_buy"));
            stockExchangeStats.setPriceSell(resultSet.getBigDecimal("price_sell"));
            stockExchangeStats.setPriceLow(resultSet.getBigDecimal("price_low"));
            stockExchangeStats.setPriceHigh(resultSet.getBigDecimal("price_high"));
            stockExchangeStats.setVolume(resultSet.getBigDecimal("volume"));
            stockExchangeStats.setDate(resultSet.getTimestamp("date").toLocalDateTime());
            return stockExchangeStats;
        });
    }

}
