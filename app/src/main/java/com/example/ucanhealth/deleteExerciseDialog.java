package com.example.ucanhealth;

import android.app.Dialog;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;

import androidx.annotation.NonNull;

import com.example.ucanhealth.sqlite.ExerciseTypeDbHelper;
import com.example.ucanhealth.sqlite.UserExerciseLog;

import java.util.Calendar;

public class deleteExerciseDialog extends Dialog {

    private ExerciseTypeDbHelper exerciseTypeDbHelper;
    private SQLiteDatabase exerciseTypeDb_write;
    Button closeBtn;
    Button deleteBtn;
    String seletedExercise;

    public deleteExerciseDialog(@NonNull Context context, String seletedExercise) {
        super(context);
        this.seletedExercise = seletedExercise;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 다이얼로그 외부 화면 흐리게 표현
        WindowManager.LayoutParams lpWindow = new WindowManager.LayoutParams();
        lpWindow.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        lpWindow.dimAmount = 0.8f;
        getWindow().setAttributes(lpWindow);

        setContentView(R.layout.delete_exercise);

        // 닫기 버튼
        closeBtn.setOnClickListener(closeDialog);
        // 삭제 버튼
        deleteBtn.setOnClickListener(deleteExercise);

    }

    private void init() {
        closeBtn = findViewById(R.id.closeBtn);
        deleteBtn = findViewById(R.id.deleteBtn);

        exerciseTypeDbHelper = new ExerciseTypeDbHelper(getContext());
        exerciseTypeDb_write = exerciseTypeDbHelper.getWritableDatabase();
    }

    private final View.OnClickListener closeDialog = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            dismiss();
        }
    };

    private final View.OnClickListener deleteExercise = new View.OnClickListener() {
        @Override
        public void onClick(View view) {

        }
    };

    // 특정 exercise를 삭제하면 이전의 데이터는 어떻게 보여줄 것인지 생각해야됨
    private void deleteExerciseToDb() {

    };


    public String getCurrentDate() {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH) + 1; // month는 0부터 시작하므로 1을 더해줍니다.
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        return String.format("%04d-%02d-%02d", year, month, day);
    };


}

