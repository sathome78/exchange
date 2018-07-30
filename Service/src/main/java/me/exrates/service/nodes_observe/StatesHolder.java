package me.exrates.service.nodes_observe;


import me.exrates.model.dto.NodesInfoDto;
import org.jvnet.hk2.annotations.Service;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.Map;

@Service
public class StatesHolder {

    Map<String, NodesInfoDto> map = new HashMap<>();

    @PostConstruct
    private void init() {
        /*init map here*/
    }

    public Map<String, NodesInfoDto> getMap() {
        return map;
    }
}
