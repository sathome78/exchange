package me.exrates.service.impl;

import lombok.extern.log4j.Log4j2;
import me.exrates.dao.RefillRequestDao;
import me.exrates.dao.WithdrawRequestDao;
import me.exrates.model.Email;
import me.exrates.model.Merchant;
import me.exrates.model.User;
import me.exrates.model.dto.RefillRequestAcceptDto;
import me.exrates.model.dto.RefillRequestCreateDto;
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
import me.exrates.service.coinpay.CoinpayApi;
import me.exrates.service.exception.RefillRequestAppropriateNotFoundException;
import me.exrates.service.stomp.StompMessenger;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Service
@Log4j2
@PropertySource({"classpath:/merchants/coinpay.properties", "classpath:/angular.properties"})
public class CoinPayMerchantServiceImpl implements CoinPayMerchantService {

    @Value("${coinpay.wallet}")
    private String wallet;

    @Value("${server-host}")
    private String serverHost;

    @Autowired
    private CoinpayApi coinpayApi;

    @Autowired
    private MerchantService merchantService;

    @Autowired
    private RefillService refillService;

    @Autowired
    private GtagService gtagService;

    @Autowired
    private UserService userService;

    @Autowired
    private SendMailService sendMailService;

    @Autowired
    private StompMessenger stompMessenger;

    @Autowired
    private WithdrawRequestDao withdrawRequestDao;

    @Autowired
    private RefillRequestDao refillRequestDao;

    private ScheduledExecutorService newTxCheckerScheduler = Executors.newSingleThreadScheduledExecutor();

    @PostConstruct
    public void checkWithdrawPayments() {
        newTxCheckerScheduler.scheduleAtFixedRate(this::regularyCheckPayments, 10, 60, TimeUnit.MINUTES);
    }

    @Override
    public Map<String, String> refill(RefillRequestCreateDto request) {

        String callBackUrl = serverHost + "/merchant/coinpay/payment/status/" + request.getId();

        String token = coinpayApi.authorizeUser();
        CoinPayResponseDepositDto response = coinpayApi.createDeposit(
                token,
                request.getAmount().toPlainString(),
                request.getCurrencyName(),
                callBackUrl);

        Properties properties = new Properties() {{
            put("qr", response.getQr());
        }};

        return generateFullUrlMap(response.getAddr(), "GET", properties);
    }

    @Override
    public void processPayment(Map<String, String> params) throws RefillRequestAppropriateNotFoundException {
        Merchant merchant = merchantService.findById(Integer.parseInt(params.get("merchantId")));
        int requestId = Integer.parseInt(params.get("id"));

        String paymentAmount = params.getOrDefault("amount", "0");
        RefillRequestAcceptDto requestAcceptDto = RefillRequestAcceptDto.builder()
                .requestId(requestId)
                .merchantId(merchant.getId())
                .amount(new BigDecimal(paymentAmount))
                .address(StringUtils.EMPTY)
                .toMainAccountTransferringConfirmNeeded(this.toMainAccountTransferringConfirmNeeded())
                .build();
        refillService.acceptRefillRequest(requestAcceptDto);

        final String gaTag = refillService.getUserGAByRequestId(requestId);
        log.info("Process of sending data to Google Analytics...");
        gtagService.sendGtagEvents(paymentAmount, "UAH", gaTag);
    }

    @Override
    public Map<String, String> withdraw(WithdrawMerchantOperationDto withdrawMerchantOperationDto) throws Exception {
        log.info("Starting withdraw by CoinPay {}", withdrawMerchantOperationDto);
        String token = coinpayApi.authorizeUser();
        String amount = withdrawMerchantOperationDto.getAmount();
        String currencyName = withdrawMerchantOperationDto.getCurrency();

        CoinPayCreateWithdrawDto request = CoinPayCreateWithdrawDto.builder()
                .amount(new BigDecimal(amount))
                .currency(currencyName)
                .walletTo(withdrawMerchantOperationDto.getDestinationTag())
                .withdrawalType(CoinPayCreateWithdrawDto.WithdrawalType.GATEWAY)
                .build();

        CoinPayWithdrawRequestDto response = coinpayApi.createWithdrawRequest(token, request);

        Map<String, String> result = new HashMap<>();
        result.put("hash", response.getOrderId());
        result.put("params", "PENDING");
        return result;
    }

    @Override
    public boolean isValidDestinationAddress(String address) {
        return false;
    }

    private void regularyCheckPayments() {
        Merchant merchant = merchantService.findByName("CoinPay");
        List<WithdrawRequestFlatDto> pendingRequests = withdrawRequestDao.findByMerchantIdAndAdditionParam(merchant.getId(), "PENDING");
        if (pendingRequests.isEmpty()) {
            return;
        }

        String token = coinpayApi.authorizeUser();
        for (WithdrawRequestFlatDto request : pendingRequests) {
            String orderId = request.getTransactionHash();
            String status = coinpayApi.checkOrderById(token, orderId);
            if (status.equalsIgnoreCase("success")) {
                withdrawRequestDao.updateAdditionalParamById(request.getId(), status);
            }
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
        sendMailService.sendMail(email);
    }
}
