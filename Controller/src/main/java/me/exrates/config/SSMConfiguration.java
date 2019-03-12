package me.exrates.config;


import lombok.extern.log4j.Log4j2;
import me.exrates.SSMGetter;
import me.exrates.SSMGetterImpl;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@Log4j2
@PropertySource(value = "classpath:/ssm.properties")
public class SSMConfiguration {

    @Value("${ssm.mode}")
    private String ssmMode;

    @Bean
    public SSMGetter ssmGetter() {
        if(ssmMode.equals("dev")) return new MockSSM();

        return new SSMGetterImpl();
    }

    private class MockSSM implements SSMGetter {
        MockSSM(){
            log.info("Using mock ssm lookup...");
        }

        @Override
        public String lookup(String s) {
            return "MOCK_TOKEN";
        }
    }
}

