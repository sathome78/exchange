package me.exrates.service.newsExt;

import me.exrates.model.dto.newsDto.NewsEditorCreationFormDto;
import me.exrates.model.dto.newsDto.NewsSyncDataContainerDto;
import me.exrates.model.enums.NewsTypeEnum;
import me.exrates.model.newsEntity.News;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface NewsExtService {
  String uploadImageForNews(MultipartFile multipartFile) throws IOException;

  String uploadFileForNews(MultipartFile multipartFile) throws IOException;

  NewsEditorCreationFormDto uploadNews(NewsEditorCreationFormDto newsEditorCreationFormDto) throws IOException;

  News getByNewsTypeAndResource(NewsTypeEnum newsTypeEnum, String resource);

  News getNewsById(Integer newsId);

  void deleteNews(final Integer id);

  NewsSyncDataContainerDto getNotSyncronizedNews(NewsTypeEnum newsTypeEnum);
}
