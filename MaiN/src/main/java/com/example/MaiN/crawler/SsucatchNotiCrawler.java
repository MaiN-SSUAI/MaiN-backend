package com.example.MaiN.crawler;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import java.io.IOException;
import java.sql.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;

public class SsucatchNotiCrawler implements Job {

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        JobDataMap dataMap = context.getJobDetail().getJobDataMap();
        String dbUrl = dataMap.getString("dbUrl");
        String dbUsername = dataMap.getString("dbUsername");
        String dbPassword = dataMap.getString("dbPassword");

        String baseUrl = "https://scatch.ssu.ac.kr/%EA%B3%B5%EC%A7%80%EC%82%AC%ED%95%AD/page/";
        int page = 1;

        try (Connection conn = DriverManager.getConnection(dbUrl, dbUsername, dbPassword)) {
            conn.setAutoCommit(false);

            while (true) {
                String url = baseUrl + page;
                Document doc = Jsoup.connect(url).get();
                Elements data = doc.select("div.row.no-gutters.align-items-center");

                int countBefore = getCount(conn,"ssucatch_noti");
                System.out.println("SSU Catch - 크롤링 전 데이터 개수: " + countBefore);

                for (Element item : data) {
                    Element titleLink = item.selectFirst("a");
                    if (titleLink != null) {
                        String title = titleLink.select("span.d-inline-blcok.m-pt-5").text().trim();
                        String link = titleLink.attr("href");
                        String dateStr = item.selectFirst("div.h2.text-info.font-weight-bold").text();
                        String category = item.selectFirst("span.label.d-inline-blcok.border.pl-3.pr-3.mr-2").text().trim();
                        String progress = item.selectFirst("div.notice_col2") != null ? item.selectFirst("div.notice_col2").text() : null;

                        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy.MM.dd");
                        java.util.Date parsedDate = dateFormat.parse(dateStr);
                        java.sql.Date sqlDate = new java.sql.Date(parsedDate.getTime());

                        if (!isNotiExists(conn, title, sqlDate)) {
                            insertNoti(conn, title, link, sqlDate, category, progress);
                        } else {
                            updateProgress(conn, title, progress);
                        }
                    }
                }

                int countAfter = getCount(conn, "ssucatch_noti");
                if (countBefore == countAfter) {
                    break;
                }
                page++;
            }

            int countAfter = getCount(conn, "ssucatch_noti");
            System.out.println("SSU Catch - 크롤링 후 데이터 개수: " + countAfter);

            conn.commit();
            System.out.println("SSU Catch - 크롤링이 완료되었습니다. 완료 시간: \" + new Date()");
        } catch (SQLException | IOException | ParseException e) {
            System.out.println("SSU Catch - 크롤링 중 오류 발생: " + e.getMessage());
        }
    }
    private boolean isNotiExists(Connection conn, String title, Date date) throws SQLException {
        try (PreparedStatement statement = conn.prepareStatement("SELECT COUNT(*) FROM ssucatch_noti WHERE title = ? AND date = ?")) {
            statement.setString(1, title);
            statement.setDate(2, new java.sql.Date(date.getTime()));
            ResultSet resultSet = statement.executeQuery();
            resultSet.next();
            return resultSet.getInt(1) > 0;
        }
    }
    private void updateProgress(Connection conn, String title, String progress) throws SQLException {
        try (PreparedStatement statement = conn.prepareStatement("UPDATE ssucatch_noti SET progress = ? WHERE title = ?")) {
            statement.setString(1, progress);
            statement.setString(2, title);
            statement.executeUpdate();
        }
    }
    private void insertNoti(Connection conn, String title, String link, Date date, String category, String progress) throws SQLException {
        try (PreparedStatement statement = conn.prepareStatement(
                "INSERT INTO ssucatch_noti(title, link, date, category, progress) VALUES (?, ?, ?, ?, ?)")) {
            statement.setString(1, title);
            statement.setString(2, link);
            statement.setDate(3, new java.sql.Date(date.getTime()));
            statement.setString(4, category);
            statement.setString(5, progress);
            statement.executeUpdate();
        }
    }
    private int getCount(Connection conn, String tableName) throws SQLException {
        try (Statement statement = conn.createStatement()) {
            ResultSet resultSet = statement.executeQuery("SELECT COUNT(*) FROM " + tableName);
            resultSet.next();
            return resultSet.getInt(1);
        }
    }

}
