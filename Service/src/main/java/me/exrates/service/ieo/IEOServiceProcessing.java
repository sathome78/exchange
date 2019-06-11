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
import me.exrates.model.Wallet;
import me.exrates.model.dto.UserNotificationMessage;
import me.exrates.model.enums.IEODetailsStatus;
import me.exrates.model.enums.UserNotificationType;
import me.exrates.model.enums.WsSourceTypeEnum;
import me.exrates.service.CurrencyService;
import me.exrates.service.SendMailService;
import me.exrates.service.UserService;
import me.exrates.service.WalletService;
import me.exrates.service.stomp.StompMessenger;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

@Service
@Log4j2
public class IEOServiceProcessing {
    private static final Logger logger = LogManager.getLogger(IEOServiceProcessing.class);
    private static final int CHUNK = 10;
    private final WalletService walletService;
    private final IEOResultRepository ieoResultRepository;
    private final IeoDetailsRepository ieoDetailsRepository;
    private final ObjectMapper objectMapper;
    private final SendMailService sendMailService;
    private final StompMessenger stompMessenger;
    private final UserService userService;
    private final IEOClaimRepository ieoClaimRepository;
    private final CurrencyService currencyService;
    private int CURRENCY_BTC_ID;

    @Autowired
    public IEOServiceProcessing(WalletService walletService,
                                IEOResultRepository ieoResultRepository,
                                IeoDetailsRepository ieoDetailsRepository,
                                ObjectMapper objectMapper,
                                SendMailService sendMailService,
                                StompMessenger stompMessenger,
                                UserService userService,
                                IEOClaimRepository ieoClaimRepository,
                                CurrencyService currencyService) {
        this.ieoDetailsRepository = ieoDetailsRepository;
        this.sendMailService = sendMailService;
        this.userService = userService;
        this.walletService = walletService;
        this.ieoResultRepository = ieoResultRepository;
        this.objectMapper = objectMapper;
        this.stompMessenger = stompMessenger;
        this.ieoClaimRepository = ieoClaimRepository;
        this.currencyService = currencyService;
        this.CURRENCY_BTC_ID = currencyService.findByName("BTC").getId();
    }

    @Scheduled(fixedDelay = 1000)
    public void processClaims() {
        Collection<IEODetails> ieos = ieoDetailsRepository.findAllRunningAndAvailableIeo();
        for (IEODetails ieoDetail : ieos) {
            boolean filled = true;
            while (filled) {
                List<IEOClaim> claims = ieoClaimRepository.findUnprocessedIeoClaimsByIeoId(ieoDetail.getId(), CHUNK);
                if (claims.isEmpty()) {
                    filled = false;
                }
                for (IEOClaim claim : claims) {
                    processIeoClaim(claim, ieoDetail);
                }
            }
        }
    }

    @Transactional(rollbackFor = Exception.class)
    public void processIeoClaim(IEOClaim ieoClaim, IEODetails ieoDetails) {
        if (ieoDetails == null) {
            String message = String.format("Failed to find ieo details for id: %d", ieoClaim.getIeoId());
            log.warn(message);
            return;
        }
        logger.info("Starting process ieoClaim {}", ieoClaim.getUuid());
        if (ieoClaim.isTestClaim()) {
            processTestClaim(ieoDetails, ieoClaim);
            return;
        }
        String msg = String.format("Congrats! You successfully purchased %s %s", ieoClaim.getAmount().toPlainString(), ieoClaim.getCurrencyName());
        final UserNotificationMessage notificationMessage = new UserNotificationMessage(WsSourceTypeEnum.IEO, UserNotificationType.SUCCESS, msg);
        String principalEmail = userService.findEmailById(ieoClaim.getUserId());
        BigDecimal amountInBtcLocked = walletService.getAvailableAmountInBtcLocked(ieoClaim.getUserId(), CURRENCY_BTC_ID);

        if (amountInBtcLocked.compareTo(ieoClaim.getPriceInBtc()) < 0) {
            log.info("User active balance less claim amount, active {}, required {}", amountInBtcLocked, ieoClaim.getPriceInBtc());
            String text = String.format("Unfortunately, You doesn't have required amount in BTC for purchase %s %s. Your amount is %s BTC",
                    ieoClaim.getAmount(), ieoDetails.getCurrencyName(), amountInBtcLocked);
            String resultIeoMessage = String.format("Not enough user BTC amount %s", amountInBtcLocked);
            failedClaim(ieoClaim, ieoDetails.getAvailableAmount(), principalEmail, notificationMessage,
                    resultIeoMessage, text, ieoDetails);
            return;
        }

        BigDecimal availableAmount = ieoDetails.getAvailableAmount();
        if (availableAmount.compareTo(BigDecimal.ZERO) == 0) {
            log.info("{} {} has 0 available balance", ieoDetails.getCurrencyName(), ieoDetails.getCurrencyDescription());
            String text = String.format("Unfortunately, there are no tokens available in IEO %s (%s), asked amount %s %s",
                    ieoDetails.getCurrencyDescription(), ieoDetails.getCurrencyName(), ieoClaim.getAmount().toPlainString(),
                    ieoDetails.getCurrencyName());
            String resultIeoMessage = String.format("No tokens available in IEO %s", ieoDetails.getCurrencyName());
            failedClaim(ieoClaim, availableAmount, principalEmail, notificationMessage, resultIeoMessage, text, ieoDetails);
            return;
        } else if (availableAmount.compareTo(ieoClaim.getAmount()) < 0) {
            String text = String.format("Token purchase successful! You purchased a maximal available sum: %s %s",
                    availableAmount.toPlainString(),
                    ieoDetails.getCurrencyName());
            notificationMessage.setText(text);
            refactorClaim(availableAmount, ieoClaim);
            availableAmount = BigDecimal.ZERO;
            ieoDetails.setStatus(IEODetailsStatus.TERMINATED);
        } else if (ieoDetails.getMaxAmountPerUser().compareTo(BigDecimal.ZERO) > 0) {
            Wallet userIeoWallet = walletService.findByUserAndCurrency(ieoClaim.getUserId(), ieoDetails.getCurrencyName());
            if (userIeoWallet != null) {
                BigDecimal availableForUser = ieoDetails.getMaxAmountPerUser().subtract(userIeoWallet.getActiveBalance());
                if (availableForUser.compareTo(ieoClaim.getAmount()) < 0) {
                    String text = String.format("Token purchase successful! You purchased a maximal available sum: %s %s",
                            availableForUser.toPlainString(), ieoDetails.getCurrencyName());
                    notificationMessage.setText(text);
                    refactorClaim(availableForUser, ieoClaim);
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
        ieoDetails.setAvailableAmount(availableAmount);
        ieoDetailsRepository.updateAvailableAmount(ieoDetails.getId(), ieoDetails.getAvailableAmount());
        if (ieoDetails.getAvailableAmount().compareTo(BigDecimal.ZERO) == 0) {
            ieoDetailsRepository.updateIeoDetailStatus(IEODetailsStatus.TERMINATED, ieoDetails.getId());
            ieoDetailsRepository.updateIeoSoldOutTime(ieoDetails.getId());
        }
        BigDecimal amount = walletService.findUserCurrencyBalance(ieoClaim);
        ieoDetails.setPersonalAmount(amount);
        ieoDetails.setReadyToIeo(true);
        Email email = prepareEmail(principalEmail, notificationMessage);
        if (!ieoDetails.getTestIeo()) {
            sendMailService.sendInfoMail(email);
        }
        sendNotifications(principalEmail, ieoDetails, notificationMessage);
    }

    private void failedClaim(IEOClaim ieoClaim, BigDecimal availableAmount, String email,
                             UserNotificationMessage notificationMessage, String resultIEOMessage, String text, IEODetails details) {
        notificationMessage.setNotificationType(UserNotificationType.ERROR);
        notificationMessage.setText(text);
        IEOResult ieoResult = IEOResult.builder()
                .claimId(ieoClaim.getId())
                .ieoId(ieoClaim.getIeoId())
                .availableAmount(availableAmount)
                .status(IEOResult.IEOResultStatus.FAILED)
                .message(resultIEOMessage)
                .build();
        ieoClaimRepository.updateStatusIEOClaim(ieoClaim.getId(), ieoResult.getStatus());
        ieoResultRepository.save(ieoResult);
        if (!details.getTestIeo()) {
            sendMailService.sendInfoMail(prepareEmail(email, notificationMessage));
            try {
                stompMessenger.sendPersonalMessageToUser(email, notificationMessage);
            } catch (Exception e) {
                /*ignore*/
            }
        }

        try {
            if (StringUtils.isNotEmpty(email)) {
                stompMessenger.sendPersonalDetailsIeo(email, objectMapper.writeValueAsString(ImmutableList.of(details)));
            }
        } catch (Exception e) {
            /*ignore*/
        }
        try {
            stompMessenger.sendDetailsIeo(details.getId(), objectMapper.writeValueAsString(details));
        } catch (Exception e) {
            /*ignore*/
        }

        try {
            stompMessenger.sendAllIeos(Collections.singletonList(details));
        } catch (Exception e) {
            /*ignore*/
        }

    }

    private void refactorClaim(BigDecimal newAmount, IEOClaim ieoClaim) {
        ieoClaim.setAmount(newAmount);
        BigDecimal newPriceInBtc = newAmount.multiply(ieoClaim.getRate());
        ieoClaim.setPriceInBtc(newPriceInBtc);
        ieoClaimRepository.updateClaim(ieoClaim);
    }

    private void sendNotifications(String userEmail, IEODetails ieoDetails, UserNotificationMessage message) {
        try {
            if (StringUtils.isNotEmpty(userEmail) && !ieoDetails.getTestIeo()) {
                stompMessenger.sendPersonalMessageToUser(userEmail, message);
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

        try {
            stompMessenger.sendAllIeos(Collections.singletonList(ieoDetails));
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

    private void processTestClaim(IEODetails ieoDetails, IEOClaim ieoClaim) {

        BigDecimal availableAmount = ieoDetails.getAvailableAmount();

        if (availableAmount.compareTo(BigDecimal.ZERO) == 0) {
            logger.info("Available amount for fake processing is ZERO");
            ieoClaimRepository.updateStatusIEOClaim(ieoClaim.getId(), IEOResult.IEOResultStatus.FAILED);
            IEOResult ieoResult = IEOResult.builder()
                    .claimId(ieoClaim.getId())
                    .ieoId(ieoClaim.getIeoId())
                    .availableAmount(availableAmount)
                    .status(IEOResult.IEOResultStatus.FAILED)
                    .message("No available tokens")
                    .build();
            ieoResultRepository.save(ieoResult);
            return;
        }

        if (availableAmount.compareTo(ieoClaim.getAmount()) < 0) {
            refactorClaim(availableAmount, ieoClaim);
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

        ieoClaimRepository.updateStatusIEOClaim(ieoClaim.getId(), ieoResult.getStatus());
        ieoResultRepository.save(ieoResult);
        ieoDetails.setAvailableAmount(availableAmount);
        ieoDetailsRepository.updateAvailableAmount(ieoDetails.getId(), availableAmount);
        if (availableAmount.compareTo(BigDecimal.ZERO) == 0) {
            ieoDetailsRepository.updateIeoDetailStatus(IEODetailsStatus.TERMINATED, ieoDetails.getId());
            ieoDetails.setStatus(IEODetailsStatus.TERMINATED);
            ieoDetailsRepository.updateIeoSoldOutTime(ieoDetails.getId());
        }

        try {
            stompMessenger.sendDetailsIeo(ieoDetails.getId(), objectMapper.writeValueAsString(ieoDetails));
        } catch (Exception e) {
            /*ignore*/
        }

        try {
            stompMessenger.sendAllIeos(Collections.singletonList(ieoDetails));
        } catch (Exception e) {
            /*ignore*/
        }


    }
}
