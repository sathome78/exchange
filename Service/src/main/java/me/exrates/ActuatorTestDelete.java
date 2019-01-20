package me.exrates;

import me.exrates.service.achain.test;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Scope;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Service;

@Service
@Scope
public class ActuatorTestDelete implements
        ApplicationListener<ContextRefreshedEvent>{

//    private final BtcCoinTesterImpl btcCoinTester;
//
//    public ActuatorTestDelete(BtcCoinTesterImpl btcCoinTester) {
//        this.btcCoinTester = btcCoinTester;
//
//        new Thread(() -> {
//            try {
//                btcCoinTester.init("KOD");
//                btcCoinTester.testCoin("KOD", 0.001);
//            } catch (IOException e) {
//                e.printStackTrace();
//            } catch (BitcoindException e) {
//                e.printStackTrace();
//            } catch (CommunicationException e) {
//                e.printStackTrace();
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
//        }).start();
//    }

    @Override
    public void onApplicationEvent(ContextRefreshedEvent contextRefreshedEvent) {
        new Thread(() -> {test(contextRefreshedEvent); }).start();
    }

    private void test(ContextRefreshedEvent contextRefreshedEvent) {
        CoinTester kodTester = (BtcCoinTesterImpl) contextRefreshedEvent.getApplicationContext().getBean(CoinTester.class);
        try {
            kodTester.initBot("KOD");
            kodTester.testCoin(0.001);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
