package me.exrates.model.enums;

import java.util.Arrays;

public enum SessionLifeTypeEnum {
    FIXED_LIFETIME(1, false),
    INACTIVE_COUNT_LIFETIME(2, true);/*default type of session life, applied when others disabled*/

    private Integer typeId;
    private boolean refreshOnUserRequests;

    SessionLifeTypeEnum(Integer code, boolean refreshOnUserRequests) {
        this.typeId = code;
        this.refreshOnUserRequests = refreshOnUserRequests;
    }

    public Integer getTypeId() {
        return typeId;
    }

    public boolean isRefreshOnUserRequests() {
        return refreshOnUserRequests;
    }

    public static SessionLifeTypeEnum convert(int id) {
        return Arrays.stream(SessionLifeTypeEnum.class.getEnumConstants())
                .filter(e -> e.typeId == id)
                .findAny()
                .orElseThrow(() -> new RuntimeException(String.valueOf(id)));
    }
}
