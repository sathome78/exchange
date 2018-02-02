package me.exrates.security.config;

import me.exrates.security.entryPoint.RestAuthenticationEntryPoint;
import me.exrates.security.filter.AuthenticationTokenProcessingFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.servlet.Filter;

/**
 * Created by OLEG on 22.08.2016.
 */
@Configuration
@Order(value = 1)
@EnableWebSecurity
public class ApiSecurityConfig extends WebSecurityConfigurerAdapter {


    @Bean
    public AuthenticationEntryPoint authenticationEntryPoint() {
        return new RestAuthenticationEntryPoint();
    }

   @Bean
    public Filter authenticationTokenProcessingFilter() throws Exception {
        return new AuthenticationTokenProcessingFilter("/**", authenticationManagerBean());
    }

    @Bean(name="ApiAuthenticationManager")
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        AuthenticationManager manager = super.authenticationManagerBean();
        return manager;
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .antMatcher("/api/**")
                .authorizeRequests()
                .antMatchers("/api/**").authenticated()
                .and()
                .antMatcher("/info/private/**")
                .authorizeRequests()
                .antMatchers("/info/private/**").authenticated()
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
}
