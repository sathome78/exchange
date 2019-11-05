package me.exrates.service.impl;

import me.exrates.dao.RefillRequestDao;
import me.exrates.service.AdgroupService;
import me.exrates.service.AdgroupServiceImpl;
import me.exrates.service.CurrencyService;
import me.exrates.service.GtagService;
import me.exrates.service.MerchantService;
import me.exrates.service.RefillService;
import me.exrates.service.SendMailService;
import me.exrates.service.UserService;
import me.exrates.service.WithdrawService;
import me.exrates.service.http.AdGroupHttpClient;
import me.exrates.service.stomp.StompMessenger;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@PropertySource(value = {"classpath:/adgroup.properties"})
public class ConfigAdGroupContext {
    @Value("${base_url}")
    private String url = "url";
    @Value("${client_id}")
    private String clientId = "clienId";

    @Value("${client_secret}")
    private String clientSecret = "clientSecret";

    @Value("${walllet}")
    private String wallet = "11111";

    @Value("${pin}")
    private String pin = "ewfwefwew";

    @Bean
    public AdgroupService getAdgroupeService() {
        return new AdgroupServiceImpl();
    }

    @Bean
    public RefillService getRefillService() {
        return Mockito.mock(RefillService.class);
    }

    @Bean
    public MerchantService getMerchantService() {
        return Mockito.mock(MerchantService.class);
    }

    @Bean
    public CurrencyService getCurrencyService() {
        return Mockito.mock(CurrencyService.class);
    }

    @Bean
    public GtagService getGtagService() {
        return Mockito.mock(GtagService.class);
    }

    @Bean
    public UserService getUserService() {
        return Mockito.mock(UserService.class);
    }

    @Bean
    public SendMailService getSendMailService() {
        return Mockito.mock(SendMailService.class);
    }

    @Bean
    public StompMessenger getStompMessenger() {
        return Mockito.mock(StompMessenger.class);
    }

    @Bean
    public RefillRequestDao getRefillRequestDao() {
        return Mockito.mock(RefillRequestDao.class);
    }

    @Bean
    public AdGroupHttpClient ieoClaimRepository() {
        return Mockito.mock(AdGroupHttpClient.class);
    }

    @Bean
    public WithdrawService getWithdrawService() {
        return Mockito.mock(WithdrawService.class);
    }

}
