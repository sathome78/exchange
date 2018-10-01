package me.exrates.dao.impl;

import me.exrates.dao.BotDao;
import me.exrates.model.BotLaunchSettings;
import me.exrates.model.BotTrader;
import me.exrates.model.BotTradingSettings;
import me.exrates.model.dto.BotTradingSettingsShortDto;
import me.exrates.model.enums.OperationType;
import me.exrates.model.enums.OrderType;
import me.exrates.model.enums.PriceGrowthDirection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
public class BotDaoImpl implements BotDao {

    @Autowired
    @Qualifier(value = "masterTemplate")
    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private final RowMapper<BotTrader> botRowMapper = (rs, rowNum) -> {
        BotTrader botTrader = new BotTrader();
        botTrader.setId(rs.getInt("id"));
        botTrader.setUserId(rs.getInt("user_id"));
        botTrader.setEnabled(rs.getBoolean("is_enabled"));
        botTrader.setAcceptDelayInMillis(rs.getInt("order_accept_timeout"));
        return botTrader;
    };

    private final RowMapper<BotLaunchSettings> botLaunchSettingsRowMapper = (rs, rowNum) -> {
        BotLaunchSettings launchSettings = new BotLaunchSettings();
        launchSettings.setId(rs.getInt("launch_id"));
        launchSettings.setBotId(rs.getInt("bot_trader_id"));
        launchSettings.setCurrencyPairId(rs.getInt("currency_pair_id"));
        launchSettings.setCurrencyPairName(rs.getString("currency_pair_name"));
        launchSettings.setEnabledForPair(rs.getBoolean("is_enabled"));
        launchSettings.setUserOrderPriceConsidered(rs.getBoolean("consider_user_orders"));
        launchSettings.setLaunchIntervalInMinutes(rs.getInt("launch_interval_minutes"));
        launchSettings.setCreateTimeoutInSeconds(rs.getInt("create_timeout_seconds"));
        launchSettings.setQuantityPerSequence(rs.getInt("quantity_per_sequence"));
        return launchSettings;
    };

    @Override
    public Optional<BotTrader> retrieveBotTrader() {
        String sql = "SELECT id, user_id, is_enabled, order_accept_timeout FROM BOT_TRADER ORDER BY id DESC LIMIT 1";
        try {
            BotTrader botTrader = jdbcTemplate.queryForObject(sql, botRowMapper);
            return Optional.of(botTrader);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    @Override
    public Optional<BotTrader> findById(int id) {
        String sql = "SELECT id, user_id, is_enabled, order_accept_timeout FROM BOT_TRADER WHERE id = :id";
        Map<String, Object> params = new HashMap<>();
        params.put("id", id);
        try {
            return Optional.of(namedParameterJdbcTemplate.queryForObject(sql, params, botRowMapper));
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    @Override
    public void createBot(Integer userId) {
        String sqlBase = "INSERT INTO BOT_TRADER (user_id) VALUES (:user_id)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        String sqlLaunch = "INSERT INTO BOT_LAUNCH_SETTINGS(bot_trader_id, currency_pair_id) " +
                "  SELECT :bot_id, CP.id FROM CURRENCY_PAIR CP ";
        String sqlTrade = "INSERT INTO BOT_TRADING_SETTINGS(bot_launch_settings_id, order_type_id) " +
                "  SELECT BLCH.id, OT.id FROM BOT_LAUNCH_SETTINGS BLCH JOIN ORDER_TYPE OT WHERE BLCH.bot_trader_id = :bot_id;";
        namedParameterJdbcTemplate.update(sqlBase, new MapSqlParameterSource(Collections.singletonMap("user_id", userId)), keyHolder);
        int botId = keyHolder.getKey().intValue();
        namedParameterJdbcTemplate.update(sqlLaunch, Collections.singletonMap("bot_id", botId));
        namedParameterJdbcTemplate.update(sqlTrade, Collections.singletonMap("bot_id", botId));
    }

    @Override
    public void updateBot(BotTrader botTrader) {
        String sql = "UPDATE BOT_TRADER SET is_enabled = :is_enabled, " +
                "order_accept_timeout = :accept_timeout " +
                "WHERE id = :id";
        Map<String, Object> params = new HashMap<>();
        params.put("id", botTrader.getId());
        params.put("is_enabled", botTrader.isEnabled());
        params.put("accept_timeout", botTrader.getAcceptDelayInMillis());
        namedParameterJdbcTemplate.update(sql, params);
    }

    @Override
    public Optional<BotTradingSettings> retrieveBotTradingSettingsForCurrencyPairAndOrderType(int botId, int currencyPairId, OrderType orderType) {
        String sql = "SELECT BTS.id AS trading_id, BLCH.id AS launch_id, BLCH.bot_trader_id, BLCH.currency_pair_id, CP.name AS currency_pair_name, " +
                "  BTS.order_type_id, BLCH.is_enabled, BLCH.consider_user_orders, " +
                "  BLCH.launch_interval_minutes, BLCH.create_timeout_seconds, BLCH.quantity_per_sequence, " +
                "  BTS.max_amount, BTS.min_amount, BTS.max_price, BTS.min_price, BTS.price_step, BTS.price_growth_direction, " +
                "  BTS.min_price_deviation, BTS.max_price_deviation, BTS.randomize_price_step, BTS.price_step_deviation, " +
                "  MIN(EX.exrate) AS min_user_price, MAX(EX.exrate) AS max_user_price " +
                "FROM BOT_LAUNCH_SETTINGS BLCH " +
                "  JOIN CURRENCY_PAIR CP ON CP.id = BLCH.currency_pair_id " +
                "  JOIN BOT_TRADING_SETTINGS BTS ON BLCH.id = BTS.bot_launch_settings_id " +
                "  JOIN EXORDERS EX ON EX.status_id = 2 AND EX.currency_pair_id = :currency_pair_id AND EX.operation_type_id = :operation_type_id " +
                "  JOIN USER ON EX.user_id = USER.id AND USER.roleid IN (SELECT user_role_id FROM USER_ROLE_SETTINGS where considered_for_price_range = 1) " +
                "WHERE BLCH.bot_trader_id = :bot_id AND BLCH.currency_pair_id = :currency_pair_id AND BTS.order_type_id = :order_type_id";
        Map<String, Object> params = new HashMap<>();
        params.put("bot_id", botId);
        params.put("currency_pair_id", currencyPairId);
        params.put("order_type_id", orderType.getType());
        params.put("operation_type_id", OperationType.valueOf(orderType.name()).getType());

        try {
            return Optional.of(namedParameterJdbcTemplate.queryForObject(sql, params, (rs, rowNum) -> {
                BotLaunchSettings launchSettings = botLaunchSettingsRowMapper.mapRow(rs, rowNum);
                BotTradingSettings tradingSettings = new BotTradingSettings();
                tradingSettings.setId(rs.getInt("trading_id"));
                tradingSettings.setBotLaunchSettings(launchSettings);
                tradingSettings.setMaxAmount(rs.getBigDecimal("max_amount"));
                tradingSettings.setMinAmount(rs.getBigDecimal("min_amount"));
                tradingSettings.setMaxPrice(rs.getBigDecimal("max_price"));
                tradingSettings.setMinPrice(rs.getBigDecimal("min_price"));
                tradingSettings.setMaxUserPrice(rs.getBigDecimal("max_user_price"));
                tradingSettings.setMinUserPrice(rs.getBigDecimal("min_user_price"));
                tradingSettings.setPriceStep(rs.getBigDecimal("price_step"));
                tradingSettings.setMinDeviationPercent(rs.getInt("min_price_deviation"));
                tradingSettings.setMaxDeviationPercent(rs.getInt("max_price_deviation"));
                tradingSettings.setPriceStepRandom(rs.getBoolean("randomize_price_step"));
                tradingSettings.setPriceStepDeviationPercent(rs.getInt("price_step_deviation"));
                tradingSettings.setDirection(PriceGrowthDirection.valueOf(rs.getString("price_growth_direction")));

                return tradingSettings;
            }));
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    @Override
    public BotLaunchSettings retrieveBotLaunchSettingsForCurrencyPair(int botId, int currencyPairId) {
        String sql = "SELECT BLCH.id AS launch_id, BLCH.bot_trader_id, BLCH.currency_pair_id, CP.name AS currency_pair_name, BLCH.is_enabled, " +
                "BLCH.launch_interval_minutes, BLCH.create_timeout_seconds, BLCH.quantity_per_sequence, BLCH.consider_user_orders " +
                "FROM BOT_LAUNCH_SETTINGS BLCH " +
                "JOIN CURRENCY_PAIR CP ON CP.id = BLCH.currency_pair_id " +
                "WHERE BLCH.bot_trader_id = :bot_id AND BLCH.currency_pair_id = :currency_pair_id ";
        Map<String, Integer> params = new HashMap<>();
        params.put("bot_id", botId);
        params.put("currency_pair_id", currencyPairId);
        return namedParameterJdbcTemplate.queryForObject(sql, params, botLaunchSettingsRowMapper);

    }



    @Override
    public void updatePriceGrowthDirection(int settingsId, PriceGrowthDirection direction) {
        String sql = "UPDATE BOT_TRADING_SETTINGS SET price_growth_direction = :direction WHERE id = :id";
        Map<String, Object> params = new HashMap<>();
        params.put("id", settingsId);
        params.put("direction", direction.name());
        namedParameterJdbcTemplate.update(sql, params);
    }

    @Override
    public void setEnabledForCurrencyPair(int botId, int currencyPairId, boolean isEnabled) {
        String sql = "UPDATE BOT_LAUNCH_SETTINGS SET is_enabled = :is_enabled WHERE bot_trader_id = :bot_id AND currency_pair_id = :currency_pair_id";
        Map<String, Object> params = new HashMap<>();
        params.put("bot_id", botId);
        params.put("currency_pair_id", currencyPairId);
        params.put("is_enabled", isEnabled);
        namedParameterJdbcTemplate.update(sql, params);
    }

    @Override
    public List<BotLaunchSettings> retrieveLaunchSettingsForAllPairs(int botId, Boolean isEnabled) {
        String enabledClause = isEnabled == null ? "" : " AND BLCH.is_enabled = :is_enabled ";

        String sql = "SELECT BLCH.id AS launch_id, BLCH.bot_trader_id, BLCH.currency_pair_id, CP.name AS currency_pair_name, BLCH.is_enabled, " +
                "BLCH.launch_interval_minutes, BLCH.create_timeout_seconds, BLCH.quantity_per_sequence, BLCH.consider_user_orders " +
                "FROM BOT_LAUNCH_SETTINGS BLCH " +
                "JOIN CURRENCY_PAIR CP ON CP.id = BLCH.currency_pair_id AND CP.hidden != 1 " +
                "WHERE BLCH.bot_trader_id = :bot_id " + enabledClause;
        Map<String, Object> params = new HashMap<>();
        params.put("bot_id", botId);
        if (isEnabled != null) {
            params.put("is_enabled", isEnabled);
        }
        return namedParameterJdbcTemplate.query(sql, params, botLaunchSettingsRowMapper);
    }

    @Override
    public BotTradingSettingsShortDto retrieveTradingSettingsShort(int botLaunchSettingsId, int orderTypeId) {
        String sql = "SELECT id, order_type_id, max_amount, min_amount, max_price, min_price, price_step," +
                "min_price_deviation, max_price_deviation, randomize_price_step, price_step_deviation " +
                "FROM BOT_TRADING_SETTINGS " +
                "WHERE bot_launch_settings_id = :bot_launch_settings_id AND order_type_id = :order_type_id";
        Map<String, Integer> params = new HashMap<>();
        params.put("bot_launch_settings_id", botLaunchSettingsId);
        params.put("order_type_id", orderTypeId);
        return namedParameterJdbcTemplate.queryForObject(sql, params, (rs, rowNum) -> {
            BotTradingSettingsShortDto tradingSettings = new BotTradingSettingsShortDto();
            tradingSettings.setId(rs.getInt("id"));
            tradingSettings.setOrderType(OrderType.convert(rs.getInt("order_type_id")));
            tradingSettings.setMaxAmount(rs.getBigDecimal("max_amount"));
            tradingSettings.setMinAmount(rs.getBigDecimal("min_amount"));
            tradingSettings.setMaxPrice(rs.getBigDecimal("max_price"));
            tradingSettings.setMinPrice(rs.getBigDecimal("min_price"));
            tradingSettings.setPriceStep(rs.getBigDecimal("price_step"));
            tradingSettings.setMinDeviationPercent(rs.getInt("min_price_deviation"));
            tradingSettings.setMaxDeviationPercent(rs.getInt("max_price_deviation"));
            tradingSettings.setPriceStepRandom(rs.getBoolean("randomize_price_step"));
            tradingSettings.setPriceStepDeviationPercent(rs.getInt("price_step_deviation"));
            return tradingSettings;
        });
    }

    @Override
    public void updateLaunchSettings(BotLaunchSettings launchSettings) {
        String sql = "UPDATE BOT_LAUNCH_SETTINGS SET launch_interval_minutes = :launch_interval_minutes, create_timeout_seconds = :create_timeout_seconds, " +
                "quantity_per_sequence = :quantity_per_sequence WHERE id = :id";
        Map<String, Object> params = new HashMap<>();
        params.put("id", launchSettings.getId());
        params.put("launch_interval_minutes", launchSettings.getLaunchIntervalInMinutes());
        params.put("create_timeout_seconds", launchSettings.getCreateTimeoutInSeconds());
        params.put("quantity_per_sequence", launchSettings.getQuantityPerSequence());
        namedParameterJdbcTemplate.update(sql, params);
    }

    @Override
    public void updateTradingSettings(BotTradingSettingsShortDto tradingSettings) {
        String sql = "UPDATE BOT_TRADING_SETTINGS SET min_amount = :min_amount, max_amount = :max_amount, min_price = :min_price, " +
                " max_price = :max_price, price_step = :price_step, " +
                " min_price_deviation = :min_price_deviation, max_price_deviation = :max_price_deviation, randomize_price_step = :randomize_price_step, " +
                "price_step_deviation = :price_step_deviation WHERE id = :id";

        Map<String, Object> params = new HashMap<>();
        params.put("id", tradingSettings.getId());
        params.put("min_amount", tradingSettings.getMinAmount());
        params.put("max_amount", tradingSettings.getMaxAmount());
        params.put("min_price", tradingSettings.getMinPrice());
        params.put("max_price", tradingSettings.getMaxPrice());
        params.put("price_step", tradingSettings.getPriceStep());
        params.put("min_price_deviation", tradingSettings.getMinDeviationPercent());
        params.put("max_price_deviation", tradingSettings.getMaxDeviationPercent());
        params.put("randomize_price_step", tradingSettings.isPriceStepRandom());
        params.put("price_step_deviation", tradingSettings.getPriceStepDeviationPercent());
        namedParameterJdbcTemplate.update(sql, params);
    }

    @Override
    public void toggleCreationForAllCurrencyPairs(int botId, boolean newStatus) {
        String sql = "UPDATE BOT_LAUNCH_SETTINGS SET is_enabled = :enabled WHERE bot_trader_id = :id";
        Map<String, Object> params = new HashMap<>();
        params.put("id", botId);
        params.put("enabled", newStatus);
        namedParameterJdbcTemplate.update(sql, params);
    }

    @Override
    public void setConsiderUserOrders(int launchSettingsId, boolean considerUserOrders) {
        String sql = "UPDATE BOT_LAUNCH_SETTINGS SET consider_user_orders = :consider_user_orders WHERE id = :id";
        Map<String, Object> params = new HashMap<>();
        params.put("id", launchSettingsId);
        params.put("consider_user_orders", considerUserOrders);
        namedParameterJdbcTemplate.update(sql, params);
    }



}
