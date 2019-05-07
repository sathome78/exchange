package me.exrates.security.config;

import me.exrates.security.entryPoint.RestAuthenticationEntryPoint;
import me.exrates.security.filter.AuthenticationTokenProcessingFilter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import javax.servlet.Filter;
import java.util.Arrays;

@Configuration
@Order(value = 1)
@EnableWebSecurity
@PropertySource("classpath:angular.properties")
public class ApiSecurityConfig extends WebSecurityConfigurerAdapter {

    @Value("${angular.allowed.origins}")
    private String[] angularAllowedOrigins;

    @Value("${angular.allowed.methods}")
    private String[] angularAllowedMethods;

    @Value("${angular.allowed.headers}")
    private String[] angularAllowedHeaders;

    @Bean
    public AuthenticationEntryPoint authenticationEntryPoint() {
        return new RestAuthenticationEntryPoint();
    }

    @Bean
    public Filter authenticationTokenProcessingFilter() {
        return new AuthenticationTokenProcessingFilter("/api/**");
    }

    @Bean(name = "ApiAuthenticationManager")
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        final CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Arrays.asList(angularAllowedOrigins));
        configuration.setAllowedMethods(Arrays.asList(angularAllowedMethods));
        configuration.setAllowedHeaders(Arrays.asList(angularAllowedHeaders));
        final UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/api/**", configuration);
        return source;
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .cors()
                .and()
                .antMatcher("/api/private/**")
                .authorizeRequests()
                .antMatchers("/api/private/**").authenticated()
                .and()
                .exceptionHandling()
                .authenticationEntryPoint(authenticationEntryPoint())
                .and()
                .addFilterAfter(authenticationTokenProcessingFilter(), UsernamePasswordAuthenticationFilter.class)
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .csrf().disable()
                .httpBasic();
    }

    @Override
    public void configure(WebSecurity web) {
        web.ignoring().antMatchers("/api/public/**");
    }

}
