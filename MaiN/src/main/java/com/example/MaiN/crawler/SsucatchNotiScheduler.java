package com.example.MaiN.crawler;

import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

@Component
public class SsucatchNotiScheduler {
    @Value("${spring.datasource.url}")
    private String dbUrl;

    @Value("${spring.datasource.username}")
    private String dbUsername;

    @Value("${spring.datasource.password}")
    private String dbPassword;

    @PostConstruct
    public void init() {
        try {
            SchedulerFactory schedulerFactory = new StdSchedulerFactory();
            Scheduler scheduler = schedulerFactory.getScheduler();

            // 크롤링이 수행될 트리거 설정
            Trigger trigger = TriggerBuilder.newTrigger()
                    .withIdentity("ssucatchNotiCrawlingTrigger", "group1")
                    .startAt(getScheduledTime()) // 크롤링이 처음 실행될 시간 설정
                    .withSchedule(SimpleScheduleBuilder.simpleSchedule()
                            .withIntervalInHours(24)
                            .repeatForever()) // 매일 24시간 주기로 반복
                    .build();

            // 크롤링 작업을 수행할 Job을 정의
            JobDetail job = JobBuilder.newJob(com.example.MaiN.crawler.SsucatchNotiCrawler.class)
                    .withIdentity("ssucatchNotiCrawlingJob", "group1")
                    .build();

            JobDataMap jobDataMap = new JobDataMap();
            jobDataMap.put("dbUrl", dbUrl);
            jobDataMap.put("dbUsername", dbUsername);
            jobDataMap.put("dbPassword", dbPassword);

            // 스케줄러에 Job과 Trigger 등록
            scheduler.scheduleJob(job, trigger);

            // 스케줄러 시작
            scheduler.start();
        } catch (SchedulerException | ParseException e) {
            e.printStackTrace();
        }
    }

    private Date getScheduledTime() throws ParseException {
        // 현재 날짜와 시간을 가져옴
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String currentDateStr = sdf.format(new Date());

        // 시간을 현재 날짜에 맞게 설정
        String scheduledTimeStr = currentDateStr.substring(0, 11) + "00:00:00"; // 원하는 시간으로 변경

        return sdf.parse(scheduledTimeStr);
    }
}
