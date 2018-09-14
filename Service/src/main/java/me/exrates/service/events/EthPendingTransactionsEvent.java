package me.exrates.service.events;

import org.springframework.context.ApplicationEvent;
import org.web3j.protocol.core.methods.response.Transaction;

/**
 * Created by Maks on 19.09.2017.
 */
public class EthPendingTransactionsEvent extends ApplicationEvent {
    /**
     * Create a new ApplicationEvent.
     *
     * @param transaction the object on which the event initially occurred (never {@code null})
     */
    public EthPendingTransactionsEvent(Transaction transaction) {
        super(transaction);
    }
}
