package me.exrates.service;

import me.exrates.model.News;
import me.exrates.model.dto.NewsSummaryDto;
import me.exrates.model.dto.onlineTableDto.NewsDto;
import me.exrates.model.vo.CacheData;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.Locale;

/**
 * Created by Valk on 28.05.2016.
 */
public interface NewsService {
    /**
     * Returns list the news sorted by date when news was published
     * list consists the news in variant of current locale
     * @param offset used for pagination
     * @param limit  used for pagination
     * @param locale is current locale
     * @return list the news
     */
    List<NewsDto> getNewsBriefList(CacheData cacheData, final Integer offset, final Integer limit, Locale locale);

    @Transactional(readOnly = true)
    List<NewsDto> getNewsBriefList(Integer offset, Integer limit, Locale locale);

    /**
     * Returns news with given ID in variant of current locale
     * @param newsId
     * @param locale is current locale
     * @return news of given ID
     */
    News getNews(final Integer newsId, Locale locale);

    News getNewsWithContent(Integer newsId, Locale locale, String locationDir);

    /**
     * Receives list the one News in variants corresponding to locales which collected from ZIP package-file
     * If the News is new, then creates it in DB
     * Files from the ZIP package-file are being deployed on disk in newsLocationDir
     * @param variants is the list of locale variants of the one News
     * @param multipartFile is the file received from frontend
     * @param newsLocationDir is the place where ZIP package-file must be deployed
     * @return true is success
     */
    boolean uploadNews(Collection<News> variants, MultipartFile multipartFile, String newsLocationDir);

    News createNewsVariant(News news, String newsLocationDir, String tempImageDir, String logicalPath);

    String uploadImageForNews(MultipartFile file, String location, String logicalPath) throws IOException;

    int deleteNewsVariant(News news);

    int deleteNews(News news);

    List<NewsSummaryDto> findAllNewsVariants();
}
