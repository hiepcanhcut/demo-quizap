package com.example.quizapp.ui.exam;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.quizapp.R;
import com.example.quizapp.model.AnswerResult;
import com.example.quizapp.model.Question;

import java.util.ArrayList;
import java.util.List;

/**
 * Adapter hiển thị chi tiết câu trả lời đúng/sai và lời giải thích trong màn hình kết quả.
 * 
 * PHÂN CÔNG:
 * - Module: Module 3 - Trải nghiệm làm bài thi (Exam Engine)
 * - Phụ trách: Đinh Đức Nghĩa
 */
public class ResultDetailAdapter extends RecyclerView.Adapter<ResultDetailAdapter.DetailViewHolder> {

    private List<AnswerResult> results = new ArrayList<>();

    public void setResults(List<AnswerResult> results) {
        this.results = results != null ? results : new ArrayList<>();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public DetailViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_result_detail, parent, false);
        return new DetailViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DetailViewHolder holder, int position) {
        AnswerResult item = results.get(position);
        holder.bind(position + 1, item);
    }

    @Override
    public int getItemCount() {
        return results.size();
    }

    static class DetailViewHolder extends RecyclerView.ViewHolder {
        private final TextView tvIndex;
        private final TextView tvContent;
        private final TextView tvUserAnswer;
        private final TextView tvCorrectAnswer;
        private final TextView tvExplanation;

        public DetailViewHolder(@NonNull View itemView) {
            super(itemView);
            tvIndex = itemView.findViewById(R.id.tvDetailQuestionIndex);
            tvContent = itemView.findViewById(R.id.tvDetailQuestionContent);
            tvUserAnswer = itemView.findViewById(R.id.tvDetailUserAnswer);
            tvCorrectAnswer = itemView.findViewById(R.id.tvDetailCorrectAnswer);
            tvExplanation = itemView.findViewById(R.id.tvDetailExplanation);
        }

        public void bind(int questionNumber, AnswerResult item) {
            Question q = item.getQuestion();
            tvIndex.setText("Câu " + questionNumber + ":");
            tvContent.setText(q.getContent());

            String userText;
            if (item.getSelectedOption() == -1) {
                userText = "Bạn chọn: (Chưa chọn đáp án)";
                tvUserAnswer.setTextColor(Color.GRAY);
            } else {
                char userChar = (char) ('A' + item.getSelectedOption());
                userText = "Bạn chọn: " + userChar + ". " + q.getOptionText(item.getSelectedOption());
                tvUserAnswer.setTextColor(item.isCorrect() ? Color.parseColor("#2E7D32") : Color.parseColor("#C62828"));
            }
            tvUserAnswer.setText(userText);

            char correctChar = (char) ('A' + q.getCorrectAnswer());
            tvCorrectAnswer.setText("Đáp án đúng: " + correctChar + ". " + q.getOptionText(q.getCorrectAnswer()));
            tvCorrectAnswer.setTextColor(Color.parseColor("#2E7D32"));

            if (q.getExplanation() != null && !q.getExplanation().trim().isEmpty()) {
                tvExplanation.setVisibility(View.VISIBLE);
                tvExplanation.setText("Giải thích: " + q.getExplanation());
            } else {
                tvExplanation.setVisibility(View.GONE);
            }
        }
    }
}

