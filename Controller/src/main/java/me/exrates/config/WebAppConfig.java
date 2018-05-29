package me.exrates.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import lombok.extern.log4j.Log4j2;
import me.exrates.aspect.LoggingAspect;
import me.exrates.controller.handler.ChatWebSocketHandler;
import me.exrates.controller.interceptor.FinPassCheckInterceptor;
import me.exrates.controller.listener.StoreSessionListener;
import me.exrates.controller.listener.StoreSessionListenerImpl;
import me.exrates.model.converter.CurrencyPairConverter;
import me.exrates.model.dto.MosaicIdDto;
import me.exrates.model.enums.ChatLang;
import me.exrates.security.config.SecurityConfig;
import me.exrates.security.filter.VerifyReCaptchaSec;
import me.exrates.service.BitcoinService;
import me.exrates.service.ethereum.*;
import me.exrates.service.handler.RestResponseErrorHandler;
import me.exrates.service.impl.BitcoinServiceImpl;
import me.exrates.service.job.QuartzJobFactory;
import me.exrates.service.nem.XemMosaicService;
import me.exrates.service.nem.XemMosaicServiceImpl;
import me.exrates.service.lisk.LiskService;
import me.exrates.service.lisk.LiskServiceImpl;
import me.exrates.service.qtum.QtumTokenService;
import me.exrates.service.qtum.QtumTokenServiceImpl;
import me.exrates.service.nem.XemMosaicService;
import me.exrates.service.nem.XemMosaicServiceImpl;
import me.exrates.service.stellar.StellarAsset;
import me.exrates.service.token.TokenScheduler;
import me.exrates.service.util.ChatComponent;
import org.apache.commons.dbcp2.BasicDataSource;
import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.nem.core.model.primitive.Supply;
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
import org.springframework.social.twitter.api.Twitter;
import org.springframework.social.twitter.api.impl.TwitterTemplate;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.support.StandardServletMultipartResolver;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.config.annotation.*;
import org.springframework.web.servlet.i18n.AcceptHeaderLocaleResolver;
import org.springframework.web.servlet.i18n.CookieLocaleResolver;
import org.springframework.web.servlet.i18n.LocaleChangeInterceptor;
import org.springframework.web.servlet.view.InternalResourceViewResolver;
import org.springframework.web.servlet.view.JstlView;
import org.web3j.utils.Convert;
import org.zeromq.ZMQ;

import javax.annotation.PostConstruct;
import javax.servlet.annotation.MultipartConfig;
import javax.sql.DataSource;
import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.util.*;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.stream.Collectors;

@Log4j2(topic = "config")
@EnableAsync
@Configuration
@EnableWebMvc
@EnableTransactionManagement
@EnableScheduling
@EnableAspectJAutoProxy
@ComponentScan({"me.exrates"})
@Import(
        {
                SecurityConfig.class, WebSocketConfig.class, CryptocurrencyConfig.class
        }
)
@PropertySource(value = {
  "classpath:/db.properties",
    "classpath:/uploadfiles.properties",
    "classpath:/news.properties",
    "classpath:/mail.properties",
    "classpath:/angular.properties",
    "classpath:/twitter.properties",
    "classpath:/angular.properties",
    "classpath:/merchants/stellar.properties"})
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

    @Value("${angular.allowed.origin}")
    private String angularAllowedOrigin;

    @Value("${twitter.appId}")
    private String twitterConsumerKey;
    @Value("${twitter.appSecret}")
    private String twitterConsumerSecret;
    @Value("${twitter.accessToken}")
    private String twitterAccessToken;
    @Value("${twitter.accessTokenSecret}")
    private String twitterAccessTokenSecret;


    @PostConstruct
    public void init() {
        log.debug("initNem");
        RuntimeMXBean runtimeMxBean = ManagementFactory.getRuntimeMXBean();
        List<String> arguments = runtimeMxBean.getInputArguments();
        log.debug(arguments.stream().collect(Collectors.joining("; ")));
    }



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

    @Override
    public void configureAsyncSupport(AsyncSupportConfigurer configurer) {
        configurer.setDefaultTimeout(120_000L);
        super.configureAsyncSupport(configurer);
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
    public StoreSessionListener storeSessionListener() {
        return new StoreSessionListenerImpl();
    }


    @Bean
    public LoggingAspect loggingAspect() {
        return new LoggingAspect();
    }

    @Bean(name = "nsrServiceImpl")
    public BitcoinService nsrService() {
        return new BitcoinServiceImpl("merchants/nushares_wallet.properties",
                "NuShares", "NSR", 4, 20, false, false);
    }

    @Bean(name = "amlServiceImpl")
    public BitcoinService amlService() {
        return new BitcoinServiceImpl("merchants/aml_wallet.properties",
                "AML", "ABTC", 4, 20, false);
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

    @Bean(name = "etzServiceImpl")
    public EthereumCommonService etzService() {
        return new EthereumCommonServiceImpl("merchants/etherzero.properties",
                "EtherZero", "ETZ", 12);
    }

    @Bean(name = "eosServiceImpl")
    public EthTokenService EosService() {
        List<String> tokensList = new ArrayList<>();
        tokensList.add("0x86fa049857e0209aa7d9e616f7eb3b3b78ecfdb0");
        return new EthTokenServiceImpl(
                tokensList,
                "EOS",
                "EOS", true, ExConvert.Unit.ETHER);
    }

    @Bean(name = "repServiceImpl")
    public EthTokenService RepService() {
        List<String> tokensList = new ArrayList<>();
        tokensList.add("0xe94327d07fc17907b4db788e5adf2ed424addff6");
        return new EthTokenServiceImpl(
                tokensList,
                "REP",
                "REP", true, ExConvert.Unit.ETHER);
    }

    @Bean(name = "golemServiceImpl")
    public EthTokenService GolemService() {
        List<String> tokensList = new ArrayList<>();
        tokensList.add("0xa74476443119a942de498590fe1f2454d7d4ac0d");
        return new EthTokenServiceImpl(
                tokensList,
                "Golem",
                "GNT", false, ExConvert.Unit.ETHER);
    }

    @Bean(name = "omgServiceImpl")
    public EthTokenService OmgService() {
        List<String> tokensList = new ArrayList<>();
        tokensList.add("0xd26114cd6ee289accf82350c8d8487fedb8a0c07");
        return new EthTokenServiceImpl(
                tokensList,
                "OmiseGo",
                "OMG", true, ExConvert.Unit.ETHER);
    }

    @Bean(name = "bnbServiceImpl")
    public EthTokenService BnbService() {
        List<String> tokensList = new ArrayList<>();
        tokensList.add("0xb8c77482e45f1f44de1745f52c74426c631bdd52");
        return new EthTokenServiceImpl(
                tokensList,
                "BinanceCoin",
                "BNB", true, ExConvert.Unit.ETHER);
    }

    @Bean(name = "atlServiceImpl")
    public EthTokenService ATLANTService() {
        List<String> tokensList = new ArrayList<>();
        tokensList.add("0x78b7fada55a64dd895d8c8c35779dd8b67fa8a05");
        return new EthTokenServiceImpl(
                tokensList,
                "ATLANT",
                "ATL", true, ExConvert.Unit.ETHER);
    }

    @Bean(name = "bitRentServiceImpl")
    public EthTokenService BitRentService() {
        List<String> tokensList = new ArrayList<>();
        tokensList.add("0x1fe70be734e473e5721ea57c8b5b01e6caa52686");
        return new EthTokenServiceImpl(
                tokensList,
                "BitRent",
                "RNTB", true, ExConvert.Unit.ETHER);
    }

    @Bean(name = "nioServiceImpl")
    public EthTokenService NioService() {
        List<String> tokensList = new ArrayList<>();
        tokensList.add("0x5554e04e76533e1d14c52f05beef6c9d329e1e30");
        return new EthTokenServiceImpl(
                tokensList,
                "NIO",
                "NIO", true, ExConvert.Unit.WEI);
    }

    @Bean(name = "gosServiceImpl")
    public EthTokenService GosService() {
        List<String> tokensList = new ArrayList<>();
        tokensList.add("0x5ce8e61f28f5948de4913bcaada90039481f1f53");
        return new EthTokenServiceImpl(
                tokensList,
                "GOS",
                "GOS", true, ExConvert.Unit.MWEI);
    }


    @Bean(name = "bptnServiceImpl")
    public EthTokenService BptnRentService() {
        List<String> tokensList = new ArrayList<>();
        tokensList.add("0x6c22b815904165f3599f0a4a092d458966bd8024");
        return new EthTokenServiceImpl(
                tokensList,
                "BPTN",
                "BPTN", true, ExConvert.Unit.ETHER);
    }

    @Bean(name = "nbcServiceImpl")
    public EthTokenService NbcService() {
        List<String> tokensList = new ArrayList<>();
        tokensList.add("0x9f195617fa8fbad9540c5d113a99a0a0172aaedc");
        return new EthTokenServiceImpl(
                tokensList,
                "NBC",
                "NBC", true, ExConvert.Unit.ETHER);
    }



    @Bean(name = "taxiServiceImpl")
    public EthTokenService taxiRentService() {
        List<String> tokensList = new ArrayList<>();
        tokensList.add("0x8409e9c7d23ae978e809866abf46ac2e116f4d0e");
        return new EthTokenServiceImpl(
                tokensList,
                "TAXI",
                "TAXI", true, ExConvert.Unit.ETHER);
    }

    @Bean(name = "nbtkServiceImpl")
    public EthTokenService nbtkRentService() {
        List<String> tokensList = new ArrayList<>();
        tokensList.add("0xb0e6f83eba6a4ea20617e134b1aee30fcb0ac634");
        return new EthTokenServiceImpl(
                tokensList,
                "NBTK",
                "NBTK", false, ExConvert.Unit.WEI);
    }

    @Bean(name = "ucashServiceImpl")
    public EthTokenService ucashService() {
        List<String> tokensList = new ArrayList<>();
        tokensList.add("0x92e52a1a235d9a103d970901066ce910aacefd37");
        return new EthTokenServiceImpl(
                tokensList,
                "UCASH",
                "UCASH", true, ExConvert.Unit.AIWEI);
    }

    @Bean(name = "nacServiceImpl")
    public EthTokenService nacService() {
        List<String> tokensList = new ArrayList<>();
        tokensList.add("0x8d80de8a78198396329dfa769ad54d24bf90e7aa");
        return new EthTokenServiceImpl(
                tokensList,
                "NAC",
                "NAC", true, ExConvert.Unit.ETHER);
    }

    @Bean(name = "echtServiceImpl")
    public EthTokenService echtService() {
        List<String> tokensList = new ArrayList<>();
        tokensList.add("0x1a2277c83930b7a64c3e3d5544eaa8c4f946b1b7");
        return new EthTokenServiceImpl(
                tokensList,
                "ECHT",
                "ECHT", true, ExConvert.Unit.ETHER);
    }

    @Bean(name = "idhServiceImpl")
    public EthTokenService idhService() {
        List<String> tokensList = new ArrayList<>();
        tokensList.add("0x5136c98a80811c3f46bdda8b5c4555cfd9f812f0");
        return new EthTokenServiceImpl(
                tokensList,
                "IDH",
                "IDH", false, ExConvert.Unit.MWEI);
    }

    @Bean(name = "cobcServiceImpl")
    public EthTokenService cobcService() {
        List<String> tokensList = new ArrayList<>();
        tokensList.add("0x6292cec07c345c6c6953e9166324f58db6d9f814");
        return new EthTokenServiceImpl(
                tokensList,
                "COBC",
                "COBC", true, ExConvert.Unit.ETHER);
    }


    @Bean(name = "bcsServiceImpl")
    public EthTokenService bcsService() {
        List<String> tokensList = new ArrayList<>();
        tokensList.add("0x98bde3a768401260e7025faf9947ef1b81295519");
        return new EthTokenServiceImpl(
                tokensList,
                "BCS",
                "BCS", true, ExConvert.Unit.ETHER);
    }

    @Bean(name = "uqcServiceImpl")
    public EthTokenService uqcService() {
        List<String> tokensList = new ArrayList<>();
        tokensList.add("0xd01db73e047855efb414e6202098c4be4cd2423b");
        return new EthTokenServiceImpl(
                tokensList,
                "UQC",
                "UQC", true, ExConvert.Unit.ETHER);
    }

    @Bean(name = "inoServiceImpl")
    public EthTokenService inoService() {
        List<String> tokensList = new ArrayList<>();
        tokensList.add("0xc9859fccc876e6b4b3c749c5d29ea04f48acb74f");
        return new EthTokenServiceImpl(
                tokensList,
                "INO",
                "INO", true, ExConvert.Unit.WEI);
    }

    @Bean(name = "profitServiceImpl")
    public EthTokenService profitService() {
        List<String> tokensList = new ArrayList<>();
        tokensList.add("0xe540935cabf4c2bac547c8067cbbc2991d030122");
        return new EthTokenServiceImpl(
                tokensList,
                "PROFIT",
                "PROFIT", false, ExConvert.Unit.ETHER);
    }


    @Bean(name = "ormeServiceImpl")
    public EthTokenService ormeService() {
        List<String> tokensList = new ArrayList<>();
        tokensList.add("0x516e5436bafdc11083654de7bb9b95382d08d5de");
        return new EthTokenServiceImpl(
                tokensList,
                "ORME",
                "ORME", true, ExConvert.Unit.AIWEI);
    }

    @Bean(name = "bezServiceImpl")
    public EthTokenService bezService() {
        List<String> tokensList = new ArrayList<>();
        tokensList.add("0x3839d8ba312751aa0248fed6a8bacb84308e20ed");
        return new EthTokenServiceImpl(
                tokensList,
                "BEZ",
                "BEZ", true, ExConvert.Unit.ETHER);
    }

    @Bean(name = "simServiceImpl")
    public EthTokenService simService() {
        List<String> tokensList = new ArrayList<>();
        tokensList.add("0x7528e3040376edd5db8263db2f5bd1bed91467fb");
        return new EthTokenServiceImpl(
                tokensList,
                "SIM",
                "SIM", false, ExConvert.Unit.ETHER);
    }

    @Bean(name = "amnServiceImpl")
    public EthTokenService amnService() {
        List<String> tokensList = new ArrayList<>();
        tokensList.add("0x737f98ac8ca59f2c68ad658e3c3d8c8963e40a4c");
        return new EthTokenServiceImpl(
                tokensList,
                "AMN",
                "AMN", true, ExConvert.Unit.ETHER);
    }

    @Bean(name = "getServiceImpl")
    public EthTokenService getService() {
        List<String> tokensList = new ArrayList<>();
        tokensList.add("0x8a854288a5976036a725879164ca3e91d30c6a1b");
        return new EthTokenServiceImpl(
                tokensList,
                "GET",
                "GET", true, ExConvert.Unit.ETHER);
    }

    @Bean(name = "flotServiceImpl")
    public EthTokenService flotService() {
        List<String> tokensList = new ArrayList<>();
        tokensList.add("0x049399a6b048d52971f7d122ae21a1532722285f");
        return new EthTokenServiceImpl(
                tokensList,
                "FLOT",
                "FLOT", true, ExConvert.Unit.ETHER);
    }

    @Bean(name = "vdgServiceImpl")
    public EthTokenService vdgService() {
        List<String> tokensList = new ArrayList<>();
        tokensList.add("0x57c75eccc8557136d32619a191fbcdc88560d711");
        return new EthTokenServiceImpl(
                tokensList,
                "VDG",
                "VDG", true, ExConvert.Unit.WEI);
    }

    @Bean(name = "dgtxServiceImpl")
    public EthTokenService dgtxService() {
        List<String> tokensList = new ArrayList<>();
        tokensList.add("0x1c83501478f1320977047008496dacbd60bb15ef");
        return new EthTokenServiceImpl(
                tokensList,
                "DGTX",
                "DGTX", true, ExConvert.Unit.ETHER);
    }

    @Bean(name = "droneServiceImpl")
    public EthTokenService droneService() {
        List<String> tokensList = new ArrayList<>();
        tokensList.add("0x131f193692b5cce8c87d12ff4f7aa1d4e1668f1e");
        return new EthTokenServiceImpl(
                tokensList,
                "DRONE",
                "DRONE", true, ExConvert.Unit.WEI);
    }

    @Bean(name = "wdscServiceImpl")
    public EthTokenService wdscService() {
        List<String> tokensList = new ArrayList<>();
        tokensList.add("0x170cf89358ce17742955ea43927da5fc1e8e1211");
        return new EthTokenServiceImpl(
                tokensList,
                "WDSC",
                "WDSC", true, ExConvert.Unit.ETHER, new BigInteger("1"));
    }

    @Bean(name = "fsbtServiceImpl")
    public EthTokenService fsbtService() {
        List<String> tokensList = new ArrayList<>();
        tokensList.add("0x1ed7ae1f0e2fa4276dd7ddc786334a3df81d50c0");
        return new EthTokenServiceImpl(
                tokensList,
                "FSBT",
                "FSBT", true, ExConvert.Unit.ETHER);
    }

    @Bean(name = "iprServiceImpl")
    public EthTokenService iprService() {
        List<String> tokensList = new ArrayList<>();
        tokensList.add("0x68b539381b317a04190c3bd7ce95b9233275d02a");
        return new EthTokenServiceImpl(
                tokensList,
                "IPR",
                "IPR", false, ExConvert.Unit.ETHER);
    }

    @Bean(name = "casServiceImpl")
    public EthTokenService casService() {
        List<String> tokensList = new ArrayList<>();
        tokensList.add("0xe8780b48bdb05f928697a5e8155f672ed91462f7");
        return new EthTokenServiceImpl(
                tokensList,
                "CAS",
                "CAS", true, ExConvert.Unit.ETHER);
    }

    @Bean(name = "inkServiceImpl")
    public EthTokenService InkService() {
        List<String> tokensList = new ArrayList<>();
        tokensList.add("0xf4c90e18727c5c76499ea6369c856a6d61d3e92e");
        return new EthTokenServiceImpl(
                tokensList,
                "Ink",
                "INK", true, ExConvert.Unit.GWEI);
    }

    @Bean(name = "rthServiceImpl")
    public EthTokenService rthService() {
        List<String> tokensList = new ArrayList<>();
        tokensList.add("0x3fd8f39a962efda04956981c31ab89fab5fb8bc8");
        return new EthTokenServiceImpl(
                tokensList,
                "RTH",
                "RTH", true, ExConvert.Unit.ETHER);
    }

    @Bean(name = "egtServiceImpl")
    public EthTokenService egtService() {
        List<String> tokensList = new ArrayList<>();
        tokensList.add("0x5dbac24e98e2a4f43adc0dc82af403fca063ce2c");
        return new EthTokenServiceImpl(
                tokensList,
                "EGT",
                "EGT", true, ExConvert.Unit.ETHER);
    }

    //    Qtum tokens:
    @Bean(name = "spcServiceImpl")
    public QtumTokenService spcService() {
        List<String> tokensList = new ArrayList<>();
        tokensList.add("57931faffdec114056a49adfcaa1caac159a1a25");

        return new QtumTokenServiceImpl(tokensList, "SPC", "SPC", ExConvert.Unit.AIWEI);
    }


    /***tokens based on xem mosaic)****/
    @Bean(name = "dimCoinServiceImpl")
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


    @Bean(name = "npxsServiceImpl")
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

    /***stellarAssets****/
    private @Value("${stellar.slt.emitter}")String SLT_EMMITER;
    @Bean(name = "sltStellarService")
    public StellarAsset sltStellarService() {
        return new StellarAsset("SLT",
                "SLT",
                "SLT",
        SLT_EMMITER);
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

        QuartzJobFactory jobFactory = new QuartzJobFactory();
        jobFactory.setApplicationContext(applicationContext);
        return jobFactory;
    }

    @Bean
    public SchedulerFactoryBean botSchedulerFactoryBean(ApplicationContext applicationContext) {
        return createSchedulerFactory(applicationContext, "botScheduler");
    }

    @Bean
    public SchedulerFactoryBean reportSchedulerFactoryBean(ApplicationContext applicationContext) {
        return createSchedulerFactory(applicationContext, "reportScheduler");
    }

    private SchedulerFactoryBean createSchedulerFactory(ApplicationContext applicationContext, String schedulerName) {
        SchedulerFactoryBean factory = new SchedulerFactoryBean();
        factory.setOverwriteExistingJobs(true);
        factory.setJobFactory(jobFactory(applicationContext));
        factory.setSchedulerName(schedulerName);
        return factory;
    }

    @Bean
    public Scheduler botOrderCreationScheduler(ApplicationContext applicationContext) {
        return botSchedulerFactoryBean(applicationContext).getScheduler();
    }

    @Bean
    public Scheduler reportScheduler(ApplicationContext applicationContext) {
        return reportSchedulerFactoryBean(applicationContext).getScheduler();
    }


    @Bean
    public ObjectMapper objectMapper() {
        return new ObjectMapper();
    }

    @Bean
    public ZMQ.Context zmqContext() {
        return ZMQ.context(1);
    }

    @Bean
    public Map<String, String> angularProperties(){
        Map<String, String> props = new HashMap<>();
        props.put("angularAllowedOrigin", angularAllowedOrigin);
        return props;
    }

    @Bean
    public Twitter twitter() {
        return new TwitterTemplate(
                twitterConsumerKey,
                twitterConsumerSecret,
                twitterAccessToken,
                twitterAccessTokenSecret);
    }

}
