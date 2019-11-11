package me.exrates.dao;

import me.exrates.model.dto.UserLoginSessionDto;

import java.time.LocalDateTime;
import java.util.List;

public interface UserSessionsDao {

    void insertSessionDto(UserLoginSessionDto userLoginSessionDto, String email);

    boolean updateModified(String userAgent, String token, LocalDateTime modified);

    List<UserLoginSessionDto> getPage(String email, int limit, int offset);

    int countAll(String email);
}
