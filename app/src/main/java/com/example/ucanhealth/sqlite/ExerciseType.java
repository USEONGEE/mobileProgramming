package com.example.ucanhealth.sqlite;

import android.provider.BaseColumns;

public final class ExerciseType {
    private ExerciseType(){}

    public static class ExerciseTypeEntry implements BaseColumns {
        public static final String TABLE_NAME = "ExerciseType";
        public static final String COLUMN_CATEGORY = "category";
        public static final String COLUMN_EXERCISE_TYPE = "exercise_type";
        public static final String COLUMN_EXERCISE = "exercise";
    }

}
