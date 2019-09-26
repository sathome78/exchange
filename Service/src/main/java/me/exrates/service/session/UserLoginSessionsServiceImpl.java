package me.exrates.service.session;

import me.exrates.dao.UserSessionsDao;
import me.exrates.model.dto.UserLoginSessionDto;
import me.exrates.model.dto.UserLoginSessionShortDto;
import me.exrates.model.ngUtil.PagedResult;
import me.exrates.service.util.IpUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserLoginSessionsServiceImpl implements UserLoginSessionsService {

    private final String HEADER_SECURITY_TOKEN = "Exrates-Rest-Token";

    private final UserSessionsDao userSessionsDao;

    @Autowired
    public UserLoginSessionsServiceImpl(UserSessionsDao userSessionsDao) {
        this.userSessionsDao = userSessionsDao;
    }

    @Override
    public PagedResult<UserLoginSessionShortDto> getSessionsHistory(String userEmail, int limit, int offset, HttpServletRequest request) {

        int count = userSessionsDao.countAll(userEmail);
        List<UserLoginSessionDto> items = userSessionsDao.getPage(userEmail, limit, offset);
        List<UserLoginSessionShortDto> dtos = mapToShortDto(items, getToken(request));

        return new PagedResult<>(count, dtos);
    }

    @Override
    public void insert(HttpServletRequest request, String token, String email) {
        LocalDateTime now = LocalDateTime.now();
        UserLoginSessionDto dto = toDto(request, token);
        dto.setStarted(now);
        dto.setModified(now);
        userSessionsDao.insertSessionDto(dto, email);
    }

    @Override
    public void update(HttpServletRequest request, Authentication authentication) {
        LocalDateTime requestTime = LocalDateTime.now();
        String email = authentication.getName();
        String token = getToken(request);

        if (!userSessionsDao.updateModified(getUserAgent(request), token, requestTime)) {
            UserLoginSessionDto dto = toDto(request, token);
            dto.setStarted(requestTime);
            dto.setModified(requestTime);
            userSessionsDao.insertSessionDto(dto, email);
        }
    }

    private UserLoginSessionDto toDto(HttpServletRequest request, String token) {
        String ip = IpUtils.getIpForUserHistory(request);
        /*todo*/
        String device = "unknown";
        String userAgent = getUserAgent(request);
        String os = "unknown";
        String country = "unknown";
        String city = "unknown";
        String region = "unknown";
        /*--------------------------*/
        return UserLoginSessionDto.builder()
                .ip(ip)
                .device(device)
                .userAgent(userAgent)
                .os(os)
                .country(country)
                .city(city)
                .region(region)
                .token(token)
                .build();
    }

    private String getUserAgent(HttpServletRequest request) {
        /*todo*/
        return "unknown";
    }

    private List<UserLoginSessionShortDto> mapToShortDto(List<UserLoginSessionDto> dtos, String currentToken) {
        return dtos.stream()
                   .map(p-> new UserLoginSessionShortDto(p, currentToken))
                   .collect(Collectors.toList());
    }

    private String getToken(HttpServletRequest request) {
        return request.getHeader(HEADER_SECURITY_TOKEN);
    }




}
