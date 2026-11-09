package com.example.quizapp.model;

import java.io.Serializable;

/**
 * Domain Model: Kết quả chi tiết sau khi chấm một câu hỏi trong đề thi.
 * 
 * PHÂN CÔNG:
 * - Module: Module 3 - Trải nghiệm làm bài thi (Exam Engine)
 * - Phụ trách: Đinh Đức Nghĩa
 */
public class AnswerResult implements Serializable {

    private Question question;      // Câu hỏi
    private int selectedOption;     // Đáp án người dùng chọn (-1: bỏ trống, 0..3)
    private boolean isCorrect;      // true nếu trả lời đúng

    public AnswerResult() {
    }

    public AnswerResult(Question question, int selectedOption, boolean isCorrect) {
        this.question = question;
        this.selectedOption = selectedOption;
        this.isCorrect = isCorrect;
    }

    public Question getQuestion() { return question; }
    public void setQuestion(Question question) { this.question = question; }

    public int getSelectedOption() { return selectedOption; }
    public void setSelectedOption(int selectedOption) { this.selectedOption = selectedOption; }

    public boolean isCorrect() { return isCorrect; }
    public void setCorrect(boolean correct) { isCorrect = correct; }
}

