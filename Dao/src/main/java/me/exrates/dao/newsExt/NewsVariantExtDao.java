package me.exrates.dao.newsExt;

import me.exrates.model.newsEntity.NewsVariant;

import java.time.LocalDateTime;

public interface NewsVariantExtDao {
  NewsVariant findActiveByNewsTypeAndResourceAndNewsVariantLanguage(Integer newsTypeId, String resource, String locale);

  NewsVariant findByNewsVariantLanguage(Integer newsId, String language);

  NewsVariant save(NewsVariant newsVariant);

  void setNewsVariantInactive(Integer id, LocalDateTime updatedDate);
}
