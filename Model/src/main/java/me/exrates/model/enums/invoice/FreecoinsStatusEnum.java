package me.exrates.model.enums.invoice;


import lombok.Getter;
import lombok.extern.log4j.Log4j2;
import me.exrates.model.enums.BaseStatus;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@Log4j2
public enum FreecoinsStatusEnum implements InvoiceStatus {

    CREATED_BY_USER(1) {
        @Override
        public void initSchema(Map<InvoiceActionTypeEnum, InvoiceStatus> schemaMap) {
        }
    },
    REVOKED_BY_FAILURE(2) {
        @Override
        public void initSchema(Map<InvoiceActionTypeEnum, InvoiceStatus> schemaMap) {
        }
    },
    SUCCESS(3) {
        @Override
        public void initSchema(Map<InvoiceActionTypeEnum, InvoiceStatus> schemaMap) {
        }
    };

    @Getter
    private Integer code;

    FreecoinsStatusEnum(Integer code) {
        this.code = code;
    }

    final private Map<InvoiceActionTypeEnum, InvoiceStatus> schemaMap = new HashMap<>();

    @Override
    public InvoiceStatus nextState(InvoiceActionTypeEnum action) {
        log.warn("Not implemented yet");

        return null;
    }

    @Override
    public InvoiceStatus nextState(InvoiceActionTypeEnum action, InvoiceActionTypeEnum.InvoiceActionParamsValue paramsValue) {
        log.warn("Not implemented yet");

        return null;
    }

    @Override
    public Boolean availableForAction(InvoiceActionTypeEnum action) {
        return availableForAction(schemaMap, action);
    }

    public Set<InvoiceActionTypeEnum> getAvailableActionList(InvoiceActionTypeEnum.InvoiceActionParamsValue paramsValue) {
        log.warn("Not implemented yet");

        return Collections.emptySet();
    }

    public static FreecoinsStatusEnum convert(int id) {
        return Arrays.stream(FreecoinsStatusEnum.class.getEnumConstants())
                .filter(e -> e.code == id)
                .findAny()
                .orElse(FreecoinsStatusEnum.CREATED_BY_USER);
    }

    public static FreecoinsStatusEnum convert(String name) {
        return Arrays.stream(FreecoinsStatusEnum.class.getEnumConstants())
                .filter(e -> e.name().equals(name))
                .findAny()
                .orElseThrow(() -> new RuntimeException(String.format("Not found status with name: %s", name)));
    }

    public static InvoiceStatus getBeginState() {
        log.warn("Not implemented yet");

        return null;
    }

    @Override
    public Boolean isEndStatus() {
        return schemaMap.isEmpty();
    }

    @Override
    public Boolean isSuccessEndStatus() {
        log.warn("Not implemented yet");

        return false;
    }

    @Override
    public BaseStatus getBaseStatus() {
        log.warn("Not implemented yet");

        return null;
    }
}