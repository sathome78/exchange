package me.exrates.api.aspect;

import me.exrates.api.service.ApiRateLimitService;
import me.exrates.api.ApiRequestsLimitExceedException;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Created by Yuriy Berezin on 17.09.2018.
 */
@Aspect
@Component
public class ApiRateLimitAspect {

    @Autowired
    private ApiRateLimitService rateLimitService;

    @Before(value = "@annotation(me.exrates.api.aspect.ApiRateLimitCheck)")
    public void checkRateLimit() throws ApiRequestsLimitExceedException {

        rateLimitService.registerRequest();
        if (rateLimitService.isLimitExceed()) {
            throw new ApiRequestsLimitExceedException("Requests limit exceeded");
        }
    }
}
