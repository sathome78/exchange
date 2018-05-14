package me.exrates.service.stomp;

import com.google.gson.GsonBuilder;
import lombok.Synchronized;
import lombok.extern.log4j.Log4j2;
import me.exrates.model.dto.CurrencyBalanceDto;
import me.exrates.model.dto.OrdersListWrapper;
import me.exrates.model.enums.ChartPeriodsEnum;
import me.exrates.model.enums.OperationType;
import me.exrates.model.enums.RefreshObjectsEnum;
import me.exrates.model.enums.UserRole;
import me.exrates.model.vo.BackDealInterval;
import me.exrates.service.OrderService;
import me.exrates.service.UserService;
import me.exrates.service.WalletService;
import me.exrates.service.cache.ChartsCache;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.user.SimpSubscription;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.DefaultSimpUserRegistry;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by Maks on 24.08.2017.
 */
@Log4j2(topic = "ws_stomp_log")
@Component
public class StompMessengerImpl implements StompMessenger{

    @Autowired
    private OrderService orderService;
    @Autowired
    private SimpMessagingTemplate messagingTemplate;
    @Autowired
    private DefaultSimpUserRegistry registry;
    @Autowired
    private UserService userService;
    @Autowired
    private ChartsCache chartsCache;
    @Autowired
    private WalletService walletService;



    private final List<BackDealInterval> intervals = Arrays.stream(ChartPeriodsEnum.values())
                                                    .map(ChartPeriodsEnum::getBackDealInterval)
                                                    .collect(Collectors.toList());


   @Override
   public void sendRefreshTradeOrdersMessage(Integer pairId, OperationType operationType){
       List<OrdersListWrapper> wrappers = new ArrayList<>();
       wrappers.add(orderService.getOrdersForRefresh(pairId, operationType, null));
       String message = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create().toJson(wrappers);
       sendMessageToDestination("/app/trade_orders/".concat(pairId.toString()), message);
      /* sendMessageToDestination("/app/info/trade_orders/".concat(pairId.toString()), message);*/
       sendRefreshTradeOrdersMessageToFiltered(pairId, operationType);
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
              List<OrdersListWrapper> wrappers = new ArrayList<>();
              wrappers.add(orderService.getOrdersForRefresh(pairId, operationType, k));
              String message = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create().toJson(wrappers);
              for (SimpSubscription subscription : v) {
                  sendMessageToSubscription(subscription, message, "/queue/trade_orders/f/".concat(pairId.toString()));
              }
          });
      }
   }

   @Override
   public void sendMyTradesToUser(final int userId, final Integer currencyPair) {
       String userEmail = userService.getEmailById(userId);
       String destination = "/queue/personal/".concat(currencyPair.toString());
       String message = orderService.getTradesForRefresh(currencyPair, RefreshObjectsEnum.MY_TRADES, userEmail);
       messagingTemplate.convertAndSendToUser(userEmail, destination, message);
   }

    @Override
    public void sendAllTrades(final Integer currencyPair) {
        String destination = "/app/trades/".concat(currencyPair.toString());
        String message = orderService.getTradesForRefresh(currencyPair, RefreshObjectsEnum.ALL_TRADES, null);
        sendMessageToDestination(destination, message);
    }

    @Override
    public void sendCurrencyBalance(int walletId, int currencyId) {
        String destination = "/queue/balance/";
        String email = userService.getEmailByWalletId(walletId);
        List<String> destinations = registry.getUser(email).getSessions().stream()
                .flatMap(p->p.getSubscriptions().stream())
                .filter(p->p.getDestination().contains(destination)
                        && p.getDestination().contains(String.valueOf(currencyId)))
                .map(SimpSubscription::getDestination).collect(Collectors.toList());
        String message = walletService.getActiveBalanceForCurrencies(Arrays.asList(currencyId), email);
        destinations.forEach(p->{
            messagingTemplate.convertAndSendToUser(email, p.replace("/user", ""), message);
        });
    }

    @Override
    public void sendChartData(final Integer currencyPairId) {
       Map<String, String> data = chartsCache.getData(currencyPairId);
        orderService.getIntervals().forEach(p-> {
            String message = data.get(p.getInterval());
            String destination = "/app/charts/".concat(currencyPairId.toString().concat("/").concat(p.getInterval()));
            sendMessageToDestination(destination, message);
        });
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

    @Synchronized
    @Override
    public void sendStatisticMessage(List<Integer> currenciesIds) {
       sendMessageToDestination("/app/statistics", orderService.getSomeCurrencyStatForRefresh(currenciesIds));
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
