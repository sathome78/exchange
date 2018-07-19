package me.exrates.service.nodes_observe;

import java.util.HashMap;
import java.util.Map;

public interface NodeStateControl {

    String method1Name = "isNodeInWork";

    String method2NAme = "isNodeWorkCorrect";

    /*Map<String, Long> should contains keys with method names - 'isNodeInWork' and 'isNodeWorkCorrect' and values of desired polling intervals in seconds */
    default Map<String, Long> getPollingPeriods() {
        return new HashMap<String, Long>() {{
            put(method1Name, 300L);
            put(method2NAme, 600L);
        }};
    };

    boolean isNodeInWork();

    boolean isNodeWorkCorrect();

    String nodeName();
}
