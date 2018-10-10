package me.exrates.dao;

import me.exrates.dao.impl.ApiAuthTokenDaoImpl;
import me.exrates.dao.rowmappers.ApiAuthTokenRowMapper;
import me.exrates.model.ApiAuthToken;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static me.exrates.dao.ApiAuthTokenDao.SELECT_TOKEN_BY_ID;
import static org.junit.Assert.assertEquals;


@RunWith(SpringJUnit4ClassRunner.class)
@DirtiesContext
@ContextConfiguration(locations = "classpath:dao-tests.xml")
public class ApiAuthTokenDaoTest {


    @Autowired
    NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    @Spy
    @Autowired
    ApiAuthTokenDaoImpl apiAuthTokenDao;

    public ApiAuthTokenDaoTest() {
        MockitoAnnotations.initMocks(this);
    }

    @Test

    public void createToken_successfull() {
        ApiAuthToken apiAuthToken = ApiAuthToken.builder().
                id(1L).
                username("user").
                value("value").
                lastRequest(LocalDateTime.now()).
                build();
        long token = apiAuthTokenDao.createToken(apiAuthToken);

        Map<String, Object> map = new HashMap<>();
        map.put("id", 0);
        List<ApiAuthToken> query = namedParameterJdbcTemplate.query(SELECT_TOKEN_BY_ID, map, new ApiAuthTokenRowMapper());

        assertEquals(0, token);
        assertEquals(1, query.size());
        assertEquals("user", query.get(0).getUsername());
        assertEquals("value", query.get(0).getValue());
    }

    @Test
    public void createToken_failed() {

    }

    @Test
    public void retrieveTokenById() {
    }

    @Test
    public void prolongToken() {
    }

    @Test
    public void deleteExpiredToken() {
    }

    @Test
    public void deleteAllExpired() {
    }
}