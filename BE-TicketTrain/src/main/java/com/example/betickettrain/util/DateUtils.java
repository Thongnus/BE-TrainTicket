package com.example.betickettrain.util;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Date;

public class DateUtils {

    // Region: Formatters
    public static final DateTimeFormatter DD_MM_YYYY = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    public static final DateTimeFormatter DD_MM_YYYY_HH_MM = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
    public static final DateTimeFormatter ISO_DATE_TIME = DateTimeFormatter.ISO_DATE_TIME;
    public static final DateTimeFormatter YYYYMMDDHHMMSS = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");


    // Region: Converters
    /**
     * Chuyển LocalDate sang Date
     */
    public static Date toDate(LocalDate localDate) {
        return Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
    }

    /**
     * Chuyển LocalDateTime sang Date
     */
    public static Date toDate(LocalDateTime localDateTime) {
        return Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant());
    }

    /**
     * Chuyển Date sang LocalDate
     */
    public static LocalDate toLocalDate(Date date) {
        return date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
    }

    /**
     * Chuyển Date sang LocalDateTime
     */
    public static LocalDateTime toLocalDateTime(Date date) {
        return date.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
    }

    /**
     * Chuyển Timestamp sang LocalDateTime
     */
    public static LocalDateTime toLocalDateTime(java.sql.Timestamp timestamp) {
        return timestamp.toLocalDateTime();
    }

    // Region: Date Operations
    /**
     * Lấy ngày hiện tại (không có giờ)
     */
    public static LocalDate today() {
        return LocalDate.now();
    }

    /**
     * Lấy thời gian hiện tại (có ngày và giờ)
     */
    public static LocalDateTime now() {
        return LocalDateTime.now();
    }

    /**
     * Thêm số ngày vào ngày cho trước
     */
    public static LocalDate addDays(LocalDate date, long days) {
        return date.plusDays(days);
    }

    /**
     * Trừ số ngày từ ngày cho trước
     */
    public static LocalDate minusDays(LocalDate date, long days) {
        return date.minusDays(days);
    }

    /**
     * Tính số ngày giữa 2 ngày
     */
    public static long daysBetween(LocalDate start, LocalDate end) {
        return ChronoUnit.DAYS.between(start, end);
    }

    // Region: Formatting

    /**
     * Định dạng LocalDate thành chuỗi
     */
    public static String format(LocalDate date) {
        return date.format(DD_MM_YYYY);
    }

    /**
     * Chuyển Date sang chuỗi với định dạng yyyyMMddHHmmss
     */
    public static String toString(Date date) {
        return date.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime().format(YYYYMMDDHHMMSS);
    }

    /**
     * Chuyển LocalDateTime sang chuỗi với định dạng yyyyMMddHHmmss
     */
    public static String toString(LocalDateTime localDateTime) {
        return localDateTime.format(YYYYMMDDHHMMSS);
    }

    /**
     * Chuyển LocalDate sang chuỗi với định dạng yyyyMMddHHmmss
     */
    public static String toString(LocalDate localDate) {
        return localDate.atStartOfDay().format(YYYYMMDDHHMMSS);
    }

    /**
     * Định dạng LocalDate với pattern tùy chỉnh
     */
    public static String format(LocalDate date, String pattern) {
        return date.format(DateTimeFormatter.ofPattern(pattern));
    }

    /**
     * Định dạng LocalDateTime thành chuỗi
     */
    public static String format(LocalDateTime dateTime) {
        return dateTime.format(DD_MM_YYYY_HH_MM);
    }

    /**
     * Định dạng LocalDateTime với pattern tùy chỉnh
     */
    public static String format(LocalDateTime dateTime, String pattern) {
        return dateTime.format(DateTimeFormatter.ofPattern(pattern));
    }

    // Region: Parsing
    /**
     * Chuyển chuỗi thành LocalDate
     */
    public static LocalDate parseLocalDate(String dateString) {
        return LocalDate.parse(dateString, DD_MM_YYYY);
    }

    /**
     * Chuyển chuỗi thành LocalDate với pattern tùy chỉnh
     */
    public static LocalDate parseLocalDate(String dateString, String pattern) {
        return LocalDate.parse(dateString, DateTimeFormatter.ofPattern(pattern));
    }

    /**
     * Chuyển chuỗi thành LocalDateTime
     */
    public static LocalDateTime parseLocalDateTime(String dateTimeString) {
        return LocalDateTime.parse(dateTimeString, DD_MM_YYYY_HH_MM);
    }

    /**
     * Chuyển chuỗi thành LocalDateTime với pattern tùy chỉnh
     */
    public static LocalDateTime parseLocalDateTime(String dateTimeString, String pattern) {
        return LocalDateTime.parse(dateTimeString, DateTimeFormatter.ofPattern(pattern));
    }

    // Region: Validation
    /**
     * Kiểm tra xem ngày có hợp lệ không
     */
    public static boolean isValidDate(String dateString) {
        try {
            LocalDate.parse(dateString, DD_MM_YYYY);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Kiểm tra xem ngày có nằm trong khoảng không
     */
    public static boolean isBetween(LocalDate date, LocalDate start, LocalDate end) {
        return !date.isBefore(start) && !date.isAfter(end);
    }

    // Region: Database helpers
    /**
     * Lấy LocalDateTime từ các field riêng biệt (dùng cho JPA Native Query)
     */
    public static LocalDateTime toLocalDateTime(int year, int month, int day, int hour, int minute) {
        return LocalDateTime.of(year, month, day, hour, minute);
    }
}