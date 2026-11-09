package com.example.quizapp.logic;

import com.example.quizapp.model.AnswerResult;
import com.example.quizapp.model.Question;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Domain Logic: Động cơ xử lý bài thi (chấm điểm, sinh đề, đối chiếu kết quả).
 * 
 * PHÂN CÔNG:
 * - Module: Module 3 - Trải nghiệm làm bài thi (Exam Engine)
 * - Phụ trách: Đinh Đức Nghĩa
 */
public class ExamEngine {

    public static class GradingOutput {
        private final int totalQuestions;
        private final int correctCount;
        private final double scorePercent;
        private final List<AnswerResult> details;

        public GradingOutput(int totalQuestions, int correctCount, double scorePercent, List<AnswerResult> details) {
            this.totalQuestions = totalQuestions;
            this.correctCount = correctCount;
            this.scorePercent = scorePercent;
            this.details = details;
        }

        public int getTotalQuestions() { return totalQuestions; }
        public int getCorrectCount() { return correctCount; }
        public double getScorePercent() { return scorePercent; }
        public List<AnswerResult> getDetails() { return details; }
    }

    /**
     * Chấm điểm bài thi bằng cách đối chiếu đáp án chọn với đáp án đúng của từng câu.
     */
    public static GradingOutput grade(List<Question> questions, int[] selectedAnswers) {
        if (questions == null || questions.isEmpty()) {
            return new GradingOutput(0, 0, 0.0, Collections.emptyList());
        }

        int total = questions.size();
        int correct = 0;
        List<AnswerResult> details = new ArrayList<>();

        for (int i = 0; i < total; i++) {
            Question q = questions.get(i);
            int selected = (selectedAnswers != null && i < selectedAnswers.length) ? selectedAnswers[i] : -1;
            boolean isCorrect = (selected != -1 && selected == q.getCorrectAnswer());
            if (isCorrect) {
                correct++;
            }
            details.add(new AnswerResult(q, selected, isCorrect));
        }

        double percent = ((double) correct / total) * 100.0;
        return new GradingOutput(total, correct, percent, details);
    }
}

