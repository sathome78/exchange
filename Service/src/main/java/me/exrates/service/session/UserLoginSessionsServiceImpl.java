package me.exrates.service.session;

import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;
import me.exrates.dao.UserSessionsDao;
import me.exrates.model.dto.GeoLocation;
import me.exrates.model.dto.UserLoginSessionDto;
import me.exrates.model.dto.UserLoginSessionShortDto;
import me.exrates.model.ngUtil.PagedResult;
import me.exrates.ngService.GeoLocationService;
import me.exrates.service.util.IpUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import ua_parser.Client;
import ua_parser.Parser;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Log4j2
@Service
public class UserLoginSessionsServiceImpl implements UserLoginSessionsService {

    private final String HEADER_SECURITY_TOKEN = "Exrates-Rest-Token";
    private final String USER_AGENT_HEADER = "User-Agent";

    private final UserSessionsDao userSessionsDao;
    private final Parser uaParser;

    @Autowired
    private GeoLocationService geoLocationService;

    @SneakyThrows
    @Autowired
    public UserLoginSessionsServiceImpl(UserSessionsDao userSessionsDao) {
        this.userSessionsDao = userSessionsDao;
        this.uaParser = new Parser();
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
        Client values = parseUserAgentHeader(request.getHeader(USER_AGENT_HEADER));

        if (!userSessionsDao.updateModified(getFullUserAgent(values), token, requestTime)) {
            UserLoginSessionDto dto = toDto(request, token);
            dto.setStarted(requestTime);
            dto.setModified(requestTime);
            userSessionsDao.insertSessionDto(dto, email);
        }
    }

    private UserLoginSessionDto toDto(HttpServletRequest request, String token) {
        String ip = IpUtils.getIpForUserHistory(request);
        ip = ip.startsWith("/")
                ? ip.substring(1).trim()
                : ip.trim();
        Client values = parseUserAgentHeader(request.getHeader(USER_AGENT_HEADER));
        GeoLocation geoLocation = geoLocationService.findById(ip);

        /*--------------------------*/
        return UserLoginSessionDto.builder()
                .ip(ip)
                .device(values.device.family)
                .userAgent(getFullUserAgent(values))
                .os(getFullOs(values))
                .country(geoLocation.getCountry())
                .city(geoLocation.getCity())
                .region(geoLocation.getRegion())
                .token(token)
                .build();
    }


    private List<UserLoginSessionShortDto> mapToShortDto(List<UserLoginSessionDto> dtos, String currentToken) {
        return dtos.stream()
                   .map(p-> new UserLoginSessionShortDto(p, currentToken))
                   .collect(Collectors.toList());
    }

    private String getToken(HttpServletRequest request) {
        return request.getHeader(HEADER_SECURITY_TOKEN);
    }

    private Client parseUserAgentHeader(String headerValue) {
        return uaParser.parse(headerValue);
    }

    private String getFullUserAgent(Client client) {
        return String.format("%s  %s%s %s",
                client.userAgent.family,
                client.userAgent.major,
                getEmptyOrValueWithDot(client.userAgent.minor),
                getEmptyOrValue(client.userAgent.patch));
    }

    private String getFullOs(Client client) {
        return String.format("%s  %s%s  %s %s",
                client.os.family,
                client.os.major,
                getEmptyOrValueWithDot(client.os.minor),
                getEmptyOrValue(client.os.patch),
                getEmptyOrValueWithDot(client.os.patchMinor));
    }

    private String getEmptyOrValue(String value) {
        return Objects.isNull(value) ? StringUtils.EMPTY : value;
    }

    private String getEmptyOrValueWithDot(String value) {
        return Objects.isNull(value) ? StringUtils.EMPTY : ".".concat(value);
    }
}
