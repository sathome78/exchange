package me.exrates.dao;

import me.exrates.model.ApiAuthToken;

import java.util.Date;
import java.util.Optional;

public interface ApiAuthTokenDao {

    String INSERT_API_AUTH_TOKEN = "INSERT INTO API_AUTH_TOKEN(username, value, expired_at) VALUES(:username, :value, :expired_at)";

    String SELECT_TOKEN_BY_ID = "SELECT id, username, value, expired_at FROM API_AUTH_TOKEN WHERE id = :id";

    long createToken(ApiAuthToken token);

    Optional<ApiAuthToken> retrieveTokenById(Long id);

    boolean deleteExpiredToken(Long id);

    int deleteAllExpired();

    boolean deleteAllByUsername(String username);

    boolean deleteAllExceptCurrent(Long tokenId, String username);

    boolean updateExpiration(Long tokenId, Date expiredAt);
}