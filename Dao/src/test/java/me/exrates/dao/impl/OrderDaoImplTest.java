package me.exrates.dao.impl;

import config.DataComparisonTest;
import me.exrates.dao.CommissionDao;
import me.exrates.dao.OrderDao;
import me.exrates.dao.WalletDao;
import me.exrates.model.CurrencyPair;
import me.exrates.model.ExOrder;
import me.exrates.model.dto.onlineTableDto.OrderListDto;
import me.exrates.model.dto.onlineTableDto.OrderWideListDto;
import me.exrates.model.dto.openAPI.OpenOrderDto;
import me.exrates.model.dto.openAPI.OrderBookItem;
import me.exrates.model.enums.OperationType;
import me.exrates.model.enums.OrderBaseType;
import me.exrates.model.enums.OrderEventEnum;
import me.exrates.model.enums.OrderStatus;
import me.exrates.model.enums.OrderType;
import org.apache.commons.compress.utils.Lists;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {OrderDaoImplTest.InnerConf.class})
public class OrderDaoImplTest extends DataComparisonTest {

    private final String TABLE_EXORDERS = "EXORDERS";
    private final String TABLE_STOP_ORDERS = "STOP_ORDERS";
    private final String TABLE_CURRENCY_PAIR = "CURRENCY_PAIR";
    private final String TABLE_COMMISSION = "COMMISSION";

    @Autowired
    private OrderDao orderDao;

    @Override
    protected void before() {
        try {
            truncateTables(TABLE_EXORDERS, TABLE_CURRENCY_PAIR, TABLE_STOP_ORDERS, TABLE_COMMISSION);
            String sql = "INSERT INTO EXORDERS"
                    + " (id, user_id, currency_pair_id, operation_type_id, exrate, amount_base, amount_convert, "
                    + "commission_id, commission_fixed_amount, status_id, order_source_id, base_type)"
                    + " VALUES "
                    + " (1, 1, 1, 1, 0.5, 1, 0.5, 1, 0.01, 2, 1, \'LIMIT\'), (2, 1, 1, 1, 0.5, 1, 0.5, 1, 0.01, 1, 1, \'ICO\')";
            prepareTestData(sql);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void createOrder_Success() {
        String sql = "SELECT * FROM " + TABLE_EXORDERS;

        around()
                .withSQL(sql)
                .run(() -> orderDao.createOrder(getTestOrder()));
    }

    @Test
    public void createOrder_Error() {
        ExOrder wrongOrder = getTestOrder();
        wrongOrder.setOrderBaseType(OrderBaseType.STOP_LIMIT);
        String errorMsg = "Data truncated for column 'base_type' at row 1";

        try {
            orderDao.createOrder(wrongOrder);
        } catch (Exception e) {
            assertEquals(e.getCause().getClass(), SQLException.class);
            assertEquals(e.getCause().getMessage(), errorMsg);
        }
    }

    @Test
    public void getOrderById_Ok() {
        ExOrder order = orderDao.getOrderById(1);
        assertEquals(1, order.getId());
    }

    @Test
    public void getOrderById_NotFound() {
        ExOrder order = orderDao.getOrderById(5);
        assertNull(order);
    }

    @Test
    public void getOrderById_Has_2_Arguments_Ok() {
        ExOrder order = orderDao.getOrderById(1, 1);
        assertEquals(1, order.getId());
    }

    @Test
    public void getOrderById_Has_2_Arguments_NotFound_Wrong_OrderId() {
        ExOrder order = orderDao.getOrderById(5, 1);
        assertNull(order);
    }

    @Test
    public void getOrderById_Has_2_Arguments_NotFound_Wrong_UserId() {
        ExOrder order = orderDao.getOrderById(1, 5);
        assertNull(order);
    }

    @Test
    public void getOrderById_Has_2_Arguments_NotFound_All_Args_Wrong() {
        ExOrder order = orderDao.getOrderById(5, 5);
        assertNull(order);
    }


    @Test
    public void updateOrder_Ok() {
        String sql = "SELECT * FROM " + TABLE_EXORDERS;

        ExOrder order = getTestOrder();
        order.setId(1);
        order.setUserAcceptorId(8);
        order.setStatus(OrderStatus.SPLIT_CLOSED);

        around()
                .withSQL(sql)
                .run(() -> orderDao.updateOrder(order));
    }

    @Test
    public void updateOrder_NotUpdate_The_Same_Object() {
        String sql = "SELECT * FROM " + TABLE_EXORDERS;

        ExOrder order = getTestOrder();

        around()
                .withSQL(sql)
                .run(() -> orderDao.updateOrder(order));
    }

    @Test
    public void updateOrder_Has_2_Arguments_Ok() {
        String sql = "SELECT * FROM " + TABLE_EXORDERS;

        int orderId = 1;

        ExOrder order = getTestOrder();
        order.setUserId(8);
        order.setCurrencyPairId(8);
        order.setOperationType(OperationType.MANUAL);
        order.setExRate(BigDecimal.TEN);
        order.setAmountBase(BigDecimal.TEN);
        order.setAmountConvert(BigDecimal.TEN);
        order.setComissionId(8);
        order.setCommissionFixedAmount(BigDecimal.TEN);
        order.setStatus(OrderStatus.SPLIT_CLOSED);
        order.setSourceId(8);
        order.setOrderBaseType(OrderBaseType.ICO);

        around()
                .withSQL(sql)
                .run(() -> orderDao.updateOrder(orderId, order));
    }

    @Test
    public void updateOrder_Has_2_Arguments_NotUpdate_The_Same_Object() {
        String sql = "SELECT * FROM " + TABLE_EXORDERS;

        int orderId = 1;
        ExOrder order = getTestOrder();

        around()
                .withSQL(sql)
                .run(() -> orderDao.updateOrder(orderId, order));
    }

    @Test
    public void postAcceptedOrderToDB_Ok() {
        String sql = "SELECT * FROM " + TABLE_EXORDERS;

        around()
                .withSQL(sql)
                .run(() -> orderDao.postAcceptedOrderToDB(getTestOrder()));
    }

    @Test
    public void getAllOpenedOrdersByUserId_Ok() {
        int userId = 1;

        List<ExOrder> actual = orderDao.getAllOpenedOrdersByUserId(userId);
        assertEquals(1, actual.size());
    }

    @Test
    public void getAllOpenedOrdersByUserId_NotFound() {
        int wrongUserId = 5;

        List<ExOrder> actual = orderDao.getAllOpenedOrdersByUserId(wrongUserId);
        assertEquals(0, actual.size());
    }

    @Test
    public void getMyOpenOrdersForCurrencyPair_Ok() throws SQLException {
        String sql = "INSERT INTO EXORDERS"
                + " (id, user_id, currency_pair_id, operation_type_id, exrate, amount_base, amount_convert, "
                + "commission_id, commission_fixed_amount, status_id, order_source_id, base_type) VALUES "
                + " (3, 3, 1, 3, 0.5, 1, 0.5, 1, 0.01, 2, 1, \'LIMIT\')";

        prepareTestData(sql);

        CurrencyPair currencyPair = new CurrencyPair();
        currencyPair.setId(1);
        OrderType orderType = OrderType.SELL;
        int userId = 3;

        List<OrderListDto> actual = orderDao.getMyOpenOrdersForCurrencyPair(currencyPair, orderType, userId);
        assertEquals(1, actual.size());
    }

    @Test
    public void getMyOpenOrdersForCurrencyPair_NotFound() {
        CurrencyPair currencyPair = new CurrencyPair();
        currencyPair.setId(1);
        OrderType orderType = OrderType.SELL;
        int userId = 3;

        List<OrderListDto> actual = orderDao.getMyOpenOrdersForCurrencyPair(currencyPair, orderType, userId);
        assertEquals(0, actual.size());
    }

    @Test
    public void getLastOrderPriceByCurrencyPairAndOperationType_Ok() throws SQLException {
        String sql = "INSERT INTO EXORDERS"
                + " (id, user_id, currency_pair_id, operation_type_id, exrate, amount_base, amount_convert, "
                + "commission_id, commission_fixed_amount, status_id, order_source_id, base_type) VALUES "
                + " (3, 3, 1, 3, 0.5, 1, 0.5, 1, 0.01, 3, 1, \'LIMIT\')";

        prepareTestData(sql);

        int currencyPairId = 1;
        int operationTypeId = 3;

        Optional<BigDecimal> actual = orderDao.getLastOrderPriceByCurrencyPairAndOperationType(currencyPairId, operationTypeId);
        assertTrue(actual.isPresent());
    }

    @Test
    public void getLastOrderPriceByCurrencyPairAndOperationType_NotFound() {
        int wrongCurrencyPairId = 1;
        int wrongOperationTypeId = 3;

        Optional<BigDecimal> actual = orderDao.getLastOrderPriceByCurrencyPairAndOperationType(wrongCurrencyPairId, wrongOperationTypeId);
        assertFalse(actual.isPresent());
    }

    @Test
    public void getLowestOpenOrderPriceByCurrencyPairAndOperationType_Ok() throws SQLException {
        String sql = "INSERT INTO EXORDERS"
                + " (id, user_id, currency_pair_id, operation_type_id, exrate, amount_base, amount_convert, "
                + "commission_id, commission_fixed_amount, status_id, order_source_id, base_type) VALUES "
                + " (3, 3, 1, 3, 0.5, 1, 0.5, 1, 0.01, 2, 1, \'LIMIT\')";

        prepareTestData(sql);

        int currencyPairId = 1;
        int operationTypeId = 3;

        Optional<BigDecimal> actual = orderDao.getLowestOpenOrderPriceByCurrencyPairAndOperationType(currencyPairId, operationTypeId);
        assertTrue(actual.isPresent());
    }

    @Test
    public void getLowestOpenOrderPriceByCurrencyPairAndOperationType_NotFound() {
        int currencyPairId = 1;
        int operationTypeId = 3;

        Optional<BigDecimal> actual = orderDao.getLowestOpenOrderPriceByCurrencyPairAndOperationType(currencyPairId, operationTypeId);
        assertFalse(actual.isPresent());
    }

    @Test
    public void setStatus_Ok() {
        String sql = "SELECT * FROM " + TABLE_EXORDERS;

        int orderId = 1;
        OrderStatus status = OrderStatus.DRAFT;

        around()
                .withSQL(sql)
                .run(() -> orderDao.setStatus(orderId, status));
    }

    @Test
    public void setStatus_NotUpdate_WrongOrderId() {
        String sql = "SELECT * FROM " + TABLE_EXORDERS;

        int wrongOrderId = 17;
        OrderStatus status = OrderStatus.DRAFT;

        around()
                .withSQL(sql)
                .run(() -> orderDao.setStatus(wrongOrderId, status));
    }

    @Test
    public void setStatus_NotUpdate_The_Same_Args() {
        String sql = "SELECT * FROM " + TABLE_EXORDERS;

        int wrongOrderId = 1;
        OrderStatus status = OrderStatus.SPLIT_CLOSED;

        around()
                .withSQL(sql)
                .run(() -> orderDao.setStatus(wrongOrderId, status));
    }

    @Test
    @Ignore
    public void searchOrderByAdmin_Ok() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        int currencyPair = 1;
        int orderType = 1;
        String orderDate = formatter.format(LocalDateTime.now());
        BigDecimal orderRate = BigDecimal.valueOf(0.5);
        BigDecimal orderVolume = BigDecimal.ONE;

        int actual = orderDao.searchOrderByAdmin(currencyPair, orderType, orderDate, orderRate, orderVolume);
        assertEquals(-1, actual);
    }

    @Test
    public void searchOrderByAdmin_EmptyResultDataAccessException() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        int currencyPair = 2;
        int orderType = 1;
        String orderDate = formatter.format(LocalDateTime.now());
        BigDecimal orderRate = BigDecimal.valueOf(0.5);
        BigDecimal orderVolume = BigDecimal.ONE;

        int actual = orderDao.searchOrderByAdmin(currencyPair, orderType, orderDate, orderRate, orderVolume);
        assertEquals(-1, actual);
    }

    @Test
    public void lockOrdersListForAcception_True() {
        boolean actual = orderDao.lockOrdersListForAcception(Collections.singletonList(1));
        assertTrue(actual);
    }

    @Test
    public void lockOrdersListForAcception_False() {
        boolean actual = orderDao.lockOrdersListForAcception(Collections.singletonList(8));
        assertFalse(actual);
    }

    @Test
    public void getOrderBookItems_Ok() throws SQLException {
        String sql = "INSERT INTO EXORDERS"
                + " (id, user_id, currency_pair_id, operation_type_id, exrate, amount_base, amount_convert, "
                + "commission_id, commission_fixed_amount, status_id, order_source_id, base_type) VALUES "
                + " (3, 3, 2, 3, 0.5, 1, 0.5, 1, 0.01, 2, 1, \'LIMIT\')";

        prepareTestData(sql);

        List<OrderBookItem> actual = orderDao.getOrderBookItems(2);
        assertEquals(1, actual.size());
    }

    @Test
    public void getOrderBookItems_NotFound() {
        List<OrderBookItem> actual = orderDao.getOrderBookItems(2);
        assertEquals(0, actual.size());
    }

    @Test
    public void getOrderBookItems_IllegalArgumentException() {
        String errorMsg = "Operation type INPUT not convertible to order type";
        try {
            orderDao.getOrderBookItems(1);
        } catch (Exception e) {
            assertTrue(e instanceof IllegalArgumentException);
            assertEquals(e.getMessage(), errorMsg);
        }
    }

    @Test
    public void getOpenOrders_Ok() throws SQLException {
        String sql = "INSERT INTO EXORDERS"
                + " (id, user_id, currency_pair_id, operation_type_id, exrate, amount_base, amount_convert, "
                + "commission_id, commission_fixed_amount, status_id, order_source_id, base_type) VALUES "
                + " (3, 3, 2, 3, 0.5, 1, 0.5, 1, 0.01, 2, 1, \'LIMIT\')";

        prepareTestData(sql);

        int currencyPairId = 2;
        OrderType orderType = OrderType.SELL;

        List<OpenOrderDto> actual = orderDao.getOpenOrders(currencyPairId, orderType);
        assertEquals(1, actual.size());
    }

    @Test
    public void getOpenOrders_NotFound() {
        Integer currencyPairId = 1;
        OrderType orderType = OrderType.BUY;

        List<OpenOrderDto> actual = orderDao.getOpenOrders(currencyPairId, orderType);
        assertEquals(0, actual.size());
    }

    @Test
    public void getLastOrderPriceByCurrencyPair_Ok() throws SQLException {
        String sql = "INSERT INTO EXORDERS"
                + " (id, user_id, currency_pair_id, operation_type_id, exrate, amount_base, amount_convert, "
                + "commission_id, commission_fixed_amount, status_id, order_source_id, base_type) VALUES "
                + " (3, 3, 2, 3, 0.5, 1, 0.5, 1, 0.01, 3, 1, \'LIMIT\')";

        prepareTestData(sql);

        int currencyPairId = 2;

        Optional<BigDecimal> actual = orderDao.getLastOrderPriceByCurrencyPair(currencyPairId);
        assertTrue(actual.isPresent());
    }

    @Test
    public void getLastOrderPriceByCurrencyPair_NotFound() {
        int currencyPairId = 2;

        Optional<BigDecimal> actual = orderDao.getLastOrderPriceByCurrencyPair(currencyPairId);
        assertFalse(actual.isPresent());
    }

    @Test
    public void findAllByOrderTypeAndCurrencyId_Ok() throws SQLException {
        String sql = "INSERT INTO EXORDERS"
                + " (id, user_id, currency_pair_id, operation_type_id, exrate, amount_base, amount_convert, "
                + "commission_id, commission_fixed_amount, status_id, order_source_id, base_type) VALUES "
                + " (3, 3, 2, 3, 0.5, 1, 0.5, 1, 0.01, 2, 1, \'LIMIT\')";

        prepareTestData(sql);

        int currencyId = 2;
        OrderType[] orderTypes = {OrderType.SELL, OrderType.BUY};

        List<OrderListDto> actual = orderDao.findAllByOrderTypeAndCurrencyId(currencyId, orderTypes);
        assertEquals(1, actual.size());
    }

    @Test
    public void findAllByOrderTypeAndCurrencyId_NotFound() {
        int currencyId = 2;
        OrderType[] orderTypes = {OrderType.SELL, OrderType.BUY};

        List<OrderListDto> actual = orderDao.findAllByOrderTypeAndCurrencyId(currencyId, orderTypes);
        assertEquals(0, actual.size());
    }

    @Test
    public void getMyOrdersWithState_Ok_Scope_OTHER() throws SQLException {
        String sql1 = "INSERT INTO EXORDERS"
                + " (id, user_id, currency_pair_id, operation_type_id, exrate, amount_base, amount_convert, "
                + "commission_id, commission_fixed_amount, status_id, order_source_id, base_type) VALUES "
                + " (3, 3, 2, 3, 0.5, 1, 0.5, 1, 0.01, 2, 1, \'LIMIT\')";

        String sql2 = "INSERT INTO CURRENCY_PAIR " +
                "(id,name,currency1_id,currency2_id,ticker_name) " +
                "VALUES " +
                "(2,\'BTC/USD\',1,2,\'BTC/USD\');";

        prepareTestData(sql1, sql2);


        Integer userId = 3;
        CurrencyPair currencyPair = new CurrencyPair();
        currencyPair.setId(2);
        List<OrderStatus> statuses = Collections.singletonList(OrderStatus.OPENED);
        OperationType operationType = OperationType.SELL;
        String scopeDefault = "";
        int offset = 0;
        int limit = 10;
        Locale locale = Locale.ENGLISH;

        List<OrderWideListDto> actual = orderDao.getMyOrdersWithState(
                userId,
                currencyPair,
                statuses,
                operationType,
                scopeDefault,
                offset,
                limit,
                locale);

        assertEquals(1, actual.size());
    }

    @Test
    public void getMyOrdersWithState_ByAcceptor() throws SQLException {
        String insertOrder = "INSERT INTO " + TABLE_EXORDERS
                + " (id, user_id, currency_pair_id, operation_type_id, exrate, amount_base, amount_convert, "
                + "commission_id, commission_fixed_amount, status_id, order_source_id, base_type) VALUES "
                + " (3, 3, 2, 3, 0.5, 1, 0.5, 1, 0.01, 2, 1, \'LIMIT\')";

        String insertCP = "INSERT INTO " + TABLE_CURRENCY_PAIR +
                "(id,name,currency1_id,currency2_id,ticker_name) " +
                "VALUES " +
                "(2,\'BTC/USD\',1,2,\'BTC/USD\');";

        prepareTestData(insertOrder, insertCP);

        String updateOrder = "UPDATE " + TABLE_EXORDERS + " SET date_acception = NOW(), date_creation = \'2019-01-01 12:12:12\', " +
                "user_acceptor_id = 1, status_id = 3 WHERE id = 3";

        String insertCommission = "INSERT INTO " + TABLE_COMMISSION + " (id, value, operation_type) VALUES (1, 1.0, 2)";

        prepareTestData(updateOrder, insertCommission);

        CurrencyPair currencyPair = new CurrencyPair("BTC/USD");
        currencyPair.setId(2);

        List<OrderWideListDto> actual = Lists.newArrayList();
        around()
                .withSQL("SELECT * FROM " + TABLE_EXORDERS)
                .run(() -> actual.addAll(orderDao.getMyOrdersWithState(
                        1,
                        currencyPair,
                        null,
                        OrderStatus.CLOSED,
                        "ALL",
                        10,
                        0,
                        false,
                        "DESC",
                        LocalDateTime.now().minusYears(1),
                        LocalDateTime.now(),
                        Locale.ENGLISH)));

        assertEquals(1, actual.size());
        assertTrue(actual.get(0).getDateCreation().isAfter(LocalDateTime.now().minusHours(6)));
    }

    @Test
    public void getMyOrdersWithState_Ok_Scope_ALL() throws SQLException {
        String sql1 = "INSERT INTO EXORDERS"
                + " (id, user_id, currency_pair_id, operation_type_id, exrate, amount_base, amount_convert, "
                + "commission_id, commission_fixed_amount, status_id, order_source_id, base_type) VALUES "
                + " (3, 3, 2, 3, 0.5, 1, 0.5, 1, 0.01, 2, 1, \'LIMIT\')";

        String sql2 = "INSERT INTO CURRENCY_PAIR " +
                "(id,name,currency1_id,currency2_id,ticker_name) " +
                "VALUES " +
                "(2,\'BTC/USD\',1,2,\'BTC/USD\');";

        prepareTestData(sql1, sql2);


        Integer userId = 3;
        CurrencyPair currencyPair = new CurrencyPair();
        currencyPair.setId(2);
        List<OrderStatus> statuses = Collections.singletonList(OrderStatus.OPENED);
        OperationType operationType = OperationType.SELL;
        String scopeALL = "ALL";
        int offset = 0;
        int limit = 10;
        Locale locale = Locale.ENGLISH;

        List<OrderWideListDto> actual = orderDao.getMyOrdersWithState(
                userId,
                currencyPair,
                statuses,
                operationType,
                scopeALL,
                offset,
                limit,
                locale);

        assertEquals(1, actual.size());
    }

    @Test
    public void getMyOrdersWithState_Ok_Scope_ACCEPTED() throws SQLException {
        String sql1 = "INSERT INTO EXORDERS " +
                "(id,user_id,operation_type_id,exrate,amount_base,amount_convert,commission_id,commission_fixed_amount," +
                "user_acceptor_id,status_id,currency_pair_id,base_type) " +
                "VALUES " +
                "(3,16,3,41340.930000000,0.002221200,91.826473716,8,0.183652947,16,1,3,\'LIMIT\');";

        String sql2 = "INSERT INTO CURRENCY_PAIR " +
                "(id,name,currency1_id,currency2_id,ticker_name) " +
                "VALUES " +
                "(3,\'BTC/USD\',1,2,\'BTC/USD\');";

        prepareTestData(sql1, sql2);


        Integer userId = 16;
        CurrencyPair currencyPair = new CurrencyPair();
        currencyPair.setId(3);
        List<OrderStatus> statuses = Collections.singletonList(OrderStatus.INPROCESS);
        OperationType operationType = OperationType.SELL;
        String scopeACCEPTED = "ACCEPTED";
        int offset = 0;
        int limit = 5;
        Locale locale = Locale.ENGLISH;

        List<OrderWideListDto> actual = orderDao.getMyOrdersWithState(
                userId,
                currencyPair,
                statuses,
                operationType,
                scopeACCEPTED,
                offset,
                limit,
                locale
        );
        assertEquals(1, actual.size());
    }

    @Test
    public void getMyOrdersWithState_Ok_Insert() throws SQLException {
        String sql1 = "INSERT INTO EXORDERS " +
                "(id, user_id, operation_type_id, exrate, amount_base, amount_convert, commission_id, commission_fixed_amount," +
                "user_acceptor_id, status_id, currency_pair_id, base_type, date_creation, status_modification_date) " +
                "VALUES " +
                "(3, 16, 3, 41340.930000000, 0.002221200, 91.826473716, 8, 0.183652947, 1, 2, 3, \'LIMIT\', \'2018-07-04 21:50:54\', \'2018-07-04 21:50:54\');";

        prepareTestData(sql1);

        String sql2 = "SELECT * FROM " + TABLE_EXORDERS;

        Integer userId = 16;
        CurrencyPair currencyPair = new CurrencyPair();
        currencyPair.setId(3);
        OrderStatus status = OrderStatus.OPENED;
        String all = "ALL";
        int offset = 0;
        int limit = 5;
        Locale locale = Locale.ENGLISH;

        around()
                .withSQL(sql2)
                .run(() -> orderDao.getMyOrdersWithState(
                        userId,
                        currencyPair,
                        null,
                        status,
                        all,
                        limit,
                        offset,
                        false,
                        "DESC",
                        null,
                        null,
                        locale
                ));
    }

    @Test
    public void getMyOrdersWithState_Ok_OpenedOrders() throws SQLException {
        String sql1 = "INSERT INTO EXORDERS " +
                "(id, user_id, operation_type_id, exrate, amount_base, amount_convert, commission_id, commission_fixed_amount," +
                "user_acceptor_id, status_id, currency_pair_id, base_type, date_creation, status_modification_date) " +
                "VALUES " +
                "(3, 16, 3, 41340.930000000, 0.002221200, 91.826473716, 8, 0.183652947, 1, 2, 3, \'LIMIT\', \'2018-07-04 21:50:54\', \'2018-07-04 21:50:54\');";

        String sql2 = "INSERT INTO CURRENCY_PAIR " +
                "(id, name, currency1_id, currency2_id, ticker_name) " +
                "VALUES " +
                "(3, \'BTC/USD\', 1, 2, \'BTC/USD\');";

        String sql3 = "INSERT INTO COMMISSION " +
                "(id, operation_type, value, date) " +
                "VALUES " +
                "(8,3, 0.002221200, \'2018-07-04 21:50:54\');";

        prepareTestData(sql1, sql2, sql3);


        Integer userId = 16;
        CurrencyPair currencyPair = new CurrencyPair();
        currencyPair.setId(3);
        OrderStatus status = OrderStatus.OPENED;
        String all = "ALL";
        int offset = 0;
        int limit = 5;
        Locale locale = Locale.ENGLISH;

        List<OrderWideListDto> actual = orderDao.getMyOrdersWithState(
                userId,
                currencyPair,
                null,
                status,
                all,
                limit,
                offset,
                false,
                "DESC",
                null,
                null,
                locale
        );
        assertEquals(1, actual.size());
        assertNotNull(actual.get(0).getDateCreation());
        assertNotNull(actual.get(0).getDateStatusModification());
        assertNull(actual.get(0).getDateModification());
    }

    @Test
    public void getMyOrdersWithState_Ok_ClosedOrders() throws SQLException {
        String sql1 = "INSERT INTO EXORDERS " +
                "(id, user_id, operation_type_id, exrate, amount_base, amount_convert, commission_id, commission_fixed_amount," +
                "user_acceptor_id, status_id, currency_pair_id, base_type, date_creation, date_acception, status_modification_date) " +
                "VALUES " +
                "(3, 16, 3, 41340.930000000, 0.002221200, 91.826473716, 8, 0.183652947, 16, 3, 3, \'LIMIT\', \'2018-07-04 21:50:54\', \'2018-07-04 21:50:54\', \'2018-07-04 21:50:54\');";

        String sql2 = "INSERT INTO CURRENCY_PAIR " +
                "(id, name, currency1_id, currency2_id, ticker_name) " +
                "VALUES " +
                "(3, \'BTC/USD\', 1, 2, \'BTC/USD\');";

        String sql3 = "INSERT INTO COMMISSION " +
                "(id, operation_type, value, date) " +
                "VALUES " +
                "(8,3, 0.002221200, \'2018-07-04 21:50:54\');";

        prepareTestData(sql1, sql2, sql3);


        Integer userId = 16;
        CurrencyPair currencyPair = new CurrencyPair();
        currencyPair.setId(3);
        OrderStatus status = OrderStatus.CLOSED;
        String all = "ALL";
        int offset = 0;
        int limit = 5;
        Locale locale = Locale.ENGLISH;

        List<OrderWideListDto> actual = orderDao.getMyOrdersWithState(
                userId,
                currencyPair,
                null,
                status,
                all,
                limit,
                offset,
                false,
                "DESC",
                null,
                null,
                locale
        );
        assertEquals(1, actual.size());
        assertNotNull(actual.get(0).getDateCreation());
        assertNotNull(actual.get(0).getDateAcception());
        assertNotNull(actual.get(0).getDateStatusModification());
        assertNull(actual.get(0).getDateModification());
    }

    @Test
    public void getMyOrdersWithState_Ok_OpenedStopOrders() throws SQLException {
        String sql1 = "INSERT INTO STOP_ORDERS " +
                "(id, user_id, currency_pair_id, operation_type_id, stop_rate, limit_rate, amount_base, amount_convert," +
                "commission_id, commission_fixed_amount, date_creation, date_modification, status_id) " +
                "VALUES " +
                "(3, 16, 3, 3, 41340.930000000, 41500.000000000, 0.002221200, 91.826473716, 8, 0.183652947, \'2018-07-04 21:50:54\', \'2018-07-04 21:50:54\', 2);";

        String sql2 = "INSERT INTO CURRENCY_PAIR " +
                "(id, name, currency1_id, currency2_id, ticker_name) " +
                "VALUES " +
                "(3, \'BTC/USD\', 1, 2, \'BTC/USD\');";

        String sql3 = "INSERT INTO COMMISSION " +
                "(id, operation_type, value, date) " +
                "VALUES " +
                "(8,3, 0.002221200, \'2018-07-04 21:50:54\');";

        prepareTestData(sql1, sql2, sql3);


        Integer userId = 16;
        CurrencyPair currencyPair = new CurrencyPair();
        currencyPair.setId(3);
        OrderStatus status = OrderStatus.OPENED;
        String all = "ALL";
        int offset = 0;
        int limit = 5;
        Locale locale = Locale.ENGLISH;

        List<OrderWideListDto> actual = orderDao.getMyOrdersWithState(
                userId,
                currencyPair,
                null,
                status,
                all,
                limit,
                offset,
                false,
                "ASC",
                null,
                null,
                locale
        );
        assertEquals(1, actual.size());
        assertNotNull(actual.get(0).getDateCreation());
        assertNotNull(actual.get(0).getDateModification());
        assertNull(actual.get(0).getDateStatusModification());
        assertNull(actual.get(0).getDateAcception());
    }

    @Test
    public void getMyOrdersWithState_Ok_ClosedStopOrders() throws SQLException {
        String sql1 = "INSERT INTO STOP_ORDERS " +
                "(id, user_id, currency_pair_id, operation_type_id, stop_rate, limit_rate, amount_base, amount_convert," +
                "commission_id, commission_fixed_amount, date_creation, date_modification, status_id) " +
                "VALUES " +
                "(3, 16, 3, 3, 41340.930000000, 41500.000000000, 0.002221200, 91.826473716, 8, 0.183652947, \'2018-07-04 21:50:54\', \'2018-07-04 21:50:54\', 3);";

        String sql2 = "INSERT INTO CURRENCY_PAIR " +
                "(id, name, currency1_id, currency2_id, ticker_name) " +
                "VALUES " +
                "(3, \'BTC/USD\', 1, 2, \'BTC/USD\');";

        String sql3 = "INSERT INTO COMMISSION " +
                "(id, operation_type, value, date) " +
                "VALUES " +
                "(8,3, 0.002221200, \'2018-07-04 21:50:54\');";

        prepareTestData(sql1, sql2, sql3);


        Integer userId = 16;
        CurrencyPair currencyPair = new CurrencyPair();
        currencyPair.setId(3);
        OrderStatus status = OrderStatus.CLOSED;
        String all = "ALL";
        int offset = 0;
        int limit = 5;
        Locale locale = Locale.ENGLISH;

        List<OrderWideListDto> actual = orderDao.getMyOrdersWithState(
                userId,
                currencyPair,
                null,
                status,
                all,
                limit,
                offset,
                false,
                "ASC",
                null,
                null,
                locale
        );
        assertEquals(1, actual.size());
        assertNotNull(actual.get(0).getDateCreation());
        assertNotNull(actual.get(0).getDateModification());
        assertNull(actual.get(0).getDateStatusModification());
        assertNull(actual.get(0).getDateAcception());
    }

    @Test
    public void getMyOrdersWithState_NotFound() {
        Integer userId = 3;
        CurrencyPair currencyPair = new CurrencyPair();
        currencyPair.setId(2);
        List<OrderStatus> statuses = Collections.singletonList(OrderStatus.OPENED);
        OperationType operationType = OperationType.SELL;
        String scopeDefault = "ACCEPTED";
        Integer offset = 0;
        Integer limit = 10;
        Locale locale = Locale.ENGLISH;

        List<OrderWideListDto> actual = orderDao.getMyOrdersWithState(
                userId,
                currencyPair,
                statuses,
                operationType,
                scopeDefault,
                offset,
                limit,
                locale);

        assertEquals(0, actual.size());
    }

    @Test
    public void getUnfilteredOrdersCount_Ok_Scope_OTHER() throws SQLException {
        String sql1 = "INSERT INTO EXORDERS " +
                "(id,user_id,operation_type_id,exrate,amount_base,amount_convert,commission_id,commission_fixed_amount," +
                "user_acceptor_id,status_id,currency_pair_id,base_type) " +
                "VALUES " +
                "(3,16,3,41340.930000000,0.002221200,91.826473716,8,0.183652947,16,1,3,\'LIMIT\');";

        String sql2 = "INSERT INTO CURRENCY_PAIR " +
                "(id,name,currency1_id,currency2_id,ticker_name) " +
                "VALUES " +
                "(3,\'BTC/USD\',1,2,\'BTC/USD\');";

        prepareTestData(sql1, sql2);

        int id = 16;
        CurrencyPair currencyPair = new CurrencyPair();
        currencyPair.setId(3);
        List<OrderStatus> statuses = Collections.singletonList(OrderStatus.INPROCESS);
        OperationType operationType = OperationType.SELL;
        String scopeDefault = "";
        int offset = 0;
        int limit = 5;
        Locale locale = Locale.ENGLISH;

        int actual = orderDao.getUnfilteredOrdersCount(
                id,
                currencyPair,
                statuses,
                operationType,
                scopeDefault,
                offset,
                limit
        );
        assertEquals(1, actual);
    }

    @Test
    public void getUnfilteredOrdersCount_Ok_Scope_ALL() throws SQLException {
        String sql = "INSERT INTO EXORDERS " +
                "(id,user_id,operation_type_id,exrate,amount_base,amount_convert,commission_id,commission_fixed_amount," +
                "user_acceptor_id,status_id,currency_pair_id,base_type) " +
                "VALUES " +
                "(3,16,3,41340.930000000,0.002221200,91.826473716,8,0.183652947,16,1,3,\'LIMIT\');";

        prepareTestData(sql);

        int id = 16;
        CurrencyPair currencyPair = new CurrencyPair();
        currencyPair.setId(3);
        List<OrderStatus> statuses = Collections.singletonList(OrderStatus.INPROCESS);
        OperationType operationType = OperationType.SELL;
        String scopeALL = "ALL";
        int offset = 0;
        int limit = 5;
        Locale locale = Locale.ENGLISH;

        int actual = orderDao.getUnfilteredOrdersCount(
                id,
                currencyPair,
                statuses,
                operationType,
                scopeALL,
                offset,
                limit
        );
        assertEquals(1, actual);
    }

    @Test
    public void getUnfilteredOrdersCount_Ok_Scope_ACCEPTED() throws SQLException {
        String sql = "INSERT INTO EXORDERS " +
                "(id,user_id,operation_type_id,exrate,amount_base,amount_convert,commission_id,commission_fixed_amount," +
                "user_acceptor_id,status_id,currency_pair_id,base_type) " +
                "VALUES " +
                "(3,16,3,41340.930000000,0.002221200,91.826473716,8,0.183652947,16,1,3,\'LIMIT\');";

        prepareTestData(sql);

        int id = 16;
        CurrencyPair currencyPair = new CurrencyPair();
        currencyPair.setId(3);
        List<OrderStatus> statuses = Collections.singletonList(OrderStatus.INPROCESS);
        OperationType operationType = OperationType.SELL;
        String scopeACCEPTED = "ACCEPTED";
        int offset = 0;
        int limit = 5;
        Locale locale = Locale.ENGLISH;

        int actual = orderDao.getUnfilteredOrdersCount(
                id,
                currencyPair,
                statuses,
                operationType,
                scopeACCEPTED,
                offset,
                limit
        );
        assertEquals(1, actual);
    }

    @Test
    public void getUnfilteredOrdersCount_Many_Statuses() throws SQLException {
        String sql = "INSERT INTO EXORDERS " +
                "(id,user_id,operation_type_id,exrate,amount_base,amount_convert,commission_id,commission_fixed_amount," +
                "user_acceptor_id,status_id,currency_pair_id,base_type) " +
                "VALUES " +
                "(3,16,3,41340.930000000,0.002221200,91.826473716,8,0.183652947,16,1,3,\'LIMIT\');";

        prepareTestData(sql);

        int id = 16;
        CurrencyPair currencyPair = new CurrencyPair();
        currencyPair.setId(3);
        List<OrderStatus> statuses = Arrays.asList(OrderStatus.INPROCESS, OrderStatus.OPENED);
        OperationType operationType = OperationType.SELL;
        String scopeACCEPTED = "ACCEPTED";
        int offset = 0;
        int limit = 5;
        Locale locale = Locale.ENGLISH;

        int actual = orderDao.getUnfilteredOrdersCount(
                id,
                currencyPair,
                statuses,
                operationType,
                scopeACCEPTED,
                offset,
                limit
        );
        assertEquals(1, actual);
    }

    @Test
    public void getUnfilteredOrdersCount_NotFound() {
        int id = 116;
        CurrencyPair currencyPair = new CurrencyPair();
        currencyPair.setId(3);
        List<OrderStatus> statuses = Collections.singletonList(OrderStatus.OPENED);
        OperationType operationType = OperationType.BUY;
        String scopeACCEPTED = "ACCEPTED";
        int offset = 0;
        int limit = 5;
        Locale locale = Locale.ENGLISH;

        int actual = orderDao.getUnfilteredOrdersCount(
                id,
                currencyPair,
                statuses,
                operationType,
                scopeACCEPTED,
                offset,
                limit
        );
        assertEquals(0, actual);
    }

    private ExOrder getTestOrder() {
        ExOrder order = new ExOrder();
        order.setUserId(1);
        order.setCurrencyPairId(1);
        order.setOperationType(OperationType.BUY);
        order.setExRate(BigDecimal.TEN);
        order.setAmountBase(BigDecimal.ONE);
        order.setAmountConvert(BigDecimal.TEN);
        order.setComissionId(1);
        order.setCommissionFixedAmount(BigDecimal.ZERO);
        order.setUserAcceptorId(2);
        order.setDateCreation(LocalDateTime.now());
        order.setDateAcception(LocalDateTime.now());
        order.setStatus(OrderStatus.OPENED);
        order.setCurrencyPair(null);
        order.setSourceId(1);
        order.setOrderBaseType(OrderBaseType.LIMIT);
        order.setTradeId(1L);
        order.setEvent(OrderEventEnum.CREATE);
        return order;
    }

    @Configuration
    static class InnerConf extends AppContextConfig {

        @Override
        protected String getSchema() {
            return "OrderDaoImplTest";
        }

        @Bean
        public OrderDao orderDao() {
            return new OrderDaoImpl();
        }

        @Bean
        public CommissionDao commissionDao() {
            return Mockito.mock(CommissionDao.class);
        }

        @Bean
        public WalletDao walletDao() {
            return Mockito.mock(WalletDao.class);
        }
    }
}
