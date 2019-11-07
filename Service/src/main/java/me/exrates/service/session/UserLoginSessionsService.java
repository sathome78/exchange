package me.exrates.service.session;

import me.exrates.model.dto.UserLoginSessionShortDto;
import me.exrates.model.dto.mobileApiDto.AuthTokenDto;
import me.exrates.model.ngUtil.PagedResult;
import org.springframework.security.core.Authentication;

import javax.servlet.http.HttpServletRequest;

public interface UserLoginSessionsService {
    PagedResult<UserLoginSessionShortDto> getSessionsHistory(String userEmail, int limit, int offset, HttpServletRequest request);

    void insert(HttpServletRequest request, String token, String email);

    void update(HttpServletRequest request, Authentication authentication);
}
