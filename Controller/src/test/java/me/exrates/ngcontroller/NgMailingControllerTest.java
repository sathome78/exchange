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

import static org.mockito.Matchers.*;
import static org.mockito.Mockito.doNothing;
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
        String json = "{\n" +
                "  \"name\":\"name\",\n" +
                "  \"email\":\"email@email.com\",\n" +
                "  \"telegram\":\"telegram\",\n" +
                "  \"text\":\"text\"\n" +
                "}";
//        when(errors.hasErrors()).thenReturn(Boolean.TRUE);

        mockMvc.perform(MockMvcRequestBuilders.post(BASE_URL + "/mail/send")
                .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE)
                .content(json)).andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    public void sendEmail_WhenOk() throws Exception {
        String json = "{\n" +
                "  \"name\":\"name\",\n" +
                "  \"email\":\"email@email.com\",\n" +
                "  \"telegram\":\"telegram\",\n" +
                "  \"text\":\"text\"\n" +
                "}";

        doNothing().when(sendMailService).sendListingRequestEmail(anyObject());

        mockMvc.perform(MockMvcRequestBuilders.post(BASE_URL + "/mail/send")
                .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE)
                .content(json))
                .andExpect(status().isOk());
    }
}