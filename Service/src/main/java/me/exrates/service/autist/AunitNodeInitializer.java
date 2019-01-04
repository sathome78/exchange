package me.exrates.service.autist;

import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class AunitNodeInitializer {

    @EventListener
    public void handleContextRefresh(ContextRefreshedEvent event) {
        event.getApplicationContext().getBean(AunitNodeServiceImpl.class);
    }
}
