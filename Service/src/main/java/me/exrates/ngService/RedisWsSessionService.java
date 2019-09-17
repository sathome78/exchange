package me.exrates.ngService;

import java.util.Map;
import java.util.Optional;

public interface RedisWsSessionService {

    void addSession(String email, String sessionId);

    void removeSession(String email);

    Optional<String> getSessionId(String email);

    Map<String, String> getSessions();
}
