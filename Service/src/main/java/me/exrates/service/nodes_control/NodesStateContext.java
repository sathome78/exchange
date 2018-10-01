package me.exrates.service.nodes_control;

import me.exrates.model.dto.NodesInfoDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.Map;

@Component
public class NodesStateContext {

    @Autowired
    private Map<String, NodeStateControl> map;

    private Map<String, NodeStateControl> mapByNodeName;

    @PostConstruct
    private void init() {
        map.forEach((k,v) -> {
            mapByNodeName.put(v.getCurrencyName(), v);
        });

    }

    public NodeStateControl getByCurrencyName(String currencyName) {
        return mapByNodeName.get(currencyName);
    }
}
