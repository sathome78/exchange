package me.exrates.service.ieo;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableList;
import lombok.extern.log4j.Log4j2;
import me.exrates.dao.IEOClaimRepository;
import me.exrates.dao.IEOResultRepository;
import me.exrates.dao.IeoDetailsRepository;
import me.exrates.model.Email;
import me.exrates.model.IEOClaim;
import me.exrates.model.IEODetails;
import me.exrates.model.IEOResult;
import me.exrates.model.constants.ErrorApiTitles;
import me.exrates.model.dto.UserNotificationMessage;
import me.exrates.model.dto.WsMessageObject;
import me.exrates.model.enums.IEODetailsStatus;
import me.exrates.model.enums.UserNotificationType;
import me.exrates.model.enums.WsMessageTypeEnum;
import me.exrates.service.SendMailService;
import me.exrates.service.WalletService;
import me.exrates.service.exception.IeoException;
import me.exrates.service.stomp.StompMessenger;
import org.apache.commons.lang3.StringUtils;

import java.math.BigDecimal;
import java.util.concurrent.CompletableFuture;

@Log4j2
public class IEOProcessor implements Runnable {

    private final IEOResultRepository ieoResultRepository;
    private final IEOClaimRepository ieoClaimRepository;
    private final IeoDetailsRepository ieoDetailsRepository;
    private final SendMailService sendMailService;
    private final WalletService walletService;
    private final IEOClaim ieoClaim;
    private final ObjectMapper objectMapper;
    private final StompMessenger stompMessenger;

    public IEOProcessor(IEOResultRepository ieoResultRepository,
                        IEOClaimRepository ieoClaimRepository,
                        IeoDetailsRepository ieoDetailsRepository,
                        SendMailService sendMailService, IEOClaim ieoClaim,
                        WalletService walletService,
                        ObjectMapper objectMapper,
                        StompMessenger stompMessenger) {
        this.ieoResultRepository = ieoResultRepository;
        this.ieoClaimRepository = ieoClaimRepository;
        this.ieoDetailsRepository = ieoDetailsRepository;
        this.sendMailService = sendMailService;
        this.walletService = walletService;
        this.ieoClaim = ieoClaim;
        this.objectMapper = objectMapper;
        this.stompMessenger = stompMessenger;
    }

    @Override
    public void run() {
        final UserNotificationMessage notificationMessage;
        IEODetails ieoDetails = ieoDetailsRepository.findOne(ieoClaim.getIeoId());
        if (ieoDetails == null) {
            String message = String.format("Failed to find ieo details for id: %d", ieoClaim.getIeoId());
            log.warn(message);
            throw new IeoException(ErrorApiTitles.IEO_DETAILS_NOT_FOUND, message);
        }
        BigDecimal availableAmount = ieoDetails.getAvailableAmount();
        boolean firstTransaction = false;
        if (availableAmount.compareTo(BigDecimal.ZERO) == 0) {
            if (ieoResultRepository.isAlreadyStarted(ieoClaim)) {
                String text = String.format("Unfortunately all available %s amount is sold out!", ieoClaim.getCurrencyName());
                notificationMessage = new UserNotificationMessage(UserNotificationType.ERROR, text);
                log.error("{} {} has 0 available balance", ieoDetails.getCurrencyName(), ieoDetails.getCurrencyDescription());
                return;
            }
            log.error("The IEO is running, but it's first transaction");
            firstTransaction = true;
            ieoDetails.setStatus(IEODetailsStatus.RUNNING);
            ieoDetailsRepository.update(ieoDetails);
        }
        if (availableAmount.compareTo(ieoClaim.getAmount()) < 0) {
            // todo update notification message
            String notoficationMessage = String.format("Unfortunately, the available amount of is %s ", availableAmount.toPlainString());

            BigDecimal oldAmount = ieoClaim.getAmount();
            BigDecimal remainingAmount = oldAmount.subtract(availableAmount);
            BigDecimal btcRollBack = remainingAmount.multiply(ieoClaim.getRate());
            walletService.rollbackUserBtcForIeo(ieoClaim.getUserId(), btcRollBack);
            ieoClaim.setAmount(availableAmount);
            BigDecimal newPriceInBtc = remainingAmount.multiply(ieoClaim.getRate());
            ieoClaim.setPriceInBtc(newPriceInBtc);
            availableAmount = BigDecimal.ZERO;
        } else {
            availableAmount = availableAmount.subtract(ieoClaim.getAmount());
        }

        IEOResult ieoResult = IEOResult.builder()
                .claimId(ieoClaim.getId())
                .ieoId(ieoClaim.getIeoId())
                .availableAmount(availableAmount)
                .status(IEOResult.IEOResultStatus.SUCCESS)
                .build();
        if (firstTransaction) {
            ieoResult.setAvailableAmount(ieoDetails.getAmount());
            ieoResult.setClaimId(-1);
        } else {
            if (!walletService.performIeoTransfer(ieoClaim)) {
                ieoResult.setStatus(IEOResult.IEOResultStatus.FAILED);
            }
            ieoClaimRepository.updateStatusIEOClaim(ieoClaim.getId(), ieoResult.getStatus());
        }
        String text = String.format("Congratulations! You purchased %s %s, it will be able when IEO will succeed!",
                ieoClaim.getAmount().toPlainString(), ieoClaim.getCurrencyName());
        notificationMessage = new UserNotificationMessage(UserNotificationType.SUCCESS, text);
        ieoResultRepository.save(ieoResult);
        ieoDetailsRepository.updateAvailableAmount(ieoClaim.getIeoId(), availableAmount);
        ieoDetails.setAvailableAmount(availableAmount);
        ieoDetails.setPersonalAmount(walletService.findUserCurrencyBalance(ieoClaim));
        CompletableFuture.runAsync(() -> sendNotifications(ieoClaim.getCreatorEmail(), ieoDetails, notificationMessage));
    }

    private void sendNotifications(String userEmail, IEODetails ieoDetails, UserNotificationMessage message) {
        try {
            if (StringUtils.isNotEmpty(userEmail)) {
                String payload = objectMapper.writeValueAsString(new WsMessageObject(WsMessageTypeEnum.IEO, message));
                stompMessenger.sendPersonalMessageToUser(userEmail, payload);
                sendMailService.sendInfoMail(prepareEmail(userEmail, message));
            }
        } catch (Exception e) {
            /*ignore*/
        }
        try {
            if(StringUtils.isNotEmpty(userEmail)) {
                stompMessenger.sendPersonalDetailsIeo(userEmail, objectMapper.writeValueAsString(ImmutableList.of(ieoDetails)));
            }
        } catch (Exception e) {
            /*ignore*/
        }
        try {
            stompMessenger.sendDetailsIeo(ieoDetails.getId(), objectMapper.writeValueAsString(ieoDetails));
        } catch (Exception e) {
            /*ignore*/
        }
    }

    private Email prepareEmail(String userEmail, UserNotificationMessage message) {
        Email email = new Email();
        email.setTo(userEmail);
        email.setMessage(message.getText());
        email.setSubject(message.getNotificationType().name());
        return email;
    }
}
