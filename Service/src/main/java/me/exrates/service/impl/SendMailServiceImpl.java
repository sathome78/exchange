package me.exrates.service.impl;

import lombok.extern.log4j.Log4j2;
import me.exrates.model.Email;
import me.exrates.model.mail.ListingRequest;
import me.exrates.service.SendMailService;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;

@Log4j2(topic = "email_log")
@Service
@PropertySource(value = {"classpath:/mail.properties"})
public class SendMailServiceImpl implements SendMailService {

    @Value("${email-info-queue}")
    private String EMAIL_INFO_QUEUE;

    @Value("${email-ses-queue}")
    private String EMAIL_SES_QUEUE;

    @Value("${email-queue}")
    private String EMAIL_QUEUE;

    @Value("${email-listing-email-queue}")
    private String EMAIL_LISTING_REQUEST_QUEUE;

    @Autowired
    RabbitTemplate rabbitTemplate;

    @Override
    public void sendMail(Email email) {
        rabbitTemplate.convertAndSend(EMAIL_QUEUE, email);
    }

    @Override
    public void sendMailSes(Email email) {
        rabbitTemplate.convertAndSend(EMAIL_SES_QUEUE, email);
    }

    @Override
    public void sendInfoMail(Email email) {
        rabbitTemplate.convertAndSend(EMAIL_INFO_QUEUE, email);
    }

    @Override
    public void sendListingRequestEmail(ListingRequest request) {
        rabbitTemplate.convertAndSend(EMAIL_LISTING_REQUEST_QUEUE, request);
    }
}
