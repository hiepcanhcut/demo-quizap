package com.example.quizapp.ui.question;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.quizapp.R;
import com.example.quizapp.model.Question;

import java.util.ArrayList;
import java.util.List;

/**
 * Adapter hiển thị danh sách câu hỏi trong RecyclerView.
 * 
 * PHÂN CÔNG:
 * - Module: Module 2 - Quản lý Ngân hàng câu hỏi & Đề thi
 * - Phụ trách: Lê Đức Mạnh
 */
public class QuestionAdapter extends RecyclerView.Adapter<QuestionAdapter.QuestionViewHolder> {

    public interface OnQuestionActionListener {
        void onEdit(Question question);
        void onDelete(Question question);
    }

    private List<Question> questionList = new ArrayList<>();
    private final OnQuestionActionListener actionListener;

    public QuestionAdapter(OnQuestionActionListener actionListener) {
        this.actionListener = actionListener;
    }

    public void setQuestions(List<Question> questions) {
        this.questionList = questions != null ? questions : new ArrayList<>();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public QuestionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_question, parent, false);
        return new QuestionViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull QuestionViewHolder holder, int position) {
        Question q = questionList.get(position);
        holder.bind(q, actionListener);
    }

    @Override
    public int getItemCount() {
        return questionList.size();
    }

    static class QuestionViewHolder extends RecyclerView.ViewHolder {
        private final TextView tvCategory;
        private final TextView tvContent;
        private final TextView tvSampleBadge;
        private final ImageButton btnEdit;
        private final ImageButton btnDelete;

        public QuestionViewHolder(@NonNull View itemView) {
            super(itemView);
            tvCategory = itemView.findViewById(R.id.tvCategory);
            tvContent = itemView.findViewById(R.id.tvContent);
            tvSampleBadge = itemView.findViewById(R.id.tvSampleBadge);
            btnEdit = itemView.findViewById(R.id.btnEdit);
            btnDelete = itemView.findViewById(R.id.btnDelete);
        }

        public void bind(Question q, OnQuestionActionListener listener) {
            tvCategory.setText(q.getCategory());
            tvContent.setText(q.getContent());
            tvSampleBadge.setVisibility(q.isSample() ? View.VISIBLE : View.GONE);

            btnEdit.setOnClickListener(v -> {
                if (listener != null) listener.onEdit(q);
            });
            btnDelete.setOnClickListener(v -> {
                if (listener != null) listener.onDelete(q);
            });
        }
    }
}

