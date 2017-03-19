package me.exrates.dao.impl;

import me.exrates.dao.CommissionDao;
import me.exrates.dao.OrderDao;
import me.exrates.dao.WalletDao;
import me.exrates.jdbc.OrderRowMapper;
import me.exrates.model.*;
import me.exrates.model.Currency;
import me.exrates.model.dto.*;
import me.exrates.model.dto.dataTable.DataTableParams;
import me.exrates.model.dto.filterData.AdminOrderFilterData;
import me.exrates.model.dto.mobileApiDto.dashboard.CommissionsDto;
import me.exrates.model.dto.onlineTableDto.ExOrderStatisticsShortByPairsDto;
import me.exrates.model.dto.onlineTableDto.OrderAcceptedHistoryDto;
import me.exrates.model.dto.onlineTableDto.OrderListDto;
import me.exrates.model.dto.onlineTableDto.OrderWideListDto;
import me.exrates.model.enums.*;
import me.exrates.model.util.BigDecimalProcessing;
import me.exrates.model.vo.BackDealInterval;
import me.exrates.model.vo.WalletOperationData;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.PreparedStatementCallback;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Repository
public class OrderDaoImpl implements OrderDao {

    private static final Logger LOGGER = LogManager.getLogger(OrderDaoImpl.class);

    @Autowired
    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    @Autowired
    CommissionDao commissionDao;

    @Autowired
    WalletDao walletDao;

    public int createOrder(ExOrder exOrder) {
        String sql = "INSERT INTO EXORDERS" +
                "  (user_id, currency_pair_id, operation_type_id, exrate, amount_base, amount_convert, commission_id, commission_fixed_amount, status_id)" +
                "  VALUES " +
                "  (:user_id, :currency_pair_id, :operation_type_id, :exrate, :amount_base, :amount_convert, :commission_id, :commission_fixed_amount, :status_id)";
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
                .addValue("status_id", OrderStatus.INPROCESS.getStatus());
        int result = namedParameterJdbcTemplate.update(sql, parameters, keyHolder);
        int id = (int) keyHolder.getKey().longValue();
        if (result <= 0) {
            id = 0;
        }
        return id;
    }

    @Override
    public List<OrderListDto> getOrdersSellForCurrencyPair(CurrencyPair currencyPair) {
        String email = null;
        String sql = "SELECT EXORDERS.id, user_id, currency_pair_id, operation_type_id, exrate, amount_base, amount_convert, commission_fixed_amount" +
                "  FROM EXORDERS " +
                (StringUtils.isEmpty(email) ? "" : " JOIN USER ON (USER.id=EXORDERS.user_id)  AND (USER.email != '" + email + "') ") +
                "  WHERE status_id = 2 and operation_type_id= 3 and currency_pair_id=:currency_pair_id" +
                "  ORDER BY exrate ASC";
        Map<String, Object> namedParameters = new HashMap<>();
        namedParameters.put("currency_pair_id", currencyPair.getId());
        return namedParameterJdbcTemplate.query(sql, namedParameters, (rs, row) -> {
            OrderListDto order = new OrderListDto();
            order.setId(rs.getInt("id"));
            order.setUserId(rs.getInt("user_id"));
            order.setOrderType(OperationType.convert(rs.getInt("operation_type_id")));
            order.setExrate(rs.getString("exrate"));
            order.setAmountBase(rs.getString("amount_base"));
            order.setAmountConvert(rs.getString("amount_convert"));
            return order;
        });
    }

    @Override
    public List<OrderListDto> getOrdersBuyForCurrencyPair(CurrencyPair currencyPair) {
        String email = null;
        String sql = "SELECT EXORDERS.id, user_id, currency_pair_id, operation_type_id, exrate, amount_base, amount_convert, commission_fixed_amount" +
                "  FROM EXORDERS " +
                (StringUtils.isEmpty(email) ? "" : " JOIN USER ON (USER.id=EXORDERS.user_id)  AND (USER.email != '" + email + "') ") +
                "  WHERE status_id = 2 and operation_type_id= 4 and currency_pair_id=:currency_pair_id" +
                "  ORDER BY exrate DESC";
        Map<String, String> namedParameters = new HashMap<>();
        namedParameters.put("currency_pair_id", String.valueOf(currencyPair.getId()));
        return namedParameterJdbcTemplate.query(sql, namedParameters, (rs, row) -> {
            OrderListDto order = new OrderListDto();
            order.setId(rs.getInt("id"));
            order.setUserId(rs.getInt("user_id"));
            order.setOrderType(OperationType.convert(rs.getInt("operation_type_id")));
            order.setExrate(rs.getString("exrate"));
            order.setAmountBase(rs.getString("amount_base"));
            order.setAmountConvert(rs.getString("amount_convert"));
            return order;
        });
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
        List<Map<String, Object>> rows = namedParameterJdbcTemplate.query(sql, namedParameters, (rs, row) -> {
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
        String startTimeString = endTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        String startTimeSql = String.format("STR_TO_DATE('%s', '%%Y-%%m-%%d %%H:%%i:%%s')", startTimeString);
        return getCandleChartData(currencyPair, backDealInterval, startTimeSql);
    }
    
    
    private List<CandleChartItemDto> getCandleChartData(CurrencyPair currencyPair, BackDealInterval backDealInterval, String startTimeSql) {
        String s = "{call GET_DATA_FOR_CANDLE(" + startTimeSql + ", " + backDealInterval.intervalValue + ", '" + backDealInterval.intervalType.name() + "', " + currencyPair.getId() + ")}";
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
            return namedParameterJdbcTemplate.queryForObject(sql, namedParameters, new RowMapper<ExOrderStatisticsDto>() {
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
            String sql = "SELECT  " +
                    "   CURRENCY_PAIR.name AS currency_pair_name,       " +
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
                    "       LIMIT 1,1) AS pred_last_exrate " +
                    " FROM ( " +
                    "   SELECT DISTINCT" +
                    "   EXORDERS.status_id AS status_id,  " +
                    "   EXORDERS.currency_pair_id AS currency_pair_id " +
                    "   FROM EXORDERS          " +
                    "   WHERE EXORDERS.status_id = :status_id         " +
                    "   ) " +
                    " AGRIGATE " +
                    " JOIN CURRENCY_PAIR ON (CURRENCY_PAIR.id = AGRIGATE.currency_pair_id) AND (CURRENCY_PAIR.hidden != 1) " +
                    "" +
                    " ORDER BY -CURRENCY_PAIR.pair_order DESC ";
            Map<String, String> namedParameters = new HashMap<>();
            namedParameters.put("status_id", String.valueOf(3));
            return namedParameterJdbcTemplate.query(sql, namedParameters, new RowMapper<ExOrderStatisticsShortByPairsDto>() {
                @Override
                public ExOrderStatisticsShortByPairsDto mapRow(ResultSet rs, int rowNum) throws SQLException {
                    ExOrderStatisticsShortByPairsDto exOrderStatisticsDto = new ExOrderStatisticsShortByPairsDto();
                    exOrderStatisticsDto.setCurrencyPairName(rs.getString("currency_pair_name"));
                    exOrderStatisticsDto.setLastOrderRate(rs.getString("last_exrate"));
                    exOrderStatisticsDto.setPredLastOrderRate(rs.getString("pred_last_exrate"));
                    return exOrderStatisticsDto;
                }
            });
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
        LOGGER.debug(currencyPairName);
        String s = "{call GET_COINMARKETCAP_STATISTICS('" + currencyPairName + "')}";
        LOGGER.debug(s);
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
        LOGGER.debug(result);
        return result;
    }

    @Override
    public OrderInfoDto getOrderInfo(int orderId, Locale locale) {
        String sql =
                " SELECT  " +
                        "     EXORDERS.id, EXORDERS.date_creation, EXORDERS.date_acception,  " +
                        "     ORDER_STATUS.name AS order_status_name,  " +
                        "     CURRENCY_PAIR.name as currency_pair_name,  " +
                        "     UPPER(ORDER_OPERATION.name) AS order_type_name,  " +
                        "     EXORDERS.exrate, EXORDERS.amount_base, EXORDERS.amount_convert, " +
                        "     ORDER_CURRENCY_BASE.name as currency_base_name, ORDER_CURRENCY_CONVERT.name as currency_convert_name, " +
                        "     CREATOR.email AS order_creator_email, " +
                        "     ACCEPTOR.email AS order_acceptor_email, " +
                        "     COUNT(TRANSACTION.id) AS transaction_count,  " +
                        "     SUM(TRANSACTION.commission_amount) AS company_commission " +
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
            return namedParameterJdbcTemplate.queryForObject(sql, mapParameters, new RowMapper<OrderInfoDto>() {
                @Override
                public OrderInfoDto mapRow(ResultSet rs, int rowNum) throws SQLException {
                    OrderInfoDto orderInfoDto = new OrderInfoDto();
                    orderInfoDto.setId(rs.getInt("id"));
                    orderInfoDto.setDateCreation(rs.getTimestamp("date_creation").toLocalDateTime());
                    orderInfoDto.setDateAcception(rs.getTimestamp("date_acception") == null ? null : rs.getTimestamp("date_acception").toLocalDateTime());
                    orderInfoDto.setCurrencyPairName(rs.getString("currency_pair_name"));
                    orderInfoDto.setOrderTypeName(rs.getString("order_type_name"));
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
                    return orderInfoDto;
                }
            });
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
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
            return namedParameterJdbcTemplate.queryForObject(sql, namedParameters, new RowMapper<Integer>() {
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
    public Object deleteOrderByAdmin(int orderId) {
        return deleteOrder(orderId, OrderStatus.DELETED);
    }

    @Override
    public Object deleteOrderForPartialAccept(int orderId) {
        return deleteOrder(orderId, OrderStatus.SPLIT);
    }

    private Object deleteOrder(int orderId, OrderStatus status) {
        List<OrderDetailDto> list = walletDao.getOrderRelatedDataAndBlock(orderId);
        if (list.isEmpty()) {
            return OrderDeleteStatus.NOT_FOUND;
        }
        int processedRows = 1;
        /**/
        OrderStatus orderStatus = list.get(0).getOrderStatus();
        /**/
        String sql = "UPDATE EXORDERS " +
                " SET status_id = :status_id" +
                " WHERE id = :order_id ";
        Map<String, Object> params = new HashMap<>();
        params.put("status_id", status.getStatus());
        params.put("order_id", orderId);
        if (namedParameterJdbcTemplate.update(sql, params) <= 0) {
            return OrderDeleteStatus.ORDER_UPDATE_ERROR;
        }
        /**/
        for (OrderDetailDto orderDetailDto : list) {
            if (orderStatus == OrderStatus.CLOSED) {
                if (orderDetailDto.getCompanyCommission().compareTo(BigDecimal.ZERO) != 0) {
                    sql = "UPDATE COMPANY_WALLET " +
                            " SET commission_balance = commission_balance - :amount" +
                            " WHERE id = :company_wallet_id ";
                    params = new HashMap<>();
                    params.put("amount", orderDetailDto.getCompanyCommission());
                    params.put("company_wallet_id", orderDetailDto.getCompanyWalletId());
                    if (orderDetailDto.getCompanyWalletId() != 0 && namedParameterJdbcTemplate.update(sql, params) <= 0) {
                        return OrderDeleteStatus.COMPANY_WALLET_UPDATE_ERROR;
                    }
                }
                /**/
                WalletOperationData walletOperationData = new WalletOperationData();
                OperationType operationType = null;
                if (orderDetailDto.getTransactionType() == OperationType.OUTPUT) {
                    operationType = OperationType.INPUT;
                } else if (orderDetailDto.getTransactionType() == OperationType.INPUT) {
                    operationType = OperationType.OUTPUT;
                }
                if (operationType != null) {
                    walletOperationData.setOperationType(operationType);
                    walletOperationData.setWalletId(orderDetailDto.getUserWalletId());
                    walletOperationData.setAmount(orderDetailDto.getTransactionAmount());
                    walletOperationData.setBalanceType(WalletOperationData.BalanceType.ACTIVE);
                    Commission commission = commissionDao.getDefaultCommission(OperationType.STORNO);
                    walletOperationData.setCommission(commission);
                    walletOperationData.setCommissionAmount(commission.getValue());
                    walletOperationData.setSourceType(TransactionSourceType.ORDER);
                    walletOperationData.setSourceId(orderId);
                    WalletTransferStatus walletTransferStatus = walletDao.walletBalanceChange(walletOperationData);
                    if (walletTransferStatus != WalletTransferStatus.SUCCESS) {
                        return OrderDeleteStatus.TRANSACTION_CREATE_ERROR;
                    }
                }
                /**/
                sql = "UPDATE TRANSACTION " +
                        " SET status_id = :status_id" +
                        " WHERE id = :transaction_id ";
                params = new HashMap<>();
                params.put("status_id", TransactionStatus.DELETED.getStatus());
                params.put("transaction_id", orderDetailDto.getTransactionId());
                if (namedParameterJdbcTemplate.update(sql, params) <= 0) {
                    return OrderDeleteStatus.TRANSACTION_UPDATE_ERROR;
                }
                /**/
                processedRows++;
            } else if (orderStatus == OrderStatus.OPENED) {
                walletDao.walletInnerTransfer(orderDetailDto.getOrderCreatorReservedWalletId(),
                        orderDetailDto.getOrderCreatorReservedAmount(), TransactionSourceType.ORDER, orderId);
                /**/
                sql = "UPDATE TRANSACTION " +
                        " SET status_id = :status_id" +
                        " WHERE id = :transaction_id ";
                params = new HashMap<>();
                params.put("status_id", TransactionStatus.DELETED.getStatus());
                params.put("transaction_id", orderDetailDto.getTransactionId());
                if (namedParameterJdbcTemplate.update(sql, params) <= 0) {
                    return OrderDeleteStatus.TRANSACTION_UPDATE_ERROR;
                }
            }
        }
        return processedRows;
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
        return namedParameterJdbcTemplate.query(sql, params, (rs, rowNum) -> {
            OrderAcceptedHistoryDto orderAcceptedHistoryDto = new OrderAcceptedHistoryDto();
            orderAcceptedHistoryDto.setOrderId(rs.getInt("id"));
            orderAcceptedHistoryDto.setDateAcceptionTime(rs.getTimestamp("date_acception").toLocalDateTime().toLocalTime().format(DateTimeFormatter.ISO_LOCAL_TIME));
            orderAcceptedHistoryDto.setAcceptionTime(rs.getTimestamp("date_acception"));
            orderAcceptedHistoryDto.setRate(rs.getString("exrate"));
            orderAcceptedHistoryDto.setAmountBase(rs.getString("amount_base"));
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
    public CommissionsDto getAllCommissions(UserRole userRole){
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
            return namedParameterJdbcTemplate.queryForObject(sql, params, (rs, row) -> {
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
    public List<OrderWideListDto> getMyOrdersWithState(String email, CurrencyPair currencyPair, OrderStatus status,
                                                       OperationType operationType,
                                                       String scope, Integer offset, Integer limit, Locale locale) {
        return getMyOrdersWithState(email, currencyPair, Collections.singletonList(status), operationType, scope, offset, limit, locale);
    }

    @Override
    public List<OrderWideListDto> getMyOrdersWithState(String email, CurrencyPair currencyPair, List<OrderStatus> statuses,
                                                       OperationType operationType,
                                                       String scope, Integer offset, Integer limit, Locale locale) {
        String userFilterClause;
        String userJoinClause;
        if(scope == null || scope.isEmpty()) {
            userFilterClause = " AND CREATOR.email = :email ";
            userJoinClause = "  JOIN USER AS CREATOR ON CREATOR.id=EXORDERS.user_id ";
        } else {
            switch (scope) {
                case "ALL":
                    userFilterClause = " AND (CREATOR.email = :email OR ACCEPTOR.email = :email) ";
                    userJoinClause = "  JOIN USER AS CREATOR ON CREATOR.id=EXORDERS.user_id " +
                            "  JOIN USER AS ACCEPTOR ON ACCEPTOR.id=EXORDERS.user_acceptor_id ";
                    break;
                case "ACCEPTED":
                    userFilterClause = " AND ACCEPTOR.email = :email ";
                    userJoinClause = "  JOIN USER AS ACCEPTOR ON ACCEPTOR.id=EXORDERS.user_acceptor_id ";
                    break;
                default:
                    userFilterClause = " AND CREATOR.email = :email ";
                    userJoinClause = "  JOIN USER AS CREATOR ON CREATOR.id=EXORDERS.user_id ";
                    break;
            }
        }

        List<Integer> statusIds = statuses.stream().map(OrderStatus::getStatus).collect(Collectors.toList());
        String orderClause = "  ORDER BY -date_acception ASC, date_creation DESC";
        if (statusIds.size() > 1) {
            orderClause = "  ORDER BY status_modification_date DESC";
        }
        String sql = "SELECT EXORDERS.*, CURRENCY_PAIR.name AS currency_pair_name" +
                "  FROM EXORDERS " +
                userJoinClause +
                "  JOIN CURRENCY_PAIR ON (CURRENCY_PAIR.id = EXORDERS.currency_pair_id) " +
                "  WHERE (status_id IN (:status_ids))" +
                "    AND (operation_type_id = :operation_type_id)" +
                userFilterClause +
                (currencyPair == null ? "" : " AND EXORDERS.currency_pair_id=" + currencyPair.getId()) +
                 orderClause +
                (limit == -1 ? "" : "  LIMIT " + limit + " OFFSET " + offset);
        Map<String, Object> namedParameters = new HashMap<>();
        namedParameters.put("email", email);
        namedParameters.put("status_ids", statusIds);
        namedParameters.put("operation_type_id", operationType.getType());
        return namedParameterJdbcTemplate.query(sql, namedParameters, new RowMapper<OrderWideListDto>() {
            @Override
            public OrderWideListDto mapRow(ResultSet rs, int rowNum) throws SQLException {
                OrderWideListDto orderWideListDto = new OrderWideListDto();
                orderWideListDto.setId(rs.getInt("id"));
                orderWideListDto.setUserId(rs.getInt("user_id"));
                orderWideListDto.setOperationType(OperationType.convert(rs.getInt("operation_type_id")));
                orderWideListDto.setExExchangeRate(BigDecimalProcessing.formatLocale(rs.getBigDecimal("exrate"), locale, 2));
                orderWideListDto.setAmountBase(BigDecimalProcessing.formatLocale(rs.getBigDecimal("amount_base"), locale, 2));
                orderWideListDto.setAmountConvert(BigDecimalProcessing.formatLocale(rs.getBigDecimal("amount_convert"), locale, 2));
                orderWideListDto.setComissionId(rs.getInt("commission_id"));
                orderWideListDto.setCommissionFixedAmount(BigDecimalProcessing.formatLocale(rs.getBigDecimal("commission_fixed_amount"), locale, 2));
                BigDecimal amountWithCommission = rs.getBigDecimal("amount_convert");
                if (orderWideListDto.getOperationType() == OperationType.SELL) {
                    amountWithCommission = BigDecimalProcessing.doAction(amountWithCommission, rs.getBigDecimal("commission_fixed_amount"), ActionType.SUBTRACT);
                } else if (orderWideListDto.getOperationType() == OperationType.BUY) {
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
                return orderWideListDto;
            }
        });
    }

    @Override
    public OrderCreateDto getMyOrderById(int orderId) {
        String sql = "SELECT EXORDERS.id as order_id, EXORDERS.user_id, EXORDERS.status_id, EXORDERS.operation_type_id,  " +
                "  EXORDERS.exrate, EXORDERS.amount_base, EXORDERS.amount_convert, EXORDERS.commission_fixed_amount, " +
                "  CURRENCY_PAIR.id AS currency_pair_id, CURRENCY_PAIR.name AS currency_pair_name  " +
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
                "     EXORDERS.id, EXORDERS.date_creation, EXORDERS.status_id AS status, " +
                "     CURRENCY_PAIR.name as currency_pair_name,  " +
                "     UPPER(ORDER_OPERATION.name) AS order_type_name,  " +
                "     EXORDERS.exrate, EXORDERS.amount_base, " +
                "     CREATOR.email AS order_creator_email ";
        String sqlFrom = "FROM EXORDERS " +
                "      JOIN OPERATION_TYPE AS ORDER_OPERATION ON (ORDER_OPERATION.id = EXORDERS.operation_type_id) " +
                "      JOIN CURRENCY_PAIR ON (CURRENCY_PAIR.id = EXORDERS.currency_pair_id) " +
                "      JOIN USER CREATOR ON (CREATOR.id = EXORDERS.user_id) ";
        String sqlSelectCount = "SELECT COUNT(*) ";
        String limit;
        if (dataTableParams.getLength() > 0) {
            String offset = dataTableParams.getStart() > 0 ? " OFFSET :offset " : "";
            limit = " LIMIT :limit " + offset;
        } else {
            limit = "";
        }
        String orderBy = dataTableParams.getOrderByClause();
        Map<String, Object> namedParameters = new HashMap<>();
        namedParameters.put("offset", dataTableParams.getStart());
        namedParameters.put("limit", dataTableParams.getLength());
        namedParameters.putAll(adminOrderFilterData.getNamedParams());
        String criteria = adminOrderFilterData.getSQLFilterClause();
        String whereClause = StringUtils.isNotEmpty(criteria) ? "WHERE " + criteria : "";
        String selectQuery = new StringJoiner(" ").add(sqlSelect)
                .add(sqlFrom)
                .add(whereClause)
                .add(orderBy).add(limit).toString();
        String selectCountQuery = new StringJoiner(" ").add(sqlSelectCount)
                .add(sqlFrom)
                .add(whereClause).toString();
        LOGGER.debug(selectQuery);
        LOGGER.debug(selectCountQuery);

        PagingData<List<OrderBasicInfoDto>> result = new PagingData<>();

        List<OrderBasicInfoDto> infoDtoList = namedParameterJdbcTemplate.query(selectQuery, namedParameters, (rs, rowNum) -> {
            OrderBasicInfoDto infoDto = new OrderBasicInfoDto();
            infoDto.setId(rs.getInt("id"));
            infoDto.setDateCreation(rs.getTimestamp("date_creation").toLocalDateTime());
            infoDto.setCurrencyPairName(rs.getString("currency_pair_name"));
            infoDto.setOrderTypeName(rs.getString("order_type_name"));
            infoDto.setExrate(BigDecimalProcessing.formatLocale(rs.getBigDecimal("exrate"), locale, 2));
            infoDto.setAmountBase(BigDecimalProcessing.formatLocale(rs.getBigDecimal("amount_base"), locale, 2));
            infoDto.setOrderCreatorEmail(rs.getString("order_creator_email"));
            infoDto.setStatusId(rs.getInt("status"));
            infoDto.setStatus(OrderStatus.convert(rs.getInt("status")).toString());
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
                                         OperationType orderType) {
        String sortDirection = "";
        String exrateClause = "";
        if (orderType == OperationType.BUY) {
            sortDirection = "DESC";
            exrateClause = "AND exrate >= :exrate ";
        } else if (orderType == OperationType.SELL) {
            sortDirection = "ASC";
            exrateClause = "AND exrate <= :exrate ";
        }
        String sqlSetVar = "SET @cumsum := 0";

        /*needs to return several orders with best exrate if their total sum is less than amount in param,
        * or at least one order if base amount is greater than param amount*/
        String sql = "SELECT id, user_id, currency_pair_id, operation_type_id, exrate, amount_base, amount_convert, " +
                "commission_id, commission_fixed_amount, date_creation, status_id " +
                "FROM EXORDERS WHERE status_id = 2 AND currency_pair_id = :currency_pair_id " +
                "AND operation_type_id = :operation_type_id " + exrateClause +
                " ORDER BY exrate " + sortDirection + ", amount_base ASC ";
        Map<String, Number> params = new HashMap<String, Number>() {{
            put("currency_pair_id", currencyPairId);
            put("exrate", exrate);
            put("operation_type_id", orderType.getType());
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
            return exOrder;
        });
    }

}