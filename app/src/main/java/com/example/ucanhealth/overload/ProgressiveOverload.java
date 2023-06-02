package com.example.ucanhealth.overload;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.example.ucanhealth.R;
import com.example.ucanhealth.sqlite.UcanHealthDbHelper;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.ArrayList;

public class ProgressiveOverload extends AppCompatActivity {
    UcanHealthDbHelper dbHelper;
    SQLiteDatabase db;
    PieChart laskWeekPieChart;
    PieChart thisWeekPieChart;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.progressive_overload);

        init();
        ///
        setLastWeekPieChart();

        ///
    }

    private void init() {
        laskWeekPieChart = findViewById(R.id.lastPieChart);
        thisWeekPieChart = findViewById(R.id.thisPieChart);

        dbHelper = new UcanHealthDbHelper(this);
        db = dbHelper.getReadableDatabase();
    }

    private void setLastWeekPieChart() {
        ArrayList<PieEntry> visitors = new ArrayList<>();
        Cursor cursor = dbHelper.getExerciseTypeDataBar();
        while(cursor.moveToNext()) {
            int sum_total = Integer.parseInt(cursor.getString(0));
            int sum_setCount = Integer.parseInt(cursor.getString(1));
            visitors.add(new PieEntry(sum_setCount / sum_total * 100,cursor.getString(2)));
        }
//        visitors.add(new PieEntry(508,"2016"));
////        visitors.add(new PieEntry(600,"2017"));
////        visitors.add(new PieEntry(750,"2018"));
////        visitors.add(new PieEntry(600,"2019"));
////        visitors.add(new PieEntry(607,"2020"));

        PieDataSet pieDataSet = new PieDataSet(visitors, "Visitors");
        pieDataSet.setColors(ColorTemplate.COLORFUL_COLORS);
        pieDataSet.setValueTextColor(Color.BLACK);
        pieDataSet.setValueTextSize(16f);

        PieData pieData = new PieData(pieDataSet);

        laskWeekPieChart.setData(pieData);
        laskWeekPieChart.getDescription().setEnabled(false);
        laskWeekPieChart.setCenterText("Visitors");
        laskWeekPieChart.animate();
    }
}