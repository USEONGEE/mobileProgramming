package com.example.ucanhealth.exercising;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.app.Dialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;

import com.example.ucanhealth.MainActivity;
import com.example.ucanhealth.R;
import com.example.ucanhealth.overload.ProgressiveOverload;
import com.example.ucanhealth.recommend.menu_Routine;
import com.example.ucanhealth.schedule.exerciseScheduler;
import com.example.ucanhealth.sqlite.UcanHealth;
import com.example.ucanhealth.sqlite.UcanHealthDbHelper;
import com.example.ucanhealth.statistic.Statistics;
import com.google.android.material.navigation.NavigationView;

import java.util.ArrayList;
import java.util.Calendar;
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
    Button goPrevious;
    Button goNext;
    TextView currentSet;
    TextView TextView_order;
    TextView TextView_reps;
    TextView TextView_totalSet;
    TextView TextView_timer_exercise;
    Cursor cursor;
    TextView TextView_total_exercise;
    EditText timer_rest_minute;
    EditText timer_rest_second;

    int numOfExercise;
    int indexCurrentExercise;
    EndExerciseDialog endExerciseDialog;

    // 운동 목록을 보여주는 ArrayList
    ArrayList<Exercise> exerciseArrayList;
    ArrayList<Integer> notClearExerciseIndex;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timer);

        init();
        btn_next.setOnClickListener(nextSetClickListener);
        goPrevious.setOnClickListener(goPreviousExercise);
        goNext.setOnClickListener(goNextExercise);
    }

    public void init() {

        dbHelper = new UcanHealthDbHelper(this.getApplicationContext());
        db_write = dbHelper.getWritableDatabase();
        db_read = dbHelper.getReadableDatabase();

        exercise = findViewById(R.id.exercise);
        input_weight = findViewById(R.id.input_weight);
        btn_next = findViewById(R.id.btn_next);
        currentSet = findViewById(R.id.current_set);
        TextView_order = findViewById(R.id.TextView_order);
        TextView_reps = findViewById(R.id.TextView_reps);
        TextView_totalSet = findViewById(R.id.TextView_totalSet);
        TextView_timer_exercise = findViewById(R.id.timer_exercise);
        TextView_total_exercise = findViewById(R.id.total_exercise);
        timer_rest_second = findViewById(R.id.timer_rest_second);
        timer_rest_minute = findViewById(R.id.timer_rest_minute);
        goPrevious = findViewById(R.id.goPreviousBtn);
        goNext = findViewById(R.id.goNextBtn);

        ReadDB();
        // 운동 리스트
        exerciseArrayList = new ArrayList<>();
        // 끝나지 않은 운동 인덱스 리스트
        notClearExerciseIndex = new ArrayList<>();
        // 총 루틴 수를 가져옴
        numOfExercise = getRoutineCount() - 1;
        // db에서 읽어온 데이터로 운동 정보 생성 -> 이미 종료된 운동일 시 종료
        setInfoFromDB();
        // 모든 set를 수행하지 않은 운동 종목의 index를 가져와서 list에 저장
        getNotClearExerciseIndex();
        // 이전에 수행했던 전체 운동 시간을 가져와서 textView에 반영
        getPreviousExerciseTime();

        // 모든 운동 수행 여부에 따라서 현재 index를 다르게 보여줌
        if(notClearExerciseIndex.isEmpty()){
            indexCurrentExercise = 0;
        }else{
            indexCurrentExercise = notClearExerciseIndex.get(0);
        }

        setUI();

        this.settingSideBar();
        this.ExerciseClicked();
        this.RestClicked();
    }

    public class Exercise {
        String item;
        int reps;
        float weight;
        int set_count;
        int total_set_count;
        int rest_time;
        int order;

        // set_count를 늘리는 함수
        public void addSetCount() {
            set_count++;
        }

        // 현재 운동이 끝났는지 확인하는 함수
        public boolean isDone(){
            return set_count >= total_set_count;
        }
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

        final int[] count = {getPreviousExerciseTime()};
        final Timer[] timer = {null};

        btn_exercise.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 모든 운동이 종료되었으면 클릭 거부
                if(isEnd()) return;

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
                                    timer_exercise[0].setText(String.format("%02d:%02d", minutes, seconds));
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
        EditText timer_rest_minute = findViewById(R.id.timer_rest_minute);
        EditText timer_rest_second = findViewById(R.id.timer_rest_second);
        Button btn_rest = findViewById(R.id.btn_rest);

        btn_rest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 현재 운동이 종료되었으면 클릭 불가능
                if(exerciseArrayList.get(indexCurrentExercise).isDone()) return;

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

                    int minute = Integer.parseInt(timer_rest_minute.getText().toString());
                    int second = Integer.parseInt(timer_rest_second.getText().toString());

                    long timeInMillis = (minute * 60 + second) * 1000;
                    countDownTimer = new CountDownTimer(timeInMillis, 1000) {
                        @Override
                        public void onTick(long l) {
                            long minutes = TimeUnit.MILLISECONDS.toMinutes(l);
                            long seconds = TimeUnit.MILLISECONDS.toSeconds(l) - TimeUnit.MINUTES.toSeconds(minutes);
                            timer_rest_minute.setText(String.format("%02d",minutes));
                            timer_rest_second.setText(String.format("%02d",seconds));
                        }

                        @Override
                        public void onFinish() {
                            endRest();

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

    // 현재 운동 정보를 UI에 보여주는 함수
    // 특정 운동이 끝나거나, 화면 전환 시에 호출됨.
    // exerciseArrayList.get(indexCurrentExercise).
    public void setUI() {
        exercise.setText(exerciseArrayList.get(indexCurrentExercise).item);
        input_weight.setText(String.valueOf(exerciseArrayList.get(indexCurrentExercise).weight));
        currentSet.setText(String.valueOf(exerciseArrayList.get(indexCurrentExercise).set_count));
        TextView_order.setText(String.valueOf(indexCurrentExercise + 1));
        TextView_reps.setText(String.valueOf(exerciseArrayList.get(indexCurrentExercise).reps));
        TextView_total_exercise.setText(String.valueOf(numOfExercise + 1));
        TextView_totalSet.setText(String.valueOf(exerciseArrayList.get(indexCurrentExercise).total_set_count));
        timer_rest_second.setText(String.format("%02d",(int) exerciseArrayList.get(indexCurrentExercise).rest_time % 60));
        timer_rest_minute.setText(String.format("%02d",(int) exerciseArrayList.get(indexCurrentExercise).rest_time / 60));
    }

    public void setInfoFromDB() {
        while(cursor.moveToNext()) {
            Exercise object = new Exercise();
            object.item = cursor.getString(0);
            object.reps = Integer.parseInt(cursor.getString(1));
            object.weight = Float.valueOf(cursor.getString(2));
            object.set_count = Integer.parseInt(cursor.getString(3));
            object.total_set_count = Integer.parseInt(cursor.getString(4));
            object.rest_time = Integer.parseInt(cursor.getString(6));
            object.order = Integer.parseInt(cursor.getString(7));

            exerciseArrayList.add(object);
            Log.i("TimerActivity : setInfoFromDb","setINfoFromDb is called");
        }
        if(exerciseArrayList.isEmpty()) {
            Toast.makeText(getApplication(), "NO ROUTINE!",Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
    }

    // 완료되지 않은 운동의 index를 가져와서 보여줌
    public void getNotClearExerciseIndex() {
        Log.i("numOfExercise",String.valueOf(numOfExercise));
        // 전부다 수행됐다면 종료
        if(numOfExercise == 0) return;
        for(int i =0;i<=numOfExercise; i++) {
            if(!exerciseArrayList.get(i).isDone()) {
                notClearExerciseIndex.add((Integer) i);
            }
        }
    }

    // 총 운동 수를 가져옴
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

    // 모든 운동이 종료되었는지를 확인하는 함수
    public boolean isEnd() {
        if(indexCurrentExercise > numOfExercise) {
            return true;
        }
        Log.i("TimerActivity : isEnd",String.valueOf(notClearExerciseIndex.isEmpty()));
        return notClearExerciseIndex.isEmpty();
    }

    // isEnd()가 true라면은 실행되는 함순
    public void endTimerActivity() {
        // 오늘 수행한 운동량을 db에 저장
        String[] totalExerciseTime = TextView_timer_exercise.getText().toString().split(":");
        int minute = Integer.parseInt(totalExerciseTime[0]);
        int second = Integer.parseInt(totalExerciseTime[1]);
        if(minute + second != 0) { // 수행한 시간이 0이라면 기록 안 함
            ContentValues values = new ContentValues();
            values.put(UcanHealth.TotalExerciseTimeEntry.COLUMN_DATE, getCurrentDate());
            values.put(UcanHealth.TotalExerciseTimeEntry.COLUMN_TOTAL_EXERCISE_TIME, minute * 60 + second);
            try {
                long newRowId = db_write.insert(UcanHealth.TotalExerciseTimeEntry.TABLE_NAME, null, values);
                if (newRowId == -1) {
                    Log.i("insert", "end fail");
                    String selection = UcanHealth.TotalExerciseTimeEntry.COLUMN_DATE + " = ? ";
                    String[] selectionArgs = {
                            getCurrentDate()
                    };
                    long id = db_write.update(
                            UcanHealth.TotalExerciseTimeEntry.TABLE_NAME,
                            values,
                            selection,
                            selectionArgs
                    );
                    if(id == 0 ){
                        Log.i("TimerActivity : update total exercise time","fail");
                    }else{
                        Log.i("TimerActivity : update total exercise time","success");
                    }
                } else {
                    Log.i("insert", "end success");
                }
            } catch(Exception e) {

            }
        }
        for(Exercise object : exerciseArrayList) {
            try {
                ContentValues values = new ContentValues();
                values.put(UcanHealth.UserExerciseLogEntry.COLUMN_SET_COUNT, object.set_count);

                String selection = UcanHealth.UserExerciseLogEntry.COLUMN_ORDER + " = ? AND " +
                        UcanHealth.UserExerciseLogEntry.COLUMN_DATE + " = ?";
                String[] selectionArgs = {
                        String.valueOf(object.order),
                        getCurrentDate()
                };

                long id = db_write.update(
                        UcanHealth.UserExerciseLogEntry.TABLE_NAME,
                        values,
                        selection,
                        selectionArgs
                );
                if(id == 0) {
                    Log.i("TimerActivity : update set count","fail");
                }else{
                    Log.i("TimerActivity : update set count","success");
                }

            } catch(Exception e) {
                e.printStackTrace();
                Log.i("TimerActivity : update set count","exception");
            }
        }

        db_read.close();
        db_write.close();
        Toast.makeText(getApplicationContext(),"all routine is done. ",Toast.LENGTH_SHORT).show();
        finish();
    }

    // next버튼을 누르거나 쉬는 시간이 종료되었을 때 실행되는 함수.
    public void endRest() {
        int value = Integer.parseInt((String)currentSet.getText());
        value++;
        // 먼저 UI에서 세트를 하나 높혀줌
        currentSet.setText(String.valueOf(value));
        // 그리고 object도 늘려줌
        exerciseArrayList.get(indexCurrentExercise).addSetCount();

        // 현재 세트가 끝났는지 확인함.
        if(exerciseArrayList.get(indexCurrentExercise).isDone()) {
            notClearExerciseIndex.remove((Integer)indexCurrentExercise);
            if(isEnd()) {
                endTimerActivity();
            }
            else{
                indexCurrentExercise = notClearExerciseIndex.get(0);
                setUI();
            }
        }else{
            timer_rest_second.setText(String.format("%02d",(int) exerciseArrayList.get(indexCurrentExercise).rest_time % 60));
            timer_rest_minute.setText(String.format("%02d",(int) exerciseArrayList.get(indexCurrentExercise).rest_time / 60));
        }
    }

    // 이전에 운동했던 운동 기록이 있다면 가져옴.
    public int getPreviousExerciseTime() {
        String[] projection = {
                UcanHealth.TotalExerciseTimeEntry.COLUMN_TOTAL_EXERCISE_TIME
        };

        String selection = UcanHealth.TotalExerciseTimeEntry.COLUMN_DATE + " = ? ";
        String[] selectionArgs = {
                getCurrentDate()
        };
        Cursor cursor  = db_read.query(
                UcanHealth.TotalExerciseTimeEntry.TABLE_NAME,
                projection,
                selection,
                selectionArgs,
                null,
                null,
                null
        );
        if(!cursor.moveToNext()) return 0;
        int totalExerciseTime = Integer.parseInt(cursor.getString(0));
        int minute = (int) totalExerciseTime / 60;
        int second = totalExerciseTime % 60;
        TextView_timer_exercise.setText(String.format("%02d:%02d",minute,second));

        return totalExerciseTime;

    }

    public View.OnClickListener nextSetClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if(exerciseArrayList.get(indexCurrentExercise).isDone()) {
                Toast.makeText(getApplicationContext(),"current set is doen.",Toast.LENGTH_SHORT).show();
                return;
            }
            endRest();
        }
    };

    public View.OnClickListener goPreviousExercise = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if(indexCurrentExercise == 0) {
                return;
            }
            indexCurrentExercise--;
            setUI();
        }
    };
    public View.OnClickListener goNextExercise = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if(indexCurrentExercise == numOfExercise ) {
                Log.i("TimerActivity : goNextExercise","indexCurrentExercise == numOfExercise");
                if(isEnd()) {
                    endTimerActivity();
                }else {
                    Dialog();
                }
            }else {
                indexCurrentExercise++;
                setUI();
            }
        }
    };

    public void Dialog() {
        endExerciseDialog = new EndExerciseDialog(TimerActivity.this);
        endExerciseDialog.getWindow().setGravity(Gravity.CENTER);
        endExerciseDialog.setCancelable(true);
        endExerciseDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {
            }
        });
        endExerciseDialog.show();
    }

    // 운동이 종료가 안되었음에도 불구하고 종료를 할 건지 묻는 다이얼로그
    public class EndExerciseDialog extends Dialog {
        Button closeBtn;
        Button endBtn;
        public EndExerciseDialog(@NonNull Context context) {
            super(context);
        }

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            WindowManager.LayoutParams lpWindow = new WindowManager.LayoutParams();
            lpWindow.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND;
            lpWindow.dimAmount = 0.8f;
            getWindow().setAttributes(lpWindow);

            setContentView(R.layout.end_exercise);
            init();

            closeBtn.setOnClickListener(closeDialog);
            endBtn.setOnClickListener(endDialog);
        }

        private void init() {
            closeBtn = findViewById(R.id.closeBtn);
            endBtn = findViewById(R.id.endBtn);
        }

        private final View.OnClickListener closeDialog = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        };

        private final View.OnClickListener endDialog = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                endTimerActivity();
                dismiss();
            }
        };
    }
}
