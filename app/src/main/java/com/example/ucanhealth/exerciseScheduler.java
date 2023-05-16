package com.example.ucanhealth;

import androidx.appcompat.app.AppCompatActivity;
import android.annotation.SuppressLint;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CalendarView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import android.content.Context;
import androidx.core.content.ContextCompat;
import android.database.Cursor;

import com.example.ucanhealth.sqlite.UcanHealth;
import com.example.ucanhealth.sqlite.UcanHealthDbHelper;

import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;

public class exerciseScheduler extends AppCompatActivity {


    public String readDay = null;
    public String str = null;
    public CalendarView calendarView;
    public TextView exerciseData_View, diaryTextView; //운동한 분량을 보여주는 공간

    private UcanHealthDbHelper dbHelper;
    private SQLiteDatabase db_write;
    private SQLiteDatabase db_read;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exercise_scheduler);

        calendarView = findViewById(R.id.calendarView); // 위의 달력 표시
        diaryTextView = findViewById(R.id.diaryTextView); // 달력에서 자신이 고른 날짜를 표시
        exerciseData_View = findViewById(R.id.exerciseData_View); // 운동한 데이터를 db에서 가져온다.


        //데이터 베이스 선언
        dbHelper = new UcanHealthDbHelper(getApplicationContext());
        db_write = dbHelper.getWritableDatabase();
        //db_read = dbHelper.getReadableDatabase();

        //임의 값 대입
        //inialdata();

        //Cursor exerciseSD = db.rawQuery("SELECT * FROM UserExerciseLog", null);

        UcanHealthDbHelper dbHelper = new UcanHealthDbHelper(this);
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        Cursor cursor = db.rawQuery("SELECT * FROM UserExerciseLog WHERE date = 123;",null);


        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener()
        {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth)
            {
                int month_new = month + 1;

                String date = year + "-" + month_new + "-" + dayOfMonth;


                diaryTextView.setVisibility(View.VISIBLE); // 다이어리뷰
                exerciseData_View.setVisibility(View.VISIBLE); // 다이어리뷰
                diaryTextView.setText(String.format("%d / %d / %d", year, month + 1, dayOfMonth)); // 선택한 날짜 표기

                getString();

            }
        });

    }

    public void getString() {

        //Cursor cursor = db_read.rawQuery("SELECT * FROM UserExerciseLog where date_ = ?", new String[]{"2023-05-23"});
        db_read = dbHelper.getReadableDatabase();

        String sql = String.format("SELECT * FROM %s WHERE %s = '%s'", UcanHealth.UserExerciseLogEntry.TABLE_NAME, UcanHealth.UserExerciseLogEntry.COLUMN_DATE,"2023-05-23");

        Cursor cursor = db_read.rawQuery(sql,null);
        //Cursor cursor = db_read.rawQuery("SELECT * FROM " + UcanHealth.UserExerciseLogEntry.TABLE_NAME + " WHERE id = ?", new String[]{"2023-05-17"});

        int count = cursor.getCount(); // 해당 요일의 데이터 행 갯수

        String exercise[] = new String[500];
        int repetition[] = new int[500];
        int weight[] = new int[500];
        int set_count[] = new int[500];
        int total_set_count[] = new int[500];
        String date_[] = new String[500];
        int total_exercise_time[] = new int[500];
        int exercise_order[] = new int[500];

        cursor.moveToFirst();

        for(int i = 0; i < count; i++){
            exercise[i] = cursor.getString(1);
            repetition[i] = cursor.getInt(2);
            weight[i] = cursor.getInt(3);
            set_count[i] = cursor.getInt(4);
            total_set_count[i] = cursor.getInt(5);
            date_[i] = cursor.getString(6);
            total_exercise_time[i] = cursor.getInt(7);
            exercise_order[i] = cursor.getInt(8);

            Log.i("cursor_test", "succuse");
        }

        for(int i = 0; i < count; i++){
            exerciseData_View.setText(exercise[i] + " ," + repetition[i]  + " ," +  weight[i]  + " ," +  set_count[i]  + " ," +  total_set_count[i]  + " ," +  date_[i]  + " ," +  total_exercise_time[i]  + " ," +  exercise_order[i]);
        }

        db_read.close();
    }

    //임의 값 삽입
    public void inialdata(){

        ContentValues cv = new ContentValues();
        cv.put(UcanHealth.UserExerciseLogEntry.COLUMN_EXERCISE, "neck exercise");
        cv.put(UcanHealth.UserExerciseLogEntry.COLUMN_REPS, 10);
        cv.put(UcanHealth.UserExerciseLogEntry.COLUMN_WEIGHT, 50);
        cv.put(UcanHealth.UserExerciseLogEntry.COLUMN_SET_COUNT, 5);
        cv.put(UcanHealth.UserExerciseLogEntry.COLUMN_TOTAL_SET_COUNT, 5);
        cv.put(UcanHealth.UserExerciseLogEntry.COLUMN_DATE, "2023-05-23");
        cv.put(UcanHealth.UserExerciseLogEntry.COLUMN_TOTAL_EXERCISE_TIME, 50);
        cv.put(UcanHealth.UserExerciseLogEntry.COLUMN_ORDER, 3);

        long inialData = db_write.insert(UcanHealth.UserExerciseLogEntry.TABLE_NAME,null, cv);

        if(inialData == -1)
            Log.i("insert", "fail");
        else
            Log.i("insert", "success");

    }

    public void println(String data){
        exerciseData_View.append(data + "\n");
    }


    @SuppressLint("WrongConstant")
    public void removeDiary(String readDay)
    {
        FileOutputStream fos;
        try
        {
            fos = openFileOutput(readDay, MODE_NO_LOCALIZED_COLLATORS);
            String content = "";
            fos.write((content).getBytes());
            fos.close();

        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

}

