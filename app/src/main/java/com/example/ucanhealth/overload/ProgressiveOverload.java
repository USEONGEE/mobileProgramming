package com.example.ucanhealth.overload;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.example.ucanhealth.MainActivity;
import com.example.ucanhealth.R;
import com.example.ucanhealth.recommend.menu_Routine;
import com.example.ucanhealth.schedule.exerciseScheduler;
import com.example.ucanhealth.sqlite.UcanHealth;
import com.example.ucanhealth.sqlite.UcanHealthDbHelper;
import com.example.ucanhealth.statistic.Statistics;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.android.material.navigation.NavigationView;

import java.util.ArrayList;
import java.util.Calendar;

public class ProgressiveOverload extends AppCompatActivity {
    UcanHealthDbHelper dbHelper;
    SQLiteDatabase db;
    int[] colors;
    PieChart lastWeekPieChart;
    PieChart thisWeekPieChart;
    ImageView lastWeakImage;
    ImageView lastHardImage;
    ImageView thisWeakImage;
    ImageView thisHardImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.progressive_overload);

        InitializeLayout();
        init();

        ///
        setLastWeekPieChart();
        setThisWeekPieChart();
        ///

        ProgressiveOverloadState();
    }

    private void init() {
        colors = new int[] {
                Color.rgb(255, 189, 46),    // arm
                Color.rgb(255, 77, 106),    // back
                Color.rgb(151, 226, 0),     // chest
                Color.rgb(50, 240, 177),    // core
                Color.rgb(0, 152, 255),     // leg
                Color.rgb(250, 127, 65)     // shoulder
        };
        lastWeekPieChart = findViewById(R.id.lastPieChart);
        thisWeekPieChart = findViewById(R.id.thisPieChart);

        dbHelper = new UcanHealthDbHelper(this);
        db = dbHelper.getReadableDatabase();

        lastWeakImage = findViewById(R.id.lastWeekWeak);
        lastHardImage = findViewById(R.id.lastWeekHard);
        thisWeakImage = findViewById(R.id.thisWeekWeak);
        thisHardImage = findViewById(R.id.thisWeekHard);
    }

    private void ProgressiveOverloadState() {
        // set_count 총합을 비교
        // 이번 주 set_count 총합
        Cursor thisCursor = getCurrWeekData();

        int thisSetCount = 0;
        int sum_thisSetCount = 0;
        while(thisCursor.moveToNext()) {
            thisSetCount += Integer.parseInt(thisCursor.getString(1));
        }
        thisCursor.moveToFirst(); thisCursor.moveToPrevious();

        while(thisCursor.moveToNext()) {
            sum_thisSetCount = Integer.parseInt(thisCursor.getString(1));
        }

        // 지난 주 set_count 총합
        Cursor lastCursor = getLastWeekData();

        int lastSetCount = 0;
        int sum_lastSetCount = 0;
        while(lastCursor.moveToNext()) {
            lastSetCount += Integer.parseInt(lastCursor.getString(1));
        }
        lastCursor.moveToFirst(); lastCursor.moveToPrevious();

        while(lastCursor.moveToNext()) {
            sum_lastSetCount = Integer.parseInt(lastCursor.getString(1));
        }

        if(sum_lastSetCount < sum_thisSetCount){
            lastWeakImage.setVisibility(View.VISIBLE);
            thisHardImage.setVisibility(View.VISIBLE);
        }
        else {
            lastHardImage.setVisibility(View.VISIBLE);
            thisWeakImage.setVisibility(View.VISIBLE);
        }

    }

    private void setThisWeekPieChart() {
        ArrayList<PieEntry> exerciseType = new ArrayList<>();
        Cursor cursor = getCurrWeekData();

        int all = 0;
        while(cursor.moveToNext()) {
            all += Integer.parseInt(cursor.getString(1));
        }
        cursor.moveToFirst(); cursor.moveToPrevious();

        while(cursor.moveToNext()) {
            int sum_total = Integer.parseInt(cursor.getString(0));
            int sum_setCount = Integer.parseInt(cursor.getString(1));
            exerciseType.add(new PieEntry((float)sum_setCount / all * 100,cursor.getString(2)));
            Log.i("overload : 신체 부위", cursor.getString(2));
            Log.i("overload: 세트값" , cursor.getString(1));
            Log.i("overload: 전체값", cursor.getString(0));
        }

        PieDataSet pieDataSet = new PieDataSet(exerciseType, "");
        pieDataSet.setColors(colors);
        pieDataSet.setValueTextColor(Color.BLACK);
        pieDataSet.setValueTextSize(16f);


        PieData pieData = new PieData(pieDataSet);

        thisWeekPieChart.setData(pieData);
        thisWeekPieChart.getDescription().setEnabled(false);
        thisWeekPieChart.setCenterText("Exercise Type");
        thisWeekPieChart.animate();
    }

    private void setLastWeekPieChart() {
        ArrayList<PieEntry> exerciseType = new ArrayList<>();
        Cursor cursor = getLastWeekData();

        int all = 0;
        while(cursor.moveToNext()) {
            all += Integer.parseInt(cursor.getString(1));
        }
        cursor.moveToFirst(); cursor.moveToPrevious();

        while(cursor.moveToNext()) {
            int sum_total = Integer.parseInt(cursor.getString(0));
            int sum_setCount = Integer.parseInt(cursor.getString(1));
            exerciseType.add(new PieEntry((float)sum_setCount / all * 100,cursor.getString(2)));
            Log.i("overload : 신체 부위", cursor.getString(2));
            Log.i("overload: 세트값" , cursor.getString(1));
            Log.i("overload: 전체값", cursor.getString(0));
        }

        PieDataSet pieDataSet = new PieDataSet(exerciseType, "");
        pieDataSet.setColors(colors);
        pieDataSet.setValueTextColor(Color.BLACK);
        pieDataSet.setValueTextSize(16f);

        PieData pieData = new PieData(pieDataSet);

        lastWeekPieChart.setData(pieData);
        lastWeekPieChart.getDescription().setEnabled(false);
        lastWeekPieChart.setCenterText("Exercise Type");
        lastWeekPieChart.animate();
    }

    public Cursor getCurrWeekData() {
        ArrayList<String> date = getWeekDate();

        String[] projection = {
                "sum(total_set_count)",
                "sum(set_count)",
                "exercise_type"
        };

        String selection = String.format("%s = %s AND %s IN(?, ?, ?, ?, ?, ?, ?)",
                UcanHealth.ExerciseTypeEntry.TABLE_NAME+ "."+ UcanHealth.ExerciseTypeEntry.COLUMN_EXERCISE,
                UcanHealth.UserExerciseLogEntry.TABLE_NAME+ "."+ UcanHealth.UserExerciseLogEntry.COLUMN_EXERCISE,
                UcanHealth.UserExerciseLogEntry.COLUMN_DATE );

        String[] selectionArgs = {
                date.get(0),
                date.get(1),
                date.get(2),
                date.get(3),
                date.get(4),
                date.get(5),
                date.get(6)
        };

        String group = UcanHealth.ExerciseTypeEntry.COLUMN_EXERCISE_TYPE;

        Cursor cursor = db.query(
                UcanHealth.UserExerciseLogEntry.TABLE_NAME + ", " + UcanHealth.ExerciseTypeEntry.TABLE_NAME,   // The table to query
                projection,             // The array of columns to return (pass null to get all)
                selection,              // The columns for the WHERE clause
                selectionArgs,          // The values for the WHERE clause
                group,                   // don't group the rows
                null,                   // don't filter by row groups
                null               // The sort order
        );

        return cursor;
    }

    public ArrayList<String> getWeekDate() {
        Calendar calendar = Calendar.getInstance();
        ArrayList<String> result = new ArrayList();

        int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH) + 1; // month는 0부터 시작하므로 1을 더해줍니다.
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        int count = 0;
        dayOfWeek = -dayOfWeek;
        while(count < 7){
            Date date = new Date(year,month,day + dayOfWeek + 2);
            result.add(date.getDate());
            Log.i("dayOfWeek",String.valueOf(dayOfWeek));
            Log.i("getDate()",date.getDate());

            dayOfWeek++; count++;
        }

        return result;
    }

    public Cursor getLastWeekData() {
        ArrayList<String> date = getLastWeekDate();

        String[] projection = {
                "sum(total_set_count)",
                "sum(set_count)",
                "exercise_type"
        };

        String selection = String.format("%s = %s AND %s IN(?, ?, ?, ?, ?, ?, ?)",
                UcanHealth.ExerciseTypeEntry.TABLE_NAME+ "."+ UcanHealth.ExerciseTypeEntry.COLUMN_EXERCISE,
                UcanHealth.UserExerciseLogEntry.TABLE_NAME+ "."+ UcanHealth.UserExerciseLogEntry.COLUMN_EXERCISE,
                UcanHealth.UserExerciseLogEntry.COLUMN_DATE );

        String[] selectionArgs = {
                date.get(0),
                date.get(1),
                date.get(2),
                date.get(3),
                date.get(4),
                date.get(5),
                date.get(6)
        };

        String group = UcanHealth.ExerciseTypeEntry.COLUMN_EXERCISE_TYPE;

        Cursor cursor = db.query(
                UcanHealth.UserExerciseLogEntry.TABLE_NAME + ", " + UcanHealth.ExerciseTypeEntry.TABLE_NAME,   // The table to query
                projection,             // The array of columns to return (pass null to get all)
                selection,              // The columns for the WHERE clause
                selectionArgs,          // The values for the WHERE clause
                group,                   // don't group the rows
                null,                   // don't filter by row groups
                null               // The sort order
        );

        return cursor;
    }


    public ArrayList<String> getLastWeekDate() {
        Calendar calendar = Calendar.getInstance();
        ArrayList<String> result = new ArrayList();

        int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH) + 1; // month는 0부터 시작하므로 1을 더해줍니다.
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        int count = 0;
        dayOfWeek = -dayOfWeek;
        while(count < 7){
            Date date = new Date(year,month,day + dayOfWeek - 5);
            result.add(date.getDate());
            Log.i("dayOfWeek",String.valueOf(dayOfWeek));
            Log.i("getDate()",date.getDate());

            dayOfWeek++; count++;
        }

        return result;
    }

    // 7일을 각각 저장하는 class
    class Date{
        private int year;
        private int month;
        private int day;

        Date(int year, int month, int day) {
            this.year = year; this.month = month; this.day = day;
            converting();
        }

        void dayIsMinus(){
            // 8 -> 7월 은 31일임
            // 1월 -> 12월은 연도가 넘어감
            // 3 -> 2 월은 28일임
            if(month == 5 ||month == 7 ||month == 10 ||month == 12 ){
                month--;
                day = 30 + day;
            }
            else if(month == 3) {
                month--;
                day = 28 + day;
            }
            else if(month == 8){
                month--;
                day = 31 + day;
            }
            else if(month == 1) {
                month = 12;
                year--;
                day = 31 + day;
            }
            else {
                month--;
                day = 31 + day;
            }
        }

        boolean isMinus(){
            return day <= 0;
        }

        boolean isOver() {
            if(month == 1 || month == 3 || month == 5 || month == 7 || month == 8 || month == 10 || month == 12){
                if(day > 31) return true;
                else return false;
            }
            else if (month == 2){
                if(day > 28) return true;
                else return false;
            }
            else{
                if(day > 30) return true;
                else return false;
            }
        }

        void dayIsOver() {
            if(month == 12) {
                month = 1;
                year++;
                day = day - 31;
            }
            else if(month == 1 || month == 3 || month == 5 || month == 7 || month == 8 || month == 10){
                month++;
                day = day - 31;
            }
            else if(month == 2) {
                month++;
                day = day - 28;
            }
            else{
                month++;
                day = day - 30;
            }
        }

        void converting(){
            if(isMinus()) {
                Log.i("converting","day is minus");
                dayIsMinus();
                Log.i("after converting",String.valueOf(day));
            };
            if(isOver()) {
                Log.i("converting","day is over");
                dayIsOver();
            }
        }

        String getDate() {
            return String.format("%04d-%02d-%02d", year, month, day);
        }
    }

    public void InitializeLayout() {
        // toolBar를 통해 App Bar 생성
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();

        // App Bar의 좌측 영영에 Drawer를 Open 하기 위한 Icon 추가
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeAsUpIndicator(R.drawable.menuicon);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);

        ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(
                this,
                drawer,
                toolbar,
                R.string.open,
                R.string.closed);
        drawer.addDrawerListener(actionBarDrawerToggle);
        // navigation 객체에 nav_view의 참조 반환
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        // navigation 객체에 이벤트 리스너 달기
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                Log.i("clicked", String.valueOf(menuItem.getItemId()) + " selected");
                switch (menuItem.getItemId()) {
                    case R.id.MainPage:
                        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                        startActivity(intent);
                        break;
                    case R.id.recommendRoutine:
                        Intent intent2 = new Intent(getApplicationContext(), menu_Routine.class);
                        startActivity(intent2);
                        break;
                    case R.id.SchdulerPage:
                        Intent intent3 = new Intent(getApplicationContext(), exerciseScheduler.class);
                        startActivity(intent3);
                        break;
                    case R.id.statistic:
                        Intent intent4 = new Intent(getApplicationContext(), Statistics.class);
                        startActivity(intent4);
                        break;
                    case R.id.overload:
                        Intent intent5 = new Intent(getApplicationContext(), ProgressiveOverload.class);
                        startActivity(intent5);
                        break;
                }
                drawer.closeDrawer(GravityCompat.START);
                return false;
            }
        });
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }
}