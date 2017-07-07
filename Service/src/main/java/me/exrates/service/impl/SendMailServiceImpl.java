package me.exrates.service.impl;

import me.exrates.model.Email;
import me.exrates.model.enums.OperationType;
import me.exrates.service.SendMailService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
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

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Stream;

@Service
@PropertySource(value = {"classpath:/mail.properties"})
public class SendMailServiceImpl implements SendMailService{

	@Autowired
	@Qualifier("SupportMailSender")
	private JavaMailSender supportMailSender;

	@Autowired
	@Qualifier("InfoMailSender")
	private JavaMailSender infoMailSender;
	
	@Value("${mail_info.allowedOnly}")
	private Boolean allowedOnly;
	
	@Value("${mail_info.allowedEmails}")
	private String allowedEmailsList;

	private final static int THREADS_NUMBER = 3;
	private final static ExecutorService executors = Executors.newFixedThreadPool(THREADS_NUMBER);
	private final static ExecutorService supportMailExecutors = Executors.newFixedThreadPool(2);

	private static final Logger logger = LogManager.getLogger(SendMailServiceImpl.class);

	private final String SUPPORT_EMAIL = "mail@exrates.top";
	private final String INFO_EMAIL = "no-reply@exrates.top";

	@Transactional(propagation = Propagation.NOT_SUPPORTED)
	public void sendMail(Email email){
		supportMailExecutors.execute(() -> {
			try {
				sendMail(email, SUPPORT_EMAIL, supportMailSender);
			} catch (Exception e) {
				logger.error(e);
				sendMail(email, INFO_EMAIL, infoMailSender);
			}
		});
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
		executors.execute(() -> {
			try {
				sendMail(email, INFO_EMAIL, infoMailSender);
			} catch (MailException e) {
				logger.error(e);
				sendMail(email, SUPPORT_EMAIL, supportMailSender);
			}
		});

	}

	private void sendMail(Email email, String fromAddress, JavaMailSender mailSender) {
		email.setFrom(fromAddress);
		logger.debug(email);


		mailSender.send(mimeMessage -> {
			MimeMessageHelper message;
			message = new MimeMessageHelper(mimeMessage, true, "UTF-8");
			message.setFrom(email.getFrom());
			message.setTo(email.getTo());
			message.setSubject(email.getSubject());
			message.setText(email.getMessage(), true);
		});
	}

	@Override
	public void sendFeedbackMail(String senderName, String senderMail, String messageBody, String mailTo) {
		Email email = new Email();
		email.setFrom(senderMail);
		email.setTo(mailTo);
		email.setMessage(messageBody);
		email.setSubject("Feedback from " + senderName + " -- " + senderMail);
		sendMail(email);
	}


}
