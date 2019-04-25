package me.exrates.model.enums.invoice;


import lombok.extern.log4j.Log4j2;
import me.exrates.model.constants.ErrorApiTitles;
import me.exrates.model.exceptions.IeoException;
import me.exrates.model.exceptions.InvoiceActionIsProhibitedForCurrencyPermissionOperationException;
import me.exrates.model.exceptions.InvoiceActionIsProhibitedForNotHolderException;
import me.exrates.model.exceptions.UnsupportedInvoiceStatusForActionException;
import me.exrates.model.exceptions.UnsupportedWithdrawRequestStatusIdException;
import me.exrates.model.exceptions.UnsupportedWithdrawRequestStatusNameException;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static me.exrates.model.enums.invoice.InvoiceActionTypeEnum.InvoiceActionParamsValue;

/**
 * Created by ValkSam
 */
@Log4j2
public enum IeoStatusEnum implements InvoiceStatus {

    ACCEPTED_AUTO(9) {
        @Override
        public void initSchema(Map<InvoiceActionTypeEnum, InvoiceStatus> schemaMap) {
        }
    },
    REVOKED_USER(11) {
        @Override
        public void initSchema(Map<InvoiceActionTypeEnum, InvoiceStatus> schemaMap) {
        }
    },
    EXPIRED(12) {
        @Override
        public void initSchema(Map<InvoiceActionTypeEnum, InvoiceStatus> schemaMap) {
        }
    },
    PROCESSED_BY_CLAIM(13) {
        @Override
        public void initSchema(Map<InvoiceActionTypeEnum, InvoiceStatus> schemaMap) {
        }
    },
    REVOKED_BY_IEO_FAILURE(14) {
        @Override
        public void initSchema(Map<InvoiceActionTypeEnum, InvoiceStatus> schemaMap) {
        }
    },
    SUCCESS_IEO(15) {
        @Override
        public void initSchema(Map<InvoiceActionTypeEnum, InvoiceStatus> schemaMap) {
        }
    };

    final private Map<InvoiceActionTypeEnum, InvoiceStatus> schemaMap = new HashMap<>();

    @Override
    public InvoiceStatus nextState(InvoiceActionTypeEnum action) {
        action.checkRestrictParamNeeded();
        return nextState(schemaMap, action)
                .orElseThrow(() -> new UnsupportedInvoiceStatusForActionException(String.format("current state: %s action: %s", this.name(), action.name())));
    }

    @Override
    public InvoiceStatus nextState(InvoiceActionTypeEnum action, InvoiceActionParamsValue paramsValue) {
        try {
            action.checkAvailabilityTheActionForParamsValue(paramsValue);
        } catch (InvoiceActionIsProhibitedForNotHolderException e) {
            throw new InvoiceActionIsProhibitedForNotHolderException(String.format("current status: %s action: %s", this.name(), action.name()));
        } catch (InvoiceActionIsProhibitedForCurrencyPermissionOperationException e) {
            throw new InvoiceActionIsProhibitedForCurrencyPermissionOperationException(String.format("current status: %s action: %s permittedOperation: %s", this.name(), action.name(), paramsValue.getPermittedOperation().name()));
        } catch (Exception e) {
            throw e;
        }
        return nextState(schemaMap, action)
                .orElseThrow(() -> new UnsupportedInvoiceStatusForActionException(String.format("current state: %s action: %s", this.name(), action.name())));
    }

    @Override
    public Boolean availableForAction(InvoiceActionTypeEnum action) {
        return availableForAction(schemaMap, action);
    }

    public Set<InvoiceActionTypeEnum> getAvailableActionList(InvoiceActionParamsValue paramsValue) {
        return schemaMap.keySet()
                .stream()
                .filter(e -> e.isMatchesTheParamsValue(paramsValue))
                .collect(Collectors.toSet());
    }

    /**/

    public static IeoStatusEnum convert(int id) {
        Optional<IeoStatusEnum> result = Arrays.stream(IeoStatusEnum.class.getEnumConstants())
                .filter(e -> e.code == id)
                .findAny();

        return result.orElse(IeoStatusEnum.PROCESSED_BY_CLAIM);
    }

    public static IeoStatusEnum convert(String name) {
        return Arrays.stream(IeoStatusEnum.class.getEnumConstants())
                .filter(e -> e.name().equals(name))
                .findAny()
                .orElseThrow(() -> new UnsupportedWithdrawRequestStatusNameException(name));
    }

    public static InvoiceStatus getBeginState() {
        Set<InvoiceStatus> allNodesSet = collectAllSchemaMapNodesSet();
        List<InvoiceStatus> candidateList = Arrays.stream(IeoStatusEnum.class.getEnumConstants())
                .filter(e -> !allNodesSet.contains(e))
                .collect(Collectors.toList());
        if (candidateList.size() == 0) {
            log.fatal("begin state not found");
            throw new AssertionError();
        }
        if (candidateList.size() > 1) {
            log.fatal("more than single begin state found: " + candidateList);
            throw new AssertionError();
        }
        return candidateList.get(0);
    }

    @Override
    public Boolean isEndStatus() {
        return schemaMap.isEmpty();
    }

    @Override
    public Boolean isSuccessEndStatus() {
        Map<InvoiceActionTypeEnum, InvoiceStatus> schema = new HashMap<>();
        Arrays.stream(IeoStatusEnum.class.getEnumConstants())
                .forEach(e -> schema.putAll(e.schemaMap));
        return schema.entrySet().stream()
                .filter(e -> e.getValue() == this)
                .anyMatch(e -> e.getKey().isLeadsToSuccessFinalState());
    }

    private static Set<InvoiceStatus> collectAllSchemaMapNodesSet() {
        Set<InvoiceStatus> result = new HashSet<>();
        Arrays.stream(IeoStatusEnum.class.getEnumConstants())
                .forEach(e -> result.addAll(e.schemaMap.values()));
        return result;
    }

    private Integer code;

    IeoStatusEnum(Integer code) {
        this.code = code;
    }

    @Override
    public Integer getCode() {
        return code;
    }

}

