package me.exrates.dao;

import me.exrates.model.SessionLifeTimeType;
import me.exrates.model.SessionParams;
import org.apache.commons.lang3.tuple.Pair;

import java.util.List;

/**
 * Created by maks on 31.03.2017.
 */
public interface SessionParamsDao {

    List<SessionLifeTimeType> getAllByActive(boolean active);

    SessionParams getByUserEmail(String userEmail);

    boolean create(SessionParams sessionLifeType);

    boolean update(SessionParams sessionParams);

    List<Pair<String, Integer>> getAll();
}
