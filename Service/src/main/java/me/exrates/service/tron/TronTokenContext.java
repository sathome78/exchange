package me.exrates.service.tron;

import me.exrates.model.condition.MonolitConditional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Conditional;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Component
@Conditional(MonolitConditional.class)
public class TronTokenContext {

    @Autowired
    private Map<String, TronTrc10Token> tokensMap;

    TronTrc10Token getByNameTx(String nameTx) {
        return tokensMap.values()
                .stream()
                .filter(p -> p.getNameTx().equals(nameTx))
                .findFirst()
                .orElseThrow(RuntimeException::new);
    }

    TronTrc10Token getByCurrencyId(int currencyId) {
        return tokensMap.values()
                .stream()
                .filter(p -> p.getCurrencyId() == currencyId)
                .findFirst()
                .orElseThrow(RuntimeException::new);
    }

    List<TronTrc10Token> getAll() {
        return new ArrayList<>(tokensMap.values());
    }
}
