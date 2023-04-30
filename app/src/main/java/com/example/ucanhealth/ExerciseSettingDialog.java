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

import androidx.annotation.NonNull;

import com.example.ucanhealth.sqlite.ExerciseType;
import com.example.ucanhealth.sqlite.ExerciseTypeDbHelper;
import com.example.ucanhealth.sqlite.UserExerciseLogDbHelper;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class ExerciseSettingDialog extends Dialog {
    private ExerciseTypeDbHelper exerciseTypeDbHelper;
    private SQLiteDatabase exerciseTypeDb_read;
    private UserExerciseLogDbHelper userExerciseLogDbHelper;
    private SQLiteDatabase userExerciseLogDb_read;
    LinearLayout exerciseListContainer;
    LinearLayout todayExerciseListContainer;
    addExerciseDialog exerciseDialog;
    addRoutineDialog routineDialog;
    Button addBtn;
    Button closeBtn;
    Spinner spinner;
    String currCategory; // 현재 선택된 카테고리를 나타냄. Spinner에서 하나가 선택되면 바로 이 값을 채워줘야한다.

    public ExerciseSettingDialog(@NonNull Context context) {
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

        setContentView(R.layout.exercise_setting);
        this.init();
        // ExerciseDialog를 여는 button
        addBtn.setOnClickListener(openExerciseDialog);

        // 현재 Dialog를 닫는 button
        closeBtn.setOnClickListener(closeExerciseDialog);

        exerciseTypeDbHelper = new ExerciseTypeDbHelper(getContext());
        exerciseTypeDb_read = exerciseTypeDbHelper.getReadableDatabase();
        userExerciseLogDbHelper = new UserExerciseLogDbHelper((getContext()));
        userExerciseLogDb_read = userExerciseLogDbHelper.getReadableDatabase();

        // Spinner
        getSpinner();
    }

    public void init() {
        addBtn = findViewById(R.id.addBtn);
        closeBtn = findViewById(R.id.closeBtn);
        spinner = findViewById(R.id.spinner);
        exerciseListContainer = findViewById(R.id.exerciseListContainer);
        todayExerciseListContainer = findViewById(R.id.todayExerciseListContainer);
    }


    private final View.OnClickListener closeExerciseDialog = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            exerciseTypeDb_read.close();
            dismiss();
        }
    };

    private final View.OnClickListener openExerciseDialog = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            ExerciseDialog();
        }
    };

    public void ExerciseDialog() {
        exerciseDialog = new addExerciseDialog(getContext());
        exerciseDialog.setTitle(R.string.add_routine);
        exerciseDialog.getWindow().setGravity(Gravity.CENTER);
        exerciseDialog.setCancelable(true);
        // 호출한 다이얼로그가 종료되면 실행할 함수
        exerciseDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {
                setButtonInExerciseListContainer();
                getSpinner();
            }
        });
        exerciseDialog.show();
    }

    public List<String> readCategoryFromDb(){
        // Define a projection that specifies which columns from the database
        // you will actually use after this query.
        String[] projection = {
                "DISTINCT " + ExerciseType.ExerciseTypeEntry.COLUMN_CATEGORY
        };

        String sortOrder =
                ExerciseType.ExerciseTypeEntry.COLUMN_CATEGORY + " DESC";

        Cursor cursor = exerciseTypeDb_read.query(
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
                setButtonInExerciseListContainer();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // 선택된 항목이 없는 경우 처리
            }
        });
    }


    public void setButtonInExerciseListContainer() {
        // 현재 container에 있는 리스트 지우기
        for (int i = exerciseListContainer.getChildCount() - 1;  i >= 0; i--) {
            View view = exerciseListContainer.getChildAt(i);
            exerciseListContainer.removeView(view);
        }

        // container에 리스트 추가하기
        // 현재 리스트가 TextView를 추가하는 것으로 구현되어 있으나 Button으로 구현해야함 (완료)
        // + Button을 클릭하면 routine을 추가할 수 있는 다이얼로그를 생성하는 이벤트리스너 등록해야함 (완료)
        // + routine을 추가하는 다이얼로그 만들어야함 (완료)
        // + routine을 추가하는 다이얼로그에서 이용할 DB를 좀 더 상세하게 생각해보아야 함 (완료)
        List<String> exerciseList = readExerciseListFromDb(currCategory);
        for (int i = 0 ; i < exerciseList.size(); i++) {
            String exercise = exerciseList.get(i);
            Button button = new Button(getContext());
            button.setText(exercise);
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    RoutineDialog(exercise);
                }
            });
            exerciseListContainer.addView(button);
        }
    }

    public void setButtonInRoutineListContainer() {
        // 현재 container에 있는 리스트 지우기
        for (int i = todayExerciseListContainer.getChildCount() - 1;  i >= 0; i--) {
            View view = todayExerciseListContainer.getChildAt(i);
            if (view instanceof Button) { // TextView 인 경우만 처리
                todayExerciseListContainer.removeView(view); // 레이아웃에서 TextView 제거
            }
        }
        Cursor cursor = userExerciseLogDbHelper.getRoutineByDate(userExerciseLogDb_read, getCurrentDate());
        while(cursor.moveToNext()) {
            Button button = new Button(getContext());
            String exercise = cursor.getString(0);
            String reps = cursor.getString(1).toString();
            String weight = cursor.getString(2).toString();
            String totalSet = cursor.getString(3).toString();
            
            String text = exercise + " / " + reps + "회 / " + totalSet + "세트 / " + weight + "kg ";
            button.setText(text);
            
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // 해당 버튼을 클릭하면 루틴항목을 수정하는 다이얼로그를 호출해야함
                }
            });
        }

    }

    /**
     * DB에서 특정 카테고리에 포함된 운동종목 리스트를 반환하는 메소드
     * @param selectedCategory -> 선택된 카테고리
     * @return exerciseList -> 카테고리에 해당하는 모든 운동종목 리스트
     */
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
        Cursor cursor = exerciseTypeDb_read.query(
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

    /**
     * exerciseListContainer에 포함된 버튼을 누르면 루틴을 추가하기 위한 다이얼로그를 생성
     * @param seledtedExercise -> 운동 종목
     * @reuturn null
     */
    public void RoutineDialog(String seledtedExercise) {
        routineDialog = new addRoutineDialog(getContext(), seledtedExercise);
        routineDialog.setTitle(R.string.add_routine);
        routineDialog.getWindow().setGravity(Gravity.CENTER);
        routineDialog.setCancelable(true);
        // 호출한 다이얼로그가 종료되면 실행할 함수
        routineDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {
                setButtonInRoutineListContainer();
            }
        });
        routineDialog.show();
    }

    public String getCurrentDate() {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH) + 1; // month는 0부터 시작하므로 1을 더해줍니다.
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        return String.format("%04d-%02d-%02d", year, month, day);
    }

}