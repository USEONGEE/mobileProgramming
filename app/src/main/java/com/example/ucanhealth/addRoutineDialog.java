package com.example.ucanhealth;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.example.ucanhealth.sqlite.ExerciseType;
import com.example.ucanhealth.sqlite.ExerciseTypeDbHelper;

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
    String currCategory; // 현재 선택된 카테고리를 나타냄. Spinner에서 하나가 선택되면 바로 이 값을 채워줘야한다.

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

        spinner = findViewById(R.id.spinner);
        getSpinner();

        exerciseListContainer = findViewById(R.id.exerciseListContainer);
    }


    private final View.OnClickListener closeDialog = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            db_read.close();
            dismiss();
        }
    };

    private final View.OnClickListener openExerciseDialog = new View.OnClickListener() {
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
        // 호출한 다이얼로그가 종료되면 실행할 함수
        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {
                setButtonFromExerciseList();
            }
        });
        dialog.show();
    }

    public List<String> readCategoryFromDb(){
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
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // 선택된 항목 처리
                String selectedValue = parent.getItemAtPosition(position).toString();
                currCategory = selectedValue;
                setButtonFromExerciseList();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // 선택된 항목이 없는 경우 처리
            }
        });
    }


    public void setButtonFromExerciseList() {
        // 현재 container에 있는 리스트 지우기
        for (int i = 0; i < exerciseListContainer.getChildCount(); i++) {
            View view = exerciseListContainer.getChildAt(i);
            if (view instanceof TextView) { // TextView 인 경우만 처리
                exerciseListContainer.removeView(view); // 레이아웃에서 TextView 제거
            }
        }

        // container에 리스트 추가하기
        // 현재 리스트가 TextView를 추가하는 것으로 구현되어 있으나 Button으로 구현해야함
        // + Button을 클릭하면 routine을 추가할 수 있는 다이얼로그를 생성하는 이벤트리스너 등록해야함
        // + routine을 추가하는 다이얼로그 만들어야함
        // + routine을 추가하는 다이얼로그에서 이용할 DB를 좀 더 상세하게 생각해보아야 함
        List<String> exerciseList = readExerciseListFromDb(currCategory);
        for (int i = 0 ; i < exerciseList.size(); i++) {
            TextView textView = new TextView(getContext());
            textView.setText(exerciseList.get(i));
            exerciseListContainer.addView(textView);
        }

    }

    public List<String> readExerciseListFromDb(@NonNull String selectedCategory){
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