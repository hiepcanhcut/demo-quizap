package com.example.quizapp.util;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Tiện ích định dạng thời gian.
 * Định dạng đếm ngược đồng hồ MM:SS và định dạng ngày tháng nộp bài.
 * 
 * Phụ trách: Đinh Đức Nghĩa (Module 3 - Exam Engine) & Lê Đăng Hiệp (Module 1 - History)
 */
public class TimeFormatter {

    /**
     * Định dạng số giây sang chuỗi "MM:SS" (ví dụ 125 giây -> "02:05").
     */
    public static String formatMMSS(long totalSeconds) {
        if (totalSeconds < 0) {
            totalSeconds = 0;
        }
        long minutes = totalSeconds / 60;
        long seconds = totalSeconds % 60;
        return String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds);
    }

    /**
     * Định dạng timestamp (mili-giây) sang chuỗi ngày giờ "dd/MM/yyyy HH:mm".
     */
    public static String formatDateTime(long timestampMs) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
        return sdf.format(new Date(timestampMs));
    }
}

