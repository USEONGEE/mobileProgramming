package com.example.ucanhealth.aboutAddRoutine;

import android.app.Dialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;

import androidx.annotation.NonNull;

import android.database.sqlite.SQLiteDatabase;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.ucanhealth.R;
import com.example.ucanhealth.sqlite.UcanHealth;
import com.example.ucanhealth.sqlite.UcanHealthDbHelper;

import java.util.Calendar;

public class addRoutineDialog extends Dialog {
    String exercise;
    private UcanHealthDbHelper dbHelper;
    private SQLiteDatabase db_write;
    private SQLiteDatabase db_read;
    Button addBtn;
    Button closeBtn;
    TextView exerciseView;
    EditText repEditText;
    EditText totalSetEditText;
    EditText weightEditText;
    EditText restTimeEditText;
    ErrorDialog dialog;
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
        dbHelper = new UcanHealthDbHelper(getContext().getApplicationContext());
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

        restTimeEditText = findViewById(R.id.restTime);
        restTimeEditText.setInputType(InputType.TYPE_CLASS_NUMBER);
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
        String reps = repEditText.getText().toString();
        String totalSet = totalSetEditText.getText().toString();
        String weight = weightEditText.getText().toString();
        String today = getCurrentDate();
//        String today = "2023-05-23";
        String restTime = restTimeEditText.getText().toString();
        String order = String.valueOf(getRoutineCount() + 1);

        ContentValues values = new ContentValues();
        if(!isNull(exercise)) values.put(UcanHealth.UserExerciseLogEntry.COLUMN_EXERCISE,exercise);
        else {
            invalidInput("INVALID EXERCISE NAME");
            return;
        }

        if(!isNull(reps)) values.put(UcanHealth.UserExerciseLogEntry.COLUMN_REPS, reps);
        else {
            invalidInput("INVALID RPES");
            return;
        }

        if(!isNull(weight)) values.put(UcanHealth.UserExerciseLogEntry.COLUMN_WEIGHT, weight);
        else {
            invalidInput("INVALID WEIGHT");
            return;
        }

        if(!isNull(totalSet)) values.put(UcanHealth.UserExerciseLogEntry.COLUMN_TOTAL_SET_COUNT,totalSet);
        else {
            invalidInput("INVALID TOTAL SET");
            return;
        }

        values.put(UcanHealth.UserExerciseLogEntry.COLUMN_SET_COUNT, 1); // 처음 초기화 ZSS = 1
        values.put(UcanHealth.UserExerciseLogEntry.COLUMN_DATE, today);

        if(!isNull(restTime)) values.put(UcanHealth.UserExerciseLogEntry.COLUMN_REST_TIME, restTime);
        else {
            invalidInput("INVALID REST TIME");
            return;
        }

       values.put(UcanHealth.UserExerciseLogEntry.COLUMN_ORDER, order);

        long newRowId = db_write.insert(UcanHealth.UserExerciseLogEntry.TABLE_NAME, null, values);
        if(newRowId == -1) {
            Log.i("insert", "fail");
        }
        else {
            Log.i("insert", "success");
        }
    }

    private int getRoutineCount() {
        String sql = String.format("SELECT COUNT(*) FROM %s WHERE %s = '%s'", UcanHealth.UserExerciseLogEntry.TABLE_NAME,
                UcanHealth.UserExerciseLogEntry.COLUMN_DATE,
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

    public boolean isNull(String s) {
        return s.equals("");
    }

    /*
    * 올바르지 못한 입력값을 입력했을 때 에러 메세지 다이얼로그를 띄운 후 종료
    * */
    public void invalidInput(String errorMsg) {
        dialog = new ErrorDialog(getContext(),errorMsg);
        dialog.setTitle(R.string.add_routine);
        dialog.getWindow().setGravity(Gravity.CENTER);
        dialog.setCancelable(true);
        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {
                db_write.close();
                db_read.close();
                dismiss();
            }
        });
        dialog.show();
    }
}
