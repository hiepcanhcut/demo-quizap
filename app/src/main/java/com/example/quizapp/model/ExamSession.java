package com.example.quizapp.model;

import java.io.Serializable;
import java.util.Arrays;

/**
 * Domain Model: Quản lý trạng thái của phiên làm bài thi.
 * Được giữ trong Bundle / savedInstanceState để chống mất dữ liệu khi xoay màn hình
 * hoặc khi hệ thống kill app nền.
 * 
 * PHÂN CÔNG:
 * - Module: Module 3 - Trải nghiệm làm bài thi (Exam Engine)
 * - Phụ trách: Đinh Đức Nghĩa
 */
public class ExamSession implements Serializable {

    private long[] questionIds;          // Danh sách ID câu hỏi theo thứ tự làm bài
    private int[] selectedAnswers;       // Đáp án người dùng chọn cho từng câu (-1 nếu chưa chọn, 0..3)
    private boolean[] flaggedQuestions;  // Trạng thái gắn cờ của từng câu
    private int currentIndex;            // Chỉ số câu hỏi hiện tại đang xem (0..N-1)
    private long startElapsedRealtimeMs; // Mốc thời gian bắt đầu (SystemClock.elapsedRealtime)
    private int totalTimeSeconds;        // Tổng thời gian làm bài (giây)

    public ExamSession() {
    }

    public ExamSession(long[] questionIds, int totalTimeSeconds, long startElapsedRealtimeMs) {
        this.questionIds = questionIds;
        int size = questionIds != null ? questionIds.length : 0;
        this.selectedAnswers = new int[size];
        Arrays.fill(this.selectedAnswers, -1);
        this.flaggedQuestions = new boolean[size];
        this.currentIndex = 0;
        this.totalTimeSeconds = totalTimeSeconds;
        this.startElapsedRealtimeMs = startElapsedRealtimeMs;
    }

    // Getters and Setters
    public long[] getQuestionIds() { return questionIds; }
    public void setQuestionIds(long[] questionIds) { this.questionIds = questionIds; }

    public int[] getSelectedAnswers() { return selectedAnswers; }
    public void setSelectedAnswers(int[] selectedAnswers) { this.selectedAnswers = selectedAnswers; }

    public boolean[] getFlaggedQuestions() { return flaggedQuestions; }
    public void setFlaggedQuestions(boolean[] flaggedQuestions) { this.flaggedQuestions = flaggedQuestions; }

    public int getCurrentIndex() { return currentIndex; }
    public void setCurrentIndex(int currentIndex) { this.currentIndex = currentIndex; }

    public long getStartElapsedRealtimeMs() { return startElapsedRealtimeMs; }
    public void setStartElapsedRealtimeMs(long startElapsedRealtimeMs) { this.startElapsedRealtimeMs = startElapsedRealtimeMs; }

    public int getTotalTimeSeconds() { return totalTimeSeconds; }
    public void setTotalTimeSeconds(int totalTimeSeconds) { this.totalTimeSeconds = totalTimeSeconds; }

    public int getQuestionCount() {
        return questionIds != null ? questionIds.length : 0;
    }

    public void selectAnswer(int questionIndex, int optionIndex) {
        if (selectedAnswers != null && questionIndex >= 0 && questionIndex < selectedAnswers.length) {
            selectedAnswers[questionIndex] = optionIndex;
        }
    }

    public void toggleFlag(int questionIndex) {
        if (flaggedQuestions != null && questionIndex >= 0 && questionIndex < flaggedQuestions.length) {
            flaggedQuestions[questionIndex] = !flaggedQuestions[questionIndex];
        }
    }

    public int countAnsweredQuestions() {
        if (selectedAnswers == null) return 0;
        int count = 0;
        for (int ans : selectedAnswers) {
            if (ans != -1) count++;
        }
        return count;
    }
}

