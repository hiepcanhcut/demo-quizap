package com.example.quizapp.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.quizapp.model.ExamHistory;

import java.util.ArrayList;
import java.util.List;

/**
 * Repository phụ trách lưu và đọc lịch sử làm bài thi từ bảng exam_history trong SQLite.
 * 
 * PHÂN CÔNG:
 * - Module: Module 1 - Quản lý Người dùng & Thống kê học tập (User & Analytics)
 * - Phụ trách: Lê Đăng Hiệp
 */
public class HistoryRepository {

    private final QuizDatabaseHelper dbHelper;

    public HistoryRepository(Context context) {
        this.dbHelper = QuizDatabaseHelper.getInstance(context);
    }

    /**
     * Lưu kết quả một lần thi vào CSDL.
     */
    public long insert(ExamHistory history) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DbContract.HistoryEntry.COLUMN_CATEGORY, history.getCategory());
        values.put(DbContract.HistoryEntry.COLUMN_TOTAL_QUESTIONS, history.getTotalQuestions());
        values.put(DbContract.HistoryEntry.COLUMN_CORRECT_COUNT, history.getCorrectCount());
        values.put(DbContract.HistoryEntry.COLUMN_SCORE_PERCENT, history.getScorePercent());
        values.put(DbContract.HistoryEntry.COLUMN_DURATION_SECONDS, history.getDurationSeconds());
        values.put(DbContract.HistoryEntry.COLUMN_TAKEN_AT, history.getTakenAt());

        return db.insert(DbContract.HistoryEntry.TABLE_NAME, null, values);
    }

    /**
     * Lấy toàn bộ lịch sử các lần thi, sắp xếp mới nhất lên đầu.
     */
    public List<ExamHistory> getAll() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        List<ExamHistory> list = new ArrayList<>();
        Cursor cursor = db.query(DbContract.HistoryEntry.TABLE_NAME,
                null, null, null, null, null,
                DbContract.HistoryEntry.COLUMN_TAKEN_AT + " DESC");

        if (cursor != null) {
            while (cursor.moveToNext()) {
                list.add(cursorToHistory(cursor));
            }
            cursor.close();
        }
        return list;
    }

    /**
     * Lấy một bản ghi lịch sử theo ID.
     */
    public ExamHistory getById(long id) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query(DbContract.HistoryEntry.TABLE_NAME,
                null,
                DbContract.HistoryEntry._ID + " = ?",
                new String[]{String.valueOf(id)},
                null, null, null);

        ExamHistory history = null;
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                history = cursorToHistory(cursor);
            }
            cursor.close();
        }
        return history;
    }

    private ExamHistory cursorToHistory(Cursor cursor) {
        return new ExamHistory(
                cursor.getLong(cursor.getColumnIndexOrThrow(DbContract.HistoryEntry._ID)),
                cursor.getString(cursor.getColumnIndexOrThrow(DbContract.HistoryEntry.COLUMN_CATEGORY)),
                cursor.getInt(cursor.getColumnIndexOrThrow(DbContract.HistoryEntry.COLUMN_TOTAL_QUESTIONS)),
                cursor.getInt(cursor.getColumnIndexOrThrow(DbContract.HistoryEntry.COLUMN_CORRECT_COUNT)),
                cursor.getDouble(cursor.getColumnIndexOrThrow(DbContract.HistoryEntry.COLUMN_SCORE_PERCENT)),
                cursor.getInt(cursor.getColumnIndexOrThrow(DbContract.HistoryEntry.COLUMN_DURATION_SECONDS)),
                cursor.getLong(cursor.getColumnIndexOrThrow(DbContract.HistoryEntry.COLUMN_TAKEN_AT))
        );
    }
}

