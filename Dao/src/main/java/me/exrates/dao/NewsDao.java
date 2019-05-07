package me.exrates.dao;

import me.exrates.model.News;
import me.exrates.model.dto.NewsSummaryDto;
import me.exrates.model.dto.onlineTableDto.NewsDto;

import java.util.List;
import java.util.Locale;

public interface NewsDao {
    List<NewsDto> getNewsBriefList(Integer offset, Integer limit, Locale locale);

    News getNews(final Integer newsId, Locale locale);

    int addNews(News news);

    int addNewsVariant(News news);

    int deleteNewsVariant(News news);

    int deleteNews(News news);

    List<NewsSummaryDto> findAllNewsVariants();
}
