package me.exrates.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import me.exrates.aspect.LoggingAspect;
import me.exrates.controller.filter.RequestFilter;
import me.exrates.controller.handler.ChatWebSocketHandler;
import me.exrates.controller.interceptor.FinPassCheckInterceptor;
import me.exrates.controller.listener.StoreSessionListener;
import me.exrates.controller.listener.StoreSessionListenerImpl;
import me.exrates.model.converter.CurrencyPairConverter;
import me.exrates.model.enums.ChatLang;
import me.exrates.security.config.SecurityConfig;
import me.exrates.security.filter.VerifyReCaptchaSec;
import me.exrates.service.BitcoinService;
import me.exrates.service.EthereumCommonService;
import me.exrates.service.handler.RestResponseErrorHandler;
import me.exrates.service.impl.BitcoinServiceImpl;
import me.exrates.service.impl.EthTokenService;
import me.exrates.service.impl.EthTokenServiceImpl;
import me.exrates.service.impl.EthereumCommonServiceImpl;
import me.exrates.service.job.bot.QuartzJobFactory;
import me.exrates.service.token.TokenScheduler;
import me.exrates.service.util.ChatComponent;
import org.apache.commons.dbcp2.BasicDataSource;
import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.quartz.Scheduler;
import org.quartz.spi.JobFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.PropertiesFactoryBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.*;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.core.io.ClassPathResource;
import org.springframework.format.FormatterRegistry;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.support.StandardServletMultipartResolver;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import org.springframework.web.servlet.i18n.AcceptHeaderLocaleResolver;
import org.springframework.web.servlet.i18n.CookieLocaleResolver;
import org.springframework.web.servlet.i18n.LocaleChangeInterceptor;
import org.springframework.web.servlet.view.InternalResourceViewResolver;
import org.springframework.web.servlet.view.JstlView;

import javax.servlet.annotation.MultipartConfig;
import javax.sql.DataSource;
import java.util.*;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import static org.springframework.context.annotation.EnableLoadTimeWeaving.AspectJWeaving.ENABLED;

@EnableAsync
@Configuration
@EnableWebMvc
@EnableTransactionManagement
@EnableScheduling
@EnableAspectJAutoProxy
@ComponentScan({"me.exrates"})
@Import(
        {
                SecurityConfig.class, WebSocketConfig.class
        }
)
@PropertySource(value = {
  "classpath:/db.properties",
    "classpath:/uploadfiles.properties",
    "classpath:/news.properties",
    "classpath:/mail.properties"})
@MultipartConfig(location = "/tmp")
public class WebAppConfig extends WebMvcConfigurerAdapter {

    private
    @Value("${db.user}")
    String dbUser;
    private
    @Value("${db.password}")
    String dbPassword;
    private
    @Value("${db.url}")
    String dbUrl;
    private
    @Value("${db.classname}")
    String dbClassname;
    private
    @Value("${upload.userFilesDir}")
    String userFilesDir;
    private
    @Value("${upload.userFilesLogicalDir}")
    String userFilesLogicalDir;
    private
    @Value("${news.locationDir}")
    String newsLocationDir;
    private
    @Value("${news.urlPath}")
    String newsUrlPath;

    @Value("${news.ext.locationDir}")
    String newsExtLocationDir;
    @Value("${news.newstopic.urlPath}")
    private String newstopicUrlPath;
    @Value("${news.materialsView.urlPath}")
    private String materialsViewUrlPath;
    @Value("${news.webinar.urlPath}")
    private String webinarUrlPath;
    @Value("${news.event.urlPath}")
    private String eventUrlPath;
    @Value("${news.feastDay.urlPath}")
    private String feastDayUrlPath;
    @Value("${news.page.urlPath}")
    private String pageUrlPath;

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




    @Bean
    public static PropertySourcesPlaceholderConfigurer propertySourcesPlaceholderConfigurer() {
        return new PropertySourcesPlaceholderConfigurer();
    }

    /*@Bean(name = "dataSource")*/
    public DataSource dataSource() {
        final BasicDataSource dataSource = new BasicDataSource();
        dataSource.setDriverClassName(dbClassname);
        dataSource.setUrl(dbUrl);
        dataSource.setUsername(dbUser);
        dataSource.setPassword(dbPassword);
        return dataSource;
    }
    
    @Bean(name = "hikariDataSource")
    public DataSource hikariDataSource() {
        HikariConfig hikariConfig = new HikariConfig();
        hikariConfig.setDriverClassName(dbClassname);
        hikariConfig.setJdbcUrl(dbUrl);
        hikariConfig.setUsername(dbUser);
        hikariConfig.setPassword(dbPassword);
        hikariConfig.setMaximumPoolSize(50);
        return new HikariDataSource(hikariConfig);
    }

    @DependsOn("hikariDataSource")
    @Bean
    public NamedParameterJdbcTemplate namedParameterJdbcTemplate(DataSource dataSource) {
        return new NamedParameterJdbcTemplate(dataSource);
    }

    @DependsOn("hikariDataSource")
    @Bean
    public JdbcTemplate jdbcTemplate(DataSource dataSource) {
        return new JdbcTemplate(dataSource);
    }

    @Bean
    public InternalResourceViewResolver viewResolver() {
        InternalResourceViewResolver viewResolver = new InternalResourceViewResolver();
        viewResolver.setViewClass(JstlView.class);
        viewResolver.setPrefix("/WEB-INF/jsp/");
        viewResolver.setSuffix(".jsp");
        viewResolver.setExposedContextBeanNames("captchaProperties");
        return viewResolver;
    }

    @Bean(name = "captchaProperties")
    public PropertiesFactoryBean captchaProperties() {
        PropertiesFactoryBean prop = new PropertiesFactoryBean();
        prop.setLocation(new ClassPathResource("captcha.properties"));
        return prop;
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

    @Bean
    public VerifyReCaptchaSec verifyReCaptcha() {
        return new VerifyReCaptchaSec();
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/client/**").addResourceLocations("/client/");
        registry.addResourceHandler("/favicon.ico").addResourceLocations("/client/img/");
        registry.addResourceHandler("/**").addResourceLocations("/public/");
        registry.addResourceHandler(newsUrlPath + "/**").addResourceLocations("file:" + newsLocationDir);
        registry.addResourceHandler(userFilesLogicalDir + "/**").addResourceLocations("file:" + userFilesDir);
        registry.addResourceHandler(newstopicUrlPath + "/**").addResourceLocations("file:" + newsExtLocationDir);
        registry.addResourceHandler(materialsViewUrlPath + "/**").addResourceLocations("file:" + newsExtLocationDir);
        registry.addResourceHandler(webinarUrlPath + "/**").addResourceLocations("file:" + newsExtLocationDir);
        registry.addResourceHandler(eventUrlPath + "/**").addResourceLocations("file:" + newsExtLocationDir);
        registry.addResourceHandler(feastDayUrlPath + "/**").addResourceLocations("file:" + newsExtLocationDir);
        registry.addResourceHandler(pageUrlPath + "/**").addResourceLocations("file:" + newsExtLocationDir);
        registry.addResourceHandler("/rest" + userFilesLogicalDir + "/**").addResourceLocations("file:" + userFilesDir);
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        LocaleChangeInterceptor interceptor = new LocaleChangeInterceptor();
        interceptor.setParamName("locale");
        registry.addInterceptor(interceptor);
        registry.addInterceptor(new FinPassCheckInterceptor());
    }

    @Bean
    public PlatformTransactionManager platformTransactionManager() {
        return new DataSourceTransactionManager(hikariDataSource());
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
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

    @Bean(name = "tokenScheduler", initMethod = "init", destroyMethod = "destroy")
    public TokenScheduler tokenScheduler() {
        return new TokenScheduler();
    }

    @Override
    public void addFormatters(FormatterRegistry registry) {
        registry.addConverter(new CurrencyPairConverter());
        super.addFormatters(registry);
    }

    @Bean
    public EnumMap<ChatLang, ChatWebSocketHandler> handlers() {
        final EnumMap<ChatLang, ChatWebSocketHandler> handlers = new EnumMap<>(ChatLang.class);
        for (ChatLang lang : ChatLang.values()) {
            handlers.put(lang, new ChatWebSocketHandler());
        }
        return handlers;
    }

    @Bean
    public EnumMap<ChatLang, ChatComponent> chatComponents() {
        final EnumMap<ChatLang, ChatComponent> handlers = new EnumMap<>(ChatLang.class);
        for (ChatLang lang : ChatLang.values()) {
            final ChatComponent chatComponent = new ChatComponent(new ReentrantReadWriteLock(), new TreeSet<>());
            handlers.put(lang, chatComponent);
        }
        return handlers;
    }

    @Bean(name = "multipartResolver")
    public StandardServletMultipartResolver resolver() {
        return new StandardServletMultipartResolver();
    }


    @Bean
    public RequestFilter requestFilter() {
        return RequestFilter.getInstance();
    }

    @Bean
    public StoreSessionListener storeSessionListener() {
        return new StoreSessionListenerImpl();
    }

    
    @Bean
    public LoggingAspect loggingAspect() {
        return new LoggingAspect();
    }


    @Bean(name = "bitcoinServiceImpl")
    public BitcoinService bitcoinService() {
        return new BitcoinServiceImpl("merchants/bitcoin_wallet.properties",
                "Bitcoin", "BTC", 4);
    }

    @Bean(name = "litecoinServiceImpl")
    public BitcoinService litecoinService() {
        return new BitcoinServiceImpl("merchants/litecoin_wallet.properties",
                "Litecoin", "LTC", 4);
    }
    
    @Bean(name = "dashServiceImpl")
    public BitcoinService dashService() {
        return new BitcoinServiceImpl("merchants/dash_wallet.properties",
                "Dash", "DASH", 4);
    }

    @Bean(name = "atbServiceImpl")
    public BitcoinService atbService() {
        return new BitcoinServiceImpl("merchants/atb_wallet.properties",
                "ATB", "ATB", 10);
    }
    @Bean(name = "bitcoinCashServiceImpl")
    public BitcoinService bchService() {
        return new BitcoinServiceImpl("merchants/bitcoin_cash_wallet.properties",
                "Bitcoin Cash", "BCH", 4);
    }

    @Bean(name = "dogecoinServiceImpl")
    public BitcoinService dogeService() {
        return new BitcoinServiceImpl("merchants/dogecoin_wallet.properties",
                "Dogecoin", "DOGE", 4);
    }


    @Bean(name = "ethereumServiceImpl")
    public EthereumCommonService ethereumService() {
        return new EthereumCommonServiceImpl("merchants/ethereum.properties",
                "Ethereum", "ETH", 12);
    }

    @Bean(name = "ethereumClassicServiceImpl")
    public EthereumCommonService ethereumClassicService() {
        return new EthereumCommonServiceImpl("merchants/ethereumClassic.properties",
                "Ethereum Classic", "ETC", 12);
    }

    @Bean(name = "eosServiceImpl")
    public EthTokenService EosService() {
        List<String> tokensList = new ArrayList<>();
        tokensList.add("0x86Fa049857E0209aa7D9e616F7eb3b3B78ECfdb0");

        return new EthTokenServiceImpl(
                tokensList,
                "EOS",
                "EOS");
    }

    @Bean(name = "repServiceImpl")
    public EthTokenService RepService() {
        List<String> tokensList = new ArrayList<>();
        tokensList.add("0xe94327d07fc17907b4db788e5adf2ed424addff6");

        return new EthTokenServiceImpl(
                tokensList,
                "REP",
                "REP");
    }

    @Bean
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
        return new RestTemplate();
    }

    @Bean
    public JobFactory jobFactory(ApplicationContext applicationContext) {

        QuartzJobFactory sampleJobFactory = new QuartzJobFactory();
        sampleJobFactory.setApplicationContext(applicationContext);
        return sampleJobFactory;
    }

    @Bean
    public SchedulerFactoryBean schedulerFactoryBean(ApplicationContext applicationContext) {

        SchedulerFactoryBean factory = new SchedulerFactoryBean();

        factory.setOverwriteExistingJobs(true);
        factory.setJobFactory(jobFactory(applicationContext));
        return factory;
    }

    @Bean
    public Scheduler botOrderCreationScheduler(ApplicationContext applicationContext) {
        return schedulerFactoryBean(applicationContext).getScheduler();
    }

    @Bean
    public ObjectMapper objectMapper() {
        return new ObjectMapper();
    }

}
