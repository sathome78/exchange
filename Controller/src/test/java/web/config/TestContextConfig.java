package web.config;

import me.exrates.service.NamedParameterJdbcTemplateWrapper;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import javax.sql.DataSource;
import javax.ws.rs.client.Client;

@Configuration
@EnableWebMvc
@ComponentScan(basePackages = {"me.exrates"})
public class TestContextConfig extends WebMvcConfigurerAdapter {
    @Autowired
    @Qualifier("testDataSource")
    protected DataSource dataSource;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean(name = "masterTemplate")
    public NamedParameterJdbcTemplate masterNamedParameterJdbcTemplate() {
        return new NamedParameterJdbcTemplateWrapper(dataSource);
    }

    @Bean(name = "slaveTemplate")
    public NamedParameterJdbcTemplate slaveNamedParameterJdbcTemplate() {
        return new NamedParameterJdbcTemplateWrapper(dataSource);
    }

    @Bean(name = "slaveForReportsTemplate")
    public NamedParameterJdbcTemplate slaveForReportsTemplate() {
        return new NamedParameterJdbcTemplateWrapper(dataSource);
    }

    @Bean
    public JdbcTemplate jdbcTemplate() {
        return new JdbcTemplate(dataSource);
    }

    @Bean(name = "SupportMailSender")
    public JavaMailSenderImpl javaMailSenderImpl() {
        return Mockito.mock(JavaMailSenderImpl.class);
    }

    @Bean(name = "MandrillMailSender")
    public JavaMailSenderImpl mandrillMailSenderImpl() {
        return Mockito.mock(JavaMailSenderImpl.class);
    }

    @Bean(name = "InfoMailSender")
    public JavaMailSenderImpl infoMailSenderImpl() {
        return Mockito.mock(JavaMailSenderImpl.class);
    }

    @Bean
    public MessageSource messageSource() {
        return Mockito.mock(MessageSource.class);
    }

    @Bean
    public Client client() {
        return Mockito.mock(Client.class);
    }
}
