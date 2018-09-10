package me.exrates.dao.newsExt.impl;

import me.exrates.dao.newsExt.NewsExtDao;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static me.exrates.model.util.DateTimeUtil.stringToLocalDate;
import static me.exrates.model.util.DateTimeUtil.stringToLocalDateTime;

/**
 * Created by Valk
 */

@Repository
public class NewsExtDaoImpl implements NewsExtDao {

  @Autowired
  @Qualifier(value = "masterTemplate")
  private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

  @Autowired
  private NewsVariantExtDao newsVariantExtDao;

  @Override
  public News findOne(Integer newsId) {
    String sql = "SELECT id, date, resource, description, news_type_id, calendar_date, no_title_img " +
        " FROM NEWS_EXT NEWS" +
        " WHERE NEWS.id = :news_id ";
    Map<String, Object> params = new HashMap<String, Object>() {{
      put("news_id", newsId);
    }};
    try {
      return namedParameterJdbcTemplate.queryForObject(sql, params, new RowMapper<News>() {
        @Override
        public News mapRow(ResultSet rs, int rowNum) throws SQLException {
          News news = new News();
          news.setId(rs.getInt("id"));
          news.setDate(stringToLocalDateTime(rs.getString("date")));
          news.setResource(rs.getString("resource"));
          news.setDescription(rs.getString("description"));
          news.setNewsType(new NewsType(NewsTypeEnum.convert(rs.getInt("news_type_id"))));
          news.setCalendarDate(stringToLocalDate(rs.getString("calendar_date")));
          news.setNoTitleImg(rs.getBoolean("no_title_img"));
          NewsType newsType = new NewsType(NewsTypeEnum.convert(rs.getInt("news_type_id")));
          news.setNewsType(newsType);
          return news;
        }
      });
    } catch (EmptyResultDataAccessException e) {
      return null;
    }
  }

  @Override
  public News findByNewsTypeAndResource(
      Integer newsTypeId,
      String resource) {
    String sql = "SELECT " +
        " NEWS.id as id, NEWS.date, NEWS.resource, NEWS.description, NEWS.news_type_id, NEWS.calendar_date, NEWS.no_title_img " +
        " FROM NEWS_EXT NEWS" +
        " WHERE NEWS.resource = :resource AND NEWS.news_type_id = :news_type_id ";
    Map<String, Object> params = new HashMap<String, Object>() {{
      put("resource", resource);
      put("news_type_id", newsTypeId);
    }};
    try {
      return namedParameterJdbcTemplate.queryForObject(sql, params, new RowMapper<News>() {
        @Override
        public News mapRow(ResultSet rs, int rowNum) throws SQLException {
          News news = new News();
          news.setId(rs.getInt("id"));
          news.setDate(stringToLocalDateTime(rs.getString("date")));
          news.setResource(rs.getString("resource"));
          news.setDescription(rs.getString("description"));
          news.setNewsType(new NewsType(NewsTypeEnum.convert(rs.getInt("news_type_id"))));
          news.setCalendarDate(stringToLocalDate(rs.getString("calendar_date")));
          news.setNoTitleImg(rs.getBoolean("no_title_img"));
          return news;
        }
      });
    } catch (EmptyResultDataAccessException e) {
      return null;
    }
  }


  @Override
  public News save(News news) {
    String sql;
    if (news.getId() == null) {
      sql = "INSERT INTO NEWS_EXT (id, date, resource, description, news_type_id, calendar_date, no_title_img) " +
          "  VALUES (:id, :date, :resource, :description, :news_type_id, :calendar_date, :no_title_img)";
    } else {
      sql = "UPDATE NEWS_EXT " +
          "  SET " +
          "  date = :date, " +
          "  resource = :resource, " +
          "  description = description, " +
          "  news_type_id = :news_type_id, " +
          "  calendar_date = :calendar_date, " +
          "  no_title_img = :no_title_img " +
          "  WHERE id = :id ";
    }
    Map<String, Object> params = new HashMap<String, Object>() {{
      put("id", news.getId());
      put("date", news.getDate());
      put("resource", news.getResource());
      put("description", news.getDescription());
      put("news_type_id", news.getNewsType().getId());
      put("calendar_date", news.getCalendarDate());
      put("no_title_img", news.getNoTitleImg() ? 1 : 0);
    }};
    KeyHolder keyHolder = new GeneratedKeyHolder();
    int result = namedParameterJdbcTemplate.update(sql, new MapSqlParameterSource(params), keyHolder);
    if (keyHolder.getKey() != null) {
      int id = (int) keyHolder.getKey().longValue();
      news.setId(id);
    }
    if (news.getNewsVariant() != null) {
      for (NewsVariant newsVariant : news.getNewsVariant()) {
        newsVariant.setNews(news);
        newsVariantExtDao.save(newsVariant);
      }
    }
    return news;
  }

  @Override
  public List<News> findWithNotSyncNewsVariant(Integer newsTypeId) {
    return null;
  }

}
