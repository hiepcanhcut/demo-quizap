package com.example.quizapp.ui;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.example.quizapp.R;
import com.example.quizapp.ui.exam.ExamSetupActivity;
import com.example.quizapp.ui.history.HistoryActivity;
import com.example.quizapp.ui.question.QuestionListActivity;

/**
 * Màn hình chính điều hướng (Dashboard) kết nối 3 module chức năng của nhóm:
 * - Module 1 (Lê Đăng Hiệp): Lịch sử thi & Thống kê
 * - Module 2 (Lê Đức Mạnh): Quản lý ngân hàng câu hỏi
 * - Module 3 (Đinh Đức Nghĩa): Vào thi & Luyện tập trắc nghiệm
 */
public class MainActivity extends AppCompatActivity {

    private Button btnTakeExam;
    private Button btnQuestionBank;
    private Button btnHistory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initViews();
        setupListeners();
    }

    private void initViews() {
        btnTakeExam = findViewById(R.id.btnTakeExam);
        btnQuestionBank = findViewById(R.id.btnQuestionBank);
        btnHistory = findViewById(R.id.btnHistory);
    }

    private void setupListeners() {
        // Module 3: Vào thi (Đinh Đức Nghĩa)
        btnTakeExam.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, ExamSetupActivity.class);
            startActivity(intent);
        });

        // Module 2: Quản lý ngân hàng câu hỏi (Lê Đức Mạnh)
        btnQuestionBank.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, QuestionListActivity.class);
            startActivity(intent);
        });

        // Module 1: Lịch sử thi & Thống kê (Lê Đăng Hiệp)
        btnHistory.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, HistoryActivity.class);
            startActivity(intent);
        });
    }
}

