package me.exrates.service.stopOrder;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import lombok.extern.log4j.Log4j2;
import me.exrates.model.enums.OperationType;
import me.exrates.service.CurrencyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.math.BigDecimal;

/**
 * Created by maks on 22.04.2017.
 */
@Log4j2
@Component
public class RatesHolderImpl implements RatesHolder {

    @Autowired
    private CurrencyService currencyService;

    /*contains currency pairId and its rate*/
    private Table<Integer, OperationType, BigDecimal> ratesMap = HashBasedTable.create();

    @PostConstruct
    public void init() {
    }

    @Override
    public void onRateChange(int pairId, OperationType operationType, BigDecimal rate) {
        ratesMap.put(pairId, operationType, rate);
    }

    @Override
    public BigDecimal getCurrentRate(int pairId, OperationType operationType) {
        return ratesMap.get(pairId, operationType);
    }



}
