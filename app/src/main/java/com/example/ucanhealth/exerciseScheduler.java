package com.example.ucanhealth;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import android.annotation.SuppressLint;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CalendarView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import android.content.Context;

import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.database.Cursor;

import com.example.ucanhealth.sqlite.UcanHealth;
import com.example.ucanhealth.sqlite.UcanHealthDbHelper;
import com.google.android.material.navigation.NavigationView;

import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Toast;

public class exerciseScheduler extends AppCompatActivity {

    public String readDay = null;
    public String str = null;
    public String selectedDay; // 캘린더에서 선택된 날짜
    public CalendarView calendarView;
    public TextView diaryTextView; // 운동한 분량을 보여주는 공간

    private UcanHealthDbHelper dbHelper;
    private SQLiteDatabase db_write;
    private SQLiteDatabase db_read;

    private ListView listview = null;
    private ListAdapter adapter = null;

    Button getButton; // 오늘로 루틴 추가하는 버튼
    Button addExampleButton; // 예제 추가하는 버튼

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exercise_scheduler);

        InitializeLayout();

        calendarView = findViewById(R.id.calendarView); // 위의 달력 표시
        diaryTextView = findViewById(R.id.diaryTextView); // 달력에서 자신이 고른 날짜를 표시

        // listview 선언
        listview = (ListView) findViewById(R.id.exercise_listview); // 운동한 데이터를 db에서 가져온다.

        // 데이터 베이스 선언
        dbHelper = new UcanHealthDbHelper(getApplicationContext());
        db_write = dbHelper.getWritableDatabase();
        db_read = dbHelper.getReadableDatabase();

        // 임의 값 대입
        // inialdata();

        UcanHealthDbHelper dbHelper = new UcanHealthDbHelper(this);
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth) {
                int month_new = month + 1;

                String date = year + "-" + month_new + "-" + dayOfMonth;

                diaryTextView.setVisibility(View.VISIBLE); // 다이어리뷰
                listview.setVisibility(View.VISIBLE); // 다이어리뷰
                diaryTextView.setText(String.format("%d년 %d월 %d일", year, month + 1, dayOfMonth)); // 선택한 날짜 표기
                selectedDay = String.format("%04d-%02d-%02d", year, month + 1, dayOfMonth);
                String date_data;

                if (month_new >= 10)
                    date_data = year + "-" + month_new + "-" + dayOfMonth;
                else
                    date_data = year + "-0" + month_new + "-" + dayOfMonth;

                getString(date_data);

            }
        });

        getButton = findViewById(R.id.getBtn);
        getButton.setOnClickListener(addRoutineToDB);
        addExampleButton = findViewById(R.id.addExampleBtn);
        addExampleButton.setOnClickListener(addExample);
    }

    public void getString(String date_data) {

        ArrayList<String> datas = new ArrayList<>();

        String[][] values = {}; // 저장될 string 배열 선언

        int count_numbers = 0;

        // Cursor cursor = db_read.rawQuery("SELECT * FROM UserExerciseLog where date_ =
        // ?", new String[]{"2023-05-23"});

        String sql = String.format("SELECT * FROM %s WHERE %s = '%s'", UcanHealth.UserExerciseLogEntry.TABLE_NAME,
                UcanHealth.UserExerciseLogEntry.COLUMN_DATE, date_data);

        Cursor cursor = db_read.rawQuery(sql, null);
        // Cursor cursor = db_read.rawQuery("SELECT * FROM " +
        // UcanHealth.UserExerciseLogEntry.TABLE_NAME + " WHERE id = ?", new
        // String[]{"2023-05-17"});

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

        String values_exercise_cut = new String();

        for (int i = 0; i < count; i++) {

            values_exercise_cut = "Type of Exercise: " + cursor.getString(1);
            values_exercise_cut += "\nRepetition: " + Integer.toString(cursor.getInt(2));
            values_exercise_cut += "\nweight: " + Integer.toString(cursor.getInt(3));
            values_exercise_cut += "\nset_count" + Integer.toString(cursor.getInt(4));
            values_exercise_cut += "\ntotal_set_count" + Integer.toString(cursor.getInt(5));
            values_exercise_cut += "\ndate" + cursor.getString(6);
            values_exercise_cut += "\ntotal_exercise_time" + Integer.toString(cursor.getInt(7));
            values_exercise_cut += "\nexercise_order" + Integer.toString(cursor.getInt(8));

            datas.add(values_exercise_cut);

            /*
             * datas.add(cursor.getString(1));
             * datas.add(Integer.toString(cursor.getInt(2)));
             * datas.add(Integer.toString(cursor.getInt(3)));
             * datas.add(Integer.toString(cursor.getInt(4)));
             * datas.add(Integer.toString(cursor.getInt(5)));
             * datas.add(cursor.getString(6));
             * datas.add(Integer.toString(cursor.getInt(7)));
             * datas.add(Integer.toString(cursor.getInt(8)));
             */
            /*
             * exercise[i] = cursor.getString(1);
             * repetition[i] = cursor.getInt(2);
             * weight[i] = cursor.getInt(3);
             * set_count[i] = cursor.getInt(4);
             * total_set_count[i] = cursor.getInt(5);
             * date_[i] = cursor.getString(6);
             * total_exercise_time[i] = cursor.getInt(7);
             * exercise_order[i] = cursor.getInt(8);
             */

            count_numbers++;

            Log.i("cursor_test", "succuse");
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, datas);

        listview.setAdapter(adapter);

        /*
         * for(int i = 0; i < count; i++){
         * //exerciseData_View.setText("Type of Exercise: " + exercise[i] +
         * "\nRepetition: " + repetition[i] + "\nweight: " + weight[i] + "\nset_count: "
         * + set_count[i] + "\ntotal_set_count: " + total_set_count[i] + "\ndate: " +
         * date_[i] + "\ntotal_exercise_time: " + total_exercise_time[i] +
         * "\nexercise_order: " +
         * exercise_order[i]+"\n\ncount_numbers: "+count_numbers);
         * 
         * }
         */

    }

    // 임의 값 삽입
    public void inialdata() {

        ContentValues cv = new ContentValues();
        long inialData;
        cv.put(UcanHealth.UserExerciseLogEntry.COLUMN_EXERCISE, "neck exercise");
        cv.put(UcanHealth.UserExerciseLogEntry.COLUMN_REPS, 10);
        cv.put(UcanHealth.UserExerciseLogEntry.COLUMN_WEIGHT, 50);
        cv.put(UcanHealth.UserExerciseLogEntry.COLUMN_SET_COUNT, 5);
        cv.put(UcanHealth.UserExerciseLogEntry.COLUMN_TOTAL_SET_COUNT, 5);
        cv.put(UcanHealth.UserExerciseLogEntry.COLUMN_DATE, "2023-05-10");
        cv.put(UcanHealth.UserExerciseLogEntry.COLUMN_TOTAL_EXERCISE_TIME, 50);
        cv.put(UcanHealth.UserExerciseLogEntry.COLUMN_ORDER, 3);

        inialData = db_write.insert(UcanHealth.UserExerciseLogEntry.TABLE_NAME, null, cv);

        cv.put(UcanHealth.UserExerciseLogEntry.COLUMN_EXERCISE, "neck exercise");
        cv.put(UcanHealth.UserExerciseLogEntry.COLUMN_REPS, 10);
        cv.put(UcanHealth.UserExerciseLogEntry.COLUMN_WEIGHT, 50);
        cv.put(UcanHealth.UserExerciseLogEntry.COLUMN_SET_COUNT, 5);
        cv.put(UcanHealth.UserExerciseLogEntry.COLUMN_TOTAL_SET_COUNT, 5);
        cv.put(UcanHealth.UserExerciseLogEntry.COLUMN_DATE, "2023-05-11");
        cv.put(UcanHealth.UserExerciseLogEntry.COLUMN_TOTAL_EXERCISE_TIME, 50);
        cv.put(UcanHealth.UserExerciseLogEntry.COLUMN_ORDER, 3);

        inialData = db_write.insert(UcanHealth.UserExerciseLogEntry.TABLE_NAME, null, cv);

        cv.put(UcanHealth.UserExerciseLogEntry.COLUMN_EXERCISE, "neck exercise");
        cv.put(UcanHealth.UserExerciseLogEntry.COLUMN_REPS, 10);
        cv.put(UcanHealth.UserExerciseLogEntry.COLUMN_WEIGHT, 50);
        cv.put(UcanHealth.UserExerciseLogEntry.COLUMN_SET_COUNT, 5);
        cv.put(UcanHealth.UserExerciseLogEntry.COLUMN_TOTAL_SET_COUNT, 5);
        cv.put(UcanHealth.UserExerciseLogEntry.COLUMN_DATE, "2023-05-12");
        cv.put(UcanHealth.UserExerciseLogEntry.COLUMN_TOTAL_EXERCISE_TIME, 50);
        cv.put(UcanHealth.UserExerciseLogEntry.COLUMN_ORDER, 3);

        inialData = db_write.insert(UcanHealth.UserExerciseLogEntry.TABLE_NAME, null, cv);

        for (int i = 0; i < 4; i++) {

            cv.put(UcanHealth.UserExerciseLogEntry.COLUMN_EXERCISE, "neck exercise");
            cv.put(UcanHealth.UserExerciseLogEntry.COLUMN_REPS, 10);
            cv.put(UcanHealth.UserExerciseLogEntry.COLUMN_WEIGHT, 50);
            cv.put(UcanHealth.UserExerciseLogEntry.COLUMN_SET_COUNT, 5);
            cv.put(UcanHealth.UserExerciseLogEntry.COLUMN_TOTAL_SET_COUNT, 5);
            cv.put(UcanHealth.UserExerciseLogEntry.COLUMN_DATE, "2023-05-20");
            cv.put(UcanHealth.UserExerciseLogEntry.COLUMN_TOTAL_EXERCISE_TIME, 50);
            cv.put(UcanHealth.UserExerciseLogEntry.COLUMN_ORDER, 3);

            inialData = db_write.insert(UcanHealth.UserExerciseLogEntry.TABLE_NAME, null, cv);
        }
    }

    @SuppressLint("WrongConstant")
    public void removeDiary(String readDay) {
        FileOutputStream fos;
        try {
            fos = openFileOutput(readDay, MODE_NO_LOCALIZED_COLLATORS);
            String content = "";
            fos.write((content).getBytes());
            fos.close();

        } catch (Exception e) {
            e.printStackTrace();
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
                        Intent intent1 = new Intent(getApplicationContext(), MainActivity.class);
                        startActivity(intent1);
                        Toast.makeText(getApplicationContext(), "SelectedItem 1", Toast.LENGTH_SHORT).show();
                        break;
                    case R.id.LouinePage:
                        Intent intent2 = new Intent(getApplicationContext(), TimerActivity.class);
                        startActivity(intent2);
                        Toast.makeText(getApplicationContext(), "SelectedItem 2", Toast.LENGTH_SHORT).show();
                        break;
                    case R.id.SchdulerPage:
                        Intent intent3 = new Intent(getApplicationContext(), exerciseScheduler.class);
                        startActivity(intent3);
                        Toast.makeText(getApplicationContext(), "SelectedItem 3", Toast.LENGTH_SHORT).show();
                        break;
                }
                drawer.closeDrawer(GravityCompat.START);
                return false;
            }
        });
    }

    private final View.OnClickListener addRoutineToDB = new View.OnClickListener() {
        public void onClick(View view) {
            // 오늘 날짜에 이미 운동 리스트가 있다면 어떻게 할 것인지에 대한 로직이 추가되어야 함

            // 선택된 날짜 데이터를 가져와서 오늘 날짜로 리스트 추가하기
            String[] projection = {
                    UcanHealth.UserExerciseLogEntry.COLUMN_EXERCISE,
                    UcanHealth.UserExerciseLogEntry.COLUMN_REPS,
                    UcanHealth.UserExerciseLogEntry.COLUMN_WEIGHT,
                    UcanHealth.UserExerciseLogEntry.COLUMN_TOTAL_SET_COUNT,
                    UcanHealth.UserExerciseLogEntry.COLUMN_REST_TIME,
                    UcanHealth.UserExerciseLogEntry.COLUMN_ORDER
            };
            String sortOrder = UcanHealth.UserExerciseLogEntry.COLUMN_ORDER + " ASC";

            String selection = UcanHealth.UserExerciseLogEntry.COLUMN_DATE + " = ?";
            String[] selectionArgs = { selectedDay };

            Cursor cursor = db_read.query(
                    UcanHealth.UserExerciseLogEntry.TABLE_NAME, // The table to query
                    projection, // The array of columns to return (pass null to get all)
                    selection, // The columns for the WHERE clause
                    selectionArgs, // The values for the WHERE clause
                    null, // don't group the rows
                    null, // don't filter by row groups
                    sortOrder);
            String today = getCurrentDate();

            while (cursor.moveToNext()) {
                ContentValues values = new ContentValues();
                values.put(UcanHealth.UserExerciseLogEntry.COLUMN_EXERCISE, cursor.getString(0));
                values.put(UcanHealth.UserExerciseLogEntry.COLUMN_REPS, cursor.getString(1));
                values.put(UcanHealth.UserExerciseLogEntry.COLUMN_WEIGHT, cursor.getString(2));
                values.put(UcanHealth.UserExerciseLogEntry.COLUMN_TOTAL_SET_COUNT, cursor.getString(3));
                values.put(UcanHealth.UserExerciseLogEntry.COLUMN_REST_TIME, cursor.getString(4));
                values.put(UcanHealth.UserExerciseLogEntry.COLUMN_ORDER, cursor.getString(5));
                values.put(UcanHealth.UserExerciseLogEntry.COLUMN_SET_COUNT, 1);
                values.put(UcanHealth.UserExerciseLogEntry.COLUMN_DATE, today);

                long i = db_write.insert(UcanHealth.UserExerciseLogEntry.TABLE_NAME, null, values);
                if (i == 0) {
                    Log.i("insert", "fail");
                } else {
                    Log.i("insert", "success");
                }
            }
        }
    };

    public String getCurrentDate() {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH) + 1; // month는 0부터 시작하므로 1을 더해줍니다.
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        return String.format("%04d-%02d-%02d", year, month, day);
    }

    View.OnClickListener addExample = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            String[] exercise = { "pull up", "pull down", "bench press" };
            for (int i = 0; i < 3; i++) {
                ContentValues values = new ContentValues();

                values.put(UcanHealth.UserExerciseLogEntry.COLUMN_EXERCISE, exercise[i]);
                values.put(UcanHealth.UserExerciseLogEntry.COLUMN_REPS, i);
                values.put(UcanHealth.UserExerciseLogEntry.COLUMN_WEIGHT, i);
                values.put(UcanHealth.UserExerciseLogEntry.COLUMN_TOTAL_SET_COUNT, i + 3);
                values.put(UcanHealth.UserExerciseLogEntry.COLUMN_REST_TIME, "");
                values.put(UcanHealth.UserExerciseLogEntry.COLUMN_ORDER, 0);
                values.put(UcanHealth.UserExerciseLogEntry.COLUMN_SET_COUNT, 1);
                values.put(UcanHealth.UserExerciseLogEntry.COLUMN_DATE, "2023-05-29");

                db_write.insert(UcanHealth.UserExerciseLogEntry.TABLE_NAME, null, values);
            }
        }
    };
}
