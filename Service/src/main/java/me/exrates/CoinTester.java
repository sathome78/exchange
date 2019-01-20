package me.exrates;

import com.neemre.btcdcli4j.core.BitcoindException;
import com.neemre.btcdcli4j.core.CommunicationException;

import java.io.IOException;

public interface CoinTester {
    void testCoin(String ticker, double refillAmount) throws IOException, BitcoindException, CommunicationException, InterruptedException;
}
