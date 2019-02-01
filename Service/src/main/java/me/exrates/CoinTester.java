package me.exrates;

import com.neemre.btcdcli4j.core.BitcoindException;
import com.neemre.btcdcli4j.core.CommunicationException;
import me.exrates.service.exception.CoinTestException;

import java.io.IOException;

public interface CoinTester {
    void initBot(String name, StringBuilder stringBuilder) throws Exception;
    String testCoin(double refillAmount) throws IOException, BitcoindException, CommunicationException, InterruptedException, CoinTestException, Exception;
}
