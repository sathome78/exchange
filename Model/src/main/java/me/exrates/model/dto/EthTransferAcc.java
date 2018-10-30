package me.exrates.model.dto;


import lombok.Data;
import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.http.HttpService;

import java.util.concurrent.atomic.AtomicInteger;

@Data
public class EthTransferAcc {

    private int id;
    private Web3j web3j;
    private Credentials credentials;
    private AtomicInteger countOfUses = new AtomicInteger(0);
    private String address;

    public EthTransferAcc(EthAccCredentials accCredentials) {
        this.id = accCredentials.getId();
        web3j = Web3j.build(new HttpService(accCredentials.getUrl()));
        credentials = Credentials.create(accCredentials.getPrivateKey());
        this.address = credentials.getAddress();
    }

    public EthTransferAcc useIt() {
        countOfUses.incrementAndGet();
        return this;
    }

    public int returnFromUse() {
        return countOfUses.decrementAndGet();
    }
}
