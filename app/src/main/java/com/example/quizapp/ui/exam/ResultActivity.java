package com.example.quizapp.ui.exam;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.quizapp.R;
import com.example.quizapp.logic.ScoreGrader;
import com.example.quizapp.model.AnswerResult;
import com.example.quizapp.ui.MainActivity;
import com.example.quizapp.util.Constants;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Màn hình Kết quả bài thi:
 * - Hiển thị tỷ lệ điểm %, số câu đúng / tổng số câu
 * - Đánh giá xếp loại năng lực (ScoreGrader: Xuất sắc, Khá tốt, Đạt yêu cầu, Cần cố gắng)
 * - Hiển thị chi tiết từng câu: đáp án đã chọn (xanh/đỏ), đáp án đúng và lời giải thích chi tiết
 * 
 * PHÂN CÔNG:
 * - Module: Module 3 - Trải nghiệm làm bài thi (Exam Engine)
 * - Phụ trách: Đinh Đức Nghĩa
 */
public class ResultActivity extends AppCompatActivity {

    private TextView tvScorePercent;
    private TextView tvScoreFraction;
    private TextView tvGrade;
    private RecyclerView rvResultDetails;
    private Button btnRetry;
    private Button btnBackHome;

    private ResultDetailAdapter adapter;
    private String category = "Tổng hợp";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

        setTitle("Kết quả bài thi");

        initViews();
        displayResults();
        setupListeners();
    }

    private void initViews() {
        tvScorePercent = findViewById(R.id.tvScorePercent);
        tvScoreFraction = findViewById(R.id.tvScoreFraction);
        tvGrade = findViewById(R.id.tvGrade);
        rvResultDetails = findViewById(R.id.rvResultDetails);
        btnRetry = findViewById(R.id.btnRetry);
        btnBackHome = findViewById(R.id.btnBackHome);

        adapter = new ResultDetailAdapter();
        rvResultDetails.setLayoutManager(new LinearLayoutManager(this));
        rvResultDetails.setAdapter(adapter);
    }

    private void displayResults() {
        double scorePercent = getIntent().getDoubleExtra("score_percent", 0.0);
        int correctCount = getIntent().getIntExtra("correct_count", 0);
        int totalCount = getIntent().getIntExtra("total_count", 0);
        category = getIntent().getStringExtra(Constants.EXTRA_CATEGORY);

        @SuppressWarnings("unchecked")
        List<AnswerResult> details = (List<AnswerResult>) getIntent().getSerializableExtra(Constants.EXTRA_EXAM_RESULT);

        tvScorePercent.setText(String.format(Locale.getDefault(), "%.0f%%", scorePercent));
        tvScoreFraction.setText(String.format(Locale.getDefault(), "Đúng: %d / %d câu", correctCount, totalCount));

        String grade = ScoreGrader.grade(scorePercent);
        tvGrade.setText("Xếp loại: " + grade);

        if (details != null) {
            adapter.setResults(details);
        }
    }

    private void setupListeners() {
        btnRetry.setOnClickListener(v -> {
            Intent intent = new Intent(ResultActivity.this, ExamSetupActivity.class);
            startActivity(intent);
            finish();
        });

        btnBackHome.setOnClickListener(v -> {
            Intent intent = new Intent(ResultActivity.this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
            finish();
        });
    }

    @Override
    public void onBackPressed() {
        // Trở về Main
        Intent intent = new Intent(ResultActivity.this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(intent);
        finish();
    }
}

