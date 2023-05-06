package com.example.ucanhealth.sqlite;

import android.provider.BaseColumns;

public final class UcanHealth {

    private UcanHealth(){};
    public static class ExerciseTypeEntry implements BaseColumns {
        public static final String TABLE_NAME = "ExerciseType";
        public static final String COLUMN_CATEGORY = "category"; // health
        public static final String COLUMN_EXERCISE_TYPE = "exercise_type"; // 등, 가슴, 팔, 다리
        public static final String COLUMN_EXERCISE = "exercise";
        public static final String COLUMN_SHOW = "show";
    }

    public static class UserExerciseLogEntry implements BaseColumns {
        public static final String TABLE_NAME = "UserExerciseLog";
        public static final String COLUMN_EXERCISE = "exercise";
        public static final String COLUMN_REPS = "repetition";
        public static final String COLUMN_WEIGHT = "weight";
        public static final String COLUMN_SET_COUNT = "set_count";
        public static final String COLUMN_TOTAL_SET_COUNT = "total_set_count";
        public static final String COLUMN_DATE = "date_";
        public static final String COLUMN_ORDER = "exercise_order";
        public static final String COLUMN_TOTAL_EXERCISE_TIME = "total_exercise_time"; // 초 단위까지 계산
    }
}
