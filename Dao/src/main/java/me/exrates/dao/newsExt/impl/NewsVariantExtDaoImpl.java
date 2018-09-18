package me.exrates.dao.newsExt.impl;

import me.exrates.dao.newsExt.NewsVariantExtDao;
import me.exrates.model.enums.NewsTypeEnum;
import me.exrates.model.newsEntity.News;
import me.exrates.model.newsEntity.NewsType;
import me.exrates.model.newsEntity.NewsVariant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import static me.exrates.model.util.DateTimeUtil.stringToLocalDate;
import static me.exrates.model.util.DateTimeUtil.stringToLocalDateTime;

/**
 * Created by Valk
 */

@Repository
public class NewsVariantExtDaoImpl implements NewsVariantExtDao {

  @Autowired
  @Qualifier(value = "masterTemplate")
  private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

  @Override
  public NewsVariant findActiveByNewsTypeAndResourceAndNewsVariantLanguage(
      Integer newsTypeId,
      String resource,
      String locale) {
    String sql = "SELECT " +
        " NEWS.id as id, NEWS.date, NEWS.resource, NEWS.description, NEWS.news_type_id, NEWS.calendar_date, NEWS.no_title_img, " +
        " NEWS_VARIANTS.id as news_variant_id, title, language, brief, content, added_date, active, visit_count, tags, sync_to_wallet_date, updated_date " +
        " FROM NEWS_EXT NEWS" +
        " JOIN NEWS_VARIANTS_EXT NEWS_VARIANTS ON (NEWS_VARIANTS.news_id = NEWS.id) " +
        " AND (NEWS_VARIANTS.language = :language)" +
        " AND (NEWS_VARIANTS.active = 1)" +
        " WHERE NEWS.resource = :resource AND NEWS.news_type_id = :news_type_id ";
    Map<String, Object> params = new HashMap<String, Object>() {{
      put("language", locale);
      put("resource", resource);
      put("news_type_id", newsTypeId);
    }};
    try {
      return namedParameterJdbcTemplate.queryForObject(sql, params, new RowMapper<NewsVariant>() {
        @Override
        public NewsVariant mapRow(ResultSet rs, int rowNum) throws SQLException {
          NewsVariant newsVariant = new NewsVariant();
          newsVariant.setId(rs.getInt("news_variant_id"));
          newsVariant.setTitle(rs.getString("title"));
          newsVariant.setBrief(rs.getString("brief"));
          newsVariant.setContent(rs.getString("content"));
          newsVariant.setLanguage(rs.getString("language"));
          newsVariant.setAddedDate(stringToLocalDateTime(rs.getString("added_date")));
          newsVariant.setActive(rs.getInt("active") == 1);
          newsVariant.setVisitCount(rs.getInt("visit_count"));
          newsVariant.setTags(rs.getString("tags"));
          newsVariant.setSyncToWalletDate(stringToLocalDateTime(rs.getString("sync_to_wallet_date")));
          newsVariant.setUpdatedDate(stringToLocalDateTime(rs.getString("updated_date")));
          News news = new News();
          news.setId(rs.getInt("id"));
          news.setDate(stringToLocalDateTime(rs.getString("date")));
          news.setResource(rs.getString("resource"));
          news.setDescription(rs.getString("description"));
          news.setNewsType(new NewsType(NewsTypeEnum.convert(rs.getInt("news_type_id"))));
          news.setCalendarDate(stringToLocalDate(rs.getString("calendar_date")));
          news.setNoTitleImg(rs.getBoolean("no_title_img"));
          newsVariant.setNews(news);
          return newsVariant;
        }
      });
    } catch (EmptyResultDataAccessException e) {
      return null;
    }
  }

  @Override
  public NewsVariant findByNewsVariantLanguage(
      Integer newsId,
      String locale) {
    String sql = "SELECT id, news_id, title, language, brief, content, added_date, active, visit_count, tags, sync_to_wallet_date, updated_date " +
        " FROM NEWS_VARIANTS_EXT NEWS_VARIANTS " +
        " WHERE news_id = :news_id AND language = :language AND active = 1";
    Map<String, Object> params = new HashMap<String, Object>() {{
      put("language", locale);
      put("news_id", newsId);
    }};
    try {
      return namedParameterJdbcTemplate.queryForObject(sql, params, new RowMapper<NewsVariant>() {
        @Override
        public NewsVariant mapRow(ResultSet rs, int rowNum) throws SQLException {
          NewsVariant newsVariant = new NewsVariant();
          newsVariant.setId(rs.getInt("id"));
          newsVariant.setTitle(rs.getString("title"));
          newsVariant.setBrief(rs.getString("brief"));
          newsVariant.setContent(rs.getString("content"));
          newsVariant.setLanguage(rs.getString("language"));
          newsVariant.setAddedDate(stringToLocalDateTime(rs.getString("added_date")));
          newsVariant.setActive(rs.getInt("active") == 1);
          newsVariant.setVisitCount(rs.getInt("visit_count"));
          newsVariant.setTags(rs.getString("tags"));
          newsVariant.setSyncToWalletDate(stringToLocalDateTime(rs.getString("sync_to_wallet_date")));
          newsVariant.setUpdatedDate(stringToLocalDateTime(rs.getString("updated_date")));
          News news = new News();
          news.setId(rs.getInt("news_id"));
          newsVariant.setNews(news);
          return newsVariant;
        }
      });
    } catch (EmptyResultDataAccessException e) {
      return null;
    }
  }

  @Override
  public NewsVariant save(NewsVariant newsVariant) {
    String sql;
    if (newsVariant.getId() == null) {
      sql = "INSERT INTO NEWS_VARIANTS_EXT (id, news_id, title, language, brief, content, added_date, active, visit_count, tags, sync_to_wallet_date, updated_date) " +
          "  VALUES (:id, :news_id, :title, :language, :brief, :content, :added_date, :active, :visit_count, :tags, :sync_to_wallet_date, :updated_date)";
    } else {
      sql = "UPDATE NEWS_VARIANTS_EXT " +
          "  SET " +
          "  news_id = :news_id, " +
          "  title = :title, " +
          "  language = :language, " +
          "  brief = :brief, " +
          "  content = :content, " +
          "  added_date = :added_date, " +
          "  active = :active, " +
          "  visit_count = :visit_count, " +
          "  tags = :tags, " +
          "  sync_to_wallet_date = :sync_to_wallet_date, " +
          "  updated_date = :updated_date " +
          "  WHERE id = :id ";
    }
    Map<String, Object> params = new HashMap<String, Object>() {{
      put("id", newsVariant.getId());
      put("news_id", newsVariant.getNews().getId());
      put("title", newsVariant.getTitle());
      put("language", newsVariant.getLanguage());
      put("brief", newsVariant.getBrief());
      put("content", newsVariant.getContent());
      put("added_date", newsVariant.getAddedDate());
      put("active", newsVariant.getActive());
      put("visit_count", newsVariant.getVisitCount());
      put("tags", newsVariant.getTags());
      put("sync_to_wallet_date", newsVariant.getSyncToWalletDate());
      put("updated_date", newsVariant.getUpdatedDate());
    }};
    KeyHolder keyHolder = new GeneratedKeyHolder();
    int result = namedParameterJdbcTemplate.update(sql, new MapSqlParameterSource(params), keyHolder);
    if (keyHolder.getKey() != null) {
      int id = (int) keyHolder.getKey().longValue();
      newsVariant.setId(id);
    }
    return newsVariant;
  }

  @Override
  public void setNewsVariantInactive(Integer id, LocalDateTime updatedDate) {
    String sql = "UPDATE NEWS_VARIANTS_EXT " +
        "  SET active = 0, " +
        "  updated_date = :updated_date " +
        "  WHERE id = :id ";
    Map<String, Object> params = new HashMap<String, Object>() {{
      put("id", id);
      put("updated_date", updatedDate);
    }};
    namedParameterJdbcTemplate.update(sql, new MapSqlParameterSource(params));
  }

}
