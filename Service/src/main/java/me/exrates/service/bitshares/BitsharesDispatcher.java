package me.exrates.service.bitshares;

import me.exrates.model.TransactionsInfo;
import me.exrates.model.dto.BTSBlockInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class BitsharesDispatcher {
    private final Map<String, BitsharesService> bitsharesServiceMap;

    @Autowired
    public BitsharesDispatcher(Map<String, BitsharesService> bitsharesServiceMap) {
        this.bitsharesServiceMap = bitsharesServiceMap;
    }

    public void requestForBlockInfo(String merchantName, BTSBlockInfo BTSBlockInfo) throws Exception {
        BitsharesService service = getServiceByMerchantName(merchantName);
        service.requestBlockTransactionsInfo(BTSBlockInfo);
    }

    public TransactionsInfo getBlocksInfo(String merchantName, int blockNum) throws Exception {
        return getServiceByMerchantName(merchantName).getRequestedBlocksInfo(blockNum);
    }

    private BitsharesService getServiceByMerchantName(String merchantName) throws Exception {
        return bitsharesServiceMap.entrySet().stream().filter(e -> e.getValue().getMerchantName().equals(merchantName)).findFirst().orElseThrow(() -> new Exception("Merchant service not working")).getValue();
    }
}
