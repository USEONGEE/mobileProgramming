package com.example.ucanhealth;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

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

public class Statistics extends AppCompatActivity {

    private UcanHealthDbHelper userExerciseLogDbHelper;
    private BarChart barChart;
    private XAxis xAxis;
    private int state = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.statistics);

        // 운동 부위별 달성률
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
        barData.setBarWidth(0.4f);
        barData.setValueTextSize(15);

        barChart.setData(barData);
        setChart();
        barChart.invalidate();

        // 클릭한 운동 부위의 운동별 달성률
        barChart.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
            @Override
            public void onValueSelected(Entry e, Highlight h) {
                // get the index of selected bar
                int x = barChart.getBarData().getDataSetForEntry(e).getEntryIndex((BarEntry)e);
                state++;

                BarDataSet barDataSet = new BarDataSet(getExerciseDataValues(), "data");
                barDataSet.setDrawValues(true);

                BarData barData = new BarData(barDataSet);
                barDataSet.setValueFormatter(new ValueFormatter() {
                    @Override
                    public String getFormattedValue(float value) {
                        return (String.valueOf((int)value)) + "%";
                    }
                });
                barData.setBarWidth(0.4f);
                barData.setValueTextSize(15);

                barChart.setData(barData);
                setChart();
                barChart.invalidate();
            }

            @Override
            public void onNothingSelected() {

            }
        });
    }

    private void setChart() {
        barChart.animateY(2000);
        barChart.getDescription().setEnabled(false);
        barChart.getLegend().setEnabled(false);
        barChart.setExtraOffsets(10f, 0f,40f,0f);

        xAxis = barChart.getXAxis();
        if(state == 0)
            xAxis.setValueFormatter(new IndexAxisValueFormatter(getExerciseType()));
        else
            xAxis.setValueFormatter(new IndexAxisValueFormatter(getExerciseName()));
        xAxis.setDrawAxisLine(false);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setTextSize(15f);
        xAxis.setGridLineWidth(25f);
        xAxis.setGridColor(Color.parseColor("#80E5E5E5"));
        xAxis.setGranularity(1f);
        xAxis.setGranularityEnabled(true);  // xAxis에 값이 중복되어 출력되는 현상 제거

        YAxis axisLeft = barChart.getAxisLeft();
        axisLeft.setDrawGridLines(false);
        axisLeft.setDrawAxisLine(false);
        axisLeft.setAxisMinimum(0f); // 최솟값
        axisLeft.setAxisMaximum(100f); // 최댓값(100%)
        axisLeft.setGranularity(1f); // 값만큼 라인선 설정
        axisLeft.setDrawLabels(false); // label 삭제

        YAxis axisRight = barChart.getAxisRight();
        axisRight.setTextSize(15f);
        axisRight.setDrawLabels(false); // label 삭제
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

    // 운동 이름
    private ArrayList<String> getExerciseName(){
        userExerciseLogDbHelper = new UcanHealthDbHelper(this);
        SQLiteDatabase db = userExerciseLogDbHelper.getWritableDatabase();
        ArrayList<String> ExerciseName = new ArrayList<>();

        Cursor cursor = userExerciseLogDbHelper.getExerciseName();
        for (int i=0; i<cursor.getCount(); i++){
            cursor.moveToNext();
            ExerciseName.add(cursor.getString(0));
        }

        cursor.close();
        db.close();

        return ExerciseName;
    }

    // 운동별 달성률
    private ArrayList<BarEntry> getExerciseDataValues(){

        userExerciseLogDbHelper = new UcanHealthDbHelper(this);
        SQLiteDatabase db = userExerciseLogDbHelper.getWritableDatabase();
        ArrayList<BarEntry> dataVals = new ArrayList<>();

        Cursor cursor = userExerciseLogDbHelper.getExerciseDataBar();
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