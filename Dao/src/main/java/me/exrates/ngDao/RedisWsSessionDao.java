package me.exrates.ngDao;

import java.util.Map;
import java.util.Optional;

public interface RedisWsSessionDao {

    void addSession(String email, String sessionId);

    void removeSession(String email);

    Optional<String> getSessionId(String email);

    Map<String, String> getSessions();
}
