package com.example.ucanhealth.sqlite;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class UserExerciseLogDbHelper extends SQLiteOpenHelper {

    private static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE " + UserExerciseLog.UserExerciseLogEntry.TABLE_NAME + " (" +
                    UserExerciseLog.UserExerciseLogEntry._ID + " INTEGER PRIMARY KEY," +
                    UserExerciseLog.UserExerciseLogEntry.COLUMN_REPS  + " TEXT," +
                    UserExerciseLog.UserExerciseLogEntry.COLUMN_WEIGHT  + " TEXT," +
                    UserExerciseLog.UserExerciseLogEntry.COLUMN_SET_COUNT  + " INTEGER," +
                    UserExerciseLog.UserExerciseLogEntry.COLUMN_TOTAL_SET_COUNT + " INTEGER," +
                    UserExerciseLog.UserExerciseLogEntry.COLUMN_DATE + " TEXT," +
                    UserExerciseLog.UserExerciseLogEntry.COLUMN_ORDER + " INTEGER UNIQUE)";

    public static final String DATABASE_NAME = "UcanHealth.db";

    public static final int DATABASE_VERSION = 1;

    private static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + UserExerciseLog.UserExerciseLogEntry.TABLE_NAME;

    public UserExerciseLogDbHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db)  {
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
