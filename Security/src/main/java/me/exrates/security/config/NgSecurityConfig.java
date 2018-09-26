package me.exrates.security.config;

import me.exrates.security.entryPoint.RestAuthenticationEntryPoint;
import me.exrates.security.filter.AuthenticationTokenProcessingFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.access.channel.ChannelProcessingFilter;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.servlet.Filter;

@Configuration
@Order(value = 2)
@EnableWebSecurity
public class NgSecurityConfig extends WebSecurityConfigurerAdapter {

    private static final String PRIVATE_URL = "/info/private/**";

    @Bean
    public AuthenticationEntryPoint authenticationEntryPoint() {
        return new RestAuthenticationEntryPoint();
    }

    @Bean
    public Filter authenticationTokenProcessingFilter() throws Exception {
        return new AuthenticationTokenProcessingFilter("/**", authenticationManagerBean());
    }

    @Bean(name = "ApiAuthenticationManager")
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        AuthenticationManager manager = super.authenticationManagerBean();
        return manager;
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.addFilterBefore(new ExratesCorsFilter(), ChannelProcessingFilter.class);

        http
                .antMatcher(PRIVATE_URL)
                .authorizeRequests()
                .antMatchers(PRIVATE_URL).authenticated()
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
