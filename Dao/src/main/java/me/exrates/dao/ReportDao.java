package me.exrates.dao;

import me.exrates.model.dto.BalancesReportDto;
import org.apache.commons.lang3.tuple.Pair;

import java.util.List;

public interface ReportDao {
    String retrieveReportMailingTime();

    void updateReportMailingTime(String newMailTime);

    boolean isReportMailingEnabled();

    void updateReportMailingEnableStatus(boolean newStatus);

    List<String> retrieveReportSubscribersList(boolean selectWithPremissions);

    void addReportSubscriber(String email);

    void deleteReportSubscriber(String email);

    void addNewBalancesReport(BalancesReportDto balancesReportDto);

    BalancesReportDto getBalancesReportByFileName(String fileName);
}
