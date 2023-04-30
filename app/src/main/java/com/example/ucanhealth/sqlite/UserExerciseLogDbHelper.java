package com.example.ucanhealth.sqlite;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class UserExerciseLogDbHelper extends SQLiteOpenHelper {

    private static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE " + UserExerciseLog.UserExerciseLogEntry.TABLE_NAME + " (" +
                    UserExerciseLog.UserExerciseLogEntry._ID + " INTEGER PRIMARY KEY," +
                    UserExerciseLog.UserExerciseLogEntry.COLUMN_EXERCISE  + " TEXT," +
                    UserExerciseLog.UserExerciseLogEntry.COLUMN_REPS  + " INTEGER," +
                    UserExerciseLog.UserExerciseLogEntry.COLUMN_WEIGHT  + " REAL," +
                    UserExerciseLog.UserExerciseLogEntry.COLUMN_SET_COUNT  + " INTEGER," +
                    UserExerciseLog.UserExerciseLogEntry.COLUMN_TOTAL_SET_COUNT + " INTEGER," +
                    UserExerciseLog.UserExerciseLogEntry.COLUMN_DATE + " TEXT," +
                    UserExerciseLog.UserExerciseLogEntry.COLUMN_ORDER + " INTEGER UNIQUE)";

    public static final String DATABASE_NAME = "UcanHealth.db";

    public static final int DATABASE_VERSION = 2;

    private static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + UserExerciseLog.UserExerciseLogEntry.TABLE_NAME;

    public UserExerciseLogDbHelper(Context context) {
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

    /**
     * 인자로 주어지는 날짜의 routine list를 반환함
     * @param db -> db_read
     * @param date -> date you want to search
     * @return cursor instance
     */
    public Cursor getRoutineByDate(SQLiteDatabase db, String date) {
        String[] projection = {
                UserExerciseLog.UserExerciseLogEntry.COLUMN_EXERCISE,
                        UserExerciseLog.UserExerciseLogEntry.COLUMN_REPS,
                        UserExerciseLog.UserExerciseLogEntry.COLUMN_WEIGHT,
                        UserExerciseLog.UserExerciseLogEntry.COLUMN_SET_COUNT,
                        UserExerciseLog.UserExerciseLogEntry.COLUMN_TOTAL_SET_COUNT,
                        UserExerciseLog.UserExerciseLogEntry.COLUMN_DATE,
                        UserExerciseLog.UserExerciseLogEntry.COLUMN_ORDER
        };
        String sortOrder = UserExerciseLog.UserExerciseLogEntry.COLUMN_ORDER + " DESC";

        String selection = String.format("%s = ?",UserExerciseLog.UserExerciseLogEntry.COLUMN_DATE);
        String[] selectionArgs = {date};
        Cursor cursor = db.query(
                UserExerciseLog.UserExerciseLogEntry.TABLE_NAME,   // The table to query
                projection,             // The array of columns to return (pass null to get all)
                selection,              // The columns for the WHERE clause
                selectionArgs,          // The values for the WHERE clause
                null,                   // don't group the rows
                null,                   // don't filter by row groups
                sortOrder               // The sort order
        );

        return cursor;
    }
}
