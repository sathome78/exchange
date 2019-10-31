package me.exrates.controller;

import lombok.extern.log4j.Log4j2;
import me.exrates.dao.SettingsEmailRepository;
import me.exrates.model.EmailRule;
import me.exrates.model.dto.EmailRuleDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

import static org.springframework.web.bind.annotation.RequestMethod.GET;

@Log4j2
@Controller
public class AdminEmailSettingsController {

    private final SettingsEmailRepository settingsEmailRepository;

    @Autowired
    public AdminEmailSettingsController(SettingsEmailRepository settingsEmailRepository) {
        this.settingsEmailRepository = settingsEmailRepository;
    }

    @RequestMapping(value = "/2a8fy7b07dxe44/email/page", method = GET)
    public String getIeoPage() {
        return "admin/email_settings";
    }


    @RequestMapping(value = "/2a8fy7b07dxe44/email/all", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ResponseBody
    public List<EmailRule> getAllEmailRule() {
        return settingsEmailRepository.getAllEmailSenders();
    }

    @RequestMapping(value = "/2a8fy7b07dxe44/email/", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_UTF8_VALUE,
            consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ResponseBody
    public Boolean createNewEmailRule(@RequestBody EmailRuleDto emailRuleDto) {
        return settingsEmailRepository.addNewHost(emailRuleDto.getHost(), emailRuleDto.getSender());
    }

    @RequestMapping(value = "/2a8fy7b07dxe44/email/", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_UTF8_VALUE,
            consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ResponseBody
    public Boolean updateEmailRule(@RequestBody EmailRuleDto emailRuleDto) {
        return settingsEmailRepository.updateEmailRule(emailRuleDto.getHost(), emailRuleDto.getSender());
    }

    @RequestMapping(value = "/2a8fy7b07dxe44/email/remove", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_UTF8_VALUE,
            consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ResponseBody
    public Boolean deleteEmailRule(@RequestBody EmailRuleDto emailRuleDto) {
        return settingsEmailRepository.deleteEmailRule(emailRuleDto.getHost());
    }
}
