package me.exrates.controller.ngContorollers;

import com.google.gson.JsonObject;
import lombok.extern.log4j.Log4j2;
import me.exrates.controller.exception.WrongUsernameOrPasswordException;
import me.exrates.controller.listener.StoreSessionListener;
import me.exrates.model.User;
import me.exrates.model.dto.mobileApiDto.AuthTokenDto;
import me.exrates.model.dto.mobileApiDto.UserAuthenticationDto;
import me.exrates.model.enums.UserStatus;
import me.exrates.security.exception.IncorrectPasswordException;
import me.exrates.security.exception.UserNotEnabledException;
import me.exrates.security.service.AuthTokenService;
import me.exrates.security.service.IpBlockingService;
import me.exrates.service.ReferralService;
import me.exrates.service.UserService;
import me.exrates.service.exception.api.UnconfirmedUserException;
import me.exrates.service.util.IpUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

/**
 * Created by Maks on 02.02.2018.
 */
@Log4j2
@RestController
public class PublicControllerNg {

    @Autowired
    private UserService userService;

    @RequestMapping(value = "/info/public/test", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<String> testNg() {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("test", "ok");
        return new ResponseEntity<>(jsonObject.toString(), HttpStatus.OK);
    }

}
