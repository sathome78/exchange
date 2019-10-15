package me.exrates.service.impl;

import lombok.extern.log4j.Log4j2;
import me.exrates.dao.RefillRequestDao;
import me.exrates.dao.WithdrawRequestDao;
import me.exrates.model.Email;
import me.exrates.model.Merchant;
import me.exrates.model.User;
import me.exrates.model.dto.RefillRequestAcceptDto;
import me.exrates.model.dto.RefillRequestCreateDto;
import me.exrates.model.dto.RefillRequestFlatDto;
import me.exrates.model.dto.UserNotificationMessage;
import me.exrates.model.dto.WithdrawMerchantOperationDto;
import me.exrates.model.dto.WithdrawRequestFlatDto;
import me.exrates.model.dto.merchants.coinpay.CoinPayCreateWithdrawDto;
import me.exrates.model.dto.merchants.coinpay.CoinPayResponseDepositDto;
import me.exrates.model.dto.merchants.coinpay.CoinPayWithdrawRequestDto;
import me.exrates.model.enums.UserNotificationType;
import me.exrates.model.enums.WsSourceTypeEnum;
import me.exrates.service.CoinPayMerchantService;
import me.exrates.service.GtagService;
import me.exrates.service.MerchantService;
import me.exrates.service.RefillService;
import me.exrates.service.SendMailService;
import me.exrates.service.UserService;
import me.exrates.service.WithdrawService;
import me.exrates.service.coinpay.CoinpayApi;
import me.exrates.service.exception.RefillRequestAppropriateNotFoundException;
import me.exrates.service.stomp.StompMessenger;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Properties;
import java.util.UUID;

@Service
@Log4j2(topic = "coin_pay_log")
@PropertySource({"classpath:/merchants/coinpay.properties", "classpath:/angular.properties"})
public class CoinPayMerchantServiceImpl implements CoinPayMerchantService {

    private final CoinpayApi coinpayApi;
    private final MerchantService merchantService;
    private final RefillService refillService;
    private final GtagService gtagService;
    private final UserService userService;
    private final SendMailService sendMailService;
    private final StompMessenger stompMessenger;
    private final WithdrawRequestDao withdrawRequestDao;
    private final RefillRequestDao refillRequestDao;
    private final WithdrawService withdrawService;

    private String serverHost;

    @Autowired
    public CoinPayMerchantServiceImpl(CoinpayApi coinpayApi,
                                      MerchantService merchantService,
                                      RefillService refillService,
                                      GtagService gtagService,
                                      UserService userService,
                                      SendMailService sendMailService,
                                      StompMessenger stompMessenger,
                                      WithdrawRequestDao withdrawRequestDao,
                                      RefillRequestDao refillRequestDao,
                                      WithdrawService withdrawService,
                                      @Value("${server-host}") String serverHost) {
        this.coinpayApi = coinpayApi;
        this.merchantService = merchantService;
        this.refillService = refillService;
        this.gtagService = gtagService;
        this.userService = userService;
        this.sendMailService = sendMailService;
        this.stompMessenger = stompMessenger;
        this.withdrawRequestDao = withdrawRequestDao;
        this.refillRequestDao = refillRequestDao;
        this.withdrawService = withdrawService;
        this.serverHost = serverHost;
    }


    @Override
    public Map<String, String> refill(RefillRequestCreateDto request) {
        log.info("Starting refill {}", request);
        String callBackUrl = serverHost + "/merchants/coinpay/payment/status/" + request.getId();

        String token = coinpayApi.authorizeUser();
        CoinPayResponseDepositDto response = coinpayApi.createDeposit(
                token,
                request.getAmount().toPlainString(),
                request.getCurrencyName(),
                callBackUrl);

        Properties properties = new Properties();
        if (StringUtils.isNoneEmpty(response.getQr())) {
            properties.setProperty("qr", response.getQr());
        }
        return generateFullUrlMap(response.getAddr(), "GET", properties);
    }

    @Override
    public void processPayment(Map<String, String> params) throws RefillRequestAppropriateNotFoundException {
        Merchant merchant = merchantService.findByName("CoinPay");
        int requestId = Integer.parseInt(params.get("id"));

        Optional<RefillRequestFlatDto> flat = refillRequestDao.getFlatById(requestId);
        if (!flat.isPresent()) {
            log.info("Refill request don`t found, id {}", requestId);
            return;
        }

        if (!params.containsKey("status")) {
            log.info("Params not include status, id {}", requestId);
            return;
        }

        if (!params.get("status").equalsIgnoreCase("CLOSED")) {
            log.info("Status is not CLOSED, id {}", requestId);
            refillService.declineMerchantRefillRequest(requestId);
        }

        RefillRequestAcceptDto requestAcceptDto = RefillRequestAcceptDto.builder()
                .requestId(requestId)
                .merchantId(merchant.getId())
                .amount(flat.get().getAmount())
                .address(StringUtils.EMPTY)
                .toMainAccountTransferringConfirmNeeded(this.toMainAccountTransferringConfirmNeeded())
                .merchantTransactionId(params.get("tr_hash"))
                .build();
        log.info("requestAcceptDto {}", requestAcceptDto);
        refillService.acceptRefillRequest(requestAcceptDto);

        final String gaTag = refillService.getUserGAByRequestId(requestId);
        log.info("Process of sending data to Google Analytics...");
        gtagService.sendGtagEvents(flat.get().getAmount().toPlainString(), "UAH", gaTag);
    }

    @Override
    public Map<String, String> withdraw(WithdrawMerchantOperationDto withdrawMerchantOperationDto) throws Exception {
        log.info("Starting withdraw by CoinPay {}", withdrawMerchantOperationDto);
        String token = coinpayApi.authorizeUser();
        String amount = withdrawMerchantOperationDto.getAmount();
        String currencyName = withdrawMerchantOperationDto.getCurrency();
        String uuid = UUID.randomUUID().toString();
        String callBackUrl = serverHost + "/merchants/coinpay/payment/status/withdraw/" + uuid;

        CoinPayCreateWithdrawDto request = CoinPayCreateWithdrawDto.builder()
                .amount(new BigDecimal(amount))
                .currency(currencyName)
                .walletTo(withdrawMerchantOperationDto.getAccountTo())
                .withdrawalType(CoinPayCreateWithdrawDto.WithdrawalType.GATEWAY)
                .callBack(callBackUrl)
                .build();

        log.info("Starting send request to withdraw");
        CoinPayWithdrawRequestDto response = coinpayApi.createWithdrawRequest(token, request);

        Map<String, String> result = new HashMap<>();
        result.put("hash", response.getOrderId());
        result.put("params", uuid);
        return result;
    }

    @Override
    public boolean isValidDestinationAddress(String address) {
        return false;
    }

    @Override
    public void withdrawProcessCallBack(String uuid, Map<String, String> params) {
        Merchant merchant = merchantService.findByName("CoinPay");
        WithdrawRequestFlatDto request = withdrawRequestDao.findByMerchantIdAndAdditionParam(merchant.getId(), uuid);
        if (request == null || !params.containsKey("status")) {
            return;
        }

        if (params.get("status").equalsIgnoreCase("CLOSED")) {
            log.info("withdraw status for request {} CLOSED", request.getId());
            withdrawService.finalizePostWithdrawalRequest(request.getId());
        } else {
            withdrawService.rejectToReview(request.getId());
        }
    }

    public void sendNotification(int userId, String paymentAmount, String currency) {
        User user = userService.getUserById(userId);
        String msg = "Success deposit amount " + paymentAmount + " " + currency + ".";
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

        Properties properties = new Properties();
        properties.setProperty("public_id", user.getPublicId());
        email.setProperties(properties);

        sendMailService.sendMail(email);
    }
}
