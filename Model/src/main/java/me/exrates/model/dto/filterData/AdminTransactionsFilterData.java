package me.exrates.model.dto.filterData;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import me.exrates.model.enums.TransactionType;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

import static me.exrates.model.dto.filterData.FilterDataItem.DATE_FORMAT;
import static me.exrates.model.dto.filterData.FilterDataItem.IN_FORMAT;

/**
 * Created by OLEG on 14.04.2017.
 */
@Getter @Setter
@NoArgsConstructor
@ToString
public class AdminTransactionsFilterData extends TableFilterData {
  private Integer status;
  private List<TransactionType> types;
  private List<Integer> merchants;
  private String startDate;
  private String endDate;
  private BigDecimal amountFrom;
  private BigDecimal amountTo;
  private BigDecimal commissionAmountFrom;
  private BigDecimal commissionAmountTo;
  
  @Override
  public void initFilterItems() {
    Set<String> sourceTypes = new HashSet<>();
    Set<Integer> operationTypes = new HashSet<>();


   
    FilterDataItem[] items = new FilterDataItem[] {
            new FilterDataItem("provided", "TRANSACTION.provided =", status),
            new FilterDataItem("date_from", "TRANSACTION.datetime >=", startDate, DATE_FORMAT),
            new FilterDataItem("date_to", "TRANSACTION.datetime <=", endDate, DATE_FORMAT),
            /*new FilterDataItem("operation_types", "TRANSACTION.operation_type_id IN", operationTypes, IN_FORMAT),
            new FilterDataItem("source_types", "TRANSACTION.source_type IN", sourceTypes, IN_FORMAT),*/
            new FilterDataItem("merchants", "TRANSACTION.merchant_id IN", merchants, IN_FORMAT),
            new FilterDataItem("amount_from", "TRANSACTION.amount >=", amountFrom),
            new FilterDataItem("amount_to", "TRANSACTION.amount <=", amountTo),
            new FilterDataItem("commission_amount_from", "TRANSACTION.commission_amount >=", commissionAmountFrom),
            new FilterDataItem("commission_amount_to", "TRANSACTION.commission_amount <=", commissionAmountTo),
    };
    populateFilterItemsNonEmpty(items);
    
  }

  public String getTransationTypeClauses() {
    String allClause = null;
    if (types != null) {
      List<String> clauses = new ArrayList<>();
      types.forEach(item -> {
        List<String> oneClause = new ArrayList<>();
        if (item.getOperationType() != null) {
          oneClause.add(" TRANSACTION.operation_type_id = ".concat(String.valueOf(item.getOperationType().getType())));
        }
        if (item.getSourceType() != null) {
          oneClause.add(" TRANSACTION.source_type = '".concat(String.valueOf(item.getSourceType().toString()))+"'");
        }
        if (item.getAmountPredicate() != null ) {
          String part3;
          if (item.getAmountPredicate().test(new BigDecimal(-1))) {
            oneClause.add(" TRANSACTION.amount < 0 ");
          } else {
            oneClause.add(" TRANSACTION.amount >= 0 ");
          }
        }
        String oneClauseStr = " ( " + oneClause.stream().map(i -> i.concat(" ")).collect(Collectors.joining(" AND "))
                + " )";
        clauses.add(oneClauseStr);
      });
      allClause = "( " + clauses.stream().map(i -> i.concat(" ")).collect(Collectors.joining(" OR ")) + " )";
    }
    return allClause;
  }
 
  private static final Map<String, String> TABLE_TO_DB_COLUMN_MAP = new HashMap<String, String>() {{
    
    put("orderedDatetime", "TRANSACTION.datetime+TRANSACTION.id");
    put("datetime", "TRANSACTION.datetime");
    put("operationType", "TRANSACTION.operation_type_id");
    put("amount", "TRANSACTION.amount");
    put("status", "TRANSACTION.provided");
    put("currency", "CURRENCY.name");
    put("merchant.description", "MERCHANT.description");
    put("commissionAmount", "TRANSACTION.commission_amount");
    put("order", "TRANSACTION.source_id");
    
  }};
  
  
  
}
  
