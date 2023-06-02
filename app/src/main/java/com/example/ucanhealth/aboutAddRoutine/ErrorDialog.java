package com.example.ucanhealth.aboutAddRoutine;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.example.ucanhealth.R;

public class ErrorDialog extends Dialog {
    String errorMsg;
    TextView errorTextView;
    Button closeBtn;

    public ErrorDialog(@NonNull Context context, @NonNull String errorMsg) {
        super(context);
        this.errorMsg = errorMsg;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 화면 설정
        WindowManager.LayoutParams lpWindow = new WindowManager.LayoutParams();
        lpWindow.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        lpWindow.dimAmount = 0.8f;
        getWindow().setAttributes(lpWindow);
         
        // setContentView가 아래에 있어야함 그래야 전체가 안 보임
        setContentView(R.layout.error_dialog);

        init();

        setErrorMsg();
        closeBtn.setOnClickListener(closeDialog);
    }

    private void init() {
        errorTextView = findViewById(R.id.errorTextView);
        closeBtn = findViewById(R.id.closeBtn);
    }

    private final View.OnClickListener closeDialog = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            dismiss();
        }
    };

    private void setErrorMsg() {
        if(!errorMsg.equals("")) errorTextView.setText(errorMsg);
    }
}
