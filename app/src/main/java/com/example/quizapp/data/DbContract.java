package com.example.quizapp.data;

import android.provider.BaseColumns;

/**
 * Định nghĩa cấu trúc lược đồ CSDL SQLite (bảng, cột, câu lệnh tạo bảng).
 * 
 * PHÂN CÔNG:
 * - Module: Module 2 - Quản lý Ngân hàng câu hỏi & Đề thi
 * - Phụ trách: Lê Đức Mạnh
 */
public final class DbContract {

    private DbContract() {}

    public static final String DATABASE_NAME = "quiz.db";
    public static final int DATABASE_VERSION = 1;

    /** Bảng lưu trữ câu hỏi */
    public static class QuestionEntry implements BaseColumns {
        public static final String TABLE_NAME = "questions";
        public static final String COLUMN_CONTENT = "content";
        public static final String COLUMN_CATEGORY = "category";
        public static final String COLUMN_OPTION_A = "option_a";
        public static final String COLUMN_OPTION_B = "option_b";
        public static final String COLUMN_OPTION_C = "option_c";
        public static final String COLUMN_OPTION_D = "option_d";
        public static final String COLUMN_CORRECT_ANSWER = "correct_answer";
        public static final String COLUMN_EXPLANATION = "explanation";
        public static final String COLUMN_IS_SAMPLE = "is_sample";
        public static final String COLUMN_CREATED_AT = "created_at";
        public static final String COLUMN_UPDATED_AT = "updated_at";

        public static final String SQL_CREATE_TABLE =
                "CREATE TABLE " + TABLE_NAME + " (" +
                        _ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        COLUMN_CONTENT + " TEXT NOT NULL, " +
                        COLUMN_CATEGORY + " TEXT NOT NULL, " +
                        COLUMN_OPTION_A + " TEXT NOT NULL, " +
                        COLUMN_OPTION_B + " TEXT NOT NULL, " +
                        COLUMN_OPTION_C + " TEXT NOT NULL, " +
                        COLUMN_OPTION_D + " TEXT NOT NULL, " +
                        COLUMN_CORRECT_ANSWER + " INTEGER NOT NULL CHECK (" + COLUMN_CORRECT_ANSWER + " BETWEEN 0 AND 3), " +
                        COLUMN_EXPLANATION + " TEXT, " +
                        COLUMN_IS_SAMPLE + " INTEGER DEFAULT 0, " +
                        COLUMN_CREATED_AT + " INTEGER, " +
                        COLUMN_UPDATED_AT + " INTEGER" +
                        ");";

        public static final String SQL_CREATE_INDEX_CATEGORY =
                "CREATE INDEX idx_questions_category ON " + TABLE_NAME + " (" + COLUMN_CATEGORY + ");";

        public static final String SQL_DROP_TABLE =
                "DROP TABLE IF EXISTS " + TABLE_NAME;
    }

    /** Bảng lưu trữ lịch sử các lần thi */
    public static class HistoryEntry implements BaseColumns {
        public static final String TABLE_NAME = "exam_history";
        public static final String COLUMN_CATEGORY = "category";
        public static final String COLUMN_TOTAL_QUESTIONS = "total_questions";
        public static final String COLUMN_CORRECT_COUNT = "correct_count";
        public static final String COLUMN_SCORE_PERCENT = "score_percent";
        public static final String COLUMN_DURATION_SECONDS = "duration_seconds";
        public static final String COLUMN_TAKEN_AT = "taken_at";

        public static final String SQL_CREATE_TABLE =
                "CREATE TABLE " + TABLE_NAME + " (" +
                        _ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        COLUMN_CATEGORY + " TEXT NOT NULL, " +
                        COLUMN_TOTAL_QUESTIONS + " INTEGER NOT NULL, " +
                        COLUMN_CORRECT_COUNT + " INTEGER NOT NULL, " +
                        COLUMN_SCORE_PERCENT + " REAL NOT NULL, " +
                        COLUMN_DURATION_SECONDS + " INTEGER NOT NULL, " +
                        COLUMN_TAKEN_AT + " INTEGER NOT NULL" +
                        ");";

        public static final String SQL_CREATE_INDEX_TAKEN_AT =
                "CREATE INDEX idx_history_taken_at ON " + TABLE_NAME + " (" + COLUMN_TAKEN_AT + ");";

        public static final String SQL_DROP_TABLE =
                "DROP TABLE IF EXISTS " + TABLE_NAME;
    }
}

