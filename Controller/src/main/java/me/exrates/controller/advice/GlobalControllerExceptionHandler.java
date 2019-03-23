package me.exrates.controller.advice;

import lombok.extern.log4j.Log4j2;
import me.exrates.controller.exception.ErrorInfo;
import me.exrates.controller.exception.InvalidNumberParamException;
import me.exrates.model.UserFile;
import me.exrates.model.ngExceptions.NgResponseException;
import me.exrates.security.exception.BannedIpException;
import me.exrates.security.exception.MissingHeaderException;
import me.exrates.service.UserService;
import me.exrates.service.exception.AuthenticationNotAvailableException;
import me.exrates.service.exception.CallBackUrlAlreadyExistException;
import me.exrates.service.exception.IncorrectCurrentUserException;
import me.exrates.service.exception.NoPermissionForOperationException;
import me.exrates.service.exception.OrderDeletingException;
import me.exrates.service.exception.UserOperationAccessException;
import me.exrates.service.exception.api.ErrorCode;
import me.exrates.service.exception.api.InvalidCurrencyPairFormatException;
import me.exrates.service.exception.api.OpenApiError;
import me.exrates.service.exception.api.OrderParamsWrongException;
import me.exrates.dao.exception.notfound.NotFoundException;
import me.exrates.service.exception.process.ProcessingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.multipart.MultipartException;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Locale;


@Log4j2
@ControllerAdvice
public class GlobalControllerExceptionHandler {

    private final MessageSource messageSource;
    private final UserService userService;

    @Autowired
    public GlobalControllerExceptionHandler(final MessageSource messageSource, final UserService userService) {
        this.messageSource = messageSource;
        this.userService = userService;
    }

    @ExceptionHandler(MultipartException.class)
    public ModelAndView handleMultipartConflict(final Locale locale) {
        final User principal = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        final me.exrates.model.User user = userService.findByEmail(principal.getUsername());
        final ModelAndView mav = new ModelAndView("settings");
        final List<UserFile> userFiles = userService.findUserDoc(user.getId());
        mav.addObject("user", user);
        mav.addObject("errorNoty", messageSource.getMessage("admin.errorUploadFiles", null, locale));
        mav.addObject("userFiles", userFiles);
        return mav;
    }

    @ResponseStatus(HttpStatus.NOT_ACCEPTABLE)
    @ExceptionHandler(OrderDeletingException.class)
    @ResponseBody
    public ErrorInfo orderDeletingExceptionHandler(HttpServletRequest req, OrderDeletingException exception) {
        return new ErrorInfo(req.getRequestURL(), exception);
    }

    @ResponseStatus(HttpStatus.FORBIDDEN)
    @ExceptionHandler(NoPermissionForOperationException.class)
    @ResponseBody
    public ErrorInfo userNotEnabledExceptionHandler(HttpServletRequest req, Exception exception) {
        return new ErrorInfo(req.getRequestURL(), exception);
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MissingHeaderException.class)
    @ResponseBody
    public ErrorInfo handleMissingHeaderException(HttpServletRequest req, Exception exception) {
        return new ErrorInfo(req.getRequestURL(), exception);
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(NgResponseException.class)
    @ResponseBody
    public ErrorInfo handleNgUserAuthenticationException(HttpServletRequest req, NgResponseException exception) {
        return new ErrorInfo(req.getRequestURL(), exception);
    }

//    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
//    @ExceptionHandler(Exception.class)
//    @ResponseBody
//    public ModelAndView OtherErrorsHandler(HttpServletRequest req, Exception exception) {
//        log.error("URL: " + req.getRequestURL() + " | Exception " + exception);
//        exception.printStackTrace();
//        return new ModelAndView("errorPages/generalErrorPage");
//    }

    @ResponseStatus(HttpStatus.FORBIDDEN)
    @ExceptionHandler(BannedIpException.class)
    @ResponseBody
    public ErrorInfo ipBannedExceptionHandler(HttpServletRequest request, BannedIpException exception) {
        String clientIp = request.getHeader("X-Forwarded-For");
        String message = "Banned ip exception . Banned ip address " + clientIp + " for " + exception.getBanDurationSeconds() + " seconds";
        return new ErrorInfo(message, exception);
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(Throwable.class)
    @ResponseBody
    public ErrorInfo HZExceptionHandler(Throwable exception) {
        String message = "Cause: " + exception.getLocalizedMessage();
        return new ErrorInfo(message, exception);
    }

    //Handlers for open API

    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    @ExceptionHandler(AuthenticationNotAvailableException.class)
    @ResponseBody
    public OpenApiError authenticationNotAvailableExceptionHandler(HttpServletRequest req, AuthenticationNotAvailableException exception) {
        return new OpenApiError(ErrorCode.FAILED_AUTHENTICATION, req.getRequestURL(), exception.getMessage());
    }

    @ResponseStatus(HttpStatus.FORBIDDEN)
    @ExceptionHandler(value = AccessDeniedException.class)
    public OpenApiError accessDeniedExceptionHandler(HttpServletRequest req, AccessDeniedException exception) {
        return new OpenApiError(ErrorCode.ACCESS_DENIED, req.getRequestURL(), exception.getMessage());
    }

    @ResponseStatus(HttpStatus.FORBIDDEN)
    @ExceptionHandler(value = IncorrectCurrentUserException.class)
    public OpenApiError incorrectCurrentUserExceptionHandler(HttpServletRequest req, IncorrectCurrentUserException exception) {
        return new OpenApiError(ErrorCode.USER_MISMATCH, req.getRequestURL(), exception.getMessage());
    }

    @ResponseStatus(HttpStatus.FORBIDDEN)
    @ExceptionHandler(UserOperationAccessException.class)
    @ResponseBody
    public OpenApiError userOperationAccessExceptionHandler(HttpServletRequest req, UserOperationAccessException exception) {
        return new OpenApiError(ErrorCode.BLOCKED_TRADING, req.getRequestURL(), exception.getMessage());
    }

    @ResponseStatus(HttpStatus.NOT_ACCEPTABLE)
    @ExceptionHandler(InvalidCurrencyPairFormatException.class)
    @ResponseBody
    public OpenApiError invalidCurrencyPairFormatExceptionHandler(HttpServletRequest req, InvalidCurrencyPairFormatException exception) {
        return new OpenApiError(ErrorCode.INVALID_CURRENCY_PAIR_FORMAT, req.getRequestURL(), exception.getMessage());
    }

    @ResponseStatus(HttpStatus.NOT_ACCEPTABLE)
    @ExceptionHandler(InvalidNumberParamException.class)
    @ResponseBody
    public OpenApiError invalidNumberParamExceptionHandler(HttpServletRequest req, InvalidNumberParamException exception) {
        return new OpenApiError(ErrorCode.INVALID_NUMBER_FORMAT, req.getRequestURL(), exception.getMessage());
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(NotFoundException.class)
    @ResponseBody
    public OpenApiError notFoundExceptionHandler(HttpServletRequest req, NotFoundException exception) {
        return new OpenApiError(ErrorCode.NOT_FOUND_ERROR, req.getRequestURL(), exception.getMessage());
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MissingServletRequestParameterException.class)
    @ResponseBody
    public OpenApiError missingServletRequestParameterHandler(HttpServletRequest req, MissingServletRequestParameterException exception) {
        return new OpenApiError(ErrorCode.MISSING_REQUIRED_PARAM, req.getRequestURL(), exception.getMessage());
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler({MethodArgumentNotValidException.class, OrderParamsWrongException.class, MethodArgumentTypeMismatchException.class})
    @ResponseBody
    public OpenApiError mismatchArgumentsErrorHandler(HttpServletRequest req, Exception exception) {
        return new OpenApiError(ErrorCode.INVALID_PARAM_VALUE, req.getRequestURL(), exception.getMessage());
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(ProcessingException.class)
    @ResponseBody
    public OpenApiError processingExceptionHandler(HttpServletRequest req, ProcessingException exception) {
        return new OpenApiError(ErrorCode.PROCESSING_ERROR, req.getRequestURL(), exception.getMessage());
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(CallBackUrlAlreadyExistException.class)
    @ResponseBody
    public OpenApiError callBackExistException(HttpServletRequest req, Exception exception) {
        return new OpenApiError(ErrorCode.CALL_BACK_URL_ALREADY_EXISTS, req.getRequestURL(), exception);
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(HttpMessageNotReadableException.class)
    @ResponseBody
    public OpenApiError jsonMappingExceptionHandler(HttpServletRequest req, HttpMessageNotReadableException exception) {
        return new OpenApiError(ErrorCode.REQUEST_NOT_READABLE, req.getRequestURL(), exception.getMessage());
    }
}
