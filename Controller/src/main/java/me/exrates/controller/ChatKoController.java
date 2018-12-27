package me.exrates.controller;

import org.glassfish.jersey.spi.Contract;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.view.RedirectView;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

@Controller
public class ChatKoController {

    @GetMapping("/chat-ko/iframe.html")
    public RedirectView redirect(HttpServletResponse response){
        response.addCookie(new Cookie("myAppLocaleCookie", "ko"));
        return new RedirectView("/dashboard");
    }
}
