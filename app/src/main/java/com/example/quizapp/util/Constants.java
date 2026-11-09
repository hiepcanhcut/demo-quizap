package com.example.quizapp.util;

/**
 * Các hằng số dùng chung trong toàn bộ ứng dụng Quiz App.
 */
public final class Constants {

    private Constants() {}

    // Intent Extra Keys
    public static final String EXTRA_QUESTION_ID = "extra_question_id";
    public static final String EXTRA_CATEGORY = "extra_category";
    public static final String EXTRA_QUESTION_COUNT = "extra_question_count";
    public static final String EXTRA_QUESTION_IDS = "extra_question_ids";
    public static final String EXTRA_EXAM_HISTORY_ID = "extra_exam_history_id";
    public static final String EXTRA_EXAM_RESULT = "extra_exam_result";

    // Request Codes
    public static final int REQUEST_CODE_ADD_QUESTION = 1001;
    public static final int REQUEST_CODE_EDIT_QUESTION = 1002;

    // Default Exam Settings
    public static final int DEFAULT_SECONDS_PER_QUESTION = 60; // 1 phút / câu
    public static final String CATEGORY_ALL = "Tất cả";
}

