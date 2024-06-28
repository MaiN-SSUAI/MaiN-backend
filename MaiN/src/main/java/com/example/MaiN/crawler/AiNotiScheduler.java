package com.example.MaiN.crawler;

import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

@Component
public class AiNotiScheduler {

    @Value("${spring.datasource.url}")
    private String dbUrl;

    @Value("${spring.datasource.username}")
    private String dbUsername;

    @Value("${spring.datasource.password}")
    private String dbPassword;

    @Autowired
    private AiNotiCrawler aiNotiCrawler;

    @PostConstruct
    public void init() {
        try {
            SchedulerFactory schedulerFactory = new StdSchedulerFactory();
            Scheduler scheduler = schedulerFactory.getScheduler();

            Trigger trigger = TriggerBuilder.newTrigger()
                    .withIdentity("aiNotiCrawlingTrigger", "group1")
                    .startAt(getScheduledTime())
                    .withSchedule(SimpleScheduleBuilder.simpleSchedule()
                            .withIntervalInHours(24)
                            .repeatForever())
                    .build();

            JobDataMap jobDataMap = new JobDataMap();
            jobDataMap.put("dbUrl", dbUrl);
            jobDataMap.put("dbUsername", dbUsername);
            jobDataMap.put("dbPassword", dbPassword);

            JobDetail job = JobBuilder.newJob(AiNotiCrawler.class)
                    .withIdentity("aiNotiCrawlingJob", "group1")
                    .usingJobData(jobDataMap)
                    .build();

            scheduler.scheduleJob(job, trigger);
            scheduler.start();
        } catch (SchedulerException | ParseException e) {
            e.printStackTrace();
        }
    }

    private Date getScheduledTime() throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String currentDateStr = sdf.format(new Date());
        String scheduledTimeStr = currentDateStr.substring(0, 11) + "00:00:00";
        return sdf.parse(scheduledTimeStr);
    }
}