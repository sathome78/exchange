package me.exrates.ngcontroller;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultMatcher;
import org.springframework.test.web.servlet.setup.DefaultMockMvcBuilder;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.options;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@ContextConfiguration(classes = AngularAppTestConfig.class)
public class NgOptionsControllerTest {
    @Autowired
    private WebApplicationContext wac;

    private MockMvc mockMvc;

    @Before
    public void setUp() {
        DefaultMockMvcBuilder builder = MockMvcBuilders.webAppContextSetup(this.wac);
        this.mockMvc = builder.build();
    }

    @Test
    public void corsHeaders() throws Exception {
        ResultMatcher accessHeader = header()
                .string("Allow", "GET, HEAD, POST, PUT, DELETE, TRACE, OPTIONS, PATCH");

        mockMvc.perform(options("/api/**"))
                .andExpect(status().isOk())
                .andExpect(accessHeader)
                .andExpect(header().string("Access-Control-Allow-Methods", "HEAD, GET, POST, PUT, DELETE, PATCH, OPTION"))
                .andExpect(header().string("Access-Control-Allow-Headers", "authorization, content-type, X-Forwarded-For, x-auth-token, Exrates-Rest-Token, GACookies, access-control-request-headers, access-control-request-method, accept, origin, authorization, x-requested-with"))
                .andExpect(header().string("Access-Control-Max-Age", "3600"));
    }
}