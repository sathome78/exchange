package me.exrates.service.properties;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

@Component
@Data
@PropertySource({"classpath:/ssm.properties"})
public class SsmProperties {

    @Value("${ssm.inout.api}")
    private String inoutTokenPath;

}
