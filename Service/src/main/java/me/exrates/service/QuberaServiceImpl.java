package me.exrates.service;

import com.google.common.collect.Maps;
import me.exrates.dao.QuberaDao;
import me.exrates.model.Currency;
import me.exrates.model.Email;
import me.exrates.model.Merchant;
import me.exrates.model.QuberaUserData;
import me.exrates.model.User;
import me.exrates.model.condition.MonolitConditional;
import me.exrates.model.constants.Constants;
import me.exrates.model.constants.ErrorApiTitles;
import me.exrates.model.dto.AccountQuberaRequestDto;
import me.exrates.model.dto.AccountQuberaResponseDto;
import me.exrates.model.dto.RefillRequestAcceptDto;
import me.exrates.model.dto.RefillRequestCreateDto;
import me.exrates.model.dto.UserNotificationMessage;
import me.exrates.model.dto.WithdrawMerchantOperationDto;
import me.exrates.model.dto.kyc.CreateApplicantDto;
import me.exrates.model.dto.kyc.EventStatus;
import me.exrates.model.dto.kyc.IdentityDataKyc;
import me.exrates.model.dto.kyc.IdentityDataRequest;
import me.exrates.model.dto.kyc.PersonKycDto;
import me.exrates.model.dto.kyc.ResponseCreateApplicantDto;
import me.exrates.model.dto.kyc.request.RequestOnBoardingDto;
import me.exrates.model.dto.kyc.responces.KycStatusResponseDto;
import me.exrates.model.dto.kyc.responces.OnboardingResponseDto;
import me.exrates.model.dto.qubera.AccountInfoDto;
import me.exrates.model.dto.qubera.ExternalPaymentShortDto;
import me.exrates.model.dto.qubera.PaymentRequestDto;
import me.exrates.model.dto.qubera.QuberaPaymentInfoDto;
import me.exrates.model.dto.qubera.QuberaPaymentToMasterDto;
import me.exrates.model.dto.qubera.QuberaRequestDto;
import me.exrates.model.dto.qubera.QuberaRequestPaymentShortDto;
import me.exrates.model.dto.qubera.ResponsePaymentDto;
import me.exrates.model.dto.qubera.responses.ExternalPaymentResponseDto;
import me.exrates.model.enums.UserNotificationType;
import me.exrates.model.enums.WsSourceTypeEnum;
import me.exrates.model.exceptions.KycException;
import me.exrates.model.ngExceptions.NgDashboardException;
import me.exrates.model.ngExceptions.NgRefillException;
import me.exrates.service.exception.RefillRequestAppropriateNotFoundException;
import me.exrates.service.exception.invoice.MerchantException;
import me.exrates.service.kyc.http.KycHttpClient;
import me.exrates.service.stomp.StompMessenger;
import me.exrates.service.util.DateUtils;
import org.apache.commons.lang.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.Date;
import java.util.Map;
import java.util.UUID;

import static me.exrates.model.constants.ErrorApiTitles.KYC_NOT_PROCESSING;

@Service
@PropertySource({"classpath:/merchants/qubera.properties", "classpath:/angular.properties"})
@Conditional(MonolitConditional.class)
public class QuberaServiceImpl implements QuberaService {
    private static final Logger logger = LogManager.getLogger(QuberaServiceImpl.class);

    private final CurrencyService currencyService;
    private final GtagService gtagService;
    private final MerchantService merchantService;
    private final RefillService refillService;
    private final QuberaDao quberaDao;
    private final KycHttpClient kycHttpClient;
    private final UserService userService;
    private final StompMessenger stompMessenger;
    private final SendMailService sendMailService;
//    private final KYCService kycService;

    private @Value("${qubera.threshold.length}")
    int thresholdLength;
    private @Value("${qubera.poolId}")
    int poolId;
    private @Value("${qubera.master.account}")
    String masterAccount;

    private @Value("${qubera.bic}")
    String bic;

    private @Value("${qubera.bank_name}")
    String bankName;

    private @Value("${qubera.swift_code}")
    String swiftCode;

    private @Value("${qubera.country}")
    String country;

    private @Value("${qubera.city}")
    String city;

    private @Value("${qubera.country}")
    String address;

    @Value("${server-host}")
    private String host;

    @Autowired
    public QuberaServiceImpl(CurrencyService currencyService,
                             GtagService gtagService,
                             MerchantService merchantService,
                             RefillService refillService,
                             QuberaDao quberaDao,
                             KycHttpClient kycHttpClient,
                             UserService userService,
                             StompMessenger stompMessenger,
                             SendMailService sendMailService) {
        this.currencyService = currencyService;
        this.gtagService = gtagService;
        this.merchantService = merchantService;
        this.refillService = refillService;
        this.quberaDao = quberaDao;
        this.kycHttpClient = kycHttpClient;
        this.userService = userService;
        this.stompMessenger = stompMessenger;
        this.sendMailService = sendMailService;
    }

    @Override
    public Map<String, String> refill(RefillRequestCreateDto request) {
        logger.info(String.format("qubera refill email %s, amount %s ", request.getUserEmail(),
                request.getAmount().toPlainString()));
        PaymentRequestDto paymentRequestDto = new PaymentRequestDto(request.getAmount(), request.getCurrencyName());
        //create request to make payment to master account
        ResponsePaymentDto paymentToMaster = createPaymentToMaster(request.getUserEmail(), paymentRequestDto);
        logger.info(String.format("Success create payment, email %s, paymentId %s, amount %s", request.getUserEmail(),
                paymentToMaster.getPaymentId().toString(), paymentToMaster.getTransactionAmount().toPlainString()));

        Map<String, String> details = quberaDao.getUserDetailsForCurrency(request.getUserId(), request.getCurrencyId());
        Map<String, String> refillParams = Maps.newHashMap();
        String iban = details.getOrDefault("iban", "");
        String accountNumber = details.getOrDefault("accountNumber", "");
        refillParams.put("iban", iban);
        refillParams.put("currency", request.getCurrencyName());
        refillParams.put("accountNumber", accountNumber);
        refillParams.put("paymentAmount", paymentToMaster.getTransactionAmount().toPlainString());
        refillParams.put("paymentId", paymentToMaster.getPaymentId().toString());

        if (confirmPaymentToMaster(paymentToMaster.getPaymentId())) {
            logger.info(String.format("Confirm payment, paymentId %s, amount %s", paymentToMaster.getPaymentId(),
                    paymentToMaster.getTransactionAmount().toPlainString()));
            try {
                processPayment(refillParams);
            } catch (RefillRequestAppropriateNotFoundException e) {
                logger.error("Some exception happens " + e.getMessage());
            }
        } else {
            logger.error("Payment not confirmed {}" + paymentToMaster.getPaymentId());
            throw new NgRefillException("Payment not confirmed {}" + paymentToMaster.getPaymentId());
        }
        return refillParams;
    }

    @Override
    public void processPayment(Map<String, String> params) throws RefillRequestAppropriateNotFoundException {
        Currency currency = currencyService.findByName(params.get("currency"));
        Merchant merchant = merchantService.findByName("Qubera");
        int userId = quberaDao.findUserIdByAccountNumber(params.get("accountNumber"));

        String paymentAmount = params.getOrDefault("paymentAmount", "0");
        RefillRequestAcceptDto requestAcceptDto = RefillRequestAcceptDto.builder()
                .requestId(0)
                .merchantId(merchant.getId())
                .currencyId(currency.getId())
                .amount(new BigDecimal(paymentAmount))
                .address(StringUtils.EMPTY)
                .merchantTransactionId(params.get("paymentId"))
                .toMainAccountTransferringConfirmNeeded(this.toMainAccountTransferringConfirmNeeded())
                .build();
        Integer requestId = refillService.createAndAutoAcceptRefillRequest(requestAcceptDto, userId);
        params.put("request_id", requestId.toString());
        sendNotification(userId, paymentAmount);

        final String gaTag = refillService.getUserGAByRequestId(requestId);
        logger.info("Process of sending data to Google Analytics...");
        gtagService.sendGtagEvents(paymentAmount, currency.getName(), gaTag);
    }

    @Override
    public Map<String, String> withdraw(WithdrawMerchantOperationDto withdrawMerchantOperationDto) throws Exception {
        String currencyName = withdrawMerchantOperationDto.getCurrency();
        Currency currency = currencyService.findByName(currencyName);
        QuberaUserData quberaUserData = quberaDao.getUserDataByUserIdAndCurrencyId(withdrawMerchantOperationDto.getUserId(), currency.getId());
        logger.info("withdraw qubera service email " + quberaUserData.getEmail() + " , amount " + withdrawMerchantOperationDto.getAmount());
        QuberaPaymentToMasterDto paymentToMasterDto = QuberaPaymentToMasterDto.builder()
                .accountNumber(quberaUserData.getAccountNumber())
                .beneficiaryAccountNumber(masterAccount)
                .amount(new BigDecimal(withdrawMerchantOperationDto.getAmount()))
                .currencyCode(withdrawMerchantOperationDto.getCurrency())
                .narrative("Payment from master")
                .build();

        ResponsePaymentDto responsePaymentDto = kycHttpClient.createPaymentInternal(paymentToMasterDto, false);
        logger.info("withdraw create payment internal success, amount - " + responsePaymentDto.getTransactionAmount().toPlainString()
                + ", transaction " + responsePaymentDto.getTransactionCurrencyCode());
        if (!kycHttpClient.confirmInternalPayment(responsePaymentDto.getPaymentId(), true)) {
            logger.info("Fail confirm payment " + responsePaymentDto.getTransactionCurrencyCode());
            throw new MerchantException("Payment not confirmed");
        }
        return Collections.emptyMap();
    }

    @Override
    public boolean isValidDestinationAddress(String address) {
        return false;
    }

    @Override
    public boolean logResponse(QuberaRequestDto requestDto) {
        return quberaDao.logResponse(requestDto);
        //todo send email
    }

    @Override
    public AccountQuberaResponseDto createAccount(String email) {
        logger.info("createAccount(), {}" + email);
        QuberaUserData userData = quberaDao.getUserDataByUserEmail(email);

        if (userData != null) {
            if (userData.getIban() != null && userData.getAccountNumber() != null) {
                return new AccountQuberaResponseDto(userData.getAccountNumber(), userData.getIban());
            }
        }

        if (!userData.getBankVerificationStatus().equalsIgnoreCase("success")) {
            throw new NgDashboardException(ErrorApiTitles.KYC_NOT_PROCESSING);
        }

        String account = userData.buildAccountString();
        if (account.length() >= thresholdLength) {
            String error = "Count chars of request is over limit {}" + account.length();
            logger.error(error);
            throw new NgDashboardException(error, Constants.ErrorApi.QUBERA_PARAMS_OVER_LIMIT);
        }

        AccountQuberaRequestDto requestCreateAccountDto = new AccountQuberaRequestDto(account, "EUR", poolId);
        AccountQuberaResponseDto responseCreateAccountDto = kycHttpClient.createAccount(requestCreateAccountDto);
        logger.info("Response from create account service success, iban {} + " + responseCreateAccountDto.getIban() + ", number "
                + responseCreateAccountDto.getAccountNumber());
        userData.setIban(responseCreateAccountDto.getIban());
        userData.setAccountNumber(responseCreateAccountDto.getAccountNumber());

        boolean updateUserData = quberaDao.updateUserData(userData);

        if (updateUserData) {
            return new AccountQuberaResponseDto(userData.getAccountNumber(), userData.getIban());
        } else {
            logger.error("Error saving qubera user details " + email);
            throw new NgDashboardException(KYC_NOT_PROCESSING);
        }
    }

    @Override
    public boolean checkAccountExist(String email, String currencyName) {
        return quberaDao.existAccountByUserEmailAndCurrencyName(email, currencyName);
    }

    @Override
    public AccountInfoDto getInfoAccount(String email) {
        String account = quberaDao.getAccountByUserEmail(email);
        if (account == null) {
            logger.error("Account not found " + email);
            throw new NgDashboardException("Account not found " + email,
                    Constants.ErrorApi.QUBERA_ACCOUNT_NOT_FOUND_ERROR);
        }
        return kycHttpClient.getBalanceAccount(account);
    }

    @Override
    public ResponsePaymentDto createPaymentToMaster(String email, PaymentRequestDto paymentRequestDto) {
        String account = quberaDao.getAccountByUserEmail(email);

        if (account == null) {
            logger.error("Account not found " + email);
            throw new NgDashboardException("Account not found " + email,
                    Constants.ErrorApi.QUBERA_ACCOUNT_NOT_FOUND_ERROR);
        }

        QuberaPaymentToMasterDto paymentToMasterDto = new QuberaPaymentToMasterDto();
        paymentToMasterDto.setAmount(paymentRequestDto.getAmount());
        paymentToMasterDto.setAccountNumber(account);
        paymentToMasterDto.setCurrencyCode(paymentRequestDto.getCurrencyCode());
        paymentToMasterDto.setNarrative("Inner transfer");
        return kycHttpClient.createPaymentInternal(paymentToMasterDto, true);
    }

    @Override
    public ResponsePaymentDto createPaymentFromMater(String email, PaymentRequestDto paymentRequestDto) {
        String account = quberaDao.getAccountByUserEmail(email);

        if (account == null) {
            logger.error("Account not found " + email);
            throw new NgDashboardException("Account not found " + email,
                    Constants.ErrorApi.QUBERA_ACCOUNT_NOT_FOUND_ERROR);
        }

        QuberaPaymentToMasterDto paymentToMasterDto = new QuberaPaymentToMasterDto();
        paymentToMasterDto.setAmount(paymentRequestDto.getAmount());
        paymentToMasterDto.setSenderAccountNumber(account);
        paymentToMasterDto.setBeneficiaryAccountNumber(masterAccount);
        paymentToMasterDto.setCurrencyCode(paymentRequestDto.getCurrencyCode());
        paymentToMasterDto.setNarrative("Inner transfer");
        return kycHttpClient.createPaymentInternal(paymentToMasterDto, false);
    }

    @Override
    public boolean confirmPaymentToMaster(Integer paymentId) {
        return kycHttpClient.confirmInternalPayment(paymentId, true);
    }

    @Override
    public boolean confirmPaymentFRomMaster(Integer paymentId) {
        return kycHttpClient.confirmInternalPayment(paymentId, false);
    }

    @Override
    public ExternalPaymentResponseDto createExternalPayment(ExternalPaymentShortDto externalPaymentDto, String email) {
        String account = quberaDao.getAccountByUserEmail(email);
        if (account == null) {
            logger.error("Account not found " + email);
            throw new NgDashboardException("Account not found " + email,
                    Constants.ErrorApi.QUBERA_ACCOUNT_NOT_FOUND_ERROR);
        }
        AccountInfoDto balanceAccount = kycHttpClient.getBalanceAccount(account);

        if (balanceAccount.getAvailableBalance().getAmount()
                .compareTo(new BigDecimal(externalPaymentDto.getAmount())) < 0) {
            String messageError = "Not enough money for current payment " +
                    externalPaymentDto.getAmount()
                    + " available balance " +
                    balanceAccount.getAvailableBalance().getAmount().toPlainString();
            logger.error(messageError);
            throw new NgDashboardException(messageError, Constants.ErrorApi.QUBERA_NOT_ENOUGH_MONEY_FOR_PAYMENT);
        }

        QuberaRequestPaymentShortDto request = QuberaRequestPaymentShortDto.of(externalPaymentDto, account);;

        return kycHttpClient.createExternalPayment(request);
    }

    @Override
    public QuberaPaymentInfoDto getInfoForPayment(String email) {
        QuberaUserData userData = quberaDao.getUserDataByUserEmail(email);
        if (userData == null) {
            return null;
        }
        return QuberaPaymentInfoDto.builder()
                .bic(bic)
                .bankName(bankName)
                .swiftCode(swiftCode)
                .address(address)
                .country(country)
                .city(city)
                .iban(userData.getIban())
                .build();
    }

    @Override
    public void sendNotification(QuberaRequestDto quberaRequestDto) {
        Integer userId = quberaDao.findUserIdByAccountNumber(quberaRequestDto.getAccountNumber());
        User user = userService.getUserById(userId);
        String msg;
        UserNotificationMessage message;
        if (quberaRequestDto.getState().equalsIgnoreCase("Rejected")) {
            msg = "Your payment was rejected, reason " + quberaRequestDto.getRejectionReason();
            message = new UserNotificationMessage(WsSourceTypeEnum.FIAT, UserNotificationType.ERROR, msg);
        } else {
            msg = "Your payment was confirmed, amount " + quberaRequestDto.getPaymentAmount().toPlainString() + " " + quberaRequestDto.getCurrency();
            message = new UserNotificationMessage(WsSourceTypeEnum.FIAT, UserNotificationType.SUCCESS, msg);
        }
        try {
            stompMessenger.sendPersonalMessageToUser(user.getEmail(), message);
        } catch (Exception e) {
        }

        Email email = new Email();
        email.setTo(user.getEmail());
        email.setSubject("Deposit for your bank account");
        email.setMessage(msg);
        sendMailService.sendInfoMail(email);
    }

    @Override
    public String getUserVerificationStatus(String email) {
        QuberaUserData quberaUserData = quberaDao.getUserDataByUserEmail(email);
        return quberaUserData == null ? "none" : quberaUserData.getBankVerificationStatus();
    }

    @Override
    public void processingCallBack(String referenceId, KycStatusResponseDto kycStatusResponseDto) {
        QuberaUserData quberaUserData = quberaDao.getUserDataByReference(referenceId);
        User user = userService.getUserById(quberaUserData.getUserId());
        quberaUserData.setBankVerificationStatus(kycStatusResponseDto.getStatus());
        quberaDao.updateUserData(quberaUserData);
        sendPersonalMessage(kycStatusResponseDto, user);
        String eventStatus = kycStatusResponseDto.getStatus();
        logger.info(String.format("SEND TO EMAIL %s, STATUS %s", user.getEmail(), eventStatus));

        Email email = Email.builder()
                .to(user.getEmail())
                .subject("Notification of bank verification process")
                .message(String.format("Dear user, your current bank verification status is %s", eventStatus))
                .build();

        sendMailService.sendMailMandrill(email);
        UserNotificationType type;
        String msg = String.format("Dear user, your current bank verification status is %s", eventStatus);
        if (eventStatus.equalsIgnoreCase("SUCCESS")
                || eventStatus.equalsIgnoreCase(EventStatus.ACCEPTED.name())) {
            type = UserNotificationType.SUCCESS;
        } else {
            type = UserNotificationType.ERROR;
        }
        final UserNotificationMessage message = new UserNotificationMessage(WsSourceTypeEnum.KYC, type, msg);
        stompMessenger.sendPersonalMessageToUser(user.getEmail(), message);
    }

    @Override
    public OnboardingResponseDto startVerificationProcessing(IdentityDataRequest identityDataRequest, String email) {
        User user = userService.findByEmail(email);
        Currency currency = currencyService.findByName("EUR");
        QuberaUserData userData = quberaDao.getUserDataByUserEmail(email);
        String uuid = UUID.randomUUID().toString();

        if (userData != null) {
            userData.setReference(uuid);
        } else {
            Date dateOfBirth = DateUtils.getDateFromStringForKyc(identityDataRequest.getBirthYear(), identityDataRequest.getBirthMonth(),
                    identityDataRequest.getBirthDay());
            userData = QuberaUserData.builder()
                    .currencyId(currency.getId())
                    .userId(user.getId())
                    .firsName(identityDataRequest.getFirstName())
                    .lastName(identityDataRequest.getLastName())
                    .address(identityDataRequest.getAddress())
                    .city(identityDataRequest.getCity())
                    .countryCode(identityDataRequest.getCountryCode())
                    .birthDay(dateOfBirth)
                    .reference(uuid)
                    .build();
            if (!quberaDao.saveUserDetails(userData)) {
                throw new NgDashboardException(ErrorApiTitles.QUBERA_USER_DATA_NOT_SAVED);
            }
        }

        PersonKycDto personKycDto = new PersonKycDto(Collections.singletonList(IdentityDataKyc.of(identityDataRequest)));
        CreateApplicantDto createApplicantDto = new CreateApplicantDto(uuid, personKycDto);
        ResponseCreateApplicantDto response = kycHttpClient.createApplicant(createApplicantDto);
        if (!response.getState().equalsIgnoreCase("INITIAL")) {
            throw new KycException("Error while start processing KYC, state " + response.getState()
                    + " uid " + response.getUid() + " lastReportStatus " + response.getLastReportStatus());
        }
        String docId = RandomStringUtils.random(18, true, false);
        String callBackUrl = String.format("%s/api/public/v2/kyc/webhook/%s", host, uuid);
        RequestOnBoardingDto onBoardingDto = RequestOnBoardingDto.createOfParams(callBackUrl, email, uuid, docId);
        logger.info("Sending to create applicant " + onBoardingDto);
        OnboardingResponseDto onBoarding = kycHttpClient.createOnBoarding(onBoardingDto);
        userData.setBankVerificationStatus("Pending");
        quberaDao.updateUserData(userData);
        return onBoarding;
    }

    @Override
    public boolean confirmExternalPayment(Integer paymentId) {
        if (kycHttpClient.confirmExternalPayment(paymentId)) {
            return true;
        }
        throw new NgDashboardException(ErrorApiTitles.QUBERA_PAYMENT_NOT_CONFIFM);
    }

    public void sendNotification(int userId, String paymentAmount) {
        User user = userService.getUserById(userId);
        String msg = "Success deposit amount " + paymentAmount + " EUR.";
        UserNotificationMessage message =
                new UserNotificationMessage(WsSourceTypeEnum.FIAT, UserNotificationType.SUCCESS, msg);
        try {
            stompMessenger.sendPersonalMessageToUser(user.getEmail(), message);
        } catch (Exception e) {
        }

        Email email = new Email();
        email.setTo(user.getEmail());
        email.setSubject("Deposit fiat");
        email.setMessage(msg);
        sendMailService.sendInfoMail(email);
    }

    private void sendPersonalMessage(KycStatusResponseDto kycStatusResponseDto, User user) {
        UserNotificationMessage message = UserNotificationMessage.builder()
                .notificationType(UserNotificationType.SUCCESS)
                .sourceTypeEnum(WsSourceTypeEnum.KYC)
                .text("Dear user, your current verification status is SUCCESS")
                .build();
        if (StringUtils.isNotEmpty(kycStatusResponseDto.getErrorMsg())) {
            message.setNotificationType(UserNotificationType.WARNING);
            String text = "Dear user, your verification seems to fail as " + kycStatusResponseDto.getErrorMsg();
            message.setText(text);
        }
        stompMessenger.sendPersonalMessageToUser(user.getEmail(), message);
    }
}
