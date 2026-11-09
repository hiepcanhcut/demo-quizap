package com.example.quizapp;

import com.example.quizapp.logic.ExamEngine;
import com.example.quizapp.logic.ScoreGrader;
import com.example.quizapp.model.Question;
import com.example.quizapp.util.TimeFormatter;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Unit Test kiểm tra logic nghiệp vụ cốt lõi:
 * - ExamEngine (chấm điểm)
 * - ScoreGrader (xếp loại)
 * - TimeFormatter (định dạng thời gian)
 * 
 * Phụ trách: Đinh Đức Nghĩa & Lê Đăng Hiệp
 */
public class ExamEngineTest {

    @Test
    public void testExamGrading() {
        List<Question> questions = new ArrayList<>();
        Question q1 = new Question();
        q1.setId(1);
        q1.setContent("1 + 1 = ?");
        q1.setCorrectAnswer(0); // Đáp án A

        Question q2 = new Question();
        q2.setId(2);
        q2.setContent("Thủ đô của Việt Nam là gì?");
        q2.setCorrectAnswer(1); // Đáp án B

        questions.add(q1);
        questions.add(q2);

        // Trường hợp đúng cả 2 câu
        int[] answersAllCorrect = new int[]{0, 1};
        ExamEngine.GradingOutput output1 = ExamEngine.grade(questions, answersAllCorrect);
        assertEquals(2, output1.getTotalQuestions());
        assertEquals(2, output1.getCorrectCount());
        assertEquals(100.0, output1.getScorePercent(), 0.001);
        assertTrue(output1.getDetails().get(0).isCorrect());
        assertTrue(output1.getDetails().get(1).isCorrect());

        // Trường hợp đúng 1 câu, câu 2 chưa chọn (-1)
        int[] answersHalf = new int[]{0, -1};
        ExamEngine.GradingOutput output2 = ExamEngine.grade(questions, answersHalf);
        assertEquals(1, output2.getCorrectCount());
        assertEquals(50.0, output2.getScorePercent(), 0.001);
        assertTrue(output2.getDetails().get(0).isCorrect());
        assertFalse(output2.getDetails().get(1).isCorrect());
    }

    @Test
    public void testScoreGrader() {
        assertEquals(ScoreGrader.GRADE_EXCELLENT, ScoreGrader.grade(95.0));
        assertEquals(ScoreGrader.GRADE_GOOD, ScoreGrader.grade(75.0));
        assertEquals(ScoreGrader.GRADE_PASS, ScoreGrader.grade(50.0));
        assertEquals(ScoreGrader.GRADE_POOR, ScoreGrader.grade(30.0));
    }

    @Test
    public void testTimeFormatter() {
        assertEquals("00:00", TimeFormatter.formatMMSS(0));
        assertEquals("01:05", TimeFormatter.formatMMSS(65));
        assertEquals("15:00", TimeFormatter.formatMMSS(900));
    }
}

