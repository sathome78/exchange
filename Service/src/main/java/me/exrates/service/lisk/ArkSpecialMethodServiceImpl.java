package me.exrates.service.lisk;

import me.exrates.model.dto.merchants.lisk.ArkSendTxDto;
import me.exrates.model.dto.merchants.lisk.LiskAccount;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.PostConstruct;

public class ArkSpecialMethodServiceImpl implements LiskSpecialMethodService {

    @Autowired
    private ArkRpcClient arkRpcClient;

    private final Object SEND_TX_LOCK = new Object();

    private String propertySource;

    public ArkSpecialMethodServiceImpl(String propertySource) {
        this.propertySource = propertySource;
    }

    @PostConstruct
    private void init() {
        arkRpcClient.initClient(propertySource);

    }

    @Override
    public String sendTransaction(String secret, Long amount, String recipientId) {
        ArkSendTxDto dto = new ArkSendTxDto();
        dto.setPassphrase(secret);
        dto.setAmount(amount);
        dto.setRecipientId(recipientId);
        String txId;
        synchronized (SEND_TX_LOCK) {
            txId = arkRpcClient.createTransaction(dto);
            arkRpcClient.broadcastTransaction(txId);
        }
        return txId;
    }

    @Override
    public LiskAccount createAccount(String secret) {
        return arkRpcClient.createAccount(secret);
    }
}
