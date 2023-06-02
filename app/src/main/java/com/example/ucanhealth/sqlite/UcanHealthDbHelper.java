package com.example.ucanhealth.sqlite;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class UcanHealthDbHelper extends SQLiteOpenHelper {
    private static final String SQL_CREATE_ENTRIES_ExerciseType =
            "CREATE TABLE " + UcanHealth.ExerciseTypeEntry.TABLE_NAME + " (" +
                    UcanHealth.ExerciseTypeEntry._ID + " INTEGER PRIMARY KEY," +
                    UcanHealth.ExerciseTypeEntry.COLUMN_CATEGORY  + " TEXT," +
                    UcanHealth.ExerciseTypeEntry.COLUMN_EXERCISE_TYPE + " TEXT," +
                    UcanHealth.ExerciseTypeEntry.COLUMN_SHOW + " INTEGER," +
                    UcanHealth.ExerciseTypeEntry.COLUMN_EXERCISE + " TEXT UNIQUE)";

    private static final String SQL_CREATE_ENTRIES_UserExerciseLog =
            "CREATE TABLE " + UcanHealth.UserExerciseLogEntry.TABLE_NAME + " (" +
                    UcanHealth.UserExerciseLogEntry._ID + " INTEGER PRIMARY KEY," +
                    UcanHealth.UserExerciseLogEntry.COLUMN_EXERCISE  + " TEXT," +
                    UcanHealth.UserExerciseLogEntry.COLUMN_REPS  + " INTEGER," +
                    UcanHealth.UserExerciseLogEntry.COLUMN_WEIGHT  + " REAL," +
                    UcanHealth.UserExerciseLogEntry.COLUMN_SET_COUNT  + " INTEGER," +
                    UcanHealth.UserExerciseLogEntry.COLUMN_TOTAL_SET_COUNT + " INTEGER," +
                    UcanHealth.UserExerciseLogEntry.COLUMN_DATE + " TEXT," +
                    UcanHealth.UserExerciseLogEntry.COLUMN_REST_TIME + " INTEGER," +
                    UcanHealth.UserExerciseLogEntry.COLUMN_ORDER + " INTEGER)";
    private static final String SQL_CREATE_ENTRIES_TotalExerciseTime =
            "CREATE TABLE " + UcanHealth.TotalExerciseTimeEntry.TABLE_NAME + " (" +
                    UcanHealth.TotalExerciseTimeEntry.COLUMN_DATE + " TEXT PRIMARY KEY," +
                    UcanHealth.TotalExerciseTimeEntry.COLUMN_TOTAL_EXERCISE_TIME + " INTEGER)";


    public static final String DATABASE_NAME = "UcanHealth.db";
    public static final int DATABASE_VERSION = 2;
    private static final String SQL_DELETE_ENTRIES_ExerciseType =
            "DROP TABLE IF EXISTS " + UcanHealth.ExerciseTypeEntry.TABLE_NAME;
    private static final String SQL_DELETE_ENTRIES_UserExerciseLog =
            "DROP TABLE IF EXISTS " + UcanHealth.UserExerciseLogEntry.TABLE_NAME;
    private static final String SQL_DELETE_ENTRIES_TotalExerciseTime =
            "DROP TABLE IF EXISTS " + UcanHealth.TotalExerciseTimeEntry.TABLE_NAME;

    public UcanHealthDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_ENTRIES_ExerciseType);
        db.execSQL(SQL_CREATE_ENTRIES_UserExerciseLog);
        db.execSQL(SQL_CREATE_ENTRIES_TotalExerciseTime);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(SQL_DELETE_ENTRIES_ExerciseType);
        db.execSQL(SQL_DELETE_ENTRIES_UserExerciseLog);
        db.execSQL(SQL_DELETE_ENTRIES_TotalExerciseTime);
        onCreate(db);
    }

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }
    public Cursor getRoutineByDate(SQLiteDatabase db, String date) {
        String[] projection = {
                UcanHealth.UserExerciseLogEntry.COLUMN_EXERCISE,
                UcanHealth.UserExerciseLogEntry.COLUMN_REPS,
                UcanHealth.UserExerciseLogEntry.COLUMN_WEIGHT,
                UcanHealth.UserExerciseLogEntry.COLUMN_SET_COUNT,
                UcanHealth.UserExerciseLogEntry.COLUMN_TOTAL_SET_COUNT,
                UcanHealth.UserExerciseLogEntry.COLUMN_DATE,
                UcanHealth.UserExerciseLogEntry.COLUMN_ORDER
        };
        String sortOrder = UcanHealth.UserExerciseLogEntry.COLUMN_ORDER + " DESC";

        String selection = String.format("%s = ?",UcanHealth.UserExerciseLogEntry.COLUMN_DATE);
        String[] selectionArgs = {date};
        Cursor cursor = db.query(
                UcanHealth.UserExerciseLogEntry.TABLE_NAME,   // The table to query
                projection,             // The array of columns to return (pass null to get all)
                selection,              // The columns for the WHERE clause
                selectionArgs,          // The values for the WHERE clause
                null,                   // don't group the rows
                null,                   // don't filter by row groups
                sortOrder               // The sort order
        );

        return cursor;
    }

    // 나중에 repetition을 set_count로 변경하기
    // 운동 부위 & 운동 부위별 달성률
    public Cursor getExerciseTypeDataBar(){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor c = db.rawQuery("SELECT sum(total_set_count), sum(repetition), exercise_type from ExerciseType, UserExerciseLog where ExerciseType.exercise = UserExerciseLog.exercise group by ExerciseType.exercise_type order by exercise_order desc;", null);
        return c;
    }

    // 등운동 이름
    public Cursor getBackExerciseName(){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor c = db.rawQuery("SELECT exercise from ExerciseType where exercise_type = 'back';", null);
        return c;
    }

    // 등운동별 달성률
    public Cursor getBackExerciseDataBar(){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor c = db.rawQuery("SELECT total_set_count, repetition from ExerciseType, UserExerciseLog where ExerciseType.exercise = UserExerciseLog.exercise and ExerciseType.exercise_type = 'back';", null);
        return c;
    }

    // 가슴운동 이름
    public Cursor getChestExerciseName(){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor c = db.rawQuery("SELECT exercise from ExerciseType where exercise_type = 'chest';", null);
        return c;
    }

    // 가슴운동별 달성률
    public Cursor getChestExerciseDataBar(){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor c = db.rawQuery("SELECT total_set_count, repetition from ExerciseType, UserExerciseLog where ExerciseType.exercise = UserExerciseLog.exercise and ExerciseType.exercise_type = 'chest';", null);
        return c;
    }

    // 어깨운동 이름
    public Cursor getShoulderExerciseName(){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor c = db.rawQuery("SELECT exercise from ExerciseType where exercise_type = 'shoulder';", null);
        return c;
    }

    // 어깨운동별 달성률
    public Cursor getShoulderExerciseDataBar(){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor c = db.rawQuery("SELECT total_set_count, repetition from ExerciseType, UserExerciseLog where ExerciseType.exercise = UserExerciseLog.exercise and ExerciseType.exercise_type = 'shoulder';", null);
        return c;
    }

    // 다리운동 이름
    public Cursor getLegExerciseName(){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor c = db.rawQuery("SELECT exercise from ExerciseType where exercise_type = 'leg';", null);
        return c;
    }

    // 다리운동별 달성률
    public Cursor getLegExerciseDataBar(){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor c = db.rawQuery("SELECT total_set_count, repetition from ExerciseType, UserExerciseLog where ExerciseType.exercise = UserExerciseLog.exercise and ExerciseType.exercise_type = 'leg';", null);
        return c;
    }

    // 팔운동 이름
    public Cursor getArmExerciseName(){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor c = db.rawQuery("SELECT exercise from ExerciseType where exercise_type = 'arm';", null);
        return c;
    }

    // 팔운동별 달성률
    public Cursor getArmExerciseDataBar(){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor c = db.rawQuery("SELECT total_set_count, repetition from ExerciseType, UserExerciseLog where ExerciseType.exercise = UserExerciseLog.exercise and ExerciseType.exercise_type = 'arm';", null);
        return c;
    }

    // 코어운동 이름
    public Cursor getCoreExerciseName(){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor c = db.rawQuery("SELECT exercise from ExerciseType where exercise_type = 'core';", null);
        return c;
    }

    // 코어운동별 달성률
    public Cursor getCoreExerciseDataBar(){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor c = db.rawQuery("SELECT total_set_count, repetition from ExerciseType, UserExerciseLog where ExerciseType.exercise = UserExerciseLog.exercise and ExerciseType.exercise_type = 'core';", null);
        return c;
    }
}
