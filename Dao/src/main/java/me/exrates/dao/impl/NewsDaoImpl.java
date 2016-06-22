package me.exrates.dao.impl;

import me.exrates.dao.NewsDao;
import me.exrates.model.News;
import me.exrates.model.dto.NewsDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Created by Valk on 27.05.2016.
 */

@Repository
public class NewsDaoImpl implements NewsDao {

    @Autowired
    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    @Override
    @Transactional
    public List<NewsDto> getNewsBriefList(final Integer offset, final Integer limit, Locale locale) {
        String sql = "SELECT id, title, brief, resource, news_variant " +
                " FROM NEWS" +
                " JOIN NEWS_VARIANTS ON (NEWS_VARIANTS.news_id = NEWS.id) " +
                " AND (NEWS_VARIANTS.news_variant = :news_variant)" +
                " AND (NEWS_VARIANTS.active = 1)" +
                " ORDER BY date DESC, added_date DESC " +
                (limit == -1 ? "" : "  LIMIT " + limit + " OFFSET " + offset);
        Map<String, Object> params = new HashMap<String, Object>() {{
            put("news_variant", locale.toString().toUpperCase());
        }};
        return namedParameterJdbcTemplate.query(sql, params, new RowMapper<NewsDto>() {
            @Override
            public NewsDto mapRow(ResultSet rs, int rowNum) throws SQLException {
                NewsDto result = new NewsDto();
                result.setId(rs.getInt("id"));
                result.setTitle(rs.getString("title"));
                result.setBrief(rs.getString("brief"));
                result.setResource(rs.getString("resource"));
                result.setVariant(rs.getString("news_variant"));
                result.setRef(new StringBuilder("/news/")
                        .append(rs.getString("resource"))
                        .append(rs.getString("id"))
                        .append("/")
                        .append(locale.toString())
                        .append("/").toString());
                return result;
            }
        });
    }

    @Override
    @Transactional
    public News getNews(final Integer newsId, Locale locale) {
        String sql = "SELECT id, title, date, brief, resource, news_variant" +
                " FROM NEWS" +
                " JOIN NEWS_VARIANTS ON (NEWS_VARIANTS.news_id = NEWS.id) " +
                " AND (NEWS_VARIANTS.news_variant = :news_variant)" +
                " AND (NEWS_VARIANTS.active = 1)" +
                " WHERE id = :news_id";
        Map<String, Object> params = new HashMap<String, Object>() {{
            put("news_id", newsId);
            put("news_variant", locale.toString().toUpperCase());
        }};
        try {
            return namedParameterJdbcTemplate.queryForObject(sql, params, new RowMapper<News>() {
                @Override
                public News mapRow(ResultSet rs, int rowNum) throws SQLException {
                    News result = new News();
                    result.setId(rs.getInt("id"));
                    result.setTitle(rs.getString("title"));
                    result.setDate(rs.getTimestamp("date").toLocalDateTime().toLocalDate());
                    result.setBrief(rs.getString("brief"));
                    result.setResource(rs.getString("resource"));
                    return result;
                }
            });
        } catch (DataAccessException e) {
            return null;
        }
    }

    @Override
    @Transactional
    public int addNews(News news) {
        String sql = "INSERT INTO NEWS" +
                " (date, resource)" +
                " VALUES" +
                " (:date, :resource)";
        Map<String, Object> params = new HashMap<String, Object>() {{
            put("date", news.getDate());
            put("resource", news.getResource());
        }};
        KeyHolder keyHolder = new GeneratedKeyHolder();
        int result = namedParameterJdbcTemplate.update(sql, new MapSqlParameterSource(params), keyHolder);
        final int id = (int) keyHolder.getKey().longValue();
        if (result <= 0) {
            return 0;
        }
        return id;
    }

    @Override
    @Transactional
    public int addNewsVariant(News news) {
        String sql = " INSERT INTO NEWS_VARIANTS " +
                " (news_id, title, news_variant, brief)" +
                " VALUES" +
                " (:news_id, :title, :news_variant, :brief)";
        Map<String, Object> params = new HashMap<String, Object>() {{
            put("news_id", news.getId());
            put("title", news.getTitle());
            put("news_variant", news.getNewsVariant());
            put("brief", news.getBrief());
        }};
        try {
            return namedParameterJdbcTemplate.update(sql, new MapSqlParameterSource(params));
        } catch (DuplicateKeyException e) {
            //provide an opportunity to update the file
            return 0;
        }
    }

    @Override
    @Transactional
    public int deleteNewsVariant(News news) {
        String sql = " DELETE FROM NEWS_VARIANTS " +
                " WHERE (news_id = :news_id) and (news_variant = :news_variant)";
        Map<String, Object> params = new HashMap<String, Object>() {{
            put("news_id", news.getId());
            put("news_variant", news.getNewsVariant());
        }};
        return namedParameterJdbcTemplate.update(sql, new MapSqlParameterSource(params));
    }

    @Override
    @Transactional
    public int deleteNews(News news) {
        String sql = " DELETE FROM NEWS_VARIANTS " +
                " WHERE (news_id = :news_id)";
        Map<String, Object> params = new HashMap<String, Object>() {{
            put("news_id", news.getId());
            put("news_variant", news.getNewsVariant());
        }};
        namedParameterJdbcTemplate.update(sql, new MapSqlParameterSource(params));
        /**/
        sql = " DELETE FROM NEWS " +
                " WHERE (id = :news_id)";
        params = new HashMap<String, Object>() {{
            put("news_id", news.getId());
        }};
        return namedParameterJdbcTemplate.update(sql, new MapSqlParameterSource(params));
    }
}
