package com.example.quizapp.ui.history;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.quizapp.R;
import com.example.quizapp.model.ExamHistory;
import com.example.quizapp.util.TimeFormatter;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Adapter hiển thị danh sách các bài thi đã làm trong lịch sử.
 * 
 * PHÂN CÔNG:
 * - Module: Module 1 - Quản lý Người dùng & Thống kê học tập (User & Analytics)
 * - Phụ trách: Lê Đăng Hiệp
 */
public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.HistoryViewHolder> {

    private List<ExamHistory> historyList = new ArrayList<>();

    public void setHistoryList(List<ExamHistory> historyList) {
        this.historyList = historyList != null ? historyList : new ArrayList<>();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public HistoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_history, parent, false);
        return new HistoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HistoryViewHolder holder, int position) {
        ExamHistory item = historyList.get(position);
        holder.bind(item);
    }

    @Override
    public int getItemCount() {
        return historyList.size();
    }

    static class HistoryViewHolder extends RecyclerView.ViewHolder {
        private final TextView tvCategory;
        private final TextView tvScore;
        private final TextView tvDate;
        private final TextView tvDuration;

        public HistoryViewHolder(@NonNull View itemView) {
            super(itemView);
            tvCategory = itemView.findViewById(R.id.tvHistoryCategory);
            tvScore = itemView.findViewById(R.id.tvHistoryScore);
            tvDate = itemView.findViewById(R.id.tvHistoryDate);
            tvDuration = itemView.findViewById(R.id.tvHistoryDuration);
        }

        public void bind(ExamHistory item) {
            tvCategory.setText(item.getCategory() != null ? item.getCategory() : "Tổng hợp");
            tvScore.setText(String.format(Locale.getDefault(), "%.0f%% (%d/%d câu)",
                    item.getScorePercent(), item.getCorrectCount(), item.getTotalQuestions()));
            tvDate.setText("Ngày thi: " + TimeFormatter.formatDateTime(item.getTakenAt()));
            tvDuration.setText("Thời gian: " + TimeFormatter.formatMMSS(item.getDurationSeconds()));
        }
    }
}

