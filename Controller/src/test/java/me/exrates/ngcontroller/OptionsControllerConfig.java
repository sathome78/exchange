package me.exrates.ngcontroller;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

@EnableWebMvc
@Configuration
public class OptionsControllerConfig {
    @Bean
    public NgOptionsController ngOptionsController() {
        return new NgOptionsController();
    }
}
