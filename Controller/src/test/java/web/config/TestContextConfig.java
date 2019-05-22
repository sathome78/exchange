package web.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import me.exrates.SSMGetter;
import me.exrates.configurations.CacheConfiguration;
import me.exrates.controller.advice.GlobalControllerExceptionHandler;
import me.exrates.controller.openAPI.OpenApiOrderController;
import me.exrates.controller.openAPI.OpenApiPublicController;
import me.exrates.controller.openAPI.OpenApiUserInfoController;
import me.exrates.controller.openAPI.v1.OpenApiPrivateV1Controller;
import me.exrates.controller.openAPI.v1.OpenApiPublicV1Controller;
import me.exrates.dao.chat.telegram.TelegramChatDao;
import me.exrates.model.condition.MicroserviceConditional;
import me.exrates.model.vo.TransactionDescription;
import me.exrates.ngService.BalanceService;
import me.exrates.ngService.NgOrderService;
import me.exrates.ngService.UserVerificationService;
import me.exrates.ngcontroller.LanguageController;
import me.exrates.ngcontroller.NgBalanceController;
import me.exrates.ngcontroller.NgChartController;
import me.exrates.ngcontroller.NgDashboardController;
import me.exrates.ngcontroller.NgDownloadController;
import me.exrates.ngcontroller.NgIEOController;
import me.exrates.ngcontroller.NgKYCController;
import me.exrates.ngcontroller.NgMailingController;
import me.exrates.ngcontroller.NgOptionsController;
import me.exrates.ngcontroller.NgPublicController;
import me.exrates.ngcontroller.NgRefillController;
import me.exrates.ngcontroller.NgTokenSettingsController;
import me.exrates.ngcontroller.NgTransferController;
import me.exrates.ngcontroller.NgTwoFaController;
import me.exrates.ngcontroller.NgUserController;
import me.exrates.ngcontroller.NgUserSettingsController;
import me.exrates.ngcontroller.NgWithdrawController;
import me.exrates.security.ipsecurity.IpBlockingService;
import me.exrates.security.service.AuthTokenService;
import me.exrates.security.service.NgUserService;
import me.exrates.security.service.SecureService;
import me.exrates.service.ChatService;
import me.exrates.service.CurrencyService;
import me.exrates.service.DashboardService;
import me.exrates.service.GtagRefillService;
import me.exrates.service.IEOService;
import me.exrates.service.InputOutputService;
import me.exrates.service.KYCService;
import me.exrates.service.KYCSettingsService;
import me.exrates.service.MerchantService;
import me.exrates.service.NamedParameterJdbcTemplateWrapper;
import me.exrates.service.NewsParser;
import me.exrates.service.NotificationService;
import me.exrates.service.OpenApiTokenService;
import me.exrates.service.OrderService;
import me.exrates.service.PageLayoutSettingsService;
import me.exrates.service.ReferralService;
import me.exrates.service.RefillService;
import me.exrates.service.SendMailService;
import me.exrates.service.SessionParamsService;
import me.exrates.service.TransferService;
import me.exrates.service.UserService;
import me.exrates.service.WalletService;
import me.exrates.service.WithdrawService;
import me.exrates.service.api.ExchangeApi;
import me.exrates.service.cache.ExchangeRatesHolder;
import me.exrates.service.merchantStrategy.MerchantServiceContext;
import me.exrates.service.notifications.G2faService;
import me.exrates.service.openapi.OpenApiCommonService;
import me.exrates.service.stomp.StompMessenger;
import me.exrates.service.stopOrder.StopOrderService;
import me.exrates.service.userOperation.UserOperationService;
import me.exrates.service.util.RateLimitService;
import org.apache.http.impl.client.CloseableHttpClient;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.FilterType;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.core.session.SessionRegistryImpl;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.social.twitter.api.Twitter;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import javax.sql.DataSource;

@Configuration
@EnableWebMvc
@ComponentScan(basePackages = {"me.exrates"},
        excludeFilters = {
        //@ComponentScan.Filter(type = FilterType.REGEX, pattern = "me.exrates.*"),
        @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, value = MicroserviceConditional.class) })
//@Import({CacheConfiguration.class})
public class TestContextConfig extends WebMvcConfigurerAdapter {
    private MessageSource messageSource;
    private UserService userService;
    private BalanceService balanceService;
    private ExchangeRatesHolder exchangeRatesHolder;
    private LocaleResolver localeResolver;
    private RefillService refillService;
    private WalletService walletService;
    private WithdrawService withdrawService;
    private TransferService transferService;
    private CurrencyService currencyService;
    private NgOrderService ngOrderService;
    private OrderService orderService;
    private DashboardService dashboardService;
    private ObjectMapper objectMapper;
    private StopOrderService stopOrderService;
    private UserOperationService userOperationService;
    private IEOService ieoService;
    private KYCService kycService;
    private KYCSettingsService kycSettingsService;
    private SendMailService sendMailService;
    private ChatService chatService;
    private IpBlockingService ipBlockingService;
    private NgUserService ngUserService;
    private SimpMessagingTemplate simpMessagingTemplate;
    private G2faService g2faService;
    private TelegramChatDao telegramChatDao;
    private NewsParser newsParser;
    private InputOutputService inputOutputService;
    private MerchantService merchantService;
    private MerchantServiceContext merchantServiceContext;
    private GtagRefillService gtagRefillService;
    private OpenApiTokenService openApiTokenService;
    private SecureService secureService;
    private RateLimitService rateLimitService;
    private AuthTokenService authTokenService;
    private ReferralService referralService;
    private UserDetailsService userDetailsService;
    private PasswordEncoder passwordEncoder;
    private NotificationService notificationService;
    private SessionParamsService sessionService;
    private PageLayoutSettingsService layoutSettingsService;
    private UserVerificationService verificationService;
    private StompMessenger stompMessenger;
    private OpenApiCommonService openApiCommonService;
    private ExchangeApi exchangeApi;
    private CloseableHttpClient closeableHttpClient;

    @Bean
    public Twitter twitter() {
        return Mockito.mock(Twitter.class);
    }

    @Bean
    public UserDetailsService getUserDetailsService() {
        return Mockito.mock(UserDetailsService.class);
    }

    @Bean
    public LocaleResolver localeResolver() {
        return Mockito.mock(LocaleResolver.class);
    }

    @Bean
    public TransactionDescription transactionDescription() {
        return new TransactionDescription();
    }

    @Bean
    public ObjectMapper getObjectMapper() {
        return new ObjectMapper();
    }

    @Bean(name = "SupportMailSender")
    public JavaMailSenderImpl javaMailSenderImpl() {
        return new JavaMailSenderImpl();
    }

    @Bean(name = "MandrillMailSender")
    public JavaMailSenderImpl mandrillMailSenderImpl() {
        return new JavaMailSenderImpl();
    }

    @Bean(name = "InfoMailSender")
    public JavaMailSenderImpl infoMailSenderImpl() {
        return new JavaMailSenderImpl();
    }

    @Bean(name = "ExratesSessionRegistry")
    public SessionRegistry sessionRegistry() {
        return new SessionRegistryImpl();
    }

    @Bean
    public JdbcTemplate jdbcTemplate(DataSource dataSource) {
        return new JdbcTemplate(dataSource);
    }

    @Bean
    @Primary
    public NamedParameterJdbcTemplate namedParameterJdbcTemplate(DataSource dataSource) {
        return new NamedParameterJdbcTemplate(dataSource);
    }

    @Bean(name = "slaveForReportsTemplate")
    public NamedParameterJdbcTemplate slaveForReportsTemplate(DataSource dataSource) {
        return new NamedParameterJdbcTemplate(dataSource);
    }

    @Bean(name = "masterTemplate")
    public NamedParameterJdbcTemplate masterNamedParameterJdbcTemplate(DataSource dataSource) {
        return new NamedParameterJdbcTemplateWrapper(dataSource);
    }

    @Bean(name = "slaveTemplate")
    public NamedParameterJdbcTemplate slaveNamedParameterJdbcTemplate(DataSource dataSource) {
        return new NamedParameterJdbcTemplateWrapper(dataSource);
    }

    @Bean
    public CloseableHttpClient closeableHttpClient() {
        return Mockito.mock(CloseableHttpClient.class);
    }

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

    @Bean
    public SSMGetter ssmGetter() {
        return password -> "root";
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public OpenApiPrivateV1Controller openApiPrivateV1Controller() {
        return new OpenApiPrivateV1Controller(walletService, userService, userOperationService, messageSource, orderService);
    }

    @Bean
    public OpenApiPublicV1Controller openApiPublicV1Controller() {
        return new OpenApiPublicV1Controller(orderService);
    }

    @Bean
    public OpenApiOrderController openApiOrderController() {
        return new OpenApiOrderController(openApiCommonService, orderService, userService);
    }

    @Bean
    public OpenApiPublicController openApiPublicController() {
        return new OpenApiPublicController(orderService, currencyService, exchangeApi);
    }

    @Bean
    public OpenApiUserInfoController openApiUserInfoController() {
        return new OpenApiUserInfoController(walletService, orderService, userService);
    }

    @Bean
    public LanguageController languageController() {
        return new LanguageController(userService);
    }

    @Bean
    public NgBalanceController ngBalanceController() {
        return new NgBalanceController(balanceService,
                exchangeRatesHolder,
                localeResolver,
                refillService,
                walletService,
                withdrawService,
                userService,
                transferService);
    }

    @Bean
    public NgChartController ngChartController() {
        return new NgChartController(currencyService, ngOrderService, orderService);
    }

    @Bean
    public NgDashboardController ngDashboardController() {
        return new NgDashboardController(dashboardService,
                currencyService,
                orderService,
                userService,
                localeResolver,
                ngOrderService,
                objectMapper,
                stopOrderService,
                userOperationService);
    }

    @Bean
    public NgDownloadController ngDownloadController() {
        return new NgDownloadController(userService, orderService, currencyService, localeResolver, balanceService);
    }

    @Bean
    public NgIEOController ngIEOController() {
        return new NgIEOController(ieoService, userService);
    }

    @Bean
    public NgKYCController ngKYCController() {
        return new NgKYCController(userService, kycService, kycSettingsService);
    }

    @Bean
    public NgMailingController ngMailingController() {
        return new NgMailingController(sendMailService);
    }

    @Bean
    public NgOptionsController ngOptionsController() {
        return new NgOptionsController();
    }

    @Bean
    public NgPublicController ngPublicController() {
        return new NgPublicController(chatService,
                currencyService,
                ipBlockingService,
                ieoService,
                userService,
                ngUserService,
                simpMessagingTemplate,
                orderService,
                g2faService,
                ngOrderService,
                telegramChatDao,
                exchangeRatesHolder,
                newsParser);
    }

    @Bean
    public NgRefillController ngRefillController() {
        return new NgRefillController(currencyService,
                inputOutputService,
                userService,
                merchantService,
                messageSource,
                refillService,
                userOperationService,
                merchantServiceContext,
                gtagRefillService);
    }

    @Bean
    public NgTokenSettingsController ngTokenSettingsController() {
        return new NgTokenSettingsController(openApiTokenService, userService, secureService, g2faService);
    }

    @Bean
    public NgTransferController ngTransferController() {
        return new NgTransferController(rateLimitService,
                transferService,
                userService,
                merchantService,
                localeResolver,
                userOperationService,
                inputOutputService,
                messageSource,
                secureService,
                g2faService,
                currencyService);
    }

    @Bean
    public NgTwoFaController ngTwoFaController() {
        return new NgTwoFaController(userService, g2faService, ngUserService);
    }

    @Bean
    public NgUserController ngUserController() {
        return new NgUserController(ipBlockingService,
                authTokenService,
                userService,
                referralService,
                secureService,
                g2faService,
                ngUserService,
                userDetailsService,
                passwordEncoder);
    }

    @Bean
    public NgUserSettingsController ngUserSettingsController() {
        return new NgUserSettingsController(authTokenService,
                userService,
                notificationService,
                sessionService,
                layoutSettingsService,
                verificationService,
                ipBlockingService,
                stompMessenger,
                objectMapper);
    }

    @Bean
    public NgWithdrawController ngWithdrawController() {
        return new NgWithdrawController(currencyService,
                g2faService,
                inputOutputService,
                merchantService,
                messageSource,
                secureService,
                userOperationService,
                userService,
                walletService,
                withdrawService);
    }

    @Bean
    public GlobalControllerExceptionHandler globalControllerExceptionHandler() {
        return new GlobalControllerExceptionHandler(messageSource, userService);
    }
}
