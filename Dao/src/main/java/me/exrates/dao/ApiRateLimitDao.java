package me.exrates.dao;

/**
 * Created by Yuriy Berezin on 29-Oct-18.
 */
public interface ApiRateLimitDao {

    Integer getRequestsLimit(String email);

    void updateRequestsLimit(String email, Integer limit);

    void setRequestsDefaultLimit(String email, Integer limit);
}
