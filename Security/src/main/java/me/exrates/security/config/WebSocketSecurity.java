package me.exrates.security.config;

import me.exrates.model.UserRoleSettings;
import me.exrates.model.enums.UserRole;
import me.exrates.service.UserRoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.messaging.MessageSecurityMetadataSourceRegistry;
import org.springframework.security.config.annotation.web.socket.AbstractSecurityWebSocketMessageBrokerConfigurer;

import java.util.Arrays;
import java.util.List;


/**
 * Created by Maks on 29.08.2017.
 */
@Configuration
public class WebSocketSecurity  extends AbstractSecurityWebSocketMessageBrokerConfigurer {

    @Autowired
    private UserRoleService userRoleService;

    @Override
    protected boolean sameOriginDisabled() {
        return true;
    }

    @Override
    protected void configureInbound(MessageSecurityMetadataSourceRegistry messages) {
        List<UserRoleSettings> settings = userRoleService.retrieveSettingsForAllRoles();
        String[] roles = settings.stream()
                .filter(UserRoleSettings::isOrderAcceptionSameRoleOnly)
                .map(p->p.getUserRole().name())
                .toArray(String[]::new);
       /* -----------------------------------------------------------------------------------------------------------*/
        messages.nullDestMatcher().permitAll()
                .simpSubscribeDestMatchers("/app/statistics").permitAll()
                .simpSubscribeDestMatchers("/app/statistics/*").permitAll()
                .simpSubscribeDestMatchers("/app/statisticsNew").permitAll()
                .simpSubscribeDestMatchers("/app/statistics/*/*").permitAll()
                .simpSubscribeDestMatchers("/app/users_alerts/*").permitAll()
                .simpSubscribeDestMatchers("/app/trade_orders/*").permitAll()
                .simpSubscribeDestMatchers("/app/charts/*/*").permitAll()
                .simpSubscribeDestMatchers("/app/charts2/*/*").permitAll()
                .simpSubscribeDestMatchers("/app/trades/*").permitAll()
                .simpSubscribeDestMatchers("/app/orders/sfwfrf442fewdf/*").permitAll()
                .simpSubscribeDestMatchers("/app/orders/sfwfrf442fewdf/detailed/*").permitAll()
                .simpSubscribeDestMatchers("/app/order_book/*/*").permitAll()
                .simpSubscribeDestMatchers("/user/queue/personal/*").permitAll()
                .simpSubscribeDestMatchers("/app/all_trades/*").permitAll()
                .simpSubscribeDestMatchers("/user/queue/my_orders/*").authenticated()
                .simpSubscribeDestMatchers("/user/queue/open_orders//*").authenticated()
                .simpSubscribeDestMatchers("/app/message/private/*").authenticated()
                .simpSubscribeDestMatchers("/app/ieo_details/private/*").authenticated()
                .simpSubscribeDestMatchers("/app/ieo/ieo_details/**").permitAll()
                .simpDestMatchers("/app/ev/*").permitAll()
                .simpSubscribeDestMatchers("/user/queue/trade_orders/f/*").hasAnyAuthority(roles)
                .anyMessage().permitAll();
    }



}
