package me.exrates.service.scheduled;

import me.exrates.service.ReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.PropertySource;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@EnableScheduling
@PropertySource(value = {"classpath:/scheduler.properties"})
@Service
public class ScheduledGenerateReportFour {

    private final ReportService reportService;

    @Autowired
    public ScheduledGenerateReportFour(ReportService reportService) {
        this.reportService = reportService;
    }

    @Scheduled(cron = "${scheduled.update.report-balances}")
    public void updateExternalWalletBalances() {
        reportService.generateWalletBalancesReport();
    }
}
