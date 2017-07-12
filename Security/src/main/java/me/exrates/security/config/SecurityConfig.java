package me.exrates.security.config;

import me.exrates.model.enums.AdminAuthority;
import me.exrates.model.enums.UserRole;
import me.exrates.security.filter.*;
import me.exrates.security.postprocessor.OnlineMethodPostProcessor;
import me.exrates.security.service.UserDetailsServiceImpl;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.configurers.SessionManagementConfigurer;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.core.session.SessionRegistryImpl;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.access.channel.ChannelProcessingFilter;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.session.NullAuthenticatedSessionStrategy;
import org.springframework.security.web.authentication.session.SessionAuthenticationStrategy;
import org.springframework.security.web.session.ConcurrentSessionFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.web.filter.CharacterEncodingFilter;

import static me.exrates.model.enums.AdminAuthority.*;
import static org.springframework.http.HttpMethod.GET;
import static org.springframework.http.HttpMethod.POST;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfig extends WebSecurityConfigurerAdapter {

  private static final Logger LOGGER = LogManager.getLogger(SecurityConfig.class);
  private static final int MAXIMUM_SESSIONS = 2;

  private final PasswordEncoder passwordEncoder;
  private final UserDetailsServiceImpl userDetailsService;

  @Autowired
  public SecurityConfig(PasswordEncoder passwordEncoder, UserDetailsServiceImpl userDetailsService) {
    this.passwordEncoder = passwordEncoder;
    this.userDetailsService = userDetailsService;
  }

  @Bean
  public LoginSuccessHandler loginSuccessHandler() {
    return new LoginSuccessHandler("/dashboard");
  }

  @Bean
  public OnlineMethodPostProcessor onlineMethodPostProcessor() {
    return new OnlineMethodPostProcessor();
  }

  @Bean
  public LoginFailureHandler loginFailureHandler() {
    return new LoginFailureHandler("/login?error");
  }

  @Bean
  public CapchaAuthorizationFilter customUsernamePasswordAuthenticationFilter()
      throws Exception {
    CapchaAuthorizationFilter customUsernamePasswordAuthenticationFilter = new CapchaAuthorizationFilter();
    customUsernamePasswordAuthenticationFilter
        .setRequiresAuthenticationRequestMatcher(new AntPathRequestMatcher("/login", "POST"));
    customUsernamePasswordAuthenticationFilter
        .setAuthenticationManager(authenticationManagerBean());
    customUsernamePasswordAuthenticationFilter
        .setUsernameParameter("username");
    customUsernamePasswordAuthenticationFilter
        .setPasswordParameter("password");
    customUsernamePasswordAuthenticationFilter
        .setAuthenticationSuccessHandler(loginSuccessHandler());
    customUsernamePasswordAuthenticationFilter
        .setAuthenticationFailureHandler(loginFailureHandler());


    return customUsernamePasswordAuthenticationFilter;
  }

  @Bean
  public QRAuthorizationFilter customQRAuthorizationFilter() {
    return new QRAuthorizationFilter();
  }

  @Bean(name = "ExratesSessionRegistry")
  public SessionRegistry sessionRegistry() {
    return new SessionRegistryImpl();
  }

  /*
  * Defines separate access denied error handling logic for XHR (AJAX requests) and usual requests
  * */
  @Bean
  public AjaxAwareAccessDeniedHandler accessDeniedHandler() {
    return new AjaxAwareAccessDeniedHandler("/403");
  }

  @Bean
  public CharacterEncodingFilter characterEncodingFilter() {
    return new CharacterEncodingFilter("UTF-8", true);
  }

  @Bean
  public CustomConcurrentSessionFilter customConcurrentSessionFilter() {
    return new CustomConcurrentSessionFilter(sessionRegistry());
  }

  @Autowired
  public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
    auth
        .userDetailsService(userDetailsService)
        .passwordEncoder(passwordEncoder);
  }

  @Override
  protected void configure(HttpSecurity http) throws Exception {
    http.addFilterBefore(customUsernamePasswordAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class);
    http.addFilterBefore(customQRAuthorizationFilter(), CapchaAuthorizationFilter.class);
    http.addFilterBefore(characterEncodingFilter(), ChannelProcessingFilter.class);
    http.addFilterAt(customConcurrentSessionFilter(), ConcurrentSessionFilter.class);
    http
        .authorizeRequests()
        /*ADMIN ...*/
        .antMatchers(POST, "/2a8fy7b07dxe44/edituser/submit",
            "/2a8fy7b07dxe44/users/deleteUserFile").hasAuthority(AdminAuthority.EDIT_USER.name())
        .antMatchers("/2a8fy7b07dxe44/addComment",
            "/2a8fy7b07dxe44/deleteUserComment").hasAuthority(AdminAuthority.COMMENT_USER.name())
        .antMatchers("/2a8fy7b07dxe44/updateTransactionAmount").hasAuthority(AdminAuthority.PROCESS_INVOICE.name())
            .antMatchers("/2a8fy7b07dxe44/expireSession").hasAuthority(AdminAuthority.MANAGE_SESSIONS.name())
        .antMatchers("/2a8fy7b07dxe44/editCurrencyLimits/submit",
            "/2a8fy7b07dxe44/editCmnRefRoot",
            "/2a8fy7b07dxe44/editLevel",
            "/2a8fy7b07dxe44/commissions/editCommission",
            "/2a8fy7b07dxe44/commissions/editMerchantCommission",
            "/2a8fy7b07dxe44/merchantAccess/toggleBlock",
            "/2a8fy7b07dxe44/merchantAccess/setBlockForAll").hasAuthority(AdminAuthority.SET_CURRENCY_LIMIT.name())
        .antMatchers("/2a8fy7b07dxe44/editCmnRefRoot", "/admin/merchantAccess/setBlockForAll").hasAuthority(UserRole.ADMINISTRATOR.name())
        .antMatchers("/2a8fy7b07dxe44/merchantAccess/autoWithdrawParams").hasAuthority(UserRole.ADMINISTRATOR.name())
        .antMatchers("/2a8fy7b07dxe44/editAuthorities/submit").hasAuthority(MANAGE_ACCESS.name())
        .antMatchers("/2a8fy7b07dxe44/changeActiveBalance/submit").hasAuthority(AdminAuthority.MANUAL_BALANCE_CHANGE.name())
        .antMatchers("/2a8fy7b07dxe44/userswallets",
            "/2a8fy7b07dxe44/editCurrencyLimits",
            "/2a8fy7b07dxe44/commissions",
            "/2a8fy7b07dxe44/merchantAccess").hasAnyAuthority(UserRole.ADMINISTRATOR.name(), UserRole.ACCOUNTANT.name(), UserRole.FIN_OPERATOR.name())
        .antMatchers("/2a8fy7b07dxe44/candleTable",
            "/2a8fy7b07dxe44/getCandleTableData").hasAnyAuthority(UserRole.ADMINISTRATOR.name(), UserRole.ACCOUNTANT.name(), UserRole.ADMIN_USER.name())
            /*admin withdraw action ...*/
        .antMatchers("/2a8fy7b07dxe44/editCurrencyPermissions/submit").hasAuthority(MANAGE_ACCESS.name())
        .antMatchers("/2a8fy7b07dxe44/withdrawal").hasAuthority(PROCESS_WITHDRAW.name())
        .antMatchers(POST, "/2a8fy7b07dxe44/withdraw/**").hasAuthority(PROCESS_WITHDRAW.name())
            /*... admin withdraw action*/
            /*admin report ... */
        .antMatchers(POST, "/2a8fy7b07dxe44/report/**").hasAnyAuthority(PROCESS_INVOICE.name(), PROCESS_WITHDRAW.name())
            /*... admin report */
        .antMatchers(POST, "/2a8fy7b07dxe44/chat/deleteMessage").hasAnyAuthority(UserRole.ADMINISTRATOR.name(), UserRole.ACCOUNTANT.name(), UserRole.ADMIN_USER.name())
        .antMatchers("/2a8fy7b07dxe44/**",
            "/2a8fy7b07dxe44").hasAnyAuthority(UserRole.ADMINISTRATOR.name(), UserRole.ACCOUNTANT.name(), UserRole.ADMIN_USER.name(), UserRole.FIN_OPERATOR.name())
        /*... ADMIN */
        .antMatchers("/companywallet").hasAnyAuthority(UserRole.ADMINISTRATOR.name(), UserRole.ACCOUNTANT.name(), UserRole.FIN_OPERATOR.name())
        .antMatchers("/merchants/bitcoin/payment/accept", "/merchants/invoice/payment/accept").hasAuthority(AdminAuthority.PROCESS_INVOICE.name())
        .antMatchers("/unsafe/**").hasAnyAuthority(UserRole.ADMINISTRATOR.name())
        .antMatchers("/withdrawal/request/accept", "/withdrawal/request/decline").hasAuthority(PROCESS_WITHDRAW.name())
        .antMatchers(POST, "/2a8fy7b07dxe44/bitcoinWallet/**").hasAuthority(AdminAuthority.MANAGE_BTC_CORE_WALLET.name())
                .antMatchers("/", "/index.jsp", "/client/**", "/dashboard/**", "/registrationConfirm/**",
            "/changePasswordConfirm/**", "/changePasswordConfirm/**", "/aboutUs", "/57163a9b3d1eafe27b8b456a.txt", "/newIpConfirm/**").permitAll()
        .antMatchers(POST, "/merchants/withdrawal/request/accept",
            "/merchants/withdrawal/request/decline").hasAuthority(PROCESS_WITHDRAW.name())
        .antMatchers(POST, "/merchants/perfectmoney/payment/status",
            "/merchants/perfectmoney/payment/status",
            "/merchants/perfectmoney/payment/success",
            "/merchants/perfectmoney/payment/failure").permitAll()
        .antMatchers(POST, "/merchants/advcash/payment/status",
            "/merchants/advcash/payment/success",
            "/merchants/advcash/payment/failure").permitAll()
        .antMatchers(POST, "/merchants/liqpay/payment/status",
            "/merchants/liqpay/payment/success",
            "/merchants/liqpay/payment/failure").permitAll()
        .antMatchers(POST, "/merchants/nixmoney/payment/status",
            "/merchants/nixmoney/payment/success",
            "/merchants/nixmoney/payment/failure").permitAll()
        .antMatchers(POST, "/merchants/privat24/payment/status",
            "/merchants/privat24/payment/success",
            "/merchants/privat24/payment/failure").permitAll()
        .antMatchers(POST, "/merchants/interkassa/payment/status",
            "/merchants/interkassa/payment/success",
            "/merchants/interkassa/payment/failure").permitAll()
        .antMatchers(POST, "/merchants/yandex_kassa/payment/status",
            "/merchants/yandex_kassa/payment/success",
            "/merchants/yandex_kassa/payment/failure").permitAll()
        .antMatchers(POST, "/merchants/okpay/payment/status",
            "/merchants/okpay/payment/success",
            "/merchants/okpay/payment/failure").permitAll()
        .antMatchers(POST, "/merchants/payeer/payment/status",
            "/merchants/payeer/payment/success").permitAll()
            .antMatchers(POST,"/2a8fy7b07dxe44/order/accept").hasAuthority(UserRole.TRADER.name())
            .antMatchers("/2a8fy7b07dxe44/orderdelete", "/2a8fy7b07dxe44/searchorders", "/2a8fy7b07dxe44/orderinfo",
                    "/2a8fy7b07dxe44/removeOrder").hasAuthority(UserRole.TRADER.name())
        .antMatchers(POST, "/chat-en/**", "/chat-ru/**", "/chat-cn/**", "/chat-ar/**", "/chat-in/**").permitAll()
        .antMatchers(GET, "/chat-en/**", "/chat-ru/**", "/chat-cn/**", "/chat-ar/**", "/chat-in/**", "/chat/history").permitAll()
        .antMatchers(GET, "/generateReferral").permitAll()
        .antMatchers(POST, "/merchants/edrcoin/payment/received").permitAll()
        .antMatchers(POST, "/merchants/edc/payment/received").permitAll()
        .antMatchers(GET, "/merchants/blockchain/payment/received").permitAll()
        .antMatchers(GET, "/merchants/yandexmoney/token/access").permitAll()
        .antMatchers(GET, "/rest/yandexmoney/payment/process").permitAll()
        .antMatchers(GET, "/public/**").permitAll()
        .antMatchers(GET, "/favicon.ico").permitAll()
        .antMatchers(GET, "/news/**").permitAll()
        .antMatchers(GET, "/pageMaterials/**").permitAll()
        .antMatchers("/stickyImg").permitAll()
        .antMatchers("/simpleCaptcha").permitAll()
        .antMatchers("/botdetectcaptcha").permitAll()
        .antMatchers(GET, "/com/captcha/botdetect/**").permitAll()
        .antMatchers(POST, "/captchaSubmit").permitAll()
        .antMatchers(POST, "/news/addNewsVariant").authenticated()
        .antMatchers("/yandex_4b3a16d69d4869cb.html").permitAll()
        .antMatchers("/yandex_7a3c41ddb19f4716.html").permitAll()
        .antMatchers("/termsAndConditions", "/privacyPolicy", "/contacts").permitAll()
        .antMatchers(POST, "/sendFeedback").permitAll()
        .antMatchers(GET, "/utcOffset").permitAll()
        .antMatchers(POST, "/rest/user/register", "/rest/user/authenticate", "/rest/user/restorePassword").anonymous()
        .antMatchers(GET, "/rest/userFiles/**/avatar/**").permitAll()
        .antMatchers(GET, "/rest/userFiles/**/receipts/**").permitAll()
        .antMatchers(GET, "/rest/stockExchangeStatistics", "/rest/temp/retrieveCurrencyPairRates").permitAll()
        .antMatchers("/login", "/register", "/create", "/forgotPassword/**", "/resetPasswordConfirm/**", "/rest/user/resetPasswordConfirm/**").anonymous()
        .antMatchers(POST, "/login/new_pin_send").anonymous()
            .antMatchers("/updatePassword").hasAnyAuthority(UserRole.ROLE_CHANGE_PASSWORD.name())
        .antMatchers(POST, "/survey/**").authenticated()
        .anyRequest().hasAnyAuthority(UserRole.ADMINISTRATOR.name(), UserRole.ACCOUNTANT.name(), UserRole.ADMIN_USER.name(), UserRole.USER.name(),
        UserRole.EXCHANGE.name(), UserRole.VIP_USER.name(), UserRole.TRADER.name(), UserRole.FIN_OPERATOR.name())
        /*user withdraw action ...*/
        .antMatchers(POST, "/withdraw/request/**").authenticated()
        /*... user withdraw action*/
        /*user refill action ...*/
        .antMatchers(POST, "/refill/request/**").authenticated()
        /*... user refill action*/
        .and()
        .exceptionHandling()
        .accessDeniedHandler(accessDeniedHandler());
    SessionManagementConfigurer<HttpSecurity> sessionConfigurer = http.sessionManagement();
    sessionConfigurer
        .sessionAuthenticationStrategy(new NullAuthenticatedSessionStrategy())
        .maximumSessions(MAXIMUM_SESSIONS)
        .sessionRegistry(sessionRegistry())
        .maxSessionsPreventsLogin(false);

    //init and configure methods are required to instantiate the composite SessionAuthenticationStrategy, which is later passed to custom auth filter
    sessionConfigurer.init(http);
    sessionConfigurer.configure(http);
    SessionAuthenticationStrategy authenticationStrategy = http.getSharedObject(SessionAuthenticationStrategy.class);
    customUsernamePasswordAuthenticationFilter().setSessionAuthenticationStrategy(authenticationStrategy);
    customQRAuthorizationFilter().setAuthenticationStrategy(authenticationStrategy);

    http.formLogin()
        .loginPage("/login")
        .usernameParameter("username")
        .passwordParameter("password")
        .permitAll();

    http.logout()
        .permitAll()
        .logoutUrl("/logout")
        .logoutSuccessUrl("/")
        .invalidateHttpSession(true)
        .and()
        .csrf()
        .ignoringAntMatchers("/chat-en/**", "/chat-ru/**", "/chat-cn/**",  "/chat-ar/**", "/chat-in/**",
            "/merchants/perfectmoney/payment/status",
            "/merchants/perfectmoney/payment/failure",
            "/merchants/perfectmoney/payment/success", "/merchants/advcash/payment/status",
            "/merchants/advcash/payment/failure",
            "/merchants/advcash/payment/success",
            "/merchants/advcash/payment/status",
            "/merchants/edrcoin/payment/received",
            "/merchants/edc/payment/received",
            "/merchants/liqpay/payment/failure",
            "/merchants/liqpay/payment/success",
            "/merchants/liqpay/payment/status",
            "/merchants/nixmoney/payment/failure",
            "/merchants/nixmoney/payment/success",
            "/merchants/nixmoney/payment/status",
            "/merchants/privat24/payment/failure",
            "/merchants/privat24/payment/success",
            "/merchants/privat24/payment/status",
            "/merchants/interkassa/payment/failure",
            "/merchants/interkassa/payment/success",
            "/merchants/interkassa/payment/status",
            "/merchants/yandex_kassa/payment/failure",
            "/merchants/yandex_kassa/payment/success",
            "/merchants/yandex_kassa/payment/status",
            "/merchants/okpay/payment/failure",
            "/merchants/okpay/payment/success",
            "/merchants/okpay/payment/status",
            "/merchants/payeer/payment/success",
            "/merchants/payeer/payment/status",
            "/rest/user/register", "/rest/user/authenticate", "/rest/user/restorePassword");
    http
        .headers()
        .frameOptions()
        .sameOrigin();


  }
}
