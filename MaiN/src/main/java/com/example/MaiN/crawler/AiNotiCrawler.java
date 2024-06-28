package com.example.MaiN.crawler;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.sql.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

@Component
public class AiNotiCrawler implements Job {

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        JobDataMap dataMap = context.getJobDetail().getJobDataMap();
        String dbUrl = dataMap.getString("dbUrl");
        String dbUsername = dataMap.getString("dbUsername");
        String dbPassword = dataMap.getString("dbPassword");

        String url = "http://aix.ssu.ac.kr/notice.html?searchKey=ai&page=1";

        try (Connection conn = DriverManager.getConnection(dbUrl, dbUsername, dbPassword)) {
            conn.setAutoCommit(false);

            // 크롤링 코드
            Document doc = Jsoup.connect(url).get();
            Elements data = doc.select("tr");

            for (Element item : data) {
                Element titleLink = item.selectFirst("a");
                if (titleLink != null) {
                    String title = titleLink.text();
                    String link = "http://aix.ssu.ac.kr/" + titleLink.attr("href");
                    String dateStr = item.select("td").get(2).text();

                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy.MM.dd");
                    Date date = dateFormat.parse(dateStr);

                    try (PreparedStatement countStatement = conn.prepareStatement("SELECT COUNT(*) FROM ai_noti WHERE title = ?");
                         PreparedStatement insertStatement = conn.prepareStatement(
                                 "INSERT INTO ai_noti(title, link, date) VALUES (?, ?, ?)")) {
                        countStatement.setString(1, title);
                        ResultSet countResult = countStatement.executeQuery();
                        countResult.next();
                        if (countResult.getInt(1) > 0) {
                            continue;
                        }

                        insertStatement.setString(1, title);
                        insertStatement.setString(2, link);
                        insertStatement.setDate(3, new java.sql.Date(date.getTime()));
                        insertStatement.executeUpdate();
                    }
                }
            }

            conn.commit();

            System.out.println("AI - 크롤링이 완료되었습니다. 실행 시간: " + new Date());
        } catch (SQLException | ParseException | IOException e) {
            e.printStackTrace();
        }
    }
}