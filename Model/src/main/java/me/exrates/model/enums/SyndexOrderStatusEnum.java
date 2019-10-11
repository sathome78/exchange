package me.exrates.model.enums;

import java.util.Arrays;

public enum SyndexOrderStatusEnum {

    CREATED(0, true),     /*создалось, можно отменить или перейдет дальше в модерацию*/
    MODERATION(1, true),  /*дали адрес, можно сделать confirm(статус не меняется),  или открыть dispute -> CONFLICT*/
    COMPLETE(2, false),   /*зачислено*/
    CANCELLED(3, false),  /*отменен, возможно только из created либо системой */
    CONFLICT(4, true);    /*открыт спор, возможно только из moderation, может перейти в COMPLETE или  CANCELLED*/

    private int statusId;

    private boolean inPendingStatus;


    SyndexOrderStatusEnum(int statusId, boolean inPendingStatus) {
        this.statusId = statusId;
        this.inPendingStatus = inPendingStatus;
    }

    public int getStatusId() {
        return statusId;
    }

    public boolean isInPendingStatus() {
        return inPendingStatus;
    }

    public static SyndexOrderStatusEnum convert(int statusId) {
        return Arrays.stream(SyndexOrderStatusEnum.values())
                     .filter(p -> p.statusId == statusId)
                     .findFirst().orElseThrow(() -> new RuntimeException("no enum with status " + statusId));
    }
}
