package me.exrates.aspect;

import lombok.extern.log4j.Log4j2;
import me.exrates.service.annotation.LogIp;
import me.exrates.model.User;
import me.exrates.model.enums.UserEventEnum;
import me.exrates.service.UserService;
import me.exrates.service.util.IpUtils;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;

import javax.servlet.http.HttpServletRequest;

import java.lang.reflect.Method;

import static java.util.Objects.nonNull;
import static me.exrates.service.util.RestUtil.getUrlFromRequest;

@Log4j2
@Aspect
@Component
public class UserIpAspect {

    private final UserService userService;

    public UserIpAspect(UserService userService) {
        this.userService = userService;
    }

    @After("execution(* * (..)) && @annotation(me.exrates.service.annotation.LogIp)")
    public void checkActiveStatus(JoinPoint joinPoint) {
        Method method = ((MethodSignature) joinPoint.getSignature()).getMethod();
        String methodName = method.getName();
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            HttpServletRequest request = (HttpServletRequest) RequestContextHolder
                    .currentRequestAttributes()
                    .resolveReference(RequestAttributes.REFERENCE_REQUEST);
            if (nonNull(authentication) && nonNull(request)) {
                LogIp myAnnotation = method.getAnnotation(LogIp.class);
                UserEventEnum eventEnum = myAnnotation.event();
                User user = userService.findByEmail(authentication.getName());
                userService.logIP(user.getId(), IpUtils.getIpForDbLog(request), eventEnum, getUrlFromRequest(request));
            }
        } catch (Exception e) {
            log.error("Exception while logging user ip in {} ", methodName, e);
        }
    }
}
