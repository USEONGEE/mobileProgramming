package com.example.ucanhealth.statistic;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.example.ucanhealth.MainActivity;
import com.example.ucanhealth.R;
import com.example.ucanhealth.recommend.RecommendRoutine;
import com.example.ucanhealth.schedule.exerciseScheduler;
import com.example.ucanhealth.sqlite.UcanHealth;
import com.example.ucanhealth.sqlite.UcanHealthDbHelper;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.google.android.material.navigation.NavigationView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class Statistics extends AppCompatActivity {

    private UcanHealthDbHelper userExerciseLogDbHelper;
    private BarChart barChart;
    private XAxis xAxis;
    private ImageButton backButton;
    private ImageButton chestButton;
    private ImageButton shoulderButton;
    private ImageButton legButton;
    private ImageButton armButton;
    private ImageButton coreButton;
    private ImageButton bodyButton;
    private float backAlpha = 0.0f;
    private float chestAlpha  = 0.0f;
    private float shoulderAlpha = 0.0f;
    private float legAlpha  = 0.0f;
    private float armAlpha = 0.0f;
    private float coreAlpha  = 0.0f;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.statistics);

        InitializeLayout();

        // View 초기화 및 부위별 이미지, 색 초기화
        init();

        // body 버튼을 누른 경우에는 다시 운동부위 차트를 보여줌
        bodyButton.setOnClickListener(onClickBodyButton);

        // 클릭한 운동 부위의 운동별 달성률
        barChart.setOnChartValueSelectedListener(onClickBarChart);
    }
    
    private void init() {
        bodyButton = findViewById(R.id.bodyButton);
        backButton = findViewById(R.id.backButton);
        backButton.setAlpha(backAlpha * 1.0f);
        chestButton = findViewById(R.id.chestButton);
        chestButton.setAlpha(chestAlpha * 1.0f);
        shoulderButton = findViewById(R.id.shoulderButton);
        shoulderButton.setAlpha(shoulderAlpha * 1.0f);
        legButton = findViewById(R.id.legButton);
        legButton.setAlpha(legAlpha * 1.0f);
        armButton = findViewById(R.id.armButton);
        armButton.setAlpha(armAlpha * 1.0f);
        coreButton = findViewById(R.id.coreButton);
        coreButton.setAlpha(coreAlpha * 1.0f);

        // 운동 부위별 달성률 차트
        barChart = findViewById(R.id.barChart);
        BarDataSet barDataSet = new BarDataSet(getExerciseTypeDataValues(), "data");
        barDataSet.setDrawValues(true);


        BarData barData = new BarData(barDataSet);
        barDataSet.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                return (String.valueOf((int)value)) + "%";
            }
        });

        int barCount = barDataSet.getEntryCount();
        float barWidth = barCount * 0.08f;
        barData.setBarWidth(barWidth);
        barData.setValueTextSize(15);

        barChart.setData(barData);
        setChart();
        xAxis.setValueFormatter(new IndexAxisValueFormatter(getExerciseType()));

        // 운동 부위에 따라 다른 색상으로 차트를 표시
        int[] colors = new int[barCount];

        for(int i = 0; i < barCount; i++) {
            if (xAxis.getValueFormatter().getFormattedValue(i).equals("back")) {
                colors[i] = Color.rgb(255, 77, 106);
            }
            if (xAxis.getValueFormatter().getFormattedValue(i).equals("chest")) {
                colors[i] = Color.rgb(151, 226, 0);
            }
            if (xAxis.getValueFormatter().getFormattedValue(i).equals("shoulder")) {
                colors[i] = Color.rgb(250, 127, 65);
            }
            if (xAxis.getValueFormatter().getFormattedValue(i).equals("leg")) {
                colors[i] = Color.rgb(0, 152, 255);
            }
            if (xAxis.getValueFormatter().getFormattedValue(i).equals("arm")) {
                colors[i] = Color.rgb(255, 189, 46);
            }
            if (xAxis.getValueFormatter().getFormattedValue(i).equals("core")) {
                colors[i] = Color.rgb(50, 240, 177);
            }
        }
        barDataSet.setColors(colors);

        barChart.invalidate();

        // 달성률에 따른 운동부위 이미지 투명도 조절
        for(int i = 0; i < barCount; i++) {
            if (xAxis.getValueFormatter().getFormattedValue(i).equals("back")) {
                backAlpha = barDataSet.getEntryForIndex(i).getY() / 100;
                backButton.setAlpha(backAlpha * 1.0f);
            }
            if (xAxis.getValueFormatter().getFormattedValue(i).equals("chest")) {
                chestAlpha = barDataSet.getEntryForIndex(i).getY() / 100;
                chestButton.setAlpha(chestAlpha * 1.0f);
            }
            if (xAxis.getValueFormatter().getFormattedValue(i).equals("shoulder")) {
                shoulderAlpha = barDataSet.getEntryForIndex(i).getY() / 100;
                shoulderButton.setAlpha(shoulderAlpha * 1.0f);
            }
            if (xAxis.getValueFormatter().getFormattedValue(i).equals("leg")) {
                legAlpha = barDataSet.getEntryForIndex(i).getY() / 100;
                legButton.setAlpha(legAlpha * 1.0f);
            }
            if (xAxis.getValueFormatter().getFormattedValue(i).equals("arm")) {
                armAlpha = barDataSet.getEntryForIndex(i).getY() / 100;
                armButton.setAlpha(armAlpha * 1.0f);
            }
            if (xAxis.getValueFormatter().getFormattedValue(i).equals("core")) {
                coreAlpha = barDataSet.getEntryForIndex(i).getY() / 100;
                coreButton.setAlpha(coreAlpha * 1.0f);
            }
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
                        Intent intent2 = new Intent(getApplicationContext(), RecommendRoutine.class);
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

    private void DeleteImage() {
        backButton.setAlpha(0.0f);
        chestButton.setAlpha(0.0f);
        shoulderButton.setAlpha(0.0f);
        legButton.setAlpha(0.0f);
        armButton.setAlpha(0.0f);
        coreButton.setAlpha(0.0f);
    }

    private void setChart() {
        barChart.animateY(2000);
        barChart.getDescription().setEnabled(false);
        barChart.getLegend().setEnabled(false);
        barChart.setDrawBarShadow(true);
        barChart.setScaleEnabled(false);
        barChart.setExtraOffsets(10f, 0f,40f,0f);
//        barChart.setBackgroundColor(Color.argb(30, 128, 128, 128));

        xAxis = barChart.getXAxis();
        xAxis.setDrawAxisLine(false);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setTextSize(15f);
        xAxis.setGranularityEnabled(true);  // xAxis에 값이 중복되어 출력되는 현상 제거

        YAxis axisLeft = barChart.getAxisLeft();
        axisLeft.setDrawGridLines(false);
        axisLeft.setDrawAxisLine(false);
        axisLeft.setAxisMinimum(0f); // 최솟값
        axisLeft.setAxisMaximum(100f); // 최댓값(100%)
        axisLeft.setDrawLabels(false);

        YAxis axisRight = barChart.getAxisRight();
        axisRight.setDrawLabels(false);
        axisRight.setDrawGridLines(false);
        axisRight.setDrawAxisLine(false);
    }

    // 운동 부위
    private ArrayList<String> getExerciseType(){
        userExerciseLogDbHelper = new UcanHealthDbHelper(this);
        SQLiteDatabase db = userExerciseLogDbHelper.getWritableDatabase();
        ArrayList<String> ExerciseType = new ArrayList<>();

        Cursor cursor = userExerciseLogDbHelper.getExerciseTypeDataBar();
        for (int i=0; i<cursor.getCount(); i++){
            cursor.moveToNext();
            ExerciseType.add(cursor.getString(2));
        }

        cursor.close();
        db.close();

        return ExerciseType;
    }

    // 운동 부위별 달성률
    private ArrayList<BarEntry> getExerciseTypeDataValues(){

        userExerciseLogDbHelper = new UcanHealthDbHelper(this);
        SQLiteDatabase db = userExerciseLogDbHelper.getWritableDatabase();
        ArrayList<BarEntry> dataVals = new ArrayList<>();

        Cursor cursor = userExerciseLogDbHelper.getExerciseTypeDataBar();
        for (int i=0; i<cursor.getCount(); i++){
            cursor.moveToNext();
            int repetition = cursor.getInt(1);
            int totalSet = cursor.getInt(0);
            float AR = (float)repetition / totalSet * 100;   // Accomplishment rate
            dataVals.add(new BarEntry(i, AR));
        }

        cursor.close();
        db.close();

        return dataVals;
    }

    // 등운동 이름
    private ArrayList<String> getBackExerciseName(){
        userExerciseLogDbHelper = new UcanHealthDbHelper(this);
        SQLiteDatabase db = userExerciseLogDbHelper.getWritableDatabase();
        ArrayList<String> ExerciseName = new ArrayList<>();

        Cursor cursor = userExerciseLogDbHelper.getBackExerciseName();
        for (int i=0; i<cursor.getCount(); i++){
            cursor.moveToNext();
            ExerciseName.add(cursor.getString(0));
        }

        cursor.close();
        db.close();

        return ExerciseName;
    }

    // 등운동별 달성률
    private ArrayList<BarEntry> getBackExerciseDataValues(){

        userExerciseLogDbHelper = new UcanHealthDbHelper(this);
        SQLiteDatabase db = userExerciseLogDbHelper.getWritableDatabase();
        ArrayList<BarEntry> dataVals = new ArrayList<>();

        Cursor cursor = userExerciseLogDbHelper.getBackExerciseDataBar();
        for (int i=0; i<cursor.getCount(); i++){
            cursor.moveToNext();
            int repetition = cursor.getInt(1);
            int totalSet = cursor.getInt(0);
            float AR = (float)repetition / totalSet * 100;   // Accomplishment rate
            dataVals.add(new BarEntry(i, AR));
        }

        cursor.close();
        db.close();

        return dataVals;
    }

    // 가슴운동 이름
    private ArrayList<String> getChestExerciseName(){
        userExerciseLogDbHelper = new UcanHealthDbHelper(this);
        SQLiteDatabase db = userExerciseLogDbHelper.getWritableDatabase();
        ArrayList<String> ExerciseName = new ArrayList<>();

        Cursor cursor = userExerciseLogDbHelper.getChestExerciseName();
        for (int i=0; i<cursor.getCount(); i++){
            cursor.moveToNext();
            ExerciseName.add(cursor.getString(0));
        }

        cursor.close();
        db.close();

        return ExerciseName;
    }

    // 가슴운동별 달성률
    private ArrayList<BarEntry> getChestExerciseDataValues(){

        userExerciseLogDbHelper = new UcanHealthDbHelper(this);
        SQLiteDatabase db = userExerciseLogDbHelper.getWritableDatabase();
        ArrayList<BarEntry> dataVals = new ArrayList<>();

        Cursor cursor = userExerciseLogDbHelper.getChestExerciseDataBar();
        for (int i=0; i<cursor.getCount(); i++){
            cursor.moveToNext();
            int repetition = cursor.getInt(1);
            int totalSet = cursor.getInt(0);
            float AR = (float)repetition / totalSet * 100;   // Accomplishment rate
            dataVals.add(new BarEntry(i, AR));
        }

        cursor.close();
        db.close();

        return dataVals;
    }

    // 어깨운동 이름
    private ArrayList<String> getShoulderExerciseName(){
        userExerciseLogDbHelper = new UcanHealthDbHelper(this);
        SQLiteDatabase db = userExerciseLogDbHelper.getWritableDatabase();
        ArrayList<String> ExerciseName = new ArrayList<>();

        Cursor cursor = userExerciseLogDbHelper.getShoulderExerciseName();
        for (int i=0; i<cursor.getCount(); i++){
            cursor.moveToNext();
            ExerciseName.add(cursor.getString(0));
        }

        cursor.close();
        db.close();

        return ExerciseName;
    }

    // 어깨운동별 달성률
    private ArrayList<BarEntry> getShoulderExerciseDataValues(){

        userExerciseLogDbHelper = new UcanHealthDbHelper(this);
        SQLiteDatabase db = userExerciseLogDbHelper.getWritableDatabase();
        ArrayList<BarEntry> dataVals = new ArrayList<>();

        Cursor cursor = userExerciseLogDbHelper.getShoulderExerciseDataBar();
        for (int i=0; i<cursor.getCount(); i++){
            cursor.moveToNext();
            int repetition = cursor.getInt(1);
            int totalSet = cursor.getInt(0);
            float AR = (float)repetition / totalSet * 100;   // Accomplishment rate
            dataVals.add(new BarEntry(i, AR));
        }

        cursor.close();
        db.close();

        return dataVals;
    }

    // 다리운동 이름
    private ArrayList<String> getLegExerciseName(){
        userExerciseLogDbHelper = new UcanHealthDbHelper(this);
        SQLiteDatabase db = userExerciseLogDbHelper.getWritableDatabase();
        ArrayList<String> ExerciseName = new ArrayList<>();

        Cursor cursor = userExerciseLogDbHelper.getLegExerciseName();
        for (int i=0; i<cursor.getCount(); i++){
            cursor.moveToNext();
            ExerciseName.add(cursor.getString(0));
        }

        cursor.close();
        db.close();

        return ExerciseName;
    }

    // 다리운동별 달성률
    private ArrayList<BarEntry> getLegExerciseDataValues(){

        userExerciseLogDbHelper = new UcanHealthDbHelper(this);
        SQLiteDatabase db = userExerciseLogDbHelper.getWritableDatabase();
        ArrayList<BarEntry> dataVals = new ArrayList<>();

        Cursor cursor = userExerciseLogDbHelper.getLegExerciseDataBar();
        for (int i=0; i<cursor.getCount(); i++){
            cursor.moveToNext();
            int repetition = cursor.getInt(1);
            int totalSet = cursor.getInt(0);
            float AR = (float)repetition / totalSet * 100;   // Accomplishment rate
            dataVals.add(new BarEntry(i, AR));
        }

        cursor.close();
        db.close();

        return dataVals;
    }

    // 팔운동 이름
    private ArrayList<String> getArmExerciseName(){
        userExerciseLogDbHelper = new UcanHealthDbHelper(this);
        SQLiteDatabase db = userExerciseLogDbHelper.getWritableDatabase();
        ArrayList<String> ExerciseName = new ArrayList<>();

        Cursor cursor = userExerciseLogDbHelper.getArmExerciseName();
        for (int i=0; i<cursor.getCount(); i++){
            cursor.moveToNext();
            ExerciseName.add(cursor.getString(0));
        }

        cursor.close();
        db.close();

        return ExerciseName;
    }

    // 팔운동별 달성률
    private ArrayList<BarEntry> getArmExerciseDataValues(){

        userExerciseLogDbHelper = new UcanHealthDbHelper(this);
        SQLiteDatabase db = userExerciseLogDbHelper.getWritableDatabase();
        ArrayList<BarEntry> dataVals = new ArrayList<>();

        Cursor cursor = userExerciseLogDbHelper.getArmExerciseDataBar();
        for (int i=0; i<cursor.getCount(); i++){
            cursor.moveToNext();
            int repetition = cursor.getInt(1);
            int totalSet = cursor.getInt(0);
            float AR = (float)repetition / totalSet * 100;   // Accomplishment rate
            dataVals.add(new BarEntry(i, AR));
        }

        cursor.close();
        db.close();

        return dataVals;
    }

    // 코어운동 이름
    private ArrayList<String> getCoreExerciseName(){
        userExerciseLogDbHelper = new UcanHealthDbHelper(this);
        SQLiteDatabase db = userExerciseLogDbHelper.getWritableDatabase();
        ArrayList<String> ExerciseName = new ArrayList<>();

        Cursor cursor = userExerciseLogDbHelper.getCoreExerciseName();
        for (int i=0; i<cursor.getCount(); i++){
            cursor.moveToNext();
            ExerciseName.add(cursor.getString(0));
        }

        cursor.close();
        db.close();

        return ExerciseName;
    }

    // 코어운동별 달성률
    private ArrayList<BarEntry> getCoreExerciseDataValues(){

        userExerciseLogDbHelper = new UcanHealthDbHelper(this);
        SQLiteDatabase db = userExerciseLogDbHelper.getWritableDatabase();
        ArrayList<BarEntry> dataVals = new ArrayList<>();

        Cursor cursor = userExerciseLogDbHelper.getCoreExerciseDataBar();
        for (int i=0; i<cursor.getCount(); i++){
            cursor.moveToNext();
            int repetition = cursor.getInt(1);
            int totalSet = cursor.getInt(0);
            float AR = (float)repetition / totalSet * 100;   // Accomplishment rate
            dataVals.add(new BarEntry(i, AR));
        }

        cursor.close();
        db.close();

        return dataVals;
    }

    private final OnChartValueSelectedListener onClickBarChart = new OnChartValueSelectedListener() {
        @Override
        public void onValueSelected(Entry e, Highlight h) {
            String selectedLabel = xAxis.getValueFormatter().getFormattedValue(e.getX()); // 선택된 x값에 해당하는 라벨 가져오기

            // 등운동 차트를 눌렀을 때
            if (selectedLabel.equals("back")) {
                DeleteImage();
                backButton.setAlpha(backAlpha * 1.0f);

                BarDataSet barDataSet = new BarDataSet(getBackExerciseDataValues(), "data");
                barDataSet.setDrawValues(true);

                BarData barData = new BarData(barDataSet);
                barDataSet.setValueFormatter(new ValueFormatter() {
                    @Override
                    public String getFormattedValue(float value) {
                        return (String.valueOf((int) value)) + "%";
                    }
                });

                int barCount = barDataSet.getEntryCount();
                float barWidth = barCount * 0.08f;
                barData.setBarWidth(barWidth);
                barData.setValueTextSize(15);

                barDataSet.setColor(Color.parseColor("#FF4D6A"));

                barChart.setData(barData);
                setChart();
                xAxis.setValueFormatter(new IndexAxisValueFormatter(getBackExerciseName()));
                barChart.invalidate();
            }

            // 가슴운동 차트를 눌렀을 때
            if (selectedLabel.equals("chest")) {
                DeleteImage();
                chestButton.setAlpha(chestAlpha * 1.0f);

                BarDataSet barDataSet = new BarDataSet(getChestExerciseDataValues(), "data");
                barDataSet.setDrawValues(true);

                BarData barData = new BarData(barDataSet);
                barDataSet.setValueFormatter(new ValueFormatter() {
                    @Override
                    public String getFormattedValue(float value) {
                        return (String.valueOf((int) value)) + "%";
                    }
                });

                int barCount = barDataSet.getEntryCount();
                float barWidth = barCount * 0.08f;
                barData.setBarWidth(barWidth);
                barData.setValueTextSize(15);

                barDataSet.setColor(Color.parseColor("#97E200"));

                barChart.setData(barData);
                setChart();
                xAxis.setValueFormatter(new IndexAxisValueFormatter(getChestExerciseName()));
                barChart.invalidate();
            }

            // 어깨운동 차트를 눌렀을 때
            if (selectedLabel.equals("shoulder")) {
                DeleteImage();
                shoulderButton.setAlpha(shoulderAlpha * 1.0f);

                BarDataSet barDataSet = new BarDataSet(getShoulderExerciseDataValues(), "data");
                barDataSet.setDrawValues(true);

                BarData barData = new BarData(barDataSet);
                barDataSet.setValueFormatter(new ValueFormatter() {
                    @Override
                    public String getFormattedValue(float value) {
                        return (String.valueOf((int) value)) + "%";
                    }
                });

                int barCount = barDataSet.getEntryCount();
                float barWidth = barCount * 0.08f;
                barData.setBarWidth(barWidth);
                barData.setValueTextSize(15);

                barDataSet.setColor(Color.parseColor("#FA7F41"));

                barChart.setData(barData);
                setChart();
                xAxis.setValueFormatter(new IndexAxisValueFormatter(getShoulderExerciseName()));
                barChart.invalidate();
            }

            // 다리운동 차트를 눌렀을 때
            if (selectedLabel.equals("leg")) {
                DeleteImage();
                legButton.setAlpha(legAlpha * 1.0f);

                BarDataSet barDataSet = new BarDataSet(getLegExerciseDataValues(), "data");
                barDataSet.setDrawValues(true);

                BarData barData = new BarData(barDataSet);
                barDataSet.setValueFormatter(new ValueFormatter() {
                    @Override
                    public String getFormattedValue(float value) {
                        return (String.valueOf((int) value)) + "%";
                    }
                });

                int barCount = barDataSet.getEntryCount();
                float barWidth = barCount * 0.08f;
                barData.setBarWidth(barWidth);
                barData.setValueTextSize(15);

                barDataSet.setColor(Color.parseColor("#0098FF"));

                barChart.setData(barData);
                setChart();
                xAxis.setValueFormatter(new IndexAxisValueFormatter(getLegExerciseName()));
                barChart.invalidate();
            }

            // 팔운동 차트를 눌렀을 때
            if (selectedLabel.equals("arm")) {
                DeleteImage();
                armButton.setAlpha(armAlpha * 1.0f);

                BarDataSet barDataSet = new BarDataSet(getArmExerciseDataValues(), "data");
                barDataSet.setDrawValues(true);

                BarData barData = new BarData(barDataSet);
                barDataSet.setValueFormatter(new ValueFormatter() {
                    @Override
                    public String getFormattedValue(float value) {
                        return (String.valueOf((int) value)) + "%";
                    }
                });

                int barCount = barDataSet.getEntryCount();
                float barWidth = barCount * 0.08f;
                barData.setBarWidth(barWidth);
                barData.setValueTextSize(15);

                barDataSet.setColor(Color.parseColor("#FFBD2E"));

                barChart.setData(barData);
                setChart();
                xAxis.setValueFormatter(new IndexAxisValueFormatter(getArmExerciseName()));
                barChart.invalidate();
            }

            // 코어운동 차트를 눌렀을 때
            if (selectedLabel.equals("core")) {
                DeleteImage();
                coreButton.setAlpha(coreAlpha * 1.0f);

                BarDataSet barDataSet = new BarDataSet(getCoreExerciseDataValues(), "data");
                barDataSet.setDrawValues(true);

                BarData barData = new BarData(barDataSet);
                barDataSet.setValueFormatter(new ValueFormatter() {
                    @Override
                    public String getFormattedValue(float value) {
                        return (String.valueOf((int) value)) + "%";
                    }
                });

                int barCount = barDataSet.getEntryCount();
                float barWidth = barCount * 0.08f;
                barData.setBarWidth(barWidth);
                barData.setValueTextSize(15);

                barDataSet.setColor(Color.parseColor("#32F0B1"));

                barChart.setData(barData);
                setChart();
                xAxis.setValueFormatter(new IndexAxisValueFormatter(getCoreExerciseName()));
                barChart.invalidate();
            }
        }

        @Override
        public void onNothingSelected() {

        }
    };
    
    private final View.OnClickListener onClickBodyButton =new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            BarDataSet barDataSet = new BarDataSet(getExerciseTypeDataValues(), "data");
            barDataSet.setDrawValues(true);


            BarData barData = new BarData(barDataSet);
            barDataSet.setValueFormatter(new ValueFormatter() {
                @Override
                public String getFormattedValue(float value) {
                    return (String.valueOf((int)value)) + "%";
                }
            });

            int barCount = barDataSet.getEntryCount();
            float barWidth = barCount * 0.08f;
            barData.setBarWidth(barWidth);
            barData.setValueTextSize(15);

            barChart.setData(barData);
            setChart();
            xAxis.setValueFormatter(new IndexAxisValueFormatter(getExerciseType()));

            // 운동 부위에 따라 다른 색상으로 차트를 표시
            int[] colors = new int[barCount];

            for(int i = 0; i < barCount; i++) {
                if (xAxis.getValueFormatter().getFormattedValue(i).equals("back")) {
                    colors[i] = Color.rgb(255, 77, 106);
                }
                if (xAxis.getValueFormatter().getFormattedValue(i).equals("chest")) {
                    colors[i] = Color.rgb(151, 226, 0);
                }
                if (xAxis.getValueFormatter().getFormattedValue(i).equals("shoulder")) {
                    colors[i] = Color.rgb(250, 127, 65);
                }
                if (xAxis.getValueFormatter().getFormattedValue(i).equals("leg")) {
                    colors[i] = Color.rgb(0, 152, 255);
                }
                if (xAxis.getValueFormatter().getFormattedValue(i).equals("arm")) {
                    colors[i] = Color.rgb(255, 189, 46);
                }
                if (xAxis.getValueFormatter().getFormattedValue(i).equals("core")) {
                    colors[i] = Color.rgb(50, 240, 177);
                }
            }
            barDataSet.setColors(colors);

            barChart.invalidate();

            // 달성률에 따른 운동부위 이미지 투명도 조절
            for(int i = 0; i < barCount; i++) {
                if (xAxis.getValueFormatter().getFormattedValue(i).equals("back")) {
                    backAlpha = barDataSet.getEntryForIndex(i).getY() / 100;
                    backButton.setAlpha(backAlpha * 1.0f);
                }
                if (xAxis.getValueFormatter().getFormattedValue(i).equals("chest")) {
                    chestAlpha = barDataSet.getEntryForIndex(i).getY() / 100;
                    chestButton.setAlpha(chestAlpha * 1.0f);
                }
                if (xAxis.getValueFormatter().getFormattedValue(i).equals("shoulder")) {
                    shoulderAlpha = barDataSet.getEntryForIndex(i).getY() / 100;
                    shoulderButton.setAlpha(shoulderAlpha * 1.0f);
                }
                if (xAxis.getValueFormatter().getFormattedValue(i).equals("leg")) {
                    legAlpha = barDataSet.getEntryForIndex(i).getY() / 100;
                    legButton.setAlpha(legAlpha * 1.0f);
                }
                if (xAxis.getValueFormatter().getFormattedValue(i).equals("arm")) {
                    armAlpha = barDataSet.getEntryForIndex(i).getY() / 100;
                    armButton.setAlpha(armAlpha * 1.0f);
                }
                if (xAxis.getValueFormatter().getFormattedValue(i).equals("core")) {
                    coreAlpha = barDataSet.getEntryForIndex(i).getY() / 100;
                    coreButton.setAlpha(coreAlpha * 1.0f);
                }
            }
        }
    };

    // 신체 부위를 인자로 받아서 해당하는 운동기록을 반환
    // 테스트 중
    private Cursor getClickedExerciseDetail(String body) {
        userExerciseLogDbHelper = new UcanHealthDbHelper(this);
        SQLiteDatabase db = userExerciseLogDbHelper.getWritableDatabase();

        String[] projection = {
                UcanHealth.UserExerciseLogEntry.COLUMN_EXERCISE,
                UcanHealth.UserExerciseLogEntry.COLUMN_SET_COUNT,
                UcanHealth.UserExerciseLogEntry.COLUMN_TOTAL_SET_COUNT
        };

        String sortOrder = UcanHealth.UserExerciseLogEntry.COLUMN_ORDER + " ASC";

        String selection = String.format("%s  = ? %s >= ? AND %s <= ? ",UcanHealth.UserExerciseLogEntry.COLUMN_EXERCISE,
                UcanHealth.UserExerciseLogEntry.COLUMN_DATE,
                UcanHealth.UserExerciseLogEntry.COLUMN_DATE);
        String prevDate = "";
        String nextDate = "";

        Calendar calendar = Calendar.getInstance();
        int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
        switch (dayOfWeek) {
            case 1 : // 월요일
                prevDate = "date('now')"; nextDate = "date('now', '+6 days')";
                break;
            case 2:
                prevDate = "date('now', '-1 days')"; nextDate = "date('now', '+5 days')";
                break;
            case 3:
                prevDate = "date('now', '-2 days')"; nextDate = "date('now', '+4 days')";
                break;
            case 4:
                prevDate = "date('now', '-3 days')"; nextDate = "date('now', '+3 days')";
                break;
            case 5:
                prevDate = "date('now', '-4 days')"; nextDate = "date('now', '+2 days')";
                break;
            case 6:
                prevDate = "date('now', '-5 days')"; nextDate = "date('now', '+1 days')";
                break;
            case 7: // 일요일
                prevDate = "date('now', '-6 days')"; nextDate = "date('now')";
                break;
        }

        String[] selectionArgs = {
                body,
                prevDate,
                nextDate
        };

        Cursor cursor = db.query(
                UcanHealth.UserExerciseLogEntry.TABLE_NAME + " INNER JOIN " + UcanHealth.ExerciseTypeEntry.TABLE_NAME +
                " ON " + UcanHealth.UserExerciseLogEntry.TABLE_NAME + "." + UcanHealth.UserExerciseLogEntry.COLUMN_EXERCISE + " = " +
                UcanHealth.ExerciseTypeEntry.TABLE_NAME+ "." + UcanHealth.ExerciseTypeEntry.COLUMN_EXERCISE,   // The table to query
                projection,             // The array of columns to return (pass null to get all)
                selection,              // The columns for the WHERE clause
                selectionArgs,          // The values for the WHERE clause
                null,                   // don't group the rows
                null,                   // don't filter by row groups
                sortOrder               // The sort order
        );
        userExerciseLogDbHelper.close();
        db.close();

        return cursor;
    }

    public String getCurrentDate() {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH) + 1; // month는 0부터 시작하므로 1을 더해줍니다.
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        Log.i("stirng", String.format("%04d-%02d-%02d", year, month, day));
        return String.format("%04d-%02d-%02d", year, month, day);
    }
}