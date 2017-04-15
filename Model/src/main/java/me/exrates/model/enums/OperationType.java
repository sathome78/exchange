package me.exrates.model.enums;

import lombok.EqualsAndHashCode;
import me.exrates.model.exceptions.UnsupportedOperationTypeException;
import org.springframework.context.MessageSource;

import java.util.*;

public enum OperationType {
    INPUT(1){{
        /*Addition of three digits is required for IDR input*/
        currencyForAddRandomValueToAmount.put(10, new AdditionalRandomAmountParam(){{
            currencyName = "IDR";
            lowBound = 100;
            highBound = 999;
        }});
    }},
    OUTPUT(2),
    SELL(3),
    BUY(4),
    WALLET_INNER_TRANSFER(5),
    REFERRAL(6),
    STORNO(7),
    MANUAL(8),
    USER_TRANSFER(9);

    public class AdditionalRandomAmountParam {
        public String currencyName;
        public double lowBound;
        public double highBound;

    @Override
    public boolean equals(Object currencyName) {
        return this.currencyName.equals((String)currencyName);
    }

    @Override
    public int hashCode() {
        return currencyName != null ? currencyName.hashCode() : 0;
    }
}

    public final int type;

    protected final Map<Integer, AdditionalRandomAmountParam> currencyForAddRandomValueToAmount = new HashMap<>();

    OperationType(int type) {
        this.type = type;
    }

    public Optional<AdditionalRandomAmountParam> getRandomAmountParam(Integer currencyId){
        return Optional.ofNullable(currencyForAddRandomValueToAmount.get(currencyId));
    }

    public Optional<AdditionalRandomAmountParam> getRandomAmountParam(String currencyName){
        return currencyForAddRandomValueToAmount.values().stream()
            .filter(e->e.equals(currencyName))
            .findAny();
    }

    public static List<OperationType> getInputOutputOperationsList(){
        return new ArrayList<OperationType>(){{
            add(INPUT);
            add(OUTPUT);
        }};
    }

    public static OperationType getOpposite(OperationType ot) {
        switch (ot) {
            case INPUT:
                return OUTPUT;
            case OUTPUT:
                return INPUT;
            case SELL:
                return BUY;
            case BUY:
                return SELL;
            default:
                return ot;
        }
    }

    public int getType() {
        return type;
    }

    public static OperationType convert(int id) {
        return Arrays.stream(OperationType.class.getEnumConstants())
            .filter(e -> e.type == id)
            .findAny()
            .orElseThrow(() -> new UnsupportedOperationTypeException(id));
    }

    public String toString(MessageSource messageSource, Locale locale) {
        return messageSource.getMessage("operationtype." + this.name(), null, locale);
    }
}
