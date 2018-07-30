package me.exrates.service.nodes_observe;

import me.exrates.model.enums.NodeMonitorMethods;

import java.util.HashMap;
import java.util.Map;

public interface NodeStateControl {

    /*Map<String, Long> should contains keys with method names - 'isNodeInWork' and 'isNodeWorkCorrect' and values of desired polling intervals in seconds */
    default Map<String, Long> getPollingPeriods() {
        return new HashMap<String, Long>() {{
            put(NodeMonitorMethods.isNodeInWork.name(), 300L);
            put(NodeMonitorMethods.isNodeWorkCorrect.name(), 600L);
        }};
    };

    boolean isNodeInWork();

    boolean isNodeWorkCorrect();

    String nodeName();
}
