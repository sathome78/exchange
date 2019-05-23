package me.exrates.ngcontroller;

import com.fasterxml.jackson.databind.ObjectMapper;
import me.exrates.dao.OrderDao;
import me.exrates.dao.StopOrderDao;
import me.exrates.dao.UserDao;
import me.exrates.dao.WalletDao;
import me.exrates.dao.chat.telegram.TelegramChatDao;
import me.exrates.ngService.BalanceService;
import me.exrates.ngService.NgOrderService;
import me.exrates.ngService.UserVerificationService;
import me.exrates.security.ipsecurity.IpBlockingService;
import me.exrates.security.service.AuthTokenService;
import me.exrates.security.service.NgUserService;
import me.exrates.security.service.SecureService;
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
import me.exrates.service.merchantStrategy.IMerchantService;
import me.exrates.service.merchantStrategy.MerchantServiceContext;
import me.exrates.service.notifications.G2faService;
import me.exrates.service.stomp.StompMessenger;
import me.exrates.service.stopOrder.StopOrderService;
import me.exrates.service.userOperation.UserOperationService;
import me.exrates.service.util.RateLimitService;
import org.mockito.Mockito;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import javax.sql.DataSource;
import java.util.Map;

@Configuration
@EnableWebMvc
@ComponentScan(basePackages = {
        "me.exrates.ngcontroller",
        "me.exrates.controller.advice"
})
public class AngularAppTestConfig {

    @Bean
    public UserService userService() {
        return Mockito.mock(UserService.class);
    }

    @Bean
    public NgUserService ngUserService() {
        return Mockito.mock(NgUserService.class);
    }

    @Bean
    public StompMessenger stompMessenger() {
        return Mockito.mock(StompMessenger.class);
    }

    @Bean(name = "angularTestDataSource")
    public DataSource dataSource() {
        return Mockito.mock(DataSource.class);
    }

    @Bean("slaveTemplate")
    public NamedParameterJdbcTemplate slaveTemplate() {
        return Mockito.mock(NamedParameterJdbcTemplate.class);
    }

    @Bean("masterTemplate")
    public NamedParameterJdbcTemplate masterTemplate() {
        return Mockito.mock(NamedParameterJdbcTemplate.class);
    }

    @Bean
    public InputOutputService inputOutputService() {
        return Mockito.mock(InputOutputService.class);
    }

    @Bean
    public WalletDao walletDao() {
        return Mockito.mock(WalletDao.class);
    }

    @Bean
    public ExchangeRatesHolder exchangeRatesHolder() {
        return Mockito.mock(ExchangeRatesHolder.class);
    }

    @Bean
    public MerchantServiceContext merchantServiceContext() {
        return Mockito.mock(MerchantServiceContext.class);
    }

    @Bean
    public Map<String, IMerchantService> merchantServiceMap() {
        return Mockito.mock(Map.class);
    }

    @Bean
    public MerchantService merchantService() {
        return Mockito.mock(MerchantService.class);
    }

    @Bean
    public LocaleResolver localeResolver() {
        return Mockito.mock(LocaleResolver.class);
    }

    @Bean
    public RefillService refillService() {
        return Mockito.mock(RefillService.class);
    }

    @Bean
    public WalletService walletService() {
        return Mockito.mock(WalletService.class);
    }

    @Bean
    public CurrencyService currencyService() {
        return Mockito.mock(CurrencyService.class);
    }

    @Bean
    public OrderService orderService() {
        return Mockito.mock(OrderService.class);
    }

    @Bean
    public OrderDao orderDao() {
        return Mockito.mock(OrderDao.class);
    }

    @Bean
    public ObjectMapper objectMapper() {
        return new ObjectMapper();
    }

    @Bean
    public StopOrderDao stopOrderDao() {
        return Mockito.mock(StopOrderDao.class);
    }

    @Bean
    public DashboardService dashboardService() {
        return Mockito.mock(DashboardService.class);
    }

    @Bean
    public SimpMessagingTemplate simpMessagingTemplate() {
        return Mockito.mock(SimpMessagingTemplate.class);
    }

    @Bean
    public StopOrderService stopOrderService() {
        return Mockito.mock(StopOrderService.class);
    }

    @Bean
    public KYCService kycService() {
        return Mockito.mock(KYCService.class);
    }

    @Bean
    public KYCSettingsService kycSettingsService() {
        return Mockito.mock(KYCSettingsService.class);
    }

    @Bean
    public SendMailService sendMailService() {
        return Mockito.mock(SendMailService.class);
    }

    @Bean
    public CommissionService commissionService() {
        return Mockito.mock(CommissionService.class);
    }

    @Bean
    public WithdrawService withdrawService() {
        return Mockito.mock(WithdrawService.class);
    }

    @Bean
    public TransferService transferService() {
        return Mockito.mock(TransferService.class);
    }

    @Bean
    public RateLimitService rateLimitService() {
        return Mockito.mock(RateLimitService.class);
    }

    @Bean
    public ChatService chatService() {
        return Mockito.mock(ChatService.class);
    }

    @Bean
    public IpBlockingService ipBlockingService() {
        return Mockito.mock(IpBlockingService.class);
    }

    @Bean
    public UserDao userDao() {
        return Mockito.mock(UserDao.class);
    }

    @Bean
    public AuthTokenService authTokenService() {
        return Mockito.mock(AuthTokenService.class);
    }

    @Bean
    public ReferralService referralService() {
        return Mockito.mock(ReferralService.class);
    }

    @Bean
    public TemporalTokenService temporalTokenService() {
        return Mockito.mock(TemporalTokenService.class);
    }

    @Bean
    public G2faService g2faService() {
        return Mockito.mock(G2faService.class);
    }

    @Bean
    public TelegramChatDao telegramChatDao() {
        return Mockito.mock(TelegramChatDao.class);
    }

    @Bean
    public UserOperationService userOperationService() {
        return Mockito.mock(UserOperationService.class);
    }

    @Bean
    public SecureService secureService() {
        return Mockito.mock(SecureService.class);
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
    public NotificationService notificationService() {
        return Mockito.mock(NotificationService.class);
    }

    @Bean
    public SessionParamsService sessionParamsService() {
        return Mockito.mock(SessionParamsService.class);
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
        return Mockito.mock(GtagRefillService.class);
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
        return Mockito.mock(OpenApiTokenService.class);
    }

    @Bean
    public NewsParser newsParser() {
        return Mockito.mock(NewsParser.class);
    }
}