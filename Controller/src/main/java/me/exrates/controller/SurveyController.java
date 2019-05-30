package me.exrates.controller;

import lombok.extern.log4j.Log4j2;
import me.exrates.model.dto.SurveyDto;
import me.exrates.service.SurveyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.security.Principal;
import java.util.Locale;

import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

@Controller
@Log4j2
public class SurveyController {

    @Autowired
    private MessageSource messageSource;
    @Autowired
    private SurveyService surveyService;

    @RequestMapping(value = "/survey/saveAsDone", method = POST)
    @ResponseBody
    public void saveAsDone(@RequestParam String surveyToken, @RequestBody String surveyResultJsonString, Principal principal) {
        log.info(String.format("survey: %s user: %s  answer: %s", surveyToken, principal.getName(), surveyResultJsonString));
        surveyService.savePollAsDoneByUser(principal.getName());
    }

    @RequestMapping(value = "/survey/getSurvey", method = GET)
    @ResponseBody
    public SurveyDto getSurvey(Locale locale) {
        return surveyService.getFirstActiveSurveyByLang(locale.getLanguage());
    }

}
