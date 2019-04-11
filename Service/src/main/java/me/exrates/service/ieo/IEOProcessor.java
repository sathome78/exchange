package me.exrates.service.ieo;

import com.fasterxml.jackson.core.JsonProcessingException;
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
import me.exrates.model.Wallet;
import me.exrates.model.constants.ErrorApiTitles;
import me.exrates.model.dto.UserNotificationMessage;
import me.exrates.model.dto.WsMessageObject;
import me.exrates.model.enums.UserNotificationType;
import me.exrates.model.enums.WsMessageTypeEnum;
import me.exrates.service.SendMailService;
import me.exrates.service.UserService;
import me.exrates.service.WalletService;
import me.exrates.model.exceptions.IeoException;
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
    private final UserService userService;
    private final IEOClaim ieoClaim;
    private final ObjectMapper objectMapper;
    private final StompMessenger stompMessenger;

    public IEOProcessor(IEOResultRepository ieoResultRepository,
                        IEOClaimRepository ieoClaimRepository,
                        IeoDetailsRepository ieoDetailsRepository,
                        SendMailService sendMailService, UserService userService, IEOClaim ieoClaim,
                        WalletService walletService,
                        ObjectMapper objectMapper,
                        StompMessenger stompMessenger) {
        this.ieoResultRepository = ieoResultRepository;
        this.ieoClaimRepository = ieoClaimRepository;
        this.ieoDetailsRepository = ieoDetailsRepository;
        this.sendMailService = sendMailService;
        this.userService = userService;
        this.walletService = walletService;
        this.ieoClaim = ieoClaim;
        this.objectMapper = objectMapper;
        this.stompMessenger = stompMessenger;
    }

    @Override
    public void run() {
        String msg = String.format("Congrats! You successfully purchased %s %s", ieoClaim.getAmount().toPlainString(), ieoClaim.getCurrencyName());
        final UserNotificationMessage notificationMessage = new UserNotificationMessage(UserNotificationType.SUCCESS, msg);
        IEODetails ieoDetails = ieoDetailsRepository.findOne(ieoClaim.getIeoId());
        if (ieoDetails == null) {
            String message = String.format("Failed to find ieo details for id: %d", ieoClaim.getIeoId());
            log.warn(message);
            throw new IeoException(ErrorApiTitles.IEO_DETAILS_NOT_FOUND, message);
        }
        BigDecimal availableAmount = ieoDetails.getAvailableAmount();
        if (availableAmount.compareTo(BigDecimal.ZERO) == 0) {
            log.info("{} {} has 0 available balance", ieoDetails.getCurrencyName(), ieoDetails.getCurrencyDescription());
            String text = String.format("Unfortunately, there are no tokens available in IEO %s (%s)", ieoDetails.getCurrencyDescription(), ieoDetails.getCurrencyName());
            notificationMessage.setNotificationType(UserNotificationType.ERROR);
            notificationMessage.setText(text);
            String principalEmail = userService.getUserEmailFromSecurityContext();
            if (StringUtils.isNotEmpty(principalEmail)) {
                try {
                    String payload = objectMapper.writeValueAsString(new WsMessageObject(WsMessageTypeEnum.IEO, notificationMessage));
                    stompMessenger.sendPersonalMessageToUser(principalEmail, payload);
                } catch (JsonProcessingException e) {
                    log.warn("Failed to parse notificationMessage", e);
                }
            } else {
                principalEmail = userService.findEmailById(ieoClaim.getUserId());
            }
            sendMailService.sendInfoMail(prepareEmail(principalEmail, notificationMessage));
            return;
        } else if (availableAmount.compareTo(ieoClaim.getAmount()) < 0) {
            String text = String.format("Token purchase successful! You purchased a maximal available sum: %s %s", availableAmount.toPlainString(), ieoDetails.getCurrencyName());
            notificationMessage.setText(text);
            refactorClaim(availableAmount);
            availableAmount = BigDecimal.ZERO;
        } else if (ieoDetails.getMaxAmountPerUser().compareTo(BigDecimal.ZERO) > 0) {
            Wallet userIeoWallet = walletService.findByUserAndCurrency(ieoClaim.getUserId(), ieoDetails.getCurrencyName());
            if (userIeoWallet != null) {
                BigDecimal availableForUser = ieoDetails.getMaxAmountPerUser().subtract(userIeoWallet.getActiveBalance());
                if (availableForUser.compareTo(ieoClaim.getAmount()) < 0) {
                    String text = String.format("Token purchase successful! You purchased a maximal available sum: %s %s",
                            availableForUser.toPlainString(), ieoDetails.getCurrencyName());
                    notificationMessage.setText(text);
                    refactorClaim(availableForUser);
                    availableAmount = availableAmount.subtract(ieoClaim.getAmount());
                }
            }
            availableAmount = availableAmount.subtract(ieoClaim.getAmount());
        } else {
            availableAmount = availableAmount.subtract(ieoClaim.getAmount());
        }

        IEOResult ieoResult = IEOResult.builder()
                .claimId(ieoClaim.getId())
                .ieoId(ieoClaim.getIeoId())
                .availableAmount(availableAmount)
                .status(IEOResult.IEOResultStatus.SUCCESS)
                .build();
        if (!walletService.performIeoTransfer(ieoClaim)) {
            ieoResult.setStatus(IEOResult.IEOResultStatus.FAILED);
        }
        ieoClaimRepository.updateStatusIEOClaim(ieoClaim.getId(), ieoResult.getStatus());
        ieoResultRepository.save(ieoResult);
        ieoDetailsRepository.updateAvailableAmount(ieoClaim.getIeoId(), availableAmount);
        ieoDetails.setAvailableAmount(availableAmount);
        ieoDetails.setPersonalAmount(walletService.findUserCurrencyBalance(ieoClaim));
        CompletableFuture.runAsync(() -> sendNotifications(ieoClaim.getCreatorEmail(), ieoDetails, notificationMessage));
    }

    private void refactorClaim(BigDecimal newAmount) {
        BigDecimal oldAmount = ieoClaim.getAmount();
        BigDecimal remainingAmount = oldAmount.subtract(newAmount);
        BigDecimal btcRollBack = remainingAmount.multiply(ieoClaim.getRate());
        walletService.rollbackUserBtcForIeo(ieoClaim.getUserId(), btcRollBack);
        ieoClaim.setAmount(newAmount);
        BigDecimal newPriceInBtc = newAmount.multiply(ieoClaim.getRate());
        ieoClaim.setPriceInBtc(newPriceInBtc);
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
            if (StringUtils.isNotEmpty(userEmail)) {
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
