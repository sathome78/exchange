package me.exrates;

import com.neemre.btcdcli4j.core.BitcoindException;
import com.neemre.btcdcli4j.core.CommunicationException;
import me.exrates.service.exception.CoinTestException;

import java.io.IOException;

public interface CoinTester {
    void initBot(String name, StringBuilder stringBuilder, String email) throws Exception;
    String testCoin(String refillAmount) throws Exception;
}
