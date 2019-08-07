package me.exrates.service.properties;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

@Component
@PropertySource({"classpath:/inout.properties"})
@Data
public class InOutProperties {

    @Value("${inout.token.name}")
    private String tokenName;
    private String tokenValue;
    @Value("${inout.url.basic}")
    private String url;

}
