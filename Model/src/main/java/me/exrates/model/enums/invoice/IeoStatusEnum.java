package me.exrates.model.enums.invoice;


import lombok.extern.log4j.Log4j2;
import me.exrates.model.Merchant;
import me.exrates.model.enums.MerchantProcessType;
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
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

import static me.exrates.model.enums.invoice.InvoiceActionTypeEnum.ACCEPT_AUTO;
import static me.exrates.model.enums.invoice.InvoiceActionTypeEnum.CREATE_BY_FACT;
import static me.exrates.model.enums.invoice.InvoiceActionTypeEnum.CREATE_BY_USER;
import static me.exrates.model.enums.invoice.InvoiceActionTypeEnum.InvoiceActionParamsValue;
import static me.exrates.model.enums.invoice.InvoiceActionTypeEnum.PUT_FOR_CONFIRM_USER;
import static me.exrates.model.enums.invoice.InvoiceActionTypeEnum.PUT_FOR_PENDING;
import static me.exrates.model.enums.invoice.InvoiceActionTypeEnum.REQUEST_INNER_TRANSFER;
import static me.exrates.model.enums.invoice.InvoiceActionTypeEnum.START_BCH_EXAMINE;

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
    } catch (InvoiceActionIsProhibitedForNotHolderException e){
      throw new InvoiceActionIsProhibitedForNotHolderException(String.format("current status: %s action: %s", this.name(), action.name()));
    } catch (InvoiceActionIsProhibitedForCurrencyPermissionOperationException e){
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

  static {
    for (IeoStatusEnum status : IeoStatusEnum.class.getEnumConstants()) {
      status.initSchema(status.schemaMap);
    }
    /*check schemaMap*/
    getBeginState();
  }

  public static List<InvoiceStatus> getAvailableForActionStatusesList(InvoiceActionTypeEnum action) {
    return Arrays.stream(IeoStatusEnum.class.getEnumConstants())
        .filter(e -> e.availableForAction(action))
        .collect(Collectors.toList());
  }

  public static List<InvoiceStatus> getAvailableForActionStatusesList(List<InvoiceActionTypeEnum> action) {
    return Arrays.stream(IeoStatusEnum.class.getEnumConstants())
        .filter(e -> action.stream().filter(e::availableForAction).findFirst().isPresent())
        .collect(Collectors.toList());
  }

  public Set<InvoiceActionTypeEnum> getAvailableActionList() {
    schemaMap.keySet().forEach(InvoiceActionTypeEnum::checkRestrictParamNeeded);
    return schemaMap.keySet();
  }

  public Set<InvoiceActionTypeEnum> getAvailableActionList(InvoiceActionParamsValue paramsValue) {
    return schemaMap.keySet()
            .stream()
        .filter(e->e.isMatchesTheParamsValue(paramsValue))
        .collect(Collectors.toSet());
  }

  /**/

  public static IeoStatusEnum convert(int id) {
    return Arrays.stream(IeoStatusEnum.class.getEnumConstants())
        .filter(e -> e.code == id)
        .findAny()
        .orElseThrow(() -> new UnsupportedWithdrawRequestStatusIdException(String.valueOf(id)));
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

  public static Set<InvoiceStatus> getMiddleStatesSet() {
    return Arrays.stream(IeoStatusEnum.class.getEnumConstants())
        .filter(e -> !e.schemaMap.isEmpty())
        .collect(Collectors.toSet());
  }

  public static Set<InvoiceStatus> getEndStatesSet() {
    return Arrays.stream(IeoStatusEnum.class.getEnumConstants())
        .filter(e -> e.schemaMap.isEmpty())
        .collect(Collectors.toSet());
  }

  public static InvoiceStatus getInvoiceStatusAfterAction(InvoiceActionTypeEnum action) {
    TreeSet<InvoiceStatus> statusSet = new TreeSet(
        Arrays.stream(IeoStatusEnum.class.getEnumConstants())
            .filter(e -> e.availableForAction(action))
            .map(e -> e.nextState(action))
            .collect(Collectors.toList()));
    if (statusSet.size() == 0) {
      log.fatal("no state found !");
      throw new AssertionError();
    }
    if (statusSet.size() > 1) {
      log.fatal("more then one state found !");
      throw new AssertionError();
    }
    return statusSet.first();
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
        .filter(e -> e.getKey().isLeadsToSuccessFinalState())
        .findAny()
        .isPresent();
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

  public InvoiceActionTypeEnum getStartAction(Merchant merchant) {
    if (merchant.getProcessType() == MerchantProcessType.INVOICE) {
      return PUT_FOR_CONFIRM_USER;
    } else {
      return PUT_FOR_PENDING;
    }
  }

}

