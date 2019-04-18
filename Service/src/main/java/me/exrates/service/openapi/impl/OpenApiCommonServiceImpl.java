package me.exrates.service.openapi.impl;

import me.exrates.model.constants.ErrorApiTitles;
import me.exrates.model.exceptions.OpenApiException;
import me.exrates.model.userOperation.enums.UserOperationAuthority;
import me.exrates.service.UserService;
import me.exrates.service.openapi.OpenApiCommonService;
import me.exrates.service.userOperation.UserOperationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;

import java.util.Locale;

import static me.exrates.service.util.OpenApiUtils.transformCurrencyPair;

@Service
public class OpenApiCommonServiceImpl implements OpenApiCommonService {

    private final UserService userService;
    private final MessageSource messageSource;
    private final UserOperationService userOperationService;

    @Autowired
    public OpenApiCommonServiceImpl(UserService userService,
                                    MessageSource messageSource,
                                    UserOperationService userOperationService) {
        this.userService = userService;
        this.messageSource = messageSource;
        this.userOperationService = userOperationService;
    }

    @Override
    public String validateUserAndCurrencyPair(String currencyPair) {
        String userEmail = userService.getUserEmailFromSecurityContext();
        String currencyPairName = transformCurrencyPair(currencyPair);
        int userId = userService.getIdByEmail(userEmail);
        Locale locale = new Locale(userService.getPreferedLang(userId));
        boolean accessToOperationForUser = userOperationService.getStatusAuthorityForUserByOperation(userId, UserOperationAuthority.TRADING);
        if (!accessToOperationForUser) {
            throw new OpenApiException(ErrorApiTitles.API_USER_RESOURCE_ACCESS_DENIED, messageSource.getMessage("merchant.operationNotAvailable", null, locale));
        }
        return currencyPairName;
    }
}
