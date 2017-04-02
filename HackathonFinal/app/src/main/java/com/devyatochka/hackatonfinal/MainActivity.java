package com.devyatochka.hackatonfinal;

import android.content.Intent;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.UUID;

public class MainActivity extends AppCompatActivity {

    private EditText mEditTextInputNumber;
    private EditText mEditTextInputSMSCode;
    private TextView mTextViewTimer;
    private Button mButtonRestartTimer;
    private Button mButtonGetSMSCode;
    private Button mButtonSetSMSCode;

    private CountDownTimer countDownTimer;

    public static Boolean isLogin = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mTextViewTimer = (TextView) findViewById(R.id.textViewTimer);
        //mButtonRestartTimer = (Button) findViewById(R.id.buttonRestartTimer);
        mEditTextInputNumber = (EditText) findViewById(R.id.editTextInputNumber);
        mEditTextInputSMSCode = (EditText) findViewById(R.id.editTextInputSmsCode);
        mButtonGetSMSCode = (Button) findViewById(R.id.buttonGetSmsCode);
        mButtonSetSMSCode = (Button) findViewById(R.id.buttonSetCode);


        //mButtonRestartTimer.setVisibility(View.INVISIBLE);
        mTextViewTimer.setVisibility(View.INVISIBLE);
        mEditTextInputSMSCode.setClickable(false);


        String uniqueID = UUID.randomUUID().toString();

        mButtonGetSMSCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new GetSMSTask(mEditTextInputNumber.getText().toString()).execute();
                startTimer(45000);
            }
        });


        mButtonSetSMSCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new SetSMSCodeTask(mEditTextInputSMSCode.getText().toString()).execute();
                new CountDownTimer(2000, 1000) {
                    @Override
                    public void onTick(long millisUntilFinished) {

                    }

                    @Override
                    public void onFinish() {
                        if (!isLogin) {
                            Toast.makeText(getApplicationContext(), "Try again in a minute", Toast.LENGTH_SHORT).show();
                            countDownTimer.cancel();
                            startTimer(60000);
                        }
                        else {
                            Intent mainActivity = new Intent(getApplicationContext(), RecViewActivity.class);
                            mainActivity.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(mainActivity);
                        }

                    }
                }.start();

//                if (isLogin) {
//                    Intent mainActivity = new Intent(getApplicationContext(), RecViewActivity.class);
//                    mainActivity.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                    startActivity(mainActivity);
//                }

                //Toast.makeText(getApplicationContext(), isLogin + " 2", Toast.LENGTH_SHORT).show();
                //Toast.makeText(getApplicationContext(), isLogin + "", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void startTimer(Integer total) {
        mButtonGetSMSCode.setEnabled(false);
        mTextViewTimer.setVisibility(View.VISIBLE);

        countDownTimer = new CountDownTimer(total, 1000) {

            @Override
            public void onTick(long millisUntilFinished) {
                mTextViewTimer.setText(millisUntilFinished / 1000 + "");
            }

            @Override
            public void onFinish() {

                mTextViewTimer.setVisibility(View.INVISIBLE);
                mButtonGetSMSCode.setEnabled(true);
            }
        };

        countDownTimer.cancel();
        countDownTimer.start();
    }
}
