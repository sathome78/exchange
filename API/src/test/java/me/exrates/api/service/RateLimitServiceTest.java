package me.exrates.api.service;

import me.exrates.api.TestUtil;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.springframework.test.context.jdbc.Sql.ExecutionPhase.BEFORE_TEST_METHOD;

/**
 * Created by Yuriy Berezin on 14.09.2018.
 */
@Ignore
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({"classpath*:spring/api-test-context.xml"})
public class RateLimitServiceTest {

    private static final Logger log = LogManager.getLogger(RateLimitServiceTest.class);


    @Autowired
    private ApiRateLimitService apiRateLimitService;

    @Test
    @Sql(scripts = {"/sql/delete-test-data.sql", "/sql/insert-test-data.sql"}, executionPhase = BEFORE_TEST_METHOD)
    public void limitCRUD(){

        log.info("Default limit");
        Integer limit1 = apiRateLimitService.getRequestLimit(TestUtil.TEST_EMAIL);
        Assert.assertEquals("No default value set", ApiRateLimitService.getDefaultAttemps(), limit1);
        Assert.assertEquals("DEFAULT_ATTEMPS not cached", ApiRateLimitService.getDefaultAttemps(),
                apiRateLimitService.getUserLimits().get(TestUtil.TEST_EMAIL));
        log.info("Set limit");
        Integer updatedLimit = 100;
        apiRateLimitService.setRequestLimit(TestUtil.TEST_EMAIL, updatedLimit);
        Integer limit2 = apiRateLimitService.getRequestLimit(TestUtil.TEST_EMAIL);
        Assert.assertEquals("Value not updated", Integer.valueOf(100), limit2);
        Assert.assertEquals("Value not cached", updatedLimit,
                apiRateLimitService.getUserLimits().get(TestUtil.TEST_EMAIL));
    }

    @Test
    public void registerRequest() throws Exception {

        TestUtil.setAuth();

        log.info("Register valid requests");
        for (int i = 0; i < ApiRateLimitService.getDefaultAttemps(); i++) {
            log.info("# " + (i + 1));
            apiRateLimitService.registerRequest();
            Assert.assertFalse("Register valid request failed", apiRateLimitService.isLimitExceed());
            Thread.sleep(500);
        }
        log.info("Register exceeding request");
        apiRateLimitService.registerRequest();
        Assert.assertTrue("Register exceeding request failed", apiRateLimitService.isLimitExceed());
    }

}
