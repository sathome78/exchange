package me.exrates.service.ieo;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableList;
import lombok.extern.log4j.Log4j2;
import me.exrates.dao.IEOClaimRepository;
import me.exrates.dao.IEOSubscribeRepository;
import me.exrates.dao.IeoDetailsRepository;
import me.exrates.dao.KYCSettingsDao;
import me.exrates.model.CurrencyPair;
import me.exrates.model.Email;
import me.exrates.model.IEOClaim;
import me.exrates.model.IEODetails;
import me.exrates.model.User;
import me.exrates.model.Wallet;
import me.exrates.model.constants.ErrorApiTitles;
import me.exrates.model.dto.ieo.ClaimDto;
import me.exrates.model.dto.ieo.IEOStatusInfo;
import me.exrates.model.dto.ieo.IeoDetailsCreateDto;
import me.exrates.model.dto.ieo.IeoDetailsUpdateDto;
import me.exrates.model.dto.kyc.KycCountryDto;
import me.exrates.model.enums.ActionType;
import me.exrates.model.enums.CurrencyPairType;
import me.exrates.model.enums.IEODetailsStatus;
import me.exrates.model.enums.PolicyEnum;
import me.exrates.model.enums.UserRole;
import me.exrates.model.exceptions.IeoException;
import me.exrates.model.util.BigDecimalProcessing;
import me.exrates.service.CurrencyService;
import me.exrates.service.IEOService;
import me.exrates.service.SendMailService;
import me.exrates.service.UserService;
import me.exrates.service.WalletService;
import me.exrates.service.stomp.StompMessenger;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;
import java.util.stream.Collectors;

@EnableRabbit
@Service
@Log4j2
public class IEOServiceImpl implements IEOService {
    private static final Logger logger = LogManager.getLogger(IEOServiceImpl.class);
    private final static String IEO_CLAIM_QUEUE = "ieo_claims";
    private static ExecutorService executor = Executors.newSingleThreadExecutor();

    private final CurrencyService currencyService;
    private final IEOClaimRepository ieoClaimRepository;
    private final UserService userService;
    private final KYCSettingsDao kycSettingsDao;
    private final IeoDetailsRepository ieoDetailsRepository;
    private final WalletService walletService;
    private final SendMailService sendMailService;
    private final StompMessenger stompMessenger;
    private final ObjectMapper objectMapper;
    private final IEOSubscribeRepository ieoSubscribeRepository;
    private final RabbitTemplate rabbitTemplate;
    private final IEOServiceProcessing ieoServiceProcessing;

    @Autowired
    public IEOServiceImpl(IEOClaimRepository ieoClaimRepository,
                          IeoDetailsRepository ieoDetailsRepository,
                          CurrencyService currencyService,
                          UserService userService,
                          WalletService walletService,
                          KYCSettingsDao kycSettingsDao,
                          SendMailService sendMailService,
                          StompMessenger stompMessenger,
                          ObjectMapper objectMapper,
                          IEOSubscribeRepository ieoSubscribeRepository,
                          RabbitTemplate rabbitTemplate,
                          IEOServiceProcessing ieoServiceProcessing) {
        this.ieoClaimRepository = ieoClaimRepository;
        this.userService = userService;
        this.ieoDetailsRepository = ieoDetailsRepository;
        this.currencyService = currencyService;
        this.walletService = walletService;
        this.kycSettingsDao = kycSettingsDao;
        this.sendMailService = sendMailService;
        this.stompMessenger = stompMessenger;
        this.objectMapper = objectMapper;
        this.ieoSubscribeRepository = ieoSubscribeRepository;
        this.rabbitTemplate = rabbitTemplate;
        this.ieoServiceProcessing = ieoServiceProcessing;
    }

    @Override
    public ClaimDto addClaim(ClaimDto claimDto, String email) {
        claimDto.setEmail(email);
        claimDto.setUuid(UUID.randomUUID().toString());
        logger.info("Add claim to queue {}", claimDto.getUuid());

        if (claimDto.isTest()) {
            IEODetails ieoDetails = ieoDetailsRepository.findOpenIeoByCurrencyName(claimDto.getCurrencyName());

            if (ieoDetails == null || !ieoDetails.getTestIeo()) {
                throw new IeoException(ErrorApiTitles.IEO_NOT_FOUND, String.format(
                        "Failed to create claim, IEO for %s not started or already finished", claimDto.getCurrencyName()));
            }
            executor.execute(() -> processTestIeoTask(claimDto, ieoDetails));
            return claimDto;
        }

        rabbitTemplate.convertAndSend(IEO_CLAIM_QUEUE, claimDto);
        return claimDto;
    }

    public void processTestIeoTask(ClaimDto claimDto, IEODetails ieoDetails) {
        processTestIeo(ieoDetails);
        ieoDetails = ieoDetailsRepository.findOne(ieoDetails.getId());
        try {
            if (StringUtils.isNotEmpty(claimDto.getEmail())) {
                stompMessenger.sendPersonalDetailsIeo(claimDto.getEmail(), objectMapper.writeValueAsString(ImmutableList.of(ieoDetails)));
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

    private void processTestIeo(IEODetails ieoDetails) {
        populateTestIeo(ieoDetails);
        boolean filled = true;
        while (filled) {
            List<IEOClaim> claims = ieoClaimRepository.findUnprocessedTestIeoClaimsByIeoId(ieoDetails.getId(), 10);
            if (claims.isEmpty()) {
                filled = false;
            }
            for (IEOClaim claim : claims) {
                ieoServiceProcessing.processIeoClaim(claim, ieoDetails);
            }
            try {
                Thread.sleep(1000L);
            } catch (InterruptedException e) {
            }
        }
    }

    @RabbitListener(queues = IEO_CLAIM_QUEUE, containerFactory = "ieoListenerContainerFactory")
    @Transactional
    public void saveClaim(ClaimDto claimDto) {
        logger.info("Starting save claim uuid {}", claimDto.getUuid());
        String email = claimDto.getEmail();
        IEODetails ieoDetails = ieoDetailsRepository.findOpenIeoByCurrencyName(claimDto.getCurrencyName());
        if (ieoDetails == null) {
            String message = String.format("Failed to create claim %s while IEO for %s not started or already finished",
                    claimDto.getUuid(), claimDto.getCurrencyName());
            logger.warn(message);
            sendErrorEmail(message, claimDto.getEmail());
            return;
        }

        IEOStatusInfo statusInfo = checkUserStatusForIEO(email, ieoDetails.getId());

        if (!statusInfo.isPolicyCheck() || !statusInfo.isCountryCheck() || !statusInfo.isKycCheck()) {
            String message = String.format("Failed to create claim %s, as user KYC status check failed for ieo: %s ",
                    claimDto.getUuid(), claimDto.getCurrencyName());
            logger.warn(message);
            sendErrorEmail(message, claimDto.getEmail());
            return;
        }

        User user = userService.findByEmail(email);
        validateUserAmountRestrictions(ieoDetails, user, claimDto);
        IEOClaim ieoClaim = new IEOClaim(ieoDetails.getId(), claimDto.getCurrencyName(), ieoDetails.getMakerId(), user.getId(), claimDto.getAmount(),
                ieoDetails.getRate(), claimDto.getUuid(), email, false);

        ieoClaim = ieoClaimRepository.save(ieoClaim);

        if (ieoClaim == null) {
            String message = "Failed to save user's claim";
            logger.warn(message);
            sendErrorEmail(message, claimDto.getEmail());
            return;
        }

        logger.info("Added claim {} to IEO processor", claimDto.getUuid());
    }

    @Override
    public IEOStatusInfo checkUserStatusForIEO(String email, int idIeo) {
        User user = userService.findByEmail(email);

        String statusKyc = userService.getUserKycStatusByEmail(email);
        boolean kycCheck = statusKyc.equalsIgnoreCase("SUCCESS");
        boolean checkCountry = false;
        KycCountryDto countryDto = null;
        if (kycCheck) {
            countryDto = kycSettingsDao.getCountryByCode(user.getCountry());
            checkCountry = !ieoDetailsRepository.isCountryRestrictedByIeoId(idIeo, countryDto.getCountryCode());
        }

        boolean policyCheck = userService.existPolicyByUserIdAndPolicy(user.getId(), PolicyEnum.IEO.getName());
        return new IEOStatusInfo(kycCheck, policyCheck, checkCountry, countryDto);
    }

    @Override
    public Collection<IEODetails> findAll(User user) {
        updateIeoStatusesForAll();
        if (Objects.isNull(user)) {
            return ieoDetailsRepository.findAll();
        } else if (user.getRole() == UserRole.ICO_MARKET_MAKER) {
            return prepareMarketMakerIeos(user);
        }
        Map<String, String> userCurrencyBalances = walletService.findUserCurrencyBalances(user);
        Collection<IEODetails> details = ieoDetailsRepository.findAll();
        details.forEach(item -> {
            if (userCurrencyBalances.containsKey(item.getCurrencyName())) {
                item.setPersonalAmount(new BigDecimal(userCurrencyBalances.get(item.getCurrencyName())));
            } else {
                item.setPersonalAmount(BigDecimal.ZERO);
            }
            if (item.getStatus() == IEODetailsStatus.RUNNING) {
                IEOStatusInfo statusInfo = checkUserStatusForIEO(user.getEmail(), item.getId());
                item.setReadyToIeo(statusInfo.isKycCheck() && statusInfo.isCountryCheck() && statusInfo.isPolicyCheck());
            }
        });
        return details;
    }

    @Override
    public IEODetails findOne(int ieoId) {
        updateIeoStatusesForAll();
        return ieoDetailsRepository.findOne(ieoId);
    }

    @Override
    @Transactional
    public void createIeo(IeoDetailsCreateDto dto) {
        int makerId = userService.getIdByEmail(dto.getMakerEmail());
        int creatorId = userService.getIdByEmail(SecurityContextHolder.getContext().getAuthentication().getName());
        currencyService.addCurrencyForIco(dto.getCurrencyName(), dto.getDescription());
        currencyService.addCurrencyPairForIco(dto.getCurrencyName(), "BTC");
        ieoDetailsRepository.save(dto.toIEODetails(makerId, creatorId));
    }

    @Override
    @Transactional
    public void updateIeo(Integer id, IeoDetailsUpdateDto dto) {
        ieoDetailsRepository.updateSafe(dto.toIEODetails(id));
    }

    @Override
    public void startRevertIEO(Integer idIeo, String adminEmail) {
        logger.info("Start revert IEO id {}, email {}", idIeo, adminEmail);
        User user = userService.findByEmail(adminEmail);
        if (user.getRole() != UserRole.ADMIN_USER) {
            String message = String.format("Error while start revert IEO, user not ADMIN %s", adminEmail);
            logger.warn(message);
            throw new IeoException(ErrorApiTitles.IEO_USER_NOT_ADMIN, message);
        }
        IEODetails ieoEntity = findOne(idIeo);

        if (ieoEntity.getStatus() == IEODetailsStatus.FAILED) {
            String message = String.format("Error while start revert IEO, already FAIL, IEO %s",
                    ieoEntity.getCurrencyName());
            logger.warn(message);
            throw new IeoException(ErrorApiTitles.IEO_ALREADY_PROCESSED, message);
        }

        ieoEntity.setStatus(IEODetailsStatus.PROCESSING_FAIL);
        ieoDetailsRepository.update(ieoEntity);

        consumeClaimByPartition(idIeo, walletService::performIeoRollbackTransfer);

        ieoEntity.setStatus(IEODetailsStatus.FAILED);
        ieoDetailsRepository.update(ieoEntity);

        User maker = userService.getUserById(ieoEntity.getMakerId());
        maker.setRole(UserRole.USER);
        userService.updateUserRole(maker.getId(), UserRole.USER);

        logger.info("Finished revert IEO id {}, email {}", idIeo, adminEmail);

        Email email = new Email();
        email.setTo(user.getEmail());
        email.setMessage("Revert IEO");
        email.setSubject(String.format("Revert ieo for %s finish successful!", ieoEntity.getCurrencyName()));
        sendMailService.sendInfoMail(email);
    }

    @Override
    public synchronized void updateIeoStatuses() {
        log.info("<<IEO>>: Starting to update IEO statuses ...");
        boolean updateResultToToRunning = ieoDetailsRepository.updateIeoStatusesToRunning();
        boolean updateResultToTerminated = ieoDetailsRepository.updateIeoStatusesToRunning();
        log.info("<<IEO>>: Finished update IEO statuses to running, result: " + updateResultToToRunning);
        log.info("<<IEO>>: Finished update IEO statuses to terminated, result: " + updateResultToTerminated);
        if (updateResultToToRunning || updateResultToTerminated) {
            String userEmail = null;
            try {
                userEmail = userService.getUserEmailFromSecurityContext();
            } catch (Exception e) {
                log.debug("<<IEO>>: Principal email from Security Context not found, but we don't care ");
            }
            log.info("<<IEO>>: Principal email from Security Context: " + userEmail);
            try {
                if (StringUtils.isNotEmpty(userEmail)) {
                    User user = userService.findByEmail(userEmail);
                    log.info("<<IEO>>: Principal id from Security Context: " + (user == null ? "null" : user.getId()));
                    stompMessenger.sendPersonalDetailsIeo(userEmail, objectMapper.writeValueAsString(findAll(user)));
                }
            } catch (Exception e) {
                log.error("<<IEO>>: Failed to send personal messages as ", e);
            }
            Collection<IEODetails> ieoDetails = ieoDetailsRepository.findAll();
            log.info("<<IEO>>: Starting sending all ieo statuses ..... ");
            stompMessenger.sendAllIeos(ieoDetails);
            log.info("<<IEO>>: Finished sending all ieo statuses :) ");
            ieoDetails.forEach(ieoDetail -> {
                try {
                    stompMessenger.sendDetailsIeo(ieoDetail.getId(), objectMapper.writeValueAsString(ieoDetail));
                } catch (Exception e) {
                    log.error("Failed to send all ieo detail for id: " + ieoDetail.getId(), e);
                }
            });
            log.info("<<IEO>>: Finished sending statuses ..... ");
        }
        log.info("<<IEO>>: Exiting IEO statuses to running, result: " + updateResultToToRunning);
        log.info("<<IEO>>: Exiting IEO statuses to terminated, result: " + updateResultToTerminated);
    }

    @Override
    public boolean approveSuccessIeo(int ieoId, String adminEmail) {
        // 1. change currency to main
        // 2. change role for maker to simple user
        logger.info("Start approve to success IEO id {}, email {}", ieoId, adminEmail);
        User user = userService.findByEmail(adminEmail);
        if (user.getRole() != UserRole.ADMIN_USER) {
            String message = String.format("Error while start revert IEO, user not ADMIN %s", adminEmail);
            logger.warn(message);
            throw new IeoException(ErrorApiTitles.IEO_USER_NOT_ADMIN, message);
        }

        IEODetails ieoDetails = ieoDetailsRepository.findOne(ieoId);
        if (ieoDetails == null) {
            String message = String.format("Failed move to success state IEO %d is NULL",
                    ieoId);
            logger.error(message);
            throw new IeoException(ErrorApiTitles.IEO_NOT_FOUND, message);
        }

        if (ieoDetails.getStatus() != IEODetailsStatus.RUNNING ||
                ieoDetails.getStatus() != IEODetailsStatus.TERMINATED) {
            String message = String.format("Failed move to success state IEO %s, status IEO is %s",
                    ieoDetails.getCurrencyName(),
                    ieoDetails.getStatus());
            logger.error(message);
            throw new IeoException(ErrorApiTitles.IEO_FAILED_MOVE_TO_SUCCESS, message);
        }

        CurrencyPair ieoBtcPair = currencyService.getCurrencyPairByName(ieoDetails.getCurrencyName() + "/" + "BTC");
        if (ieoBtcPair != null) {
            ieoBtcPair.setPairType(CurrencyPairType.MAIN);
            ieoBtcPair.setHidden(false);
            ieoBtcPair.setMarket("BTC");
            currencyService.updateCurrencyPair(ieoBtcPair);
        }

        User maker = userService.getUserById(ieoDetails.getMakerId());
        if (maker.getRole() != UserRole.ICO_MARKET_MAKER) {
            userService.updateUserRole(maker.getId(), UserRole.USER);
        }

        boolean result = walletService.moveBalanceFromIeoReservedToActive(maker.getId(), "BTC");

        if (result) {
            ieoDetails.setStatus(IEODetailsStatus.SUCCEEDED);
            ieoDetailsRepository.updateSafe(ieoDetails);

            Email email = new Email();
            email.setTo(maker.getEmail());
            email.setMessage("Success finish IEO");
            email.setSubject(String.format("The IEO procedure for a currency %s has ended successfully, congratulations!",
                    ieoDetails.getCurrencyName()));
            sendMailService.sendInfoMail(email);
        }

        return result;
    }

    @Override
    public boolean subscribeEmail(String email) {
        if (ieoSubscribeRepository.isUserSubscribe(email)) {
            return ieoSubscribeRepository.updateSubscribeEmail(email);
        } else {
            return ieoSubscribeRepository.subscribeEmail(email);
        }
    }

    @Override
    public boolean subscribeTelegram(String email) {
        if (ieoSubscribeRepository.isUserSubscribe(email)) {
            return ieoSubscribeRepository.updateSubscribeTelegram(email);
        } else {
            return ieoSubscribeRepository.subscribeTelegram(email);
        }
    }

    @Override
    public boolean isUserSubscribeForIEOEmail(String email) {
        return ieoSubscribeRepository.isUserSubscribeForEmail(email);
    }

    @Override
    public boolean isUserSubscribeForIEOTelegram(String email) {
        return ieoSubscribeRepository.isUserSubscribeForTelegram(email);
    }

    private void validateUserAmountRestrictions(IEODetails ieoDetails, User user, ClaimDto claimDto) {
        if (ieoDetails.getMinAmount().compareTo(BigDecimal.ZERO) != 0
                && ieoDetails.getMinAmount().compareTo(claimDto.getAmount()) > 0) {
            String message = String.format("Failed to accept claim as minimal amount to buy is %s %s, but you submitted %s %s",
                    ieoDetails.getMinAmount().toPlainString(), ieoDetails.getCurrencyName(), claimDto.getAmount(), ieoDetails.getCurrencyName());
            logger.warn(message);
            throw new IeoException(ErrorApiTitles.IEO_MIN_AMOUNT_FAILURE, message);
        } else if (ieoDetails.getMaxAmountPerClaim().compareTo(BigDecimal.ZERO) != 0
                && ieoDetails.getMaxAmountPerClaim().compareTo(claimDto.getAmount()) < 0) {
            String message = String.format("Failed to accept claim as maximum amount to buy is %s %s, but you submitted %s %s",
                    ieoDetails.getMaxAmountPerClaim().toPlainString(), ieoDetails.getCurrencyName(), claimDto.getAmount(), ieoDetails.getCurrencyName());
            logger.warn(message);
            throw new IeoException(ErrorApiTitles.IEO_MAX_AMOUNT_FAILURE, message);
        } else if (ieoDetails.getMaxAmountPerUser().compareTo(BigDecimal.ZERO) != 0) {
            Wallet userIeoWallet = walletService.findByUserAndCurrency(user.getId(), ieoDetails.getCurrencyName());
            if (userIeoWallet != null && userIeoWallet.getActiveBalance().compareTo(ieoDetails.getMaxAmountPerUser()) >= 0) {
                String message = String.format("Failed to accept claim as user reached maximum amount per user within IEO is %s %s",
                        ieoDetails.getMaxAmountPerUser().toPlainString(), ieoDetails.getCurrencyName());
                logger.warn(message);
                throw new IeoException(ErrorApiTitles.IEO_MAX_AMOUNT_PER_USER_FAILURE, message);
            }
        }
    }

    @Transactional
    public void consumeClaimByPartition(Integer ieoId, Consumer<IEOClaim> c) {
        Collection<Integer> allIds = ieoClaimRepository.getAllSuccessClaimIdsByIeoId(ieoId);
        int partitionSize = 200;
        List<Integer> accumulator = new ArrayList<>(partitionSize);
        for (Integer each : allIds) {
            accumulator.add(each);
            if (accumulator.size() == partitionSize) {
                List<IEOClaim> claims = ieoClaimRepository.getClaimsByIds(accumulator);
                claims.forEach(c);
                accumulator.clear();
                claims.clear();
            }
        }
        List<IEOClaim> claims = ieoClaimRepository.getClaimsByIds(accumulator);
        claims.forEach(c);
        accumulator.clear();
        claims.clear();
    }

    private Collection<IEODetails> prepareMarketMakerIeos(User user) {
        Collection<IEODetails> makerIeos = ieoDetailsRepository.findAllExceptForMaker(user.getId());
        List<String> currencyNames = makerIeos
                .stream()
                .map(IEODetails::getCurrencyName)
                .collect(Collectors.toList());
        Map<String, Wallet> userWallets = walletService.findAllByUserAndCurrencyNames(user.getId(), currencyNames);
        makerIeos.forEach(ieo -> {
            if (userWallets.containsKey(ieo.getCurrencyName())
                    && userWallets.get(ieo.getCurrencyName()) != null) {
                ieo.setPersonalAmount(userWallets.get(ieo.getCurrencyName()).getIeoReserved());
            } else {
                ieo.setPersonalAmount(BigDecimal.ZERO);
            }
        });
        return makerIeos;
    }


    private void updateIeoStatusesForAll() {
        ieoDetailsRepository.updateIeoStatusesToRunning();
        ieoDetailsRepository.updateIeoStatusesToTerminated();
    }

    private void sendErrorEmail(String message, String email) {
        Email emailError = new Email();
        emailError.setSubject("IEO claim save error");
        emailError.setMessage(message);
        emailError.setTo(email);
        sendMailService.sendInfoMail(emailError);
    }

    private void populateTestIeo(IEODetails ieoDetail) {

        int countTransactions = ieoDetail.getCountTestTransaction();
        BigDecimal availableAmount = ieoDetail.getAvailableAmount();
        BigDecimal averageSumTransaction = BigDecimalProcessing.doAction(availableAmount,
                new BigDecimal(countTransactions),
                ActionType.DEVIDE);
        for (int i = 0; i < countTransactions + 1; i++) {
            IEOClaim ieoClaim = new IEOClaim(ieoDetail.getId(),
                    ieoDetail.getCurrencyName(),
                    ieoDetail.getMakerId(),
                    -2,
                    averageSumTransaction,
                    ieoDetail.getRate(),
                    UUID.randomUUID().toString(),
                    "test_ieo@gmail.com",
                    true);
            ieoClaimRepository.save(ieoClaim);
        }
    }
}
