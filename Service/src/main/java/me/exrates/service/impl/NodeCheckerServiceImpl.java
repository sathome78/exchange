package me.exrates.service.impl;

import com.neemre.btcdcli4j.core.BitcoindException;
import com.neemre.btcdcli4j.core.CommunicationException;
import me.exrates.service.BitcoinService;
import me.exrates.service.NodeCheckerService;
import me.exrates.service.merchantStrategy.IRefillable;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class NodeCheckerServiceImpl implements NodeCheckerService {

    private final Map<String, IRefillable> btcNodeMap = new HashMap<>();;

    public NodeCheckerServiceImpl(Map<String, BitcoinService> bitcoinServiceMap) {
        for (Map.Entry<String, BitcoinService> entry : bitcoinServiceMap.entrySet()) {
            btcNodeMap.put(entry.getValue().getMerchantName(), entry.getValue());
        }
    }

    @Override
    public Long getBTCBlocksCount(String ticker) {
        try {
            return btcNodeMap.get(ticker).getBlocksCount();
        } catch (Exception e){
            return null;
        }
    }

    @Override
    public List<String> listOfRefillableServicesNames() {
        return new LinkedList<>(btcNodeMap.keySet());
    }

    @Override
    public Long getLastBlockTime(String ticker) throws BitcoindException, CommunicationException {
        return  btcNodeMap.get(ticker).getLastBlockTime();
    }
}