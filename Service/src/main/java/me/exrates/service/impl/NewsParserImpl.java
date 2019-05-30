package me.exrates.service.impl;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import me.exrates.model.dto.news.Feed;
import me.exrates.model.dto.news.FeedWrapper;
import me.exrates.model.dto.news.ResourceEnum;
import me.exrates.service.NewsParser;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
public class NewsParserImpl implements NewsParser {

    private static final Logger logger = LoggerFactory.getLogger(NewsParserImpl.class);

    private static final int TIMEOUT = 30 * 1000;
    private static final String USER_AGENT = "Mozilla";

    private LoadingCache<Integer, List<Feed>> CACHE = CacheBuilder.newBuilder()
            .refreshAfterWrite(5, TimeUnit.MINUTES)
            .maximumSize(1000)
            .build(new CacheLoader<Integer, List<Feed>>() {
                @Override
                public List<Feed> load(Integer key) {
                    return parseNews();
                }
            });

    @PostConstruct()
    public void init() {
        CACHE.put(ResourceEnum.AMB_CRYPTO.getId(), parseStandardRss(ResourceEnum.AMB_CRYPTO));
        CACHE.put(ResourceEnum.COIN_CODEX.getId(), parseStandardRss(ResourceEnum.COIN_CODEX));
        CACHE.put(ResourceEnum.PORTAL_DOBITCOIN.getId(), parseStandardRss(ResourceEnum.PORTAL_DOBITCOIN));
        CACHE.put(ResourceEnum.FEEDBURNER.getId(), parseStandardRss(ResourceEnum.FEEDBURNER));
        CACHE.put(ResourceEnum.BITCOINERX.getId(), parseStandardRss(ResourceEnum.BITCOINERX));
    }

    public FeedWrapper getFeeds(int offset, int count, int index) {
        if (index == 0) {
            Collection<List<Feed>> values = CACHE.asMap().values();
            List<Feed> result = new ArrayList<>();
            values.forEach(result::addAll);
            List<Feed> pageResult = result.stream()
                    .sorted(Comparator.nullsLast((e1, e2) -> e2.getDate().compareTo(e1.getDate())))
                    .skip(offset)
                    .limit(count)
                    .collect(Collectors.toList());
            return new FeedWrapper(pageResult, result.size());
        } else {
            List<Feed> news = CACHE.getIfPresent(index);
            if (news == null) {
                return FeedWrapper.ofNull();
            }
            int countNews = news.size();
            List<Feed> result = news.stream()
                    .sorted(Comparator.nullsLast((e1, e2) -> e2.getDate().compareTo(e1.getDate())))
                    .skip(offset)
                    .limit(count)
                    .collect(Collectors.toList());
            return new FeedWrapper(result, countNews);
        }
    }

    private List<Feed> parseNews() {
        List<Feed> allFeed = new ArrayList<>();

        allFeed.addAll(parseStandardRss(ResourceEnum.AMB_CRYPTO));
        allFeed.addAll(parseStandardRss(ResourceEnum.COIN_CODEX));
        allFeed.addAll(parseStandardRss(ResourceEnum.PORTAL_DOBITCOIN));
        allFeed.addAll(parseStandardRss(ResourceEnum.FEEDBURNER));
        allFeed.addAll(parseStandardRss(ResourceEnum.BITCOINERX));

        return allFeed.stream()
                .sorted(Comparator.nullsLast((e1, e2) -> e2.getDate().compareTo(e1.getDate())))
                .collect(Collectors.toList());
    }

    private List<Feed> parseStandardRss(ResourceEnum resourceEnum) {
        logger.info("Starting parse resource {}", resourceEnum.getUr());
        List<Feed> result = new ArrayList<>();
        try {
            Document document = Jsoup.connect(resourceEnum.getUr()).userAgent(USER_AGENT).timeout(TIMEOUT).ignoreContentType(true).get();
            Elements elements = document.select("item");
            for (Element element : elements) {
                String title = element.select("title").text();
                String url = element.select("guid").text();
                String time = element.select("pubDate").text();
                Date date = getDateFromString(time, "EEE, dd MMM yyyy HH:mm:ss Z");
                result.add(new Feed(title, url, date, resourceEnum.getId()));
            }
        } catch (Exception e) {
            logger.error("Error parsing resource {}, e {}", resourceEnum.getUr(), e.getLocalizedMessage());
            return Collections.emptyList();
        }
        logger.info("Successful parsed resource {}, size list {}", resourceEnum.getUr(), result.size());
        return result;
    }

    private Date getDateFromString(String time, String pattern) {
        SimpleDateFormat format = new SimpleDateFormat(pattern, Locale.ENGLISH);
        try {
            return format.parse(time);
        } catch (ParseException i) {
            return new Date();
        }

    }
}
