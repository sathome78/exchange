package me.exrates.api.service;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

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
    private RateLimitService apiRateLimitService;

    @Test
    @Sql(scripts = {"/sql/delete-test-data.sql", "/sql/insert-test-data.sql"}, executionPhase = BEFORE_TEST_METHOD)
    public void limitCRUD(){

        log.info("Default limit");
        Integer limit1 = apiRateLimitService.getRequestLimit(TEST_EMAIL);
        Assert.assertEquals("No default value set", RateLimitService.getDefaultAttemps(), limit1);
        Assert.assertEquals("DEFAULT_ATTEMPS not cached", RateLimitService.getDefaultAttemps(),
                apiRateLimitService.getUserLimits().get(TEST_EMAIL));

        log.info("Set limit");
        Integer updatedLimit = 100;
        apiRateLimitService.setRequestLimit(TEST_EMAIL, updatedLimit);
        Integer limit2 = apiRateLimitService.getRequestLimit(TEST_EMAIL);
        Assert.assertEquals("Value not updated", Integer.valueOf(100), limit2);
        Assert.assertEquals("Value not cached", updatedLimit,
                apiRateLimitService.getUserLimits().get(TEST_EMAIL));
    }

    @Test
    public void registerRequest() throws Exception {

        setAuth();

        log.info("Register Request");
        for (int i = 0; i < 5; i++) {
            log.info("# " + (i + 1));
            apiRateLimitService.clearExpiredRequests();
            apiRateLimitService.registerRequest();
            Thread.sleep(500);
        }
        boolean checkResult = apiRateLimitService.checkLimitsExceed();
        Assert.assertFalse("Register Request limit failed", checkResult);
    }

    private void setAuth(){

        log.info("Set authentication");
        Authentication authentication = Mockito.mock(Authentication.class);
        Mockito.when(authentication.getName()).thenReturn(TEST_EMAIL);
        SecurityContext securityContext = Mockito.mock(SecurityContext.class);
        Mockito.when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
    }

}
