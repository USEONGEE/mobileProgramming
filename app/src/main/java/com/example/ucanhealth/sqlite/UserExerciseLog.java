package com.example.ucanhealth.sqlite;

import android.provider.BaseColumns;

public class UserExerciseLog {
    private UserExerciseLog() {}

    public static class UserExerciseLogEntry implements BaseColumns {
        public static final String TABLE_NAME = "UserExerciseLog";
        public static final String COLUMN_EXERCISE = "exercise";
        public static final String COLUMN_REPS = "repetition";
        public static final String COLUMN_WEIGHT = "weight";
        public static final String COLUMN_SET_COUNT = "set_count";
        public static final String COLUMN_TOTAL_SET_COUNT = "total_set_count";
        public static final String COLUMN_DATE = "date";
        public static final String COLUMN_ORDER = "order";
    }

}
