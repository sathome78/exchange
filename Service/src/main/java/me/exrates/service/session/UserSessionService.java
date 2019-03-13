package me.exrates.service.session;

import lombok.extern.log4j.Log4j2;
import me.exrates.model.dto.UserSessionDto;
import me.exrates.model.dto.UserSessionInfoDto;
import me.exrates.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.session.SessionInformation;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Log4j2
@Service
/**
 * Class for work with the sessions by users
 */
public class UserSessionService {

    @Autowired
    @Qualifier("ExratesSessionRegistry")
    private SessionRegistry sessionRegistry;

    @Autowired
    private UserService userService;

    /**
     * Method expire user session(-s) except specific session with specific session id (2nd parameter)
     * 1st parameter (String userEmail):
     * - user email (get user by email);
     * 2nd parameter (String specificSessionId):
     * - RequestContextHolder.currentRequestAttributes().getSessionId() - get current session id;
     * - null (when null, expire all session of user);
     * - other String (session id);
     *
     * @param userEmail
     * @param specificSessionId
     */
    public void invalidateUserSessionExceptSpecific(String userEmail, String specificSessionId) {
        Optional<Object> updatedUser = sessionRegistry.getAllPrincipals().stream()
                .filter(principalObj -> {
                    UserDetails principal = (UserDetails) principalObj;
                    return userEmail.equals(principal.getUsername());
                })
                .findFirst();
        updatedUser.ifPresent(o -> sessionRegistry.getAllSessions(o, false)
                .stream()
                .filter(session -> !session.getSessionId().equals(specificSessionId))
                .collect(Collectors.toList()).forEach(SessionInformation::expireNow));
    }

    public List<UserSessionDto> retrieveUserSessionInfo() {
        List<UserSessionDto> result = null;
        try {
            Map<String, String> usersSessions = sessionRegistry.getAllPrincipals()
                    .stream()
                    .flatMap(principal -> sessionRegistry.getAllSessions(principal, false).stream())
                    .collect(Collectors.toMap(
                            SessionInformation::getSessionId,
                            sessionInformation -> {
                                UserDetails user = (UserDetails) sessionInformation.getPrincipal();
                                return user.getUsername();
                            }));
            log.debug("USsize ", +usersSessions.size());
            Map<String, UserSessionInfoDto> userSessionInfo = userService.getUserSessionInfo(new HashSet<>(usersSessions.values()))
                    .stream()
                    .collect(Collectors.toMap(
                            UserSessionInfoDto::getUserEmail,
                            userSessionInfoDto -> userSessionInfoDto));
            log.debug("USinfosize ", +userSessionInfo.size());
            result = usersSessions.entrySet().stream()
                    .map(entry -> {
                        return new UserSessionDto(userSessionInfo.get(entry.getValue()), entry.getKey());
                    }).collect(Collectors.toList());
        } catch (Exception e) {
            log.error("session_error {}", e);
        }
        return result;
    }

    public ResponseEntity<String> expireSession(@RequestParam String sessionId) {
        SessionInformation sessionInfo = sessionRegistry.getSessionInformation(sessionId);
        if (sessionInfo == null) {
            return new ResponseEntity<>("Sesion not found", HttpStatus.NOT_FOUND);
        }
        sessionInfo.expireNow();
        return new ResponseEntity<>("Session " + sessionId + " expired", HttpStatus.OK);
    }

    public SessionInformation getSessionInfo(String sessionId) {
        return sessionRegistry.getSessionInformation(sessionId);
    }

}
