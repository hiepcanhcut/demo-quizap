package com.example.quizapp.ui.question;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.quizapp.R;
import com.example.quizapp.data.QuestionRepository;
import com.example.quizapp.logic.QuestionValidator;
import com.example.quizapp.model.Question;
import com.example.quizapp.util.AppExecutors;
import com.example.quizapp.util.Constants;

/**
 * Màn hình Thêm / Sửa câu hỏi trắc nghiệm.
 * 
 * PHÂN CÔNG:
 * - Module: Module 2 - Quản lý Ngân hàng câu hỏi & Đề thi
 * - Phụ trách: Lê Đức Mạnh
 */
public class QuestionEditActivity extends AppCompatActivity {

    private EditText etCategory;
    private EditText etContent;
    private EditText etOptionA;
    private EditText etOptionB;
    private EditText etOptionC;
    private EditText etOptionD;
    private RadioGroup rgCorrectAnswer;
    private RadioButton rbOptionA, rbOptionB, rbOptionC, rbOptionD;
    private EditText etExplanation;
    private Button btnSave;

    private QuestionRepository repository;
    private long editingQuestionId = -1;
    private Question currentQuestion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_question_edit);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        repository = new QuestionRepository(this);
        initViews();

        if (getIntent().hasExtra(Constants.EXTRA_QUESTION_ID)) {
            editingQuestionId = getIntent().getLongExtra(Constants.EXTRA_QUESTION_ID, -1);
            setTitle("Sửa câu hỏi");
            loadQuestionData(editingQuestionId);
        } else {
            setTitle("Thêm câu hỏi mới");
        }

        btnSave.setOnClickListener(v -> saveQuestion());
    }

    private void initViews() {
        etCategory = findViewById(R.id.etCategory);
        etContent = findViewById(R.id.etContent);
        etOptionA = findViewById(R.id.etOptionA);
        etOptionB = findViewById(R.id.etOptionB);
        etOptionC = findViewById(R.id.etOptionC);
        etOptionD = findViewById(R.id.etOptionD);
        rgCorrectAnswer = findViewById(R.id.rgCorrectAnswer);
        rbOptionA = findViewById(R.id.rbOptionA);
        rbOptionB = findViewById(R.id.rbOptionB);
        rbOptionC = findViewById(R.id.rbOptionC);
        rbOptionD = findViewById(R.id.rbOptionD);
        etExplanation = findViewById(R.id.etExplanation);
        btnSave = findViewById(R.id.btnSave);
    }

    private void loadQuestionData(long id) {
        AppExecutors.getInstance().diskIO().execute(() -> {
            currentQuestion = repository.getById(id);
            if (currentQuestion != null) {
                AppExecutors.getInstance().mainThread().execute(() -> {
                    etCategory.setText(currentQuestion.getCategory());
                    etContent.setText(currentQuestion.getContent());
                    etOptionA.setText(currentQuestion.getOptionA());
                    etOptionB.setText(currentQuestion.getOptionB());
                    etOptionC.setText(currentQuestion.getOptionC());
                    etOptionD.setText(currentQuestion.getOptionD());
                    etExplanation.setText(currentQuestion.getExplanation());

                    switch (currentQuestion.getCorrectAnswer()) {
                        case 0: rbOptionA.setChecked(true); break;
                        case 1: rbOptionB.setChecked(true); break;
                        case 2: rbOptionC.setChecked(true); break;
                        case 3: rbOptionD.setChecked(true); break;
                    }
                });
            }
        });
    }

    private void saveQuestion() {
        String category = etCategory.getText().toString().trim();
        String content = etContent.getText().toString().trim();
        String optionA = etOptionA.getText().toString().trim();
        String optionB = etOptionB.getText().toString().trim();
        String optionC = etOptionC.getText().toString().trim();
        String optionD = etOptionD.getText().toString().trim();
        String explanation = etExplanation.getText().toString().trim();

        int correctAnswer = 0;
        int checkedId = rgCorrectAnswer.getCheckedRadioButtonId();
        if (checkedId == R.id.rbOptionB) correctAnswer = 1;
        else if (checkedId == R.id.rbOptionC) correctAnswer = 2;
        else if (checkedId == R.id.rbOptionD) correctAnswer = 3;

        Question q = (currentQuestion != null) ? currentQuestion : new Question();
        q.setCategory(category);
        q.setContent(content);
        q.setOptionA(optionA);
        q.setOptionB(optionB);
        q.setOptionC(optionC);
        q.setOptionD(optionD);
        q.setCorrectAnswer(correctAnswer);
        q.setExplanation(explanation);

        // Kiểm tra tính hợp lệ qua QuestionValidator
        QuestionValidator.ValidationResult result = QuestionValidator.validate(q);
        if (!result.isValid()) {
            Toast.makeText(this, result.getErrorMessage(), Toast.LENGTH_SHORT).show();
            return;
        }

        AppExecutors.getInstance().diskIO().execute(() -> {
            if (editingQuestionId != -1) {
                repository.update(q);
            } else {
                q.setSample(false);
                repository.insert(q);
            }

            AppExecutors.getInstance().mainThread().execute(() -> {
                Toast.makeText(this, "Lưu câu hỏi thành công!", Toast.LENGTH_SHORT).show();
                setResult(RESULT_OK);
                finish();
            });
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}

