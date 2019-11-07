package me.exrates.service.impl;


import me.exrates.dao.SurveyDao;
import me.exrates.dao.UserDao;
import me.exrates.model.dto.SurveyDto;
import me.exrates.service.SurveyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class SurveyServiceImpl implements SurveyService {

  @Autowired
  private
  UserDao userDao;

  @Autowired
  SurveyDao surveyDao;

  @Override
  @Transactional
  public void savePollAsDoneByUser(String email) {
    userDao.savePollAsDoneByUser(email);
  }

  @Override
  @Transactional(readOnly = true)
  public boolean checkPollIsDoneByUser(String email) {
    return userDao.checkPollIsDoneByUser(email);
  }

  @Override
  @Transactional(readOnly = true)
  public SurveyDto getFirstActiveSurveyByLang(String lang) {
    SurveyDto surveyDto = surveyDao.findFirstActiveByLang(lang);
    if (surveyDto.getId() == null) {
      surveyDto = surveyDao.findFirstActiveByLang("en");
    }
    return surveyDto;
  }

}