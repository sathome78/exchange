package me.exrates.api.aspect;

import me.exrates.api.service.RateLimitService;
import me.exrates.api.RequestsLimitExceedException;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Created by Yuriy Berezin on 17.09.2018.
 */
@Aspect
@Component
public class RateLimitAspect {

    @Autowired
    private RateLimitService rateLimitService;

    @Before(value = "@annotation(me.exrates.api.aspect.RateLimitCheck)")
    public void checkRateLimit() throws RequestsLimitExceedException {

        rateLimitService.registerRequest();
        if (rateLimitService.isLimitExceed()) {
            throw new RequestsLimitExceedException("Requests limit exceeded");
        }
    }
}
