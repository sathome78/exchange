package me.exrates.service;

import com.google.gson.JsonObject;
import me.exrates.model.SessionLifeTimeType;
import me.exrates.model.SessionParams;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

public interface SessionParamsService {
    int getMinSessionTime();

    int getMaxSessionTime();

    List<SessionLifeTimeType> getAllByActive(boolean active);

    SessionParams getByUserEmail(String userEmail);

    SessionParams getByEmailOrDefault(String email);

    SessionParams saveOrUpdate(SessionParams sessionParams, String userEmail);

    SessionParams determineSessionParams();

    boolean isSessionTimeValid(int sessionTime);

    boolean isSessionLifeTypeIdValid(int typeId);

    boolean islifeTypeActive(int sessionLifeTypeId);

    void setSessionLifeParams(HttpServletRequest request);

    JsonObject getSessionEndString(HttpServletRequest request);
}
