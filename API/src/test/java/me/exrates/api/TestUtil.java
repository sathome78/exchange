package me.exrates.api;

import lombok.extern.log4j.Log4j2;
import org.mockito.Mockito;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

/**
 * Created by Yuriy Berezin on 18.09.2018.
 */
@Log4j2
public class TestUtil {


    public static final String TEST_EMAIL = "APITest@email.com";

    public static void setAuth(){

        log.info("Set authentication");
        Authentication authentication = Mockito.mock(Authentication.class);
        Mockito.when(authentication.getName()).thenReturn(TEST_EMAIL);
        SecurityContext securityContext = Mockito.mock(SecurityContext.class);
        Mockito.when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
    }
}
