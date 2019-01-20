package me.exrates;

import com.neemre.btcdcli4j.core.BitcoindException;
import com.neemre.btcdcli4j.core.CommunicationException;

import java.io.IOException;

public interface CoinTester {
    void initBot(String name) throws BitcoindException, IOException, CommunicationException;
    void testCoin(double refillAmount) throws IOException, BitcoindException, CommunicationException, InterruptedException;
}
