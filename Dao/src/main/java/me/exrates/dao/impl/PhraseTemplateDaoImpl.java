package me.exrates.dao.impl;

import lombok.extern.log4j.Log4j2;
import me.exrates.dao.PhraseTemplateDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.Collections;
import java.util.List;
import java.util.Map;


/**
 * Created by ValkSam
 */
@Repository
@Log4j2
public class PhraseTemplateDaoImpl implements PhraseTemplateDao {

  @Autowired
  @Qualifier(value = "masterTemplate")
  private NamedParameterJdbcTemplate parameterJdbcTemplate;

  @Autowired
  private JdbcTemplate jdbcTemplate;

  @Override
  public List<String> findByTopic(Integer topicId) {
    final String sql = "SELECT template " +
        " FROM PHRASE_TEMPLATE " +
        " WHERE topic_id = :topic_id ";
    final Map<String, Integer> params = Collections.singletonMap("topic_id", topicId);
    return parameterJdbcTemplate.query(sql, params, (resultSet, i) -> resultSet.getString("template"));
  }
}
