package web.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import me.exrates.dao.ApiAuthTokenDao;
import me.exrates.dao.OrderDao;
import me.exrates.dao.StopOrderDao;
import me.exrates.dao.UserDao;
import me.exrates.dao.WalletDao;
import me.exrates.dao.chat.telegram.TelegramChatDao;
import me.exrates.dao.impl.ApiAuthTokenDaoImpl;
import me.exrates.dao.impl.OrderDaoImpl;
import me.exrates.dao.impl.StopOrderDaoImpl;
import me.exrates.dao.impl.UserDaoImpl;
import me.exrates.dao.impl.WalletDaoImpl;
import me.exrates.ngService.BalanceService;
import me.exrates.ngService.NgOrderService;
import me.exrates.ngService.UserVerificationService;
import me.exrates.ngcontroller.NgOptionsController;
import me.exrates.security.ipsecurity.IpBlockingService;
import me.exrates.security.ipsecurity.IpBlockingServiceImpl;
import me.exrates.security.service.AuthTokenService;
import me.exrates.security.service.NgUserService;
import me.exrates.security.service.SecureService;
import me.exrates.security.service.impl.NgUserServiceImpl;
import me.exrates.security.service.impl.SecureServiceImpl;
import me.exrates.security.service.impl.UserDetailsServiceImpl;
import me.exrates.service.ChatService;
import me.exrates.service.CommissionService;
import me.exrates.service.CurrencyService;
import me.exrates.service.DashboardService;
import me.exrates.service.GtagRefillService;
import me.exrates.service.IEOService;
import me.exrates.service.InputOutputService;
import me.exrates.service.KYCService;
import me.exrates.service.KYCSettingsService;
import me.exrates.service.MerchantService;
import me.exrates.service.NewsParser;
import me.exrates.service.NotificationService;
import me.exrates.service.OpenApiTokenService;
import me.exrates.service.OrderService;
import me.exrates.service.PageLayoutSettingsService;
import me.exrates.service.ReferralService;
import me.exrates.service.RefillService;
import me.exrates.service.SendMailService;
import me.exrates.service.SessionParamsService;
import me.exrates.service.TemporalTokenService;
import me.exrates.service.TransferService;
import me.exrates.service.UserService;
import me.exrates.service.WalletService;
import me.exrates.service.WithdrawService;
import me.exrates.service.cache.ExchangeRatesHolder;
import me.exrates.service.impl.CommissionServiceImpl;
import me.exrates.service.impl.CurrencyServiceImpl;
import me.exrates.service.impl.DashboardServiceImpl;
import me.exrates.service.impl.GtagRefillServiceImpl;
import me.exrates.service.impl.InputOutputServiceImpl;
import me.exrates.service.impl.KYCSettingsServiceImpl;
import me.exrates.service.impl.MerchantServiceImpl;
import me.exrates.service.impl.NewsParserImpl;
import me.exrates.service.impl.NotificationServiceImpl;
import me.exrates.service.impl.OpenApiTokenServiceImpl;
import me.exrates.service.impl.OrderServiceImpl;
import me.exrates.service.impl.ReferralServiceImpl;
import me.exrates.service.impl.RefillServiceImpl;
import me.exrates.service.impl.SendMailServiceImpl;
import me.exrates.service.impl.SessionParamsServiceImpl;
import me.exrates.service.impl.TemporalTokenServiceImpl;
import me.exrates.service.impl.TransferServiceImpl;
import me.exrates.service.impl.UserServiceImpl;
import me.exrates.service.impl.WalletServiceImpl;
import me.exrates.service.impl.WithdrawServiceImpl;
import me.exrates.service.merchantStrategy.MerchantServiceContext;
import me.exrates.service.merchantStrategy.MerchantServiceContextImpl;
import me.exrates.service.notifications.G2faService;
import me.exrates.service.notifications.Google2faNotificatorServiceImpl;
import me.exrates.service.stomp.StompMessenger;
import me.exrates.service.stomp.StompMessengerImpl;
import me.exrates.service.stopOrder.StopOrderService;
import me.exrates.service.stopOrder.StopOrderServiceImpl;
import me.exrates.service.userOperation.UserOperationService;
import me.exrates.service.userOperation.UserOperationServiceImpl;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import javax.servlet.http.HttpServletRequest;

@Configuration
@EnableWebMvc
@ComponentScan(basePackages = {
        "me.exrates.ngcontroller",
        "me.exrates.controller.advice"
})
public class TestContextConfig {

    @Autowired
    private MessageSource messageSource;
    @Autowired
    private HttpServletRequest httpServletRequest;

    @Bean
    public UserService userService() {
        return new UserServiceImpl();
    }

    @Bean
    public NgUserService ngUserService() {
        return new NgUserServiceImpl(userDao(),
                userService(),
                messageSource,
                sendMailService(),
                authTokenService(),
                referralService(),
                ipBlockingService(),
                temporalTokenService(),
                httpServletRequest);
    }

    @Bean
    public StompMessenger stompMessenger() {
        return new StompMessengerImpl();
    }

    @Bean
    public InputOutputService inputOutputService() {
        return new InputOutputServiceImpl();
    }

    @Bean
    public WalletDao walletDao() {
        return new WalletDaoImpl();
    }

    @Bean
    public ExchangeRatesHolder exchangeRatesHolder() {
        return Mockito.mock(ExchangeRatesHolder.class);
    }

    @Bean
    public MerchantServiceContext merchantServiceContext() {
        return new MerchantServiceContextImpl();
    }

    @Bean
    public MerchantService merchantService() {
        return new MerchantServiceImpl();
    }

    @Bean
    public RefillService refillService() {
        return new RefillServiceImpl();
    }

    @Bean
    public WalletService walletService() {
        return new WalletServiceImpl();
    }

    @Bean
    public CurrencyService currencyService() {
        return new CurrencyServiceImpl();
    }

    @Bean
    public OrderService orderService() {
        return new OrderServiceImpl();
    }

    @Bean
    public OrderDao orderDao() {
        return new OrderDaoImpl();
    }

    @Bean
    public ObjectMapper objectMapper() {
        return new ObjectMapper();
    }

    @Bean
    public StopOrderDao stopOrderDao() {
        return new StopOrderDaoImpl();
    }

    @Bean
    public DashboardService dashboardService() {
        return new DashboardServiceImpl();
    }

    @Bean
    public StopOrderService stopOrderService() {
        return new StopOrderServiceImpl();
    }

    @Bean
    public KYCService kycService() {
        return Mockito.mock(KYCService.class);
    }

    @Bean
    public KYCSettingsService kycSettingsService() {
        return new KYCSettingsServiceImpl();
    }

    @Bean
    public SendMailService sendMailService() {
        return new SendMailServiceImpl();
    }

    @Bean
    public CommissionService commissionService() {
        return new CommissionServiceImpl();
    }

    @Bean
    public WithdrawService withdrawService() {
        return new WithdrawServiceImpl();
    }

    @Bean
    public TransferService transferService() {
        return new TransferServiceImpl();
    }

    @Bean
    public ChatService chatService() {
        return Mockito.mock(ChatService.class);
    }

    @Bean
    public IpBlockingService ipBlockingService() {
        return new IpBlockingServiceImpl();
    }

    @Bean
    public UserDao userDao() {
        return new UserDaoImpl();
    }

    @Bean
    public AuthTokenService authTokenService() {
//        return new AuthTokenServiceImpl(passwordEncoder,
//                getApiAuthTokenDao(),
//                userDetailsService(),
//                sessionParamsService(),
//                referralService());
        return Mockito.mock(AuthTokenService.class);
    }

    @Bean
    public ApiAuthTokenDao getApiAuthTokenDao() {
        return new ApiAuthTokenDaoImpl(masterTemplate());
    }

    @Bean
    public ReferralService referralService() {
        return new ReferralServiceImpl();
    }

    @Bean
    public TemporalTokenService temporalTokenService() {
        return new TemporalTokenServiceImpl();
    }

    @Bean
    public G2faService g2faService() {
        return new Google2faNotificatorServiceImpl();
    }

    @Bean
    public TelegramChatDao telegramChatDao() {
        return Mockito.mock(TelegramChatDao.class);
    }

    @Bean
    public UserOperationService userOperationService() {
        return new UserOperationServiceImpl();
    }

    @Bean
    public SecureService secureService() {
        return new SecureServiceImpl();
    }

    @Bean
    public UserDetailsService userDetailsService() {
        return new UserDetailsServiceImpl();
    }

    @Bean
    public NotificationService notificationService() {
        return new NotificationServiceImpl();
    }

    @Bean
    public SessionParamsService sessionParamsService() {
        return new SessionParamsServiceImpl();
    }

    @Bean
    public NgOrderService ngOrderService() {
        return Mockito.mock(NgOrderService.class);
    }

    @Bean
    public BalanceService balanceService() {
        return Mockito.mock(BalanceService.class);
    }

    @Bean
    public GtagRefillService gtagRefillService() {
        return new GtagRefillServiceImpl();
    }

    @Bean
    public UserVerificationService userVerificationService() {
        return Mockito.mock(UserVerificationService.class);
    }

    @Bean
    public PageLayoutSettingsService pageLayoutSettingsService() {
        return Mockito.mock(PageLayoutSettingsService.class);
    }

    @Bean
    public NgOptionsController ngOptionsController() {
        return new NgOptionsController();
    }

    @Bean
    public IEOService ieoService() {
        return Mockito.mock(IEOService.class);
    }

    @Bean
    public OpenApiTokenService openApiTokenService() {
        return new OpenApiTokenServiceImpl();
    }

    @Bean
    public NewsParser newsParser() {
        return new NewsParserImpl();
    }

    @Bean("slaveTemplate")
    public NamedParameterJdbcTemplate slaveTemplate() {
        return Mockito.mock(NamedParameterJdbcTemplate.class);
    }

    @Bean("masterTemplate")
    public NamedParameterJdbcTemplate masterTemplate() {
        return Mockito.mock(NamedParameterJdbcTemplate.class);
    }
}
