package com.example.quizapp.ui.exam;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.quizapp.R;
import com.example.quizapp.data.QuestionRepository;
import com.example.quizapp.util.AppExecutors;
import com.example.quizapp.util.Constants;

import java.util.ArrayList;
import java.util.List;

/**
 * Màn hình cấu hình bài thi: Chọn môn học, số lượng câu hỏi, thời gian.
 * 
 * PHÂN CÔNG:
 * - Module: Module 3 - Trải nghiệm làm bài thi (Exam Engine)
 * - Phụ trách: Đinh Đức Nghĩa
 */
public class ExamSetupActivity extends AppCompatActivity {

    private Spinner spnSubject;
    private RadioGroup rgQuestionCount;
    private RadioButton rbCount5, rbCount10, rbCount20, rbCountAll;
    private Button btnStartExam;

    private QuestionRepository repository;
    private List<String> subjects = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exam_setup);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        repository = new QuestionRepository(this);

        initViews();
        loadSubjects();
        setupListeners();
    }

    private void initViews() {
        spnSubject = findViewById(R.id.spnSubject);
        rgQuestionCount = findViewById(R.id.rgQuestionCount);
        rbCount5 = findViewById(R.id.rbCount5);
        rbCount10 = findViewById(R.id.rbCount10);
        rbCount20 = findViewById(R.id.rbCount20);
        rbCountAll = findViewById(R.id.rbCountAll);
        btnStartExam = findViewById(R.id.btnStartExam);
    }

    private void loadSubjects() {
        AppExecutors.getInstance().diskIO().execute(() -> {
            List<String> list = repository.getAllCategories();
            AppExecutors.getInstance().mainThread().execute(() -> {
                subjects.clear();
                subjects.add(Constants.CATEGORY_ALL);
                subjects.addAll(list);

                ArrayAdapter<String> adapter = new ArrayAdapter<>(
                        ExamSetupActivity.this,
                        android.R.layout.simple_spinner_dropdown_item,
                        subjects
                );
                spnSubject.setAdapter(adapter);
            });
        });
    }

    private void setupListeners() {
        btnStartExam.setOnClickListener(v -> {
            String selectedSubject = (String) spnSubject.getSelectedItem();
            int count = 5;
            int checkedId = rgQuestionCount.getCheckedRadioButtonId();
            if (checkedId == R.id.rbCount10) count = 10;
            else if (checkedId == R.id.rbCount20) count = 20;
            else if (checkedId == R.id.rbCountAll) count = 0; // 0 = tất cả

            final int requestedCount = count;
            AppExecutors.getInstance().diskIO().execute(() -> {
                long[] questionIds = repository.pickRandomIds(selectedSubject, requestedCount);
                AppExecutors.getInstance().mainThread().execute(() -> {
                    if (questionIds == null || questionIds.length == 0) {
                        Toast.makeText(this, "Không có câu hỏi nào phù hợp với môn đã chọn!", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    Intent intent = new Intent(ExamSetupActivity.this, ExamActivity.class);
                    intent.putExtra(Constants.EXTRA_QUESTION_IDS, questionIds);
                    intent.putExtra(Constants.EXTRA_CATEGORY, selectedSubject);
                    startActivity(intent);
                    finish();
                });
            });
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}

