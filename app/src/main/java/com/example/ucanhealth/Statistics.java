package com.example.ucanhealth;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.google.android.material.navigation.NavigationView;

import java.util.ArrayList;


public class Statistics extends AppCompatActivity {

    BarChart barChart;
    EditText xEditText, yEditText;
    Button btnInsert, btnShow;

    MyHelper myHelper;
    SQLiteDatabase sqLiteDatabase;

    BarDataSet barDataSet = new BarDataSet(null, null);
    ArrayList<IBarDataSet> dataSets = new ArrayList<>();
    BarData barData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.statistics);

        this.InitializeLayout();

        barChart = findViewById(R.id.mpChart);
        xEditText = findViewById(R.id.editTextX);
        yEditText = findViewById(R.id.editTextY);
        btnInsert = findViewById(R.id.btnInsert);
        btnShow = findViewById(R.id.btnShow);
        myHelper = new MyHelper(this);
        sqLiteDatabase = myHelper.getWritableDatabase();

        exqInsertBtn();
        exqShowBtn();
    }

    public void InitializeLayout() {
        //toolBar를 통해 App Bar 생성
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //App Bar의 좌측 영영에 Drawer를 Open 하기 위한 Incon 추가
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.menuicon);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);

        ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(
                this,
                drawer,
                toolbar,
                R.string.open,
                R.string.closed
        );
        drawer.addDrawerListener(actionBarDrawerToggle);

        // navigation 객체에 nav_view의 참조 반환
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        // navigation 객체에 이벤트 리스너 달기
        navigationView.setNavigationItemSelectedListener(menuItem -> {
            Log.i("clicked","success");
            switch (menuItem.getItemId())
            {
                case R.id.menuitem1:
                    Toast.makeText(getApplicationContext(), "SelectedItem 1", Toast.LENGTH_SHORT).show();
                    break;
                case R.id.menuitem2:
                    Toast.makeText(getApplicationContext(), "SelectedItem 2", Toast.LENGTH_SHORT).show();
                    break;
                case R.id.menuitem3:
                    Toast.makeText(getApplicationContext(), "SelectedItem 3", Toast.LENGTH_SHORT).show();
                    break;
            }
            drawer.closeDrawer(GravityCompat.START);
            return true;
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

    private void exqShowBtn() {
        btnShow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                barDataSet.setValues(getDataValues());
                barDataSet.setLabel("DataSet 1");
                dataSets.clear();
                dataSets.add(barDataSet);
                barData = new BarData(dataSets);
                barChart.clear();
                barChart.setData(barData);
                barChart.invalidate();
            }
        });
    }

    private void exqInsertBtn() {
        btnInsert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                float xVal = Float.parseFloat(String.valueOf(xEditText.getText()));
                float yVal = Float.parseFloat(String.valueOf(yEditText.getText()));
                myHelper.insertData(xVal, yVal);
            }
        });
    }

    private ArrayList<BarEntry> getDataValues()
    {
        ArrayList<BarEntry> dataVals = new ArrayList<>();
        String[] columns = {"xValues", "yValues"};
        Cursor cursor = sqLiteDatabase.query("myTable", columns, null, null, null, null, null);

        for(int i=0; i <cursor.getCount(); i++)
        {
            cursor.moveToNext();
            dataVals.add(new BarEntry(cursor.getFloat(0), cursor.getFloat(1)));
        }
        return dataVals;
    }
}
