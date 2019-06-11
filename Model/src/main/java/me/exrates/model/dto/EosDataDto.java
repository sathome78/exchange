package me.exrates.model.dto;

import lombok.Data;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.util.LinkedHashMap;

@Data
public class EosDataDto {

    private BigDecimal amount;

    private String currency;

    private String quantity;

    private String fromAccount;

    private String toAccount;

    private String memo;


    public EosDataDto(LinkedHashMap map) {
        this.quantity = map.get("quantity").toString();
        String[] fullAmountString = quantity.split(" ");
        this.amount = new BigDecimal(fullAmountString[0]);
        this.currency = fullAmountString[1];
        this.fromAccount = map.get("from").toString();
        this.toAccount = map.get("to").toString();
        this.memo = map.get("memo").toString();
    }
}
