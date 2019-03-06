package me.exrates.dao.impl;

import lombok.extern.log4j.Log4j2;
import me.exrates.dao.UserVerificationInfoDao;
import me.exrates.model.UserVerificationInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.Map;

@Log4j2
@Repository
public class UserVerificationDocDaoImpl implements UserVerificationInfoDao {

    @Autowired
    @Qualifier(value = "masterTemplate")
    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    @Override
    public UserVerificationInfo saveUserVerificationDoc(UserVerificationInfo userVerificationDoc) {

        String sql = "INSERT INTO USER_VERIFICATION_INFO (user_id, document_type, doc_id) " +
                "VALUES (:user_id, :document_type, :doc_id)";

        Map<String, Object> params = new HashMap<String, Object>() {
            {
                put("user_id", userVerificationDoc.getUserId());
                put("document_type", userVerificationDoc.getDocTypeEnum().name());
                put("doc_id", userVerificationDoc.getDocId());
            }
        };
        namedParameterJdbcTemplate.update(sql, params);
        return userVerificationDoc;
    }
}
