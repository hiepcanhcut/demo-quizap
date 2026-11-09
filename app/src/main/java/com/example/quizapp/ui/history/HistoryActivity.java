package com.example.quizapp.ui.history;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.quizapp.R;
import com.example.quizapp.data.HistoryRepository;
import com.example.quizapp.model.ExamHistory;
import com.example.quizapp.util.AppExecutors;

import java.util.List;

/**
 * Màn hình Lịch sử thi & Thống kê học tập:
 * - Hiển thị danh sách các bài thi đã làm, thời gian nộp bài, điểm số
 * - Nền tảng để mở rộng thống kê biểu đồ tiến độ học tập và xếp hạng
 * 
 * PHÂN CÔNG:
 * - Module: Module 1 - Quản lý Người dùng & Thống kê học tập (User & Analytics)
 * - Phụ trách: Lê Đăng Hiệp
 */
public class HistoryActivity extends AppCompatActivity {

    private RecyclerView rvHistory;
    private TextView tvEmptyHistory;

    private HistoryRepository repository;
    private HistoryAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        repository = new HistoryRepository(this);

        initViews();
        setupRecyclerView();
        loadHistory();
    }

    private void initViews() {
        rvHistory = findViewById(R.id.rvHistory);
        tvEmptyHistory = findViewById(R.id.tvEmptyHistory);
    }

    private void setupRecyclerView() {
        adapter = new HistoryAdapter();
        rvHistory.setLayoutManager(new LinearLayoutManager(this));
        rvHistory.setAdapter(adapter);
    }

    private void loadHistory() {
        AppExecutors.getInstance().diskIO().execute(() -> {
            List<ExamHistory> list = repository.getAll();
            AppExecutors.getInstance().mainThread().execute(() -> {
                adapter.setHistoryList(list);
                if (list.isEmpty()) {
                    tvEmptyHistory.setVisibility(View.VISIBLE);
                    rvHistory.setVisibility(View.GONE);
                } else {
                    tvEmptyHistory.setVisibility(View.GONE);
                    rvHistory.setVisibility(View.VISIBLE);
                }
            });
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}

