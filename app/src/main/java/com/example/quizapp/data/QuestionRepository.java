package com.example.quizapp.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.quizapp.model.Question;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Repository phụ trách truy xuất dữ liệu câu hỏi trong SQLite.
 * Đóng gói toàn bộ câu lệnh SQL và logic thao tác bảng questions.
 * 
 * PHÂN CÔNG:
 * - Module: Module 2 - Quản lý Ngân hàng câu hỏi & Đề thi
 * - Phụ trách: Lê Đức Mạnh
 */
public class QuestionRepository {

    private final QuizDatabaseHelper dbHelper;

    public QuestionRepository(Context context) {
        this.dbHelper = QuizDatabaseHelper.getInstance(context);
    }

    /**
     * Thêm câu hỏi mới vào DB.
     * @return ID bản ghi vừa thêm, hoặc -1 nếu lỗi.
     */
    public long insert(Question q) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DbContract.QuestionEntry.COLUMN_CONTENT, q.getContent());
        values.put(DbContract.QuestionEntry.COLUMN_CATEGORY, q.getCategory());
        values.put(DbContract.QuestionEntry.COLUMN_OPTION_A, q.getOptionA());
        values.put(DbContract.QuestionEntry.COLUMN_OPTION_B, q.getOptionB());
        values.put(DbContract.QuestionEntry.COLUMN_OPTION_C, q.getOptionC());
        values.put(DbContract.QuestionEntry.COLUMN_OPTION_D, q.getOptionD());
        values.put(DbContract.QuestionEntry.COLUMN_CORRECT_ANSWER, q.getCorrectAnswer());
        values.put(DbContract.QuestionEntry.COLUMN_EXPLANATION, q.getExplanation());
        values.put(DbContract.QuestionEntry.COLUMN_IS_SAMPLE, q.isSample() ? 1 : 0);
        long now = System.currentTimeMillis();
        values.put(DbContract.QuestionEntry.COLUMN_CREATED_AT, now);
        values.put(DbContract.QuestionEntry.COLUMN_UPDATED_AT, now);

        return db.insert(DbContract.QuestionEntry.TABLE_NAME, null, values);
    }

    /**
     * Cập nhật thông tin câu hỏi.
     */
    public int update(Question q) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DbContract.QuestionEntry.COLUMN_CONTENT, q.getContent());
        values.put(DbContract.QuestionEntry.COLUMN_CATEGORY, q.getCategory());
        values.put(DbContract.QuestionEntry.COLUMN_OPTION_A, q.getOptionA());
        values.put(DbContract.QuestionEntry.COLUMN_OPTION_B, q.getOptionB());
        values.put(DbContract.QuestionEntry.COLUMN_OPTION_C, q.getOptionC());
        values.put(DbContract.QuestionEntry.COLUMN_OPTION_D, q.getOptionD());
        values.put(DbContract.QuestionEntry.COLUMN_CORRECT_ANSWER, q.getCorrectAnswer());
        values.put(DbContract.QuestionEntry.COLUMN_EXPLANATION, q.getExplanation());
        values.put(DbContract.QuestionEntry.COLUMN_UPDATED_AT, System.currentTimeMillis());

        return db.update(DbContract.QuestionEntry.TABLE_NAME, values,
                DbContract.QuestionEntry._ID + " = ?",
                new String[]{String.valueOf(q.getId())});
    }

    /**
     * Xóa một câu hỏi theo ID.
     */
    public int delete(long id) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        return db.delete(DbContract.QuestionEntry.TABLE_NAME,
                DbContract.QuestionEntry._ID + " = ?",
                new String[]{String.valueOf(id)});
    }

    /**
     * Lấy chi tiết một câu hỏi theo ID.
     */
    public Question getById(long id) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query(DbContract.QuestionEntry.TABLE_NAME,
                null,
                DbContract.QuestionEntry._ID + " = ?",
                new String[]{String.valueOf(id)},
                null, null, null);

        Question q = null;
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                q = cursorToQuestion(cursor);
            }
            cursor.close();
        }
        return q;
    }

    /**
     * Tìm kiếm và lọc câu hỏi theo từ khóa và chuyên đề.
     * Sử dụng tham số ? để tránh SQL Injection.
     */
    public List<Question> search(String keyword, String category) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        List<Question> list = new ArrayList<>();

        StringBuilder selection = new StringBuilder("1=1");
        List<String> args = new ArrayList<>();

        if (category != null && !category.isEmpty() && !"Tất cả".equalsIgnoreCase(category)) {
            selection.append(" AND ").append(DbContract.QuestionEntry.COLUMN_CATEGORY).append(" = ?");
            args.add(category);
        }

        if (keyword != null && !keyword.trim().isEmpty()) {
            selection.append(" AND (")
                    .append(DbContract.QuestionEntry.COLUMN_CONTENT).append(" LIKE ? OR ")
                    .append(DbContract.QuestionEntry.COLUMN_OPTION_A).append(" LIKE ? OR ")
                    .append(DbContract.QuestionEntry.COLUMN_OPTION_B).append(" LIKE ? OR ")
                    .append(DbContract.QuestionEntry.COLUMN_OPTION_C).append(" LIKE ? OR ")
                    .append(DbContract.QuestionEntry.COLUMN_OPTION_D).append(" LIKE ? OR ")
                    .append(DbContract.QuestionEntry.COLUMN_EXPLANATION).append(" LIKE ?)");
            String likePattern = "%" + keyword.trim() + "%";
            for (int i = 0; i < 6; i++) {
                args.add(likePattern);
            }
        }

        Cursor cursor = db.query(DbContract.QuestionEntry.TABLE_NAME,
                null,
                selection.toString(),
                args.toArray(new String[0]),
                null, null,
                DbContract.QuestionEntry._ID + " DESC");

        if (cursor != null) {
            while (cursor.moveToNext()) {
                list.add(cursorToQuestion(cursor));
            }
            cursor.close();
        }
        return list;
    }

    /**
     * Lấy danh sách các môn học / danh mục hiện có.
     */
    public List<String> getAllCategories() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        List<String> categories = new ArrayList<>();
        Cursor cursor = db.query(true, DbContract.QuestionEntry.TABLE_NAME,
                new String[]{DbContract.QuestionEntry.COLUMN_CATEGORY},
                null, null, null, null,
                DbContract.QuestionEntry.COLUMN_CATEGORY + " ASC", null);

        if (cursor != null) {
            while (cursor.moveToNext()) {
                categories.add(cursor.getString(0));
            }
            cursor.close();
        }
        return categories;
    }

    /**
     * Lấy ngẫu nhiên danh sách ID câu hỏi theo môn học để tạo đề thi.
     */
    public long[] pickRandomIds(String category, int count) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String selection = null;
        String[] args = null;
        if (category != null && !category.isEmpty() && !"Tất cả".equalsIgnoreCase(category)) {
            selection = DbContract.QuestionEntry.COLUMN_CATEGORY + " = ?";
            args = new String[]{category};
        }

        String limit = count > 0 ? String.valueOf(count) : null;
        Cursor cursor = db.query(DbContract.QuestionEntry.TABLE_NAME,
                new String[]{DbContract.QuestionEntry._ID},
                selection, args, null, null,
                "RANDOM()", limit);

        if (cursor == null) return new long[0];
        long[] ids = new long[cursor.getCount()];
        int idx = 0;
        while (cursor.moveToNext()) {
            ids[idx++] = cursor.getLong(0);
        }
        cursor.close();
        return ids;
    }

    /**
     * Lấy danh sách đối tượng Question tương ứng với mảng ID (theo thứ tự mảng ID).
     */
    public List<Question> getByIds(long[] ids) {
        if (ids == null || ids.length == 0) return Collections.emptyList();
        List<Question> result = new ArrayList<>();
        for (long id : ids) {
            Question q = getById(id);
            if (q != null) {
                result.add(q);
            }
        }
        return result;
    }

    /**
     * Khôi phục câu hỏi mẫu
     */
    public void restoreSamples() {
        dbHelper.restoreSampleQuestions();
    }

    private Question cursorToQuestion(Cursor cursor) {
        return new Question(
                cursor.getLong(cursor.getColumnIndexOrThrow(DbContract.QuestionEntry._ID)),
                cursor.getString(cursor.getColumnIndexOrThrow(DbContract.QuestionEntry.COLUMN_CONTENT)),
                cursor.getString(cursor.getColumnIndexOrThrow(DbContract.QuestionEntry.COLUMN_CATEGORY)),
                cursor.getString(cursor.getColumnIndexOrThrow(DbContract.QuestionEntry.COLUMN_OPTION_A)),
                cursor.getString(cursor.getColumnIndexOrThrow(DbContract.QuestionEntry.COLUMN_OPTION_B)),
                cursor.getString(cursor.getColumnIndexOrThrow(DbContract.QuestionEntry.COLUMN_OPTION_C)),
                cursor.getString(cursor.getColumnIndexOrThrow(DbContract.QuestionEntry.COLUMN_OPTION_D)),
                cursor.getInt(cursor.getColumnIndexOrThrow(DbContract.QuestionEntry.COLUMN_CORRECT_ANSWER)),
                cursor.getString(cursor.getColumnIndexOrThrow(DbContract.QuestionEntry.COLUMN_EXPLANATION)),
                cursor.getInt(cursor.getColumnIndexOrThrow(DbContract.QuestionEntry.COLUMN_IS_SAMPLE)) == 1,
                cursor.getLong(cursor.getColumnIndexOrThrow(DbContract.QuestionEntry.COLUMN_CREATED_AT)),
                cursor.getLong(cursor.getColumnIndexOrThrow(DbContract.QuestionEntry.COLUMN_UPDATED_AT))
        );
    }
}

