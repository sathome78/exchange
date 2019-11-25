package me.exrates.service.scheduled;

import lombok.extern.log4j.Log4j2;
import me.exrates.service.ReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.PropertySource;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Log4j2
@EnableScheduling
@PropertySource(value = {"classpath:/scheduler.properties"})
@Service
public class ScheduledGenerateReportTwo {

    private final ReportService reportService;

    @Autowired
    public ScheduledGenerateReportTwo(ReportService reportService) {
        this.reportService = reportService;
    }

    @Scheduled(cron = "${scheduled.update.report}")
    public void update() {
        try {
            reportService.generateInputOutputSummaryReportObject();
        } catch (Exception ex) {
            log.error("--> In processing 'ScheduledGenerateReportTwo' occurred error", ex);
        }
    }
}
