package me.exrates;

import com.neemre.btcdcli4j.core.BitcoindException;
import com.neemre.btcdcli4j.core.CommunicationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.IOException;

@Service
public class ActuatorTestDelete {

    @Autowired
    BtcCoinTesterImpl btcCoinTester;

    @PostConstruct
    public void test() throws BitcoindException, IOException, CommunicationException, InterruptedException {
        btcCoinTester.testCoin("KOD", 0.001);
    }
}
