package com.example.quizapp.ui.question;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.quizapp.R;
import com.example.quizapp.data.QuestionRepository;
import com.example.quizapp.model.Question;
import com.example.quizapp.util.AppExecutors;
import com.example.quizapp.util.Constants;

import java.util.ArrayList;
import java.util.List;

/**
 * Màn hình danh sách ngân hàng câu hỏi (CRUD, tìm kiếm, lọc theo môn, khôi phục câu hỏi mẫu).
 * 
 * PHÂN CÔNG:
 * - Module: Module 2 - Quản lý Ngân hàng câu hỏi & Đề thi
 * - Phụ trách: Lê Đức Mạnh
 */
public class QuestionListActivity extends AppCompatActivity implements QuestionAdapter.OnQuestionActionListener {

    private SearchView searchView;
    private Spinner spnCategoryFilter;
    private RecyclerView rvQuestions;
    private TextView tvEmpty;
    private Button btnAddQuestion;
    private Button btnRestoreSample;

    private QuestionRepository repository;
    private QuestionAdapter adapter;

    private String currentKeyword = "";
    private String currentCategory = Constants.CATEGORY_ALL;
    private List<String> categoriesList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_question_list);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        repository = new QuestionRepository(this);

        initViews();
        setupRecyclerView();
        setupListeners();
        loadCategories();
        loadQuestions();
    }

    private void initViews() {
        searchView = findViewById(R.id.searchView);
        spnCategoryFilter = findViewById(R.id.spnCategoryFilter);
        rvQuestions = findViewById(R.id.rvQuestions);
        tvEmpty = findViewById(R.id.tvEmpty);
        btnAddQuestion = findViewById(R.id.btnAddQuestion);
        btnRestoreSample = findViewById(R.id.btnRestoreSample);
    }

    private void setupRecyclerView() {
        adapter = new QuestionAdapter(this);
        rvQuestions.setLayoutManager(new LinearLayoutManager(this));
        rvQuestions.setAdapter(adapter);
    }

    private void setupListeners() {
        btnAddQuestion.setOnClickListener(v -> {
            Intent intent = new Intent(QuestionListActivity.this, QuestionEditActivity.class);
            startActivityForResult(intent, Constants.REQUEST_CODE_ADD_QUESTION);
        });

        btnRestoreSample.setOnClickListener(v -> showRestoreConfirmation());

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                currentKeyword = query;
                loadQuestions();
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                currentKeyword = newText;
                loadQuestions();
                return true;
            }
        });

        spnCategoryFilter.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                currentCategory = categoriesList.get(position);
                loadQuestions();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
    }

    private void loadCategories() {
        AppExecutors.getInstance().diskIO().execute(() -> {
            List<String> list = repository.getAllCategories();
            AppExecutors.getInstance().mainThread().execute(() -> {
                categoriesList.clear();
                categoriesList.add(Constants.CATEGORY_ALL);
                categoriesList.addAll(list);

                ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(
                        QuestionListActivity.this,
                        android.R.layout.simple_spinner_dropdown_item,
                        categoriesList
                );
                spnCategoryFilter.setAdapter(spinnerAdapter);
            });
        });
    }

    private void loadQuestions() {
        AppExecutors.getInstance().diskIO().execute(() -> {
            List<Question> questions = repository.search(currentKeyword, currentCategory);
            AppExecutors.getInstance().mainThread().execute(() -> {
                adapter.setQuestions(questions);
                if (questions.isEmpty()) {
                    tvEmpty.setVisibility(View.VISIBLE);
                    rvQuestions.setVisibility(View.GONE);
                } else {
                    tvEmpty.setVisibility(View.GONE);
                    rvQuestions.setVisibility(View.VISIBLE);
                }
            });
        });
    }

    private void showRestoreConfirmation() {
        new AlertDialog.Builder(this)
                .setTitle("Khôi phục câu hỏi mẫu")
                .setMessage("Bạn có chắc chắn muốn khôi phục lại các câu hỏi mẫu? (Các câu bạn tự thêm vẫn được giữ nguyên)")
                .setPositiveButton("Khôi phục", (dialog, which) -> {
                    AppExecutors.getInstance().diskIO().execute(() -> {
                        repository.restoreSamples();
                        AppExecutors.getInstance().mainThread().execute(() -> {
                            Toast.makeText(this, "Đã khôi phục câu hỏi mẫu thành công!", Toast.LENGTH_SHORT).show();
                            loadCategories();
                            loadQuestions();
                        });
                    });
                })
                .setNegativeButton("Hủy", null)
                .show();
    }

    @Override
    public void onEdit(Question question) {
        Intent intent = new Intent(this, QuestionEditActivity.class);
        intent.putExtra(Constants.EXTRA_QUESTION_ID, question.getId());
        startActivityForResult(intent, Constants.REQUEST_CODE_EDIT_QUESTION);
    }

    @Override
    public void onDelete(Question question) {
        new AlertDialog.Builder(this)
                .setTitle("Xác nhận xóa")
                .setMessage("Bạn có chắc muốn xóa câu hỏi này?")
                .setPositiveButton("Xóa", (dialog, which) -> {
                    AppExecutors.getInstance().diskIO().execute(() -> {
                        repository.delete(question.getId());
                        AppExecutors.getInstance().mainThread().execute(() -> {
                            Toast.makeText(this, "Đã xóa câu hỏi", Toast.LENGTH_SHORT).show();
                            loadCategories();
                            loadQuestions();
                        });
                    });
                })
                .setNegativeButton("Hủy", null)
                .show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            loadCategories();
            loadQuestions();
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}

