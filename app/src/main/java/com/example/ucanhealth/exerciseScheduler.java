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
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
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
    public TextView diaryTextView; //운동한 분량을 보여주는 공간

    private UcanHealthDbHelper dbHelper;
    private SQLiteDatabase db_write;
    private SQLiteDatabase db_read;

    private ListView listview = null;
    private ListAdapter adapter = null;

    Button getButton; // 오늘로 루틴 추가하는 버튼
    Button addExampleButton; // 예제 추가하는 버튼
    Button closeBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exercise_scheduler);

        InitializeLayout();

        calendarView = findViewById(R.id.calendarView); // 위의 달력 표시
        diaryTextView = findViewById(R.id.diaryTextView); // 달력에서 자신이 고른 날짜를 표시

        //listview 선언
        listview = (ListView) findViewById(R.id.exercise_listview); // 운동한 데이터를 db에서 가져온다.

        //데이터 베이스 선언
        dbHelper = new UcanHealthDbHelper(getApplicationContext());
        db_write = dbHelper.getWritableDatabase();
        db_read = dbHelper.getReadableDatabase();

        UcanHealthDbHelper dbHelper = new UcanHealthDbHelper(this);
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener()
        {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth)
            {
                int month_new = month + 1;

                String date = year + "-" + month_new + "-" + dayOfMonth;


                diaryTextView.setVisibility(View.VISIBLE); // 다이어리뷰
                listview.setVisibility(View.VISIBLE); // 다이어리뷰
                diaryTextView.setText(String.format("%d년 %d월 %d일", year, month + 1, dayOfMonth)); // 선택한 날짜 표기
                selectedDay = String.format("%04d-%02d-%02d", year, month+1, dayOfMonth);
                String date_data;

                if(month_new >= 10)
                    date_data = year+"-"+month_new+"-"+dayOfMonth;
                else
                    date_data = year+"-0"+month_new+"-"+dayOfMonth;

                getString(date_data);

            }
        });

        getButton = findViewById(R.id.getBtn);
        getButton.setOnClickListener(addRoutineToDB);
        addExampleButton = findViewById(R.id.addExampleBtn);
        addExampleButton.setOnClickListener(addExample);
        closeBtn = findViewById(R.id.closeBtn);
        closeBtn.setOnClickListener(closeActivity);
    }

    // 5.30에 재민님이 수정
    public void getString(String date_data) {
        ArrayList<String> datas = new ArrayList<>();

        // 데이터베이스에서 데이터를 가져오는 로직
        String[] projection = {
                UcanHealth.UserExerciseLogEntry.COLUMN_EXERCISE, // 운동 이름
                UcanHealth.UserExerciseLogEntry.COLUMN_REPS, // 반복 횟수
                UcanHealth.UserExerciseLogEntry.COLUMN_WEIGHT, // 무게
                UcanHealth.UserExerciseLogEntry.COLUMN_TOTAL_SET_COUNT, // 총 횟수
                // 필요한 다른 열 추가시 추가 생성
        };

        String selection = UcanHealth.UserExerciseLogEntry.COLUMN_DATE + " = ?";
        String[] selectionArgs = {date_data};

        Cursor cursor = db_read.query(
                UcanHealth.UserExerciseLogEntry.TABLE_NAME, // 테이블 이름
                projection, // 가져올 열들
                selection, // WHERE 절
                selectionArgs, // WHERE 절의 값
                null,
                null,
                null
        );

        // 가져온 데이터를 `datas` 리스트에 추가
        while (cursor.moveToNext()) {
            String exercise = cursor.getString(cursor.getColumnIndexOrThrow(UcanHealth.UserExerciseLogEntry.COLUMN_EXERCISE));
            int reps = cursor.getInt(cursor.getColumnIndexOrThrow(UcanHealth.UserExerciseLogEntry.COLUMN_REPS));
            int weight = cursor.getInt(cursor.getColumnIndexOrThrow(UcanHealth.UserExerciseLogEntry.COLUMN_WEIGHT));
            int set = cursor.getInt(cursor.getColumnIndexOrThrow(UcanHealth.UserExerciseLogEntry.COLUMN_TOTAL_SET_COUNT));
            // 필요한 다른 열들도 가져오세요

            String data = "운동 종류: " + exercise + "\n"
                    + "반복: " + reps + "회\n"
                    + "세트: " + set + "세트\n"
                    + "무게: " + weight + "KG\n";
            // 필요한 다른 데이터 추가시 수정

            datas.add(data);
        }

        ExerciseListAdapter adapter = new ExerciseListAdapter(this, datas);
        listview.setAdapter(adapter);
    }

    public class ExerciseListAdapter extends BaseAdapter {
        private Context context;
        private ArrayList<String> dataList;

        public ExerciseListAdapter(Context context, ArrayList<String> dataList) {
            this.context = context;
            this.dataList = dataList;
        }

        @Override
        public int getCount() {
            return dataList.size();
        }

        @Override
        public Object getItem(int position) {
            return dataList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                LayoutInflater inflater = LayoutInflater.from(context);
                convertView = inflater.inflate(android.R.layout.simple_list_item_1, parent, false);
            }

            TextView textView = convertView.findViewById(android.R.id.text1);
            String item = dataList.get(position);
            textView.setText(item);

            return convertView;
        }
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

    public void InitializeLayout() {
        //toolBar를 통해 App Bar 생성
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();

        //App Bar의 좌측 영영에 Drawer를 Open 하기 위한 Icon 추가
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeAsUpIndicator(R.drawable.menuicon);

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
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                Log.i("clicked",String.valueOf(menuItem.getItemId()) + " selected");
                switch (menuItem.getItemId())
                {
                    case R.id.calender:
                        Intent intent1 = new Intent(getApplicationContext(),MainActivity.class);
                        startActivity(intent1);
                        break;
                    case R.id.menuitem2:
                        Toast.makeText(getApplicationContext(), "SelectedItem 2", Toast.LENGTH_SHORT).show();
                        break;
                    case R.id.menuitem3:
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
            String sortOrder =
                    UcanHealth.UserExerciseLogEntry.COLUMN_ORDER + " ASC";

            String selection = UcanHealth.UserExerciseLogEntry.COLUMN_DATE + " = ?";
            String[] selectionArgs = { selectedDay };

            Cursor cursor = db_read.query(
                    UcanHealth.UserExerciseLogEntry.TABLE_NAME,   // The table to query
                    projection,             // The array of columns to return (pass null to get all)
                    selection,              // The columns for the WHERE clause
                    selectionArgs,          // The values for the WHERE clause
                    null,                   // don't group the rows
                    null,                   // don't filter by row groups
                    sortOrder
            );
            String today = getCurrentDate();


            while(cursor.moveToNext()) {
                ContentValues values = new ContentValues();
                values.put( UcanHealth.UserExerciseLogEntry.COLUMN_EXERCISE,cursor.getString(0));
                values.put( UcanHealth.UserExerciseLogEntry.COLUMN_REPS,cursor.getString(1));
                values.put( UcanHealth.UserExerciseLogEntry.COLUMN_WEIGHT,cursor.getString(2));
                values.put( UcanHealth.UserExerciseLogEntry.COLUMN_TOTAL_SET_COUNT,cursor.getString(3));
                values.put( UcanHealth.UserExerciseLogEntry.COLUMN_REST_TIME,cursor.getString(4));
                values.put( UcanHealth.UserExerciseLogEntry.COLUMN_ORDER,cursor.getString(5));
                values.put( UcanHealth.UserExerciseLogEntry.COLUMN_SET_COUNT,1);
                values.put( UcanHealth.UserExerciseLogEntry.COLUMN_DATE,today);

                long i = db_write.insert(UcanHealth.UserExerciseLogEntry.TABLE_NAME, null, values);
                if(i == 0){
                    Log.i("insert","fail");
                }else{
                    Log.i("insert","success");
                }
            }
        }
    };

    private final View.OnClickListener closeActivity = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            db_read.close();
            db_write.close();
            finish();
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
            String[] exercise = {"pull up", "pull down", "bench press"};
            for(int i=0; i<3; i++) {
                ContentValues values = new ContentValues();

                values.put( UcanHealth.UserExerciseLogEntry.COLUMN_EXERCISE,exercise[i]);
                values.put( UcanHealth.UserExerciseLogEntry.COLUMN_REPS,i);
                values.put( UcanHealth.UserExerciseLogEntry.COLUMN_WEIGHT,i);
                values.put( UcanHealth.UserExerciseLogEntry.COLUMN_TOTAL_SET_COUNT,i+3);
                values.put( UcanHealth.UserExerciseLogEntry.COLUMN_REST_TIME,"");
                values.put( UcanHealth.UserExerciseLogEntry.COLUMN_ORDER,0);
                values.put( UcanHealth.UserExerciseLogEntry.COLUMN_SET_COUNT,1);
                values.put( UcanHealth.UserExerciseLogEntry.COLUMN_DATE,"2023-05-29");

                db_write.insert(UcanHealth.UserExerciseLogEntry.TABLE_NAME, null, values);
            }
        }
    };
}

