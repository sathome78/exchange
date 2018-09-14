package me.exrates.service.achain;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Maks on 15.06.2018.
 */
@Service
public class AchainTokenContext {


    private final Map<String, AchainContract> contractsMap;

    private Map<String, AchainContract> conractIdMap = new HashMap<>();

    @Autowired
    public AchainTokenContext(Map<String, AchainContract> contractsMap) {
        this.contractsMap = contractsMap;
    }

    @PostConstruct
    private void init() {
        contractsMap.forEach((k,v)-> {
            conractIdMap.put(v.getContract(), v);
        });
    }

    AchainContract getByContractId(String contractId) {
        return conractIdMap.get(contractId);
    }
}
