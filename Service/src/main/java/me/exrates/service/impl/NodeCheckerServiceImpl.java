package me.exrates.service.impl;

import me.exrates.service.NodeCheckerService;
import me.exrates.service.merchantStrategy.IRefillable;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class NodeCheckerServiceImpl implements NodeCheckerService {

    private final Map<String, IRefillable> merchantNodeMap;

    public NodeCheckerServiceImpl(Map<String, IRefillable> bitcoinServiceMap) {
        merchantNodeMap = new HashMap<>();
        for (Map.Entry<String, IRefillable> entry : bitcoinServiceMap.entrySet()) {
            merchantNodeMap.put(entry.getValue().getMerchantName(), entry.getValue());
        }
    }

    @Override
    public Long getBTCBlocksCount(String ticker) {
        try {
            return merchantNodeMap.get(ticker).getBlocksCount();
        } catch (Exception e){
            return null;
        }
    }
}