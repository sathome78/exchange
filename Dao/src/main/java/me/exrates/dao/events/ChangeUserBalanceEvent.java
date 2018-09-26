package me.exrates.dao.events;

import org.springframework.context.ApplicationEvent;

/**
 * Created by Maks on 11.05.2018.
 */
public class ChangeUserBalanceEvent extends ApplicationEvent {
    /**
     * Create a new ApplicationEvent.
     *
     * @param source the object on which the event initially occurred (never {@code null})
     */
    private Integer currencyId;

    public ChangeUserBalanceEvent(Object source, Integer currencyId) {
        super(source);
        this.currencyId = currencyId;
    }

    public Integer getCurrencyId() {
        return currencyId;
    }
}
