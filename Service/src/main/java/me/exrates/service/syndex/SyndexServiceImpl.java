package me.exrates.service.syndex;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;
import me.exrates.dao.SyndexDao;
import me.exrates.model.Currency;
import me.exrates.model.Merchant;
import me.exrates.model.dto.RefillRequestAcceptDto;
import me.exrates.model.dto.RefillRequestCreateDto;
import me.exrates.model.dto.SyndexOrderDto;
import me.exrates.model.enums.SyndexOrderStatusEnum;
import me.exrates.service.CurrencyService;
import me.exrates.service.GtagService;
import me.exrates.service.MerchantService;
import me.exrates.service.RefillService;
import me.exrates.service.UserService;
import me.exrates.service.exception.RefillRequestAppropriateNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.time.temporal.TemporalAccessor;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

@Log4j2(topic = "syndex")
@Service
public class SyndexServiceImpl implements SyndexService {

    private final SyndexDao syndexDao;
    private final SyndexClient syndexClient;
    private static final Integer minuteToWaitBeforeDisput = 90;
    private static final String MERCHANT_NAME = "Syndex";
    private static final String CURRENCY_NAME = "USD";
    private static final String AMOUNT_PARAM = "AMOUNT";
    private static final String SYNDEX_ID = "SYNDEX_ID";
    private static final String PAYMENT_ID = "PAYMENT_ID";
    private final Currency currency;
    private final Merchant merchant;

    private final ObjectMapper objectMapper;
    private final UserService userService;
    private final RefillService refillService;
    private final GtagService gtagService;

    @Autowired
    public SyndexServiceImpl(SyndexDao syndexDao, SyndexClient syndexClient, ObjectMapper objectMapper, UserService userService, RefillService refillService, GtagService gtagService, MerchantService merchantService, CurrencyService currencyService) {
        this.syndexDao = syndexDao;
        this.syndexClient = syndexClient;
        this.objectMapper = objectMapper;
        this.userService = userService;
        this.refillService = refillService;
        this.gtagService = gtagService;
        currency = currencyService.findByName(CURRENCY_NAME);
        merchant = merchantService.findByName(MERCHANT_NAME);
    }

    @Override
    public List<SyndexOrderDto> getAllPendingPayments(List<Integer> statuses, Integer userId) {
        return syndexDao.getAllorders(statuses, userId);
    }

    @Override
    public SyndexOrderDto getOrderInfo(int orderId, String email) {
        return syndexDao.getById(orderId, userService.getIdByEmail(email));
    }

    @SneakyThrows
    @Transactional(propagation = Propagation.NESTED)
    @Override
    public Map<String, String> refill(RefillRequestCreateDto request) {
        SyndexOrderDto orderDto = new SyndexOrderDto(request);
        orderDto.setStatus(SyndexOrderStatusEnum.CREATED);
        syndexDao.saveOrder(orderDto);
        SyndexClient.OrderInfo orderInfo = syndexClient.createOrder(new SyndexClient.CreateOrderRequest(orderDto));
        syndexDao.updateStatus(orderDto.getId(), orderInfo.getStatus());
        syndexDao.updatePaymentDetails(orderDto.getId(), orderInfo.getPaymentDetails());
        syndexDao.updateSyndexId(orderDto.getId(), orderInfo.getId());
        return new HashMap<String, String>() {{
            put("$__response_object",  objectMapper.writeValueAsString(orderInfo));
        }};
    }

    @Override
    public void processPayment(Map<String, String> params) throws RefillRequestAppropriateNotFoundException {
        Integer requestId = Integer.valueOf(params.get(PAYMENT_ID));
        String merchantTransactionId = params.get(SYNDEX_ID);
        BigDecimal amount = new BigDecimal(params.get(AMOUNT_PARAM));

        RefillRequestAcceptDto requestAcceptDto = RefillRequestAcceptDto.builder()
                .requestId(requestId)
                .merchantId(merchant.getId())
                .currencyId(currency.getId())
                .amount(amount)
                .merchantTransactionId(merchantTransactionId)
                .toMainAccountTransferringConfirmNeeded(this.toMainAccountTransferringConfirmNeeded())
                .build();

        refillService.autoAcceptRefillRequest(requestAcceptDto);

        final String gaTag = refillService.getUserGAByRequestId(requestId);
        log.debug("Process of sending data to Google Analytics...");
        gtagService.sendGtagEvents(amount.toString(), currency.getName(), gaTag);
    }

    @Transactional
    @Override
    public void cancelOrder(int id, String email) {
        SyndexOrderDto currentOrder = syndexDao.getByIdForUpdate(id, userService.getIdByEmail(email));

        if (currentOrder.getStatus() != SyndexOrderStatusEnum.CREATED) {
            throw new SyndexOrderException("Current status not suiatable for Cancelling order");
        }

        refillService.revokeRefillRequest(id);
        syndexDao.updateStatus(id, SyndexOrderStatusEnum.CANCELLED.getStatusId());
        syndexClient.cancelOrder(currentOrder.getSyndexId());
    }

    @Transactional
    @Override
    public void openDispute(SyndexClient.DisputeData data, String email) {
        SyndexOrderDto currentOrder = syndexDao.getByIdForUpdate(data.getId(), userService.getIdByEmail(email));
        long minutesLeft = Duration.between(currentOrder.getLastModifDate(), LocalDateTime.now()).toMinutes();

        if (minutesLeft < minuteToWaitBeforeDisput) {
            throw new SyndexOrderException("wit 90 minutes to open dispute");
        }

        if (currentOrder.getStatus() != SyndexOrderStatusEnum.MODERATION) {
            throw new SyndexOrderException("Current status not suiatable for Cancelling order");
        }

        syndexDao.updateStatus(data.getId(), SyndexOrderStatusEnum.CONFLICT.getStatusId());
        syndexClient.openDispute(currentOrder.getSyndexId(), data.getText());
    }

    @Override
    public void confirmOrder(Integer id, String email) {
        SyndexOrderDto currentOrder = syndexDao.getByIdForUpdate(id, userService.getIdByEmail(email));

        if (currentOrder.getStatus() != SyndexOrderStatusEnum.MODERATION) {
            throw new SyndexOrderException("Current status not suiatable for confirming order");
        }

        syndexDao.setConfirmed(id);
        syndexClient.confirmOrder(currentOrder.getSyndexId());
    }


    @Override
    public void onCallbackEvent(SyndexClient.OrderInfo orderFormCallback) {
        log.debug("income order {}", orderFormCallback);
        checkOrder(orderFormCallback.getId());
    }

    @Transactional
    @Override
    public void checkOrder(long syndexOrderId) {

        SyndexOrderDto currentOrderFromDb = syndexDao.getBySyndexIdForUpdate(syndexOrderId);
        SyndexClient.OrderInfo retrievedOrder = syndexClient.getOrderInfo(syndexOrderId);
        SyndexOrderStatusEnum lastSavedStatus = currentOrderFromDb.getStatus();
        SyndexOrderStatusEnum newStatus = SyndexOrderStatusEnum.convert(retrievedOrder.getStatus());

        if (lastSavedStatus != newStatus) {
            syndexDao.updateStatus(currentOrderFromDb.getId(), newStatus.getStatusId());
        }

        if (lastSavedStatus.isInPendingStatus() && newStatus == SyndexOrderStatusEnum.COMPLETE) {
            tryToRefill(currentOrderFromDb);

        } else if (lastSavedStatus.isInPendingStatus() && newStatus == SyndexOrderStatusEnum.CANCELLED) {
            refillService.revokeRefillRequest(currentOrderFromDb.getId());

        } else if (lastSavedStatus == SyndexOrderStatusEnum.CREATED && newStatus == SyndexOrderStatusEnum.MODERATION) {
            syndexDao.updatePaymentDetails(currentOrderFromDb.getId(), retrievedOrder.getPaymentDetails());

        } else {
            log.debug("do nothing on order {}", retrievedOrder);
        }
    }

    @SneakyThrows
    private void tryToRefill(SyndexOrderDto currentOrderFromDb) {
       Map<String, String> paramsMap = new HashMap<>();
       paramsMap.put(PAYMENT_ID, String.valueOf(currentOrderFromDb.getId()));
       paramsMap.put(SYNDEX_ID, String.valueOf(currentOrderFromDb.getSyndexId()));
       paramsMap.put(AMOUNT_PARAM, currentOrderFromDb.getAmount().toString());
       processPayment(paramsMap);
    }
}
