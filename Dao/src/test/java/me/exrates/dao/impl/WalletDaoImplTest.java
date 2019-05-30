package me.exrates.dao.impl;

import config.DataComparisonTest;
import me.exrates.dao.CurrencyDao;
import me.exrates.dao.TransactionDao;
import me.exrates.dao.UserDao;
import me.exrates.dao.WalletDao;
import me.exrates.model.User;
import me.exrates.model.Wallet;
import me.exrates.model.enums.TransactionSourceType;
import me.exrates.model.enums.WalletTransferStatus;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.math.BigDecimal;
import java.sql.SQLException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {WalletDaoImplTest.InnerConf.class})
public class WalletDaoImplTest extends DataComparisonTest {
    private final String TABLE_WALLET = "WALLET";

    @Autowired
    private WalletDao walletDao;

    @Override
    protected void before() {
        try {
            truncateTables(TABLE_WALLET);
            String sql = "INSERT INTO WALLET (id, currency_id, user_id, active_balance, reserved_balance, ieo_reserve) " +
                    "VALUES " +
                    "(1, 10, 9, 1000, 0.5, 0.5)," +
                    "(2, 11, 9, 1000, 0.12, 0.12);";
            prepareTestData(sql);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void getWalletABalance_WalletId_Equals_Zero() {
        BigDecimal actual = walletDao.getWalletABalance(0);

        assertEquals(BigDecimal.ZERO, actual);
    }

    @Test
    public void getWalletABalance_Ok() {
        BigDecimal actual = walletDao.getWalletABalance(1);

        assertEquals(BigDecimal.valueOf(1000).setScale(9), actual);
    }

    @Test
    public void getWalletABalance_Wrong_WalletId() {
        int wrongWalletId = 5;

        BigDecimal actual = walletDao.getWalletABalance(wrongWalletId);

        assertNull(actual);
    }

    @Test
    public void getWalletRBalance_Ok() {
        BigDecimal actual = walletDao.getWalletRBalance(1);

        assertEquals(BigDecimal.valueOf(0.5).setScale(9), actual);
    }

    @Test
    public void getWalletRBalance_Return_Null() {
        int wrongWalletId = 5;

        BigDecimal actual = walletDao.getWalletRBalance(wrongWalletId);

        assertNull(actual);
    }

    @Test
    public void getWalletId_Ok() {
        int userId = 9;
        int currencyId = 10;

        int actual = walletDao.getWalletId(userId, currencyId);

        assertEquals(1, actual);
    }

    @Test
    public void getWalletId_Wrong_UserId() {
        int wrongUserId = 19;
        int currencyId = 10;

        int actual = walletDao.getWalletId(wrongUserId, currencyId);

        assertEquals(0, actual);
    }

    @Test
    public void getWalletId_Wrong_CurrencyId() {
        int userId = 9;
        int wrongCurrencyId = 110;

        int actual = walletDao.getWalletId(userId, wrongCurrencyId);

        assertEquals(0, actual);
    }

    @Test
    public void getWalletId_Wrong_All_Args() {
        int wrongUserId = 19;
        int wrongCurrencyId = 110;

        int actual = walletDao.getWalletId(wrongUserId, wrongCurrencyId);

        assertEquals(0, actual);
    }

    @Test
    public void getWalletIdAndBlock_Ok() {
        int userId = 9;
        int currencyId = 10;

        int actual = walletDao.getWalletIdAndBlock(userId, currencyId);

        assertEquals(1, actual);
    }

    @Test
    public void getWalletIdAndBlock_Wrong_UserId() {
        int wrongUserId = 19;
        int currencyId = 10;

        int actual = walletDao.getWalletIdAndBlock(wrongUserId, currencyId);

        assertEquals(0, actual);
    }

    @Test
    public void getWalletIdAndBlock_Wrong_CurrencyId() {
        int userId = 9;
        int wrongCurrencyId = 110;

        int actual = walletDao.getWalletIdAndBlock(userId, wrongCurrencyId);

        assertEquals(0, actual);
    }

    @Test
    public void getWalletIdAndBlock_Wrong_All_Args() {
        int wrongUserId = 19;
        int wrongCurrencyId = 110;

        int actual = walletDao.getWalletIdAndBlock(wrongUserId, wrongCurrencyId);

        assertEquals(0, actual);
    }

    @Test
    public void createNewWallet_Ok() {
        String sql = "SELECT * FROM " + TABLE_WALLET;

        around()
                .withSQL(sql)
                .run(() -> walletDao.createNewWallet(getTestWallet(getTestUser())));
    }

    @Test
    public void createWallet_Ok() {
        String sql = "SELECT * FROM " + TABLE_WALLET;
        int currencyId = 4;

        around()
                .withSQL(sql)
                .run(() -> walletDao.createWallet(getTestUser(), currencyId));
    }

    @Test
    public void update_Ok() {
        String sql = "SELECT * FROM " + TABLE_WALLET;

        User testUser = getTestUser();
        testUser.setId(9);

        Wallet testWallet = getTestWallet(testUser);
        testWallet.setId(1);
        testWallet.setActiveBalance(BigDecimal.ONE);
        testWallet.setReservedBalance(BigDecimal.ONE);
        testWallet.setIeoReserved(BigDecimal.ONE);

        around()
                .withSQL(sql)
                .run(() -> walletDao.update(testWallet));
    }

    @Test
    public void update_NotUpdata() {
        Wallet testWallet = getTestWallet(getTestUser());
        testWallet.setId(3);

        boolean actual = walletDao.update(testWallet);

        assertFalse(actual);
    }

    @Test
    public void getUserIdFromWallet_Ok() {
        int actual = walletDao.getUserIdFromWallet(1);

        assertEquals(9, actual);
    }

    @Test
    public void getUserIdFromWallet_NotFound() {
        int wrongWalletId = 15;

        int actual = walletDao.getUserIdFromWallet(wrongWalletId);

        assertEquals(0, actual);
    }

    @Test
    public void walletInnerTransfer_WALLET_NOT_FOUND() {
        int wrongWalletId = 19;
        BigDecimal amount = BigDecimal.ZERO;
        TransactionSourceType sourceType = TransactionSourceType.WITHDRAW;
        int sourceId = 9;
        String description = "test description";

        WalletTransferStatus actual = walletDao.walletInnerTransfer(
                wrongWalletId,
                amount,
                sourceType,
                sourceId,
                description
        );

        assertEquals(WalletTransferStatus.WALLET_NOT_FOUND, actual);
    }

    @Test
    public void walletInnerTransfer_CAUSED_NEGATIVE_BALANCE() {
        int walletId = 1;
        BigDecimal amount = BigDecimal.ONE;
        TransactionSourceType sourceType = TransactionSourceType.IEO;
        int sourceId = 9;
        String description = "test description";

        WalletTransferStatus actual = walletDao.walletInnerTransfer(
                walletId,
                amount,
                sourceType,
                sourceId,
                description
        );

        assertEquals(WalletTransferStatus.CAUSED_NEGATIVE_BALANCE, actual);
    }

    @Test
    public void getAvailableAmountInBtcLocked_Ok() {
        int userId = 9;
        int currencyId = 10;

        BigDecimal actual = walletDao.getAvailableAmountInBtcLocked(userId, currencyId);

        assertEquals(BigDecimal.valueOf(1000).setScale(9), actual);
    }

    @Test
    public void getAvailableAmountInBtcLocked_Zero_WrongUserId() {
        int wrongUserId = 19;
        int currencyId = 10;

        BigDecimal actual = walletDao.getAvailableAmountInBtcLocked(wrongUserId, currencyId);

        assertEquals(BigDecimal.ZERO, actual);
    }

    @Test
    public void getAvailableAmountInBtcLocked_Zero_WrongCurrencyId() {
        int userId = 9;
        int wrongCurrencyId = 101;

        BigDecimal actual = walletDao.getAvailableAmountInBtcLocked(userId, wrongCurrencyId);

        assertEquals(BigDecimal.ZERO, actual);
    }

    @Test
    public void getAvailableAmountInBtcLocked_Zero_All_Args_Wrong() {
        int wrongUserId = 119;
        int wrongCurrencyId = 101;

        BigDecimal actual = walletDao.getAvailableAmountInBtcLocked(wrongUserId, wrongCurrencyId);

        assertEquals(BigDecimal.ZERO, actual);
    }

    @Test
    public void reserveUserBtcForIeo_Ok() {
        String sql = "SELECT * FROM " + TABLE_WALLET;

        int userId = 9;
        BigDecimal amountInBtc = BigDecimal.valueOf(125);
        int currencyId = 10;

        around()
                .withSQL(sql)
                .run(() -> {
                    boolean actual = walletDao.reserveUserBtcForIeo(userId, amountInBtc, currencyId);
                    assertTrue(actual);
                });
    }

    @Test
    public void reserveUserBtcForIeo_NotUpdata_Wrong_UserId() {
        int wrongUserId = 91;
        BigDecimal amountInBtc = BigDecimal.valueOf(125);
        int currencyId = 10;

        boolean actual = walletDao.reserveUserBtcForIeo(wrongUserId, amountInBtc, currencyId);

        assertFalse(actual);
    }

    @Test
    public void reserveUserBtcForIeo_NotUpdata_Wrong_CurrencyId() {
        int userId = 9;
        BigDecimal amountInBtc = BigDecimal.valueOf(125);
        int wrongCurrencyId = 101;

        boolean actual = walletDao.reserveUserBtcForIeo(userId, amountInBtc, wrongCurrencyId);

        assertFalse(actual);
    }

    @Test
    public void reserveUserBtcForIeo_NotUpdata_All_Args_Wrong() {
        int wrongUserId = 191;
        BigDecimal amountInBtc = BigDecimal.valueOf(125);
        int wrongCurrencyId = 101;

        boolean actual = walletDao.reserveUserBtcForIeo(wrongUserId, amountInBtc, wrongCurrencyId);

        assertFalse(actual);
    }

    @Test
    public void rollbackUserBtcForIeo_Ok() {
        String sql = "SELECT * FROM " + TABLE_WALLET;

        int userId = 9;
        BigDecimal amountInBtc = BigDecimal.valueOf(625);
        int currencyId = 10;

        around()
                .withSQL(sql)
                .run(() -> {
                    boolean actual = walletDao.rollbackUserBtcForIeo(userId, amountInBtc, currencyId);
                    assertTrue(actual);
                });
    }

    @Test
    public void rollbackUserBtcForIeo_NotUpdata_Wrong_UserId() {
        int wrongUserId = 91;
        BigDecimal amountInBtc = BigDecimal.valueOf(125);
        int currencyId = 10;

        boolean actual = walletDao.rollbackUserBtcForIeo(wrongUserId, amountInBtc, currencyId);

        assertFalse(actual);
    }

    @Test
    public void rollbackUserBtcForIeo_NotUpdata_Wrong_CurrencyId() {
        int userId = 9;
        BigDecimal amountInBtc = BigDecimal.valueOf(125);
        int wrongCurrencyId = 101;

        boolean actual = walletDao.rollbackUserBtcForIeo(userId, amountInBtc, wrongCurrencyId);

        assertFalse(actual);
    }

    @Test
    public void rollbackUserBtcForIeo_NotUpdata_All_Args_Wrong() {
        int wrongUserId = 191;
        BigDecimal amountInBtc = BigDecimal.valueOf(125);
        int wrongCurrencyId = 101;

        boolean actual = walletDao.rollbackUserBtcForIeo(wrongUserId, amountInBtc, wrongCurrencyId);

        assertFalse(actual);
    }

    @Test
    public void addToWalletBalance_Ok() {
        String sql = "SELECT * FROM " + TABLE_WALLET;

        int walletId = 2;
        BigDecimal addedAmountActiv = BigDecimal.valueOf(500);
        BigDecimal addedAmountReserved = BigDecimal.valueOf(100);

        around()
                .withSQL(sql)
                .run(() -> walletDao.addToWalletBalance(walletId, addedAmountActiv, addedAmountReserved));
    }

    @Test
    public void addToWalletBalance_WrongWalletId_Balance_Not_To_Add() {
        String sql = "SELECT * FROM " + TABLE_WALLET;

        int walletId = 21;
        BigDecimal addedAmountActiv = BigDecimal.valueOf(500);
        BigDecimal addedAmountReserved = BigDecimal.valueOf(100);

        around()
                .withSQL(sql)
                .run(() -> walletDao.addToWalletBalance(walletId, addedAmountActiv, addedAmountReserved));
    }

    private Wallet getTestWallet(User user) {
        Wallet wallet = new Wallet();
        wallet.setId(3);
        wallet.setUser(user);
        wallet.setCurrencyId(3);
        wallet.setActiveBalance(BigDecimal.TEN);
        wallet.setReservedBalance(BigDecimal.ZERO);
        wallet.setIeoReserved(BigDecimal.ONE);
        wallet.setName("TestWallet");
        return wallet;
    }

    private User getTestUser() {
        User user = new User();
        user.setId(10);
        user.setNickname("TestUser");
        user.setEmail("testuser@gmail.com");
        return user;
    }

    @Configuration
    static class InnerConf extends AppContextConfig {

        @Override
        protected String getSchema() {
            return "WalletDaoImplTest";
        }

        @Bean
        public WalletDao walletDao() {
            return new WalletDaoImpl();
        }

        @Bean
        public TransactionDao transactionDao() {
            return Mockito.mock(TransactionDao.class);
        }

        @Bean
        public UserDao userDao() {
            return Mockito.mock(UserDao.class);
        }

        @Bean
        public CurrencyDao currencyDao() {
            return Mockito.mock(CurrencyDao.class);
        }

        @Bean
        public NamedParameterJdbcTemplate masterTemplate() {
            return Mockito.mock(NamedParameterJdbcTemplate.class);
        }
    }
}
