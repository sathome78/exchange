package web.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import me.exrates.config.WebSocketConfig;
import me.exrates.controller.filter.LoggingFilter;
import me.exrates.model.condition.MonolitConditional;
import me.exrates.model.dto.MosaicIdDto;
import me.exrates.security.config.SecurityConfig;
import me.exrates.service.NamedParameterJdbcTemplateWrapper;
import me.exrates.service.handler.RestResponseErrorHandler;
import me.exrates.service.nem.XemMosaicService;
import me.exrates.service.nem.XemMosaicServiceImpl;
import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.nem.core.model.primitive.Supply;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Primary;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.client.support.BasicAuthorizationInterceptor;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import org.springframework.web.servlet.i18n.AcceptHeaderLocaleResolver;
import org.springframework.web.servlet.i18n.CookieLocaleResolver;
import org.zeromq.ZMQ;

import javax.sql.DataSource;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import java.util.Locale;
import java.util.Properties;

@Configuration
@EnableWebMvc
@ComponentScan(basePackages = {"me.exrates"})
@Import(
        {
                SecurityConfig.class,
                WebSocketConfig.class
        }
)
@TestPropertySource(value = {
        "classpath:/mail.properties",
        "classpath:/merchants/qiwi.properties"
})
public class TestContextConfig extends WebMvcConfigurerAdapter {
    @Value("${mail_support.host}")
    String mailSupportHost;
    @Value("${mail_support.port}")
    String mailSupportPort;
    @Value("${mail_support.protocol}")
    String mailSupportProtocol;
    @Value("${mail_support.user}")
    String mailSupportUser;
    @Value("${mail_support.password}")
    String mailSupportPassword;
    @Value("${mail_mandrill.host}")
    String mailMandrillHost;
    @Value("${mail_mandrill.port}")
    String mailMandrillPort;
    @Value("${mail_mandrill.protocol}")
    String mailMandrillProtocol;
    @Value("${mail_mandrill.user}")
    String mailMandrillUser;
    @Value("${mail_mandrill.password}")
    String mailMandrillPassword;
    @Value("${mail_info.host}")
    String mailInfoHost;
    @Value("${mail_info.port}")
    String mailInfoPort;
    @Value("${mail_info.protocol}")
    String mailInfoProtocol;
    @Value("${mail_info.user}")
    String mailInfoUser;
    @Value("${mail_info.password}")
    String mailInfoPassword;

    @Value("${qiwi.client.id}")
    private String qiwiClientId;
    @Value("${qiwi.client.secret}")
    private String qiwiClientSecret;

    @Autowired
    @Qualifier("testDataSource")
    protected DataSource dataSource;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean(name = "masterHikariDataSource")
    public DataSource masterHikariDataSource() {
        return dataSource;
    }

    @Bean(name = "slaveHikariDataSource")
    public DataSource slaveHikariDataSource() {
        return dataSource;
    }

    @Bean(name = "slaveForReportsDataSource")
    public DataSource slaveForReportsDataSource() {
        return dataSource;
    }

    @Primary
    @DependsOn("masterHikariDataSource")
    @Bean(name = "masterTemplate")
    public NamedParameterJdbcTemplate masterNamedParameterJdbcTemplate(@Qualifier("masterHikariDataSource") DataSource dataSource) {
        return new NamedParameterJdbcTemplateWrapper(dataSource);
    }

    @DependsOn("slaveHikariDataSource")
    @Bean(name = "slaveTemplate")
    public NamedParameterJdbcTemplate slaveNamedParameterJdbcTemplate(@Qualifier("slaveHikariDataSource") DataSource dataSource) {
        return new NamedParameterJdbcTemplateWrapper(dataSource);
    }

    @DependsOn("slaveForReportsDataSource")
    @Bean(name = "slaveForReportsTemplate")
    public NamedParameterJdbcTemplate slaveForReportsTemplate(@Qualifier("slaveForReportsDataSource") DataSource dataSource) {
        return new NamedParameterJdbcTemplateWrapper(dataSource);
    }

    @Primary
    @DependsOn("masterHikariDataSource")
    @Bean
    public JdbcTemplate jdbcTemplate(@Qualifier("masterHikariDataSource") DataSource dataSource) {
        return new JdbcTemplate(dataSource);
    }

    @Primary
    @Bean(name = "masterTxManager")
    public PlatformTransactionManager masterPlatformTransactionManager() {
        return new DataSourceTransactionManager(masterHikariDataSource());
    }

    @Bean(name = "slaveTxManager")
    public PlatformTransactionManager slavePlatformTransactionManager() {
        return new DataSourceTransactionManager(slaveHikariDataSource());
    }

    @Bean(name = "transactionManagerForReports")
    public PlatformTransactionManager transactionManagerForReports() {
        return new DataSourceTransactionManager(slaveForReportsDataSource());
    }

    @Bean
    public static PropertySourcesPlaceholderConfigurer propertySourcesPlaceholderConfigurer() {
        return new PropertySourcesPlaceholderConfigurer();
    }

    @Bean(name = "SupportMailSender")
    public JavaMailSenderImpl javaMailSenderImpl() {
        final JavaMailSenderImpl mailSenderImpl = new JavaMailSenderImpl();
        mailSenderImpl.setHost(mailSupportHost);
        mailSenderImpl.setPort(Integer.parseInt(mailSupportPort));
        mailSenderImpl.setProtocol(mailSupportProtocol);
        mailSenderImpl.setUsername(mailSupportUser);
        mailSenderImpl.setPassword(mailSupportPassword);
        final Properties javaMailProps = new Properties();
        javaMailProps.put("mail.smtp.auth", true);
        javaMailProps.put("mail.smtp.starttls.enable", true);
        javaMailProps.put("mail.smtp.ssl.trust", mailSupportHost);
        mailSenderImpl.setJavaMailProperties(javaMailProps);
        return mailSenderImpl;
    }

    @Bean(name = "MandrillMailSender")
    public JavaMailSenderImpl mandrillMailSenderImpl() {
        final JavaMailSenderImpl mailSenderImpl = new JavaMailSenderImpl();
        mailSenderImpl.setHost(mailMandrillHost);
        mailSenderImpl.setPort(Integer.parseInt(mailMandrillPort));
        mailSenderImpl.setProtocol(mailMandrillProtocol);
        mailSenderImpl.setUsername(mailMandrillUser);
        mailSenderImpl.setPassword(mailMandrillPassword);
        final Properties javaMailProps = new Properties();
        javaMailProps.put("mail.smtp.auth", true);
        javaMailProps.put("mail.smtp.starttls.enable", true);
        javaMailProps.put("mail.smtp.ssl.trust", mailMandrillHost);
        mailSenderImpl.setJavaMailProperties(javaMailProps);
        return mailSenderImpl;
    }

    @Bean(name = "InfoMailSender")
    public JavaMailSenderImpl infoMailSenderImpl() {
        final JavaMailSenderImpl mailSenderImpl = new JavaMailSenderImpl();
        mailSenderImpl.setHost(mailInfoHost);
        mailSenderImpl.setPort(Integer.parseInt(mailInfoPort));
        mailSenderImpl.setProtocol(mailInfoProtocol);
        mailSenderImpl.setUsername(mailInfoUser);
        mailSenderImpl.setPassword(mailInfoPassword);
        final Properties javaMailProps = new Properties();
        javaMailProps.put("mail.smtp.auth", true);
        javaMailProps.put("mail.smtp.starttls.enable", true);
        javaMailProps.put("mail.smtp.ssl.trust", mailInfoHost);
        mailSenderImpl.setJavaMailProperties(javaMailProps);
        return mailSenderImpl;
    }

    @Bean
    public MessageSource messageSource() {
        ReloadableResourceBundleMessageSource messageSource = new ReloadableResourceBundleMessageSource();
        messageSource.setBasename("classpath:messages");
        messageSource.setDefaultEncoding("UTF-8");
        messageSource.setFallbackToSystemLocale(false);
        return messageSource;
    }

    @Bean
    public Client client() {
        Client build = ClientBuilder.newBuilder().build();
        build.register(new LoggingFilter());
        return build;
    }

    @Bean
    public ObjectMapper objectMapper() {
        return new ObjectMapper();
    }

    @Bean
    @Primary
    @Conditional(MonolitConditional.class)
    public RestTemplate restTemplate() {
        HttpClientBuilder b = HttpClientBuilder.create();
        HttpClient client = b.build();
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.setErrorHandler(new RestResponseErrorHandler());
        restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
        HttpComponentsClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory();
        requestFactory.setHttpClient(client);
        requestFactory.setConnectionRequestTimeout(25000);
        requestFactory.setReadTimeout(25000);
        restTemplate.setRequestFactory(requestFactory);
        return restTemplate;
    }

    /***tokens based on xem mosaic)****/
    @Bean(name = "dimCoinServiceImpl")
    @Conditional(MonolitConditional.class)
    public XemMosaicService dimCoinService() {
        return new XemMosaicServiceImpl(
                "DimCoin",
                "DIM",
                new MosaicIdDto("dim", "coin"),
                1000000,
                6,
                new Supply(9000000000L),
                10);
    }

    @Bean(name = "npxsDimServiceImpl")
    @Conditional(MonolitConditional.class)
    public XemMosaicService npxsService() {
        return new XemMosaicServiceImpl(
                "NPXSXEM",
                "NPXSXEM",
                new MosaicIdDto("pundix", "npxs"),
                1000000,
                6,
                new Supply(9000000000L),
                0);
    }

    @Bean(name = "dimEurServiceImpl")
    @Conditional(MonolitConditional.class)
    public XemMosaicService dimEurService() {
        return new XemMosaicServiceImpl(
                "DIM.EUR",
                "DIM.EUR",
                new MosaicIdDto("dim", "eur"),
                100,
                2,
                new Supply(81000000000L),
                10);
    }

    @Bean(name = "dimUsdServiceImpl")
    @Conditional(MonolitConditional.class)
    public XemMosaicService dimUsdService() {
        return new XemMosaicServiceImpl(
                "DIM.USD",
                "DIM.USD",
                new MosaicIdDto("dim", "usd"),
                100,
                2,
                new Supply(81000000000L),
                10);
    }

    @Bean(name = "digicServiceImpl")
    @Conditional(MonolitConditional.class)
    public XemMosaicService digicService() {
        return new XemMosaicServiceImpl(
                "DIGIT",
                "DIGIT",
                new MosaicIdDto("digit", "coin"),
                1000000,
                6,
                new Supply(8999999999L),
                10);
    }

    @Bean(name = "rwdsServiceImpl")
    @Conditional(MonolitConditional.class)
    public XemMosaicService rwdsService() {
        return new XemMosaicServiceImpl(
                "RWDS",
                "RWDS",
                new MosaicIdDto("rewards4u", "rwds"),
                100,
                2,
                new Supply(100000000L),
                0);
    }

    @Bean(name = "darcServiceImpl")
    @Conditional(MonolitConditional.class)
    public XemMosaicService darcService() {
        return new XemMosaicServiceImpl(
                "DARC",
                "DARC",
                new MosaicIdDto("darc_totalsupply", "darc"),
                1000000,
                6,
                new Supply(1000000000L),
                0);
    }

    @Bean("qiwiRestTemplate")
    @Conditional(MonolitConditional.class)
    public RestTemplate qiwiRestTemplate() {
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.getInterceptors().add(new BasicAuthorizationInterceptor(qiwiClientId, qiwiClientSecret));
        return restTemplate;
    }

    @Bean
    public ZMQ.Context zmqContext() {
        return ZMQ.context(1);
    }

    @Bean
    public LocaleResolver localeResolver() {
        CookieLocaleResolver resolver = new CookieLocaleResolver();
        resolver.setDefaultLocale(new Locale("en"));
        resolver.setCookieName("myAppLocaleCookie");
        resolver.setCookieMaxAge(3600);
        return resolver;
    }

    @Bean(name = "AcceptHeaderLocaleResolver")
    public LocaleResolver localeResolverRest() {
        AcceptHeaderLocaleResolver resolver = new AcceptHeaderLocaleResolver();
        resolver.setDefaultLocale(new Locale("en"));
        return resolver;
    }
}
