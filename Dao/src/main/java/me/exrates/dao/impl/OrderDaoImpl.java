package me.exrates.dao.impl;

import me.exrates.dao.CommissionDao;
import me.exrates.dao.OrderDao;
import me.exrates.dao.WalletDao;
import me.exrates.dao.exception.OrderDaoException;
import me.exrates.jdbc.OrderRowMapper;
import me.exrates.model.Currency;
import me.exrates.model.CurrencyPair;
import me.exrates.model.ExOrder;
import me.exrates.model.PagingData;
import me.exrates.model.dto.CandleChartItemDto;
import me.exrates.model.dto.CoinmarketApiDto;
import me.exrates.model.dto.CurrencyPairTurnoverReportDto;
import me.exrates.model.dto.ExOrderStatisticsDto;
import me.exrates.model.dto.OrderBasicInfoDto;
import me.exrates.model.dto.OrderCommissionsDto;
import me.exrates.model.dto.OrderCreateDto;
import me.exrates.model.dto.OrderInfoDto;
import me.exrates.model.dto.OrdersCommissionSummaryDto;
import me.exrates.model.dto.RatesUSDForReportDto;
import me.exrates.model.dto.StatisticForMarket;
import me.exrates.model.dto.UserSummaryOrdersByCurrencyPairsDto;
import me.exrates.model.dto.WalletsAndCommissionsForOrderCreationDto;
import me.exrates.model.dto.dataTable.DataTableParams;
import me.exrates.model.dto.filterData.AdminOrderFilterData;
import me.exrates.model.dto.mobileApiDto.dashboard.CommissionsDto;
import me.exrates.model.dto.onlineTableDto.ExOrderStatisticsShortByPairsDto;
import me.exrates.model.dto.onlineTableDto.OrderAcceptedHistoryDto;
import me.exrates.model.dto.onlineTableDto.OrderListDto;
import me.exrates.model.dto.onlineTableDto.OrderWideListDto;
import me.exrates.model.dto.openAPI.OpenOrderDto;
import me.exrates.model.dto.openAPI.OrderBookItem;
import me.exrates.model.dto.openAPI.TradeHistoryDto;
import me.exrates.model.dto.openAPI.TransactionDto;
import me.exrates.model.dto.openAPI.UserOrdersDto;
import me.exrates.model.dto.openAPI.UserTradeHistoryDto;
import me.exrates.model.enums.ActionType;
import me.exrates.model.enums.CurrencyPairType;
import me.exrates.model.enums.OperationType;
import me.exrates.model.enums.OrderBaseType;
import me.exrates.model.enums.OrderStatus;
import me.exrates.model.enums.OrderType;
import me.exrates.model.enums.TransactionStatus;
import me.exrates.model.enums.UserRole;
import me.exrates.model.util.BigDecimalProcessing;
import me.exrates.model.vo.BackDealInterval;
import me.exrates.model.vo.OrderRoleInfoForDelete;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.PreparedStatementCallback;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import javax.annotation.Nullable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import static java.util.Objects.nonNull;
import static me.exrates.model.enums.OperationType.BUY;
import static me.exrates.model.enums.OperationType.INPUT;
import static me.exrates.model.enums.OperationType.OUTPUT;
import static me.exrates.model.enums.OrderStatus.CLOSED;
import static me.exrates.model.enums.TransactionSourceType.ORDER;

@Repository
public class OrderDaoImpl implements OrderDao {

    private static final Logger LOGGER = LogManager.getLogger(OrderDaoImpl.class);

    private static final String DEFAULT_DATE_FORMAT_PATTERN = "yyyy-MM-dd HH:mm:ss";
    private final RowMapper<UserOrdersDto> userOrdersRowMapper = (rs, row) -> {
        int id = rs.getInt("order_id");
        String currencyPairName = rs.getString("currency_pair_name");
        String orderType = OrderType.fromOperationType(OperationType.convert(rs.getInt("operation_type_id"))).name();
        LocalDateTime dateCreation = rs.getTimestamp("date_creation").toLocalDateTime();
        Timestamp timestampAcceptance = rs.getTimestamp("date_acception");
        LocalDateTime dateAcceptance = timestampAcceptance == null ? null : timestampAcceptance.toLocalDateTime();
        BigDecimal amount = rs.getBigDecimal("amount_base");
        BigDecimal price = rs.getBigDecimal("exrate");
        return new UserOrdersDto(id, currencyPairName, amount, orderType, price, dateCreation, dateAcceptance);
    };
    @Autowired
    CommissionDao commissionDao;
    @Autowired
    WalletDao walletDao;
    RowMapper<ExOrderStatisticsShortByPairsDto> exchangeRatesRowMapper = (rs, rowNum) -> {
        ExOrderStatisticsShortByPairsDto exOrderStatisticsDto = new ExOrderStatisticsShortByPairsDto();
        exOrderStatisticsDto.setCurrencyPairName(rs.getString("currency_pair_name"));
        exOrderStatisticsDto.setCurrencyPairId(rs.getInt("currency_pair_id"));
        exOrderStatisticsDto.setLastOrderRate(rs.getString("last_exrate"));
        exOrderStatisticsDto.setPredLastOrderRate(rs.getString("pred_last_exrate"));
        exOrderStatisticsDto.setType(CurrencyPairType.valueOf(rs.getString("type")));
        exOrderStatisticsDto.setPairOrder(rs.getInt("pair_order"));
        exOrderStatisticsDto.setMarket(rs.getString("market"));
        exOrderStatisticsDto.setVolume(rs.getString("volume"));
        return exOrderStatisticsDto;
    };
    @Autowired
    @Qualifier(value = "masterTemplate")
    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;
    @Autowired
    @Qualifier(value = "slaveTemplate")
    private NamedParameterJdbcTemplate slaveJdbcTemplate;

    @Override
    public int createOrder(ExOrder exOrder) {
        String sql = "INSERT INTO EXORDERS" +
                "  (user_id, currency_pair_id, operation_type_id, exrate, amount_base, amount_convert, commission_id, commission_fixed_amount, status_id, order_source_id, base_type)" +
                "  VALUES " +
                "  (:user_id, :currency_pair_id, :operation_type_id, :exrate, :amount_base, :amount_convert, :commission_id, :commission_fixed_amount, :status_id, :order_source_id, :base_type)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        MapSqlParameterSource parameters = new MapSqlParameterSource()
                .addValue("user_id", exOrder.getUserId())
                .addValue("currency_pair_id", exOrder.getCurrencyPairId())
                .addValue("operation_type_id", exOrder.getOperationType().getType())
                .addValue("exrate", exOrder.getExRate())
                .addValue("amount_base", exOrder.getAmountBase())
                .addValue("amount_convert", exOrder.getAmountConvert())
                .addValue("commission_id", exOrder.getComissionId())
                .addValue("commission_fixed_amount", exOrder.getCommissionFixedAmount())
                .addValue("status_id", OrderStatus.INPROCESS.getStatus())
                .addValue("order_source_id", exOrder.getSourceId())
                .addValue("base_type", exOrder.getOrderBaseType().name());
        int result = namedParameterJdbcTemplate.update(sql, parameters, keyHolder);
        int id = (int) keyHolder.getKey().longValue();
        if (result <= 0) {
            id = 0;
        }
        return id;
    }

    @Override
    public boolean updateOrder(int orderId, ExOrder exOrder) {

        String sql = "UPDATE EXORDERS SET" +
                " user_id = :user_id, currency_pair_id = :currency_pair_id, operation_type_id = :operation_type_id," +
                " exrate = :exrate, amount_base = :amount_base, amount_convert = :amount_convert, commission_id = :commission_id," +
                " commission_fixed_amount = :commission_fixed_amount, status_id = :status_id, order_source_id = :order_source_id," +
                " base_type = :base_type" +
                "  WHERE id = :id ";
        MapSqlParameterSource parameters = new MapSqlParameterSource()
                .addValue("user_id", exOrder.getUserId())
                .addValue("currency_pair_id", exOrder.getCurrencyPairId())
                .addValue("operation_type_id", exOrder.getOperationType().getType())
                .addValue("exrate", exOrder.getExRate())
                .addValue("amount_base", exOrder.getAmountBase())
                .addValue("amount_convert", exOrder.getAmountConvert())
                .addValue("commission_id", exOrder.getComissionId())
                .addValue("commission_fixed_amount", exOrder.getCommissionFixedAmount())
                .addValue("status_id", exOrder.getStatus().getStatus())
                .addValue("order_source_id", exOrder.getSourceId())
                .addValue("base_type", exOrder.getOrderBaseType().name())
                .addValue("id", orderId);
        return namedParameterJdbcTemplate.update(sql, parameters) > 0;
    }

    /*USE FOR BOT ONLY!!!*/
    @Override
    public void postAcceptedOrderToDB(ExOrder exOrder) {
        String sql = "INSERT INTO EXORDERS" +
                "  (user_id, currency_pair_id, operation_type_id, exrate, amount_base, amount_convert, commission_id, " +
                "   commission_fixed_amount, status_id, order_source_id, date_creation, date_acception, user_acceptor_id, status_modification_date)" +
                "  VALUES " +
                "  (:user_id, :currency_pair_id, :operation_type_id, :exrate, :amount_base, :amount_convert, :commission_id, :commission_fixed_amount," +
                " :status_id, :order_source_id, :date_creation, :date_acception, :user_acceptor_id, :status_modification_date)";
        Map<String, Object> params = new HashMap<String, Object>() {{
            put("user_id", exOrder.getUserId());
            put("currency_pair_id", exOrder.getCurrencyPairId());
            put("operation_type_id", exOrder.getOperationType().type);
            put("exrate", exOrder.getExRate());
            put("amount_base", exOrder.getAmountBase());
            put("amount_convert", exOrder.getAmountConvert());
            put("commission_id", exOrder.getComissionId());
            put("commission_fixed_amount", exOrder.getCommissionFixedAmount());
            put("status_id", CLOSED.getStatus());
            put("order_source_id", exOrder.getSourceId());
            put("user_acceptor_id", exOrder.getUserAcceptorId());
            Timestamp currentDate = Timestamp.valueOf(LocalDateTime.now());
            put("date_creation", currentDate);
            put("date_acception", currentDate);
            put("status_modification_date", currentDate);
        }};
        namedParameterJdbcTemplate.update(sql, params);
    }

    @Override
    public List<OrderListDto> getOrdersSellForCurrencyPair(CurrencyPair currencyPair, UserRole filterRole) {
        String sql = "SELECT EXORDERS.id, user_id, currency_pair_id, operation_type_id, exrate, amount_base, " +
                " amount_convert, commission_fixed_amount, date_creation, date_acception" +
                "  FROM EXORDERS " +
                (filterRole == null ? "" : " JOIN USER ON (USER.id=EXORDERS.user_id)  AND USER.roleid = :user_role_id ") +
                "  WHERE status_id = 2 and operation_type_id= 3 and currency_pair_id=:currency_pair_id" +
                "  ORDER BY exrate ASC";
        Map<String, Integer> namedParameters = new HashMap<>();
        namedParameters.put("currency_pair_id", currencyPair.getId());
        if (filterRole != null) {
            namedParameters.put("user_role_id", filterRole.getRole());
        }
        return slaveJdbcTemplate.query(sql, namedParameters, orderListDtoRowMapper());
    }

    @Override
    public List<OrderListDto> getOrdersBuyForCurrencyPair(CurrencyPair currencyPair, UserRole filterRole) {
        String sql = "SELECT EXORDERS.id, user_id, currency_pair_id, operation_type_id, exrate, amount_base, amount_convert, " +
                "commission_fixed_amount, date_creation, date_acception" +
                "  FROM EXORDERS " +
                (filterRole == null ? "" : " JOIN USER ON (USER.id=EXORDERS.user_id)  AND USER.roleid = :user_role_id ") +
                "  WHERE status_id = 2 and operation_type_id= 4 and currency_pair_id=:currency_pair_id" +
                "  ORDER BY exrate DESC";
        Map<String, Integer> namedParameters = new HashMap<>();
        namedParameters.put("currency_pair_id", currencyPair.getId());
        if (filterRole != null) {
            namedParameters.put("user_role_id", filterRole.getRole());
        }
        return slaveJdbcTemplate.query(sql, namedParameters, orderListDtoRowMapper());
    }

    @Override
    public OrderListDto getLastOrder(CurrencyPair pair, OperationType operationType, OrderBaseType... baseTypes) {
        String join = operationType == BUY
                ? " INNER JOIN (SELECT MAX(exrate) as exrate"
                : " INNER JOIN (SELECT MIN(exrate) as exrate";
        String sql = "SELECT t1.id as id, t1.user_id as user_id, t1.currency_pair_id as currency_pair_id, t1.operation_type_id as operation_type_id," +
                "  t1.exrate as exrate, t1.amount_base as amount_base, t1.amount_convert as amount_convert," +
                "  t1.commission_fixed_amount as commission_fixed_amount, t1.date_creation as date_creation, t1.date_acception as date_acception" +
                "  FROM EXORDERS t1 " +
                join +
                "            FROM EXORDERS" +
                "            WHERE status_id = 3" +
                "                 AND currency_pair_id = :currency_pair_id" +
                "                 AND operation_type_id = :operation_type_id" +
                "                 AND base_type IN (:base_types)" +
                "                 AND date_acception IS NOT NULL " +
                "                 AND date_acception - INTERVAL 1 DAY) t2" +
                "  ON t1.exrate = t2.exrate" +
                " ORDER BY t1.date_acception DESC LIMIT 1";
        List<String> bases = Arrays.stream(baseTypes).map(String::valueOf).collect(Collectors.toList());
        MapSqlParameterSource namedParameters = new MapSqlParameterSource();
        namedParameters.addValue("currency_pair_id", pair.getId());
        namedParameters.addValue("operation_type_id", operationType.getType());
        namedParameters.addValue("base_types", bases);
        try {
            return slaveJdbcTemplate.queryForObject(sql, namedParameters, orderListDtoRowMapper());
        } catch (Exception e) {
            return null;
        }
    }

    private RowMapper<OrderListDto> orderListDtoRowMapper() {
        return (rs, rowNum) -> {
            OrderListDto order = new OrderListDto();
            order.setId(rs.getInt("id"));
            order.setUserId(rs.getInt("user_id"));
            order.setOrderType(OperationType.convert(rs.getInt("operation_type_id")));
            order.setExrate(rs.getString("exrate"));
            order.setAmountBase(rs.getString("amount_base"));
            order.setAmountConvert(rs.getString("amount_convert"));
            order.setCreated(convertTimeStampToLocalDateTime(rs,"date_creation"));
            order.setAccepted(convertTimeStampToLocalDateTime(rs,"date_acception"));
            return order;
        };
    }

    private LocalDateTime convertTimeStampToLocalDateTime(ResultSet rs, String columnName) throws SQLException {
        Timestamp timestamp = rs.getTimestamp(columnName);
        if (timestamp == null) {
            return null;
        }
        return timestamp.toLocalDateTime();
    }

    @Override
    public Optional<BigDecimal> getLastOrderPriceByCurrencyPairAndOperationType(int currencyPairId, int operationTypeId) {
        String sql = "SELECT exrate FROM EXORDERS WHERE status_id = 3 AND currency_pair_id = :currency_pair_id AND operation_type_id = :operation_type_id " +
                "ORDER BY date_acception DESC, id DESC LIMIT 1";
        Map<String, Integer> namedParameters = new HashMap<>();
        namedParameters.put("currency_pair_id", currencyPairId);
        namedParameters.put("operation_type_id", operationTypeId);
        try {
            return Optional.of(namedParameterJdbcTemplate.queryForObject(sql, namedParameters, BigDecimal.class));
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    @Override
    public Optional<BigDecimal> getLastOrderPriceByCurrencyPair(int currencyPairId) {
        String sql = "SELECT exrate FROM EXORDERS WHERE status_id = 3 AND currency_pair_id = :currency_pair_id AND operation_type_id in (3,4) " +
                "ORDER BY date_acception DESC, id DESC LIMIT 1";
        Map<String, Integer> namedParameters = new HashMap<>();
        namedParameters.put("currency_pair_id", currencyPairId);
        try {
            return Optional.of(namedParameterJdbcTemplate.queryForObject(sql, namedParameters, BigDecimal.class));
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    @Override
    public Optional<BigDecimal> getLowestOpenOrderPriceByCurrencyPairAndOperationType(int currencyPairId, int operationTypeId) {
        String sql = "SELECT exrate FROM EXORDERS WHERE status_id = 2 AND currency_pair_id = :currency_pair_id AND operation_type_id = :operation_type_id " +
                "ORDER BY exrate ASC  LIMIT 1";
        Map<String, Integer> namedParameters = new HashMap<>();
        namedParameters.put("currency_pair_id", currencyPairId);
        namedParameters.put("operation_type_id", operationTypeId);
        try {
            return Optional.of(namedParameterJdbcTemplate.queryForObject(sql, namedParameters, BigDecimal.class));
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    @Override
    public ExOrder getOrderById(int orderId) {
        String sql = "SELECT * FROM EXORDERS WHERE id = :id";
        Map<String, String> namedParameters = new HashMap<>();
        namedParameters.put("id", String.valueOf(orderId));
        try {
            return namedParameterJdbcTemplate.queryForObject(sql, namedParameters, new OrderRowMapper());
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    @Override
    public boolean completeDeleteOrder(int orderId) {
        String sql = "DELETE FROM EXORDERS WHERE id = :id";
        Map<String, String> parameters = new HashMap<>();
        parameters.put("id", String.valueOf(orderId));
        return namedParameterJdbcTemplate.update(sql, parameters) > 0;
    }

    @Override
    public boolean setStatus(int orderId, OrderStatus status) {
        String sql = "UPDATE EXORDERS SET status_id=:status_id WHERE id = :id";
        Map<String, String> namedParameters = new HashMap<>();
        namedParameters.put("status_id", String.valueOf(status.getStatus()));
        namedParameters.put("id", String.valueOf(orderId));
        int result = namedParameterJdbcTemplate.update(sql, namedParameters);
        return result > 0;
    }

    @Override
    public boolean updateOrder(ExOrder exOrder) {
        String sql = "update EXORDERS set user_acceptor_id=:user_acceptor_id, status_id=:status_id, " +
                " date_acception=NOW()  " +
                " where id = :id";
        Map<String, String> namedParameters = new HashMap<>();
        namedParameters.put("user_acceptor_id", String.valueOf(exOrder.getUserAcceptorId()));
        namedParameters.put("status_id", String.valueOf(exOrder.getStatus().getStatus()));
//        namedParameters.put("date_acception", String.valueOf(exOrder.getDateAcception()));
        namedParameters.put("id", String.valueOf(exOrder.getId()));
        int result = namedParameterJdbcTemplate.update(sql, namedParameters);
        return result > 0;
    }

    @Override
    public List<Map<String, Object>> getDataForAreaChart(CurrencyPair currencyPair, BackDealInterval backDealInterval) {
        String sql = "SELECT date_acception, exrate, amount_base FROM EXORDERS " +
                " WHERE status_id=:status_id AND currency_pair_id=:currency_pair_id " +
                " AND date_acception >= now() - INTERVAL " + backDealInterval.getInterval() +
                " ORDER BY date_acception";

        Map<String, String> namedParameters = new HashMap<>();
        namedParameters.put("status_id", String.valueOf(3));
        namedParameters.put("currency_pair_id", String.valueOf(currencyPair.getId()));
        List<Map<String, Object>> rows = slaveJdbcTemplate.query(sql, namedParameters, (rs, row) -> {
            Map<String, Object> map = new HashMap<>();
            map.put("dateAcception", rs.getTimestamp("date_acception"));
            map.put("exrate", rs.getBigDecimal("exrate"));
            map.put("volume", rs.getBigDecimal("amount_base"));
            return map;
        });

        return rows;
    }

    @Override
    public List<CandleChartItemDto> getDataForCandleChart(CurrencyPair currencyPair, BackDealInterval backDealInterval) {
        return getCandleChartData(currencyPair, backDealInterval, "NOW()");
    }

    @Override
    public List<CandleChartItemDto> getDataForCandleChart(CurrencyPair currencyPair, BackDealInterval backDealInterval, LocalDateTime endTime) {
        String startTimeString = endTime.format(DateTimeFormatter.ofPattern(DEFAULT_DATE_FORMAT_PATTERN));
        String startTimeSql = String.format("STR_TO_DATE('%s', '%%Y-%%m-%%d %%H:%%i:%%s')", startTimeString);
        return getCandleChartData(currencyPair, backDealInterval, startTimeSql);
    }

    @Override
    public List<CandleChartItemDto> getDataForCandleChart(CurrencyPair currencyPair, LocalDateTime startTime, LocalDateTime endTime, int resolutionValue, String resolutionType) {

        int resolution = resolutionValue;
//        if (resolution == 240 || resolution == 720 || !"MINUTE".equals(resolutionType)) {
//            startTime = startTime.with(LocalTime.MIN);
//            endTime = endTime.with(LocalTime.MIN);
//        }
//
//        LocalDateTime start = startTime.truncatedTo(ChronoUnit.HOURS)
//                .plusMinutes(resolution * (startTime.getMinute() / resolution));
//        LocalDateTime end = endTime.truncatedTo(ChronoUnit.HOURS)
//                .plusMinutes(resolution * (startTime.getMinute() / resolution));

        String startTimeString = startTime.format(DateTimeFormatter.ofPattern(DEFAULT_DATE_FORMAT_PATTERN));
        String endTimeString = LocalDateTime.now().format(DateTimeFormatter.ofPattern(DEFAULT_DATE_FORMAT_PATTERN));
        String sql = "{call GET_DATA_FOR_CANDLE_RANGE(" +
                "STR_TO_DATE(:start_point, '%Y-%m-%d %H:%i:%s'), " +
                "STR_TO_DATE(:end_point, '%Y-%m-%d %H:%i:%s'), " +
                ":step_value, :step_type, :currency_pair_id)}";
        Map<String, Object> params = new HashMap<>();
        params.put("start_point", startTimeString);
        params.put("end_point", endTimeString);
        params.put("step_value", resolutionValue);
        params.put("step_type", resolutionType);
        params.put("currency_pair_id", currencyPair.getId());
        return namedParameterJdbcTemplate.execute(sql, params, ps -> {
            ResultSet rs = ps.executeQuery();
            List<CandleChartItemDto> list = new ArrayList<>();
            while (rs.next()) {
                CandleChartItemDto candleChartItemDto = new CandleChartItemDto();
                candleChartItemDto.setBeginDate(rs.getTimestamp("pred_point"));
                candleChartItemDto.setBeginPeriod(rs.getTimestamp("pred_point").toLocalDateTime());
                candleChartItemDto.setEndDate(rs.getTimestamp("current_point"));
                candleChartItemDto.setEndPeriod(rs.getTimestamp("current_point").toLocalDateTime());
                candleChartItemDto.setOpenRate(rs.getBigDecimal("open_rate"));
                candleChartItemDto.setCloseRate(rs.getBigDecimal("close_rate"));
                candleChartItemDto.setLowRate(rs.getBigDecimal("low_rate"));
                candleChartItemDto.setHighRate(rs.getBigDecimal("high_rate"));
                candleChartItemDto.setBaseVolume(rs.getBigDecimal("base_volume"));
                list.add(candleChartItemDto);
            }
            rs.close();
            return list;
        });
    }

    private List<CandleChartItemDto> getCandleChartData(CurrencyPair currencyPair, BackDealInterval backDealInterval, String startTimeSql) {
        String s = "{call GET_DATA_FOR_CANDLE(" + startTimeSql + ", " + backDealInterval.getIntervalValue() + ", '" + backDealInterval.getIntervalType().name() + "', " + currencyPair.getId() + ")}";
        List<CandleChartItemDto> result = namedParameterJdbcTemplate.execute(s, ps -> {
            ResultSet rs = ps.executeQuery();
            List<CandleChartItemDto> list = new ArrayList<>();
            while (rs.next()) {
                CandleChartItemDto candleChartItemDto = new CandleChartItemDto();
                candleChartItemDto.setBeginDate(rs.getTimestamp("pred_point"));
                candleChartItemDto.setBeginPeriod(rs.getTimestamp("pred_point").toLocalDateTime());
                candleChartItemDto.setEndDate(rs.getTimestamp("current_point"));
                candleChartItemDto.setEndPeriod(rs.getTimestamp("current_point").toLocalDateTime());
                candleChartItemDto.setOpenRate(rs.getBigDecimal("open_rate"));
                candleChartItemDto.setCloseRate(rs.getBigDecimal("close_rate"));
                candleChartItemDto.setLowRate(rs.getBigDecimal("low_rate"));
                candleChartItemDto.setHighRate(rs.getBigDecimal("high_rate"));
                candleChartItemDto.setBaseVolume(rs.getBigDecimal("base_volume"));
                list.add(candleChartItemDto);
            }
            rs.close();
            return list;
        });
        return result;
    }

    @Override
    public ExOrderStatisticsDto getOrderStatistic(CurrencyPair currencyPair, BackDealInterval backDealInterval) {
        String sql = "SELECT FIRSTORDER.amount_base AS first_amount_base, FIRSTORDER.exrate AS first_exrate," +
                "            LASTORDER.amount_base AS last_amount_base, LASTORDER.exrate AS last_exrate," +
                "            AGRIGATE.* " +
                "     FROM  " +
                "       (SELECT EXORDERS.currency_pair_id AS currency_pair_id," +
                "       MIN(EXORDERS.date_acception) AS first_date_acception, MAX(EXORDERS.date_acception) AS last_date_acception,  " +
                "       MIN(EXORDERS.exrate) AS min_exrate, MAX(EXORDERS.exrate) AS max_exrate,  " +
                "       SUM(EXORDERS.amount_base) AS deal_sum_base, SUM(EXORDERS.amount_convert) AS deal_sum_convert  " +
                "       FROM EXORDERS  " +
                "       WHERE   " +
                "       EXORDERS.currency_pair_id = :currency_pair_id AND EXORDERS.status_id = :status_id AND   " +
                "       EXORDERS.date_acception >= now() - INTERVAL " + backDealInterval.getInterval() +
                "       GROUP BY currency_pair_id " +
                "       ) AGRIGATE " +
                "     LEFT JOIN EXORDERS FIRSTORDER ON (FIRSTORDER.currency_pair_id = AGRIGATE.currency_pair_id) AND (FIRSTORDER.date_acception = AGRIGATE.first_date_acception)  " +
                "     LEFT JOIN EXORDERS LASTORDER ON (LASTORDER.currency_pair_id = AGRIGATE.currency_pair_id) AND (LASTORDER.date_acception = AGRIGATE.last_date_acception)" +
                " ORDER BY FIRSTORDER.id ASC, LASTORDER.id DESC LIMIT 1 ";
        Map<String, String> namedParameters = new HashMap<>();
        namedParameters.put("status_id", String.valueOf(3));
        namedParameters.put("currency_pair_id", String.valueOf(currencyPair.getId()));
        try {
            return slaveJdbcTemplate.queryForObject(sql, namedParameters, new RowMapper<ExOrderStatisticsDto>() {
                @Override
                public ExOrderStatisticsDto mapRow(ResultSet rs, int rowNum) throws SQLException {
                    ExOrderStatisticsDto exOrderStatisticsDto = new ExOrderStatisticsDto(currencyPair);
                    exOrderStatisticsDto.setFirstOrderAmountBase(rs.getString("first_amount_base"));
                    exOrderStatisticsDto.setFirstOrderRate(rs.getString("first_exrate"));
                    exOrderStatisticsDto.setLastOrderAmountBase(rs.getString("last_amount_base"));
                    exOrderStatisticsDto.setLastOrderRate(rs.getString("last_exrate"));
                    exOrderStatisticsDto.setMinRate(rs.getString("min_exrate"));
                    exOrderStatisticsDto.setMaxRate(rs.getString("max_exrate"));
                    exOrderStatisticsDto.setSumBase(rs.getString("deal_sum_base"));
                    exOrderStatisticsDto.setSumConvert(rs.getString("deal_sum_convert"));
                    return exOrderStatisticsDto;
                }
            });
        } catch (EmptyResultDataAccessException e) {
            return new ExOrderStatisticsDto(currencyPair);
        }
    }

    @Override
    public List<ExOrderStatisticsShortByPairsDto> getOrderStatisticByPairs() {
        long before = System.currentTimeMillis();
        try {
            String sql =
                    "SELECT RESULT.currency_pair_name, RESULT.market, RESULT.currency_pair_id, RESULT.type, RESULT.last_exrate, RESULT.pred_last_exrate, RESULT.pair_order, RESULT.volume " +
                            "FROM " +
                            "((SELECT  " +
                            "   CURRENCY_PAIR.name AS currency_pair_name, CURRENCY_PAIR.market AS market, CURRENCY_PAIR.id AS currency_pair_id, CURRENCY_PAIR.type AS type, " +
                            "   (SELECT SUM(EX.amount_base) " +
                            "       FROM EXORDERS EX " +
                            "       WHERE " +
                            "       (EX.currency_pair_id = AGRIGATE.currency_pair_id)  AND " +
                            "       (EX.status_id = AGRIGATE.status_id)) AS volume, " +
                            "   (SELECT LASTORDER.exrate " +
                            "       FROM EXORDERS LASTORDER  " +
                            "       WHERE  " +
                            "       (LASTORDER.currency_pair_id =AGRIGATE.currency_pair_id)  AND  " +
                            "       (LASTORDER.status_id =AGRIGATE.status_id) " +
                            "       ORDER BY LASTORDER.date_acception DESC, LASTORDER.id DESC " +
                            "       LIMIT 1) AS last_exrate, " +
                            "   (SELECT PRED_LASTORDER.exrate " +
                            "       FROM EXORDERS PRED_LASTORDER  " +
                            "       WHERE  " +
                            "       (PRED_LASTORDER.currency_pair_id =AGRIGATE.currency_pair_id)  AND  " +
                            "       (PRED_LASTORDER.status_id =AGRIGATE.status_id) " +
                            "       ORDER BY PRED_LASTORDER.date_acception DESC, PRED_LASTORDER.id DESC " +
                            "       LIMIT 1,1) AS pred_last_exrate, CURRENCY_PAIR.pair_order  " +
                            " FROM ( " +
                            "   SELECT DISTINCT" +
                            "   EXORDERS.status_id AS status_id,  " +
                            "   EXORDERS.currency_pair_id AS currency_pair_id " +
                            "   FROM EXORDERS          " +
                            "   WHERE EXORDERS.status_id = :status_id         " +
                            "   ) " +
                            " AGRIGATE " +
                            " JOIN CURRENCY_PAIR ON (CURRENCY_PAIR.id = AGRIGATE.currency_pair_id) AND (CURRENCY_PAIR.hidden != 1) " +
                            " ORDER BY -CURRENCY_PAIR.pair_order DESC)" +
                            " UNION ALL (" +
                            "   SELECT CP.name AS currency_pair_name, CP.market AS market, CP.id AS currency_pair_id, CP.type AS type, 0 AS volume, 0 AS last_exrate, 0 AS pred_last_exrate, CP.pair_order " +
                            "      FROM CURRENCY_PAIR CP " +
                            "      WHERE CP.id NOT IN(SELECT DISTINCT EXORDERS.currency_pair_id AS currency_pair_id FROM EXORDERS WHERE EXORDERS.status_id = :status_id) AND CP.hidden = 0 " +
                            ")) RESULT ";
            Map<String, String> namedParameters = new HashMap<>();
            namedParameters.put("status_id", String.valueOf(3));
            return slaveJdbcTemplate.query(sql, namedParameters, exchangeRatesRowMapper);
        } catch (Exception e) {
            long after = System.currentTimeMillis();
            LOGGER.error("error... ms: " + (after - before) + " : " + e);
            throw new OrderDaoException(e);
        } finally {
            long after = System.currentTimeMillis();
            LOGGER.debug("query completed ... ms: " + (after - before));
        }
    }

    @Override
    public List<ExOrderStatisticsShortByPairsDto> getOrderStatisticForSomePairs(List<Integer> pairsIds) {
        long before = System.currentTimeMillis();
        try {
            String sql = "SELECT " +
                    "   CP.name AS currency_pair_name, CP.market AS market, CP.id AS currency_pair_id, CP.type AS type,      " +
                    "   (SELECT SUM(EX.amount_base) " +
                    "       FROM EXORDERS EX  " +
                    "       WHERE  " +
                    "       (EX.currency_pair_id = CP.id)  AND  " +
                    "       (EX.status_id = :status_id)) AS volume, " +
                    "   (SELECT LASTORDER.exrate " +
                    "       FROM EXORDERS LASTORDER  " +
                    "       WHERE  " +
                    "       (LASTORDER.currency_pair_id = CP.id)  AND  " +
                    "       (LASTORDER.status_id = :status_id) " +
                    "       ORDER BY LASTORDER.date_acception DESC, LASTORDER.id DESC " +
                    "       LIMIT 1) AS last_exrate, " +
                    "   (SELECT PRED_LASTORDER.exrate " +
                    "       FROM EXORDERS PRED_LASTORDER  " +
                    "       WHERE  " +
                    "       (PRED_LASTORDER.currency_pair_id = CP.id)  AND  " +
                    "       (PRED_LASTORDER.status_id = :status_id) " +
                    "       ORDER BY PRED_LASTORDER.date_acception DESC, PRED_LASTORDER.id DESC " +
                    "       LIMIT 1,1) AS pred_last_exrate, CP.pair_order  " +
                    " FROM CURRENCY_PAIR CP " +
                    " WHERE CP.id IN (:pair_id) AND CP.hidden != 1";

            Map<String, Object> namedParameters = new HashMap<>();
            namedParameters.put("status_id", String.valueOf(3));
            namedParameters.put("pair_id", pairsIds);
            return slaveJdbcTemplate.query(sql, namedParameters, exchangeRatesRowMapper);
        } catch (Exception e) {
            long after = System.currentTimeMillis();
            LOGGER.error("error... ms: " + (after - before) + " : " + e);
            throw e;
        } finally {
            long after = System.currentTimeMillis();
            LOGGER.debug("query completed ... ms: " + (after - before));
        }
    }

    @Override
    public List<CoinmarketApiDto> getCoinmarketData(String currencyPairName) {
        String s = "{call GET_COINMARKETCAP_STATISTICS('" + currencyPairName + "')}";
        List<CoinmarketApiDto> result = namedParameterJdbcTemplate.execute(s, new PreparedStatementCallback<List<CoinmarketApiDto>>() {
            @Override
            public List<CoinmarketApiDto> doInPreparedStatement(PreparedStatement ps) throws SQLException, DataAccessException {
                ResultSet rs = ps.executeQuery();
                List<CoinmarketApiDto> list = new ArrayList();
                while (rs.next()) {
                    CoinmarketApiDto coinmarketApiDto = new CoinmarketApiDto();
                    coinmarketApiDto.setCurrencyPairId(rs.getInt("currency_pair_id"));
                    coinmarketApiDto.setCurrency_pair_name(rs.getString("currency_pair_name"));
                    coinmarketApiDto.setFirst(rs.getBigDecimal("first"));
                    coinmarketApiDto.setLast(rs.getBigDecimal("last"));
                    coinmarketApiDto.setLowestAsk(rs.getBigDecimal("lowestAsk"));
                    coinmarketApiDto.setHighestBid(rs.getBigDecimal("highestBid"));
                    coinmarketApiDto.setPercentChange(BigDecimalProcessing.doAction(coinmarketApiDto.getFirst(), coinmarketApiDto.getLast(), ActionType.PERCENT_GROWTH));
                    coinmarketApiDto.setBaseVolume(rs.getBigDecimal("baseVolume"));
                    coinmarketApiDto.setQuoteVolume(rs.getBigDecimal("quoteVolume"));
                    coinmarketApiDto.setIsFrozen(rs.getInt("isFrozen"));
                    coinmarketApiDto.setHigh24hr(rs.getBigDecimal("high24hr"));
                    coinmarketApiDto.setLow24hr(rs.getBigDecimal("low24hr"));
                    list.add(coinmarketApiDto);
                }
                rs.close();
                return list;
            }
        });
        return result;
    }

    @Override
    public OrderInfoDto getOrderInfo(int orderId, Locale locale) {
        String sql =
                " SELECT  " +
                        "     EXORDERS.id, EXORDERS.date_creation, EXORDERS.date_acception, EXORDERS.base_type, " +
                        "     ORDER_STATUS.name AS order_status_name,  " +
                        "     CURRENCY_PAIR.name as currency_pair_name,  " +
                        "     UPPER(ORDER_OPERATION.name) AS order_type_name,  " +
                        "     EXORDERS.exrate, EXORDERS.amount_base, EXORDERS.amount_convert, " +
                        "     ORDER_CURRENCY_BASE.name as currency_base_name, ORDER_CURRENCY_CONVERT.name as currency_convert_name, " +
                        "     CREATOR.email AS order_creator_email, " +
                        "     ACCEPTOR.email AS order_acceptor_email, " +
                        "     COUNT(TRANSACTION.id) AS transaction_count,  " +
                        "     SUM(TRANSACTION.commission_amount) AS company_commission," +
                        "     EXORDERS.order_source_id AS source_id  " +
                        " FROM EXORDERS " +
                        "      JOIN ORDER_STATUS ON (ORDER_STATUS.id = EXORDERS.status_id) " +
                        "      JOIN OPERATION_TYPE AS ORDER_OPERATION ON (ORDER_OPERATION.id = EXORDERS.operation_type_id) " +
                        "      JOIN CURRENCY_PAIR ON (CURRENCY_PAIR.id = EXORDERS.currency_pair_id) " +
                        "      JOIN CURRENCY ORDER_CURRENCY_BASE ON (ORDER_CURRENCY_BASE.id = CURRENCY_PAIR.currency1_id)   " +
                        "      JOIN CURRENCY ORDER_CURRENCY_CONVERT ON (ORDER_CURRENCY_CONVERT.id = CURRENCY_PAIR.currency2_id)  " +
                        "      JOIN WALLET ORDER_CREATOR_RESERVED_WALLET ON  " +
                        "              (ORDER_CREATOR_RESERVED_WALLET.user_id=EXORDERS.user_id) AND  " +
                        "              ( " +
                        "                  (upper(ORDER_OPERATION.name)='BUY' AND ORDER_CREATOR_RESERVED_WALLET.currency_id = CURRENCY_PAIR.currency2_id)  " +
                        "                  OR  " +
                        "                  (upper(ORDER_OPERATION.name)='SELL' AND ORDER_CREATOR_RESERVED_WALLET.currency_id = CURRENCY_PAIR.currency1_id) " +
                        "              ) " +
                        "      JOIN USER CREATOR ON (CREATOR.id = EXORDERS.user_id) " +
                        "      LEFT JOIN USER ACCEPTOR ON (ACCEPTOR.id = EXORDERS.user_acceptor_id) " +
                        "      LEFT JOIN TRANSACTION ON (TRANSACTION.source_type='ORDER') AND (TRANSACTION.source_id = EXORDERS.id) " +
                        "      LEFT JOIN OPERATION_TYPE TRANSACTION_OPERATION ON (TRANSACTION_OPERATION.id = TRANSACTION.operation_type_id) " +
                        "      LEFT JOIN WALLET USER_WALLET ON (USER_WALLET.id = TRANSACTION.user_wallet_id) " +
                        "      LEFT JOIN COMPANY_WALLET ON (COMPANY_WALLET.currency_id = TRANSACTION.company_wallet_id) and (TRANSACTION.commission_amount <> 0) " +
                        "      LEFT JOIN USER ON (USER.id = USER_WALLET.user_id) " +
                        " WHERE EXORDERS.id=:order_id" +
                        " GROUP BY " +
                        "     EXORDERS.id, EXORDERS.date_creation, EXORDERS.date_acception,  " +
                        "     order_status_name,  " +
                        "     currency_pair_name,  " +
                        "     order_type_name,  " +
                        "     EXORDERS.exrate, EXORDERS.amount_base, EXORDERS.amount_convert, " +
                        "     currency_base_name, currency_convert_name, " +
                        "     order_creator_email, " +
                        "     order_acceptor_email ";
        Map<String, String> mapParameters = new HashMap<>();
        mapParameters.put("order_id", String.valueOf(orderId));
        try {
            return slaveJdbcTemplate.queryForObject(sql, mapParameters, new RowMapper<OrderInfoDto>() {
                @Override
                public OrderInfoDto mapRow(ResultSet rs, int rowNum) throws SQLException {
                    OrderInfoDto orderInfoDto = new OrderInfoDto();
                    OrderBaseType orderBaseType = OrderBaseType.valueOf(rs.getString("base_type"));
                    orderInfoDto.setId(rs.getInt("id"));
                    orderInfoDto.setDateCreation(rs.getTimestamp("date_creation").toLocalDateTime());
                    orderInfoDto.setDateAcception(rs.getTimestamp("date_acception") == null ? null : rs.getTimestamp("date_acception").toLocalDateTime());
                    orderInfoDto.setCurrencyPairName(rs.getString("currency_pair_name"));
                    orderInfoDto.setOrderTypeName(rs.getString("order_type_name").concat(" ").concat(orderBaseType.name()));
                    orderInfoDto.setOrderStatusName(rs.getString("order_status_name"));
                    orderInfoDto.setExrate(BigDecimalProcessing.formatLocale(rs.getBigDecimal("exrate"), locale, 2));
                    orderInfoDto.setAmountBase(BigDecimalProcessing.formatLocale(rs.getBigDecimal("amount_base"), locale, 2));
                    orderInfoDto.setAmountConvert(BigDecimalProcessing.formatLocale(rs.getBigDecimal("amount_convert"), locale, 2));
                    orderInfoDto.setCurrencyBaseName(rs.getString("currency_base_name"));
                    orderInfoDto.setCurrencyConvertName(rs.getString("currency_convert_name"));
                    orderInfoDto.setOrderCreatorEmail(rs.getString("order_creator_email"));
                    orderInfoDto.setOrderAcceptorEmail(rs.getString("order_acceptor_email"));
                    orderInfoDto.setTransactionCount(BigDecimalProcessing.formatLocale(rs.getBigDecimal("transaction_count"), locale, 2));
                    orderInfoDto.setCompanyCommission(BigDecimalProcessing.formatLocale(rs.getBigDecimal("company_commission"), locale, 2));
                    orderInfoDto.setSource((Integer) rs.getObject("source_id"));
                    orderInfoDto.setChildren(getOrderChildren(orderId));
                    return orderInfoDto;
                }
            });
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    private List<Integer> getOrderChildren(int id) {
        String sql = "SELECT id FROM EXORDERS WHERE order_source_id = :id";
        return slaveJdbcTemplate.queryForList(sql, Collections.singletonMap("id", id), Integer.class);
    }

    @Override
    public int searchOrderByAdmin(Integer currencyPair, Integer orderType, String orderDate, BigDecimal orderRate, BigDecimal orderVolume) {
        String sql = "SELECT id " +
                "  FROM EXORDERS" +
                "  WHERE (    " +
                "      EXORDERS.currency_pair_id = :currency_pair_id AND " +
                "      EXORDERS.operation_type_id = :operation_type_id AND " +
                "      DATE_FORMAT(EXORDERS.date_creation, '%Y-%m-%d %H:%i:%s') = STR_TO_DATE(:date_creation, '%Y-%m-%d %H:%i:%s') AND " +
                "      EXORDERS.exrate = :exrate AND " +
                "      EXORDERS.amount_base = :amount_base" +
                "  )" +
                "  LIMIT 1";
        Map<String, String> namedParameters = new HashMap<>();
        namedParameters.put("currency_pair_id", String.valueOf(currencyPair));
        namedParameters.put("operation_type_id", String.valueOf(orderType));
        namedParameters.put("date_creation", orderDate);
        namedParameters.put("exrate", String.valueOf(orderRate));
        namedParameters.put("amount_base", String.valueOf(orderVolume));
        try {
            return slaveJdbcTemplate.queryForObject(sql, namedParameters, new RowMapper<Integer>() {
                @Override
                public Integer mapRow(ResultSet rs, int rowNum) throws SQLException {
                    return rs.getInt(1);
                }
            });
        } catch (EmptyResultDataAccessException e) {
            return -1;
        }
    }

    @Override
    public List<OrderAcceptedHistoryDto> getOrderAcceptedForPeriod(String email, BackDealInterval backDealInterval, Integer limit, CurrencyPair currencyPair) {
        String sql = "SELECT EXORDERS.id, EXORDERS.date_acception, EXORDERS.exrate, EXORDERS.amount_base, EXORDERS.operation_type_id " +
                "  FROM EXORDERS " +
                (email == null || email.isEmpty() ? "" : " JOIN USER ON ((USER.id = EXORDERS.user_id) OR (USER.id = EXORDERS.user_acceptor_id)) AND USER.email='" + email + "'") +
                "  WHERE EXORDERS.status_id = :status " +
                "  AND EXORDERS.date_acception >= now() - INTERVAL " + backDealInterval.getInterval() +
                "  AND EXORDERS.currency_pair_id = :currency_pair_id " +
                "  ORDER BY EXORDERS.date_acception DESC, EXORDERS.id DESC " +
                (limit == -1 ? "" : "  LIMIT " + limit);
        Map<String, Object> params = new HashMap<String, Object>() {{
            put("status", 3);
            put("currency_pair_id", currencyPair.getId());
        }};
        return slaveJdbcTemplate.query(sql, params, (rs, rowNum) -> {
            OrderAcceptedHistoryDto orderAcceptedHistoryDto = new OrderAcceptedHistoryDto();
            orderAcceptedHistoryDto.setOrderId(rs.getInt("id"));
            orderAcceptedHistoryDto.setDateAcceptionTime(rs.getTimestamp("date_acception").toLocalDateTime().toLocalTime().format(DateTimeFormatter.ISO_LOCAL_TIME));
            orderAcceptedHistoryDto.setAcceptionTime(rs.getTimestamp("date_acception"));
            orderAcceptedHistoryDto.setRate(rs.getString("exrate"));
            String amountBase = rs.getString("amount_base");
            BigDecimal bigDecimal = new BigDecimal(amountBase);
            BigDecimal normalize = BigDecimalProcessing.normalize(bigDecimal, RoundingMode.HALF_UP);
            orderAcceptedHistoryDto.setAmountBase(normalize.toString());
            orderAcceptedHistoryDto.setOperationType(OperationType.convert(rs.getInt("operation_type_id")));
            return orderAcceptedHistoryDto;
        });
    }

    @Override
    public OrderCommissionsDto getCommissionForOrder(UserRole userRole) {
        final String sql =
                "  SELECT SUM(sell_commission) as sell_commission, SUM(buy_commission) as buy_commission " +
                        "  FROM " +
                        "      ((SELECT SELL.value as sell_commission, 0 as buy_commission " +
                        "      FROM COMMISSION SELL " +
                        "      WHERE operation_type = 3 AND user_role = :user_role " +
                        "      ORDER BY date DESC LIMIT 1)  " +
                        "    UNION " +
                        "      (SELECT 0, BUY.value " +
                        "      FROM COMMISSION BUY " +
                        "      WHERE operation_type = 4 AND user_role = :user_role " +
                        "      ORDER BY date DESC LIMIT 1) " +
                        "  ) COMMISSION";
        try {
            Map<String, Integer> params = Collections.singletonMap("user_role", userRole.getRole());
            return namedParameterJdbcTemplate.queryForObject(sql, params, (rs, rowNum) -> {
                OrderCommissionsDto orderCommissionsDto = new OrderCommissionsDto();
                orderCommissionsDto.setSellCommission(rs.getBigDecimal("sell_commission"));
                orderCommissionsDto.setBuyCommission(rs.getBigDecimal("buy_commission"));
                return orderCommissionsDto;
            });
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    @Override
    public CommissionsDto getAllCommissions(UserRole userRole) {
        final String sql =
                "  SELECT SUM(sell_commission) as sell_commission, SUM(buy_commission) as buy_commission, " +
                        "SUM(input_commission) as input_commission, SUM(output_commission) as output_commission, SUM(transfer_commission) as transfer_commission" +
                        "  FROM " +
                        "      ((SELECT SELL.value as sell_commission, 0 as buy_commission, 0 as input_commission, 0 as output_commission, " +
                        " 0 as transfer_commission " +
                        "      FROM COMMISSION SELL " +
                        "      WHERE operation_type = 3 AND user_role = :user_role " +
                        "      ORDER BY date DESC LIMIT 1)  " +
                        "    UNION " +
                        "      (SELECT 0, BUY.value, 0, 0, 0 " +
                        "      FROM COMMISSION BUY " +
                        "      WHERE operation_type = 4  AND user_role = :user_role " +
                        "      ORDER BY date DESC LIMIT 1) " +
                        "    UNION " +
                        "      (SELECT 0, 0, INPUT.value, 0, 0  " +
                        "      FROM COMMISSION INPUT " +
                        "      WHERE operation_type = 1  AND user_role = :user_role " +
                        "      ORDER BY date DESC LIMIT 1) " +
                        "    UNION " +
                        "      (SELECT 0, 0, 0, OUTPUT.value, 0  " +
                        "      FROM COMMISSION OUTPUT " +
                        "      WHERE operation_type = 2 AND user_role = :user_role  " +
                        "      ORDER BY date DESC LIMIT 1) " +
                        "    UNION " +
                        "      (SELECT 0, 0, 0, 0, TRANSFER.value " +
                        "      FROM COMMISSION TRANSFER " +
                        "      WHERE operation_type = 9 AND user_role = :user_role  " +
                        "      ORDER BY date DESC LIMIT 1) " +
                        "  ) COMMISSION";
        try {
            Map<String, Integer> params = Collections.singletonMap("user_role", userRole.getRole());
            return slaveJdbcTemplate.queryForObject(sql, params, (rs, row) -> {
                CommissionsDto commissionsDto = new CommissionsDto();
                commissionsDto.setSellCommission(rs.getBigDecimal("sell_commission"));
                commissionsDto.setBuyCommission(rs.getBigDecimal("buy_commission"));
                commissionsDto.setInputCommission(rs.getBigDecimal("input_commission"));
                commissionsDto.setOutputCommission(rs.getBigDecimal("output_commission"));
                commissionsDto.setTransferCommission(rs.getBigDecimal("transfer_commission"));
                return commissionsDto;
            });
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    @Override
    public List<OrderWideListDto> getMyOrdersWithState(Integer userId, CurrencyPair currencyPair, OrderStatus status,
                                                       OperationType operationType,
                                                       String scope, Integer offset, Integer limit, Locale locale) {
        return getMyOrdersWithState(userId, currencyPair, Collections.singletonList(status), operationType, scope, offset, limit, locale);
    }

    @Override
    public List<OrderWideListDto> getMyOrdersWithState(Integer userId, CurrencyPair currencyPair, List<OrderStatus> statuses,
                                                       OperationType operationType,
                                                       String scope, Integer offset, Integer limit, Locale locale) {
        String userFilterClause;
        String joinClause = "";

        if (scope == null || scope.isEmpty()) {
            scope = "OTHER";
        }

        switch (scope) {
            case "ALL":
                userFilterClause = " AND (EXORDERS.user_id = :user_id OR EXORDERS.user_acceptor_id = :user_id) ";
                joinClause = " LEFT JOIN EXORDERS EX2 ON EXORDERS.id = EX2.counter_order_id ";
                break;
            case "ACCEPTED":
                userFilterClause = " AND EXORDERS.user_acceptor_id = :user_id ";
                break;
            default:
                userFilterClause = " AND EXORDERS.user_id = :user_id ";
                break;
        }

        List<Integer> statusIds = statuses.stream().map(OrderStatus::getStatus).collect(Collectors.toList());
        List<Integer> operationTypesIds = Arrays.asList(3, 4);

        String orderClause = "  ORDER BY -date_acception ASC, date_creation DESC";
        if (statusIds.size() > 1) {
            orderClause = "  ORDER BY status_modification_date DESC";
        }
        String sql = "SELECT EXORDERS.*, CURRENCY_PAIR.name AS currency_pair_name" +
                "  FROM EXORDERS " +
                "  JOIN CURRENCY_PAIR ON (CURRENCY_PAIR.id = EXORDERS.currency_pair_id) " +
                "  WHERE (status_id IN (:status_ids))" +
                "    AND (operation_type_id IN (:operation_type_id))" +
                (currencyPair == null ? "" : " AND EXORDERS.currency_pair_id=" + currencyPair.getId()) +
                userFilterClause +
                orderClause +
                (limit == -1 ? "" : "  LIMIT " + limit + " OFFSET " + offset);
        Map<String, Object> namedParameters = new HashMap<>();
        namedParameters.put("user_id", userId);
        namedParameters.put("status_ids", statusIds);
        if (operationType != null) {
            namedParameters.put("operation_type_id", operationType.getType());
        } else {
            namedParameters.put("operation_type_id", operationTypesIds);
        }
        return slaveJdbcTemplate.query(sql, namedParameters, new RowMapper<OrderWideListDto>() {
            @Override
            public OrderWideListDto mapRow(ResultSet rs, int rowNum) throws SQLException {
                OrderWideListDto orderWideListDto = new OrderWideListDto();
                orderWideListDto.setId(rs.getInt("id"));
                orderWideListDto.setUserId(rs.getInt("user_id"));
                orderWideListDto.setOperationTypeEnum(OperationType.convert(rs.getInt("operation_type_id")));
                orderWideListDto.setExExchangeRate(BigDecimalProcessing.formatLocale(rs.getBigDecimal("exrate"), locale, 2));
                orderWideListDto.setAmountBase(BigDecimalProcessing.formatLocale(rs.getBigDecimal("amount_base"), locale, 2));
                orderWideListDto.setAmountConvert(BigDecimalProcessing.formatLocale(rs.getBigDecimal("amount_convert"), locale, 2));
                orderWideListDto.setComissionId(rs.getInt("commission_id"));
                orderWideListDto.setCommissionFixedAmount(BigDecimalProcessing.formatLocale(rs.getBigDecimal("commission_fixed_amount"), locale, 2));
                BigDecimal amountWithCommission = rs.getBigDecimal("amount_convert");
                if (orderWideListDto.getOperationTypeEnum() == OperationType.SELL) {
                    amountWithCommission = BigDecimalProcessing.doAction(amountWithCommission, rs.getBigDecimal("commission_fixed_amount"), ActionType.SUBTRACT);
                } else if (orderWideListDto.getOperationTypeEnum() == OperationType.BUY) {
                    amountWithCommission = BigDecimalProcessing.doAction(amountWithCommission, rs.getBigDecimal("commission_fixed_amount"), ActionType.ADD);
                }
                orderWideListDto.setAmountWithCommission(BigDecimalProcessing.formatLocale(amountWithCommission, locale, 2));
                orderWideListDto.setUserAcceptorId(rs.getInt("user_acceptor_id"));
                orderWideListDto.setDateCreation(rs.getTimestamp("date_creation") == null ? null : rs.getTimestamp("date_creation").toLocalDateTime());
                orderWideListDto.setDateAcception(rs.getTimestamp("date_acception") == null ? null : rs.getTimestamp("date_acception").toLocalDateTime());
                orderWideListDto.setStatus(OrderStatus.convert(rs.getInt("status_id")));
                orderWideListDto.setDateStatusModification(rs.getTimestamp("status_modification_date") == null ? null : rs.getTimestamp("status_modification_date").toLocalDateTime());
                orderWideListDto.setCurrencyPairId(rs.getInt("currency_pair_id"));
                orderWideListDto.setCurrencyPairName(rs.getString("currency_pair_name"));
                orderWideListDto.setOrderBaseType(OrderBaseType.valueOf(rs.getString("base_type")));
                orderWideListDto.setOperationType(String.join(" ", orderWideListDto.getOperationTypeEnum().name(), orderWideListDto.getOrderBaseType().name()));
                return orderWideListDto;
            }
        });
    }

    @Override
    public List<OrderWideListDto> getMyOrdersWithState(Integer userId, OrderStatus status, CurrencyPair currencyPair, Locale locale,
                                                       String scope, Integer offset, Integer limit, Map<String, String> sortedColumns) {
        String userFilterClause;
        String currencyPairClauseJoin = currencyPair == null ? "" : " JOIN CURRENCY_PAIR ON (CURRENCY_PAIR.id = EXORDERS.currency_pair_id) ";
        String currencyPairClauseWhere = currencyPair == null ? "" : " AND EXORDERS.currency_pair_id = :currencyPairId ";

        switch (scope) {
            case "ALL":
                userFilterClause = " AND (EXORDERS.user_id = :user_id OR EXORDERS.user_acceptor_id = :user_id) ";
                break;
            case "ACCEPTED":
                userFilterClause = " AND EXORDERS.user_acceptor_id = :user_id ";
                break;
            default:
                userFilterClause = " AND EXORDERS.user_id = :user_id ";
                break;
        }

        List<Integer> operationTypesIds = Arrays.asList(3, 4);
        List<String> sortingRules = sortedColumns
                .entrySet()
                .stream()
                .map(e -> String.format("%s %s", e.getKey(), e.getValue()))
                .collect(Collectors.toList());

        String orderClause = sortingRules.isEmpty()
                ? " ORDER BY date_creation DESC "
                : " ORDER BY " + String.join(", ", sortingRules) + " ";

        String pageClause = "LIMIT ";
        pageClause += limit != 14 ? String.valueOf(limit) : "14";
        pageClause += offset > 0 ? " OFFSET " + String.valueOf(offset) : "";

        String sql = "SELECT EXORDERS.*, CURRENCY_PAIR.name AS currency_pair_name, com.value AS commission_value" +
                "  FROM EXORDERS " +
                currencyPairClauseJoin +
                " INNER JOIN COMMISSION com ON commission_id = com.id  WHERE (status_id = :statusId) " +
                "    AND (operation_type_id IN (:operation_type_id)) " +
                currencyPairClauseWhere +
                userFilterClause +
                orderClause +
                pageClause;
        Map<String, Object> namedParameters = new HashMap<>();
        namedParameters.put("user_id", userId);
        namedParameters.put("operation_type_id", operationTypesIds);
        namedParameters.put("statusId", status.getStatus());
        if (currencyPair != null) {
            namedParameters.put("currencyPairId", currencyPair.getId());
        }

        return slaveJdbcTemplate.query(sql, namedParameters, (rs, rowNum) -> {
            OrderWideListDto orderWideListDto = new OrderWideListDto();
            orderWideListDto.setId(rs.getInt("id"));
            orderWideListDto.setUserId(rs.getInt("user_id"));
            orderWideListDto.setOperationTypeEnum(OperationType.convert(rs.getInt("operation_type_id")));
            orderWideListDto.setExExchangeRate(BigDecimalProcessing.formatLocale(rs.getBigDecimal("exrate"), locale, 2));
            orderWideListDto.setAmountBase(BigDecimalProcessing.formatLocale(rs.getBigDecimal("amount_base"), locale, 2));
            orderWideListDto.setAmountConvert(BigDecimalProcessing.formatLocale(rs.getBigDecimal("amount_convert"), locale, 2));
            orderWideListDto.setComissionId(rs.getInt("commission_id"));
            orderWideListDto.setCommissionFixedAmount(BigDecimalProcessing.formatLocale(rs.getBigDecimal("commission_fixed_amount"), locale, 2));
            BigDecimal amountWithCommission = rs.getBigDecimal("amount_convert");
            orderWideListDto.setCommissionValue(rs.getDouble("commission_value"));
            if (orderWideListDto.getOperationTypeEnum() == OperationType.SELL) {
                amountWithCommission = BigDecimalProcessing.doAction(amountWithCommission, rs.getBigDecimal("commission_fixed_amount"), ActionType.SUBTRACT);
            } else if (orderWideListDto.getOperationTypeEnum() == OperationType.BUY) {
                amountWithCommission = BigDecimalProcessing.doAction(amountWithCommission, rs.getBigDecimal("commission_fixed_amount"), ActionType.ADD);
            }
            orderWideListDto.setAmountWithCommission(BigDecimalProcessing.formatLocale(amountWithCommission, locale, 2));
            orderWideListDto.setUserAcceptorId(rs.getInt("user_acceptor_id"));
            orderWideListDto.setDateCreation(rs.getTimestamp("date_creation") == null ? null : rs.getTimestamp("date_creation").toLocalDateTime());
            orderWideListDto.setDateAcception(rs.getTimestamp("date_acception") == null ? null : rs.getTimestamp("date_acception").toLocalDateTime());
            orderWideListDto.setStatus(OrderStatus.convert(rs.getInt("status_id")));
            orderWideListDto.setDateStatusModification(rs.getTimestamp("status_modification_date") == null ? null : rs.getTimestamp("status_modification_date").toLocalDateTime());
            orderWideListDto.setCurrencyPairId(rs.getInt("currency_pair_id"));
            orderWideListDto.setCurrencyPairName(rs.getString("currency_pair_name"));
            orderWideListDto.setOrderBaseType(OrderBaseType.valueOf(rs.getString("base_type")));
            orderWideListDto.setOperationType(String.join(" ", orderWideListDto.getOperationTypeEnum().name(), orderWideListDto.getOrderBaseType().name()));
            return orderWideListDto;
        });
    }

    @Override
    public Integer getMyOrdersWithStateCount(int userId, CurrencyPair currencyPair, OrderStatus status, String scope, Integer offset, Integer limit, Locale locale) {
        String userFilterClause;
        String currencyPairClauseJoin = currencyPair == null ? "" : "  JOIN CURRENCY_PAIR ON (CURRENCY_PAIR.id = EXORDERS.currency_pair_id) ";
        String currencyPairClauseWhere = currencyPair == null ? "" : "    AND EXORDERS.currency_pair_id = :currencyPairId ";

        switch (scope) {
            case "ALL":
                userFilterClause = " AND (EXORDERS.user_id = :user_id OR EXORDERS.user_acceptor_id = :user_id) ";
                break;
            case "ACCEPTED":
                userFilterClause = " AND EXORDERS.user_acceptor_id = :user_id ";
                break;
            default:
                userFilterClause = " AND EXORDERS.user_id = :user_id ";
                break;
        }

        List<Integer> operationTypesIds = Arrays.asList(3, 4);

        String sql = "SELECT COUNT(*) " +
                "  FROM EXORDERS " +
                currencyPairClauseJoin +
                "  WHERE (status_id = :statusId) " +
                "    AND (operation_type_id IN (:operation_type_id)) " +
                currencyPairClauseWhere +
                userFilterClause;
        Map<String, Object> namedParameters = new HashMap<>();
        namedParameters.put("user_id", userId);
        namedParameters.put("statusId", status.getStatus());
        namedParameters.put("operation_type_id", operationTypesIds);
        if (currencyPair != null) {
            namedParameters.put("currencyPairId", currencyPair.getId());
        }
        return slaveJdbcTemplate.queryForObject(sql, namedParameters, Integer.TYPE);
    }

    @Override
    public OrderCreateDto getMyOrderById(int orderId) {
        String sql = "SELECT EXORDERS.id as order_id, EXORDERS.user_id, EXORDERS.status_id, EXORDERS.operation_type_id,  " +
                "  EXORDERS.exrate, EXORDERS.amount_base, EXORDERS.amount_convert, EXORDERS.commission_fixed_amount, " +
                "  CURRENCY_PAIR.id AS currency_pair_id, CURRENCY_PAIR.name AS currency_pair_name, EXORDERS.base_type  " +
                "  FROM EXORDERS " +
                "  LEFT JOIN CURRENCY_PAIR ON (CURRENCY_PAIR.id = EXORDERS.currency_pair_id) " +
                "  WHERE (EXORDERS.id = :order_id)";
        Map<String, Object> namedParameters = new HashMap<>();
        namedParameters.put("order_id", orderId);
        try {
            return namedParameterJdbcTemplate.queryForObject(sql, namedParameters, new RowMapper<OrderCreateDto>() {
                @Override
                public OrderCreateDto mapRow(ResultSet rs, int rowNum) throws SQLException {
                    OrderCreateDto orderCreateDto = new OrderCreateDto();
                    orderCreateDto.setOrderId(rs.getInt("order_id"));
                    orderCreateDto.setUserId(rs.getInt("user_id"));
                    orderCreateDto.setOperationType(OperationType.convert(rs.getInt("operation_type_id")));
                    orderCreateDto.setStatus(OrderStatus.convert(rs.getInt("status_id")));
                    orderCreateDto.setExchangeRate(rs.getBigDecimal("exrate"));
                    CurrencyPair currencyPair = new CurrencyPair();
                    currencyPair.setId(rs.getInt("currency_pair_id"));
                    currencyPair.setName(rs.getString("currency_pair_name"));
                    orderCreateDto.setCurrencyPair(currencyPair);
                    orderCreateDto.setAmount(rs.getBigDecimal("amount_base"));
                    orderCreateDto.setTotal(rs.getBigDecimal("amount_convert"));
                    orderCreateDto.setComission(rs.getBigDecimal("commission_fixed_amount"));
                    orderCreateDto.setOrderBaseType(OrderBaseType.valueOf(rs.getString("base_type")));
                    if (orderCreateDto.getOperationType() == OperationType.SELL) {
                        orderCreateDto.setTotalWithComission(BigDecimalProcessing.doAction(orderCreateDto.getTotal(), orderCreateDto.getComission(), ActionType.SUBTRACT));
                    } else {
                        orderCreateDto.setTotalWithComission(BigDecimalProcessing.doAction(orderCreateDto.getTotal(), orderCreateDto.getComission(), ActionType.ADD));
                    }
                    return orderCreateDto;
                }
            });
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    @Override
    public WalletsAndCommissionsForOrderCreationDto getWalletAndCommission(String email, Currency currency,
                                                                           OperationType operationType, UserRole userRole) {
        String sql = "SELECT USER.id AS user_id, WALLET.id AS wallet_id, WALLET.active_balance, COMM.id AS commission_id, COMM.value AS commission_value" +
                "  FROM USER " +
                "    LEFT JOIN WALLET ON (WALLET.user_id=USER.id) AND (WALLET.currency_id = :currency_id) " +
                "    LEFT JOIN ((SELECT COMMISSION.id, COMMISSION.value " +
                "           FROM COMMISSION " +
                "           WHERE COMMISSION.operation_type=:operation_type_id AND COMMISSION.user_role = :user_role ORDER BY COMMISSION.date " +
                "           DESC LIMIT 1) AS COMM) ON (1=1) " +
                "  WHERE USER.email = :email";
        Map<String, Object> namedParameters = new HashMap<>();
        namedParameters.put("email", email);
        namedParameters.put("operation_type_id", operationType.getType());
        namedParameters.put("currency_id", currency.getId());
        namedParameters.put("user_role", userRole.getRole());
        try {
            return namedParameterJdbcTemplate.queryForObject(sql, namedParameters, new RowMapper<WalletsAndCommissionsForOrderCreationDto>() {
                @Override
                public WalletsAndCommissionsForOrderCreationDto mapRow(ResultSet rs, int rowNum) throws SQLException {
                    WalletsAndCommissionsForOrderCreationDto walletsAndCommissionsForOrderCreationDto = new WalletsAndCommissionsForOrderCreationDto();
                    walletsAndCommissionsForOrderCreationDto.setUserId(rs.getInt("user_id"));
                    walletsAndCommissionsForOrderCreationDto.setSpendWalletId(rs.getInt("wallet_id"));
                    walletsAndCommissionsForOrderCreationDto.setSpendWalletActiveBalance(rs.getBigDecimal("active_balance"));
                    walletsAndCommissionsForOrderCreationDto.setCommissionId(rs.getInt("commission_id"));
                    walletsAndCommissionsForOrderCreationDto.setCommissionValue(rs.getBigDecimal("commission_value"));
                    return walletsAndCommissionsForOrderCreationDto;
                }
            });
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    @Override
    public boolean lockOrdersListForAcception(List<Integer> ordersList) {
        //TODO Why cycle?? not WHERE id IN (...) ?

        for (Integer orderId : ordersList) {
            String sql = "SELECT id " +
                    "  FROM EXORDERS " +
                    "  WHERE id = :order_id " +
                    "  FOR UPDATE ";
            Map<String, Object> namedParameters = new HashMap<>();
            namedParameters.put("order_id", orderId);
            try {
                namedParameterJdbcTemplate.queryForObject(sql, namedParameters, Integer.class);
            } catch (EmptyResultDataAccessException e) {
                return false;
            }
        }
        return true;
    }


    @Override
    public PagingData<List<OrderBasicInfoDto>> searchOrders(AdminOrderFilterData adminOrderFilterData, DataTableParams dataTableParams, Locale locale) {
        String sqlSelect = " SELECT  " +
                "     EXORDERS.id, EXORDERS.date_creation, EXORDERS.status_id AS status, EXORDERS.base_type, " +
                "     CURRENCY_PAIR.name as currency_pair_name,  " +
                "     UPPER(ORDER_OPERATION.name) AS order_type_name,  " +
                "     EXORDERS.exrate, EXORDERS.amount_base, " +
                "     CREATOR.email AS order_creator_email, " +
                "     CREATOR.roleid AS role  ";
        String sqlFrom = "FROM EXORDERS " +
                "      JOIN OPERATION_TYPE AS ORDER_OPERATION ON (ORDER_OPERATION.id = EXORDERS.operation_type_id) " +
                "      JOIN CURRENCY_PAIR ON (CURRENCY_PAIR.id = EXORDERS.currency_pair_id) " +
                "      JOIN USER CREATOR ON (CREATOR.id = EXORDERS.user_id) ";
        String sqlSelectCount = "SELECT COUNT(*) ";
        String limitAndOffset = dataTableParams.getLimitAndOffsetClause();
        String orderBy = dataTableParams.getOrderByClause();
        Map<String, Object> namedParameters = new HashMap<>();
        namedParameters.put("offset", dataTableParams.getStart());
        namedParameters.put("limit", dataTableParams.getLength());
        namedParameters.putAll(adminOrderFilterData.getNamedParams());
        String criteria = adminOrderFilterData.getSQLFilterClause();
        String whereClause = StringUtils.isNotEmpty(criteria) ? "WHERE " + criteria : "";
        String selectQuery = String.join(" ", sqlSelect, sqlFrom, whereClause, orderBy, limitAndOffset);
        String selectCountQuery = String.join(" ", sqlSelectCount, sqlFrom, whereClause);
        LOGGER.debug(selectQuery);
        LOGGER.debug(selectCountQuery);

        PagingData<List<OrderBasicInfoDto>> result = new PagingData<>();

        List<OrderBasicInfoDto> infoDtoList = slaveJdbcTemplate.query(selectQuery, namedParameters, (rs, rowNum) -> {
            OrderBasicInfoDto infoDto = new OrderBasicInfoDto();
            OrderBaseType baseType = OrderBaseType.convert(rs.getString("base_type"));
            infoDto.setId(rs.getInt("id"));
            infoDto.setDateCreation(rs.getTimestamp("date_creation").toLocalDateTime());
            infoDto.setCurrencyPairName(rs.getString("currency_pair_name"));
            infoDto.setOrderTypeName(rs.getString("order_type_name").concat(" ").concat(baseType.name()));
            infoDto.setExrate(BigDecimalProcessing.formatLocale(rs.getBigDecimal("exrate"), locale, 2));
            infoDto.setAmountBase(BigDecimalProcessing.formatLocale(rs.getBigDecimal("amount_base"), locale, 2));
            infoDto.setOrderCreatorEmail(rs.getString("order_creator_email"));
            infoDto.setStatusId(rs.getInt("status"));
            infoDto.setStatus(OrderStatus.convert(rs.getInt("status")).toString());
            infoDto.setRole(UserRole.convert(rs.getInt("role")).name());
            return infoDto;

        });
        int total = namedParameterJdbcTemplate.queryForObject(selectCountQuery, namedParameters, Integer.class);
        result.setData(infoDtoList);
        result.setTotal(total);
        result.setFiltered(total);
        return result;
    }

    @Override
    public List<ExOrder> selectTopOrders(Integer currencyPairId, BigDecimal exrate,
                                         OperationType orderType, boolean sameRoleOnly, Integer userAcceptorRoleId, OrderBaseType orderBaseType) {
        String sortDirection = "";
        String exrateClause = "";
        if (orderType == OperationType.BUY) {
            sortDirection = "DESC";
            exrateClause = "AND EO.exrate >= :exrate ";
        } else if (orderType == OperationType.SELL) {
            sortDirection = "ASC";
            exrateClause = "AND EO.exrate <= :exrate ";
        }
        String roleJoinClause = sameRoleOnly ? " JOIN USER U ON EO.user_id = U.id AND U.roleid = :acceptor_role_id " :
                "JOIN USER U ON EO.user_id = U.id AND U.roleid IN (SELECT user_role_id FROM USER_ROLE_SETTINGS " +
                        "WHERE user_role_id = :acceptor_role_id OR order_acception_same_role_only = 0)";
        String sqlSetVar = "SET @cumsum := 0";

        /*needs to return several orders with best exrate if their total sum is less than amount in param,
         * or at least one order if base amount is greater than param amount*/
        String sql = "SELECT EO.id, EO.user_id, EO.currency_pair_id, EO.operation_type_id, EO.exrate, EO.amount_base, EO.amount_convert, " +
                "EO.commission_id, EO.commission_fixed_amount, EO.date_creation, EO.status_id, EO.base_type " +
                "FROM EXORDERS EO " + roleJoinClause +
                "WHERE EO.status_id = 2 AND EO.currency_pair_id = :currency_pair_id AND EO.base_type =:order_base_type " +
                "AND EO.operation_type_id = :operation_type_id " + exrateClause +
                " ORDER BY EO.exrate " + sortDirection + ", EO.amount_base ASC ";
        Map<String, Object> params = new HashMap<String, Object>() {{
            put("currency_pair_id", currencyPairId);
            put("exrate", exrate);
            put("operation_type_id", orderType.getType());
            put("acceptor_role_id", userAcceptorRoleId);
            put("order_base_type", orderBaseType.name());
        }};
        namedParameterJdbcTemplate.execute(sqlSetVar, PreparedStatement::execute);

        return namedParameterJdbcTemplate.query(sql, params, (rs, row) -> {
            ExOrder exOrder = new ExOrder();
            exOrder.setId(rs.getInt("id"));
            exOrder.setUserId(rs.getInt("user_id"));
            exOrder.setCurrencyPairId(rs.getInt("currency_pair_id"));
            exOrder.setOperationType(OperationType.convert(rs.getInt("operation_type_id")));
            exOrder.setExRate(rs.getBigDecimal("exrate"));
            exOrder.setAmountBase(rs.getBigDecimal("amount_base"));
            exOrder.setAmountConvert(rs.getBigDecimal("amount_convert"));
            exOrder.setComissionId(rs.getInt("commission_id"));
            exOrder.setCommissionFixedAmount(rs.getBigDecimal("commission_fixed_amount"));
            exOrder.setDateCreation(rs.getTimestamp("date_creation").toLocalDateTime());
            exOrder.setStatus(OrderStatus.convert(rs.getInt("status_id")));
            exOrder.setOrderBaseType(OrderBaseType.valueOf(rs.getString("base_type")));
            return exOrder;
        });
    }

    @Override
    public List<UserSummaryOrdersByCurrencyPairsDto> getUserSummaryOrdersByCurrencyPairList(
            Integer requesterUserId,
            String startDate,
            String endDate,
            List<Integer> roles) {
        String condition = "";
        if (!roles.isEmpty()) {
            condition = " AND USER_ROLE.id IN (:roles) ";
        }

        String sql = "SELECT (select name from OPERATION_TYPE where id = EXORDERS.operation_type_id) as operation, date_acception, " +
                "  (select email from USER where id = EXORDERS.user_id) as user_owner,  " +
                "  (select nickname from USER where id = EXORDERS.user_id) as user_owner_nickname,  " +
                "  (select email from USER where id = EXORDERS.user_acceptor_id) as user_acceptor,  " +
                "  (select nickname from USER where id = EXORDERS.user_acceptor_id) as user_acceptor_nickname,  " +
                "  (select name from CURRENCY_PAIR where id = EXORDERS.currency_pair_id) as currency_pair, amount_base, amount_convert, exrate  " +
                "  from EXORDERS join USER on(USER.id=EXORDERS.user_id) join USER_ROLE on(USER_ROLE.id = USER.roleid)  " +
                "    WHERE status_id = 3     " +
                condition +
                "  AND (operation_type_id IN (3,4))   " +
                "  AND  (EXORDERS.date_acception BETWEEN STR_TO_DATE(:start_date, '%Y-%m-%d %H:%i:%s')  " +
                "  AND STR_TO_DATE(:end_date, '%Y-%m-%d %H:%i:%s')) " +
                "  AND EXISTS (SELECT * " +
                "                  FROM CURRENCY_PAIR CP " +
                "                  JOIN USER_CURRENCY_INVOICE_OPERATION_PERMISSION IOP1  ON (IOP1.user_id = :requester_user_id) AND (IOP1.currency_id = CP.currency1_id) " +
                "                  JOIN USER_CURRENCY_INVOICE_OPERATION_PERMISSION IOP2  ON (IOP2.user_id = :requester_user_id) AND (IOP2.currency_id = CP.currency2_id) " +
                "                  WHERE (CP.id=EXORDERS.currency_pair_id))" +
                "  ORDER BY date_acception, date_creation";
        Map<String, Object> namedParameters = new HashMap<>();
        namedParameters.put("start_date", startDate);
        namedParameters.put("end_date", endDate);
        namedParameters.put("roles", roles);
        namedParameters.put("requester_user_id", requesterUserId);

        ArrayList<UserSummaryOrdersByCurrencyPairsDto> result = (ArrayList<UserSummaryOrdersByCurrencyPairsDto>) slaveJdbcTemplate.query(sql, namedParameters, new BeanPropertyRowMapper<UserSummaryOrdersByCurrencyPairsDto>() {
            @Override
            public UserSummaryOrdersByCurrencyPairsDto mapRow(ResultSet rs, int rowNumber) throws SQLException {
                UserSummaryOrdersByCurrencyPairsDto userSummaryOrdersByCurrencyPairsDto = new UserSummaryOrdersByCurrencyPairsDto();
                userSummaryOrdersByCurrencyPairsDto.setOperationType(rs.getString("operation"));
                userSummaryOrdersByCurrencyPairsDto.setDate(rs.getTimestamp("date_acception").toLocalDateTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
                userSummaryOrdersByCurrencyPairsDto.setOwnerEmail(rs.getString("user_owner"));
                userSummaryOrdersByCurrencyPairsDto.setOwnerNickname(rs.getString("user_owner_nickname"));
                userSummaryOrdersByCurrencyPairsDto.setAcceptorEmail(rs.getString("user_acceptor"));
                userSummaryOrdersByCurrencyPairsDto.setAcceptorNickname(rs.getString("user_acceptor_nickname"));
                userSummaryOrdersByCurrencyPairsDto.setCurrencyPair(rs.getString("currency_pair"));
                userSummaryOrdersByCurrencyPairsDto.setAmountBase(rs.getBigDecimal("amount_base"));
                userSummaryOrdersByCurrencyPairsDto.setAmountConvert(rs.getBigDecimal("amount_convert"));
                userSummaryOrdersByCurrencyPairsDto.setExrate(rs.getBigDecimal("exrate"));
                return userSummaryOrdersByCurrencyPairsDto;
            }
        });
        return result;
    }

    @Override
    public List<CurrencyPairTurnoverReportDto> getCurrencyPairTurnoverForPeriod(LocalDateTime startTime, LocalDateTime endTime, List<Integer> userRoleIdList) {
        String sql = "SELECT CP.name AS currency_pair_name, CR.name as currency_ac_name, OT.id AS operation_type_id, COUNT(EO.id) AS quantity, " +
                //wolper 19.04.18
                // MIN is used for performance reason
                // as an alternative to additional "group by CP.ID"
                " MIN(CP.id) " +
                "AS currency__pair_id, SUM(EO.amount_base) AS amount_base, SUM(EO.amount_convert) AS amount_convert " +
                "FROM EXORDERS EO " +
                "  JOIN CURRENCY_PAIR CP ON EO.currency_pair_id = CP.id " +
                "  JOIN CURRENCY CR ON CP.currency2_id = CR.id " +
                "  JOIN OPERATION_TYPE OT ON EO.operation_type_id = OT.id " +
                "  JOIN USER U ON EO.user_id = U.id AND U.roleid IN (:user_roles) " +
                "  WHERE EO.status_id = 3 AND EO.date_acception BETWEEN STR_TO_DATE(:start_time, '%Y-%m-%d %H:%i:%s') " +
                "  AND STR_TO_DATE(:end_time, '%Y-%m-%d %H:%i:%s')" +
                "  GROUP BY CP.name, OT.id, CR.name ORDER BY CP.name ASC, OT.id ASC";
        Map<String, Object> params = new HashMap<>();
        params.put("start_time", Timestamp.valueOf(startTime));
        params.put("end_time", Timestamp.valueOf(endTime));
        params.put("user_roles", userRoleIdList);

        return slaveJdbcTemplate.query(sql, params, (rs, row) -> {
            CurrencyPairTurnoverReportDto dto = new CurrencyPairTurnoverReportDto();
            dto.setOrderNum(row + 1);
            dto.setCurrencyPairName(rs.getString("currency_pair_name"));
            dto.setCurrencyAccountingName(rs.getString("currency_ac_name"));
            dto.setOperationType(OperationType.convert(rs.getInt("operation_type_id")));
            dto.setAmountBase(rs.getBigDecimal("amount_base"));
            dto.setAmountConvert(rs.getBigDecimal("amount_convert"));
            dto.setQuantity(rs.getInt("quantity"));
            //wolper 19.04.2018
            //currency id added
            dto.setPairId(rs.getInt("currency__pair_id"));
            return dto;
        });
    }


    /*maybe add index
     * CREATE INDEX exorders__status_date_accept ON EXORDERS (status_id, date_acception);
     * */

    @Override
    public List<OrdersCommissionSummaryDto> getOrderCommissionsByPairsForPeriod(LocalDateTime startTime, LocalDateTime endTime, List<Integer> userRoleIdList) {
        String sql = "SELECT CP.name AS currency_pair_name, CR.name as currency_ac_name, " +
                //wolper 19.04.18
                // MIN is used for performance reason
                // as an alternative to additional "group by CUR.ID"
                " MIN(CP.ID)" +
                " AS currency_pair_id, OT.id AS operation_type_id, " +
                "       SUM(EO.amount_base) AS amount_base, SUM(EO.amount_convert) AS amount_convert, " +
                "  SUM((SELECT SUM(commission_amount) FROM TRANSACTION WHERE source_type = 'ORDER' AND source_id = EO.id AND operation_type_id != 5 )) " +
                "    AS commission " +
                "FROM EXORDERS EO " +
                "  JOIN CURRENCY_PAIR CP ON EO.currency_pair_id = CP.id " +
                "  JOIN CURRENCY CR ON CP.currency2_id = CR.id " +
                "  JOIN OPERATION_TYPE OT ON EO.operation_type_id = OT.id " +
                "  JOIN USER U ON EO.user_id = U.id AND U.roleid IN (:user_roles) " +
                "WHERE EO.status_id = 3 AND EO.date_acception BETWEEN STR_TO_DATE(:start_time, '%Y-%m-%d %H:%i:%s') " +
                "AND STR_TO_DATE(:end_time, '%Y-%m-%d %H:%i:%s') " +
                "GROUP BY CP.name, OT.id, CR.name ORDER BY CP.name ASC, OT.id ASC";
        Map<String, Object> params = new HashMap<>();
        params.put("start_time", Timestamp.valueOf(startTime));
        params.put("end_time", Timestamp.valueOf(endTime));
        params.put("user_roles", userRoleIdList);

        return slaveJdbcTemplate.query(sql, params, (rs, row) -> {
            OrdersCommissionSummaryDto dto = new OrdersCommissionSummaryDto();
            dto.setOrderNum(row + 1);
            dto.setCurrencyPairName(rs.getString("currency_pair_name"));
            dto.setCurrencyAccountingName(rs.getString("currency_ac_name"));
            dto.setOperationType(OperationType.convert(rs.getInt("operation_type_id")));
            dto.setAmountBase(rs.getBigDecimal("amount_base"));
            dto.setAmountConvert(rs.getBigDecimal("amount_convert"));
            dto.setCommissionAmount(rs.getBigDecimal("commission"));
            //wolper 19.04.2018
            //added currency id
            dto.setPairId(rs.getInt("currency_pair_id"));
            return dto;
        });
    }

    @Override
    public OrderRoleInfoForDelete getOrderRoleInfo(int orderId) {
        String sql = "SELECT EO.status_id, CREATOR.roleid AS creator_role, ACCEPTOR.roleid AS acceptor_role, COUNT(TX.id) AS tx_count from EXORDERS EO " +
                "  JOIN USER CREATOR ON EO.user_id = CREATOR.id " +
                "  LEFT JOIN USER ACCEPTOR ON EO.user_acceptor_id = ACCEPTOR.id " +
                // join on source type and source id to use index
                "  LEFT JOIN TRANSACTION TX ON TX.source_type = 'ORDER' AND TX.source_id = EO.id " +
                "WHERE EO.id = :order_id;";
        return namedParameterJdbcTemplate.queryForObject(sql, Collections.singletonMap("order_id", orderId), (rs, rowNum) -> {
            Integer statusId = getInteger(rs, "status_id");
            Integer creatorRoleId = getInteger(rs, "creator_role");
            Integer acceptorRoleId = getInteger(rs, "acceptor_role");
            OrderStatus status = statusId == null ? null : OrderStatus.convert(statusId);
            UserRole creatorRole = creatorRoleId == null ? null : UserRole.convert(creatorRoleId);
            UserRole acceptorRole = acceptorRoleId == null ? null : UserRole.convert(acceptorRoleId);
            int txCount = rs.getInt("tx_count");
            return new OrderRoleInfoForDelete(status, creatorRole, acceptorRole, txCount);
        });
    }

    private Integer getInteger(ResultSet rs, String fieldName) throws SQLException {
        Integer result = rs.getInt(fieldName);
        if (rs.wasNull()) {
            result = null;
        }
        return result;
    }
    //wolper 24.04.18

    //query to get the last rates to exchange to USD
    @Override
    public List<RatesUSDForReportDto> getRatesToUSDForReport() {

        String sqlBtc = "SELECT EX.exrate AS exrate" +
                " FROM EXORDERS EX" +
                "INNER JOIN" +
                "(SELECT currency_pair_id, max(date_acception) max_date_acception FROM EXORDERS group by currency_pair_id) EX_LAST" +
                "ON EX.currency_pair_id = EX_LAST.currency_pair_id" +
                "AND EX.date_acception = EX_LAST.max_date_acception" +
                "JOIN CURRENCY_PAIR CP ON (CP.id = EX.currency_pair_id)" +
                "AND (CP.name LIKE '%BTC/USD')" +
                "WHERE  status_id = 3 group by EX.currency_pair_id, EX.exrate limit 1;";

        int btc;
        try {
            btc = slaveJdbcTemplate.queryForObject(sqlBtc, new HashMap<>(), Integer.class);
        } catch (EmptyResultDataAccessException e) {
            btc = 0;
        }

        String sqlEtc =
                "SELECT EX.exrate AS exrate" +
                        " FROM EXORDERS EX" +
                        "INNER JOIN" +
                        "(SELECT currency_pair_id, max(date_acception) max_date_acception FROM EXORDERS group by currency_pair_id) EX_LAST" +
                        "ON EX.currency_pair_id = EX_LAST.currency_pair_id" +
                        "AND EX.date_acception = EX_LAST.max_date_acception" +
                        "JOIN CURRENCY_PAIR CP ON (CP.id = EX.currency_pair_id)" +
                        "AND (CP.name LIKE '%ETH/USD')" +
                        "WHERE  status_id = 3 group by EX.currency_pair_id, EX.exrate limit 1;";

        int eth;
        try {
            eth = slaveJdbcTemplate.queryForObject(sqlEtc, new HashMap<>(), Integer.class);
        } catch (EmptyResultDataAccessException e) {
            eth = 0;
        }

        Map<String, Object> params = new HashMap<>();
        params.put("btc", btc);
        params.put("eth", eth);

        String sql =
                " select distinct CWE.currency_id as id, CR.name,  IFNULL(rate, rate_usd_additional) AS rate from " +
                        "(select id, " +
                        "avg(case when name LIKE '%/BTC' then exrate*:btc" +
                        "when name LIKE '%/ETH' then exrate*:eth " +
                        "when name LIKE '%/USD' then exrate end) as rate" +
                        "" +
                        "from (SELECT CP.currency1_id as id, CP.id AS cp_id, CP.name AS name, EX_LAST.max_date_acception AS date, EX.exrate AS exrate " +
                        " FROM EXORDERS EX" +
                        "INNER JOIN" +
                        "(SELECT currency_pair_id, max(date_acception) max_date_acception FROM EXORDERS group by currency_pair_id) EX_LAST" +
                        "ON EX.currency_pair_id = EX_LAST.currency_pair_id" +
                        "AND EX.date_acception = EX_LAST.max_date_acception" +
                        "JOIN CURRENCY_PAIR CP ON (CP.id = EX.currency_pair_id)" +
                        "AND (CP.name LIKE '%/USD' OR CP.name LIKE '%/BTC' OR CP.name LIKE '%/ETH')" +
                        "WHERE  status_id = 3 group by EX.currency_pair_id, EX.exrate) as RATES group by id) as INNER_QUERY" +
                        "right join COMPANY_WALLET_EXTERNAL CWE on (INNER_QUERY.id = CWE.currency_id)" +
                        "join CURRENCY CR on (CWE.currency_id = CR.id);";

        return slaveJdbcTemplate.query(sql, params, (rs, row) -> {
            RatesUSDForReportDto dto = new RatesUSDForReportDto();
            dto.setId(rs.getInt("id"));
            dto.setCurrencyName(rs.getString("name"));
            dto.setRate(rs.getBigDecimal("rate"));
            return dto;
        });
    }

    @Override
    public List<OrderBookItem> getOrderBookItemsForType(Integer currencyPairId, OrderType orderType) {
        String orderDirection = orderType == OrderType.BUY ? " DESC " : " ASC ";
        String sql = "SELECT amount_base, exrate FROM EXORDERS WHERE currency_pair_id = :currency_pair_id " +
                "AND status_id = :status_id AND operation_type_id = :operation_type_id " +
                "ORDER BY exrate " + orderDirection;
        Map<String, Object> params = new HashMap<>();
        params.put("currency_pair_id", currencyPairId);
        params.put("status_id", OrderStatus.OPENED.getStatus());
        params.put("operation_type_id", orderType.getOperationType().type);

        return slaveJdbcTemplate.query(sql, params, (rs, row) -> {
            OrderBookItem item = new OrderBookItem();
            item.setOrderType(orderType);
            item.setAmount(rs.getBigDecimal("amount_base"));
            item.setRate(rs.getBigDecimal("exrate"));
            return item;
        });
    }

    @Override
    public List<OrderBookItem> getOrderBookItems(Integer currencyPairId) {
        String sql = "SELECT operation_type_id, amount_base, exrate FROM EXORDERS WHERE currency_pair_id = :currency_pair_id " +
                "AND status_id = :status_id ";
        Map<String, Object> params = new HashMap<>();
        params.put("currency_pair_id", currencyPairId);
        params.put("status_id", OrderStatus.OPENED.getStatus());
        return slaveJdbcTemplate.query(sql, params, (rs, row) -> {
            OrderBookItem item = new OrderBookItem();
            item.setOrderType(OrderType.fromOperationType(OperationType.convert(rs.getInt("operation_type_id"))));
            item.setAmount(rs.getBigDecimal("amount_base"));
            item.setRate(rs.getBigDecimal("exrate"));
            return item;
        });
    }

    @Override
    public List<OpenOrderDto> getOpenOrders(Integer currencyPairId, OrderType orderType) {
        String orderByDirection = orderType == OrderType.SELL ? " ASC " : " DESC ";
        String orderBySql = " ORDER BY exrate " + orderByDirection;
        String sql = "SELECT id, operation_type_id, amount_base, exrate FROM EXORDERS " +
                "WHERE currency_pair_id = :currency_pair_id " +
                "AND status_id = :status_id AND operation_type_id = :operation_type_id " + orderBySql;
        Map<String, Object> params = new HashMap<>();
        params.put("currency_pair_id", currencyPairId);
        params.put("status_id", OrderStatus.OPENED.getStatus());
        params.put("operation_type_id", orderType.getOperationType().type);
        return slaveJdbcTemplate.query(sql, params, (rs, row) -> {
            OpenOrderDto item = new OpenOrderDto();
            item.setId(rs.getInt("id"));
            item.setOrderType(OrderType.fromOperationType(OperationType.convert(rs.getInt("operation_type_id"))).name());
            item.setAmount(rs.getBigDecimal("amount_base"));
            item.setPrice(rs.getBigDecimal("exrate"));
            return item;
        });
    }

    @Override
    public List<TradeHistoryDto> getTradeHistory(Integer currencyPairId,
                                                 LocalDateTime fromDate,
                                                 LocalDateTime toDate,
                                                 Integer limit) {
        String limitSql = nonNull(limit) ? " LIMIT :limit" : StringUtils.EMPTY;

        String sql = "SELECT o.id as order_id, " +
                "o.date_creation as created, " +
                "o.date_acception as accepted, " +
                "o.amount_base as amount, " +
                "o.exrate as price, " +
                "o.amount_convert as sum, " +
                "c.value as commission, " +
                "o.operation_type_id" +
                " FROM EXORDERS o" +
                " JOIN COMMISSION c on o.commission_id = c.id" +
                " WHERE o.currency_pair_id=:currency_pair_id AND o.status_id=:status_id" +
                " AND o.date_acception BETWEEN :start_date AND :end_date" +
                " ORDER BY o.date_acception ASC"
                + limitSql;

        Map<String, Object> params = new HashMap<>();
        params.put("status_id", CLOSED.getStatus());
        params.put("currency_pair_id", currencyPairId);
        params.put("start_date", fromDate);
        params.put("end_date", toDate);
        params.put("limit", limit);

        return slaveJdbcTemplate.query(sql, params, (rs, row) -> {
            TradeHistoryDto tradeHistoryDto = new TradeHistoryDto();
            tradeHistoryDto.setOrderId(rs.getInt("order_id"));
            tradeHistoryDto.setDateCreation(rs.getTimestamp("created").toLocalDateTime());
            tradeHistoryDto.setDateAcceptance(rs.getTimestamp("accepted").toLocalDateTime());
            tradeHistoryDto.setAmount(rs.getBigDecimal("amount"));
            tradeHistoryDto.setPrice(rs.getBigDecimal("price"));
            tradeHistoryDto.setTotal(rs.getBigDecimal("sum"));
            tradeHistoryDto.setCommission(rs.getBigDecimal("commission"));
            tradeHistoryDto.setOrderType(OrderType.fromOperationType(OperationType.convert(rs.getInt("operation_type_id"))));
            return tradeHistoryDto;
        });
    }

    @Override
    public List<UserOrdersDto> getUserOpenOrders(Integer userId, @Nullable Integer currencyPairId) {
        String currencyPairSql = currencyPairId == null ? "" : " AND EO.currency_pair_id = :currency_pair_id ";
        String sql = "SELECT EO.id AS order_id, EO.amount_base, EO.exrate, CP.name AS currency_pair_name, EO.operation_type_id, " +
                " EO.date_creation, EO.date_acception FROM EXORDERS EO " +
                " JOIN CURRENCY_PAIR CP ON EO.currency_pair_id = CP.id " +
                " WHERE EO.user_id = :user_id AND EO.status_id = :status_id " + currencyPairSql +
                " ORDER BY EO.date_creation DESC";
        Map<String, Object> params = new HashMap<>();
        params.put("user_id", userId);
        params.put("currency_pair_id", currencyPairId);
        params.put("status_id", OrderStatus.OPENED.getStatus());

        return slaveJdbcTemplate.query(sql, params, userOrdersRowMapper);
    }

    @Override
    public List<UserOrdersDto> getUserOrdersByStatus(Integer userId,
                                                     Integer currencyPairId,
                                                     OrderStatus status,
                                                     int limit,
                                                     int offset) {
        String currencyPairSql = nonNull(currencyPairId) ? " AND EO.currency_pair_id = :currency_pair_id " : StringUtils.EMPTY;
        String limitSql = limit > 0 ? " LIMIT :limit " : StringUtils.EMPTY;
        String offsetSql = (limit > 0 && offset > 0) ? "OFFSET :offset" : StringUtils.EMPTY;

        String sql = "SELECT EO.id AS order_id, EO.amount_base, EO.exrate, CP.name AS currency_pair_name, EO.operation_type_id, " +
                " EO.date_creation, EO.date_acception FROM EXORDERS EO " +
                " JOIN CURRENCY_PAIR CP ON EO.currency_pair_id = CP.id " +
                " WHERE (EO.user_id = :user_id OR EO.user_acceptor_id = :user_id) AND EO.status_id = :status_id " + currencyPairSql +
                " ORDER BY EO.date_creation DESC " + limitSql + offsetSql;

        Map<String, Object> params = new HashMap<>();
        params.put("user_id", userId);
        params.put("currency_pair_id", currencyPairId);
        params.put("status_id", status.getStatus());
        params.put("limit", limit);
        params.put("offset", offset);

        return slaveJdbcTemplate.query(sql, params, userOrdersRowMapper);
    }

    @Override
    public List<UserTradeHistoryDto> getUserTradeHistoryByCurrencyPair(Integer userId,
                                                                       Integer currencyPairId,
                                                                       LocalDateTime fromDate,
                                                                       LocalDateTime toDate,
                                                                       Integer limit) {
        String limitSql = nonNull(limit) ? " LIMIT :limit" : StringUtils.EMPTY;

        String sql = "SELECT o.id as order_id, " +
                "o.user_id as user_id, " +
                "o.date_creation as created, " +
                "o.date_acception as accepted, " +
                "o.amount_base as amount, " +
                "o.exrate as price, " +
                "o.amount_convert as sum, " +
                "c.value as commission, " +
                "o.operation_type_id" +
                " FROM EXORDERS o" +
                " JOIN COMMISSION c on o.commission_id = c.id" +
                " WHERE (o.user_id = :user_id OR o.user_acceptor_id = :user_id) AND o.currency_pair_id = :currency_pair_id" +
                " AND o.status_id = :status_id AND o.date_acception BETWEEN :start_date AND :end_date" +
                " ORDER BY o.date_acception ASC"
                + limitSql;

        Map<String, Object> params = new HashMap<>();
        params.put("status_id", CLOSED.getStatus());
        params.put("user_id", userId);
        params.put("currency_pair_id", currencyPairId);
        params.put("start_date", fromDate);
        params.put("end_date", toDate);
        params.put("limit", limit);

        return slaveJdbcTemplate.query(sql, params, (rs, row) -> {
            UserTradeHistoryDto userTradeHistoryDto = new UserTradeHistoryDto();
            userTradeHistoryDto.setUserId(userId);
            userTradeHistoryDto.setIsMaker(userId == rs.getInt("user_id"));
            userTradeHistoryDto.setOrderId(rs.getInt("order_id"));
            userTradeHistoryDto.setDateCreation(rs.getTimestamp("created").toLocalDateTime());
            userTradeHistoryDto.setDateAcceptance(rs.getTimestamp("accepted").toLocalDateTime());
            userTradeHistoryDto.setAmount(rs.getBigDecimal("amount"));
            userTradeHistoryDto.setPrice(rs.getBigDecimal("price"));
            userTradeHistoryDto.setTotal(rs.getBigDecimal("sum"));
            userTradeHistoryDto.setCommission(rs.getBigDecimal("commission"));
            userTradeHistoryDto.setOrderType(OrderType.fromOperationType(OperationType.convert(rs.getInt("operation_type_id"))));
            return userTradeHistoryDto;
        });
    }

    @Override
    public List<ExOrder> getAllOpenedOrdersByUserId(Integer userId) {
        String sql = "SELECT o.id AS order_id, " +
                "o.currency_pair_id, " +
                "o.operation_type_id, " +
                "o.exrate AS price, " +
                "o.amount_base AS amount, " +
                "o.amount_convert AS sum, " +
                "o.commission_id, " +
                "o.commission_fixed_amount, " +
                "o.date_creation AS created, " +
                "o.status_id, " +
                "o.base_type" +
                " FROM EXORDERS o" +
                " WHERE o.user_id = :user_id AND o.status_id = : status_id";

        Map<String, Object> params = new HashMap<>();
        params.put("user_id", userId);
        params.put("status_id", OrderStatus.OPENED.getStatus());

        return namedParameterJdbcTemplate.query(sql, params, (rs, row) -> {
            ExOrder exOrder = new ExOrder();
            exOrder.setId(rs.getInt("id"));
            exOrder.setUserId(userId);
            exOrder.setCurrencyPairId(rs.getInt("currency_pair_id"));
            exOrder.setOperationType(OperationType.convert(rs.getInt("operation_type_id")));
            exOrder.setExRate(rs.getBigDecimal("price"));
            exOrder.setAmountBase(rs.getBigDecimal("amount"));
            exOrder.setAmountConvert(rs.getBigDecimal("sum"));
            exOrder.setComissionId(rs.getInt("commission_id"));
            exOrder.setCommissionFixedAmount(rs.getBigDecimal("commission_fixed_amount"));
            exOrder.setDateCreation(rs.getTimestamp("created").toLocalDateTime());
            exOrder.setStatus(OrderStatus.convert(rs.getInt("status_id")));
            exOrder.setOrderBaseType(OrderBaseType.valueOf(rs.getString("base_type")));
            return exOrder;
        });
    }

    @Override
    public List<ExOrder> getOpenedOrdersByCurrencyPair(Integer userId, String currencyPair) {
        String sql = "SELECT o.id AS order_id, " +
                "o.currency_pair_id, " +
                "o.operation_type_id, " +
                "o.exrate AS price, " +
                "o.amount_base AS amount, " +
                "o.amount_convert AS sum, " +
                "o.commission_id, " +
                "o.commission_fixed_amount, " +
                "o.date_creation AS created, " +
                "o.status_id, " +
                "o.base_type" +
                " FROM EXORDERS o" +
                " JOIN CURRENCY_PAIR cp on o.currency_pair_id = cp.id" +
                " WHERE o.user_id = :user_id AND cp.name = :currency_pair AND o.status_id = : status_id";

        Map<String, Object> params = new HashMap<>();
        params.put("user_id", userId);
        params.put("currency_pair", currencyPair);
        params.put("status_id", OrderStatus.OPENED.getStatus());

        return namedParameterJdbcTemplate.query(sql, params, (rs, row) -> {
            ExOrder exOrder = new ExOrder();
            exOrder.setId(rs.getInt("id"));
            exOrder.setUserId(userId);
            exOrder.setCurrencyPairId(rs.getInt("currency_pair_id"));
            exOrder.setOperationType(OperationType.convert(rs.getInt("operation_type_id")));
            exOrder.setExRate(rs.getBigDecimal("price"));
            exOrder.setAmountBase(rs.getBigDecimal("amount"));
            exOrder.setAmountConvert(rs.getBigDecimal("sum"));
            exOrder.setComissionId(rs.getInt("commission_id"));
            exOrder.setCommissionFixedAmount(rs.getBigDecimal("commission_fixed_amount"));
            exOrder.setDateCreation(rs.getTimestamp("created").toLocalDateTime());
            exOrder.setStatus(OrderStatus.convert(rs.getInt("status_id")));
            exOrder.setOrderBaseType(OrderBaseType.valueOf(rs.getString("base_type")));
            return exOrder;
        });
    }

    @Override
    public List<TransactionDto> getOrderTransactions(Integer userId, Integer orderId) {
        String sql = "SELECT t.id, " +
                "t.user_wallet_id, " +
                "t.amount, " +
                "t.commission_amount AS commission, " +
                "cur.name AS currency, " +
                "t.datetime AS time, " +
                "t.operation_type_id, " +
                "o.status_id" +
                " FROM TRANSACTION t" +
                " JOIN CURRENCY cur on t.currency_id = cur.id" +
                " JOIN EXORDERS o on o.id = t.source_id" +
                " WHERE (o.user_id = :user_id OR o.user_acceptor_id = :user_id)" +
                " AND t.source_id = :order_id" +
                " AND t.source_type = :source_type" +
                " AND (t.operation_type_id = :operation_type_1 OR t.operation_type_id = :operation_type_2)" +
                " ORDER BY t.id";

        Map<String, Object> params = new HashMap<>();
        params.put("user_id", userId);
        params.put("order_id", orderId);
        params.put("source_type", ORDER.name());
        params.put("operation_type_1", INPUT.getType());
        params.put("operation_type_2", OUTPUT.getType());

        return slaveJdbcTemplate.query(sql, params, (rs, row) -> TransactionDto.builder()
                .transactionId(rs.getInt("id"))
                .walletId(rs.getInt("user_wallet_id"))
                .amount(rs.getBigDecimal("amount"))
                .commission(rs.getBigDecimal("commission"))
                .currency(rs.getString("currency"))
                .time(rs.getTimestamp("time").toLocalDateTime())
                .operationType(OperationType.convert(rs.getInt("operation_type_id")))
                .status(TransactionStatus.convert(rs.getInt("status_id")))
                .build());
    }

    @Override
    public List<StatisticForMarket> getOrderStatisticForNewMarkets() {

        String sql = "SELECT" +
                "  RESULT.currency_pair_name," +
                "  RESULT.market," +
                "  RESULT.currency_pair_id," +
                "  RESULT.last_exrate," +
                "  RESULT.pred_last_exrate," +
                "  RESULT.volume," +
                "  RESULT.type" +
                " FROM" +
                "  ((SELECT" +
                "      CURRENCY_PAIR.name          AS currency_pair_name," +
                "      CURRENCY_PAIR.market        AS market," +
                "      CURRENCY_PAIR.id            AS currency_pair_id," +
                "      CURRENCY_PAIR.type                      AS type," +
                "      (SELECT SUM(EX.amount_base)" +
                "       FROM EXORDERS EX" +
                "       WHERE" +
                "         (EX.currency_pair_id = AGRIGATE.currency_pair_id) AND" +
                "         (EX.status_id = AGRIGATE.status_id) AND (EX.date_creation >= NOW() - INTERVAL 24 HOUR)) AS volume," +
                "      (SELECT LASTORDER.exrate" +
                "       FROM EXORDERS LASTORDER" +
                "       WHERE" +
                "         (LASTORDER.currency_pair_id = AGRIGATE.currency_pair_id) AND" +
                "         (LASTORDER.status_id = AGRIGATE.status_id)" +
                "       ORDER BY LASTORDER.date_acception DESC, LASTORDER.id DESC" +
                "       LIMIT 1)  AS last_exrate," +
                "      (SELECT PRED_LASTORDER.exrate" +
                "       FROM EXORDERS PRED_LASTORDER" +
                "       WHERE" +
                "         (PRED_LASTORDER.currency_pair_id = AGRIGATE.currency_pair_id) AND" +
                "         (PRED_LASTORDER.status_id = AGRIGATE.status_id) AND" +
                "         (PRED_LASTORDER.date_creation >= NOW() - INTERVAL 24 HOUR)" +
                "       ORDER BY PRED_LASTORDER.date_acception ASC, PRED_LASTORDER.id DESC" +
                "       LIMIT 1)  AS pred_last_exrate" +
                "    FROM (" +
                "           SELECT DISTINCT" +
                "             EXORDERS.status_id        AS status_id," +
                "             EXORDERS.currency_pair_id AS currency_pair_id" +
                "           FROM EXORDERS" +
                "           WHERE EXORDERS.status_id = :status_id" +
                "         )" +
                "         AGRIGATE" +
                "      JOIN CURRENCY_PAIR ON (CURRENCY_PAIR.id = AGRIGATE.currency_pair_id) AND (CURRENCY_PAIR.hidden != 1)" +
                "    ORDER BY -CURRENCY_PAIR.pair_order DESC)" +
                "   UNION ALL (" +
                "     SELECT" +
                "       CP.name   AS currency_pair_name," +
                "       CP.market AS market," +
                "       CP.id     AS currency_pair_id," +
                "       CP.type   AS type," +
                "       0         AS volume," +
                "       0         AS last_exrate," +
                "       0         AS pred_last_exrate" +
                "     FROM CURRENCY_PAIR CP" +
                "     WHERE CP.id NOT IN (SELECT DISTINCT EXORDERS.currency_pair_id AS currency_pair_id" +
                "                         FROM EXORDERS" +
                "                         WHERE EXORDERS.status_id = :status_id) AND CP.hidden = 0" +
                "   )) RESULT";

        Map<String, Object> params = new HashMap<>();
        params.put("status_id", 3);

        return namedParameterJdbcTemplate.query(sql, params, (rs, row) -> {
            StatisticForMarket statisticForMarket = new StatisticForMarket();

            statisticForMarket.setCurrencyPairId(rs.getInt("currency_pair_id"));
            statisticForMarket.setCurrencyPairName(rs.getString("currency_pair_name"));
            statisticForMarket.setMarket(rs.getString("market"));
            statisticForMarket.setLastOrderRate(rs.getBigDecimal("last_exrate"));
            statisticForMarket.setPredLastOrderRate(rs.getBigDecimal("pred_last_exrate"));
            if (rs.getObject("volume") != null) {
                statisticForMarket.setVolume(rs.getBigDecimal("volume"));
            } else {
                statisticForMarket.setVolume(BigDecimal.ZERO);
            }
            statisticForMarket.setType(CurrencyPairType.valueOf(rs.getString("type")));
            return statisticForMarket;
        });
    }

    @Override
    public List<OrderListDto> findAllByOrderTypeAndCurrencyId(OrderType orderType, Integer currencyId) {
        String sql = "SELECT id, currency_pair_id, operation_type_id, exrate, amount_base, " +
                " amount_convert, commission_fixed_amount, date_creation, date_acception" +
                "  FROM EXORDERS " +
                "  WHERE status_id = 2 AND operation_type_id = :operationTypeId AND currency_pair_id=:currency_pair_id" +
//                "  AND date_creation >= (DATE_SUB(CURDATE(), INTERVAL 10 DAY))" +
                "  ORDER BY exrate ASC";
        Map<String, Integer> namedParameters = new HashMap<>();
        namedParameters.put("currency_pair_id", currencyId);
        namedParameters.put("operationTypeId", orderType.getOperationType().getType());
        return slaveJdbcTemplate.query(sql, namedParameters, openOrderListDtoRowMapper());
    }

    private RowMapper<OrderListDto> openOrderListDtoRowMapper(){
        return (rs, rowNum) -> {
            OrderListDto order = new OrderListDto();
            order.setId(rs.getInt("id"));
            order.setOrderType(OperationType.convert(rs.getInt("operation_type_id")));
            order.setExrate(rs.getString("exrate"));
            order.setAmountBase(rs.getString("amount_base"));
            order.setCreated(convertTimeStampToLocalDateTime(rs,"date_creation"));
            return order;
        };
    }
}