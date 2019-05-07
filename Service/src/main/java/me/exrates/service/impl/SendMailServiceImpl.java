package me.exrates.service.impl;

import lombok.extern.log4j.Log4j2;
import me.exrates.model.Email;
import me.exrates.model.enums.EmailSenderType;
import me.exrates.model.mail.ListingRequest;
import me.exrates.service.SendMailService;
import me.exrates.service.util.MessageFormatterUtil;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.time.StopWatch;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ResourceUtils;

import javax.annotation.PreDestroy;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;

@Log4j2(topic = "email_log")
@Service
@PropertySource(value = {"classpath:/mail.properties"})
public class SendMailServiceImpl implements SendMailService {

    @Value("${email-info-queue}")
    private String EMAIL_INFO_QUEUE;

    @Value("${email-mandrill-queue}")
    private String EMAIL_MANDRILL_QUEUE;

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
    public void sendMailMandrill(Email email) {
        rabbitTemplate.convertAndSend(EMAIL_MANDRILL_QUEUE, email);
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
