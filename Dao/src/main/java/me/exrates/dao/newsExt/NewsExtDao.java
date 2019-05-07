package me.exrates.dao.newsExt;

import me.exrates.model.newsEntity.News;

import java.util.List;

public interface NewsExtDao {
    News findOne(Integer newsId);

    News findByNewsTypeAndResource(Integer newsTypeId, String resource);

    News save(News news);

    List<News> findWithNotSyncNewsVariant(Integer newsTypeId);
}
