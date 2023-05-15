package com.example.ucanhealth;

import androidx.appcompat.app.AppCompatActivity;
import android.annotation.SuppressLint;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.CalendarView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;

import com.example.ucanhealth.sqlite.UcanHealthDbHelper;

import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class exerciseScheduler extends AppCompatActivity {


    public String readDay = null;
    public String str = null;
    public CalendarView calendarView;
    public TextView exerciseData_View, diaryTextView; //운동한 분량을 보여주는 공간

    private SQLiteDatabase ucanHealthDb_read;
    private UcanHealthDbHelper userExerciseLogDbhelper; // db 선언

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exercise_scheduler);

        calendarView = findViewById(R.id.calendarView); // 위의 달력 표시
        diaryTextView = findViewById(R.id.diaryTextView); // 달력에서 자신이 고른 날짜를 표시
        exerciseData_View = findViewById(R.id.exerciseData_View); // 운동한 데이터를 db에서 가져온다.

        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener()
        {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth)
            {

                diaryTextView.setVisibility(View.VISIBLE);
                exerciseData_View.setVisibility(View.VISIBLE);
                diaryTextView.setText(String.format("%d / %d / %d", year, month + 1, dayOfMonth));

                Cursor cursor = userExerciseLogDbhelper.getRoutineByDate(ucanHealthDb_read, getCurrentDate());

            }
        });

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

    public String getCurrentDate() {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH) + 1; // month는 0부터 시작하므로 1을 더해줍니다.
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        return String.format("%04d-%02d-%02d", year, month, day);
    }
}

