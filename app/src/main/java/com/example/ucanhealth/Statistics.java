package com.example.ucanhealth;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import com.example.ucanhealth.sqlite.UserExerciseLogDbHelper;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.ArrayList;
import java.util.Calendar;

public class Statistics extends AppCompatActivity {

    private UserExerciseLogDbHelper userExerciseLogDbHelper;
    private SQLiteDatabase userExerciseLogDb_read;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.statistics);

        BarChart barChart = findViewById(R.id.barChart);
        BarDataSet barDataSet = new BarDataSet(getExerciseDataValues(), "data");

        barDataSet.setColors(ColorTemplate.MATERIAL_COLORS);
        barDataSet.setValueTextColor(Color.BLACK);
        barDataSet.setValueTextSize(16f);

        BarData barData = new BarData(barDataSet);

        barChart.setFitBars(true);
        barChart.setData(barData);
        barChart.getDescription().setText("Example");
        barChart.animateY(2000);

    }

    private ArrayList<BarEntry> getExerciseDataValues(){

        userExerciseLogDbHelper = new UserExerciseLogDbHelper(this);
        SQLiteDatabase db = userExerciseLogDbHelper.getWritableDatabase();
        ArrayList<BarEntry> dataVals = new ArrayList<>();

        Cursor cursor = userExerciseLogDbHelper.getExerciseDataBar();
        for (int i=0; i<cursor.getCount(); i++){
            cursor.moveToNext();
            dataVals.add(new BarEntry(i, cursor.getInt(0)));
        }

        cursor.close();
        db.close();

        return dataVals;
    }
}