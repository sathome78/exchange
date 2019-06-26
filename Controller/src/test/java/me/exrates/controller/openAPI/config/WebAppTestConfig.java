package me.exrates.controller.openAPI.config;

import me.exrates.dao.IeoDetailsRepository;
import me.exrates.security.service.OpenApiAuthService;
import me.exrates.service.CurrencyService;
import me.exrates.service.OrderService;
import me.exrates.service.UserService;
import me.exrates.service.WalletService;
import me.exrates.service.api.ExchangeApi;
import me.exrates.service.openapi.OpenApiCommonService;
import me.exrates.service.openapi.impl.OpenApiCommonServiceImpl;
import me.exrates.service.userOperation.UserOperationService;
import org.mockito.Mockito;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

@Configuration
@EnableWebMvc
@ComponentScan(basePackages = {
        "me.exrates.controller.openAPI",
})
public class WebAppTestConfig {

    @Bean
    public OrderService orderService() {
        return Mockito.mock(OrderService.class);
    }

    @Bean
    public UserService userService() {
        return Mockito.mock(UserService.class);
    }

    @Bean
    public IeoDetailsRepository ieoDetailsRepository() {
        return Mockito.mock(IeoDetailsRepository.class);
    }

    @Bean
    public CurrencyService currencyService() {
        return Mockito.mock(CurrencyService.class);
    }

    @Bean
    public UserOperationService userOperationService() {
        return Mockito.mock(UserOperationService.class);
    }

    @Bean
    public MessageSource messageSource() {
        return Mockito.mock(MessageSource.class);
    }

    @Bean
    public OpenApiAuthService openApiAuthService() {
        return Mockito.mock(OpenApiAuthService.class);
    }

    @Bean
    public WalletService walletService() {
        return Mockito.mock(WalletService.class);
    }

    @Bean
    public ExchangeApi exchangeApi() {
        return Mockito.mock(ExchangeApi.class);
    }

    @Bean
    public OpenApiCommonService openApiCommonService() {
        return new OpenApiCommonServiceImpl(userService(), messageSource(), userOperationService());
    }
}
