package me.exrates.ngcontroller;

import me.exrates.ngcontroller.service.UserVerificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@RestController
@RequestMapping("/info/public/confirm")
public class NgConfirmationController {

    private final UserVerificationService userVerificationService;

    @Autowired
    public NgConfirmationController(UserVerificationService userVerificationService) {
        this.userVerificationService = userVerificationService;
    }

    @GetMapping("/registration")
    public void getConfirmation(@RequestParam("p") String token, HttpServletResponse response) {
        if (userVerificationService.confirmRegistrationUser(token)) {
            try {
                response.setHeader("token", token);
                response.sendRedirect("some location" + "/final-registration/token");
            } catch (IOException e) {
                throw new NgDashboardException("Error processing verification");
            }
        }
    }
}
