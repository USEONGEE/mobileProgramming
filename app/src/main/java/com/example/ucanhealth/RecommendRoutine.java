package com.example.ucanhealth;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.material.navigation.NavigationView;

public class RecommendRoutine extends AppCompatActivity {
    FirstFragment firstFragment;
    SecondFragment secondFragment;
    ThirdFragment thirdFragment;

    Button firstBtn;
    Button secondBtn;
    Button thirdBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recommend_routine);

        InitializeLayout();

        firstFragment = new FirstFragment();
        secondFragment = new SecondFragment();
        thirdFragment = new ThirdFragment();

        firstBtn = findViewById(R.id.firstBtn);
        firstBtn.setOnClickListener(changeFragment1);
        secondBtn = findViewById(R.id.secondBtn);
        secondBtn.setOnClickListener(changeFragment2);
        thirdBtn = findViewById(R.id.thirdBtn);
        thirdBtn.setOnClickListener(changeFragment3);

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
                    case R.id.MainPage:
                        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                        startActivity(intent);
                        break;
                    case R.id.recommendRoutine:
                        Intent intent2 = new Intent(getApplicationContext(), RecommendRoutine.class);
                        startActivity(intent2);
                        break;
                    case R.id.SchdulerPage:
                        Intent intent3 = new Intent(getApplicationContext(), exerciseScheduler.class);
                        startActivity(intent3);
                        break;
                }
                drawer.closeDrawer(GravityCompat.START);
                return false;
            }
        });
    }

    public void onFragmentChanged(int index) {
        if(index == 1) {
            // getSupportFragmentManager()을 사용하면 오류임
            getFragmentManager().beginTransaction().replace(R.id.container, firstFragment).commit();
        } else if (index == 2) {
            getFragmentManager().beginTransaction().replace(R.id.container, secondFragment).commit();
        }else if(index == 3){
            getFragmentManager().beginTransaction().replace(R.id.container, thirdFragment).commit();
        }
    }

    private final View.OnClickListener changeFragment1 = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            onFragmentChanged(1);
        }
    };
    private final View.OnClickListener changeFragment2 = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            onFragmentChanged(2);
        }
    };
    private final View.OnClickListener changeFragment3 = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            onFragmentChanged(3);
        }
    };
}