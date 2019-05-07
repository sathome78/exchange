package me.exrates.service.events.EventsForDetailed;

import me.exrates.model.ExOrder;
import me.exrates.model.enums.OrderEventEnum;

public class CancelDetailorderEvent extends DetailOrderEvent {

    private boolean byAdmin;

    private boolean forPartialAccept;

    public boolean isByAdmin() {
        return byAdmin;
    }

    public boolean ifForPartialaccept() {
        return  forPartialAccept;
    }

    public CancelDetailorderEvent(ExOrder source, boolean byAdmin, boolean forPartialAccept, int pairId) {
        super(source, pairId);
        this.byAdmin = byAdmin;
        this.forPartialAccept = forPartialAccept;
        setOrderEventEnum(OrderEventEnum.CANCEL);
    }
}
