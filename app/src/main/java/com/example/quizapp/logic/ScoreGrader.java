package com.example.quizapp.logic;

/**
 * Domain Logic: Xếp loại kết quả bài thi dựa trên tỷ lệ % câu trả lời đúng.
 * 
 * PHÂN CÔNG:
 * - Module: Module 3 - Trải nghiệm làm bài thi (Exam Engine)
 * - Phụ trách: Đinh Đức Nghĩa
 */
public class ScoreGrader {

    public static final String GRADE_EXCELLENT = "Xuất sắc";
    public static final String GRADE_GOOD = "Khá tốt";
    public static final String GRADE_PASS = "Đạt yêu cầu";
    public static final String GRADE_POOR = "Cần cố gắng";

    /**
     * Trả về danh hiệu xếp loại từ tỷ lệ phần trăm (0..100)
     */
    public static String grade(double scorePercent) {
        if (scorePercent >= 90.0) {
            return GRADE_EXCELLENT;
        } else if (scorePercent >= 70.0) {
            return GRADE_GOOD;
        } else if (scorePercent >= 50.0) {
            return GRADE_PASS;
        } else {
            return GRADE_POOR;
        }
    }
}

