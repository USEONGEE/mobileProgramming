package com.example.ucanhealth;

import android.app.Dialog;
import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import androidx.annotation.NonNull;

import com.example.ucanhealth.sqlite.ExerciseType;
import com.example.ucanhealth.sqlite.ExerciseTypeDbHelper;

public class addExerciseDialog extends Dialog {

    Context context;

    private ExerciseTypeDbHelper dbHelper;
    private SQLiteDatabase db_write;
    private Button closeDialogBtn;
    private Button addBtn;
    private Spinner spinner;
    String exercise_typeText;
    final String categoryText = "Health";
    private EditText exercise;
    public addExerciseDialog(@NonNull Context context) {
        super(context);
        this.context = context;
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
        db_write = dbHelper.getWritableDatabase();

        addBtn = findViewById(R.id.addBtn);
        addBtn.setOnClickListener(setExercise);

        spinner = findViewById(R.id.spinner);
        getSpinner();
    }

    private final View.OnClickListener closeDialog = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            db_write.close();
            dismiss();
        }
    };

    public boolean isNull(String str) {
        return str.equals("");
    }

    public View.OnClickListener setExercise = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            exercise = findViewById(R.id.exercise);

            // EditText값 가져오고 지우기
            String exerciseText = exercise.getText().toString(); exercise.setText("");

            if(isNull(exercise_typeText) && isNull(exerciseText)) {
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

    public void getSpinner() {
        String[] dataList = {
                "back", "chest", "shoulder", "leg", "arm", "core"
        };
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext(),
                android.R.layout.simple_spinner_item, dataList);

        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // 선택된 항목 처리
                String selectedValue = parent.getItemAtPosition(position).toString();
                exercise_typeText = selectedValue;
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // 선택된 항목이 없는 경우 처리
            }
        });
    }

}