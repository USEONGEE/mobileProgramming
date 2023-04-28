package com.example.ucanhealth;

import android.app.Dialog;
import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;

import com.example.ucanhealth.sqlite.ExerciseType;
import com.example.ucanhealth.sqlite.ExerciseTypeDbHelper;

public class addExerciseDialog extends Dialog {

    private ExerciseTypeDbHelper dbHelper;
    private SQLiteDatabase db_read;
    private SQLiteDatabase db_write;
    private Button closeDialogBtn;
    private Button addBtn;

    private EditText category;
    private EditText exercise_type;
    private EditText exercise;
    public addExerciseDialog(@NonNull Context context) {
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

        setContentView(R.layout.add_exercise);

        closeDialogBtn = findViewById(R.id.clsoeBtn);
        closeDialogBtn.setOnClickListener(closeDialog);

        // edit text

        // db
        dbHelper = new ExerciseTypeDbHelper(getContext());
        db_read = dbHelper.getReadableDatabase();
        db_write = dbHelper.getWritableDatabase();

        addBtn = findViewById(R.id.addBtn);
        addBtn.setOnClickListener(setExercise);
    }

    private View.OnClickListener closeDialog = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            dismiss();
        }
    };

    public boolean isNull(String str) {
        return str.equals("");
    }

    public View.OnClickListener setExercise = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            category = findViewById(R.id.category);
            exercise_type = findViewById(R.id.exercise_type);
            exercise = findViewById(R.id.exercise);

            // EditText값 가져오고 지우기
            String categoryText = category.getText().toString(); category.setText("");
            String exercise_typeText = exercise_type.getText().toString(); exercise_type.setText("");
            String exerciseText = exercise.getText().toString(); exercise.setText("");

            if(isNull(categoryText) && isNull(exercise_typeText) && isNull(exerciseText)) {
                return;
            }

            ContentValues values = new ContentValues();
            values.put(ExerciseType.ExerciseTypeEntry.COLUMN_CATEGORY, categoryText);
            values.put(ExerciseType.ExerciseTypeEntry.COLUMN_EXERCISE_TYPE, exercise_typeText);
            values.put(ExerciseType.ExerciseTypeEntry.COLUMN_EXERCISE, exerciseText);

            long newRowId = db_write.insert(ExerciseType.ExerciseTypeEntry.TABLE_NAME,null,values);

            if(newRowId == -1)
                Log.i("insert", "fail");
            else
                Log.i("insert", "success");
        }
    };

//    public View.OnClickListener getExercise = new View.OnClickListener() {
//        @Override
//        public void onClick(View view) {
//
//        }
//    }

}