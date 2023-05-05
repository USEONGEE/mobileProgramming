package com.example.ucanhealth;

import android.app.Dialog;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;

import androidx.annotation.NonNull;

import android.database.sqlite.SQLiteDatabase;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.ucanhealth.sqlite.ExerciseTypeDbHelper;
import com.example.ucanhealth.sqlite.UserExerciseLog;
import com.example.ucanhealth.sqlite.UserExerciseLogDbHelper;

import java.time.LocalDate;
import java.util.Calendar;

public class addRoutineDialog extends Dialog {
    String exercise;
    private UserExerciseLogDbHelper dbHelper;
    private SQLiteDatabase db_write;
    private SQLiteDatabase db_read;
    Button addBtn;
    Button closeBtn;
    TextView exerciseView;
    EditText repEditText;
    EditText totalSetEditText;
    EditText weightEditText;
    public addRoutineDialog(@NonNull Context context, String exercise) {
        super(context);
        this.exercise = exercise;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 화면 설정
        WindowManager.LayoutParams lpWindow = new WindowManager.LayoutParams();
        lpWindow.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        lpWindow.dimAmount = 0.8f;
        getWindow().setAttributes(lpWindow);

        setContentView(R.layout.add_routine);

        // View들 init하기
        initView();

        //dbHelper = new UserExerciseLogDbHelper(getContext());
        dbHelper = new UserExerciseLogDbHelper(getContext().getApplicationContext());
        db_write = dbHelper.getWritableDatabase();
        db_read = dbHelper.getReadableDatabase();

        addBtn.setOnClickListener(addRoutineAndCloseDialog);
        closeBtn.setOnClickListener(closeDialog);
    }

    private void initView() {
        exerciseView = findViewById(R.id.exercise);
        exerciseView.setText(exercise);

        addBtn = findViewById(R.id.addBtn);
        closeBtn = findViewById(R.id.closeBtn);

        repEditText = findViewById(R.id.rep);
        repEditText.setInputType(InputType.TYPE_CLASS_NUMBER);

        totalSetEditText = findViewById(R.id.totalSet);
        totalSetEditText.setInputType(InputType.TYPE_CLASS_NUMBER);

        weightEditText = findViewById(R.id.weight);
        weightEditText.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
    }


    // 루틴 추가해야됨
    private final View.OnClickListener addRoutineAndCloseDialog = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            addRoutineToDb();
            db_write.close();
            db_read.close();
            dismiss();
        }
    };

    private final View.OnClickListener closeDialog = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            dismiss();
        }
    };

    private void addRoutineToDb() {

        int rep = Integer.parseInt(repEditText.getText().toString()); repEditText.setText("");
        int totalSet = Integer.parseInt(totalSetEditText.getText().toString()); totalSetEditText.setText("");
        float weight = Float.parseFloat(weightEditText.getText().toString()); weightEditText.setText("");
        String today = getCurrentDate();
        int order = getRoutineCount() + 1;

        Log.i("insert", today); //

        ContentValues values = new ContentValues();
        values.put(UserExerciseLog.UserExerciseLogEntry.COLUMN_EXERCISE,exercise);
        values.put(UserExerciseLog.UserExerciseLogEntry.COLUMN_REPS, rep);
        values.put(UserExerciseLog.UserExerciseLogEntry.COLUMN_WEIGHT, weight);
        values.put(UserExerciseLog.UserExerciseLogEntry.COLUMN_TOTAL_SET_COUNT,totalSet);
        values.put(UserExerciseLog.UserExerciseLogEntry.COLUMN_SET_COUNT, 0);
        values.put(UserExerciseLog.UserExerciseLogEntry.COLUMN_DATE, today);
        values.put(UserExerciseLog.UserExerciseLogEntry.COLUMN_TOTAL_EXERCISE_TIME, 0);
        values.put(UserExerciseLog.UserExerciseLogEntry.COLUMN_ORDER, order);

        long newRowId = db_write.insert(UserExerciseLog.UserExerciseLogEntry.TABLE_NAME, null, values);
        if(newRowId == -1) {
            Log.i("insert", "fail");
        }
        else {
            Log.i("insert", "success");
        }
    }

    private int getRoutineCount() {
        String sql = String.format("SELECT COUNT(*) FROM %s WHERE %s = '%s'", UserExerciseLog.UserExerciseLogEntry.TABLE_NAME,
                UserExerciseLog.UserExerciseLogEntry.COLUMN_DATE,
                getCurrentDate());
        Cursor cursor = db_read.rawQuery(sql, null);

        cursor.moveToNext();

        int count = cursor.getInt(0);
        Log.i("count",String.valueOf(count));

        return count;
    }

    public String getCurrentDate() {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH) + 1; // month는 0부터 시작하므로 1을 더해줍니다.
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        return String.format("%04d-%02d-%02d", year, month, day);
    }
}
