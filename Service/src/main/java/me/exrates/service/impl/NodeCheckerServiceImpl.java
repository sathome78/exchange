package me.exrates.service.impl;

import com.neemre.btcdcli4j.core.BitcoindException;
import com.neemre.btcdcli4j.core.CommunicationException;
import me.exrates.service.NodeCheckerService;
import me.exrates.service.merchantStrategy.IRefillable;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

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

    @Override
    public List<String> listOfRefillableServicesNames() {
        return new LinkedList<>(merchantNodeMap.keySet());
    }

    @Override
    public Long getLastBlockTime(String ticker) throws BitcoindException, CommunicationException {
        return  merchantNodeMap.get(ticker).getLastBlockTime();
    }
}