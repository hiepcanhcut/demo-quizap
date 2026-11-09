package com.example.quizapp.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Quản lý khởi tạo và nâng cấp CSDL SQLite của ứng dụng.
 * 
 * PHÂN CÔNG:
 * - Module: Module 2 - Quản lý Ngân hàng câu hỏi & Đề thi
 * - Phụ trách: Lê Đức Mạnh
 */
public class QuizDatabaseHelper extends SQLiteOpenHelper {

    private static volatile QuizDatabaseHelper sInstance;

    public static synchronized QuizDatabaseHelper getInstance(Context context) {
        if (sInstance == null) {
            sInstance = new QuizDatabaseHelper(context.getApplicationContext());
        }
        return sInstance;
    }

    private QuizDatabaseHelper(Context context) {
        super(context, DbContract.DATABASE_NAME, null, DbContract.DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Tạo bảng questions và index
        db.execSQL(DbContract.QuestionEntry.SQL_CREATE_TABLE);
        db.execSQL(DbContract.QuestionEntry.SQL_CREATE_INDEX_CATEGORY);

        // Tạo bảng exam_history và index
        db.execSQL(DbContract.HistoryEntry.SQL_CREATE_TABLE);
        db.execSQL(DbContract.HistoryEntry.SQL_CREATE_INDEX_TAKEN_AT);

        // Tự động nạp câu hỏi mẫu lần đầu
        SampleDataProvider.seed(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Nâng cấp schema từng bước khi tăng DATABASE_VERSION (không drop mất dữ liệu)
        // Hiện tại là version 1
    }

    /**
     * Khôi phục bộ câu hỏi mẫu mặc định mà không làm mất câu hỏi tự tạo của người dùng.
     */
    public void restoreSampleQuestions() {
        SQLiteDatabase db = getWritableDatabase();
        db.beginTransaction();
        try {
            // Xóa các câu hỏi mẫu cũ (is_sample = 1)
            db.delete(DbContract.QuestionEntry.TABLE_NAME,
                    DbContract.QuestionEntry.COLUMN_IS_SAMPLE + " = 1", null);

            // Nạp lại bộ câu hỏi mẫu
            SampleDataProvider.seed(db);

            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
    }
}

