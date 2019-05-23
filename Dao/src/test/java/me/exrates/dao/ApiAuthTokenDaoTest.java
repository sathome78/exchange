package me.exrates.dao;

import config.DataComparisonTest;
import me.exrates.dao.impl.ApiAuthTokenDaoImpl;
import me.exrates.model.ApiAuthToken;
import org.apache.commons.lang3.mutable.MutableBoolean;
import org.apache.commons.lang3.mutable.MutableInt;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.sql.SQLException;
import java.time.Clock;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.Optional;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertFalse;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = ApiAuthTokenDaoTest.InnerConfig.class)
public class ApiAuthTokenDaoTest extends DataComparisonTest {

    private final String TABLE = "API_AUTH_TOKEN";
    private final String SELECT_ALL = "SELECT * FROM " + TABLE;
    private final LocalDateTime TEST_TIME_UTC = LocalDateTime.now(Clock.systemUTC()).minusHours(1);

    @Autowired
    private ApiAuthTokenDao apiAuthTokenDao;

    @Override
    protected void before() {
        try {
            truncateTables(TABLE);
            String sql = "INSERT INTO " + TABLE + " (id, username, value, expired_at) VALUE (1, \'username\', \'value\', \'" + TEST_TIME_UTC + "');";
            prepareTestData(sql);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Test

    public void createToken_successfull() {
        ApiAuthToken apiAuthToken = ApiAuthToken.builder()
                .id(1L)
                .username("user")
                .value("value1")
                .expiredAt(new Date())
                .build();

        around()
                .withSQL(SELECT_ALL)
                .run(() -> apiAuthTokenDao.createToken(apiAuthToken));
    }

    @Test
    public void retrieveTokenById_Ok() {
        final Optional<ApiAuthToken> found = apiAuthTokenDao.retrieveTokenById(1L);
        assertTrue(found.isPresent());
        assertEquals("username", found.get().getUsername());
    }

    @Test
    public void retrieveTokenById_NotFound() {
        final Optional<ApiAuthToken> found = apiAuthTokenDao.retrieveTokenById(2L);
        assertFalse(found.isPresent());
    }

    @Test
    public void deleteExpiredToken() {
        MutableBoolean result = new MutableBoolean(false);
        around()
                .withSQL(SELECT_ALL)
                .run(() -> result.setValue(apiAuthTokenDao.deleteExpiredToken(1L)));

        assertTrue(result.getValue());
    }

    @Test
    public void deleteExpiredToken_ifNotFound() {
        MutableBoolean result = new MutableBoolean(false);
        around()
                .withSQL(SELECT_ALL)
                .run(() -> apiAuthTokenDao.deleteExpiredToken(2L));

        assertFalse(result.getValue());
    }

    @Test
    public void deleteAllByUsername() {
        MutableBoolean result = new MutableBoolean(false);
        around()
                .withSQL(SELECT_ALL)
                .run(() -> result.setValue(apiAuthTokenDao.deleteAllByUsername("username")));

        assertTrue(result.getValue());
    }

    @Test
    public void deleteAllByUsername_ifNotFound() {
        MutableBoolean result = new MutableBoolean(false);
        around()
                .withSQL(SELECT_ALL)
                .run(() -> result.setValue(apiAuthTokenDao.deleteAllByUsername("username2")));
        assertFalse(result.getValue());
    }

    @Test
    public void deleteAllExpired() {
        MutableInt numberOfAffectedRows = new MutableInt(0);
        around()
                .withSQL(SELECT_ALL)
                .run(() -> numberOfAffectedRows.setValue(apiAuthTokenDao.deleteAllExpired()));

        assertTrue(numberOfAffectedRows.getValue() == 1);
    }

    @Test
    public void deleteAllExceptCurrent() throws SQLException {
        String sql = "INSERT INTO " + TABLE + " (id, username, value, expired_at) VALUES "
                + "(2, \'username\', \'value\', \'" + TEST_TIME_UTC + "'), "
                + "(3, \'username\', \'value\', \'" + TEST_TIME_UTC + "'), "
                + "(4, \'username\', \'value\', \'" + TEST_TIME_UTC + "');";
        prepareTestData(sql);

        MutableBoolean result = new MutableBoolean(false);

        around()
                .withSQL(SELECT_ALL)
                .run(() -> result.setValue(apiAuthTokenDao.deleteAllExceptCurrent(1L, "username")));

        assertTrue(result.getValue());
    }

    @Configuration
    static class InnerConfig extends AppContextConfig {

        @Autowired
        @Qualifier("masterTemplate")
        private NamedParameterJdbcTemplate jdbcTemplate;

        @Override
        protected String getSchema() {
            return "ApiAuthTokenDaoTest";
        }

        @Bean
        public ApiAuthTokenDao apiAuthTokenDao() {
            return new ApiAuthTokenDaoImpl(jdbcTemplate);
        }

    }
}
