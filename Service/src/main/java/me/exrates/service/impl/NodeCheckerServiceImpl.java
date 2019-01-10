package me.exrates.service.impl;

import com.neemre.btcdcli4j.core.BitcoindException;
import com.neemre.btcdcli4j.core.CommunicationException;
import me.exrates.service.NodeCheckerService;
import me.exrates.service.merchantStrategy.IRefillable;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class NodeCheckerServiceImpl implements NodeCheckerService {

    private final Map<String, IRefillable> nodeMap;

    public NodeCheckerServiceImpl(Map<String, IRefillable> bitcoinServiceMap) {
        this.nodeMap = bitcoinServiceMap;
    }

    @Override
    public Long getBTCBlocksCount(String ticker) throws BitcoindException, CommunicationException {
        try {
            for (Map.Entry<String, IRefillable> entry : nodeMap.entrySet()) {
                IRefillable service = entry.getValue();
                String merchantName = service.getMerchantName();
                if (merchantName.equalsIgnoreCase(ticker)) {
                    return service.getBlocksCount();
                }
            }
        } catch (Exception e){
            return null;
        }
        return 0L;
    }
}
