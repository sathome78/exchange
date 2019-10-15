package me.exrates.service.job.invoice;

import lombok.extern.log4j.Log4j2;
import me.exrates.model.Email;
import me.exrates.model.condition.MonolitConditional;
import me.exrates.model.dto.WithdrawRequestPostDto;
import me.exrates.model.enums.invoice.InvoiceActionTypeEnum;
import me.exrates.model.enums.invoice.InvoiceStatus;
import me.exrates.model.enums.invoice.WithdrawStatusEnum;
import me.exrates.service.SendMailService;
import me.exrates.service.UserService;
import me.exrates.service.WithdrawService;
import me.exrates.service.exception.invoice.InsufficientCostsInWalletException;
import me.exrates.service.exception.invoice.InvalidAccountException;
import me.exrates.service.exception.invoice.MerchantException;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Properties;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static me.exrates.model.enums.invoice.InvoiceActionTypeEnum.POST_AUTO;

/**
 * Created by ValkSam
 */
@Service
@Log4j2(topic = "job")
@PropertySource(value = {"classpath:/job.properties"})
@Conditional(MonolitConditional.class)
public class withdrawRequestJob {
  
  
  @Value("${withdraw.rejectErrorTimeout}")
  private Long REJECT_ERROR_TIMEOUT;
  
  @Value("${withdraw.walletNotifyEmails}")
  private String WALLET_NOTIFY_EMAILS;

  @Autowired
  WithdrawService withdrawService;
  
  @Autowired
  private UserService userService;
  
  @Autowired
  SendMailService sendMailService;
  
  @Autowired
  MessageSource messageSource;

  private ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(2);

  @PostConstruct
  private void initSchedule() {
    scheduler.scheduleAtFixedRate(this::setInPostingStatus, 3, 1, TimeUnit.MINUTES);
    scheduler.scheduleAtFixedRate(this::postWithdraw, 3, 1, TimeUnit.MINUTES);
  }

 // @Scheduled(initialDelay = 1000, fixedDelay = 1000 * 60 * 1)
  private void setInPostingStatus() {
    log.info("before setInPostingStatus()");
    try {
      withdrawService.setAllAvailableInPostingStatus();
    } catch (Exception e) {
      log.error(ExceptionUtils.getStackTrace(e));
    }
    log.info("after setInPostingStatus()");

  }

 // @Scheduled(initialDelay = 1000, fixedDelay = 1000 * 60 * 1)
  private void postWithdraw() {
    try {
      log.info("start postWithdraw()");
      InvoiceActionTypeEnum action = POST_AUTO;
      List<InvoiceStatus> candidate = WithdrawStatusEnum.getAvailableForActionStatusesList(action);
      if (candidate.size() != 1) {
        log.fatal("no one or more then one base status for action " + action);
        throw new AssertionError();
      }
      List<WithdrawRequestPostDto> withdrawForPostingList = withdrawService.dirtyReadForPostByStatusList(candidate.get(0));
      for (WithdrawRequestPostDto withdrawRequest : withdrawForPostingList) {
        try {
          log.info("before autoPostWithdrawalRequest()");
          withdrawService.autoPostWithdrawalRequest(withdrawRequest);
          log.info("successful withdrawal: " + withdrawRequest.toString());
        }
        catch (InsufficientCostsInWalletException e) {
          log.error(ExceptionUtils.getStackTrace(e));
          withdrawService.rejectToReview(withdrawRequest.getId());
          sendEmailsOnInsufficientCosts(withdrawRequest.getCurrencyName());
        }
        catch (InvalidAccountException e) {
          log.error(ExceptionUtils.getStackTrace(e));
          withdrawService.rejectError(withdrawRequest.getId(), e.getReason());
        }
        catch (MerchantException e) {
          log.error(ExceptionUtils.getStackTrace(e));
          withdrawService.rejectToReview(withdrawRequest.getId());
        }
        catch (Exception e) {
          log.error(ExceptionUtils.getStackTrace(e));
        }
      }
    }
        catch (Exception e) {
      log.error(ExceptionUtils.getStackTrace(e));
    }
  }
  
  private void sendEmailsOnInsufficientCosts(String currencyName) {
    String[] notifyEmails = WALLET_NOTIFY_EMAILS.split(",");
    log.info("EMAILS TO NOTIFY: " + Arrays.toString(notifyEmails));
    for (String emailAddress : notifyEmails) {
      String userLanguage = userService.getPreferedLangByEmail(emailAddress);
      if (userLanguage == null) {
        userLanguage = "en";
      }
      Email email = new Email();
      email.setTo(emailAddress);
      Locale locale = new Locale(userLanguage);
      email.setSubject(messageSource.getMessage("withdraw.wallet.insufficientCosts.title", null, locale));
      email.setMessage(messageSource.getMessage("withdraw.wallet.insufficientCosts.body", new Object[]{currencyName}, locale));

      Properties properties = new Properties();
      properties.setProperty("public_id", userService.getPubIdByEmail(emailAddress));
      email.setProperties(properties);

      sendMailService.sendMail(email);

    }
  }

  @PreDestroy
  public void shutdown() {
    scheduler.shutdown();
  }

}



