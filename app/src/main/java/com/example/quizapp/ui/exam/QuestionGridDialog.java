package com.example.quizapp.ui.exam;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.quizapp.R;
import com.example.quizapp.model.ExamSession;

/**
 * Hộp thoại hiển thị lưới câu hỏi nhanh (Question Grid).
 * Cho phép xem trạng thái câu đã chọn/chưa chọn/gắn cờ và nhảy tới câu bất kỳ.
 * 
 * PHÂN CÔNG:
 * - Module: Module 3 - Trải nghiệm làm bài thi (Exam Engine)
 * - Phụ trách: Đinh Đức Nghĩa
 */
public class QuestionGridDialog extends Dialog {

    public interface OnGridItemClickListener {
        void onQuestionSelected(int index);
        void onSubmitRequested();
    }

    private final ExamSession session;
    private final OnGridItemClickListener listener;

    public QuestionGridDialog(@NonNull Context context, ExamSession session, OnGridItemClickListener listener) {
        super(context);
        this.session = session;
        this.listener = listener;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_question_grid);

        RecyclerView rvGrid = findViewById(R.id.rvQuestionGrid);
        Button btnSubmit = findViewById(R.id.btnSubmitFromDialog);

        rvGrid.setLayoutManager(new GridLayoutManager(getContext(), 5));
        rvGrid.setAdapter(new GridAdapter());

        btnSubmit.setOnClickListener(v -> {
            dismiss();
            if (listener != null) listener.onSubmitRequested();
        });
    }

    private class GridAdapter extends RecyclerView.Adapter<GridAdapter.GridViewHolder> {

        @NonNull
        @Override
        public GridViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_question_grid, parent, false);
            return new GridViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull GridViewHolder holder, int position) {
            holder.tvNumber.setText(String.valueOf(position + 1));

            boolean isCurrent = (position == session.getCurrentIndex());
            boolean isAnswered = (session.getSelectedAnswers()[position] != -1);
            boolean isFlagged = session.getFlaggedQuestions()[position];

            if (isCurrent) {
                holder.tvNumber.setBackgroundResource(R.drawable.bg_option_selected);
                holder.tvNumber.setTextColor(Color.parseColor("#1976D2"));
            } else if (isFlagged) {
                holder.tvNumber.setBackgroundResource(R.drawable.bg_option_default);
                holder.tvNumber.setTextColor(Color.parseColor("#FF9800"));
            } else if (isAnswered) {
                holder.tvNumber.setBackgroundResource(R.drawable.bg_option_correct);
                holder.tvNumber.setTextColor(Color.parseColor("#2E7D32"));
            } else {
                holder.tvNumber.setBackgroundResource(R.drawable.bg_option_default);
                holder.tvNumber.setTextColor(Color.DKGRAY);
            }

            holder.itemView.setOnClickListener(v -> {
                dismiss();
                if (listener != null) listener.onQuestionSelected(position);
            });
        }

        @Override
        public int getItemCount() {
            return session.getQuestionCount();
        }

        class GridViewHolder extends RecyclerView.ViewHolder {
            TextView tvNumber;

            public GridViewHolder(@NonNull View itemView) {
                super(itemView);
                tvNumber = itemView.findViewById(R.id.tvGridNumber);
            }
        }
    }
}

