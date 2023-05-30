package com.example.ucanhealth;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;

import com.example.ucanhealth.sqlite.UcanHealth;
import com.example.ucanhealth.sqlite.UcanHealthDbHelper;
import com.google.android.material.navigation.NavigationView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

public class TimerActivity extends AppCompatActivity {

    private static final String TAG = "TimerActivity";  //오류 발생시 로그캣 찍어보려고 만들어둔 변수.
    private UcanHealthDbHelper dbHelper;
    private SQLiteDatabase db_write;
    private SQLiteDatabase db_read;
    TextView exercise;
    TextView input_weight;
    Button btn_next;
    TextView totalSet;
    TextView currentSet;
    TextView TextView_order;
    TextView TextView_reps;
    TextView TextView_totalSet;
    TextView TextView_timer_exercise;
    Cursor cursor;
    TextView TextView_total_exercise;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timer);

        init();
        ReadDB();
        btn_next.setOnClickListener(nextSetClickListener);

        this.settingSideBar();
        this.ExerciseClicked();
        this.RestClicked();
    }

    public void init() {
        dbHelper = new UcanHealthDbHelper(this.getApplicationContext());
        db_write = dbHelper.getWritableDatabase();
        db_read = dbHelper.getReadableDatabase();

        exercise = findViewById(R.id.exercise);
        input_weight = findViewById(R.id.input_weight);

        btn_next = findViewById(R.id.btn_next);
        totalSet = findViewById(R.id.totalSet);
        currentSet = findViewById(R.id.current_set);
        TextView_order = findViewById(R.id.TextView_order);
        TextView_reps = findViewById(R.id.TextView_reps);
        TextView_totalSet = findViewById(R.id.TextView_totalSet);
        TextView_timer_exercise = findViewById(R.id.timer_exercise);
        TextView_total_exercise = findViewById(R.id.total_exercise);
        // activity가 실행되면
        ReadDB();
        setInfoFromDB();

        TextView_total_exercise.setText(String.valueOf(getRoutineCount()));
    }

    /*사이드 바 관련 함수*/
    public void settingSideBar() {
        // toolbar 생성
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // 사이드 바를 오픈하기 위한 아이콘 추가
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.menuicon);

        // 사이드 바 구현
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);

        ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(
                this,
                drawer,
                toolbar,
                R.string.open,
                R.string.closed
        );

        //사이드 바 클릭 리스너
        drawer.addDrawerListener(actionBarDrawerToggle);

        //사이드 바 아이템 클릭 이벤트 설정
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                int curId = item.getItemId();

                switch (curId) {
                    case R.id.menuitem1:
                        Toast.makeText(getApplicationContext(), "메뉴아이템 1 선택", Toast.LENGTH_SHORT).show();
                        break;
                    case R.id.menuitem2:
                        Toast.makeText(getApplicationContext(), "메뉴아이템 2 선택", Toast.LENGTH_SHORT).show();
                        break;
                    case R.id.menuitem3:
                        Toast.makeText(getApplicationContext(), "메뉴아이템 3 선택", Toast.LENGTH_SHORT).show();
                        break;
                }

                DrawerLayout drawer = findViewById(R.id.drawer_layout);
                drawer.closeDrawer(GravityCompat.START);

                return true;
            }
        });
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);

        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            finish();
        }
    }


    /*Exercise 버튼 동작 함수*/
    public void ExerciseClicked() {
        Button btn_exercise = findViewById(R.id.btn_exercise);
        final TextView[] timer_exercise = {findViewById(R.id.timer_exercise)};

        final int[] count = {0};
        final Timer[] timer = {null};

        btn_exercise.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 타이머 객체를 생성, 타이머의 작동 방식을 설정
                if (timer[0] == null) {
                    timer[0] = new Timer();
                    TimerTask task = new TimerTask() {
                        @Override
                        public void run() {
                            count[0]++; // 카운트를 1 증가
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    // 카운트를 출력
                                    int minutes = count[0] / 60;
                                    int seconds = count[0] % 60;
                                    timer_exercise[0].setText(String.format("%d:%02d", minutes, seconds));
                                }
                            });
                        }
                    };
                    // 타이머를 시작.
                    timer[0].scheduleAtFixedRate(task, 0, 1000);
                    btn_exercise.setText(getString(R.string.start));
                } else {
                    // 타이머가 돌아가는 상태에서 버튼을 누르면 타이머 일시정지.
                    timer[0].cancel();
                    timer[0] = null;
                    btn_exercise.setText(getString(R.string.stop));
                }
            }
        });
    }

    /*Rest 버튼 동작 함수*/
    //사용자에게 직접 restTime을 입력 받을 수 있도록 editText로 구성하였습니다.
    private CountDownTimer countDownTimer = null;   //rest 카운트다운 타이머
    private boolean isPaused = false;   //일시정지 여부 판단을 위한 변수

    private void RestClicked() {
        EditText timer_rest = findViewById(R.id.timer_rest);
        Button btn_rest = findViewById(R.id.btn_rest);

        btn_rest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (countDownTimer != null && isPaused) { // 일시정지된 상태에서 Rest 버튼을 누른 경우
                    countDownTimer.cancel();
                    btn_rest.setText(getString(R.string.start));
                    isPaused = false;
                } else { // 타이머를 새로 생성해야 하는 경우
                    if (countDownTimer != null) {
                        countDownTimer.cancel();
                        countDownTimer = null;
                    }
                    // editText에서 시간을 가져와서 타이머 설정
                    String timeStr = timer_rest.getText().toString();
                    String[] timeArr = timeStr.split(":");
                    int minute = Integer.parseInt(timeArr[0]);
                    int second = Integer.parseInt(timeArr[1]);
                    long timeInMillis = (minute * 60 + second) * 1000;
                    countDownTimer = new CountDownTimer(timeInMillis, 1000) {
                        @Override
                        public void onTick(long l) {
                            long minutes = TimeUnit.MILLISECONDS.toMinutes(l);
                            long seconds = TimeUnit.MILLISECONDS.toSeconds(l) - TimeUnit.MINUTES.toSeconds(minutes);
                            timer_rest.setText(String.format("%02d:%02d", minutes, seconds));
                        }

                        @Override
                        public void onFinish() {
                            timer_rest.setText("00:00");
                            btn_rest.setText(getString(R.string.start));
                            countDownTimer = null;
                        }
                    };
                    countDownTimer.start();
                    btn_rest.setText(getString(R.string.stop));
                    isPaused = true;
                }
            }
        });
    }


    /*운동 시작시 DB에서 데이터를 모두 가져오는 함수*/
    public void ReadDB() {
        String[] projection = {
                UcanHealth.UserExerciseLogEntry.COLUMN_EXERCISE,
                UcanHealth.UserExerciseLogEntry.COLUMN_REPS,
                UcanHealth.UserExerciseLogEntry.COLUMN_WEIGHT,
                UcanHealth.UserExerciseLogEntry.COLUMN_SET_COUNT,
                UcanHealth.UserExerciseLogEntry.COLUMN_TOTAL_SET_COUNT,
                UcanHealth.UserExerciseLogEntry.COLUMN_DATE,
                UcanHealth.UserExerciseLogEntry.COLUMN_REST_TIME,
                UcanHealth.UserExerciseLogEntry.COLUMN_ORDER,
        };

        String sortOrder =
                UcanHealth.UserExerciseLogEntry.COLUMN_ORDER + " ASC";
        String selection = UcanHealth.UserExerciseLogEntry.COLUMN_DATE + " = ?";
        String[] selectionArgs = {getCurrentDate()};

        cursor = db_read.query(
                UcanHealth.UserExerciseLogEntry.TABLE_NAME,   // The table to query
                projection,             // The array of columns to return (pass null to get all)
                selection,              // The columns for the WHERE clause
                selectionArgs,          // The values for the WHERE clause
                null,                   // don't group the rows
                null,                   // don't filter by row groups
                sortOrder               // The sort order
        );
    }

    public String getCurrentDate() {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH) + 1; // month는 0부터 시작하므로 1을 더해줍니다.
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        return String.format("%04d-%02d-%02d", year, month, day);
    }

    public void addSetCountToDb(int value) {
        ContentValues values = new ContentValues();

        values.put(UcanHealth.UserExerciseLogEntry.COLUMN_SET_COUNT, value);

        String selection = UcanHealth.UserExerciseLogEntry.COLUMN_ORDER + " = ? AND " +
                UcanHealth.UserExerciseLogEntry.COLUMN_DATE + " = ? ";
        String[] selectionArgs = {
                TextView_order.getText().toString(),
                getCurrentDate()
        };

        int result =  db_write.update(
                UcanHealth.UserExerciseLogEntry.TABLE_NAME,
                values,
                selection,
                selectionArgs
        );
        Log.i("addSetCountToDb","value is " + String.valueOf(value));

        if (result == 0) {
            Log.i("addSetCountToDb", "update fail.");
        }else {
            Log.i("addSetCountToDb","update success.");
        }

    }

    public View.OnClickListener nextSetClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            int set_count = Integer.parseInt(currentSet.getText().toString());
            int total_set_count = Integer.parseInt(TextView_totalSet.getText().toString());
            set_count++;
            currentSet.setText(String.valueOf(set_count));
            addSetCountToDb(set_count);

            if (set_count == total_set_count) {
                setInfoFromDB();
            }

        }
    };

    public void setInfoFromDB() {
        if(!cursor.moveToNext()) {
//            endExercise();
            Log.i("finish", "finish");
            Toast.makeText(this, "All routine is doen.", Toast. LENGTH_SHORT).show();
            finish();
            return;
        }

        String item = cursor.getString(0); // 0번째 인덱스의 데이터(exercise) 가져오기
        String reps = cursor.getString(1);
        String weight = cursor.getString(2);    //2번째 인덱스의 데이터(weight) 가져오기
        String set_count = cursor.getString(3);
        String total_set_count = cursor.getString(4);
        String order = cursor.getString(7);

        // 조건문 -> 문자열 비교, 정수 비교 ********
        if (set_count.equals(total_set_count)) {
            cursor.moveToNext();
            setInfoFromDB();
            return;
        }

        TextView_order.setText(order);
        currentSet.setText(set_count);
        TextView_reps.setText(reps);
        exercise.setText(item);
        input_weight.setText(weight);
        TextView_totalSet.setText(total_set_count);
    }

    private int getRoutineCount() {
        String sql = String.format("SELECT COUNT(*) FROM %s WHERE %s = '%s'", UcanHealth.UserExerciseLogEntry.TABLE_NAME,
                UcanHealth.UserExerciseLogEntry.COLUMN_DATE,
                getCurrentDate());
        Cursor cursor = db_read.rawQuery(sql, null);

        cursor.moveToNext();

        int count = cursor.getInt(0);
        Log.i("count", String.valueOf(count));

        return count;
    }

    public void endExercise() {
        String[] totalExerciseTime = TextView_timer_exercise.getText().toString().split(":");
        int minute = Integer.parseInt(totalExerciseTime[0]);
        int second = Integer.parseInt(totalExerciseTime[1]);

        ContentValues values = new ContentValues();
        values.put(UcanHealth.TotalExerciseTimeEntry.COLUMN_DATE, getCurrentDate());
        values.put(UcanHealth.TotalExerciseTimeEntry.COLUMN_TOTAL_EXERCISE_TIME, minute * 60 + second);
        long newRowId = db_write.insert(UcanHealth.UserExerciseLogEntry.TABLE_NAME, null, values);
        if (newRowId == -1) {
            Log.i("insert", "end fail");
        } else {
            Log.i("insert", "end success");
        }
    };
}
