package me.exrates.dao;

import config.DataComparisonTest;
import me.exrates.dao.impl.ApiAuthTokenDaoImpl;
import me.exrates.model.ApiAuthToken;
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
import java.time.LocalDateTime;
import java.util.Date;
import java.util.Optional;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertFalse;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = ApiAuthTokenDaoTest.InnerConfig.class)
public class ApiAuthTokenDaoTest extends DataComparisonTest {

    private final String TABLE_TOKEN = "API_AUTH_TOKEN";

    @Autowired
    private ApiAuthTokenDao apiAuthTokenDao;

    @Override
    protected void before() {
        try {
            truncateTables(TABLE_TOKEN);
            String sql = "INSERT INTO " + TABLE_TOKEN + " (id, username, value, expired_at) VALUE (1, \'username\', \'value\', \'" + LocalDateTime.now().minusHours(1) + "');";
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
                .withSQL("SELECT * FROM " + TABLE_TOKEN)
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
        final boolean deleted = apiAuthTokenDao.deleteExpiredToken(1L);

        assertTrue(deleted);
    }

    @Test
    public void deleteExpiredToken_ifNotFound() {
        final boolean deleted = apiAuthTokenDao.deleteExpiredToken(2L);

        assertFalse(deleted);
    }

    @Test
    public void deleteAllByUsername() {
        final boolean deleted = apiAuthTokenDao.deleteAllByUsername("username");

        assertTrue(deleted);
    }

    @Test
    public void deleteAllByUsername_ifNotFound() {
        final boolean deleted = apiAuthTokenDao.deleteAllByUsername("username2");

        assertFalse(deleted);
    }

    @Test
    public void deleteAllExpired() {
        final int count_of_deleted = apiAuthTokenDao.deleteAllExpired();

        assertEquals(1, count_of_deleted);
    }

    @Test
    public void deleteAllExceptCurrent() {
        final boolean deleted = apiAuthTokenDao.deleteAllExceptCurrent(1L, "username");

        assertFalse(deleted);
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