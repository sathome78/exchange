package me.exrates.service;

import info.blockchain.api.APIException;
import info.blockchain.api.receive.Receive;
import info.blockchain.api.receive.ReceiveResponse;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public class BlockchainSDKWrapper {

    public ReceiveResponse receive(final String xPub, final String callback, final String apiCode)
            throws APIException, IOException
    {
        return Receive.receive(xPub, callback, apiCode);
    }
}