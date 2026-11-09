package com.example.quizapp.data;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;

import com.example.quizapp.model.Question;

import java.util.ArrayList;
import java.util.List;

/**
 * Cung cấp bộ câu hỏi mẫu ban đầu cho ứng dụng.
 * 
 * PHÂN CÔNG:
 * - Module: Module 2 - Quản lý Ngân hàng câu hỏi & Đề thi
 * - Phụ trách: Lê Đức Mạnh
 */
public class SampleDataProvider {

    public static List<Question> getSampleQuestions() {
        List<Question> list = new ArrayList<>();
        long now = System.currentTimeMillis();

        // Môn Toán
        list.add(new Question(0, "Căn bậc hai số học của 81 là bao nhiêu?", "Toán học",
                "9", "-9", "±9", "81", 0, "Căn bậc hai số học của số a không âm là số không âm x mà x^2 = a. Do đó là 9.", true, now, now));

        list.add(new Question(0, "Đạo hàm của hàm số y = x^3 là gì?", "Toán học",
                "3x", "3x^2", "x^2", "3x^3", 1, "Quy tắc tính đạo hàm: (x^n)' = n * x^(n-1). Với n=3, đạo hàm là 3x^2.", true, now, now));

        // Môn Lịch sử
        list.add(new Question(0, "Chiến dịch Điện Biên Phủ toàn thắng vào ngày tháng năm nào?", "Lịch sử",
                "30/4/1975", "19/8/1945", "07/5/1954", "02/9/1945", 2, "Vào 17h30 ngày 7/5/1954, lá cờ quyết chiến quyết thắng tung bay trên nóc hầm Đờ Cát, đánh dấu chiến thắng Điện Biên Phủ.", true, now, now));

        list.add(new Question(0, "Bản Tuyên ngôn Độc lập được Chủ tịch Hồ Chí Minh đọc tại đâu?", "Lịch sử",
                "Quảng trường Ba Đình", "Bến Nhà Rồng", "Tân Trào", "Pác Bó", 0, "Ngày 2/9/1945, Bác Hồ đọc Tuyên ngôn Độc lập tại Quảng trường Ba Đình, Hà Nội.", true, now, now));

        // Môn Công nghệ thông tin
        list.add(new Question(0, "Hệ điều hành Android dựa trên nhân (kernel) nào?", "Công nghệ",
                "Windows NT", "FreeBSD", "Linux", "Darwin", 2, "Android được phát triển dựa trên một phiên bản sửa đổi của nhân Linux (Linux kernel).", true, now, now));

        list.add(new Question(0, "Thành phần nào trong Android chịu trách nhiệm hiển thị giao diện người dùng?", "Công nghệ",
                "Service", "BroadcastReceiver", "Activity", "ContentProvider", 2, "Activity là thành phần cơ bản đại diện cho một màn hình đơn có giao diện người dùng.", true, now, now));

        list.add(new Question(0, "Trong Java, từ khóa nào dùng để kế thừa một lớp khác?", "Công nghệ",
                "implements", "extends", "inherits", "super", 1, "Java sử dụng từ khóa 'extends' để kế thừa lớp cha và 'implements' để hiện thực interface.", true, now, now));

        // Môn Khoa học
        list.add(new Question(0, "Hành tinh nào gần Mặt Trời nhất trong Hệ Mặt Trời?", "Khoa học",
                "Sao Kim", "Sao Thủy", "Sao Hỏa", "Trái Đất", 1, "Sao Thủy (Mercury) là hành tinh gần Mặt Trời nhất.", true, now, now));

        list.add(new Question(0, "Công thức hóa học của nước là gì?", "Khoa học",
                "H2O", "CO2", "NaCl", "O2", 0, "Nước bao gồm 2 nguyên tử Hydro và 1 nguyên tử Oxy, công thức là H2O.", true, now, now));

        list.add(new Question(0, "Khí nào chiếm tỷ lệ thể tích lớn nhất trong không khí của Trái Đất?", "Khoa học",
                "Oxy", "Cacbonic", "Nito", "Argon", 2, "Khí Nitơ (N2) chiếm khoảng 78% thể tích khí quyển Trái Đất.", true, now, now));

        return list;
    }

    /**
     * Nạp dữ liệu mẫu vào database trong 1 Transaction
     */
    public static void seed(SQLiteDatabase db) {
        List<Question> samples = getSampleQuestions();
        db.beginTransaction();
        try {
            for (Question q : samples) {
                ContentValues values = new ContentValues();
                values.put(DbContract.QuestionEntry.COLUMN_CONTENT, q.getContent());
                values.put(DbContract.QuestionEntry.COLUMN_CATEGORY, q.getCategory());
                values.put(DbContract.QuestionEntry.COLUMN_OPTION_A, q.getOptionA());
                values.put(DbContract.QuestionEntry.COLUMN_OPTION_B, q.getOptionB());
                values.put(DbContract.QuestionEntry.COLUMN_OPTION_C, q.getOptionC());
                values.put(DbContract.QuestionEntry.COLUMN_OPTION_D, q.getOptionD());
                values.put(DbContract.QuestionEntry.COLUMN_CORRECT_ANSWER, q.getCorrectAnswer());
                values.put(DbContract.QuestionEntry.COLUMN_EXPLANATION, q.getExplanation());
                values.put(DbContract.QuestionEntry.COLUMN_IS_SAMPLE, 1);
                values.put(DbContract.QuestionEntry.COLUMN_CREATED_AT, q.getCreatedAt());
                values.put(DbContract.QuestionEntry.COLUMN_UPDATED_AT, q.getUpdatedAt());

                db.insert(DbContract.QuestionEntry.TABLE_NAME, null, values);
            }
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
    }
}

