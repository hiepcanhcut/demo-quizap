package com.example.quizapp.model;

import java.io.Serializable;

/**
 * Domain Model: Bản ghi lịch sử của một lần làm bài thi trắc nghiệm.
 * 
 * PHÂN CÔNG:
 * - Module: Module 1 - Quản lý Người dùng & Thống kê học tập (User & Analytics)
 * - Phụ trách: Lê Đăng Hiệp
 */
public class ExamHistory implements Serializable {

    private long id;
    private String category;            // Môn thi đã làm
    private int totalQuestions;         // Tổng số câu trong đề thi
    private int correctCount;           // Số câu trả lời đúng
    private double scorePercent;        // Tỷ lệ phần trăm đúng (0 - 100%)
    private int durationSeconds;        // Thời gian hoàn thành (giây)
    private long takenAt;               // Timestamp thời điểm nộp bài

    public ExamHistory() {
    }

    public ExamHistory(long id, String category, int totalQuestions,
                       int correctCount, double scorePercent,
                       int durationSeconds, long takenAt) {
        this.id = id;
        this.category = category;
        this.totalQuestions = totalQuestions;
        this.correctCount = correctCount;
        this.scorePercent = scorePercent;
        this.durationSeconds = durationSeconds;
        this.takenAt = takenAt;
    }

    // Getters and Setters
    public long getId() { return id; }
    public void setId(long id) { this.id = id; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public int getTotalQuestions() { return totalQuestions; }
    public void setTotalQuestions(int totalQuestions) { this.totalQuestions = totalQuestions; }

    public int getCorrectCount() { return correctCount; }
    public void setCorrectCount(int correctCount) { this.correctCount = correctCount; }

    public double getScorePercent() { return scorePercent; }
    public void setScorePercent(double scorePercent) { this.scorePercent = scorePercent; }

    public int getDurationSeconds() { return durationSeconds; }
    public void setDurationSeconds(int durationSeconds) { this.durationSeconds = durationSeconds; }

    public long getTakenAt() { return takenAt; }
    public void setTakenAt(long takenAt) { this.takenAt = takenAt; }
}

