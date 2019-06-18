package me.exrates.service.impl;

import lombok.extern.log4j.Log4j2;
import me.exrates.model.Email;
import me.exrates.model.mail.ListingRequest;
import me.exrates.service.SendMailService;
import me.exrates.service.util.MessageFormatterUtil;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

@Log4j2(topic = "email_log")
@Service
@PropertySource(value = {"classpath:/mail.properties"})
public class SendMailServiceImpl implements SendMailService {

    @Autowired
    ResourceLoader resourceLoader;

    @Autowired
    RabbitTemplate rabbitTemplate;

    @Value("${email-queue}")
    private String EMAIL_QUEUE;

    @Value("${listing.email}")
    private String listingEmail;

    @Value("${listing.subject}")
    private String listingSubject;

    @Override
    public void sendMail(Email email) {
        email.setMessage(prepareTemplate(email.getMessage()));
        rabbitTemplate.convertAndSend(EMAIL_QUEUE, email);
    }

    @Override
    public void sendListingRequestEmail(ListingRequest request) {
        final String name = request.getName();
        final String emailBody = request.getEmail();
        final String telegram = request.getTelegram();
        final String text = request.getText();

        Email email = Email.builder()
                .to(listingEmail)
                .subject(listingSubject)
                .message(MessageFormatterUtil.format(name, emailBody, telegram, text))
                .build();

        this.sendMail(email);
    }

    private String prepareTemplate(String text) {
        File file;
        String html;

        try {
            Resource resource = resourceLoader.getResource(
                    "classpath:email/template.html");

            file = resource.getFile();
            byte[] encoded = Files.readAllBytes(Paths.get(file.getAbsolutePath()));
            html = new String(encoded, StandardCharsets.UTF_8.name());
        } catch (IOException e) {
            return text;
        }
        html = html.replace("{::text::}", text);
        return html;
    }
}
