package me.exrates.service.impl;

import lombok.extern.log4j.Log4j2;
import me.exrates.model.Email;
import me.exrates.model.enums.EmailSenderType;
import me.exrates.model.mail.ListingRequest;
import me.exrates.service.SendMailService;
import me.exrates.service.util.MessageFormatterUtil;
import org.apache.commons.lang3.time.StopWatch;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PreDestroy;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;

@Log4j2(topic = "email_log")
@Service
@PropertySource(value = {"classpath:/mail.properties"})
public class SendMailServiceImpl implements SendMailService {

    @Autowired
    @Qualifier("SupportMailSender")
    private JavaMailSender supportMailSender;

    @Autowired
    @Qualifier("MandrillMailSender")
    private JavaMailSender mandrillMailSender;

    @Autowired
    @Qualifier("InfoMailSender")
    private JavaMailSender infoMailSender;

    @Value("${mail_info.allowedOnly}")
    private Boolean allowedOnly;

    @Value("${mail_info.allowedEmails}")
    private String allowedEmailsList;

    @Value("${default_mail_type}")
    private String mailType;

    @Value("${support.email}")
    private String supportEmail;

    @Value("${mandrill.email}")
    private String mandrillEmail;

    @Value("${info.email}")
    private String infoEmail;

    @Value("${listing.email}")
    private String listingEmail;

    @Value("${listing.subject}")
    private String listingSubject;

    private final static ExecutorService EXECUTORS = Executors.newFixedThreadPool(8);
    private final static ExecutorService SUPPORT_MAIL_EXECUTORS = Executors.newFixedThreadPool(3);

    private static final String UTF8 = "UTF-8";

    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    public void sendMail(Email email) {
        SUPPORT_MAIL_EXECUTORS.execute(() -> {
            try {
                sendMail(email.toBuilder()
                                .from(supportEmail)
                                .build(),
                        supportMailSender);
            } catch (Exception ex) {
                log.error(ex);
                sendMail(email.toBuilder()
                                .from(infoEmail)
                                .build(),
                        infoMailSender);
            }
        });
    }

    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    public void sendMailMandrill(Email email) {
        SUPPORT_MAIL_EXECUTORS.execute(() -> {
            try {
                sendByType(email, EmailSenderType.valueOf(mailType));
            } catch (Exception e) {
                log.error(e);
                sendMail(email.toBuilder()
                                .from(supportEmail)
                                .build(),
                        supportMailSender);
            }
        });
    }

    private void sendByType(Email email, EmailSenderType type) {
        switch (type) {
            case gmail: {
                sendInfoMail(email);
                break;
            }
            case mandrill: {
                sendMail(email.toBuilder()
                                .from(mandrillEmail)
                                .build(),
                        mandrillMailSender);
                break;
            }
        }
    }

    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    @Override
    public void sendInfoMail(Email email) {
        if (allowedOnly) {
            String[] allowedEmails = allowedEmailsList.split(",");
            if (Stream.of(allowedEmails).noneMatch(mail -> mail.equals(email.getTo()))) {
                return;
            }
        }
        EXECUTORS.execute(() -> {
            try {
                sendMail(email.toBuilder()
                                .from(infoEmail)
                                .build(),
                        infoMailSender);
            } catch (MailException ex) {
                log.error(ex);
                sendMail(email.toBuilder()
                                .from(supportEmail)
                                .build(),
                        supportMailSender);
            }
        });
    }

    private void sendMail(Email email, JavaMailSender mailSender) {
        StopWatch stopWatch = StopWatch.createStarted();
        log.debug("try to send email {} to {}, time {}", email.getSubject(), email.getTo());
        try {
            mailSender.send(mimeMessage -> {
                MimeMessageHelper message;
                message = new MimeMessageHelper(mimeMessage, true, UTF8);
                message.setFrom(email.getFrom());
                message.setTo(email.getTo());
                message.setSubject(email.getSubject());
                message.setText(email.getMessage(), true);
                if (email.getAttachments() != null) {
                    for (Email.Attachment attachment : email.getAttachments())
                        message.addAttachment(attachment.getName(), attachment.getResource(), attachment.getContentType());
                }
            });
            log.info("Email sent: {}, duration {} seconds", email, stopWatch.getTime(TimeUnit.SECONDS));
        } catch (Exception ex) {
            log.error("Could not send email {}. Reason: {}", email, ex.getMessage());
        }
    }

    @Override
    public void sendFeedbackMail(String senderName, String senderMail, String messageBody, String mailTo) {
        sendMail(Email.builder()
                .from(senderMail)
                .to(mailTo)
                .message(messageBody)
                .subject(String.format("Feedback from %s -- %s", senderName, senderMail))
                .build());
    }

    public void sendListingRequestEmail(ListingRequest request) {
        final String name = request.getName();
        final String email = request.getEmail();
        final String telegram = request.getTelegram();
        final String text = request.getText();

        sendInfoMail(Email.builder()
                .to(listingEmail)
                .subject(listingSubject)
                .message(MessageFormatterUtil.format(name, email, telegram, text))
                .build());
    }

    @PreDestroy
    public void destroy() {
        EXECUTORS.shutdown();
        SUPPORT_MAIL_EXECUTORS.shutdown();
    }
}
