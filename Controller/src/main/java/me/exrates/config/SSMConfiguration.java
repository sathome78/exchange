//package me.exrates.config;
//
//import com.amazonaws.SdkClientException;
//import me.exrates.SSMGetter;
//import me.exrates.SSMGetterImpl;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//
//@Configuration
//public class SSMConfiguration {
//    @Bean
//    public SSMGetter ssmGetter() {
//        try {
//            return new SSMGetterImpl();
//        }catch (SdkClientException e){
//            return new MockSSM();
//        }
//    }
//
//    private class MockSSM implements SSMGetter {
//        @Override
//        public String lookup(String s) {
//            return "MOCK_TOKEN";
//        }
//    }
//}
