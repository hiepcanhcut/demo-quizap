package com.example.quizapp.ui.exam;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.SystemClock;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.quizapp.R;
import com.example.quizapp.data.HistoryRepository;
import com.example.quizapp.data.QuestionRepository;
import com.example.quizapp.logic.ExamEngine;
import com.example.quizapp.model.ExamHistory;
import com.example.quizapp.model.ExamSession;
import com.example.quizapp.model.Question;
import com.example.quizapp.util.AppExecutors;
import com.example.quizapp.util.Constants;
import com.example.quizapp.util.TimeFormatter;

import java.util.ArrayList;
import java.util.List;

/**
 * Màn hình làm bài thi trắc nghiệm (Core Exam Engine):
 * - Đếm ngược thời gian chính xác, tự nộp bài khi hết giờ
 * - Chuyển câu hỏi Trước / Sau, chọn đáp án
 * - Gắn cờ câu hỏi khó, lưới câu hỏi xem nhanh
 * - Giữ trạng thái nguyên vẹn khi xoay màn hình qua Bundle/ExamSession
 * - Chấm điểm và lưu kết quả vào CSDL
 * 
 * PHÂN CÔNG:
 * - Module: Module 3 - Trải nghiệm làm bài thi (Exam Engine)
 * - Phụ trách: Đinh Đức Nghĩa
 */
public class ExamActivity extends AppCompatActivity {

    private static final String KEY_EXAM_SESSION = "key_exam_session";

    private TextView tvQuestionProgress;
    private TextView tvTimer;
    private Button btnFlag;
    private ProgressBar progressBar;
    private TextView tvQuestionContent;
    private TextView tvOptionA, tvOptionB, tvOptionC, tvOptionD;
    private Button btnPrevious, btnGrid, btnNext;

    private ExamSession examSession;
    private List<Question> questionList = new ArrayList<>();
    private QuestionRepository questionRepository;
    private HistoryRepository historyRepository;

    private final Handler timerHandler = new Handler(Looper.getMainLooper());
    private Runnable timerRunnable;
    private boolean isSubmitted = false;
    private String examCategory = "Tổng hợp";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exam);

        questionRepository = new QuestionRepository(this);
        historyRepository = new HistoryRepository(this);

        initViews();
        setupListeners();

        if (savedInstanceState != null && savedInstanceState.containsKey(KEY_EXAM_SESSION)) {
            // Khôi phục trạng thái khi xoay màn hình
            examSession = (ExamSession) savedInstanceState.getSerializable(KEY_EXAM_SESSION);
            loadQuestionsAndDisplay();
        } else {
            // Khởi tạo phiên thi mới từ Intent
            long[] questionIds = getIntent().getLongArrayExtra(Constants.EXTRA_QUESTION_IDS);
            if (getIntent().hasExtra(Constants.EXTRA_CATEGORY)) {
                examCategory = getIntent().getStringExtra(Constants.EXTRA_CATEGORY);
            }

            if (questionIds == null || questionIds.length == 0) {
                Toast.makeText(this, "Không có câu hỏi để bắt đầu!", Toast.LENGTH_SHORT).show();
                finish();
                return;
            }

            int totalSeconds = questionIds.length * Constants.DEFAULT_SECONDS_PER_QUESTION;
            examSession = new ExamSession(questionIds, totalSeconds, SystemClock.elapsedRealtime());
            loadQuestionsAndDisplay();
        }

        startTimer();
    }

    private void initViews() {
        tvQuestionProgress = findViewById(R.id.tvQuestionProgress);
        tvTimer = findViewById(R.id.tvTimer);
        btnFlag = findViewById(R.id.btnFlag);
        progressBar = findViewById(R.id.progressBar);
        tvQuestionContent = findViewById(R.id.tvQuestionContent);
        tvOptionA = findViewById(R.id.tvOptionA);
        tvOptionB = findViewById(R.id.tvOptionB);
        tvOptionC = findViewById(R.id.tvOptionC);
        tvOptionD = findViewById(R.id.tvOptionD);
        btnPrevious = findViewById(R.id.btnPrevious);
        btnGrid = findViewById(R.id.btnGrid);
        btnNext = findViewById(R.id.btnNext);
    }

    private void loadQuestionsAndDisplay() {
        AppExecutors.getInstance().diskIO().execute(() -> {
            questionList = questionRepository.getByIds(examSession.getQuestionIds());
            AppExecutors.getInstance().mainThread().execute(() -> {
                progressBar.setMax(examSession.getQuestionCount());
                displayQuestion(examSession.getCurrentIndex());
            });
        });
    }

    private void displayQuestion(int index) {
        if (questionList.isEmpty() || index < 0 || index >= questionList.size()) return;

        examSession.setCurrentIndex(index);
        Question q = questionList.get(index);

        tvQuestionProgress.setText(String.format("Câu %d / %d", index + 1, questionList.size()));
        progressBar.setProgress(index + 1);

        tvQuestionContent.setText(q.getContent());
        tvOptionA.setText("A. " + q.getOptionA());
        tvOptionB.setText("B. " + q.getOptionB());
        tvOptionC.setText("C. " + q.getOptionC());
        tvOptionD.setText("D. " + q.getOptionD());

        // Cập nhật trạng thái gắn cờ
        boolean isFlagged = examSession.getFlaggedQuestions()[index];
        btnFlag.setText(isFlagged ? "Bỏ cờ" : "Gắn cờ");

        // Đánh dấu đáp án đã chọn
        highlightSelectedOption(examSession.getSelectedAnswers()[index]);

        // Trạng thái các nút điều hướng
        btnPrevious.setEnabled(index > 0);
        btnNext.setText(index == questionList.size() - 1 ? "Nộp bài" : "Câu sau");
    }

    private void highlightSelectedOption(int optionIndex) {
        resetOptionBackgrounds();
        switch (optionIndex) {
            case 0: tvOptionA.setBackgroundResource(R.drawable.bg_option_selected); break;
            case 1: tvOptionB.setBackgroundResource(R.drawable.bg_option_selected); break;
            case 2: tvOptionC.setBackgroundResource(R.drawable.bg_option_selected); break;
            case 3: tvOptionD.setBackgroundResource(R.drawable.bg_option_selected); break;
        }
    }

    private void resetOptionBackgrounds() {
        tvOptionA.setBackgroundResource(R.drawable.bg_option_default);
        tvOptionB.setBackgroundResource(R.drawable.bg_option_default);
        tvOptionC.setBackgroundResource(R.drawable.bg_option_default);
        tvOptionD.setBackgroundResource(R.drawable.bg_option_default);
    }

    private void setupListeners() {
        tvOptionA.setOnClickListener(v -> selectOption(0));
        tvOptionB.setOnClickListener(v -> selectOption(1));
        tvOptionC.setOnClickListener(v -> selectOption(2));
        tvOptionD.setOnClickListener(v -> selectOption(3));

        btnPrevious.setOnClickListener(v -> {
            if (examSession.getCurrentIndex() > 0) {
                displayQuestion(examSession.getCurrentIndex() - 1);
            }
        });

        btnNext.setOnClickListener(v -> {
            if (examSession.getCurrentIndex() < questionList.size() - 1) {
                displayQuestion(examSession.getCurrentIndex() + 1);
            } else {
                confirmSubmitExam();
            }
        });

        btnFlag.setOnClickListener(v -> {
            examSession.toggleFlag(examSession.getCurrentIndex());
            btnFlag.setText(examSession.getFlaggedQuestions()[examSession.getCurrentIndex()] ? "Bỏ cờ" : "Gắn cờ");
        });

        btnGrid.setOnClickListener(v -> showQuestionGridDialog());
    }

    private void selectOption(int optionIndex) {
        examSession.selectAnswer(examSession.getCurrentIndex(), optionIndex);
        highlightSelectedOption(optionIndex);
    }

    private void showQuestionGridDialog() {
        QuestionGridDialog dialog = new QuestionGridDialog(this, examSession, new QuestionGridDialog.OnGridItemClickListener() {
            @Override
            public void onQuestionSelected(int index) {
                displayQuestion(index);
            }

            @Override
            public void onSubmitRequested() {
                confirmSubmitExam();
            }
        });
        dialog.show();
    }

    private void startTimer() {
        timerRunnable = new Runnable() {
            @Override
            public void run() {
                long elapsedSeconds = (SystemClock.elapsedRealtime() - examSession.getStartElapsedRealtimeMs()) / 1000;
                long remainingSeconds = examSession.getTotalTimeSeconds() - elapsedSeconds;

                if (remainingSeconds <= 0) {
                    tvTimer.setText("00:00");
                    Toast.makeText(ExamActivity.this, "Hết giờ làm bài!", Toast.LENGTH_SHORT).show();
                    submitExam();
                } else {
                    tvTimer.setText(TimeFormatter.formatMMSS(remainingSeconds));
                    timerHandler.postDelayed(this, 1000);
                }
            }
        };
        timerHandler.post(timerRunnable);
    }

    private void confirmSubmitExam() {
        int unanswered = examSession.getQuestionCount() - examSession.countAnsweredQuestions();
        String message = unanswered > 0
                ? "Bạn còn " + unanswered + " câu chưa trả lời. Bạn có chắc chắn muốn nộp bài?"
                : "Bạn có chắc chắn muốn nộp bài thi?";

        new AlertDialog.Builder(this)
                .setTitle("Nộp bài thi")
                .setMessage(message)
                .setPositiveButton("Nộp ngay", (dialog, which) -> submitExam())
                .setNegativeButton("Làm tiếp", null)
                .show();
    }

    private void submitExam() {
        if (isSubmitted) return;
        isSubmitted = true;
        timerHandler.removeCallbacks(timerRunnable);

        long elapsedSeconds = (SystemClock.elapsedRealtime() - examSession.getStartElapsedRealtimeMs()) / 1000;
        int durationSeconds = (int) Math.min(elapsedSeconds, examSession.getTotalTimeSeconds());

        // Chấm điểm qua ExamEngine
        ExamEngine.GradingOutput output = ExamEngine.grade(questionList, examSession.getSelectedAnswers());

        // Lưu vào CSDL lịch sử thi
        ExamHistory history = new ExamHistory(
                0,
                examCategory,
                output.getTotalQuestions(),
                output.getCorrectCount(),
                output.getScorePercent(),
                durationSeconds,
                System.currentTimeMillis()
        );

        AppExecutors.getInstance().diskIO().execute(() -> {
            historyRepository.insert(history);
            AppExecutors.getInstance().mainThread().execute(() -> {
                Intent intent = new Intent(ExamActivity.this, ResultActivity.class);
                intent.putExtra(Constants.EXTRA_EXAM_RESULT, new ArrayList<>(output.getDetails()));
                intent.putExtra("score_percent", output.getScorePercent());
                intent.putExtra("correct_count", output.getCorrectCount());
                intent.putExtra("total_count", output.getTotalQuestions());
                intent.putExtra(Constants.EXTRA_CATEGORY, examCategory);
                startActivity(intent);
                finish();
            });
        });
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable(KEY_EXAM_SESSION, examSession);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        timerHandler.removeCallbacks(timerRunnable);
    }

    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(this)
                .setTitle("Thoát bài thi")
                .setMessage("Bài thi chưa được nộp. Bạn có chắc chắn muốn thoát về menu chính?")
                .setPositiveButton("Thoát", (dialog, which) -> super.onBackPressed())
                .setNegativeButton("Ở lại", null)
                .show();
    }
}

