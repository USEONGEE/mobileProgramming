package com.example.ucanhealth;

import androidx.appcompat.app.AppCompatActivity;
import android.annotation.SuppressLint;

import android.os.Bundle;
import android.view.View;
import android.widget.CalendarView;
import android.widget.TextView;
import androidx.annotation.NonNull;

import com.example.ucanhealth.sqlite.UcanHealthDbHelper;

import java.io.FileOutputStream;

public class exerciseScheduler extends AppCompatActivity {


    public String readDay = null;
    public String str = null;
    public CalendarView calendarView;
    public TextView exerciseData_View, diaryTextView; //운동한 분량을 보여주는 공간

    private UcanHealthDbHelper dbHelper;

//    final static String dbName = "T3.db"; // db 생성 파일 이름
//    final static int dbVersion = 1; // db 버전


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exercise_scheduler);

        calendarView = findViewById(R.id.calendarView);
        diaryTextView = findViewById(R.id.diaryTextView);
        exerciseData_View = findViewById(R.id.exerciseData_View);

        dbHelper = new UcanHealthDbHelper(this);

        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener()
        {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth)
            {

                diaryTextView.setVisibility(View.VISIBLE);
                exerciseData_View.setVisibility(View.VISIBLE);
                diaryTextView.setText(String.format("%d / %d / %d", year, month + 1, dayOfMonth));


                //exerciseData_View.setText();
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
}

