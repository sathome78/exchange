package me.exrates.api.controller;

import me.exrates.api.RequestsLimitExceedException;
import me.exrates.api.aspect.RateLimitCheck;
import org.springframework.web.bind.annotation.*;

import static org.springframework.http.HttpStatus.NOT_ACCEPTABLE;
import static org.springframework.web.bind.annotation.RequestMethod.GET;

/**
 * Created by Yuriy Berezin on 17.09.2018.
 */
@RestController
public class RateLimitController {

    static final String TEST_ENDPOINT = "/rateTestEndpoint";

    @RateLimitCheck
    @RequestMapping(value = TEST_ENDPOINT, method = GET)
    public String testEndpoint() {
        return "OK";
    }

    @ResponseStatus(NOT_ACCEPTABLE)
    @ExceptionHandler(RequestsLimitExceedException.class)
    @ResponseBody
    public String requestsLimitExceedExceptionHandler(RequestsLimitExceedException exception) {
//        return new ApiError(ErrorCode.OUTPUT_REQUEST_LIMIT_EXCEEDED, req.getRequestURL(), exception);
        return exception.getClass().getSimpleName();
    }

}
