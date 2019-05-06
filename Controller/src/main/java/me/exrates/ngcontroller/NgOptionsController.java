package me.exrates.ngcontroller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;

@RestController
@PropertySource(value = { "classpath:/angular.properties" })
public class NgOptionsController {

    @Value("${angular.allowed.methods}")
    private String[] angularAllowedMethods;

    @Value("${angular.allowed.headers}")
    private String[] angularAllowedHeaders;

    @RequestMapping(value= "/api/**", method=RequestMethod.OPTIONS)
    public void corsHeaders(HttpServletResponse response) {
        response.addHeader("Access-Control-Allow-Methods", String.join(", ", angularAllowedMethods));
        response.addHeader("Access-Control-Allow-Headers", String.join(", ", angularAllowedHeaders));
        response.addHeader("Access-Control-Max-Age", "3600");
    }
}