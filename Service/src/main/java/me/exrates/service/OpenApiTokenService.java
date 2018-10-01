package me.exrates.service;

import me.exrates.model.OpenApiToken;
import me.exrates.model.dto.openAPI.OpenApiTokenPublicDto;

import java.util.List;

public interface OpenApiTokenService {
    OpenApiToken generateToken(String userEmail, String alias);

    OpenApiToken getById(Long id);

    OpenApiToken getByPublicKey(String publicKey, String currentUserEmail);

    List<OpenApiTokenPublicDto> getUserTokens(String userEmail);

    void updateToken(Long tokenId, Boolean allowTrade, String currentUserEmail);

    void deleteToken(Long tokenId, String currentUserEmail);
}
