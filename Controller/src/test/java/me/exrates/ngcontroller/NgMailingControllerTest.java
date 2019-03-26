package me.exrates.ngcontroller;

import me.exrates.model.mail.ListingRequest;
import me.exrates.model.ngExceptions.ValidationException;
import me.exrates.service.SendMailService;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.validation.Errors;

import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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
    public void sendEmail_WhenException() throws Exception {
        Errors result
        when(result.hasErrors()).thenThrow(ValidationException.class);
        mockMvc.perform(MockMvcRequestBuilders.post(BASE_URL + "/mail/send")
                .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE)).andDo(print())

                .andExpect(status().isOk());
    }

    @Test
    public void sendEmail_WhenOk() throws Exception {
        String json = "name";
        mockMvc.perform(MockMvcRequestBuilders.post(BASE_URL + "/mail/send")
                .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE)).andDo(print())

                .andExpect(status().isOk());
    }
}