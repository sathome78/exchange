package me.exrates.security.postprocessor;


import me.exrates.security.annotation.OnlineMethod;
import me.exrates.security.filter.CustomConcurrentSessionFilter;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.web.bind.annotation.RequestMapping;

import java.lang.reflect.Method;

public class OnlineMethodPostProcessor implements BeanPostProcessor {

    @Autowired
    private CustomConcurrentSessionFilter sessionFilter;

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        Class beanClass = bean.getClass();
        for (Method method : beanClass.getMethods()) {
            if (method.isAnnotationPresent(OnlineMethod.class) && method.isAnnotationPresent(RequestMapping.class)) {
                RequestMapping mapping = (RequestMapping) method.getAnnotation(RequestMapping.class);
                for (String url : mapping.value()) {
                    url = url.split("\\{")[0];
                    sessionFilter.getOnlineMethods().add(url);
                }
            }
        }
        return bean;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        return bean;
    }
}
