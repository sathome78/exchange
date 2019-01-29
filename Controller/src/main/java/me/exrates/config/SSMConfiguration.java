package me.exrates.config;

import com.amazonaws.SdkClientException;
import lombok.extern.log4j.Log4j2;
import me.exrates.SSMGetter;
import me.exrates.SSMGetterImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@Log4j2
public class SSMConfiguration {

    @Bean
    public SSMGetter ssmGetter() {
        try {
            return new SSMGetterImpl();
        }catch (SdkClientException e){
            log.error("Could'n connetect to AWS SSM, using MOCK_TOKEN password");
            return new MockSSM();
        }
    }

    private class MockSSM implements SSMGetter {
        @Override
        public String lookup(String s) {
            return "MOCK_TOKEN";
        }
    }
}
