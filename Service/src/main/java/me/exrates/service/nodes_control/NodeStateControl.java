package me.exrates.service.nodes_control;

import me.exrates.model.enums.NodeMonitorMethods;

import java.util.HashMap;
import java.util.Map;

public interface NodeStateControl {


    boolean isNodeWorkCorrect();

    String getBalance();

    String getMerchantName();

    String getCurrencyName();
}
