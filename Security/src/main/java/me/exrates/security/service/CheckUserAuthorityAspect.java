package me.exrates.security.service;


import lombok.extern.log4j.Log4j2;
import me.exrates.model.constants.ErrorApiTitles;
import me.exrates.model.enums.UserRole;
import me.exrates.model.exceptions.OpenApiException;
import me.exrates.model.userOperation.enums.UserOperationAuthority;
import me.exrates.service.UserService;
import me.exrates.service.userOperation.UserOperationService;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;

@Log4j2
@Component
@Aspect
public class CheckUserAuthorityAspect {

    @Autowired
    private UserService userService;

    @Autowired
    private UserOperationService userOperationService;

    @Before("@annotation(CheckUserAuthority)")
    public void checkIp(JoinPoint jp) {

        UserRole userRole = userService.getUserRoleFromSecurityContext();

        if (userRole == UserRole.ICO_MARKET_MAKER) {
            String errorMessage = "Failed to process user's request as such activity is forbidden for role: " + userRole.name();
            log.debug(errorMessage);
            throw new OpenApiException(ErrorApiTitles.IEO_MARKET_MAKER_RESTRICTION, errorMessage);
        }

        MethodSignature methodSignature = (MethodSignature) jp.getSignature();
        Method method = methodSignature.getMethod();

        CheckUserAuthority myAnnotation = method.getAnnotation(CheckUserAuthority.class);

        UserOperationAuthority userAuthority = myAnnotation.authority();
        String userEmail = userService.getUserEmailFromSecurityContext();
        int userId = userService.getIdByEmail(userEmail);

        boolean allowed = userOperationService.getStatusAuthorityForUserByOperation(userId, userAuthority);

        if (!allowed) {
            String errorMessage = "Failed to process user's request as no sufficient authority for " + userAuthority.name();
            log.debug(errorMessage);
            throw new OpenApiException(ErrorApiTitles.USER_OPERATION_DENIED, errorMessage);
        }
    }

}
