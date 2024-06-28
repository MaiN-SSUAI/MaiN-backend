package com.example.MaiN.crawler;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import java.io.IOException;
import java.sql.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class FunsysNotiCrawler implements Job {

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        String baseUrl = "https://fun.ssu.ac.kr/ko/program/all/list/all/";
        int page = 1;

        try (Connection conn = DriverManager.getConnection(
                "jdbc:mysql://database-1.cjgkeo6ugyuv.ap-northeast-2.rds.amazonaws.com:3306/main_db",
                "admin", "wodudtnalsduswowlghks1228")) {
            conn.setAutoCommit(false);

            while (true) {
                Document doc = Jsoup.connect(baseUrl + page).get();
                Elements data = doc.select("li");

                PreparedStatement countBeforeStatement = conn.prepareStatement("SELECT COUNT(*) FROM funsys_noti");
                ResultSet countBeforeResult = countBeforeStatement.executeQuery();
                countBeforeResult.next();
                int countBefore = countBeforeResult.getInt(1);

                for (Element item : data) {
                    Element titleLink = item.selectFirst("a");
                    if (titleLink != null) {
                        Element titleTag = titleLink.selectFirst("b.title");
                        if (titleTag != null) {
                            String title = titleTag.text();
                            String link = "https://fun.ssu.ac.kr/" + titleLink.attr("href");

                            Elements times = item.select("time");
                            if (!times.isEmpty()) {
                                String startDateStr = times.get(0).attr("datetime").split("T")[0];
                                String endDateStr = times.get(1).attr("datetime").split("T")[0];

                                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                                Date startDate = dateFormat.parse(startDateStr);
                                Date endDate = dateFormat.parse(endDateStr);

                                try (PreparedStatement countStatement = conn.prepareStatement(
                                        "SELECT COUNT(*) FROM funsys_noti WHERE title = ? AND start_date = ?")) {
                                    countStatement.setString(1, title);
                                    countStatement.setDate(2, new java.sql.Date(startDate.getTime()));
                                    ResultSet countResult = countStatement.executeQuery();
                                    countResult.next();
                                    if (countResult.getInt(1) > 0) {
                                        continue;
                                    }

                                    try (PreparedStatement insertStatement = conn.prepareStatement(
                                            "INSERT INTO funsys_noti(title, link, start_date, end_date) VALUES (?, ?, ?, ?)")) {
                                        insertStatement.setString(1, title);
                                        insertStatement.setString(2, link);
                                        insertStatement.setDate(3, new java.sql.Date(startDate.getTime()));
                                        insertStatement.setDate(4, new java.sql.Date(endDate.getTime()));
                                        insertStatement.executeUpdate();
                                    }
                                }
                            }
                        }
                    }
                }

                PreparedStatement countAfterStatement = conn.prepareStatement("SELECT COUNT(*) FROM funsys_noti");
                ResultSet countAfterResult = countAfterStatement.executeQuery();
                countAfterResult.next();
                int countAfter = countAfterResult.getInt(1);

                conn.commit();

                if (countBefore == countAfter) {
                    break;
                }

                page++;
            }

            System.out.println("FUNSYS  - 크롤링이 완료되었습니다. 실행 시간: " + new Date());

        } catch (SQLException | ParseException | IOException e) {
            e.printStackTrace();
        }
    }
}
