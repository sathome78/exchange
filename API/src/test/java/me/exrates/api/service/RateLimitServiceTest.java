package me.exrates.api.service;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.jdbc.JdbcTestUtils;

import static org.springframework.test.context.jdbc.Sql.ExecutionPhase.AFTER_TEST_METHOD;
import static org.springframework.test.context.jdbc.Sql.ExecutionPhase.BEFORE_TEST_METHOD;

/**
 * Created by Yuriy Berezin on 14.09.2018.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({"classpath*:api-test-context.xml"})
public class RateLimitServiceTest {

    private static final Logger log = LogManager.getLogger(RateLimitServiceTest.class);

    private static final String TEST_EMAIL = "APITest@email.com";

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Before
    public void setAuth(){

        log.info("Set up");
        Authentication authentication = Mockito.mock(Authentication.class);
        Mockito.when(authentication.getName()).thenReturn(TEST_EMAIL);
        SecurityContext securityContext = Mockito.mock(SecurityContext.class);
        Mockito.when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
    }

    @Autowired
    private RateLimitService apiRateLimitService;

    @Test
    @Sql(scripts = {"/sql/insert-test-data.sql"}, executionPhase = BEFORE_TEST_METHOD)
    @Sql(scripts = {"/sql/delete-test-data.sql"}, executionPhase = AFTER_TEST_METHOD)
    public void limitCRUD(){

        int countBefore = JdbcTestUtils.countRowsInTable(jdbcTemplate, "user");
        apiRateLimitService.setLimit(TEST_EMAIL, 1000);
        int countAfter = JdbcTestUtils.countRowsInTable(jdbcTemplate, "user");

    }

    @Test(expected = RequestsLimitExceedException.class)
    public void registerRequest() throws Exception {

        log.info("Register Request");
        for (int i = 0; i < 6; i++) {
            log.info("# " + (i + 1));
            apiRateLimitService.clearExpiredRequests();
            apiRateLimitService.registerRequest();
            apiRateLimitService.checkLimitsExceed();
            Thread.sleep(1000);
        }
        Assert.fail("Register Request limit failed");
    }

}
