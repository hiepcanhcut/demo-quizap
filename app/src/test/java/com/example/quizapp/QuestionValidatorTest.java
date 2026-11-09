package com.example.quizapp;

import com.example.quizapp.logic.QuestionValidator;
import com.example.quizapp.model.Question;

import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Unit Test kiểm tra tính hợp lệ của câu hỏi khi nhập form.
 * 
 * Phụ trách: Lê Đức Mạnh (Module 2)
 */
public class QuestionValidatorTest {

    @Test
    public void testValidQuestion() {
        Question q = new Question();
        q.setCategory("Toán học");
        q.setContent("1 + 1 bằng mấy?");
        q.setOptionA("2");
        q.setOptionB("3");
        q.setOptionC("4");
        q.setOptionD("5");
        q.setCorrectAnswer(0);

        QuestionValidator.ValidationResult result = QuestionValidator.validate(q);
        assertTrue(result.isValid());
    }

    @Test
    public void testInvalidEmptyContent() {
        Question q = new Question();
        q.setCategory("Toán học");
        q.setContent(""); // Rỗng
        q.setOptionA("2");
        q.setOptionB("3");
        q.setOptionC("4");
        q.setOptionD("5");
        q.setCorrectAnswer(0);

        QuestionValidator.ValidationResult result = QuestionValidator.validate(q);
        assertFalse(result.isValid());
    }

    @Test
    public void testInvalidCorrectAnswerIndex() {
        Question q = new Question();
        q.setCategory("Toán học");
        q.setContent("Câu hỏi mẫu?");
        q.setOptionA("A");
        q.setOptionB("B");
        q.setOptionC("C");
        q.setOptionD("D");
        q.setCorrectAnswer(5); // Sai ngoài khoảng 0..3

        QuestionValidator.ValidationResult result = QuestionValidator.validate(q);
        assertFalse(result.isValid());
    }
}

