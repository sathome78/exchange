package me.exrates.service.nodes_control;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.Map;

@Component
public class NodesStateContext {

    @Autowired
    private Map<String, NodeStateControl> map;

    private Map<String, NodeStateControl> byCurrencyName;

    @PostConstruct
    private void init() {
        map.forEach((k,v) -> {
            byCurrencyName.put(v.getCurrencyName(), v);
        });

    }

    public NodeStateControl getByCurrencyName(String currencyName) {
        return byCurrencyName.get(currencyName);
    }
}
