package me.exrates.dao;

import me.exrates.model.dto.ReportDto;

import java.time.LocalDateTime;
import java.util.List;

public interface ReportDao {
    String retrieveReportMailingTime();

    void updateReportMailingTime(String newMailTime);

    boolean isReportMailingEnabled();

    void updateReportMailingEnableStatus(boolean newStatus);

    List<String> retrieveReportSubscribersList(boolean selectWithPremissions);

    void addReportSubscriber(String email);

    void deleteReportSubscriber(String email);

    List<ReportDto> getBalancesReportsNames(LocalDateTime fromDate, LocalDateTime toDate);

    ReportDto getBalancesReportById(int id);

    ReportDto getBalancesReportByTime(LocalDateTime fromTime, LocalDateTime toTime);

    List<ReportDto> getInOutReportsNames(LocalDateTime fromDate, LocalDateTime toDate);

    ReportDto getInOutReportById(Integer id);
}
