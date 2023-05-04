package com.example.ucanhealth.sqlite;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class ExerciseTypeDbHelper extends SQLiteOpenHelper {

    private static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE " + ExerciseType.ExerciseTypeEntry.TABLE_NAME + " (" +
                    ExerciseType.ExerciseTypeEntry._ID + " INTEGER PRIMARY KEY," +
                    ExerciseType.ExerciseTypeEntry.COLUMN_CATEGORY  + " TEXT," +
                    ExerciseType.ExerciseTypeEntry.COLUMN_EXERCISE_TYPE + " TEXT," +
                    ExerciseType.ExerciseTypeEntry.COLUMN_EXERCISE + " TEXT UNIQUE)";

    public static final String DATABASE_NAME = "UcanHealth.db";
    public static final int DATABASE_VERSION = 1;
    private static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + ExerciseType.ExerciseTypeEntry.TABLE_NAME;

    public ExerciseTypeDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_ENTRIES);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(SQL_DELETE_ENTRIES);
        onCreate(db);
    }
    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }
}
