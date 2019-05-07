package me.exrates.service.job.report;

import me.exrates.service.ReportService;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ReportMailingJob implements Job {

    @Autowired
    private ReportService reportService;


    @Override
    public void execute(JobExecutionContext context) {
    }
}
