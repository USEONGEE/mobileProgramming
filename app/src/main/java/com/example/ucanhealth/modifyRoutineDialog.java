package com.example.ucanhealth;

import android.app.Dialog;
import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.example.ucanhealth.sqlite.ExerciseType;
import com.example.ucanhealth.sqlite.ExerciseTypeDbHelper;
import com.example.ucanhealth.sqlite.UserExerciseLog;
import com.example.ucanhealth.sqlite.UserExerciseLogDbHelper;

import java.util.Calendar;

public class modifyRoutineDialog extends Dialog {

    private UserExerciseLogDbHelper userExerciseLogDbHelper;
    private SQLiteDatabase userExerciseLogDb_write;
    private SQLiteDatabase userExerciseLogDb_read;
    Button closeBtn;
    Button deleteBtn;
    Button modifyBtn;
    String selectedExercise;
    TextView exerciseType;
    EditText repEditText;
    EditText totalSetEditText;
    EditText weightEditText;

    public modifyRoutineDialog(@NonNull Context context, String selectedExercise) {
        super(context);
        this.selectedExercise = selectedExercise;
        Log.i("selectedExercise",selectedExercise);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 다이얼로그 외부 화면 흐리게 표현
        WindowManager.LayoutParams lpWindow = new WindowManager.LayoutParams();
        lpWindow.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        lpWindow.dimAmount = 0.8f;
        getWindow().setAttributes(lpWindow);

        setContentView(R.layout.modify_routine);

        init();

        // 닫기 버튼
        closeBtn.setOnClickListener(closeDialog);
        // 삭제 버튼
        deleteBtn.setOnClickListener(deleteExercise);
        // 수정 버튼
        modifyBtn.setOnClickListener(modifyExercise);

    }

    private void init() {
        closeBtn = findViewById(R.id.closeBtn);
        deleteBtn = findViewById(R.id.deleteBtn);
        modifyBtn = findViewById(R.id.modifyBtn);
        exerciseType = findViewById(R.id.exercise);
        exerciseType.setText(selectedExercise);

        repEditText = findViewById(R.id.rep);
        repEditText.setInputType(InputType.TYPE_CLASS_NUMBER);

        totalSetEditText = findViewById(R.id.totalSet);
        totalSetEditText.setInputType(InputType.TYPE_CLASS_NUMBER);

        weightEditText = findViewById(R.id.weight);
        weightEditText.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);

        userExerciseLogDbHelper = new UserExerciseLogDbHelper(getContext());
        userExerciseLogDb_write = userExerciseLogDbHelper.getWritableDatabase();
        userExerciseLogDb_read = userExerciseLogDbHelper.getReadableDatabase();
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
            String selection = UserExerciseLog.UserExerciseLogEntry.COLUMN_DATE + " LIKE ? AND " +
                    UserExerciseLog.UserExerciseLogEntry.COLUMN_EXERCISE + " = ?";
            String[] selectionArgs = {getCurrentDate(), selectedExercise};
            Log.i("delete",selectedExercise);

            userExerciseLogDb_write.delete(UserExerciseLog.UserExerciseLogEntry.TABLE_NAME,
                    selection,
                    selectionArgs);

            userExerciseLogDbHelper.close();
            userExerciseLogDb_write.close();
            userExerciseLogDb_read.close();
            dismiss();
        }
    };

    private final View.OnClickListener modifyExercise = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            ContentValues values = new ContentValues();

            String reps = repEditText.getText().toString();
            String totalSet = totalSetEditText.getText().toString();
            String weight = weightEditText.getText().toString();

            if(!isNull(reps)) values.put(UserExerciseLog.UserExerciseLogEntry.COLUMN_REPS, Integer.parseInt(reps));
            if(!isNull(totalSet)) values.put(UserExerciseLog.UserExerciseLogEntry.COLUMN_TOTAL_SET_COUNT,Integer.parseInt(totalSet));
            if(!isNull(weight)) values.put(UserExerciseLog.UserExerciseLogEntry.COLUMN_WEIGHT, weight);

            // 아무것도 입력 안 하면 그냥 종료시키기
            if(isNull(reps) && isNull(totalSet) && isNull(weight)) {
                userExerciseLogDbHelper.close();
                userExerciseLogDb_write.close();
                userExerciseLogDb_read.close();
                dismiss();

                return;
            }

            String selection = UserExerciseLog.UserExerciseLogEntry.COLUMN_EXERCISE + " = ? AND " +
                    UserExerciseLog.UserExerciseLogEntry.COLUMN_DATE + " = ?";
            String[] selectionArgs = {
                    selectedExercise,
                    getCurrentDate()
            };

            int count = userExerciseLogDb_write.update(
                    UserExerciseLog.UserExerciseLogEntry.TABLE_NAME,
                    values,
                    selection,
                    selectionArgs
            );
            Log.i("update",String.valueOf(count));

            userExerciseLogDbHelper.close();
            userExerciseLogDb_write.close();
            userExerciseLogDb_read.close();
            dismiss();
        }
    };

    public String getCurrentDate() {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH) + 1; // month는 0부터 시작하므로 1을 더해줍니다.
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        return String.format("%04d-%02d-%02d", year, month, day);
    };

    public boolean isNull(String s) {
        return s.equals("");
    }
}

