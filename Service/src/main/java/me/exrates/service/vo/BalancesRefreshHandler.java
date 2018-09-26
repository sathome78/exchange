package me.exrates.service.vo;

import lombok.extern.log4j.Log4j2;
import me.exrates.service.stomp.StompMessenger;
import org.web3j.abi.datatypes.Int;

import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

/**
 * Created by Maks on 11.05.2018.
 */
@Log4j2
public class BalancesRefreshHandler {

    private Set<Integer> walletsSet = new CopyOnWriteArraySet<>();
    private final Object synchronizer = new Object();
    private static final long DELAY = 2000;
    private StompMessenger stompMessenger;

    public BalancesRefreshHandler(StompMessenger stompMessenger) {
        this.stompMessenger = stompMessenger;
    }

    public void refresh(int walletId, int currencyId) {
        synchronized (synchronizer) {
            if(walletsSet.contains(walletId)) {
                return;
            }
            walletsSet.add(walletId);
        }
        try {
            Thread.sleep(DELAY);
        } catch (InterruptedException e) {
            log.error(e);
        } finally {
            walletsSet.remove(walletId);
        }
        stompMessenger.sendCurrencyBalance(walletId, currencyId);

    }
}
