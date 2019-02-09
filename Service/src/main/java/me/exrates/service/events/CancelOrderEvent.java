package me.exrates.service.events;

import me.exrates.model.ExOrder;
import me.exrates.model.enums.OrderEventEnum;

/**
 * Created by Maks on 30.08.2017.
 */
public class CancelOrderEvent extends OrderEvent {

    private boolean byAdmin;

    private boolean forPartialAccept;

    public boolean isByAdmin() {
        return byAdmin;
    }

    public boolean ifForPartialaccept() {
        return  forPartialAccept;
    }

    /**
     * Create a new ApplicationEvent.
     *
     * @param source the object on which the event initially occurred (never {@code null})
     */
    public CancelOrderEvent(ExOrder source, boolean byAdmin) {
        super(source);
        this.byAdmin = byAdmin;
        this.forPartialAccept = false;
        setOrderEventEnum(OrderEventEnum.CANCEL);
    }

    public CancelOrderEvent(ExOrder source, boolean byAdmin, boolean forPartialAccept) {
        super(source);
        this.byAdmin = byAdmin;
        this.forPartialAccept = forPartialAccept;
        setOrderEventEnum(OrderEventEnum.CANCEL);
    }
}
