package me.exrates.model.enums.invoice;


import lombok.extern.log4j.Log4j2;
import me.exrates.model.exceptions.*;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

import static me.exrates.model.enums.invoice.InvoiceActionTypeEnum.*;

/**
 * Created by ValkSam
 */
@Log4j2
public enum WithdrawStatusEnum implements InvoiceStatus {
  CREATED_USER(1) {
    @Override
    public void initSchema(Map<InvoiceActionTypeEnum, InvoiceStatus> schemaMap) {
      schemaMap.put(PUT_FOR_MANUAL, WAITING_MANUAL_POSTING);
      schemaMap.put(InvoiceActionTypeEnum.PUT_FOR_AUTO, WAITING_AUTO_POSTING);
      schemaMap.put(InvoiceActionTypeEnum.PUT_FOR_CONFIRM, WAITING_CONFIRMATION);
    }
  },
  WAITING_MANUAL_POSTING(2) {
    @Override
    public void initSchema(Map<InvoiceActionTypeEnum, InvoiceStatus> schemaMap) {
      schemaMap.put(InvoiceActionTypeEnum.TAKE_TO_WORK, IN_WORK_OF_ADMIN);
      schemaMap.put(InvoiceActionTypeEnum.REVOKE, REVOKED_USER);
    }
  },
  WAITING_AUTO_POSTING(3) {
    @Override
    public void initSchema(Map<InvoiceActionTypeEnum, InvoiceStatus> schemaMap) {
      schemaMap.put(InvoiceActionTypeEnum.DECLINE, DECLINED_ADMIN);
      schemaMap.put(InvoiceActionTypeEnum.HOLD_TO_POST, IN_POSTING);
      schemaMap.put(InvoiceActionTypeEnum.REVOKE, REVOKED_USER);
    }
  },
  WAITING_CONFIRMATION(4) {
    @Override
    public void initSchema(Map<InvoiceActionTypeEnum, InvoiceStatus> schemaMap) {
      schemaMap.put(InvoiceActionTypeEnum.DECLINE, DECLINED_ADMIN);
      schemaMap.put(InvoiceActionTypeEnum.CONFIRM_ADMIN, WAITING_CONFIRMED_POSTING);
      schemaMap.put(InvoiceActionTypeEnum.REVOKE, REVOKED_USER);
    }
  },
  IN_WORK_OF_ADMIN(5) {
    @Override
    public void initSchema(Map<InvoiceActionTypeEnum, InvoiceStatus> schemaMap) {
      schemaMap.put(InvoiceActionTypeEnum.DECLINE_HOLDED, DECLINED_ADMIN);
      schemaMap.put(InvoiceActionTypeEnum.POST_HOLDED, POSTED_MANUAL);
      schemaMap.put(InvoiceActionTypeEnum.RETURN_FROM_WORK, WAITING_MANUAL_POSTING);
    }
  },
  WAITING_CONFIRMED_POSTING(6) {
    @Override
    public void initSchema(Map<InvoiceActionTypeEnum, InvoiceStatus> schemaMap) {
      schemaMap.put(InvoiceActionTypeEnum.DECLINE, DECLINED_ADMIN);
      schemaMap.put(InvoiceActionTypeEnum.HOLD_TO_POST, IN_POSTING);
      schemaMap.put(InvoiceActionTypeEnum.REVOKE, REVOKED_USER);
    }
  },
  REVOKED_USER(7) {
    @Override
    public void initSchema(Map<InvoiceActionTypeEnum, InvoiceStatus> schemaMap) {
    }
  },
  DECLINED_ADMIN(8) {
    @Override
    public void initSchema(Map<InvoiceActionTypeEnum, InvoiceStatus> schemaMap) {
    }
  },
  POSTED_MANUAL(9) {
    @Override
    public void initSchema(Map<InvoiceActionTypeEnum, InvoiceStatus> schemaMap) {
    }
  },
  POSTED_AUTO(10) {
    @Override
    public void initSchema(Map<InvoiceActionTypeEnum, InvoiceStatus> schemaMap) {
    }
  },
  IN_POSTING(11) {
    @Override
    public void initSchema(Map<InvoiceActionTypeEnum, InvoiceStatus> schemaMap) {
      schemaMap.put(InvoiceActionTypeEnum.POST_AUTO, POSTED_AUTO);
      schemaMap.put(InvoiceActionTypeEnum.REJECT_TO_REVIEW, WAITING_MANUAL_POSTING);
      schemaMap.put(InvoiceActionTypeEnum.REJECT_ERROR, DECLINED_ERROR);
    }
  },
  DECLINED_ERROR(12) {
    @Override
    public void initSchema(Map<InvoiceActionTypeEnum, InvoiceStatus> schemaMap) {
    }
  };

  final private Map<InvoiceActionTypeEnum, InvoiceStatus> schemaMap = new HashMap<>();

  @Override
  public InvoiceStatus nextState(InvoiceActionTypeEnum action) {
    if (action.isAvailableForHolderOnly()) {
      throw new AuthorisedUserIsHolderParamNeededForThisStatusException(action.name());
    }
    if (action.getOperationPermissionOnlyList() != null) {
      throw new PermittedOperationParamNeededForThisStatusException(action.name());
    }
    return nextState(schemaMap, action)
        .orElseThrow(() -> new UnsupportedInvoiceStatusForActionException(String.format("current state: %s action: %s", this.name(), action.name())));
  }

  @Override
  public InvoiceStatus nextState(InvoiceActionTypeEnum action, Boolean authorisedUserIsHolder, InvoiceOperationPermission permittedOperation) {
    if (action.isAvailableForHolderOnly() && !authorisedUserIsHolder) {
      throw new InvoiceActionIsProhibitedForNotHolderException(String.format("current status: %s action: %s", this.name(), action.name()));
    }
    if (action.getOperationPermissionOnlyList() != null && !action.getOperationPermissionOnlyList().contains(permittedOperation)) {
      throw new InvoiceActionIsProhibitedForCurrencyPermissionOperationException(String.format("current status: %s action: %s permittedOperation: %s", this.name(), action.name(), permittedOperation.name()));
    }
    return nextState(schemaMap, action)
        .orElseThrow(() -> new UnsupportedInvoiceStatusForActionException(String.format("current state: %s action: %s", this.name(), action.name())));
  }

  @Override
  public Boolean availableForAction(InvoiceActionTypeEnum action) {
    return availableForAction(schemaMap, action);
  }

  static {
    for (WithdrawStatusEnum status : WithdrawStatusEnum.class.getEnumConstants()) {
      status.initSchema(status.schemaMap);
    }
    /*check schemaMap*/
    getBeginState();
  }

  public static List<InvoiceStatus> getAvailableForActionStatusesList(InvoiceActionTypeEnum action) {
    return Arrays.stream(WithdrawStatusEnum.class.getEnumConstants())
        .filter(e -> e.availableForAction(action))
        .collect(Collectors.toList());
  }

  public static List<InvoiceStatus> getAvailableForActionStatusesList(List<InvoiceActionTypeEnum> action) {
    return Arrays.stream(WithdrawStatusEnum.class.getEnumConstants())
        .filter(e -> action.stream().filter(e::availableForAction).findFirst().isPresent())
        .collect(Collectors.toList());
  }

  public Set<InvoiceActionTypeEnum> getAvailableActionList() {
    schemaMap.keySet().stream()
        .filter(InvoiceActionTypeEnum::isAvailableForHolderOnly)
        .findAny()
        .ifPresent(action -> {
          throw new AuthorisedUserIsHolderParamNeededForThisStatusException(action.name());
        });
    schemaMap.keySet().stream()
        .filter(e -> e.getOperationPermissionOnlyList() != null)
        .findAny()
        .ifPresent(action -> {
          throw new PermittedOperationParamNeededForThisStatusException(action.name());
        });
    return schemaMap.keySet();
  }

  public Set<InvoiceActionTypeEnum> getAvailableActionList(Boolean authorisedUserIsHolder, InvoiceOperationPermission permittedOperation) {
    return schemaMap.keySet().stream()
        .filter(e -> (!e.isAvailableForHolderOnly() || authorisedUserIsHolder) &&
            (e.getOperationPermissionOnlyList() == null || e.getOperationPermissionOnlyList().contains(permittedOperation)))
        .collect(Collectors.toSet());
  }

  /**/

  public static WithdrawStatusEnum convert(int id) {
    return Arrays.stream(WithdrawStatusEnum.class.getEnumConstants())
        .filter(e -> e.code == id)
        .findAny()
        .orElseThrow(() -> new UnsupportedWithdrawRequestStatusIdException(String.valueOf(id)));
  }

  public static WithdrawStatusEnum convert(String name) {
    return Arrays.stream(WithdrawStatusEnum.class.getEnumConstants())
        .filter(e -> e.name().equals(name))
        .findAny()
        .orElseThrow(() -> new UnsupportedWithdrawRequestStatusNameException(name));
  }

  public static InvoiceStatus getBeginState() {
    Set<InvoiceStatus> allNodesSet = collectAllSchemaMapNodesSet();
    List<InvoiceStatus> candidateList = Arrays.stream(WithdrawStatusEnum.class.getEnumConstants())
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
    return Arrays.stream(WithdrawStatusEnum.class.getEnumConstants())
        .filter(e -> !e.schemaMap.isEmpty())
        .collect(Collectors.toSet());
  }

  public static Set<InvoiceStatus> getEndStatesSet() {
    return Arrays.stream(WithdrawStatusEnum.class.getEnumConstants())
        .filter(e -> e.schemaMap.isEmpty())
        .collect(Collectors.toSet());
  }

  public static InvoiceStatus getInvoiceStatusAfterAction(InvoiceActionTypeEnum action) {
    TreeSet<InvoiceStatus> statusSet = new TreeSet(
        Arrays.stream(WithdrawStatusEnum.class.getEnumConstants())
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
    Arrays.stream(WithdrawStatusEnum.class.getEnumConstants())
        .forEach(e -> schema.putAll(e.schemaMap));
    return schema.entrySet().stream()
        .filter(e -> e.getValue() == this)
        .filter(e -> e.getKey().isLeadsToSuccessFinalState())
        .findAny()
        .isPresent();
  }

  private static Set<InvoiceStatus> collectAllSchemaMapNodesSet() {
    Set<InvoiceStatus> result = new HashSet<>();
    Arrays.stream(WithdrawStatusEnum.class.getEnumConstants())
        .forEach(e -> result.addAll(e.schemaMap.values()));
    return result;
  }

  private Integer code;

  WithdrawStatusEnum(Integer code) {
    this.code = code;
  }

  @Override
  public Integer getCode() {
    return code;
  }

  public InvoiceActionTypeEnum getStartAction(Boolean autoEnabled, BigDecimal withdrawAutoEnabled, BigDecimal withdrawAutoThresholdAmount) {
    if (autoEnabled) {
      if (withdrawAutoEnabled.compareTo(withdrawAutoThresholdAmount) <= 0) {
        return PUT_FOR_AUTO;
      } else {
        return PUT_FOR_CONFIRM;
      }
    } else {
      return PUT_FOR_MANUAL;
    }
  }

}

