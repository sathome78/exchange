package me.exrates.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.extern.log4j.Log4j;
import me.exrates.controller.exception.ErrorInfo;
import me.exrates.model.dto.newsDto.NewsTopicDto;
import me.exrates.model.newsEntity.News;
import me.exrates.model.newsEntity.NewsVariant;
import me.exrates.service.exception.NewsReadingFromDiskException;
import me.exrates.service.exception.NewsVariantNotFoundException;
import me.exrates.service.newsExt.NewsExtService;
import me.exrates.service.newsExt.NewsVariantExtService;
import me.exrates.service.util.LogMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.PropertySource;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.LocaleResolver;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Locale;

import static me.exrates.model.enums.NewsTypeEnum.PAGE;

@RestController
@PropertySource(value = {"classpath:/materials.properties"})
@Log4j
public class MaterialsController {
    @Autowired
    LocaleResolver localeResolver;

    @Autowired
    MessageSource messageSource;

    @Autowired
    NewsVariantExtService newsVariantExtService;

    @Autowired
    NewsExtService newsExtService;

    /*skip resources: img, css, js*/
    @RequestMapping(value = "/pageMaterials/{resource}/content")
    @ResponseBody
    public NewsTopicDto getAboutUsContent(@PathVariable String resource, HttpServletRequest request, HttpServletResponse response) throws JsonProcessingException {
        Locale locale = localeResolver.resolveLocale(request);
        try {
            NewsTopicDto newsTopicDto = newsVariantExtService.getMaterialPageContent(PAGE, resource.toUpperCase(), locale);
            log.debug(LogMessage.requestLogMessageWithResult(request, newsTopicDto));
            return newsTopicDto;
        } catch (NewsReadingFromDiskException | NewsVariantNotFoundException e) {
            String newsContent = messageSource.getMessage("news.absent", null, localeResolver.resolveLocale(request));
            NewsVariant newsVariant = new NewsVariant();
            News news = newsExtService.getByNewsTypeAndResource(PAGE, resource.toUpperCase());
            newsVariant.setNews(news);
            NewsTopicDto newsTopicDto = new NewsTopicDto(newsVariant);
            newsTopicDto.setContent(newsContent);
            log.error(LogMessage.requestLogMessageWithResult(request, newsTopicDto));
            return newsTopicDto;
        } catch (Exception e) {
            throw e;
        }
    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(Exception.class)
    @ResponseBody
    public ErrorInfo OtherErrorsHandler(HttpServletRequest req, Exception exception) {
        log.error(exception);
        exception.printStackTrace();
        return new ErrorInfo(req.getRequestURL(), exception);
    }

}