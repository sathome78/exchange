package me.exrates.service.impl;

import me.exrates.dao.CurrencyDao;
import me.exrates.dao.NotificationDao;
import me.exrates.dao.ReferralLevelDao;
import me.exrates.dao.ReferralTransactionDao;
import me.exrates.dao.ReferralUserGraphDao;
import me.exrates.dao.UserDao;
import me.exrates.dao.UserRoleDao;
import me.exrates.dao.WalletDao;
import me.exrates.service.CurrencyService;
import me.exrates.service.NotificationService;
import me.exrates.service.ReferralService;
import me.exrates.service.SendMailService;
import me.exrates.service.UserRoleService;
import me.exrates.service.UserService;
import me.exrates.service.UserSettingService;
import me.exrates.service.WalletService;
import me.exrates.service.api.ExchangeApi;
import me.exrates.service.notifications.G2faService;
import me.exrates.service.notifications.Google2faNotificatorServiceImpl;
import me.exrates.service.notifications.NotificationsSettingsService;
import me.exrates.service.notifications.NotificationsSettingsServiceImpl;
import me.exrates.service.session.UserSessionService;
import me.exrates.service.token.TokenScheduler;
import me.exrates.service.util.BigDecimalConverter;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.security.core.session.SessionRegistry;

import javax.servlet.http.HttpServletRequest;

@Configuration
@PropertySource(value = {
        "classpath:/mail.properties",
        "classpath:/angular.properties",
        "classpath:/precision.properties"
})
public class ServiceTestConfig {

    @Value("${precision.value1}") int precision1;
    @Value("${precision.value2}") int precision2;
    @Value("${precision.value3}") int precision3;
    @Value("${precision.value4}") int precision4;
    @Value("${precision.value5}") int precision5;
    @Value("${precision.value6}") int precision6;
    @Value("${precision.value7}") int precision7;
    @Value("${precision.value8}") int precision8;
    @Value("${precision.value9}") int precision9;
    @Value("${precision.value10}") int precision10;

    @Bean
    public CurrencyDao currencyDao() {
        return Mockito.mock(CurrencyDao.class);
    }

    @Bean
    public UserDao userDao() {
        return Mockito.mock(UserDao.class);
    }

    @Bean
    public UserService userService() {
        return new UserServiceImpl();
    }

    @Bean
    public CurrencyService currencyService() {
        return new CurrencyServiceImpl();
    }

    @Bean
    public UserSessionService userSessionService() {
        return new UserSessionService();
    }

    @Bean("ExratesSessionRegistry")
    public SessionRegistry sessionRegistry() {
        return Mockito.mock(SessionRegistry.class);
    }

    @Bean
    public SendMailService sendMailService() {
        return new SendMailServiceImpl();
    }

    @Bean("SupportMailSender")
    public JavaMailSender supportMailSender() {
        return Mockito.mock(JavaMailSenderImpl.class);
    }

    @Bean("MandrillMailSender")
    public JavaMailSender mandrillMailSender() {
        return Mockito.mock(JavaMailSender.class);
    }

    @Bean("InfoMailSender")
    public JavaMailSender infoMailSender() {
        return Mockito.mock(JavaMailSender.class);
    }

    @Bean
    public NotificationDao notificationDao() {
        return Mockito.mock(NotificationDao.class);
    }

    @Bean
    public NotificationService notificationService() {
        return new NotificationServiceImpl();
    }

    @Bean
    public HttpServletRequest request() {
        return Mockito.mock(HttpServletRequest.class);
    }

    @Bean
    public TokenScheduler tokenScheduler() {
        return Mockito.mock(TokenScheduler.class);
    }

    @Bean
    public ReferralLevelDao referralLevelDao() {
        return Mockito.mock(ReferralLevelDao.class);
    }

    @Bean
    public ReferralUserGraphDao referralUserGraphDao() {
        return Mockito.mock(ReferralUserGraphDao.class);
    }

    @Bean
    public ReferralTransactionDao referralTransactionDao() {
        return Mockito.mock(ReferralTransactionDao.class);
    }

    @Bean
    public ReferralService referralService() {
        return new ReferralServiceImpl();
    }

    @Bean
    public NotificationsSettingsService settingsService() {
        return new NotificationsSettingsServiceImpl();
    }

    @Bean
    public G2faService g2faService() {
        return new Google2faNotificatorServiceImpl();
    }


    @Bean
    public ExchangeApi exchangeApi() {
        return Mockito.mock(ExchangeApi.class);
    }

    @Bean
    public UserSettingService userSettingService() {
        return new UserSettingServiceImpl();
    }

    @Bean
    public WalletDao walletDao() {
        return Mockito.mock(WalletDao.class);
    }

    @Bean
    public WalletService walletService() {
        return new WalletServiceImpl();
    }

    @Bean
    public UserRoleDao userRoleDao() {
        return Mockito.mock(UserRoleDao.class);
    }

    @Bean
    public UserRoleService userRoleService () {
        return new UserRoleServiceImpl();
    }

    @Bean
    public BigDecimalConverter bigDecimalConverter () {
        return new BigDecimalConverter(precision1,precision2,precision3,precision4,precision5,precision6,precision7,precision8,precision9,precision10);
    }



}
