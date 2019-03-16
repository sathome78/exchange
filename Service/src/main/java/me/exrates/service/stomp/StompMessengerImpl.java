package me.exrates.service.stomp;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Synchronized;
import lombok.extern.log4j.Log4j2;
import me.exrates.model.CurrencyPair;
import me.exrates.model.chart.ChartTimeFrame;
import me.exrates.model.enums.ChartPeriodsEnum;
import me.exrates.model.enums.OperationType;
import me.exrates.model.enums.OrderType;
import me.exrates.model.enums.PrecissionsEnum;
import me.exrates.model.enums.RefreshObjectsEnum;
import me.exrates.model.enums.UserRole;
import me.exrates.model.vo.BackDealInterval;
import me.exrates.service.OrderService;
import me.exrates.service.UserService;
import me.exrates.service.util.OpenApiUtils;
import me.exrates.service.util.BiTuple;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.user.SimpSubscription;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.DefaultSimpUserRegistry;

import javax.annotation.PostConstruct;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * Created by Maks on 24.08.2017.
 */
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


    private ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();

    @PostConstruct
    public void init() {
        scheduler.scheduleAtFixedRate(() ->  registry.findSubscriptions(sub -> true)
                        .forEach(sub -> System.out.printf("sub: dest %s, user %s")),
                1, 2, TimeUnit.MINUTES);
    }


    private final List<BackDealInterval> intervals = Arrays.stream(ChartPeriodsEnum.values())
                                                    .map(ChartPeriodsEnum::getBackDealInterval)
                                                    .collect(Collectors.toList());


   @Override
   public void sendRefreshTradeOrdersMessage(CurrencyPair currencyPair, OperationType operationType){
       String message = orderService.getOrdersForRefresh(currencyPair.getId(), operationType, null);
       sendMessageToDestination("/app/trade_orders/".concat(String.valueOf(currencyPair.getId())), message);
       sendMessageToDestination("/app/orders/sfwfrf442fewdf/".concat(String.valueOf(currencyPair.getId())), message);
       sendMessageToOrderBookNg(currencyPair, operationType);
       /*sendRefreshTradeOrdersMessageToFiltered(pairId, operationType);*/
   }

   private void sendMessageToOrderBookNg(CurrencyPair currencyPair, OperationType operationType) {
       String pairName = OpenApiUtils.transformCurrencyPairBack(currencyPair.getName());
       String basePath2 = "/order_book/%s/%d";
       List<PrecissionsEnum> finalSubscribed = new ArrayList<>();
       Arrays.stream(PrecissionsEnum.values()).forEach(p -> {
           String path = String.format(basePath2, pairName, p.getValue());
           Set<SimpSubscription> subscribers = findSubscribersByDestination(path);
           if (subscribers.size() > 0) {
               System.out.println("added " + path);
               finalSubscribed.add(p);
           }
       });
       String basePath = "/app/order_book/%s/%d";
       List<PrecissionsEnum> subscribed = Arrays.asList(PrecissionsEnum.values());
       Map<PrecissionsEnum, String> result = orderService.findAllOrderBookItemsForAllPrecissions(OrderType.fromOperationType(operationType), currencyPair.getId(), subscribed);
       result.forEach((k,v) -> {
           String path = String.format(basePath, pairName, k.getValue());
           sendMessageToDestination(path, v);
       });

   }

    @Override
    public void sendRefreshTradeOrdersDetailMessage(String pairName, String message){
        sendMessageToDestination("/app/orders/sfwfrf442fewdf/detailed/".concat(pairName), message);
    }

   private void sendRefreshTradeOrdersMessageToFiltered(Integer pairId, OperationType operationType) {
      Set<SimpSubscription> subscriptions =
              findSubscribersByDestination("/user/queue/trade_orders/f/".concat(pairId.toString()));
      if (!subscriptions.isEmpty()) {
          Map<UserRole, List<SimpSubscription>> map = new HashMap<>();
          subscriptions.forEach(p -> {
              String userEmail = p.getSession().getUser().getName();
              if (!StringUtils.isEmpty(userEmail)) {
                  UserRole role = userService.getUserRoleFromDB(userEmail);
                  if (map.containsKey(role)) {
                      map.get(role).add(p);
                  } else {
                      map.put(role, new ArrayList<SimpSubscription>(){{add(p);}});
                  }
              }
          });
          map.forEach((k,v) -> {
              String message = orderService.getOrdersForRefresh(pairId, operationType, k);
              for (SimpSubscription subscription : v) {
                  sendMessageToSubscription(subscription, message, "/queue/trade_orders/f/".concat(pairId.toString()));
              }
          });
      }
   }


    @Override
    public void sendPersonalOpenOrdersAndDealsToUser(Integer userId, String pairName, String message) {
        String destination = "/queue/my_orders/".concat(pairName);
        String userEmail = userService.getEmailById(userId);
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
        System.out.println(destination);
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


    private List<BackDealInterval> getSubscribedIntervalsForCurrencyPair(Integer pairId) {
       List<BackDealInterval> intervals = new ArrayList<>();
       orderService.getIntervals().forEach(p->{
            Set<SimpSubscription> subscribers = findSubscribersByDestination("/app/charts/".concat(pairId.toString().concat("/").concat(p.getInterval())));
            if (subscribers.size() > 0) {
                intervals.add(p);
            }
       });
       return intervals;
    }

   /* public void sendChartUpdate(Integer currencyPairId) {
       registry.findSubscriptions(sub -> sub.).forEach(sub -> sub.);
    }*/

    @Synchronized
    @Override
    public void sendStatisticMessage(List<Integer> currenciesIds) {
        Map<RefreshObjectsEnum, String> result =  orderService.getSomeCurrencyStatForRefresh(currenciesIds);
        result.forEach((k,v) -> {
            sendMessageToDestination("/app/statisticsNew", v);
            sendMessageToDestination("/app/statistics/".concat(k.getSubscribeChannel()), v);
        });
    }

    @Override
    public void sendCpInfoMessage(String pairName, String message) {
        sendMessageToDestination("/app/statistics/pairInfo/".concat(OpenApiUtils.transformCurrencyPairBack(pairName)), message);
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


    private Set<SimpSubscription> findSubscribersByDestination(final String destination) {
       return registry.findSubscriptions(subscription -> subscription.getDestination().equals(destination));
   }

   private void sendMessageToDestination(String destination, String message) {
       messagingTemplate.convertAndSend(destination, message);
   }

   private void sendMessageToSubscription(SimpSubscription subscription, String message, String dest) {
       sendMessageToDestinationAndUser(subscription.getSession().getUser().getName(), dest, message);
   }

    private void sendMessageToDestinationAndUser(final String user, String destination, String message) {
       messagingTemplate.convertAndSendToUser(user,
                                               destination,
                                               message);
    }

}
