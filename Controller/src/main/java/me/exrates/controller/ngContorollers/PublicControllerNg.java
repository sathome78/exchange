package me.exrates.controller.ngContorollers;

import com.google.gson.JsonObject;
import lombok.extern.log4j.Log4j2;
import me.exrates.model.NotificationOption;
import me.exrates.model.SessionParams;
import me.exrates.model.User;
import me.exrates.model.UserFile;
import me.exrates.model.dto.NotificationsUserSetting;
import me.exrates.model.dto.ngDto.UserSettingsDto;
import me.exrates.model.enums.NotificationMessageEventEnum;
import me.exrates.model.enums.SessionLifeTypeEnum;
import me.exrates.model.form.NotificationOptionsForm;
import me.exrates.service.NotificationService;
import me.exrates.service.SessionParamsService;
import me.exrates.service.UserService;
import me.exrates.service.notifications.NotificationsSettingsService;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.PropertySource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.view.RedirectView;

import javax.servlet.http.HttpServletRequest;
import java.security.Principal;
import java.util.*;

/**
 * Created by Maks on 02.02.2018.
 */
@Log4j2
@RestController
@PropertySource(value = {"classpath:/telegram_bot.properties"})
public class PublicControllerNg {



    @RequestMapping(value = "/info/public/test", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<String> testNg() {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("test", "ok");
        return new ResponseEntity<>(jsonObject.toString(), HttpStatus.OK);
    }

    @RequestMapping(value = "/info/private/test", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<String> test2Ng() {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("test", "ok");
        return new ResponseEntity<>(jsonObject.toString(), HttpStatus.OK);
    }



}
