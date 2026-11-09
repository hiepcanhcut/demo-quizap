package com.example.quizapp.logic;

import com.example.quizapp.model.Question;

/**
 * Domain Logic: Kiểm tra tính hợp lệ của câu hỏi khi người dùng nhập trên form.
 * 
 * PHÂN CÔNG:
 * - Module: Module 2 - Quản lý Ngân hàng câu hỏi & Đề thi
 * - Phụ trách: Lê Đức Mạnh
 */
public class QuestionValidator {

    public static class ValidationResult {
        private final boolean isValid;
        private final String errorMessage;

        public ValidationResult(boolean isValid, String errorMessage) {
            this.isValid = isValid;
            this.errorMessage = errorMessage;
        }

        public boolean isValid() { return isValid; }
        public String getErrorMessage() { return errorMessage; }
    }

    /**
     * Kiểm tra form câu hỏi có đầy đủ dữ liệu không.
     * Quy tắc:
     * - Nội dung câu hỏi không rỗng
     * - Môn học không rỗng
     * - 4 đáp án A, B, C, D không rỗng
     * - Đáp án đúng phải nằm trong khoảng [0, 3]
     */
    public static ValidationResult validate(Question question) {
        if (question == null) {
            return new ValidationResult(false, "Dữ liệu câu hỏi không tồn tại.");
        }
        if (question.getCategory() == null || question.getCategory().trim().isEmpty()) {
            return new ValidationResult(false, "Vui lòng nhập môn học / chuyên đề.");
        }
        if (question.getContent() == null || question.getContent().trim().isEmpty()) {
            return new ValidationResult(false, "Vui lòng nhập nội dung câu hỏi.");
        }
        if (question.getOptionA() == null || question.getOptionA().trim().isEmpty()) {
            return new ValidationResult(false, "Vui lòng nhập đáp án A.");
        }
        if (question.getOptionB() == null || question.getOptionB().trim().isEmpty()) {
            return new ValidationResult(false, "Vui lòng nhập đáp án B.");
        }
        if (question.getOptionC() == null || question.getOptionC().trim().isEmpty()) {
            return new ValidationResult(false, "Vui lòng nhập đáp án C.");
        }
        if (question.getOptionD() == null || question.getOptionD().trim().isEmpty()) {
            return new ValidationResult(false, "Vui lòng nhập đáp án D.");
        }
        if (question.getCorrectAnswer() < 0 || question.getCorrectAnswer() > 3) {
            return new ValidationResult(false, "Đáp án đúng phải là một trong 4 phương án A, B, C, D.");
        }
        return new ValidationResult(true, null);
    }
}

