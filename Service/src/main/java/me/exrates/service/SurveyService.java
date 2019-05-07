package me.exrates.service;

import me.exrates.model.dto.SurveyDto;

public interface SurveyService {
  void savePollAsDoneByUser(String email);

  boolean checkPollIsDoneByUser(String email);

  SurveyDto getFirstActiveSurveyByLang(String lang);
}
