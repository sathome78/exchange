package me.exrates.service.stomp;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import lombok.Synchronized;
import lombok.extern.log4j.Log4j2;
import me.exrates.model.CurrencyPair;
import me.exrates.model.IEODetails;
import me.exrates.model.chart.ChartTimeFrame;
import me.exrates.model.dto.RefreshStatisticDto;
import me.exrates.model.dto.UserNotificationMessage;
import me.exrates.model.enums.OperationType;
import me.exrates.model.enums.OrderType;
import me.exrates.model.enums.PrecissionsEnum;
import me.exrates.model.enums.RefreshObjectsEnum;
import me.exrates.service.OrderService;
import me.exrates.service.UserService;
import me.exrates.service.util.BiTuple;
import me.exrates.service.util.OpenApiUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.user.SimpSubscription;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.DefaultSimpUserRegistry;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static java.util.Objects.isNull;

@Log4j2(topic = "ws_stomp_log")
@Component
public class StompMessengerImpl implements StompMessenger {

    @Autowired
    private OrderService orderService;
    @Autowired
    private SimpMessagingTemplate messagingTemplate;
    @Autowired
    private DefaultSimpUserRegistry registry;
    @Autowired
    private UserService userService;
    @Autowired
    private ObjectMapper objectMapper;


    @Override
    public void sendRefreshTradeOrdersMessage(CurrencyPair currencyPair, OperationType operationType) {
        String message = orderService.getOrdersForRefresh(currencyPair.getId(), operationType, null);
        sendMessageToDestination("/app/trade_orders/".concat(String.valueOf(currencyPair.getId())), message);
        sendMessageToOrderBookNg(currencyPair, operationType);
        sendMessageToDestination("/app/orders/sfwfrf442fewdf/".concat(String.valueOf(currencyPair.getId())), message);
    }

    private void sendMessageToOrderBookNg(CurrencyPair currencyPair, OperationType operationType) {
        String pairName = OpenApiUtils.transformCurrencyPairBack(currencyPair.getName());
        String basePath = "/app/order_book/%s/%d";
        List<PrecissionsEnum> subscribed = Arrays.asList(PrecissionsEnum.values());
        Map<PrecissionsEnum, String> result = orderService.findAllOrderBookItemsForAllPrecissions(OrderType.fromOperationType(operationType), currencyPair.getId(), subscribed);
        result.forEach((k, v) -> {
            String path = String.format(basePath, pairName, k.getValue());
            sendMessageToDestination(path, v);
        });

    }

    @Override
    public void sendRefreshTradeOrdersDetailMessage(String pairName, String message) {
        sendMessageToDestination("/app/orders/sfwfrf442fewdf/detailed/".concat(pairName), message);
    }


    @Override
    public void sendPersonalOpenOrdersAndDealsToUser(Integer userId, String pairName, String message) {
        String destination = "/queue/my_orders/".concat(pairName);
        String userEmail = userService.getEmailById(userId);
        log.debug("dest {} message {}", destination, message);
        messagingTemplate.convertAndSendToUser(userEmail, destination, message);
    }

    @Override
    public void sendMyTradesToUser(final int userId, final Integer currencyPair) {
        String userEmail = userService.getEmailById(userId);
        String destination = "/queue/personal/".concat(currencyPair.toString());
        String message = String.valueOf(orderService.getTradesForRefresh(currencyPair, userEmail, RefreshObjectsEnum.MY_TRADES).right);
        messagingTemplate.convertAndSendToUser(userEmail, destination, message);
    }

    @Override
    public void sendAllTrades(CurrencyPair currencyPair) {
        BiTuple<String, String> results = orderService.getTradesForRefresh(currencyPair.getId(), null, RefreshObjectsEnum.ALL_TRADES);
        sendMessageToDestination("/app/trades/".concat(String.valueOf(currencyPair.getId())), results.right);
        String destination = "/app/all_trades/".concat(OpenApiUtils.transformCurrencyPairBack(currencyPair.getName()));

        sendMessageToDestination(destination, results.left);
    }


    @Override
    public List<ChartTimeFrame> getSubscribedTimeFramesForCurrencyPair(Integer pairId) {
        List<ChartTimeFrame> timeFrames = new ArrayList<>();
        orderService.getChartTimeFrames().forEach(timeFrame -> {
            String destination = String.join("/", "/app/charts", pairId.toString(),
                    timeFrame.getResolution().toString());
            Set<SimpSubscription> subscribers = findSubscribersByDestination(destination);
            if (subscribers.size() > 0) {
                timeFrames.add(timeFrame);
            }
        });
        return timeFrames;
    }


    @Synchronized
    @Override
    public void sendStatisticMessage(Set<Integer> currenciesIds) {
        RefreshStatisticDto dto = orderService.getSomeCurrencyStatForRefresh(currenciesIds);
        if (!isNull(dto.getIcoData())) {
            sendMessageToDestination("/app/statistics/".concat(RefreshObjectsEnum.ICO_CURRENCIES_STATISTIC.name()), dto.getIcoData());
            sendMessageToDestination("/app/statisticsNew", dto.getIcoData());
        }
        if (!isNull(dto.getMainCurrenciesData())) {
            sendMessageToDestination("/app/statisticsNew", dto.getMainCurrenciesData());
            sendMessageToDestination("/app/statistics/".concat(RefreshObjectsEnum.MAIN_CURRENCIES_STATISTIC.name()), dto.getMainCurrenciesData());
        }
        sendCpInfoMessage(dto.getStatisticInfoDtos());
    }

    @Override
    public void sendCpInfoMessage(Map<String, String> currenciesStatisticMap) {
        if (!isNull(currenciesStatisticMap)) {
            currenciesStatisticMap.forEach((k, v) -> {
                sendMessageToDestination("/app/statistics/pairInfo/".concat(OpenApiUtils.transformCurrencyPairBack(k)), v);
            });
        }
    }

    @Override
    public void sendEventMessage(final String sessionId, final String message) {
        sendMessageToDestination("/app/ev/".concat(sessionId), message);
    }

    @Override
    public void sendAlerts(final String message, final String lang) {
        log.debug("lang to send {}", lang);
        sendMessageToDestination("/app/users_alerts/".concat(lang), message);
    }

    @SneakyThrows
    @Override
    public void sendPersonalMessageToUser(String userEmail, UserNotificationMessage message) {
        String destination = "/app/message/private/".concat(userService.getPubIdByEmail(userEmail));
        sendMessageToDestination(destination, objectMapper.writeValueAsString(message));
    }

    @Override
    public void sendPersonalDetailsIeo(String userEmail, String payload) {
        String destination = "/app/ieo_details/private/".concat(userService.getPubIdByEmail(userEmail));
        sendMessageToDestination(destination, payload);
    }

    @Override
    public void sendDetailsIeo(Integer detailId, String payload) {
        sendMessageToDestination("/app/ieo/ieo_details/".concat(detailId.toString()), payload);
    }

    @Override
    public void sendAllIeos(Collection<IEODetails> ieoDetails) {
        try {
            String payload = objectMapper.writeValueAsString(ieoDetails);
            sendMessageToDestination("/app/ieo/ieo_details", payload);
        } catch (JsonProcessingException e) {
            log.warn("<<IEO>>>: Failed parse to json ieo details", e);
        }
    }

    private Set<SimpSubscription> findSubscribersByDestination(final String destination) {
        return registry.findSubscriptions(subscription -> subscription.getDestination().equals(destination));
    }

    private void sendMessageToDestination(String destination, String message) {
        messagingTemplate.convertAndSend(destination, message);
    }


    private void sendMessageToDestinationAndUser(final String user, String destination, String message) {
        messagingTemplate.convertAndSendToUser(user,
                destination,
                message);
    }

}
