package me.exrates.config;

import me.exrates.controller.filter.HeaderFilter;
import me.exrates.controller.filter.XssRequestFilter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.security.web.session.HttpSessionEventPublisher;
import org.springframework.web.filter.CharacterEncodingFilter;
import org.springframework.web.servlet.support.AbstractAnnotationConfigDispatcherServletInitializer;

import javax.servlet.Filter;
import javax.servlet.MultipartConfigElement;
import javax.servlet.ServletContext;
import javax.servlet.ServletRegistration;
import java.io.IOException;
import java.util.Properties;


public class SpringMvcInitializer extends AbstractAnnotationConfigDispatcherServletInitializer {

    private static final Logger LOG = LogManager.getLogger(SpringMvcInitializer.class);

    @Override
    protected Class<?>[] getRootConfigClasses() {
        return new Class[]{WebAppConfig.class};
    }

    @Override
    protected Class<?>[] getServletConfigClasses() {
        return null;
    }

    @Override
    protected String[] getServletMappings() {
        return new String[]{"/"};
    }

    @Override
    protected void customizeRegistration(ServletRegistration.Dynamic registration) {
        registration.setMultipartConfig(getMultipartConfigElement());
        registration.setInitParameter("dispatchOptionsRequest", "true");
        registration.setAsyncSupported(true);
    }

    private MultipartConfigElement getMultipartConfigElement() {
        try {
            final ClassLoader classLoader = getClass().getClassLoader();
            final Properties properties = new Properties();
            properties.load(classLoader.getResourceAsStream("uploadfiles.properties"));
            final String location = properties.getProperty("upload.uploadDir");
            final long maxFile = Long.parseLong(properties.getProperty("upload.maxFile"));
            final long maxRequest = Long.parseLong(properties.getProperty("upload.maxRequest"));
            final int threshold = Integer.parseInt(properties.getProperty("upload.threshold"));
            return new MultipartConfigElement(location, maxFile, maxRequest, threshold);
        } catch (final IOException e) {
            LOG.error(e);
            throw new RuntimeException("Can not load uploadfiles.properties." + e);
        }
    }


    @Override
    public Filter[] getServletFilters() {
        return new Filter[]{new XssRequestFilter(), new HeaderFilter(), new CharacterEncodingFilter("UTF-8", true)};
    }

    @Override
    protected void registerDispatcherServlet(ServletContext servletContext) {
        super.registerDispatcherServlet(servletContext);
        servletContext.addListener(HttpSessionEventPublisher.class);
        //TODO temporary disable
        //  servletContext.addListener(StoreSessionListenerImpl.class);
    }
}
