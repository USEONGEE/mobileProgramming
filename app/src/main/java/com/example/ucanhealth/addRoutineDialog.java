package com.example.ucanhealth;

import android.app.Dialog;
import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.ucanhealth.sqlite.ExerciseType;
import com.example.ucanhealth.sqlite.ExerciseTypeDbHelper;
import com.example.ucanhealth.sqlite.FeedReaderDbHelper;

public class addRoutineDialog extends Dialog {
    addExerciseDialog dialog;
    Button addBtn;
    Button closeBtn;
    public addRoutineDialog(@NonNull Context context) {
        super(context);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 다이얼로그 외부 화면 흐리게 표현
        WindowManager.LayoutParams lpWindow = new WindowManager.LayoutParams();
        lpWindow.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        lpWindow.dimAmount = 0.8f;
        getWindow().setAttributes(lpWindow);

        setContentView(R.layout.add_routine);

        addBtn = findViewById(R.id.addBtn);
        addBtn.setOnClickListener(openExerciseDialog);

        closeBtn = findViewById(R.id.closeBtn);
        closeBtn.setOnClickListener(closeDialog);
    }

    private View.OnClickListener closeDialog = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            dismiss();
        }
    };

    private View.OnClickListener openExerciseDialog = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Dialog();
        }
    };

    public void Dialog() {
        dialog = new addExerciseDialog(getContext());
        dialog.setTitle(R.string.add_routine);
        dialog.getWindow().setGravity(Gravity.CENTER);
        dialog.setCancelable(true);
        dialog.show();
    }


}