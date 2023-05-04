package com.example.ucanhealth;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ucanhealth.sqlite.UserExerciseLogDbHelper;
import com.google.android.material.navigation.NavigationView;

import java.util.Calendar;


public class MainActivity extends AppCompatActivity {

    private ExerciseSettingDialog dialog;
    Button addRoutineBtn;
    private LinearLayout todayExerciseListContainer;
    private UserExerciseLogDbHelper userExerciseLogDbHelper;
    private SQLiteDatabase userExerciseLogDb_read;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        this.InitializeLayout();

        init();
        addRoutineBtn.setOnClickListener(openExerciseSettingDialog);
        setButtonInRoutineListContainer();
    }
    public void init() {
        addRoutineBtn = findViewById(R.id.addButton);
        todayExerciseListContainer = findViewById(R.id.todayExerciseListContainer);

        userExerciseLogDbHelper = new UserExerciseLogDbHelper(this);
        userExerciseLogDb_read = userExerciseLogDbHelper.getReadableDatabase();
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
        for (int i = todayExerciseListContainer.getChildCount() - 1;  i >= 0; i--) {
            View view = todayExerciseListContainer.getChildAt(i);
            todayExerciseListContainer.removeView(view); // 레이아웃에서 TextView 제거
        }

        Cursor cursor = userExerciseLogDbHelper.getRoutineByDate(userExerciseLogDb_read, getCurrentDate());

        // textView 꾸며야함
        while(cursor.moveToNext()) {
            Log.i("makeButton","success");
            TextView textView = new TextView(this);
            String exercise = cursor.getString(0);
            String reps = cursor.getString(1).toString();
            String weight = cursor.getString(2).toString();
            String totalSet = cursor.getString(4).toString();

            String text = exercise + " / " + reps + "회 / " + totalSet + "세트 / " + weight + "kg ";
            textView.setText(text);

            todayExerciseListContainer.addView(textView);
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
}
