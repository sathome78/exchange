package me.exrates.service.properties;

import lombok.Data;
import me.exrates.SSMGetter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

@Component
@Data
@PropertySource({"classpath:/ssm.properties"})
public class SsmProperties {
    private String inoutToken;

    public SsmProperties(SSMGetter ssmGetter,
                         @Value("${ssm.inout.api}") String inoutTokenPath) {
        this.inoutToken = ssmGetter.lookup(inoutTokenPath);
    }
}
