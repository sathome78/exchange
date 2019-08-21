package me.exrates.service;

import lombok.extern.log4j.Log4j2;
import me.exrates.dao.RefillRequestDao;
import me.exrates.model.Currency;
import me.exrates.model.Email;
import me.exrates.model.Merchant;
import me.exrates.model.User;
import me.exrates.model.condition.MonolitConditional;
import me.exrates.model.dto.RefillRequestAcceptDto;
import me.exrates.model.dto.RefillRequestCreateDto;
import me.exrates.model.dto.RefillRequestFlatDto;
import me.exrates.model.dto.UserNotificationMessage;
import me.exrates.model.dto.WithdrawMerchantOperationDto;
import me.exrates.model.dto.merchants.adgroup.AdGroupCommonRequestDto;
import me.exrates.model.dto.merchants.adgroup.AdGroupFetchTxDto;
import me.exrates.model.dto.merchants.adgroup.AdGroupRequestPayOutDto;
import me.exrates.model.dto.merchants.adgroup.AdGroupRequestRefillBodyDto;
import me.exrates.model.dto.merchants.adgroup.CommonAdGroupHeaderDto;
import me.exrates.model.dto.merchants.adgroup.enums.TxStatus;
import me.exrates.model.dto.merchants.adgroup.responses.AdGroupResponseDto;
import me.exrates.model.dto.merchants.adgroup.responses.InvoiceDto;
import me.exrates.model.dto.merchants.adgroup.responses.ResponseListTxDto;
import me.exrates.model.dto.merchants.adgroup.responses.ResponsePayOutDto;
import me.exrates.model.enums.UserNotificationType;
import me.exrates.model.enums.WsSourceTypeEnum;
import me.exrates.service.exception.RefillRequestAppropriateNotFoundException;
import me.exrates.service.exception.invoice.MerchantException;
import me.exrates.service.http.AdGroupHttpClient;
import me.exrates.service.stomp.StompMessenger;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.math.BigDecimal;
import java.util.Base64;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
@PropertySource({"classpath:/merchants/ad_group.properties"})
@Log4j2(topic = "adgroup_log")
@Conditional(MonolitConditional.class)
public class AdgroupServiceImpl implements AdgroupService {

    @Autowired
    private AdGroupHttpClient httpClient;
    @Autowired
    private CurrencyService currencyService;
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
    private RefillRequestDao refillRequestDao;

    @Value("${base_url}")
    private String url;
    @Value("${client_id}")
    private String clientId;
    @Value("${client_secret}")
    private String clientSecret;
    @Value("${walllet}")
    private String wallet;
    @Value("${pin}")
    private String pin;

    private ScheduledExecutorService newTxCheckerScheduler = Executors.newSingleThreadScheduledExecutor();

    @PostConstruct
    void startAdgroup() {
        newTxCheckerScheduler.scheduleAtFixedRate(this::regularlyCheckStatusTransactions, 10, 60, TimeUnit.MINUTES);
    }

    @Override
    public Map<String, String> refill(RefillRequestCreateDto request) {
        log.info("Starting refill {}", request);

        Merchant merchant = merchantService.findById(request.getMerchantId());
        String paymentMethod = merchant.getName().equalsIgnoreCase("Adgroup_Wallet") ? "PC" : "AC";

        CommonAdGroupHeaderDto header = new CommonAdGroupHeaderDto("p2pInvoiceRequest", 0.1);
        AdGroupRequestRefillBodyDto reqBody = AdGroupRequestRefillBodyDto.builder()
                .amount(request.getAmount())
                .currency(request.getCurrencyName())
                .platform("YANDEX")
                .tel(Integer.valueOf(wallet))
                .paymentMethod(paymentMethod)
                .build();

        AdGroupCommonRequestDto requestDto = new AdGroupCommonRequestDto<>(header, reqBody);
        final String urlRequest = url + "/transfer/tx-merchant-wallet";

        AdGroupResponseDto<InvoiceDto> response = httpClient.createInvoice(urlRequest, getAuthorizationKey(), requestDto);
        log.info("Response refill {}", response);

        refillRequestDao.setRemarkAndTransactionIdById("PENDING",
                response.getResponseData().getId(),
                request.getId());

        String link = response.getResponseData().getPaymentLink();
        return generateFullUrlMap(link, "GET", new Properties());
    }

    @Override
    public void processPayment(Map<String, String> params) throws RefillRequestAppropriateNotFoundException {
        Currency currency = currencyService.findByName(params.get("currency"));
        Merchant merchant = merchantService.findById(Integer.parseInt(params.get("merchantId")));
        int userId = Integer.parseInt(params.get("userId"));

        String paymentAmount = params.getOrDefault("amount", "0");
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
        sendNotification(userId, paymentAmount, currency.getName());

        refillRequestDao.setRemarkById(requestId, "SUCCESS");
        final String gaTag = refillService.getUserGAByRequestId(requestId);
        log.info("Process of sending data to Google Analytics...");
        gtagService.sendGtagEvents(paymentAmount, currency.getName(), gaTag);
    }

    @Override
    public Map<String, String> withdraw(WithdrawMerchantOperationDto withdrawMerchantOperationDto) throws Exception {
        CommonAdGroupHeaderDto header = new CommonAdGroupHeaderDto("YandexPayout", 0.1);
        log.info("Starting withdraw {}", withdrawMerchantOperationDto);
        AdGroupRequestPayOutDto requestPayOutDto = AdGroupRequestPayOutDto.builder()
                .amount(new BigDecimal(withdrawMerchantOperationDto.getAmount()))
                .currency(withdrawMerchantOperationDto.getCurrency())
                .pin(pin)
                .platform("YANDEX")
                .address(withdrawMerchantOperationDto.getDestinationTag())
                .build();

        AdGroupCommonRequestDto requestDto = new AdGroupCommonRequestDto<>(header, requestPayOutDto);
        String urlRequest = url + "/transfer/send-wallet-external";
        AdGroupResponseDto<ResponsePayOutDto> responseDto =
                httpClient.createPayOut(urlRequest, getAuthorizationKey(), requestDto);
        log.info("Response from adgroup {}", responseDto);
        if (!responseDto.getResponseData().getStatus().equalsIgnoreCase("APPROVED")) {
            throw new MerchantException("Not approved");
        }
        return Collections.emptyMap();
    }

    @Override
    public boolean isValidDestinationAddress(String address) {
        return false;
    }

    private String getAuthorizationKey() {
        String forEncode = clientId + ":" + clientSecret;
        return Base64.getEncoder().encodeToString(forEncode.getBytes());
    }

    public void regularlyCheckStatusTransactions() {
        Merchant merchantWallet = merchantService.findByName("Adgroup_Wallet");
        Merchant merchantPaymentCard = merchantService.findByName("Adgroup_PaymentCard");
        List<RefillRequestFlatDto> pendingTx = refillRequestDao.getByMerchantIdAndRemark(merchantWallet.getId(), "PENDING");
        List<RefillRequestFlatDto> pendingTxPaymentCard = refillRequestDao.getByMerchantIdAndRemark(merchantPaymentCard.getId(), "PENDING");
        pendingTx.addAll(pendingTxPaymentCard);

        if (pendingTx.isEmpty()) {
            return;
        }
        log.info("Staring check transactions {}", pendingTx);
        final String requestUrl = url + "/transfer/get-merchant-tx";
        List<String> txStrings = pendingTx.stream().map(RefillRequestFlatDto::getMerchantTransactionId).collect(Collectors.toList());

        CommonAdGroupHeaderDto header = new CommonAdGroupHeaderDto("fetchMerchTx", 0.1);
        AdGroupFetchTxDto requestBody = AdGroupFetchTxDto.builder()
                .start(0)
                .limit(pendingTx.size())
                .txStatus(new String[]{"PENDING", "APPROVED", "REJECTED", "CREATED"})
                .refId(txStrings.toArray(new String[0]))
                .build();

        AdGroupCommonRequestDto requestDto = new AdGroupCommonRequestDto<>(header, requestBody);
        AdGroupResponseDto<ResponseListTxDto> responseDto =
                httpClient.getTransactions(requestUrl, getAuthorizationKey(), requestDto);

        log.info("Response from adgroup {}", responseDto);
        for (RefillRequestFlatDto transaction : pendingTx) {
            responseDto.getResponseData().getTransactions()
                    .stream()
                    .filter(tx -> tx.getRefid().equalsIgnoreCase(transaction.getMerchantTransactionId()))
                    .peek(tx -> {
                        switch (TxStatus.valueOf(tx.getTxStatus())) {
                            case APPROVED:
                                Map<String, String> params = new HashMap<>();
                                params.put("amount", tx.getAmount().toString());
                                params.put("currency", tx.getCurrency());
                                params.put("paymentId", transaction.getMerchantTransactionId());
                                params.put("userId", String.valueOf(transaction.getUserId()));
                                params.put("merchantId", String.valueOf(transaction.getMerchantId()));
                                try {
                                    processPayment(params);
                                } catch (RefillRequestAppropriateNotFoundException e) {
                                    e.printStackTrace();
                                }
                                break;
                            case PENDING:
                                break;
                            case REJECTED:
                                refillRequestDao.setRemarkById(transaction.getId(), "REJECTED");
                                break;
                            case CREATED:
                        }
                    });
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
