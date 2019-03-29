package me.exrates.ngcontroller;

import me.exrates.model.constants.ErrorApiTitles;
import me.exrates.model.ngExceptions.NgResponseException;
import me.exrates.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.Locale;
import java.util.Optional;

@RestController
@RequestMapping("/")
public class LanguageController {

    private final UserService userService;

    @Autowired
    public LanguageController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/user-language")
    public ResponseEntity<String> getUserLanguage(Principal principal) {
        String email = principal.getName();
        Optional<String> language = Optional.ofNullable(userService.getPreferedLangByEmail(email));
        if (language.isPresent()) {
            return new ResponseEntity<>("en", HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.SERVICE_UNAVAILABLE);
    }

    @PatchMapping("/user-language/update")
    @ResponseBody
    public ResponseEntity updateUserLanguage(Principal principal, @RequestParam(name = "language") String language) {
        Integer userId = userService.findByEmail(principal.getName()).getId();
        boolean result = this.userService.setPreferedLang(userId, new Locale(language));
        if (result) {
            return new ResponseEntity(HttpStatus.ACCEPTED);
        }
        String message = "Preferred locale for user cannot save in DB";
        throw new NgResponseException(ErrorApiTitles.PREFERRED_LOCALE_NOT_SAVE, message);
    }
}
