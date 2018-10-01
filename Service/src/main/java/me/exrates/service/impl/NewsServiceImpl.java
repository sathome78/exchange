package me.exrates.service.impl;

import me.exrates.dao.NewsDao;
import me.exrates.model.News;
import me.exrates.model.dto.NewsSummaryDto;
import me.exrates.model.dto.onlineTableDto.NewsDto;
import me.exrates.model.vo.CacheData;
import me.exrates.service.NewsService;
import me.exrates.service.exception.FileLoadingException;
import me.exrates.service.exception.NewsCreationException;
import me.exrates.service.impl.proxy.ServiceCacheableProxy;
import me.exrates.service.util.Cache;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.social.twitter.api.Tweet;
import org.springframework.social.twitter.api.UrlEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * Created by Valk on 28.05.2016.
 */
@Service
public class NewsServiceImpl implements NewsService {

    private static final Logger LOG = LogManager.getLogger(NewsServiceImpl.class);

    @Autowired
    private NewsDao newsDao;

    @Autowired
    ServiceCacheableProxy serviceCacheableProxy;

    @Override
    @Transactional(readOnly = true)
    public List<NewsDto> getNewsBriefList(CacheData cacheData, Integer offset, Integer limit, Locale locale) {
        List<NewsDto> result = serviceCacheableProxy.getNewsBriefList(offset, limit, locale);
        if (Cache.checkCache(cacheData, result)) {
            result = new ArrayList<NewsDto>() {{
                add(new NewsDto(false));
            }};
        }
        return result;
    }

    @Override
    public News getNews(Integer newsId, Locale locale) {
        return newsDao.getNews(newsId, locale);
    }

    @Override
    public News getNewsWithContent(Integer newsId, Locale locale, String locationDir) {
        News news = getNews(newsId, locale);
        if (news == null) {
            return null;
        }
        String contentPathString = locationDir + news.getResource() +
                new StringJoiner("/").add(String.valueOf(news.getId())).add(news.getNewsVariant()).add("newstopic.html").toString();
        Path contentPath = Paths.get(contentPathString);
        LOG.debug(contentPathString);
        if (contentPath.toFile().exists()) {
            try {
                StringBuilder fileContent = new StringBuilder();
                Files.lines(contentPath).forEach(line -> fileContent.append(line).append('\n'));
                news.setContent(fileContent.toString().substring(0, fileContent.length() - 1));
                LOG.debug(news.getContent());
            } catch (IOException e) {
                throw new FileLoadingException(e.getLocalizedMessage());
            }

        } else {
            throw new FileLoadingException("Content does not exist!");
        }
        return news;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean uploadNews(Collection<News> variants, MultipartFile multipartFile, String newsLocationDir) {
        News news = variants.iterator().next();
        if (news.getId() == null) {
            int id = newsDao.addNews(news);
            if (id == 0) {
                throw new NewsCreationException("");
            }
            for (News n : variants) {
                n.setId(id);
            }
        }
        String newsRootContentPath = new StringBuilder()
                .append(newsLocationDir)
                .append(news.getResource())
                .append(news.getResource().matches(".+\\/$") ? "" : "/")
                .append(news.getId())
                .append("/")
                .toString();
        try {
            for (News v : variants) {
                newsDao.addNewsVariant(v);
            }
            Path op = Paths.get(newsRootContentPath);
            if (!op.toFile().exists()) {
                Files.createDirectories(op);
            }
            ZipInputStream zis = new ZipInputStream(multipartFile.getInputStream(), Charset.forName("CP866"));
            ZipEntry ze;
            while ((ze = zis.getNextEntry()) != null) {
                String fn = newsRootContentPath + ze.getName();
                op = Paths.get(fn);
                if (fn.matches(".+\\/$")) {
                    if (!op.toFile().exists()) {
                        Files.createDirectories(op);
                    }
                } else {
                    if (op.toFile().exists()) {
                        op.toFile().delete();
                    }
                    Files.copy(zis, op);
                }
                zis.closeEntry();
            }
            zis.close();
        } catch (IOException e) {
            e.printStackTrace();
            throw new FileLoadingException(e.getLocalizedMessage());
        }
        return true;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public News createNewsVariant(News news, String newsLocationDir, String tempImageDir, String logicalPath) {
        Integer id = news.getId();
        boolean isNew = id == null;

        if (isNew) {
            id = newsDao.addNews(news);
            news.setId(id);


        }

        try {
            if (isNew || (newsDao.getNews(id, new Locale(news.getNewsVariant())) == null)) {
                newsDao.addNewsVariant(news);
            }
            String newsRootContentPath = new StringJoiner("")
                    .add(newsLocationDir)
                    .add(news.getResource())
                    .add(String.valueOf(id)).add("/")
                    .add(news.getNewsVariant()).add("/")
                    .toString();
            LOG.debug(newsRootContentPath);
            Path newsDirPath = Paths.get(newsRootContentPath);
            if (!newsDirPath.toFile().exists()) {
                Files.createDirectories(newsDirPath);
            }
            if (isNew) {
                String updatedContent = replaceImageLinksAndFiles(news.getContent(),
                        newsRootContentPath.substring(0, newsRootContentPath.length() - 3) + "img/",
                        tempImageDir, logicalPath + "/" + news.getResource() + news.getId() + "/img/");
                news.setContent(updatedContent);
            }
            Path contentPath = Paths.get(newsRootContentPath + "newstopic.html");
            Path titlePath = Paths.get(newsRootContentPath + "title.md");
            Path briefPath = Paths.get(newsRootContentPath + "brief.md");
            Files.write(contentPath, Collections.singleton(news.getContent()));
            Files.write(titlePath, Collections.singleton(news.getTitle()));
            Files.write(briefPath, Collections.singleton(news.getBrief()));


        } catch (IOException e) {
            e.printStackTrace();
            throw new FileLoadingException(e.getLocalizedMessage());
        }
        return news;
    }

    private String replaceImageLinksAndFiles(String content, String newsImageDir,
                                             String tempImageDir, String logicalPath) {
        Document document = Jsoup.parse(content);
        Elements images = document.getElementsByTag("img");
        images.forEach(LOG::debug);
        images.forEach(elem -> {
            String filename = elem.attr("src").substring(elem.attr("src").lastIndexOf('/') + 1);
            Path filePath = Paths.get(tempImageDir + filename);
            Path targetDirPath = Paths.get(newsImageDir);
            if(filePath.toFile().exists()) {
                try {
                    if (!targetDirPath.toFile().exists()) {
                        Files.createDirectories(targetDirPath);
                    }
                    Files.move(filePath, Paths.get(newsImageDir + filename));
                    elem = elem.attr("src", logicalPath + filename);
                } catch (IOException e) {
                    LOG.error(e.getLocalizedMessage());
                }
            }

        });
        LOG.debug(document.body());
        return document.body().html();
    }

    @Override
    public String uploadImageForNews(MultipartFile file, String location, String logicalPath) throws IOException {
        final Path path = Paths.get(location);
        if (!Files.exists(path)) {
            Files.createDirectories(path);
        }
        final String name = UUID.randomUUID().toString() + "." + file.getContentType().toLowerCase().substring(6);
        final Path target = Paths.get(path.toString(), name);
        Files.write(target, file.getBytes());
        return logicalPath + name;
    }

    private List<String> getAvailableImages(String locationDir) {
        Path path = Paths.get(locationDir);
        if (!path.toFile().exists()) {
            throw new FileLoadingException("Content does not exist!");
        }
       return Arrays.stream(path.toFile().listFiles((dir, name) -> name.toLowerCase().endsWith(".jpg") ||
                name.toLowerCase().endsWith(".jpeg") ||
                name.toLowerCase().endsWith(".png"))).map(File::getAbsolutePath)
                .collect(Collectors.toList());
    }

    @Override
    public int deleteNewsVariant(News news) {
        return newsDao.deleteNewsVariant(news);
    }

    @Override
    public int deleteNews(News news) {
        return newsDao.deleteNews(news);
    }

    @Override
    public List<NewsSummaryDto> findAllNewsVariants() {
        return newsDao.findAllNewsVariants();
    }

    @Override
    public List<NewsDto> getTwitterNews(Integer amount) {
        return serviceCacheableProxy.getTwitterTimeline(amount)
                .stream()
                .map(tweet -> {
                    NewsDto dto = new NewsDto();
                    dto.setTitle(this.removeUrlFromTweet(tweet));
                    dto.setDate(tweet.getCreatedAt().toInstant().atZone(ZoneId.systemDefault()).toLocalDate());
                    dto.setRef(tweet.getIdStr());
                    return dto;
                })
                .collect(Collectors.toList());
    }

    private String removeUrlFromTweet(Tweet tweet) {
        String fullText = tweet.getText();
        List<UrlEntity> URLs = tweet.getEntities().getUrls();
        for (UrlEntity ue : URLs) {
            fullText = fullText.replace(ue.getUrl(), "");
        }
        return fullText;
    }



}
