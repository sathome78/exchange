package me.exrates.dao.impl;

import me.exrates.dao.UserSessionsDao;
import me.exrates.model.dto.UserLoginSessionDto;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public class UserSessionsDaoImpl implements UserSessionsDao {


    @Override
    public void insertSessionDto(UserLoginSessionDto userLoginSessionDto, String email) {
        /*todo*/
    }

    @Override
    public boolean updateModified(String userAgent, LocalDateTime modified, String email) {
        /*todo*/
        return true;
    }

    @Override
    public List<UserLoginSessionDto> getPage(String email, int limit, int offset) {
        /*todo*/
        return null;
    }

    @Override
    public int countAll(String email) {
        /*todo*/
        return 0;
    }
}
