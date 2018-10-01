package me.exrates.service.events;

import lombok.Getter;
import lombok.Setter;
import org.springframework.context.ApplicationEvent;

import javax.servlet.http.HttpServletRequest;

/**
 * Created by Maks on 06.09.2017.
 */
@Getter
@Setter
public class QRLoginEvent extends ApplicationEvent {

    public QRLoginEvent(HttpServletRequest request) {
        super(request);
    }
}
