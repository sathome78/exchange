package me.exrates.service.nodes_control;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.Map;

@Component
public class NodesStateContext {

    private final Map<String, NodeStateControl> map;
    private Map<String, NodeStateControl> byCurrencyName;

    @Autowired
    public NodesStateContext(Map<String, NodeStateControl> map) {
        this.map = map;
    }

    @PostConstruct
    private void init() {
        byCurrencyName = new HashMap<>();
        map.forEach((k,v) -> byCurrencyName.put(v.getCurrencyName(), v));

    }
    public NodeStateControl getByCurrencyName(String currencyName) {
        return byCurrencyName.get(currencyName);
    }
}
