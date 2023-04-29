package com.example.ucanhealth;

import android.app.Dialog;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;
import android.provider.BaseColumns;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.ucanhealth.sqlite.ExerciseType;
import com.example.ucanhealth.sqlite.ExerciseTypeDbHelper;
import com.example.ucanhealth.sqlite.FeedReaderDbHelper;

import java.util.ArrayList;
import java.util.List;

public class addRoutineDialog extends Dialog {
    private ExerciseTypeDbHelper dbHelper;
    private SQLiteDatabase db_read;
    LinearLayout exerciseListContainer;
    addExerciseDialog dialog;
    Button addBtn;
    Button closeBtn;
    Spinner spinner;

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

        dbHelper = new ExerciseTypeDbHelper(getContext());
        db_read = dbHelper.getReadableDatabase();
        getSpinner();

        exerciseListContainer = findViewById(R.id.exerciseListContainer);
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

    public List<String> readCategoryFromDb(){
        db_read = dbHelper.getReadableDatabase();

        // Define a projection that specifies which columns from the database
        // you will actually use after this query.
        String[] projection = {
                "DISTINCT " + ExerciseType.ExerciseTypeEntry.COLUMN_CATEGORY
        };

        String sortOrder =
                ExerciseType.ExerciseTypeEntry.COLUMN_CATEGORY + " DESC";

        Cursor cursor = db_read.query(
                ExerciseType.ExerciseTypeEntry.TABLE_NAME,   // The table to query
                projection,             // The array of columns to return (pass null to get all)
                null,              // The columns for the WHERE clause
                null,          // The values for the WHERE clause
                null,                   // don't group the rows
                null,                   // don't filter by row groups
                sortOrder               // The sort order
        );

        List<String> category = new ArrayList<>();
        category.add("NO SELECT");
        while(cursor.moveToNext()) {
            String item = cursor.getString(0); // 0번째 인덱스의 데이터 가져오기
            category.add(item);
        }

        cursor.close(); // 커서 닫기

        return category;
    };

    // onResume()에 정의되어야하는 메소드 근데 onResume()이 없음 ㅜㅜ
    public void getSpinner() {
        List<String> dataList = readCategoryFromDb();
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext(),
                android.R.layout.simple_spinner_item, dataList);

        spinner.setAdapter(adapter);
    }


    public void setButtonFromExerciseList(String selectedCategory) {
        // 현재 container에 있는 리스트 지우기
        for (int i = 0; i < exerciseListContainer.getChildCount(); i++) {
            View view = exerciseListContainer.getChildAt(i);
            if (view instanceof TextView) { // TextView 인 경우만 처리
                exerciseListContainer.removeView(view); // 레이아웃에서 TextView 제거
            }
        }

        // container에 리스트 추가하기
        List<String> exerciseList = readExerciseListFromDb(selectedCategory);
        for (int i = 0 ; i < exerciseList.size(); i++) {
            TextView textView = new TextView(getContext());
            textView.setText(exerciseList.get(i));
            exerciseListContainer.addView(textView);
        }

    }

    public List<String> readExerciseListFromDb(String selectedCategory){
        // Query 결과로 받아올 column 정의하기
        String[] projection = {
                ExerciseType.ExerciseTypeEntry.COLUMN_EXERCISE
        };

        // where절
        String selection = ExerciseType.ExerciseTypeEntry.COLUMN_CATEGORY + " = ?";
        String[] selectionArgs = { selectedCategory };

        // 정렬
        String sortOrder =
                ExerciseType.ExerciseTypeEntry.COLUMN_EXERCISE + " DESC";

        // Queyr 결과를 담은 cursor 가져오기
        Cursor cursor = db_read.query(
                ExerciseType.ExerciseTypeEntry.TABLE_NAME,   // The table to query
                projection,             // The array of columns to return (pass null to get all)
                selection,              // The columns for the WHERE clause
                selectionArgs,          // The values for the WHERE clause
                null,                   // don't group the rows
                null,                   // don't filter by row groups
                sortOrder               // The sort order
        );

        List<String> exerciseList = new ArrayList<>();
        while(cursor.moveToNext()) {
            String item = cursor.getString(0); // 0번째 인덱스의 데이터 가져오기
            exerciseList.add(item);
        }

        cursor.close(); // 커서 닫기

        return exerciseList;
    };

}