package me.exrates.model.enums.invoice;

import me.exrates.model.enums.BaseStatus;

import java.util.Map;
import java.util.Optional;
import java.util.Set;

public interface InvoiceStatus {

    default Optional<InvoiceStatus> nextState(Map<InvoiceActionTypeEnum, InvoiceStatus> schemaMap, InvoiceActionTypeEnum action) {
        return Optional.ofNullable(schemaMap.get(action));
    }

    InvoiceStatus nextState(InvoiceActionTypeEnum action);

    default Boolean availableForAction(Map<InvoiceActionTypeEnum, InvoiceStatus> schemaMap, InvoiceActionTypeEnum action) {
        return schemaMap.get(action) != null;
    }

    InvoiceStatus nextState(InvoiceActionTypeEnum action, InvoiceActionTypeEnum.InvoiceActionParamsValue paramsValue);

    Boolean availableForAction(InvoiceActionTypeEnum action);

    Set<InvoiceActionTypeEnum> getAvailableActionList(InvoiceActionTypeEnum.InvoiceActionParamsValue paramsValue);

    void initSchema(Map<InvoiceActionTypeEnum, InvoiceStatus> schemaMap);

    Boolean isEndStatus();

    Boolean isSuccessEndStatus();

    Integer getCode();

    String name();

    BaseStatus getBaseStatus();
}