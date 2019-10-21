package me.exrates.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import me.exrates.dao.CallBackLogDao;
import me.exrates.dao.CommissionDao;
import me.exrates.dao.CompanyWalletDao;
import me.exrates.dao.CurrencyDao;
import me.exrates.dao.G2faDao;
import me.exrates.dao.IEOClaimRepository;
import me.exrates.dao.IeoDetailsRepository;
import me.exrates.dao.MerchantDao;
import me.exrates.dao.NewsDao;
import me.exrates.dao.NotificationDao;
import me.exrates.dao.NotificationUserSettingsDao;
import me.exrates.dao.NotificatorPriceDao;
import me.exrates.dao.NotificatorsDao;
import me.exrates.dao.OrderDao;
import me.exrates.dao.ReferralLevelDao;
import me.exrates.dao.ReferralTransactionDao;
import me.exrates.dao.ReferralUserGraphDao;
import me.exrates.dao.SettingsEmailRepository;
import me.exrates.dao.StopOrderDao;
import me.exrates.dao.TelegramSubscriptionDao;
import me.exrates.dao.TransactionDao;
import me.exrates.dao.UserDao;
import me.exrates.dao.UserPinDao;
import me.exrates.dao.UserRoleDao;
import me.exrates.dao.UserSettingsDao;
import me.exrates.dao.UserTransferDao;
import me.exrates.dao.WalletDao;
import me.exrates.model.vo.TransactionDescription;
import me.exrates.ngService.RedisUserNotificationService;
import me.exrates.service.BitcoinService;
import me.exrates.service.CommissionService;
import me.exrates.service.CompanyWalletService;
import me.exrates.service.CurrencyService;
import me.exrates.service.MerchantService;
import me.exrates.service.NotificationService;
import me.exrates.service.OrderService;
import me.exrates.service.ReferralService;
import me.exrates.service.SendMailService;
import me.exrates.service.TransactionService;
import me.exrates.service.UserRoleService;
import me.exrates.service.UserService;
import me.exrates.service.UserSettingService;
import me.exrates.service.UserTransferService;
import me.exrates.service.WalletService;
import me.exrates.service.api.ExchangeApi;
import me.exrates.service.api.WalletsApi;
import me.exrates.service.cache.ExchangeRatesHolder;
import me.exrates.service.cache.SettingsService;
import me.exrates.service.cache.SettingsServiceImpl;
import me.exrates.service.impl.proxy.ServiceCacheableProxy;
import me.exrates.service.merchantStrategy.IMerchantService;
import me.exrates.service.merchantStrategy.MerchantServiceContext;
import me.exrates.service.merchantStrategy.MerchantServiceContextImpl;
import me.exrates.service.notifications.EmailNotificatorServiceImpl;
import me.exrates.service.notifications.G2faService;
import me.exrates.service.notifications.Google2faNotificatorServiceImpl;
import me.exrates.service.notifications.NotificationsSettingsService;
import me.exrates.service.notifications.NotificationsSettingsServiceImpl;
import me.exrates.service.notifications.NotificatorService;
import me.exrates.service.notifications.NotificatorsService;
import me.exrates.service.notifications.NotificatorsServiceImpl;
import me.exrates.service.notifications.Subscribable;
import me.exrates.service.session.UserSessionService;
import me.exrates.service.stomp.StompMessenger;
import me.exrates.service.stomp.StompMessengerImpl;
import me.exrates.service.stopOrder.RatesHolder;
import me.exrates.service.stopOrder.RatesHolderImpl;
import me.exrates.service.stopOrder.StopOrderService;
import me.exrates.service.stopOrder.StopOrderServiceImpl;
import me.exrates.service.stopOrder.StopOrdersHolder;
import me.exrates.service.token.TokenScheduler;
import me.exrates.service.util.BigDecimalConverter;
import me.exrates.service.util.RestApiUtilComponent;
import me.exrates.service.util.WithdrawUtils;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.social.twitter.api.Twitter;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.socket.messaging.DefaultSimpUserRegistry;

import javax.servlet.http.HttpServletRequest;

@Configuration
@PropertySource(value = {
        "classpath:/mail.properties",
        "classpath:/angular.properties",
        "classpath:/precision.properties",
        "classpath:/external-apis.properties"
})
public class ServiceTestConfig {

    @Value("${precision.value1}")
    int precision1;
    @Value("${precision.value2}")
    int precision2;
    @Value("${precision.value3}")
    int precision3;
    @Value("${precision.value4}")
    int precision4;
    @Value("${precision.value5}")
    int precision5;
    @Value("${precision.value6}")
    int precision6;
    @Value("${precision.value7}")
    int precision7;
    @Value("${precision.value8}")
    int precision8;
    @Value("${precision.value9}")
    int precision9;
    @Value("${precision.value10}")
    int precision10;

    @Value("${api.wallets.url}")
    String url;
    @Value("${api.wallets.username}")
    String username;
    @Value("${api.wallets.password}")
    String password;

    @Bean
    public CurrencyDao currencyDao() {
        return Mockito.mock(CurrencyDao.class);
    }

    @Bean
    public UserDao userDao() {
        return Mockito.mock(UserDao.class);
    }

    @Bean
    public IeoDetailsRepository ieoDetailsRepository() {
        return Mockito.mock(IeoDetailsRepository.class);
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

    @Bean
    public IEOClaimRepository ieoClaimRepository() {
        return Mockito.mock(IEOClaimRepository.class);
    }

    @Bean
    public UserPinDao userPinDao() {
        return Mockito.mock(UserPinDao.class);
    }

    @Bean("ExratesSessionRegistry")
    public SessionRegistry sessionRegistry() {
        return Mockito.mock(SessionRegistry.class);
    }

    @Bean
    public SendMailService sendMailService() {
        return new SendMailServiceImpl();
    }

    @Bean
    public SettingsEmailRepository settingsEmailRepository() {
        return Mockito.mock(SettingsEmailRepository.class);
    }

    @Bean
    public SettingsService settingsServiceNew() {
        return new SettingsServiceImpl(settingsEmailRepository());
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

    @Bean("SesMailSender")
    public JavaMailSender sesMailSender() {
        return Mockito.mock(JavaMailSender.class);
    }

    @Bean("SendGridMailSender")
    public JavaMailSender sendGridMailSender() {
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
    public UserRoleService userRoleService() {
        return Mockito.mock(UserRoleService.class);
    }

    @Bean
    public BigDecimalConverter bigDecimalConverter() {
        return new BigDecimalConverter(precision1, precision2, precision3, precision4, precision5, precision6, precision7, precision8, precision9, precision10);
    }

    @Bean
    public CommissionDao commissionDao() {
        return Mockito.mock(CommissionDao.class);
    }

    @Bean
    public CommissionService commissionService() {
        return new CommissionServiceImpl();
    }

    @Bean
    public MerchantDao merchantDao() {
        return Mockito.mock(MerchantDao.class);
    }

    @Bean
    public MerchantService merchantService() {
        return new MerchantServiceImpl();
    }

    @Bean
    public MerchantServiceContext merchantServiceContext() {
        return new MerchantServiceContextImpl();
    }

    @Bean
    public IMerchantService iMerchantService() {
        return new IcoServiceImpl();
    }

    @Bean
    public WithdrawUtils withdrawUtils() {
        return new WithdrawUtils();
    }

    @Bean("masterTemplate")
    public NamedParameterJdbcTemplate namedParameterJdbcTemplate() {
        return Mockito.mock(NamedParameterJdbcTemplate.class);
    }

    @Bean("bitcoinServiceImpl")
    public BitcoinService bitcoinService() {
        return Mockito.mock(BitcoinService.class);
    }

    @Bean
    public CompanyWalletService companyWalletService() {
        return new CompanyWalletServiceImpl();
    }

    @Bean
    public CompanyWalletDao companyWalletDao() {
        return Mockito.mock(CompanyWalletDao.class);
    }

    @Bean
    public UserTransferService userTransferService() {
        return new UserTransferServiceImpl();
    }

    @Bean
    public UserTransferDao userTransferDao() {
        return Mockito.mock(UserTransferDao.class);
    }

    @Bean
    public OrderService orderService() {
        return new OrderServiceImpl();
    }

    @Bean
    public ServiceCacheableProxy serviceCacheableProxy() {
        return new ServiceCacheableProxy();
    }

    @Bean
    public OrderDao orderDao() {
        return Mockito.mock(OrderDao.class);
    }

    @Bean
    public NewsDao newsDao() {
        return Mockito.mock(NewsDao.class);
    }

    @Bean
    public Twitter twitter() {
        return Mockito.mock(Twitter.class);
    }

    @Bean
    public TransactionDescription transactionDescription() {
        return new TransactionDescription();
    }

    @Bean
    public StopOrderService stopOrderService() {
        return new StopOrderServiceImpl();
    }

    @Bean
    public StopOrderDao stopOrderDao() {
        return Mockito.mock(StopOrderDao.class);
    }

    @Bean
    public StopOrdersHolder stopOrdersHolder() {
        return Mockito.mock(StopOrdersHolder.class);
    }

    @Bean
    public RatesHolder ratesHolder() {
        return new RatesHolderImpl();
    }

    @Bean
    public CallBackLogDao callBackLogDao() {
        return Mockito.mock(CallBackLogDao.class);
    }

    @Bean
    public TransactionService transactionService() {
        return new TransactionServiceImpl(transactionDao(), walletService(), companyWalletService(), merchantService(),
                currencyService());
    }

    @Bean
    public TransactionDao transactionDao() {
        return Mockito.mock(TransactionDao.class);
    }

    @Bean
    public ObjectMapper objectMapper() {
        return Mockito.mock(ObjectMapper.class);
    }

    @Bean
    public StompMessenger stompMessenger() {
        return new StompMessengerImpl(defaultSimpUserRegistry(), objectMapper(), orderService(),
                redisUserNotificationService(), simpMessagingTemplate(), userService());
    }

    @Bean
    public RedisUserNotificationService redisUserNotificationService() {
        return Mockito.mock(RedisUserNotificationService.class);
    }

    @Bean
    public SimpMessagingTemplate simpMessagingTemplate() {
        return Mockito.mock(SimpMessagingTemplate.class);
    }

    @Bean
    public DefaultSimpUserRegistry defaultSimpUserRegistry() {
        return Mockito.mock(DefaultSimpUserRegistry.class);
    }

    @Bean
    public ExchangeRatesHolder exchangeRatesHolder() {
        return Mockito.mock(ExchangeRatesHolder.class);
    }

    @Bean
    public WalletsApi walletsApi() {
        return new WalletsApi(url, username, password, currencyService());
    }

    @Bean
    public NotificationUserSettingsDao notificationUserSettingsDao() {
        return Mockito.mock(NotificationUserSettingsDao.class);
    }

    @Bean
    public NotificatorsService notificatorsService() {
        return new NotificatorsServiceImpl();
    }

    @Bean
    public NotificatorsDao notificatorsDao() {
        return Mockito.mock(NotificatorsDao.class);
    }

    @Bean
    public NotificatorPriceDao notificatorPriceDao() {
        return Mockito.mock(NotificatorPriceDao.class);
    }

    @Bean
    public NotificatorService notificatorService() {
        return new EmailNotificatorServiceImpl();
    }

    @Bean
    public TelegramSubscriptionDao telegramSubscriptionDao() {
        return Mockito.mock(TelegramSubscriptionDao.class);
    }

    @Bean("telegramNotificatorServiceImpl")
    public Subscribable subscribable() {
        return Mockito.mock(Subscribable.class);
    }

    @Bean
    public G2faDao g2faDao() {
        return Mockito.mock(G2faDao.class);
    }

    @Bean
    public LocaleResolver localeResolver() {
        return Mockito.mock(LocaleResolver.class);
    }

    @Bean
    public UserDetailsService userDetailsService() {
        return Mockito.mock(UserDetailsService.class);
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return Mockito.mock(PasswordEncoder.class);
    }

    @Bean
    public UserSettingsDao userSettingsDao() {
        return Mockito.mock(UserSettingsDao.class);
    }

    @Bean
    public RestApiUtilComponent restApiUtils() {
        return Mockito.mock(RestApiUtilComponent.class);
    }
}
