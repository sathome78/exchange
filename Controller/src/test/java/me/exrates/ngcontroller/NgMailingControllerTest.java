package me.exrates.ngcontroller;

import me.exrates.service.SendMailService;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

public class NgMailingControllerTest {

    private final static String BASE_URL = "/api/public/v2/listing";

    @Mock
    private SendMailService sendMailService;

    @InjectMocks
    private NgMailingController ngMailingController;

    private MockMvc mockMvc;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);

        mockMvc = MockMvcBuilders.standaloneSetup(ngMailingController)
                .build();
    }

    @Test
    public void sendEmail() {
    }
}