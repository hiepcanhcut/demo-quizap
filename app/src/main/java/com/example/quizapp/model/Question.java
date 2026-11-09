package com.example.quizapp.model;

import java.io.Serializable;

/**
 * Domain Model: Đại diện cho một câu hỏi trắc nghiệm trong ngân hàng câu hỏi.
 * 
 * PHÂN CÔNG:
 * - Module: Module 2 - Quản lý Ngân hàng câu hỏi & Đề thi
 * - Phụ trách: Lê Đức Mạnh
 */
public class Question implements Serializable {

    private long id;
    private String content;         // Nội dung câu hỏi
    private String category;        // Môn học / chuyên đề (Toán, Lịch sử, Khoa học...)
    private String optionA;         // Lựa chọn A
    private String optionB;         // Lựa chọn B
    private String optionC;         // Lựa chọn C
    private String optionD;         // Lựa chọn D
    private int correctAnswer;      // 0: A, 1: B, 2: C, 3: D
    private String explanation;     // Lời giải thích chi tiết
    private boolean isSample;       // true nếu là câu hỏi mẫu mặc định của hệ thống
    private long createdAt;         // Timestamp tạo
    private long updatedAt;         // Timestamp cập nhật gần nhất

    public Question() {
    }

    public Question(long id, String content, String category,
                    String optionA, String optionB, String optionC, String optionD,
                    int correctAnswer, String explanation, boolean isSample,
                    long createdAt, long updatedAt) {
        this.id = id;
        this.content = content;
        this.category = category;
        this.optionA = optionA;
        this.optionB = optionB;
        this.optionC = optionC;
        this.optionD = optionD;
        this.correctAnswer = correctAnswer;
        this.explanation = explanation;
        this.isSample = isSample;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    // Getters and Setters
    public long getId() { return id; }
    public void setId(long id) { this.id = id; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public String getOptionA() { return optionA; }
    public void setOptionA(String optionA) { this.optionA = optionA; }

    public String getOptionB() { return optionB; }
    public void setOptionB(String optionB) { this.optionB = optionB; }

    public String getOptionC() { return optionC; }
    public void setOptionC(String optionC) { this.optionC = optionC; }

    public String getOptionD() { return optionD; }
    public void setOptionD(String optionD) { this.optionD = optionD; }

    public int getCorrectAnswer() { return correctAnswer; }
    public void setCorrectAnswer(int correctAnswer) { this.correctAnswer = correctAnswer; }

    public String getExplanation() { return explanation; }
    public void setExplanation(String explanation) { this.explanation = explanation; }

    public boolean isSample() { return isSample; }
    public void setSample(boolean sample) { isSample = sample; }

    public long getCreatedAt() { return createdAt; }
    public void setCreatedAt(long createdAt) { this.createdAt = createdAt; }

    public long getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(long updatedAt) { this.updatedAt = updatedAt; }

    /**
     * Lấy chuỗi hiển thị của đáp án theo chỉ số (0 -> A, 1 -> B, ...)
     */
    public String getOptionText(int optionIndex) {
        switch (optionIndex) {
            case 0: return optionA;
            case 1: return optionB;
            case 2: return optionC;
            case 3: return optionD;
            default: return "";
        }
    }
}

