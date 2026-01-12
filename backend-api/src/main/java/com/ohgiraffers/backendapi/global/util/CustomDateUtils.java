package com.ohgiraffers.backendapi.global.util;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

public class CustomDateUtils {

    // 인스턴스화 방지 (유틸리티 클래스 관례)
    private CustomDateUtils() {}

    // 1. 기본 포맷 (yyyy-MM-dd)
    public static String formatLocalDate(LocalDateTime dateTime) {
        if (dateTime == null) return null;
        return dateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
    }

    // 2. 상세 포맷 (yyyy-MM-dd HH:mm:ss)
    public static String formatLocalDateTime(LocalDateTime dateTime) {
        if (dateTime == null) return null;
        return dateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }

    // 3. 상대적 시간 표시 (방금 전, n분 전, n일 전 등)
    public static String toRelativeTime(LocalDateTime dateTime) {
        if (dateTime == null) return null;

        LocalDateTime now = LocalDateTime.now();
        long seconds = ChronoUnit.SECONDS.between(dateTime, now);
        long minutes = ChronoUnit.MINUTES.between(dateTime, now);
        long hours = ChronoUnit.HOURS.between(dateTime, now);
        long days = ChronoUnit.DAYS.between(dateTime, now);

        if (seconds < 60) {
            return "방금 전";
        } else if (minutes < 60) {
            return minutes + "분 전";
        } else if (hours < 24) {
            return hours + "시간 전";
        } else if (days < 7) {
            return days + "일 전";
        } else {
            // 일주일이 지나면 날짜를 그대로 표시
            return formatLocalDate(dateTime);
        }
    }
}
