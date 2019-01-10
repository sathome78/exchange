package me.exrates.service.impl;

import com.neemre.btcdcli4j.core.BitcoindException;
import com.neemre.btcdcli4j.core.CommunicationException;
import me.exrates.service.NodeCheckerService;
import me.exrates.service.merchantStrategy.IRefillable;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class NodeCheckerServiceImpl implements NodeCheckerService {

    private final Map<String, IRefillable> nodeMap;
    private final Map<String, IRefillable> merchantNodeMap;

    public NodeCheckerServiceImpl(Map<String, IRefillable> bitcoinServiceMap) {
        this.nodeMap = bitcoinServiceMap;
        merchantNodeMap = new HashMap<>();
        for (Map.Entry<String, IRefillable> entry : nodeMap.entrySet()) {
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
