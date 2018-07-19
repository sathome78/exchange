package me.exrates.service.nodes_observe;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class NodedsStateContext {

    @Autowired
    private Map<String, NodeStateControl> map;
}
