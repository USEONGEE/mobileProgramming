package com.example.ucanhealth.statistic;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.ucanhealth.R;
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

import java.util.ArrayList;
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

        ImageButton bodyButton = findViewById(R.id.bodyButton);
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

        // body 버튼을 누른 경우에는 다시 운동부위 차트를 보여줌
        bodyButton.setOnClickListener(new View.OnClickListener() {
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
        });

        // 클릭한 운동 부위의 운동별 달성률
        barChart.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
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
        });
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
}