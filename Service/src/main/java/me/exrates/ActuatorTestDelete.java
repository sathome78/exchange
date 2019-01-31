//package me.exrates;
//
//import org.springframework.context.ApplicationListener;
//import org.springframework.context.event.ContextRefreshedEvent;
//import org.springframework.stereotype.Service;
//
//@Service
//public class ActuatorTestDelete implements
//        ApplicationListener<ContextRefreshedEvent>{
//
//    private int counter = 0;
//    @Override
//    public void onApplicationEvent(ContextRefreshedEvent contextRefreshedEvent) {
//        counter++;
//        if(counter == 1) new Thread(() -> {test(contextRefreshedEvent); }).start();
//    }
//
//    private void test(ContextRefreshedEvent contextRefreshedEvent) {
//        CoinTester kodTester = contextRefreshedEvent.getApplicationContext().getBean(CoinTester.class);
//        try {
//            System.out.println("INIT BOT");
//            StringBuilder stringBuilder = new StringBuilder();
//            kodTester.initBot("RIME", stringBuilder);
//            String s = kodTester.testCoin(0.00001);
//            System.out.println(s);
//            System.out.println(stringBuilder.toString());
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
//}
