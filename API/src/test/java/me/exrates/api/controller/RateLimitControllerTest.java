package me.exrates.api.controller;

import lombok.extern.log4j.Log4j2;
import me.exrates.api.ApiRequestsLimitExceedException;
import me.exrates.api.TestUtil;
import me.exrates.api.service.ApiRateLimitService;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import javax.annotation.PostConstruct;
import java.nio.charset.Charset;

import static me.exrates.api.controller.RateLimitController.TEST_ENDPOINT;
import static org.springframework.test.context.jdbc.Sql.ExecutionPhase.BEFORE_TEST_METHOD;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Created by Yuriy Berezin on 17.09.2018.
 */
@Log4j2
@Ignore
@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@ContextConfiguration({"classpath*:spring/servlet-context.xml", "classpath*:spring/api-test-context.xml"})
public class RateLimitControllerTest {

    private final MediaType contentType = new MediaType(MediaType.APPLICATION_JSON.getType(),
            MediaType.APPLICATION_JSON.getSubtype(),
            Charset.forName("utf8"));

    private MockMvc mockMvc;

    @Autowired
    private WebApplicationContext wac;

    @PostConstruct
    public void setup() {
        mockMvc = MockMvcBuilders.webAppContextSetup(wac).build();
    }

    @Test
    @Sql(scripts = {"/sql/delete-test-data.sql", "/sql/insert-test-data.sql"}, executionPhase = BEFORE_TEST_METHOD)
    public void testEndPoint() throws Exception {

        TestUtil.setAuth();

        log.info("Register valid requests");
        for (int i = 0; i < ApiRateLimitService.getDefaultAttemps(); i++) {
            log.info("# " + (i + 1));
            ResultActions actions = mockMvc.perform(get(TEST_ENDPOINT).
                    contentType(contentType)).
                    andExpect(status().isOk());
            String retStr = actions.andReturn().getResponse().getContentAsString();
            Assert.assertEquals("Register valid request failed", "\"OK\"", retStr);
            Thread.sleep(500);
        }
        log.info("Register exceeding requests");
        mockMvc.perform(get(TEST_ENDPOINT).
                contentType(contentType));

        ResultActions actions = mockMvc.perform(get(TEST_ENDPOINT).
                contentType(contentType)).
                andExpect(status().isNotAcceptable());
        String retStr = actions.andReturn().getResponse().getContentAsString();
        Assert.assertEquals("Invalid test response",
                "\"" + ApiRequestsLimitExceedException.class.getSimpleName() + "\"", retStr);
    }

}
