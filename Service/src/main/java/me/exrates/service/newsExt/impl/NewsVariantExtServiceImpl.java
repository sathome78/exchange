package me.exrates.service.newsExt.impl;

import lombok.extern.log4j.Log4j;
import me.exrates.dao.newsExt.NewsVariantExtDao;
import me.exrates.model.dto.newsDto.NewsTopicDto;
import me.exrates.model.enums.NewsTypeEnum;
import me.exrates.model.newsEntity.NewsVariant;
import me.exrates.service.exception.NewsReadingFromDiskException;
import me.exrates.service.exception.NewsVariantNotFoundException;
import me.exrates.service.newsExt.NewsVariantExtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Locale;

@Service
@Log4j
@PropertySource(value = {"classpath:/materials.properties"})
public class NewsVariantExtServiceImpl implements NewsVariantExtService {

  @Autowired
  NewsVariantExtDao newsVariantExtDao;

  @Autowired
  NewsContentManipulator newsContentManipulator;


  @Override
  @Transactional(readOnly = true)
  public NewsTopicDto getMaterialPageContent(NewsTypeEnum newsTypeEnum, String resource, Locale locale) {
    resource = resource.endsWith("/") ? resource : resource.concat("/");
    NewsVariant newsVariant = newsVariantExtDao.findActiveByNewsTypeAndResourceAndNewsVariantLanguage(newsTypeEnum.getCode(), resource, locale.getLanguage());
    if (newsVariant != null) {
      return convertToNewsTopicDto(newsVariant);
    } else {
      throw new NewsVariantNotFoundException("news: type: ".concat(newsTypeEnum.name()).concat(" resource: ").concat(resource).concat(" lang: ").concat(locale.getLanguage()));
    }
  }

  @Override
  @Transactional
  public void deleteNewsVariant(Integer id) {
    newsVariantExtDao.setNewsVariantInactive(id, LocalDateTime.now());
  }

  private NewsTopicDto convertToNewsTopicDto(NewsVariant newsVariant) {
    try {
      NewsTopicDto newsTopicDto = new NewsTopicDto(newsVariant);
      newsContentManipulator.correctResourcesPath(newsTopicDto);
      newsContentManipulator.setReferenceToNewstopicPage(newsTopicDto);
      newsContentManipulator.setTitleAndBrief(newsTopicDto);
      newsContentManipulator.setTitleImageSource(newsTopicDto);
      newsContentManipulator.setContent(newsTopicDto);
      newsContentManipulator.replaceReferencesInHtmlToAbsoluteResourcesPath(newsTopicDto); //  src='..img/picture.png' -> src='/newstopic/2015/MAY/27/48/img/picture.png'
      newsContentManipulator.addTargetBlankToReference(newsTopicDto);
      return newsTopicDto;
    } catch (IOException e) {
      throw new NewsReadingFromDiskException(newsVariant.getNews().getId().toString());
    }
  }

}
