package com.example.ucanhealth;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Spinner;

import androidx.annotation.NonNull;

import com.example.ucanhealth.sqlite.UcanHealth;
import com.example.ucanhealth.sqlite.UcanHealthDbHelper;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class ExerciseSettingDialog_using_schduler extends Dialog {

    private UcanHealthDbHelper ucanHealthDbHelper;
    private SQLiteDatabase ucanHealthDb_read;
    private SQLiteDatabase ucanHealthDb_write;
    LinearLayout exerciseListContainer;
    LinearLayout todayExerciseListContainer;
    addExerciseDialog exerciseDialog;
    addRoutineDialog routineDialog;
    modifyRoutineDialog modifyRoutineDialog;
    Button addBtn;
    Button deleteAllBtn;
    Button closeBtn;
    Spinner spinner;
    String currCategory; // 현재 선택된 카테고리를 나타냄. Spinner에서 하나가 선택되면 바로 이 값을 채워줘야한다.

    public ExerciseSettingDialog_using_schduler(@NonNull Context context) {
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
        closeBtn.setOnClickListener(closeCurrentDialog);

        // 루틴을 전부 다 지우는 buton
        deleteAllBtn.setOnClickListener(deleteAllRoutine);

        // db connector
        ucanHealthDbHelper = new UcanHealthDbHelper((getContext()));
        ucanHealthDb_read = ucanHealthDbHelper.getReadableDatabase();
        ucanHealthDb_write = ucanHealthDbHelper.getWritableDatabase();


        // Spinner
        getSpinner();

        setButtonInRoutineListContainer();
    }

    public void init() {
        addBtn = findViewById(R.id.addBtn);
        closeBtn = findViewById(R.id.closeBtn);
        spinner = findViewById(R.id.spinner);
        deleteAllBtn = findViewById(R.id.deleteAllBtn);
        exerciseListContainer = findViewById(R.id.exerciseListContainer);
        todayExerciseListContainer = findViewById(R.id.todayExerciseListContainer);
    }


    private final View.OnClickListener closeCurrentDialog = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            ucanHealthDb_read.close();
            ucanHealthDb_write.close();
            dismiss();
        }
    };

    private final View.OnClickListener openExerciseDialog = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            ExerciseDialog();
        }
    };

    private final View.OnClickListener deleteAllRoutine = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            String selection = UcanHealth.UserExerciseLogEntry.COLUMN_DATE + " LIKE ?";
            String[] selectionArgs = {getCurrentDate()};
            Log.i("delete",getCurrentDate());

            ucanHealthDb_write.delete(UcanHealth.UserExerciseLogEntry.TABLE_NAME,
                    selection,
                    selectionArgs);

            setButtonInRoutineListContainer();
        }
    };

    public void ExerciseDialog() {
        exerciseDialog = new addExerciseDialog(getContext());
        exerciseDialog.setTitle(R.string.add_routine);
        exerciseDialog.getWindow().setGravity(Gravity.CENTER);
        exerciseDialog.setCancelable(true);
        // 호출한 다이얼로그가 종료되면 실행할 함수
        exerciseDialog.setOnDismissListener(new OnDismissListener() {
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
                "DISTINCT " + UcanHealth.ExerciseTypeEntry.COLUMN_CATEGORY
        };

        String sortOrder =
                UcanHealth.ExerciseTypeEntry.COLUMN_CATEGORY + " DESC";

        Cursor cursor = ucanHealthDb_read.query(
                UcanHealth.ExerciseTypeEntry.TABLE_NAME,   // The table to query
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
//        List<String> dataList = readCategoryFromDb();
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
            todayExerciseListContainer.removeView(view); // 레이아웃에서 TextView 제거

        }

        Cursor cursor = ucanHealthDbHelper.getRoutineByDate(ucanHealthDb_read, getCurrentDate());

        while(cursor.moveToNext()) {
            Log.i("makeButton","success");
            Button button = new Button(getContext());
            String exercise = cursor.getString(0);
            String reps = cursor.getString(1).toString();
            String weight = cursor.getString(2).toString();
            String totalSet = cursor.getString(4).toString();

            String text = exercise + " / " + reps + "회 / " + totalSet + "세트 / " + weight + "kg ";
            button.setText(text);

            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ModifyRoutineDialog(exercise);
                }
            });
            todayExerciseListContainer.addView(button);
        }
    }

    /**
     * DB에서 특정 카테고리에 포함된 운동종목 리스트를 반환하는 메소드
     * @param selectedItem -> 선택된 카테고리
     * @return exerciseList -> 카테고리에 해당하는 모든 운동종목 리스트
     */
    public List<String> readExerciseListFromDb(@NonNull String selectedItem){
        // Query 결과로 받아올 column 정의하기
        String[] projection = {
                UcanHealth.ExerciseTypeEntry.COLUMN_EXERCISE
        };

        // where절
        String selection = UcanHealth.ExerciseTypeEntry.COLUMN_EXERCISE_TYPE + " = ? AND " +
                UcanHealth.ExerciseTypeEntry.COLUMN_SHOW + " = ?";
        String[] selectionArgs = { selectedItem, "1" };

        // 정렬
        String sortOrder =
                UcanHealth.ExerciseTypeEntry.COLUMN_EXERCISE + " DESC";

        // Queyr 결과를 담은 cursor 가져오기
        Cursor cursor = ucanHealthDb_read.query(
                UcanHealth.ExerciseTypeEntry.TABLE_NAME,   // The table to query
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
     * @param selectedExercise -> 운동 종목
     * @reuturn null
     */
    public void RoutineDialog(String selectedExercise) {
        routineDialog = new addRoutineDialog(getContext(), selectedExercise);
        routineDialog.setTitle(R.string.add_routine);
        routineDialog.getWindow().setGravity(Gravity.CENTER);
        routineDialog.setCancelable(true);
        // 호출한 다이얼로그가 종료되면 실행할 함수
        routineDialog.setOnDismissListener(new OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {
                setButtonInRoutineListContainer();
            }
        });
        routineDialog.show();
    }

    public void ModifyRoutineDialog(String selectedRoutine) {
        modifyRoutineDialog = new modifyRoutineDialog(getContext(),selectedRoutine);
        modifyRoutineDialog.getWindow().setGravity(Gravity.CENTER);
        modifyRoutineDialog.setCancelable(true);
        // 호출한 다이얼로그가 종료되면 실행할 함수
        modifyRoutineDialog.setOnDismissListener(new OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {
                setButtonInRoutineListContainer();
            }
        });

        modifyRoutineDialog.show();
    }

    public String getCurrentDate() {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH) + 1; // month는 0부터 시작하므로 1을 더해줍니다.
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        Log.i("stirng", String.format("%04d-%02d-%02d", year, month, day));
        return String.format("%04d-%02d-%02d", year, month, day);
    }

}