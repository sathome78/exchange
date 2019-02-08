package me.exrates.controller.advice;

import lombok.extern.log4j.Log4j2;
import me.exrates.controller.exception.ErrorInfo;
import me.exrates.model.UserFile;
import me.exrates.ngcontroller.exception.NgResponseException;
import me.exrates.security.exception.BannedIpException;
import me.exrates.security.exception.MissingHeaderException;
import me.exrates.service.UserService;
import me.exrates.service.exception.NoPermissionForOperationException;
import me.exrates.service.exception.OrderDeletingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
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
    public ErrorInfo OrderDeletingExceptionHandler(HttpServletRequest req, Exception exception) {
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
        String clientIp = request.getHeader("client_ip");
        String message = "Banned ip exception . Banned ip address " + clientIp + " for " + exception.getBanDurationSeconds() + " seconds";
        return new ErrorInfo(message, exception);
    }

}
