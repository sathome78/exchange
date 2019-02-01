package me.exrates;

import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Service;
import org.web3j.crypto.Credentials;
import org.web3j.crypto.WalletUtils;

import java.io.File;

@Service
public class ActuatorTestDelete implements
        ApplicationListener<ContextRefreshedEvent>{

    private int counter = 0;

    @Override
    public void onApplicationEvent(ContextRefreshedEvent contextRefreshedEvent) {
        counter++;
        if(counter == 1) new Thread(() -> {test(contextRefreshedEvent); }).start();
    }

    private void test(ContextRefreshedEvent contextRefreshedEvent) {
        CoinTester kodTester = (CoinTester) contextRefreshedEvent.getApplicationContext().getBean("ethTokenTester");
        try {
            kodTester.initBot("DGTX", new StringBuilder());
            kodTester.testCoin(1);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
