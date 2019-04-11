package me.exrates.service.impl;

import me.exrates.dao.OpenApiTokenDao;
import me.exrates.model.OpenApiToken;
import me.exrates.model.dto.openAPI.OpenApiTokenPublicDto;
import me.exrates.service.OpenApiTokenService;
import me.exrates.service.UserService;
import me.exrates.service.exception.TokenNotFoundException;
import me.exrates.service.exception.api.TokenAccessDeniedException;
import org.apache.commons.lang.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.util.List;

@Service
public class OpenApiTokenServiceImpl implements OpenApiTokenService {

    private static final int KEY_LENGTH = 40;

    @Autowired
    private OpenApiTokenDao openApiTokenDao;

    @Autowired
    private UserService userService;

    private final String ALIAS_REGEX = "^[a-zA-Z\\d]{4,15}$";


    @Override
    public OpenApiToken generateToken(String userEmail, String alias) {
        if (StringUtils.isEmpty(alias) || !alias.matches(ALIAS_REGEX)) {
            throw new IllegalArgumentException("Incorrect alias");
        }
        OpenApiToken token = new OpenApiToken();
        token.setUserEmail(userEmail);
        token.setUserId(userService.getIdByEmail(userEmail));
        token.setPublicKey(generateKey());
        token.setPrivateKey(generateKey());
        token.setAlias(alias);
        openApiTokenDao.saveToken(token);
        return token;
    }



    @Override
    public OpenApiToken getById(Long id) {
        return openApiTokenDao.getById(id).orElseThrow(() -> new TokenNotFoundException("Token not found by id: " + id));
    }

    @Transactional(readOnly = true)
    @Override
    public OpenApiToken getByPublicKey(String publicKey, String currentUserEmail) {
        OpenApiToken token = openApiTokenDao.getByPublicKey(publicKey).
                orElseThrow(() -> new TokenNotFoundException("Token not found by pubkey: " + publicKey));
        checkUser(currentUserEmail, token);
        return token;
    }


    @Override
    public List<OpenApiTokenPublicDto> getUserTokens(String userEmail) {
        return openApiTokenDao.getActiveTokensForUser(userEmail);
    }

    @Override
    public void updateToken(Long tokenId, Boolean allowTrade, String currentUserEmail) {
        OpenApiToken token = openApiTokenDao.getById(tokenId).orElseThrow(() -> new TokenNotFoundException("Token not found by id: " + tokenId));
        checkUser(currentUserEmail, token);
        openApiTokenDao.updateToken(tokenId, token.getAlias(), allowTrade, token.getAllowWithdraw(), token.getAllowAcceptById());
    }

    private void checkUser(String currentUserEmail, OpenApiToken token) {
        if (!currentUserEmail.equals(token.getUserEmail())) {
            throw new TokenAccessDeniedException("Access to token is forbidden");
        }
    }

    @Override
    public void deleteToken(Long tokenId, String currentUserEmail) {
        OpenApiToken token = openApiTokenDao.getById(tokenId).orElseThrow(() -> new TokenNotFoundException("Token not found by id: " + tokenId));
        checkUser(currentUserEmail, token);
        openApiTokenDao.deactivateToken(tokenId);
    }



    private String generateKey() {
        return RandomStringUtils.random(KEY_LENGTH, 0, 0, true, true, null, new SecureRandom());
    }


}
