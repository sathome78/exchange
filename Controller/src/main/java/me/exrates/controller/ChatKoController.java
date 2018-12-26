package me.exrates.controller;

import org.glassfish.jersey.spi.Contract;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.view.RedirectView;

@Controller
public class ChatKoController {

    @GetMapping("/chat-ko/iframe.html")
    public RedirectView redirect(){
        return new RedirectView("/dashboard");
    }
}
