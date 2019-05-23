package web.config;

import me.exrates.model.condition.MicroserviceConditional;
import me.exrates.model.condition.MonolitConditional;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.FilterType;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

@Configuration
@EnableWebMvc
@ComponentScan(basePackages = {"me.exrates"},
        useDefaultFilters = false,
        excludeFilters = {
                @ComponentScan.Filter(type = FilterType.REGEX, pattern = "me.exrates.model.condition"),
                @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, value = {MonolitConditional.class, MicroserviceConditional.class})
        }
)
public class TestContextConfig extends WebMvcConfigurerAdapter {

}
