package me.exrates.controller.interceptor;

import lombok.extern.log4j.Log4j2;
import me.exrates.controller.annotation.FinPassCheck;
import me.exrates.controller.exception.CheckFinPassException;
import me.exrates.model.User;
import me.exrates.service.UserService;
import me.exrates.service.exception.AbsentFinPasswordException;
import me.exrates.service.exception.NotConfirmedFinPasswordException;
import me.exrates.service.exception.WrongFinPasswordException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.context.support.SpringBeanAutowiringSupport;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

/**
 * Created by maks on 27.03.2017.
 */
@Log4j2
@Component
public class FinPassCheckInterceptor extends HandlerInterceptorAdapter {

    @Autowired
    private UserService userService;
    @Autowired
    private LocaleResolver localeResolver;
    @Autowired
    private MessageSource messageSource;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        SpringBeanAutowiringSupport.processInjectionBasedOnCurrentContext(this);
        if (handler instanceof HandlerMethod) {
            HandlerMethod handlerMethod = (HandlerMethod) handler;
            if (handlerMethod.hasMethodAnnotation(FinPassCheck.class)) {
                Map<String, Object> annotationAttributes =
                        AnnotationUtils.getAnnotationAttributes(handlerMethod.getMethodAnnotation(FinPassCheck.class));
                try {
                    boolean onlyCheckAttribute = (boolean) annotationAttributes.get("notCheckPassIfCheckOnlyParamTrue");
                    if (onlyCheckAttribute) {
                        boolean onlyCheckParam = Boolean.valueOf(request.getParameter("checkOnly"));
                        if (onlyCheckParam) {
                            return true;
                        }
                    }
                    String financePassFieldName = String.valueOf(annotationAttributes.get("paramName"));
                    String finPass = String.valueOf(request.getParameter(financePassFieldName));
                    User storedUser = userService.getUserById(userService.getIdByEmail(request.getUserPrincipal().getName()));
                    userService.checkFinPassword(finPass, storedUser, localeResolver.resolveLocale(request));
                } catch (AbsentFinPasswordException | NotConfirmedFinPasswordException | WrongFinPasswordException e) {
                    boolean throwCheckPassExceptionAttribute = (boolean) annotationAttributes.get("throwCheckPassException");
                    if (throwCheckPassExceptionAttribute) {
                        throw new CheckFinPassException(messageSource.getMessage("admin.wrongfinpassword", null, localeResolver.resolveLocale(request)));
                    }
                    throw e;
                } catch (Exception e) {
                    log.error(e);
                    throw new RuntimeException(e);
                }
            }
        }
        return true;
    }
}
