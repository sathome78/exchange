package me.exrates.service.session;

import me.exrates.dao.UserSessionsDao;
import me.exrates.model.dto.UserLoginSessionDto;
import me.exrates.model.dto.UserLoginSessionShortDto;
import me.exrates.model.dto.mobileApiDto.AuthTokenDto;
import me.exrates.model.ngUtil.PagedResult;
import me.exrates.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class UserLoginSessionsServiceImpl implements UserLoginSessionsService {

    private final String HEADER_SECURITY_TOKEN = "Exrates-Rest-Token";

    @Autowired
    private UserSessionsDao userSessionsDao;
    @Autowired
    private UserService userService;

    @Override
    public PagedResult<UserLoginSessionShortDto> getSessionsHistory(String userEmail, int limit, int offset, HttpServletRequest request) {

        int count = userSessionsDao.countAll(userEmail);
        List<UserLoginSessionDto> items = userSessionsDao.getPage(userEmail, limit, offset);

        List<UserLoginSessionShortDto> dtos = mapToShortDto(items, getToken(request));

        return new PagedResult<>(count, dtos);
    }

    @Override
    public void insert(HttpServletRequest request, AuthTokenDto authTokenDto, String email) {

        UserLoginSessionDto dto = toDto(request, authTokenDto.getToken());
        userSessionsDao.insertSessionDto(dto, email);
    }

    @Override
    public void update(HttpServletRequest request, Authentication authentication) {
        LocalDateTime requestTime = LocalDateTime.now();

        String email = authentication.getName();
        UserLoginSessionDto dto = toDto(request, getToken(request));

        if (!userSessionsDao.updateModified(dto.getUserAgent(), requestTime, email)) {
            userSessionsDao.insertSessionDto(dto, email);
        }
    }

    private UserLoginSessionDto toDto(HttpServletRequest request, String token) {
        /*todo*/
        return null;
    }

    private List<UserLoginSessionShortDto> mapToShortDto(List<UserLoginSessionDto> dtos, String currentToken) {
        /*todo*/
        return null;
    }

    private String getToken(HttpServletRequest request) {
        return request.getHeader(HEADER_SECURITY_TOKEN);
    }




}
