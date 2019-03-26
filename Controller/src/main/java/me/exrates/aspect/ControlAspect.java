package me.exrates.aspect;


import lombok.extern.log4j.Log4j2;
import me.exrates.model.User;
import me.exrates.model.enums.UserStatus;
import me.exrates.service.UserService;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;


@Log4j2(topic = "control_aspect_log")
@Aspect
@Component
public class ControlAspect {

    @Autowired
    private UserService userService;

    private List<UserStatus> activeStatuses = Arrays.asList(UserStatus.ACTIVE, UserStatus.BANNED_IN_CHAT);

    @Before("execution(* * (..)) && @annotation(me.exrates.controller.annotation.CheckActiveUserStatus)")
    public void checkActiveStatus(JoinPoint joinPoint) {
        log.info("check user status");
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userService.findByEmail(email);
        log.info("user {} status {}", email, user.getUserStatus());
        if(!activeStatuses.contains(user.getUserStatus())) {
            log.error("status checking: user {} logged in and has status {}", email, user.getUserStatus());
            throw new RuntimeException("USER " + email + " BANNED, status " + user.getUserStatus());
        }
    }
}
