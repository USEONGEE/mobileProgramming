package com.example.ucanhealth;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.ucanhealth.aboutAddRoutine.ExerciseSettingDialog;
import com.example.ucanhealth.exercising.TimerActivity;
import com.example.ucanhealth.overload.ProgressiveOverload;
import com.example.ucanhealth.recommend.menu_Routine;
import com.example.ucanhealth.schedule.exerciseScheduler;
import com.example.ucanhealth.sqlite.UcanHealthDbHelper;
import com.example.ucanhealth.statistic.Statistics;
import com.google.android.material.navigation.NavigationView;

import java.util.Calendar;

public class MainActivity extends AppCompatActivity {

    private ExerciseSettingDialog dialog;
    Button addRoutineBtn;
    Button startBtn;
    private LinearLayout todayExerciseListContainer;
    private UcanHealthDbHelper ucanHealthDbHelper;
    private SQLiteDatabase ucanHealthDb_read;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        InitializeLayout();

        init();
        addRoutineBtn.setOnClickListener(openExerciseSettingDialog);
        startBtn.setOnClickListener(startExercise);
        setButtonInRoutineListContainer();
    }

    @Override
    protected void onResume() {
        super.onResume();

        setButtonInRoutineListContainer();
    }

    public void init() {
        addRoutineBtn = findViewById(R.id.addButton);
        todayExerciseListContainer = findViewById(R.id.todayExerciseListContainer);
        startBtn = findViewById(R.id.startBtn);

        ucanHealthDbHelper = new UcanHealthDbHelper(this);
        ucanHealthDb_read = ucanHealthDbHelper.getReadableDatabase();
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

    public void Dialog() {
        dialog = new ExerciseSettingDialog(MainActivity.this);
        dialog.setTitle(R.string.add_routine);
        dialog.getWindow().setGravity(Gravity.CENTER);
        dialog.setCancelable(true);
        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {
                setButtonInRoutineListContainer();
            }
        });
        dialog.show();
    }

    private View.OnClickListener openExerciseSettingDialog = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Dialog();
        }
    };

    public void setButtonInRoutineListContainer() {
        // 현재 container에 있는 리스트 지우기
        for (int i = todayExerciseListContainer.getChildCount() - 1; i >= 0; i--) {
            View view = todayExerciseListContainer.getChildAt(i);
            todayExerciseListContainer.removeView(view); // 레이아웃에서 TextView 제거
        }

        Cursor cursor = ucanHealthDbHelper.getRoutineByDate(ucanHealthDb_read, getCurrentDate());

        // textView 꾸며야함
        while (cursor.moveToNext()) {
            String exercise = cursor.getString(0);
            String setCount = cursor.getString(3);
            String total_set = cursor.getString(4);

            TextView textView = new TextView(this);
            ImageButton imgBtn = new ImageButton(this);
            imgBtn.setBackgroundColor(Color.TRANSPARENT);


            if (setCount.equals(total_set)) {
                Bitmap originalBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.check_done);
                Bitmap resizedBitmap = Bitmap.createScaledBitmap(originalBitmap, 100, 100, true);
                imgBtn.setImageBitmap(resizedBitmap);
            }
            else {
                Bitmap originalBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.check);
                Bitmap resizedBitmap = Bitmap.createScaledBitmap(originalBitmap, 100, 100, true);
                imgBtn.setImageBitmap(resizedBitmap);
            }

            String text = exercise;
            textView.setText(text);
            textView.setTextSize(20);
            textView.setTypeface(null, Typeface.BOLD);
            textView.setTextColor(Color.rgb(0, 0, 0));

            // LinearLayout.LayoutParams를 사용하여 TextView의 레이아웃 매개변수 설정
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );
            layoutParams.gravity = Gravity.CENTER_VERTICAL; // 상하 중앙으로 정렬
            textView.setLayoutParams(layoutParams);

            LinearLayout layout = new LinearLayout(this);
            layout.setOrientation(LinearLayout.HORIZONTAL);

            layout.addView(imgBtn);
            layout.addView(textView);
            todayExerciseListContainer.addView(layout);
        }
    }

    public String getCurrentDate() {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH) + 1; // month는 0부터 시작하므로 1을 더해줍니다.
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        Log.i("stirng", String.format("%04d-%02d-%02d", year, month, day));
        return String.format("%04d-%02d-%02d", year, month, day);
    }

    public View.OnClickListener startExercise = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Intent intent = new Intent(getApplicationContext(), TimerActivity.class);
            startActivity(intent);
        }
    };
}
