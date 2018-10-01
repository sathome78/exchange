package me.exrates.controller.listener;

import lombok.extern.log4j.Log4j2;

/**
 * Created by OLEG on 11.10.2016.
 */
@Log4j2
public class StoreSessionListenerImpl /*implements StoreSessionListener*/ {

    /*private static Map<String, HttpSession> sessionStorage = new ConcurrentHashMap<>();


    @Override
    public void sessionCreated(HttpSessionEvent se) {
        HttpSession session = se.getSession();
        session.setMaxInactiveInterval(0);
        sessionStorage.put(session.getId(), session);
        log.debug(String.format("created session: %s, total registered: %s", se.getSession().getId(), sessionStorage.size()));
    }

    @Override
    public void sessionDestroyed(HttpSessionEvent se) {
        sessionStorage.remove(se.getSession().getId());
        log.debug(String.format("destroyed session: %s, total registered: %s", se.getSession().getId(), sessionStorage.size()));
    }

    @Override
    public Optional<HttpSession> getSessionById(String sessionId) {
        return Optional.ofNullable(sessionStorage.get(sessionId));
    }

    @Override
    public void sessionIdChanged(HttpSessionEvent event, String oldSessionId) {
        sessionStorage.remove(oldSessionId);
        HttpSession session = event.getSession();
        session.setMaxInactiveInterval(0);
        sessionStorage.put(session.getId(), session);
    }*/

}
